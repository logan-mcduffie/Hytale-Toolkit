package it.unimi.dsi.fastutil.ints;

import it.unimi.dsi.fastutil.chars.AbstractCharCollection;
import it.unimi.dsi.fastutil.chars.CharArrays;
import it.unimi.dsi.fastutil.chars.CharCollection;
import it.unimi.dsi.fastutil.chars.CharConsumer;
import it.unimi.dsi.fastutil.chars.CharIterator;
import it.unimi.dsi.fastutil.chars.CharSpliterator;
import it.unimi.dsi.fastutil.chars.CharSpliterators;
import it.unimi.dsi.fastutil.objects.AbstractObjectSet;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectSpliterator;
import it.unimi.dsi.fastutil.objects.ObjectSpliterators;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Map.Entry;
import java.util.function.Consumer;

public class Int2CharArrayMap extends AbstractInt2CharMap implements Serializable, Cloneable {
   private static final long serialVersionUID = 1L;
   protected transient int[] key;
   protected transient char[] value;
   protected int size;
   protected transient Int2CharMap.FastEntrySet entries;
   protected transient IntSet keys;
   protected transient CharCollection values;

   public Int2CharArrayMap(int[] key, char[] value) {
      this.key = key;
      this.value = value;
      this.size = key.length;
      if (key.length != value.length) {
         throw new IllegalArgumentException("Keys and values have different lengths (" + key.length + ", " + value.length + ")");
      }
   }

   public Int2CharArrayMap() {
      this.key = IntArrays.EMPTY_ARRAY;
      this.value = CharArrays.EMPTY_ARRAY;
   }

   public Int2CharArrayMap(int capacity) {
      this.key = new int[capacity];
      this.value = new char[capacity];
   }

   public Int2CharArrayMap(Int2CharMap m) {
      this(m.size());
      int i = 0;

      for (Int2CharMap.Entry e : m.int2CharEntrySet()) {
         this.key[i] = e.getIntKey();
         this.value[i] = e.getCharValue();
         i++;
      }

      this.size = i;
   }

   public Int2CharArrayMap(Map<? extends Integer, ? extends Character> m) {
      this(m.size());
      int i = 0;

      for (Entry<? extends Integer, ? extends Character> e : m.entrySet()) {
         this.key[i] = e.getKey();
         this.value[i] = e.getValue();
         i++;
      }

      this.size = i;
   }

   public Int2CharArrayMap(int[] key, char[] value, int size) {
      this.key = key;
      this.value = value;
      this.size = size;
      if (key.length != value.length) {
         throw new IllegalArgumentException("Keys and values have different lengths (" + key.length + ", " + value.length + ")");
      } else if (size > key.length) {
         throw new IllegalArgumentException("The provided size (" + size + ") is larger than or equal to the backing-arrays size (" + key.length + ")");
      }
   }

   public Int2CharMap.FastEntrySet int2CharEntrySet() {
      if (this.entries == null) {
         this.entries = new Int2CharArrayMap.EntrySet();
      }

      return this.entries;
   }

   private int findKey(int k) {
      int[] key = this.key;
      int i = this.size;

      while (i-- != 0) {
         if (key[i] == k) {
            return i;
         }
      }

      return -1;
   }

   @Override
   public char get(int k) {
      int[] key = this.key;
      int i = this.size;

      while (i-- != 0) {
         if (key[i] == k) {
            return this.value[i];
         }
      }

      return this.defRetValue;
   }

   @Override
   public int size() {
      return this.size;
   }

   @Override
   public void clear() {
      this.size = 0;
   }

   @Override
   public boolean containsKey(int k) {
      return this.findKey(k) != -1;
   }

   @Override
   public boolean containsValue(char v) {
      int i = this.size;

      while (i-- != 0) {
         if (this.value[i] == v) {
            return true;
         }
      }

      return false;
   }

   @Override
   public boolean isEmpty() {
      return this.size == 0;
   }

