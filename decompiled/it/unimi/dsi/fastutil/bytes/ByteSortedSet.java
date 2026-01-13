package it.unimi.dsi.fastutil.bytes;

import it.unimi.dsi.fastutil.Size64;
import java.util.SortedSet;

public interface ByteSortedSet extends ByteSet, SortedSet<Byte>, ByteBidirectionalIterable {
   ByteBidirectionalIterator iterator(byte var1);

   @Override
   ByteBidirectionalIterator iterator();

   @Override
   default ByteSpliterator spliterator() {
      return ByteSpliterators.asSpliteratorFromSorted(this.iterator(), Size64.sizeOf(this), 341, this.comparator());
   }

   ByteSortedSet subSet(byte var1, byte var2);

   ByteSortedSet headSet(byte var1);

   ByteSortedSet tailSet(byte var1);

   ByteComparator comparator();

   byte firstByte();

   byte lastByte();

   @Deprecated
   default ByteSortedSet subSet(Byte from, Byte to) {
      return this.subSet(from.byteValue(), to.byteValue());
   }

   @Deprecated
   default ByteSortedSet headSet(Byte to) {
      return this.headSet(to.byteValue());
   }

   @Deprecated
   default ByteSortedSet tailSet(Byte from) {
      return this.tailSet(from.byteValue());
   }

   @Deprecated
   default Byte first() {
      return this.firstByte();
   }

   @Deprecated
   default Byte last() {
      return this.lastByte();
   }
}
