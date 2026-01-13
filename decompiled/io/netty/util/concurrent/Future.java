package io.netty.util.concurrent;

import java.util.concurrent.TimeUnit;

public interface Future<V> extends java.util.concurrent.Future<V> {
   boolean isSuccess();

   boolean isCancellable();

   Throwable cause();

   Future<V> addListener(GenericFutureListener<? extends Future<? super V>> var1);

   Future<V> addListeners(GenericFutureListener<? extends Future<? super V>>... var1);

   Future<V> removeListener(GenericFutureListener<? extends Future<? super V>> var1);

   Future<V> removeListeners(GenericFutureListener<? extends Future<? super V>>... var1);

   Future<V> sync() throws InterruptedException;

   Future<V> syncUninterruptibly();

   Future<V> await() throws InterruptedException;

   Future<V> awaitUninterruptibly();

   boolean await(long var1, TimeUnit var3) throws InterruptedException;

   boolean await(long var1) throws InterruptedException;

   boolean awaitUninterruptibly(long var1, TimeUnit var3);

   boolean awaitUninterruptibly(long var1);

   V getNow();

   @Override
   boolean cancel(boolean var1);
}
