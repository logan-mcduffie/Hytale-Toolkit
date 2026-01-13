package it.unimi.dsi.fastutil.doubles;

import it.unimi.dsi.fastutil.Hash;
import it.unimi.dsi.fastutil.HashCommon;
import it.unimi.dsi.fastutil.Size64;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.stream.DoubleStream;

public class DoubleLinkedOpenHashSet extends AbstractDoubleSortedSet implements Serializable, Cloneable, Hash {
   private static final long serialVersionUID = 0L;
   private static final boolean ASSERTS = false;
   protected transient double[] key;
   protected transient int mask;
   protected transient boolean containsNull;
   protected transient int first = -1;
   protected transient int last = -1;
   protected transient long[] link;
   protected transient int n;
   protected transient int maxFill;
   protected final transient int minN;
   protected int size;
   protected final float f;
   private static final int SPLITERATOR_CHARACTERISTICS = 337;

   public DoubleLinkedOpenHashSet(int expected, float f) {
      if (f <= 0.0F || f >= 1.0F) {
         throw new IllegalArgumentException("Load factor must be greater than 0 and smaller than 1");
      } else if (expected < 0) {
         throw new IllegalArgumentException("The expected number of elements must be nonnegative");
      } else {
         this.f = f;
         this.minN = this.n = HashCommon.arraySize(expected, f);
         this.mask = this.n - 1;
         this.maxFill = HashCommon.maxFill(this.n, f);
         this.key = new double[this.n + 1];
         this.link = new long[this.n + 1];
      }
   }

   public DoubleLinkedOpenHashSet(int expected) {
      this(expected, 0.75F);
   }

   public DoubleLinkedOpenHashSet() {
      this(16, 0.75F);
   }

   public DoubleLinkedOpenHashSet(Collection<? extends Double> c, float f) {
      this(c.size(), f);
      this.addAll(c);
   }

   public DoubleLinkedOpenHashSet(Collection<? extends Double> c) {
      this(c, 0.75F);
   }

   public DoubleLinkedOpenHashSet(DoubleCollection c, float f) {
      this(c.size(), f);
      this.addAll(c);
   }

   public DoubleLinkedOpenHashSet(DoubleCollection c) {
      this(c, 0.75F);
   }

   public DoubleLinkedOpenHashSet(DoubleIterator i, float f) {
      this(16, f);

      while (i.hasNext()) {
         this.add(i.nextDouble());
      }
   }

   public DoubleLinkedOpenHashSet(DoubleIterator i) {
      this(i, 0.75F);
   }

   public DoubleLinkedOpenHashSet(Iterator<?> i, float f) {
      this(DoubleIterators.asDoubleIterator(i), f);
   }

   public DoubleLinkedOpenHashSet(Iterator<?> i) {
      this(DoubleIterators.asDoubleIterator(i));
   }

   public DoubleLinkedOpenHashSet(double[] a, int offset, int length, float f) {
      this(length < 0 ? 0 : length, f);
      DoubleArrays.ensureOffsetLength(a, offset, length);

      for (int i = 0; i < length; i++) {
         this.add(a[offset + i]);
      }
   }

   public DoubleLinkedOpenHashSet(double[] a, int offset, int length) {
      this(a, offset, length, 0.75F);
   }

   public DoubleLinkedOpenHashSet(double[] a, float f) {
      this(a, 0, a.length, f);
   }

   public DoubleLinkedOpenHashSet(double[] a) {
      this(a, 0.75F);
   }

   public static DoubleLinkedOpenHashSet of() {
      return new DoubleLinkedOpenHashSet();
   }

   public static DoubleLinkedOpenHashSet of(double e) {
      DoubleLinkedOpenHashSet result = new DoubleLinkedOpenHashSet(1, 0.75F);
      result.add(e);
      return result;
   }

