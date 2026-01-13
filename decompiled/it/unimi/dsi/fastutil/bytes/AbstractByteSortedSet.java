package it.unimi.dsi.fastutil.bytes;

public abstract class AbstractByteSortedSet extends AbstractByteSet implements ByteSortedSet {
   protected AbstractByteSortedSet() {
   }

   @Override
   public abstract ByteBidirectionalIterator iterator();
}
