package io.netty.util.collection;

import java.util.Map;

public interface IntObjectMap<V> extends Map<Integer, V> {
   V get(int var1);

   V put(int var1, V var2);

   V remove(int var1);

   Iterable<IntObjectMap.PrimitiveEntry<V>> entries();

   boolean containsKey(int var1);

   public interface PrimitiveEntry<V> {
      int key();

      V value();

      void setValue(V var1);
   }
}
