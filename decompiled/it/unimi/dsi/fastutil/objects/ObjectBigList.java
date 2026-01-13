package it.unimi.dsi.fastutil.objects;

import it.unimi.dsi.fastutil.BigArrays;
import it.unimi.dsi.fastutil.BigList;

public interface ObjectBigList<K> extends BigList<K>, ObjectCollection<K>, Comparable<BigList<? extends K>> {
   ObjectBigListIterator<K> iterator();

   ObjectBigListIterator<K> listIterator();

   ObjectBigListIterator<K> listIterator(long var1);

   @Override
   default ObjectSpliterator<K> spliterator() {
      return ObjectSpliterators.asSpliterator(this.iterator(), this.size64(), 16464);
   }

   ObjectBigList<K> subList(long var1, long var3);

   void getElements(long var1, Object[][] var3, long var4, long var6);

   default void getElements(long from, Object[] a, int offset, int length) {
      this.getElements(from, new Object[][]{a}, offset, length);
   }

   void removeElements(long var1, long var3);

   void addElements(long var1, K[][] var3);

   void addElements(long var1, K[][] var3, long var4, long var6);

   default void setElements(K[][] a) {
      this.setElements(0L, a);
   }

   default void setElements(long index, K[][] a) {
      this.setElements(index, a, 0L, BigArrays.length(a));
   }

   default void setElements(long index, K[][] a, long offset, long length) {
      if (index < 0L) {
         throw new IndexOutOfBoundsException("Index (" + index + ") is negative");
      } else if (index > this.size64()) {
         throw new IndexOutOfBoundsException("Index (" + index + ") is greater than list size (" + this.size64() + ")");
      } else {
         BigArrays.ensureOffsetLength(a, offset, length);
         if (index + length > this.size64()) {
            throw new IndexOutOfBoundsException("End index (" + (index + length) + ") is greater than list size (" + this.size64() + ")");
         } else {
            ObjectBigListIterator<K> iter = this.listIterator(index);
            long i = 0L;

            while (i < length) {
               iter.next();
               iter.set(BigArrays.get(a, offset + i++));
            }
         }
      }
   }

   default boolean addAll(long index, ObjectBigList<? extends K> l) {
      return this.addAll(index, l);
   }

   default boolean addAll(ObjectBigList<? extends K> l) {
      return this.addAll(this.size64(), l);
   }

   default boolean addAll(long index, ObjectList<? extends K> l) {
      return this.addAll(index, l);
   }

   default boolean addAll(ObjectList<? extends K> l) {
      return this.addAll(this.size64(), l);
   }
}
