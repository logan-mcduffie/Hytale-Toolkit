package it.unimi.dsi.fastutil.objects;

import it.unimi.dsi.fastutil.booleans.AbstractBooleanCollection;
import it.unimi.dsi.fastutil.booleans.BooleanArrays;
import it.unimi.dsi.fastutil.booleans.BooleanCollection;
import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import it.unimi.dsi.fastutil.booleans.BooleanIterator;
import it.unimi.dsi.fastutil.booleans.BooleanSpliterator;
import it.unimi.dsi.fastutil.booleans.BooleanSpliterators;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Map.Entry;
import java.util.function.Consumer;

public class Object2BooleanArrayMap<K> extends AbstractObject2BooleanMap<K> implements Serializable, Cloneable {
   private static final long serialVersionUID = 1L;
   protected transient Object[] key;
   protected transient boolean[] value;
   protected int size;
   protected transient Object2BooleanMap.FastEntrySet<K> entries;
   protected transient ObjectSet<K> keys;
   protected transient BooleanCollection values;

   public Object2BooleanArrayMap(Object[] key, boolean[] value) {
      this.key = key;
      this.value = value;
      this.size = key.length;
      if (key.length != value.length) {
         throw new IllegalArgumentException("Keys and values have different lengths (" + key.length + ", " + value.length + ")");
      }
   }

   public Object2BooleanArrayMap() {
      this.key = ObjectArrays.EMPTY_ARRAY;
      this.value = BooleanArrays.EMPTY_ARRAY;
   }

   public Object2BooleanArrayMap(int capacity) {
      this.key = new Object[capacity];
      this.value = new boolean[capacity];
   }

   public Object2BooleanArrayMap(Object2BooleanMap<K> m) {
      this(m.size());
      int i = 0;

      for (Object2BooleanMap.Entry<K> e : m.object2BooleanEntrySet()) {
         this.key[i] = e.getKey();
         this.value[i] = e.getBooleanValue();
         i++;
      }

      this.size = i;
   }

   public Object2BooleanArrayMap(Map<? extends K, ? extends Boolean> m) {
      this(m.size());
      int i = 0;

      for (Entry<? extends K, ? extends Boolean> e : m.entrySet()) {
         this.key[i] = e.getKey();
         this.value[i] = e.getValue();
         i++;
      }

      this.size = i;
   }

   public Object2BooleanArrayMap(Object[] key, boolean[] value, int size) {
      this.key = key;
      this.value = value;
      this.size = size;
      if (key.length != value.length) {
         throw new IllegalArgumentException("Keys and values have different lengths (" + key.length + ", " + value.length + ")");
      } else if (size > key.length) {
         throw new IllegalArgumentException("The provided size (" + size + ") is larger than or equal to the backing-arrays size (" + key.length + ")");
      }
   }

