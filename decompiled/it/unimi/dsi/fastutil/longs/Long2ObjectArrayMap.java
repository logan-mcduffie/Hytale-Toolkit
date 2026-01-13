package it.unimi.dsi.fastutil.longs;

import it.unimi.dsi.fastutil.objects.AbstractObjectCollection;
import it.unimi.dsi.fastutil.objects.AbstractObjectSet;
import it.unimi.dsi.fastutil.objects.ObjectArrays;
import it.unimi.dsi.fastutil.objects.ObjectCollection;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectSpliterator;
import it.unimi.dsi.fastutil.objects.ObjectSpliterators;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Map.Entry;
import java.util.function.Consumer;

public class Long2ObjectArrayMap<V> extends AbstractLong2ObjectMap<V> implements Serializable, Cloneable {
   private static final long serialVersionUID = 1L;
   protected transient long[] key;
   protected transient Object[] value;
   protected int size;
   protected transient Long2ObjectMap.FastEntrySet<V> entries;
   protected transient LongSet keys;
   protected transient ObjectCollection<V> values;

   public Long2ObjectArrayMap(long[] key, Object[] value) {
      this.key = key;
      this.value = value;
      this.size = key.length;
      if (key.length != value.length) {
         throw new IllegalArgumentException("Keys and values have different lengths (" + key.length + ", " + value.length + ")");
      }
   }

   public Long2ObjectArrayMap() {
      this.key = LongArrays.EMPTY_ARRAY;
      this.value = ObjectArrays.EMPTY_ARRAY;
   }

   public Long2ObjectArrayMap(int capacity) {
      this.key = new long[capacity];
      this.value = new Object[capacity];
   }

   public Long2ObjectArrayMap(Long2ObjectMap<V> m) {
      this(m.size());
      int i = 0;

      for (Long2ObjectMap.Entry<V> e : m.long2ObjectEntrySet()) {
         this.key[i] = e.getLongKey();
         this.value[i] = e.getValue();
         i++;
      }

      this.size = i;
   }

   public Long2ObjectArrayMap(Map<? extends Long, ? extends V> m) {
      this(m.size());
      int i = 0;

      for (Entry<? extends Long, ? extends V> e : m.entrySet()) {
         this.key[i] = e.getKey();
         this.value[i] = e.getValue();
         i++;
      }

      this.size = i;
   }

   public Long2ObjectArrayMap(long[] key, Object[] value, int size) {
      this.key = key;
      this.value = value;
      this.size = size;
      if (key.length != value.length) {
         throw new IllegalArgumentException("Keys and values have different lengths (" + key.length + ", " + value.length + ")");
      } else if (size > key.length) {
         throw new IllegalArgumentException("The provided size (" + size + ") is larger than or equal to the backing-arrays size (" + key.length + ")");
      }
   }

   public Long2ObjectMap.FastEntrySet<V> long2ObjectEntrySet() {
      if (this.entries == null) {
         this.entries = new Long2ObjectArrayMap.EntrySet();
      }

      return this.entries;
   }

   private int findKey(long k) {
      long[] key = this.key;
      int i = this.size;

      while (i-- != 0) {
         if (key[i] == k) {
            return i;
         }
      }

      return -1;
   }

   @Override
   public V get(long k) {
      long[] key = this.key;
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
      int i = this.size;

      while (i-- != 0) {
         this.value[i] = null;
      }

      this.size = 0;
   }