   @Override
   public char put(int k, char v) {
      int oldKey = this.findKey(k);
      if (oldKey != -1) {
         char oldValue = this.value[oldKey];
         this.value[oldKey] = v;
         return oldValue;
      } else {
         if (this.size == this.key.length) {
            int[] newKey = new int[this.size == 0 ? 2 : this.size * 2];
            char[] newValue = new char[this.size == 0 ? 2 : this.size * 2];

            for (int i = this.size; i-- != 0; newValue[i] = this.value[i]) {
               newKey[i] = this.key[i];
            }

            this.key = newKey;
            this.value = newValue;
         }

         this.key[this.size] = k;
         this.value[this.size] = v;
         this.size++;
         return this.defRetValue;
      }
   }

   @Override
   public char remove(int k) {
      int oldPos = this.findKey(k);
      if (oldPos == -1) {
         return this.defRetValue;
      } else {
         char oldValue = this.value[oldPos];
         int tail = this.size - oldPos - 1;
         System.arraycopy(this.key, oldPos + 1, this.key, oldPos, tail);
         System.arraycopy(this.value, oldPos + 1, this.value, oldPos, tail);
         this.size--;
         return oldValue;
      }
   }

   @Override
   public IntSet keySet() {
      if (this.keys == null) {
         this.keys = new Int2CharArrayMap.KeySet();
      }

      return this.keys;
   }

   @Override
   public CharCollection values() {
      if (this.values == null) {
         this.values = new Int2CharArrayMap.ValuesCollection();
      }

      return this.values;
   }

   public Int2CharArrayMap clone() {
      Int2CharArrayMap c;
      try {
         c = (Int2CharArrayMap)super.clone();
      } catch (CloneNotSupportedException var3) {
         throw new InternalError();
      }

      c.key = (int[])this.key.clone();
      c.value = (char[])this.value.clone();
      c.entries = null;
      c.keys = null;
      c.values = null;
      return c;
   }

   private void writeObject(ObjectOutputStream s) throws IOException {
      s.defaultWriteObject();
      int i = 0;

      for (int max = this.size; i < max; i++) {
         s.writeInt(this.key[i]);
         s.writeChar(this.value[i]);
      }
   }

   private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
      s.defaultReadObject();
      this.key = new int[this.size];
      this.value = new char[this.size];

