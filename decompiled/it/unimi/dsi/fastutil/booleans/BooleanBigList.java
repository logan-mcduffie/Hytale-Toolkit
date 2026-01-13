package it.unimi.dsi.fastutil.booleans;

import it.unimi.dsi.fastutil.BigArrays;
import it.unimi.dsi.fastutil.BigList;

public interface BooleanBigList extends BigList<Boolean>, BooleanCollection, Comparable<BigList<? extends Boolean>> {
   BooleanBigListIterator iterator();

   BooleanBigListIterator listIterator();

   BooleanBigListIterator listIterator(long var1);

   @Override
   default BooleanSpliterator spliterator() {
      return BooleanSpliterators.asSpliterator(this.iterator(), this.size64(), 16720);
   }

   BooleanBigList subList(long var1, long var3);

   void getElements(long var1, boolean[][] var3, long var4, long var6);

   default void getElements(long from, boolean[] a, int offset, int length) {
      this.getElements(from, new boolean[][]{a}, offset, length);
   }

   void removeElements(long var1, long var3);

   void addElements(long var1, boolean[][] var3);

   void addElements(long var1, boolean[][] var3, long var4, long var6);

   default void setElements(boolean[][] a) {
      this.setElements(0L, a);
   }

   default void setElements(long index, boolean[][] a) {
      this.setElements(index, a, 0L, BigArrays.length(a));
   }

   default void setElements(long index, boolean[][] a, long offset, long length) {
      if (index < 0L) {
         throw new IndexOutOfBoundsException("Index (" + index + ") is negative");
      } else if (index > this.size64()) {
         throw new IndexOutOfBoundsException("Index (" + index + ") is greater than list size (" + this.size64() + ")");
      } else {
         BigArrays.ensureOffsetLength(a, offset, length);
         if (index + length > this.size64()) {
            throw new IndexOutOfBoundsException("End index (" + (index + length) + ") is greater than list size (" + this.size64() + ")");
         } else {
            BooleanBigListIterator iter = this.listIterator(index);
            long i = 0L;

            while (i < length) {
               iter.nextBoolean();
               iter.set(BigArrays.get(a, offset + i++));
            }
         }
      }
   }

   void add(long var1, boolean var3);

   boolean addAll(long var1, BooleanCollection var3);

   boolean getBoolean(long var1);

   boolean removeBoolean(long var1);

   boolean set(long var1, boolean var3);

   long indexOf(boolean var1);

   long lastIndexOf(boolean var1);

   @Deprecated
   void add(long var1, Boolean var3);

   @Deprecated
   Boolean get(long var1);

   @Deprecated
   @Override
   long indexOf(Object var1);

   @Deprecated
   @Override
   long lastIndexOf(Object var1);

   @Deprecated
   Boolean remove(long var1);

   @Deprecated
   Boolean set(long var1, Boolean var3);

   default boolean addAll(long index, BooleanBigList l) {
      return this.addAll(index, (BooleanCollection)l);
   }

   default boolean addAll(BooleanBigList l) {
      return this.addAll(this.size64(), l);
   }

   default boolean addAll(long index, BooleanList l) {
      return this.addAll(index, (BooleanCollection)l);
   }

   default boolean addAll(BooleanList l) {
      return this.addAll(this.size64(), l);
   }
}
