package it.unimi.dsi.fastutil.floats;

import it.unimi.dsi.fastutil.BigArrays;
import it.unimi.dsi.fastutil.BigList;

public interface FloatBigList extends BigList<Float>, FloatCollection, Comparable<BigList<? extends Float>> {
   FloatBigListIterator iterator();

   FloatBigListIterator listIterator();

   FloatBigListIterator listIterator(long var1);

   @Override
   default FloatSpliterator spliterator() {
      return FloatSpliterators.asSpliterator(this.iterator(), this.size64(), 16720);
   }

   FloatBigList subList(long var1, long var3);

   void getElements(long var1, float[][] var3, long var4, long var6);

   default void getElements(long from, float[] a, int offset, int length) {
      this.getElements(from, new float[][]{a}, offset, length);
   }

   void removeElements(long var1, long var3);

   void addElements(long var1, float[][] var3);

   void addElements(long var1, float[][] var3, long var4, long var6);

   default void setElements(float[][] a) {
      this.setElements(0L, a);
   }

   default void setElements(long index, float[][] a) {
      this.setElements(index, a, 0L, BigArrays.length(a));
   }

   default void setElements(long index, float[][] a, long offset, long length) {
      if (index < 0L) {
         throw new IndexOutOfBoundsException("Index (" + index + ") is negative");
      } else if (index > this.size64()) {
         throw new IndexOutOfBoundsException("Index (" + index + ") is greater than list size (" + this.size64() + ")");
      } else {
         BigArrays.ensureOffsetLength(a, offset, length);
         if (index + length > this.size64()) {
            throw new IndexOutOfBoundsException("End index (" + (index + length) + ") is greater than list size (" + this.size64() + ")");
         } else {
            FloatBigListIterator iter = this.listIterator(index);
            long i = 0L;

            while (i < length) {
               iter.nextFloat();
               iter.set(BigArrays.get(a, offset + i++));
            }
         }
      }
   }

   void add(long var1, float var3);

   boolean addAll(long var1, FloatCollection var3);

   float getFloat(long var1);

   float removeFloat(long var1);

   float set(long var1, float var3);

   long indexOf(float var1);

   long lastIndexOf(float var1);

   @Deprecated
   void add(long var1, Float var3);

   @Deprecated
   Float get(long var1);

   @Deprecated
   @Override
   long indexOf(Object var1);

   @Deprecated
   @Override
   long lastIndexOf(Object var1);

   @Deprecated
   Float remove(long var1);

   @Deprecated
   Float set(long var1, Float var3);

   default boolean addAll(long index, FloatBigList l) {
      return this.addAll(index, (FloatCollection)l);
   }

   default boolean addAll(FloatBigList l) {
      return this.addAll(this.size64(), l);
   }

   default boolean addAll(long index, FloatList l) {
      return this.addAll(index, (FloatCollection)l);
   }

   default boolean addAll(FloatList l) {
      return this.addAll(this.size64(), l);
   }
}
