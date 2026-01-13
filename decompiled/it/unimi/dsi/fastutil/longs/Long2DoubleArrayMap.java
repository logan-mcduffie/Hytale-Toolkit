package it.unimi.dsi.fastutil.longs;

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

public class Long2DoubleArrayMap extends AbstractLong2DoubleMap implements Serializable, Cloneable {
   private static final long serialVersionUID = 1L;
   protected transient long[] key;
   protected transient double[] value;
   protected int size;
   protected transient Long2DoubleMap.FastEntrySet entries;
   protected transient LongSet keys;
   protected transient DoubleCollection values;

   public Long2DoubleArrayMap(long[] key, double[] value) {
      this.key = key;
      this.value = value;
      this.size = key.length;
      if (key.length != value.length) {
         throw new IllegalArgumentException("Keys and values have different lengths (" + key.length + ", " + value.length + ")");
      }
   }

   public Long2DoubleArrayMap() {
      this.key = LongArrays.EMPTY_ARRAY;
      this.value = DoubleArrays.EMPTY_ARRAY;
   }

   public Long2DoubleArrayMap(int capacity) {
      this.key = new long[capacity];
      this.value = new double[capacity];
   }

   public Long2DoubleArrayMap(Long2DoubleMap m) {
      this(m.size());
      int i = 0;

      for (Long2DoubleMap.Entry e : m.long2DoubleEntrySet()) {
         this.key[i] = e.getLongKey();
         this.value[i] = e.getDoubleValue();
         i++;
      }

      this.size = i;
   }

   public Long2DoubleArrayMap(Map<? extends Long, ? extends Double> m) {
      this(m.size());
      int i = 0;

      for (Entry<? extends Long, ? extends Double> e : m.entrySet()) {
         this.key[i] = e.getKey();
         this.value[i] = e.getValue();
         i++;
      }

      this.size = i;
   }

   public Long2DoubleArrayMap(long[] key, double[] value, int size) {
      this.key = key;
      this.value = value;
      this.size = size;
      if (key.length != value.length) {
         throw new IllegalArgumentException("Keys and values have different lengths (" + key.length + ", " + value.length + ")");
      } else if (size > key.length) {
         throw new IllegalArgumentException("The provided size (" + size + ") is larger than or equal to the backing-arrays size (" + key.length + ")");
      }
   }

