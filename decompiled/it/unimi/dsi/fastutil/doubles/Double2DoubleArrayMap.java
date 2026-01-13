package it.unimi.dsi.fastutil.doubles;

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

public class Double2DoubleArrayMap extends AbstractDouble2DoubleMap implements Serializable, Cloneable {
   private static final long serialVersionUID = 1L;
   protected transient double[] key;
   protected transient double[] value;
   protected int size;
   protected transient Double2DoubleMap.FastEntrySet entries;
   protected transient DoubleSet keys;
   protected transient DoubleCollection values;

   public Double2DoubleArrayMap(double[] key, double[] value) {
      this.key = key;
      this.value = value;
      this.size = key.length;
      if (key.length != value.length) {
         throw new IllegalArgumentException("Keys and values have different lengths (" + key.length + ", " + value.length + ")");
      }
   }

   public Double2DoubleArrayMap() {
      this.key = DoubleArrays.EMPTY_ARRAY;
      this.value = DoubleArrays.EMPTY_ARRAY;
   }

   public Double2DoubleArrayMap(int capacity) {
      this.key = new double[capacity];
      this.value = new double[capacity];
   }

   public Double2DoubleArrayMap(Double2DoubleMap m) {
      this(m.size());
      int i = 0;

      for (Double2DoubleMap.Entry e : m.double2DoubleEntrySet()) {
         this.key[i] = e.getDoubleKey();
         this.value[i] = e.getDoubleValue();
         i++;
      }

      this.size = i;
   }

   public Double2DoubleArrayMap(Map<? extends Double, ? extends Double> m) {
      this(m.size());
      int i = 0;

      for (Entry<? extends Double, ? extends Double> e : m.entrySet()) {
         this.key[i] = e.getKey();
         this.value[i] = e.getValue();
         i++;
      }

      this.size = i;
   }

   public Double2DoubleArrayMap(double[] key, double[] value, int size) {
      this.key = key;
      this.value = value;
      this.size = size;
      if (key.length != value.length) {
         throw new IllegalArgumentException("Keys and values have different lengths (" + key.length + ", " + value.length + ")");
      } else if (size > key.length) {
         throw new IllegalArgumentException("The provided size (" + size + ") is larger than or equal to the backing-arrays size (" + key.length + ")");
      }
   }

   public Double2DoubleMap.FastEntrySet double2DoubleEntrySet() {
      if (this.entries == null) {
         this.entries = new Double2DoubleArrayMap.EntrySet();
      }

      return this.entries;
   }

   private int findKey(double k) {
      double[] key = this.key;
      int i = this.size;

      while (i-- != 0) {
         if (Double.doubleToLongBits(key[i]) == Double.doubleToLongBits(k)) {
            return i;
         }
      }

      return -1;
   }

