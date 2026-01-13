package io.netty.util.concurrent;

import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;

@Deprecated
public final class UnaryPromiseNotifier<T> implements FutureListener<T> {
   private static final InternalLogger logger = InternalLoggerFactory.getInstance(UnaryPromiseNotifier.class);
   private final Promise<? super T> promise;

   public UnaryPromiseNotifier(Promise<? super T> promise) {
      this.promise = ObjectUtil.checkNotNull(promise, "promise");
   }

   @Override
   public void operationComplete(Future<T> future) throws Exception {
      cascadeTo(future, this.promise);
   }

   public static <X> void cascadeTo(Future<X> completedFuture, Promise<? super X> promise) {
      if (completedFuture.isSuccess()) {
         if (!promise.trySuccess(completedFuture.getNow())) {
            logger.warn("Failed to mark a promise as success because it is done already: {}", promise);
         }
      } else if (completedFuture.isCancelled()) {
         if (!promise.cancel(false)) {
            logger.warn("Failed to cancel a promise because it is done already: {}", promise);
         }
      } else if (!promise.tryFailure(completedFuture.cause())) {
         logger.warn("Failed to mark a promise as failure because it's done already: {}", promise, completedFuture.cause());
      }
   }
}
