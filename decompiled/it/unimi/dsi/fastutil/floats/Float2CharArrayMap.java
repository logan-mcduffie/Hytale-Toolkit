package it.unimi.dsi.fastutil.floats;

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

public class Float2CharArrayMap extends AbstractFloat2CharMap implements Serializable, Cloneable {
   private static final long serialVersionUID = 1L;
   protected transient float[] key;
   protected transient char[] value;
   protected int size;
   protected transient Float2CharMap.FastEntrySet entries;
   protected transient FloatSet keys;
   protected transient CharCollection values;

   public Float2CharArrayMap(float[] key, char[] value) {
      this.key = key;
      this.value = value;
      this.size = key.length;
      if (key.length != value.length) {
         throw new IllegalArgumentException("Keys and values have different lengths (" + key.length + ", " + value.length + ")");
      }
   }

   public Float2CharArrayMap() {
      this.key = FloatArrays.EMPTY_ARRAY;
      this.value = CharArrays.EMPTY_ARRAY;
   }

   public Float2CharArrayMap(int capacity) {
      this.key = new float[capacity];
      this.value = new char[capacity];
   }

   public Float2CharArrayMap(Float2CharMap m) {
      this(m.size());
      int i = 0;

      for (Float2CharMap.Entry e : m.float2CharEntrySet()) {
         this.key[i] = e.getFloatKey();
         this.value[i] = e.getCharValue();
         i++;
      }

      this.size = i;
   }

   public Float2CharArrayMap(Map<? extends Float, ? extends Character> m) {
      this(m.size());
      int i = 0;

      for (Entry<? extends Float, ? extends Character> e : m.entrySet()) {
         this.key[i] = e.getKey();
         this.value[i] = e.getValue();
         i++;
      }

      this.size = i;
   }

   public Float2CharArrayMap(float[] key, char[] value, int size) {
      this.key = key;
      this.value = value;
      this.size = size;
      if (key.length != value.length) {
         throw new IllegalArgumentException("Keys and values have different lengths (" + key.length + ", " + value.length + ")");
      } else if (size > key.length) {
         throw new IllegalArgumentException("The provided size (" + size + ") is larger than or equal to the backing-arrays size (" + key.length + ")");
      }
   }

   public Float2CharMap.FastEntrySet float2CharEntrySet() {
      if (this.entries == null) {
         this.entries = new Float2CharArrayMap.EntrySet();
      }

      return this.entries;
   }

   private int findKey(float k) {
      float[] key = this.key;
      int i = this.size;

      while (i-- != 0) {
         if (Float.floatToIntBits(key[i]) == Float.floatToIntBits(k)) {
            return i;
         }
      }

      return -1;
   }

