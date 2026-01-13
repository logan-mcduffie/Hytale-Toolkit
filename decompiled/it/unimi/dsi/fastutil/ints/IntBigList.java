package it.unimi.dsi.fastutil.ints;

import it.unimi.dsi.fastutil.BigArrays;
import it.unimi.dsi.fastutil.BigList;

public interface IntBigList extends BigList<Integer>, IntCollection, Comparable<BigList<? extends Integer>> {
   IntBigListIterator iterator();

   IntBigListIterator listIterator();

   IntBigListIterator listIterator(long var1);

   @Override
   default IntSpliterator spliterator() {
      return IntSpliterators.asSpliterator(this.iterator(), this.size64(), 16720);
   }

   IntBigList subList(long var1, long var3);

   void getElements(long var1, int[][] var3, long var4, long var6);

   default void getElements(long from, int[] a, int offset, int length) {
      this.getElements(from, new int[][]{a}, offset, length);
   }

   void removeElements(long var1, long var3);

   void addElements(long var1, int[][] var3);

   void addElements(long var1, int[][] var3, long var4, long var6);

   default void setElements(int[][] a) {
      this.setElements(0L, a);
   }

   default void setElements(long index, int[][] a) {
      this.setElements(index, a, 0L, BigArrays.length(a));
   }

   default void setElements(long index, int[][] a, long offset, long length) {
      if (index < 0L) {
         throw new IndexOutOfBoundsException("Index (" + index + ") is negative");
      } else if (index > this.size64()) {
         throw new IndexOutOfBoundsException("Index (" + index + ") is greater than list size (" + this.size64() + ")");
      } else {
         BigArrays.ensureOffsetLength(a, offset, length);
         if (index + length > this.size64()) {
            throw new IndexOutOfBoundsException("End index (" + (index + length) + ") is greater than list size (" + this.size64() + ")");
         } else {
            IntBigListIterator iter = this.listIterator(index);
            long i = 0L;

            while (i < length) {
               iter.nextInt();
               iter.set(BigArrays.get(a, offset + i++));
            }
         }
      }
   }

   void add(long var1, int var3);

   boolean addAll(long var1, IntCollection var3);

   int getInt(long var1);

   int removeInt(long var1);

   int set(long var1, int var3);

   long indexOf(int var1);

   long lastIndexOf(int var1);

   @Deprecated
   void add(long var1, Integer var3);

   @Deprecated
   Integer get(long var1);

   @Deprecated
   @Override
   long indexOf(Object var1);

   @Deprecated
   @Override
   long lastIndexOf(Object var1);

   @Deprecated
   Integer remove(long var1);

   @Deprecated
   Integer set(long var1, Integer var3);

   default boolean addAll(long index, IntBigList l) {
      return this.addAll(index, (IntCollection)l);
   }

   default boolean addAll(IntBigList l) {
      return this.addAll(this.size64(), l);
   }

   default boolean addAll(long index, IntList l) {
      return this.addAll(index, (IntCollection)l);
   }

   default boolean addAll(IntList l) {
      return this.addAll(this.size64(), l);
   }
}