   public Object2BooleanMap.FastEntrySet<K> object2BooleanEntrySet() {
      if (this.entries == null) {
         this.entries = new Object2BooleanArrayMap.EntrySet();
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
   public boolean getBoolean(Object k) {
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
   public boolean containsValue(boolean v) {
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
   public boolean put(K k, boolean v) {
      int oldKey = this.findKey(k);
      if (oldKey != -1) {
         boolean oldValue = this.value[oldKey];
         this.value[oldKey] = v;
         return oldValue;
      } else {
         if (this.size == this.key.length) {
            Object[] newKey = new Object[this.size == 0 ? 2 : this.size * 2];
            boolean[] newValue = new boolean[this.size == 0 ? 2 : this.size * 2];

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
   public boolean removeBoolean(Object k) {
      int oldPos = this.findKey(k);
      if (oldPos == -1) {
         return this.defRetValue;
      } else {
         boolean oldValue = this.value[oldPos];
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
         this.keys = new Object2BooleanArrayMap.KeySet();
      }

      return this.keys;
   }

   @Override
   public BooleanCollection values() {
      if (this.values == null) {
         this.values = new Object2BooleanArrayMap.ValuesCollection();
      }

      return this.values;
   }

   public Object2BooleanArrayMap<K> clone() {
      Object2BooleanArrayMap<K> c;
      try {
         c = (Object2BooleanArrayMap<K>)super.clone();
      } catch (CloneNotSupportedException var3) {
         throw new InternalError();
      }

      c.key = (Object[])this.key.clone();
      c.value = (boolean[])this.value.clone();
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
         s.writeBoolean(this.value[i]);
      }
   }

   private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
      s.defaultReadObject();
      this.key = new Object[this.size];
      this.value = new boolean[this.size];

      for (int i = 0; i < this.size; i++) {
         this.key[i] = s.readObject();
         this.value[i] = s.readBoolean();
      }
   }

   private final class EntrySet extends AbstractObjectSet<Object2BooleanMap.Entry<K>> implements Object2BooleanMap.FastEntrySet<K> {
      private EntrySet() {
      }

      @Override
      public ObjectIterator<Object2BooleanMap.Entry<K>> iterator() {
         return new ObjectIterator<Object2BooleanMap.Entry<K>>() {
            int curr = -1;
            int next = 0;

            @Override
            public boolean hasNext() {
               return this.next < Object2BooleanArrayMap.this.size;
            }

            public Object2BooleanMap.Entry<K> next() {
               if (!this.hasNext()) {
                  throw new NoSuchElementException();
               } else {
                  return new AbstractObject2BooleanMap.BasicEntry<>(
                     (K)Object2BooleanArrayMap.this.key[this.curr = this.next], Object2BooleanArrayMap.this.value[this.next++]
                  );
               }
            }

            @Override
            public void remove() {
               if (this.curr == -1) {
                  throw new IllegalStateException();
               } else {
                  this.curr = -1;
                  int tail = Object2BooleanArrayMap.this.size-- - this.next--;
                  System.arraycopy(Object2BooleanArrayMap.this.key, this.next + 1, Object2BooleanArrayMap.this.key, this.next, tail);
                  System.arraycopy(Object2BooleanArrayMap.this.value, this.next + 1, Object2BooleanArrayMap.this.value, this.next, tail);
                  Object2BooleanArrayMap.this.key[Object2BooleanArrayMap.this.size] = null;
               }
            }

            @Override
            public void forEachRemaining(Consumer<? super Object2BooleanMap.Entry<K>> action) {
               int max = Object2BooleanArrayMap.this.size;

               while (this.next < max) {
                  action.accept(
                     new AbstractObject2BooleanMap.BasicEntry<>(
                        (K)Object2BooleanArrayMap.this.key[this.curr = this.next], Object2BooleanArrayMap.this.value[this.next++]
                     )
                  );
               }
            }
         };
      }

      @Override
      public ObjectIterator<Object2BooleanMap.Entry<K>> fastIterator() {
         return new ObjectIterator<Object2BooleanMap.Entry<K>>() {
            int next = 0;
            int curr = -1;
            final AbstractObject2BooleanMap.BasicEntry<K> entry = new AbstractObject2BooleanMap.BasicEntry<>();

            @Override
            public boolean hasNext() {
               return this.next < Object2BooleanArrayMap.this.size;
            }

            public Object2BooleanMap.Entry<K> next() {
               if (!this.hasNext()) {
                  throw new NoSuchElementException();
               } else {
                  this.entry.key = (K)Object2BooleanArrayMap.this.key[this.curr = this.next];
                  this.entry.value = Object2BooleanArrayMap.this.value[this.next++];
                  return this.entry;
               }
            }

            @Override
            public void remove() {
               if (this.curr == -1) {
                  throw new IllegalStateException();
               } else {
                  this.curr = -1;
                  int tail = Object2BooleanArrayMap.this.size-- - this.next--;
                  System.arraycopy(Object2BooleanArrayMap.this.key, this.next + 1, Object2BooleanArrayMap.this.key, this.next, tail);
                  System.arraycopy(Object2BooleanArrayMap.this.value, this.next + 1, Object2BooleanArrayMap.this.value, this.next, tail);
                  Object2BooleanArrayMap.this.key[Object2BooleanArrayMap.this.size] = null;
               }
            }

            @Override
            public void forEachRemaining(Consumer<? super Object2BooleanMap.Entry<K>> action) {
               int max = Object2BooleanArrayMap.this.size;

               while (this.next < max) {
                  this.entry.key = (K)Object2BooleanArrayMap.this.key[this.curr = this.next];
                  this.entry.value = Object2BooleanArrayMap.this.value[this.next++];
                  action.accept(this.entry);
               }
            }
         };
      }

      @Override
      public ObjectSpliterator<Object2BooleanMap.Entry<K>> spliterator() {
         return new Object2BooleanArrayMap.EntrySet.EntrySetSpliterator(0, Object2BooleanArrayMap.this.size);
      }

      @Override
      public void forEach(Consumer<? super Object2BooleanMap.Entry<K>> action) {
         int i = 0;

         for (int max = Object2BooleanArrayMap.this.size; i < max; i++) {
            action.accept(new AbstractObject2BooleanMap.BasicEntry<>((K)Object2BooleanArrayMap.this.key[i], Object2BooleanArrayMap.this.value[i]));
         }
      }

      @Override
      public void fastForEach(Consumer<? super Object2BooleanMap.Entry<K>> action) {
         AbstractObject2BooleanMap.BasicEntry<K> entry = new AbstractObject2BooleanMap.BasicEntry<>();
         int i = 0;

         for (int max = Object2BooleanArrayMap.this.size; i < max; i++) {
            entry.key = (K)Object2BooleanArrayMap.this.key[i];
            entry.value = Object2BooleanArrayMap.this.value[i];
            action.accept(entry);
         }
      }

      @Override
      public int size() {
         return Object2BooleanArrayMap.this.size;
      }

      @Override
      public boolean contains(Object o) {
         if (!(o instanceof Entry)) {
            return false;
         } else {
            Entry<?, ?> e = (Entry<?, ?>)o;
            if (e.getValue() != null && e.getValue() instanceof Boolean) {
               K k = (K)e.getKey();
               return Object2BooleanArrayMap.this.containsKey(k) && Object2BooleanArrayMap.this.getBoolean(k) == (Boolean)e.getValue();
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
            if (e.getValue() != null && e.getValue() instanceof Boolean) {
               K k = (K)e.getKey();
               boolean v = (Boolean)e.getValue();
               int oldPos = Object2BooleanArrayMap.this.findKey(k);
               if (oldPos != -1 && v == Object2BooleanArrayMap.this.value[oldPos]) {
                  int tail = Object2BooleanArrayMap.this.size - oldPos - 1;
                  System.arraycopy(Object2BooleanArrayMap.this.key, oldPos + 1, Object2BooleanArrayMap.this.key, oldPos, tail);
                  System.arraycopy(Object2BooleanArrayMap.this.value, oldPos + 1, Object2BooleanArrayMap.this.value, oldPos, tail);
                  Object2BooleanArrayMap.this.size--;
                  Object2BooleanArrayMap.this.key[Object2BooleanArrayMap.this.size] = null;
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
         extends ObjectSpliterators.EarlyBindingSizeIndexBasedSpliterator<Object2BooleanMap.Entry<K>>
         implements ObjectSpliterator<Object2BooleanMap.Entry<K>> {
         EntrySetSpliterator(int pos, int maxPos) {
            super(pos, maxPos);
         }

         @Override
         public int characteristics() {
            return 16465;
         }

         protected final Object2BooleanMap.Entry<K> get(int location) {
            return new AbstractObject2BooleanMap.BasicEntry<>((K)Object2BooleanArrayMap.this.key[location], Object2BooleanArrayMap.this.value[location]);
         }

         protected final Object2BooleanArrayMap<K>.EntrySet.EntrySetSpliterator makeForSplit(int pos, int maxPos) {
            return EntrySet.this.new EntrySetSpliterator(pos, maxPos);
         }
      }
   }

   private final class KeySet extends AbstractObjectSet<K> {
      private KeySet() {
      }

      @Override
      public boolean contains(Object k) {
         return Object2BooleanArrayMap.this.findKey(k) != -1;
      }

      @Override
      public boolean remove(Object k) {
         int oldPos = Object2BooleanArrayMap.this.findKey(k);
         if (oldPos == -1) {
            return false;
         } else {
            int tail = Object2BooleanArrayMap.this.size - oldPos - 1;
            System.arraycopy(Object2BooleanArrayMap.this.key, oldPos + 1, Object2BooleanArrayMap.this.key, oldPos, tail);
            System.arraycopy(Object2BooleanArrayMap.this.value, oldPos + 1, Object2BooleanArrayMap.this.value, oldPos, tail);
            Object2BooleanArrayMap.this.size--;
            Object2BooleanArrayMap.this.key[Object2BooleanArrayMap.this.size] = null;
            return true;
         }
      }

      @Override
      public ObjectIterator<K> iterator() {
         return new ObjectIterator<K>() {
            int pos = 0;

            @Override
            public boolean hasNext() {
               return this.pos < Object2BooleanArrayMap.this.size;
            }

            @Override
            public K next() {
               if (!this.hasNext()) {
                  throw new NoSuchElementException();
               } else {
                  return (K)Object2BooleanArrayMap.this.key[this.pos++];
               }
            }

            @Override
            public void remove() {
               if (this.pos == 0) {
                  throw new IllegalStateException();
               } else {
                  int tail = Object2BooleanArrayMap.this.size - this.pos;
                  System.arraycopy(Object2BooleanArrayMap.this.key, this.pos, Object2BooleanArrayMap.this.key, this.pos - 1, tail);
                  System.arraycopy(Object2BooleanArrayMap.this.value, this.pos, Object2BooleanArrayMap.this.value, this.pos - 1, tail);
                  Object2BooleanArrayMap.this.size--;
                  this.pos--;
                  Object2BooleanArrayMap.this.key[Object2BooleanArrayMap.this.size] = null;
               }
            }

            @Override
            public void forEachRemaining(Consumer<? super K> action) {
               int max = Object2BooleanArrayMap.this.size;

               while (this.pos < max) {
                  action.accept((K)Object2BooleanArrayMap.this.key[this.pos++]);
               }
            }
         };
      }

      @Override
      public ObjectSpliterator<K> spliterator() {
         return new Object2BooleanArrayMap.KeySet.KeySetSpliterator(0, Object2BooleanArrayMap.this.size);
      }

      @Override
      public void forEach(Consumer<? super K> action) {
         int i = 0;

         for (int max = Object2BooleanArrayMap.this.size; i < max; i++) {
            action.accept((K)Object2BooleanArrayMap.this.key[i]);
         }
      }

      @Override
      public int size() {
         return Object2BooleanArrayMap.this.size;
      }

      @Override
      public void clear() {
         Object2BooleanArrayMap.this.clear();
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
            return (K)Object2BooleanArrayMap.this.key[location];
         }

         protected final Object2BooleanArrayMap<K>.KeySet.KeySetSpliterator makeForSplit(int pos, int maxPos) {
            return KeySet.this.new KeySetSpliterator(pos, maxPos);
         }

         @Override
         public void forEachRemaining(Consumer<? super K> action) {
            int max = Object2BooleanArrayMap.this.size;

            while (this.pos < max) {
               action.accept((K)Object2BooleanArrayMap.this.key[this.pos++]);
            }
         }
      }
   }

   private final class ValuesCollection extends AbstractBooleanCollection {
      private ValuesCollection() {
      }

      @Override
      public boolean contains(boolean v) {
         return Object2BooleanArrayMap.this.containsValue(v);
      }

      @Override
      public BooleanIterator iterator() {
         return new BooleanIterator() {
            int pos = 0;

            @Override
            public boolean hasNext() {
               return this.pos < Object2BooleanArrayMap.this.size;
            }

            @Override
            public boolean nextBoolean() {
               if (!this.hasNext()) {
                  throw new NoSuchElementException();
               } else {
                  return Object2BooleanArrayMap.this.value[this.pos++];
               }
            }

            @Override
            public void remove() {
               if (this.pos == 0) {
                  throw new IllegalStateException();
               } else {
                  int tail = Object2BooleanArrayMap.this.size - this.pos;
                  System.arraycopy(Object2BooleanArrayMap.this.key, this.pos, Object2BooleanArrayMap.this.key, this.pos - 1, tail);
                  System.arraycopy(Object2BooleanArrayMap.this.value, this.pos, Object2BooleanArrayMap.this.value, this.pos - 1, tail);
                  Object2BooleanArrayMap.this.size--;
                  this.pos--;
                  Object2BooleanArrayMap.this.key[Object2BooleanArrayMap.this.size] = null;
               }
            }

            @Override
            public void forEachRemaining(BooleanConsumer action) {
               int max = Object2BooleanArrayMap.this.size;

               while (this.pos < max) {
                  action.accept(Object2BooleanArrayMap.this.value[this.pos++]);
               }
            }
         };
      }

      @Override
      public BooleanSpliterator spliterator() {
         return new Object2BooleanArrayMap.ValuesCollection.ValuesSpliterator(0, Object2BooleanArrayMap.this.size);
      }

      @Override
      public void forEach(BooleanConsumer action) {
         int i = 0;

         for (int max = Object2BooleanArrayMap.this.size; i < max; i++) {
            action.accept(Object2BooleanArrayMap.this.value[i]);
         }
      }

      @Override
      public int size() {
         return Object2BooleanArrayMap.this.size;
      }

      @Override
      public void clear() {
         Object2BooleanArrayMap.this.clear();
      }

      final class ValuesSpliterator extends BooleanSpliterators.EarlyBindingSizeIndexBasedSpliterator implements BooleanSpliterator {
         ValuesSpliterator(int pos, int maxPos) {
            super(pos, maxPos);
         }

         @Override
         public int characteristics() {
            return 16720;
         }

         @Override
         protected final boolean get(int location) {
            return Object2BooleanArrayMap.this.value[location];
         }

         protected final Object2BooleanArrayMap<K>.ValuesCollection.ValuesSpliterator makeForSplit(int pos, int maxPos) {
            return ValuesCollection.this.new ValuesSpliterator(pos, maxPos);
         }

         @Override
         public void forEachRemaining(BooleanConsumer action) {
            int max = Object2BooleanArrayMap.this.size;

            while (this.pos < max) {
               action.accept(Object2BooleanArrayMap.this.value[this.pos++]);
            }
         }
      }
   }
}