   @Override
   public double get(double k) {
      double[] key = this.key;
      int i = this.size;

      while (i-- != 0) {
         if (Double.doubleToLongBits(key[i]) == Double.doubleToLongBits(k)) {
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
   public boolean containsKey(double k) {
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
   public double put(double k, double v) {
      int oldKey = this.findKey(k);
      if (oldKey != -1) {
         double oldValue = this.value[oldKey];
         this.value[oldKey] = v;
         return oldValue;
      } else {
         if (this.size == this.key.length) {
            double[] newKey = new double[this.size == 0 ? 2 : this.size * 2];
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
   public double remove(double k) {
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
   public DoubleSet keySet() {
      if (this.keys == null) {
         this.keys = new Double2DoubleArrayMap.KeySet();
      }

      return this.keys;
   }

   @Override
   public DoubleCollection values() {
      if (this.values == null) {
         this.values = new Double2DoubleArrayMap.ValuesCollection();
      }

      return this.values;
   }

   public Double2DoubleArrayMap clone() {
      Double2DoubleArrayMap c;
      try {
         c = (Double2DoubleArrayMap)super.clone();
      } catch (CloneNotSupportedException var3) {
         throw new InternalError();
      }

      c.key = (double[])this.key.clone();
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
         s.writeDouble(this.key[i]);
         s.writeDouble(this.value[i]);
      }
   }

   private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
      s.defaultReadObject();
      this.key = new double[this.size];
      this.value = new double[this.size];

      for (int i = 0; i < this.size; i++) {
         this.key[i] = s.readDouble();
         this.value[i] = s.readDouble();
      }
   }

   private final class EntrySet extends AbstractObjectSet<Double2DoubleMap.Entry> implements Double2DoubleMap.FastEntrySet {
      private EntrySet() {
      }

      @Override
      public ObjectIterator<Double2DoubleMap.Entry> iterator() {
         return new ObjectIterator<Double2DoubleMap.Entry>() {
            int curr = -1;
            int next = 0;

            @Override
            public boolean hasNext() {
               return this.next < Double2DoubleArrayMap.this.size;
            }

            public Double2DoubleMap.Entry next() {
               if (!this.hasNext()) {
                  throw new NoSuchElementException();
               } else {
                  return new AbstractDouble2DoubleMap.BasicEntry(
                     Double2DoubleArrayMap.this.key[this.curr = this.next], Double2DoubleArrayMap.this.value[this.next++]
                  );
               }
            }

            @Override
            public void remove() {
               if (this.curr == -1) {
                  throw new IllegalStateException();
               } else {
                  this.curr = -1;
                  int tail = Double2DoubleArrayMap.this.size-- - this.next--;
                  System.arraycopy(Double2DoubleArrayMap.this.key, this.next + 1, Double2DoubleArrayMap.this.key, this.next, tail);
                  System.arraycopy(Double2DoubleArrayMap.this.value, this.next + 1, Double2DoubleArrayMap.this.value, this.next, tail);
               }
            }

            @Override
            public void forEachRemaining(Consumer<? super Double2DoubleMap.Entry> action) {
               int max = Double2DoubleArrayMap.this.size;

               while (this.next < max) {
                  action.accept(
                     new AbstractDouble2DoubleMap.BasicEntry(
                        Double2DoubleArrayMap.this.key[this.curr = this.next], Double2DoubleArrayMap.this.value[this.next++]
                     )
                  );
               }
            }
         };
      }

      @Override
      public ObjectIterator<Double2DoubleMap.Entry> fastIterator() {
         return new ObjectIterator<Double2DoubleMap.Entry>() {
            int next = 0;
            int curr = -1;
            final AbstractDouble2DoubleMap.BasicEntry entry = new AbstractDouble2DoubleMap.BasicEntry();

            @Override
            public boolean hasNext() {
               return this.next < Double2DoubleArrayMap.this.size;
            }

            public Double2DoubleMap.Entry next() {
               if (!this.hasNext()) {
                  throw new NoSuchElementException();
               } else {
                  this.entry.key = Double2DoubleArrayMap.this.key[this.curr = this.next];
                  this.entry.value = Double2DoubleArrayMap.this.value[this.next++];
                  return this.entry;
               }
            }

            @Override
            public void remove() {
               if (this.curr == -1) {
                  throw new IllegalStateException();
               } else {
                  this.curr = -1;
                  int tail = Double2DoubleArrayMap.this.size-- - this.next--;
                  System.arraycopy(Double2DoubleArrayMap.this.key, this.next + 1, Double2DoubleArrayMap.this.key, this.next, tail);
                  System.arraycopy(Double2DoubleArrayMap.this.value, this.next + 1, Double2DoubleArrayMap.this.value, this.next, tail);
               }
            }

            @Override
            public void forEachRemaining(Consumer<? super Double2DoubleMap.Entry> action) {
               int max = Double2DoubleArrayMap.this.size;

               while (this.next < max) {
                  this.entry.key = Double2DoubleArrayMap.this.key[this.curr = this.next];
                  this.entry.value = Double2DoubleArrayMap.this.value[this.next++];
                  action.accept(this.entry);
               }
            }
         };
      }

      @Override
      public ObjectSpliterator<Double2DoubleMap.Entry> spliterator() {
         return new Double2DoubleArrayMap.EntrySet.EntrySetSpliterator(0, Double2DoubleArrayMap.this.size);
      }

      @Override
      public void forEach(Consumer<? super Double2DoubleMap.Entry> action) {
         int i = 0;

         for (int max = Double2DoubleArrayMap.this.size; i < max; i++) {
            action.accept(new AbstractDouble2DoubleMap.BasicEntry(Double2DoubleArrayMap.this.key[i], Double2DoubleArrayMap.this.value[i]));
         }
      }

      @Override
      public void fastForEach(Consumer<? super Double2DoubleMap.Entry> action) {
         AbstractDouble2DoubleMap.BasicEntry entry = new AbstractDouble2DoubleMap.BasicEntry();
         int i = 0;

         for (int max = Double2DoubleArrayMap.this.size; i < max; i++) {
            entry.key = Double2DoubleArrayMap.this.key[i];
            entry.value = Double2DoubleArrayMap.this.value[i];
            action.accept(entry);
         }
      }

      @Override
      public int size() {
         return Double2DoubleArrayMap.this.size;
      }

      @Override
      public boolean contains(Object o) {
         if (!(o instanceof Entry)) {
            return false;
         } else {
            Entry<?, ?> e = (Entry<?, ?>)o;
            if (e.getKey() == null || !(e.getKey() instanceof Double)) {
               return false;
            } else if (e.getValue() != null && e.getValue() instanceof Double) {
               double k = (Double)e.getKey();
               return Double2DoubleArrayMap.this.containsKey(k)
                  && Double.doubleToLongBits(Double2DoubleArrayMap.this.get(k)) == Double.doubleToLongBits((Double)e.getValue());
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
            if (e.getKey() == null || !(e.getKey() instanceof Double)) {
               return false;
            } else if (e.getValue() != null && e.getValue() instanceof Double) {
               double k = (Double)e.getKey();
               double v = (Double)e.getValue();
               int oldPos = Double2DoubleArrayMap.this.findKey(k);
               if (oldPos != -1 && Double.doubleToLongBits(v) == Double.doubleToLongBits(Double2DoubleArrayMap.this.value[oldPos])) {
                  int tail = Double2DoubleArrayMap.this.size - oldPos - 1;
                  System.arraycopy(Double2DoubleArrayMap.this.key, oldPos + 1, Double2DoubleArrayMap.this.key, oldPos, tail);
                  System.arraycopy(Double2DoubleArrayMap.this.value, oldPos + 1, Double2DoubleArrayMap.this.value, oldPos, tail);
                  Double2DoubleArrayMap.this.size--;
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
         extends ObjectSpliterators.EarlyBindingSizeIndexBasedSpliterator<Double2DoubleMap.Entry>
         implements ObjectSpliterator<Double2DoubleMap.Entry> {
         EntrySetSpliterator(int pos, int maxPos) {
            super(pos, maxPos);
         }

         @Override
         public int characteristics() {
            return 16465;
         }

         protected final Double2DoubleMap.Entry get(int location) {
            return new AbstractDouble2DoubleMap.BasicEntry(Double2DoubleArrayMap.this.key[location], Double2DoubleArrayMap.this.value[location]);
         }

         protected final Double2DoubleArrayMap.EntrySet.EntrySetSpliterator makeForSplit(int pos, int maxPos) {
            return EntrySet.this.new EntrySetSpliterator(pos, maxPos);
         }
      }
   }

   private final class KeySet extends AbstractDoubleSet {
      private KeySet() {
      }

      @Override
      public boolean contains(double k) {
         return Double2DoubleArrayMap.this.findKey(k) != -1;
      }

      @Override
      public boolean remove(double k) {
         int oldPos = Double2DoubleArrayMap.this.findKey(k);
         if (oldPos == -1) {
            return false;
         } else {
            int tail = Double2DoubleArrayMap.this.size - oldPos - 1;
            System.arraycopy(Double2DoubleArrayMap.this.key, oldPos + 1, Double2DoubleArrayMap.this.key, oldPos, tail);
            System.arraycopy(Double2DoubleArrayMap.this.value, oldPos + 1, Double2DoubleArrayMap.this.value, oldPos, tail);
            Double2DoubleArrayMap.this.size--;
            return true;
         }
      }

      @Override
      public DoubleIterator iterator() {
         return new DoubleIterator() {
            int pos = 0;

            @Override
            public boolean hasNext() {
               return this.pos < Double2DoubleArrayMap.this.size;
            }

            @Override
            public double nextDouble() {
               if (!this.hasNext()) {
                  throw new NoSuchElementException();
               } else {
                  return Double2DoubleArrayMap.this.key[this.pos++];
               }
            }

            @Override
            public void remove() {
               if (this.pos == 0) {
                  throw new IllegalStateException();
               } else {
                  int tail = Double2DoubleArrayMap.this.size - this.pos;
                  System.arraycopy(Double2DoubleArrayMap.this.key, this.pos, Double2DoubleArrayMap.this.key, this.pos - 1, tail);
                  System.arraycopy(Double2DoubleArrayMap.this.value, this.pos, Double2DoubleArrayMap.this.value, this.pos - 1, tail);
                  Double2DoubleArrayMap.this.size--;
                  this.pos--;
               }
            }

            @Override
            public void forEachRemaining(java.util.function.DoubleConsumer action) {
               int max = Double2DoubleArrayMap.this.size;

               while (this.pos < max) {
                  action.accept(Double2DoubleArrayMap.this.key[this.pos++]);
               }
            }
         };
      }

      @Override
      public DoubleSpliterator spliterator() {
         return new Double2DoubleArrayMap.KeySet.KeySetSpliterator(0, Double2DoubleArrayMap.this.size);
      }

      @Override
      public void forEach(java.util.function.DoubleConsumer action) {
         int i = 0;

         for (int max = Double2DoubleArrayMap.this.size; i < max; i++) {
            action.accept(Double2DoubleArrayMap.this.key[i]);
         }
      }

      @Override
      public int size() {
         return Double2DoubleArrayMap.this.size;
      }

      @Override
      public void clear() {
         Double2DoubleArrayMap.this.clear();
      }

      final class KeySetSpliterator extends DoubleSpliterators.EarlyBindingSizeIndexBasedSpliterator implements DoubleSpliterator {
         KeySetSpliterator(int pos, int maxPos) {
            super(pos, maxPos);
         }

         @Override
         public int characteristics() {
            return 16721;
         }

         @Override
         protected final double get(int location) {
            return Double2DoubleArrayMap.this.key[location];
         }

         protected final Double2DoubleArrayMap.KeySet.KeySetSpliterator makeForSplit(int pos, int maxPos) {
            return KeySet.this.new KeySetSpliterator(pos, maxPos);
         }

         @Override
         public void forEachRemaining(java.util.function.DoubleConsumer action) {
            int max = Double2DoubleArrayMap.this.size;

            while (this.pos < max) {
               action.accept(Double2DoubleArrayMap.this.key[this.pos++]);
            }
         }
      }
   }

   private final class ValuesCollection extends AbstractDoubleCollection {
      private ValuesCollection() {
      }

      @Override
      public boolean contains(double v) {
         return Double2DoubleArrayMap.this.containsValue(v);
      }

      @Override
      public DoubleIterator iterator() {
         return new DoubleIterator() {
            int pos = 0;

            @Override
            public boolean hasNext() {
               return this.pos < Double2DoubleArrayMap.this.size;
            }

            @Override
            public double nextDouble() {
               if (!this.hasNext()) {
                  throw new NoSuchElementException();
               } else {
                  return Double2DoubleArrayMap.this.value[this.pos++];
               }
            }

            @Override
            public void remove() {
               if (this.pos == 0) {
                  throw new IllegalStateException();
               } else {
                  int tail = Double2DoubleArrayMap.this.size - this.pos;
                  System.arraycopy(Double2DoubleArrayMap.this.key, this.pos, Double2DoubleArrayMap.this.key, this.pos - 1, tail);
                  System.arraycopy(Double2DoubleArrayMap.this.value, this.pos, Double2DoubleArrayMap.this.value, this.pos - 1, tail);
                  Double2DoubleArrayMap.this.size--;
                  this.pos--;
               }
            }

            @Override
            public void forEachRemaining(java.util.function.DoubleConsumer action) {
               int max = Double2DoubleArrayMap.this.size;

               while (this.pos < max) {
                  action.accept(Double2DoubleArrayMap.this.value[this.pos++]);
               }
            }
         };
      }

      @Override
      public DoubleSpliterator spliterator() {
         return new Double2DoubleArrayMap.ValuesCollection.ValuesSpliterator(0, Double2DoubleArrayMap.this.size);
      }

      @Override
      public void forEach(java.util.function.DoubleConsumer action) {
         int i = 0;

         for (int max = Double2DoubleArrayMap.this.size; i < max; i++) {
            action.accept(Double2DoubleArrayMap.this.value[i]);
         }
      }

      @Override
      public int size() {
         return Double2DoubleArrayMap.this.size;
      }

      @Override
      public void clear() {
         Double2DoubleArrayMap.this.clear();
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
            return Double2DoubleArrayMap.this.value[location];
         }

         protected final Double2DoubleArrayMap.ValuesCollection.ValuesSpliterator makeForSplit(int pos, int maxPos) {
            return ValuesCollection.this.new ValuesSpliterator(pos, maxPos);
         }

         @Override
         public void forEachRemaining(java.util.function.DoubleConsumer action) {
            int max = Double2DoubleArrayMap.this.size;

            while (this.pos < max) {
               action.accept(Double2DoubleArrayMap.this.value[this.pos++]);
            }
         }
      }
   }
}
