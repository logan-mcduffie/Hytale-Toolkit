package it.unimi.dsi.fastutil.longs;

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

public class Long2LongArrayMap extends AbstractLong2LongMap implements Serializable, Cloneable {
   private static final long serialVersionUID = 1L;
   protected transient long[] key;
   protected transient long[] value;
   protected int size;
   protected transient Long2LongMap.FastEntrySet entries;
   protected transient LongSet keys;
   protected transient LongCollection values;

   public Long2LongArrayMap(long[] key, long[] value) {
      this.key = key;
      this.value = value;
      this.size = key.length;
      if (key.length != value.length) {
         throw new IllegalArgumentException("Keys and values have different lengths (" + key.length + ", " + value.length + ")");
      }
   }

   public Long2LongArrayMap() {
      this.key = LongArrays.EMPTY_ARRAY;
      this.value = LongArrays.EMPTY_ARRAY;
   }

   public Long2LongArrayMap(int capacity) {
      this.key = new long[capacity];
      this.value = new long[capacity];
   }

   public Long2LongArrayMap(Long2LongMap m) {
      this(m.size());
      int i = 0;

      for (Long2LongMap.Entry e : m.long2LongEntrySet()) {
         this.key[i] = e.getLongKey();
         this.value[i] = e.getLongValue();
         i++;
      }

      this.size = i;
   }

   public Long2LongArrayMap(Map<? extends Long, ? extends Long> m) {
      this(m.size());
      int i = 0;

      for (Entry<? extends Long, ? extends Long> e : m.entrySet()) {
         this.key[i] = e.getKey();
         this.value[i] = e.getValue();
         i++;
      }

      this.size = i;
   }

   public Long2LongArrayMap(long[] key, long[] value, int size) {
      this.key = key;
      this.value = value;
      this.size = size;
      if (key.length != value.length) {
         throw new IllegalArgumentException("Keys and values have different lengths (" + key.length + ", " + value.length + ")");
      } else if (size > key.length) {
         throw new IllegalArgumentException("The provided size (" + size + ") is larger than or equal to the backing-arrays size (" + key.length + ")");
      }
   }

