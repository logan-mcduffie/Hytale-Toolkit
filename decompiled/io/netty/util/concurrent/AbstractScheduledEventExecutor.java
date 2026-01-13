package io.netty.util.concurrent;

import io.netty.util.internal.DefaultPriorityQueue;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.PriorityQueue;
import java.util.Comparator;
import java.util.Objects;
import java.util.Queue;
import java.util.concurrent.Callable;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

public abstract class AbstractScheduledEventExecutor extends AbstractEventExecutor {
   private static final Comparator<ScheduledFutureTask<?>> SCHEDULED_FUTURE_TASK_COMPARATOR = new Comparator<ScheduledFutureTask<?>>() {
      public int compare(ScheduledFutureTask<?> o1, ScheduledFutureTask<?> o2) {
         return o1.compareTo((Delayed)o2);
      }
   };
   static final Runnable WAKEUP_TASK = new Runnable() {
      @Override
      public void run() {
      }
   };
   PriorityQueue<ScheduledFutureTask<?>> scheduledTaskQueue;
   long nextTaskId;

   protected AbstractScheduledEventExecutor() {
   }

   protected AbstractScheduledEventExecutor(EventExecutorGroup parent) {
      super(parent);
   }

   @Override
   public Ticker ticker() {
      return Ticker.systemTicker();
   }

   @Deprecated
   protected long getCurrentTimeNanos() {
      return this.ticker().nanoTime();
   }

   @Deprecated
   protected static long nanoTime() {
      return Ticker.systemTicker().nanoTime();
   }

   @Deprecated
   static long defaultCurrentTimeNanos() {
      return Ticker.systemTicker().nanoTime();
   }

   static long deadlineNanos(long nanoTime, long delay) {
      long deadlineNanos = nanoTime + delay;
      return deadlineNanos < 0L ? Long.MAX_VALUE : deadlineNanos;
   }

   @Deprecated
   protected static long deadlineToDelayNanos(long deadlineNanos) {
      return ScheduledFutureTask.deadlineToDelayNanos(defaultCurrentTimeNanos(), deadlineNanos);
   }

   protected long delayNanos(long currentTimeNanos, long scheduledPurgeInterval) {
      currentTimeNanos -= this.ticker().initialNanoTime();
      ScheduledFutureTask<?> scheduledTask = this.peekScheduledTask();
      return scheduledTask == null ? scheduledPurgeInterval : scheduledTask.delayNanos(currentTimeNanos);
   }

   @Deprecated
   protected static long initialNanoTime() {
      return Ticker.systemTicker().initialNanoTime();
   }

   PriorityQueue<ScheduledFutureTask<?>> scheduledTaskQueue() {
      if (this.scheduledTaskQueue == null) {
         this.scheduledTaskQueue = new DefaultPriorityQueue<>(SCHEDULED_FUTURE_TASK_COMPARATOR, 11);
      }

      return this.scheduledTaskQueue;
   }

   private static boolean isNullOrEmpty(Queue<ScheduledFutureTask<?>> queue) {
      return queue == null || queue.isEmpty();
   }

   protected void cancelScheduledTasks() {
      assert this.inEventLoop();

      PriorityQueue<ScheduledFutureTask<?>> scheduledTaskQueue = this.scheduledTaskQueue;
      if (!isNullOrEmpty(scheduledTaskQueue)) {
         ScheduledFutureTask<?>[] scheduledTasks = scheduledTaskQueue.toArray(new ScheduledFutureTask[0]);

         for (ScheduledFutureTask<?> task : scheduledTasks) {
            task.cancelWithoutRemove(false);
         }

         scheduledTaskQueue.clearIgnoringIndexes();
      }
   }

   protected final Runnable pollScheduledTask() {
      return this.pollScheduledTask(this.getCurrentTimeNanos());
   }

   protected boolean fetchFromScheduledTaskQueue(Queue<Runnable> taskQueue) {
      assert this.inEventLoop();

      Objects.requireNonNull(taskQueue, "taskQueue");
      if (this.scheduledTaskQueue != null && !this.scheduledTaskQueue.isEmpty()) {
         long nanoTime = this.getCurrentTimeNanos();

         ScheduledFutureTask scheduledTask;
         do {
            scheduledTask = (ScheduledFutureTask)this.pollScheduledTask(nanoTime);
            if (scheduledTask == null) {
               return true;
            }
         } while (scheduledTask.isCancelled() || taskQueue.offer(scheduledTask));

         this.scheduledTaskQueue.add(scheduledTask);
         return false;
      } else {
         return true;
      }
   }

   protected final Runnable pollScheduledTask(long nanoTime) {
      assert this.inEventLoop();

      ScheduledFutureTask<?> scheduledTask = this.peekScheduledTask();
      if (scheduledTask != null && scheduledTask.deadlineNanos() - nanoTime <= 0L) {
         this.scheduledTaskQueue.remove();
         scheduledTask.setConsumed();
         return scheduledTask;
      } else {
         return null;
      }
   }

   protected final long nextScheduledTaskNano() {
      ScheduledFutureTask<?> scheduledTask = this.peekScheduledTask();
      return scheduledTask != null ? scheduledTask.delayNanos() : -1L;
   }