   @Override
   public boolean containsKey(long k) {
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
   public V put(long k, V v) {
      int oldKey = this.findKey(k);
      if (oldKey != -1) {
         V oldValue = (V)this.value[oldKey];
         this.value[oldKey] = v;
         return oldValue;
      } else {
         if (this.size == this.key.length) {
            long[] newKey = new long[this.size == 0 ? 2 : this.size * 2];
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
   public V remove(long k) {
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
   public LongSet keySet() {
      if (this.keys == null) {
         this.keys = new Long2ObjectArrayMap.KeySet();
      }

      return this.keys;
   }

   @Override
   public ObjectCollection<V> values() {
      if (this.values == null) {
         this.values = new Long2ObjectArrayMap.ValuesCollection();
      }

      return this.values;
   }

   public Long2ObjectArrayMap<V> clone() {
      Long2ObjectArrayMap<V> c;
      try {
         c = (Long2ObjectArrayMap<V>)super.clone();
      } catch (CloneNotSupportedException var3) {
         throw new InternalError();
      }

      c.key = (long[])this.key.clone();
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
         s.writeLong(this.key[i]);
         s.writeObject(this.value[i]);
      }
   }

   private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
      s.defaultReadObject();
      this.key = new long[this.size];
      this.value = new Object[this.size];

      for (int i = 0; i < this.size; i++) {
         this.key[i] = s.readLong();
         this.value[i] = s.readObject();
      }
   }

   private final class EntrySet extends AbstractObjectSet<Long2ObjectMap.Entry<V>> implements Long2ObjectMap.FastEntrySet<V> {
      private EntrySet() {
      }

      @Override
      public ObjectIterator<Long2ObjectMap.Entry<V>> iterator() {
         return new ObjectIterator<Long2ObjectMap.Entry<V>>() {
            int curr = -1;
            int next = 0;

            @Override
            public boolean hasNext() {
               return this.next < Long2ObjectArrayMap.this.size;
            }

            public Long2ObjectMap.Entry<V> next() {
               if (!this.hasNext()) {
                  throw new NoSuchElementException();
               } else {
                  return new AbstractLong2ObjectMap.BasicEntry<>(
                     Long2ObjectArrayMap.this.key[this.curr = this.next], (V)Long2ObjectArrayMap.this.value[this.next++]
                  );
               }
            }

            @Override
            public void remove() {
               if (this.curr == -1) {
                  throw new IllegalStateException();
               } else {
                  this.curr = -1;
                  int tail = Long2ObjectArrayMap.this.size-- - this.next--;
                  System.arraycopy(Long2ObjectArrayMap.this.key, this.next + 1, Long2ObjectArrayMap.this.key, this.next, tail);
                  System.arraycopy(Long2ObjectArrayMap.this.value, this.next + 1, Long2ObjectArrayMap.this.value, this.next, tail);
                  Long2ObjectArrayMap.this.value[Long2ObjectArrayMap.this.size] = null;
               }
            }

            @Override
            public void forEachRemaining(Consumer<? super Long2ObjectMap.Entry<V>> action) {
               int max = Long2ObjectArrayMap.this.size;

               while (this.next < max) {
                  action.accept(
                     new AbstractLong2ObjectMap.BasicEntry<>(
                        Long2ObjectArrayMap.this.key[this.curr = this.next], (V)Long2ObjectArrayMap.this.value[this.next++]
                     )
                  );
               }
            }
         };
      }

      @Override
      public ObjectIterator<Long2ObjectMap.Entry<V>> fastIterator() {
         return new ObjectIterator<Long2ObjectMap.Entry<V>>() {
            int next = 0;
            int curr = -1;
            final AbstractLong2ObjectMap.BasicEntry<V> entry = new AbstractLong2ObjectMap.BasicEntry<>();

            @Override
            public boolean hasNext() {
               return this.next < Long2ObjectArrayMap.this.size;
            }

            public Long2ObjectMap.Entry<V> next() {
               if (!this.hasNext()) {
                  throw new NoSuchElementException();
               } else {
                  this.entry.key = Long2ObjectArrayMap.this.key[this.curr = this.next];
                  this.entry.value = (V)Long2ObjectArrayMap.this.value[this.next++];
                  return this.entry;
               }
            }

            @Override
            public void remove() {
               if (this.curr == -1) {
                  throw new IllegalStateException();
               } else {
                  this.curr = -1;
                  int tail = Long2ObjectArrayMap.this.size-- - this.next--;
                  System.arraycopy(Long2ObjectArrayMap.this.key, this.next + 1, Long2ObjectArrayMap.this.key, this.next, tail);
                  System.arraycopy(Long2ObjectArrayMap.this.value, this.next + 1, Long2ObjectArrayMap.this.value, this.next, tail);
                  Long2ObjectArrayMap.this.value[Long2ObjectArrayMap.this.size] = null;
               }
            }

            @Override
            public void forEachRemaining(Consumer<? super Long2ObjectMap.Entry<V>> action) {
               int max = Long2ObjectArrayMap.this.size;

               while (this.next < max) {
                  this.entry.key = Long2ObjectArrayMap.this.key[this.curr = this.next];
                  this.entry.value = (V)Long2ObjectArrayMap.this.value[this.next++];
                  action.accept(this.entry);
               }
            }
         };
      }

      @Override
      public ObjectSpliterator<Long2ObjectMap.Entry<V>> spliterator() {
         return new Long2ObjectArrayMap.EntrySet.EntrySetSpliterator(0, Long2ObjectArrayMap.this.size);
      }

      @Override
      public void forEach(Consumer<? super Long2ObjectMap.Entry<V>> action) {
         int i = 0;

         for (int max = Long2ObjectArrayMap.this.size; i < max; i++) {
            action.accept(new AbstractLong2ObjectMap.BasicEntry<>(Long2ObjectArrayMap.this.key[i], (V)Long2ObjectArrayMap.this.value[i]));
         }
      }

      @Override
      public void fastForEach(Consumer<? super Long2ObjectMap.Entry<V>> action) {
         AbstractLong2ObjectMap.BasicEntry<V> entry = new AbstractLong2ObjectMap.BasicEntry<>();
         int i = 0;

         for (int max = Long2ObjectArrayMap.this.size; i < max; i++) {
            entry.key = Long2ObjectArrayMap.this.key[i];
            entry.value = (V)Long2ObjectArrayMap.this.value[i];
            action.accept(entry);
         }
      }

      @Override
      public int size() {
         return Long2ObjectArrayMap.this.size;
      }

      @Override
      public boolean contains(Object o) {
         if (!(o instanceof Entry)) {
            return false;
         } else {
            Entry<?, ?> e = (Entry<?, ?>)o;
            if (e.getKey() != null && e.getKey() instanceof Long) {
               long k = (Long)e.getKey();
               return Long2ObjectArrayMap.this.containsKey(k) && Objects.equals(Long2ObjectArrayMap.this.get(k), e.getValue());
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
            if (e.getKey() != null && e.getKey() instanceof Long) {
               long k = (Long)e.getKey();
               V v = (V)e.getValue();
               int oldPos = Long2ObjectArrayMap.this.findKey(k);
               if (oldPos != -1 && Objects.equals(v, Long2ObjectArrayMap.this.value[oldPos])) {
                  int tail = Long2ObjectArrayMap.this.size - oldPos - 1;
                  System.arraycopy(Long2ObjectArrayMap.this.key, oldPos + 1, Long2ObjectArrayMap.this.key, oldPos, tail);
                  System.arraycopy(Long2ObjectArrayMap.this.value, oldPos + 1, Long2ObjectArrayMap.this.value, oldPos, tail);
                  Long2ObjectArrayMap.this.size--;
                  Long2ObjectArrayMap.this.value[Long2ObjectArrayMap.this.size] = null;
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
         extends ObjectSpliterators.EarlyBindingSizeIndexBasedSpliterator<Long2ObjectMap.Entry<V>>
         implements ObjectSpliterator<Long2ObjectMap.Entry<V>> {
         EntrySetSpliterator(int pos, int maxPos) {
            super(pos, maxPos);
         }

         @Override
         public int characteristics() {
            return 16465;
         }

         protected final Long2ObjectMap.Entry<V> get(int location) {
            return new AbstractLong2ObjectMap.BasicEntry<>(Long2ObjectArrayMap.this.key[location], (V)Long2ObjectArrayMap.this.value[location]);
         }

         protected final Long2ObjectArrayMap<V>.EntrySet.EntrySetSpliterator makeForSplit(int pos, int maxPos) {
            return EntrySet.this.new EntrySetSpliterator(pos, maxPos);
         }
      }
   }

   private final class KeySet extends AbstractLongSet {
      private KeySet() {
      }

      @Override
      public boolean contains(long k) {
         return Long2ObjectArrayMap.this.findKey(k) != -1;
      }

      @Override
      public boolean remove(long k) {
         int oldPos = Long2ObjectArrayMap.this.findKey(k);
         if (oldPos == -1) {
            return false;
         } else {
            int tail = Long2ObjectArrayMap.this.size - oldPos - 1;
            System.arraycopy(Long2ObjectArrayMap.this.key, oldPos + 1, Long2ObjectArrayMap.this.key, oldPos, tail);
            System.arraycopy(Long2ObjectArrayMap.this.value, oldPos + 1, Long2ObjectArrayMap.this.value, oldPos, tail);
            Long2ObjectArrayMap.this.size--;
            Long2ObjectArrayMap.this.value[Long2ObjectArrayMap.this.size] = null;
            return true;
         }
      }

      @Override
      public LongIterator iterator() {
         return new LongIterator() {
            int pos = 0;

            @Override
            public boolean hasNext() {
               return this.pos < Long2ObjectArrayMap.this.size;
            }

            @Override
            public long nextLong() {
               if (!this.hasNext()) {
                  throw new NoSuchElementException();
               } else {
                  return Long2ObjectArrayMap.this.key[this.pos++];
               }
            }

            @Override
            public void remove() {
               if (this.pos == 0) {
                  throw new IllegalStateException();
               } else {
                  int tail = Long2ObjectArrayMap.this.size - this.pos;
                  System.arraycopy(Long2ObjectArrayMap.this.key, this.pos, Long2ObjectArrayMap.this.key, this.pos - 1, tail);
                  System.arraycopy(Long2ObjectArrayMap.this.value, this.pos, Long2ObjectArrayMap.this.value, this.pos - 1, tail);
                  Long2ObjectArrayMap.this.size--;
                  this.pos--;
                  Long2ObjectArrayMap.this.value[Long2ObjectArrayMap.this.size] = null;
               }
            }

            @Override
            public void forEachRemaining(java.util.function.LongConsumer action) {
               int max = Long2ObjectArrayMap.this.size;

               while (this.pos < max) {
                  action.accept(Long2ObjectArrayMap.this.key[this.pos++]);
               }
            }
         };
      }

      @Override
      public LongSpliterator spliterator() {
         return new Long2ObjectArrayMap.KeySet.KeySetSpliterator(0, Long2ObjectArrayMap.this.size);
      }

      @Override
      public void forEach(java.util.function.LongConsumer action) {
         int i = 0;

         for (int max = Long2ObjectArrayMap.this.size; i < max; i++) {
            action.accept(Long2ObjectArrayMap.this.key[i]);
         }
      }

      @Override
      public int size() {
         return Long2ObjectArrayMap.this.size;
      }

      @Override
      public void clear() {
         Long2ObjectArrayMap.this.clear();
      }

      final class KeySetSpliterator extends LongSpliterators.EarlyBindingSizeIndexBasedSpliterator implements LongSpliterator {
         KeySetSpliterator(int pos, int maxPos) {
            super(pos, maxPos);
         }

         @Override
         public int characteristics() {
            return 16721;
         }

         @Override
         protected final long get(int location) {
            return Long2ObjectArrayMap.this.key[location];
         }

         protected final Long2ObjectArrayMap<V>.KeySet.KeySetSpliterator makeForSplit(int pos, int maxPos) {
            return KeySet.this.new KeySetSpliterator(pos, maxPos);
         }

         @Override
         public void forEachRemaining(java.util.function.LongConsumer action) {
            int max = Long2ObjectArrayMap.this.size;

            while (this.pos < max) {
               action.accept(Long2ObjectArrayMap.this.key[this.pos++]);
            }
         }
      }
   }

   private final class ValuesCollection extends AbstractObjectCollection<V> {
      private ValuesCollection() {
      }

      @Override
      public boolean contains(Object v) {
         return Long2ObjectArrayMap.this.containsValue(v);
      }

      @Override
      public ObjectIterator<V> iterator() {
         return new ObjectIterator<V>() {
            int pos = 0;

            @Override
            public boolean hasNext() {
               return this.pos < Long2ObjectArrayMap.this.size;
            }

            @Override
            public V next() {
               if (!this.hasNext()) {
                  throw new NoSuchElementException();
               } else {
                  return (V)Long2ObjectArrayMap.this.value[this.pos++];
               }
            }

            @Override
            public void remove() {
               if (this.pos == 0) {
                  throw new IllegalStateException();
               } else {
                  int tail = Long2ObjectArrayMap.this.size - this.pos;
                  System.arraycopy(Long2ObjectArrayMap.this.key, this.pos, Long2ObjectArrayMap.this.key, this.pos - 1, tail);
                  System.arraycopy(Long2ObjectArrayMap.this.value, this.pos, Long2ObjectArrayMap.this.value, this.pos - 1, tail);
                  Long2ObjectArrayMap.this.size--;
                  this.pos--;
                  Long2ObjectArrayMap.this.value[Long2ObjectArrayMap.this.size] = null;
               }
            }

            @Override
            public void forEachRemaining(Consumer<? super V> action) {
               int max = Long2ObjectArrayMap.this.size;

               while (this.pos < max) {
                  action.accept((V)Long2ObjectArrayMap.this.value[this.pos++]);
               }
            }
         };
      }

      @Override
      public ObjectSpliterator<V> spliterator() {
         return new Long2ObjectArrayMap.ValuesCollection.ValuesSpliterator(0, Long2ObjectArrayMap.this.size);
      }

      @Override
      public void forEach(Consumer<? super V> action) {
         int i = 0;

         for (int max = Long2ObjectArrayMap.this.size; i < max; i++) {
            action.accept((V)Long2ObjectArrayMap.this.value[i]);
         }
      }

      @Override
      public int size() {
         return Long2ObjectArrayMap.this.size;
      }

      @Override
      public void clear() {
         Long2ObjectArrayMap.this.clear();
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
            return (V)Long2ObjectArrayMap.this.value[location];
         }

         protected final Long2ObjectArrayMap<V>.ValuesCollection.ValuesSpliterator makeForSplit(int pos, int maxPos) {
            return ValuesCollection.this.new ValuesSpliterator(pos, maxPos);
         }

         @Override
         public void forEachRemaining(Consumer<? super V> action) {
            int max = Long2ObjectArrayMap.this.size;

            while (this.pos < max) {
               action.accept((V)Long2ObjectArrayMap.this.value[this.pos++]);
            }
         }
      }
   }
}
