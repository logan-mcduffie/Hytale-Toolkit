package it.unimi.dsi.fastutil.shorts;

import it.unimi.dsi.fastutil.floats.AbstractFloatCollection;
import it.unimi.dsi.fastutil.floats.FloatArrays;
import it.unimi.dsi.fastutil.floats.FloatCollection;
import it.unimi.dsi.fastutil.floats.FloatConsumer;
import it.unimi.dsi.fastutil.floats.FloatIterator;
import it.unimi.dsi.fastutil.floats.FloatSpliterator;
import it.unimi.dsi.fastutil.floats.FloatSpliterators;
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

public class Short2FloatArrayMap extends AbstractShort2FloatMap implements Serializable, Cloneable {
   private static final long serialVersionUID = 1L;
   protected transient short[] key;
   protected transient float[] value;
   protected int size;
   protected transient Short2FloatMap.FastEntrySet entries;
   protected transient ShortSet keys;
   protected transient FloatCollection values;

   public Short2FloatArrayMap(short[] key, float[] value) {
      this.key = key;
      this.value = value;
      this.size = key.length;
      if (key.length != value.length) {
         throw new IllegalArgumentException("Keys and values have different lengths (" + key.length + ", " + value.length + ")");
      }
   }

   public Short2FloatArrayMap() {
      this.key = ShortArrays.EMPTY_ARRAY;
      this.value = FloatArrays.EMPTY_ARRAY;
   }

   public Short2FloatArrayMap(int capacity) {
      this.key = new short[capacity];
      this.value = new float[capacity];
   }

   public Short2FloatArrayMap(Short2FloatMap m) {
      this(m.size());
      int i = 0;

      for (Short2FloatMap.Entry e : m.short2FloatEntrySet()) {
         this.key[i] = e.getShortKey();
         this.value[i] = e.getFloatValue();
         i++;
      }

      this.size = i;
   }

   public Short2FloatArrayMap(Map<? extends Short, ? extends Float> m) {
      this(m.size());
      int i = 0;

      for (Entry<? extends Short, ? extends Float> e : m.entrySet()) {
         this.key[i] = e.getKey();
         this.value[i] = e.getValue();
         i++;
      }

      this.size = i;
   }

   public Short2FloatArrayMap(short[] key, float[] value, int size) {
      this.key = key;
      this.value = value;
      this.size = size;
      if (key.length != value.length) {
         throw new IllegalArgumentException("Keys and values have different lengths (" + key.length + ", " + value.length + ")");
      } else if (size > key.length) {
         throw new IllegalArgumentException("The provided size (" + size + ") is larger than or equal to the backing-arrays size (" + key.length + ")");
      }
   }

   public Short2FloatMap.FastEntrySet short2FloatEntrySet() {
      if (this.entries == null) {
         this.entries = new Short2FloatArrayMap.EntrySet();
      }

      return this.entries;
   }

   private int findKey(short k) {
      short[] key = this.key;
      int i = this.size;

      while (i-- != 0) {
         if (key[i] == k) {
            return i;
         }
      }

      return -1;
   }

