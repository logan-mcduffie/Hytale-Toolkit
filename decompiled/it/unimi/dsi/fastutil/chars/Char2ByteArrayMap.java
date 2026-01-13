package it.unimi.dsi.fastutil.chars;

import it.unimi.dsi.fastutil.bytes.AbstractByteCollection;
import it.unimi.dsi.fastutil.bytes.ByteArrays;
import it.unimi.dsi.fastutil.bytes.ByteCollection;
import it.unimi.dsi.fastutil.bytes.ByteConsumer;
import it.unimi.dsi.fastutil.bytes.ByteIterator;
import it.unimi.dsi.fastutil.bytes.ByteSpliterator;
import it.unimi.dsi.fastutil.bytes.ByteSpliterators;
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

public class Char2ByteArrayMap extends AbstractChar2ByteMap implements Serializable, Cloneable {
   private static final long serialVersionUID = 1L;
   protected transient char[] key;
   protected transient byte[] value;
   protected int size;
   protected transient Char2ByteMap.FastEntrySet entries;
   protected transient CharSet keys;
   protected transient ByteCollection values;

   public Char2ByteArrayMap(char[] key, byte[] value) {
      this.key = key;
      this.value = value;
      this.size = key.length;
      if (key.length != value.length) {
         throw new IllegalArgumentException("Keys and values have different lengths (" + key.length + ", " + value.length + ")");
      }
   }

   public Char2ByteArrayMap() {
      this.key = CharArrays.EMPTY_ARRAY;
      this.value = ByteArrays.EMPTY_ARRAY;
   }

   public Char2ByteArrayMap(int capacity) {
      this.key = new char[capacity];
      this.value = new byte[capacity];
   }

   public Char2ByteArrayMap(Char2ByteMap m) {
      this(m.size());
      int i = 0;

      for (Char2ByteMap.Entry e : m.char2ByteEntrySet()) {
         this.key[i] = e.getCharKey();
         this.value[i] = e.getByteValue();
         i++;
      }

      this.size = i;
   }

   public Char2ByteArrayMap(Map<? extends Character, ? extends Byte> m) {
      this(m.size());
      int i = 0;

      for (Entry<? extends Character, ? extends Byte> e : m.entrySet()) {
         this.key[i] = e.getKey();
         this.value[i] = e.getValue();
         i++;
      }

      this.size = i;
   }

   public Char2ByteArrayMap(char[] key, byte[] value, int size) {
      this.key = key;
      this.value = value;
      this.size = size;
      if (key.length != value.length) {
         throw new IllegalArgumentException("Keys and values have different lengths (" + key.length + ", " + value.length + ")");
      } else if (size > key.length) {
         throw new IllegalArgumentException("The provided size (" + size + ") is larger than or equal to the backing-arrays size (" + key.length + ")");
      }
   }

