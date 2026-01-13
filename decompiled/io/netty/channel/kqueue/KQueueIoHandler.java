package io.netty.channel.kqueue;

import io.netty.channel.Channel;
import io.netty.channel.DefaultSelectStrategyFactory;
import io.netty.channel.IoHandle;
import io.netty.channel.IoHandler;
import io.netty.channel.IoHandlerContext;
import io.netty.channel.IoHandlerFactory;
import io.netty.channel.IoOps;
import io.netty.channel.IoRegistration;
import io.netty.channel.SelectStrategy;
import io.netty.channel.SelectStrategyFactory;
import io.netty.channel.unix.FileDescriptor;
import io.netty.util.IntSupplier;
import io.netty.util.collection.LongObjectHashMap;
import io.netty.util.collection.LongObjectMap;
import io.netty.util.concurrent.ThreadAwareExecutor;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.StringUtil;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;

public final class KQueueIoHandler implements IoHandler {
   private static final InternalLogger logger = InternalLoggerFactory.getInstance(KQueueIoHandler.class);
   private static final AtomicIntegerFieldUpdater<KQueueIoHandler> WAKEN_UP_UPDATER = AtomicIntegerFieldUpdater.newUpdater(KQueueIoHandler.class, "wakenUp");
   private static final int KQUEUE_WAKE_UP_IDENT = 0;
   private static final int KQUEUE_MAX_TIMEOUT_SECONDS = 86399;
   private final boolean allowGrowing;
   private final FileDescriptor kqueueFd;
   private final KQueueEventArray changeList;
   private final KQueueEventArray eventList;
   private final SelectStrategy selectStrategy;
   private final NativeArrays nativeArrays;
   private final IntSupplier selectNowSupplier;
   private final ThreadAwareExecutor executor;
   private final Queue<KQueueIoHandler.DefaultKqueueIoRegistration> cancelledRegistrations;
   private final LongObjectMap<KQueueIoHandler.DefaultKqueueIoRegistration> registrations;
   private int numChannels;
   private long nextId;
   private volatile int wakenUp;

   private long generateNextId() {
      boolean reset = false;

      do {
         if (this.nextId == Long.MAX_VALUE) {
            if (reset) {
               throw new IllegalStateException("All possible ids in use");
            }

            reset = true;
         }

         this.nextId++;
      } while (this.nextId == 0L || this.registrations.containsKey(this.nextId));

      return this.nextId;
   }

   public static IoHandlerFactory newFactory() {
      return newFactory(0, DefaultSelectStrategyFactory.INSTANCE);
   }

   public static IoHandlerFactory newFactory(final int maxEvents, final SelectStrategyFactory selectStrategyFactory) {
      KQueue.ensureAvailability();
      ObjectUtil.checkPositiveOrZero(maxEvents, "maxEvents");
      ObjectUtil.checkNotNull(selectStrategyFactory, "selectStrategyFactory");
      return new IoHandlerFactory() {
         @Override
         public IoHandler newHandler(ThreadAwareExecutor executor) {
            return new KQueueIoHandler(executor, maxEvents, selectStrategyFactory.newSelectStrategy());
         }

         @Override
         public boolean isChangingThreadSupported() {
            return true;
         }
      };
   }

   private KQueueIoHandler(ThreadAwareExecutor executor, int maxEvents, SelectStrategy strategy) {
      KQueue.ensureAvailability();
      this.selectNowSupplier = new IntSupplier() {
         @Override
         public int get() throws Exception {
            return KQueueIoHandler.this.kqueueWaitNow();
         }
      };
      this.cancelledRegistrations = new ArrayDeque<>();
      this.registrations = new LongObjectHashMap<>(4096);
      this.executor = ObjectUtil.checkNotNull(executor, "executor");
      this.selectStrategy = ObjectUtil.checkNotNull(strategy, "strategy");
      this.kqueueFd = Native.newKQueue();
      if (maxEvents == 0) {
         this.allowGrowing = true;
         maxEvents = 4096;
      } else {
         this.allowGrowing = false;
      }

      this.changeList = new KQueueEventArray(maxEvents);
      this.eventList = new KQueueEventArray(maxEvents);
      this.nativeArrays = new NativeArrays();
      int result = Native.keventAddUserEvent(this.kqueueFd.intValue(), 0);
      if (result < 0) {
         this.destroy();
         throw new IllegalStateException("kevent failed to add user event with errno: " + -result);
      }
   }

   @Override
   public void wakeup() {
      if (!this.executor.isExecutorThread(Thread.currentThread()) && WAKEN_UP_UPDATER.compareAndSet(this, 0, 1)) {
         this.wakeup0();
      }
   }

   private void wakeup0() {
      Native.keventTriggerUserEvent(this.kqueueFd.intValue(), 0);
   }

