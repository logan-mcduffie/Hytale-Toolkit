package it.unimi.dsi.fastutil.objects;

import it.unimi.dsi.fastutil.BigArrays;
import it.unimi.dsi.fastutil.Hash;
import it.unimi.dsi.fastutil.HashCommon;
import it.unimi.dsi.fastutil.Size64;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.function.Consumer;
import java.util.stream.Collector;

public class ObjectOpenHashBigSet<K> extends AbstractObjectSet<K> implements Serializable, Cloneable, Hash, Size64 {
   private static final long serialVersionUID = 0L;
   private static final boolean ASSERTS = false;
   protected transient K[][] key;
   protected transient long mask;
   protected transient int segmentMask;
   protected transient int baseMask;
   protected transient boolean containsNull;
   protected transient long n;
   protected transient long maxFill;
   protected final transient long minN;
   protected final float f;
   protected long size;
   private static final Collector<Object, ?, ObjectOpenHashBigSet<Object>> TO_SET_COLLECTOR = Collector.of(
      ObjectOpenHashBigSet::new, ObjectOpenHashBigSet::add, ObjectOpenHashBigSet::combine
   );

   private void initMasks() {
      this.mask = this.n - 1L;
      this.segmentMask = this.key[0].length - 1;
      this.baseMask = this.key.length - 1;
   }

   public ObjectOpenHashBigSet(long expected, float f) {
      if (f <= 0.0F || f > 1.0F) {
         throw new IllegalArgumentException("Load factor must be greater than 0 and smaller than or equal to 1");
      } else if (this.n < 0L) {
         throw new IllegalArgumentException("The expected number of elements must be nonnegative");
      } else {
         this.f = f;
         this.minN = this.n = HashCommon.bigArraySize(expected, f);
         this.maxFill = HashCommon.maxFill(this.n, f);
         this.key = (K[][])ObjectBigArrays.newBigArray(this.n);
         this.initMasks();
      }
   }

   public ObjectOpenHashBigSet(long expected) {
      this(expected, 0.75F);
   }

   public ObjectOpenHashBigSet() {
      this(16L, 0.75F);
   }

   public ObjectOpenHashBigSet(Collection<? extends K> c, float f) {
      this(Size64.sizeOf(c), f);
      this.addAll(c);
   }

   public ObjectOpenHashBigSet(Collection<? extends K> c) {
      this(c, 0.75F);
   }

   public ObjectOpenHashBigSet(ObjectCollection<? extends K> c, float f) {
      this(Size64.sizeOf(c), f);
      this.addAll(c);
   }

   public ObjectOpenHashBigSet(ObjectCollection<? extends K> c) {
      this(c, 0.75F);
   }

   public ObjectOpenHashBigSet(Iterator<? extends K> i, float f) {
      this(16L, f);

      while (i.hasNext()) {
         this.add((K)i.next());
      }
   }

   public ObjectOpenHashBigSet(Iterator<? extends K> i) {
      this(i, 0.75F);
   }

   public ObjectOpenHashBigSet(K[] a, int offset, int length, float f) {
      this(length < 0 ? 0L : length, f);
      ObjectArrays.ensureOffsetLength(a, offset, length);

      for (int i = 0; i < length; i++) {
         this.add(a[offset + i]);
      }
   }

   public ObjectOpenHashBigSet(K[] a, int offset, int length) {
      this(a, offset, length, 0.75F);
   }

   public ObjectOpenHashBigSet(K[] a, float f) {
      this(a, 0, a.length, f);
   }

   public ObjectOpenHashBigSet(K[] a) {
      this(a, 0.75F);
   }

   private ObjectOpenHashBigSet<K> combine(ObjectOpenHashBigSet<? extends K> toAddFrom) {
      this.addAll(toAddFrom);
      return this;
   }

   public static <K> Collector<K, ?, ObjectOpenHashBigSet<K>> toBigSet() {
      return (Collector<K, ?, ObjectOpenHashBigSet<K>>)TO_SET_COLLECTOR;
   }

   public static <K> Collector<K, ?, ObjectOpenHashBigSet<K>> toBigSetWithExpectedSize(long expectedSize) {
      return Collector.of(() -> new ObjectOpenHashBigSet(expectedSize), ObjectOpenHashBigSet::add, ObjectOpenHashBigSet::combine);
   }