   public static DoubleLinkedOpenHashSet of(double e0, double e1) {
      DoubleLinkedOpenHashSet result = new DoubleLinkedOpenHashSet(2, 0.75F);
      result.add(e0);
      if (!result.add(e1)) {
         throw new IllegalArgumentException("Duplicate element: " + e1);
      } else {
         return result;
      }
   }

   public static DoubleLinkedOpenHashSet of(double e0, double e1, double e2) {
      DoubleLinkedOpenHashSet result = new DoubleLinkedOpenHashSet(3, 0.75F);
      result.add(e0);
      if (!result.add(e1)) {
         throw new IllegalArgumentException("Duplicate element: " + e1);
      } else if (!result.add(e2)) {
         throw new IllegalArgumentException("Duplicate element: " + e2);
      } else {
         return result;
      }
   }

   public static DoubleLinkedOpenHashSet of(double... a) {
      DoubleLinkedOpenHashSet result = new DoubleLinkedOpenHashSet(a.length, 0.75F);

      for (double element : a) {
         if (!result.add(element)) {
            throw new IllegalArgumentException("Duplicate element " + element);
         }
      }

      return result;
   }

   public static DoubleLinkedOpenHashSet toSet(DoubleStream stream) {
      return stream.collect(DoubleLinkedOpenHashSet::new, DoubleLinkedOpenHashSet::add, DoubleLinkedOpenHashSet::addAll);
   }

   public static DoubleLinkedOpenHashSet toSetWithExpectedSize(DoubleStream stream, int expectedSize) {
      return expectedSize <= 16
         ? toSet(stream)
         : stream.collect(
            new DoubleCollections.SizeDecreasingSupplier<>(expectedSize, size -> size <= 16 ? new DoubleLinkedOpenHashSet() : new DoubleLinkedOpenHashSet(size)),
            DoubleLinkedOpenHashSet::add,
            DoubleLinkedOpenHashSet::addAll
         );
   }

   private int realSize() {
      return this.containsNull ? this.size - 1 : this.size;
   }

   public void ensureCapacity(int capacity) {
      int needed = HashCommon.arraySize(capacity, this.f);
      if (needed > this.n) {
         this.rehash(needed);
      }
   }

   private void tryCapacity(long capacity) {
      int needed = (int)Math.min(1073741824L, Math.max(2L, HashCommon.nextPowerOfTwo((long)Math.ceil((float)capacity / this.f))));
      if (needed > this.n) {
         this.rehash(needed);
      }
   }

   @Override
   public boolean addAll(DoubleCollection c) {
      if (this.f <= 0.5) {
         this.ensureCapacity(c.size());
      } else {
         this.tryCapacity(this.size() + c.size());
      }

      return super.addAll(c);
   }

   @Override
   public boolean addAll(Collection<? extends Double> c) {
      if (this.f <= 0.5) {
         this.ensureCapacity(c.size());
      } else {
         this.tryCapacity(this.size() + c.size());
      }

      return super.addAll(c);
   }

   @Override
   public boolean add(double k) {
      int pos;
      if (Double.doubleToLongBits(k) == 0L) {
         if (this.containsNull) {
            return false;
         }

         pos = this.n;
         this.containsNull = true;
      } else {
         double[] key = this.key;
         double curr;
         if (Double.doubleToLongBits(curr = key[pos = (int)HashCommon.mix(Double.doubleToRawLongBits(k)) & this.mask]) != 0L) {
            if (Double.doubleToLongBits(curr) == Double.doubleToLongBits(k)) {
               return false;
            }

            while (Double.doubleToLongBits(curr = key[pos = pos + 1 & this.mask]) != 0L) {
               if (Double.doubleToLongBits(curr) == Double.doubleToLongBits(k)) {
                  return false;
               }
            }
         }

         key[pos] = k;
      }

      if (this.size == 0) {
         this.first = this.last = pos;
         this.link[pos] = -1L;
      } else {
         this.link[this.last] = this.link[this.last] ^ (this.link[this.last] ^ pos & 4294967295L) & 4294967295L;
         this.link[pos] = (this.last & 4294967295L) << 32 | 4294967295L;
         this.last = pos;
      }

      if (this.size++ >= this.maxFill) {
         this.rehash(HashCommon.arraySize(this.size + 1, this.f));
      }

      return true;
   }

