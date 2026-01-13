package io.netty.util.concurrent;

import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.PlatformDependent;
import io.netty.util.internal.SystemPropertyUtil;
import io.netty.util.internal.ThreadExecutorMap;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.lang.Thread.State;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;
import java.util.concurrent.atomic.AtomicLongFieldUpdater;
import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import org.jetbrains.annotations.Async.Schedule;

public abstract class SingleThreadEventExecutor extends AbstractScheduledEventExecutor implements OrderedEventExecutor {
   static final int DEFAULT_MAX_PENDING_EXECUTOR_TASKS = Math.max(16, SystemPropertyUtil.getInt("io.netty.eventexecutor.maxPendingTasks", Integer.MAX_VALUE));
   private static final InternalLogger logger = InternalLoggerFactory.getInstance(SingleThreadEventExecutor.class);
   private static final int ST_NOT_STARTED = 1;
   private static final int ST_SUSPENDING = 2;
   private static final int ST_SUSPENDED = 3;
   private static final int ST_STARTED = 4;
   private static final int ST_SHUTTING_DOWN = 5;
   private static final int ST_SHUTDOWN = 6;
   private static final int ST_TERMINATED = 7;
   private static final Runnable NOOP_TASK = new Runnable() {
      @Override
      public void run() {
      }
   };
   private static final AtomicIntegerFieldUpdater<SingleThreadEventExecutor> STATE_UPDATER = AtomicIntegerFieldUpdater.newUpdater(
      SingleThreadEventExecutor.class, "state"
   );
   private static final AtomicReferenceFieldUpdater<SingleThreadEventExecutor, ThreadProperties> PROPERTIES_UPDATER = AtomicReferenceFieldUpdater.newUpdater(
      SingleThreadEventExecutor.class, ThreadProperties.class, "threadProperties"
   );
   private static final AtomicLongFieldUpdater<SingleThreadEventExecutor> ACCUMULATED_ACTIVE_TIME_NANOS_UPDATER = AtomicLongFieldUpdater.newUpdater(
      SingleThreadEventExecutor.class, "accumulatedActiveTimeNanos"
   );
   private static final AtomicIntegerFieldUpdater<SingleThreadEventExecutor> CONSECUTIVE_IDLE_CYCLES_UPDATER = AtomicIntegerFieldUpdater.newUpdater(
      SingleThreadEventExecutor.class, "consecutiveIdleCycles"
   );
   private static final AtomicIntegerFieldUpdater<SingleThreadEventExecutor> CONSECUTIVE_BUSY_CYCLES_UPDATER = AtomicIntegerFieldUpdater.newUpdater(
      SingleThreadEventExecutor.class, "consecutiveBusyCycles"
   );
   private final Queue<Runnable> taskQueue;
   private volatile Thread thread;
   private volatile ThreadProperties threadProperties;
   private final Executor executor;
   private volatile boolean interrupted;
   private final Lock processingLock = new ReentrantLock();
   private final CountDownLatch threadLock = new CountDownLatch(1);
   private final Set<Runnable> shutdownHooks = new LinkedHashSet<>();
   private final boolean addTaskWakesUp;
   private final int maxPendingTasks;
   private final RejectedExecutionHandler rejectedExecutionHandler;
   private final boolean supportSuspension;
   private volatile long accumulatedActiveTimeNanos;
   private volatile long lastActivityTimeNanos;
   private volatile int consecutiveIdleCycles;
   private volatile int consecutiveBusyCycles;
   private long lastExecutionTime;
   private volatile int state = 1;
   private volatile long gracefulShutdownQuietPeriod;
   private volatile long gracefulShutdownTimeout;
   private long gracefulShutdownStartTime;
   private final Promise<?> terminationFuture = new DefaultPromise(GlobalEventExecutor.INSTANCE);
   private static final long SCHEDULE_PURGE_INTERVAL = TimeUnit.SECONDS.toNanos(1L);