   private long realSize() {
      return this.containsNull ? this.size - 1L : this.size;
   }

   public void ensureCapacity(long capacity) {
      long needed = HashCommon.bigArraySize(capacity, this.f);
      if (needed > this.n) {
         this.rehash(needed);
      }
   }

   @Override
   public boolean addAll(Collection<? extends K> c) {
      long size = Size64.sizeOf(c);
      if (this.f <= 0.5) {
         this.ensureCapacity(size);
      } else {
         this.ensureCapacity(this.size64() + size);
      }

      return super.addAll(c);
   }

   @Override
   public boolean add(K k) {
      if (k == null) {
         if (this.containsNull) {
            return false;
         }

         this.containsNull = true;
      } else {
         K[][] key = this.key;
         long h = HashCommon.mix((long)k.hashCode());
         int displ;
         int base;
         K curr;
         if ((curr = key[base = (int)((h & this.mask) >>> 27)][displ = (int)(h & this.segmentMask)]) != null) {
            if (curr.equals(k)) {
               return false;
            }

            while ((curr = key[base = base + ((displ = displ + 1 & this.segmentMask) == 0 ? 1 : 0) & this.baseMask][displ]) != null) {
               if (curr.equals(k)) {
                  return false;
               }
            }
         }

         key[base][displ] = k;
      }

      if (this.size++ >= this.maxFill) {
         this.rehash(2L * this.n);
      }

      return true;
   }

   public K addOrGet(K k) {
      if (k == null) {
         if (this.containsNull) {
            return null;
         }

         this.containsNull = true;
      } else {
         K[][] key = this.key;
         long h = HashCommon.mix((long)k.hashCode());
         int displ;
         int base;
         K curr;
         if ((curr = key[base = (int)((h & this.mask) >>> 27)][displ = (int)(h & this.segmentMask)]) != null) {
            if (curr.equals(k)) {
               return curr;
            }

            while ((curr = key[base = base + ((displ = displ + 1 & this.segmentMask) == 0 ? 1 : 0) & this.baseMask][displ]) != null) {
               if (curr.equals(k)) {
                  return curr;
               }
            }
         }

         key[base][displ] = k;
      }

      if (this.size++ >= this.maxFill) {
         this.rehash(2L * this.n);
      }

      return k;
   }

   protected final void shiftKeys(long pos) {
      K[][] key = this.key;

      label30:
      while (true) {
         long last = pos;

         for (pos = pos + 1L & this.mask; BigArrays.get(key, pos) != null; pos = pos + 1L & this.mask) {
            long slot = HashCommon.mix((long)BigArrays.get(key, pos).hashCode()) & this.mask;
            if (last <= pos ? last >= slot || slot > pos : last >= slot && slot > pos) {
               BigArrays.set(key, last, BigArrays.get(key, pos));
               continue label30;
            }
         }

         BigArrays.set(key, last, null);
         return;
      }
   }

   private boolean removeEntry(int base, int displ) {
      this.size--;
      this.shiftKeys(base * 134217728L + displ);
      if (this.n > this.minN && this.size < this.maxFill / 4L && this.n > 16L) {
         this.rehash(this.n / 2L);
      }

      return true;
   }

   private boolean removeNullEntry() {
      this.containsNull = false;
      this.size--;
      if (this.n > this.minN && this.size < this.maxFill / 4L && this.n > 16L) {
         this.rehash(this.n / 2L);
      }

      return true;
   }

   @Override
   public boolean remove(Object k) {
      if (k == null) {
         return this.containsNull ? this.removeNullEntry() : false;
      } else {
         K[][] key = this.key;
         long h = HashCommon.mix((long)k.hashCode());
         K curr;
         int displ;
         int base;
         if ((curr = key[base = (int)((h & this.mask) >>> 27)][displ = (int)(h & this.segmentMask)]) == null) {
            return false;
         } else if (curr.equals(k)) {
            return this.removeEntry(base, displ);
         } else {
            while ((curr = key[base = base + ((displ = displ + 1 & this.segmentMask) == 0 ? 1 : 0) & this.baseMask][displ]) != null) {
               if (curr.equals(k)) {
                  return this.removeEntry(base, displ);
               }
            }

            return false;
         }
      }
   }

