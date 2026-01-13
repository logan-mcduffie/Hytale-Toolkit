package it.unimi.dsi.fastutil.floats;

import it.unimi.dsi.fastutil.doubles.AbstractDoubleCollection;
import it.unimi.dsi.fastutil.doubles.DoubleArrays;
import it.unimi.dsi.fastutil.doubles.DoubleCollection;
import it.unimi.dsi.fastutil.doubles.DoubleIterator;
import it.unimi.dsi.fastutil.doubles.DoubleSpliterator;
import it.unimi.dsi.fastutil.doubles.DoubleSpliterators;
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
import java.util.function.DoubleConsumer;

public class Float2DoubleArrayMap extends AbstractFloat2DoubleMap implements Serializable, Cloneable {
   private static final long serialVersionUID = 1L;
   protected transient float[] key;
   protected transient double[] value;
   protected int size;
   protected transient Float2DoubleMap.FastEntrySet entries;
   protected transient FloatSet keys;
   protected transient DoubleCollection values;

   public Float2DoubleArrayMap(float[] key, double[] value) {
      this.key = key;
      this.value = value;
      this.size = key.length;
      if (key.length != value.length) {
         throw new IllegalArgumentException("Keys and values have different lengths (" + key.length + ", " + value.length + ")");
      }
   }

   public Float2DoubleArrayMap() {
      this.key = FloatArrays.EMPTY_ARRAY;
      this.value = DoubleArrays.EMPTY_ARRAY;
   }

   public Float2DoubleArrayMap(int capacity) {
      this.key = new float[capacity];
      this.value = new double[capacity];
   }

   public Float2DoubleArrayMap(Float2DoubleMap m) {
      this(m.size());
      int i = 0;

      for (Float2DoubleMap.Entry e : m.float2DoubleEntrySet()) {
         this.key[i] = e.getFloatKey();
         this.value[i] = e.getDoubleValue();
         i++;
      }

      this.size = i;
   }

   public Float2DoubleArrayMap(Map<? extends Float, ? extends Double> m) {
      this(m.size());
      int i = 0;

      for (Entry<? extends Float, ? extends Double> e : m.entrySet()) {
         this.key[i] = e.getKey();
         this.value[i] = e.getValue();
         i++;
      }

      this.size = i;
   }

   public Float2DoubleArrayMap(float[] key, double[] value, int size) {
      this.key = key;
      this.value = value;
      this.size = size;
      if (key.length != value.length) {
         throw new IllegalArgumentException("Keys and values have different lengths (" + key.length + ", " + value.length + ")");
      } else if (size > key.length) {
         throw new IllegalArgumentException("The provided size (" + size + ") is larger than or equal to the backing-arrays size (" + key.length + ")");
      }
   }

