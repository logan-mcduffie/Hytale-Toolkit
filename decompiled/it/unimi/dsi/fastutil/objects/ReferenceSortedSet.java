package it.unimi.dsi.fastutil.objects;

import it.unimi.dsi.fastutil.Size64;
import java.util.SortedSet;

public interface ReferenceSortedSet<K> extends ReferenceSet<K>, SortedSet<K>, ObjectBidirectionalIterable<K> {
   ObjectBidirectionalIterator<K> iterator(K var1);

   @Override
   ObjectBidirectionalIterator<K> iterator();

   @Override
   default ObjectSpliterator<K> spliterator() {
      return ObjectSpliterators.asSpliteratorFromSorted(this.iterator(), Size64.sizeOf(this), 85, this.comparator());
   }

   ReferenceSortedSet<K> subSet(K var1, K var2);

   ReferenceSortedSet<K> headSet(K var1);

   ReferenceSortedSet<K> tailSet(K var1);
}