   @Override
   public boolean contains(Object k) {
      if (k == null) {
         return this.containsNull;
      } else {
         K[][] key = this.key;
         long h = HashCommon.mix((long)k.hashCode());
         K curr;
         int displ;
         int base;
         if ((curr = key[base = (int)((h & this.mask) >>> 27)][displ = (int)(h & this.segmentMask)]) == null) {
            return false;
         } else if (curr.equals(k)) {
            return true;
         } else {
            while ((curr = key[base = base + ((displ = displ + 1 & this.segmentMask) == 0 ? 1 : 0) & this.baseMask][displ]) != null) {
               if (curr.equals(k)) {
                  return true;
               }
            }

            return false;
         }
      }
   }

   public K get(Object k) {
      if (k == null) {
         return null;
      } else {
         K[][] key = this.key;
         long h = HashCommon.mix((long)k.hashCode());
         K curr;
         int displ;
         int base;
         if ((curr = key[base = (int)((h & this.mask) >>> 27)][displ = (int)(h & this.segmentMask)]) == null) {
            return null;
         } else if (curr.equals(k)) {
            return curr;
         } else {
            while ((curr = key[base = base + ((displ = displ + 1 & this.segmentMask) == 0 ? 1 : 0) & this.baseMask][displ]) != null) {
               if (curr.equals(k)) {
                  return curr;
               }
            }

            return null;
         }
      }
   }

   @Override
   public void clear() {
      if (this.size != 0L) {
         this.size = 0L;
         this.containsNull = false;
         BigArrays.fill(this.key, null);
      }
   }

   @Override
   public ObjectIterator<K> iterator() {
      return new ObjectOpenHashBigSet.SetIterator();
   }

   @Override
   public ObjectSpliterator<K> spliterator() {
      return new ObjectOpenHashBigSet.SetSpliterator();
   }

   @Override
   public void forEach(Consumer<? super K> action) {
      if (this.containsNull) {
         action.accept(null);
      }

      long pos = 0L;
      long max = this.n;
      K[][] key = this.key;

      while (pos < max) {
         K gotten = BigArrays.get(key, pos++);
         if (gotten != null) {
            action.accept(gotten);
         }
      }
   }

   public boolean trim() {
      return this.trim(this.size);
   }

   public boolean trim(long n) {
      long l = HashCommon.bigArraySize(n, this.f);
      if (l < this.n && this.size <= HashCommon.maxFill(l, this.f)) {
         try {
            this.rehash(l);
            return true;
         } catch (OutOfMemoryError var6) {
            return false;
         }
      } else {
         return true;
      }
   }

   protected void rehash(long newN) {
      K[][] key = this.key;
      K[][] newKey = (K[][])ObjectBigArrays.newBigArray(newN);
      long mask = newN - 1L;
      int newSegmentMask = newKey[0].length - 1;
      int newBaseMask = newKey.length - 1;
      int base = 0;
      int displ = 0;

      for (long i = this.realSize(); i-- != 0L; base += (displ = displ + 1 & this.segmentMask) == 0 ? 1 : 0) {
         while (key[base][displ] == null) {
            base += (displ = displ + 1 & this.segmentMask) == 0 ? 1 : 0;
         }

         K k = key[base][displ];
         long h = HashCommon.mix((long)k.hashCode());
         int b;
         int d;
         if (newKey[b = (int)((h & mask) >>> 27)][d = (int)(h & newSegmentMask)] != null) {
            while (newKey[b = b + ((d = d + 1 & newSegmentMask) == 0 ? 1 : 0) & newBaseMask][d] != null) {
            }
         }

         newKey[b][d] = k;
      }

      this.n = newN;
      this.key = newKey;
      this.initMasks();
      this.maxFill = HashCommon.maxFill(this.n, this.f);
   }

   @Deprecated
   @Override
   public int size() {
      return (int)Math.min(2147483647L, this.size);
   }

   @Override
   public long size64() {
      return this.size;
   }

   @Override
   public boolean isEmpty() {
      return this.size == 0L;
   }