   public Float2DoubleMap.FastEntrySet float2DoubleEntrySet() {
      if (this.entries == null) {
         this.entries = new Float2DoubleArrayMap.EntrySet();
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
   public double get(float k) {
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
   public boolean containsValue(double v) {
      int i = this.size;

      while (i-- != 0) {
         if (Double.doubleToLongBits(this.value[i]) == Double.doubleToLongBits(v)) {
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
   public double put(float k, double v) {
      int oldKey = this.findKey(k);
      if (oldKey != -1) {
         double oldValue = this.value[oldKey];
         this.value[oldKey] = v;
         return oldValue;
      } else {
         if (this.size == this.key.length) {
            float[] newKey = new float[this.size == 0 ? 2 : this.size * 2];
            double[] newValue = new double[this.size == 0 ? 2 : this.size * 2];

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
   public double remove(float k) {
      int oldPos = this.findKey(k);
      if (oldPos == -1) {
         return this.defRetValue;
      } else {
         double oldValue = this.value[oldPos];
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
         this.keys = new Float2DoubleArrayMap.KeySet();
      }

      return this.keys;
   }

   @Override
   public DoubleCollection values() {
      if (this.values == null) {
         this.values = new Float2DoubleArrayMap.ValuesCollection();
      }

      return this.values;
   }

   public Float2DoubleArrayMap clone() {
      Float2DoubleArrayMap c;
      try {
         c = (Float2DoubleArrayMap)super.clone();
      } catch (CloneNotSupportedException var3) {
         throw new InternalError();
      }

      c.key = (float[])this.key.clone();
      c.value = (double[])this.value.clone();
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
         s.writeDouble(this.value[i]);
      }
   }

   private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
      s.defaultReadObject();
      this.key = new float[this.size];
      this.value = new double[this.size];

      for (int i = 0; i < this.size; i++) {
         this.key[i] = s.readFloat();
         this.value[i] = s.readDouble();
      }
   }

   private final class EntrySet extends AbstractObjectSet<Float2DoubleMap.Entry> implements Float2DoubleMap.FastEntrySet {
      private EntrySet() {
      }

      @Override
      public ObjectIterator<Float2DoubleMap.Entry> iterator() {
         return new ObjectIterator<Float2DoubleMap.Entry>() {
            int curr = -1;
            int next = 0;

            @Override
            public boolean hasNext() {
               return this.next < Float2DoubleArrayMap.this.size;
            }

            public Float2DoubleMap.Entry next() {
               if (!this.hasNext()) {
                  throw new NoSuchElementException();
               } else {
                  return new AbstractFloat2DoubleMap.BasicEntry(
                     Float2DoubleArrayMap.this.key[this.curr = this.next], Float2DoubleArrayMap.this.value[this.next++]
                  );
               }
            }

            @Override
            public void remove() {
               if (this.curr == -1) {
                  throw new IllegalStateException();
               } else {
                  this.curr = -1;
                  int tail = Float2DoubleArrayMap.this.size-- - this.next--;
                  System.arraycopy(Float2DoubleArrayMap.this.key, this.next + 1, Float2DoubleArrayMap.this.key, this.next, tail);
                  System.arraycopy(Float2DoubleArrayMap.this.value, this.next + 1, Float2DoubleArrayMap.this.value, this.next, tail);
               }
            }

            @Override
            public void forEachRemaining(Consumer<? super Float2DoubleMap.Entry> action) {
               int max = Float2DoubleArrayMap.this.size;

               while (this.next < max) {
                  action.accept(
                     new AbstractFloat2DoubleMap.BasicEntry(Float2DoubleArrayMap.this.key[this.curr = this.next], Float2DoubleArrayMap.this.value[this.next++])
                  );
               }
            }
         };
      }

      @Override
      public ObjectIterator<Float2DoubleMap.Entry> fastIterator() {
         return new ObjectIterator<Float2DoubleMap.Entry>() {
            int next = 0;
            int curr = -1;
            final AbstractFloat2DoubleMap.BasicEntry entry = new AbstractFloat2DoubleMap.BasicEntry();

            @Override
            public boolean hasNext() {
               return this.next < Float2DoubleArrayMap.this.size;
            }

            public Float2DoubleMap.Entry next() {
               if (!this.hasNext()) {
                  throw new NoSuchElementException();
               } else {
                  this.entry.key = Float2DoubleArrayMap.this.key[this.curr = this.next];
                  this.entry.value = Float2DoubleArrayMap.this.value[this.next++];
                  return this.entry;
               }
            }

            @Override
            public void remove() {
               if (this.curr == -1) {
                  throw new IllegalStateException();
               } else {
                  this.curr = -1;
                  int tail = Float2DoubleArrayMap.this.size-- - this.next--;
                  System.arraycopy(Float2DoubleArrayMap.this.key, this.next + 1, Float2DoubleArrayMap.this.key, this.next, tail);
                  System.arraycopy(Float2DoubleArrayMap.this.value, this.next + 1, Float2DoubleArrayMap.this.value, this.next, tail);
               }
            }

            @Override
            public void forEachRemaining(Consumer<? super Float2DoubleMap.Entry> action) {
               int max = Float2DoubleArrayMap.this.size;

               while (this.next < max) {
                  this.entry.key = Float2DoubleArrayMap.this.key[this.curr = this.next];
                  this.entry.value = Float2DoubleArrayMap.this.value[this.next++];
                  action.accept(this.entry);
               }
            }
         };
      }

      @Override
      public ObjectSpliterator<Float2DoubleMap.Entry> spliterator() {
         return new Float2DoubleArrayMap.EntrySet.EntrySetSpliterator(0, Float2DoubleArrayMap.this.size);
      }

      @Override
      public void forEach(Consumer<? super Float2DoubleMap.Entry> action) {
         int i = 0;

         for (int max = Float2DoubleArrayMap.this.size; i < max; i++) {
            action.accept(new AbstractFloat2DoubleMap.BasicEntry(Float2DoubleArrayMap.this.key[i], Float2DoubleArrayMap.this.value[i]));
         }
      }

      @Override
      public void fastForEach(Consumer<? super Float2DoubleMap.Entry> action) {
         AbstractFloat2DoubleMap.BasicEntry entry = new AbstractFloat2DoubleMap.BasicEntry();
         int i = 0;

         for (int max = Float2DoubleArrayMap.this.size; i < max; i++) {
            entry.key = Float2DoubleArrayMap.this.key[i];
            entry.value = Float2DoubleArrayMap.this.value[i];
            action.accept(entry);
         }
      }

      @Override
      public int size() {
         return Float2DoubleArrayMap.this.size;
      }

      @Override
      public boolean contains(Object o) {
         if (!(o instanceof Entry)) {
            return false;
         } else {
            Entry<?, ?> e = (Entry<?, ?>)o;
            if (e.getKey() == null || !(e.getKey() instanceof Float)) {
               return false;
            } else if (e.getValue() != null && e.getValue() instanceof Double) {
               float k = (Float)e.getKey();
               return Float2DoubleArrayMap.this.containsKey(k)
                  && Double.doubleToLongBits(Float2DoubleArrayMap.this.get(k)) == Double.doubleToLongBits((Double)e.getValue());
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
            } else if (e.getValue() != null && e.getValue() instanceof Double) {
               float k = (Float)e.getKey();
               double v = (Double)e.getValue();
               int oldPos = Float2DoubleArrayMap.this.findKey(k);
               if (oldPos != -1 && Double.doubleToLongBits(v) == Double.doubleToLongBits(Float2DoubleArrayMap.this.value[oldPos])) {
                  int tail = Float2DoubleArrayMap.this.size - oldPos - 1;
                  System.arraycopy(Float2DoubleArrayMap.this.key, oldPos + 1, Float2DoubleArrayMap.this.key, oldPos, tail);
                  System.arraycopy(Float2DoubleArrayMap.this.value, oldPos + 1, Float2DoubleArrayMap.this.value, oldPos, tail);
                  Float2DoubleArrayMap.this.size--;
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
         extends ObjectSpliterators.EarlyBindingSizeIndexBasedSpliterator<Float2DoubleMap.Entry>
         implements ObjectSpliterator<Float2DoubleMap.Entry> {
         EntrySetSpliterator(int pos, int maxPos) {
            super(pos, maxPos);
         }

         @Override
         public int characteristics() {
            return 16465;
         }

         protected final Float2DoubleMap.Entry get(int location) {
            return new AbstractFloat2DoubleMap.BasicEntry(Float2DoubleArrayMap.this.key[location], Float2DoubleArrayMap.this.value[location]);
         }

         protected final Float2DoubleArrayMap.EntrySet.EntrySetSpliterator makeForSplit(int pos, int maxPos) {
            return EntrySet.this.new EntrySetSpliterator(pos, maxPos);
         }
      }
   }

   private final class KeySet extends AbstractFloatSet {
      private KeySet() {
      }

      @Override
      public boolean contains(float k) {
         return Float2DoubleArrayMap.this.findKey(k) != -1;
      }

      @Override
      public boolean remove(float k) {
         int oldPos = Float2DoubleArrayMap.this.findKey(k);
         if (oldPos == -1) {
            return false;
         } else {
            int tail = Float2DoubleArrayMap.this.size - oldPos - 1;
            System.arraycopy(Float2DoubleArrayMap.this.key, oldPos + 1, Float2DoubleArrayMap.this.key, oldPos, tail);
            System.arraycopy(Float2DoubleArrayMap.this.value, oldPos + 1, Float2DoubleArrayMap.this.value, oldPos, tail);
            Float2DoubleArrayMap.this.size--;
            return true;
         }
      }

      @Override
      public FloatIterator iterator() {
         return new FloatIterator() {
            int pos = 0;

            @Override
            public boolean hasNext() {
               return this.pos < Float2DoubleArrayMap.this.size;
            }

            @Override
            public float nextFloat() {
               if (!this.hasNext()) {
                  throw new NoSuchElementException();
               } else {
                  return Float2DoubleArrayMap.this.key[this.pos++];
               }
            }

            @Override
            public void remove() {
               if (this.pos == 0) {
                  throw new IllegalStateException();
               } else {
                  int tail = Float2DoubleArrayMap.this.size - this.pos;
                  System.arraycopy(Float2DoubleArrayMap.this.key, this.pos, Float2DoubleArrayMap.this.key, this.pos - 1, tail);
                  System.arraycopy(Float2DoubleArrayMap.this.value, this.pos, Float2DoubleArrayMap.this.value, this.pos - 1, tail);
                  Float2DoubleArrayMap.this.size--;
                  this.pos--;
               }
            }

            @Override
            public void forEachRemaining(FloatConsumer action) {
               int max = Float2DoubleArrayMap.this.size;

               while (this.pos < max) {
                  action.accept(Float2DoubleArrayMap.this.key[this.pos++]);
               }
            }
         };
      }

      @Override
      public FloatSpliterator spliterator() {
         return new Float2DoubleArrayMap.KeySet.KeySetSpliterator(0, Float2DoubleArrayMap.this.size);
      }

      @Override
      public void forEach(FloatConsumer action) {
         int i = 0;

         for (int max = Float2DoubleArrayMap.this.size; i < max; i++) {
            action.accept(Float2DoubleArrayMap.this.key[i]);
         }
      }

      @Override
      public int size() {
         return Float2DoubleArrayMap.this.size;
      }

      @Override
      public void clear() {
         Float2DoubleArrayMap.this.clear();
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
            return Float2DoubleArrayMap.this.key[location];
         }

         protected final Float2DoubleArrayMap.KeySet.KeySetSpliterator makeForSplit(int pos, int maxPos) {
            return KeySet.this.new KeySetSpliterator(pos, maxPos);
         }

         @Override
         public void forEachRemaining(FloatConsumer action) {
            int max = Float2DoubleArrayMap.this.size;

            while (this.pos < max) {
               action.accept(Float2DoubleArrayMap.this.key[this.pos++]);
            }
         }
      }
   }

   private final class ValuesCollection extends AbstractDoubleCollection {
      private ValuesCollection() {
      }

      @Override
      public boolean contains(double v) {
         return Float2DoubleArrayMap.this.containsValue(v);
      }

      @Override
      public DoubleIterator iterator() {
         return new DoubleIterator() {
            int pos = 0;

            @Override
            public boolean hasNext() {
               return this.pos < Float2DoubleArrayMap.this.size;
            }

            @Override
            public double nextDouble() {
               if (!this.hasNext()) {
                  throw new NoSuchElementException();
               } else {
                  return Float2DoubleArrayMap.this.value[this.pos++];
               }
            }

            @Override
            public void remove() {
               if (this.pos == 0) {
                  throw new IllegalStateException();
               } else {
                  int tail = Float2DoubleArrayMap.this.size - this.pos;
                  System.arraycopy(Float2DoubleArrayMap.this.key, this.pos, Float2DoubleArrayMap.this.key, this.pos - 1, tail);
                  System.arraycopy(Float2DoubleArrayMap.this.value, this.pos, Float2DoubleArrayMap.this.value, this.pos - 1, tail);
                  Float2DoubleArrayMap.this.size--;
                  this.pos--;
               }
            }

            @Override
            public void forEachRemaining(DoubleConsumer action) {
               int max = Float2DoubleArrayMap.this.size;

               while (this.pos < max) {
                  action.accept(Float2DoubleArrayMap.this.value[this.pos++]);
               }
            }
         };
      }

      @Override
      public DoubleSpliterator spliterator() {
         return new Float2DoubleArrayMap.ValuesCollection.ValuesSpliterator(0, Float2DoubleArrayMap.this.size);
      }

      @Override
      public void forEach(DoubleConsumer action) {
         int i = 0;

         for (int max = Float2DoubleArrayMap.this.size; i < max; i++) {
            action.accept(Float2DoubleArrayMap.this.value[i]);
         }
      }

      @Override
      public int size() {
         return Float2DoubleArrayMap.this.size;
      }

      @Override
      public void clear() {
         Float2DoubleArrayMap.this.clear();
      }

      final class ValuesSpliterator extends DoubleSpliterators.EarlyBindingSizeIndexBasedSpliterator implements DoubleSpliterator {
         ValuesSpliterator(int pos, int maxPos) {
            super(pos, maxPos);
         }

         @Override
         public int characteristics() {
            return 16720;
         }

         @Override
         protected final double get(int location) {
            return Float2DoubleArrayMap.this.value[location];
         }

         protected final Float2DoubleArrayMap.ValuesCollection.ValuesSpliterator makeForSplit(int pos, int maxPos) {
            return ValuesCollection.this.new ValuesSpliterator(pos, maxPos);
         }

         @Override
         public void forEachRemaining(DoubleConsumer action) {
            int max = Float2DoubleArrayMap.this.size;

            while (this.pos < max) {
               action.accept(Float2DoubleArrayMap.this.value[this.pos++]);
            }
         }
      }
   }
}
