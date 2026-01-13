package it.unimi.dsi.fastutil.floats;

import it.unimi.dsi.fastutil.Size64;
import java.util.SortedSet;

public interface FloatSortedSet extends FloatSet, SortedSet<Float>, FloatBidirectionalIterable {
   FloatBidirectionalIterator iterator(float var1);

   @Override
   FloatBidirectionalIterator iterator();

   @Override
   default FloatSpliterator spliterator() {
      return FloatSpliterators.asSpliteratorFromSorted(this.iterator(), Size64.sizeOf(this), 341, this.comparator());
   }

   FloatSortedSet subSet(float var1, float var2);

   FloatSortedSet headSet(float var1);

   FloatSortedSet tailSet(float var1);

   FloatComparator comparator();

   float firstFloat();

   float lastFloat();

   @Deprecated
   default FloatSortedSet subSet(Float from, Float to) {
      return this.subSet(from.floatValue(), to.floatValue());
   }

   @Deprecated
   default FloatSortedSet headSet(Float to) {
      return this.headSet(to.floatValue());
   }

   @Deprecated
   default FloatSortedSet tailSet(Float from) {
      return this.tailSet(from.floatValue());
   }

   @Deprecated
   default Float first() {
      return this.firstFloat();
   }

   @Deprecated
   default Float last() {
      return this.lastFloat();
   }
}
