package it.unimi.dsi.fastutil;

import it.unimi.dsi.fastutil.objects.ObjectObjectImmutableSortedPair;
import java.util.Objects;

public interface SortedPair<K extends Comparable<K>> extends Pair<K, K> {
   static <K extends Comparable<K>> SortedPair<K> of(K l, K r) {
      return ObjectObjectImmutableSortedPair.of(l, r);
   }

   default boolean contains(Object o) {
      return Objects.equals(o, this.left()) || Objects.equals(o, this.right());
   }
}