   public Long2LongMap.FastEntrySet long2LongEntrySet() {
      if (this.entries == null) {
         this.entries = new Long2LongArrayMap.EntrySet();
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
   public long get(long k) {
      long[] key = this.key;
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
   public boolean containsKey(long k) {
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
   public long put(long k, long v) {
      int oldKey = this.findKey(k);
      if (oldKey != -1) {
         long oldValue = this.value[oldKey];
         this.value[oldKey] = v;
         return oldValue;
      } else {
         if (this.size == this.key.length) {
            long[] newKey = new long[this.size == 0 ? 2 : this.size * 2];
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
   public long remove(long k) {
      int oldPos = this.findKey(k);
      if (oldPos == -1) {
         return this.defRetValue;
      } else {
         long oldValue = this.value[oldPos];
         int tail = this.size - oldPos - 1;
         System.arraycopy(this.key, oldPos + 1, this.key, oldPos, tail);
         System.arraycopy(this.value, oldPos + 1, this.value, oldPos, tail);
         this.size--;
         return oldValue;
      }
   }

   @Override
   public LongSet keySet() {
      if (this.keys == null) {
         this.keys = new Long2LongArrayMap.KeySet();
      }

      return this.keys;
   }

   @Override
   public LongCollection values() {
      if (this.values == null) {
         this.values = new Long2LongArrayMap.ValuesCollection();
      }

      return this.values;
   }

   public Long2LongArrayMap clone() {
      Long2LongArrayMap c;
      try {
         c = (Long2LongArrayMap)super.clone();
      } catch (CloneNotSupportedException var3) {
         throw new InternalError();
      }

      c.key = (long[])this.key.clone();
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
         s.writeLong(this.key[i]);
         s.writeLong(this.value[i]);
      }
   }

   private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
      s.defaultReadObject();
      this.key = new long[this.size];
      this.value = new long[this.size];

      for (int i = 0; i < this.size; i++) {
         this.key[i] = s.readLong();
         this.value[i] = s.readLong();
      }
   }

   private final class EntrySet extends AbstractObjectSet<Long2LongMap.Entry> implements Long2LongMap.FastEntrySet {
      private EntrySet() {
      }

      @Override
      public ObjectIterator<Long2LongMap.Entry> iterator() {
         return new ObjectIterator<Long2LongMap.Entry>() {
            int curr = -1;
            int next = 0;

            @Override
            public boolean hasNext() {
               return this.next < Long2LongArrayMap.this.size;
            }

            public Long2LongMap.Entry next() {
               if (!this.hasNext()) {
                  throw new NoSuchElementException();
               } else {
                  return new AbstractLong2LongMap.BasicEntry(Long2LongArrayMap.this.key[this.curr = this.next], Long2LongArrayMap.this.value[this.next++]);
               }
            }

            @Override
            public void remove() {
               if (this.curr == -1) {
                  throw new IllegalStateException();
               } else {
                  this.curr = -1;
                  int tail = Long2LongArrayMap.this.size-- - this.next--;
                  System.arraycopy(Long2LongArrayMap.this.key, this.next + 1, Long2LongArrayMap.this.key, this.next, tail);
                  System.arraycopy(Long2LongArrayMap.this.value, this.next + 1, Long2LongArrayMap.this.value, this.next, tail);
               }
            }

            @Override
            public void forEachRemaining(Consumer<? super Long2LongMap.Entry> action) {
               int max = Long2LongArrayMap.this.size;

               while (this.next < max) {
                  action.accept(
                     new AbstractLong2LongMap.BasicEntry(Long2LongArrayMap.this.key[this.curr = this.next], Long2LongArrayMap.this.value[this.next++])
                  );
               }
            }
         };
      }

      @Override
      public ObjectIterator<Long2LongMap.Entry> fastIterator() {
         return new ObjectIterator<Long2LongMap.Entry>() {
            int next = 0;
            int curr = -1;
            final AbstractLong2LongMap.BasicEntry entry = new AbstractLong2LongMap.BasicEntry();

            @Override
            public boolean hasNext() {
               return this.next < Long2LongArrayMap.this.size;
            }

            public Long2LongMap.Entry next() {
               if (!this.hasNext()) {
                  throw new NoSuchElementException();
               } else {
                  this.entry.key = Long2LongArrayMap.this.key[this.curr = this.next];
                  this.entry.value = Long2LongArrayMap.this.value[this.next++];
                  return this.entry;
               }
            }

            @Override
            public void remove() {
               if (this.curr == -1) {
                  throw new IllegalStateException();
               } else {
                  this.curr = -1;
                  int tail = Long2LongArrayMap.this.size-- - this.next--;
                  System.arraycopy(Long2LongArrayMap.this.key, this.next + 1, Long2LongArrayMap.this.key, this.next, tail);
                  System.arraycopy(Long2LongArrayMap.this.value, this.next + 1, Long2LongArrayMap.this.value, this.next, tail);
               }
            }

            @Override
            public void forEachRemaining(Consumer<? super Long2LongMap.Entry> action) {
               int max = Long2LongArrayMap.this.size;

               while (this.next < max) {
                  this.entry.key = Long2LongArrayMap.this.key[this.curr = this.next];
                  this.entry.value = Long2LongArrayMap.this.value[this.next++];
                  action.accept(this.entry);
               }
            }
         };
      }

      @Override
      public ObjectSpliterator<Long2LongMap.Entry> spliterator() {
         return new Long2LongArrayMap.EntrySet.EntrySetSpliterator(0, Long2LongArrayMap.this.size);
      }

      @Override
      public void forEach(Consumer<? super Long2LongMap.Entry> action) {
         int i = 0;

         for (int max = Long2LongArrayMap.this.size; i < max; i++) {
            action.accept(new AbstractLong2LongMap.BasicEntry(Long2LongArrayMap.this.key[i], Long2LongArrayMap.this.value[i]));
         }
      }

      @Override
      public void fastForEach(Consumer<? super Long2LongMap.Entry> action) {
         AbstractLong2LongMap.BasicEntry entry = new AbstractLong2LongMap.BasicEntry();
         int i = 0;

         for (int max = Long2LongArrayMap.this.size; i < max; i++) {
            entry.key = Long2LongArrayMap.this.key[i];
            entry.value = Long2LongArrayMap.this.value[i];
            action.accept(entry);
         }
      }

      @Override
      public int size() {
         return Long2LongArrayMap.this.size;
      }

      @Override
      public boolean contains(Object o) {
         if (!(o instanceof Entry)) {
            return false;
         } else {
            Entry<?, ?> e = (Entry<?, ?>)o;
            if (e.getKey() == null || !(e.getKey() instanceof Long)) {
               return false;
            } else if (e.getValue() != null && e.getValue() instanceof Long) {
               long k = (Long)e.getKey();
               return Long2LongArrayMap.this.containsKey(k) && Long2LongArrayMap.this.get(k) == (Long)e.getValue();
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
            if (e.getKey() == null || !(e.getKey() instanceof Long)) {
               return false;
            } else if (e.getValue() != null && e.getValue() instanceof Long) {
               long k = (Long)e.getKey();
               long v = (Long)e.getValue();
               int oldPos = Long2LongArrayMap.this.findKey(k);
               if (oldPos != -1 && v == Long2LongArrayMap.this.value[oldPos]) {
                  int tail = Long2LongArrayMap.this.size - oldPos - 1;
                  System.arraycopy(Long2LongArrayMap.this.key, oldPos + 1, Long2LongArrayMap.this.key, oldPos, tail);
                  System.arraycopy(Long2LongArrayMap.this.value, oldPos + 1, Long2LongArrayMap.this.value, oldPos, tail);
                  Long2LongArrayMap.this.size--;
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
         extends ObjectSpliterators.EarlyBindingSizeIndexBasedSpliterator<Long2LongMap.Entry>
         implements ObjectSpliterator<Long2LongMap.Entry> {
         EntrySetSpliterator(int pos, int maxPos) {
            super(pos, maxPos);
         }

         @Override
         public int characteristics() {
            return 16465;
         }

         protected final Long2LongMap.Entry get(int location) {
            return new AbstractLong2LongMap.BasicEntry(Long2LongArrayMap.this.key[location], Long2LongArrayMap.this.value[location]);
         }

         protected final Long2LongArrayMap.EntrySet.EntrySetSpliterator makeForSplit(int pos, int maxPos) {
            return EntrySet.this.new EntrySetSpliterator(pos, maxPos);
         }
      }
   }

   private final class KeySet extends AbstractLongSet {
      private KeySet() {
      }

      @Override
      public boolean contains(long k) {
         return Long2LongArrayMap.this.findKey(k) != -1;
      }

      @Override
      public boolean remove(long k) {
         int oldPos = Long2LongArrayMap.this.findKey(k);
         if (oldPos == -1) {
            return false;
         } else {
            int tail = Long2LongArrayMap.this.size - oldPos - 1;
            System.arraycopy(Long2LongArrayMap.this.key, oldPos + 1, Long2LongArrayMap.this.key, oldPos, tail);
            System.arraycopy(Long2LongArrayMap.this.value, oldPos + 1, Long2LongArrayMap.this.value, oldPos, tail);
            Long2LongArrayMap.this.size--;
            return true;
         }
      }

      @Override
      public LongIterator iterator() {
         return new LongIterator() {
            int pos = 0;

            @Override
            public boolean hasNext() {
               return this.pos < Long2LongArrayMap.this.size;
            }

            @Override
            public long nextLong() {
               if (!this.hasNext()) {
                  throw new NoSuchElementException();
               } else {
                  return Long2LongArrayMap.this.key[this.pos++];
               }
            }

            @Override
            public void remove() {
               if (this.pos == 0) {
                  throw new IllegalStateException();
               } else {
                  int tail = Long2LongArrayMap.this.size - this.pos;
                  System.arraycopy(Long2LongArrayMap.this.key, this.pos, Long2LongArrayMap.this.key, this.pos - 1, tail);
                  System.arraycopy(Long2LongArrayMap.this.value, this.pos, Long2LongArrayMap.this.value, this.pos - 1, tail);
                  Long2LongArrayMap.this.size--;
                  this.pos--;
               }
            }

            @Override
            public void forEachRemaining(java.util.function.LongConsumer action) {
               int max = Long2LongArrayMap.this.size;

               while (this.pos < max) {
                  action.accept(Long2LongArrayMap.this.key[this.pos++]);
               }
            }
         };
      }

      @Override
      public LongSpliterator spliterator() {
         return new Long2LongArrayMap.KeySet.KeySetSpliterator(0, Long2LongArrayMap.this.size);
      }

      @Override
      public void forEach(java.util.function.LongConsumer action) {
         int i = 0;

         for (int max = Long2LongArrayMap.this.size; i < max; i++) {
            action.accept(Long2LongArrayMap.this.key[i]);
         }
      }

      @Override
      public int size() {
         return Long2LongArrayMap.this.size;
      }

      @Override
      public void clear() {
         Long2LongArrayMap.this.clear();
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
            return Long2LongArrayMap.this.key[location];
         }

         protected final Long2LongArrayMap.KeySet.KeySetSpliterator makeForSplit(int pos, int maxPos) {
            return KeySet.this.new KeySetSpliterator(pos, maxPos);
         }

         @Override
         public void forEachRemaining(java.util.function.LongConsumer action) {
            int max = Long2LongArrayMap.this.size;

            while (this.pos < max) {
               action.accept(Long2LongArrayMap.this.key[this.pos++]);
            }
         }
      }
   }

   private final class ValuesCollection extends AbstractLongCollection {
      private ValuesCollection() {
      }

      @Override
      public boolean contains(long v) {
         return Long2LongArrayMap.this.containsValue(v);
      }

      @Override
      public LongIterator iterator() {
         return new LongIterator() {
            int pos = 0;

            @Override
            public boolean hasNext() {
               return this.pos < Long2LongArrayMap.this.size;
            }

            @Override
            public long nextLong() {
               if (!this.hasNext()) {
                  throw new NoSuchElementException();
               } else {
                  return Long2LongArrayMap.this.value[this.pos++];
               }
            }

            @Override
            public void remove() {
               if (this.pos == 0) {
                  throw new IllegalStateException();
               } else {
                  int tail = Long2LongArrayMap.this.size - this.pos;
                  System.arraycopy(Long2LongArrayMap.this.key, this.pos, Long2LongArrayMap.this.key, this.pos - 1, tail);
                  System.arraycopy(Long2LongArrayMap.this.value, this.pos, Long2LongArrayMap.this.value, this.pos - 1, tail);
                  Long2LongArrayMap.this.size--;
                  this.pos--;
               }
            }

            @Override
            public void forEachRemaining(java.util.function.LongConsumer action) {
               int max = Long2LongArrayMap.this.size;

               while (this.pos < max) {
                  action.accept(Long2LongArrayMap.this.value[this.pos++]);
               }
            }
         };
      }

      @Override
      public LongSpliterator spliterator() {
         return new Long2LongArrayMap.ValuesCollection.ValuesSpliterator(0, Long2LongArrayMap.this.size);
      }

      @Override
      public void forEach(java.util.function.LongConsumer action) {
         int i = 0;

         for (int max = Long2LongArrayMap.this.size; i < max; i++) {
            action.accept(Long2LongArrayMap.this.value[i]);
         }
      }

      @Override
      public int size() {
         return Long2LongArrayMap.this.size;
      }

      @Override
      public void clear() {
         Long2LongArrayMap.this.clear();
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
            return Long2LongArrayMap.this.value[location];
         }

         protected final Long2LongArrayMap.ValuesCollection.ValuesSpliterator makeForSplit(int pos, int maxPos) {
            return ValuesCollection.this.new ValuesSpliterator(pos, maxPos);
         }

         @Override
         public void forEachRemaining(java.util.function.LongConsumer action) {
            int max = Long2LongArrayMap.this.size;

            while (this.pos < max) {
               action.accept(Long2LongArrayMap.this.value[this.pos++]);
            }
         }
      }
   }
}
