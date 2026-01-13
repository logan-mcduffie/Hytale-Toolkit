package it.unimi.dsi.fastutil.objects;

import it.unimi.dsi.fastutil.floats.AbstractFloatCollection;
import it.unimi.dsi.fastutil.floats.FloatArrays;
import it.unimi.dsi.fastutil.floats.FloatCollection;
import it.unimi.dsi.fastutil.floats.FloatConsumer;
import it.unimi.dsi.fastutil.floats.FloatIterator;
import it.unimi.dsi.fastutil.floats.FloatSpliterator;
import it.unimi.dsi.fastutil.floats.FloatSpliterators;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Map.Entry;
import java.util.function.Consumer;

public class Reference2FloatArrayMap<K> extends AbstractReference2FloatMap<K> implements Serializable, Cloneable {
   private static final long serialVersionUID = 1L;
   protected transient Object[] key;
   protected transient float[] value;
   protected int size;
   protected transient Reference2FloatMap.FastEntrySet<K> entries;
   protected transient ReferenceSet<K> keys;
   protected transient FloatCollection values;

   public Reference2FloatArrayMap(Object[] key, float[] value) {
      this.key = key;
      this.value = value;
      this.size = key.length;
      if (key.length != value.length) {
         throw new IllegalArgumentException("Keys and values have different lengths (" + key.length + ", " + value.length + ")");
      }
   }

   public Reference2FloatArrayMap() {
      this.key = ObjectArrays.EMPTY_ARRAY;
      this.value = FloatArrays.EMPTY_ARRAY;
   }

   public Reference2FloatArrayMap(int capacity) {
      this.key = new Object[capacity];
      this.value = new float[capacity];
   }

   public Reference2FloatArrayMap(Reference2FloatMap<K> m) {
      this(m.size());
      int i = 0;

      for (Reference2FloatMap.Entry<K> e : m.reference2FloatEntrySet()) {
         this.key[i] = e.getKey();
         this.value[i] = e.getFloatValue();
         i++;
      }

      this.size = i;
   }

   public Reference2FloatArrayMap(Map<? extends K, ? extends Float> m) {
      this(m.size());
      int i = 0;

      for (Entry<? extends K, ? extends Float> e : m.entrySet()) {
         this.key[i] = e.getKey();
         this.value[i] = e.getValue();
         i++;
      }

      this.size = i;
   }

   public Reference2FloatArrayMap(Object[] key, float[] value, int size) {
      this.key = key;
      this.value = value;
      this.size = size;
      if (key.length != value.length) {
         throw new IllegalArgumentException("Keys and values have different lengths (" + key.length + ", " + value.length + ")");
      } else if (size > key.length) {
         throw new IllegalArgumentException("The provided size (" + size + ") is larger than or equal to the backing-arrays size (" + key.length + ")");
      }
   }

   public Reference2FloatMap.FastEntrySet<K> reference2FloatEntrySet() {
      if (this.entries == null) {
         this.entries = new Reference2FloatArrayMap.EntrySet();
      }

      return this.entries;
   }

   private int findKey(Object k) {
      Object[] key = this.key;
      int i = this.size;

      while (i-- != 0) {
         if (key[i] == k) {
            return i;
         }
      }

      return -1;
   }

   @Override
   public float getFloat(Object k) {
      Object[] key = this.key;
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
      int i = this.size;

      while (i-- != 0) {
         this.key[i] = null;
      }

      this.size = 0;
   }

