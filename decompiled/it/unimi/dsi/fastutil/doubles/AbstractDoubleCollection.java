package it.unimi.dsi.fastutil.doubles;

import java.util.AbstractCollection;
import java.util.Arrays;
import java.util.Collection;

public abstract class AbstractDoubleCollection extends AbstractCollection<Double> implements DoubleCollection {
   protected AbstractDoubleCollection() {
   }

   @Override
   public abstract DoubleIterator iterator();

   @Override
   public boolean add(double k) {
      throw new UnsupportedOperationException();
   }

   @Override
   public boolean contains(double k) {
      DoubleIterator iterator = this.iterator();

      while (iterator.hasNext()) {
         if (k == iterator.nextDouble()) {
            return true;
         }
      }

      return false;
   }

   @Override
   public boolean rem(double k) {
      DoubleIterator iterator = this.iterator();

      while (iterator.hasNext()) {
         if (k == iterator.nextDouble()) {
            iterator.remove();
            return true;
         }
      }

      return false;
   }

   @Deprecated
   @Override
   public boolean add(Double key) {
      return DoubleCollection.super.add(key);
   }

   @Deprecated
   @Override
   public boolean contains(Object key) {
      return DoubleCollection.super.contains(key);
   }

   @Deprecated
   @Override
   public boolean remove(Object key) {
      return DoubleCollection.super.remove(key);
   }

   @Override
   public double[] toArray(double[] a) {
      int size = this.size();
      if (a == null) {
         a = new double[size];
      } else if (a.length < size) {
         a = Arrays.copyOf(a, size);
      }

      DoubleIterators.unwrap(this.iterator(), a);
      return a;
   }

   @Override
   public double[] toDoubleArray() {
      int size = this.size();
      if (size == 0) {
         return DoubleArrays.EMPTY_ARRAY;
      } else {
         double[] a = new double[size];
         DoubleIterators.unwrap(this.iterator(), a);
         return a;
      }
   }

   @Deprecated
   @Override
   public double[] toDoubleArray(double[] a) {
      return this.toArray(a);
   }

   @Override
   public final void forEach(DoubleConsumer action) {
      DoubleCollection.super.forEach(action);
   }

   @Override
   public final boolean removeIf(DoublePredicate filter) {
      return DoubleCollection.super.removeIf(filter);
   }

   @Override
   public boolean addAll(DoubleCollection c) {
      boolean retVal = false;
      DoubleIterator i = c.iterator();

      while (i.hasNext()) {
         if (this.add(i.nextDouble())) {
            retVal = true;
         }
      }

      return retVal;
   }

   @Override
   public boolean addAll(Collection<? extends Double> c) {
      return c instanceof DoubleCollection ? this.addAll((DoubleCollection)c) : super.addAll(c);
   }

   @Override
   public boolean containsAll(DoubleCollection c) {
      DoubleIterator i = c.iterator();

      while (i.hasNext()) {
         if (!this.contains(i.nextDouble())) {
            return false;
         }
      }

      return true;
   }

   @Override
   public boolean containsAll(Collection<?> c) {
      return c instanceof DoubleCollection ? this.containsAll((DoubleCollection)c) : super.containsAll(c);
   }

   @Override
   public boolean removeAll(DoubleCollection c) {
      boolean retVal = false;
      DoubleIterator i = c.iterator();

      while (i.hasNext()) {
         if (this.rem(i.nextDouble())) {
            retVal = true;
         }
      }

      return retVal;
   }

   @Override
   public boolean removeAll(Collection<?> c) {
      return c instanceof DoubleCollection ? this.removeAll((DoubleCollection)c) : super.removeAll(c);
   }

   @Override
   public boolean retainAll(DoubleCollection c) {
      boolean retVal = false;
      DoubleIterator i = this.iterator();

      while (i.hasNext()) {
         if (!c.contains(i.nextDouble())) {
            i.remove();
            retVal = true;
         }
      }

      return retVal;
   }

   @Override
   public boolean retainAll(Collection<?> c) {
      return c instanceof DoubleCollection ? this.retainAll((DoubleCollection)c) : super.retainAll(c);
   }

   @Override
   public String toString() {
      StringBuilder s = new StringBuilder();
      DoubleIterator i = this.iterator();
      int n = this.size();
      boolean first = true;
      s.append("{");

      while (n-- != 0) {
         if (first) {
            first = false;
         } else {
            s.append(", ");
         }

         double k = i.nextDouble();
         s.append(String.valueOf(k));
      }

      s.append("}");
      return s.toString();
   }
}
