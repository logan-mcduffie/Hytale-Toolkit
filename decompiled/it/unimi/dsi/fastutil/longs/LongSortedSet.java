package it.unimi.dsi.fastutil.longs;

import it.unimi.dsi.fastutil.Size64;
import java.util.SortedSet;

public interface LongSortedSet extends LongSet, SortedSet<Long>, LongBidirectionalIterable {
   LongBidirectionalIterator iterator(long var1);

   @Override
   LongBidirectionalIterator iterator();

   @Override
   default LongSpliterator spliterator() {
      return LongSpliterators.asSpliteratorFromSorted(this.iterator(), Size64.sizeOf(this), 341, this.comparator());
   }

   LongSortedSet subSet(long var1, long var3);

   LongSortedSet headSet(long var1);

   LongSortedSet tailSet(long var1);

   LongComparator comparator();

   long firstLong();

   long lastLong();

   @Deprecated
   default LongSortedSet subSet(Long from, Long to) {
      return this.subSet(from.longValue(), to.longValue());
   }

   @Deprecated
   default LongSortedSet headSet(Long to) {
      return this.headSet(to.longValue());
   }

   @Deprecated
   default LongSortedSet tailSet(Long from) {
      return this.tailSet(from.longValue());
   }

   @Deprecated
   default Long first() {
      return this.firstLong();
   }

   @Deprecated
   default Long last() {
      return this.lastLong();
   }
}
