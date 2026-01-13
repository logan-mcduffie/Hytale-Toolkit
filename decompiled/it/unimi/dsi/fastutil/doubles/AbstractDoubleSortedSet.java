package it.unimi.dsi.fastutil.doubles;

public abstract class AbstractDoubleSortedSet extends AbstractDoubleSet implements DoubleSortedSet {
   protected AbstractDoubleSortedSet() {
   }

   @Override
   public abstract DoubleBidirectionalIterator iterator();
}
