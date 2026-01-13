package it.unimi.dsi.fastutil.chars;

import it.unimi.dsi.fastutil.Hash;
import it.unimi.dsi.fastutil.HashCommon;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class CharOpenCustomHashSet extends AbstractCharSet implements Serializable, Cloneable, Hash {
   private static final long serialVersionUID = 0L;
   private static final boolean ASSERTS = false;
   protected transient char[] key;
   protected transient int mask;
   protected transient boolean containsNull;
   protected CharHash.Strategy strategy;
   protected transient int n;
   protected transient int maxFill;
   protected final transient int minN;
   protected int size;
   protected final float f;

   public CharOpenCustomHashSet(int expected, float f, CharHash.Strategy strategy) {
      this.strategy = strategy;
      if (f <= 0.0F || f >= 1.0F) {
         throw new IllegalArgumentException("Load factor must be greater than 0 and smaller than 1");
      } else if (expected < 0) {
         throw new IllegalArgumentException("The expected number of elements must be nonnegative");
      } else {
         this.f = f;
         this.minN = this.n = HashCommon.arraySize(expected, f);
         this.mask = this.n - 1;
         this.maxFill = HashCommon.maxFill(this.n, f);
         this.key = new char[this.n + 1];
      }
   }

   public CharOpenCustomHashSet(int expected, CharHash.Strategy strategy) {
      this(expected, 0.75F, strategy);
   }

   public CharOpenCustomHashSet(CharHash.Strategy strategy) {
      this(16, 0.75F, strategy);
   }

   public CharOpenCustomHashSet(Collection<? extends Character> c, float f, CharHash.Strategy strategy) {
      this(c.size(), f, strategy);
      this.addAll(c);
   }

   public CharOpenCustomHashSet(Collection<? extends Character> c, CharHash.Strategy strategy) {
      this(c, 0.75F, strategy);
   }

   public CharOpenCustomHashSet(CharCollection c, float f, CharHash.Strategy strategy) {
      this(c.size(), f, strategy);
      this.addAll(c);
   }

   public CharOpenCustomHashSet(CharCollection c, CharHash.Strategy strategy) {
      this(c, 0.75F, strategy);
   }

   public CharOpenCustomHashSet(CharIterator i, float f, CharHash.Strategy strategy) {
      this(16, f, strategy);

      while (i.hasNext()) {
         this.add(i.nextChar());
      }
   }

   public CharOpenCustomHashSet(CharIterator i, CharHash.Strategy strategy) {
      this(i, 0.75F, strategy);
   }

   public CharOpenCustomHashSet(Iterator<?> i, float f, CharHash.Strategy strategy) {
      this(CharIterators.asCharIterator(i), f, strategy);
   }

   public CharOpenCustomHashSet(Iterator<?> i, CharHash.Strategy strategy) {
      this(CharIterators.asCharIterator(i), strategy);
   }

   public CharOpenCustomHashSet(char[] a, int offset, int length, float f, CharHash.Strategy strategy) {
      this(length < 0 ? 0 : length, f, strategy);
      CharArrays.ensureOffsetLength(a, offset, length);

      for (int i = 0; i < length; i++) {
         this.add(a[offset + i]);
      }
   }

   public CharOpenCustomHashSet(char[] a, int offset, int length, CharHash.Strategy strategy) {
      this(a, offset, length, 0.75F, strategy);
   }

   public CharOpenCustomHashSet(char[] a, float f, CharHash.Strategy strategy) {
      this(a, 0, a.length, f, strategy);
   }

   public CharOpenCustomHashSet(char[] a, CharHash.Strategy strategy) {
      this(a, 0.75F, strategy);
   }

   public CharHash.Strategy strategy() {
      return this.strategy;
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
   public boolean addAll(CharCollection c) {
      if (this.f <= 0.5) {
         this.ensureCapacity(c.size());
      } else {
         this.tryCapacity(this.size() + c.size());
      }

      return super.addAll(c);
   }

   @Override
   public boolean addAll(Collection<? extends Character> c) {
      if (this.f <= 0.5) {
         this.ensureCapacity(c.size());
      } else {
         this.tryCapacity(this.size() + c.size());
      }

      return super.addAll(c);
   }

   @Override
   public boolean add(char k) {
      if (this.strategy.equals(k, '\u0000')) {
         if (this.containsNull) {
            return false;
         }

         this.containsNull = true;
         this.key[this.n] = k;
      } else {
         char[] key = this.key;
         int pos;
         char curr;
         if ((curr = key[pos = HashCommon.mix(this.strategy.hashCode(k)) & this.mask]) != 0) {
            if (this.strategy.equals(curr, k)) {
               return false;
            }

            while ((curr = key[pos = pos + 1 & this.mask]) != 0) {
               if (this.strategy.equals(curr, k)) {
                  return false;
               }
            }
         }

         key[pos] = k;
      }

      if (this.size++ >= this.maxFill) {
         this.rehash(HashCommon.arraySize(this.size + 1, this.f));
      }

      return true;
   }

   protected final void shiftKeys(int pos) {
      char[] key = this.key;

      label30:
      while (true) {
         int last = pos;

         char curr;
         for (pos = pos + 1 & this.mask; (curr = key[pos]) != 0; pos = pos + 1 & this.mask) {
            int slot = HashCommon.mix(this.strategy.hashCode(curr)) & this.mask;
            if (last <= pos ? last >= slot || slot > pos : last >= slot && slot > pos) {
               key[last] = curr;
               continue label30;
            }
         }

         key[last] = 0;
         return;
      }
   }

   private boolean removeEntry(int pos) {
      this.size--;
      this.shiftKeys(pos);
      if (this.n > this.minN && this.size < this.maxFill / 4 && this.n > 16) {
         this.rehash(this.n / 2);
      }

      return true;
   }

   private boolean removeNullEntry() {
      this.containsNull = false;
      this.key[this.n] = 0;
      this.size--;
      if (this.n > this.minN && this.size < this.maxFill / 4 && this.n > 16) {
         this.rehash(this.n / 2);
      }

      return true;
   }

   @Override
   public boolean remove(char k) {
      if (this.strategy.equals(k, '\u0000')) {
         return this.containsNull ? this.removeNullEntry() : false;
      } else {
         char[] key = this.key;
         char curr;
         int pos;
         if ((curr = key[pos = HashCommon.mix(this.strategy.hashCode(k)) & this.mask]) == 0) {
            return false;
         } else if (this.strategy.equals(k, curr)) {
            return this.removeEntry(pos);
         } else {
            while ((curr = key[pos = pos + 1 & this.mask]) != 0) {
               if (this.strategy.equals(k, curr)) {
                  return this.removeEntry(pos);
               }
            }

            return false;
         }
      }
   }

   @Override
   public boolean contains(char k) {
      if (this.strategy.equals(k, '\u0000')) {
         return this.containsNull;
      } else {
         char[] key = this.key;
         char curr;
         int pos;
         if ((curr = key[pos = HashCommon.mix(this.strategy.hashCode(k)) & this.mask]) == 0) {
            return false;
         } else if (this.strategy.equals(k, curr)) {
            return true;
         } else {
            while ((curr = key[pos = pos + 1 & this.mask]) != 0) {
               if (this.strategy.equals(k, curr)) {
                  return true;
               }
            }

            return false;
         }
      }
   }

   @Override
   public void clear() {
      if (this.size != 0) {
         this.size = 0;
         this.containsNull = false;
         Arrays.fill(this.key, '\u0000');
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

   @Override
   public CharIterator iterator() {
      return new CharOpenCustomHashSet.SetIterator();
   }

   @Override
   public CharSpliterator spliterator() {
      return new CharOpenCustomHashSet.SetSpliterator();
   }

   @Override
   public void forEach(CharConsumer action) {
      if (this.containsNull) {
         action.accept(this.key[this.n]);
      }

      char[] key = this.key;
      int pos = this.n;

      while (pos-- != 0) {
         if (key[pos] != 0) {
            action.accept(key[pos]);
         }
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
      char[] key = this.key;
      int mask = newN - 1;
      char[] newKey = new char[newN + 1];
      int i = this.n;
      int j = this.realSize();

      while (j-- != 0) {
         while (key[--i] == 0) {
         }

         int pos;
         if (newKey[pos = HashCommon.mix(this.strategy.hashCode(key[i])) & mask] != 0) {
            while (newKey[pos = pos + 1 & mask] != 0) {
            }
         }

         newKey[pos] = key[i];
      }

      this.n = newN;
      this.mask = mask;
      this.maxFill = HashCommon.maxFill(this.n, this.f);
      this.key = newKey;
   }

   public CharOpenCustomHashSet clone() {
      CharOpenCustomHashSet c;
      try {
         c = (CharOpenCustomHashSet)super.clone();
      } catch (CloneNotSupportedException var3) {
         throw new InternalError();
      }

      c.key = (char[])this.key.clone();
      c.containsNull = this.containsNull;
      c.strategy = this.strategy;
      return c;
   }

   @Override
   public int hashCode() {
      int h = 0;
      int j = this.realSize();

      for (int i = 0; j-- != 0; i++) {
         while (this.key[i] == 0) {
            i++;
         }

         h += this.strategy.hashCode(this.key[i]);
      }

      return h;
   }

   private void writeObject(ObjectOutputStream s) throws IOException {
      CharIterator i = this.iterator();
      s.defaultWriteObject();
      int j = this.size;

      while (j-- != 0) {
         s.writeChar(i.nextChar());
      }
   }

   private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
      s.defaultReadObject();
      this.n = HashCommon.arraySize(this.size, this.f);
      this.maxFill = HashCommon.maxFill(this.n, this.f);
      this.mask = this.n - 1;
      char[] key = this.key = new char[this.n + 1];
      int i = this.size;

      while (i-- != 0) {
         char k = s.readChar();
         int pos;
         if (this.strategy.equals(k, '\u0000')) {
            pos = this.n;
            this.containsNull = true;
         } else if (key[pos = HashCommon.mix(this.strategy.hashCode(k)) & this.mask] != 0) {
            while (key[pos = pos + 1 & this.mask] != 0) {
            }
         }

         key[pos] = k;
      }
   }

   private void checkTable() {
   }

   private final class SetIterator implements CharIterator {
      int pos = CharOpenCustomHashSet.this.n;
      int last = -1;
      int c = CharOpenCustomHashSet.this.size;
      boolean mustReturnNull = CharOpenCustomHashSet.this.containsNull;
      CharArrayList wrapped;

      private SetIterator() {
      }

      @Override
      public boolean hasNext() {
         return this.c != 0;
      }

      @Override
      public char nextChar() {
         if (!this.hasNext()) {
            throw new NoSuchElementException();
         } else {
            this.c--;
            if (this.mustReturnNull) {
               this.mustReturnNull = false;
               this.last = CharOpenCustomHashSet.this.n;
               return CharOpenCustomHashSet.this.key[CharOpenCustomHashSet.this.n];
            } else {
               char[] key = CharOpenCustomHashSet.this.key;

               while (--this.pos >= 0) {
                  if (key[this.pos] != 0) {
                     return key[this.last = this.pos];
                  }
               }

               this.last = Integer.MIN_VALUE;
               return this.wrapped.getChar(-this.pos - 1);
            }
         }
      }

      private final void shiftKeys(int pos) {
         char[] key = CharOpenCustomHashSet.this.key;

         label38:
         while (true) {
            int last = pos;

            char curr;
            for (pos = pos + 1 & CharOpenCustomHashSet.this.mask; (curr = key[pos]) != 0; pos = pos + 1 & CharOpenCustomHashSet.this.mask) {
               int slot = HashCommon.mix(CharOpenCustomHashSet.this.strategy.hashCode(curr)) & CharOpenCustomHashSet.this.mask;
               if (last <= pos ? last >= slot || slot > pos : last >= slot && slot > pos) {
                  if (pos < last) {
                     if (this.wrapped == null) {
                        this.wrapped = new CharArrayList(2);
                     }

                     this.wrapped.add(key[pos]);
                  }

                  key[last] = curr;
                  continue label38;
               }
            }

            key[last] = 0;
            return;
         }
      }

      @Override
      public void remove() {
         if (this.last == -1) {
            throw new IllegalStateException();
         } else {
            if (this.last == CharOpenCustomHashSet.this.n) {
               CharOpenCustomHashSet.this.containsNull = false;
               CharOpenCustomHashSet.this.key[CharOpenCustomHashSet.this.n] = 0;
            } else {
               if (this.pos < 0) {
                  CharOpenCustomHashSet.this.remove(this.wrapped.getChar(-this.pos - 1));
                  this.last = -1;
                  return;
               }

               this.shiftKeys(this.last);
            }

            CharOpenCustomHashSet.this.size--;
            this.last = -1;
         }
      }

      @Override
      public void forEachRemaining(CharConsumer action) {
         char[] key = CharOpenCustomHashSet.this.key;
         if (this.mustReturnNull) {
            this.mustReturnNull = false;
            this.last = CharOpenCustomHashSet.this.n;
            action.accept(key[CharOpenCustomHashSet.this.n]);
            this.c--;
         }

         while (this.c != 0) {
            if (--this.pos < 0) {
               this.last = Integer.MIN_VALUE;
               action.accept(this.wrapped.getChar(-this.pos - 1));
               this.c--;
            } else if (key[this.pos] != 0) {
               action.accept(key[this.last = this.pos]);
               this.c--;
            }
         }
      }
   }

   private final class SetSpliterator implements CharSpliterator {
      private static final int POST_SPLIT_CHARACTERISTICS = 257;
      int pos = 0;
      int max;
      int c;
      boolean mustReturnNull;
      boolean hasSplit;

      SetSpliterator() {
         this.max = CharOpenCustomHashSet.this.n;
         this.c = 0;
         this.mustReturnNull = CharOpenCustomHashSet.this.containsNull;
         this.hasSplit = false;
      }

      SetSpliterator(int pos, int max, boolean mustReturnNull, boolean hasSplit) {
         this.max = CharOpenCustomHashSet.this.n;
         this.c = 0;
         this.mustReturnNull = CharOpenCustomHashSet.this.containsNull;
         this.hasSplit = false;
         this.pos = pos;
         this.max = max;
         this.mustReturnNull = mustReturnNull;
         this.hasSplit = hasSplit;
      }

      public boolean tryAdvance(CharConsumer action) {
         if (this.mustReturnNull) {
            this.mustReturnNull = false;
            this.c++;
            action.accept(CharOpenCustomHashSet.this.key[CharOpenCustomHashSet.this.n]);
            return true;
         } else {
            for (char[] key = CharOpenCustomHashSet.this.key; this.pos < this.max; this.pos++) {
               if (key[this.pos] != 0) {
                  this.c++;
                  action.accept(key[this.pos++]);
                  return true;
               }
            }

            return false;
         }
      }

      public void forEachRemaining(CharConsumer action) {
         char[] key = CharOpenCustomHashSet.this.key;
         if (this.mustReturnNull) {
            this.mustReturnNull = false;
            action.accept(key[CharOpenCustomHashSet.this.n]);
            this.c++;
         }

         for (; this.pos < this.max; this.pos++) {
            if (key[this.pos] != 0) {
               action.accept(key[this.pos]);
               this.c++;
            }
         }
      }

      @Override
      public int characteristics() {
         return this.hasSplit ? 257 : 321;
      }

      @Override
      public long estimateSize() {
         return !this.hasSplit
            ? CharOpenCustomHashSet.this.size - this.c
            : Math.min(
               (long)(CharOpenCustomHashSet.this.size - this.c),
               (long)((double)CharOpenCustomHashSet.this.realSize() / CharOpenCustomHashSet.this.n * (this.max - this.pos)) + (this.mustReturnNull ? 1 : 0)
            );
      }

      public CharOpenCustomHashSet.SetSpliterator trySplit() {
         if (this.pos >= this.max - 1) {
            return null;
         } else {
            int retLen = this.max - this.pos >> 1;
            if (retLen <= 1) {
               return null;
            } else {
               int myNewPos = this.pos + retLen;
               int retPos = this.pos;
               CharOpenCustomHashSet.SetSpliterator split = CharOpenCustomHashSet.this.new SetSpliterator(retPos, myNewPos, this.mustReturnNull, true);
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

            char[] key = CharOpenCustomHashSet.this.key;

            while (this.pos < this.max && n > 0L) {
               if (key[this.pos++] != 0) {
                  skipped++;
                  n--;
               }
            }

            return skipped;
         }
      }
   }
}
