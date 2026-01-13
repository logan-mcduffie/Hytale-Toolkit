package it.unimi.dsi.fastutil.objects;

import it.unimi.dsi.fastutil.SafeMath;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Collection;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.function.Consumer;

public class ReferenceArraySet<K> extends AbstractReferenceSet<K> implements Serializable, Cloneable {
   private static final long serialVersionUID = 1L;
   protected transient Object[] a;
   protected int size;

   public ReferenceArraySet(Object[] a) {
      this.a = a;
      this.size = a.length;
   }

   public ReferenceArraySet() {
      this.a = ObjectArrays.EMPTY_ARRAY;
   }

   public ReferenceArraySet(int capacity) {
      this.a = new Object[capacity];
   }

   public ReferenceArraySet(ReferenceCollection<K> c) {
      this(c.size());
      this.addAll(c);
   }

   public ReferenceArraySet(Collection<? extends K> c) {
      this(c.size());
      this.addAll(c);
   }

   public ReferenceArraySet(ReferenceSet<K> c) {
      this(c.size());
      int i = 0;

      for (Object x : c) {
         this.a[i] = x;
         i++;
      }

      this.size = i;
   }

   public ReferenceArraySet(Set<? extends K> c) {
      this(c.size());
      int i = 0;

      for (K x : c) {
         this.a[i] = x;
         i++;
      }

      this.size = i;
   }

   public ReferenceArraySet(Object[] a, int size) {
      this.a = a;
      this.size = size;
      if (size > a.length) {
         throw new IllegalArgumentException("The provided size (" + size + ") is larger than or equal to the array size (" + a.length + ")");
      }
   }

   public static <K> ReferenceArraySet<K> of() {
      return ofUnchecked();
   }

   public static <K> ReferenceArraySet<K> of(K e) {
      return ofUnchecked(e);
   }

   @SafeVarargs
   public static <K> ReferenceArraySet<K> of(K... a) {
      if (a.length == 2) {
         if (a[0] == a[1]) {
            throw new IllegalArgumentException("Duplicate element: " + a[1]);
         }
      } else if (a.length > 2) {
         ReferenceOpenHashSet.of(a);
      }

      return ofUnchecked(a);
   }

   public static <K> ReferenceArraySet<K> ofUnchecked() {
      return new ReferenceArraySet<>();
   }

   @SafeVarargs
   public static <K> ReferenceArraySet<K> ofUnchecked(K... a) {
      return new ReferenceArraySet<>(a);
   }

   private int findKey(Object o) {
      int i = this.size;

      while (i-- != 0) {
         if (this.a[i] == o) {
            return i;
         }
      }

      return -1;
   }

   @Override
   public ObjectIterator<K> iterator() {
      return new ObjectIterator<K>() {
         int next = 0;

         @Override
         public boolean hasNext() {
            return this.next < ReferenceArraySet.this.size;
         }

         @Override
         public K next() {
            if (!this.hasNext()) {
               throw new NoSuchElementException();
            } else {
               return (K)ReferenceArraySet.this.a[this.next++];
            }
         }

         @Override
         public void remove() {
            int tail = ReferenceArraySet.this.size-- - this.next--;
            System.arraycopy(ReferenceArraySet.this.a, this.next + 1, ReferenceArraySet.this.a, this.next, tail);
            ReferenceArraySet.this.a[ReferenceArraySet.this.size] = null;
         }

         @Override
         public int skip(int n) {
            if (n < 0) {
               throw new IllegalArgumentException("Argument must be nonnegative: " + n);
            } else {
               int remaining = ReferenceArraySet.this.size - this.next;
               if (n < remaining) {
                  this.next += n;
                  return n;
               } else {
                  this.next = ReferenceArraySet.this.size;
                  return remaining;
               }
            }
         }
      };
   }

   @Override
   public ObjectSpliterator<K> spliterator() {
      return new ReferenceArraySet.Spliterator();
   }

   @Override
   public boolean contains(Object k) {
      return this.findKey(k) != -1;
   }

   @Override
   public int size() {
      return this.size;
   }

   @Override
   public boolean remove(Object k) {
      int pos = this.findKey(k);
      if (pos == -1) {
         return false;
      } else {
         int tail = this.size - pos - 1;

         for (int i = 0; i < tail; i++) {
            this.a[pos + i] = this.a[pos + i + 1];
         }

         this.size--;
         this.a[this.size] = null;
         return true;
      }
   }

   @Override
   public boolean add(K k) {
      int pos = this.findKey(k);
      if (pos != -1) {
         return false;
      } else {
         if (this.size == this.a.length) {
            Object[] b = new Object[this.size == 0 ? 2 : this.size * 2];
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
      Arrays.fill(this.a, 0, this.size, null);
      this.size = 0;
   }

   @Override
   public boolean isEmpty() {
      return this.size == 0;
   }

   @Override
   public Object[] toArray() {
      int size = this.size();
      return size == 0 ? ObjectArrays.EMPTY_ARRAY : Arrays.copyOf(this.a, size, Object[].class);
   }

   @Override
   public <T> T[] toArray(T[] a) {
      if (a == null) {
         a = (T[])(new Object[this.size]);
      } else if (a.length < this.size) {
         a = (T[])Array.newInstance(a.getClass().getComponentType(), this.size);
      }

      System.arraycopy(this.a, 0, a, 0, this.size);
      if (a.length > this.size) {
         a[this.size] = null;
      }

      return a;
   }

   public ReferenceArraySet<K> clone() {
      ReferenceArraySet<K> c;
      try {
         c = (ReferenceArraySet<K>)super.clone();
      } catch (CloneNotSupportedException var3) {
         throw new InternalError();
      }

      c.a = (Object[])this.a.clone();
      return c;
   }

   private void writeObject(ObjectOutputStream s) throws IOException {
      s.defaultWriteObject();

      for (int i = 0; i < this.size; i++) {
         s.writeObject(this.a[i]);
      }
   }

   private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
      s.defaultReadObject();
      this.a = new Object[this.size];

      for (int i = 0; i < this.size; i++) {
         this.a[i] = s.readObject();
      }
   }

   private final class Spliterator implements ObjectSpliterator<K> {
      boolean hasSplit = false;
      int pos;
      int max;

      public Spliterator() {
         this(0, ReferenceArraySet.this.size, false);
      }

      private Spliterator(int pos, int max, boolean hasSplit) {
         assert pos <= max : "pos " + pos + " must be <= max " + max;

         this.pos = pos;
         this.max = max;
         this.hasSplit = hasSplit;
      }

      private int getWorkingMax() {
         return this.hasSplit ? this.max : ReferenceArraySet.this.size;
      }

      @Override
      public int characteristics() {
         return 16465;
      }

      @Override
      public long estimateSize() {
         return this.getWorkingMax() - this.pos;
      }

      @Override
      public boolean tryAdvance(Consumer<? super K> action) {
         if (this.pos >= this.getWorkingMax()) {
            return false;
         } else {
            action.accept((K)ReferenceArraySet.this.a[this.pos++]);
            return true;
         }
      }

      @Override
      public void forEachRemaining(Consumer<? super K> action) {
         for (int max = this.getWorkingMax(); this.pos < max; this.pos++) {
            action.accept((K)ReferenceArraySet.this.a[this.pos]);
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
      public ObjectSpliterator<K> trySplit() {
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
            return ReferenceArraySet.this.new Spliterator(oldPos, myNewPos, true);
         }
      }
   }
}
