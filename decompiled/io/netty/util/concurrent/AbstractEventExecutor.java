package io.netty.util.concurrent;

import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.AbstractExecutorService;
import java.util.concurrent.Callable;
import java.util.concurrent.RunnableFuture;
import java.util.concurrent.TimeUnit;
import org.jetbrains.annotations.Async.Execute;

public abstract class AbstractEventExecutor extends AbstractExecutorService implements EventExecutor {
   private static final InternalLogger logger = InternalLoggerFactory.getInstance(AbstractEventExecutor.class);
   static final long DEFAULT_SHUTDOWN_QUIET_PERIOD = 2L;
   static final long DEFAULT_SHUTDOWN_TIMEOUT = 15L;
   private final EventExecutorGroup parent;
   private final Collection<EventExecutor> selfCollection = Collections.singleton(this);

   protected AbstractEventExecutor() {
      this(null);
   }

   protected AbstractEventExecutor(EventExecutorGroup parent) {
      this.parent = parent;
   }

   @Override
   public EventExecutorGroup parent() {
      return this.parent;
   }

   @Override
   public EventExecutor next() {
      return this;
   }

   @Override
   public Iterator<EventExecutor> iterator() {
      return this.selfCollection.iterator();
   }

   @Override
   public Future<?> shutdownGracefully() {
      return this.shutdownGracefully(2L, 15L, TimeUnit.SECONDS);
   }

   @Deprecated
   @Override
   public abstract void shutdown();

   @Deprecated
   @Override
   public List<Runnable> shutdownNow() {
      this.shutdown();
      return Collections.emptyList();
   }

   @Override
   public Future<?> submit(Runnable task) {
      return (Future<?>)super.submit(task);
   }

   @Override
   public <T> Future<T> submit(Runnable task, T result) {
      return (Future<T>)super.<T>submit(task, result);
   }

   @Override
   public <T> Future<T> submit(Callable<T> task) {
      return (Future<T>)super.<T>submit(task);
   }

   @Override
   protected final <T> RunnableFuture<T> newTaskFor(Runnable runnable, T value) {
      return new PromiseTask<>(this, runnable, value);
   }

   @Override
   protected final <T> RunnableFuture<T> newTaskFor(Callable<T> callable) {
      return new PromiseTask<>(this, callable);
   }

   @Override
   public ScheduledFuture<?> schedule(Runnable command, long delay, TimeUnit unit) {
      throw new UnsupportedOperationException();
   }

   @Override
   public <V> ScheduledFuture<V> schedule(Callable<V> callable, long delay, TimeUnit unit) {
      throw new UnsupportedOperationException();
   }

   @Override
   public ScheduledFuture<?> scheduleAtFixedRate(Runnable command, long initialDelay, long period, TimeUnit unit) {
      throw new UnsupportedOperationException();
   }

   @Override
   public ScheduledFuture<?> scheduleWithFixedDelay(Runnable command, long initialDelay, long delay, TimeUnit unit) {
      throw new UnsupportedOperationException();
   }

   protected static void safeExecute(Runnable task) {
      try {
         runTask(task);
      } catch (Throwable var2) {
         logger.warn("A task raised an exception. Task: {}", task, var2);
      }
   }

   protected static void runTask(@Execute Runnable task) {
      task.run();
   }

   public void lazyExecute(Runnable task) {
      this.execute(task);
   }

   @Deprecated
   public interface LazyRunnable extends Runnable {
   }
}
