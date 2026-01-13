package it.unimi.dsi.fastutil.shorts;

import it.unimi.dsi.fastutil.SafeMath;
import java.io.InvalidObjectException;
import java.io.ObjectStreamException;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.RandomAccess;

public class ShortImmutableList extends ShortLists.ImmutableListBase implements ShortList, RandomAccess, Cloneable, Serializable {
   private static final long serialVersionUID = 0L;
   static final ShortImmutableList EMPTY = new ShortImmutableList(ShortArrays.EMPTY_ARRAY);
   private final short[] a;

   public ShortImmutableList(short[] a) {
      this.a = a;
   }

   public ShortImmutableList(Collection<? extends Short> c) {
      this(c.isEmpty() ? ShortArrays.EMPTY_ARRAY : ShortIterators.unwrap(ShortIterators.asShortIterator(c.iterator())));
   }

   public ShortImmutableList(ShortCollection c) {
      this(c.isEmpty() ? ShortArrays.EMPTY_ARRAY : ShortIterators.unwrap(c.iterator()));
   }

   public ShortImmutableList(ShortList l) {
      this(l.isEmpty() ? ShortArrays.EMPTY_ARRAY : new short[l.size()]);
      l.getElements(0, this.a, 0, l.size());
   }

   public ShortImmutableList(short[] a, int offset, int length) {
      this(length == 0 ? ShortArrays.EMPTY_ARRAY : new short[length]);
      System.arraycopy(a, offset, this.a, 0, length);
   }

   public ShortImmutableList(ShortIterator i) {
      this(i.hasNext() ? ShortIterators.unwrap(i) : ShortArrays.EMPTY_ARRAY);
   }

   public static ShortImmutableList of() {
      return EMPTY;
   }

   public static ShortImmutableList of(short... init) {
      return init.length == 0 ? of() : new ShortImmutableList(init);
   }

   @Override
   public short getShort(int index) {
      if (index >= this.a.length) {
         throw new IndexOutOfBoundsException("Index (" + index + ") is greater than or equal to list size (" + this.a.length + ")");
      } else {
         return this.a[index];
      }
   }

   @Override
   public int indexOf(short k) {
      int i = 0;

      for (int size = this.a.length; i < size; i++) {
         if (k == this.a[i]) {
            return i;
         }
      }

      return -1;
   }

   @Override
   public int lastIndexOf(short k) {
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
   public void getElements(int from, short[] a, int offset, int length) {
      ShortArrays.ensureOffsetLength(a, offset, length);
      System.arraycopy(this.a, from, a, offset, length);
   }

   @Override
   public void forEach(ShortConsumer action) {
      for (int i = 0; i < this.a.length; i++) {
         action.accept(this.a[i]);
      }
   }

   @Override
   public short[] toShortArray() {
      return this.a.length == 0 ? ShortArrays.EMPTY_ARRAY : (short[])this.a.clone();
   }

   @Override
   public short[] toArray(short[] a) {
      if (a == null || a.length < this.size()) {
         a = new short[this.a.length];
      }

      System.arraycopy(this.a, 0, a, 0, a.length);
      return a;
   }

   @Override
   public ShortListIterator listIterator(final int index) {
      this.ensureIndex(index);
      return new ShortListIterator() {
         int pos = index;

         @Override
         public boolean hasNext() {
            return this.pos < ShortImmutableList.this.a.length;
         }

         @Override
         public boolean hasPrevious() {
            return this.pos > 0;
         }

         @Override
         public short nextShort() {
            if (!this.hasNext()) {
               throw new NoSuchElementException();
            } else {
               return ShortImmutableList.this.a[this.pos++];
            }
         }

         @Override
         public short previousShort() {
            if (!this.hasPrevious()) {
               throw new NoSuchElementException();
            } else {
               return ShortImmutableList.this.a[--this.pos];
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
         public void forEachRemaining(ShortConsumer action) {
            while (this.pos < ShortImmutableList.this.a.length) {
               action.accept(ShortImmutableList.this.a[this.pos++]);
            }
         }

         @Override
         public void add(short k) {
            throw new UnsupportedOperationException();
         }

         @Override
         public void set(short k) {
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
               int remaining = ShortImmutableList.this.a.length - this.pos;
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
               int remaining = ShortImmutableList.this.a.length - this.pos;
               if (n < remaining) {
                  this.pos += n;
               } else {
                  n = remaining;
                  this.pos = ShortImmutableList.this.a.length;
               }

               return n;
            }
         }
      };
   }

   @Override
   public ShortSpliterator spliterator() {
      return new ShortImmutableList.Spliterator();
   }

   @Override
   public ShortList subList(int from, int to) {
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
            return new ShortImmutableList.ImmutableSubList(this, from, to);
         }
      }
   }

