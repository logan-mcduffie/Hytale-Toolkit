package it.unimi.dsi.fastutil.objects;

import it.unimi.dsi.fastutil.longs.AbstractLongCollection;
import it.unimi.dsi.fastutil.longs.LongArrays;
import it.unimi.dsi.fastutil.longs.LongCollection;
import it.unimi.dsi.fastutil.longs.LongIterator;
import it.unimi.dsi.fastutil.longs.LongSpliterator;
import it.unimi.dsi.fastutil.longs.LongSpliterators;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Map.Entry;
import java.util.function.Consumer;
import java.util.function.LongConsumer;

public class Object2LongArrayMap<K> extends AbstractObject2LongMap<K> implements Serializable, Cloneable {
   private static final long serialVersionUID = 1L;
   protected transient Object[] key;
   protected transient long[] value;
   protected int size;
   protected transient Object2LongMap.FastEntrySet<K> entries;
   protected transient ObjectSet<K> keys;
   protected transient LongCollection values;

   public Object2LongArrayMap(Object[] key, long[] value) {
      this.key = key;
      this.value = value;
      this.size = key.length;
      if (key.length != value.length) {
         throw new IllegalArgumentException("Keys and values have different lengths (" + key.length + ", " + value.length + ")");
      }
   }

   public Object2LongArrayMap() {
      this.key = ObjectArrays.EMPTY_ARRAY;
      this.value = LongArrays.EMPTY_ARRAY;
   }

   public Object2LongArrayMap(int capacity) {
      this.key = new Object[capacity];
      this.value = new long[capacity];
   }

   public Object2LongArrayMap(Object2LongMap<K> m) {
      this(m.size());
      int i = 0;

      for (Object2LongMap.Entry<K> e : m.object2LongEntrySet()) {
         this.key[i] = e.getKey();
         this.value[i] = e.getLongValue();
         i++;
      }

      this.size = i;
   }

   public Object2LongArrayMap(Map<? extends K, ? extends Long> m) {
      this(m.size());
      int i = 0;

      for (Entry<? extends K, ? extends Long> e : m.entrySet()) {
         this.key[i] = e.getKey();
         this.value[i] = e.getValue();
         i++;
      }

      this.size = i;
   }

   public Object2LongArrayMap(Object[] key, long[] value, int size) {
      this.key = key;
      this.value = value;
      this.size = size;
      if (key.length != value.length) {
         throw new IllegalArgumentException("Keys and values have different lengths (" + key.length + ", " + value.length + ")");
      } else if (size > key.length) {
         throw new IllegalArgumentException("The provided size (" + size + ") is larger than or equal to the backing-arrays size (" + key.length + ")");
      }
   }

   public Object2LongMap.FastEntrySet<K> object2LongEntrySet() {
      if (this.entries == null) {
         this.entries = new Object2LongArrayMap.EntrySet();
      }

      return this.entries;
   }

   private int findKey(Object k) {
      Object[] key = this.key;
      int i = this.size;

      while (i-- != 0) {
         if (Objects.equals(key[i], k)) {
            return i;
         }
      }

      return -1;
   }