   protected final long nextScheduledTaskDeadlineNanos() {
      ScheduledFutureTask<?> scheduledTask = this.peekScheduledTask();
      return scheduledTask != null ? scheduledTask.deadlineNanos() : -1L;
   }

   final ScheduledFutureTask<?> peekScheduledTask() {
      Queue<ScheduledFutureTask<?>> scheduledTaskQueue = this.scheduledTaskQueue;
      return scheduledTaskQueue != null ? scheduledTaskQueue.peek() : null;
   }

   protected final boolean hasScheduledTasks() {
      ScheduledFutureTask<?> scheduledTask = this.peekScheduledTask();
      return scheduledTask != null && scheduledTask.deadlineNanos() <= this.getCurrentTimeNanos();
   }

   @Override
   public ScheduledFuture<?> schedule(Runnable command, long delay, TimeUnit unit) {
      ObjectUtil.checkNotNull(command, "command");
      ObjectUtil.checkNotNull(unit, "unit");
      if (delay < 0L) {
         delay = 0L;
      }

      this.validateScheduled0(delay, unit);
      return this.schedule(new ScheduledFutureTask(this, command, deadlineNanos(this.getCurrentTimeNanos(), unit.toNanos(delay))));
   }

   @Override
   public <V> ScheduledFuture<V> schedule(Callable<V> callable, long delay, TimeUnit unit) {
      ObjectUtil.checkNotNull(callable, "callable");
      ObjectUtil.checkNotNull(unit, "unit");
      if (delay < 0L) {
         delay = 0L;
      }

      this.validateScheduled0(delay, unit);
      return this.schedule(new ScheduledFutureTask<>(this, callable, deadlineNanos(this.getCurrentTimeNanos(), unit.toNanos(delay))));
   }

   @Override
   public ScheduledFuture<?> scheduleAtFixedRate(Runnable command, long initialDelay, long period, TimeUnit unit) {
      ObjectUtil.checkNotNull(command, "command");
      ObjectUtil.checkNotNull(unit, "unit");
      if (initialDelay < 0L) {
         throw new IllegalArgumentException(String.format("initialDelay: %d (expected: >= 0)", initialDelay));
      } else if (period <= 0L) {
         throw new IllegalArgumentException(String.format("period: %d (expected: > 0)", period));
      } else {
         this.validateScheduled0(initialDelay, unit);
         this.validateScheduled0(period, unit);
         return this.schedule(
            new ScheduledFutureTask(this, command, deadlineNanos(this.getCurrentTimeNanos(), unit.toNanos(initialDelay)), unit.toNanos(period))
         );
      }
   }

   @Override
   public ScheduledFuture<?> scheduleWithFixedDelay(Runnable command, long initialDelay, long delay, TimeUnit unit) {
      ObjectUtil.checkNotNull(command, "command");
      ObjectUtil.checkNotNull(unit, "unit");
      if (initialDelay < 0L) {
         throw new IllegalArgumentException(String.format("initialDelay: %d (expected: >= 0)", initialDelay));
      } else if (delay <= 0L) {
         throw new IllegalArgumentException(String.format("delay: %d (expected: > 0)", delay));
      } else {
         this.validateScheduled0(initialDelay, unit);
         this.validateScheduled0(delay, unit);
         return this.schedule(
            new ScheduledFutureTask(this, command, deadlineNanos(this.getCurrentTimeNanos(), unit.toNanos(initialDelay)), -unit.toNanos(delay))
         );
      }
   }

   private void validateScheduled0(long amount, TimeUnit unit) {
      this.validateScheduled(amount, unit);
   }

   @Deprecated
   protected void validateScheduled(long amount, TimeUnit unit) {
   }

   final void scheduleFromEventLoop(ScheduledFutureTask<?> task) {
      if (task.getId() == 0L) {
         task.setId(++this.nextTaskId);
      }

      this.scheduledTaskQueue().add(task);
   }

   private <V> ScheduledFuture<V> schedule(ScheduledFutureTask<V> task) {
      if (this.inEventLoop()) {
         this.scheduleFromEventLoop(task);
      } else {
         long deadlineNanos = task.deadlineNanos();
         if (this.beforeScheduledTaskSubmitted(deadlineNanos)) {
            this.execute(task);
         } else {
            this.lazyExecute(task);
            if (this.afterScheduledTaskSubmitted(deadlineNanos)) {
               this.execute(WAKEUP_TASK);
            }
         }
      }

      return task;
   }

   final void removeScheduled(ScheduledFutureTask<?> task) {
      assert task.isCancelled();

      if (this.inEventLoop()) {
         this.scheduledTaskQueue().removeTyped(task);
      } else {
         this.scheduleRemoveScheduled(task);
      }
   }

   void scheduleRemoveScheduled(ScheduledFutureTask<?> task) {
      this.lazyExecute(task);
   }

   protected boolean beforeScheduledTaskSubmitted(long deadlineNanos) {
      return true;
   }

   protected boolean afterScheduledTaskSubmitted(long deadlineNanos) {
      return true;
   }
}