   private int kqueueWait(IoHandlerContext context, boolean oldWakeup) throws IOException {
      if (oldWakeup && !context.canBlock()) {
         return this.kqueueWaitNow();
      } else {
         long totalDelay = context.delayNanos(System.nanoTime());
         int delaySeconds = (int)Math.min(totalDelay / 1000000000L, 86399L);
         int delayNanos = (int)(totalDelay % 1000000000L);
         return this.kqueueWait(delaySeconds, delayNanos);
      }
   }

   private int kqueueWaitNow() throws IOException {
      return this.kqueueWait(0, 0);
   }

   private int kqueueWait(int timeoutSec, int timeoutNs) throws IOException {
      int numEvents = Native.keventWait(this.kqueueFd.intValue(), this.changeList, this.eventList, timeoutSec, timeoutNs);
      this.changeList.clear();
      return numEvents;
   }

   private void processReady(int ready) {
      for (int i = 0; i < ready; i++) {
         short filter = this.eventList.filter(i);
         short flags = this.eventList.flags(i);
         int ident = this.eventList.ident(i);
         if (filter != Native.EVFILT_USER && (flags & Native.EV_ERROR) == 0) {
            long id = this.eventList.udata(i);
            KQueueIoHandler.DefaultKqueueIoRegistration registration = this.registrations.get(id);
            if (registration == null) {
               logger.warn("events[{}]=[{}, {}, {}] had no registration!", i, ident, id, filter);
            } else {
               registration.handle(ident, filter, flags, this.eventList.fflags(i), this.eventList.data(i), id);
            }
         } else {
            assert filter != Native.EVFILT_USER || filter == Native.EVFILT_USER && ident == 0;
         }
      }
   }

   @Override
   public int run(IoHandlerContext context) {
      int handled = 0;

      byte activeIoStartTimeNanos;
      try {
         int strategy = this.selectStrategy.calculateStrategy(this.selectNowSupplier, !context.canBlock());
         switch (strategy) {
            case -3:
            case -1:
               strategy = this.kqueueWait(context, WAKEN_UP_UPDATER.getAndSet(this, 0) == 1);
               if (this.wakenUp == 1) {
                  this.wakeup0();
               }
            default:
               if (strategy > 0) {
                  handled = strategy;
                  if (context.shouldReportActiveIoTime()) {
                     long activeIoStartTimeNanosx = System.nanoTime();
                     this.processReady(strategy);
                     long activeIoEndTimeNanos = System.nanoTime();
                     context.reportActiveIoTime(activeIoEndTimeNanos - activeIoStartTimeNanosx);
                  } else {
                     this.processReady(strategy);
                  }
               } else if (context.shouldReportActiveIoTime()) {
                  context.reportActiveIoTime(0L);
               }

               if (this.allowGrowing && strategy == this.eventList.capacity()) {
                  this.eventList.realloc(false);
               }

               return handled;
            case -2:
         }

         if (context.shouldReportActiveIoTime()) {
            context.reportActiveIoTime(0L);
         }

         activeIoStartTimeNanos = 0;
      } catch (Error var12) {
         throw var12;
      } catch (Throwable var13) {
         handleLoopException(var13);
         return handled;
      } finally {
         this.processCancelledRegistrations();
      }

      return activeIoStartTimeNanos;
   }

   private void processCancelledRegistrations() {
      while (true) {
         KQueueIoHandler.DefaultKqueueIoRegistration cancelledRegistration = this.cancelledRegistrations.poll();
         if (cancelledRegistration == null) {
            return;
         }

         KQueueIoHandler.DefaultKqueueIoRegistration removed = this.registrations.remove(cancelledRegistration.id);

         assert removed == cancelledRegistration;

         if (removed.isHandleForChannel()) {
            this.numChannels--;
         }

         removed.handle.unregistered();
      }
   }

   int numRegisteredChannels() {
      return this.numChannels;
   }

   List<Channel> registeredChannelsList() {
      LongObjectMap<KQueueIoHandler.DefaultKqueueIoRegistration> ch = this.registrations;
      if (ch.isEmpty()) {
         return Collections.emptyList();
      } else {
         List<Channel> channels = new ArrayList<>(ch.size());

         for (KQueueIoHandler.DefaultKqueueIoRegistration registration : ch.values()) {
            if (registration.handle instanceof AbstractKQueueChannel.AbstractKQueueUnsafe) {
               channels.add(((AbstractKQueueChannel.AbstractKQueueUnsafe)registration.handle).channel());
            }
         }

         return Collections.unmodifiableList(channels);
      }
   }

   private static void handleLoopException(Throwable t) {
      logger.warn("Unexpected exception in the selector loop.", t);

      try {
         Thread.sleep(1000L);
      } catch (InterruptedException var2) {
      }
   }

