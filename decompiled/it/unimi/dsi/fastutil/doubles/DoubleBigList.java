package it.unimi.dsi.fastutil.doubles;

import it.unimi.dsi.fastutil.BigArrays;
import it.unimi.dsi.fastutil.BigList;

public interface DoubleBigList extends BigList<Double>, DoubleCollection, Comparable<BigList<? extends Double>> {
   DoubleBigListIterator iterator();

   DoubleBigListIterator listIterator();

   DoubleBigListIterator listIterator(long var1);

   @Override
   default DoubleSpliterator spliterator() {
      return DoubleSpliterators.asSpliterator(this.iterator(), this.size64(), 16720);
   }

   DoubleBigList subList(long var1, long var3);

   void getElements(long var1, double[][] var3, long var4, long var6);

   default void getElements(long from, double[] a, int offset, int length) {
      this.getElements(from, new double[][]{a}, offset, length);
   }

   void removeElements(long var1, long var3);

   void addElements(long var1, double[][] var3);

   void addElements(long var1, double[][] var3, long var4, long var6);

   default void setElements(double[][] a) {
      this.setElements(0L, a);
   }

   default void setElements(long index, double[][] a) {
      this.setElements(index, a, 0L, BigArrays.length(a));
   }

   default void setElements(long index, double[][] a, long offset, long length) {
      if (index < 0L) {
         throw new IndexOutOfBoundsException("Index (" + index + ") is negative");
      } else if (index > this.size64()) {
         throw new IndexOutOfBoundsException("Index (" + index + ") is greater than list size (" + this.size64() + ")");
      } else {
         BigArrays.ensureOffsetLength(a, offset, length);
         if (index + length > this.size64()) {
            throw new IndexOutOfBoundsException("End index (" + (index + length) + ") is greater than list size (" + this.size64() + ")");
         } else {
            DoubleBigListIterator iter = this.listIterator(index);
            long i = 0L;

            while (i < length) {
               iter.nextDouble();
               iter.set(BigArrays.get(a, offset + i++));
            }
         }
      }
   }

   void add(long var1, double var3);

   boolean addAll(long var1, DoubleCollection var3);

   double getDouble(long var1);

   double removeDouble(long var1);

   double set(long var1, double var3);

   long indexOf(double var1);

   long lastIndexOf(double var1);

   @Deprecated
   void add(long var1, Double var3);

   @Deprecated
   Double get(long var1);

   @Deprecated
   @Override
   long indexOf(Object var1);

   @Deprecated
   @Override
   long lastIndexOf(Object var1);

   @Deprecated
   Double remove(long var1);

   @Deprecated
   Double set(long var1, Double var3);

   default boolean addAll(long index, DoubleBigList l) {
      return this.addAll(index, (DoubleCollection)l);
   }

   default boolean addAll(DoubleBigList l) {
      return this.addAll(this.size64(), l);
   }

   default boolean addAll(long index, DoubleList l) {
      return this.addAll(index, (DoubleCollection)l);
   }

   default boolean addAll(DoubleList l) {
      return this.addAll(this.size64(), l);
   }
}