   public Long2DoubleMap.FastEntrySet long2DoubleEntrySet() {
      if (this.entries == null) {
         this.entries = new Long2DoubleArrayMap.EntrySet();
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
   public double get(long k) {
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
   public double put(long k, double v) {
      int oldKey = this.findKey(k);
      if (oldKey != -1) {
         double oldValue = this.value[oldKey];
         this.value[oldKey] = v;
         return oldValue;
      } else {
         if (this.size == this.key.length) {
            long[] newKey = new long[this.size == 0 ? 2 : this.size * 2];
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
   public double remove(long k) {
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
   public LongSet keySet() {
      if (this.keys == null) {
         this.keys = new Long2DoubleArrayMap.KeySet();
      }

      return this.keys;
   }

   @Override
   public DoubleCollection values() {
      if (this.values == null) {
         this.values = new Long2DoubleArrayMap.ValuesCollection();
      }

      return this.values;
   }

   public Long2DoubleArrayMap clone() {
      Long2DoubleArrayMap c;
      try {
         c = (Long2DoubleArrayMap)super.clone();
      } catch (CloneNotSupportedException var3) {
         throw new InternalError();
      }

      c.key = (long[])this.key.clone();
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
         s.writeLong(this.key[i]);
         s.writeDouble(this.value[i]);
      }
   }

   private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
      s.defaultReadObject();
      this.key = new long[this.size];
      this.value = new double[this.size];

      for (int i = 0; i < this.size; i++) {
         this.key[i] = s.readLong();
         this.value[i] = s.readDouble();
      }
   }

   private final class EntrySet extends AbstractObjectSet<Long2DoubleMap.Entry> implements Long2DoubleMap.FastEntrySet {
      private EntrySet() {
      }

      @Override
      public ObjectIterator<Long2DoubleMap.Entry> iterator() {
         return new ObjectIterator<Long2DoubleMap.Entry>() {
            int curr = -1;
            int next = 0;

            @Override
            public boolean hasNext() {
               return this.next < Long2DoubleArrayMap.this.size;
            }

            public Long2DoubleMap.Entry next() {
               if (!this.hasNext()) {
                  throw new NoSuchElementException();
               } else {
                  return new AbstractLong2DoubleMap.BasicEntry(Long2DoubleArrayMap.this.key[this.curr = this.next], Long2DoubleArrayMap.this.value[this.next++]);
               }
            }

            @Override
            public void remove() {
               if (this.curr == -1) {
                  throw new IllegalStateException();
               } else {
                  this.curr = -1;
                  int tail = Long2DoubleArrayMap.this.size-- - this.next--;
                  System.arraycopy(Long2DoubleArrayMap.this.key, this.next + 1, Long2DoubleArrayMap.this.key, this.next, tail);
                  System.arraycopy(Long2DoubleArrayMap.this.value, this.next + 1, Long2DoubleArrayMap.this.value, this.next, tail);
               }
            }

            @Override
            public void forEachRemaining(Consumer<? super Long2DoubleMap.Entry> action) {
               int max = Long2DoubleArrayMap.this.size;

               while (this.next < max) {
                  action.accept(
                     new AbstractLong2DoubleMap.BasicEntry(Long2DoubleArrayMap.this.key[this.curr = this.next], Long2DoubleArrayMap.this.value[this.next++])
                  );
               }
            }
         };
      }

      @Override
      public ObjectIterator<Long2DoubleMap.Entry> fastIterator() {
         return new ObjectIterator<Long2DoubleMap.Entry>() {
            int next = 0;
            int curr = -1;
            final AbstractLong2DoubleMap.BasicEntry entry = new AbstractLong2DoubleMap.BasicEntry();

            @Override
            public boolean hasNext() {
               return this.next < Long2DoubleArrayMap.this.size;
            }

            public Long2DoubleMap.Entry next() {
               if (!this.hasNext()) {
                  throw new NoSuchElementException();
               } else {
                  this.entry.key = Long2DoubleArrayMap.this.key[this.curr = this.next];
                  this.entry.value = Long2DoubleArrayMap.this.value[this.next++];
                  return this.entry;
               }
            }

            @Override
            public void remove() {
               if (this.curr == -1) {
                  throw new IllegalStateException();
               } else {
                  this.curr = -1;
                  int tail = Long2DoubleArrayMap.this.size-- - this.next--;
                  System.arraycopy(Long2DoubleArrayMap.this.key, this.next + 1, Long2DoubleArrayMap.this.key, this.next, tail);
                  System.arraycopy(Long2DoubleArrayMap.this.value, this.next + 1, Long2DoubleArrayMap.this.value, this.next, tail);
               }
            }

            @Override
            public void forEachRemaining(Consumer<? super Long2DoubleMap.Entry> action) {
               int max = Long2DoubleArrayMap.this.size;

               while (this.next < max) {
                  this.entry.key = Long2DoubleArrayMap.this.key[this.curr = this.next];
                  this.entry.value = Long2DoubleArrayMap.this.value[this.next++];
                  action.accept(this.entry);
               }
            }
         };
      }

      @Override
      public ObjectSpliterator<Long2DoubleMap.Entry> spliterator() {
         return new Long2DoubleArrayMap.EntrySet.EntrySetSpliterator(0, Long2DoubleArrayMap.this.size);
      }

      @Override
      public void forEach(Consumer<? super Long2DoubleMap.Entry> action) {
         int i = 0;

         for (int max = Long2DoubleArrayMap.this.size; i < max; i++) {
            action.accept(new AbstractLong2DoubleMap.BasicEntry(Long2DoubleArrayMap.this.key[i], Long2DoubleArrayMap.this.value[i]));
         }
      }

      @Override
      public void fastForEach(Consumer<? super Long2DoubleMap.Entry> action) {
         AbstractLong2DoubleMap.BasicEntry entry = new AbstractLong2DoubleMap.BasicEntry();
         int i = 0;

         for (int max = Long2DoubleArrayMap.this.size; i < max; i++) {
            entry.key = Long2DoubleArrayMap.this.key[i];
            entry.value = Long2DoubleArrayMap.this.value[i];
            action.accept(entry);
         }
      }

      @Override
      public int size() {
         return Long2DoubleArrayMap.this.size;
      }

      @Override
      public boolean contains(Object o) {
         if (!(o instanceof Entry)) {
            return false;
         } else {
            Entry<?, ?> e = (Entry<?, ?>)o;
            if (e.getKey() == null || !(e.getKey() instanceof Long)) {
               return false;
            } else if (e.getValue() != null && e.getValue() instanceof Double) {
               long k = (Long)e.getKey();
               return Long2DoubleArrayMap.this.containsKey(k)
                  && Double.doubleToLongBits(Long2DoubleArrayMap.this.get(k)) == Double.doubleToLongBits((Double)e.getValue());
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
            } else if (e.getValue() != null && e.getValue() instanceof Double) {
               long k = (Long)e.getKey();
               double v = (Double)e.getValue();
               int oldPos = Long2DoubleArrayMap.this.findKey(k);
               if (oldPos != -1 && Double.doubleToLongBits(v) == Double.doubleToLongBits(Long2DoubleArrayMap.this.value[oldPos])) {
                  int tail = Long2DoubleArrayMap.this.size - oldPos - 1;
                  System.arraycopy(Long2DoubleArrayMap.this.key, oldPos + 1, Long2DoubleArrayMap.this.key, oldPos, tail);
                  System.arraycopy(Long2DoubleArrayMap.this.value, oldPos + 1, Long2DoubleArrayMap.this.value, oldPos, tail);
                  Long2DoubleArrayMap.this.size--;
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
         extends ObjectSpliterators.EarlyBindingSizeIndexBasedSpliterator<Long2DoubleMap.Entry>
         implements ObjectSpliterator<Long2DoubleMap.Entry> {
         EntrySetSpliterator(int pos, int maxPos) {
            super(pos, maxPos);
         }

         @Override
         public int characteristics() {
            return 16465;
         }

         protected final Long2DoubleMap.Entry get(int location) {
            return new AbstractLong2DoubleMap.BasicEntry(Long2DoubleArrayMap.this.key[location], Long2DoubleArrayMap.this.value[location]);
         }

         protected final Long2DoubleArrayMap.EntrySet.EntrySetSpliterator makeForSplit(int pos, int maxPos) {
            return EntrySet.this.new EntrySetSpliterator(pos, maxPos);
         }
      }
   }

   private final class KeySet extends AbstractLongSet {
      private KeySet() {
      }

      @Override
      public boolean contains(long k) {
         return Long2DoubleArrayMap.this.findKey(k) != -1;
      }

      @Override
      public boolean remove(long k) {
         int oldPos = Long2DoubleArrayMap.this.findKey(k);
         if (oldPos == -1) {
            return false;
         } else {
            int tail = Long2DoubleArrayMap.this.size - oldPos - 1;
            System.arraycopy(Long2DoubleArrayMap.this.key, oldPos + 1, Long2DoubleArrayMap.this.key, oldPos, tail);
            System.arraycopy(Long2DoubleArrayMap.this.value, oldPos + 1, Long2DoubleArrayMap.this.value, oldPos, tail);
            Long2DoubleArrayMap.this.size--;
            return true;
         }
      }

      @Override
      public LongIterator iterator() {
         return new LongIterator() {
            int pos = 0;

            @Override
            public boolean hasNext() {
               return this.pos < Long2DoubleArrayMap.this.size;
            }

            @Override
            public long nextLong() {
               if (!this.hasNext()) {
                  throw new NoSuchElementException();
               } else {
                  return Long2DoubleArrayMap.this.key[this.pos++];
               }
            }

            @Override
            public void remove() {
               if (this.pos == 0) {
                  throw new IllegalStateException();
               } else {
                  int tail = Long2DoubleArrayMap.this.size - this.pos;
                  System.arraycopy(Long2DoubleArrayMap.this.key, this.pos, Long2DoubleArrayMap.this.key, this.pos - 1, tail);
                  System.arraycopy(Long2DoubleArrayMap.this.value, this.pos, Long2DoubleArrayMap.this.value, this.pos - 1, tail);
                  Long2DoubleArrayMap.this.size--;
                  this.pos--;
               }
            }

            @Override
            public void forEachRemaining(java.util.function.LongConsumer action) {
               int max = Long2DoubleArrayMap.this.size;

               while (this.pos < max) {
                  action.accept(Long2DoubleArrayMap.this.key[this.pos++]);
               }
            }
         };
      }

      @Override
      public LongSpliterator spliterator() {
         return new Long2DoubleArrayMap.KeySet.KeySetSpliterator(0, Long2DoubleArrayMap.this.size);
      }

      @Override
      public void forEach(java.util.function.LongConsumer action) {
         int i = 0;

         for (int max = Long2DoubleArrayMap.this.size; i < max; i++) {
            action.accept(Long2DoubleArrayMap.this.key[i]);
         }
      }

      @Override
      public int size() {
         return Long2DoubleArrayMap.this.size;
      }

      @Override
      public void clear() {
         Long2DoubleArrayMap.this.clear();
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
            return Long2DoubleArrayMap.this.key[location];
         }

         protected final Long2DoubleArrayMap.KeySet.KeySetSpliterator makeForSplit(int pos, int maxPos) {
            return KeySet.this.new KeySetSpliterator(pos, maxPos);
         }

         @Override
         public void forEachRemaining(java.util.function.LongConsumer action) {
            int max = Long2DoubleArrayMap.this.size;

            while (this.pos < max) {
               action.accept(Long2DoubleArrayMap.this.key[this.pos++]);
            }
         }
      }
   }

   private final class ValuesCollection extends AbstractDoubleCollection {
      private ValuesCollection() {
      }

      @Override
      public boolean contains(double v) {
         return Long2DoubleArrayMap.this.containsValue(v);
      }

      @Override
      public DoubleIterator iterator() {
         return new DoubleIterator() {
            int pos = 0;

            @Override
            public boolean hasNext() {
               return this.pos < Long2DoubleArrayMap.this.size;
            }

            @Override
            public double nextDouble() {
               if (!this.hasNext()) {
                  throw new NoSuchElementException();
               } else {
                  return Long2DoubleArrayMap.this.value[this.pos++];
               }
            }

            @Override
            public void remove() {
               if (this.pos == 0) {
                  throw new IllegalStateException();
               } else {
                  int tail = Long2DoubleArrayMap.this.size - this.pos;
                  System.arraycopy(Long2DoubleArrayMap.this.key, this.pos, Long2DoubleArrayMap.this.key, this.pos - 1, tail);
                  System.arraycopy(Long2DoubleArrayMap.this.value, this.pos, Long2DoubleArrayMap.this.value, this.pos - 1, tail);
                  Long2DoubleArrayMap.this.size--;
                  this.pos--;
               }
            }

            @Override
            public void forEachRemaining(DoubleConsumer action) {
               int max = Long2DoubleArrayMap.this.size;

               while (this.pos < max) {
                  action.accept(Long2DoubleArrayMap.this.value[this.pos++]);
               }
            }
         };
      }

      @Override
      public DoubleSpliterator spliterator() {
         return new Long2DoubleArrayMap.ValuesCollection.ValuesSpliterator(0, Long2DoubleArrayMap.this.size);
      }

      @Override
      public void forEach(DoubleConsumer action) {
         int i = 0;

         for (int max = Long2DoubleArrayMap.this.size; i < max; i++) {
            action.accept(Long2DoubleArrayMap.this.value[i]);
         }
      }

      @Override
      public int size() {
         return Long2DoubleArrayMap.this.size;
      }

      @Override
      public void clear() {
         Long2DoubleArrayMap.this.clear();
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
            return Long2DoubleArrayMap.this.value[location];
         }

         protected final Long2DoubleArrayMap.ValuesCollection.ValuesSpliterator makeForSplit(int pos, int maxPos) {
            return ValuesCollection.this.new ValuesSpliterator(pos, maxPos);
         }

         @Override
         public void forEachRemaining(DoubleConsumer action) {
            int max = Long2DoubleArrayMap.this.size;

            while (this.pos < max) {
               action.accept(Long2DoubleArrayMap.this.value[this.pos++]);
            }
         }
      }
   }
}