   protected SingleThreadEventExecutor(EventExecutorGroup parent, ThreadFactory threadFactory, boolean addTaskWakesUp) {
      this(parent, new ThreadPerTaskExecutor(threadFactory), addTaskWakesUp);
   }

   protected SingleThreadEventExecutor(
      EventExecutorGroup parent, ThreadFactory threadFactory, boolean addTaskWakesUp, int maxPendingTasks, RejectedExecutionHandler rejectedHandler
   ) {
      this(parent, new ThreadPerTaskExecutor(threadFactory), addTaskWakesUp, maxPendingTasks, rejectedHandler);
   }

   protected SingleThreadEventExecutor(
      EventExecutorGroup parent,
      ThreadFactory threadFactory,
      boolean addTaskWakesUp,
      boolean supportSuspension,
      int maxPendingTasks,
      RejectedExecutionHandler rejectedHandler
   ) {
      this(parent, new ThreadPerTaskExecutor(threadFactory), addTaskWakesUp, supportSuspension, maxPendingTasks, rejectedHandler);
   }

   protected SingleThreadEventExecutor(EventExecutorGroup parent, Executor executor, boolean addTaskWakesUp) {
      this(parent, executor, addTaskWakesUp, DEFAULT_MAX_PENDING_EXECUTOR_TASKS, RejectedExecutionHandlers.reject());
   }

   protected SingleThreadEventExecutor(
      EventExecutorGroup parent, Executor executor, boolean addTaskWakesUp, int maxPendingTasks, RejectedExecutionHandler rejectedHandler
   ) {
      this(parent, executor, addTaskWakesUp, false, maxPendingTasks, rejectedHandler);
   }

   protected SingleThreadEventExecutor(
      EventExecutorGroup parent,
      Executor executor,
      boolean addTaskWakesUp,
      boolean supportSuspension,
      int maxPendingTasks,
      RejectedExecutionHandler rejectedHandler
   ) {
      super(parent);
      this.addTaskWakesUp = addTaskWakesUp;
      this.supportSuspension = supportSuspension;
      this.maxPendingTasks = Math.max(16, maxPendingTasks);
      this.executor = ThreadExecutorMap.apply(executor, this);
      this.taskQueue = this.newTaskQueue(this.maxPendingTasks);
      this.rejectedExecutionHandler = ObjectUtil.checkNotNull(rejectedHandler, "rejectedHandler");
      this.lastActivityTimeNanos = this.ticker().nanoTime();
   }

   protected SingleThreadEventExecutor(
      EventExecutorGroup parent, Executor executor, boolean addTaskWakesUp, Queue<Runnable> taskQueue, RejectedExecutionHandler rejectedHandler
   ) {
      this(parent, executor, addTaskWakesUp, false, taskQueue, rejectedHandler);
   }

   protected SingleThreadEventExecutor(
      EventExecutorGroup parent,
      Executor executor,
      boolean addTaskWakesUp,
      boolean supportSuspension,
      Queue<Runnable> taskQueue,
      RejectedExecutionHandler rejectedHandler
   ) {
      super(parent);
      this.addTaskWakesUp = addTaskWakesUp;
      this.supportSuspension = supportSuspension;
      this.maxPendingTasks = DEFAULT_MAX_PENDING_EXECUTOR_TASKS;
      this.executor = ThreadExecutorMap.apply(executor, this);
      this.taskQueue = ObjectUtil.checkNotNull(taskQueue, "taskQueue");
      this.rejectedExecutionHandler = ObjectUtil.checkNotNull(rejectedHandler, "rejectedHandler");
   }

   @Deprecated
   protected Queue<Runnable> newTaskQueue() {
      return this.newTaskQueue(this.maxPendingTasks);
   }

   protected Queue<Runnable> newTaskQueue(int maxPendingTasks) {
      return new LinkedBlockingQueue<>(maxPendingTasks);
   }

   protected void interruptThread() {
      Thread currentThread = this.thread;
      if (currentThread == null) {
         this.interrupted = true;
      } else {
         currentThread.interrupt();
      }
   }

   protected Runnable pollTask() {
      assert this.inEventLoop();

      return pollTaskFrom(this.taskQueue);
   }