   @Override
   public long getLong(Object k) {
      Object[] key = this.key;
      int i = this.size;

      while (i-- != 0) {
         if (Objects.equals(key[i], k)) {
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
   public boolean containsValue(long v) {
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
   public long put(K k, long v) {
      int oldKey = this.findKey(k);
      if (oldKey != -1) {
         long oldValue = this.value[oldKey];
         this.value[oldKey] = v;
         return oldValue;
      } else {
         if (this.size == this.key.length) {
            Object[] newKey = new Object[this.size == 0 ? 2 : this.size * 2];
            long[] newValue = new long[this.size == 0 ? 2 : this.size * 2];

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
   public long removeLong(Object k) {
      int oldPos = this.findKey(k);
      if (oldPos == -1) {
         return this.defRetValue;
      } else {
         long oldValue = this.value[oldPos];
         int tail = this.size - oldPos - 1;
         System.arraycopy(this.key, oldPos + 1, this.key, oldPos, tail);
         System.arraycopy(this.value, oldPos + 1, this.value, oldPos, tail);
         this.size--;
         this.key[this.size] = null;
         return oldValue;
      }
   }

   @Override
   public ObjectSet<K> keySet() {
      if (this.keys == null) {
         this.keys = new Object2LongArrayMap.KeySet();
      }

      return this.keys;
   }

   @Override
   public LongCollection values() {
      if (this.values == null) {
         this.values = new Object2LongArrayMap.ValuesCollection();
      }

      return this.values;
   }

   public Object2LongArrayMap<K> clone() {
      Object2LongArrayMap<K> c;
      try {
         c = (Object2LongArrayMap<K>)super.clone();
      } catch (CloneNotSupportedException var3) {
         throw new InternalError();
      }

      c.key = (Object[])this.key.clone();
      c.value = (long[])this.value.clone();
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
         s.writeLong(this.value[i]);
      }
   }

   private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
      s.defaultReadObject();
      this.key = new Object[this.size];
      this.value = new long[this.size];

      for (int i = 0; i < this.size; i++) {
         this.key[i] = s.readObject();
         this.value[i] = s.readLong();
      }
   }

   private final class EntrySet extends AbstractObjectSet<Object2LongMap.Entry<K>> implements Object2LongMap.FastEntrySet<K> {
      private EntrySet() {
      }

      @Override
      public ObjectIterator<Object2LongMap.Entry<K>> iterator() {
         return new ObjectIterator<Object2LongMap.Entry<K>>() {
            int curr = -1;
            int next = 0;

            @Override
            public boolean hasNext() {
               return this.next < Object2LongArrayMap.this.size;
            }

            public Object2LongMap.Entry<K> next() {
               if (!this.hasNext()) {
                  throw new NoSuchElementException();
               } else {
                  return new AbstractObject2LongMap.BasicEntry<>(
                     (K)Object2LongArrayMap.this.key[this.curr = this.next], Object2LongArrayMap.this.value[this.next++]
                  );
               }
            }

            @Override
            public void remove() {
               if (this.curr == -1) {
                  throw new IllegalStateException();
               } else {
                  this.curr = -1;
                  int tail = Object2LongArrayMap.this.size-- - this.next--;
                  System.arraycopy(Object2LongArrayMap.this.key, this.next + 1, Object2LongArrayMap.this.key, this.next, tail);
                  System.arraycopy(Object2LongArrayMap.this.value, this.next + 1, Object2LongArrayMap.this.value, this.next, tail);
                  Object2LongArrayMap.this.key[Object2LongArrayMap.this.size] = null;
               }
            }

            @Override
            public void forEachRemaining(Consumer<? super Object2LongMap.Entry<K>> action) {
               int max = Object2LongArrayMap.this.size;

               while (this.next < max) {
                  action.accept(
                     new AbstractObject2LongMap.BasicEntry<>(
                        (K)Object2LongArrayMap.this.key[this.curr = this.next], Object2LongArrayMap.this.value[this.next++]
                     )
                  );
               }
            }
         };
      }

      @Override
      public ObjectIterator<Object2LongMap.Entry<K>> fastIterator() {
         return new ObjectIterator<Object2LongMap.Entry<K>>() {
            int next = 0;
            int curr = -1;
            final AbstractObject2LongMap.BasicEntry<K> entry = new AbstractObject2LongMap.BasicEntry<>();

            @Override
            public boolean hasNext() {
               return this.next < Object2LongArrayMap.this.size;
            }

            public Object2LongMap.Entry<K> next() {
               if (!this.hasNext()) {
                  throw new NoSuchElementException();
               } else {
                  this.entry.key = (K)Object2LongArrayMap.this.key[this.curr = this.next];
                  this.entry.value = Object2LongArrayMap.this.value[this.next++];
                  return this.entry;
               }
            }

            @Override
            public void remove() {
               if (this.curr == -1) {
                  throw new IllegalStateException();
               } else {
                  this.curr = -1;
                  int tail = Object2LongArrayMap.this.size-- - this.next--;
                  System.arraycopy(Object2LongArrayMap.this.key, this.next + 1, Object2LongArrayMap.this.key, this.next, tail);
                  System.arraycopy(Object2LongArrayMap.this.value, this.next + 1, Object2LongArrayMap.this.value, this.next, tail);
                  Object2LongArrayMap.this.key[Object2LongArrayMap.this.size] = null;
               }
            }

            @Override
            public void forEachRemaining(Consumer<? super Object2LongMap.Entry<K>> action) {
               int max = Object2LongArrayMap.this.size;

               while (this.next < max) {
                  this.entry.key = (K)Object2LongArrayMap.this.key[this.curr = this.next];
                  this.entry.value = Object2LongArrayMap.this.value[this.next++];
                  action.accept(this.entry);
               }
            }
         };
      }

      @Override
      public ObjectSpliterator<Object2LongMap.Entry<K>> spliterator() {
         return new Object2LongArrayMap.EntrySet.EntrySetSpliterator(0, Object2LongArrayMap.this.size);
      }

      @Override
      public void forEach(Consumer<? super Object2LongMap.Entry<K>> action) {
         int i = 0;

         for (int max = Object2LongArrayMap.this.size; i < max; i++) {
            action.accept(new AbstractObject2LongMap.BasicEntry<>((K)Object2LongArrayMap.this.key[i], Object2LongArrayMap.this.value[i]));
         }
      }

      @Override
      public void fastForEach(Consumer<? super Object2LongMap.Entry<K>> action) {
         AbstractObject2LongMap.BasicEntry<K> entry = new AbstractObject2LongMap.BasicEntry<>();
         int i = 0;

         for (int max = Object2LongArrayMap.this.size; i < max; i++) {
            entry.key = (K)Object2LongArrayMap.this.key[i];
            entry.value = Object2LongArrayMap.this.value[i];
            action.accept(entry);
         }
      }

      @Override
      public int size() {
         return Object2LongArrayMap.this.size;
      }

      @Override
      public boolean contains(Object o) {
         if (!(o instanceof Entry)) {
            return false;
         } else {
            Entry<?, ?> e = (Entry<?, ?>)o;
            if (e.getValue() != null && e.getValue() instanceof Long) {
               K k = (K)e.getKey();
               return Object2LongArrayMap.this.containsKey(k) && Object2LongArrayMap.this.getLong(k) == (Long)e.getValue();
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
            if (e.getValue() != null && e.getValue() instanceof Long) {
               K k = (K)e.getKey();
               long v = (Long)e.getValue();
               int oldPos = Object2LongArrayMap.this.findKey(k);
               if (oldPos != -1 && v == Object2LongArrayMap.this.value[oldPos]) {
                  int tail = Object2LongArrayMap.this.size - oldPos - 1;
                  System.arraycopy(Object2LongArrayMap.this.key, oldPos + 1, Object2LongArrayMap.this.key, oldPos, tail);
                  System.arraycopy(Object2LongArrayMap.this.value, oldPos + 1, Object2LongArrayMap.this.value, oldPos, tail);
                  Object2LongArrayMap.this.size--;
                  Object2LongArrayMap.this.key[Object2LongArrayMap.this.size] = null;
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
         extends ObjectSpliterators.EarlyBindingSizeIndexBasedSpliterator<Object2LongMap.Entry<K>>
         implements ObjectSpliterator<Object2LongMap.Entry<K>> {
         EntrySetSpliterator(int pos, int maxPos) {
            super(pos, maxPos);
         }

         @Override
         public int characteristics() {
            return 16465;
         }

         protected final Object2LongMap.Entry<K> get(int location) {
            return new AbstractObject2LongMap.BasicEntry<>((K)Object2LongArrayMap.this.key[location], Object2LongArrayMap.this.value[location]);
         }

         protected final Object2LongArrayMap<K>.EntrySet.EntrySetSpliterator makeForSplit(int pos, int maxPos) {
            return EntrySet.this.new EntrySetSpliterator(pos, maxPos);
         }
      }
   }

   private final class KeySet extends AbstractObjectSet<K> {
      private KeySet() {
      }

      @Override
      public boolean contains(Object k) {
         return Object2LongArrayMap.this.findKey(k) != -1;
      }

      @Override
      public boolean remove(Object k) {
         int oldPos = Object2LongArrayMap.this.findKey(k);
         if (oldPos == -1) {
            return false;
         } else {
            int tail = Object2LongArrayMap.this.size - oldPos - 1;
            System.arraycopy(Object2LongArrayMap.this.key, oldPos + 1, Object2LongArrayMap.this.key, oldPos, tail);
            System.arraycopy(Object2LongArrayMap.this.value, oldPos + 1, Object2LongArrayMap.this.value, oldPos, tail);
            Object2LongArrayMap.this.size--;
            Object2LongArrayMap.this.key[Object2LongArrayMap.this.size] = null;
            return true;
         }
      }

      @Override
      public ObjectIterator<K> iterator() {
         return new ObjectIterator<K>() {
            int pos = 0;

            @Override
            public boolean hasNext() {
               return this.pos < Object2LongArrayMap.this.size;
            }

            @Override
            public K next() {
               if (!this.hasNext()) {
                  throw new NoSuchElementException();
               } else {
                  return (K)Object2LongArrayMap.this.key[this.pos++];
               }
            }

            @Override
            public void remove() {
               if (this.pos == 0) {
                  throw new IllegalStateException();
               } else {
                  int tail = Object2LongArrayMap.this.size - this.pos;
                  System.arraycopy(Object2LongArrayMap.this.key, this.pos, Object2LongArrayMap.this.key, this.pos - 1, tail);
                  System.arraycopy(Object2LongArrayMap.this.value, this.pos, Object2LongArrayMap.this.value, this.pos - 1, tail);
                  Object2LongArrayMap.this.size--;
                  this.pos--;
                  Object2LongArrayMap.this.key[Object2LongArrayMap.this.size] = null;
               }
            }

            @Override
            public void forEachRemaining(Consumer<? super K> action) {
               int max = Object2LongArrayMap.this.size;

               while (this.pos < max) {
                  action.accept((K)Object2LongArrayMap.this.key[this.pos++]);
               }
            }
         };
      }

      @Override
      public ObjectSpliterator<K> spliterator() {
         return new Object2LongArrayMap.KeySet.KeySetSpliterator(0, Object2LongArrayMap.this.size);
      }

      @Override
      public void forEach(Consumer<? super K> action) {
         int i = 0;

         for (int max = Object2LongArrayMap.this.size; i < max; i++) {
            action.accept((K)Object2LongArrayMap.this.key[i]);
         }
      }

      @Override
      public int size() {
         return Object2LongArrayMap.this.size;
      }

      @Override
      public void clear() {
         Object2LongArrayMap.this.clear();
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
            return (K)Object2LongArrayMap.this.key[location];
         }

         protected final Object2LongArrayMap<K>.KeySet.KeySetSpliterator makeForSplit(int pos, int maxPos) {
            return KeySet.this.new KeySetSpliterator(pos, maxPos);
         }

         @Override
         public void forEachRemaining(Consumer<? super K> action) {
            int max = Object2LongArrayMap.this.size;

            while (this.pos < max) {
               action.accept((K)Object2LongArrayMap.this.key[this.pos++]);
            }
         }
      }
   }

   private final class ValuesCollection extends AbstractLongCollection {
      private ValuesCollection() {
      }

      @Override
      public boolean contains(long v) {
         return Object2LongArrayMap.this.containsValue(v);
      }

      @Override
      public LongIterator iterator() {
         return new LongIterator() {
            int pos = 0;

            @Override
            public boolean hasNext() {
               return this.pos < Object2LongArrayMap.this.size;
            }

            @Override
            public long nextLong() {
               if (!this.hasNext()) {
                  throw new NoSuchElementException();
               } else {
                  return Object2LongArrayMap.this.value[this.pos++];
               }
            }

            @Override
            public void remove() {
               if (this.pos == 0) {
                  throw new IllegalStateException();
               } else {
                  int tail = Object2LongArrayMap.this.size - this.pos;
                  System.arraycopy(Object2LongArrayMap.this.key, this.pos, Object2LongArrayMap.this.key, this.pos - 1, tail);
                  System.arraycopy(Object2LongArrayMap.this.value, this.pos, Object2LongArrayMap.this.value, this.pos - 1, tail);
                  Object2LongArrayMap.this.size--;
                  this.pos--;
                  Object2LongArrayMap.this.key[Object2LongArrayMap.this.size] = null;
               }
            }

            @Override
            public void forEachRemaining(LongConsumer action) {
               int max = Object2LongArrayMap.this.size;

               while (this.pos < max) {
                  action.accept(Object2LongArrayMap.this.value[this.pos++]);
               }
            }
         };
      }

      @Override
      public LongSpliterator spliterator() {
         return new Object2LongArrayMap.ValuesCollection.ValuesSpliterator(0, Object2LongArrayMap.this.size);
      }

      @Override
      public void forEach(LongConsumer action) {
         int i = 0;

         for (int max = Object2LongArrayMap.this.size; i < max; i++) {
            action.accept(Object2LongArrayMap.this.value[i]);
         }
      }

      @Override
      public int size() {
         return Object2LongArrayMap.this.size;
      }

      @Override
      public void clear() {
         Object2LongArrayMap.this.clear();
      }

      final class ValuesSpliterator extends LongSpliterators.EarlyBindingSizeIndexBasedSpliterator implements LongSpliterator {
         ValuesSpliterator(int pos, int maxPos) {
            super(pos, maxPos);
         }

         @Override
         public int characteristics() {
            return 16720;
         }

         @Override
         protected final long get(int location) {
            return Object2LongArrayMap.this.value[location];
         }

         protected final Object2LongArrayMap<K>.ValuesCollection.ValuesSpliterator makeForSplit(int pos, int maxPos) {
            return ValuesCollection.this.new ValuesSpliterator(pos, maxPos);
         }

         @Override
         public void forEachRemaining(LongConsumer action) {
            int max = Object2LongArrayMap.this.size;

            while (this.pos < max) {
               action.accept(Object2LongArrayMap.this.value[this.pos++]);
            }
         }
      }
   }
}