   @Override
   public char get(float k) {
      float[] key = this.key;
      int i = this.size;

      while (i-- != 0) {
         if (Float.floatToIntBits(key[i]) == Float.floatToIntBits(k)) {
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
   public boolean containsKey(float k) {
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
   public char put(float k, char v) {
      int oldKey = this.findKey(k);
      if (oldKey != -1) {
         char oldValue = this.value[oldKey];
         this.value[oldKey] = v;
         return oldValue;
      } else {
         if (this.size == this.key.length) {
            float[] newKey = new float[this.size == 0 ? 2 : this.size * 2];
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
   public char remove(float k) {
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
   public FloatSet keySet() {
      if (this.keys == null) {
         this.keys = new Float2CharArrayMap.KeySet();
      }

      return this.keys;
   }

   @Override
   public CharCollection values() {
      if (this.values == null) {
         this.values = new Float2CharArrayMap.ValuesCollection();
      }

      return this.values;
   }

   public Float2CharArrayMap clone() {
      Float2CharArrayMap c;
      try {
         c = (Float2CharArrayMap)super.clone();
      } catch (CloneNotSupportedException var3) {
         throw new InternalError();
      }

      c.key = (float[])this.key.clone();
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
         s.writeFloat(this.key[i]);
         s.writeChar(this.value[i]);
      }
   }

   private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
      s.defaultReadObject();
      this.key = new float[this.size];
      this.value = new char[this.size];

      for (int i = 0; i < this.size; i++) {
         this.key[i] = s.readFloat();
         this.value[i] = s.readChar();
      }
   }

   private final class EntrySet extends AbstractObjectSet<Float2CharMap.Entry> implements Float2CharMap.FastEntrySet {
      private EntrySet() {
      }

      @Override
      public ObjectIterator<Float2CharMap.Entry> iterator() {
         return new ObjectIterator<Float2CharMap.Entry>() {
            int curr = -1;
            int next = 0;

            @Override
            public boolean hasNext() {
               return this.next < Float2CharArrayMap.this.size;
            }

            public Float2CharMap.Entry next() {
               if (!this.hasNext()) {
                  throw new NoSuchElementException();
               } else {
                  return new AbstractFloat2CharMap.BasicEntry(Float2CharArrayMap.this.key[this.curr = this.next], Float2CharArrayMap.this.value[this.next++]);
               }
            }

            @Override
            public void remove() {
               if (this.curr == -1) {
                  throw new IllegalStateException();
               } else {
                  this.curr = -1;
                  int tail = Float2CharArrayMap.this.size-- - this.next--;
                  System.arraycopy(Float2CharArrayMap.this.key, this.next + 1, Float2CharArrayMap.this.key, this.next, tail);
                  System.arraycopy(Float2CharArrayMap.this.value, this.next + 1, Float2CharArrayMap.this.value, this.next, tail);
               }
            }

            @Override
            public void forEachRemaining(Consumer<? super Float2CharMap.Entry> action) {
               int max = Float2CharArrayMap.this.size;

               while (this.next < max) {
                  action.accept(
                     new AbstractFloat2CharMap.BasicEntry(Float2CharArrayMap.this.key[this.curr = this.next], Float2CharArrayMap.this.value[this.next++])
                  );
               }
            }
         };
      }

      @Override
      public ObjectIterator<Float2CharMap.Entry> fastIterator() {
         return new ObjectIterator<Float2CharMap.Entry>() {
            int next = 0;
            int curr = -1;
            final AbstractFloat2CharMap.BasicEntry entry = new AbstractFloat2CharMap.BasicEntry();

            @Override
            public boolean hasNext() {
               return this.next < Float2CharArrayMap.this.size;
            }

            public Float2CharMap.Entry next() {
               if (!this.hasNext()) {
                  throw new NoSuchElementException();
               } else {
                  this.entry.key = Float2CharArrayMap.this.key[this.curr = this.next];
                  this.entry.value = Float2CharArrayMap.this.value[this.next++];
                  return this.entry;
               }
            }

            @Override
            public void remove() {
               if (this.curr == -1) {
                  throw new IllegalStateException();
               } else {
                  this.curr = -1;
                  int tail = Float2CharArrayMap.this.size-- - this.next--;
                  System.arraycopy(Float2CharArrayMap.this.key, this.next + 1, Float2CharArrayMap.this.key, this.next, tail);
                  System.arraycopy(Float2CharArrayMap.this.value, this.next + 1, Float2CharArrayMap.this.value, this.next, tail);
               }
            }

            @Override
            public void forEachRemaining(Consumer<? super Float2CharMap.Entry> action) {
               int max = Float2CharArrayMap.this.size;

               while (this.next < max) {
                  this.entry.key = Float2CharArrayMap.this.key[this.curr = this.next];
                  this.entry.value = Float2CharArrayMap.this.value[this.next++];
                  action.accept(this.entry);
               }
            }
         };
      }

      @Override
      public ObjectSpliterator<Float2CharMap.Entry> spliterator() {
         return new Float2CharArrayMap.EntrySet.EntrySetSpliterator(0, Float2CharArrayMap.this.size);
      }

      @Override
      public void forEach(Consumer<? super Float2CharMap.Entry> action) {
         int i = 0;

         for (int max = Float2CharArrayMap.this.size; i < max; i++) {
            action.accept(new AbstractFloat2CharMap.BasicEntry(Float2CharArrayMap.this.key[i], Float2CharArrayMap.this.value[i]));
         }
      }

      @Override
      public void fastForEach(Consumer<? super Float2CharMap.Entry> action) {
         AbstractFloat2CharMap.BasicEntry entry = new AbstractFloat2CharMap.BasicEntry();
         int i = 0;

         for (int max = Float2CharArrayMap.this.size; i < max; i++) {
            entry.key = Float2CharArrayMap.this.key[i];
            entry.value = Float2CharArrayMap.this.value[i];
            action.accept(entry);
         }
      }

      @Override
      public int size() {
         return Float2CharArrayMap.this.size;
      }

      @Override
      public boolean contains(Object o) {
         if (!(o instanceof Entry)) {
            return false;
         } else {
            Entry<?, ?> e = (Entry<?, ?>)o;
            if (e.getKey() == null || !(e.getKey() instanceof Float)) {
               return false;
            } else if (e.getValue() != null && e.getValue() instanceof Character) {
               float k = (Float)e.getKey();
               return Float2CharArrayMap.this.containsKey(k) && Float2CharArrayMap.this.get(k) == (Character)e.getValue();
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
            if (e.getKey() == null || !(e.getKey() instanceof Float)) {
               return false;
            } else if (e.getValue() != null && e.getValue() instanceof Character) {
               float k = (Float)e.getKey();
               char v = (Character)e.getValue();
               int oldPos = Float2CharArrayMap.this.findKey(k);
               if (oldPos != -1 && v == Float2CharArrayMap.this.value[oldPos]) {
                  int tail = Float2CharArrayMap.this.size - oldPos - 1;
                  System.arraycopy(Float2CharArrayMap.this.key, oldPos + 1, Float2CharArrayMap.this.key, oldPos, tail);
                  System.arraycopy(Float2CharArrayMap.this.value, oldPos + 1, Float2CharArrayMap.this.value, oldPos, tail);
                  Float2CharArrayMap.this.size--;
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
         extends ObjectSpliterators.EarlyBindingSizeIndexBasedSpliterator<Float2CharMap.Entry>
         implements ObjectSpliterator<Float2CharMap.Entry> {
         EntrySetSpliterator(int pos, int maxPos) {
            super(pos, maxPos);
         }

         @Override
         public int characteristics() {
            return 16465;
         }

         protected final Float2CharMap.Entry get(int location) {
            return new AbstractFloat2CharMap.BasicEntry(Float2CharArrayMap.this.key[location], Float2CharArrayMap.this.value[location]);
         }

         protected final Float2CharArrayMap.EntrySet.EntrySetSpliterator makeForSplit(int pos, int maxPos) {
            return EntrySet.this.new EntrySetSpliterator(pos, maxPos);
         }
      }
   }

   private final class KeySet extends AbstractFloatSet {
      private KeySet() {
      }

      @Override
      public boolean contains(float k) {
         return Float2CharArrayMap.this.findKey(k) != -1;
      }

      @Override
      public boolean remove(float k) {
         int oldPos = Float2CharArrayMap.this.findKey(k);
         if (oldPos == -1) {
            return false;
         } else {
            int tail = Float2CharArrayMap.this.size - oldPos - 1;
            System.arraycopy(Float2CharArrayMap.this.key, oldPos + 1, Float2CharArrayMap.this.key, oldPos, tail);
            System.arraycopy(Float2CharArrayMap.this.value, oldPos + 1, Float2CharArrayMap.this.value, oldPos, tail);
            Float2CharArrayMap.this.size--;
            return true;
         }
      }

      @Override
      public FloatIterator iterator() {
         return new FloatIterator() {
            int pos = 0;

            @Override
            public boolean hasNext() {
               return this.pos < Float2CharArrayMap.this.size;
            }

            @Override
            public float nextFloat() {
               if (!this.hasNext()) {
                  throw new NoSuchElementException();
               } else {
                  return Float2CharArrayMap.this.key[this.pos++];
               }
            }

            @Override
            public void remove() {
               if (this.pos == 0) {
                  throw new IllegalStateException();
               } else {
                  int tail = Float2CharArrayMap.this.size - this.pos;
                  System.arraycopy(Float2CharArrayMap.this.key, this.pos, Float2CharArrayMap.this.key, this.pos - 1, tail);
                  System.arraycopy(Float2CharArrayMap.this.value, this.pos, Float2CharArrayMap.this.value, this.pos - 1, tail);
                  Float2CharArrayMap.this.size--;
                  this.pos--;
               }
            }

            @Override
            public void forEachRemaining(FloatConsumer action) {
               int max = Float2CharArrayMap.this.size;

               while (this.pos < max) {
                  action.accept(Float2CharArrayMap.this.key[this.pos++]);
               }
            }
         };
      }

      @Override
      public FloatSpliterator spliterator() {
         return new Float2CharArrayMap.KeySet.KeySetSpliterator(0, Float2CharArrayMap.this.size);
      }

      @Override
      public void forEach(FloatConsumer action) {
         int i = 0;

         for (int max = Float2CharArrayMap.this.size; i < max; i++) {
            action.accept(Float2CharArrayMap.this.key[i]);
         }
      }

      @Override
      public int size() {
         return Float2CharArrayMap.this.size;
      }

      @Override
      public void clear() {
         Float2CharArrayMap.this.clear();
      }

      final class KeySetSpliterator extends FloatSpliterators.EarlyBindingSizeIndexBasedSpliterator implements FloatSpliterator {
         KeySetSpliterator(int pos, int maxPos) {
            super(pos, maxPos);
         }

         @Override
         public int characteristics() {
            return 16721;
         }

         @Override
         protected final float get(int location) {
            return Float2CharArrayMap.this.key[location];
         }

         protected final Float2CharArrayMap.KeySet.KeySetSpliterator makeForSplit(int pos, int maxPos) {
            return KeySet.this.new KeySetSpliterator(pos, maxPos);
         }

         @Override
         public void forEachRemaining(FloatConsumer action) {
            int max = Float2CharArrayMap.this.size;

            while (this.pos < max) {
               action.accept(Float2CharArrayMap.this.key[this.pos++]);
            }
         }
      }
   }

   private final class ValuesCollection extends AbstractCharCollection {
      private ValuesCollection() {
      }

      @Override
      public boolean contains(char v) {
         return Float2CharArrayMap.this.containsValue(v);
      }

      @Override
      public CharIterator iterator() {
         return new CharIterator() {
            int pos = 0;

            @Override
            public boolean hasNext() {
               return this.pos < Float2CharArrayMap.this.size;
            }

            @Override
            public char nextChar() {
               if (!this.hasNext()) {
                  throw new NoSuchElementException();
               } else {
                  return Float2CharArrayMap.this.value[this.pos++];
               }
            }

            @Override
            public void remove() {
               if (this.pos == 0) {
                  throw new IllegalStateException();
               } else {
                  int tail = Float2CharArrayMap.this.size - this.pos;
                  System.arraycopy(Float2CharArrayMap.this.key, this.pos, Float2CharArrayMap.this.key, this.pos - 1, tail);
                  System.arraycopy(Float2CharArrayMap.this.value, this.pos, Float2CharArrayMap.this.value, this.pos - 1, tail);
                  Float2CharArrayMap.this.size--;
                  this.pos--;
               }
            }

            @Override
            public void forEachRemaining(CharConsumer action) {
               int max = Float2CharArrayMap.this.size;

               while (this.pos < max) {
                  action.accept(Float2CharArrayMap.this.value[this.pos++]);
               }
            }
         };
      }

      @Override
      public CharSpliterator spliterator() {
         return new Float2CharArrayMap.ValuesCollection.ValuesSpliterator(0, Float2CharArrayMap.this.size);
      }

      @Override
      public void forEach(CharConsumer action) {
         int i = 0;

         for (int max = Float2CharArrayMap.this.size; i < max; i++) {
            action.accept(Float2CharArrayMap.this.value[i]);
         }
      }

      @Override
      public int size() {
         return Float2CharArrayMap.this.size;
      }

      @Override
      public void clear() {
         Float2CharArrayMap.this.clear();
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
            return Float2CharArrayMap.this.value[location];
         }

         protected final Float2CharArrayMap.ValuesCollection.ValuesSpliterator makeForSplit(int pos, int maxPos) {
            return ValuesCollection.this.new ValuesSpliterator(pos, maxPos);
         }

         @Override
         public void forEachRemaining(CharConsumer action) {
            int max = Float2CharArrayMap.this.size;

            while (this.pos < max) {
               action.accept(Float2CharArrayMap.this.value[this.pos++]);
            }
         }
      }
   }
}
