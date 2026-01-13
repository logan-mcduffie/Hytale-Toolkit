package it.unimi.dsi.fastutil.bytes;

import java.util.AbstractCollection;
import java.util.Arrays;
import java.util.Collection;

public abstract class AbstractByteCollection extends AbstractCollection<Byte> implements ByteCollection {
   protected AbstractByteCollection() {
   }

   @Override
   public abstract ByteIterator iterator();

   @Override
   public boolean add(byte k) {
      throw new UnsupportedOperationException();
   }

   @Override
   public boolean contains(byte k) {
      ByteIterator iterator = this.iterator();

      while (iterator.hasNext()) {
         if (k == iterator.nextByte()) {
            return true;
         }
      }

      return false;
   }

   @Override
   public boolean rem(byte k) {
      ByteIterator iterator = this.iterator();

      while (iterator.hasNext()) {
         if (k == iterator.nextByte()) {
            iterator.remove();
            return true;
         }
      }

      return false;
   }

   @Deprecated
   @Override
   public boolean add(Byte key) {
      return ByteCollection.super.add(key);
   }

   @Deprecated
   @Override
   public boolean contains(Object key) {
      return ByteCollection.super.contains(key);
   }

   @Deprecated
   @Override
   public boolean remove(Object key) {
      return ByteCollection.super.remove(key);
   }

   @Override
   public byte[] toArray(byte[] a) {
      int size = this.size();
      if (a == null) {
         a = new byte[size];
      } else if (a.length < size) {
         a = Arrays.copyOf(a, size);
      }

      ByteIterators.unwrap(this.iterator(), a);
      return a;
   }

   @Override
   public byte[] toByteArray() {
      int size = this.size();
      if (size == 0) {
         return ByteArrays.EMPTY_ARRAY;
      } else {
         byte[] a = new byte[size];
         ByteIterators.unwrap(this.iterator(), a);
         return a;
      }
   }

   @Deprecated
   @Override
   public byte[] toByteArray(byte[] a) {
      return this.toArray(a);
   }

   @Override
   public boolean addAll(ByteCollection c) {
      boolean retVal = false;
      ByteIterator i = c.iterator();

      while (i.hasNext()) {
         if (this.add(i.nextByte())) {
            retVal = true;
         }
      }

      return retVal;
   }

   @Override
   public boolean addAll(Collection<? extends Byte> c) {
      return c instanceof ByteCollection ? this.addAll((ByteCollection)c) : super.addAll(c);
   }

   @Override
   public boolean containsAll(ByteCollection c) {
      ByteIterator i = c.iterator();

      while (i.hasNext()) {
         if (!this.contains(i.nextByte())) {
            return false;
         }
      }

      return true;
   }

   @Override
   public boolean containsAll(Collection<?> c) {
      return c instanceof ByteCollection ? this.containsAll((ByteCollection)c) : super.containsAll(c);
   }

   @Override
   public boolean removeAll(ByteCollection c) {
      boolean retVal = false;
      ByteIterator i = c.iterator();

      while (i.hasNext()) {
         if (this.rem(i.nextByte())) {
            retVal = true;
         }
      }

      return retVal;
   }

   @Override
   public boolean removeAll(Collection<?> c) {
      return c instanceof ByteCollection ? this.removeAll((ByteCollection)c) : super.removeAll(c);
   }

   @Override
   public boolean retainAll(ByteCollection c) {
      boolean retVal = false;
      ByteIterator i = this.iterator();

      while (i.hasNext()) {
         if (!c.contains(i.nextByte())) {
            i.remove();
            retVal = true;
         }
      }

      return retVal;
   }

   @Override
   public boolean retainAll(Collection<?> c) {
      return c instanceof ByteCollection ? this.retainAll((ByteCollection)c) : super.retainAll(c);
   }

   @Override
   public String toString() {
      StringBuilder s = new StringBuilder();
      ByteIterator i = this.iterator();
      int n = this.size();
      boolean first = true;
      s.append("{");

      while (n-- != 0) {
         if (first) {
            first = false;
         } else {
            s.append(", ");
         }

         byte k = i.nextByte();
         s.append(String.valueOf((int)k));
      }

      s.append("}");
      return s.toString();
   }
}
