package it.unimi.dsi.fastutil.shorts;

import it.unimi.dsi.fastutil.Size64;
import java.util.SortedSet;

public interface ShortSortedSet extends ShortSet, SortedSet<Short>, ShortBidirectionalIterable {
   ShortBidirectionalIterator iterator(short var1);

   @Override
   ShortBidirectionalIterator iterator();

   @Override
   default ShortSpliterator spliterator() {
      return ShortSpliterators.asSpliteratorFromSorted(this.iterator(), Size64.sizeOf(this), 341, this.comparator());
   }

   ShortSortedSet subSet(short var1, short var2);

   ShortSortedSet headSet(short var1);

   ShortSortedSet tailSet(short var1);

   ShortComparator comparator();

   short firstShort();

   short lastShort();

   @Deprecated
   default ShortSortedSet subSet(Short from, Short to) {
      return this.subSet(from.shortValue(), to.shortValue());
   }

   @Deprecated
   default ShortSortedSet headSet(Short to) {
      return this.headSet(to.shortValue());
   }

   @Deprecated
   default ShortSortedSet tailSet(Short from) {
      return this.tailSet(from.shortValue());
   }

   @Deprecated
   default Short first() {
      return this.firstShort();
   }

   @Deprecated
   default Short last() {
      return this.lastShort();
   }
}
