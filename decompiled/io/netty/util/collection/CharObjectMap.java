package io.netty.util.collection;

import java.util.Map;

public interface CharObjectMap<V> extends Map<Character, V> {
   V get(char var1);

   V put(char var1, V var2);

   V remove(char var1);

   Iterable<CharObjectMap.PrimitiveEntry<V>> entries();

   boolean containsKey(char var1);

   public interface PrimitiveEntry<V> {
      char key();

      V value();

      void setValue(V var1);
   }
}