   @Override
   public boolean containsKey(Object k) {
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
   public float put(K k, float v) {
      int oldKey = this.findKey(k);
      if (oldKey != -1) {
         float oldValue = this.value[oldKey];
         this.value[oldKey] = v;
         return oldValue;
      } else {
         if (this.size == this.key.length) {
            Object[] newKey = new Object[this.size == 0 ? 2 : this.size * 2];
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
   public float removeFloat(Object k) {
      int oldPos = this.findKey(k);
      if (oldPos == -1) {
         return this.defRetValue;
      } else {
         float oldValue = this.value[oldPos];
         int tail = this.size - oldPos - 1;
         System.arraycopy(this.key, oldPos + 1, this.key, oldPos, tail);
         System.arraycopy(this.value, oldPos + 1, this.value, oldPos, tail);
         this.size--;
         this.key[this.size] = null;
         return oldValue;
      }
   }

   @Override
   public ReferenceSet<K> keySet() {
      if (this.keys == null) {
         this.keys = new Reference2FloatArrayMap.KeySet();
      }

      return this.keys;
   }

   @Override
   public FloatCollection values() {
      if (this.values == null) {
         this.values = new Reference2FloatArrayMap.ValuesCollection();
      }

      return this.values;
   }

   public Reference2FloatArrayMap<K> clone() {
      Reference2FloatArrayMap<K> c;
      try {
         c = (Reference2FloatArrayMap<K>)super.clone();
      } catch (CloneNotSupportedException var3) {
         throw new InternalError();
      }

      c.key = (Object[])this.key.clone();
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
         s.writeObject(this.key[i]);
         s.writeFloat(this.value[i]);
      }
   }

   private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
      s.defaultReadObject();
      this.key = new Object[this.size];
      this.value = new float[this.size];

      for (int i = 0; i < this.size; i++) {
         this.key[i] = s.readObject();
         this.value[i] = s.readFloat();
      }
   }

   private final class EntrySet extends AbstractObjectSet<Reference2FloatMap.Entry<K>> implements Reference2FloatMap.FastEntrySet<K> {
      private EntrySet() {
      }

      @Override
      public ObjectIterator<Reference2FloatMap.Entry<K>> iterator() {
         return new ObjectIterator<Reference2FloatMap.Entry<K>>() {
            int curr = -1;
            int next = 0;

            @Override
            public boolean hasNext() {
               return this.next < Reference2FloatArrayMap.this.size;
            }

            public Reference2FloatMap.Entry<K> next() {
               if (!this.hasNext()) {
                  throw new NoSuchElementException();
               } else {
                  return new AbstractReference2FloatMap.BasicEntry<>(
                     (K)Reference2FloatArrayMap.this.key[this.curr = this.next], Reference2FloatArrayMap.this.value[this.next++]
                  );
               }
            }

            @Override
            public void remove() {
               if (this.curr == -1) {
                  throw new IllegalStateException();
               } else {
                  this.curr = -1;
                  int tail = Reference2FloatArrayMap.this.size-- - this.next--;
                  System.arraycopy(Reference2FloatArrayMap.this.key, this.next + 1, Reference2FloatArrayMap.this.key, this.next, tail);
                  System.arraycopy(Reference2FloatArrayMap.this.value, this.next + 1, Reference2FloatArrayMap.this.value, this.next, tail);
                  Reference2FloatArrayMap.this.key[Reference2FloatArrayMap.this.size] = null;
               }
            }

            @Override
            public void forEachRemaining(Consumer<? super Reference2FloatMap.Entry<K>> action) {
               int max = Reference2FloatArrayMap.this.size;

               while (this.next < max) {
                  action.accept(
                     new AbstractReference2FloatMap.BasicEntry<>(
                        (K)Reference2FloatArrayMap.this.key[this.curr = this.next], Reference2FloatArrayMap.this.value[this.next++]
                     )
                  );
               }
            }
         };
      }

      @Override
      public ObjectIterator<Reference2FloatMap.Entry<K>> fastIterator() {
         return new ObjectIterator<Reference2FloatMap.Entry<K>>() {
            int next = 0;
            int curr = -1;
            final AbstractReference2FloatMap.BasicEntry<K> entry = new AbstractReference2FloatMap.BasicEntry<>();

            @Override
            public boolean hasNext() {
               return this.next < Reference2FloatArrayMap.this.size;
            }

            public Reference2FloatMap.Entry<K> next() {
               if (!this.hasNext()) {
                  throw new NoSuchElementException();
               } else {
                  this.entry.key = (K)Reference2FloatArrayMap.this.key[this.curr = this.next];
                  this.entry.value = Reference2FloatArrayMap.this.value[this.next++];
                  return this.entry;
               }
            }

            @Override
            public void remove() {
               if (this.curr == -1) {
                  throw new IllegalStateException();
               } else {
                  this.curr = -1;
                  int tail = Reference2FloatArrayMap.this.size-- - this.next--;
                  System.arraycopy(Reference2FloatArrayMap.this.key, this.next + 1, Reference2FloatArrayMap.this.key, this.next, tail);
                  System.arraycopy(Reference2FloatArrayMap.this.value, this.next + 1, Reference2FloatArrayMap.this.value, this.next, tail);
                  Reference2FloatArrayMap.this.key[Reference2FloatArrayMap.this.size] = null;
               }
            }

            @Override
            public void forEachRemaining(Consumer<? super Reference2FloatMap.Entry<K>> action) {
               int max = Reference2FloatArrayMap.this.size;

               while (this.next < max) {
                  this.entry.key = (K)Reference2FloatArrayMap.this.key[this.curr = this.next];
                  this.entry.value = Reference2FloatArrayMap.this.value[this.next++];
                  action.accept(this.entry);
               }
            }
         };
      }

      @Override
      public ObjectSpliterator<Reference2FloatMap.Entry<K>> spliterator() {
         return new Reference2FloatArrayMap.EntrySet.EntrySetSpliterator(0, Reference2FloatArrayMap.this.size);
      }

      @Override
      public void forEach(Consumer<? super Reference2FloatMap.Entry<K>> action) {
         int i = 0;

         for (int max = Reference2FloatArrayMap.this.size; i < max; i++) {
            action.accept(new AbstractReference2FloatMap.BasicEntry<>((K)Reference2FloatArrayMap.this.key[i], Reference2FloatArrayMap.this.value[i]));
         }
      }

      @Override
      public void fastForEach(Consumer<? super Reference2FloatMap.Entry<K>> action) {
         AbstractReference2FloatMap.BasicEntry<K> entry = new AbstractReference2FloatMap.BasicEntry<>();
         int i = 0;

         for (int max = Reference2FloatArrayMap.this.size; i < max; i++) {
            entry.key = (K)Reference2FloatArrayMap.this.key[i];
            entry.value = Reference2FloatArrayMap.this.value[i];
            action.accept(entry);
         }
      }

      @Override
      public int size() {
         return Reference2FloatArrayMap.this.size;
      }

      @Override
      public boolean contains(Object o) {
         if (!(o instanceof Entry)) {
            return false;
         } else {
            Entry<?, ?> e = (Entry<?, ?>)o;
            if (e.getValue() != null && e.getValue() instanceof Float) {
               K k = (K)e.getKey();
               return Reference2FloatArrayMap.this.containsKey(k)
                  && Float.floatToIntBits(Reference2FloatArrayMap.this.getFloat(k)) == Float.floatToIntBits((Float)e.getValue());
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
            if (e.getValue() != null && e.getValue() instanceof Float) {
               K k = (K)e.getKey();
               float v = (Float)e.getValue();
               int oldPos = Reference2FloatArrayMap.this.findKey(k);
               if (oldPos != -1 && Float.floatToIntBits(v) == Float.floatToIntBits(Reference2FloatArrayMap.this.value[oldPos])) {
                  int tail = Reference2FloatArrayMap.this.size - oldPos - 1;
                  System.arraycopy(Reference2FloatArrayMap.this.key, oldPos + 1, Reference2FloatArrayMap.this.key, oldPos, tail);
                  System.arraycopy(Reference2FloatArrayMap.this.value, oldPos + 1, Reference2FloatArrayMap.this.value, oldPos, tail);
                  Reference2FloatArrayMap.this.size--;
                  Reference2FloatArrayMap.this.key[Reference2FloatArrayMap.this.size] = null;
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
         extends ObjectSpliterators.EarlyBindingSizeIndexBasedSpliterator<Reference2FloatMap.Entry<K>>
         implements ObjectSpliterator<Reference2FloatMap.Entry<K>> {
         EntrySetSpliterator(int pos, int maxPos) {
            super(pos, maxPos);
         }

         @Override
         public int characteristics() {
            return 16465;
         }

         protected final Reference2FloatMap.Entry<K> get(int location) {
            return new AbstractReference2FloatMap.BasicEntry<>((K)Reference2FloatArrayMap.this.key[location], Reference2FloatArrayMap.this.value[location]);
         }

         protected final Reference2FloatArrayMap<K>.EntrySet.EntrySetSpliterator makeForSplit(int pos, int maxPos) {
            return EntrySet.this.new EntrySetSpliterator(pos, maxPos);
         }
      }
   }

   private final class KeySet extends AbstractReferenceSet<K> {
      private KeySet() {
      }

      @Override
      public boolean contains(Object k) {
         return Reference2FloatArrayMap.this.findKey(k) != -1;
      }

      @Override
      public boolean remove(Object k) {
         int oldPos = Reference2FloatArrayMap.this.findKey(k);
         if (oldPos == -1) {
            return false;
         } else {
            int tail = Reference2FloatArrayMap.this.size - oldPos - 1;
            System.arraycopy(Reference2FloatArrayMap.this.key, oldPos + 1, Reference2FloatArrayMap.this.key, oldPos, tail);
            System.arraycopy(Reference2FloatArrayMap.this.value, oldPos + 1, Reference2FloatArrayMap.this.value, oldPos, tail);
            Reference2FloatArrayMap.this.size--;
            Reference2FloatArrayMap.this.key[Reference2FloatArrayMap.this.size] = null;
            return true;
         }
      }

      @Override
      public ObjectIterator<K> iterator() {
         return new ObjectIterator<K>() {
            int pos = 0;

            @Override
            public boolean hasNext() {
               return this.pos < Reference2FloatArrayMap.this.size;
            }

            @Override
            public K next() {
               if (!this.hasNext()) {
                  throw new NoSuchElementException();
               } else {
                  return (K)Reference2FloatArrayMap.this.key[this.pos++];
               }
            }

            @Override
            public void remove() {
               if (this.pos == 0) {
                  throw new IllegalStateException();
               } else {
                  int tail = Reference2FloatArrayMap.this.size - this.pos;
                  System.arraycopy(Reference2FloatArrayMap.this.key, this.pos, Reference2FloatArrayMap.this.key, this.pos - 1, tail);
                  System.arraycopy(Reference2FloatArrayMap.this.value, this.pos, Reference2FloatArrayMap.this.value, this.pos - 1, tail);
                  Reference2FloatArrayMap.this.size--;
                  this.pos--;
                  Reference2FloatArrayMap.this.key[Reference2FloatArrayMap.this.size] = null;
               }
            }

            @Override
            public void forEachRemaining(Consumer<? super K> action) {
               int max = Reference2FloatArrayMap.this.size;

               while (this.pos < max) {
                  action.accept((K)Reference2FloatArrayMap.this.key[this.pos++]);
               }
            }
         };
      }

      @Override
      public ObjectSpliterator<K> spliterator() {
         return new Reference2FloatArrayMap.KeySet.KeySetSpliterator(0, Reference2FloatArrayMap.this.size);
      }

      @Override
      public void forEach(Consumer<? super K> action) {
         int i = 0;

         for (int max = Reference2FloatArrayMap.this.size; i < max; i++) {
            action.accept((K)Reference2FloatArrayMap.this.key[i]);
         }
      }

      @Override
      public int size() {
         return Reference2FloatArrayMap.this.size;
      }

      @Override
      public void clear() {
         Reference2FloatArrayMap.this.clear();
      }

      final class KeySetSpliterator extends ObjectSpliterators.EarlyBindingSizeIndexBasedSpliterator<K> implements ObjectSpliterator<K> {
         KeySetSpliterator(int pos, int maxPos) {
            super(pos, maxPos);
         }

         @Override
         public int characteristics() {
            return 16465;
         }

         @Override
         protected final K get(int location) {
            return (K)Reference2FloatArrayMap.this.key[location];
         }

         protected final Reference2FloatArrayMap<K>.KeySet.KeySetSpliterator makeForSplit(int pos, int maxPos) {
            return KeySet.this.new KeySetSpliterator(pos, maxPos);
         }

         @Override
         public void forEachRemaining(Consumer<? super K> action) {
            int max = Reference2FloatArrayMap.this.size;

            while (this.pos < max) {
               action.accept((K)Reference2FloatArrayMap.this.key[this.pos++]);
            }
         }
      }
   }

   private final class ValuesCollection extends AbstractFloatCollection {
      private ValuesCollection() {
      }

      @Override
      public boolean contains(float v) {
         return Reference2FloatArrayMap.this.containsValue(v);
      }

      @Override
      public FloatIterator iterator() {
         return new FloatIterator() {
            int pos = 0;

            @Override
            public boolean hasNext() {
               return this.pos < Reference2FloatArrayMap.this.size;
            }

            @Override
            public float nextFloat() {
               if (!this.hasNext()) {
                  throw new NoSuchElementException();
               } else {
                  return Reference2FloatArrayMap.this.value[this.pos++];
               }
            }

            @Override
            public void remove() {
               if (this.pos == 0) {
                  throw new IllegalStateException();
               } else {
                  int tail = Reference2FloatArrayMap.this.size - this.pos;
                  System.arraycopy(Reference2FloatArrayMap.this.key, this.pos, Reference2FloatArrayMap.this.key, this.pos - 1, tail);
                  System.arraycopy(Reference2FloatArrayMap.this.value, this.pos, Reference2FloatArrayMap.this.value, this.pos - 1, tail);
                  Reference2FloatArrayMap.this.size--;
                  this.pos--;
                  Reference2FloatArrayMap.this.key[Reference2FloatArrayMap.this.size] = null;
               }
            }

            @Override
            public void forEachRemaining(FloatConsumer action) {
               int max = Reference2FloatArrayMap.this.size;

               while (this.pos < max) {
                  action.accept(Reference2FloatArrayMap.this.value[this.pos++]);
               }
            }
         };
      }

      @Override
      public FloatSpliterator spliterator() {
         return new Reference2FloatArrayMap.ValuesCollection.ValuesSpliterator(0, Reference2FloatArrayMap.this.size);
      }

      @Override
      public void forEach(FloatConsumer action) {
         int i = 0;

         for (int max = Reference2FloatArrayMap.this.size; i < max; i++) {
            action.accept(Reference2FloatArrayMap.this.value[i]);
         }
      }

      @Override
      public int size() {
         return Reference2FloatArrayMap.this.size;
      }

      @Override
      public void clear() {
         Reference2FloatArrayMap.this.clear();
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
            return Reference2FloatArrayMap.this.value[location];
         }

         protected final Reference2FloatArrayMap<K>.ValuesCollection.ValuesSpliterator makeForSplit(int pos, int maxPos) {
            return ValuesCollection.this.new ValuesSpliterator(pos, maxPos);
         }

         @Override
         public void forEachRemaining(FloatConsumer action) {
            int max = Reference2FloatArrayMap.this.size;

            while (this.pos < max) {
               action.accept(Reference2FloatArrayMap.this.value[this.pos++]);
            }
         }
      }
   }
}
