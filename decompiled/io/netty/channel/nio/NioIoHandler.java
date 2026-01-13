package io.netty.channel.nio;

import io.netty.channel.ChannelException;
import io.netty.channel.DefaultSelectStrategyFactory;
import io.netty.channel.IoHandle;
import io.netty.channel.IoHandler;
import io.netty.channel.IoHandlerContext;
import io.netty.channel.IoHandlerFactory;
import io.netty.channel.IoOps;
import io.netty.channel.IoRegistration;
import io.netty.channel.SelectStrategy;
import io.netty.channel.SelectStrategyFactory;
import io.netty.util.IntSupplier;
import io.netty.util.concurrent.ThreadAwareExecutor;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.PlatformDependent;
import io.netty.util.internal.ReflectionUtil;
import io.netty.util.internal.StringUtil;
import io.netty.util.internal.SystemPropertyUtil;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.channels.CancelledKeyException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.spi.SelectorProvider;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public final class NioIoHandler implements IoHandler {
   private static final InternalLogger logger = InternalLoggerFactory.getInstance(NioIoHandler.class);
   private static final int CLEANUP_INTERVAL = 256;
   private static final boolean DISABLE_KEY_SET_OPTIMIZATION = SystemPropertyUtil.getBoolean("io.netty.noKeySetOptimization", false);
   private static final int MIN_PREMATURE_SELECTOR_RETURNS = 3;
   private static final int SELECTOR_AUTO_REBUILD_THRESHOLD;
   private final IntSupplier selectNowSupplier = new IntSupplier() {
      @Override
      public int get() throws Exception {
         return NioIoHandler.this.selectNow();
      }
   };
   private Selector selector;
   private Selector unwrappedSelector;
   private SelectedSelectionKeySet selectedKeys;
   private final SelectorProvider provider;
   private final AtomicBoolean wakenUp = new AtomicBoolean();
   private final SelectStrategy selectStrategy;
   private final ThreadAwareExecutor executor;
   private int cancelledKeys;
   private boolean needsToSelectAgain;

   private NioIoHandler(ThreadAwareExecutor executor, SelectorProvider selectorProvider, SelectStrategy strategy) {
      this.executor = ObjectUtil.checkNotNull(executor, "executionContext");
      this.provider = ObjectUtil.checkNotNull(selectorProvider, "selectorProvider");
      this.selectStrategy = ObjectUtil.checkNotNull(strategy, "selectStrategy");
      NioIoHandler.SelectorTuple selectorTuple = this.openSelector();
      this.selector = selectorTuple.selector;
      this.unwrappedSelector = selectorTuple.unwrappedSelector;
   }

   private NioIoHandler.SelectorTuple openSelector() {
      final Selector unwrappedSelector;
      try {
         unwrappedSelector = this.provider.openSelector();
      } catch (IOException var7) {
         throw new ChannelException("failed to open a new selector", var7);
      }

      if (DISABLE_KEY_SET_OPTIMIZATION) {
         return new NioIoHandler.SelectorTuple(unwrappedSelector);
      } else {
         Object maybeSelectorImplClass = AccessController.doPrivileged(new PrivilegedAction<Object>() {
            @Override
            public Object run() {
               try {
                  return Class.forName("sun.nio.ch.SelectorImpl", false, PlatformDependent.getSystemClassLoader());
               } catch (Throwable var2) {
                  return var2;
               }
            }
         });
         if (maybeSelectorImplClass instanceof Class && ((Class)maybeSelectorImplClass).isAssignableFrom(unwrappedSelector.getClass())) {
            final Class<?> selectorImplClass = (Class<?>)maybeSelectorImplClass;
            final SelectedSelectionKeySet selectedKeySet = new SelectedSelectionKeySet();
            Object maybeException = AccessController.doPrivileged(new PrivilegedAction<Object>() {
               @Override
               public Object run() {
                  try {
                     Field selectedKeysField = selectorImplClass.getDeclaredField("selectedKeys");
                     Field publicSelectedKeysField = selectorImplClass.getDeclaredField("publicSelectedKeys");
                     if (PlatformDependent.javaVersion() >= 9 && PlatformDependent.hasUnsafe()) {
                        long selectedKeysFieldOffset = PlatformDependent.objectFieldOffset(selectedKeysField);
                        long publicSelectedKeysFieldOffset = PlatformDependent.objectFieldOffset(publicSelectedKeysField);
                        if (selectedKeysFieldOffset != -1L && publicSelectedKeysFieldOffset != -1L) {
                           PlatformDependent.putObject(unwrappedSelector, selectedKeysFieldOffset, selectedKeySet);
                           PlatformDependent.putObject(unwrappedSelector, publicSelectedKeysFieldOffset, selectedKeySet);
                           return null;
                        }
                     }

                     Throwable cause = ReflectionUtil.trySetAccessible(selectedKeysField, true);
                     if (cause != null) {
                        return cause;
                     } else {
                        cause = ReflectionUtil.trySetAccessible(publicSelectedKeysField, true);
                        if (cause != null) {
                           return cause;
                        } else {
                           selectedKeysField.set(unwrappedSelector, selectedKeySet);
                           publicSelectedKeysField.set(unwrappedSelector, selectedKeySet);
                           return null;
                        }
                     }
                  } catch (IllegalAccessException | NoSuchFieldException var7) {
                     return var7;
                  }
               }
            });
            if (maybeException instanceof Exception) {
               this.selectedKeys = null;
               Exception e = (Exception)maybeException;
               logger.trace("failed to instrument a special java.util.Set into: {}", unwrappedSelector, e);
               return new NioIoHandler.SelectorTuple(unwrappedSelector);
            } else {
               this.selectedKeys = selectedKeySet;
               logger.trace("instrumented a special java.util.Set into: {}", unwrappedSelector);
               return new NioIoHandler.SelectorTuple(unwrappedSelector, new SelectedSelectionKeySetSelector(unwrappedSelector, selectedKeySet));
            }
         } else {
            if (maybeSelectorImplClass instanceof Throwable) {
               Throwable t = (Throwable)maybeSelectorImplClass;
               logger.trace("failed to instrument a special java.util.Set into: {}", unwrappedSelector, t);
            }

            return new NioIoHandler.SelectorTuple(unwrappedSelector);
         }
      }
   }

   public SelectorProvider selectorProvider() {
      return this.provider;
   }

   Selector selector() {
      return this.selector;
   }

   int numRegistered() {
      return this.selector().keys().size() - this.cancelledKeys;
   }

   Set<SelectionKey> registeredSet() {
      return this.selector().keys();
   }

   void rebuildSelector0() {
      Selector oldSelector = this.selector;
      if (oldSelector != null) {
         NioIoHandler.SelectorTuple newSelectorTuple;
         try {
            newSelectorTuple = this.openSelector();
         } catch (Exception var9) {
            logger.warn("Failed to create a new Selector.", (Throwable)var9);
            return;
         }

         int nChannels = 0;

         for (SelectionKey key : oldSelector.keys()) {
            NioIoHandler.DefaultNioRegistration handle = (NioIoHandler.DefaultNioRegistration)key.attachment();

            try {
               if (key.isValid() && key.channel().keyFor(newSelectorTuple.unwrappedSelector) == null) {
                  handle.register(newSelectorTuple.unwrappedSelector);
                  nChannels++;
               }
            } catch (Exception var8) {
               logger.warn("Failed to re-register a NioHandle to the new Selector.", (Throwable)var8);
               handle.cancel();
            }
         }

         this.selector = newSelectorTuple.selector;
         this.unwrappedSelector = newSelectorTuple.unwrappedSelector;

         try {
            oldSelector.close();
         } catch (Throwable var10) {
            if (logger.isWarnEnabled()) {
               logger.warn("Failed to close the old Selector.", var10);
            }
         }

         if (logger.isInfoEnabled()) {
            logger.info("Migrated " + nChannels + " channel(s) to the new Selector.");
         }
      }
   }

   private static NioIoHandle nioHandle(IoHandle handle) {
      if (handle instanceof NioIoHandle) {
         return (NioIoHandle)handle;
      } else {
         throw new IllegalArgumentException("IoHandle of type " + StringUtil.simpleClassName(handle) + " not supported");
      }
   }

   private static NioIoOps cast(IoOps ops) {
      if (ops instanceof NioIoOps) {
         return (NioIoOps)ops;
      } else {
         throw new IllegalArgumentException("IoOps of type " + StringUtil.simpleClassName(ops) + " not supported");
      }
   }

   @Override
   public IoRegistration register(IoHandle handle) throws Exception {
      NioIoHandle nioHandle = nioHandle(handle);
      NioIoOps ops = NioIoOps.NONE;
      boolean selected = false;

      while (true) {
         try {
            IoRegistration registration = new NioIoHandler.DefaultNioRegistration(this.executor, nioHandle, ops, this.unwrappedSelector());
            handle.registered();
            return registration;
         } catch (CancelledKeyException var6) {
            if (selected) {
               throw var6;
            }

            this.selectNow();
            selected = true;
         }
      }
   }

   @Override
   public int run(IoHandlerContext context) {
      int handled = 0;

      try {
         try {
            switch (this.selectStrategy.calculateStrategy(this.selectNowSupplier, !context.canBlock())) {
               case -3:
               case -1:
                  this.select(context, this.wakenUp.getAndSet(false));
                  if (this.wakenUp.get()) {
                     this.selector.wakeup();
                  }
                  break;
               case -2:
                  if (context.shouldReportActiveIoTime()) {
                     context.reportActiveIoTime(0L);
                  }

                  return 0;
            }
         } catch (IOException var7) {
            this.rebuildSelector0();
            handleLoopException(var7);
            return 0;
         }

         this.cancelledKeys = 0;
         this.needsToSelectAgain = false;
         if (context.shouldReportActiveIoTime()) {
            long activeIoStartTimeNanos = System.nanoTime();
            handled = this.processSelectedKeys();
            long activeIoEndTimeNanos = System.nanoTime();
            context.reportActiveIoTime(activeIoEndTimeNanos - activeIoStartTimeNanos);
         } else {
            handled = this.processSelectedKeys();
         }
      } catch (Error var8) {
         throw var8;
      } catch (Throwable var9) {
         handleLoopException(var9);
      }

      return handled;
   }

   private static void handleLoopException(Throwable t) {
      logger.warn("Unexpected exception in the selector loop.", t);

      try {
         Thread.sleep(1000L);
      } catch (InterruptedException var2) {
      }
   }

   private int processSelectedKeys() {
      return this.selectedKeys != null ? this.processSelectedKeysOptimized() : this.processSelectedKeysPlain(this.selector.selectedKeys());
   }

   @Override
   public void destroy() {
      try {
         this.selector.close();
      } catch (IOException var2) {
         logger.warn("Failed to close a selector.", (Throwable)var2);
      }
   }

   private int processSelectedKeysPlain(Set<SelectionKey> selectedKeys) {
      if (selectedKeys.isEmpty()) {
         return 0;
      } else {
         Iterator<SelectionKey> i = selectedKeys.iterator();
         int handled = 0;

         while (true) {
            SelectionKey k = i.next();
            i.remove();
            this.processSelectedKey(k);
            handled++;
            if (!i.hasNext()) {
               break;
            }

            if (this.needsToSelectAgain) {
               this.selectAgain();
               selectedKeys = this.selector.selectedKeys();
               if (selectedKeys.isEmpty()) {
                  break;
               }

               i = selectedKeys.iterator();
            }
         }

         return handled;
      }
   }

   private int processSelectedKeysOptimized() {
      int handled = 0;

      for (int i = 0; i < this.selectedKeys.size; i++) {
         SelectionKey k = this.selectedKeys.keys[i];
         this.selectedKeys.keys[i] = null;
         this.processSelectedKey(k);
         handled++;
         if (this.needsToSelectAgain) {
            this.selectedKeys.reset(i + 1);
            this.selectAgain();
            i = -1;
         }
      }

      return handled;
   }

   private void processSelectedKey(SelectionKey k) {
      NioIoHandler.DefaultNioRegistration registration = (NioIoHandler.DefaultNioRegistration)k.attachment();
      if (!registration.isValid()) {
         try {
            registration.handle.close();
         } catch (Exception var4) {
            logger.debug("Exception during closing " + registration.handle, (Throwable)var4);
         }
      } else {
         registration.handle(k.readyOps());
      }
   }

   @Override
   public void prepareToDestroy() {
      this.selectAgain();
      Set<SelectionKey> keys = this.selector.keys();
      Collection<NioIoHandler.DefaultNioRegistration> registrations = new ArrayList<>(keys.size());

      for (SelectionKey k : keys) {
         NioIoHandler.DefaultNioRegistration handle = (NioIoHandler.DefaultNioRegistration)k.attachment();
         registrations.add(handle);
      }

      for (NioIoHandler.DefaultNioRegistration reg : registrations) {
         reg.close();
      }
   }

   @Override
   public void wakeup() {
      if (!this.executor.isExecutorThread(Thread.currentThread()) && this.wakenUp.compareAndSet(false, true)) {
         this.selector.wakeup();
      }
   }

   @Override
   public boolean isCompatible(Class<? extends IoHandle> handleType) {
      return NioIoHandle.class.isAssignableFrom(handleType);
   }

   Selector unwrappedSelector() {
      return this.unwrappedSelector;
   }

   private void select(IoHandlerContext runner, boolean oldWakenUp) throws IOException {
      Selector selector = this.selector;

      try {
         int selectCnt = 0;
         long currentTimeNanos = System.nanoTime();
         long delayNanos = runner.delayNanos(currentTimeNanos);
         long selectDeadLineNanos = Long.MAX_VALUE;
         if (delayNanos != Long.MAX_VALUE) {
            selectDeadLineNanos = currentTimeNanos + runner.delayNanos(currentTimeNanos);
         }

         while (true) {
            long timeoutMillis;
            if (delayNanos != Long.MAX_VALUE) {
               long millisBeforeDeadline = millisBeforeDeadline(selectDeadLineNanos, currentTimeNanos);
               if (millisBeforeDeadline <= 0L) {
                  if (selectCnt == 0) {
                     selector.selectNow();
                     selectCnt = 1;
                  }
                  break;
               }

               timeoutMillis = millisBeforeDeadline;
            } else {
               timeoutMillis = 0L;
            }

            if (!runner.canBlock() && this.wakenUp.compareAndSet(false, true)) {
               selector.selectNow();
               selectCnt = 1;
               break;
            }

            int selectedKeys = selector.select(timeoutMillis);
            selectCnt++;
            if (selectedKeys != 0 || oldWakenUp || this.wakenUp.get() || !runner.canBlock()) {
               break;
            }

            if (Thread.interrupted()) {
               if (logger.isDebugEnabled()) {
                  logger.debug(
                     "Selector.select() returned prematurely because Thread.currentThread().interrupt() was called. Use NioHandler.shutdownGracefully() to shutdown the NioHandler."
                  );
               }

               selectCnt = 1;
               break;
            }

            long time = System.nanoTime();
            if (time - TimeUnit.MILLISECONDS.toNanos(timeoutMillis) >= currentTimeNanos) {
               selectCnt = 1;
            } else if (SELECTOR_AUTO_REBUILD_THRESHOLD > 0 && selectCnt >= SELECTOR_AUTO_REBUILD_THRESHOLD) {
               selector = this.selectRebuildSelector(selectCnt);
               selectCnt = 1;
               break;
            }

            currentTimeNanos = time;
         }

         if (selectCnt > 3 && logger.isDebugEnabled()) {
            logger.debug("Selector.select() returned prematurely {} times in a row for Selector {}.", selectCnt - 1, selector);
         }
      } catch (CancelledKeyException var16) {
         if (logger.isDebugEnabled()) {
            logger.debug(CancelledKeyException.class.getSimpleName() + " raised by a Selector {} - JDK bug?", selector, var16);
         }
      }
   }

   private static long millisBeforeDeadline(long selectDeadLineNanos, long currentTimeNanos) {
      assert selectDeadLineNanos != Long.MAX_VALUE;

      long nanosBeforeDeadline = selectDeadLineNanos - currentTimeNanos;
      return nanosBeforeDeadline >= 9223372036854275807L ? 9223372036854L : (nanosBeforeDeadline + 500000L) / 1000000L;
   }

   int selectNow() throws IOException {
      int var1;
      try {
         var1 = this.selector.selectNow();
      } finally {
         if (this.wakenUp.get()) {
            this.selector.wakeup();
         }
      }

      return var1;
   }

   private Selector selectRebuildSelector(int selectCnt) throws IOException {
      logger.warn("Selector.select() returned prematurely {} times in a row; rebuilding Selector {}.", selectCnt, this.selector);
      this.rebuildSelector0();
      Selector selector = this.selector;
      selector.selectNow();
      return selector;
   }

   private void selectAgain() {
      this.needsToSelectAgain = false;

      try {
         this.selector.selectNow();
      } catch (Throwable var2) {
         logger.warn("Failed to update SelectionKeys.", var2);
      }
   }

   public static IoHandlerFactory newFactory() {
      return newFactory(SelectorProvider.provider(), DefaultSelectStrategyFactory.INSTANCE);
   }

   public static IoHandlerFactory newFactory(SelectorProvider selectorProvider) {
      return newFactory(selectorProvider, DefaultSelectStrategyFactory.INSTANCE);
   }

   public static IoHandlerFactory newFactory(final SelectorProvider selectorProvider, final SelectStrategyFactory selectStrategyFactory) {
      ObjectUtil.checkNotNull(selectorProvider, "selectorProvider");
      ObjectUtil.checkNotNull(selectStrategyFactory, "selectStrategyFactory");
      return new IoHandlerFactory() {
         @Override
         public IoHandler newHandler(ThreadAwareExecutor executor) {
            return new NioIoHandler(executor, selectorProvider, selectStrategyFactory.newSelectStrategy());
         }

         @Override
         public boolean isChangingThreadSupported() {
            return true;
         }
      };
   }

   static {
      int selectorAutoRebuildThreshold = SystemPropertyUtil.getInt("io.netty.selectorAutoRebuildThreshold", 512);
      if (selectorAutoRebuildThreshold < 3) {
         selectorAutoRebuildThreshold = 0;
      }

      SELECTOR_AUTO_REBUILD_THRESHOLD = selectorAutoRebuildThreshold;
      if (logger.isDebugEnabled()) {
         logger.debug("-Dio.netty.noKeySetOptimization: {}", DISABLE_KEY_SET_OPTIMIZATION);
         logger.debug("-Dio.netty.selectorAutoRebuildThreshold: {}", SELECTOR_AUTO_REBUILD_THRESHOLD);
      }
   }

   final class DefaultNioRegistration implements IoRegistration {
      private final AtomicBoolean canceled = new AtomicBoolean();
      private final NioIoHandle handle;
      private volatile SelectionKey key;

      DefaultNioRegistration(ThreadAwareExecutor executor, NioIoHandle handle, NioIoOps initialOps, Selector selector) throws IOException {
         this.handle = handle;
         this.key = handle.selectableChannel().register(selector, initialOps.value, this);
      }

      NioIoHandle handle() {
         return this.handle;
      }

      void register(Selector selector) throws IOException {
         SelectionKey newKey = this.handle.selectableChannel().register(selector, this.key.interestOps(), this);
         this.key.cancel();
         this.key = newKey;
      }

      @Override
      public <T> T attachment() {
         return (T)this.key;
      }

      @Override
      public boolean isValid() {
         return !this.canceled.get() && this.key.isValid();
      }

      @Override
      public long submit(IoOps ops) {
         if (!this.isValid()) {
            return -1L;
         } else {
            int v = NioIoHandler.cast(ops).value;
            this.key.interestOps(v);
            return v;
         }
      }

      @Override
      public boolean cancel() {
         if (!this.canceled.compareAndSet(false, true)) {
            return false;
         } else {
            this.key.cancel();
            NioIoHandler.this.cancelledKeys++;
            if (NioIoHandler.this.cancelledKeys >= 256) {
               NioIoHandler.this.cancelledKeys = 0;
               NioIoHandler.this.needsToSelectAgain = true;
            }

            this.handle.unregistered();
            return true;
         }
      }

      void close() {
         this.cancel();

         try {
            this.handle.close();
         } catch (Exception var2) {
            NioIoHandler.logger.debug("Exception during closing " + this.handle, (Throwable)var2);
         }
      }

      void handle(int ready) {
         if (this.isValid()) {
            this.handle.handle(this, NioIoOps.eventOf(ready));
         }
      }
   }

   private static final class SelectorTuple {
      final Selector unwrappedSelector;
      final Selector selector;

      SelectorTuple(Selector unwrappedSelector) {
         this.unwrappedSelector = unwrappedSelector;
         this.selector = unwrappedSelector;
      }

      SelectorTuple(Selector unwrappedSelector, Selector selector) {
         this.unwrappedSelector = unwrappedSelector;
         this.selector = selector;
      }
   }
}