   protected final void shiftKeys(int pos) {
      double[] key = this.key;

      label30:
      while (true) {
         int last = pos;

         double curr;
         for (pos = pos + 1 & this.mask; Double.doubleToLongBits(curr = key[pos]) != 0L; pos = pos + 1 & this.mask) {
            int slot = (int)HashCommon.mix(Double.doubleToRawLongBits(curr)) & this.mask;
            if (last <= pos ? last >= slot || slot > pos : last >= slot && slot > pos) {
               key[last] = curr;
               this.fixPointers(pos, last);
               continue label30;
            }
         }

         key[last] = 0.0;
         return;
      }
   }

   private boolean removeEntry(int pos) {
      this.size--;
      this.fixPointers(pos);
      this.shiftKeys(pos);
      if (this.n > this.minN && this.size < this.maxFill / 4 && this.n > 16) {
         this.rehash(this.n / 2);
      }

      return true;
   }

   private boolean removeNullEntry() {
      this.containsNull = false;
      this.key[this.n] = 0.0;
      this.size--;
      this.fixPointers(this.n);
      if (this.n > this.minN && this.size < this.maxFill / 4 && this.n > 16) {
         this.rehash(this.n / 2);
      }

      return true;
   }

   @Override
   public boolean remove(double k) {
      if (Double.doubleToLongBits(k) == 0L) {
         return this.containsNull ? this.removeNullEntry() : false;
      } else {
         double[] key = this.key;
         double curr;
         int pos;
         if (Double.doubleToLongBits(curr = key[pos = (int)HashCommon.mix(Double.doubleToRawLongBits(k)) & this.mask]) == 0L) {
            return false;
         } else if (Double.doubleToLongBits(k) == Double.doubleToLongBits(curr)) {
            return this.removeEntry(pos);
         } else {
            while (Double.doubleToLongBits(curr = key[pos = pos + 1 & this.mask]) != 0L) {
               if (Double.doubleToLongBits(k) == Double.doubleToLongBits(curr)) {
                  return this.removeEntry(pos);
               }
            }

            return false;
         }
      }
   }

   @Override
   public boolean contains(double k) {
      if (Double.doubleToLongBits(k) == 0L) {
         return this.containsNull;
      } else {
         double[] key = this.key;
         double curr;
         int pos;
         if (Double.doubleToLongBits(curr = key[pos = (int)HashCommon.mix(Double.doubleToRawLongBits(k)) & this.mask]) == 0L) {
            return false;
         } else if (Double.doubleToLongBits(k) == Double.doubleToLongBits(curr)) {
            return true;
         } else {
            while (Double.doubleToLongBits(curr = key[pos = pos + 1 & this.mask]) != 0L) {
               if (Double.doubleToLongBits(k) == Double.doubleToLongBits(curr)) {
                  return true;
               }
            }

            return false;
         }
      }
   }

   public double removeFirstDouble() {
      if (this.size == 0) {
         throw new NoSuchElementException();
      } else {
         int pos = this.first;
         if (this.size == 1) {
            this.first = this.last = -1;
         } else {
            this.first = (int)this.link[pos];
            if (0 <= this.first) {
               this.link[this.first] = this.link[this.first] | -4294967296L;
            }
         }

         double k = this.key[pos];
         this.size--;
         if (Double.doubleToLongBits(k) == 0L) {
            this.containsNull = false;
            this.key[this.n] = 0.0;
         } else {
            this.shiftKeys(pos);
         }

         if (this.n > this.minN && this.size < this.maxFill / 4 && this.n > 16) {
            this.rehash(this.n / 2);
         }

         return k;
      }
   }

