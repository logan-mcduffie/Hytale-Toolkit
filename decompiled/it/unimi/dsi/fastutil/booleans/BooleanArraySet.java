package it.unimi.dsi.fastutil.booleans;

import it.unimi.dsi.fastutil.SafeMath;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.NoSuchElementException;
import java.util.Set;

public class BooleanArraySet extends AbstractBooleanSet implements Serializable, Cloneable {
   private static final long serialVersionUID = 1L;
   protected transient boolean[] a;
   protected int size;

   public BooleanArraySet(boolean[] a) {
      this.a = a;
      this.size = a.length;
   }

   public BooleanArraySet() {
      this.a = BooleanArrays.EMPTY_ARRAY;
   }

   public BooleanArraySet(int capacity) {
      this.a = new boolean[capacity];
   }

   public BooleanArraySet(BooleanCollection c) {
      this(c.size());
      this.addAll(c);
   }

   public BooleanArraySet(Collection<? extends Boolean> c) {
      this(c.size());
      this.addAll(c);
   }

   public BooleanArraySet(BooleanSet c) {
      this(c.size());
      int i = 0;

      for (boolean x : c) {
         this.a[i] = x;
         i++;
      }

      this.size = i;
   }

   public BooleanArraySet(Set<? extends Boolean> c) {
      this(c.size());
      int i = 0;

      for (Boolean x : c) {
         this.a[i] = x;
         i++;
      }

      this.size = i;
   }

   public BooleanArraySet(boolean[] a, int size) {
      this.a = a;
      this.size = size;
      if (size > a.length) {
         throw new IllegalArgumentException("The provided size (" + size + ") is larger than or equal to the array size (" + a.length + ")");
      }
   }

   public static BooleanArraySet of() {
      return ofUnchecked();
   }

   public static BooleanArraySet of(boolean e) {
      return ofUnchecked(e);
   }

   public static BooleanArraySet of(boolean... a) {
      if (a.length == 2) {
         if (a[0] == a[1]) {
            throw new IllegalArgumentException("Duplicate element: " + a[1]);
         }
      } else if (a.length > 2) {
         BooleanOpenHashSet.of(a);
      }

      return ofUnchecked(a);
   }

   public static BooleanArraySet ofUnchecked() {
      return new BooleanArraySet();
   }

   public static BooleanArraySet ofUnchecked(boolean... a) {
      return new BooleanArraySet(a);
   }

   private int findKey(boolean o) {
      int i = this.size;

      while (i-- != 0) {
         if (this.a[i] == o) {
            return i;
         }
      }

      return -1;
   }

   @Override
   public BooleanIterator iterator() {
      return new BooleanIterator() {
         int next = 0;

         @Override
         public boolean hasNext() {
            return this.next < BooleanArraySet.this.size;
         }

         @Override
         public boolean nextBoolean() {
            if (!this.hasNext()) {
               throw new NoSuchElementException();
            } else {
               return BooleanArraySet.this.a[this.next++];
            }
         }

         @Override
         public void remove() {
            int tail = BooleanArraySet.this.size-- - this.next--;
            System.arraycopy(BooleanArraySet.this.a, this.next + 1, BooleanArraySet.this.a, this.next, tail);
         }

         @Override
         public int skip(int n) {
            if (n < 0) {
               throw new IllegalArgumentException("Argument must be nonnegative: " + n);
            } else {
               int remaining = BooleanArraySet.this.size - this.next;
               if (n < remaining) {
                  this.next += n;
                  return n;
               } else {
                  this.next = BooleanArraySet.this.size;
                  return remaining;
               }
            }
         }
      };
   }

   @Override
   public BooleanSpliterator spliterator() {
      return new BooleanArraySet.Spliterator();
   }

   @Override
   public boolean contains(boolean k) {
      return this.findKey(k) != -1;
   }

   @Override
   public int size() {
      return this.size;
   }

   @Override
   public boolean remove(boolean k) {
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
   public boolean add(boolean k) {
      int pos = this.findKey(k);
      if (pos != -1) {
         return false;
      } else {
         if (this.size == this.a.length) {
            boolean[] b = new boolean[this.size == 0 ? 2 : this.size * 2];
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
   public boolean[] toBooleanArray() {
      return this.size == 0 ? BooleanArrays.EMPTY_ARRAY : Arrays.copyOf(this.a, this.size);
   }

   @Override
   public boolean[] toArray(boolean[] a) {
      if (a == null || a.length < this.size) {
         a = new boolean[this.size];
      }

      System.arraycopy(this.a, 0, a, 0, this.size);
      return a;
   }

   public BooleanArraySet clone() {
      BooleanArraySet c;
      try {
         c = (BooleanArraySet)super.clone();
      } catch (CloneNotSupportedException var3) {
         throw new InternalError();
      }

      c.a = (boolean[])this.a.clone();
      return c;
   }

   private void writeObject(ObjectOutputStream s) throws IOException {
      s.defaultWriteObject();

      for (int i = 0; i < this.size; i++) {
         s.writeBoolean(this.a[i]);
      }
   }

   private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
      s.defaultReadObject();
      this.a = new boolean[this.size];

      for (int i = 0; i < this.size; i++) {
         this.a[i] = s.readBoolean();
      }
   }

   private final class Spliterator implements BooleanSpliterator {
      boolean hasSplit = false;
      int pos;
      int max;

      public Spliterator() {
         this(0, BooleanArraySet.this.size, false);
      }

      private Spliterator(int pos, int max, boolean hasSplit) {
         assert pos <= max : "pos " + pos + " must be <= max " + max;

         this.pos = pos;
         this.max = max;
         this.hasSplit = hasSplit;
      }

      private int getWorkingMax() {
         return this.hasSplit ? this.max : BooleanArraySet.this.size;
      }

      @Override
      public int characteristics() {
         return 16721;
      }

      @Override
      public long estimateSize() {
         return this.getWorkingMax() - this.pos;
      }

      public boolean tryAdvance(BooleanConsumer action) {
         if (this.pos >= this.getWorkingMax()) {
            return false;
         } else {
            action.accept(BooleanArraySet.this.a[this.pos++]);
            return true;
         }
      }

      public void forEachRemaining(BooleanConsumer action) {
         for (int max = this.getWorkingMax(); this.pos < max; this.pos++) {
            action.accept(BooleanArraySet.this.a[this.pos]);
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
      public BooleanSpliterator trySplit() {
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
            return BooleanArraySet.this.new Spliterator(oldPos, myNewPos, true);
         }
      }
   }
}
