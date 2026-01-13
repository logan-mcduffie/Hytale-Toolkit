package io.netty.util.collection;

import java.util.Map;

public interface ShortObjectMap<V> extends Map<Short, V> {
   V get(short var1);

   V put(short var1, V var2);

   V remove(short var1);

   Iterable<ShortObjectMap.PrimitiveEntry<V>> entries();

   boolean containsKey(short var1);

   public interface PrimitiveEntry<V> {
      short key();

      V value();

      void setValue(V var1);
   }
}
