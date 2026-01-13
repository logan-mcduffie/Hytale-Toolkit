package io.netty.util.collection;

import java.util.Map;

public interface LongObjectMap<V> extends Map<Long, V> {
   V get(long var1);

   V put(long var1, V var3);

   V remove(long var1);

   Iterable<LongObjectMap.PrimitiveEntry<V>> entries();

   boolean containsKey(long var1);

   public interface PrimitiveEntry<V> {
      long key();

      V value();

      void setValue(V var1);
   }
}
