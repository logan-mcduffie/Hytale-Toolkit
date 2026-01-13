package it.unimi.dsi.fastutil.shorts;

import it.unimi.dsi.fastutil.SafeMath;
import it.unimi.dsi.fastutil.Size64;
import java.util.Comparator;
import java.util.List;
import java.util.RandomAccess;
import java.util.function.IntUnaryOperator;
import java.util.function.UnaryOperator;

public interface ShortList extends List<Short>, Comparable<List<? extends Short>>, ShortCollection {
   ShortListIterator iterator();

   @Override
   default ShortSpliterator spliterator() {
      return (ShortSpliterator)(this instanceof RandomAccess
         ? new AbstractShortList.IndexBasedSpliterator(this, 0)
         : ShortSpliterators.asSpliterator(this.iterator(), Size64.sizeOf(this), 16720));
   }

   ShortListIterator listIterator();

   ShortListIterator listIterator(int var1);

   ShortList subList(int var1, int var2);

   void size(int var1);

   void getElements(int var1, short[] var2, int var3, int var4);

   void removeElements(int var1, int var2);

   void addElements(int var1, short[] var2);

   void addElements(int var1, short[] var2, int var3, int var4);

   default void setElements(short[] a) {
      this.setElements(0, a);
   }

   default void setElements(int index, short[] a) {
      this.setElements(index, a, 0, a.length);
   }

   default void setElements(int index, short[] a, int offset, int length) {
      if (index < 0) {
         throw new IndexOutOfBoundsException("Index (" + index + ") is negative");
      } else if (index > this.size()) {
         throw new IndexOutOfBoundsException("Index (" + index + ") is greater than list size (" + this.size() + ")");
      } else {
         ShortArrays.ensureOffsetLength(a, offset, length);
         if (index + length > this.size()) {
            throw new IndexOutOfBoundsException("End index (" + (index + length) + ") is greater than list size (" + this.size() + ")");
         } else {
            ShortListIterator iter = this.listIterator(index);
            int i = 0;

            while (i < length) {
               iter.nextShort();
               iter.set(a[offset + i++]);
            }
         }
      }
   }

   @Override
   boolean add(short var1);

   void add(int var1, short var2);

   @Deprecated
   default void add(int index, Short key) {
      this.add(index, key.shortValue());
   }

   boolean addAll(int var1, ShortCollection var2);

   short set(int var1, short var2);

   default void replaceAll(ShortUnaryOperator operator) {
      ShortListIterator iter = this.listIterator();

      while (iter.hasNext()) {
         iter.set(operator.apply(iter.nextShort()));
      }
   }

   default void replaceAll(IntUnaryOperator operator) {
      this.replaceAll(operator instanceof ShortUnaryOperator ? (ShortUnaryOperator)operator : x -> SafeMath.safeIntToShort(operator.applyAsInt(x)));
   }

   @Deprecated
   @Override
   default void replaceAll(UnaryOperator<Short> operator) {
      this.replaceAll(operator instanceof ShortUnaryOperator ? (ShortUnaryOperator)operator : operator::apply);
   }

   short getShort(int var1);

   int indexOf(short var1);

   int lastIndexOf(short var1);

   @Deprecated
   @Override
   default boolean contains(Object key) {
      return ShortCollection.super.contains(key);
   }

   @Deprecated
   default Short get(int index) {
      return this.getShort(index);
   }

   @Deprecated
   @Override
   default int indexOf(Object o) {
      return this.indexOf(((Short)o).shortValue());
   }

   @Deprecated
   @Override
   default int lastIndexOf(Object o) {
      return this.lastIndexOf(((Short)o).shortValue());
   }

   @Deprecated
   @Override
   default boolean add(Short k) {
      return this.add(k.shortValue());
   }

   short removeShort(int var1);

   @Deprecated
   @Override
   default boolean remove(Object key) {
      return ShortCollection.super.remove(key);
   }

   @Deprecated
   default Short remove(int index) {
      return this.removeShort(index);
   }

   @Deprecated
   default Short set(int index, Short k) {
      return this.set(index, k.shortValue());
   }

   default boolean addAll(int index, ShortList l) {
      return this.addAll(index, (ShortCollection)l);
   }

   default boolean addAll(ShortList l) {
      return this.addAll(this.size(), l);
   }

   static ShortList of() {
      return ShortImmutableList.of();
   }

   static ShortList of(short e) {
      return ShortLists.singleton(e);
   }

   static ShortList of(short e0, short e1) {
      return ShortImmutableList.of(e0, e1);
   }

   static ShortList of(short e0, short e1, short e2) {
      return ShortImmutableList.of(e0, e1, e2);
   }

   static ShortList of(short... a) {
      switch (a.length) {
         case 0:
            return of();
         case 1:
            return of(a[0]);
         default:
            return ShortImmutableList.of(a);
      }
   }

   @Deprecated
   @Override
   default void sort(Comparator<? super Short> comparator) {
      this.sort(ShortComparators.asShortComparator(comparator));
   }

   default void sort(ShortComparator comparator) {
      if (comparator == null) {
         this.unstableSort(comparator);
      } else {
         short[] elements = this.toShortArray();
         ShortArrays.stableSort(elements, comparator);
         this.setElements(elements);
      }
   }

   @Deprecated
   default void unstableSort(Comparator<? super Short> comparator) {
      this.unstableSort(ShortComparators.asShortComparator(comparator));
   }

   default void unstableSort(ShortComparator comparator) {
      short[] elements = this.toShortArray();
      if (comparator == null) {
         ShortArrays.unstableSort(elements);
      } else {
         ShortArrays.unstableSort(elements, comparator);
      }

      this.setElements(elements);
   }
}