   public Char2ByteMap.FastEntrySet char2ByteEntrySet() {
      if (this.entries == null) {
         this.entries = new Char2ByteArrayMap.EntrySet();
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
   public byte get(char k) {
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
   public boolean containsValue(byte v) {
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
   public byte put(char k, byte v) {
      int oldKey = this.findKey(k);
      if (oldKey != -1) {
         byte oldValue = this.value[oldKey];
         this.value[oldKey] = v;
         return oldValue;
      } else {
         if (this.size == this.key.length) {
            char[] newKey = new char[this.size == 0 ? 2 : this.size * 2];
            byte[] newValue = new byte[this.size == 0 ? 2 : this.size * 2];

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
   public byte remove(char k) {
      int oldPos = this.findKey(k);
      if (oldPos == -1) {
         return this.defRetValue;
      } else {
         byte oldValue = this.value[oldPos];
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
         this.keys = new Char2ByteArrayMap.KeySet();
      }

      return this.keys;
   }

   @Override
   public ByteCollection values() {
      if (this.values == null) {
         this.values = new Char2ByteArrayMap.ValuesCollection();
      }

      return this.values;
   }

   public Char2ByteArrayMap clone() {
      Char2ByteArrayMap c;
      try {
         c = (Char2ByteArrayMap)super.clone();
      } catch (CloneNotSupportedException var3) {
         throw new InternalError();
      }

      c.key = (char[])this.key.clone();
      c.value = (byte[])this.value.clone();
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
         s.writeByte(this.value[i]);
      }
   }

   private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
      s.defaultReadObject();
      this.key = new char[this.size];
      this.value = new byte[this.size];

      for (int i = 0; i < this.size; i++) {
         this.key[i] = s.readChar();
         this.value[i] = s.readByte();
      }
   }

   private final class EntrySet extends AbstractObjectSet<Char2ByteMap.Entry> implements Char2ByteMap.FastEntrySet {
      private EntrySet() {
      }

      @Override
      public ObjectIterator<Char2ByteMap.Entry> iterator() {
         return new ObjectIterator<Char2ByteMap.Entry>() {
            int curr = -1;
            int next = 0;

            @Override
            public boolean hasNext() {
               return this.next < Char2ByteArrayMap.this.size;
            }

            public Char2ByteMap.Entry next() {
               if (!this.hasNext()) {
                  throw new NoSuchElementException();
               } else {
                  return new AbstractChar2ByteMap.BasicEntry(Char2ByteArrayMap.this.key[this.curr = this.next], Char2ByteArrayMap.this.value[this.next++]);
               }
            }

            @Override
            public void remove() {
               if (this.curr == -1) {
                  throw new IllegalStateException();
               } else {
                  this.curr = -1;
                  int tail = Char2ByteArrayMap.this.size-- - this.next--;
                  System.arraycopy(Char2ByteArrayMap.this.key, this.next + 1, Char2ByteArrayMap.this.key, this.next, tail);
                  System.arraycopy(Char2ByteArrayMap.this.value, this.next + 1, Char2ByteArrayMap.this.value, this.next, tail);
               }
            }

            @Override
            public void forEachRemaining(Consumer<? super Char2ByteMap.Entry> action) {
               int max = Char2ByteArrayMap.this.size;

               while (this.next < max) {
                  action.accept(
                     new AbstractChar2ByteMap.BasicEntry(Char2ByteArrayMap.this.key[this.curr = this.next], Char2ByteArrayMap.this.value[this.next++])
                  );
               }
            }
         };
      }

      @Override
      public ObjectIterator<Char2ByteMap.Entry> fastIterator() {
         return new ObjectIterator<Char2ByteMap.Entry>() {
            int next = 0;
            int curr = -1;
            final AbstractChar2ByteMap.BasicEntry entry = new AbstractChar2ByteMap.BasicEntry();

            @Override
            public boolean hasNext() {
               return this.next < Char2ByteArrayMap.this.size;
            }

            public Char2ByteMap.Entry next() {
               if (!this.hasNext()) {
                  throw new NoSuchElementException();
               } else {
                  this.entry.key = Char2ByteArrayMap.this.key[this.curr = this.next];
                  this.entry.value = Char2ByteArrayMap.this.value[this.next++];
                  return this.entry;
               }
            }

            @Override
            public void remove() {
               if (this.curr == -1) {
                  throw new IllegalStateException();
               } else {
                  this.curr = -1;
                  int tail = Char2ByteArrayMap.this.size-- - this.next--;
                  System.arraycopy(Char2ByteArrayMap.this.key, this.next + 1, Char2ByteArrayMap.this.key, this.next, tail);
                  System.arraycopy(Char2ByteArrayMap.this.value, this.next + 1, Char2ByteArrayMap.this.value, this.next, tail);
               }
            }

            @Override
            public void forEachRemaining(Consumer<? super Char2ByteMap.Entry> action) {
               int max = Char2ByteArrayMap.this.size;

               while (this.next < max) {
                  this.entry.key = Char2ByteArrayMap.this.key[this.curr = this.next];
                  this.entry.value = Char2ByteArrayMap.this.value[this.next++];
                  action.accept(this.entry);
               }
            }
         };
      }

      @Override
      public ObjectSpliterator<Char2ByteMap.Entry> spliterator() {
         return new Char2ByteArrayMap.EntrySet.EntrySetSpliterator(0, Char2ByteArrayMap.this.size);
      }

      @Override
      public void forEach(Consumer<? super Char2ByteMap.Entry> action) {
         int i = 0;

         for (int max = Char2ByteArrayMap.this.size; i < max; i++) {
            action.accept(new AbstractChar2ByteMap.BasicEntry(Char2ByteArrayMap.this.key[i], Char2ByteArrayMap.this.value[i]));
         }
      }

      @Override
      public void fastForEach(Consumer<? super Char2ByteMap.Entry> action) {
         AbstractChar2ByteMap.BasicEntry entry = new AbstractChar2ByteMap.BasicEntry();
         int i = 0;

         for (int max = Char2ByteArrayMap.this.size; i < max; i++) {
            entry.key = Char2ByteArrayMap.this.key[i];
            entry.value = Char2ByteArrayMap.this.value[i];
            action.accept(entry);
         }
      }

      @Override
      public int size() {
         return Char2ByteArrayMap.this.size;
      }

      @Override
      public boolean contains(Object o) {
         if (!(o instanceof Entry)) {
            return false;
         } else {
            Entry<?, ?> e = (Entry<?, ?>)o;
            if (e.getKey() == null || !(e.getKey() instanceof Character)) {
               return false;
            } else if (e.getValue() != null && e.getValue() instanceof Byte) {
               char k = (Character)e.getKey();
               return Char2ByteArrayMap.this.containsKey(k) && Char2ByteArrayMap.this.get(k) == (Byte)e.getValue();
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
            } else if (e.getValue() != null && e.getValue() instanceof Byte) {
               char k = (Character)e.getKey();
               byte v = (Byte)e.getValue();
               int oldPos = Char2ByteArrayMap.this.findKey(k);
               if (oldPos != -1 && v == Char2ByteArrayMap.this.value[oldPos]) {
                  int tail = Char2ByteArrayMap.this.size - oldPos - 1;
                  System.arraycopy(Char2ByteArrayMap.this.key, oldPos + 1, Char2ByteArrayMap.this.key, oldPos, tail);
                  System.arraycopy(Char2ByteArrayMap.this.value, oldPos + 1, Char2ByteArrayMap.this.value, oldPos, tail);
                  Char2ByteArrayMap.this.size--;
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
         extends ObjectSpliterators.EarlyBindingSizeIndexBasedSpliterator<Char2ByteMap.Entry>
         implements ObjectSpliterator<Char2ByteMap.Entry> {
         EntrySetSpliterator(int pos, int maxPos) {
            super(pos, maxPos);
         }

         @Override
         public int characteristics() {
            return 16465;
         }

         protected final Char2ByteMap.Entry get(int location) {
            return new AbstractChar2ByteMap.BasicEntry(Char2ByteArrayMap.this.key[location], Char2ByteArrayMap.this.value[location]);
         }

         protected final Char2ByteArrayMap.EntrySet.EntrySetSpliterator makeForSplit(int pos, int maxPos) {
            return EntrySet.this.new EntrySetSpliterator(pos, maxPos);
         }
      }
   }

   private final class KeySet extends AbstractCharSet {
      private KeySet() {
      }

      @Override
      public boolean contains(char k) {
         return Char2ByteArrayMap.this.findKey(k) != -1;
      }

      @Override
      public boolean remove(char k) {
         int oldPos = Char2ByteArrayMap.this.findKey(k);
         if (oldPos == -1) {
            return false;
         } else {
            int tail = Char2ByteArrayMap.this.size - oldPos - 1;
            System.arraycopy(Char2ByteArrayMap.this.key, oldPos + 1, Char2ByteArrayMap.this.key, oldPos, tail);
            System.arraycopy(Char2ByteArrayMap.this.value, oldPos + 1, Char2ByteArrayMap.this.value, oldPos, tail);
            Char2ByteArrayMap.this.size--;
            return true;
         }
      }

      @Override
      public CharIterator iterator() {
         return new CharIterator() {
            int pos = 0;

            @Override
            public boolean hasNext() {
               return this.pos < Char2ByteArrayMap.this.size;
            }

            @Override
            public char nextChar() {
               if (!this.hasNext()) {
                  throw new NoSuchElementException();
               } else {
                  return Char2ByteArrayMap.this.key[this.pos++];
               }
            }

            @Override
            public void remove() {
               if (this.pos == 0) {
                  throw new IllegalStateException();
               } else {
                  int tail = Char2ByteArrayMap.this.size - this.pos;
                  System.arraycopy(Char2ByteArrayMap.this.key, this.pos, Char2ByteArrayMap.this.key, this.pos - 1, tail);
                  System.arraycopy(Char2ByteArrayMap.this.value, this.pos, Char2ByteArrayMap.this.value, this.pos - 1, tail);
                  Char2ByteArrayMap.this.size--;
                  this.pos--;
               }
            }

            @Override
            public void forEachRemaining(CharConsumer action) {
               int max = Char2ByteArrayMap.this.size;

               while (this.pos < max) {
                  action.accept(Char2ByteArrayMap.this.key[this.pos++]);
               }
            }
         };
      }

      @Override
      public CharSpliterator spliterator() {
         return new Char2ByteArrayMap.KeySet.KeySetSpliterator(0, Char2ByteArrayMap.this.size);
      }

      @Override
      public void forEach(CharConsumer action) {
         int i = 0;

         for (int max = Char2ByteArrayMap.this.size; i < max; i++) {
            action.accept(Char2ByteArrayMap.this.key[i]);
         }
      }

      @Override
      public int size() {
         return Char2ByteArrayMap.this.size;
      }

      @Override
      public void clear() {
         Char2ByteArrayMap.this.clear();
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
            return Char2ByteArrayMap.this.key[location];
         }

         protected final Char2ByteArrayMap.KeySet.KeySetSpliterator makeForSplit(int pos, int maxPos) {
            return KeySet.this.new KeySetSpliterator(pos, maxPos);
         }

         @Override
         public void forEachRemaining(CharConsumer action) {
            int max = Char2ByteArrayMap.this.size;

            while (this.pos < max) {
               action.accept(Char2ByteArrayMap.this.key[this.pos++]);
            }
         }
      }
   }

   private final class ValuesCollection extends AbstractByteCollection {
      private ValuesCollection() {
      }

      @Override
      public boolean contains(byte v) {
         return Char2ByteArrayMap.this.containsValue(v);
      }

      @Override
      public ByteIterator iterator() {
         return new ByteIterator() {
            int pos = 0;

            @Override
            public boolean hasNext() {
               return this.pos < Char2ByteArrayMap.this.size;
            }

            @Override
            public byte nextByte() {
               if (!this.hasNext()) {
                  throw new NoSuchElementException();
               } else {
                  return Char2ByteArrayMap.this.value[this.pos++];
               }
            }

            @Override
            public void remove() {
               if (this.pos == 0) {
                  throw new IllegalStateException();
               } else {
                  int tail = Char2ByteArrayMap.this.size - this.pos;
                  System.arraycopy(Char2ByteArrayMap.this.key, this.pos, Char2ByteArrayMap.this.key, this.pos - 1, tail);
                  System.arraycopy(Char2ByteArrayMap.this.value, this.pos, Char2ByteArrayMap.this.value, this.pos - 1, tail);
                  Char2ByteArrayMap.this.size--;
                  this.pos--;
               }
            }

            @Override
            public void forEachRemaining(ByteConsumer action) {
               int max = Char2ByteArrayMap.this.size;

               while (this.pos < max) {
                  action.accept(Char2ByteArrayMap.this.value[this.pos++]);
               }
            }
         };
      }

      @Override
      public ByteSpliterator spliterator() {
         return new Char2ByteArrayMap.ValuesCollection.ValuesSpliterator(0, Char2ByteArrayMap.this.size);
      }

      @Override
      public void forEach(ByteConsumer action) {
         int i = 0;

         for (int max = Char2ByteArrayMap.this.size; i < max; i++) {
            action.accept(Char2ByteArrayMap.this.value[i]);
         }
      }

      @Override
      public int size() {
         return Char2ByteArrayMap.this.size;
      }

      @Override
      public void clear() {
         Char2ByteArrayMap.this.clear();
      }

      final class ValuesSpliterator extends ByteSpliterators.EarlyBindingSizeIndexBasedSpliterator implements ByteSpliterator {
         ValuesSpliterator(int pos, int maxPos) {
            super(pos, maxPos);
         }

         @Override
         public int characteristics() {
            return 16720;
         }

         @Override
         protected final byte get(int location) {
            return Char2ByteArrayMap.this.value[location];
         }

         protected final Char2ByteArrayMap.ValuesCollection.ValuesSpliterator makeForSplit(int pos, int maxPos) {
            return ValuesCollection.this.new ValuesSpliterator(pos, maxPos);
         }

         @Override
         public void forEachRemaining(ByteConsumer action) {
            int max = Char2ByteArrayMap.this.size;

            while (this.pos < max) {
               action.accept(Char2ByteArrayMap.this.value[this.pos++]);
            }
         }
      }
   }
}