   @Override
   public void prepareToDestroy() {
      try {
         this.kqueueWaitNow();
      } catch (IOException var6) {
      }

      KQueueIoHandler.DefaultKqueueIoRegistration[] copy = this.registrations.values().toArray(new KQueueIoHandler.DefaultKqueueIoRegistration[0]);

      for (KQueueIoHandler.DefaultKqueueIoRegistration reg : copy) {
         reg.close();
      }

      this.processCancelledRegistrations();
   }

   @Override
   public void destroy() {
      try {
         this.kqueueFd.close();
      } catch (IOException var5) {
         logger.warn("Failed to close the kqueue fd.", (Throwable)var5);
      } finally {
         this.nativeArrays.free();
         this.changeList.free();
         this.eventList.free();
      }
   }

   @Override
   public IoRegistration register(IoHandle handle) {
      KQueueIoHandle kqueueHandle = cast(handle);
      if (kqueueHandle.ident() == 0) {
         throw new IllegalArgumentException("ident 0 is reserved for internal usage");
      } else {
         KQueueIoHandler.DefaultKqueueIoRegistration registration = new KQueueIoHandler.DefaultKqueueIoRegistration(this.executor, kqueueHandle);
         KQueueIoHandler.DefaultKqueueIoRegistration old = this.registrations.put(registration.id, registration);
         if (old != null) {
            this.registrations.put(old.id, old);
            throw new IllegalStateException();
         } else {
            if (registration.isHandleForChannel()) {
               this.numChannels++;
            }

            handle.registered();
            return registration;
         }
      }
   }

   private static KQueueIoHandle cast(IoHandle handle) {
      if (handle instanceof KQueueIoHandle) {
         return (KQueueIoHandle)handle;
      } else {
         throw new IllegalArgumentException("IoHandle of type " + StringUtil.simpleClassName(handle) + " not supported");
      }
   }

   private static KQueueIoOps cast(IoOps ops) {
      if (ops instanceof KQueueIoOps) {
         return (KQueueIoOps)ops;
      } else {
         throw new IllegalArgumentException("IoOps of type " + StringUtil.simpleClassName(ops) + " not supported");
      }
   }

   @Override
   public boolean isCompatible(Class<? extends IoHandle> handleType) {
      return KQueueIoHandle.class.isAssignableFrom(handleType);
   }

   private final class DefaultKqueueIoRegistration implements IoRegistration {
      private boolean cancellationPending;
      private final AtomicBoolean canceled = new AtomicBoolean();
      private final KQueueIoEvent event = new KQueueIoEvent();
      final KQueueIoHandle handle;
      final long id;
      private final ThreadAwareExecutor executor;

      DefaultKqueueIoRegistration(ThreadAwareExecutor executor, KQueueIoHandle handle) {
         this.executor = executor;
         this.handle = handle;
         this.id = KQueueIoHandler.this.generateNextId();
      }

      boolean isHandleForChannel() {
         return this.handle instanceof AbstractKQueueChannel.AbstractKQueueUnsafe;
      }

      @Override
      public <T> T attachment() {
         return (T)KQueueIoHandler.this.nativeArrays;
      }

      @Override
      public long submit(IoOps ops) {
         KQueueIoOps kQueueIoOps = KQueueIoHandler.cast(ops);
         if (!this.isValid()) {
            return -1L;
         } else {
            short filter = kQueueIoOps.filter();
            short flags = kQueueIoOps.flags();
            int fflags = kQueueIoOps.fflags();
            if (this.executor.isExecutorThread(Thread.currentThread())) {
               this.evSet(filter, flags, fflags);
            } else {
               this.executor.execute(() -> this.evSet(filter, flags, fflags));
            }

            return 0L;
         }
      }

      void handle(int ident, short filter, short flags, int fflags, long data, long udata) {
         if (!this.cancellationPending) {
            this.event.update(ident, filter, flags, fflags, data, udata);
            this.handle.handle(this, this.event);
         }
      }

      private void evSet(short filter, short flags, int fflags) {
         if (!this.cancellationPending) {
            KQueueIoHandler.this.changeList.evSet(this.handle.ident(), filter, flags, fflags, 0L, this.id);
         }
      }

      @Override
      public boolean isValid() {
         return !this.canceled.get();
      }

      @Override
      public boolean cancel() {
         if (!this.canceled.compareAndSet(false, true)) {
            return false;
         } else {
            if (this.executor.isExecutorThread(Thread.currentThread())) {
               this.cancel0();
            } else {
               this.executor.execute(this::cancel0);
            }

            return true;
         }
      }

      private void cancel0() {
         this.cancellationPending = true;
         KQueueIoHandler.this.cancelledRegistrations.offer(this);
      }

      void close() {
         this.cancel();

         try {
            this.handle.close();
         } catch (Exception var2) {
            KQueueIoHandler.logger.debug("Exception during closing " + this.handle, (Throwable)var2);
         }
      }
   }
}
