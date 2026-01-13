package io.netty.util.concurrent;

import java.util.concurrent.Callable;
import java.util.concurrent.RunnableFuture;

class PromiseTask<V> extends DefaultPromise<V> implements RunnableFuture<V> {
   private static final Runnable COMPLETED = new PromiseTask.SentinelRunnable("COMPLETED");
   private static final Runnable CANCELLED = new PromiseTask.SentinelRunnable("CANCELLED");
   private static final Runnable FAILED = new PromiseTask.SentinelRunnable("FAILED");
   private Object task;

   PromiseTask(EventExecutor executor, Runnable runnable, V result) {
      super(executor);
      this.task = result == null ? runnable : new PromiseTask.RunnableAdapter<>(runnable, result);
   }

   PromiseTask(EventExecutor executor, Runnable runnable) {
      super(executor);
      this.task = runnable;
   }

   PromiseTask(EventExecutor executor, Callable<V> callable) {
      super(executor);
      this.task = callable;
   }

   @Override
   public final int hashCode() {
      return System.identityHashCode(this);
   }

   @Override
   public final boolean equals(Object obj) {
      return this == obj;
   }

   V runTask() throws Throwable {
      Object task = this.task;
      if (task instanceof Callable) {
         return (V)((Callable)task).call();
      } else {
         ((Runnable)task).run();
         return null;
      }
   }

   @Override
   public void run() {
      try {
         if (this.setUncancellableInternal()) {
            V result = this.runTask();
            this.setSuccessInternal(result);
         }
      } catch (Throwable var2) {
         this.setFailureInternal(var2);
      }
   }

   private boolean clearTaskAfterCompletion(boolean done, Runnable result) {
      if (done) {
         this.task = result;
      }

      return done;
   }

   @Override
   public final Promise<V> setFailure(Throwable cause) {
      throw new IllegalStateException();
   }

   protected final Promise<V> setFailureInternal(Throwable cause) {
      super.setFailure(cause);
      this.clearTaskAfterCompletion(true, FAILED);
      return this;
   }

   @Override
   public final boolean tryFailure(Throwable cause) {
      return false;
   }

   protected final boolean tryFailureInternal(Throwable cause) {
      return this.clearTaskAfterCompletion(super.tryFailure(cause), FAILED);
   }

   @Override
   public final Promise<V> setSuccess(V result) {
      throw new IllegalStateException();
   }

   protected final Promise<V> setSuccessInternal(V result) {
      super.setSuccess(result);
      this.clearTaskAfterCompletion(true, COMPLETED);
      return this;
   }

   @Override
   public final boolean trySuccess(V result) {
      return false;
   }

   protected final boolean trySuccessInternal(V result) {
      return this.clearTaskAfterCompletion(super.trySuccess(result), COMPLETED);
   }

   @Override
   public final boolean setUncancellable() {
      throw new IllegalStateException();
   }

   protected final boolean setUncancellableInternal() {
      return super.setUncancellable();
   }

   @Override
   public boolean cancel(boolean mayInterruptIfRunning) {
      return this.clearTaskAfterCompletion(super.cancel(mayInterruptIfRunning), CANCELLED);
   }

   @Override
   protected StringBuilder toStringBuilder() {
      StringBuilder buf = super.toStringBuilder();
      buf.setCharAt(buf.length() - 1, ',');
      return buf.append(" task: ").append(this.task).append(')');
   }

   private static final class RunnableAdapter<T> implements Callable<T> {
      final Runnable task;
      final T result;

      RunnableAdapter(Runnable task, T result) {
         this.task = task;
         this.result = result;
      }

      @Override
      public T call() {
         this.task.run();
         return this.result;
      }

      @Override
      public String toString() {
         return "Callable(task: " + this.task + ", result: " + this.result + ')';
      }
   }

   private static class SentinelRunnable implements Runnable {
      private final String name;

      SentinelRunnable(String name) {
         this.name = name;
      }

      @Override
      public void run() {
      }

      @Override
      public String toString() {
         return this.name;
      }
   }
}
