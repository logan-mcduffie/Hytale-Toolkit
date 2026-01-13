package it.unimi.dsi.fastutil.floats;

import it.unimi.dsi.fastutil.objects.AbstractObjectSet;
import it.unimi.dsi.fastutil.objects.AbstractReferenceCollection;
import it.unimi.dsi.fastutil.objects.ObjectArrays;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectSpliterator;
import it.unimi.dsi.fastutil.objects.ObjectSpliterators;
import it.unimi.dsi.fastutil.objects.ReferenceCollection;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Map.Entry;
import java.util.function.Consumer;

public class Float2ReferenceArrayMap<V> extends AbstractFloat2ReferenceMap<V> implements Serializable, Cloneable {
   private static final long serialVersionUID = 1L;
   protected transient float[] key;
   protected transient Object[] value;
   protected int size;
   protected transient Float2ReferenceMap.FastEntrySet<V> entries;
   protected transient FloatSet keys;
   protected transient ReferenceCollection<V> values;

   public Float2ReferenceArrayMap(float[] key, Object[] value) {
      this.key = key;
      this.value = value;
      this.size = key.length;
      if (key.length != value.length) {
         throw new IllegalArgumentException("Keys and values have different lengths (" + key.length + ", " + value.length + ")");
      }
   }

   public Float2ReferenceArrayMap() {
      this.key = FloatArrays.EMPTY_ARRAY;
      this.value = ObjectArrays.EMPTY_ARRAY;
   }

   public Float2ReferenceArrayMap(int capacity) {
      this.key = new float[capacity];
      this.value = new Object[capacity];
   }

   public Float2ReferenceArrayMap(Float2ReferenceMap<V> m) {
      this(m.size());
      int i = 0;

      for (Float2ReferenceMap.Entry<V> e : m.float2ReferenceEntrySet()) {
         this.key[i] = e.getFloatKey();
         this.value[i] = e.getValue();
         i++;
      }

      this.size = i;
   }

   public Float2ReferenceArrayMap(Map<? extends Float, ? extends V> m) {
      this(m.size());
      int i = 0;

      for (Entry<? extends Float, ? extends V> e : m.entrySet()) {
         this.key[i] = e.getKey();
         this.value[i] = e.getValue();
         i++;
      }

      this.size = i;
   }

   public Float2ReferenceArrayMap(float[] key, Object[] value, int size) {
      this.key = key;
      this.value = value;
      this.size = size;
      if (key.length != value.length) {
         throw new IllegalArgumentException("Keys and values have different lengths (" + key.length + ", " + value.length + ")");
      } else if (size > key.length) {
         throw new IllegalArgumentException("The provided size (" + size + ") is larger than or equal to the backing-arrays size (" + key.length + ")");
      }
   }

