package it.unimi.dsi.fastutil.chars;

import it.unimi.dsi.fastutil.objects.AbstractObjectSet;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectSpliterator;
import it.unimi.dsi.fastutil.objects.ObjectSpliterators;
import it.unimi.dsi.fastutil.shorts.AbstractShortCollection;
import it.unimi.dsi.fastutil.shorts.ShortArrays;
import it.unimi.dsi.fastutil.shorts.ShortCollection;
import it.unimi.dsi.fastutil.shorts.ShortConsumer;
import it.unimi.dsi.fastutil.shorts.ShortIterator;
import it.unimi.dsi.fastutil.shorts.ShortSpliterator;
import it.unimi.dsi.fastutil.shorts.ShortSpliterators;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Map.Entry;
import java.util.function.Consumer;

public class Char2ShortArrayMap extends AbstractChar2ShortMap implements Serializable, Cloneable {
   private static final long serialVersionUID = 1L;
   protected transient char[] key;
   protected transient short[] value;
   protected int size;
   protected transient Char2ShortMap.FastEntrySet entries;
   protected transient CharSet keys;
   protected transient ShortCollection values;

   public Char2ShortArrayMap(char[] key, short[] value) {
      this.key = key;
      this.value = value;
      this.size = key.length;
      if (key.length != value.length) {
         throw new IllegalArgumentException("Keys and values have different lengths (" + key.length + ", " + value.length + ")");
      }
   }

   public Char2ShortArrayMap() {
      this.key = CharArrays.EMPTY_ARRAY;
      this.value = ShortArrays.EMPTY_ARRAY;
   }

   public Char2ShortArrayMap(int capacity) {
      this.key = new char[capacity];
      this.value = new short[capacity];
   }

   public Char2ShortArrayMap(Char2ShortMap m) {
      this(m.size());
      int i = 0;

      for (Char2ShortMap.Entry e : m.char2ShortEntrySet()) {
         this.key[i] = e.getCharKey();
         this.value[i] = e.getShortValue();
         i++;
      }

      this.size = i;
   }

   public Char2ShortArrayMap(Map<? extends Character, ? extends Short> m) {
      this(m.size());
      int i = 0;

      for (Entry<? extends Character, ? extends Short> e : m.entrySet()) {
         this.key[i] = e.getKey();
         this.value[i] = e.getValue();
         i++;
      }

      this.size = i;
   }

   public Char2ShortArrayMap(char[] key, short[] value, int size) {
      this.key = key;
      this.value = value;
      this.size = size;
      if (key.length != value.length) {
         throw new IllegalArgumentException("Keys and values have different lengths (" + key.length + ", " + value.length + ")");
      } else if (size > key.length) {
         throw new IllegalArgumentException("The provided size (" + size + ") is larger than or equal to the backing-arrays size (" + key.length + ")");
      }
   }

   public Char2ShortMap.FastEntrySet char2ShortEntrySet() {
      if (this.entries == null) {
         this.entries = new Char2ShortArrayMap.EntrySet();
      }

      return this.entries;
   }

   private int findKey(char k) {
      char[] key = this.key;
      int i = this.size;

      while (i-- != 0) {
         if (key[i] == k) {
            return i;
         }
      }

      return -1;
   }