   public double removeLastDouble() {
      if (this.size == 0) {
         throw new NoSuchElementException();
      } else {
         int pos = this.last;
         if (this.size == 1) {
            this.first = this.last = -1;
         } else {
            this.last = (int)(this.link[pos] >>> 32);
            if (0 <= this.last) {
               this.link[this.last] = this.link[this.last] | 4294967295L;
            }
         }

         double k = this.key[pos];
         this.size--;
         if (Double.doubleToLongBits(k) == 0L) {
            this.containsNull = false;
            this.key[this.n] = 0.0;
         } else {
            this.shiftKeys(pos);
         }

         if (this.n > this.minN && this.size < this.maxFill / 4 && this.n > 16) {
            this.rehash(this.n / 2);
         }

         return k;
      }
   }

   private void moveIndexToFirst(int i) {
      if (this.size != 1 && this.first != i) {
         if (this.last == i) {
            this.last = (int)(this.link[i] >>> 32);
            this.link[this.last] = this.link[this.last] | 4294967295L;
         } else {
            long linki = this.link[i];
            int prev = (int)(linki >>> 32);
            int next = (int)linki;
            this.link[prev] = this.link[prev] ^ (this.link[prev] ^ linki & 4294967295L) & 4294967295L;
            this.link[next] = this.link[next] ^ (this.link[next] ^ linki & -4294967296L) & -4294967296L;
         }

         this.link[this.first] = this.link[this.first] ^ (this.link[this.first] ^ (i & 4294967295L) << 32) & -4294967296L;
         this.link[i] = -4294967296L | this.first & 4294967295L;
         this.first = i;
      }
   }

   private void moveIndexToLast(int i) {
      if (this.size != 1 && this.last != i) {
         if (this.first == i) {
            this.first = (int)this.link[i];
            this.link[this.first] = this.link[this.first] | -4294967296L;
         } else {
            long linki = this.link[i];
            int prev = (int)(linki >>> 32);
            int next = (int)linki;
            this.link[prev] = this.link[prev] ^ (this.link[prev] ^ linki & 4294967295L) & 4294967295L;
            this.link[next] = this.link[next] ^ (this.link[next] ^ linki & -4294967296L) & -4294967296L;
         }

         this.link[this.last] = this.link[this.last] ^ (this.link[this.last] ^ i & 4294967295L) & 4294967295L;
         this.link[i] = (this.last & 4294967295L) << 32 | 4294967295L;
         this.last = i;
      }
   }

   public boolean addAndMoveToFirst(double k) {
      int pos;
      if (Double.doubleToLongBits(k) == 0L) {
         if (this.containsNull) {
            this.moveIndexToFirst(this.n);
            return false;
         }

         this.containsNull = true;
         pos = this.n;
      } else {
         double[] key = this.key;

         for (pos = (int)HashCommon.mix(Double.doubleToRawLongBits(k)) & this.mask; Double.doubleToLongBits(key[pos]) != 0L; pos = pos + 1 & this.mask) {
            if (Double.doubleToLongBits(k) == Double.doubleToLongBits(key[pos])) {
               this.moveIndexToFirst(pos);
               return false;
            }
         }
      }

      this.key[pos] = k;
      if (this.size == 0) {
         this.first = this.last = pos;
         this.link[pos] = -1L;
      } else {
         this.link[this.first] = this.link[this.first] ^ (this.link[this.first] ^ (pos & 4294967295L) << 32) & -4294967296L;
         this.link[pos] = -4294967296L | this.first & 4294967295L;
         this.first = pos;
      }

      if (this.size++ >= this.maxFill) {
         this.rehash(HashCommon.arraySize(this.size, this.f));
      }

      return true;
   }