   public Float2ReferenceMap.FastEntrySet<V> float2ReferenceEntrySet() {
      if (this.entries == null) {
         this.entries = new Float2ReferenceArrayMap.EntrySet();
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
   public V get(float k) {
      float[] key = this.key;
      int i = this.size;

      while (i-- != 0) {
         if (Float.floatToIntBits(key[i]) == Float.floatToIntBits(k)) {
            return (V)this.value[i];
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
      int i = this.size;

      while (i-- != 0) {
         this.value[i] = null;
      }

      this.size = 0;
   }

   @Override
   public boolean containsKey(float k) {
      return this.findKey(k) != -1;
   }

   @Override
   public boolean containsValue(Object v) {
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
   public V put(float k, V v) {
      int oldKey = this.findKey(k);
      if (oldKey != -1) {
         V oldValue = (V)this.value[oldKey];
         this.value[oldKey] = v;
         return oldValue;
      } else {
         if (this.size == this.key.length) {
            float[] newKey = new float[this.size == 0 ? 2 : this.size * 2];
            Object[] newValue = new Object[this.size == 0 ? 2 : this.size * 2];

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
   public V remove(float k) {
      int oldPos = this.findKey(k);
      if (oldPos == -1) {
         return this.defRetValue;
      } else {
         V oldValue = (V)this.value[oldPos];
         int tail = this.size - oldPos - 1;
         System.arraycopy(this.key, oldPos + 1, this.key, oldPos, tail);
         System.arraycopy(this.value, oldPos + 1, this.value, oldPos, tail);
         this.size--;
         this.value[this.size] = null;
         return oldValue;
      }
   }

   @Override
   public FloatSet keySet() {
      if (this.keys == null) {
         this.keys = new Float2ReferenceArrayMap.KeySet();
      }

      return this.keys;
   }

   @Override
   public ReferenceCollection<V> values() {
      if (this.values == null) {
         this.values = new Float2ReferenceArrayMap.ValuesCollection();
      }

      return this.values;
   }

   public Float2ReferenceArrayMap<V> clone() {
      Float2ReferenceArrayMap<V> c;
      try {
         c = (Float2ReferenceArrayMap<V>)super.clone();
      } catch (CloneNotSupportedException var3) {
         throw new InternalError();
      }

      c.key = (float[])this.key.clone();
      c.value = (Object[])this.value.clone();
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
         s.writeObject(this.value[i]);
      }
   }

   private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
      s.defaultReadObject();
      this.key = new float[this.size];
      this.value = new Object[this.size];

      for (int i = 0; i < this.size; i++) {
         this.key[i] = s.readFloat();
         this.value[i] = s.readObject();
      }
   }

   private final class EntrySet extends AbstractObjectSet<Float2ReferenceMap.Entry<V>> implements Float2ReferenceMap.FastEntrySet<V> {
      private EntrySet() {
      }

      @Override
      public ObjectIterator<Float2ReferenceMap.Entry<V>> iterator() {
         return new ObjectIterator<Float2ReferenceMap.Entry<V>>() {
            int curr = -1;
            int next = 0;

            @Override
            public boolean hasNext() {
               return this.next < Float2ReferenceArrayMap.this.size;
            }

            public Float2ReferenceMap.Entry<V> next() {
               if (!this.hasNext()) {
                  throw new NoSuchElementException();
               } else {
                  return new AbstractFloat2ReferenceMap.BasicEntry<>(
                     Float2ReferenceArrayMap.this.key[this.curr = this.next], (V)Float2ReferenceArrayMap.this.value[this.next++]
                  );
               }
            }

            @Override
            public void remove() {
               if (this.curr == -1) {
                  throw new IllegalStateException();
               } else {
                  this.curr = -1;
                  int tail = Float2ReferenceArrayMap.this.size-- - this.next--;
                  System.arraycopy(Float2ReferenceArrayMap.this.key, this.next + 1, Float2ReferenceArrayMap.this.key, this.next, tail);
                  System.arraycopy(Float2ReferenceArrayMap.this.value, this.next + 1, Float2ReferenceArrayMap.this.value, this.next, tail);
                  Float2ReferenceArrayMap.this.value[Float2ReferenceArrayMap.this.size] = null;
               }
            }

            @Override
            public void forEachRemaining(Consumer<? super Float2ReferenceMap.Entry<V>> action) {
               int max = Float2ReferenceArrayMap.this.size;

               while (this.next < max) {
                  action.accept(
                     new AbstractFloat2ReferenceMap.BasicEntry<>(
                        Float2ReferenceArrayMap.this.key[this.curr = this.next], (V)Float2ReferenceArrayMap.this.value[this.next++]
                     )
                  );
               }
            }
         };
      }

      @Override
      public ObjectIterator<Float2ReferenceMap.Entry<V>> fastIterator() {
         return new ObjectIterator<Float2ReferenceMap.Entry<V>>() {
            int next = 0;
            int curr = -1;
            final AbstractFloat2ReferenceMap.BasicEntry<V> entry = new AbstractFloat2ReferenceMap.BasicEntry<>();

            @Override
            public boolean hasNext() {
               return this.next < Float2ReferenceArrayMap.this.size;
            }

            public Float2ReferenceMap.Entry<V> next() {
               if (!this.hasNext()) {
                  throw new NoSuchElementException();
               } else {
                  this.entry.key = Float2ReferenceArrayMap.this.key[this.curr = this.next];
                  this.entry.value = (V)Float2ReferenceArrayMap.this.value[this.next++];
                  return this.entry;
               }
            }

            @Override
            public void remove() {
               if (this.curr == -1) {
                  throw new IllegalStateException();
               } else {
                  this.curr = -1;
                  int tail = Float2ReferenceArrayMap.this.size-- - this.next--;
                  System.arraycopy(Float2ReferenceArrayMap.this.key, this.next + 1, Float2ReferenceArrayMap.this.key, this.next, tail);
                  System.arraycopy(Float2ReferenceArrayMap.this.value, this.next + 1, Float2ReferenceArrayMap.this.value, this.next, tail);
                  Float2ReferenceArrayMap.this.value[Float2ReferenceArrayMap.this.size] = null;
               }
            }

            @Override
            public void forEachRemaining(Consumer<? super Float2ReferenceMap.Entry<V>> action) {
               int max = Float2ReferenceArrayMap.this.size;

               while (this.next < max) {
                  this.entry.key = Float2ReferenceArrayMap.this.key[this.curr = this.next];
                  this.entry.value = (V)Float2ReferenceArrayMap.this.value[this.next++];
                  action.accept(this.entry);
               }
            }
         };
      }

      @Override
      public ObjectSpliterator<Float2ReferenceMap.Entry<V>> spliterator() {
         return new Float2ReferenceArrayMap.EntrySet.EntrySetSpliterator(0, Float2ReferenceArrayMap.this.size);
      }

      @Override
      public void forEach(Consumer<? super Float2ReferenceMap.Entry<V>> action) {
         int i = 0;

         for (int max = Float2ReferenceArrayMap.this.size; i < max; i++) {
            action.accept(new AbstractFloat2ReferenceMap.BasicEntry<>(Float2ReferenceArrayMap.this.key[i], (V)Float2ReferenceArrayMap.this.value[i]));
         }
      }

      @Override
      public void fastForEach(Consumer<? super Float2ReferenceMap.Entry<V>> action) {
         AbstractFloat2ReferenceMap.BasicEntry<V> entry = new AbstractFloat2ReferenceMap.BasicEntry<>();
         int i = 0;

         for (int max = Float2ReferenceArrayMap.this.size; i < max; i++) {
            entry.key = Float2ReferenceArrayMap.this.key[i];
            entry.value = (V)Float2ReferenceArrayMap.this.value[i];
            action.accept(entry);
         }
      }

      @Override
      public int size() {
         return Float2ReferenceArrayMap.this.size;
      }

      @Override
      public boolean contains(Object o) {
         if (!(o instanceof Entry)) {
            return false;
         } else {
            Entry<?, ?> e = (Entry<?, ?>)o;
            if (e.getKey() != null && e.getKey() instanceof Float) {
               float k = (Float)e.getKey();
               return Float2ReferenceArrayMap.this.containsKey(k) && Float2ReferenceArrayMap.this.get(k) == e.getValue();
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
            if (e.getKey() != null && e.getKey() instanceof Float) {
               float k = (Float)e.getKey();
               V v = (V)e.getValue();
               int oldPos = Float2ReferenceArrayMap.this.findKey(k);
               if (oldPos != -1 && v == Float2ReferenceArrayMap.this.value[oldPos]) {
                  int tail = Float2ReferenceArrayMap.this.size - oldPos - 1;
                  System.arraycopy(Float2ReferenceArrayMap.this.key, oldPos + 1, Float2ReferenceArrayMap.this.key, oldPos, tail);
                  System.arraycopy(Float2ReferenceArrayMap.this.value, oldPos + 1, Float2ReferenceArrayMap.this.value, oldPos, tail);
                  Float2ReferenceArrayMap.this.size--;
                  Float2ReferenceArrayMap.this.value[Float2ReferenceArrayMap.this.size] = null;
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
         extends ObjectSpliterators.EarlyBindingSizeIndexBasedSpliterator<Float2ReferenceMap.Entry<V>>
         implements ObjectSpliterator<Float2ReferenceMap.Entry<V>> {
         EntrySetSpliterator(int pos, int maxPos) {
            super(pos, maxPos);
         }

         @Override
         public int characteristics() {
            return 16465;
         }

         protected final Float2ReferenceMap.Entry<V> get(int location) {
            return new AbstractFloat2ReferenceMap.BasicEntry<>(Float2ReferenceArrayMap.this.key[location], (V)Float2ReferenceArrayMap.this.value[location]);
         }

         protected final Float2ReferenceArrayMap<V>.EntrySet.EntrySetSpliterator makeForSplit(int pos, int maxPos) {
            return EntrySet.this.new EntrySetSpliterator(pos, maxPos);
         }
      }
   }

   private final class KeySet extends AbstractFloatSet {
      private KeySet() {
      }

      @Override
      public boolean contains(float k) {
         return Float2ReferenceArrayMap.this.findKey(k) != -1;
      }

      @Override
      public boolean remove(float k) {
         int oldPos = Float2ReferenceArrayMap.this.findKey(k);
         if (oldPos == -1) {
            return false;
         } else {
            int tail = Float2ReferenceArrayMap.this.size - oldPos - 1;
            System.arraycopy(Float2ReferenceArrayMap.this.key, oldPos + 1, Float2ReferenceArrayMap.this.key, oldPos, tail);
            System.arraycopy(Float2ReferenceArrayMap.this.value, oldPos + 1, Float2ReferenceArrayMap.this.value, oldPos, tail);
            Float2ReferenceArrayMap.this.size--;
            Float2ReferenceArrayMap.this.value[Float2ReferenceArrayMap.this.size] = null;
            return true;
         }
      }

      @Override
      public FloatIterator iterator() {
         return new FloatIterator() {
            int pos = 0;

            @Override
            public boolean hasNext() {
               return this.pos < Float2ReferenceArrayMap.this.size;
            }

            @Override
            public float nextFloat() {
               if (!this.hasNext()) {
                  throw new NoSuchElementException();
               } else {
                  return Float2ReferenceArrayMap.this.key[this.pos++];
               }
            }

            @Override
            public void remove() {
               if (this.pos == 0) {
                  throw new IllegalStateException();
               } else {
                  int tail = Float2ReferenceArrayMap.this.size - this.pos;
                  System.arraycopy(Float2ReferenceArrayMap.this.key, this.pos, Float2ReferenceArrayMap.this.key, this.pos - 1, tail);
                  System.arraycopy(Float2ReferenceArrayMap.this.value, this.pos, Float2ReferenceArrayMap.this.value, this.pos - 1, tail);
                  Float2ReferenceArrayMap.this.size--;
                  this.pos--;
                  Float2ReferenceArrayMap.this.value[Float2ReferenceArrayMap.this.size] = null;
               }
            }

            @Override
            public void forEachRemaining(FloatConsumer action) {
               int max = Float2ReferenceArrayMap.this.size;

               while (this.pos < max) {
                  action.accept(Float2ReferenceArrayMap.this.key[this.pos++]);
               }
            }
         };
      }

      @Override
      public FloatSpliterator spliterator() {
         return new Float2ReferenceArrayMap.KeySet.KeySetSpliterator(0, Float2ReferenceArrayMap.this.size);
      }

      @Override
      public void forEach(FloatConsumer action) {
         int i = 0;

         for (int max = Float2ReferenceArrayMap.this.size; i < max; i++) {
            action.accept(Float2ReferenceArrayMap.this.key[i]);
         }
      }

      @Override
      public int size() {
         return Float2ReferenceArrayMap.this.size;
      }

      @Override
      public void clear() {
         Float2ReferenceArrayMap.this.clear();
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
            return Float2ReferenceArrayMap.this.key[location];
         }

         protected final Float2ReferenceArrayMap<V>.KeySet.KeySetSpliterator makeForSplit(int pos, int maxPos) {
            return KeySet.this.new KeySetSpliterator(pos, maxPos);
         }

         @Override
         public void forEachRemaining(FloatConsumer action) {
            int max = Float2ReferenceArrayMap.this.size;

            while (this.pos < max) {
               action.accept(Float2ReferenceArrayMap.this.key[this.pos++]);
            }
         }
      }
   }

   private final class ValuesCollection extends AbstractReferenceCollection<V> {
      private ValuesCollection() {
      }

      @Override
      public boolean contains(Object v) {
         return Float2ReferenceArrayMap.this.containsValue(v);
      }

      @Override
      public ObjectIterator<V> iterator() {
         return new ObjectIterator<V>() {
            int pos = 0;

            @Override
            public boolean hasNext() {
               return this.pos < Float2ReferenceArrayMap.this.size;
            }

            @Override
            public V next() {
               if (!this.hasNext()) {
                  throw new NoSuchElementException();
               } else {
                  return (V)Float2ReferenceArrayMap.this.value[this.pos++];
               }
            }

            @Override
            public void remove() {
               if (this.pos == 0) {
                  throw new IllegalStateException();
               } else {
                  int tail = Float2ReferenceArrayMap.this.size - this.pos;
                  System.arraycopy(Float2ReferenceArrayMap.this.key, this.pos, Float2ReferenceArrayMap.this.key, this.pos - 1, tail);
                  System.arraycopy(Float2ReferenceArrayMap.this.value, this.pos, Float2ReferenceArrayMap.this.value, this.pos - 1, tail);
                  Float2ReferenceArrayMap.this.size--;
                  this.pos--;
                  Float2ReferenceArrayMap.this.value[Float2ReferenceArrayMap.this.size] = null;
               }
            }

            @Override
            public void forEachRemaining(Consumer<? super V> action) {
               int max = Float2ReferenceArrayMap.this.size;

               while (this.pos < max) {
                  action.accept((V)Float2ReferenceArrayMap.this.value[this.pos++]);
               }
            }
         };
      }

      @Override
      public ObjectSpliterator<V> spliterator() {
         return new Float2ReferenceArrayMap.ValuesCollection.ValuesSpliterator(0, Float2ReferenceArrayMap.this.size);
      }

      @Override
      public void forEach(Consumer<? super V> action) {
         int i = 0;

         for (int max = Float2ReferenceArrayMap.this.size; i < max; i++) {
            action.accept((V)Float2ReferenceArrayMap.this.value[i]);
         }
      }

      @Override
      public int size() {
         return Float2ReferenceArrayMap.this.size;
      }

      @Override
      public void clear() {
         Float2ReferenceArrayMap.this.clear();
      }

      final class ValuesSpliterator extends ObjectSpliterators.EarlyBindingSizeIndexBasedSpliterator<V> implements ObjectSpliterator<V> {
         ValuesSpliterator(int pos, int maxPos) {
            super(pos, maxPos);
         }

         @Override
         public int characteristics() {
            return 16464;
         }

         @Override
         protected final V get(int location) {
            return (V)Float2ReferenceArrayMap.this.value[location];
         }

         protected final Float2ReferenceArrayMap<V>.ValuesCollection.ValuesSpliterator makeForSplit(int pos, int maxPos) {
            return ValuesCollection.this.new ValuesSpliterator(pos, maxPos);
         }

         @Override
         public void forEachRemaining(Consumer<? super V> action) {
            int max = Float2ReferenceArrayMap.this.size;

            while (this.pos < max) {
               action.accept((V)Float2ReferenceArrayMap.this.value[this.pos++]);
            }
         }
      }
   }
}
