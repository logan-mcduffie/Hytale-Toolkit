package io.netty.util.concurrent;

import io.netty.util.internal.ObjectUtil;
import java.util.LinkedHashSet;
import java.util.Set;

@Deprecated
public class PromiseAggregator<V, F extends Future<V>> implements GenericFutureListener<F> {
   private final Promise<?> aggregatePromise;
   private final boolean failPending;
   private Set<Promise<V>> pendingPromises;

   public PromiseAggregator(Promise<Void> aggregatePromise, boolean failPending) {
      this.aggregatePromise = ObjectUtil.checkNotNull(aggregatePromise, "aggregatePromise");
      this.failPending = failPending;
   }

   public PromiseAggregator(Promise<Void> aggregatePromise) {
      this(aggregatePromise, true);
   }

   @SafeVarargs
   public final PromiseAggregator<V, F> add(Promise<V>... promises) {
      ObjectUtil.checkNotNull(promises, "promises");
      if (promises.length == 0) {
         return this;
      } else {
         synchronized (this) {
            if (this.pendingPromises == null) {
               int size;
               if (promises.length > 1) {
                  size = promises.length;
               } else {
                  size = 2;
               }

               this.pendingPromises = new LinkedHashSet<>(size);
            }

            for (Promise<V> p : promises) {
               if (p != null) {
                  this.pendingPromises.add(p);
                  p.addListener(this);
               }
            }

            return this;
         }
      }
   }

   @Override
   public synchronized void operationComplete(F future) throws Exception {
      if (this.pendingPromises == null) {
         this.aggregatePromise.setSuccess(null);
      } else {
         this.pendingPromises.remove(future);
         if (!future.isSuccess()) {
            Throwable cause = future.cause();
            this.aggregatePromise.setFailure(cause);
            if (this.failPending) {
               for (Promise<V> pendingFuture : this.pendingPromises) {
                  pendingFuture.setFailure(cause);
               }
            }
         } else if (this.pendingPromises.isEmpty()) {
            this.aggregatePromise.setSuccess(null);
         }
      }
   }
}
