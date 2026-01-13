package it.unimi.dsi.fastutil.chars;

public abstract class AbstractCharSortedSet extends AbstractCharSet implements CharSortedSet {
   protected AbstractCharSortedSet() {
   }

   @Override
   public abstract CharBidirectionalIterator iterator();
}
