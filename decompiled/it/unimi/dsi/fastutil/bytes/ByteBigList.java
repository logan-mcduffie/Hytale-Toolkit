package it.unimi.dsi.fastutil.bytes;

import it.unimi.dsi.fastutil.BigArrays;
import it.unimi.dsi.fastutil.BigList;

public interface ByteBigList extends BigList<Byte>, ByteCollection, Comparable<BigList<? extends Byte>> {
   ByteBigListIterator iterator();

   ByteBigListIterator listIterator();

   ByteBigListIterator listIterator(long var1);

   @Override
   default ByteSpliterator spliterator() {
      return ByteSpliterators.asSpliterator(this.iterator(), this.size64(), 16720);
   }

   ByteBigList subList(long var1, long var3);

   void getElements(long var1, byte[][] var3, long var4, long var6);

   default void getElements(long from, byte[] a, int offset, int length) {
      this.getElements(from, new byte[][]{a}, offset, length);
   }

   void removeElements(long var1, long var3);

   void addElements(long var1, byte[][] var3);

   void addElements(long var1, byte[][] var3, long var4, long var6);

   default void setElements(byte[][] a) {
      this.setElements(0L, a);
   }

   default void setElements(long index, byte[][] a) {
      this.setElements(index, a, 0L, BigArrays.length(a));
   }

   default void setElements(long index, byte[][] a, long offset, long length) {
      if (index < 0L) {
         throw new IndexOutOfBoundsException("Index (" + index + ") is negative");
      } else if (index > this.size64()) {
         throw new IndexOutOfBoundsException("Index (" + index + ") is greater than list size (" + this.size64() + ")");
      } else {
         BigArrays.ensureOffsetLength(a, offset, length);
         if (index + length > this.size64()) {
            throw new IndexOutOfBoundsException("End index (" + (index + length) + ") is greater than list size (" + this.size64() + ")");
         } else {
            ByteBigListIterator iter = this.listIterator(index);
            long i = 0L;

            while (i < length) {
               iter.nextByte();
               iter.set(BigArrays.get(a, offset + i++));
            }
         }
      }
   }

   void add(long var1, byte var3);

   boolean addAll(long var1, ByteCollection var3);

   byte getByte(long var1);

   byte removeByte(long var1);

   byte set(long var1, byte var3);

   long indexOf(byte var1);

   long lastIndexOf(byte var1);

   @Deprecated
   void add(long var1, Byte var3);

   @Deprecated
   Byte get(long var1);

   @Deprecated
   @Override
   long indexOf(Object var1);

   @Deprecated
   @Override
   long lastIndexOf(Object var1);

   @Deprecated
   Byte remove(long var1);

   @Deprecated
   Byte set(long var1, Byte var3);

   default boolean addAll(long index, ByteBigList l) {
      return this.addAll(index, (ByteCollection)l);
   }

   default boolean addAll(ByteBigList l) {
      return this.addAll(this.size64(), l);
   }

   default boolean addAll(long index, ByteList l) {
      return this.addAll(index, (ByteCollection)l);
   }

   default boolean addAll(ByteList l) {
      return this.addAll(this.size64(), l);
   }
}
