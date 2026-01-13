package it.unimi.dsi.fastutil.floats;

import java.util.AbstractCollection;
import java.util.Arrays;
import java.util.Collection;

public abstract class AbstractFloatCollection extends AbstractCollection<Float> implements FloatCollection {
   protected AbstractFloatCollection() {
   }

   @Override
   public abstract FloatIterator iterator();

   @Override
   public boolean add(float k) {
      throw new UnsupportedOperationException();
   }

   @Override
   public boolean contains(float k) {
      FloatIterator iterator = this.iterator();

      while (iterator.hasNext()) {
         if (k == iterator.nextFloat()) {
            return true;
         }
      }

      return false;
   }

   @Override
   public boolean rem(float k) {
      FloatIterator iterator = this.iterator();

      while (iterator.hasNext()) {
         if (k == iterator.nextFloat()) {
            iterator.remove();
            return true;
         }
      }

      return false;
   }

   @Deprecated
   @Override
   public boolean add(Float key) {
      return FloatCollection.super.add(key);
   }

   @Deprecated
   @Override
   public boolean contains(Object key) {
      return FloatCollection.super.contains(key);
   }

   @Deprecated
   @Override
   public boolean remove(Object key) {
      return FloatCollection.super.remove(key);
   }

   @Override
   public float[] toArray(float[] a) {
      int size = this.size();
      if (a == null) {
         a = new float[size];
      } else if (a.length < size) {
         a = Arrays.copyOf(a, size);
      }

      FloatIterators.unwrap(this.iterator(), a);
      return a;
   }

   @Override
   public float[] toFloatArray() {
      int size = this.size();
      if (size == 0) {
         return FloatArrays.EMPTY_ARRAY;
      } else {
         float[] a = new float[size];
         FloatIterators.unwrap(this.iterator(), a);
         return a;
      }
   }

   @Deprecated
   @Override
   public float[] toFloatArray(float[] a) {
      return this.toArray(a);
   }

   @Override
   public boolean addAll(FloatCollection c) {
      boolean retVal = false;
      FloatIterator i = c.iterator();

      while (i.hasNext()) {
         if (this.add(i.nextFloat())) {
            retVal = true;
         }
      }

      return retVal;
   }

   @Override
   public boolean addAll(Collection<? extends Float> c) {
      return c instanceof FloatCollection ? this.addAll((FloatCollection)c) : super.addAll(c);
   }

   @Override
   public boolean containsAll(FloatCollection c) {
      FloatIterator i = c.iterator();

      while (i.hasNext()) {
         if (!this.contains(i.nextFloat())) {
            return false;
         }
      }

      return true;
   }

   @Override
   public boolean containsAll(Collection<?> c) {
      return c instanceof FloatCollection ? this.containsAll((FloatCollection)c) : super.containsAll(c);
   }

   @Override
   public boolean removeAll(FloatCollection c) {
      boolean retVal = false;
      FloatIterator i = c.iterator();

      while (i.hasNext()) {
         if (this.rem(i.nextFloat())) {
            retVal = true;
         }
      }

      return retVal;
   }

   @Override
   public boolean removeAll(Collection<?> c) {
      return c instanceof FloatCollection ? this.removeAll((FloatCollection)c) : super.removeAll(c);
   }

   @Override
   public boolean retainAll(FloatCollection c) {
      boolean retVal = false;
      FloatIterator i = this.iterator();

      while (i.hasNext()) {
         if (!c.contains(i.nextFloat())) {
            i.remove();
            retVal = true;
         }
      }

      return retVal;
   }

   @Override
   public boolean retainAll(Collection<?> c) {
      return c instanceof FloatCollection ? this.retainAll((FloatCollection)c) : super.retainAll(c);
   }

   @Override
   public String toString() {
      StringBuilder s = new StringBuilder();
      FloatIterator i = this.iterator();
      int n = this.size();
      boolean first = true;
      s.append("{");

      while (n-- != 0) {
         if (first) {
            first = false;
         } else {
            s.append(", ");
         }

         float k = i.nextFloat();
         s.append(String.valueOf(k));
      }

      s.append("}");
      return s.toString();
   }
}