   protected static Runnable pollTaskFrom(Queue<Runnable> taskQueue) {
      Runnable task;
      do {
         task = taskQueue.poll();
      } while (task == WAKEUP_TASK);

      return task;
   }

   protected Runnable takeTask() {
      assert this.inEventLoop();

      if (!(this.taskQueue instanceof BlockingQueue)) {
         throw new UnsupportedOperationException();
      } else {
         BlockingQueue<Runnable> taskQueue = (BlockingQueue<Runnable>)this.taskQueue;

         Runnable task;
         do {
            ScheduledFutureTask<?> scheduledTask = this.peekScheduledTask();
            if (scheduledTask == null) {
               Runnable taskx = null;

               try {
                  taskx = taskQueue.take();
                  if (taskx == WAKEUP_TASK) {
                     taskx = null;
                  }
               } catch (InterruptedException var7) {
               }

               return taskx;
            }

            long delayNanos = scheduledTask.delayNanos();
            task = null;
            if (delayNanos > 0L) {
               try {
                  task = taskQueue.poll(delayNanos, TimeUnit.NANOSECONDS);
               } catch (InterruptedException var8) {
                  return null;
               }
            }

            if (task == null) {
               this.fetchFromScheduledTaskQueue();
               task = taskQueue.poll();
            }
         } while (task == null);

         return task == WAKEUP_TASK ? null : task;
      }
   }

   private boolean fetchFromScheduledTaskQueue() {
      return this.fetchFromScheduledTaskQueue(this.taskQueue);
   }

   private boolean executeExpiredScheduledTasks() {
      if (this.scheduledTaskQueue != null && !this.scheduledTaskQueue.isEmpty()) {
         long nanoTime = this.getCurrentTimeNanos();
         Runnable scheduledTask = this.pollScheduledTask(nanoTime);
         if (scheduledTask == null) {
            return false;
         } else {
            do {
               safeExecute(scheduledTask);
            } while ((scheduledTask = this.pollScheduledTask(nanoTime)) != null);

            return true;
         }
      } else {
         return false;
      }
   }

   protected Runnable peekTask() {
      assert this.inEventLoop();

      return this.taskQueue.peek();
   }

   protected boolean hasTasks() {
      assert this.inEventLoop();

      return !this.taskQueue.isEmpty();
   }

   public int pendingTasks() {
      return this.taskQueue.size();
   }

   protected void addTask(Runnable task) {
      ObjectUtil.checkNotNull(task, "task");
      if (!this.offerTask(task)) {
         this.reject(task);
      }
   }

   final boolean offerTask(Runnable task) {
      if (this.isShutdown()) {
         reject();
      }

      return this.taskQueue.offer(task);
   }

   protected boolean removeTask(Runnable task) {
      return this.taskQueue.remove(ObjectUtil.checkNotNull(task, "task"));
   }

   protected boolean runAllTasks() {
      assert this.inEventLoop();

      boolean ranAtLeastOne = false;

      boolean fetchedAll;
      do {
         fetchedAll = this.fetchFromScheduledTaskQueue(this.taskQueue);
         if (this.runAllTasksFrom(this.taskQueue)) {
            ranAtLeastOne = true;
         }
      } while (!fetchedAll);

      if (ranAtLeastOne) {
         this.lastExecutionTime = this.getCurrentTimeNanos();
      }

      this.afterRunningAllTasks();
      return ranAtLeastOne;
   }

   protected final boolean runScheduledAndExecutorTasks(int maxDrainAttempts) {
      assert this.inEventLoop();

      int drainAttempt = 0;

      boolean ranAtLeastOneTask;
      do {
         ranAtLeastOneTask = this.runExistingTasksFrom(this.taskQueue) | this.executeExpiredScheduledTasks();
      } while (ranAtLeastOneTask && ++drainAttempt < maxDrainAttempts);

      if (drainAttempt > 0) {
         this.lastExecutionTime = this.getCurrentTimeNanos();
      }

      this.afterRunningAllTasks();
      return drainAttempt > 0;
   }

