package it.unimi.dsi.fastutil.shorts;

import it.unimi.dsi.fastutil.SafeMath;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.NoSuchElementException;
import java.util.Set;

public class ShortArraySet extends AbstractShortSet implements Serializable, Cloneable {
   private static final long serialVersionUID = 1L;
   protected transient short[] a;
   protected int size;

   public ShortArraySet(short[] a) {
      this.a = a;
      this.size = a.length;
   }

   public ShortArraySet() {
      this.a = ShortArrays.EMPTY_ARRAY;
   }

   public ShortArraySet(int capacity) {
      this.a = new short[capacity];
   }

   public ShortArraySet(ShortCollection c) {
      this(c.size());
      this.addAll(c);
   }

   public ShortArraySet(Collection<? extends Short> c) {
      this(c.size());
      this.addAll(c);
   }

   public ShortArraySet(ShortSet c) {
      this(c.size());
      int i = 0;

      for (short x : c) {
         this.a[i] = x;
         i++;
      }

      this.size = i;
   }

   public ShortArraySet(Set<? extends Short> c) {
      this(c.size());
      int i = 0;

      for (Short x : c) {
         this.a[i] = x;
         i++;
      }

      this.size = i;
   }

   public ShortArraySet(short[] a, int size) {
      this.a = a;
      this.size = size;
      if (size > a.length) {
         throw new IllegalArgumentException("The provided size (" + size + ") is larger than or equal to the array size (" + a.length + ")");
      }
   }

   public static ShortArraySet of() {
      return ofUnchecked();
   }

   public static ShortArraySet of(short e) {
      return ofUnchecked(e);
   }

   public static ShortArraySet of(short... a) {
      if (a.length == 2) {
         if (a[0] == a[1]) {
            throw new IllegalArgumentException("Duplicate element: " + a[1]);
         }
      } else if (a.length > 2) {
         ShortOpenHashSet.of(a);
      }

      return ofUnchecked(a);
   }

   public static ShortArraySet ofUnchecked() {
      return new ShortArraySet();
   }

   public static ShortArraySet ofUnchecked(short... a) {
      return new ShortArraySet(a);
   }

   private int findKey(short o) {
      int i = this.size;

      while (i-- != 0) {
         if (this.a[i] == o) {
            return i;
         }
      }

      return -1;
   }

   @Override
   public ShortIterator iterator() {
      return new ShortIterator() {
         int next = 0;

         @Override
         public boolean hasNext() {
            return this.next < ShortArraySet.this.size;
         }

         @Override
         public short nextShort() {
            if (!this.hasNext()) {
               throw new NoSuchElementException();
            } else {
               return ShortArraySet.this.a[this.next++];
            }
         }

         @Override
         public void remove() {
            int tail = ShortArraySet.this.size-- - this.next--;
            System.arraycopy(ShortArraySet.this.a, this.next + 1, ShortArraySet.this.a, this.next, tail);
         }

         @Override
         public int skip(int n) {
            if (n < 0) {
               throw new IllegalArgumentException("Argument must be nonnegative: " + n);
            } else {
               int remaining = ShortArraySet.this.size - this.next;
               if (n < remaining) {
                  this.next += n;
                  return n;
               } else {
                  this.next = ShortArraySet.this.size;
                  return remaining;
               }
            }
         }
      };
   }

   @Override
   public ShortSpliterator spliterator() {
      return new ShortArraySet.Spliterator();
   }

   @Override
   public boolean contains(short k) {
      return this.findKey(k) != -1;
   }

   @Override
   public int size() {
      return this.size;
   }

   @Override
   public boolean remove(short k) {
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
   public boolean add(short k) {
      int pos = this.findKey(k);
      if (pos != -1) {
         return false;
      } else {
         if (this.size == this.a.length) {
            short[] b = new short[this.size == 0 ? 2 : this.size * 2];
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
   public short[] toShortArray() {
      return this.size == 0 ? ShortArrays.EMPTY_ARRAY : Arrays.copyOf(this.a, this.size);
   }

   @Override
   public short[] toArray(short[] a) {
      if (a == null || a.length < this.size) {
         a = new short[this.size];
      }

      System.arraycopy(this.a, 0, a, 0, this.size);
      return a;
   }

   public ShortArraySet clone() {
      ShortArraySet c;
      try {
         c = (ShortArraySet)super.clone();
      } catch (CloneNotSupportedException var3) {
         throw new InternalError();
      }

      c.a = (short[])this.a.clone();
      return c;
   }

   private void writeObject(ObjectOutputStream s) throws IOException {
      s.defaultWriteObject();

      for (int i = 0; i < this.size; i++) {
         s.writeShort(this.a[i]);
      }
   }

   private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
      s.defaultReadObject();
      this.a = new short[this.size];

      for (int i = 0; i < this.size; i++) {
         this.a[i] = s.readShort();
      }
   }

   private final class Spliterator implements ShortSpliterator {
      boolean hasSplit = false;
      int pos;
      int max;

      public Spliterator() {
         this(0, ShortArraySet.this.size, false);
      }

      private Spliterator(int pos, int max, boolean hasSplit) {
         assert pos <= max : "pos " + pos + " must be <= max " + max;

         this.pos = pos;
         this.max = max;
         this.hasSplit = hasSplit;
      }

      private int getWorkingMax() {
         return this.hasSplit ? this.max : ShortArraySet.this.size;
      }

      @Override
      public int characteristics() {
         return 16721;
      }

      @Override
      public long estimateSize() {
         return this.getWorkingMax() - this.pos;
      }

      public boolean tryAdvance(ShortConsumer action) {
         if (this.pos >= this.getWorkingMax()) {
            return false;
         } else {
            action.accept(ShortArraySet.this.a[this.pos++]);
            return true;
         }
      }

      public void forEachRemaining(ShortConsumer action) {
         for (int max = this.getWorkingMax(); this.pos < max; this.pos++) {
            action.accept(ShortArraySet.this.a[this.pos]);
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
      public ShortSpliterator trySplit() {
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
            return ShortArraySet.this.new Spliterator(oldPos, myNewPos, true);
         }
      }
   }
}