   @Override
   public float get(short k) {
      short[] key = this.key;
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
   public boolean containsKey(short k) {
      return this.findKey(k) != -1;
   }

   @Override
   public boolean containsValue(float v) {
      int i = this.size;

      while (i-- != 0) {
         if (Float.floatToIntBits(this.value[i]) == Float.floatToIntBits(v)) {
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
   public float put(short k, float v) {
      int oldKey = this.findKey(k);
      if (oldKey != -1) {
         float oldValue = this.value[oldKey];
         this.value[oldKey] = v;
         return oldValue;
      } else {
         if (this.size == this.key.length) {
            short[] newKey = new short[this.size == 0 ? 2 : this.size * 2];
            float[] newValue = new float[this.size == 0 ? 2 : this.size * 2];

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
   public float remove(short k) {
      int oldPos = this.findKey(k);
      if (oldPos == -1) {
         return this.defRetValue;
      } else {
         float oldValue = this.value[oldPos];
         int tail = this.size - oldPos - 1;
         System.arraycopy(this.key, oldPos + 1, this.key, oldPos, tail);
         System.arraycopy(this.value, oldPos + 1, this.value, oldPos, tail);
         this.size--;
         return oldValue;
      }
   }

   @Override
   public ShortSet keySet() {
      if (this.keys == null) {
         this.keys = new Short2FloatArrayMap.KeySet();
      }

      return this.keys;
   }

   @Override
   public FloatCollection values() {
      if (this.values == null) {
         this.values = new Short2FloatArrayMap.ValuesCollection();
      }

      return this.values;
   }

   public Short2FloatArrayMap clone() {
      Short2FloatArrayMap c;
      try {
         c = (Short2FloatArrayMap)super.clone();
      } catch (CloneNotSupportedException var3) {
         throw new InternalError();
      }

      c.key = (short[])this.key.clone();
      c.value = (float[])this.value.clone();
      c.entries = null;
      c.keys = null;
      c.values = null;
      return c;
   }

   private void writeObject(ObjectOutputStream s) throws IOException {
      s.defaultWriteObject();
      int i = 0;

      for (int max = this.size; i < max; i++) {
         s.writeShort(this.key[i]);
         s.writeFloat(this.value[i]);
      }
   }

   private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
      s.defaultReadObject();
      this.key = new short[this.size];
      this.value = new float[this.size];

      for (int i = 0; i < this.size; i++) {
         this.key[i] = s.readShort();
         this.value[i] = s.readFloat();
      }
   }

   private final class EntrySet extends AbstractObjectSet<Short2FloatMap.Entry> implements Short2FloatMap.FastEntrySet {
      private EntrySet() {
      }

      @Override
      public ObjectIterator<Short2FloatMap.Entry> iterator() {
         return new ObjectIterator<Short2FloatMap.Entry>() {
            int curr = -1;
            int next = 0;

            @Override
            public boolean hasNext() {
               return this.next < Short2FloatArrayMap.this.size;
            }

            public Short2FloatMap.Entry next() {
               if (!this.hasNext()) {
                  throw new NoSuchElementException();
               } else {
                  return new AbstractShort2FloatMap.BasicEntry(Short2FloatArrayMap.this.key[this.curr = this.next], Short2FloatArrayMap.this.value[this.next++]);
               }
            }

            @Override
            public void remove() {
               if (this.curr == -1) {
                  throw new IllegalStateException();
               } else {
                  this.curr = -1;
                  int tail = Short2FloatArrayMap.this.size-- - this.next--;
                  System.arraycopy(Short2FloatArrayMap.this.key, this.next + 1, Short2FloatArrayMap.this.key, this.next, tail);
                  System.arraycopy(Short2FloatArrayMap.this.value, this.next + 1, Short2FloatArrayMap.this.value, this.next, tail);
               }
            }

            @Override
            public void forEachRemaining(Consumer<? super Short2FloatMap.Entry> action) {
               int max = Short2FloatArrayMap.this.size;

               while (this.next < max) {
                  action.accept(
                     new AbstractShort2FloatMap.BasicEntry(Short2FloatArrayMap.this.key[this.curr = this.next], Short2FloatArrayMap.this.value[this.next++])
                  );
               }
            }
         };
      }

      @Override
      public ObjectIterator<Short2FloatMap.Entry> fastIterator() {
         return new ObjectIterator<Short2FloatMap.Entry>() {
            int next = 0;
            int curr = -1;
            final AbstractShort2FloatMap.BasicEntry entry = new AbstractShort2FloatMap.BasicEntry();

            @Override
            public boolean hasNext() {
               return this.next < Short2FloatArrayMap.this.size;
            }

            public Short2FloatMap.Entry next() {
               if (!this.hasNext()) {
                  throw new NoSuchElementException();
               } else {
                  this.entry.key = Short2FloatArrayMap.this.key[this.curr = this.next];
                  this.entry.value = Short2FloatArrayMap.this.value[this.next++];
                  return this.entry;
               }
            }

            @Override
            public void remove() {
               if (this.curr == -1) {
                  throw new IllegalStateException();
               } else {
                  this.curr = -1;
                  int tail = Short2FloatArrayMap.this.size-- - this.next--;
                  System.arraycopy(Short2FloatArrayMap.this.key, this.next + 1, Short2FloatArrayMap.this.key, this.next, tail);
                  System.arraycopy(Short2FloatArrayMap.this.value, this.next + 1, Short2FloatArrayMap.this.value, this.next, tail);
               }
            }

            @Override
            public void forEachRemaining(Consumer<? super Short2FloatMap.Entry> action) {
               int max = Short2FloatArrayMap.this.size;

               while (this.next < max) {
                  this.entry.key = Short2FloatArrayMap.this.key[this.curr = this.next];
                  this.entry.value = Short2FloatArrayMap.this.value[this.next++];
                  action.accept(this.entry);
               }
            }
         };
      }

      @Override
      public ObjectSpliterator<Short2FloatMap.Entry> spliterator() {
         return new Short2FloatArrayMap.EntrySet.EntrySetSpliterator(0, Short2FloatArrayMap.this.size);
      }

      @Override
      public void forEach(Consumer<? super Short2FloatMap.Entry> action) {
         int i = 0;

         for (int max = Short2FloatArrayMap.this.size; i < max; i++) {
            action.accept(new AbstractShort2FloatMap.BasicEntry(Short2FloatArrayMap.this.key[i], Short2FloatArrayMap.this.value[i]));
         }
      }

      @Override
      public void fastForEach(Consumer<? super Short2FloatMap.Entry> action) {
         AbstractShort2FloatMap.BasicEntry entry = new AbstractShort2FloatMap.BasicEntry();
         int i = 0;

         for (int max = Short2FloatArrayMap.this.size; i < max; i++) {
            entry.key = Short2FloatArrayMap.this.key[i];
            entry.value = Short2FloatArrayMap.this.value[i];
            action.accept(entry);
         }
      }

      @Override
      public int size() {
         return Short2FloatArrayMap.this.size;
      }

      @Override
      public boolean contains(Object o) {
         if (!(o instanceof Entry)) {
            return false;
         } else {
            Entry<?, ?> e = (Entry<?, ?>)o;
            if (e.getKey() == null || !(e.getKey() instanceof Short)) {
               return false;
            } else if (e.getValue() != null && e.getValue() instanceof Float) {
               short k = (Short)e.getKey();
               return Short2FloatArrayMap.this.containsKey(k)
                  && Float.floatToIntBits(Short2FloatArrayMap.this.get(k)) == Float.floatToIntBits((Float)e.getValue());
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
            if (e.getKey() == null || !(e.getKey() instanceof Short)) {
               return false;
            } else if (e.getValue() != null && e.getValue() instanceof Float) {
               short k = (Short)e.getKey();
               float v = (Float)e.getValue();
               int oldPos = Short2FloatArrayMap.this.findKey(k);
               if (oldPos != -1 && Float.floatToIntBits(v) == Float.floatToIntBits(Short2FloatArrayMap.this.value[oldPos])) {
                  int tail = Short2FloatArrayMap.this.size - oldPos - 1;
                  System.arraycopy(Short2FloatArrayMap.this.key, oldPos + 1, Short2FloatArrayMap.this.key, oldPos, tail);
                  System.arraycopy(Short2FloatArrayMap.this.value, oldPos + 1, Short2FloatArrayMap.this.value, oldPos, tail);
                  Short2FloatArrayMap.this.size--;
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
         extends ObjectSpliterators.EarlyBindingSizeIndexBasedSpliterator<Short2FloatMap.Entry>
         implements ObjectSpliterator<Short2FloatMap.Entry> {
         EntrySetSpliterator(int pos, int maxPos) {
            super(pos, maxPos);
         }

         @Override
         public int characteristics() {
            return 16465;
         }

         protected final Short2FloatMap.Entry get(int location) {
            return new AbstractShort2FloatMap.BasicEntry(Short2FloatArrayMap.this.key[location], Short2FloatArrayMap.this.value[location]);
         }

         protected final Short2FloatArrayMap.EntrySet.EntrySetSpliterator makeForSplit(int pos, int maxPos) {
            return EntrySet.this.new EntrySetSpliterator(pos, maxPos);
         }
      }
   }

   private final class KeySet extends AbstractShortSet {
      private KeySet() {
      }

      @Override
      public boolean contains(short k) {
         return Short2FloatArrayMap.this.findKey(k) != -1;
      }

      @Override
      public boolean remove(short k) {
         int oldPos = Short2FloatArrayMap.this.findKey(k);
         if (oldPos == -1) {
            return false;
         } else {
            int tail = Short2FloatArrayMap.this.size - oldPos - 1;
            System.arraycopy(Short2FloatArrayMap.this.key, oldPos + 1, Short2FloatArrayMap.this.key, oldPos, tail);
            System.arraycopy(Short2FloatArrayMap.this.value, oldPos + 1, Short2FloatArrayMap.this.value, oldPos, tail);
            Short2FloatArrayMap.this.size--;
            return true;
         }
      }

      @Override
      public ShortIterator iterator() {
         return new ShortIterator() {
            int pos = 0;

            @Override
            public boolean hasNext() {
               return this.pos < Short2FloatArrayMap.this.size;
            }

            @Override
            public short nextShort() {
               if (!this.hasNext()) {
                  throw new NoSuchElementException();
               } else {
                  return Short2FloatArrayMap.this.key[this.pos++];
               }
            }

            @Override
            public void remove() {
               if (this.pos == 0) {
                  throw new IllegalStateException();
               } else {
                  int tail = Short2FloatArrayMap.this.size - this.pos;
                  System.arraycopy(Short2FloatArrayMap.this.key, this.pos, Short2FloatArrayMap.this.key, this.pos - 1, tail);
                  System.arraycopy(Short2FloatArrayMap.this.value, this.pos, Short2FloatArrayMap.this.value, this.pos - 1, tail);
                  Short2FloatArrayMap.this.size--;
                  this.pos--;
               }
            }

            @Override
            public void forEachRemaining(ShortConsumer action) {
               int max = Short2FloatArrayMap.this.size;

               while (this.pos < max) {
                  action.accept(Short2FloatArrayMap.this.key[this.pos++]);
               }
            }
         };
      }

      @Override
      public ShortSpliterator spliterator() {
         return new Short2FloatArrayMap.KeySet.KeySetSpliterator(0, Short2FloatArrayMap.this.size);
      }

      @Override
      public void forEach(ShortConsumer action) {
         int i = 0;

         for (int max = Short2FloatArrayMap.this.size; i < max; i++) {
            action.accept(Short2FloatArrayMap.this.key[i]);
         }
      }

      @Override
      public int size() {
         return Short2FloatArrayMap.this.size;
      }

      @Override
      public void clear() {
         Short2FloatArrayMap.this.clear();
      }

      final class KeySetSpliterator extends ShortSpliterators.EarlyBindingSizeIndexBasedSpliterator implements ShortSpliterator {
         KeySetSpliterator(int pos, int maxPos) {
            super(pos, maxPos);
         }

         @Override
         public int characteristics() {
            return 16721;
         }

         @Override
         protected final short get(int location) {
            return Short2FloatArrayMap.this.key[location];
         }

         protected final Short2FloatArrayMap.KeySet.KeySetSpliterator makeForSplit(int pos, int maxPos) {
            return KeySet.this.new KeySetSpliterator(pos, maxPos);
         }

         @Override
         public void forEachRemaining(ShortConsumer action) {
            int max = Short2FloatArrayMap.this.size;

            while (this.pos < max) {
               action.accept(Short2FloatArrayMap.this.key[this.pos++]);
            }
         }
      }
   }

   private final class ValuesCollection extends AbstractFloatCollection {
      private ValuesCollection() {
      }

      @Override
      public boolean contains(float v) {
         return Short2FloatArrayMap.this.containsValue(v);
      }

      @Override
      public FloatIterator iterator() {
         return new FloatIterator() {
            int pos = 0;

            @Override
            public boolean hasNext() {
               return this.pos < Short2FloatArrayMap.this.size;
            }

            @Override
            public float nextFloat() {
               if (!this.hasNext()) {
                  throw new NoSuchElementException();
               } else {
                  return Short2FloatArrayMap.this.value[this.pos++];
               }
            }

            @Override
            public void remove() {
               if (this.pos == 0) {
                  throw new IllegalStateException();
               } else {
                  int tail = Short2FloatArrayMap.this.size - this.pos;
                  System.arraycopy(Short2FloatArrayMap.this.key, this.pos, Short2FloatArrayMap.this.key, this.pos - 1, tail);
                  System.arraycopy(Short2FloatArrayMap.this.value, this.pos, Short2FloatArrayMap.this.value, this.pos - 1, tail);
                  Short2FloatArrayMap.this.size--;
                  this.pos--;
               }
            }

            @Override
            public void forEachRemaining(FloatConsumer action) {
               int max = Short2FloatArrayMap.this.size;

               while (this.pos < max) {
                  action.accept(Short2FloatArrayMap.this.value[this.pos++]);
               }
            }
         };
      }

      @Override
      public FloatSpliterator spliterator() {
         return new Short2FloatArrayMap.ValuesCollection.ValuesSpliterator(0, Short2FloatArrayMap.this.size);
      }

      @Override
      public void forEach(FloatConsumer action) {
         int i = 0;

         for (int max = Short2FloatArrayMap.this.size; i < max; i++) {
            action.accept(Short2FloatArrayMap.this.value[i]);
         }
      }

      @Override
      public int size() {
         return Short2FloatArrayMap.this.size;
      }

      @Override
      public void clear() {
         Short2FloatArrayMap.this.clear();
      }

      final class ValuesSpliterator extends FloatSpliterators.EarlyBindingSizeIndexBasedSpliterator implements FloatSpliterator {
         ValuesSpliterator(int pos, int maxPos) {
            super(pos, maxPos);
         }

         @Override
         public int characteristics() {
            return 16720;
         }

         @Override
         protected final float get(int location) {
            return Short2FloatArrayMap.this.value[location];
         }

         protected final Short2FloatArrayMap.ValuesCollection.ValuesSpliterator makeForSplit(int pos, int maxPos) {
            return ValuesCollection.this.new ValuesSpliterator(pos, maxPos);
         }

         @Override
         public void forEachRemaining(FloatConsumer action) {
            int max = Short2FloatArrayMap.this.size;

            while (this.pos < max) {
               action.accept(Short2FloatArrayMap.this.value[this.pos++]);
            }
         }
      }
   }
}