   protected final boolean runAllTasksFrom(Queue<Runnable> taskQueue) {
      Runnable task = pollTaskFrom(taskQueue);
      if (task == null) {
         return false;
      } else {
         do {
            safeExecute(task);
            task = pollTaskFrom(taskQueue);
         } while (task != null);

         return true;
      }
   }

   private boolean runExistingTasksFrom(Queue<Runnable> taskQueue) {
      Runnable task = pollTaskFrom(taskQueue);
      if (task == null) {
         return false;
      } else {
         int remaining = Math.min(this.maxPendingTasks, taskQueue.size());
         safeExecute(task);

         while (remaining-- > 0 && (task = taskQueue.poll()) != null) {
            safeExecute(task);
         }

         return true;
      }
   }

   protected boolean runAllTasks(long timeoutNanos) {
      this.fetchFromScheduledTaskQueue(this.taskQueue);
      Runnable task = this.pollTask();
      if (task == null) {
         this.afterRunningAllTasks();
         return false;
      } else {
         long deadline = timeoutNanos > 0L ? this.getCurrentTimeNanos() + timeoutNanos : 0L;
         long runTasks = 0L;
         long workStartTime = this.ticker().nanoTime();

         long lastExecutionTime;
         while (true) {
            safeExecute(task);
            if ((++runTasks & 63L) == 0L) {
               lastExecutionTime = this.getCurrentTimeNanos();
               if (lastExecutionTime >= deadline) {
                  break;
               }
            }

            task = this.pollTask();
            if (task == null) {
               lastExecutionTime = this.getCurrentTimeNanos();
               break;
            }
         }

         long workEndTime = this.ticker().nanoTime();
         this.accumulatedActiveTimeNanos += workEndTime - workStartTime;
         this.lastActivityTimeNanos = workEndTime;
         this.afterRunningAllTasks();
         this.lastExecutionTime = lastExecutionTime;
         return true;
      }
   }

   protected void afterRunningAllTasks() {
   }

   protected long delayNanos(long currentTimeNanos) {
      currentTimeNanos -= this.ticker().initialNanoTime();
      ScheduledFutureTask<?> scheduledTask = this.peekScheduledTask();
      return scheduledTask == null ? SCHEDULE_PURGE_INTERVAL : scheduledTask.delayNanos(currentTimeNanos);
   }

   protected long deadlineNanos() {
      ScheduledFutureTask<?> scheduledTask = this.peekScheduledTask();
      return scheduledTask == null ? this.getCurrentTimeNanos() + SCHEDULE_PURGE_INTERVAL : scheduledTask.deadlineNanos();
   }

   protected void updateLastExecutionTime() {
      long now = this.getCurrentTimeNanos();
      this.lastExecutionTime = now;
      this.lastActivityTimeNanos = now;
   }

   protected int getNumOfRegisteredChannels() {
      return -1;
   }

   protected void reportActiveIoTime(long nanos) {
      assert this.inEventLoop();

      if (nanos > 0L) {
         this.accumulatedActiveTimeNanos += nanos;
         this.lastActivityTimeNanos = this.ticker().nanoTime();
      }
   }

   protected long getAndResetAccumulatedActiveTimeNanos() {
      return ACCUMULATED_ACTIVE_TIME_NANOS_UPDATER.getAndSet(this, 0L);
   }

   protected long getLastActivityTimeNanos() {
      return this.lastActivityTimeNanos;
   }

   protected int getAndIncrementIdleCycles() {
      return CONSECUTIVE_IDLE_CYCLES_UPDATER.getAndIncrement(this);
   }

   protected void resetIdleCycles() {
      CONSECUTIVE_IDLE_CYCLES_UPDATER.set(this, 0);
   }

   protected int getAndIncrementBusyCycles() {
      return CONSECUTIVE_BUSY_CYCLES_UPDATER.getAndIncrement(this);
   }

   protected void resetBusyCycles() {
      CONSECUTIVE_BUSY_CYCLES_UPDATER.set(this, 0);
   }

