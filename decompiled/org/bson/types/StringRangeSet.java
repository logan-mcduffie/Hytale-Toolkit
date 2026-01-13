package org.bson.types;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Set;
import org.bson.assertions.Assertions;

class StringRangeSet implements Set<String> {
   private static final String[] STRINGS = new String[1024];
   private final int size;

   StringRangeSet(int size) {
      Assertions.isTrue("size >= 0", size >= 0);
      this.size = size;
   }

   @Override
   public int size() {
      return this.size;
   }

   @Override
   public boolean isEmpty() {
      return this.size() == 0;
   }

   @Override
   public boolean contains(Object o) {
      if (!(o instanceof String)) {
         return false;
      } else {
         try {
            int i = Integer.parseInt((String)o);
            return i >= 0 && i < this.size();
         } catch (NumberFormatException var3) {
            return false;
         }
      }
   }

   @Override
   public Iterator<String> iterator() {
      return new Iterator<String>() {
         private int cur = 0;

         @Override
         public boolean hasNext() {
            return this.cur < StringRangeSet.this.size;
         }

         public String next() {
            if (!this.hasNext()) {
               throw new NoSuchElementException();
            } else {
               return StringRangeSet.this.intToString(this.cur++);
            }
         }

         @Override
         public void remove() {
            throw new UnsupportedOperationException();
         }
      };
   }

   @Override
   public Object[] toArray() {
      Object[] retVal = new Object[this.size()];

      for (int i = 0; i < this.size(); i++) {
         retVal[i] = this.intToString(i);
      }

      return retVal;
   }

   @Override
   public <T> T[] toArray(T[] a) {
      T[] retVal = (T[])(a.length >= this.size() ? a : (Object[])Array.newInstance(a.getClass().getComponentType(), this.size));

      for (int i = 0; i < this.size(); i++) {
         retVal[i] = (T)this.intToString(i);
      }

      if (a.length > this.size()) {
         a[this.size] = null;
      }

      return retVal;
   }

   public boolean add(String integer) {
      throw new UnsupportedOperationException();
   }

   @Override
   public boolean remove(Object o) {
      throw new UnsupportedOperationException();
   }

   @Override
   public boolean containsAll(Collection<?> c) {
      for (Object e : c) {
         if (!this.contains(e)) {
            return false;
         }
      }

      return true;
   }

   @Override
   public boolean addAll(Collection<? extends String> c) {
      throw new UnsupportedOperationException();
   }

   @Override
   public boolean retainAll(Collection<?> c) {
      throw new UnsupportedOperationException();
   }

   @Override
   public boolean removeAll(Collection<?> c) {
      throw new UnsupportedOperationException();
   }

   @Override
   public void clear() {
      throw new UnsupportedOperationException();
   }

   private String intToString(int i) {
      return i < STRINGS.length ? STRINGS[i] : Integer.toString(i);
   }

   static {
      for (int i = 0; i < STRINGS.length; i++) {
         STRINGS[i] = String.valueOf(i);
      }
   }
}
