package it.unimi.dsi.fastutil.ints;

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

public class Int2DoubleArrayMap extends AbstractInt2DoubleMap implements Serializable, Cloneable {
   private static final long serialVersionUID = 1L;
   protected transient int[] key;
   protected transient double[] value;
   protected int size;
   protected transient Int2DoubleMap.FastEntrySet entries;
   protected transient IntSet keys;
   protected transient DoubleCollection values;

   public Int2DoubleArrayMap(int[] key, double[] value) {
      this.key = key;
      this.value = value;
      this.size = key.length;
      if (key.length != value.length) {
         throw new IllegalArgumentException("Keys and values have different lengths (" + key.length + ", " + value.length + ")");
      }
   }

   public Int2DoubleArrayMap() {
      this.key = IntArrays.EMPTY_ARRAY;
      this.value = DoubleArrays.EMPTY_ARRAY;
   }

   public Int2DoubleArrayMap(int capacity) {
      this.key = new int[capacity];
      this.value = new double[capacity];
   }

   public Int2DoubleArrayMap(Int2DoubleMap m) {
      this(m.size());
      int i = 0;

      for (Int2DoubleMap.Entry e : m.int2DoubleEntrySet()) {
         this.key[i] = e.getIntKey();
         this.value[i] = e.getDoubleValue();
         i++;
      }

      this.size = i;
   }

   public Int2DoubleArrayMap(Map<? extends Integer, ? extends Double> m) {
      this(m.size());
      int i = 0;

      for (Entry<? extends Integer, ? extends Double> e : m.entrySet()) {
         this.key[i] = e.getKey();
         this.value[i] = e.getValue();
         i++;
      }

      this.size = i;
   }

   public Int2DoubleArrayMap(int[] key, double[] value, int size) {
      this.key = key;
      this.value = value;
      this.size = size;
      if (key.length != value.length) {
         throw new IllegalArgumentException("Keys and values have different lengths (" + key.length + ", " + value.length + ")");
      } else if (size > key.length) {
         throw new IllegalArgumentException("The provided size (" + size + ") is larger than or equal to the backing-arrays size (" + key.length + ")");
      }
   }

   public Int2DoubleMap.FastEntrySet int2DoubleEntrySet() {
      if (this.entries == null) {
         this.entries = new Int2DoubleArrayMap.EntrySet();
      }

      return this.entries;
   }

   private int findKey(int k) {
      int[] key = this.key;
      int i = this.size;

      while (i-- != 0) {
         if (key[i] == k) {
            return i;
         }
      }

      return -1;
   }

