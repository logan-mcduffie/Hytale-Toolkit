package it.unimi.dsi.fastutil.objects;

import it.unimi.dsi.fastutil.Pair;

public interface ReferenceObjectPair<K, V> extends Pair<K, V> {
   static <K, V> ReferenceObjectPair<K, V> of(K left, V right) {
      return new ReferenceObjectImmutablePair<>(left, right);
   }
}