   protected boolean isSuspensionSupported() {
      return this.supportSuspension;
   }

   protected abstract void run();

   protected void cleanup() {
   }

   protected void wakeup(boolean inEventLoop) {
      if (!inEventLoop) {
         this.taskQueue.offer(WAKEUP_TASK);
      }
   }

   @Override
   public boolean inEventLoop(Thread thread) {
      return thread == this.thread;
   }

   public void addShutdownHook(final Runnable task) {
      if (this.inEventLoop()) {
         this.shutdownHooks.add(task);
      } else {
         this.execute(new Runnable() {
            @Override
            public void run() {
               SingleThreadEventExecutor.this.shutdownHooks.add(task);
            }
         });
      }
   }

   public void removeShutdownHook(final Runnable task) {
      if (this.inEventLoop()) {
         this.shutdownHooks.remove(task);
      } else {
         this.execute(new Runnable() {
            @Override
            public void run() {
               SingleThreadEventExecutor.this.shutdownHooks.remove(task);
            }
         });
      }
   }

   private boolean runShutdownHooks() {
      boolean ran = false;

      while (!this.shutdownHooks.isEmpty()) {
         List<Runnable> copy = new ArrayList<>(this.shutdownHooks);
         this.shutdownHooks.clear();

         for (Runnable task : copy) {
            try {
               runTask(task);
            } catch (Throwable var9) {
               logger.warn("Shutdown hook raised an exception.", var9);
            } finally {
               ran = true;
            }
         }
      }

      if (ran) {
         this.lastExecutionTime = this.getCurrentTimeNanos();
      }

      return ran;
   }

   private void shutdown0(long quietPeriod, long timeout, int shutdownState) {
      if (!this.isShuttingDown()) {
         boolean inEventLoop = this.inEventLoop();

         while (!this.isShuttingDown()) {
            boolean wakeup = true;
            int oldState = this.state;
            int newState;
            if (inEventLoop) {
               newState = shutdownState;
            } else {
               switch (oldState) {
                  case 1:
                  case 2:
                  case 3:
                  case 4:
                     newState = shutdownState;
                     break;
                  default:
                     newState = oldState;
                     wakeup = false;
               }
            }

            if (STATE_UPDATER.compareAndSet(this, oldState, newState)) {
               if (quietPeriod != -1L) {
                  this.gracefulShutdownQuietPeriod = quietPeriod;
               }

               if (timeout != -1L) {
                  this.gracefulShutdownTimeout = timeout;
               }

               if (this.ensureThreadStarted(oldState)) {
                  return;
               }

               if (wakeup) {
                  this.taskQueue.offer(WAKEUP_TASK);
                  if (!this.addTaskWakesUp) {
                     this.wakeup(inEventLoop);
                  }
               }

               return;
            }
         }
      }
   }

   @Override
   public Future<?> shutdownGracefully(long quietPeriod, long timeout, TimeUnit unit) {
      ObjectUtil.checkPositiveOrZero(quietPeriod, "quietPeriod");
      if (timeout < quietPeriod) {
         throw new IllegalArgumentException("timeout: " + timeout + " (expected >= quietPeriod (" + quietPeriod + "))");
      } else {
         ObjectUtil.checkNotNull(unit, "unit");
         this.shutdown0(unit.toNanos(quietPeriod), unit.toNanos(timeout), 5);
         return this.terminationFuture();
      }
   }

   @Override
   public Future<?> terminationFuture() {
      return this.terminationFuture;
   }

   @Deprecated
   @Override
   public void shutdown() {
      this.shutdown0(-1L, -1L, 6);
   }

   @Override
   public boolean isShuttingDown() {
      return this.state >= 5;
   }

   @Override
   public boolean isShutdown() {
      return this.state >= 6;
   }

   @Override
   public boolean isTerminated() {
      return this.state == 7;
   }

   @Override
   public boolean isSuspended() {
      int currentState = this.state;
      return currentState == 3 || currentState == 2;
   }

