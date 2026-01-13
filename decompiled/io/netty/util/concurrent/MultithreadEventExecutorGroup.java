package io.netty.util.concurrent;

import io.netty.util.internal.ObjectUtil;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class MultithreadEventExecutorGroup extends AbstractEventExecutorGroup {
   private final EventExecutor[] children;
   private final Set<EventExecutor> readonlyChildren;
   private final AtomicInteger terminatedChildren = new AtomicInteger();
   private final Promise<?> terminationFuture = new DefaultPromise(GlobalEventExecutor.INSTANCE);
   private final EventExecutorChooserFactory.EventExecutorChooser chooser;

   protected MultithreadEventExecutorGroup(int nThreads, ThreadFactory threadFactory, Object... args) {
      this(nThreads, threadFactory == null ? null : new ThreadPerTaskExecutor(threadFactory), args);
   }

   protected MultithreadEventExecutorGroup(int nThreads, Executor executor, Object... args) {
      this(nThreads, executor, DefaultEventExecutorChooserFactory.INSTANCE, args);
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   protected MultithreadEventExecutorGroup(int nThreads, Executor executor, EventExecutorChooserFactory chooserFactory, Object... args) {
      ObjectUtil.checkPositive(nThreads, "nThreads");
      if (executor == null) {
         executor = new ThreadPerTaskExecutor(this.newDefaultThreadFactory());
      }

      this.children = new EventExecutor[nThreads];

      for (int i = 0; i < nThreads; i++) {
         boolean success = false;
         boolean var18 = false /* VF: Semaphore variable */;

         try {
            var18 = true;
            this.children[i] = this.newChild(executor, args);
            success = true;
            var18 = false;
         } catch (Exception var19) {
            throw new IllegalStateException("failed to create a child event loop", var19);
         } finally {
            if (var18) {
               if (!success) {
                  for (int j = 0; j < i; j++) {
                     this.children[j].shutdownGracefully();
                  }

                  for (int j = 0; j < i; j++) {
                     EventExecutor e = this.children[j];

                     try {
                        while (!e.isTerminated()) {
                           e.awaitTermination(2147483647L, TimeUnit.SECONDS);
                        }
                     } catch (InterruptedException var20) {
                        Thread.currentThread().interrupt();
                        break;
                     }
                  }
               }
            }
         }

         if (!success) {
            for (int j = 0; j < i; j++) {
               this.children[j].shutdownGracefully();
            }

            for (int j = 0; j < i; j++) {
               EventExecutor e = this.children[j];

               try {
                  while (!e.isTerminated()) {
                     e.awaitTermination(2147483647L, TimeUnit.SECONDS);
                  }
               } catch (InterruptedException var22) {
                  Thread.currentThread().interrupt();
                  break;
               }
            }
         }
      }

      this.chooser = chooserFactory.newChooser(this.children);
      FutureListener<Object> terminationListener = new FutureListener<Object>() {
         @Override
         public void operationComplete(Future<Object> future) throws Exception {
            if (MultithreadEventExecutorGroup.this.terminatedChildren.incrementAndGet() == MultithreadEventExecutorGroup.this.children.length) {
               MultithreadEventExecutorGroup.this.terminationFuture.setSuccess(null);
            }
         }
      };

      for (EventExecutor e : this.children) {
         e.terminationFuture().addListener(terminationListener);
      }

      Set<EventExecutor> childrenSet = new LinkedHashSet<>(this.children.length);
      Collections.addAll(childrenSet, this.children);
      this.readonlyChildren = Collections.unmodifiableSet(childrenSet);
   }

   protected ThreadFactory newDefaultThreadFactory() {
      return new DefaultThreadFactory(this.getClass());
   }

   @Override
   public EventExecutor next() {
      return this.chooser.next();
   }

   @Override
   public Iterator<EventExecutor> iterator() {
      return this.readonlyChildren.iterator();
   }

   public final int executorCount() {
      return this.children.length;
   }

   public int activeExecutorCount() {
      return this.chooser instanceof EventExecutorChooserFactory.ObservableEventExecutorChooser
         ? ((EventExecutorChooserFactory.ObservableEventExecutorChooser)this.chooser).activeExecutorCount()
         : this.executorCount();
   }

   public List<AutoScalingEventExecutorChooserFactory.AutoScalingUtilizationMetric> executorUtilizations() {
      return this.chooser instanceof EventExecutorChooserFactory.ObservableEventExecutorChooser
         ? ((EventExecutorChooserFactory.ObservableEventExecutorChooser)this.chooser).executorUtilizations()
         : Collections.emptyList();
   }

   protected abstract EventExecutor newChild(Executor var1, Object... var2) throws Exception;

   @Override
   public Future<?> shutdownGracefully(long quietPeriod, long timeout, TimeUnit unit) {
      for (EventExecutor l : this.children) {
         l.shutdownGracefully(quietPeriod, timeout, unit);
      }

      return this.terminationFuture();
   }

   @Override
   public Future<?> terminationFuture() {
      return this.terminationFuture;
   }

   @Deprecated
   @Override
   public void shutdown() {
      for (EventExecutor l : this.children) {
         l.shutdown();
      }
   }

   @Override
   public boolean isShuttingDown() {
      for (EventExecutor l : this.children) {
         if (!l.isShuttingDown()) {
            return false;
         }
      }

      return true;
   }

   @Override
   public boolean isShutdown() {
      for (EventExecutor l : this.children) {
         if (!l.isShutdown()) {
            return false;
         }
      }

      return true;
   }

   @Override
   public boolean isTerminated() {
      for (EventExecutor l : this.children) {
         if (!l.isTerminated()) {
            return false;
         }
      }

      return true;
   }

   @Override
   public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
      long deadline = System.nanoTime() + unit.toNanos(timeout);

      long timeLeft;
      for (EventExecutor l : this.children) {
         do {
            timeLeft = deadline - System.nanoTime();
            if (timeLeft <= 0L) {
               return this.isTerminated();
            }
         } while (!l.awaitTermination(timeLeft, TimeUnit.NANOSECONDS));
      }

      return this.isTerminated();
   }
}
