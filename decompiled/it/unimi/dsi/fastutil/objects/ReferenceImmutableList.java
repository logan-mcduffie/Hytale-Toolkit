package it.unimi.dsi.fastutil.objects;

import it.unimi.dsi.fastutil.SafeMath;
import java.io.InvalidObjectException;
import java.io.ObjectStreamException;
import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.RandomAccess;
import java.util.function.Consumer;
import java.util.stream.Collector;

public class ReferenceImmutableList<K> extends ReferenceLists.ImmutableListBase<K> implements ReferenceList<K>, RandomAccess, Cloneable, Serializable {
   private static final long serialVersionUID = 0L;
   static final ReferenceImmutableList EMPTY = new ReferenceImmutableList<>(ObjectArrays.EMPTY_ARRAY);
   private final K[] a;
   private static final Collector<Object, ?, ReferenceImmutableList<Object>> TO_LIST_COLLECTOR = Collector.of(
      ReferenceArrayList::new, ReferenceArrayList::add, ReferenceArrayList::combine, ReferenceImmutableList::convertTrustedToImmutableList
   );

   private static final <K> K[] emptyArray() {
      return (K[])ObjectArrays.EMPTY_ARRAY;
   }

   public ReferenceImmutableList(K[] a) {
      this.a = a;
   }

   public ReferenceImmutableList(Collection<? extends K> c) {
      this((K[])(c.isEmpty() ? emptyArray() : ObjectIterators.unwrap(c.iterator())));
   }

   public ReferenceImmutableList(ReferenceCollection<? extends K> c) {
      this((K[])(c.isEmpty() ? emptyArray() : ObjectIterators.unwrap(c.iterator())));
   }

   public ReferenceImmutableList(ReferenceList<? extends K> l) {
      this((K[])(l.isEmpty() ? emptyArray() : new Object[l.size()]));
      l.getElements(0, this.a, 0, l.size());
   }

   public ReferenceImmutableList(K[] a, int offset, int length) {
      this((K[])(length == 0 ? emptyArray() : new Object[length]));
      System.arraycopy(a, offset, this.a, 0, length);
   }

   public ReferenceImmutableList(ObjectIterator<? extends K> i) {
      this((K[])(i.hasNext() ? ObjectIterators.unwrap(i) : emptyArray()));
   }

   public static <K> ReferenceImmutableList<K> of() {
      return EMPTY;
   }

   @SafeVarargs
   public static <K> ReferenceImmutableList<K> of(K... init) {
      return init.length == 0 ? of() : new ReferenceImmutableList<>(init);
   }

   private static <K> ReferenceImmutableList<K> convertTrustedToImmutableList(ReferenceArrayList<K> arrayList) {
      if (arrayList.isEmpty()) {
         return of();
      } else {
         K[] backingArray = arrayList.elements();
         if (arrayList.size() != backingArray.length) {
            backingArray = Arrays.copyOf(backingArray, arrayList.size());
         }

         return new ReferenceImmutableList<>(backingArray);
      }
   }

   public static <K> Collector<K, ?, ReferenceImmutableList<K>> toList() {
      return (Collector<K, ?, ReferenceImmutableList<K>>)TO_LIST_COLLECTOR;
   }

   public static <K> Collector<K, ?, ReferenceImmutableList<K>> toListWithExpectedSize(int expectedSize) {
      return expectedSize <= 10
         ? toList()
         : Collector.of(
            new ReferenceCollections.SizeDecreasingSupplier<>(expectedSize, size -> size <= 10 ? new ReferenceArrayList() : new ReferenceArrayList(size)),
            ReferenceArrayList::add,
            ReferenceArrayList::combine,
            ReferenceImmutableList::convertTrustedToImmutableList
         );
   }

   @Override
   public K get(int index) {
      if (index >= this.a.length) {
         throw new IndexOutOfBoundsException("Index (" + index + ") is greater than or equal to list size (" + this.a.length + ")");
      } else {
         return this.a[index];
      }
   }

   @Override
   public int indexOf(Object k) {
      int i = 0;

      for (int size = this.a.length; i < size; i++) {
         if (k == this.a[i]) {
            return i;
         }
      }

      return -1;
   }

   @Override
   public int lastIndexOf(Object k) {
      int i = this.a.length;

      while (i-- != 0) {
         if (k == this.a[i]) {
            return i;
         }
      }

      return -1;
   }

   @Override
   public int size() {
      return this.a.length;
   }

   @Override
   public boolean isEmpty() {
      return this.a.length == 0;
   }

   @Override
   public void getElements(int from, Object[] a, int offset, int length) {
      ObjectArrays.ensureOffsetLength(a, offset, length);
      System.arraycopy(this.a, from, a, offset, length);
   }

   @Override
   public void forEach(Consumer<? super K> action) {
      for (int i = 0; i < this.a.length; i++) {
         action.accept(this.a[i]);
      }
   }

