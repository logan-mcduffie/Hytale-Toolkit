package it.unimi.dsi.fastutil.longs;

import it.unimi.dsi.fastutil.BigArrays;
import it.unimi.dsi.fastutil.BigList;

public interface LongBigList extends BigList<Long>, LongCollection, Comparable<BigList<? extends Long>> {
   LongBigListIterator iterator();

   LongBigListIterator listIterator();

   LongBigListIterator listIterator(long var1);

   @Override
   default LongSpliterator spliterator() {
      return LongSpliterators.asSpliterator(this.iterator(), this.size64(), 16720);
   }

   LongBigList subList(long var1, long var3);

   void getElements(long var1, long[][] var3, long var4, long var6);

   default void getElements(long from, long[] a, int offset, int length) {
      this.getElements(from, new long[][]{a}, offset, length);
   }

   void removeElements(long var1, long var3);

   void addElements(long var1, long[][] var3);

   void addElements(long var1, long[][] var3, long var4, long var6);

   default void setElements(long[][] a) {
      this.setElements(0L, a);
   }

   default void setElements(long index, long[][] a) {
      this.setElements(index, a, 0L, BigArrays.length(a));
   }

   default void setElements(long index, long[][] a, long offset, long length) {
      if (index < 0L) {
         throw new IndexOutOfBoundsException("Index (" + index + ") is negative");
      } else if (index > this.size64()) {
         throw new IndexOutOfBoundsException("Index (" + index + ") is greater than list size (" + this.size64() + ")");
      } else {
         BigArrays.ensureOffsetLength(a, offset, length);
         if (index + length > this.size64()) {
            throw new IndexOutOfBoundsException("End index (" + (index + length) + ") is greater than list size (" + this.size64() + ")");
         } else {
            LongBigListIterator iter = this.listIterator(index);
            long i = 0L;

            while (i < length) {
               iter.nextLong();
               iter.set(BigArrays.get(a, offset + i++));
            }
         }
      }
   }

   void add(long var1, long var3);

   boolean addAll(long var1, LongCollection var3);

   long getLong(long var1);

   long removeLong(long var1);

   long set(long var1, long var3);

   long indexOf(long var1);

   long lastIndexOf(long var1);

   @Deprecated
   void add(long var1, Long var3);

   @Deprecated
   Long get(long var1);

   @Deprecated
   @Override
   long indexOf(Object var1);

   @Deprecated
   @Override
   long lastIndexOf(Object var1);

   @Deprecated
   Long remove(long var1);

   @Deprecated
   Long set(long var1, Long var3);

   default boolean addAll(long index, LongBigList l) {
      return this.addAll(index, (LongCollection)l);
   }

   default boolean addAll(LongBigList l) {
      return this.addAll(this.size64(), l);
   }

   default boolean addAll(long index, LongList l) {
      return this.addAll(index, (LongCollection)l);
   }

   default boolean addAll(LongList l) {
      return this.addAll(this.size64(), l);
   }
}
