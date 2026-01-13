package io.netty.util.collection;

import java.util.Map;

public interface ByteObjectMap<V> extends Map<Byte, V> {
   V get(byte var1);

   V put(byte var1, V var2);

   V remove(byte var1);

   Iterable<ByteObjectMap.PrimitiveEntry<V>> entries();

   boolean containsKey(byte var1);

   public interface PrimitiveEntry<V> {
      byte key();

      V value();

      void setValue(V var1);
   }
}
