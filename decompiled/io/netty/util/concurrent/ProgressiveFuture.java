package io.netty.util.concurrent;

public interface ProgressiveFuture<V> extends Future<V> {
   ProgressiveFuture<V> addListener(GenericFutureListener<? extends Future<? super V>> var1);

   ProgressiveFuture<V> addListeners(GenericFutureListener<? extends Future<? super V>>... var1);

   ProgressiveFuture<V> removeListener(GenericFutureListener<? extends Future<? super V>> var1);

   ProgressiveFuture<V> removeListeners(GenericFutureListener<? extends Future<? super V>>... var1);

   ProgressiveFuture<V> sync() throws InterruptedException;

   ProgressiveFuture<V> syncUninterruptibly();

   ProgressiveFuture<V> await() throws InterruptedException;

   ProgressiveFuture<V> awaitUninterruptibly();
}
