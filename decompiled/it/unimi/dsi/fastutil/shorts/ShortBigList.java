package it.unimi.dsi.fastutil.shorts;

import it.unimi.dsi.fastutil.BigArrays;
import it.unimi.dsi.fastutil.BigList;

public interface ShortBigList extends BigList<Short>, ShortCollection, Comparable<BigList<? extends Short>> {
   ShortBigListIterator iterator();

   ShortBigListIterator listIterator();

   ShortBigListIterator listIterator(long var1);

   @Override
   default ShortSpliterator spliterator() {
      return ShortSpliterators.asSpliterator(this.iterator(), this.size64(), 16720);
   }

   ShortBigList subList(long var1, long var3);

   void getElements(long var1, short[][] var3, long var4, long var6);

   default void getElements(long from, short[] a, int offset, int length) {
      this.getElements(from, new short[][]{a}, offset, length);
   }

   void removeElements(long var1, long var3);

   void addElements(long var1, short[][] var3);

   void addElements(long var1, short[][] var3, long var4, long var6);

   default void setElements(short[][] a) {
      this.setElements(0L, a);
   }

   default void setElements(long index, short[][] a) {
      this.setElements(index, a, 0L, BigArrays.length(a));
   }

   default void setElements(long index, short[][] a, long offset, long length) {
      if (index < 0L) {
         throw new IndexOutOfBoundsException("Index (" + index + ") is negative");
      } else if (index > this.size64()) {
         throw new IndexOutOfBoundsException("Index (" + index + ") is greater than list size (" + this.size64() + ")");
      } else {
         BigArrays.ensureOffsetLength(a, offset, length);
         if (index + length > this.size64()) {
            throw new IndexOutOfBoundsException("End index (" + (index + length) + ") is greater than list size (" + this.size64() + ")");
         } else {
            ShortBigListIterator iter = this.listIterator(index);
            long i = 0L;

            while (i < length) {
               iter.nextShort();
               iter.set(BigArrays.get(a, offset + i++));
            }
         }
      }
   }

   void add(long var1, short var3);

   boolean addAll(long var1, ShortCollection var3);

   short getShort(long var1);

   short removeShort(long var1);

   short set(long var1, short var3);

   long indexOf(short var1);

   long lastIndexOf(short var1);

   @Deprecated
   void add(long var1, Short var3);

   @Deprecated
   Short get(long var1);

   @Deprecated
   @Override
   long indexOf(Object var1);

   @Deprecated
   @Override
   long lastIndexOf(Object var1);

   @Deprecated
   Short remove(long var1);

   @Deprecated
   Short set(long var1, Short var3);

   default boolean addAll(long index, ShortBigList l) {
      return this.addAll(index, (ShortCollection)l);
   }

   default boolean addAll(ShortBigList l) {
      return this.addAll(this.size64(), l);
   }

   default boolean addAll(long index, ShortList l) {
      return this.addAll(index, (ShortCollection)l);
   }

   default boolean addAll(ShortList l) {
      return this.addAll(this.size64(), l);
   }
}