   public ShortImmutableList clone() {
      return this;
   }

   public boolean equals(ShortImmutableList l) {
      if (l == this) {
         return true;
      } else if (this.a == l.a) {
         return true;
      } else {
         int s = this.size();
         if (s != l.size()) {
            return false;
         } else {
            short[] a1 = this.a;
            short[] a2 = l.a;
            return Arrays.equals(a1, a2);
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
      } else if (o instanceof ShortImmutableList) {
         return this.equals((ShortImmutableList)o);
      } else {
         return o instanceof ShortImmutableList.ImmutableSubList ? ((ShortImmutableList.ImmutableSubList)o).equals(this) : super.equals(o);
      }
   }

   public int compareTo(ShortImmutableList l) {
      if (this.a == l.a) {
         return 0;
      } else {
         int s1 = this.size();
         int s2 = l.size();
         short[] a1 = this.a;
         short[] a2 = l.a;

         int i;
         for (i = 0; i < s1 && i < s2; i++) {
            short e1 = a1[i];
            short e2 = a2[i];
            int r;
            if ((r = Short.compare(e1, e2)) != 0) {
               return r;
            }
         }

         return i < s2 ? -1 : (i < s1 ? 1 : 0);
      }
   }

   @Override
   public int compareTo(List<? extends Short> l) {
      if (l instanceof ShortImmutableList) {
         return this.compareTo((ShortImmutableList)l);
      } else if (l instanceof ShortImmutableList.ImmutableSubList) {
         ShortImmutableList.ImmutableSubList other = (ShortImmutableList.ImmutableSubList)l;
         return -other.compareTo(this);
      } else {
         return super.compareTo(l);
      }
   }

   private static final class ImmutableSubList extends ShortLists.ImmutableListBase implements RandomAccess, Serializable {
      private static final long serialVersionUID = 7054639518438982401L;
      final ShortImmutableList innerList;
      final int from;
      final int to;
      final transient short[] a;

      ImmutableSubList(ShortImmutableList innerList, int from, int to) {
         this.innerList = innerList;
         this.from = from;
         this.to = to;
         this.a = innerList.a;
      }

      @Override
      public short getShort(int index) {
         this.ensureRestrictedIndex(index);
         return this.a[index + this.from];
      }

      @Override
      public int indexOf(short k) {
         for (int i = this.from; i < this.to; i++) {
            if (k == this.a[i]) {
               return i - this.from;
            }
         }

         return -1;
      }

      @Override
      public int lastIndexOf(short k) {
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
      public void getElements(int fromSublistIndex, short[] a, int offset, int length) {
         ShortArrays.ensureOffsetLength(a, offset, length);
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
      public void forEach(ShortConsumer action) {
         for (int i = this.from; i < this.to; i++) {
            action.accept(this.a[i]);
         }
      }

      @Override
      public short[] toShortArray() {
         return Arrays.copyOfRange(this.a, this.from, this.to);
      }

      @Override
      public short[] toArray(short[] a) {
         if (a == null || a.length < this.size()) {
            a = new short[this.size()];
         }

         System.arraycopy(this.a, this.from, a, 0, this.size());
         return a;
      }

      @Override
      public ShortListIterator listIterator(final int index) {
         this.ensureIndex(index);
         return new ShortListIterator() {
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
            public short nextShort() {
               if (!this.hasNext()) {
                  throw new NoSuchElementException();
               } else {
                  return ImmutableSubList.this.a[this.pos++ + ImmutableSubList.this.from];
               }
            }

            @Override
            public short previousShort() {
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
            public void forEachRemaining(ShortConsumer action) {
               while (this.pos < ImmutableSubList.this.to) {
                  action.accept(ImmutableSubList.this.a[this.pos++ + ImmutableSubList.this.from]);
               }
            }

            @Override
            public void add(short k) {
               throw new UnsupportedOperationException();
            }

            @Override
            public void set(short k) {
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
      public ShortSpliterator spliterator() {
         return new ShortImmutableList.ImmutableSubList.SubListSpliterator();
      }

      boolean contentsEquals(short[] otherA, int otherAFrom, int otherATo) {
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
         } else if (o instanceof ShortImmutableList) {
            ShortImmutableList other = (ShortImmutableList)o;
            return this.contentsEquals(other.a, 0, other.size());
         } else if (o instanceof ShortImmutableList.ImmutableSubList) {
            ShortImmutableList.ImmutableSubList other = (ShortImmutableList.ImmutableSubList)o;
            return this.contentsEquals(other.a, other.from, other.to);
         } else {
            return super.equals(o);
         }
      }

      int contentsCompareTo(short[] otherA, int otherAFrom, int otherATo) {
         if (this.a == otherA && this.from == otherAFrom && this.to == otherATo) {
            return 0;
         } else {
            int i = this.from;

            for (int j = otherAFrom; i < this.to && i < otherATo; j++) {
               short e1 = this.a[i];
               short e2 = otherA[j];
               int r;
               if ((r = Short.compare(e1, e2)) != 0) {
                  return r;
               }

               i++;
            }

            return i < otherATo ? -1 : (i < this.to ? 1 : 0);
         }
      }

      @Override
      public int compareTo(List<? extends Short> l) {
         if (l instanceof ShortImmutableList) {
            ShortImmutableList other = (ShortImmutableList)l;
            return this.contentsCompareTo(other.a, 0, other.size());
         } else if (l instanceof ShortImmutableList.ImmutableSubList) {
            ShortImmutableList.ImmutableSubList other = (ShortImmutableList.ImmutableSubList)l;
            return this.contentsCompareTo(other.a, other.from, other.to);
         } else {
            return super.compareTo(l);
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
      public ShortList subList(int from, int to) {
         this.ensureIndex(from);
         this.ensureIndex(to);
         if (from == to) {
            return ShortImmutableList.EMPTY;
         } else if (from > to) {
            throw new IllegalArgumentException("Start index (" + from + ") is greater than end index (" + to + ")");
         } else {
            return new ShortImmutableList.ImmutableSubList(this.innerList, from + this.from, to + this.from);
         }
      }

      private final class SubListSpliterator extends ShortSpliterators.EarlyBindingSizeIndexBasedSpliterator {
         SubListSpliterator() {
            super(ImmutableSubList.this.from, ImmutableSubList.this.to);
         }

         private SubListSpliterator(int pos, int maxPos) {
            super(pos, maxPos);
         }

         @Override
         protected final short get(int i) {
            return ImmutableSubList.this.a[i];
         }

         protected final ShortImmutableList.ImmutableSubList.SubListSpliterator makeForSplit(int pos, int maxPos) {
            return ImmutableSubList.this.new SubListSpliterator(pos, maxPos);
         }

         @Override
         public boolean tryAdvance(ShortConsumer action) {
            if (this.pos >= this.maxPos) {
               return false;
            } else {
               action.accept(ImmutableSubList.this.a[this.pos++]);
               return true;
            }
         }

         @Override
         public void forEachRemaining(ShortConsumer action) {
            int max = this.maxPos;

            while (this.pos < max) {
               action.accept(ImmutableSubList.this.a[this.pos++]);
            }
         }

         @Override
         public int characteristics() {
            return 17744;
         }
      }
   }

   private final class Spliterator implements ShortSpliterator {
      int pos;
      int max;

      public Spliterator() {
         this(0, ShortImmutableList.this.a.length);
      }

      private Spliterator(int pos, int max) {
         assert pos <= max : "pos " + pos + " must be <= max " + max;

         this.pos = pos;
         this.max = max;
      }

      @Override
      public int characteristics() {
         return 17744;
      }

      @Override
      public long estimateSize() {
         return this.max - this.pos;
      }

      public boolean tryAdvance(ShortConsumer action) {
         if (this.pos >= this.max) {
            return false;
         } else {
            action.accept(ShortImmutableList.this.a[this.pos++]);
            return true;
         }
      }

      public void forEachRemaining(ShortConsumer action) {
         while (this.pos < this.max) {
            action.accept(ShortImmutableList.this.a[this.pos]);
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
      public ShortSpliterator trySplit() {
         int retLen = this.max - this.pos >> 1;
         if (retLen <= 1) {
            return null;
         } else {
            int myNewPos = this.pos + retLen;
            int oldPos = this.pos;
            this.pos = myNewPos;
            return ShortImmutableList.this.new Spliterator(oldPos, myNewPos);
         }
      }
   }
}
