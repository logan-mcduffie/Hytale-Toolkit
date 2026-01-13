package it.unimi.dsi.fastutil.floats;

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

public class FloatLinkedOpenHashSet extends AbstractFloatSortedSet implements Serializable, Cloneable, Hash {
   private static final long serialVersionUID = 0L;
   private static final boolean ASSERTS = false;
   protected transient float[] key;
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

   public FloatLinkedOpenHashSet(int expected, float f) {
      if (f <= 0.0F || f >= 1.0F) {
         throw new IllegalArgumentException("Load factor must be greater than 0 and smaller than 1");
      } else if (expected < 0) {
         throw new IllegalArgumentException("The expected number of elements must be nonnegative");
      } else {
         this.f = f;
         this.minN = this.n = HashCommon.arraySize(expected, f);
         this.mask = this.n - 1;
         this.maxFill = HashCommon.maxFill(this.n, f);
         this.key = new float[this.n + 1];
         this.link = new long[this.n + 1];
      }
   }

   public FloatLinkedOpenHashSet(int expected) {
      this(expected, 0.75F);
   }

   public FloatLinkedOpenHashSet() {
      this(16, 0.75F);
   }

   public FloatLinkedOpenHashSet(Collection<? extends Float> c, float f) {
      this(c.size(), f);
      this.addAll(c);
   }

   public FloatLinkedOpenHashSet(Collection<? extends Float> c) {
      this(c, 0.75F);
   }

   public FloatLinkedOpenHashSet(FloatCollection c, float f) {
      this(c.size(), f);
      this.addAll(c);
   }

   public FloatLinkedOpenHashSet(FloatCollection c) {
      this(c, 0.75F);
   }

   public FloatLinkedOpenHashSet(FloatIterator i, float f) {
      this(16, f);

      while (i.hasNext()) {
         this.add(i.nextFloat());
      }
   }

   public FloatLinkedOpenHashSet(FloatIterator i) {
      this(i, 0.75F);
   }

   public FloatLinkedOpenHashSet(Iterator<?> i, float f) {
      this(FloatIterators.asFloatIterator(i), f);
   }

   public FloatLinkedOpenHashSet(Iterator<?> i) {
      this(FloatIterators.asFloatIterator(i));
   }

   public FloatLinkedOpenHashSet(float[] a, int offset, int length, float f) {
      this(length < 0 ? 0 : length, f);
      FloatArrays.ensureOffsetLength(a, offset, length);

      for (int i = 0; i < length; i++) {
         this.add(a[offset + i]);
      }
   }

   public FloatLinkedOpenHashSet(float[] a, int offset, int length) {
      this(a, offset, length, 0.75F);
   }

   public FloatLinkedOpenHashSet(float[] a, float f) {
      this(a, 0, a.length, f);
   }

   public FloatLinkedOpenHashSet(float[] a) {
      this(a, 0.75F);
   }

   public static FloatLinkedOpenHashSet of() {
      return new FloatLinkedOpenHashSet();
   }

   public static FloatLinkedOpenHashSet of(float e) {
      FloatLinkedOpenHashSet result = new FloatLinkedOpenHashSet(1, 0.75F);
      result.add(e);
      return result;
   }

   public static FloatLinkedOpenHashSet of(float e0, float e1) {
      FloatLinkedOpenHashSet result = new FloatLinkedOpenHashSet(2, 0.75F);
      result.add(e0);
      if (!result.add(e1)) {
         throw new IllegalArgumentException("Duplicate element: " + e1);
      } else {
         return result;
      }
   }

   public static FloatLinkedOpenHashSet of(float e0, float e1, float e2) {
      FloatLinkedOpenHashSet result = new FloatLinkedOpenHashSet(3, 0.75F);
      result.add(e0);
      if (!result.add(e1)) {
         throw new IllegalArgumentException("Duplicate element: " + e1);
      } else if (!result.add(e2)) {
         throw new IllegalArgumentException("Duplicate element: " + e2);
      } else {
         return result;
      }
   }