   @Override
   public double get(int k) {
      int[] key = this.key;
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
   public boolean containsKey(int k) {
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
   public double put(int k, double v) {
      int oldKey = this.findKey(k);
      if (oldKey != -1) {
         double oldValue = this.value[oldKey];
         this.value[oldKey] = v;
         return oldValue;
      } else {
         if (this.size == this.key.length) {
            int[] newKey = new int[this.size == 0 ? 2 : this.size * 2];
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
   public double remove(int k) {
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
   public IntSet keySet() {
      if (this.keys == null) {
         this.keys = new Int2DoubleArrayMap.KeySet();
      }

      return this.keys;
   }

   @Override
   public DoubleCollection values() {
      if (this.values == null) {
         this.values = new Int2DoubleArrayMap.ValuesCollection();
      }

      return this.values;
   }

   public Int2DoubleArrayMap clone() {
      Int2DoubleArrayMap c;
      try {
         c = (Int2DoubleArrayMap)super.clone();
      } catch (CloneNotSupportedException var3) {
         throw new InternalError();
      }

      c.key = (int[])this.key.clone();
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
         s.writeInt(this.key[i]);
         s.writeDouble(this.value[i]);
      }
   }

   private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
      s.defaultReadObject();
      this.key = new int[this.size];
      this.value = new double[this.size];

      for (int i = 0; i < this.size; i++) {
         this.key[i] = s.readInt();
         this.value[i] = s.readDouble();
      }
   }

   private final class EntrySet extends AbstractObjectSet<Int2DoubleMap.Entry> implements Int2DoubleMap.FastEntrySet {
      private EntrySet() {
      }

      @Override
      public ObjectIterator<Int2DoubleMap.Entry> iterator() {
         return new ObjectIterator<Int2DoubleMap.Entry>() {
            int curr = -1;
            int next = 0;

            @Override
            public boolean hasNext() {
               return this.next < Int2DoubleArrayMap.this.size;
            }

            public Int2DoubleMap.Entry next() {
               if (!this.hasNext()) {
                  throw new NoSuchElementException();
               } else {
                  return new AbstractInt2DoubleMap.BasicEntry(Int2DoubleArrayMap.this.key[this.curr = this.next], Int2DoubleArrayMap.this.value[this.next++]);
               }
            }

            @Override
            public void remove() {
               if (this.curr == -1) {
                  throw new IllegalStateException();
               } else {
                  this.curr = -1;
                  int tail = Int2DoubleArrayMap.this.size-- - this.next--;
                  System.arraycopy(Int2DoubleArrayMap.this.key, this.next + 1, Int2DoubleArrayMap.this.key, this.next, tail);
                  System.arraycopy(Int2DoubleArrayMap.this.value, this.next + 1, Int2DoubleArrayMap.this.value, this.next, tail);
               }
            }

            @Override
            public void forEachRemaining(Consumer<? super Int2DoubleMap.Entry> action) {
               int max = Int2DoubleArrayMap.this.size;

               while (this.next < max) {
                  action.accept(
                     new AbstractInt2DoubleMap.BasicEntry(Int2DoubleArrayMap.this.key[this.curr = this.next], Int2DoubleArrayMap.this.value[this.next++])
                  );
               }
            }
         };
      }

      @Override
      public ObjectIterator<Int2DoubleMap.Entry> fastIterator() {
         return new ObjectIterator<Int2DoubleMap.Entry>() {
            int next = 0;
            int curr = -1;
            final AbstractInt2DoubleMap.BasicEntry entry = new AbstractInt2DoubleMap.BasicEntry();

            @Override
            public boolean hasNext() {
               return this.next < Int2DoubleArrayMap.this.size;
            }

            public Int2DoubleMap.Entry next() {
               if (!this.hasNext()) {
                  throw new NoSuchElementException();
               } else {
                  this.entry.key = Int2DoubleArrayMap.this.key[this.curr = this.next];
                  this.entry.value = Int2DoubleArrayMap.this.value[this.next++];
                  return this.entry;
               }
            }

            @Override
            public void remove() {
               if (this.curr == -1) {
                  throw new IllegalStateException();
               } else {
                  this.curr = -1;
                  int tail = Int2DoubleArrayMap.this.size-- - this.next--;
                  System.arraycopy(Int2DoubleArrayMap.this.key, this.next + 1, Int2DoubleArrayMap.this.key, this.next, tail);
                  System.arraycopy(Int2DoubleArrayMap.this.value, this.next + 1, Int2DoubleArrayMap.this.value, this.next, tail);
               }
            }

            @Override
            public void forEachRemaining(Consumer<? super Int2DoubleMap.Entry> action) {
               int max = Int2DoubleArrayMap.this.size;

               while (this.next < max) {
                  this.entry.key = Int2DoubleArrayMap.this.key[this.curr = this.next];
                  this.entry.value = Int2DoubleArrayMap.this.value[this.next++];
                  action.accept(this.entry);
               }
            }
         };
      }

      @Override
      public ObjectSpliterator<Int2DoubleMap.Entry> spliterator() {
         return new Int2DoubleArrayMap.EntrySet.EntrySetSpliterator(0, Int2DoubleArrayMap.this.size);
      }

      @Override
      public void forEach(Consumer<? super Int2DoubleMap.Entry> action) {
         int i = 0;

         for (int max = Int2DoubleArrayMap.this.size; i < max; i++) {
            action.accept(new AbstractInt2DoubleMap.BasicEntry(Int2DoubleArrayMap.this.key[i], Int2DoubleArrayMap.this.value[i]));
         }
      }

      @Override
      public void fastForEach(Consumer<? super Int2DoubleMap.Entry> action) {
         AbstractInt2DoubleMap.BasicEntry entry = new AbstractInt2DoubleMap.BasicEntry();
         int i = 0;

         for (int max = Int2DoubleArrayMap.this.size; i < max; i++) {
            entry.key = Int2DoubleArrayMap.this.key[i];
            entry.value = Int2DoubleArrayMap.this.value[i];
            action.accept(entry);
         }
      }

      @Override
      public int size() {
         return Int2DoubleArrayMap.this.size;
      }

      @Override
      public boolean contains(Object o) {
         if (!(o instanceof Entry)) {
            return false;
         } else {
            Entry<?, ?> e = (Entry<?, ?>)o;
            if (e.getKey() == null || !(e.getKey() instanceof Integer)) {
               return false;
            } else if (e.getValue() != null && e.getValue() instanceof Double) {
               int k = (Integer)e.getKey();
               return Int2DoubleArrayMap.this.containsKey(k)
                  && Double.doubleToLongBits(Int2DoubleArrayMap.this.get(k)) == Double.doubleToLongBits((Double)e.getValue());
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
            if (e.getKey() == null || !(e.getKey() instanceof Integer)) {
               return false;
            } else if (e.getValue() != null && e.getValue() instanceof Double) {
               int k = (Integer)e.getKey();
               double v = (Double)e.getValue();
               int oldPos = Int2DoubleArrayMap.this.findKey(k);
               if (oldPos != -1 && Double.doubleToLongBits(v) == Double.doubleToLongBits(Int2DoubleArrayMap.this.value[oldPos])) {
                  int tail = Int2DoubleArrayMap.this.size - oldPos - 1;
                  System.arraycopy(Int2DoubleArrayMap.this.key, oldPos + 1, Int2DoubleArrayMap.this.key, oldPos, tail);
                  System.arraycopy(Int2DoubleArrayMap.this.value, oldPos + 1, Int2DoubleArrayMap.this.value, oldPos, tail);
                  Int2DoubleArrayMap.this.size--;
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
         extends ObjectSpliterators.EarlyBindingSizeIndexBasedSpliterator<Int2DoubleMap.Entry>
         implements ObjectSpliterator<Int2DoubleMap.Entry> {
         EntrySetSpliterator(int pos, int maxPos) {
            super(pos, maxPos);
         }

         @Override
         public int characteristics() {
            return 16465;
         }

         protected final Int2DoubleMap.Entry get(int location) {
            return new AbstractInt2DoubleMap.BasicEntry(Int2DoubleArrayMap.this.key[location], Int2DoubleArrayMap.this.value[location]);
         }

         protected final Int2DoubleArrayMap.EntrySet.EntrySetSpliterator makeForSplit(int pos, int maxPos) {
            return EntrySet.this.new EntrySetSpliterator(pos, maxPos);
         }
      }
   }

   private final class KeySet extends AbstractIntSet {
      private KeySet() {
      }

      @Override
      public boolean contains(int k) {
         return Int2DoubleArrayMap.this.findKey(k) != -1;
      }

      @Override
      public boolean remove(int k) {
         int oldPos = Int2DoubleArrayMap.this.findKey(k);
         if (oldPos == -1) {
            return false;
         } else {
            int tail = Int2DoubleArrayMap.this.size - oldPos - 1;
            System.arraycopy(Int2DoubleArrayMap.this.key, oldPos + 1, Int2DoubleArrayMap.this.key, oldPos, tail);
            System.arraycopy(Int2DoubleArrayMap.this.value, oldPos + 1, Int2DoubleArrayMap.this.value, oldPos, tail);
            Int2DoubleArrayMap.this.size--;
            return true;
         }
      }

      @Override
      public IntIterator iterator() {
         return new IntIterator() {
            int pos = 0;

            @Override
            public boolean hasNext() {
               return this.pos < Int2DoubleArrayMap.this.size;
            }

            @Override
            public int nextInt() {
               if (!this.hasNext()) {
                  throw new NoSuchElementException();
               } else {
                  return Int2DoubleArrayMap.this.key[this.pos++];
               }
            }

            @Override
            public void remove() {
               if (this.pos == 0) {
                  throw new IllegalStateException();
               } else {
                  int tail = Int2DoubleArrayMap.this.size - this.pos;
                  System.arraycopy(Int2DoubleArrayMap.this.key, this.pos, Int2DoubleArrayMap.this.key, this.pos - 1, tail);
                  System.arraycopy(Int2DoubleArrayMap.this.value, this.pos, Int2DoubleArrayMap.this.value, this.pos - 1, tail);
                  Int2DoubleArrayMap.this.size--;
                  this.pos--;
               }
            }

            @Override
            public void forEachRemaining(java.util.function.IntConsumer action) {
               int max = Int2DoubleArrayMap.this.size;

               while (this.pos < max) {
                  action.accept(Int2DoubleArrayMap.this.key[this.pos++]);
               }
            }
         };
      }

      @Override
      public IntSpliterator spliterator() {
         return new Int2DoubleArrayMap.KeySet.KeySetSpliterator(0, Int2DoubleArrayMap.this.size);
      }

      @Override
      public void forEach(java.util.function.IntConsumer action) {
         int i = 0;

         for (int max = Int2DoubleArrayMap.this.size; i < max; i++) {
            action.accept(Int2DoubleArrayMap.this.key[i]);
         }
      }

      @Override
      public int size() {
         return Int2DoubleArrayMap.this.size;
      }

      @Override
      public void clear() {
         Int2DoubleArrayMap.this.clear();
      }

      final class KeySetSpliterator extends IntSpliterators.EarlyBindingSizeIndexBasedSpliterator implements IntSpliterator {
         KeySetSpliterator(int pos, int maxPos) {
            super(pos, maxPos);
         }

         @Override
         public int characteristics() {
            return 16721;
         }

         @Override
         protected final int get(int location) {
            return Int2DoubleArrayMap.this.key[location];
         }

         protected final Int2DoubleArrayMap.KeySet.KeySetSpliterator makeForSplit(int pos, int maxPos) {
            return KeySet.this.new KeySetSpliterator(pos, maxPos);
         }

         @Override
         public void forEachRemaining(java.util.function.IntConsumer action) {
            int max = Int2DoubleArrayMap.this.size;

            while (this.pos < max) {
               action.accept(Int2DoubleArrayMap.this.key[this.pos++]);
            }
         }
      }
   }

   private final class ValuesCollection extends AbstractDoubleCollection {
      private ValuesCollection() {
      }

      @Override
      public boolean contains(double v) {
         return Int2DoubleArrayMap.this.containsValue(v);
      }

      @Override
      public DoubleIterator iterator() {
         return new DoubleIterator() {
            int pos = 0;

            @Override
            public boolean hasNext() {
               return this.pos < Int2DoubleArrayMap.this.size;
            }

            @Override
            public double nextDouble() {
               if (!this.hasNext()) {
                  throw new NoSuchElementException();
               } else {
                  return Int2DoubleArrayMap.this.value[this.pos++];
               }
            }

            @Override
            public void remove() {
               if (this.pos == 0) {
                  throw new IllegalStateException();
               } else {
                  int tail = Int2DoubleArrayMap.this.size - this.pos;
                  System.arraycopy(Int2DoubleArrayMap.this.key, this.pos, Int2DoubleArrayMap.this.key, this.pos - 1, tail);
                  System.arraycopy(Int2DoubleArrayMap.this.value, this.pos, Int2DoubleArrayMap.this.value, this.pos - 1, tail);
                  Int2DoubleArrayMap.this.size--;
                  this.pos--;
               }
            }

            @Override
            public void forEachRemaining(DoubleConsumer action) {
               int max = Int2DoubleArrayMap.this.size;

               while (this.pos < max) {
                  action.accept(Int2DoubleArrayMap.this.value[this.pos++]);
               }
            }
         };
      }

      @Override
      public DoubleSpliterator spliterator() {
         return new Int2DoubleArrayMap.ValuesCollection.ValuesSpliterator(0, Int2DoubleArrayMap.this.size);
      }

      @Override
      public void forEach(DoubleConsumer action) {
         int i = 0;

         for (int max = Int2DoubleArrayMap.this.size; i < max; i++) {
            action.accept(Int2DoubleArrayMap.this.value[i]);
         }
      }

      @Override
      public int size() {
         return Int2DoubleArrayMap.this.size;
      }

      @Override
      public void clear() {
         Int2DoubleArrayMap.this.clear();
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
            return Int2DoubleArrayMap.this.value[location];
         }

         protected final Int2DoubleArrayMap.ValuesCollection.ValuesSpliterator makeForSplit(int pos, int maxPos) {
            return ValuesCollection.this.new ValuesSpliterator(pos, maxPos);
         }

         @Override
         public void forEachRemaining(DoubleConsumer action) {
            int max = Int2DoubleArrayMap.this.size;

            while (this.pos < max) {
               action.accept(Int2DoubleArrayMap.this.value[this.pos++]);
            }
         }
      }
   }
}
