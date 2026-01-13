package it.unimi.dsi.fastutil.floats;

import it.unimi.dsi.fastutil.SafeMath;
import it.unimi.dsi.fastutil.Size64;
import java.util.Comparator;
import java.util.List;
import java.util.RandomAccess;
import java.util.function.DoubleUnaryOperator;
import java.util.function.UnaryOperator;

public interface FloatList extends List<Float>, Comparable<List<? extends Float>>, FloatCollection {
   FloatListIterator iterator();

   @Override
   default FloatSpliterator spliterator() {
      return (FloatSpliterator)(this instanceof RandomAccess
         ? new AbstractFloatList.IndexBasedSpliterator(this, 0)
         : FloatSpliterators.asSpliterator(this.iterator(), Size64.sizeOf(this), 16720));
   }

   FloatListIterator listIterator();

   FloatListIterator listIterator(int var1);

   FloatList subList(int var1, int var2);

   void size(int var1);

   void getElements(int var1, float[] var2, int var3, int var4);

   void removeElements(int var1, int var2);

   void addElements(int var1, float[] var2);

   void addElements(int var1, float[] var2, int var3, int var4);

   default void setElements(float[] a) {
      this.setElements(0, a);
   }

   default void setElements(int index, float[] a) {
      this.setElements(index, a, 0, a.length);
   }

   default void setElements(int index, float[] a, int offset, int length) {
      if (index < 0) {
         throw new IndexOutOfBoundsException("Index (" + index + ") is negative");
      } else if (index > this.size()) {
         throw new IndexOutOfBoundsException("Index (" + index + ") is greater than list size (" + this.size() + ")");
      } else {
         FloatArrays.ensureOffsetLength(a, offset, length);
         if (index + length > this.size()) {
            throw new IndexOutOfBoundsException("End index (" + (index + length) + ") is greater than list size (" + this.size() + ")");
         } else {
            FloatListIterator iter = this.listIterator(index);
            int i = 0;

            while (i < length) {
               iter.nextFloat();
               iter.set(a[offset + i++]);
            }
         }
      }
   }

   @Override
   boolean add(float var1);

   void add(int var1, float var2);

   @Deprecated
   default void add(int index, Float key) {
      this.add(index, key.floatValue());
   }

   boolean addAll(int var1, FloatCollection var2);

   float set(int var1, float var2);

   default void replaceAll(FloatUnaryOperator operator) {
      FloatListIterator iter = this.listIterator();

      while (iter.hasNext()) {
         iter.set(operator.apply(iter.nextFloat()));
      }
   }

   default void replaceAll(DoubleUnaryOperator operator) {
      this.replaceAll(operator instanceof FloatUnaryOperator ? (FloatUnaryOperator)operator : x -> SafeMath.safeDoubleToFloat(operator.applyAsDouble(x)));
   }

   @Deprecated
   @Override
   default void replaceAll(UnaryOperator<Float> operator) {
      this.replaceAll(operator instanceof FloatUnaryOperator ? (FloatUnaryOperator)operator : operator::apply);
   }

   float getFloat(int var1);

   int indexOf(float var1);

   int lastIndexOf(float var1);

   @Deprecated
   @Override
   default boolean contains(Object key) {
      return FloatCollection.super.contains(key);
   }

   @Deprecated
   default Float get(int index) {
      return this.getFloat(index);
   }

   @Deprecated
   @Override
   default int indexOf(Object o) {
      return this.indexOf(((Float)o).floatValue());
   }

   @Deprecated
   @Override
   default int lastIndexOf(Object o) {
      return this.lastIndexOf(((Float)o).floatValue());
   }

   @Deprecated
   @Override
   default boolean add(Float k) {
      return this.add(k.floatValue());
   }

   float removeFloat(int var1);

   @Deprecated
   @Override
   default boolean remove(Object key) {
      return FloatCollection.super.remove(key);
   }

   @Deprecated
   default Float remove(int index) {
      return this.removeFloat(index);
   }

   @Deprecated
   default Float set(int index, Float k) {
      return this.set(index, k.floatValue());
   }

   default boolean addAll(int index, FloatList l) {
      return this.addAll(index, (FloatCollection)l);
   }

   default boolean addAll(FloatList l) {
      return this.addAll(this.size(), l);
   }

   static FloatList of() {
      return FloatImmutableList.of();
   }

   static FloatList of(float e) {
      return FloatLists.singleton(e);
   }

   static FloatList of(float e0, float e1) {
      return FloatImmutableList.of(e0, e1);
   }

   static FloatList of(float e0, float e1, float e2) {
      return FloatImmutableList.of(e0, e1, e2);
   }

   static FloatList of(float... a) {
      switch (a.length) {
         case 0:
            return of();
         case 1:
            return of(a[0]);
         default:
            return FloatImmutableList.of(a);
      }
   }

   @Deprecated
   @Override
   default void sort(Comparator<? super Float> comparator) {
      this.sort(FloatComparators.asFloatComparator(comparator));
   }

   default void sort(FloatComparator comparator) {
      float[] elements = this.toFloatArray();
      if (comparator == null) {
         FloatArrays.stableSort(elements);
      } else {
         FloatArrays.stableSort(elements, comparator);
      }

      this.setElements(elements);
   }

   @Deprecated
   default void unstableSort(Comparator<? super Float> comparator) {
      this.unstableSort(FloatComparators.asFloatComparator(comparator));
   }

   default void unstableSort(FloatComparator comparator) {
      float[] elements = this.toFloatArray();
      if (comparator == null) {
         FloatArrays.unstableSort(elements);
      } else {
         FloatArrays.unstableSort(elements, comparator);
      }

      this.setElements(elements);
   }
}
