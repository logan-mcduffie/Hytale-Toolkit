package it.unimi.dsi.fastutil.floats;

import it.unimi.dsi.fastutil.ints.AbstractIntCollection;
import it.unimi.dsi.fastutil.ints.IntArrays;
import it.unimi.dsi.fastutil.ints.IntCollection;
import it.unimi.dsi.fastutil.ints.IntIterator;
import it.unimi.dsi.fastutil.ints.IntSpliterator;
import it.unimi.dsi.fastutil.ints.IntSpliterators;
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
import java.util.function.IntConsumer;

public class Float2IntArrayMap extends AbstractFloat2IntMap implements Serializable, Cloneable {
   private static final long serialVersionUID = 1L;
   protected transient float[] key;
   protected transient int[] value;
   protected int size;
   protected transient Float2IntMap.FastEntrySet entries;
   protected transient FloatSet keys;
   protected transient IntCollection values;

   public Float2IntArrayMap(float[] key, int[] value) {
      this.key = key;
      this.value = value;
      this.size = key.length;
      if (key.length != value.length) {
         throw new IllegalArgumentException("Keys and values have different lengths (" + key.length + ", " + value.length + ")");
      }
   }

   public Float2IntArrayMap() {
      this.key = FloatArrays.EMPTY_ARRAY;
      this.value = IntArrays.EMPTY_ARRAY;
   }

   public Float2IntArrayMap(int capacity) {
      this.key = new float[capacity];
      this.value = new int[capacity];
   }

   public Float2IntArrayMap(Float2IntMap m) {
      this(m.size());
      int i = 0;

      for (Float2IntMap.Entry e : m.float2IntEntrySet()) {
         this.key[i] = e.getFloatKey();
         this.value[i] = e.getIntValue();
         i++;
      }

      this.size = i;
   }

   public Float2IntArrayMap(Map<? extends Float, ? extends Integer> m) {
      this(m.size());
      int i = 0;

      for (Entry<? extends Float, ? extends Integer> e : m.entrySet()) {
         this.key[i] = e.getKey();
         this.value[i] = e.getValue();
         i++;
      }

      this.size = i;
   }

   public Float2IntArrayMap(float[] key, int[] value, int size) {
      this.key = key;
      this.value = value;
      this.size = size;
      if (key.length != value.length) {
         throw new IllegalArgumentException("Keys and values have different lengths (" + key.length + ", " + value.length + ")");
      } else if (size > key.length) {
         throw new IllegalArgumentException("The provided size (" + size + ") is larger than or equal to the backing-arrays size (" + key.length + ")");
      }
   }