      for (int i = 0; i < this.size; i++) {
         this.key[i] = s.readInt();
         this.value[i] = s.readChar();
      }
   }

   private final class EntrySet extends AbstractObjectSet<Int2CharMap.Entry> implements Int2CharMap.FastEntrySet {
      private EntrySet() {
      }

      @Override
      public ObjectIterator<Int2CharMap.Entry> iterator() {
         return new ObjectIterator<Int2CharMap.Entry>() {
            int curr = -1;
            int next = 0;

            @Override
            public boolean hasNext() {
               return this.next < Int2CharArrayMap.this.size;
            }

            public Int2CharMap.Entry next() {
               if (!this.hasNext()) {
                  throw new NoSuchElementException();
               } else {
                  return new AbstractInt2CharMap.BasicEntry(Int2CharArrayMap.this.key[this.curr = this.next], Int2CharArrayMap.this.value[this.next++]);
               }
            }

            @Override
            public void remove() {
               if (this.curr == -1) {
                  throw new IllegalStateException();
               } else {
                  this.curr = -1;
                  int tail = Int2CharArrayMap.this.size-- - this.next--;
                  System.arraycopy(Int2CharArrayMap.this.key, this.next + 1, Int2CharArrayMap.this.key, this.next, tail);
                  System.arraycopy(Int2CharArrayMap.this.value, this.next + 1, Int2CharArrayMap.this.value, this.next, tail);
               }
            }

            @Override
            public void forEachRemaining(Consumer<? super Int2CharMap.Entry> action) {
               int max = Int2CharArrayMap.this.size;

               while (this.next < max) {
                  action.accept(new AbstractInt2CharMap.BasicEntry(Int2CharArrayMap.this.key[this.curr = this.next], Int2CharArrayMap.this.value[this.next++]));
               }
            }
         };
      }

      @Override
      public ObjectIterator<Int2CharMap.Entry> fastIterator() {
         return new ObjectIterator<Int2CharMap.Entry>() {
            int next = 0;
            int curr = -1;
            final AbstractInt2CharMap.BasicEntry entry = new AbstractInt2CharMap.BasicEntry();

            @Override
            public boolean hasNext() {
               return this.next < Int2CharArrayMap.this.size;
            }

            public Int2CharMap.Entry next() {
               if (!this.hasNext()) {
                  throw new NoSuchElementException();
               } else {
                  this.entry.key = Int2CharArrayMap.this.key[this.curr = this.next];
                  this.entry.value = Int2CharArrayMap.this.value[this.next++];
                  return this.entry;
               }
            }

            @Override
            public void remove() {
               if (this.curr == -1) {
                  throw new IllegalStateException();
               } else {
                  this.curr = -1;
                  int tail = Int2CharArrayMap.this.size-- - this.next--;
                  System.arraycopy(Int2CharArrayMap.this.key, this.next + 1, Int2CharArrayMap.this.key, this.next, tail);
                  System.arraycopy(Int2CharArrayMap.this.value, this.next + 1, Int2CharArrayMap.this.value, this.next, tail);
               }
            }

            @Override
            public void forEachRemaining(Consumer<? super Int2CharMap.Entry> action) {
               int max = Int2CharArrayMap.this.size;

               while (this.next < max) {
                  this.entry.key = Int2CharArrayMap.this.key[this.curr = this.next];
                  this.entry.value = Int2CharArrayMap.this.value[this.next++];
                  action.accept(this.entry);
               }
            }
         };
      }

      @Override
      public ObjectSpliterator<Int2CharMap.Entry> spliterator() {
         return new Int2CharArrayMap.EntrySet.EntrySetSpliterator(0, Int2CharArrayMap.this.size);
      }

      @Override
      public void forEach(Consumer<? super Int2CharMap.Entry> action) {
         int i = 0;

         for (int max = Int2CharArrayMap.this.size; i < max; i++) {
            action.accept(new AbstractInt2CharMap.BasicEntry(Int2CharArrayMap.this.key[i], Int2CharArrayMap.this.value[i]));
         }
      }

      @Override
      public void fastForEach(Consumer<? super Int2CharMap.Entry> action) {
         AbstractInt2CharMap.BasicEntry entry = new AbstractInt2CharMap.BasicEntry();
         int i = 0;

         for (int max = Int2CharArrayMap.this.size; i < max; i++) {
            entry.key = Int2CharArrayMap.this.key[i];
            entry.value = Int2CharArrayMap.this.value[i];
            action.accept(entry);
         }
      }

      @Override
      public int size() {
         return Int2CharArrayMap.this.size;
      }

      @Override
      public boolean contains(Object o) {
         if (!(o instanceof Entry)) {
            return false;
         } else {
            Entry<?, ?> e = (Entry<?, ?>)o;
            if (e.getKey() == null || !(e.getKey() instanceof Integer)) {
               return false;
            } else if (e.getValue() != null && e.getValue() instanceof Character) {
               int k = (Integer)e.getKey();
               return Int2CharArrayMap.this.containsKey(k) && Int2CharArrayMap.this.get(k) == (Character)e.getValue();
            } else {
               return false;
            }
         }
      }

      @Override
      public boolean remove(Object o) {
         if (!(o instanceof Entry)) {
            return false;
         } else {
            Entry<?, ?> e = (Entry<?, ?>)o;
            if (e.getKey() == null || !(e.getKey() instanceof Integer)) {
               return false;
            } else if (e.getValue() != null && e.getValue() instanceof Character) {
               int k = (Integer)e.getKey();
               char v = (Character)e.getValue();
               int oldPos = Int2CharArrayMap.this.findKey(k);
               if (oldPos != -1 && v == Int2CharArrayMap.this.value[oldPos]) {
                  int tail = Int2CharArrayMap.this.size - oldPos - 1;
                  System.arraycopy(Int2CharArrayMap.this.key, oldPos + 1, Int2CharArrayMap.this.key, oldPos, tail);
                  System.arraycopy(Int2CharArrayMap.this.value, oldPos + 1, Int2CharArrayMap.this.value, oldPos, tail);
                  Int2CharArrayMap.this.size--;
                  return true;
               } else {
                  return false;
               }
            } else {
               return false;
            }
         }
      }

      final class EntrySetSpliterator
         extends ObjectSpliterators.EarlyBindingSizeIndexBasedSpliterator<Int2CharMap.Entry>
         implements ObjectSpliterator<Int2CharMap.Entry> {
         EntrySetSpliterator(int pos, int maxPos) {
            super(pos, maxPos);
         }

         @Override
         public int characteristics() {
            return 16465;
         }

         protected final Int2CharMap.Entry get(int location) {
            return new AbstractInt2CharMap.BasicEntry(Int2CharArrayMap.this.key[location], Int2CharArrayMap.this.value[location]);
         }

         protected final Int2CharArrayMap.EntrySet.EntrySetSpliterator makeForSplit(int pos, int maxPos) {
            return EntrySet.this.new EntrySetSpliterator(pos, maxPos);
         }
      }
   }

   private final class KeySet extends AbstractIntSet {
      private KeySet() {
      }

      @Override
      public boolean contains(int k) {
         return Int2CharArrayMap.this.findKey(k) != -1;
      }

      @Override
      public boolean remove(int k) {
         int oldPos = Int2CharArrayMap.this.findKey(k);
         if (oldPos == -1) {
            return false;
         } else {
            int tail = Int2CharArrayMap.this.size - oldPos - 1;
            System.arraycopy(Int2CharArrayMap.this.key, oldPos + 1, Int2CharArrayMap.this.key, oldPos, tail);
            System.arraycopy(Int2CharArrayMap.this.value, oldPos + 1, Int2CharArrayMap.this.value, oldPos, tail);
            Int2CharArrayMap.this.size--;
            return true;
         }
      }

      @Override
      public IntIterator iterator() {
         return new IntIterator() {
            int pos = 0;

            @Override
            public boolean hasNext() {
               return this.pos < Int2CharArrayMap.this.size;
            }

            @Override
            public int nextInt() {
               if (!this.hasNext()) {
                  throw new NoSuchElementException();
               } else {
                  return Int2CharArrayMap.this.key[this.pos++];
               }
            }

            @Override
            public void remove() {
               if (this.pos == 0) {
                  throw new IllegalStateException();
               } else {
                  int tail = Int2CharArrayMap.this.size - this.pos;
                  System.arraycopy(Int2CharArrayMap.this.key, this.pos, Int2CharArrayMap.this.key, this.pos - 1, tail);
                  System.arraycopy(Int2CharArrayMap.this.value, this.pos, Int2CharArrayMap.this.value, this.pos - 1, tail);
                  Int2CharArrayMap.this.size--;
                  this.pos--;
               }
            }

            @Override
            public void forEachRemaining(java.util.function.IntConsumer action) {
               int max = Int2CharArrayMap.this.size;

               while (this.pos < max) {
                  action.accept(Int2CharArrayMap.this.key[this.pos++]);
               }
            }
         };
      }

      @Override
      public IntSpliterator spliterator() {
         return new Int2CharArrayMap.KeySet.KeySetSpliterator(0, Int2CharArrayMap.this.size);
      }

      @Override
      public void forEach(java.util.function.IntConsumer action) {
         int i = 0;

         for (int max = Int2CharArrayMap.this.size; i < max; i++) {
            action.accept(Int2CharArrayMap.this.key[i]);
         }
      }

      @Override
      public int size() {
         return Int2CharArrayMap.this.size;
      }

      @Override
      public void clear() {
         Int2CharArrayMap.this.clear();
      }

      final class KeySetSpliterator extends IntSpliterators.EarlyBindingSizeIndexBasedSpliterator implements IntSpliterator {
         KeySetSpliterator(int pos, int maxPos) {
            super(pos, maxPos);
         }

         @Override
         public int characteristics() {
            return 16721;
         }

         @Override
         protected final int get(int location) {
            return Int2CharArrayMap.this.key[location];
         }

         protected final Int2CharArrayMap.KeySet.KeySetSpliterator makeForSplit(int pos, int maxPos) {
            return KeySet.this.new KeySetSpliterator(pos, maxPos);
         }

         @Override
         public void forEachRemaining(java.util.function.IntConsumer action) {
            int max = Int2CharArrayMap.this.size;

            while (this.pos < max) {
               action.accept(Int2CharArrayMap.this.key[this.pos++]);
            }
         }
      }
   }

   private final class ValuesCollection extends AbstractCharCollection {
      private ValuesCollection() {
      }

      @Override
      public boolean contains(char v) {
         return Int2CharArrayMap.this.containsValue(v);
      }

      @Override
      public CharIterator iterator() {
         return new CharIterator() {
            int pos = 0;

            @Override
            public boolean hasNext() {
               return this.pos < Int2CharArrayMap.this.size;
            }

            @Override
            public char nextChar() {
               if (!this.hasNext()) {
                  throw new NoSuchElementException();
               } else {
                  return Int2CharArrayMap.this.value[this.pos++];
               }
            }

            @Override
            public void remove() {
               if (this.pos == 0) {
                  throw new IllegalStateException();
               } else {
                  int tail = Int2CharArrayMap.this.size - this.pos;
                  System.arraycopy(Int2CharArrayMap.this.key, this.pos, Int2CharArrayMap.this.key, this.pos - 1, tail);
                  System.arraycopy(Int2CharArrayMap.this.value, this.pos, Int2CharArrayMap.this.value, this.pos - 1, tail);
                  Int2CharArrayMap.this.size--;
                  this.pos--;
               }
            }

            @Override
            public void forEachRemaining(CharConsumer action) {
               int max = Int2CharArrayMap.this.size;

               while (this.pos < max) {
                  action.accept(Int2CharArrayMap.this.value[this.pos++]);
               }
            }
         };
      }

      @Override
      public CharSpliterator spliterator() {
         return new Int2CharArrayMap.ValuesCollection.ValuesSpliterator(0, Int2CharArrayMap.this.size);
      }

      @Override
      public void forEach(CharConsumer action) {
         int i = 0;

         for (int max = Int2CharArrayMap.this.size; i < max; i++) {
            action.accept(Int2CharArrayMap.this.value[i]);
         }
      }

      @Override
      public int size() {
         return Int2CharArrayMap.this.size;
      }

      @Override
      public void clear() {
         Int2CharArrayMap.this.clear();
      }

      final class ValuesSpliterator extends CharSpliterators.EarlyBindingSizeIndexBasedSpliterator implements CharSpliterator {
         ValuesSpliterator(int pos, int maxPos) {
            super(pos, maxPos);
         }

         @Override
         public int characteristics() {
            return 16720;
         }

         @Override
         protected final char get(int location) {
            return Int2CharArrayMap.this.value[location];
         }

         protected final Int2CharArrayMap.ValuesCollection.ValuesSpliterator makeForSplit(int pos, int maxPos) {
            return ValuesCollection.this.new ValuesSpliterator(pos, maxPos);
         }

         @Override
         public void forEachRemaining(CharConsumer action) {
            int max = Int2CharArrayMap.this.size;

            while (this.pos < max) {
               action.accept(Int2CharArrayMap.this.value[this.pos++]);
            }
         }
      }
   }
}
