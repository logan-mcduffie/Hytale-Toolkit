package it.unimi.dsi.fastutil.longs;

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

public class Long2IntArrayMap extends AbstractLong2IntMap implements Serializable, Cloneable {
   private static final long serialVersionUID = 1L;
   protected transient long[] key;
   protected transient int[] value;
   protected int size;
   protected transient Long2IntMap.FastEntrySet entries;
   protected transient LongSet keys;
   protected transient IntCollection values;

   public Long2IntArrayMap(long[] key, int[] value) {
      this.key = key;
      this.value = value;
      this.size = key.length;
      if (key.length != value.length) {
         throw new IllegalArgumentException("Keys and values have different lengths (" + key.length + ", " + value.length + ")");
      }
   }

   public Long2IntArrayMap() {
      this.key = LongArrays.EMPTY_ARRAY;
      this.value = IntArrays.EMPTY_ARRAY;
   }

   public Long2IntArrayMap(int capacity) {
      this.key = new long[capacity];
      this.value = new int[capacity];
   }

   public Long2IntArrayMap(Long2IntMap m) {
      this(m.size());
      int i = 0;

      for (Long2IntMap.Entry e : m.long2IntEntrySet()) {
         this.key[i] = e.getLongKey();
         this.value[i] = e.getIntValue();
         i++;
      }

      this.size = i;
   }

   public Long2IntArrayMap(Map<? extends Long, ? extends Integer> m) {
      this(m.size());
      int i = 0;

      for (Entry<? extends Long, ? extends Integer> e : m.entrySet()) {
         this.key[i] = e.getKey();
         this.value[i] = e.getValue();
         i++;
      }

      this.size = i;
   }

   public Long2IntArrayMap(long[] key, int[] value, int size) {
      this.key = key;
      this.value = value;
      this.size = size;
      if (key.length != value.length) {
         throw new IllegalArgumentException("Keys and values have different lengths (" + key.length + ", " + value.length + ")");
      } else if (size > key.length) {
         throw new IllegalArgumentException("The provided size (" + size + ") is larger than or equal to the backing-arrays size (" + key.length + ")");
      }
   }