   @Override
   public short get(char k) {
      char[] key = this.key;
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
   public boolean containsKey(char k) {
      return this.findKey(k) != -1;
   }

   @Override
   public boolean containsValue(short v) {
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
   public short put(char k, short v) {
      int oldKey = this.findKey(k);
      if (oldKey != -1) {
         short oldValue = this.value[oldKey];
         this.value[oldKey] = v;
         return oldValue;
      } else {
         if (this.size == this.key.length) {
            char[] newKey = new char[this.size == 0 ? 2 : this.size * 2];
            short[] newValue = new short[this.size == 0 ? 2 : this.size * 2];

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
   public short remove(char k) {
      int oldPos = this.findKey(k);
      if (oldPos == -1) {
         return this.defRetValue;
      } else {
         short oldValue = this.value[oldPos];
         int tail = this.size - oldPos - 1;
         System.arraycopy(this.key, oldPos + 1, this.key, oldPos, tail);
         System.arraycopy(this.value, oldPos + 1, this.value, oldPos, tail);
         this.size--;
         return oldValue;
      }
   }

   @Override
   public CharSet keySet() {
      if (this.keys == null) {
         this.keys = new Char2ShortArrayMap.KeySet();
      }

      return this.keys;
   }

   @Override
   public ShortCollection values() {
      if (this.values == null) {
         this.values = new Char2ShortArrayMap.ValuesCollection();
      }

      return this.values;
   }

   public Char2ShortArrayMap clone() {
      Char2ShortArrayMap c;
      try {
         c = (Char2ShortArrayMap)super.clone();
      } catch (CloneNotSupportedException var3) {
         throw new InternalError();
      }

      c.key = (char[])this.key.clone();
      c.value = (short[])this.value.clone();
      c.entries = null;
      c.keys = null;
      c.values = null;
      return c;
   }

   private void writeObject(ObjectOutputStream s) throws IOException {
      s.defaultWriteObject();
      int i = 0;

      for (int max = this.size; i < max; i++) {
         s.writeChar(this.key[i]);
         s.writeShort(this.value[i]);
      }
   }

   private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
      s.defaultReadObject();
      this.key = new char[this.size];
      this.value = new short[this.size];

      for (int i = 0; i < this.size; i++) {
         this.key[i] = s.readChar();
         this.value[i] = s.readShort();
      }
   }

   private final class EntrySet extends AbstractObjectSet<Char2ShortMap.Entry> implements Char2ShortMap.FastEntrySet {
      private EntrySet() {
      }

      @Override
      public ObjectIterator<Char2ShortMap.Entry> iterator() {
         return new ObjectIterator<Char2ShortMap.Entry>() {
            int curr = -1;
            int next = 0;

            @Override
            public boolean hasNext() {
               return this.next < Char2ShortArrayMap.this.size;
            }

            public Char2ShortMap.Entry next() {
               if (!this.hasNext()) {
                  throw new NoSuchElementException();
               } else {
                  return new AbstractChar2ShortMap.BasicEntry(Char2ShortArrayMap.this.key[this.curr = this.next], Char2ShortArrayMap.this.value[this.next++]);
               }
            }

            @Override
            public void remove() {
               if (this.curr == -1) {
                  throw new IllegalStateException();
               } else {
                  this.curr = -1;
                  int tail = Char2ShortArrayMap.this.size-- - this.next--;
                  System.arraycopy(Char2ShortArrayMap.this.key, this.next + 1, Char2ShortArrayMap.this.key, this.next, tail);
                  System.arraycopy(Char2ShortArrayMap.this.value, this.next + 1, Char2ShortArrayMap.this.value, this.next, tail);
               }
            }

            @Override
            public void forEachRemaining(Consumer<? super Char2ShortMap.Entry> action) {
               int max = Char2ShortArrayMap.this.size;

               while (this.next < max) {
                  action.accept(
                     new AbstractChar2ShortMap.BasicEntry(Char2ShortArrayMap.this.key[this.curr = this.next], Char2ShortArrayMap.this.value[this.next++])
                  );
               }
            }
         };
      }

      @Override
      public ObjectIterator<Char2ShortMap.Entry> fastIterator() {
         return new ObjectIterator<Char2ShortMap.Entry>() {
            int next = 0;
            int curr = -1;
            final AbstractChar2ShortMap.BasicEntry entry = new AbstractChar2ShortMap.BasicEntry();

            @Override
            public boolean hasNext() {
               return this.next < Char2ShortArrayMap.this.size;
            }

            public Char2ShortMap.Entry next() {
               if (!this.hasNext()) {
                  throw new NoSuchElementException();
               } else {
                  this.entry.key = Char2ShortArrayMap.this.key[this.curr = this.next];
                  this.entry.value = Char2ShortArrayMap.this.value[this.next++];
                  return this.entry;
               }
            }

            @Override
            public void remove() {
               if (this.curr == -1) {
                  throw new IllegalStateException();
               } else {
                  this.curr = -1;
                  int tail = Char2ShortArrayMap.this.size-- - this.next--;
                  System.arraycopy(Char2ShortArrayMap.this.key, this.next + 1, Char2ShortArrayMap.this.key, this.next, tail);
                  System.arraycopy(Char2ShortArrayMap.this.value, this.next + 1, Char2ShortArrayMap.this.value, this.next, tail);
               }
            }

            @Override
            public void forEachRemaining(Consumer<? super Char2ShortMap.Entry> action) {
               int max = Char2ShortArrayMap.this.size;

               while (this.next < max) {
                  this.entry.key = Char2ShortArrayMap.this.key[this.curr = this.next];
                  this.entry.value = Char2ShortArrayMap.this.value[this.next++];
                  action.accept(this.entry);
               }
            }
         };
      }

      @Override
      public ObjectSpliterator<Char2ShortMap.Entry> spliterator() {
         return new Char2ShortArrayMap.EntrySet.EntrySetSpliterator(0, Char2ShortArrayMap.this.size);
      }

      @Override
      public void forEach(Consumer<? super Char2ShortMap.Entry> action) {
         int i = 0;

         for (int max = Char2ShortArrayMap.this.size; i < max; i++) {
            action.accept(new AbstractChar2ShortMap.BasicEntry(Char2ShortArrayMap.this.key[i], Char2ShortArrayMap.this.value[i]));
         }
      }

      @Override
      public void fastForEach(Consumer<? super Char2ShortMap.Entry> action) {
         AbstractChar2ShortMap.BasicEntry entry = new AbstractChar2ShortMap.BasicEntry();
         int i = 0;

         for (int max = Char2ShortArrayMap.this.size; i < max; i++) {
            entry.key = Char2ShortArrayMap.this.key[i];
            entry.value = Char2ShortArrayMap.this.value[i];
            action.accept(entry);
         }
      }

      @Override
      public int size() {
         return Char2ShortArrayMap.this.size;
      }

      @Override
      public boolean contains(Object o) {
         if (!(o instanceof Entry)) {
            return false;
         } else {
            Entry<?, ?> e = (Entry<?, ?>)o;
            if (e.getKey() == null || !(e.getKey() instanceof Character)) {
               return false;
            } else if (e.getValue() != null && e.getValue() instanceof Short) {
               char k = (Character)e.getKey();
               return Char2ShortArrayMap.this.containsKey(k) && Char2ShortArrayMap.this.get(k) == (Short)e.getValue();
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
            if (e.getKey() == null || !(e.getKey() instanceof Character)) {
               return false;
            } else if (e.getValue() != null && e.getValue() instanceof Short) {
               char k = (Character)e.getKey();
               short v = (Short)e.getValue();
               int oldPos = Char2ShortArrayMap.this.findKey(k);
               if (oldPos != -1 && v == Char2ShortArrayMap.this.value[oldPos]) {
                  int tail = Char2ShortArrayMap.this.size - oldPos - 1;
                  System.arraycopy(Char2ShortArrayMap.this.key, oldPos + 1, Char2ShortArrayMap.this.key, oldPos, tail);
                  System.arraycopy(Char2ShortArrayMap.this.value, oldPos + 1, Char2ShortArrayMap.this.value, oldPos, tail);
                  Char2ShortArrayMap.this.size--;
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
         extends ObjectSpliterators.EarlyBindingSizeIndexBasedSpliterator<Char2ShortMap.Entry>
         implements ObjectSpliterator<Char2ShortMap.Entry> {
         EntrySetSpliterator(int pos, int maxPos) {
            super(pos, maxPos);
         }

         @Override
         public int characteristics() {
            return 16465;
         }

         protected final Char2ShortMap.Entry get(int location) {
            return new AbstractChar2ShortMap.BasicEntry(Char2ShortArrayMap.this.key[location], Char2ShortArrayMap.this.value[location]);
         }

         protected final Char2ShortArrayMap.EntrySet.EntrySetSpliterator makeForSplit(int pos, int maxPos) {
            return EntrySet.this.new EntrySetSpliterator(pos, maxPos);
         }
      }
   }

   private final class KeySet extends AbstractCharSet {
      private KeySet() {
      }

      @Override
      public boolean contains(char k) {
         return Char2ShortArrayMap.this.findKey(k) != -1;
      }

      @Override
      public boolean remove(char k) {
         int oldPos = Char2ShortArrayMap.this.findKey(k);
         if (oldPos == -1) {
            return false;
         } else {
            int tail = Char2ShortArrayMap.this.size - oldPos - 1;
            System.arraycopy(Char2ShortArrayMap.this.key, oldPos + 1, Char2ShortArrayMap.this.key, oldPos, tail);
            System.arraycopy(Char2ShortArrayMap.this.value, oldPos + 1, Char2ShortArrayMap.this.value, oldPos, tail);
            Char2ShortArrayMap.this.size--;
            return true;
         }
      }

      @Override
      public CharIterator iterator() {
         return new CharIterator() {
            int pos = 0;

            @Override
            public boolean hasNext() {
               return this.pos < Char2ShortArrayMap.this.size;
            }

            @Override
            public char nextChar() {
               if (!this.hasNext()) {
                  throw new NoSuchElementException();
               } else {
                  return Char2ShortArrayMap.this.key[this.pos++];
               }
            }

            @Override
            public void remove() {
               if (this.pos == 0) {
                  throw new IllegalStateException();
               } else {
                  int tail = Char2ShortArrayMap.this.size - this.pos;
                  System.arraycopy(Char2ShortArrayMap.this.key, this.pos, Char2ShortArrayMap.this.key, this.pos - 1, tail);
                  System.arraycopy(Char2ShortArrayMap.this.value, this.pos, Char2ShortArrayMap.this.value, this.pos - 1, tail);
                  Char2ShortArrayMap.this.size--;
                  this.pos--;
               }
            }

            @Override
            public void forEachRemaining(CharConsumer action) {
               int max = Char2ShortArrayMap.this.size;

               while (this.pos < max) {
                  action.accept(Char2ShortArrayMap.this.key[this.pos++]);
               }
            }
         };
      }

      @Override
      public CharSpliterator spliterator() {
         return new Char2ShortArrayMap.KeySet.KeySetSpliterator(0, Char2ShortArrayMap.this.size);
      }

      @Override
      public void forEach(CharConsumer action) {
         int i = 0;

         for (int max = Char2ShortArrayMap.this.size; i < max; i++) {
            action.accept(Char2ShortArrayMap.this.key[i]);
         }
      }

      @Override
      public int size() {
         return Char2ShortArrayMap.this.size;
      }

      @Override
      public void clear() {
         Char2ShortArrayMap.this.clear();
      }

      final class KeySetSpliterator extends CharSpliterators.EarlyBindingSizeIndexBasedSpliterator implements CharSpliterator {
         KeySetSpliterator(int pos, int maxPos) {
            super(pos, maxPos);
         }

         @Override
         public int characteristics() {
            return 16721;
         }

         @Override
         protected final char get(int location) {
            return Char2ShortArrayMap.this.key[location];
         }

         protected final Char2ShortArrayMap.KeySet.KeySetSpliterator makeForSplit(int pos, int maxPos) {
            return KeySet.this.new KeySetSpliterator(pos, maxPos);
         }

         @Override
         public void forEachRemaining(CharConsumer action) {
            int max = Char2ShortArrayMap.this.size;

            while (this.pos < max) {
               action.accept(Char2ShortArrayMap.this.key[this.pos++]);
            }
         }
      }
   }

   private final class ValuesCollection extends AbstractShortCollection {
      private ValuesCollection() {
      }

      @Override
      public boolean contains(short v) {
         return Char2ShortArrayMap.this.containsValue(v);
      }

      @Override
      public ShortIterator iterator() {
         return new ShortIterator() {
            int pos = 0;

            @Override
            public boolean hasNext() {
               return this.pos < Char2ShortArrayMap.this.size;
            }

            @Override
            public short nextShort() {
               if (!this.hasNext()) {
                  throw new NoSuchElementException();
               } else {
                  return Char2ShortArrayMap.this.value[this.pos++];
               }
            }

            @Override
            public void remove() {
               if (this.pos == 0) {
                  throw new IllegalStateException();
               } else {
                  int tail = Char2ShortArrayMap.this.size - this.pos;
                  System.arraycopy(Char2ShortArrayMap.this.key, this.pos, Char2ShortArrayMap.this.key, this.pos - 1, tail);
                  System.arraycopy(Char2ShortArrayMap.this.value, this.pos, Char2ShortArrayMap.this.value, this.pos - 1, tail);
                  Char2ShortArrayMap.this.size--;
                  this.pos--;
               }
            }

            @Override
            public void forEachRemaining(ShortConsumer action) {
               int max = Char2ShortArrayMap.this.size;

               while (this.pos < max) {
                  action.accept(Char2ShortArrayMap.this.value[this.pos++]);
               }
            }
         };
      }

      @Override
      public ShortSpliterator spliterator() {
         return new Char2ShortArrayMap.ValuesCollection.ValuesSpliterator(0, Char2ShortArrayMap.this.size);
      }

      @Override
      public void forEach(ShortConsumer action) {
         int i = 0;

         for (int max = Char2ShortArrayMap.this.size; i < max; i++) {
            action.accept(Char2ShortArrayMap.this.value[i]);
         }
      }

      @Override
      public int size() {
         return Char2ShortArrayMap.this.size;
      }

      @Override
      public void clear() {
         Char2ShortArrayMap.this.clear();
      }

      final class ValuesSpliterator extends ShortSpliterators.EarlyBindingSizeIndexBasedSpliterator implements ShortSpliterator {
         ValuesSpliterator(int pos, int maxPos) {
            super(pos, maxPos);
         }

         @Override
         public int characteristics() {
            return 16720;
         }

         @Override
         protected final short get(int location) {
            return Char2ShortArrayMap.this.value[location];
         }

         protected final Char2ShortArrayMap.ValuesCollection.ValuesSpliterator makeForSplit(int pos, int maxPos) {
            return ValuesCollection.this.new ValuesSpliterator(pos, maxPos);
         }

         @Override
         public void forEachRemaining(ShortConsumer action) {
            int max = Char2ShortArrayMap.this.size;

            while (this.pos < max) {
               action.accept(Char2ShortArrayMap.this.value[this.pos++]);
            }
         }
      }
   }
}
