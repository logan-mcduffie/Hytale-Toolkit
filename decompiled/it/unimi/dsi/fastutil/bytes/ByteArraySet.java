package it.unimi.dsi.fastutil.bytes;

import it.unimi.dsi.fastutil.SafeMath;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.NoSuchElementException;
import java.util.Set;

public class ByteArraySet extends AbstractByteSet implements Serializable, Cloneable {
   private static final long serialVersionUID = 1L;
   protected transient byte[] a;
   protected int size;

   public ByteArraySet(byte[] a) {
      this.a = a;
      this.size = a.length;
   }

   public ByteArraySet() {
      this.a = ByteArrays.EMPTY_ARRAY;
   }

   public ByteArraySet(int capacity) {
      this.a = new byte[capacity];
   }

   public ByteArraySet(ByteCollection c) {
      this(c.size());
      this.addAll(c);
   }

   public ByteArraySet(Collection<? extends Byte> c) {
      this(c.size());
      this.addAll(c);
   }

   public ByteArraySet(ByteSet c) {
      this(c.size());
      int i = 0;

      for (byte x : c) {
         this.a[i] = x;
         i++;
      }

      this.size = i;
   }

   public ByteArraySet(Set<? extends Byte> c) {
      this(c.size());
      int i = 0;

      for (Byte x : c) {
         this.a[i] = x;
         i++;
      }

      this.size = i;
   }

   public ByteArraySet(byte[] a, int size) {
      this.a = a;
      this.size = size;
      if (size > a.length) {
         throw new IllegalArgumentException("The provided size (" + size + ") is larger than or equal to the array size (" + a.length + ")");
      }
   }

   public static ByteArraySet of() {
      return ofUnchecked();
   }

   public static ByteArraySet of(byte e) {
      return ofUnchecked(e);
   }

   public static ByteArraySet of(byte... a) {
      if (a.length == 2) {
         if (a[0] == a[1]) {
            throw new IllegalArgumentException("Duplicate element: " + a[1]);
         }
      } else if (a.length > 2) {
         ByteOpenHashSet.of(a);
      }

      return ofUnchecked(a);
   }

   public static ByteArraySet ofUnchecked() {
      return new ByteArraySet();
   }

   public static ByteArraySet ofUnchecked(byte... a) {
      return new ByteArraySet(a);
   }

   private int findKey(byte o) {
      int i = this.size;

      while (i-- != 0) {
         if (this.a[i] == o) {
            return i;
         }
      }

      return -1;
   }

   @Override
   public ByteIterator iterator() {
      return new ByteIterator() {
         int next = 0;

         @Override
         public boolean hasNext() {
            return this.next < ByteArraySet.this.size;
         }

         @Override
         public byte nextByte() {
            if (!this.hasNext()) {
               throw new NoSuchElementException();
            } else {
               return ByteArraySet.this.a[this.next++];
            }
         }

         @Override
         public void remove() {
            int tail = ByteArraySet.this.size-- - this.next--;
            System.arraycopy(ByteArraySet.this.a, this.next + 1, ByteArraySet.this.a, this.next, tail);
         }

         @Override
         public int skip(int n) {
            if (n < 0) {
               throw new IllegalArgumentException("Argument must be nonnegative: " + n);
            } else {
               int remaining = ByteArraySet.this.size - this.next;
               if (n < remaining) {
                  this.next += n;
                  return n;
               } else {
                  this.next = ByteArraySet.this.size;
                  return remaining;
               }
            }
         }
      };
   }

   @Override
   public ByteSpliterator spliterator() {
      return new ByteArraySet.Spliterator();
   }

   @Override
   public boolean contains(byte k) {
      return this.findKey(k) != -1;
   }

   @Override
   public int size() {
      return this.size;
   }

   @Override
   public boolean remove(byte k) {
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
   public boolean add(byte k) {
      int pos = this.findKey(k);
      if (pos != -1) {
         return false;
      } else {
         if (this.size == this.a.length) {
            byte[] b = new byte[this.size == 0 ? 2 : this.size * 2];
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
   public byte[] toByteArray() {
      return this.size == 0 ? ByteArrays.EMPTY_ARRAY : Arrays.copyOf(this.a, this.size);
   }

   @Override
   public byte[] toArray(byte[] a) {
      if (a == null || a.length < this.size) {
         a = new byte[this.size];
      }

      System.arraycopy(this.a, 0, a, 0, this.size);
      return a;
   }

   public ByteArraySet clone() {
      ByteArraySet c;
      try {
         c = (ByteArraySet)super.clone();
      } catch (CloneNotSupportedException var3) {
         throw new InternalError();
      }

      c.a = (byte[])this.a.clone();
      return c;
   }

   private void writeObject(ObjectOutputStream s) throws IOException {
      s.defaultWriteObject();

      for (int i = 0; i < this.size; i++) {
         s.writeByte(this.a[i]);
      }
   }

   private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
      s.defaultReadObject();
      this.a = new byte[this.size];

      for (int i = 0; i < this.size; i++) {
         this.a[i] = s.readByte();
      }
   }

   private final class Spliterator implements ByteSpliterator {
      boolean hasSplit = false;
      int pos;
      int max;

      public Spliterator() {
         this(0, ByteArraySet.this.size, false);
      }

      private Spliterator(int pos, int max, boolean hasSplit) {
         assert pos <= max : "pos " + pos + " must be <= max " + max;

         this.pos = pos;
         this.max = max;
         this.hasSplit = hasSplit;
      }

      private int getWorkingMax() {
         return this.hasSplit ? this.max : ByteArraySet.this.size;
      }

      @Override
      public int characteristics() {
         return 16721;
      }

      @Override
      public long estimateSize() {
         return this.getWorkingMax() - this.pos;
      }

      public boolean tryAdvance(ByteConsumer action) {
         if (this.pos >= this.getWorkingMax()) {
            return false;
         } else {
            action.accept(ByteArraySet.this.a[this.pos++]);
            return true;
         }
      }

      public void forEachRemaining(ByteConsumer action) {
         for (int max = this.getWorkingMax(); this.pos < max; this.pos++) {
            action.accept(ByteArraySet.this.a[this.pos]);
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
      public ByteSpliterator trySplit() {
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
            return ByteArraySet.this.new Spliterator(oldPos, myNewPos, true);
         }
      }
   }
}