   public boolean addAndMoveToLast(double k) {
      int pos;
      if (Double.doubleToLongBits(k) == 0L) {
         if (this.containsNull) {
            this.moveIndexToLast(this.n);
            return false;
         }

         this.containsNull = true;
         pos = this.n;
      } else {
         double[] key = this.key;

         for (pos = (int)HashCommon.mix(Double.doubleToRawLongBits(k)) & this.mask; Double.doubleToLongBits(key[pos]) != 0L; pos = pos + 1 & this.mask) {
            if (Double.doubleToLongBits(k) == Double.doubleToLongBits(key[pos])) {
               this.moveIndexToLast(pos);
               return false;
            }
         }
      }

      this.key[pos] = k;
      if (this.size == 0) {
         this.first = this.last = pos;
         this.link[pos] = -1L;
      } else {
         this.link[this.last] = this.link[this.last] ^ (this.link[this.last] ^ pos & 4294967295L) & 4294967295L;
         this.link[pos] = (this.last & 4294967295L) << 32 | 4294967295L;
         this.last = pos;
      }

      if (this.size++ >= this.maxFill) {
         this.rehash(HashCommon.arraySize(this.size, this.f));
      }

      return true;
   }

   @Override
   public void clear() {
      if (this.size != 0) {
         this.size = 0;
         this.containsNull = false;
         Arrays.fill(this.key, 0.0);
         this.first = this.last = -1;
      }
   }

   @Override
   public int size() {
      return this.size;
   }

   @Override
   public boolean isEmpty() {
      return this.size == 0;
   }

   protected void fixPointers(int i) {
      if (this.size == 0) {
         this.first = this.last = -1;
      } else if (this.first == i) {
         this.first = (int)this.link[i];
         if (0 <= this.first) {
            this.link[this.first] = this.link[this.first] | -4294967296L;
         }
      } else if (this.last == i) {
         this.last = (int)(this.link[i] >>> 32);
         if (0 <= this.last) {
            this.link[this.last] = this.link[this.last] | 4294967295L;
         }
      } else {
         long linki = this.link[i];
         int prev = (int)(linki >>> 32);
         int next = (int)linki;
         this.link[prev] = this.link[prev] ^ (this.link[prev] ^ linki & 4294967295L) & 4294967295L;
         this.link[next] = this.link[next] ^ (this.link[next] ^ linki & -4294967296L) & -4294967296L;
      }
   }

   protected void fixPointers(int s, int d) {
      if (this.size == 1) {
         this.first = this.last = d;
         this.link[d] = -1L;
      } else if (this.first == s) {
         this.first = d;
         this.link[(int)this.link[s]] = this.link[(int)this.link[s]] ^ (this.link[(int)this.link[s]] ^ (d & 4294967295L) << 32) & -4294967296L;
         this.link[d] = this.link[s];
      } else if (this.last == s) {
         this.last = d;
         this.link[(int)(this.link[s] >>> 32)] = this.link[(int)(this.link[s] >>> 32)]
            ^ (this.link[(int)(this.link[s] >>> 32)] ^ d & 4294967295L) & 4294967295L;
         this.link[d] = this.link[s];
      } else {
         long links = this.link[s];
         int prev = (int)(links >>> 32);
         int next = (int)links;
         this.link[prev] = this.link[prev] ^ (this.link[prev] ^ d & 4294967295L) & 4294967295L;
         this.link[next] = this.link[next] ^ (this.link[next] ^ (d & 4294967295L) << 32) & -4294967296L;
         this.link[d] = links;
      }
   }

   @Override
   public double firstDouble() {
      if (this.size == 0) {
         throw new NoSuchElementException();
      } else {
         return this.key[this.first];
      }
   }

   @Override
   public double lastDouble() {
      if (this.size == 0) {
         throw new NoSuchElementException();
      } else {
         return this.key[this.last];
      }
   }

   @Override
   public DoubleSortedSet tailSet(double from) {
      throw new UnsupportedOperationException();
   }