   @Override
   public Object[] toArray() {
      if (this.a.length == 0) {
         return ObjectArrays.EMPTY_ARRAY;
      } else {
         return this.a.getClass() == Object[].class ? (Object[])this.a.clone() : Arrays.copyOf(this.a, this.a.length, Object[].class);
      }
   }

   @Override
   public <T> T[] toArray(T[] a) {
      if (a == null) {
         a = (T[])(new Object[this.size()]);
      } else if (a.length < this.size()) {
         a = (T[])Array.newInstance(a.getClass().getComponentType(), this.size());
      }

      System.arraycopy(this.a, 0, a, 0, this.size());
      if (a.length > this.size()) {
         a[this.size()] = null;
      }

      return a;
   }

   @Override
   public ObjectListIterator<K> listIterator(final int index) {
      this.ensureIndex(index);
      return new ObjectListIterator<K>() {
         int pos = index;

         @Override
         public boolean hasNext() {
            return this.pos < ReferenceImmutableList.this.a.length;
         }

         @Override
         public boolean hasPrevious() {
            return this.pos > 0;
         }

         @Override
         public K next() {
            if (!this.hasNext()) {
               throw new NoSuchElementException();
            } else {
               return ReferenceImmutableList.this.a[this.pos++];
            }
         }

         @Override
         public K previous() {
            if (!this.hasPrevious()) {
               throw new NoSuchElementException();
            } else {
               return ReferenceImmutableList.this.a[--this.pos];
            }
         }

         @Override
         public int nextIndex() {
            return this.pos;
         }

         @Override
         public int previousIndex() {
            return this.pos - 1;
         }

         @Override
         public void forEachRemaining(Consumer<? super K> action) {
            while (this.pos < ReferenceImmutableList.this.a.length) {
               action.accept(ReferenceImmutableList.this.a[this.pos++]);
            }
         }

         @Override
         public void add(K k) {
            throw new UnsupportedOperationException();
         }

         @Override
         public void set(K k) {
            throw new UnsupportedOperationException();
         }

         @Override
         public void remove() {
            throw new UnsupportedOperationException();
         }

         @Override
         public int back(int n) {
            if (n < 0) {
               throw new IllegalArgumentException("Argument must be nonnegative: " + n);
            } else {
               int remaining = ReferenceImmutableList.this.a.length - this.pos;
               if (n < remaining) {
                  this.pos -= n;
               } else {
                  n = remaining;
                  this.pos = 0;
               }

               return n;
            }
         }

         @Override
         public int skip(int n) {
            if (n < 0) {
               throw new IllegalArgumentException("Argument must be nonnegative: " + n);
            } else {
               int remaining = ReferenceImmutableList.this.a.length - this.pos;
               if (n < remaining) {
                  this.pos += n;
               } else {
                  n = remaining;
                  this.pos = ReferenceImmutableList.this.a.length;
               }

               return n;
            }
         }
      };
   }

   @Override
   public ObjectSpliterator<K> spliterator() {
      return new ReferenceImmutableList.Spliterator();
   }

   @Override
   public ReferenceList<K> subList(int from, int to) {
      if (from == 0 && to == this.size()) {
         return this;
      } else {
         this.ensureIndex(from);
         this.ensureIndex(to);
         if (from == to) {
            return EMPTY;
         } else if (from > to) {
            throw new IllegalArgumentException("Start index (" + from + ") is greater than end index (" + to + ")");
         } else {
            return new ReferenceImmutableList.ImmutableSubList<>(this, from, to);
         }
      }
   }

   public ReferenceImmutableList<K> clone() {
      return this;
   }

   public boolean equals(ReferenceImmutableList<K> l) {
      if (l == this) {
         return true;
      } else if (this.a == l.a) {
         return true;
      } else {
         int s = this.size();
         if (s != l.size()) {
            return false;
         } else {
            K[] a1 = this.a;
            K[] a2 = l.a;

            while (s-- != 0) {
               if (a1[s] != a2[s]) {
                  return false;
               }
            }

            return true;
         }
      }
   }

   @Override
   public boolean equals(Object o) {
      if (o == this) {
         return true;
      } else if (o == null) {
         return false;
      } else if (!(o instanceof List)) {
         return false;
      } else if (o instanceof ReferenceImmutableList) {
         return this.equals((ReferenceImmutableList<K>)o);
      } else {
         return o instanceof ReferenceImmutableList.ImmutableSubList ? ((ReferenceImmutableList.ImmutableSubList)o).equals(this) : super.equals(o);
      }
   }

   private static final class ImmutableSubList<K> extends ReferenceLists.ImmutableListBase<K> implements RandomAccess, Serializable {
      private static final long serialVersionUID = 7054639518438982401L;
      final ReferenceImmutableList<K> innerList;
      final int from;
      final int to;
      final transient K[] a;