   public static FloatLinkedOpenHashSet of(float... a) {
      FloatLinkedOpenHashSet result = new FloatLinkedOpenHashSet(a.length, 0.75F);

      for (float element : a) {
         if (!result.add(element)) {
            throw new IllegalArgumentException("Duplicate element " + element);
         }
      }

      return result;
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
   public boolean addAll(FloatCollection c) {
      if (this.f <= 0.5) {
         this.ensureCapacity(c.size());
      } else {
         this.tryCapacity(this.size() + c.size());
      }

      return super.addAll(c);
   }

   @Override
   public boolean addAll(Collection<? extends Float> c) {
      if (this.f <= 0.5) {
         this.ensureCapacity(c.size());
      } else {
         this.tryCapacity(this.size() + c.size());
      }

      return super.addAll(c);
   }

   @Override
   public boolean add(float k) {
      int pos;
      if (Float.floatToIntBits(k) == 0) {
         if (this.containsNull) {
            return false;
         }

         pos = this.n;
         this.containsNull = true;
      } else {
         float[] key = this.key;
         float curr;
         if (Float.floatToIntBits(curr = key[pos = HashCommon.mix(HashCommon.float2int(k)) & this.mask]) != 0) {
            if (Float.floatToIntBits(curr) == Float.floatToIntBits(k)) {
               return false;
            }

            while (Float.floatToIntBits(curr = key[pos = pos + 1 & this.mask]) != 0) {
               if (Float.floatToIntBits(curr) == Float.floatToIntBits(k)) {
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
      float[] key = this.key;

      label30:
      while (true) {
         int last = pos;

         float curr;
         for (pos = pos + 1 & this.mask; Float.floatToIntBits(curr = key[pos]) != 0; pos = pos + 1 & this.mask) {
            int slot = HashCommon.mix(HashCommon.float2int(curr)) & this.mask;
            if (last <= pos ? last >= slot || slot > pos : last >= slot && slot > pos) {
               key[last] = curr;
               this.fixPointers(pos, last);
               continue label30;
            }
         }

         key[last] = 0.0F;
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
      this.key[this.n] = 0.0F;
      this.size--;
      this.fixPointers(this.n);
      if (this.n > this.minN && this.size < this.maxFill / 4 && this.n > 16) {
         this.rehash(this.n / 2);
      }

      return true;
   }

   @Override
   public boolean remove(float k) {
      if (Float.floatToIntBits(k) == 0) {
         return this.containsNull ? this.removeNullEntry() : false;
      } else {
         float[] key = this.key;
         float curr;
         int pos;
         if (Float.floatToIntBits(curr = key[pos = HashCommon.mix(HashCommon.float2int(k)) & this.mask]) == 0) {
            return false;
         } else if (Float.floatToIntBits(k) == Float.floatToIntBits(curr)) {
            return this.removeEntry(pos);
         } else {
            while (Float.floatToIntBits(curr = key[pos = pos + 1 & this.mask]) != 0) {
               if (Float.floatToIntBits(k) == Float.floatToIntBits(curr)) {
                  return this.removeEntry(pos);
               }
            }

            return false;
         }
      }
   }

   @Override
   public boolean contains(float k) {
      if (Float.floatToIntBits(k) == 0) {
         return this.containsNull;
      } else {
         float[] key = this.key;
         float curr;
         int pos;
         if (Float.floatToIntBits(curr = key[pos = HashCommon.mix(HashCommon.float2int(k)) & this.mask]) == 0) {
            return false;
         } else if (Float.floatToIntBits(k) == Float.floatToIntBits(curr)) {
            return true;
         } else {
            while (Float.floatToIntBits(curr = key[pos = pos + 1 & this.mask]) != 0) {
               if (Float.floatToIntBits(k) == Float.floatToIntBits(curr)) {
                  return true;
               }
            }

            return false;
         }
      }
   }

   public float removeFirstFloat() {
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

         float k = this.key[pos];
         this.size--;
         if (Float.floatToIntBits(k) == 0) {
            this.containsNull = false;
            this.key[this.n] = 0.0F;
         } else {
            this.shiftKeys(pos);
         }

         if (this.n > this.minN && this.size < this.maxFill / 4 && this.n > 16) {
            this.rehash(this.n / 2);
         }

         return k;
      }
   }

   public float removeLastFloat() {
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

         float k = this.key[pos];
         this.size--;
         if (Float.floatToIntBits(k) == 0) {
            this.containsNull = false;
            this.key[this.n] = 0.0F;
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

   public boolean addAndMoveToFirst(float k) {
      int pos;
      if (Float.floatToIntBits(k) == 0) {
         if (this.containsNull) {
            this.moveIndexToFirst(this.n);
            return false;
         }

         this.containsNull = true;
         pos = this.n;
      } else {
         float[] key = this.key;

         for (pos = HashCommon.mix(HashCommon.float2int(k)) & this.mask; Float.floatToIntBits(key[pos]) != 0; pos = pos + 1 & this.mask) {
            if (Float.floatToIntBits(k) == Float.floatToIntBits(key[pos])) {
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

   public boolean addAndMoveToLast(float k) {
      int pos;
      if (Float.floatToIntBits(k) == 0) {
         if (this.containsNull) {
            this.moveIndexToLast(this.n);
            return false;
         }

         this.containsNull = true;
         pos = this.n;
      } else {
         float[] key = this.key;

         for (pos = HashCommon.mix(HashCommon.float2int(k)) & this.mask; Float.floatToIntBits(key[pos]) != 0; pos = pos + 1 & this.mask) {
            if (Float.floatToIntBits(k) == Float.floatToIntBits(key[pos])) {
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
         Arrays.fill(this.key, 0.0F);
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
   public float firstFloat() {
      if (this.size == 0) {
         throw new NoSuchElementException();
      } else {
         return this.key[this.first];
      }
   }

   @Override
   public float lastFloat() {
      if (this.size == 0) {
         throw new NoSuchElementException();
      } else {
         return this.key[this.last];
      }
   }

   @Override
   public FloatSortedSet tailSet(float from) {
      throw new UnsupportedOperationException();
   }

   @Override
   public FloatSortedSet headSet(float to) {
      throw new UnsupportedOperationException();
   }

   @Override
   public FloatSortedSet subSet(float from, float to) {
      throw new UnsupportedOperationException();
   }

   @Override
   public FloatComparator comparator() {
      return null;
   }

   public FloatListIterator iterator(float from) {
      return new FloatLinkedOpenHashSet.SetIterator(from);
   }

   public FloatListIterator iterator() {
      return new FloatLinkedOpenHashSet.SetIterator();
   }

   @Override
   public FloatSpliterator spliterator() {
      return FloatSpliterators.asSpliterator(this.iterator(), Size64.sizeOf(this), 337);
   }

   @Override
   public void forEach(FloatConsumer action) {
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
      float[] key = this.key;
      int mask = newN - 1;
      float[] newKey = new float[newN + 1];
      int i = this.first;
      int prev = -1;
      int newPrev = -1;
      long[] link = this.link;
      long[] newLink = new long[newN + 1];
      this.first = -1;
      int j = this.size;

      while (j-- != 0) {
         int pos;
         if (Float.floatToIntBits(key[i]) == 0) {
            pos = newN;
         } else {
            pos = HashCommon.mix(HashCommon.float2int(key[i])) & mask;

            while (Float.floatToIntBits(newKey[pos]) != 0) {
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

   public FloatLinkedOpenHashSet clone() {
      FloatLinkedOpenHashSet c;
      try {
         c = (FloatLinkedOpenHashSet)super.clone();
      } catch (CloneNotSupportedException var3) {
         throw new InternalError();
      }

      c.key = (float[])this.key.clone();
      c.containsNull = this.containsNull;
      c.link = (long[])this.link.clone();
      return c;
   }

   @Override
   public int hashCode() {
      int h = 0;
      int j = this.realSize();

      for (int i = 0; j-- != 0; i++) {
         while (Float.floatToIntBits(this.key[i]) == 0) {
            i++;
         }

         h += HashCommon.float2int(this.key[i]);
      }

      return h;
   }

   private void writeObject(ObjectOutputStream s) throws IOException {
      FloatIterator i = this.iterator();
      s.defaultWriteObject();
      int j = this.size;

      while (j-- != 0) {
         s.writeFloat(i.nextFloat());
      }
   }

   private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
      s.defaultReadObject();
      this.n = HashCommon.arraySize(this.size, this.f);
      this.maxFill = HashCommon.maxFill(this.n, this.f);
      this.mask = this.n - 1;
      float[] key = this.key = new float[this.n + 1];
      long[] link = this.link = new long[this.n + 1];
      int prev = -1;
      this.first = this.last = -1;
      int i = this.size;

      while (i-- != 0) {
         float k = s.readFloat();
         int pos;
         if (Float.floatToIntBits(k) == 0) {
            pos = this.n;
            this.containsNull = true;
         } else if (Float.floatToIntBits(key[pos = HashCommon.mix(HashCommon.float2int(k)) & this.mask]) != 0) {
            while (Float.floatToIntBits(key[pos = pos + 1 & this.mask]) != 0) {
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

   private final class SetIterator implements FloatListIterator {
      int prev = -1;
      int next = -1;
      int curr = -1;
      int index = -1;

      SetIterator() {
         this.next = FloatLinkedOpenHashSet.this.first;
         this.index = 0;
      }

      SetIterator(float from) {
         if (Float.floatToIntBits(from) == 0) {
            if (FloatLinkedOpenHashSet.this.containsNull) {
               this.next = (int)FloatLinkedOpenHashSet.this.link[FloatLinkedOpenHashSet.this.n];
               this.prev = FloatLinkedOpenHashSet.this.n;
            } else {
               throw new NoSuchElementException("The key " + from + " does not belong to this set.");
            }
         } else if (Float.floatToIntBits(FloatLinkedOpenHashSet.this.key[FloatLinkedOpenHashSet.this.last]) == Float.floatToIntBits(from)) {
            this.prev = FloatLinkedOpenHashSet.this.last;
            this.index = FloatLinkedOpenHashSet.this.size;
         } else {
            float[] key = FloatLinkedOpenHashSet.this.key;

            for (int pos = HashCommon.mix(HashCommon.float2int(from)) & FloatLinkedOpenHashSet.this.mask;
               Float.floatToIntBits(key[pos]) != 0;
               pos = pos + 1 & FloatLinkedOpenHashSet.this.mask
            ) {
               if (Float.floatToIntBits(key[pos]) == Float.floatToIntBits(from)) {
                  this.next = (int)FloatLinkedOpenHashSet.this.link[pos];
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
      public float nextFloat() {
         if (!this.hasNext()) {
            throw new NoSuchElementException();
         } else {
            this.curr = this.next;
            this.next = (int)FloatLinkedOpenHashSet.this.link[this.curr];
            this.prev = this.curr;
            if (this.index >= 0) {
               this.index++;
            }

            return FloatLinkedOpenHashSet.this.key[this.curr];
         }
      }

      @Override
      public float previousFloat() {
         if (!this.hasPrevious()) {
            throw new NoSuchElementException();
         } else {
            this.curr = this.prev;
            this.prev = (int)(FloatLinkedOpenHashSet.this.link[this.curr] >>> 32);
            this.next = this.curr;
            if (this.index >= 0) {
               this.index--;
            }

            return FloatLinkedOpenHashSet.this.key[this.curr];
         }
      }

      @Override
      public void forEachRemaining(FloatConsumer action) {
         float[] key = FloatLinkedOpenHashSet.this.key;
         long[] link = FloatLinkedOpenHashSet.this.link;

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
               this.index = FloatLinkedOpenHashSet.this.size;
            } else {
               int pos = FloatLinkedOpenHashSet.this.first;

               for (this.index = 1; pos != this.prev; this.index++) {
                  pos = (int)FloatLinkedOpenHashSet.this.link[pos];
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
               this.prev = (int)(FloatLinkedOpenHashSet.this.link[this.curr] >>> 32);
            } else {
               this.next = (int)FloatLinkedOpenHashSet.this.link[this.curr];
            }

            FloatLinkedOpenHashSet.this.size--;
            if (this.prev == -1) {
               FloatLinkedOpenHashSet.this.first = this.next;
            } else {
               FloatLinkedOpenHashSet.this.link[this.prev] = FloatLinkedOpenHashSet.this.link[this.prev]
                  ^ (FloatLinkedOpenHashSet.this.link[this.prev] ^ this.next & 4294967295L) & 4294967295L;
            }

            if (this.next == -1) {
               FloatLinkedOpenHashSet.this.last = this.prev;
            } else {
               FloatLinkedOpenHashSet.this.link[this.next] = FloatLinkedOpenHashSet.this.link[this.next]
                  ^ (FloatLinkedOpenHashSet.this.link[this.next] ^ (this.prev & 4294967295L) << 32) & -4294967296L;
            }

            int pos = this.curr;
            this.curr = -1;
            if (pos == FloatLinkedOpenHashSet.this.n) {
               FloatLinkedOpenHashSet.this.containsNull = false;
               FloatLinkedOpenHashSet.this.key[FloatLinkedOpenHashSet.this.n] = 0.0F;
            } else {
               float[] key = FloatLinkedOpenHashSet.this.key;

               label61:
               while (true) {
                  int last = pos;

                  float curr;
                  for (pos = pos + 1 & FloatLinkedOpenHashSet.this.mask;
                     Float.floatToIntBits(curr = key[pos]) != 0;
                     pos = pos + 1 & FloatLinkedOpenHashSet.this.mask
                  ) {
                     int slot = HashCommon.mix(HashCommon.float2int(curr)) & FloatLinkedOpenHashSet.this.mask;
                     if (last <= pos ? last >= slot || slot > pos : last >= slot && slot > pos) {
                        key[last] = curr;
                        if (this.next == pos) {
                           this.next = last;
                        }

                        if (this.prev == pos) {
                           this.prev = last;
                        }

                        FloatLinkedOpenHashSet.this.fixPointers(pos, last);
                        continue label61;
                     }
                  }

                  key[last] = 0.0F;
                  return;
               }
            }
         }
      }
   }
}
