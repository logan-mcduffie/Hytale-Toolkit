package io.netty.util.internal;

public interface LongCounter {
   void add(long var1);

   void increment();

   void decrement();

   long value();
}
