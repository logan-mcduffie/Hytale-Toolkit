package it.unimi.dsi.fastutil.longs;

import it.unimi.dsi.fastutil.SafeMath;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.NoSuchElementException;
import java.util.Set;

public class LongArraySet extends AbstractLongSet implements Serializable, Cloneable {
   private static final long serialVersionUID = 1L;
   protected transient long[] a;
   protected int size;

   public LongArraySet(long[] a) {
      this.a = a;
      this.size = a.length;
   }

   public LongArraySet() {
      this.a = LongArrays.EMPTY_ARRAY;
   }

   public LongArraySet(int capacity) {
      this.a = new long[capacity];
   }

   public LongArraySet(LongCollection c) {
      this(c.size());
      this.addAll(c);
   }

   public LongArraySet(Collection<? extends Long> c) {
      this(c.size());
      this.addAll(c);
   }

   public LongArraySet(LongSet c) {
      this(c.size());
      int i = 0;

      for (long x : c) {
         this.a[i] = x;
         i++;
      }

      this.size = i;
   }

   public LongArraySet(Set<? extends Long> c) {
      this(c.size());
      int i = 0;

      for (Long x : c) {
         this.a[i] = x;
         i++;
      }

      this.size = i;
   }

   public LongArraySet(long[] a, int size) {
      this.a = a;
      this.size = size;
      if (size > a.length) {
         throw new IllegalArgumentException("The provided size (" + size + ") is larger than or equal to the array size (" + a.length + ")");
      }
   }

   public static LongArraySet of() {
      return ofUnchecked();
   }

   public static LongArraySet of(long e) {
      return ofUnchecked(e);
   }

   public static LongArraySet of(long... a) {
      if (a.length == 2) {
         if (a[0] == a[1]) {
            throw new IllegalArgumentException("Duplicate element: " + a[1]);
         }
      } else if (a.length > 2) {
         LongOpenHashSet.of(a);
      }

      return ofUnchecked(a);
   }

   public static LongArraySet ofUnchecked() {
      return new LongArraySet();
   }

   public static LongArraySet ofUnchecked(long... a) {
      return new LongArraySet(a);
   }

   private int findKey(long o) {
      int i = this.size;

      while (i-- != 0) {
         if (this.a[i] == o) {
            return i;
         }
      }

      return -1;
   }

   @Override
   public LongIterator iterator() {
      return new LongIterator() {
         int next = 0;

         @Override
         public boolean hasNext() {
            return this.next < LongArraySet.this.size;
         }

         @Override
         public long nextLong() {
            if (!this.hasNext()) {
               throw new NoSuchElementException();
            } else {
               return LongArraySet.this.a[this.next++];
            }
         }

         @Override
         public void remove() {
            int tail = LongArraySet.this.size-- - this.next--;
            System.arraycopy(LongArraySet.this.a, this.next + 1, LongArraySet.this.a, this.next, tail);
         }

         @Override
         public int skip(int n) {
            if (n < 0) {
               throw new IllegalArgumentException("Argument must be nonnegative: " + n);
            } else {
               int remaining = LongArraySet.this.size - this.next;
               if (n < remaining) {
                  this.next += n;
                  return n;
               } else {
                  this.next = LongArraySet.this.size;
                  return remaining;
               }
            }
         }
      };
   }

   @Override
   public LongSpliterator spliterator() {
      return new LongArraySet.Spliterator();
   }

   @Override
   public boolean contains(long k) {
      return this.findKey(k) != -1;
   }

   @Override
   public int size() {
      return this.size;
   }

   @Override
   public boolean remove(long k) {
      int pos = this.findKey(k);
      if (pos == -1) {
         return false;
      } else {
         int tail = this.size - pos - 1;

         for (int i = 0; i < tail; i++) {
            this.a[pos + i] = this.a[pos + i + 1];
         }

         this.size--;
         return true;
      }
   }

   @Override
   public boolean add(long k) {
      int pos = this.findKey(k);
      if (pos != -1) {
         return false;
      } else {
         if (this.size == this.a.length) {
            long[] b = new long[this.size == 0 ? 2 : this.size * 2];
            int i = this.size;

            while (i-- != 0) {
               b[i] = this.a[i];
            }

            this.a = b;
         }

         this.a[this.size++] = k;
         return true;
      }
   }

   @Override
   public void clear() {
      this.size = 0;
   }

   @Override
   public boolean isEmpty() {
      return this.size == 0;
   }

   @Override
   public long[] toLongArray() {
      return this.size == 0 ? LongArrays.EMPTY_ARRAY : Arrays.copyOf(this.a, this.size);
   }

   @Override
   public long[] toArray(long[] a) {
      if (a == null || a.length < this.size) {
         a = new long[this.size];
      }

      System.arraycopy(this.a, 0, a, 0, this.size);
      return a;
   }

   public LongArraySet clone() {
      LongArraySet c;
      try {
         c = (LongArraySet)super.clone();
      } catch (CloneNotSupportedException var3) {
         throw new InternalError();
      }

      c.a = (long[])this.a.clone();
      return c;
   }

   private void writeObject(ObjectOutputStream s) throws IOException {
      s.defaultWriteObject();

      for (int i = 0; i < this.size; i++) {
         s.writeLong(this.a[i]);
      }
   }

   private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
      s.defaultReadObject();
      this.a = new long[this.size];

      for (int i = 0; i < this.size; i++) {
         this.a[i] = s.readLong();
      }
   }

   private final class Spliterator implements LongSpliterator {
      boolean hasSplit = false;
      int pos;
      int max;

      public Spliterator() {
         this(0, LongArraySet.this.size, false);
      }

      private Spliterator(int pos, int max, boolean hasSplit) {
         assert pos <= max : "pos " + pos + " must be <= max " + max;

         this.pos = pos;
         this.max = max;
         this.hasSplit = hasSplit;
      }

      private int getWorkingMax() {
         return this.hasSplit ? this.max : LongArraySet.this.size;
      }

      @Override
      public int characteristics() {
         return 16721;
      }

      @Override
      public long estimateSize() {
         return this.getWorkingMax() - this.pos;
      }

      @Override
      public boolean tryAdvance(java.util.function.LongConsumer action) {
         if (this.pos >= this.getWorkingMax()) {
            return false;
         } else {
            action.accept(LongArraySet.this.a[this.pos++]);
            return true;
         }
      }

      @Override
      public void forEachRemaining(java.util.function.LongConsumer action) {
         for (int max = this.getWorkingMax(); this.pos < max; this.pos++) {
            action.accept(LongArraySet.this.a[this.pos]);
         }
      }

      @Override
      public long skip(long n) {
         if (n < 0L) {
            throw new IllegalArgumentException("Argument must be nonnegative: " + n);
         } else {
            int max = this.getWorkingMax();
            if (this.pos >= max) {
               return 0L;
            } else {
               int remaining = max - this.pos;
               if (n < remaining) {
                  this.pos = SafeMath.safeLongToInt(this.pos + n);
                  return n;
               } else {
                  n = remaining;
                  this.pos = max;
                  return n;
               }
            }
         }
      }

      @Override
      public LongSpliterator trySplit() {
         int max = this.getWorkingMax();
         int retLen = max - this.pos >> 1;
         if (retLen <= 1) {
            return null;
         } else {
            this.max = max;
            int myNewPos = this.pos + retLen;
            int oldPos = this.pos;
            this.pos = myNewPos;
            this.hasSplit = true;
            return LongArraySet.this.new Spliterator(oldPos, myNewPos, true);
         }
      }
   }
}
