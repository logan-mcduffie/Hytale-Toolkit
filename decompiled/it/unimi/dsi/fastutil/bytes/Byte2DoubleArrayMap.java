package it.unimi.dsi.fastutil.bytes;

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

public class Byte2DoubleArrayMap extends AbstractByte2DoubleMap implements Serializable, Cloneable {
   private static final long serialVersionUID = 1L;
   protected transient byte[] key;
   protected transient double[] value;
   protected int size;
   protected transient Byte2DoubleMap.FastEntrySet entries;
   protected transient ByteSet keys;
   protected transient DoubleCollection values;

   public Byte2DoubleArrayMap(byte[] key, double[] value) {
      this.key = key;
      this.value = value;
      this.size = key.length;
      if (key.length != value.length) {
         throw new IllegalArgumentException("Keys and values have different lengths (" + key.length + ", " + value.length + ")");
      }
   }

   public Byte2DoubleArrayMap() {
      this.key = ByteArrays.EMPTY_ARRAY;
      this.value = DoubleArrays.EMPTY_ARRAY;
   }

   public Byte2DoubleArrayMap(int capacity) {
      this.key = new byte[capacity];
      this.value = new double[capacity];
   }

   public Byte2DoubleArrayMap(Byte2DoubleMap m) {
      this(m.size());
      int i = 0;

      for (Byte2DoubleMap.Entry e : m.byte2DoubleEntrySet()) {
         this.key[i] = e.getByteKey();
         this.value[i] = e.getDoubleValue();
         i++;
      }

      this.size = i;
   }

   public Byte2DoubleArrayMap(Map<? extends Byte, ? extends Double> m) {
      this(m.size());
      int i = 0;

      for (Entry<? extends Byte, ? extends Double> e : m.entrySet()) {
         this.key[i] = e.getKey();
         this.value[i] = e.getValue();
         i++;
      }

      this.size = i;
   }

   public Byte2DoubleArrayMap(byte[] key, double[] value, int size) {
      this.key = key;
      this.value = value;
      this.size = size;
      if (key.length != value.length) {
         throw new IllegalArgumentException("Keys and values have different lengths (" + key.length + ", " + value.length + ")");
      } else if (size > key.length) {
         throw new IllegalArgumentException("The provided size (" + size + ") is larger than or equal to the backing-arrays size (" + key.length + ")");
      }
   }

   public Byte2DoubleMap.FastEntrySet byte2DoubleEntrySet() {
      if (this.entries == null) {
         this.entries = new Byte2DoubleArrayMap.EntrySet();
      }

      return this.entries;
   }

   private int findKey(byte k) {
      byte[] key = this.key;
      int i = this.size;

      while (i-- != 0) {
         if (key[i] == k) {
            return i;
         }
      }

      return -1;
   }

