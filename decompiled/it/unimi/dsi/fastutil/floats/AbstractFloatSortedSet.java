package it.unimi.dsi.fastutil.floats;

public abstract class AbstractFloatSortedSet extends AbstractFloatSet implements FloatSortedSet {
   protected AbstractFloatSortedSet() {
   }

   @Override
   public abstract FloatBidirectionalIterator iterator();
}