   @Override
   public boolean trySuspend() {
      if (!this.supportSuspension) {
         return false;
      } else if (STATE_UPDATER.compareAndSet(this, 4, 2)) {
         this.wakeup(this.inEventLoop());
         return true;
      } else if (STATE_UPDATER.compareAndSet(this, 1, 3)) {
         return true;
      } else {
         int currentState = this.state;
         return currentState == 3 || currentState == 2;
      }
   }

   protected boolean canSuspend() {
      return this.canSuspend(this.state);
   }

   protected boolean canSuspend(int state) {
      assert this.inEventLoop();

      return this.supportSuspension && (state == 3 || state == 2) && !this.hasTasks() && this.nextScheduledTaskDeadlineNanos() == -1L;
   }

   protected boolean confirmShutdown() {
      if (!this.isShuttingDown()) {
         return false;
      } else if (!this.inEventLoop()) {
         throw new IllegalStateException("must be invoked from an event loop");
      } else {
         this.cancelScheduledTasks();
         if (this.gracefulShutdownStartTime == 0L) {
            this.gracefulShutdownStartTime = this.getCurrentTimeNanos();
         }

         if (!this.runAllTasks() && !this.runShutdownHooks()) {
            long nanoTime = this.getCurrentTimeNanos();
            if (this.isShutdown() || nanoTime - this.gracefulShutdownStartTime > this.gracefulShutdownTimeout) {
               return true;
            } else if (nanoTime - this.lastExecutionTime <= this.gracefulShutdownQuietPeriod) {
               this.taskQueue.offer(WAKEUP_TASK);

               try {
                  Thread.sleep(100L);
               } catch (InterruptedException var4) {
               }

               return false;
            } else {
               return true;
            }
         } else if (this.isShutdown()) {
            return true;
         } else if (this.gracefulShutdownQuietPeriod == 0L) {
            return true;
         } else {
            this.taskQueue.offer(WAKEUP_TASK);
            return false;
         }
      }
   }

