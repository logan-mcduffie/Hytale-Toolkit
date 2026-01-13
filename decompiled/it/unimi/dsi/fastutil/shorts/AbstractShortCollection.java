package it.unimi.dsi.fastutil.shorts;

import java.util.AbstractCollection;
import java.util.Arrays;
import java.util.Collection;

public abstract class AbstractShortCollection extends AbstractCollection<Short> implements ShortCollection {
   protected AbstractShortCollection() {
   }

   @Override
   public abstract ShortIterator iterator();

   @Override
   public boolean add(short k) {
      throw new UnsupportedOperationException();
   }

   @Override
   public boolean contains(short k) {
      ShortIterator iterator = this.iterator();

      while (iterator.hasNext()) {
         if (k == iterator.nextShort()) {
            return true;
         }
      }

      return false;
   }

   @Override
   public boolean rem(short k) {
      ShortIterator iterator = this.iterator();

      while (iterator.hasNext()) {
         if (k == iterator.nextShort()) {
            iterator.remove();
            return true;
         }
      }

      return false;
   }

   @Deprecated
   @Override
   public boolean add(Short key) {
      return ShortCollection.super.add(key);
   }

   @Deprecated
   @Override
   public boolean contains(Object key) {
      return ShortCollection.super.contains(key);
   }

   @Deprecated
   @Override
   public boolean remove(Object key) {
      return ShortCollection.super.remove(key);
   }

   @Override
   public short[] toArray(short[] a) {
      int size = this.size();
      if (a == null) {
         a = new short[size];
      } else if (a.length < size) {
         a = Arrays.copyOf(a, size);
      }

      ShortIterators.unwrap(this.iterator(), a);
      return a;
   }

   @Override
   public short[] toShortArray() {
      int size = this.size();
      if (size == 0) {
         return ShortArrays.EMPTY_ARRAY;
      } else {
         short[] a = new short[size];
         ShortIterators.unwrap(this.iterator(), a);
         return a;
      }
   }

   @Deprecated
   @Override
   public short[] toShortArray(short[] a) {
      return this.toArray(a);
   }

   @Override
   public boolean addAll(ShortCollection c) {
      boolean retVal = false;
      ShortIterator i = c.iterator();

      while (i.hasNext()) {
         if (this.add(i.nextShort())) {
            retVal = true;
         }
      }

      return retVal;
   }

   @Override
   public boolean addAll(Collection<? extends Short> c) {
      return c instanceof ShortCollection ? this.addAll((ShortCollection)c) : super.addAll(c);
   }

   @Override
   public boolean containsAll(ShortCollection c) {
      ShortIterator i = c.iterator();

      while (i.hasNext()) {
         if (!this.contains(i.nextShort())) {
            return false;
         }
      }

      return true;
   }

   @Override
   public boolean containsAll(Collection<?> c) {
      return c instanceof ShortCollection ? this.containsAll((ShortCollection)c) : super.containsAll(c);
   }

   @Override
   public boolean removeAll(ShortCollection c) {
      boolean retVal = false;
      ShortIterator i = c.iterator();

      while (i.hasNext()) {
         if (this.rem(i.nextShort())) {
            retVal = true;
         }
      }

      return retVal;
   }

   @Override
   public boolean removeAll(Collection<?> c) {
      return c instanceof ShortCollection ? this.removeAll((ShortCollection)c) : super.removeAll(c);
   }

   @Override
   public boolean retainAll(ShortCollection c) {
      boolean retVal = false;
      ShortIterator i = this.iterator();

      while (i.hasNext()) {
         if (!c.contains(i.nextShort())) {
            i.remove();
            retVal = true;
         }
      }

      return retVal;
   }

   @Override
   public boolean retainAll(Collection<?> c) {
      return c instanceof ShortCollection ? this.retainAll((ShortCollection)c) : super.retainAll(c);
   }

   @Override
   public String toString() {
      StringBuilder s = new StringBuilder();
      ShortIterator i = this.iterator();
      int n = this.size();
      boolean first = true;
      s.append("{");

      while (n-- != 0) {
         if (first) {
            first = false;
         } else {
            s.append(", ");
         }

         short k = i.nextShort();
         s.append(String.valueOf((int)k));
      }

      s.append("}");
      return s.toString();
   }
}