   @Override
   public DoubleSortedSet headSet(double to) {
      throw new UnsupportedOperationException();
   }

   @Override
   public DoubleSortedSet subSet(double from, double to) {
      throw new UnsupportedOperationException();
   }

   @Override
   public DoubleComparator comparator() {
      return null;
   }

   public DoubleListIterator iterator(double from) {
      return new DoubleLinkedOpenHashSet.SetIterator(from);
   }

   public DoubleListIterator iterator() {
      return new DoubleLinkedOpenHashSet.SetIterator();
   }

   @Override
   public DoubleSpliterator spliterator() {
      return DoubleSpliterators.asSpliterator(this.iterator(), Size64.sizeOf(this), 337);
   }

   @Override
   public void forEach(java.util.function.DoubleConsumer action) {
      int next = this.first;

      while (next != -1) {
         int curr = next;
         next = (int)this.link[next];
         action.accept(this.key[curr]);
      }
   }

   public boolean trim() {
      return this.trim(this.size);
   }

   public boolean trim(int n) {
      int l = HashCommon.nextPowerOfTwo((int)Math.ceil(n / this.f));
      if (l < this.n && this.size <= HashCommon.maxFill(l, this.f)) {
         try {
            this.rehash(l);
            return true;
         } catch (OutOfMemoryError var4) {
            return false;
         }
      } else {
         return true;
      }
   }

   protected void rehash(int newN) {
      double[] key = this.key;
      int mask = newN - 1;
      double[] newKey = new double[newN + 1];
      int i = this.first;
      int prev = -1;
      int newPrev = -1;
      long[] link = this.link;
      long[] newLink = new long[newN + 1];
      this.first = -1;
      int j = this.size;

      while (j-- != 0) {
         int pos;
         if (Double.doubleToLongBits(key[i]) == 0L) {
            pos = newN;
         } else {
            pos = (int)HashCommon.mix(Double.doubleToRawLongBits(key[i])) & mask;

            while (Double.doubleToLongBits(newKey[pos]) != 0L) {
               pos = pos + 1 & mask;
            }
         }

         newKey[pos] = key[i];
         if (prev != -1) {
            newLink[newPrev] ^= (newLink[newPrev] ^ pos & 4294967295L) & 4294967295L;
            newLink[pos] ^= (newLink[pos] ^ (newPrev & 4294967295L) << 32) & -4294967296L;
            newPrev = pos;
         } else {
            newPrev = this.first = pos;
            newLink[pos] = -1L;
         }

         int t = i;
         i = (int)link[i];
         prev = t;
      }

      this.link = newLink;
      this.last = newPrev;
      if (newPrev != -1) {
         newLink[newPrev] |= 4294967295L;
      }

      this.n = newN;
      this.mask = mask;
      this.maxFill = HashCommon.maxFill(this.n, this.f);
      this.key = newKey;
   }

   public DoubleLinkedOpenHashSet clone() {
      DoubleLinkedOpenHashSet c;
      try {
         c = (DoubleLinkedOpenHashSet)super.clone();
      } catch (CloneNotSupportedException var3) {
         throw new InternalError();
      }

      c.key = (double[])this.key.clone();
      c.containsNull = this.containsNull;
      c.link = (long[])this.link.clone();
      return c;
   }

   @Override
   public int hashCode() {
      int h = 0;
      int j = this.realSize();

      for (int i = 0; j-- != 0; i++) {
         while (Double.doubleToLongBits(this.key[i]) == 0L) {
            i++;
         }

         h += HashCommon.double2int(this.key[i]);
      }

      return h;
   }

   private void writeObject(ObjectOutputStream s) throws IOException {
      DoubleIterator i = this.iterator();
      s.defaultWriteObject();
      int j = this.size;

      while (j-- != 0) {
         s.writeDouble(i.nextDouble());
      }
   }

