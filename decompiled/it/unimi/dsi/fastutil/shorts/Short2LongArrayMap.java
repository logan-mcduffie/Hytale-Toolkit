package it.unimi.dsi.fastutil.shorts;

import it.unimi.dsi.fastutil.longs.AbstractLongCollection;
import it.unimi.dsi.fastutil.longs.LongArrays;
import it.unimi.dsi.fastutil.longs.LongCollection;
import it.unimi.dsi.fastutil.longs.LongIterator;
import it.unimi.dsi.fastutil.longs.LongSpliterator;
import it.unimi.dsi.fastutil.longs.LongSpliterators;
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
import java.util.function.LongConsumer;

public class Short2LongArrayMap extends AbstractShort2LongMap implements Serializable, Cloneable {
   private static final long serialVersionUID = 1L;
   protected transient short[] key;
   protected transient long[] value;
   protected int size;
   protected transient Short2LongMap.FastEntrySet entries;
   protected transient ShortSet keys;
   protected transient LongCollection values;

   public Short2LongArrayMap(short[] key, long[] value) {
      this.key = key;
      this.value = value;
      this.size = key.length;
      if (key.length != value.length) {
         throw new IllegalArgumentException("Keys and values have different lengths (" + key.length + ", " + value.length + ")");
      }
   }

   public Short2LongArrayMap() {
      this.key = ShortArrays.EMPTY_ARRAY;
      this.value = LongArrays.EMPTY_ARRAY;
   }

   public Short2LongArrayMap(int capacity) {
      this.key = new short[capacity];
      this.value = new long[capacity];
   }

   public Short2LongArrayMap(Short2LongMap m) {
      this(m.size());
      int i = 0;

      for (Short2LongMap.Entry e : m.short2LongEntrySet()) {
         this.key[i] = e.getShortKey();
         this.value[i] = e.getLongValue();
         i++;
      }

      this.size = i;
   }

   public Short2LongArrayMap(Map<? extends Short, ? extends Long> m) {
      this(m.size());
      int i = 0;

      for (Entry<? extends Short, ? extends Long> e : m.entrySet()) {
         this.key[i] = e.getKey();
         this.value[i] = e.getValue();
         i++;
      }

      this.size = i;
   }

   public Short2LongArrayMap(short[] key, long[] value, int size) {
      this.key = key;
      this.value = value;
      this.size = size;
      if (key.length != value.length) {
         throw new IllegalArgumentException("Keys and values have different lengths (" + key.length + ", " + value.length + ")");
      } else if (size > key.length) {
         throw new IllegalArgumentException("The provided size (" + size + ") is larger than or equal to the backing-arrays size (" + key.length + ")");
      }
   }

   public Short2LongMap.FastEntrySet short2LongEntrySet() {
      if (this.entries == null) {
         this.entries = new Short2LongArrayMap.EntrySet();
      }

      return this.entries;
   }

   private int findKey(short k) {
      short[] key = this.key;
      int i = this.size;

      while (i-- != 0) {
         if (key[i] == k) {
            return i;
         }
      }

      return -1;
   }

