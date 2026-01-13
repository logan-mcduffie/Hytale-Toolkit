package it.unimi.dsi.fastutil.objects;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Map.Entry;
import java.util.function.Consumer;

public class Reference2ObjectArrayMap<K, V> extends AbstractReference2ObjectMap<K, V> implements Serializable, Cloneable {
   private static final long serialVersionUID = 1L;
   protected transient Object[] key;
   protected transient Object[] value;
   protected int size;
   protected transient Reference2ObjectMap.FastEntrySet<K, V> entries;
   protected transient ReferenceSet<K> keys;
   protected transient ObjectCollection<V> values;

   public Reference2ObjectArrayMap(Object[] key, Object[] value) {
      this.key = key;
      this.value = value;
      this.size = key.length;
      if (key.length != value.length) {
         throw new IllegalArgumentException("Keys and values have different lengths (" + key.length + ", " + value.length + ")");
      }
   }

   public Reference2ObjectArrayMap() {
      this.key = ObjectArrays.EMPTY_ARRAY;
      this.value = ObjectArrays.EMPTY_ARRAY;
   }

   public Reference2ObjectArrayMap(int capacity) {
      this.key = new Object[capacity];
      this.value = new Object[capacity];
   }

   public Reference2ObjectArrayMap(Reference2ObjectMap<K, V> m) {
      this(m.size());
      int i = 0;

      for (Reference2ObjectMap.Entry<K, V> e : m.reference2ObjectEntrySet()) {
         this.key[i] = e.getKey();
         this.value[i] = e.getValue();
         i++;
      }

      this.size = i;
   }

   public Reference2ObjectArrayMap(Map<? extends K, ? extends V> m) {
      this(m.size());
      int i = 0;

      for (Entry<? extends K, ? extends V> e : m.entrySet()) {
         this.key[i] = e.getKey();
         this.value[i] = e.getValue();
         i++;
      }

      this.size = i;
   }

   public Reference2ObjectArrayMap(Object[] key, Object[] value, int size) {
      this.key = key;
      this.value = value;
      this.size = size;
      if (key.length != value.length) {
         throw new IllegalArgumentException("Keys and values have different lengths (" + key.length + ", " + value.length + ")");
      } else if (size > key.length) {
         throw new IllegalArgumentException("The provided size (" + size + ") is larger than or equal to the backing-arrays size (" + key.length + ")");
      }
   }