   public Long2IntMap.FastEntrySet long2IntEntrySet() {
      if (this.entries == null) {
         this.entries = new Long2IntArrayMap.EntrySet();
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
   public int get(long k) {
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
   public int put(long k, int v) {
      int oldKey = this.findKey(k);
      if (oldKey != -1) {
         int oldValue = this.value[oldKey];
         this.value[oldKey] = v;
         return oldValue;
      } else {
         if (this.size == this.key.length) {
            long[] newKey = new long[this.size == 0 ? 2 : this.size * 2];
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
   public int remove(long k) {
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
   public LongSet keySet() {
      if (this.keys == null) {
         this.keys = new Long2IntArrayMap.KeySet();
      }

      return this.keys;
   }

   @Override
   public IntCollection values() {
      if (this.values == null) {
         this.values = new Long2IntArrayMap.ValuesCollection();
      }

      return this.values;
   }

   public Long2IntArrayMap clone() {
      Long2IntArrayMap c;
      try {
         c = (Long2IntArrayMap)super.clone();
      } catch (CloneNotSupportedException var3) {
         throw new InternalError();
      }

      c.key = (long[])this.key.clone();
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
         s.writeLong(this.key[i]);
         s.writeInt(this.value[i]);
      }
   }

   private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
      s.defaultReadObject();
      this.key = new long[this.size];
      this.value = new int[this.size];

      for (int i = 0; i < this.size; i++) {
         this.key[i] = s.readLong();
         this.value[i] = s.readInt();
      }
   }

   private final class EntrySet extends AbstractObjectSet<Long2IntMap.Entry> implements Long2IntMap.FastEntrySet {
      private EntrySet() {
      }

      @Override
      public ObjectIterator<Long2IntMap.Entry> iterator() {
         return new ObjectIterator<Long2IntMap.Entry>() {
            int curr = -1;
            int next = 0;

            @Override
            public boolean hasNext() {
               return this.next < Long2IntArrayMap.this.size;
            }

            public Long2IntMap.Entry next() {
               if (!this.hasNext()) {
                  throw new NoSuchElementException();
               } else {
                  return new AbstractLong2IntMap.BasicEntry(Long2IntArrayMap.this.key[this.curr = this.next], Long2IntArrayMap.this.value[this.next++]);
               }
            }

            @Override
            public void remove() {
               if (this.curr == -1) {
                  throw new IllegalStateException();
               } else {
                  this.curr = -1;
                  int tail = Long2IntArrayMap.this.size-- - this.next--;
                  System.arraycopy(Long2IntArrayMap.this.key, this.next + 1, Long2IntArrayMap.this.key, this.next, tail);
                  System.arraycopy(Long2IntArrayMap.this.value, this.next + 1, Long2IntArrayMap.this.value, this.next, tail);
               }
            }

            @Override
            public void forEachRemaining(Consumer<? super Long2IntMap.Entry> action) {
               int max = Long2IntArrayMap.this.size;

               while (this.next < max) {
                  action.accept(new AbstractLong2IntMap.BasicEntry(Long2IntArrayMap.this.key[this.curr = this.next], Long2IntArrayMap.this.value[this.next++]));
               }
            }
         };
      }

      @Override
      public ObjectIterator<Long2IntMap.Entry> fastIterator() {
         return new ObjectIterator<Long2IntMap.Entry>() {
            int next = 0;
            int curr = -1;
            final AbstractLong2IntMap.BasicEntry entry = new AbstractLong2IntMap.BasicEntry();

            @Override
            public boolean hasNext() {
               return this.next < Long2IntArrayMap.this.size;
            }

            public Long2IntMap.Entry next() {
               if (!this.hasNext()) {
                  throw new NoSuchElementException();
               } else {
                  this.entry.key = Long2IntArrayMap.this.key[this.curr = this.next];
                  this.entry.value = Long2IntArrayMap.this.value[this.next++];
                  return this.entry;
               }
            }

            @Override
            public void remove() {
               if (this.curr == -1) {
                  throw new IllegalStateException();
               } else {
                  this.curr = -1;
                  int tail = Long2IntArrayMap.this.size-- - this.next--;
                  System.arraycopy(Long2IntArrayMap.this.key, this.next + 1, Long2IntArrayMap.this.key, this.next, tail);
                  System.arraycopy(Long2IntArrayMap.this.value, this.next + 1, Long2IntArrayMap.this.value, this.next, tail);
               }
            }

            @Override
            public void forEachRemaining(Consumer<? super Long2IntMap.Entry> action) {
               int max = Long2IntArrayMap.this.size;

               while (this.next < max) {
                  this.entry.key = Long2IntArrayMap.this.key[this.curr = this.next];
                  this.entry.value = Long2IntArrayMap.this.value[this.next++];
                  action.accept(this.entry);
               }
            }
         };
      }

      @Override
      public ObjectSpliterator<Long2IntMap.Entry> spliterator() {
         return new Long2IntArrayMap.EntrySet.EntrySetSpliterator(0, Long2IntArrayMap.this.size);
      }

      @Override
      public void forEach(Consumer<? super Long2IntMap.Entry> action) {
         int i = 0;

         for (int max = Long2IntArrayMap.this.size; i < max; i++) {
            action.accept(new AbstractLong2IntMap.BasicEntry(Long2IntArrayMap.this.key[i], Long2IntArrayMap.this.value[i]));
         }
      }

      @Override
      public void fastForEach(Consumer<? super Long2IntMap.Entry> action) {
         AbstractLong2IntMap.BasicEntry entry = new AbstractLong2IntMap.BasicEntry();
         int i = 0;

         for (int max = Long2IntArrayMap.this.size; i < max; i++) {
            entry.key = Long2IntArrayMap.this.key[i];
            entry.value = Long2IntArrayMap.this.value[i];
            action.accept(entry);
         }
      }

      @Override
      public int size() {
         return Long2IntArrayMap.this.size;
      }

      @Override
      public boolean contains(Object o) {
         if (!(o instanceof Entry)) {
            return false;
         } else {
            Entry<?, ?> e = (Entry<?, ?>)o;
            if (e.getKey() == null || !(e.getKey() instanceof Long)) {
               return false;
            } else if (e.getValue() != null && e.getValue() instanceof Integer) {
               long k = (Long)e.getKey();
               return Long2IntArrayMap.this.containsKey(k) && Long2IntArrayMap.this.get(k) == (Integer)e.getValue();
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
            } else if (e.getValue() != null && e.getValue() instanceof Integer) {
               long k = (Long)e.getKey();
               int v = (Integer)e.getValue();
               int oldPos = Long2IntArrayMap.this.findKey(k);
               if (oldPos != -1 && v == Long2IntArrayMap.this.value[oldPos]) {
                  int tail = Long2IntArrayMap.this.size - oldPos - 1;
                  System.arraycopy(Long2IntArrayMap.this.key, oldPos + 1, Long2IntArrayMap.this.key, oldPos, tail);
                  System.arraycopy(Long2IntArrayMap.this.value, oldPos + 1, Long2IntArrayMap.this.value, oldPos, tail);
                  Long2IntArrayMap.this.size--;
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
         extends ObjectSpliterators.EarlyBindingSizeIndexBasedSpliterator<Long2IntMap.Entry>
         implements ObjectSpliterator<Long2IntMap.Entry> {
         EntrySetSpliterator(int pos, int maxPos) {
            super(pos, maxPos);
         }

         @Override
         public int characteristics() {
            return 16465;
         }

         protected final Long2IntMap.Entry get(int location) {
            return new AbstractLong2IntMap.BasicEntry(Long2IntArrayMap.this.key[location], Long2IntArrayMap.this.value[location]);
         }

         protected final Long2IntArrayMap.EntrySet.EntrySetSpliterator makeForSplit(int pos, int maxPos) {
            return EntrySet.this.new EntrySetSpliterator(pos, maxPos);
         }
      }
   }

   private final class KeySet extends AbstractLongSet {
      private KeySet() {
      }

      @Override
      public boolean contains(long k) {
         return Long2IntArrayMap.this.findKey(k) != -1;
      }

      @Override
      public boolean remove(long k) {
         int oldPos = Long2IntArrayMap.this.findKey(k);
         if (oldPos == -1) {
            return false;
         } else {
            int tail = Long2IntArrayMap.this.size - oldPos - 1;
            System.arraycopy(Long2IntArrayMap.this.key, oldPos + 1, Long2IntArrayMap.this.key, oldPos, tail);
            System.arraycopy(Long2IntArrayMap.this.value, oldPos + 1, Long2IntArrayMap.this.value, oldPos, tail);
            Long2IntArrayMap.this.size--;
            return true;
         }
      }

      @Override
      public LongIterator iterator() {
         return new LongIterator() {
            int pos = 0;

            @Override
            public boolean hasNext() {
               return this.pos < Long2IntArrayMap.this.size;
            }

            @Override
            public long nextLong() {
               if (!this.hasNext()) {
                  throw new NoSuchElementException();
               } else {
                  return Long2IntArrayMap.this.key[this.pos++];
               }
            }

            @Override
            public void remove() {
               if (this.pos == 0) {
                  throw new IllegalStateException();
               } else {
                  int tail = Long2IntArrayMap.this.size - this.pos;
                  System.arraycopy(Long2IntArrayMap.this.key, this.pos, Long2IntArrayMap.this.key, this.pos - 1, tail);
                  System.arraycopy(Long2IntArrayMap.this.value, this.pos, Long2IntArrayMap.this.value, this.pos - 1, tail);
                  Long2IntArrayMap.this.size--;
                  this.pos--;
               }
            }

            @Override
            public void forEachRemaining(java.util.function.LongConsumer action) {
               int max = Long2IntArrayMap.this.size;

               while (this.pos < max) {
                  action.accept(Long2IntArrayMap.this.key[this.pos++]);
               }
            }
         };
      }

      @Override
      public LongSpliterator spliterator() {
         return new Long2IntArrayMap.KeySet.KeySetSpliterator(0, Long2IntArrayMap.this.size);
      }

      @Override
      public void forEach(java.util.function.LongConsumer action) {
         int i = 0;

         for (int max = Long2IntArrayMap.this.size; i < max; i++) {
            action.accept(Long2IntArrayMap.this.key[i]);
         }
      }

      @Override
      public int size() {
         return Long2IntArrayMap.this.size;
      }

      @Override
      public void clear() {
         Long2IntArrayMap.this.clear();
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
            return Long2IntArrayMap.this.key[location];
         }

         protected final Long2IntArrayMap.KeySet.KeySetSpliterator makeForSplit(int pos, int maxPos) {
            return KeySet.this.new KeySetSpliterator(pos, maxPos);
         }

         @Override
         public void forEachRemaining(java.util.function.LongConsumer action) {
            int max = Long2IntArrayMap.this.size;

            while (this.pos < max) {
               action.accept(Long2IntArrayMap.this.key[this.pos++]);
            }
         }
      }
   }

   private final class ValuesCollection extends AbstractIntCollection {
      private ValuesCollection() {
      }

      @Override
      public boolean contains(int v) {
         return Long2IntArrayMap.this.containsValue(v);
      }

      @Override
      public IntIterator iterator() {
         return new IntIterator() {
            int pos = 0;

            @Override
            public boolean hasNext() {
               return this.pos < Long2IntArrayMap.this.size;
            }

            @Override
            public int nextInt() {
               if (!this.hasNext()) {
                  throw new NoSuchElementException();
               } else {
                  return Long2IntArrayMap.this.value[this.pos++];
               }
            }

            @Override
            public void remove() {
               if (this.pos == 0) {
                  throw new IllegalStateException();
               } else {
                  int tail = Long2IntArrayMap.this.size - this.pos;
                  System.arraycopy(Long2IntArrayMap.this.key, this.pos, Long2IntArrayMap.this.key, this.pos - 1, tail);
                  System.arraycopy(Long2IntArrayMap.this.value, this.pos, Long2IntArrayMap.this.value, this.pos - 1, tail);
                  Long2IntArrayMap.this.size--;
                  this.pos--;
               }
            }

            @Override
            public void forEachRemaining(IntConsumer action) {
               int max = Long2IntArrayMap.this.size;

               while (this.pos < max) {
                  action.accept(Long2IntArrayMap.this.value[this.pos++]);
               }
            }
         };
      }

      @Override
      public IntSpliterator spliterator() {
         return new Long2IntArrayMap.ValuesCollection.ValuesSpliterator(0, Long2IntArrayMap.this.size);
      }

      @Override
      public void forEach(IntConsumer action) {
         int i = 0;

         for (int max = Long2IntArrayMap.this.size; i < max; i++) {
            action.accept(Long2IntArrayMap.this.value[i]);
         }
      }

      @Override
      public int size() {
         return Long2IntArrayMap.this.size;
      }

      @Override
      public void clear() {
         Long2IntArrayMap.this.clear();
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
            return Long2IntArrayMap.this.value[location];
         }

         protected final Long2IntArrayMap.ValuesCollection.ValuesSpliterator makeForSplit(int pos, int maxPos) {
            return ValuesCollection.this.new ValuesSpliterator(pos, maxPos);
         }

         @Override
         public void forEachRemaining(IntConsumer action) {
            int max = Long2IntArrayMap.this.size;

            while (this.pos < max) {
               action.accept(Long2IntArrayMap.this.value[this.pos++]);
            }
         }
      }
   }
}
