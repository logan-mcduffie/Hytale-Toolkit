package it.unimi.dsi.fastutil.doubles;

import it.unimi.dsi.fastutil.Size64;
import java.util.SortedSet;

public interface DoubleSortedSet extends DoubleSet, SortedSet<Double>, DoubleBidirectionalIterable {
   DoubleBidirectionalIterator iterator(double var1);

   @Override
   DoubleBidirectionalIterator iterator();

   @Override
   default DoubleSpliterator spliterator() {
      return DoubleSpliterators.asSpliteratorFromSorted(this.iterator(), Size64.sizeOf(this), 341, this.comparator());
   }

   DoubleSortedSet subSet(double var1, double var3);

   DoubleSortedSet headSet(double var1);

   DoubleSortedSet tailSet(double var1);

   DoubleComparator comparator();

   double firstDouble();

   double lastDouble();

   @Deprecated
   default DoubleSortedSet subSet(Double from, Double to) {
      return this.subSet(from.doubleValue(), to.doubleValue());
   }

   @Deprecated
   default DoubleSortedSet headSet(Double to) {
      return this.headSet(to.doubleValue());
   }

   @Deprecated
   default DoubleSortedSet tailSet(Double from) {
      return this.tailSet(from.doubleValue());
   }

   @Deprecated
   default Double first() {
      return this.firstDouble();
   }

   @Deprecated
   default Double last() {
      return this.lastDouble();
   }
}