   public ObjectOpenHashBigSet<K> clone() {
      ObjectOpenHashBigSet<K> c;
      try {
         c = (ObjectOpenHashBigSet<K>)super.clone();
      } catch (CloneNotSupportedException var3) {
         throw new InternalError();
      }

      c.key = (K[][])BigArrays.copy(this.key);
      c.containsNull = this.containsNull;
      return c;
   }

   @Override
   public int hashCode() {
      K[][] key = this.key;
      int h = 0;
      int base = 0;
      int displ = 0;

      for (long j = this.realSize(); j-- != 0L; base += (displ = displ + 1 & this.segmentMask) == 0 ? 1 : 0) {
         while (key[base][displ] == null) {
            base += (displ = displ + 1 & this.segmentMask) == 0 ? 1 : 0;
         }

         if (this != key[base][displ]) {
            h += key[base][displ].hashCode();
         }
      }

      return h;
   }

   private void writeObject(ObjectOutputStream s) throws IOException {
      ObjectIterator<K> i = this.iterator();
      s.defaultWriteObject();
      long j = this.size;

      while (j-- != 0L) {
         s.writeObject(i.next());
      }
   }

   private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
      s.defaultReadObject();
      this.n = HashCommon.bigArraySize(this.size, this.f);
      this.maxFill = HashCommon.maxFill(this.n, this.f);
      K[][] key = this.key = (K[][])ObjectBigArrays.newBigArray(this.n);
      this.initMasks();
      long i = this.size;

