package io.netty.util;

public interface Attribute<T> {
   AttributeKey<T> key();

   T get();

   void set(T var1);

   T getAndSet(T var1);

   T setIfAbsent(T var1);

   @Deprecated
   T getAndRemove();

   boolean compareAndSet(T var1, T var2);

   @Deprecated
   void remove();
}