   public Reference2ObjectMap.FastEntrySet<K, V> reference2ObjectEntrySet() {
      if (this.entries == null) {
         this.entries = new Reference2ObjectArrayMap.EntrySet();
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
   public V get(Object k) {
      Object[] key = this.key;
      int i = this.size;

      while (i-- != 0) {
         if (key[i] == k) {
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
      for (int i = this.size; i-- != 0; this.value[i] = null) {
         this.key[i] = null;
      }

      this.size = 0;
   }

   @Override
   public boolean containsKey(Object k) {
      return this.findKey(k) != -1;
   }

   @Override
   public boolean containsValue(Object v) {
      int i = this.size;

      while (i-- != 0) {
         if (Objects.equals(this.value[i], v)) {
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
   public V put(K k, V v) {
      int oldKey = this.findKey(k);
      if (oldKey != -1) {
         V oldValue = (V)this.value[oldKey];
         this.value[oldKey] = v;
         return oldValue;
      } else {
         if (this.size == this.key.length) {
            Object[] newKey = new Object[this.size == 0 ? 2 : this.size * 2];
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
   public V remove(Object k) {
      int oldPos = this.findKey(k);
      if (oldPos == -1) {
         return this.defRetValue;
      } else {
         V oldValue = (V)this.value[oldPos];
         int tail = this.size - oldPos - 1;
         System.arraycopy(this.key, oldPos + 1, this.key, oldPos, tail);
         System.arraycopy(this.value, oldPos + 1, this.value, oldPos, tail);
         this.size--;
         this.key[this.size] = null;
         this.value[this.size] = null;
         return oldValue;
      }
   }

   @Override
   public ReferenceSet<K> keySet() {
      if (this.keys == null) {
         this.keys = new Reference2ObjectArrayMap.KeySet();
      }

      return this.keys;
   }

   @Override
   public ObjectCollection<V> values() {
      if (this.values == null) {
         this.values = new Reference2ObjectArrayMap.ValuesCollection();
      }

      return this.values;
   }

   public Reference2ObjectArrayMap<K, V> clone() {
      Reference2ObjectArrayMap<K, V> c;
      try {
         c = (Reference2ObjectArrayMap<K, V>)super.clone();
      } catch (CloneNotSupportedException var3) {
         throw new InternalError();
      }

      c.key = (Object[])this.key.clone();
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
         s.writeObject(this.key[i]);
         s.writeObject(this.value[i]);
      }
   }

   private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
      s.defaultReadObject();
      this.key = new Object[this.size];
      this.value = new Object[this.size];

      for (int i = 0; i < this.size; i++) {
         this.key[i] = s.readObject();
         this.value[i] = s.readObject();
      }
   }

   private final class EntrySet extends AbstractObjectSet<Reference2ObjectMap.Entry<K, V>> implements Reference2ObjectMap.FastEntrySet<K, V> {
      private EntrySet() {
      }

      @Override
      public ObjectIterator<Reference2ObjectMap.Entry<K, V>> iterator() {
         return new ObjectIterator<Reference2ObjectMap.Entry<K, V>>() {
            int curr = -1;
            int next = 0;

            @Override
            public boolean hasNext() {
               return this.next < Reference2ObjectArrayMap.this.size;
            }

            public Reference2ObjectMap.Entry<K, V> next() {
               if (!this.hasNext()) {
                  throw new NoSuchElementException();
               } else {
                  return new AbstractReference2ObjectMap.BasicEntry<>(
                     (K)Reference2ObjectArrayMap.this.key[this.curr = this.next], (V)Reference2ObjectArrayMap.this.value[this.next++]
                  );
               }
            }

            @Override
            public void remove() {
               if (this.curr == -1) {
                  throw new IllegalStateException();
               } else {
                  this.curr = -1;
                  int tail = Reference2ObjectArrayMap.this.size-- - this.next--;
                  System.arraycopy(Reference2ObjectArrayMap.this.key, this.next + 1, Reference2ObjectArrayMap.this.key, this.next, tail);
                  System.arraycopy(Reference2ObjectArrayMap.this.value, this.next + 1, Reference2ObjectArrayMap.this.value, this.next, tail);
                  Reference2ObjectArrayMap.this.key[Reference2ObjectArrayMap.this.size] = null;
                  Reference2ObjectArrayMap.this.value[Reference2ObjectArrayMap.this.size] = null;
               }
            }

            @Override
            public void forEachRemaining(Consumer<? super Reference2ObjectMap.Entry<K, V>> action) {
               int max = Reference2ObjectArrayMap.this.size;

               while (this.next < max) {
                  action.accept(
                     new AbstractReference2ObjectMap.BasicEntry<>(
                        (K)Reference2ObjectArrayMap.this.key[this.curr = this.next], (V)Reference2ObjectArrayMap.this.value[this.next++]
                     )
                  );
               }
            }
         };
      }

      @Override
      public ObjectIterator<Reference2ObjectMap.Entry<K, V>> fastIterator() {
         return new ObjectIterator<Reference2ObjectMap.Entry<K, V>>() {
            int next = 0;
            int curr = -1;
            final AbstractReference2ObjectMap.BasicEntry<K, V> entry = new AbstractReference2ObjectMap.BasicEntry<>();

            @Override
            public boolean hasNext() {
               return this.next < Reference2ObjectArrayMap.this.size;
            }

            public Reference2ObjectMap.Entry<K, V> next() {
               if (!this.hasNext()) {
                  throw new NoSuchElementException();
               } else {
                  this.entry.key = (K)Reference2ObjectArrayMap.this.key[this.curr = this.next];
                  this.entry.value = (V)Reference2ObjectArrayMap.this.value[this.next++];
                  return this.entry;
               }
            }

            @Override
            public void remove() {
               if (this.curr == -1) {
                  throw new IllegalStateException();
               } else {
                  this.curr = -1;
                  int tail = Reference2ObjectArrayMap.this.size-- - this.next--;
                  System.arraycopy(Reference2ObjectArrayMap.this.key, this.next + 1, Reference2ObjectArrayMap.this.key, this.next, tail);
                  System.arraycopy(Reference2ObjectArrayMap.this.value, this.next + 1, Reference2ObjectArrayMap.this.value, this.next, tail);
                  Reference2ObjectArrayMap.this.key[Reference2ObjectArrayMap.this.size] = null;
                  Reference2ObjectArrayMap.this.value[Reference2ObjectArrayMap.this.size] = null;
               }
            }

            @Override
            public void forEachRemaining(Consumer<? super Reference2ObjectMap.Entry<K, V>> action) {
               int max = Reference2ObjectArrayMap.this.size;

               while (this.next < max) {
                  this.entry.key = (K)Reference2ObjectArrayMap.this.key[this.curr = this.next];
                  this.entry.value = (V)Reference2ObjectArrayMap.this.value[this.next++];
                  action.accept(this.entry);
               }
            }
         };
      }

      @Override
      public ObjectSpliterator<Reference2ObjectMap.Entry<K, V>> spliterator() {
         return new Reference2ObjectArrayMap.EntrySet.EntrySetSpliterator(0, Reference2ObjectArrayMap.this.size);
      }

      @Override
      public void forEach(Consumer<? super Reference2ObjectMap.Entry<K, V>> action) {
         int i = 0;

         for (int max = Reference2ObjectArrayMap.this.size; i < max; i++) {
            action.accept(new AbstractReference2ObjectMap.BasicEntry<>((K)Reference2ObjectArrayMap.this.key[i], (V)Reference2ObjectArrayMap.this.value[i]));
         }
      }

      @Override
      public void fastForEach(Consumer<? super Reference2ObjectMap.Entry<K, V>> action) {
         AbstractReference2ObjectMap.BasicEntry<K, V> entry = new AbstractReference2ObjectMap.BasicEntry<>();
         int i = 0;

         for (int max = Reference2ObjectArrayMap.this.size; i < max; i++) {
            entry.key = (K)Reference2ObjectArrayMap.this.key[i];
            entry.value = (V)Reference2ObjectArrayMap.this.value[i];
            action.accept(entry);
         }
      }

      @Override
      public int size() {
         return Reference2ObjectArrayMap.this.size;
      }

      @Override
      public boolean contains(Object o) {
         if (!(o instanceof Entry)) {
            return false;
         } else {
            Entry<?, ?> e = (Entry<?, ?>)o;
            K k = (K)e.getKey();
            return Reference2ObjectArrayMap.this.containsKey(k) && Objects.equals(Reference2ObjectArrayMap.this.get(k), e.getValue());
         }
      }

      @Override
      public boolean remove(Object o) {
         if (!(o instanceof Entry)) {
            return false;
         } else {
            Entry<?, ?> e = (Entry<?, ?>)o;
            K k = (K)e.getKey();
            V v = (V)e.getValue();
            int oldPos = Reference2ObjectArrayMap.this.findKey(k);
            if (oldPos != -1 && Objects.equals(v, Reference2ObjectArrayMap.this.value[oldPos])) {
               int tail = Reference2ObjectArrayMap.this.size - oldPos - 1;
               System.arraycopy(Reference2ObjectArrayMap.this.key, oldPos + 1, Reference2ObjectArrayMap.this.key, oldPos, tail);
               System.arraycopy(Reference2ObjectArrayMap.this.value, oldPos + 1, Reference2ObjectArrayMap.this.value, oldPos, tail);
               Reference2ObjectArrayMap.this.size--;
               Reference2ObjectArrayMap.this.key[Reference2ObjectArrayMap.this.size] = null;
               Reference2ObjectArrayMap.this.value[Reference2ObjectArrayMap.this.size] = null;
               return true;
            } else {
               return false;
            }
         }
      }

      final class EntrySetSpliterator
         extends ObjectSpliterators.EarlyBindingSizeIndexBasedSpliterator<Reference2ObjectMap.Entry<K, V>>
         implements ObjectSpliterator<Reference2ObjectMap.Entry<K, V>> {
         EntrySetSpliterator(int pos, int maxPos) {
            super(pos, maxPos);
         }

         @Override
         public int characteristics() {
            return 16465;
         }

         protected final Reference2ObjectMap.Entry<K, V> get(int location) {
            return new AbstractReference2ObjectMap.BasicEntry<>(
               (K)Reference2ObjectArrayMap.this.key[location], (V)Reference2ObjectArrayMap.this.value[location]
            );
         }

         protected final Reference2ObjectArrayMap<K, V>.EntrySet.EntrySetSpliterator makeForSplit(int pos, int maxPos) {
            return EntrySet.this.new EntrySetSpliterator(pos, maxPos);
         }
      }
   }

   private final class KeySet extends AbstractReferenceSet<K> {
      private KeySet() {
      }

      @Override
      public boolean contains(Object k) {
         return Reference2ObjectArrayMap.this.findKey(k) != -1;
      }

      @Override
      public boolean remove(Object k) {
         int oldPos = Reference2ObjectArrayMap.this.findKey(k);
         if (oldPos == -1) {
            return false;
         } else {
            int tail = Reference2ObjectArrayMap.this.size - oldPos - 1;
            System.arraycopy(Reference2ObjectArrayMap.this.key, oldPos + 1, Reference2ObjectArrayMap.this.key, oldPos, tail);
            System.arraycopy(Reference2ObjectArrayMap.this.value, oldPos + 1, Reference2ObjectArrayMap.this.value, oldPos, tail);
            Reference2ObjectArrayMap.this.size--;
            Reference2ObjectArrayMap.this.key[Reference2ObjectArrayMap.this.size] = null;
            Reference2ObjectArrayMap.this.value[Reference2ObjectArrayMap.this.size] = null;
            return true;
         }
      }

      @Override
      public ObjectIterator<K> iterator() {
         return new ObjectIterator<K>() {
            int pos = 0;

            @Override
            public boolean hasNext() {
               return this.pos < Reference2ObjectArrayMap.this.size;
            }

            @Override
            public K next() {
               if (!this.hasNext()) {
                  throw new NoSuchElementException();
               } else {
                  return (K)Reference2ObjectArrayMap.this.key[this.pos++];
               }
            }

            @Override
            public void remove() {
               if (this.pos == 0) {
                  throw new IllegalStateException();
               } else {
                  int tail = Reference2ObjectArrayMap.this.size - this.pos;
                  System.arraycopy(Reference2ObjectArrayMap.this.key, this.pos, Reference2ObjectArrayMap.this.key, this.pos - 1, tail);
                  System.arraycopy(Reference2ObjectArrayMap.this.value, this.pos, Reference2ObjectArrayMap.this.value, this.pos - 1, tail);
                  Reference2ObjectArrayMap.this.size--;
                  this.pos--;
                  Reference2ObjectArrayMap.this.key[Reference2ObjectArrayMap.this.size] = null;
                  Reference2ObjectArrayMap.this.value[Reference2ObjectArrayMap.this.size] = null;
               }
            }

            @Override
            public void forEachRemaining(Consumer<? super K> action) {
               int max = Reference2ObjectArrayMap.this.size;

               while (this.pos < max) {
                  action.accept((K)Reference2ObjectArrayMap.this.key[this.pos++]);
               }
            }
         };
      }

      @Override
      public ObjectSpliterator<K> spliterator() {
         return new Reference2ObjectArrayMap.KeySet.KeySetSpliterator(0, Reference2ObjectArrayMap.this.size);
      }

      @Override
      public void forEach(Consumer<? super K> action) {
         int i = 0;

         for (int max = Reference2ObjectArrayMap.this.size; i < max; i++) {
            action.accept((K)Reference2ObjectArrayMap.this.key[i]);
         }
      }

      @Override
      public int size() {
         return Reference2ObjectArrayMap.this.size;
      }

      @Override
      public void clear() {
         Reference2ObjectArrayMap.this.clear();
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
            return (K)Reference2ObjectArrayMap.this.key[location];
         }

         protected final Reference2ObjectArrayMap<K, V>.KeySet.KeySetSpliterator makeForSplit(int pos, int maxPos) {
            return KeySet.this.new KeySetSpliterator(pos, maxPos);
         }

         @Override
         public void forEachRemaining(Consumer<? super K> action) {
            int max = Reference2ObjectArrayMap.this.size;

            while (this.pos < max) {
               action.accept((K)Reference2ObjectArrayMap.this.key[this.pos++]);
            }
         }
      }
   }

   private final class ValuesCollection extends AbstractObjectCollection<V> {
      private ValuesCollection() {
      }

      @Override
      public boolean contains(Object v) {
         return Reference2ObjectArrayMap.this.containsValue(v);
      }

      @Override
      public ObjectIterator<V> iterator() {
         return new ObjectIterator<V>() {
            int pos = 0;

            @Override
            public boolean hasNext() {
               return this.pos < Reference2ObjectArrayMap.this.size;
            }

            @Override
            public V next() {
               if (!this.hasNext()) {
                  throw new NoSuchElementException();
               } else {
                  return (V)Reference2ObjectArrayMap.this.value[this.pos++];
               }
            }

            @Override
            public void remove() {
               if (this.pos == 0) {
                  throw new IllegalStateException();
               } else {
                  int tail = Reference2ObjectArrayMap.this.size - this.pos;
                  System.arraycopy(Reference2ObjectArrayMap.this.key, this.pos, Reference2ObjectArrayMap.this.key, this.pos - 1, tail);
                  System.arraycopy(Reference2ObjectArrayMap.this.value, this.pos, Reference2ObjectArrayMap.this.value, this.pos - 1, tail);
                  Reference2ObjectArrayMap.this.size--;
                  this.pos--;
                  Reference2ObjectArrayMap.this.key[Reference2ObjectArrayMap.this.size] = null;
                  Reference2ObjectArrayMap.this.value[Reference2ObjectArrayMap.this.size] = null;
               }
            }

            @Override
            public void forEachRemaining(Consumer<? super V> action) {
               int max = Reference2ObjectArrayMap.this.size;

               while (this.pos < max) {
                  action.accept((V)Reference2ObjectArrayMap.this.value[this.pos++]);
               }
            }
         };
      }

      @Override
      public ObjectSpliterator<V> spliterator() {
         return new Reference2ObjectArrayMap.ValuesCollection.ValuesSpliterator(0, Reference2ObjectArrayMap.this.size);
      }

      @Override
      public void forEach(Consumer<? super V> action) {
         int i = 0;

         for (int max = Reference2ObjectArrayMap.this.size; i < max; i++) {
            action.accept((V)Reference2ObjectArrayMap.this.value[i]);
         }
      }

      @Override
      public int size() {
         return Reference2ObjectArrayMap.this.size;
      }

      @Override
      public void clear() {
         Reference2ObjectArrayMap.this.clear();
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
            return (V)Reference2ObjectArrayMap.this.value[location];
         }

         protected final Reference2ObjectArrayMap<K, V>.ValuesCollection.ValuesSpliterator makeForSplit(int pos, int maxPos) {
            return ValuesCollection.this.new ValuesSpliterator(pos, maxPos);
         }

         @Override
         public void forEachRemaining(Consumer<? super V> action) {
            int max = Reference2ObjectArrayMap.this.size;

            while (this.pos < max) {
               action.accept((V)Reference2ObjectArrayMap.this.value[this.pos++]);
            }
         }
      }
   }
}
