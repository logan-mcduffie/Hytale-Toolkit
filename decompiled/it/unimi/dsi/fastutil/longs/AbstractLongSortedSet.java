package it.unimi.dsi.fastutil.longs;

public abstract class AbstractLongSortedSet extends AbstractLongSet implements LongSortedSet {
   protected AbstractLongSortedSet() {
   }

   @Override
   public abstract LongBidirectionalIterator iterator();
}
