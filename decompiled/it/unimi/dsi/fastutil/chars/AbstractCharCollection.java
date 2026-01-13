package it.unimi.dsi.fastutil.chars;

import java.util.AbstractCollection;
import java.util.Arrays;
import java.util.Collection;

public abstract class AbstractCharCollection extends AbstractCollection<Character> implements CharCollection {
   protected AbstractCharCollection() {
   }

   @Override
   public abstract CharIterator iterator();

   @Override
   public boolean add(char k) {
      throw new UnsupportedOperationException();
   }

   @Override
   public boolean contains(char k) {
      CharIterator iterator = this.iterator();

      while (iterator.hasNext()) {
         if (k == iterator.nextChar()) {
            return true;
         }
      }

      return false;
   }

   @Override
   public boolean rem(char k) {
      CharIterator iterator = this.iterator();

      while (iterator.hasNext()) {
         if (k == iterator.nextChar()) {
            iterator.remove();
            return true;
         }
      }

      return false;
   }

   @Deprecated
   @Override
   public boolean add(Character key) {
      return CharCollection.super.add(key);
   }

   @Deprecated
   @Override
   public boolean contains(Object key) {
      return CharCollection.super.contains(key);
   }

   @Deprecated
   @Override
   public boolean remove(Object key) {
      return CharCollection.super.remove(key);
   }

   @Override
   public char[] toArray(char[] a) {
      int size = this.size();
      if (a == null) {
         a = new char[size];
      } else if (a.length < size) {
         a = Arrays.copyOf(a, size);
      }

      CharIterators.unwrap(this.iterator(), a);
      return a;
   }

   @Override
   public char[] toCharArray() {
      int size = this.size();
      if (size == 0) {
         return CharArrays.EMPTY_ARRAY;
      } else {
         char[] a = new char[size];
         CharIterators.unwrap(this.iterator(), a);
         return a;
      }
   }

   @Deprecated
   @Override
   public char[] toCharArray(char[] a) {
      return this.toArray(a);
   }

   @Override
   public boolean addAll(CharCollection c) {
      boolean retVal = false;
      CharIterator i = c.iterator();

      while (i.hasNext()) {
         if (this.add(i.nextChar())) {
            retVal = true;
         }
      }

      return retVal;
   }

   @Override
   public boolean addAll(Collection<? extends Character> c) {
      return c instanceof CharCollection ? this.addAll((CharCollection)c) : super.addAll(c);
   }

   @Override
   public boolean containsAll(CharCollection c) {
      CharIterator i = c.iterator();

      while (i.hasNext()) {
         if (!this.contains(i.nextChar())) {
            return false;
         }
      }

      return true;
   }

   @Override
   public boolean containsAll(Collection<?> c) {
      return c instanceof CharCollection ? this.containsAll((CharCollection)c) : super.containsAll(c);
   }

   @Override
   public boolean removeAll(CharCollection c) {
      boolean retVal = false;
      CharIterator i = c.iterator();

      while (i.hasNext()) {
         if (this.rem(i.nextChar())) {
            retVal = true;
         }
      }

      return retVal;
   }

   @Override
   public boolean removeAll(Collection<?> c) {
      return c instanceof CharCollection ? this.removeAll((CharCollection)c) : super.removeAll(c);
   }

   @Override
   public boolean retainAll(CharCollection c) {
      boolean retVal = false;
      CharIterator i = this.iterator();

      while (i.hasNext()) {
         if (!c.contains(i.nextChar())) {
            i.remove();
            retVal = true;
         }
      }

      return retVal;
   }

   @Override
   public boolean retainAll(Collection<?> c) {
      return c instanceof CharCollection ? this.retainAll((CharCollection)c) : super.retainAll(c);
   }

   @Override
   public String toString() {
      StringBuilder s = new StringBuilder();
      CharIterator i = this.iterator();
      int n = this.size();
      boolean first = true;
      s.append("{");

      while (n-- != 0) {
         if (first) {
            first = false;
         } else {
            s.append(", ");
         }

         char k = i.nextChar();
         s.append(String.valueOf(k));
      }

      s.append("}");
      return s.toString();
   }
}