   private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
      s.defaultReadObject();
      this.n = HashCommon.arraySize(this.size, this.f);
      this.maxFill = HashCommon.maxFill(this.n, this.f);
      this.mask = this.n - 1;
      double[] key = this.key = new double[this.n + 1];
      long[] link = this.link = new long[this.n + 1];
      int prev = -1;
      this.first = this.last = -1;
      int i = this.size;

      while (i-- != 0) {
         double k = s.readDouble();
         int pos;
         if (Double.doubleToLongBits(k) == 0L) {
            pos = this.n;
            this.containsNull = true;
         } else if (Double.doubleToLongBits(key[pos = (int)HashCommon.mix(Double.doubleToRawLongBits(k)) & this.mask]) != 0L) {
            while (Double.doubleToLongBits(key[pos = pos + 1 & this.mask]) != 0L) {
            }
         }

         key[pos] = k;
         if (this.first != -1) {
            link[prev] ^= (link[prev] ^ pos & 4294967295L) & 4294967295L;
            link[pos] ^= (link[pos] ^ (prev & 4294967295L) << 32) & -4294967296L;
            prev = pos;
         } else {
            prev = this.first = pos;
            link[pos] |= -4294967296L;
         }
      }

      this.last = prev;
      if (prev != -1) {
         link[prev] |= 4294967295L;
      }
   }

   private void checkTable() {
   }

   private final class SetIterator implements DoubleListIterator {
      int prev = -1;
      int next = -1;
      int curr = -1;
      int index = -1;

      SetIterator() {
         this.next = DoubleLinkedOpenHashSet.this.first;
         this.index = 0;
      }

      SetIterator(double from) {
         if (Double.doubleToLongBits(from) == 0L) {
            if (DoubleLinkedOpenHashSet.this.containsNull) {
               this.next = (int)DoubleLinkedOpenHashSet.this.link[DoubleLinkedOpenHashSet.this.n];
               this.prev = DoubleLinkedOpenHashSet.this.n;
            } else {
               throw new NoSuchElementException("The key " + from + " does not belong to this set.");
            }
         } else if (Double.doubleToLongBits(DoubleLinkedOpenHashSet.this.key[DoubleLinkedOpenHashSet.this.last]) == Double.doubleToLongBits(from)) {
            this.prev = DoubleLinkedOpenHashSet.this.last;
            this.index = DoubleLinkedOpenHashSet.this.size;
         } else {
            double[] key = DoubleLinkedOpenHashSet.this.key;

            for (int pos = (int)HashCommon.mix(Double.doubleToRawLongBits(from)) & DoubleLinkedOpenHashSet.this.mask;
               Double.doubleToLongBits(key[pos]) != 0L;
               pos = pos + 1 & DoubleLinkedOpenHashSet.this.mask
            ) {
               if (Double.doubleToLongBits(key[pos]) == Double.doubleToLongBits(from)) {
                  this.next = (int)DoubleLinkedOpenHashSet.this.link[pos];
                  this.prev = pos;
                  return;
               }
            }

            throw new NoSuchElementException("The key " + from + " does not belong to this set.");
         }
      }

      @Override
      public boolean hasNext() {
         return this.next != -1;
      }

      @Override
      public boolean hasPrevious() {
         return this.prev != -1;
      }

      @Override
      public double nextDouble() {
         if (!this.hasNext()) {
            throw new NoSuchElementException();
         } else {
            this.curr = this.next;
            this.next = (int)DoubleLinkedOpenHashSet.this.link[this.curr];
            this.prev = this.curr;
            if (this.index >= 0) {
               this.index++;
            }

            return DoubleLinkedOpenHashSet.this.key[this.curr];
         }
      }

      @Override
      public double previousDouble() {
         if (!this.hasPrevious()) {
            throw new NoSuchElementException();
         } else {
            this.curr = this.prev;
            this.prev = (int)(DoubleLinkedOpenHashSet.this.link[this.curr] >>> 32);
            this.next = this.curr;
            if (this.index >= 0) {
               this.index--;
            }

            return DoubleLinkedOpenHashSet.this.key[this.curr];
         }
      }

      @Override
      public void forEachRemaining(java.util.function.DoubleConsumer action) {
         double[] key = DoubleLinkedOpenHashSet.this.key;
         long[] link = DoubleLinkedOpenHashSet.this.link;

         while (this.next != -1) {
            this.curr = this.next;
            this.next = (int)link[this.curr];
            this.prev = this.curr;
            if (this.index >= 0) {
               this.index++;
            }

            action.accept(key[this.curr]);
         }
      }

      private final void ensureIndexKnown() {
         if (this.index < 0) {
            if (this.prev == -1) {
               this.index = 0;
            } else if (this.next == -1) {
               this.index = DoubleLinkedOpenHashSet.this.size;
            } else {
               int pos = DoubleLinkedOpenHashSet.this.first;

               for (this.index = 1; pos != this.prev; this.index++) {
                  pos = (int)DoubleLinkedOpenHashSet.this.link[pos];
               }
            }
         }
      }

      @Override
      public int nextIndex() {
         this.ensureIndexKnown();
         return this.index;
      }

      @Override
      public int previousIndex() {
         this.ensureIndexKnown();
         return this.index - 1;
      }

      @Override
      public void remove() {
         this.ensureIndexKnown();
         if (this.curr == -1) {
            throw new IllegalStateException();
         } else {
            if (this.curr == this.prev) {
               this.index--;
               this.prev = (int)(DoubleLinkedOpenHashSet.this.link[this.curr] >>> 32);
            } else {
               this.next = (int)DoubleLinkedOpenHashSet.this.link[this.curr];
            }

            DoubleLinkedOpenHashSet.this.size--;
            if (this.prev == -1) {
               DoubleLinkedOpenHashSet.this.first = this.next;
            } else {
               DoubleLinkedOpenHashSet.this.link[this.prev] = DoubleLinkedOpenHashSet.this.link[this.prev]
                  ^ (DoubleLinkedOpenHashSet.this.link[this.prev] ^ this.next & 4294967295L) & 4294967295L;
            }

            if (this.next == -1) {
               DoubleLinkedOpenHashSet.this.last = this.prev;
            } else {
               DoubleLinkedOpenHashSet.this.link[this.next] = DoubleLinkedOpenHashSet.this.link[this.next]
                  ^ (DoubleLinkedOpenHashSet.this.link[this.next] ^ (this.prev & 4294967295L) << 32) & -4294967296L;
            }

            int pos = this.curr;
            this.curr = -1;
            if (pos == DoubleLinkedOpenHashSet.this.n) {
               DoubleLinkedOpenHashSet.this.containsNull = false;
               DoubleLinkedOpenHashSet.this.key[DoubleLinkedOpenHashSet.this.n] = 0.0;
            } else {
               double[] key = DoubleLinkedOpenHashSet.this.key;

               label61:
               while (true) {
                  int last = pos;

                  double curr;
                  for (pos = pos + 1 & DoubleLinkedOpenHashSet.this.mask;
                     Double.doubleToLongBits(curr = key[pos]) != 0L;
                     pos = pos + 1 & DoubleLinkedOpenHashSet.this.mask
                  ) {
                     int slot = (int)HashCommon.mix(Double.doubleToRawLongBits(curr)) & DoubleLinkedOpenHashSet.this.mask;
                     if (last <= pos ? last >= slot || slot > pos : last >= slot && slot > pos) {
                        key[last] = curr;
                        if (this.next == pos) {
                           this.next = last;
                        }

                        if (this.prev == pos) {
                           this.prev = last;
                        }

                        DoubleLinkedOpenHashSet.this.fixPointers(pos, last);
                        continue label61;
                     }
                  }

                  key[last] = 0.0;
                  return;
               }
            }
         }
      }
   }
}