      ImmutableSubList(ReferenceImmutableList<K> innerList, int from, int to) {
         this.innerList = innerList;
         this.from = from;
         this.to = to;
         this.a = innerList.a;
      }

      @Override
      public K get(int index) {
         this.ensureRestrictedIndex(index);
         return this.a[index + this.from];
      }

      @Override
      public int indexOf(Object k) {
         for (int i = this.from; i < this.to; i++) {
            if (k == this.a[i]) {
               return i - this.from;
            }
         }

         return -1;
      }

      @Override
      public int lastIndexOf(Object k) {
         int i = this.to;

         while (i-- != this.from) {
            if (k == this.a[i]) {
               return i - this.from;
            }
         }

         return -1;
      }

      @Override
      public int size() {
         return this.to - this.from;
      }

      @Override
      public boolean isEmpty() {
         return this.to <= this.from;
      }

      @Override
      public void getElements(int fromSublistIndex, Object[] a, int offset, int length) {
         ObjectArrays.ensureOffsetLength(a, offset, length);
         this.ensureRestrictedIndex(fromSublistIndex);
         if (this.from + length > this.to) {
            throw new IndexOutOfBoundsException(
               "Final index "
                  + (this.from + length)
                  + " (startingIndex: "
                  + this.from
                  + " + length: "
                  + length
                  + ") is greater then list length "
                  + this.size()
            );
         } else {
            System.arraycopy(this.a, fromSublistIndex + this.from, a, offset, length);
         }
      }

      @Override
      public void forEach(Consumer<? super K> action) {
         for (int i = this.from; i < this.to; i++) {
            action.accept(this.a[i]);
         }
      }

      @Override
      public Object[] toArray() {
         return Arrays.copyOfRange(this.a, this.from, this.to, Object[].class);
      }

      @Override
      public <K> K[] toArray(K[] a) {
         int size = this.size();
         if (a == null) {
            a = (K[])(new Object[size]);
         } else if (a.length < size) {
            a = (K[])Array.newInstance(a.getClass().getComponentType(), size);
         }

         System.arraycopy(this.a, this.from, a, 0, size);
         if (a.length > size) {
            a[size] = null;
         }

         return a;
      }

      @Override
      public ObjectListIterator<K> listIterator(final int index) {
         this.ensureIndex(index);
         return new ObjectListIterator<K>() {
            int pos = index;

            @Override
            public boolean hasNext() {
               return this.pos < ImmutableSubList.this.to;
            }

            @Override
            public boolean hasPrevious() {
               return this.pos > ImmutableSubList.this.from;
            }

            @Override
            public K next() {
               if (!this.hasNext()) {
                  throw new NoSuchElementException();
               } else {
                  return ImmutableSubList.this.a[this.pos++ + ImmutableSubList.this.from];
               }
            }

            @Override
            public K previous() {
               if (!this.hasPrevious()) {
                  throw new NoSuchElementException();
               } else {
                  return ImmutableSubList.this.a[--this.pos + ImmutableSubList.this.from];
               }
            }

            @Override
            public int nextIndex() {
               return this.pos;
            }

            @Override
            public int previousIndex() {
               return this.pos - 1;
            }

            @Override
            public void forEachRemaining(Consumer<? super K> action) {
               while (this.pos < ImmutableSubList.this.to) {
                  action.accept(ImmutableSubList.this.a[this.pos++ + ImmutableSubList.this.from]);
               }
            }

            @Override
            public void add(K k) {
               throw new UnsupportedOperationException();
            }

            @Override
            public void set(K k) {
               throw new UnsupportedOperationException();
            }

            @Override
            public void remove() {
               throw new UnsupportedOperationException();
            }

            @Override
            public int back(int n) {
               if (n < 0) {
                  throw new IllegalArgumentException("Argument must be nonnegative: " + n);
               } else {
                  int remaining = ImmutableSubList.this.to - this.pos;
                  if (n < remaining) {
                     this.pos -= n;
                  } else {
                     n = remaining;
                     this.pos = 0;
                  }

                  return n;
               }
            }

            @Override
            public int skip(int n) {
               if (n < 0) {
                  throw new IllegalArgumentException("Argument must be nonnegative: " + n);
               } else {
                  int remaining = ImmutableSubList.this.to - this.pos;
                  if (n < remaining) {
                     this.pos += n;
                  } else {
                     n = remaining;
                     this.pos = ImmutableSubList.this.to;
                  }

                  return n;
               }
            }
         };
      }

      @Override
      public ObjectSpliterator<K> spliterator() {
         return new ReferenceImmutableList.ImmutableSubList.SubListSpliterator();
      }

