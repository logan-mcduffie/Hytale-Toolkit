package io.netty.util.concurrent;

import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.PromiseNotificationUtil;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;

public class PromiseNotifier<V, F extends Future<V>> implements GenericFutureListener<F> {
   private static final InternalLogger logger = InternalLoggerFactory.getInstance(PromiseNotifier.class);
   private final Promise<? super V>[] promises;
   private final boolean logNotifyFailure;

   @SafeVarargs
   public PromiseNotifier(Promise<? super V>... promises) {
      this(true, promises);
   }

   @SafeVarargs
   public PromiseNotifier(boolean logNotifyFailure, Promise<? super V>... promises) {
      ObjectUtil.checkNotNull(promises, "promises");

      for (Promise<? super V> promise : promises) {
         ObjectUtil.checkNotNullWithIAE(promise, "promise");
      }

      this.promises = (Promise<? super V>[])promises.clone();
      this.logNotifyFailure = logNotifyFailure;
   }

   public static <V, F extends Future<V>> F cascade(F future, Promise<? super V> promise) {
      return cascade(true, future, promise);
   }

   public static <V, F extends Future<V>> F cascade(boolean logNotifyFailure, final F future, final Promise<? super V> promise) {
      promise.addListener(new FutureListener() {
         @Override
         public void operationComplete(Future f) {
            if (f.isCancelled()) {
               future.cancel(false);
            }
         }
      });
      future.addListener(new PromiseNotifier(logNotifyFailure, new Promise[]{promise}) {
         @Override
         public void operationComplete(Future f) throws Exception {
            if (!promise.isCancelled() || !f.isCancelled()) {
               super.operationComplete(future);
            }
         }
      });
      return future;
   }

   @Override
   public void operationComplete(F future) throws Exception {
      InternalLogger internalLogger = this.logNotifyFailure ? logger : null;
      if (future.isSuccess()) {
         V result = future.get();

         for (Promise<? super V> p : this.promises) {
            PromiseNotificationUtil.trySuccess(p, result, internalLogger);
         }
      } else if (future.isCancelled()) {
         for (Promise<? super V> p : this.promises) {
            PromiseNotificationUtil.tryCancel(p, internalLogger);
         }
      } else {
         Throwable cause = future.cause();

         for (Promise<? super V> p : this.promises) {
            PromiseNotificationUtil.tryFailure(p, cause, internalLogger);
         }
      }
   }
}