   @Override
   public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
      ObjectUtil.checkNotNull(unit, "unit");
      if (this.inEventLoop()) {
         throw new IllegalStateException("cannot await termination of the current thread");
      } else {
         this.threadLock.await(timeout, unit);
         return this.isTerminated();
      }
   }

   @Override
   public void execute(Runnable task) {
      this.execute0(task);
   }

   @Override
   public void lazyExecute(Runnable task) {
      this.lazyExecute0(task);
   }

   private void execute0(@Schedule Runnable task) {
      ObjectUtil.checkNotNull(task, "task");
      this.execute(task, this.wakesUpForTask(task));
   }

   private void lazyExecute0(@Schedule Runnable task) {
      this.execute(ObjectUtil.checkNotNull(task, "task"), false);
   }

   @Override
   void scheduleRemoveScheduled(final ScheduledFutureTask<?> task) {
      ObjectUtil.checkNotNull(task, "task");
      int currentState = this.state;
      if (this.supportSuspension && currentState == 3) {
         this.execute(new Runnable() {
            @Override
            public void run() {
               task.run();
               if (SingleThreadEventExecutor.this.canSuspend(3)) {
                  SingleThreadEventExecutor.this.trySuspend();
               }
            }
         }, true);
      } else {
         this.execute(task, false);
      }
   }

   private void execute(Runnable task, boolean immediate) {
      boolean inEventLoop = this.inEventLoop();
      this.addTask(task);
      if (!inEventLoop) {
         this.startThread();
         if (this.isShutdown()) {
            boolean reject = false;

            try {
               if (this.removeTask(task)) {
                  reject = true;
               }
            } catch (UnsupportedOperationException var6) {
            }

            if (reject) {
               reject();
            }
         }
      }

      if (!this.addTaskWakesUp && immediate) {
         this.wakeup(inEventLoop);
      }
   }

   @Override
   public <T> T invokeAny(Collection<? extends Callable<T>> tasks) throws InterruptedException, ExecutionException {
      this.throwIfInEventLoop("invokeAny");
      return super.invokeAny(tasks);
   }

   @Override
   public <T> T invokeAny(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
      this.throwIfInEventLoop("invokeAny");
      return super.invokeAny(tasks, timeout, unit);
   }

   @Override
   public <T> List<java.util.concurrent.Future<T>> invokeAll(Collection<? extends Callable<T>> tasks) throws InterruptedException {
      this.throwIfInEventLoop("invokeAll");
      return super.invokeAll(tasks);
   }

   @Override
   public <T> List<java.util.concurrent.Future<T>> invokeAll(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit) throws InterruptedException {
      this.throwIfInEventLoop("invokeAll");
      return super.invokeAll(tasks, timeout, unit);
   }

   private void throwIfInEventLoop(String method) {
      if (this.inEventLoop()) {
         throw new RejectedExecutionException("Calling " + method + " from within the EventLoop is not allowed");
      }
   }

   public final ThreadProperties threadProperties() {
      ThreadProperties threadProperties = this.threadProperties;
      if (threadProperties == null) {
         Thread thread = this.thread;
         if (thread == null) {
            assert !this.inEventLoop();

            this.submit(NOOP_TASK).syncUninterruptibly();
            thread = this.thread;

            assert thread != null;
         }

         threadProperties = new SingleThreadEventExecutor.DefaultThreadProperties(thread);
         if (!PROPERTIES_UPDATER.compareAndSet(this, null, threadProperties)) {
            threadProperties = this.threadProperties;
         }
      }

      return threadProperties;
   }

   protected boolean wakesUpForTask(Runnable task) {
      return true;
   }

   protected static void reject() {
      throw new RejectedExecutionException("event executor terminated");
   }

   protected final void reject(Runnable task) {
      this.rejectedExecutionHandler.rejected(task, this);
   }

   private void startThread() {
      int currentState = this.state;
      if ((currentState == 1 || currentState == 3) && STATE_UPDATER.compareAndSet(this, currentState, 4)) {
         this.resetIdleCycles();
         this.resetBusyCycles();
         boolean success = false;

         try {
            this.doStartThread();
            success = true;
         } finally {
            if (!success) {
               STATE_UPDATER.compareAndSet(this, 4, 1);
            }
         }
      }
   }

   private boolean ensureThreadStarted(int oldState) {
      if (oldState == 1 || oldState == 3) {
         try {
            this.doStartThread();
         } catch (Throwable var3) {
            STATE_UPDATER.set(this, 7);
            this.terminationFuture.tryFailure(var3);
            if (!(var3 instanceof Exception)) {
               PlatformDependent.throwException(var3);
            }

            return true;
         }
      }

      return false;
   }

   private void doStartThread() {
      this.executor
         .execute(
            new Runnable() {
               @Override
               public void run() {
                  SingleThreadEventExecutor.this.processingLock.lock();

                  assert SingleThreadEventExecutor.this.thread == null;

                  SingleThreadEventExecutor.this.thread = Thread.currentThread();
                  if (SingleThreadEventExecutor.this.interrupted) {
                     SingleThreadEventExecutor.this.thread.interrupt();
                     SingleThreadEventExecutor.this.interrupted = false;
                  }

                  boolean success = false;
                  Throwable unexpectedException = null;
                  SingleThreadEventExecutor.this.updateLastExecutionTime();
                  boolean suspend = false;

                  try {
                     while (true) {
                        SingleThreadEventExecutor.this.run();
                        success = true;
                        int currentState = SingleThreadEventExecutor.this.state;
                        if (!SingleThreadEventExecutor.this.canSuspend(currentState)) {
                           break;
                        }

                        if (SingleThreadEventExecutor.STATE_UPDATER.compareAndSet(SingleThreadEventExecutor.this, 2, 3)
                           && (
                              SingleThreadEventExecutor.this.canSuspend(3)
                                 || !SingleThreadEventExecutor.STATE_UPDATER.compareAndSet(SingleThreadEventExecutor.this, 3, 4)
                           )) {
                           suspend = true;
                           break;
                        }
                     }
                  } catch (Throwable var272) {
                     unexpectedException = var272;
                     SingleThreadEventExecutor.logger.warn("Unexpected exception from an event executor: ", var272);
                  } finally {
                     boolean shutdown = !suspend;
                     if (shutdown) {
                        int oldState;
                        do {
                           oldState = SingleThreadEventExecutor.this.state;
                        } while (oldState < 5 && !SingleThreadEventExecutor.STATE_UPDATER.compareAndSet(SingleThreadEventExecutor.this, oldState, 5));

                        if (success && SingleThreadEventExecutor.this.gracefulShutdownStartTime == 0L && SingleThreadEventExecutor.logger.isErrorEnabled()) {
                           SingleThreadEventExecutor.logger
                              .error(
                                 "Buggy "
                                    + EventExecutor.class.getSimpleName()
                                    + " implementation; "
                                    + SingleThreadEventExecutor.class.getSimpleName()
                                    + ".confirmShutdown() must be called before run() implementation terminates."
                              );
                        }
                     }

                     try {
                        if (shutdown) {
                           while (!SingleThreadEventExecutor.this.confirmShutdown()) {
                           }

                           int currentStatex;
                           do {
                              currentStatex = SingleThreadEventExecutor.this.state;
                           } while (
                              currentStatex < 6 && !SingleThreadEventExecutor.STATE_UPDATER.compareAndSet(SingleThreadEventExecutor.this, currentStatex, 6)
                           );

                           SingleThreadEventExecutor.this.confirmShutdown();
                        }
                     } finally {
                        try {
                           if (shutdown) {
                              try {
                                 SingleThreadEventExecutor.this.cleanup();
                              } finally {
                                 FastThreadLocal.removeAll();
                                 SingleThreadEventExecutor.STATE_UPDATER.set(SingleThreadEventExecutor.this, 7);
                                 SingleThreadEventExecutor.this.threadLock.countDown();
                                 int numUserTasks = SingleThreadEventExecutor.this.drainTasks();
                                 if (numUserTasks > 0 && SingleThreadEventExecutor.logger.isWarnEnabled()) {
                                    SingleThreadEventExecutor.logger.warn("An event executor terminated with non-empty task queue (" + numUserTasks + ')');
                                 }

                                 if (unexpectedException == null) {
                                    SingleThreadEventExecutor.this.terminationFuture.setSuccess(null);
                                 } else {
                                    SingleThreadEventExecutor.this.terminationFuture.setFailure(unexpectedException);
                                 }
                              }
                           } else {
                              FastThreadLocal.removeAll();
                              SingleThreadEventExecutor.this.threadProperties = null;
                           }
                        } finally {
                           SingleThreadEventExecutor.this.thread = null;
                           SingleThreadEventExecutor.this.processingLock.unlock();
                        }
                     }
                  }
               }
            }
         );
   }

   final int drainTasks() {
      int numTasks = 0;

      while (true) {
         Runnable runnable = this.taskQueue.poll();
         if (runnable == null) {
            return numTasks;
         }

         if (WAKEUP_TASK != runnable) {
            numTasks++;
         }
      }
   }

   private static final class DefaultThreadProperties implements ThreadProperties {
      private final Thread t;

      DefaultThreadProperties(Thread t) {
         this.t = t;
      }

      @Override
      public State state() {
         return this.t.getState();
      }

      @Override
      public int priority() {
         return this.t.getPriority();
      }

      @Override
      public boolean isInterrupted() {
         return this.t.isInterrupted();
      }

      @Override
      public boolean isDaemon() {
         return this.t.isDaemon();
      }

      @Override
      public String name() {
         return this.t.getName();
      }

      @Override
      public long id() {
         return this.t.getId();
      }

      @Override
      public StackTraceElement[] stackTrace() {
         return this.t.getStackTrace();
      }

      @Override
      public boolean isAlive() {
         return this.t.isAlive();
      }
   }

   @Deprecated
   protected interface NonWakeupRunnable extends AbstractEventExecutor.LazyRunnable {
   }
}