   @Override
   public double get(byte k) {
      byte[] key = this.key;
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
   public boolean containsKey(byte k) {
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
   public double put(byte k, double v) {
      int oldKey = this.findKey(k);
      if (oldKey != -1) {
         double oldValue = this.value[oldKey];
         this.value[oldKey] = v;
         return oldValue;
      } else {
         if (this.size == this.key.length) {
            byte[] newKey = new byte[this.size == 0 ? 2 : this.size * 2];
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
   public double remove(byte k) {
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
   public ByteSet keySet() {
      if (this.keys == null) {
         this.keys = new Byte2DoubleArrayMap.KeySet();
      }

      return this.keys;
   }

   @Override
   public DoubleCollection values() {
      if (this.values == null) {
         this.values = new Byte2DoubleArrayMap.ValuesCollection();
      }

      return this.values;
   }

   public Byte2DoubleArrayMap clone() {
      Byte2DoubleArrayMap c;
      try {
         c = (Byte2DoubleArrayMap)super.clone();
      } catch (CloneNotSupportedException var3) {
         throw new InternalError();
      }

      c.key = (byte[])this.key.clone();
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
         s.writeByte(this.key[i]);
         s.writeDouble(this.value[i]);
      }
   }

   private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
      s.defaultReadObject();
      this.key = new byte[this.size];
      this.value = new double[this.size];

      for (int i = 0; i < this.size; i++) {
         this.key[i] = s.readByte();
         this.value[i] = s.readDouble();
      }
   }

   private final class EntrySet extends AbstractObjectSet<Byte2DoubleMap.Entry> implements Byte2DoubleMap.FastEntrySet {
      private EntrySet() {
      }

      @Override
      public ObjectIterator<Byte2DoubleMap.Entry> iterator() {
         return new ObjectIterator<Byte2DoubleMap.Entry>() {
            int curr = -1;
            int next = 0;

            @Override
            public boolean hasNext() {
               return this.next < Byte2DoubleArrayMap.this.size;
            }

            public Byte2DoubleMap.Entry next() {
               if (!this.hasNext()) {
                  throw new NoSuchElementException();
               } else {
                  return new AbstractByte2DoubleMap.BasicEntry(Byte2DoubleArrayMap.this.key[this.curr = this.next], Byte2DoubleArrayMap.this.value[this.next++]);
               }
            }

            @Override
            public void remove() {
               if (this.curr == -1) {
                  throw new IllegalStateException();
               } else {
                  this.curr = -1;
                  int tail = Byte2DoubleArrayMap.this.size-- - this.next--;
                  System.arraycopy(Byte2DoubleArrayMap.this.key, this.next + 1, Byte2DoubleArrayMap.this.key, this.next, tail);
                  System.arraycopy(Byte2DoubleArrayMap.this.value, this.next + 1, Byte2DoubleArrayMap.this.value, this.next, tail);
               }
            }

            @Override
            public void forEachRemaining(Consumer<? super Byte2DoubleMap.Entry> action) {
               int max = Byte2DoubleArrayMap.this.size;

               while (this.next < max) {
                  action.accept(
                     new AbstractByte2DoubleMap.BasicEntry(Byte2DoubleArrayMap.this.key[this.curr = this.next], Byte2DoubleArrayMap.this.value[this.next++])
                  );
               }
            }
         };
      }

      @Override
      public ObjectIterator<Byte2DoubleMap.Entry> fastIterator() {
         return new ObjectIterator<Byte2DoubleMap.Entry>() {
            int next = 0;
            int curr = -1;
            final AbstractByte2DoubleMap.BasicEntry entry = new AbstractByte2DoubleMap.BasicEntry();

            @Override
            public boolean hasNext() {
               return this.next < Byte2DoubleArrayMap.this.size;
            }

            public Byte2DoubleMap.Entry next() {
               if (!this.hasNext()) {
                  throw new NoSuchElementException();
               } else {
                  this.entry.key = Byte2DoubleArrayMap.this.key[this.curr = this.next];
                  this.entry.value = Byte2DoubleArrayMap.this.value[this.next++];
                  return this.entry;
               }
            }

            @Override
            public void remove() {
               if (this.curr == -1) {
                  throw new IllegalStateException();
               } else {
                  this.curr = -1;
                  int tail = Byte2DoubleArrayMap.this.size-- - this.next--;
                  System.arraycopy(Byte2DoubleArrayMap.this.key, this.next + 1, Byte2DoubleArrayMap.this.key, this.next, tail);
                  System.arraycopy(Byte2DoubleArrayMap.this.value, this.next + 1, Byte2DoubleArrayMap.this.value, this.next, tail);
               }
            }

            @Override
            public void forEachRemaining(Consumer<? super Byte2DoubleMap.Entry> action) {
               int max = Byte2DoubleArrayMap.this.size;

               while (this.next < max) {
                  this.entry.key = Byte2DoubleArrayMap.this.key[this.curr = this.next];
                  this.entry.value = Byte2DoubleArrayMap.this.value[this.next++];
                  action.accept(this.entry);
               }
            }
         };
      }

      @Override
      public ObjectSpliterator<Byte2DoubleMap.Entry> spliterator() {
         return new Byte2DoubleArrayMap.EntrySet.EntrySetSpliterator(0, Byte2DoubleArrayMap.this.size);
      }

      @Override
      public void forEach(Consumer<? super Byte2DoubleMap.Entry> action) {
         int i = 0;

         for (int max = Byte2DoubleArrayMap.this.size; i < max; i++) {
            action.accept(new AbstractByte2DoubleMap.BasicEntry(Byte2DoubleArrayMap.this.key[i], Byte2DoubleArrayMap.this.value[i]));
         }
      }

      @Override
      public void fastForEach(Consumer<? super Byte2DoubleMap.Entry> action) {
         AbstractByte2DoubleMap.BasicEntry entry = new AbstractByte2DoubleMap.BasicEntry();
         int i = 0;

         for (int max = Byte2DoubleArrayMap.this.size; i < max; i++) {
            entry.key = Byte2DoubleArrayMap.this.key[i];
            entry.value = Byte2DoubleArrayMap.this.value[i];
            action.accept(entry);
         }
      }

      @Override
      public int size() {
         return Byte2DoubleArrayMap.this.size;
      }

      @Override
      public boolean contains(Object o) {
         if (!(o instanceof Entry)) {
            return false;
         } else {
            Entry<?, ?> e = (Entry<?, ?>)o;
            if (e.getKey() == null || !(e.getKey() instanceof Byte)) {
               return false;
            } else if (e.getValue() != null && e.getValue() instanceof Double) {
               byte k = (Byte)e.getKey();
               return Byte2DoubleArrayMap.this.containsKey(k)
                  && Double.doubleToLongBits(Byte2DoubleArrayMap.this.get(k)) == Double.doubleToLongBits((Double)e.getValue());
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
            if (e.getKey() == null || !(e.getKey() instanceof Byte)) {
               return false;
            } else if (e.getValue() != null && e.getValue() instanceof Double) {
               byte k = (Byte)e.getKey();
               double v = (Double)e.getValue();
               int oldPos = Byte2DoubleArrayMap.this.findKey(k);
               if (oldPos != -1 && Double.doubleToLongBits(v) == Double.doubleToLongBits(Byte2DoubleArrayMap.this.value[oldPos])) {
                  int tail = Byte2DoubleArrayMap.this.size - oldPos - 1;
                  System.arraycopy(Byte2DoubleArrayMap.this.key, oldPos + 1, Byte2DoubleArrayMap.this.key, oldPos, tail);
                  System.arraycopy(Byte2DoubleArrayMap.this.value, oldPos + 1, Byte2DoubleArrayMap.this.value, oldPos, tail);
                  Byte2DoubleArrayMap.this.size--;
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
         extends ObjectSpliterators.EarlyBindingSizeIndexBasedSpliterator<Byte2DoubleMap.Entry>
         implements ObjectSpliterator<Byte2DoubleMap.Entry> {
         EntrySetSpliterator(int pos, int maxPos) {
            super(pos, maxPos);
         }

         @Override
         public int characteristics() {
            return 16465;
         }

         protected final Byte2DoubleMap.Entry get(int location) {
            return new AbstractByte2DoubleMap.BasicEntry(Byte2DoubleArrayMap.this.key[location], Byte2DoubleArrayMap.this.value[location]);
         }

         protected final Byte2DoubleArrayMap.EntrySet.EntrySetSpliterator makeForSplit(int pos, int maxPos) {
            return EntrySet.this.new EntrySetSpliterator(pos, maxPos);
         }
      }
   }

   private final class KeySet extends AbstractByteSet {
      private KeySet() {
      }

      @Override
      public boolean contains(byte k) {
         return Byte2DoubleArrayMap.this.findKey(k) != -1;
      }

      @Override
      public boolean remove(byte k) {
         int oldPos = Byte2DoubleArrayMap.this.findKey(k);
         if (oldPos == -1) {
            return false;
         } else {
            int tail = Byte2DoubleArrayMap.this.size - oldPos - 1;
            System.arraycopy(Byte2DoubleArrayMap.this.key, oldPos + 1, Byte2DoubleArrayMap.this.key, oldPos, tail);
            System.arraycopy(Byte2DoubleArrayMap.this.value, oldPos + 1, Byte2DoubleArrayMap.this.value, oldPos, tail);
            Byte2DoubleArrayMap.this.size--;
            return true;
         }
      }

      @Override
      public ByteIterator iterator() {
         return new ByteIterator() {
            int pos = 0;

            @Override
            public boolean hasNext() {
               return this.pos < Byte2DoubleArrayMap.this.size;
            }

            @Override
            public byte nextByte() {
               if (!this.hasNext()) {
                  throw new NoSuchElementException();
               } else {
                  return Byte2DoubleArrayMap.this.key[this.pos++];
               }
            }

            @Override
            public void remove() {
               if (this.pos == 0) {
                  throw new IllegalStateException();
               } else {
                  int tail = Byte2DoubleArrayMap.this.size - this.pos;
                  System.arraycopy(Byte2DoubleArrayMap.this.key, this.pos, Byte2DoubleArrayMap.this.key, this.pos - 1, tail);
                  System.arraycopy(Byte2DoubleArrayMap.this.value, this.pos, Byte2DoubleArrayMap.this.value, this.pos - 1, tail);
                  Byte2DoubleArrayMap.this.size--;
                  this.pos--;
               }
            }

            @Override
            public void forEachRemaining(ByteConsumer action) {
               int max = Byte2DoubleArrayMap.this.size;

               while (this.pos < max) {
                  action.accept(Byte2DoubleArrayMap.this.key[this.pos++]);
               }
            }
         };
      }

      @Override
      public ByteSpliterator spliterator() {
         return new Byte2DoubleArrayMap.KeySet.KeySetSpliterator(0, Byte2DoubleArrayMap.this.size);
      }

      @Override
      public void forEach(ByteConsumer action) {
         int i = 0;

         for (int max = Byte2DoubleArrayMap.this.size; i < max; i++) {
            action.accept(Byte2DoubleArrayMap.this.key[i]);
         }
      }

      @Override
      public int size() {
         return Byte2DoubleArrayMap.this.size;
      }

      @Override
      public void clear() {
         Byte2DoubleArrayMap.this.clear();
      }

      final class KeySetSpliterator extends ByteSpliterators.EarlyBindingSizeIndexBasedSpliterator implements ByteSpliterator {
         KeySetSpliterator(int pos, int maxPos) {
            super(pos, maxPos);
         }

         @Override
         public int characteristics() {
            return 16721;
         }

         @Override
         protected final byte get(int location) {
            return Byte2DoubleArrayMap.this.key[location];
         }

         protected final Byte2DoubleArrayMap.KeySet.KeySetSpliterator makeForSplit(int pos, int maxPos) {
            return KeySet.this.new KeySetSpliterator(pos, maxPos);
         }

         @Override
         public void forEachRemaining(ByteConsumer action) {
            int max = Byte2DoubleArrayMap.this.size;

            while (this.pos < max) {
               action.accept(Byte2DoubleArrayMap.this.key[this.pos++]);
            }
         }
      }
   }

   private final class ValuesCollection extends AbstractDoubleCollection {
      private ValuesCollection() {
      }

      @Override
      public boolean contains(double v) {
         return Byte2DoubleArrayMap.this.containsValue(v);
      }

      @Override
      public DoubleIterator iterator() {
         return new DoubleIterator() {
            int pos = 0;

            @Override
            public boolean hasNext() {
               return this.pos < Byte2DoubleArrayMap.this.size;
            }

            @Override
            public double nextDouble() {
               if (!this.hasNext()) {
                  throw new NoSuchElementException();
               } else {
                  return Byte2DoubleArrayMap.this.value[this.pos++];
               }
            }

            @Override
            public void remove() {
               if (this.pos == 0) {
                  throw new IllegalStateException();
               } else {
                  int tail = Byte2DoubleArrayMap.this.size - this.pos;
                  System.arraycopy(Byte2DoubleArrayMap.this.key, this.pos, Byte2DoubleArrayMap.this.key, this.pos - 1, tail);
                  System.arraycopy(Byte2DoubleArrayMap.this.value, this.pos, Byte2DoubleArrayMap.this.value, this.pos - 1, tail);
                  Byte2DoubleArrayMap.this.size--;
                  this.pos--;
               }
            }

            @Override
            public void forEachRemaining(DoubleConsumer action) {
               int max = Byte2DoubleArrayMap.this.size;

               while (this.pos < max) {
                  action.accept(Byte2DoubleArrayMap.this.value[this.pos++]);
               }
            }
         };
      }

      @Override
      public DoubleSpliterator spliterator() {
         return new Byte2DoubleArrayMap.ValuesCollection.ValuesSpliterator(0, Byte2DoubleArrayMap.this.size);
      }

      @Override
      public void forEach(DoubleConsumer action) {
         int i = 0;

         for (int max = Byte2DoubleArrayMap.this.size; i < max; i++) {
            action.accept(Byte2DoubleArrayMap.this.value[i]);
         }
      }

      @Override
      public int size() {
         return Byte2DoubleArrayMap.this.size;
      }

      @Override
      public void clear() {
         Byte2DoubleArrayMap.this.clear();
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
            return Byte2DoubleArrayMap.this.value[location];
         }

         protected final Byte2DoubleArrayMap.ValuesCollection.ValuesSpliterator makeForSplit(int pos, int maxPos) {
            return ValuesCollection.this.new ValuesSpliterator(pos, maxPos);
         }

         @Override
         public void forEachRemaining(DoubleConsumer action) {
            int max = Byte2DoubleArrayMap.this.size;

            while (this.pos < max) {
               action.accept(Byte2DoubleArrayMap.this.value[this.pos++]);
            }
         }
      }
   }
}