      boolean contentsEquals(K[] otherA, int otherAFrom, int otherATo) {
         if (this.a == otherA && this.from == otherAFrom && this.to == otherATo) {
            return true;
         } else if (otherATo - otherAFrom != this.size()) {
            return false;
         } else {
            int pos = this.from;
            int otherPos = otherAFrom;

            while (pos < this.to) {
               if (this.a[pos++] != otherA[otherPos++]) {
                  return false;
               }
            }

            return true;
         }
      }

      @Override
      public boolean equals(Object o) {
         if (o == this) {
            return true;
         } else if (o == null) {
            return false;
         } else if (!(o instanceof List)) {
            return false;
         } else if (o instanceof ReferenceImmutableList) {
            ReferenceImmutableList<K> other = (ReferenceImmutableList<K>)o;
            return this.contentsEquals(other.a, 0, other.size());
         } else if (o instanceof ReferenceImmutableList.ImmutableSubList) {
            ReferenceImmutableList.ImmutableSubList<K> other = (ReferenceImmutableList.ImmutableSubList<K>)o;
            return this.contentsEquals(other.a, other.from, other.to);
         } else {
            return super.equals(o);
         }
      }

      private Object readResolve() throws ObjectStreamException {
         try {
            return this.innerList.subList(this.from, this.to);
         } catch (IndexOutOfBoundsException | IllegalArgumentException var2) {
            throw (InvalidObjectException)new InvalidObjectException(var2.getMessage()).initCause(var2);
         }
      }

      @Override
      public ReferenceList<K> subList(int from, int to) {
         this.ensureIndex(from);
         this.ensureIndex(to);
         if (from == to) {
            return ReferenceImmutableList.EMPTY;
         } else if (from > to) {
            throw new IllegalArgumentException("Start index (" + from + ") is greater than end index (" + to + ")");
         } else {
            return new ReferenceImmutableList.ImmutableSubList<>(this.innerList, from + this.from, to + this.from);
         }
      }

      private final class SubListSpliterator extends ObjectSpliterators.EarlyBindingSizeIndexBasedSpliterator<K> {
         SubListSpliterator() {
            super(ImmutableSubList.this.from, ImmutableSubList.this.to);
         }

         private SubListSpliterator(int pos, int maxPos) {
            super(pos, maxPos);
         }

         @Override
         protected final K get(int i) {
            return ImmutableSubList.this.a[i];
         }

         protected final ReferenceImmutableList.ImmutableSubList<K>.SubListSpliterator makeForSplit(int pos, int maxPos) {
            return ImmutableSubList.this.new SubListSpliterator(pos, maxPos);
         }

         @Override
         public boolean tryAdvance(Consumer<? super K> action) {
            if (this.pos >= this.maxPos) {
               return false;
            } else {
               action.accept(ImmutableSubList.this.a[this.pos++]);
               return true;
            }
         }

         @Override
         public void forEachRemaining(Consumer<? super K> action) {
            int max = this.maxPos;

            while (this.pos < max) {
               action.accept(ImmutableSubList.this.a[this.pos++]);
            }
         }

         @Override
         public int characteristics() {
            return 17488;
         }
      }
   }

   private final class Spliterator implements ObjectSpliterator<K> {
      int pos;
      int max;

      public Spliterator() {
         this(0, ReferenceImmutableList.this.a.length);
      }

      private Spliterator(int pos, int max) {
         assert pos <= max : "pos " + pos + " must be <= max " + max;

         this.pos = pos;
         this.max = max;
      }

      @Override
      public int characteristics() {
         return 17488;
      }

      @Override
      public long estimateSize() {
         return this.max - this.pos;
      }

      @Override
      public boolean tryAdvance(Consumer<? super K> action) {
         if (this.pos >= this.max) {
            return false;
         } else {
            action.accept(ReferenceImmutableList.this.a[this.pos++]);
            return true;
         }
      }

      @Override
      public void forEachRemaining(Consumer<? super K> action) {
         while (this.pos < this.max) {
            action.accept(ReferenceImmutableList.this.a[this.pos]);
            this.pos++;
         }
      }

      @Override
      public long skip(long n) {
         if (n < 0L) {
            throw new IllegalArgumentException("Argument must be nonnegative: " + n);
         } else if (this.pos >= this.max) {
            return 0L;
         } else {
            int remaining = this.max - this.pos;
            if (n < remaining) {
               this.pos = SafeMath.safeLongToInt(this.pos + n);
               return n;
            } else {
               n = remaining;
               this.pos = this.max;
               return n;
            }
         }
      }

      @Override
      public ObjectSpliterator<K> trySplit() {
         int retLen = this.max - this.pos >> 1;
         if (retLen <= 1) {
            return null;
         } else {
            int myNewPos = this.pos + retLen;
            int oldPos = this.pos;
            this.pos = myNewPos;
            return ReferenceImmutableList.this.new Spliterator(oldPos, myNewPos);
         }
      }
   }
}
