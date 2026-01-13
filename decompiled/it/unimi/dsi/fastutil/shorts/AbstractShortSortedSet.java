package it.unimi.dsi.fastutil.shorts;

public abstract class AbstractShortSortedSet extends AbstractShortSet implements ShortSortedSet {
   protected AbstractShortSortedSet() {
   }

   @Override
   public abstract ShortBidirectionalIterator iterator();
}