   @Override
   public long get(short k) {
      short[] key = this.key;
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
   public boolean containsKey(short k) {
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
   public long put(short k, long v) {
      int oldKey = this.findKey(k);
      if (oldKey != -1) {
         long oldValue = this.value[oldKey];
         this.value[oldKey] = v;
         return oldValue;
      } else {
         if (this.size == this.key.length) {
            short[] newKey = new short[this.size == 0 ? 2 : this.size * 2];
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
   public long remove(short k) {
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
   public ShortSet keySet() {
      if (this.keys == null) {
         this.keys = new Short2LongArrayMap.KeySet();
      }

      return this.keys;
   }

   @Override
   public LongCollection values() {
      if (this.values == null) {
         this.values = new Short2LongArrayMap.ValuesCollection();
      }

      return this.values;
   }

   public Short2LongArrayMap clone() {
      Short2LongArrayMap c;
      try {
         c = (Short2LongArrayMap)super.clone();
      } catch (CloneNotSupportedException var3) {
         throw new InternalError();
      }

      c.key = (short[])this.key.clone();
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
         s.writeShort(this.key[i]);
         s.writeLong(this.value[i]);
      }
   }

   private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
      s.defaultReadObject();
      this.key = new short[this.size];
      this.value = new long[this.size];

      for (int i = 0; i < this.size; i++) {
         this.key[i] = s.readShort();
         this.value[i] = s.readLong();
      }
   }

   private final class EntrySet extends AbstractObjectSet<Short2LongMap.Entry> implements Short2LongMap.FastEntrySet {
      private EntrySet() {
      }

      @Override
      public ObjectIterator<Short2LongMap.Entry> iterator() {
         return new ObjectIterator<Short2LongMap.Entry>() {
            int curr = -1;
            int next = 0;

            @Override
            public boolean hasNext() {
               return this.next < Short2LongArrayMap.this.size;
            }

            public Short2LongMap.Entry next() {
               if (!this.hasNext()) {
                  throw new NoSuchElementException();
               } else {
                  return new AbstractShort2LongMap.BasicEntry(Short2LongArrayMap.this.key[this.curr = this.next], Short2LongArrayMap.this.value[this.next++]);
               }
            }

            @Override
            public void remove() {
               if (this.curr == -1) {
                  throw new IllegalStateException();
               } else {
                  this.curr = -1;
                  int tail = Short2LongArrayMap.this.size-- - this.next--;
                  System.arraycopy(Short2LongArrayMap.this.key, this.next + 1, Short2LongArrayMap.this.key, this.next, tail);
                  System.arraycopy(Short2LongArrayMap.this.value, this.next + 1, Short2LongArrayMap.this.value, this.next, tail);
               }
            }

            @Override
            public void forEachRemaining(Consumer<? super Short2LongMap.Entry> action) {
               int max = Short2LongArrayMap.this.size;

               while (this.next < max) {
                  action.accept(
                     new AbstractShort2LongMap.BasicEntry(Short2LongArrayMap.this.key[this.curr = this.next], Short2LongArrayMap.this.value[this.next++])
                  );
               }
            }
         };
      }

      @Override
      public ObjectIterator<Short2LongMap.Entry> fastIterator() {
         return new ObjectIterator<Short2LongMap.Entry>() {
            int next = 0;
            int curr = -1;
            final AbstractShort2LongMap.BasicEntry entry = new AbstractShort2LongMap.BasicEntry();

            @Override
            public boolean hasNext() {
               return this.next < Short2LongArrayMap.this.size;
            }

            public Short2LongMap.Entry next() {
               if (!this.hasNext()) {
                  throw new NoSuchElementException();
               } else {
                  this.entry.key = Short2LongArrayMap.this.key[this.curr = this.next];
                  this.entry.value = Short2LongArrayMap.this.value[this.next++];
                  return this.entry;
               }
            }

            @Override
            public void remove() {
               if (this.curr == -1) {
                  throw new IllegalStateException();
               } else {
                  this.curr = -1;
                  int tail = Short2LongArrayMap.this.size-- - this.next--;
                  System.arraycopy(Short2LongArrayMap.this.key, this.next + 1, Short2LongArrayMap.this.key, this.next, tail);
                  System.arraycopy(Short2LongArrayMap.this.value, this.next + 1, Short2LongArrayMap.this.value, this.next, tail);
               }
            }

            @Override
            public void forEachRemaining(Consumer<? super Short2LongMap.Entry> action) {
               int max = Short2LongArrayMap.this.size;

               while (this.next < max) {
                  this.entry.key = Short2LongArrayMap.this.key[this.curr = this.next];
                  this.entry.value = Short2LongArrayMap.this.value[this.next++];
                  action.accept(this.entry);
               }
            }
         };
      }

      @Override
      public ObjectSpliterator<Short2LongMap.Entry> spliterator() {
         return new Short2LongArrayMap.EntrySet.EntrySetSpliterator(0, Short2LongArrayMap.this.size);
      }

      @Override
      public void forEach(Consumer<? super Short2LongMap.Entry> action) {
         int i = 0;

         for (int max = Short2LongArrayMap.this.size; i < max; i++) {
            action.accept(new AbstractShort2LongMap.BasicEntry(Short2LongArrayMap.this.key[i], Short2LongArrayMap.this.value[i]));
         }
      }

      @Override
      public void fastForEach(Consumer<? super Short2LongMap.Entry> action) {
         AbstractShort2LongMap.BasicEntry entry = new AbstractShort2LongMap.BasicEntry();
         int i = 0;

         for (int max = Short2LongArrayMap.this.size; i < max; i++) {
            entry.key = Short2LongArrayMap.this.key[i];
            entry.value = Short2LongArrayMap.this.value[i];
            action.accept(entry);
         }
      }

      @Override
      public int size() {
         return Short2LongArrayMap.this.size;
      }

      @Override
      public boolean contains(Object o) {
         if (!(o instanceof Entry)) {
            return false;
         } else {
            Entry<?, ?> e = (Entry<?, ?>)o;
            if (e.getKey() == null || !(e.getKey() instanceof Short)) {
               return false;
            } else if (e.getValue() != null && e.getValue() instanceof Long) {
               short k = (Short)e.getKey();
               return Short2LongArrayMap.this.containsKey(k) && Short2LongArrayMap.this.get(k) == (Long)e.getValue();
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
            if (e.getKey() == null || !(e.getKey() instanceof Short)) {
               return false;
            } else if (e.getValue() != null && e.getValue() instanceof Long) {
               short k = (Short)e.getKey();
               long v = (Long)e.getValue();
               int oldPos = Short2LongArrayMap.this.findKey(k);
               if (oldPos != -1 && v == Short2LongArrayMap.this.value[oldPos]) {
                  int tail = Short2LongArrayMap.this.size - oldPos - 1;
                  System.arraycopy(Short2LongArrayMap.this.key, oldPos + 1, Short2LongArrayMap.this.key, oldPos, tail);
                  System.arraycopy(Short2LongArrayMap.this.value, oldPos + 1, Short2LongArrayMap.this.value, oldPos, tail);
                  Short2LongArrayMap.this.size--;
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
         extends ObjectSpliterators.EarlyBindingSizeIndexBasedSpliterator<Short2LongMap.Entry>
         implements ObjectSpliterator<Short2LongMap.Entry> {
         EntrySetSpliterator(int pos, int maxPos) {
            super(pos, maxPos);
         }

         @Override
         public int characteristics() {
            return 16465;
         }

         protected final Short2LongMap.Entry get(int location) {
            return new AbstractShort2LongMap.BasicEntry(Short2LongArrayMap.this.key[location], Short2LongArrayMap.this.value[location]);
         }

         protected final Short2LongArrayMap.EntrySet.EntrySetSpliterator makeForSplit(int pos, int maxPos) {
            return EntrySet.this.new EntrySetSpliterator(pos, maxPos);
         }
      }
   }

   private final class KeySet extends AbstractShortSet {
      private KeySet() {
      }

      @Override
      public boolean contains(short k) {
         return Short2LongArrayMap.this.findKey(k) != -1;
      }

      @Override
      public boolean remove(short k) {
         int oldPos = Short2LongArrayMap.this.findKey(k);
         if (oldPos == -1) {
            return false;
         } else {
            int tail = Short2LongArrayMap.this.size - oldPos - 1;
            System.arraycopy(Short2LongArrayMap.this.key, oldPos + 1, Short2LongArrayMap.this.key, oldPos, tail);
            System.arraycopy(Short2LongArrayMap.this.value, oldPos + 1, Short2LongArrayMap.this.value, oldPos, tail);
            Short2LongArrayMap.this.size--;
            return true;
         }
      }

      @Override
      public ShortIterator iterator() {
         return new ShortIterator() {
            int pos = 0;

            @Override
            public boolean hasNext() {
               return this.pos < Short2LongArrayMap.this.size;
            }

            @Override
            public short nextShort() {
               if (!this.hasNext()) {
                  throw new NoSuchElementException();
               } else {
                  return Short2LongArrayMap.this.key[this.pos++];
               }
            }

            @Override
            public void remove() {
               if (this.pos == 0) {
                  throw new IllegalStateException();
               } else {
                  int tail = Short2LongArrayMap.this.size - this.pos;
                  System.arraycopy(Short2LongArrayMap.this.key, this.pos, Short2LongArrayMap.this.key, this.pos - 1, tail);
                  System.arraycopy(Short2LongArrayMap.this.value, this.pos, Short2LongArrayMap.this.value, this.pos - 1, tail);
                  Short2LongArrayMap.this.size--;
                  this.pos--;
               }
            }

            @Override
            public void forEachRemaining(ShortConsumer action) {
               int max = Short2LongArrayMap.this.size;

               while (this.pos < max) {
                  action.accept(Short2LongArrayMap.this.key[this.pos++]);
               }
            }
         };
      }

      @Override
      public ShortSpliterator spliterator() {
         return new Short2LongArrayMap.KeySet.KeySetSpliterator(0, Short2LongArrayMap.this.size);
      }

      @Override
      public void forEach(ShortConsumer action) {
         int i = 0;

         for (int max = Short2LongArrayMap.this.size; i < max; i++) {
            action.accept(Short2LongArrayMap.this.key[i]);
         }
      }

      @Override
      public int size() {
         return Short2LongArrayMap.this.size;
      }

      @Override
      public void clear() {
         Short2LongArrayMap.this.clear();
      }

      final class KeySetSpliterator extends ShortSpliterators.EarlyBindingSizeIndexBasedSpliterator implements ShortSpliterator {
         KeySetSpliterator(int pos, int maxPos) {
            super(pos, maxPos);
         }

         @Override
         public int characteristics() {
            return 16721;
         }

         @Override
         protected final short get(int location) {
            return Short2LongArrayMap.this.key[location];
         }

         protected final Short2LongArrayMap.KeySet.KeySetSpliterator makeForSplit(int pos, int maxPos) {
            return KeySet.this.new KeySetSpliterator(pos, maxPos);
         }

         @Override
         public void forEachRemaining(ShortConsumer action) {
            int max = Short2LongArrayMap.this.size;

            while (this.pos < max) {
               action.accept(Short2LongArrayMap.this.key[this.pos++]);
            }
         }
      }
   }

   private final class ValuesCollection extends AbstractLongCollection {
      private ValuesCollection() {
      }

      @Override
      public boolean contains(long v) {
         return Short2LongArrayMap.this.containsValue(v);
      }

      @Override
      public LongIterator iterator() {
         return new LongIterator() {
            int pos = 0;

            @Override
            public boolean hasNext() {
               return this.pos < Short2LongArrayMap.this.size;
            }

            @Override
            public long nextLong() {
               if (!this.hasNext()) {
                  throw new NoSuchElementException();
               } else {
                  return Short2LongArrayMap.this.value[this.pos++];
               }
            }

            @Override
            public void remove() {
               if (this.pos == 0) {
                  throw new IllegalStateException();
               } else {
                  int tail = Short2LongArrayMap.this.size - this.pos;
                  System.arraycopy(Short2LongArrayMap.this.key, this.pos, Short2LongArrayMap.this.key, this.pos - 1, tail);
                  System.arraycopy(Short2LongArrayMap.this.value, this.pos, Short2LongArrayMap.this.value, this.pos - 1, tail);
                  Short2LongArrayMap.this.size--;
                  this.pos--;
               }
            }

            @Override
            public void forEachRemaining(LongConsumer action) {
               int max = Short2LongArrayMap.this.size;

               while (this.pos < max) {
                  action.accept(Short2LongArrayMap.this.value[this.pos++]);
               }
            }
         };
      }

      @Override
      public LongSpliterator spliterator() {
         return new Short2LongArrayMap.ValuesCollection.ValuesSpliterator(0, Short2LongArrayMap.this.size);
      }

      @Override
      public void forEach(LongConsumer action) {
         int i = 0;

         for (int max = Short2LongArrayMap.this.size; i < max; i++) {
            action.accept(Short2LongArrayMap.this.value[i]);
         }
      }

      @Override
      public int size() {
         return Short2LongArrayMap.this.size;
      }

      @Override
      public void clear() {
         Short2LongArrayMap.this.clear();
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
            return Short2LongArrayMap.this.value[location];
         }

         protected final Short2LongArrayMap.ValuesCollection.ValuesSpliterator makeForSplit(int pos, int maxPos) {
            return ValuesCollection.this.new ValuesSpliterator(pos, maxPos);
         }

         @Override
         public void forEachRemaining(LongConsumer action) {
            int max = Short2LongArrayMap.this.size;

            while (this.pos < max) {
               action.accept(Short2LongArrayMap.this.value[this.pos++]);
            }
         }
      }
   }
}
