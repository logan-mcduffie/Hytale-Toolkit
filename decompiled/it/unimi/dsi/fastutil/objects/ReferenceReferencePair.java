package it.unimi.dsi.fastutil.objects;

import it.unimi.dsi.fastutil.Pair;

public interface ReferenceReferencePair<K, V> extends Pair<K, V> {
   static <K, V> ReferenceReferencePair<K, V> of(K left, V right) {
      return new ReferenceReferenceImmutablePair<>(left, right);
   }
}