   public Float2IntMap.FastEntrySet float2IntEntrySet() {
      if (this.entries == null) {
         this.entries = new Float2IntArrayMap.EntrySet();
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
   public int get(float k) {
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
   public boolean containsValue(int v) {
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
   public int put(float k, int v) {
      int oldKey = this.findKey(k);
      if (oldKey != -1) {
         int oldValue = this.value[oldKey];
         this.value[oldKey] = v;
         return oldValue;
      } else {
         if (this.size == this.key.length) {
            float[] newKey = new float[this.size == 0 ? 2 : this.size * 2];
            int[] newValue = new int[this.size == 0 ? 2 : this.size * 2];

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
   public int remove(float k) {
      int oldPos = this.findKey(k);
      if (oldPos == -1) {
         return this.defRetValue;
      } else {
         int oldValue = this.value[oldPos];
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
         this.keys = new Float2IntArrayMap.KeySet();
      }

      return this.keys;
   }

   @Override
   public IntCollection values() {
      if (this.values == null) {
         this.values = new Float2IntArrayMap.ValuesCollection();
      }

      return this.values;
   }

   public Float2IntArrayMap clone() {
      Float2IntArrayMap c;
      try {
         c = (Float2IntArrayMap)super.clone();
      } catch (CloneNotSupportedException var3) {
         throw new InternalError();
      }

      c.key = (float[])this.key.clone();
      c.value = (int[])this.value.clone();
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
         s.writeInt(this.value[i]);
      }
   }

   private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
      s.defaultReadObject();
      this.key = new float[this.size];
      this.value = new int[this.size];

      for (int i = 0; i < this.size; i++) {
         this.key[i] = s.readFloat();
         this.value[i] = s.readInt();
      }
   }

   private final class EntrySet extends AbstractObjectSet<Float2IntMap.Entry> implements Float2IntMap.FastEntrySet {
      private EntrySet() {
      }

      @Override
      public ObjectIterator<Float2IntMap.Entry> iterator() {
         return new ObjectIterator<Float2IntMap.Entry>() {
            int curr = -1;
            int next = 0;

            @Override
            public boolean hasNext() {
               return this.next < Float2IntArrayMap.this.size;
            }

            public Float2IntMap.Entry next() {
               if (!this.hasNext()) {
                  throw new NoSuchElementException();
               } else {
                  return new AbstractFloat2IntMap.BasicEntry(Float2IntArrayMap.this.key[this.curr = this.next], Float2IntArrayMap.this.value[this.next++]);
               }
            }

            @Override
            public void remove() {
               if (this.curr == -1) {
                  throw new IllegalStateException();
               } else {
                  this.curr = -1;
                  int tail = Float2IntArrayMap.this.size-- - this.next--;
                  System.arraycopy(Float2IntArrayMap.this.key, this.next + 1, Float2IntArrayMap.this.key, this.next, tail);
                  System.arraycopy(Float2IntArrayMap.this.value, this.next + 1, Float2IntArrayMap.this.value, this.next, tail);
               }
            }

            @Override
            public void forEachRemaining(Consumer<? super Float2IntMap.Entry> action) {
               int max = Float2IntArrayMap.this.size;

               while (this.next < max) {
                  action.accept(
                     new AbstractFloat2IntMap.BasicEntry(Float2IntArrayMap.this.key[this.curr = this.next], Float2IntArrayMap.this.value[this.next++])
                  );
               }
            }
         };
      }

      @Override
      public ObjectIterator<Float2IntMap.Entry> fastIterator() {
         return new ObjectIterator<Float2IntMap.Entry>() {
            int next = 0;
            int curr = -1;
            final AbstractFloat2IntMap.BasicEntry entry = new AbstractFloat2IntMap.BasicEntry();

            @Override
            public boolean hasNext() {
               return this.next < Float2IntArrayMap.this.size;
            }

            public Float2IntMap.Entry next() {
               if (!this.hasNext()) {
                  throw new NoSuchElementException();
               } else {
                  this.entry.key = Float2IntArrayMap.this.key[this.curr = this.next];
                  this.entry.value = Float2IntArrayMap.this.value[this.next++];
                  return this.entry;
               }
            }

            @Override
            public void remove() {
               if (this.curr == -1) {
                  throw new IllegalStateException();
               } else {
                  this.curr = -1;
                  int tail = Float2IntArrayMap.this.size-- - this.next--;
                  System.arraycopy(Float2IntArrayMap.this.key, this.next + 1, Float2IntArrayMap.this.key, this.next, tail);
                  System.arraycopy(Float2IntArrayMap.this.value, this.next + 1, Float2IntArrayMap.this.value, this.next, tail);
               }
            }

            @Override
            public void forEachRemaining(Consumer<? super Float2IntMap.Entry> action) {
               int max = Float2IntArrayMap.this.size;

               while (this.next < max) {
                  this.entry.key = Float2IntArrayMap.this.key[this.curr = this.next];
                  this.entry.value = Float2IntArrayMap.this.value[this.next++];
                  action.accept(this.entry);
               }
            }
         };
      }

      @Override
      public ObjectSpliterator<Float2IntMap.Entry> spliterator() {
         return new Float2IntArrayMap.EntrySet.EntrySetSpliterator(0, Float2IntArrayMap.this.size);
      }

      @Override
      public void forEach(Consumer<? super Float2IntMap.Entry> action) {
         int i = 0;

         for (int max = Float2IntArrayMap.this.size; i < max; i++) {
            action.accept(new AbstractFloat2IntMap.BasicEntry(Float2IntArrayMap.this.key[i], Float2IntArrayMap.this.value[i]));
         }
      }

      @Override
      public void fastForEach(Consumer<? super Float2IntMap.Entry> action) {
         AbstractFloat2IntMap.BasicEntry entry = new AbstractFloat2IntMap.BasicEntry();
         int i = 0;

         for (int max = Float2IntArrayMap.this.size; i < max; i++) {
            entry.key = Float2IntArrayMap.this.key[i];
            entry.value = Float2IntArrayMap.this.value[i];
            action.accept(entry);
         }
      }

      @Override
      public int size() {
         return Float2IntArrayMap.this.size;
      }

      @Override
      public boolean contains(Object o) {
         if (!(o instanceof Entry)) {
            return false;
         } else {
            Entry<?, ?> e = (Entry<?, ?>)o;
            if (e.getKey() == null || !(e.getKey() instanceof Float)) {
               return false;
            } else if (e.getValue() != null && e.getValue() instanceof Integer) {
               float k = (Float)e.getKey();
               return Float2IntArrayMap.this.containsKey(k) && Float2IntArrayMap.this.get(k) == (Integer)e.getValue();
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
            } else if (e.getValue() != null && e.getValue() instanceof Integer) {
               float k = (Float)e.getKey();
               int v = (Integer)e.getValue();
               int oldPos = Float2IntArrayMap.this.findKey(k);
               if (oldPos != -1 && v == Float2IntArrayMap.this.value[oldPos]) {
                  int tail = Float2IntArrayMap.this.size - oldPos - 1;
                  System.arraycopy(Float2IntArrayMap.this.key, oldPos + 1, Float2IntArrayMap.this.key, oldPos, tail);
                  System.arraycopy(Float2IntArrayMap.this.value, oldPos + 1, Float2IntArrayMap.this.value, oldPos, tail);
                  Float2IntArrayMap.this.size--;
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
         extends ObjectSpliterators.EarlyBindingSizeIndexBasedSpliterator<Float2IntMap.Entry>
         implements ObjectSpliterator<Float2IntMap.Entry> {
         EntrySetSpliterator(int pos, int maxPos) {
            super(pos, maxPos);
         }

         @Override
         public int characteristics() {
            return 16465;
         }

         protected final Float2IntMap.Entry get(int location) {
            return new AbstractFloat2IntMap.BasicEntry(Float2IntArrayMap.this.key[location], Float2IntArrayMap.this.value[location]);
         }

         protected final Float2IntArrayMap.EntrySet.EntrySetSpliterator makeForSplit(int pos, int maxPos) {
            return EntrySet.this.new EntrySetSpliterator(pos, maxPos);
         }
      }
   }

   private final class KeySet extends AbstractFloatSet {
      private KeySet() {
      }

      @Override
      public boolean contains(float k) {
         return Float2IntArrayMap.this.findKey(k) != -1;
      }

      @Override
      public boolean remove(float k) {
         int oldPos = Float2IntArrayMap.this.findKey(k);
         if (oldPos == -1) {
            return false;
         } else {
            int tail = Float2IntArrayMap.this.size - oldPos - 1;
            System.arraycopy(Float2IntArrayMap.this.key, oldPos + 1, Float2IntArrayMap.this.key, oldPos, tail);
            System.arraycopy(Float2IntArrayMap.this.value, oldPos + 1, Float2IntArrayMap.this.value, oldPos, tail);
            Float2IntArrayMap.this.size--;
            return true;
         }
      }

      @Override
      public FloatIterator iterator() {
         return new FloatIterator() {
            int pos = 0;

            @Override
            public boolean hasNext() {
               return this.pos < Float2IntArrayMap.this.size;
            }

            @Override
            public float nextFloat() {
               if (!this.hasNext()) {
                  throw new NoSuchElementException();
               } else {
                  return Float2IntArrayMap.this.key[this.pos++];
               }
            }

            @Override
            public void remove() {
               if (this.pos == 0) {
                  throw new IllegalStateException();
               } else {
                  int tail = Float2IntArrayMap.this.size - this.pos;
                  System.arraycopy(Float2IntArrayMap.this.key, this.pos, Float2IntArrayMap.this.key, this.pos - 1, tail);
                  System.arraycopy(Float2IntArrayMap.this.value, this.pos, Float2IntArrayMap.this.value, this.pos - 1, tail);
                  Float2IntArrayMap.this.size--;
                  this.pos--;
               }
            }

            @Override
            public void forEachRemaining(FloatConsumer action) {
               int max = Float2IntArrayMap.this.size;

               while (this.pos < max) {
                  action.accept(Float2IntArrayMap.this.key[this.pos++]);
               }
            }
         };
      }

      @Override
      public FloatSpliterator spliterator() {
         return new Float2IntArrayMap.KeySet.KeySetSpliterator(0, Float2IntArrayMap.this.size);
      }

      @Override
      public void forEach(FloatConsumer action) {
         int i = 0;

         for (int max = Float2IntArrayMap.this.size; i < max; i++) {
            action.accept(Float2IntArrayMap.this.key[i]);
         }
      }

      @Override
      public int size() {
         return Float2IntArrayMap.this.size;
      }

      @Override
      public void clear() {
         Float2IntArrayMap.this.clear();
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
            return Float2IntArrayMap.this.key[location];
         }

         protected final Float2IntArrayMap.KeySet.KeySetSpliterator makeForSplit(int pos, int maxPos) {
            return KeySet.this.new KeySetSpliterator(pos, maxPos);
         }

         @Override
         public void forEachRemaining(FloatConsumer action) {
            int max = Float2IntArrayMap.this.size;

            while (this.pos < max) {
               action.accept(Float2IntArrayMap.this.key[this.pos++]);
            }
         }
      }
   }

   private final class ValuesCollection extends AbstractIntCollection {
      private ValuesCollection() {
      }

      @Override
      public boolean contains(int v) {
         return Float2IntArrayMap.this.containsValue(v);
      }

      @Override
      public IntIterator iterator() {
         return new IntIterator() {
            int pos = 0;

            @Override
            public boolean hasNext() {
               return this.pos < Float2IntArrayMap.this.size;
            }

            @Override
            public int nextInt() {
               if (!this.hasNext()) {
                  throw new NoSuchElementException();
               } else {
                  return Float2IntArrayMap.this.value[this.pos++];
               }
            }

            @Override
            public void remove() {
               if (this.pos == 0) {
                  throw new IllegalStateException();
               } else {
                  int tail = Float2IntArrayMap.this.size - this.pos;
                  System.arraycopy(Float2IntArrayMap.this.key, this.pos, Float2IntArrayMap.this.key, this.pos - 1, tail);
                  System.arraycopy(Float2IntArrayMap.this.value, this.pos, Float2IntArrayMap.this.value, this.pos - 1, tail);
                  Float2IntArrayMap.this.size--;
                  this.pos--;
               }
            }

            @Override
            public void forEachRemaining(IntConsumer action) {
               int max = Float2IntArrayMap.this.size;

               while (this.pos < max) {
                  action.accept(Float2IntArrayMap.this.value[this.pos++]);
               }
            }
         };
      }

      @Override
      public IntSpliterator spliterator() {
         return new Float2IntArrayMap.ValuesCollection.ValuesSpliterator(0, Float2IntArrayMap.this.size);
      }

      @Override
      public void forEach(IntConsumer action) {
         int i = 0;

         for (int max = Float2IntArrayMap.this.size; i < max; i++) {
            action.accept(Float2IntArrayMap.this.value[i]);
         }
      }

      @Override
      public int size() {
         return Float2IntArrayMap.this.size;
      }

      @Override
      public void clear() {
         Float2IntArrayMap.this.clear();
      }

      final class ValuesSpliterator extends IntSpliterators.EarlyBindingSizeIndexBasedSpliterator implements IntSpliterator {
         ValuesSpliterator(int pos, int maxPos) {
            super(pos, maxPos);
         }

         @Override
         public int characteristics() {
            return 16720;
         }

         @Override
         protected final int get(int location) {
            return Float2IntArrayMap.this.value[location];
         }

         protected final Float2IntArrayMap.ValuesCollection.ValuesSpliterator makeForSplit(int pos, int maxPos) {
            return ValuesCollection.this.new ValuesSpliterator(pos, maxPos);
         }

         @Override
         public void forEachRemaining(IntConsumer action) {
            int max = Float2IntArrayMap.this.size;

            while (this.pos < max) {
               action.accept(Float2IntArrayMap.this.value[this.pos++]);
            }
         }
      }
   }
}