      while (i-- != 0L) {
         K k = (K)s.readObject();
         if (k == null) {
            this.containsNull = true;
         } else {
            long h = HashCommon.mix((long)k.hashCode());
            int base;
            int displ;
            if (key[base = (int)((h & this.mask) >>> 27)][displ = (int)(h & this.segmentMask)] != null) {
               while (key[base = base + ((displ = displ + 1 & this.segmentMask) == 0 ? 1 : 0) & this.baseMask][displ] != null) {
               }
            }

            key[base][displ] = k;
         }
      }
   }

   private void checkTable() {
   }

   private class SetIterator implements ObjectIterator<K> {
      int base = ObjectOpenHashBigSet.this.key.length;
      int displ;
      long last = -1L;
      long c = ObjectOpenHashBigSet.this.size;
      boolean mustReturnNull = ObjectOpenHashBigSet.this.containsNull;
      ObjectArrayList<K> wrapped;

      private SetIterator() {
      }

      @Override
      public boolean hasNext() {
         return this.c != 0L;
      }

      @Override
      public K next() {
         if (!this.hasNext()) {
            throw new NoSuchElementException();
         } else {
            this.c--;
            if (this.mustReturnNull) {
               this.mustReturnNull = false;
               this.last = ObjectOpenHashBigSet.this.n;
               return null;
            } else {
               K[][] key = ObjectOpenHashBigSet.this.key;

               while (this.displ != 0 || this.base > 0) {
                  if (this.displ-- == 0) {
                     this.displ = key[--this.base].length - 1;
                  }

                  K k = key[this.base][this.displ];
                  if (k != null) {
                     this.last = this.base * 134217728L + this.displ;
                     return k;
                  }
               }

               this.last = Long.MIN_VALUE;
               return this.wrapped.get(-(--this.base) - 1);
            }
         }
      }

      private final void shiftKeys(long pos) {
         K[][] key = ObjectOpenHashBigSet.this.key;

         label38:
         while (true) {
            long last = pos;

            K curr;
            for (pos = pos + 1L & ObjectOpenHashBigSet.this.mask; (curr = BigArrays.get(key, pos)) != null; pos = pos + 1L & ObjectOpenHashBigSet.this.mask) {
               long slot = HashCommon.mix((long)curr.hashCode()) & ObjectOpenHashBigSet.this.mask;
               if (last <= pos ? last >= slot || slot > pos : last >= slot && slot > pos) {
                  if (pos < last) {
                     if (this.wrapped == null) {
                        this.wrapped = new ObjectArrayList<>();
                     }

                     this.wrapped.add(BigArrays.get(key, pos));
                  }

                  BigArrays.set(key, last, curr);
                  continue label38;
               }
            }

            BigArrays.set(key, last, null);
            return;
         }
      }

      @Override
      public void remove() {
         if (this.last == -1L) {
            throw new IllegalStateException();
         } else {
            if (this.last == ObjectOpenHashBigSet.this.n) {
               ObjectOpenHashBigSet.this.containsNull = false;
            } else {
               if (this.base < 0) {
                  ObjectOpenHashBigSet.this.remove(this.wrapped.set(-this.base - 1, null));
                  this.last = -1L;
                  return;
               }

               this.shiftKeys(this.last);
            }

            ObjectOpenHashBigSet.this.size--;
            this.last = -1L;
         }
      }
   }

   private class SetSpliterator implements ObjectSpliterator<K> {
      private static final int POST_SPLIT_CHARACTERISTICS = 1;
      long pos = 0L;
      long max;
      long c;
      boolean mustReturnNull;
      boolean hasSplit;

      SetSpliterator() {
         this.max = ObjectOpenHashBigSet.this.n;
         this.c = 0L;
         this.mustReturnNull = ObjectOpenHashBigSet.this.containsNull;
         this.hasSplit = false;
      }

      SetSpliterator(long pos, long max, boolean mustReturnNull, boolean hasSplit) {
         this.max = ObjectOpenHashBigSet.this.n;
         this.c = 0L;
         this.mustReturnNull = ObjectOpenHashBigSet.this.containsNull;
         this.hasSplit = false;
         this.pos = pos;
         this.max = max;
         this.mustReturnNull = mustReturnNull;
         this.hasSplit = hasSplit;
      }

      @Override
      public boolean tryAdvance(Consumer<? super K> action) {
         if (this.mustReturnNull) {
            this.mustReturnNull = false;
            this.c++;
            action.accept(null);
            return true;
         } else {
            for (K[][] key = ObjectOpenHashBigSet.this.key; this.pos < this.max; this.pos++) {
               K gotten = BigArrays.get(key, this.pos);
               if (gotten != null) {
                  this.c++;
                  this.pos++;
                  action.accept(gotten);
                  return true;
               }
            }

            return false;
         }
      }

      @Override
      public void forEachRemaining(Consumer<? super K> action) {
         if (this.mustReturnNull) {
            this.mustReturnNull = false;
            action.accept(null);
            this.c++;
         }

         for (K[][] key = ObjectOpenHashBigSet.this.key; this.pos < this.max; this.pos++) {
            K gotten = BigArrays.get(key, this.pos);
            if (gotten != null) {
               action.accept(gotten);
               this.c++;
            }
         }
      }

      @Override
      public int characteristics() {
         return this.hasSplit ? 1 : 65;
      }

      @Override
      public long estimateSize() {
         return !this.hasSplit
            ? ObjectOpenHashBigSet.this.size - this.c
            : Math.min(
               ObjectOpenHashBigSet.this.size - this.c,
               (long)((double)ObjectOpenHashBigSet.this.realSize() / ObjectOpenHashBigSet.this.n * (this.max - this.pos)) + (this.mustReturnNull ? 1 : 0)
            );
      }

      public ObjectOpenHashBigSet<K>.SetSpliterator trySplit() {
         if (this.pos >= this.max - 1L) {
            return null;
         } else {
            long retLen = this.max - this.pos >> 1;
            if (retLen <= 1L) {
               return null;
            } else {
               long myNewPos = this.pos + retLen;
               myNewPos = BigArrays.nearestSegmentStart(myNewPos, this.pos + 1L, this.max - 1L);
               long retPos = this.pos;
               ObjectOpenHashBigSet<K>.SetSpliterator split = ObjectOpenHashBigSet.this.new SetSpliterator(retPos, myNewPos, this.mustReturnNull, true);
               this.pos = myNewPos;
               this.mustReturnNull = false;
               this.hasSplit = true;
               return split;
            }
         }
      }

      @Override
      public long skip(long n) {
         if (n < 0L) {
            throw new IllegalArgumentException("Argument must be nonnegative: " + n);
         } else if (n == 0L) {
            return 0L;
         } else {
            long skipped = 0L;
            if (this.mustReturnNull) {
               this.mustReturnNull = false;
               skipped++;
               n--;
            }

            K[][] key = ObjectOpenHashBigSet.this.key;

            while (this.pos < this.max && n > 0L) {
               if (BigArrays.get(key, this.pos++) != null) {
                  skipped++;
                  n--;
               }
            }

            return skipped;
         }
      }
   }
}
