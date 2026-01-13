package io.netty.channel;

import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.Promise;
import io.netty.util.concurrent.RejectedExecutionHandler;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.PlatformDependent;
import io.netty.util.internal.SystemPropertyUtil;
import java.util.Queue;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class SingleThreadIoEventLoop extends SingleThreadEventLoop implements IoEventLoop {
   private static final long DEFAULT_MAX_TASK_PROCESSING_QUANTUM_NS = TimeUnit.MILLISECONDS
      .toNanos(Math.max(100, SystemPropertyUtil.getInt("io.netty.eventLoop.maxTaskProcessingQuantumMs", 1000)));
   private final long maxTaskProcessingQuantumNs;
   private final IoHandlerContext context = new IoHandlerContext() {
      @Override
      public boolean canBlock() {
         assert SingleThreadIoEventLoop.this.inEventLoop();

         return !SingleThreadIoEventLoop.this.hasTasks() && !SingleThreadIoEventLoop.this.hasScheduledTasks();
      }

      @Override
      public long delayNanos(long currentTimeNanos) {
         assert SingleThreadIoEventLoop.this.inEventLoop();

         return SingleThreadIoEventLoop.this.delayNanos(currentTimeNanos);
      }

      @Override
      public long deadlineNanos() {
         assert SingleThreadIoEventLoop.this.inEventLoop();

         return SingleThreadIoEventLoop.this.deadlineNanos();
      }

      @Override
      public void reportActiveIoTime(long activeNanos) {
         SingleThreadIoEventLoop.this.reportActiveIoTime(activeNanos);
      }

      @Override
      public boolean shouldReportActiveIoTime() {
         return SingleThreadIoEventLoop.this.isSuspensionSupported();
      }
   };
   private final IoHandler ioHandler;
   private final AtomicInteger numRegistrations = new AtomicInteger();

   public SingleThreadIoEventLoop(IoEventLoopGroup parent, ThreadFactory threadFactory, IoHandlerFactory ioHandlerFactory) {
      super(parent, threadFactory, false, ObjectUtil.checkNotNull(ioHandlerFactory, "ioHandlerFactory").isChangingThreadSupported());
      this.maxTaskProcessingQuantumNs = DEFAULT_MAX_TASK_PROCESSING_QUANTUM_NS;
      this.ioHandler = ioHandlerFactory.newHandler(this);
   }

   public SingleThreadIoEventLoop(IoEventLoopGroup parent, Executor executor, IoHandlerFactory ioHandlerFactory) {
      super(parent, executor, false, ObjectUtil.checkNotNull(ioHandlerFactory, "ioHandlerFactory").isChangingThreadSupported());
      this.maxTaskProcessingQuantumNs = DEFAULT_MAX_TASK_PROCESSING_QUANTUM_NS;
      this.ioHandler = ioHandlerFactory.newHandler(this);
   }

   public SingleThreadIoEventLoop(
      IoEventLoopGroup parent,
      ThreadFactory threadFactory,
      IoHandlerFactory ioHandlerFactory,
      int maxPendingTasks,
      RejectedExecutionHandler rejectedExecutionHandler,
      long maxTaskProcessingQuantumMs
   ) {
      super(
         parent,
         threadFactory,
         false,
         ObjectUtil.checkNotNull(ioHandlerFactory, "ioHandlerFactory").isChangingThreadSupported(),
         maxPendingTasks,
         rejectedExecutionHandler
      );
      this.maxTaskProcessingQuantumNs = ObjectUtil.checkPositiveOrZero(maxTaskProcessingQuantumMs, "maxTaskProcessingQuantumMs") == 0L
         ? DEFAULT_MAX_TASK_PROCESSING_QUANTUM_NS
         : TimeUnit.MILLISECONDS.toNanos(maxTaskProcessingQuantumMs);
      this.ioHandler = ioHandlerFactory.newHandler(this);
   }

   public SingleThreadIoEventLoop(
      IoEventLoopGroup parent,
      Executor executor,
      IoHandlerFactory ioHandlerFactory,
      int maxPendingTasks,
      RejectedExecutionHandler rejectedExecutionHandler,
      long maxTaskProcessingQuantumMs
   ) {
      super(
         parent,
         executor,
         false,
         ObjectUtil.checkNotNull(ioHandlerFactory, "ioHandlerFactory").isChangingThreadSupported(),
         maxPendingTasks,
         rejectedExecutionHandler
      );
      this.maxTaskProcessingQuantumNs = ObjectUtil.checkPositiveOrZero(maxTaskProcessingQuantumMs, "maxTaskProcessingQuantumMs") == 0L
         ? DEFAULT_MAX_TASK_PROCESSING_QUANTUM_NS
         : TimeUnit.MILLISECONDS.toNanos(maxTaskProcessingQuantumMs);
      this.ioHandler = ioHandlerFactory.newHandler(this);
   }

   protected SingleThreadIoEventLoop(
      IoEventLoopGroup parent,
      Executor executor,
      IoHandlerFactory ioHandlerFactory,
      Queue<Runnable> taskQueue,
      Queue<Runnable> tailTaskQueue,
      RejectedExecutionHandler rejectedExecutionHandler
   ) {
      super(
         parent,
         executor,
         false,
         ObjectUtil.checkNotNull(ioHandlerFactory, "ioHandlerFactory").isChangingThreadSupported(),
         taskQueue,
         tailTaskQueue,
         rejectedExecutionHandler
      );
      this.maxTaskProcessingQuantumNs = DEFAULT_MAX_TASK_PROCESSING_QUANTUM_NS;
      this.ioHandler = ioHandlerFactory.newHandler(this);
   }

   @Override
   protected void run() {
      assert this.inEventLoop();

      this.ioHandler.initialize();

      do {
         this.runIo();
         if (this.isShuttingDown()) {
            this.ioHandler.prepareToDestroy();
         }

         this.runAllTasks(this.maxTaskProcessingQuantumNs);
      } while (!this.confirmShutdown() && !this.canSuspend());
   }

   protected final IoHandler ioHandler() {
      return this.ioHandler;
   }

   @Override
   protected boolean canSuspend(int state) {
      return super.canSuspend(state) && this.numRegistrations.get() == 0;
   }

   protected int runIo() {
      assert this.inEventLoop();

      return this.ioHandler.run(this.context);
   }

   @Override
   public IoEventLoop next() {
      return this;
   }

   @Override
   public final Future<IoRegistration> register(IoHandle handle) {
      Promise<IoRegistration> promise = this.newPromise();
      if (this.inEventLoop()) {
         this.registerForIo0(handle, promise);
      } else {
         this.execute(() -> this.registerForIo0(handle, promise));
      }

      return promise;
   }

   @Override
   protected int getNumOfRegisteredChannels() {
      return this.numRegistrations.get();
   }

   private void registerForIo0(IoHandle handle, Promise<IoRegistration> promise) {
      assert this.inEventLoop();

      IoRegistration registration;
      try {
         registration = this.ioHandler.register(handle);
      } catch (Exception var5) {
         promise.setFailure(var5);
         return;
      }

      this.numRegistrations.incrementAndGet();
      promise.setSuccess(new SingleThreadIoEventLoop.IoRegistrationWrapper(registration));
   }

   @Override
   protected final void wakeup(boolean inEventLoop) {
      this.ioHandler.wakeup();
   }

   @Override
   protected final void cleanup() {
      assert this.inEventLoop();

      this.ioHandler.destroy();
   }

   @Override
   public boolean isCompatible(Class<? extends IoHandle> handleType) {
      return this.ioHandler.isCompatible(handleType);
   }

   @Override
   public boolean isIoType(Class<? extends IoHandler> handlerType) {
      return this.ioHandler.getClass().equals(handlerType);
   }

   @Override
   protected Queue<Runnable> newTaskQueue(int maxPendingTasks) {
      return newTaskQueue0(maxPendingTasks);
   }

   protected static Queue<Runnable> newTaskQueue0(int maxPendingTasks) {
      return maxPendingTasks == Integer.MAX_VALUE ? PlatformDependent.newMpscQueue() : PlatformDependent.newMpscQueue(maxPendingTasks);
   }

   private final class IoRegistrationWrapper implements IoRegistration {
      private final IoRegistration registration;

      IoRegistrationWrapper(IoRegistration registration) {
         this.registration = registration;
      }

      @Override
      public <T> T attachment() {
         return this.registration.attachment();
      }

      @Override
      public long submit(IoOps ops) {
         return this.registration.submit(ops);
      }

      @Override
      public boolean isValid() {
         return this.registration.isValid();
      }

      @Override
      public boolean cancel() {
         if (this.registration.cancel()) {
            SingleThreadIoEventLoop.this.numRegistrations.decrementAndGet();
            return true;
         } else {
            return false;
         }
      }
   }
}
