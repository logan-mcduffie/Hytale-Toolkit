package io.netty.channel;

import io.netty.util.concurrent.AbstractScheduledEventExecutor;
import io.netty.util.concurrent.DefaultPromise;
import io.netty.util.concurrent.EventExecutor;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GlobalEventExecutor;
import io.netty.util.concurrent.Promise;
import io.netty.util.concurrent.Ticker;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.PlatformDependent;
import io.netty.util.internal.ThreadExecutorMap;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Queue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class ManualIoEventLoop extends AbstractScheduledEventExecutor implements IoEventLoop {
   private static final Runnable WAKEUP_TASK = () -> {};
   private static final int ST_STARTED = 0;
   private static final int ST_SHUTTING_DOWN = 1;
   private static final int ST_SHUTDOWN = 2;
   private static final int ST_TERMINATED = 3;
   private final AtomicInteger state;
   private final Promise<?> terminationFuture = new DefaultPromise(GlobalEventExecutor.INSTANCE);
   private final Queue<Runnable> taskQueue = PlatformDependent.newMpscQueue();
   private final IoHandlerContext nonBlockingContext = new IoHandlerContext() {
      @Override
      public boolean canBlock() {
         assert ManualIoEventLoop.this.inEventLoop();

         return false;
      }

      @Override
      public long delayNanos(long currentTimeNanos) {
         assert ManualIoEventLoop.this.inEventLoop();

         return 0L;
      }

      @Override
      public long deadlineNanos() {
         assert ManualIoEventLoop.this.inEventLoop();

         return -1L;
      }
   };
   private final ManualIoEventLoop.BlockingIoHandlerContext blockingContext = new ManualIoEventLoop.BlockingIoHandlerContext();
   private final IoEventLoopGroup parent;
   private final AtomicReference<Thread> owningThread;
   private final IoHandler handler;
   private final Ticker ticker;
   private volatile long gracefulShutdownQuietPeriod;
   private volatile long gracefulShutdownTimeout;
   private long gracefulShutdownStartTime;
   private long lastExecutionTime;
   private boolean initialized;

   protected boolean canBlock() {
      return true;
   }

   public ManualIoEventLoop(Thread owningThread, IoHandlerFactory factory) {
      this(null, owningThread, factory);
   }

   public ManualIoEventLoop(IoEventLoopGroup parent, Thread owningThread, IoHandlerFactory factory) {
      this(parent, owningThread, factory, Ticker.systemTicker());
   }

   public ManualIoEventLoop(IoEventLoopGroup parent, Thread owningThread, IoHandlerFactory factory, Ticker ticker) {
      this.parent = parent;
      this.owningThread = new AtomicReference<>(owningThread);
      this.handler = factory.newHandler(this);
      this.ticker = Objects.requireNonNull(ticker, "ticker");
      this.state = new AtomicInteger(0);
   }

   @Override
   public final Ticker ticker() {
      return this.ticker;
   }

   public final int runNonBlockingTasks(long timeoutNanos) {
      return this.runAllTasks(timeoutNanos, true);
   }

   private int runAllTasks(long timeoutNanos, boolean setCurrentExecutor) {
      assert this.inEventLoop();

      Queue<Runnable> taskQueue = this.taskQueue;
      boolean alwaysTrue = this.fetchFromScheduledTaskQueue(taskQueue);

      assert alwaysTrue;

      Runnable task = taskQueue.poll();
      if (task == null) {
         return 0;
      } else {
         EventExecutor old = setCurrentExecutor ? ThreadExecutorMap.setCurrentExecutor(this) : null;

         int var14;
         try {
            long deadline = timeoutNanos > 0L ? this.getCurrentTimeNanos() + timeoutNanos : 0L;
            int runTasks = 0;
            Ticker ticker = this.ticker;

            long lastExecutionTime;
            while (true) {
               safeExecute(task);
               runTasks++;
               if (timeoutNanos > 0L) {
                  lastExecutionTime = ticker.nanoTime();
                  if (lastExecutionTime - deadline >= 0L) {
                     break;
                  }
               }

               task = taskQueue.poll();
               if (task == null) {
                  lastExecutionTime = ticker.nanoTime();
                  break;
               }
            }

            this.lastExecutionTime = lastExecutionTime;
            var14 = runTasks;
         } finally {
            if (setCurrentExecutor) {
               ThreadExecutorMap.setCurrentExecutor(old);
            }
         }

         return var14;
      }
   }

   private int run(IoHandlerContext context, long runAllTasksTimeoutNanos) {
      if (!this.initialized) {
         if (this.owningThread.get() == null) {
            throw new IllegalStateException("Owning thread not set");
         }

         this.initialized = true;
         this.handler.initialize();
      }

      EventExecutor old = ThreadExecutorMap.setCurrentExecutor(this);

      int ioTasks;
      try {
         if (!this.isShuttingDown()) {
            ioTasks = this.handler.run(context);
            if (runAllTasksTimeoutNanos >= 0L) {
               assert runAllTasksTimeoutNanos >= 0L;

               return ioTasks + this.runAllTasks(runAllTasksTimeoutNanos, false);
            }

            return ioTasks;
         }

         if (!this.terminationFuture.isDone()) {
            return this.runAllTasksBeforeDestroy();
         }

         ioTasks = 0;
      } finally {
         ThreadExecutorMap.setCurrentExecutor(old);
      }

      return ioTasks;
   }

   private int runAllTasksBeforeDestroy() {
      int run = this.runAllTasks(-1L, false);
      this.handler.prepareToDestroy();
      if (this.confirmShutdown()) {
         try {
            this.handler.destroy();

            int r;
            do {
               r = this.runAllTasks(-1L, false);
               run += r;
            } while (r != 0);
         } finally {
            this.state.set(3);
            this.terminationFuture.setSuccess(null);
         }
      }

      return run;
   }

   public final int runNow(long runAllTasksTimeoutNanos) {
      this.checkCurrentThread();
      return this.run(this.nonBlockingContext, runAllTasksTimeoutNanos);
   }

   public final int runNow() {
      this.checkCurrentThread();
      return this.run(this.nonBlockingContext, 0L);
   }

   public final int run(long waitNanos, long runAllTasksTimeoutNanos) {
      this.checkCurrentThread();
      IoHandlerContext context;
      if (waitNanos < 0L) {
         context = this.nonBlockingContext;
      } else {
         context = this.blockingContext;
         this.blockingContext.maxBlockingNanos = waitNanos == 0L ? Long.MAX_VALUE : waitNanos;
      }

      return this.run(context, runAllTasksTimeoutNanos);
   }

   public final int run(long waitNanos) {
      return this.run(waitNanos, 0L);
   }

   private void checkCurrentThread() {
      if (!this.inEventLoop(Thread.currentThread())) {
         throw new IllegalStateException();
      }
   }

   public final void wakeup() {
      if (!this.isShuttingDown()) {
         this.handler.wakeup();
      }
   }

   public final ManualIoEventLoop next() {
      return this;
   }

   public final IoEventLoopGroup parent() {
      return this.parent;
   }

   @Deprecated
   @Override
   public final ChannelFuture register(Channel channel) {
      return this.register(new DefaultChannelPromise(channel, this));
   }

   @Deprecated
   @Override
   public final ChannelFuture register(ChannelPromise promise) {
      ObjectUtil.checkNotNull(promise, "promise");
      promise.channel().unsafe().register(this, promise);
      return promise;
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

   private void registerForIo0(IoHandle handle, Promise<IoRegistration> promise) {
      assert this.inEventLoop();

      IoRegistration registration;
      try {
         registration = this.handler.register(handle);
      } catch (Exception var5) {
         promise.setFailure(var5);
         return;
      }

      promise.setSuccess(registration);
   }

   @Deprecated
   @Override
   public final ChannelFuture register(Channel channel, ChannelPromise promise) {
      ObjectUtil.checkNotNull(promise, "promise");
      ObjectUtil.checkNotNull(channel, "channel");
      channel.unsafe().register(this, promise);
      return promise;
   }

   @Override
   public final boolean isCompatible(Class<? extends IoHandle> handleType) {
      return this.handler.isCompatible(handleType);
   }

   @Override
   public final boolean isIoType(Class<? extends IoHandler> handlerType) {
      return this.handler.getClass().equals(handlerType);
   }

   @Override
   public final boolean inEventLoop(Thread thread) {
      return this.owningThread.get() == thread;
   }

   public final void setOwningThread(Thread owningThread) {
      Objects.requireNonNull(owningThread, "owningThread");
      if (!this.owningThread.compareAndSet(null, owningThread)) {
         throw new IllegalStateException("Owning thread already set");
      }
   }

   private void shutdown0(long quietPeriod, long timeout, int shutdownState) {
      boolean inEventLoop = this.inEventLoop();

      while (!this.isShuttingDown()) {
         boolean wakeup = true;
         int oldState = this.state.get();
         int newState;
         if (inEventLoop) {
            newState = shutdownState;
         } else if (oldState == 0) {
            newState = shutdownState;
         } else {
            newState = oldState;
            wakeup = false;
         }

         if (this.state.compareAndSet(oldState, newState)) {
            if (quietPeriod != -1L) {
               this.gracefulShutdownQuietPeriod = quietPeriod;
            }

            if (timeout != -1L) {
               this.gracefulShutdownTimeout = timeout;
            }

            if (wakeup) {
               this.taskQueue.offer(WAKEUP_TASK);
               this.handler.wakeup();
            }

            return;
         }
      }
   }

   @Override
   public final Future<?> shutdownGracefully(long quietPeriod, long timeout, TimeUnit unit) {
      ObjectUtil.checkPositiveOrZero(quietPeriod, "quietPeriod");
      if (timeout < quietPeriod) {
         throw new IllegalArgumentException("timeout: " + timeout + " (expected >= quietPeriod (" + quietPeriod + "))");
      } else {
         ObjectUtil.checkNotNull(unit, "unit");
         this.shutdown0(unit.toNanos(quietPeriod), unit.toNanos(timeout), 1);
         return this.terminationFuture();
      }
   }

   @Deprecated
   @Override
   public final void shutdown() {
      this.shutdown0(-1L, -1L, 2);
   }

   @Override
   public final Future<?> terminationFuture() {
      return this.terminationFuture;
   }

   @Override
   public final boolean isShuttingDown() {
      return this.state.get() >= 1;
   }

   @Override
   public final boolean isShutdown() {
      return this.state.get() >= 2;
   }

   @Override
   public final boolean isTerminated() {
      return this.state.get() == 3;
   }

   @Override
   public final boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
      return this.terminationFuture.await(timeout, unit);
   }

   @Override
   public final void execute(Runnable command) {
      Objects.requireNonNull(command, "command");
      boolean inEventLoop = this.inEventLoop();
      if (inEventLoop && this.isShutdown()) {
         throw new RejectedExecutionException("event executor terminated");
      } else {
         this.taskQueue.add(command);
         if (!inEventLoop) {
            if (this.isShutdown()) {
               boolean reject = false;

               try {
                  if (this.taskQueue.remove(command)) {
                     reject = true;
                  }
               } catch (UnsupportedOperationException var5) {
               }

               if (reject) {
                  throw new RejectedExecutionException("event executor terminated");
               }
            }

            this.handler.wakeup();
         }
      }
   }

   private boolean hasTasks() {
      return !this.taskQueue.isEmpty();
   }

   private boolean confirmShutdown() {
      if (!this.isShuttingDown()) {
         return false;
      } else if (!this.inEventLoop()) {
         throw new IllegalStateException("must be invoked from an event loop");
      } else {
         this.cancelScheduledTasks();
         if (this.gracefulShutdownStartTime == 0L) {
            this.gracefulShutdownStartTime = this.ticker.nanoTime();
         }

         if (this.runAllTasks(-1L, false) > 0) {
            return this.isShutdown() ? true : this.gracefulShutdownQuietPeriod == 0L;
         } else {
            long nanoTime = this.ticker.nanoTime();
            if (this.isShutdown() || nanoTime - this.gracefulShutdownStartTime > this.gracefulShutdownTimeout) {
               return true;
            } else if (nanoTime - this.lastExecutionTime <= this.gracefulShutdownQuietPeriod) {
               try {
                  Thread.sleep(100L);
               } catch (InterruptedException var4) {
               }

               return false;
            } else {
               return true;
            }
         }
      }
   }

   @Override
   public final <T> T invokeAny(Collection<? extends Callable<T>> tasks) throws InterruptedException, ExecutionException {
      this.throwIfInEventLoop("invokeAny");
      return super.invokeAny(tasks);
   }

   @Override
   public final <T> T invokeAny(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
      this.throwIfInEventLoop("invokeAny");
      return super.invokeAny(tasks, timeout, unit);
   }

   @Override
   public final <T> List<java.util.concurrent.Future<T>> invokeAll(Collection<? extends Callable<T>> tasks) throws InterruptedException {
      this.throwIfInEventLoop("invokeAll");
      return super.invokeAll(tasks);
   }

   @Override
   public final <T> List<java.util.concurrent.Future<T>> invokeAll(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit) throws InterruptedException {
      this.throwIfInEventLoop("invokeAll");
      return super.invokeAll(tasks, timeout, unit);
   }

   private void throwIfInEventLoop(String method) {
      if (this.inEventLoop()) {
         throw new RejectedExecutionException("Calling " + method + " from within the EventLoop is not allowed as it would deadlock");
      }
   }

   private class BlockingIoHandlerContext implements IoHandlerContext {
      long maxBlockingNanos = Long.MAX_VALUE;

      private BlockingIoHandlerContext() {
      }

      @Override
      public boolean canBlock() {
         assert ManualIoEventLoop.this.inEventLoop();

         return !ManualIoEventLoop.this.hasTasks() && !ManualIoEventLoop.this.hasScheduledTasks() && ManualIoEventLoop.this.canBlock();
      }

      @Override
      public long delayNanos(long currentTimeNanos) {
         assert ManualIoEventLoop.this.inEventLoop();

         return Math.min(this.maxBlockingNanos, ManualIoEventLoop.this.delayNanos(currentTimeNanos, this.maxBlockingNanos));
      }

      @Override
      public long deadlineNanos() {
         assert ManualIoEventLoop.this.inEventLoop();

         long next = ManualIoEventLoop.this.nextScheduledTaskDeadlineNanos();
         if (this.maxBlockingNanos == Long.MAX_VALUE) {
            return next;
         } else {
            long now = ManualIoEventLoop.this.ticker.nanoTime();
            return next != -1L && next - now <= this.maxBlockingNanos ? next : now + this.maxBlockingNanos;
         }
      }
   }
}
