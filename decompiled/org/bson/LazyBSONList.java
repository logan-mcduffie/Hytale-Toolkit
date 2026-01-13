package org.bson;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import java.util.Set;

public class LazyBSONList extends LazyBSONObject implements List {
   public LazyBSONList(byte[] bytes, LazyBSONCallback callback) {
      super(bytes, callback);
   }

   public LazyBSONList(byte[] bytes, int offset, LazyBSONCallback callback) {
      super(bytes, offset, callback);
   }

   @Override
   public int size() {
      return this.keySet().size();
   }

   @Override
   public boolean contains(Object o) {
      return this.indexOf(o) > -1;
   }

   @Override
   public Iterator iterator() {
      return new LazyBSONList.LazyBSONListIterator();
   }

   @Override
   public boolean containsAll(Collection collection) {
      Set<Object> values = new HashSet<>();

      for (Object o : this) {
         values.add(o);
      }

      return values.containsAll(collection);
   }

   @Override
   public Object get(int index) {
      return this.get(String.valueOf(index));
   }

   @Override
   public int indexOf(Object o) {
      Iterator it = this.iterator();

      for (int pos = 0; it.hasNext(); pos++) {
         if (o.equals(it.next())) {
            return pos;
         }
      }

      return -1;
   }

   @Override
   public int lastIndexOf(Object o) {
      int lastFound = -1;
      Iterator it = this.iterator();

      for (int pos = 0; it.hasNext(); pos++) {
         if (o.equals(it.next())) {
            lastFound = pos;
         }
      }

      return lastFound;
   }

   @Override
   public ListIterator listIterator() {
      throw new UnsupportedOperationException("Operation is not supported instance of this type");
   }

   @Override
   public ListIterator listIterator(int index) {
      throw new UnsupportedOperationException("Operation is not supported instance of this type");
   }

   @Override
   public boolean add(Object o) {
      throw new UnsupportedOperationException("Object is read only");
   }

   @Override
   public boolean remove(Object o) {
      throw new UnsupportedOperationException("Object is read only");
   }

   @Override
   public boolean addAll(Collection c) {
      throw new UnsupportedOperationException("Object is read only");
   }

   @Override
   public boolean addAll(int index, Collection c) {
      throw new UnsupportedOperationException("Object is read only");
   }

   @Override
   public boolean removeAll(Collection c) {
      throw new UnsupportedOperationException("Object is read only");
   }

   @Override
   public boolean retainAll(Collection c) {
      throw new UnsupportedOperationException("Object is read only");
   }

   @Override
   public void clear() {
      throw new UnsupportedOperationException("Object is read only");
   }

   @Override
   public Object set(int index, Object element) {
      throw new UnsupportedOperationException("Object is read only");
   }

   @Override
   public void add(int index, Object element) {
      throw new UnsupportedOperationException("Object is read only");
   }

   @Override
   public Object remove(int index) {
      throw new UnsupportedOperationException("Object is read only");
   }

   @Override
   public List subList(int fromIndex, int toIndex) {
      throw new UnsupportedOperationException("Operation is not supported");
   }

   @Override
   public Object[] toArray() {
      throw new UnsupportedOperationException("Operation is not supported");
   }

   @Override
   public Object[] toArray(Object[] a) {
      throw new UnsupportedOperationException("Operation is not supported");
   }

   public class LazyBSONListIterator implements Iterator {
      private final BsonBinaryReader reader = LazyBSONList.this.getBsonReader();
      private BsonType cachedBsonType;

      public LazyBSONListIterator() {
         this.reader.readStartDocument();
      }

      @Override
      public boolean hasNext() {
         if (this.cachedBsonType == null) {
            this.cachedBsonType = this.reader.readBsonType();
         }

         return this.cachedBsonType != BsonType.END_OF_DOCUMENT;
      }

      @Override
      public Object next() {
         if (!this.hasNext()) {
            throw new NoSuchElementException();
         } else {
            this.cachedBsonType = null;
            this.reader.readName();
            return LazyBSONList.this.readValue(this.reader);
         }
      }

      @Override
      public void remove() {
         throw new UnsupportedOperationException("Operation is not supported");
      }
   }
}
