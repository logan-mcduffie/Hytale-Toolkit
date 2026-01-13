package io.netty.channel.local;

import io.netty.channel.IoHandle;
import io.netty.channel.IoHandler;
import io.netty.channel.IoHandlerContext;
import io.netty.channel.IoHandlerFactory;
import io.netty.channel.IoOps;
import io.netty.channel.IoRegistration;
import io.netty.util.concurrent.ThreadAwareExecutor;
import io.netty.util.internal.StringUtil;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.LockSupport;

public final class LocalIoHandler implements IoHandler {
   private final Set<LocalIoHandle> registeredChannels = new HashSet<>(64);
   private final ThreadAwareExecutor executor;
   private volatile Thread executionThread;

   private LocalIoHandler(ThreadAwareExecutor executor) {
      this.executor = Objects.requireNonNull(executor, "executor");
   }

   public static IoHandlerFactory newFactory() {
      return LocalIoHandler::new;
   }

   private static LocalIoHandle cast(IoHandle handle) {
      if (handle instanceof LocalIoHandle) {
         return (LocalIoHandle)handle;
      } else {
         throw new IllegalArgumentException("IoHandle of type " + StringUtil.simpleClassName(handle) + " not supported");
      }
   }

   @Override
   public int run(IoHandlerContext context) {
      if (this.executionThread == null) {
         this.executionThread = Thread.currentThread();
      }

      if (context.canBlock()) {
         LockSupport.parkNanos(this, context.delayNanos(System.nanoTime()));
      }

      if (context.shouldReportActiveIoTime()) {
         context.reportActiveIoTime(0L);
      }

      return 0;
   }

   @Override
   public void wakeup() {
      if (!this.executor.isExecutorThread(Thread.currentThread())) {
         Thread thread = this.executionThread;
         if (thread != null) {
            LockSupport.unpark(thread);
         }
      }
   }

   @Override
   public void prepareToDestroy() {
      for (LocalIoHandle handle : this.registeredChannels) {
         handle.closeNow();
      }

      this.registeredChannels.clear();
   }

   @Override
   public void destroy() {
   }

   @Override
   public IoRegistration register(IoHandle handle) {
      LocalIoHandle localHandle = cast(handle);
      if (this.registeredChannels.add(localHandle)) {
         LocalIoHandler.LocalIoRegistration registration = new LocalIoHandler.LocalIoRegistration(this.executor, localHandle);
         localHandle.registered();
         return registration;
      } else {
         throw new IllegalStateException();
      }
   }

   @Override
   public boolean isCompatible(Class<? extends IoHandle> handleType) {
      return LocalIoHandle.class.isAssignableFrom(handleType);
   }

   private final class LocalIoRegistration implements IoRegistration {
      private final AtomicBoolean canceled = new AtomicBoolean();
      private final ThreadAwareExecutor executor;
      private final LocalIoHandle handle;

      LocalIoRegistration(ThreadAwareExecutor executor, LocalIoHandle handle) {
         this.executor = executor;
         this.handle = handle;
      }

      @Override
      public <T> T attachment() {
         return null;
      }

      @Override
      public long submit(IoOps ops) {
         throw new UnsupportedOperationException();
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
         if (LocalIoHandler.this.registeredChannels.remove(this.handle)) {
            this.handle.unregistered();
         }
      }
   }
}
