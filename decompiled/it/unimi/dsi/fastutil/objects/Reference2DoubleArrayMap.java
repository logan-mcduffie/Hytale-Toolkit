package it.unimi.dsi.fastutil.objects;

import it.unimi.dsi.fastutil.doubles.AbstractDoubleCollection;
import it.unimi.dsi.fastutil.doubles.DoubleArrays;
import it.unimi.dsi.fastutil.doubles.DoubleCollection;
import it.unimi.dsi.fastutil.doubles.DoubleIterator;
import it.unimi.dsi.fastutil.doubles.DoubleSpliterator;
import it.unimi.dsi.fastutil.doubles.DoubleSpliterators;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Map.Entry;
import java.util.function.Consumer;
import java.util.function.DoubleConsumer;

public class Reference2DoubleArrayMap<K> extends AbstractReference2DoubleMap<K> implements Serializable, Cloneable {
   private static final long serialVersionUID = 1L;
   protected transient Object[] key;
   protected transient double[] value;
   protected int size;
   protected transient Reference2DoubleMap.FastEntrySet<K> entries;
   protected transient ReferenceSet<K> keys;
   protected transient DoubleCollection values;

   public Reference2DoubleArrayMap(Object[] key, double[] value) {
      this.key = key;
      this.value = value;
      this.size = key.length;
      if (key.length != value.length) {
         throw new IllegalArgumentException("Keys and values have different lengths (" + key.length + ", " + value.length + ")");
      }
   }

   public Reference2DoubleArrayMap() {
      this.key = ObjectArrays.EMPTY_ARRAY;
      this.value = DoubleArrays.EMPTY_ARRAY;
   }

   public Reference2DoubleArrayMap(int capacity) {
      this.key = new Object[capacity];
      this.value = new double[capacity];
   }

   public Reference2DoubleArrayMap(Reference2DoubleMap<K> m) {
      this(m.size());
      int i = 0;

      for (Reference2DoubleMap.Entry<K> e : m.reference2DoubleEntrySet()) {
         this.key[i] = e.getKey();
         this.value[i] = e.getDoubleValue();
         i++;
      }

      this.size = i;
   }

   public Reference2DoubleArrayMap(Map<? extends K, ? extends Double> m) {
      this(m.size());
      int i = 0;

      for (Entry<? extends K, ? extends Double> e : m.entrySet()) {
         this.key[i] = e.getKey();
         this.value[i] = e.getValue();
         i++;
      }

      this.size = i;
   }

   public Reference2DoubleArrayMap(Object[] key, double[] value, int size) {
      this.key = key;
      this.value = value;
      this.size = size;
      if (key.length != value.length) {
         throw new IllegalArgumentException("Keys and values have different lengths (" + key.length + ", " + value.length + ")");
      } else if (size > key.length) {
         throw new IllegalArgumentException("The provided size (" + size + ") is larger than or equal to the backing-arrays size (" + key.length + ")");
      }
   }

   public Reference2DoubleMap.FastEntrySet<K> reference2DoubleEntrySet() {
      if (this.entries == null) {
         this.entries = new Reference2DoubleArrayMap.EntrySet();
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
   public double getDouble(Object k) {
      Object[] key = this.key;
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
   public double put(K k, double v) {
      int oldKey = this.findKey(k);
      if (oldKey != -1) {
         double oldValue = this.value[oldKey];
         this.value[oldKey] = v;
         return oldValue;
      } else {
         if (this.size == this.key.length) {
            Object[] newKey = new Object[this.size == 0 ? 2 : this.size * 2];
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
   public double removeDouble(Object k) {
      int oldPos = this.findKey(k);
      if (oldPos == -1) {
         return this.defRetValue;
      } else {
         double oldValue = this.value[oldPos];
         int tail = this.size - oldPos - 1;
         System.arraycopy(this.key, oldPos + 1, this.key, oldPos, tail);
         System.arraycopy(this.value, oldPos + 1, this.value, oldPos, tail);
         this.size--;
         this.key[this.size] = null;
         return oldValue;
      }
   }

   @Override
   public ReferenceSet<K> keySet() {
      if (this.keys == null) {
         this.keys = new Reference2DoubleArrayMap.KeySet();
      }

      return this.keys;
   }

   @Override
   public DoubleCollection values() {
      if (this.values == null) {
         this.values = new Reference2DoubleArrayMap.ValuesCollection();
      }

      return this.values;
   }

   public Reference2DoubleArrayMap<K> clone() {
      Reference2DoubleArrayMap<K> c;
      try {
         c = (Reference2DoubleArrayMap<K>)super.clone();
      } catch (CloneNotSupportedException var3) {
         throw new InternalError();
      }

      c.key = (Object[])this.key.clone();
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
         s.writeObject(this.key[i]);
         s.writeDouble(this.value[i]);
      }
   }

   private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
      s.defaultReadObject();
      this.key = new Object[this.size];
      this.value = new double[this.size];

      for (int i = 0; i < this.size; i++) {
         this.key[i] = s.readObject();
         this.value[i] = s.readDouble();
      }
   }

   private final class EntrySet extends AbstractObjectSet<Reference2DoubleMap.Entry<K>> implements Reference2DoubleMap.FastEntrySet<K> {
      private EntrySet() {
      }

      @Override
      public ObjectIterator<Reference2DoubleMap.Entry<K>> iterator() {
         return new ObjectIterator<Reference2DoubleMap.Entry<K>>() {
            int curr = -1;
            int next = 0;

            @Override
            public boolean hasNext() {
               return this.next < Reference2DoubleArrayMap.this.size;
            }

            public Reference2DoubleMap.Entry<K> next() {
               if (!this.hasNext()) {
                  throw new NoSuchElementException();
               } else {
                  return new AbstractReference2DoubleMap.BasicEntry<>(
                     (K)Reference2DoubleArrayMap.this.key[this.curr = this.next], Reference2DoubleArrayMap.this.value[this.next++]
                  );
               }
            }

            @Override
            public void remove() {
               if (this.curr == -1) {
                  throw new IllegalStateException();
               } else {
                  this.curr = -1;
                  int tail = Reference2DoubleArrayMap.this.size-- - this.next--;
                  System.arraycopy(Reference2DoubleArrayMap.this.key, this.next + 1, Reference2DoubleArrayMap.this.key, this.next, tail);
                  System.arraycopy(Reference2DoubleArrayMap.this.value, this.next + 1, Reference2DoubleArrayMap.this.value, this.next, tail);
                  Reference2DoubleArrayMap.this.key[Reference2DoubleArrayMap.this.size] = null;
               }
            }

            @Override
            public void forEachRemaining(Consumer<? super Reference2DoubleMap.Entry<K>> action) {
               int max = Reference2DoubleArrayMap.this.size;

               while (this.next < max) {
                  action.accept(
                     new AbstractReference2DoubleMap.BasicEntry<>(
                        (K)Reference2DoubleArrayMap.this.key[this.curr = this.next], Reference2DoubleArrayMap.this.value[this.next++]
                     )
                  );
               }
            }
         };
      }

      @Override
      public ObjectIterator<Reference2DoubleMap.Entry<K>> fastIterator() {
         return new ObjectIterator<Reference2DoubleMap.Entry<K>>() {
            int next = 0;
            int curr = -1;
            final AbstractReference2DoubleMap.BasicEntry<K> entry = new AbstractReference2DoubleMap.BasicEntry<>();

            @Override
            public boolean hasNext() {
               return this.next < Reference2DoubleArrayMap.this.size;
            }

            public Reference2DoubleMap.Entry<K> next() {
               if (!this.hasNext()) {
                  throw new NoSuchElementException();
               } else {
                  this.entry.key = (K)Reference2DoubleArrayMap.this.key[this.curr = this.next];
                  this.entry.value = Reference2DoubleArrayMap.this.value[this.next++];
                  return this.entry;
               }
            }

            @Override
            public void remove() {
               if (this.curr == -1) {
                  throw new IllegalStateException();
               } else {
                  this.curr = -1;
                  int tail = Reference2DoubleArrayMap.this.size-- - this.next--;
                  System.arraycopy(Reference2DoubleArrayMap.this.key, this.next + 1, Reference2DoubleArrayMap.this.key, this.next, tail);
                  System.arraycopy(Reference2DoubleArrayMap.this.value, this.next + 1, Reference2DoubleArrayMap.this.value, this.next, tail);
                  Reference2DoubleArrayMap.this.key[Reference2DoubleArrayMap.this.size] = null;
               }
            }

            @Override
            public void forEachRemaining(Consumer<? super Reference2DoubleMap.Entry<K>> action) {
               int max = Reference2DoubleArrayMap.this.size;

               while (this.next < max) {
                  this.entry.key = (K)Reference2DoubleArrayMap.this.key[this.curr = this.next];
                  this.entry.value = Reference2DoubleArrayMap.this.value[this.next++];
                  action.accept(this.entry);
               }
            }
         };
      }

      @Override
      public ObjectSpliterator<Reference2DoubleMap.Entry<K>> spliterator() {
         return new Reference2DoubleArrayMap.EntrySet.EntrySetSpliterator(0, Reference2DoubleArrayMap.this.size);
      }

      @Override
      public void forEach(Consumer<? super Reference2DoubleMap.Entry<K>> action) {
         int i = 0;

         for (int max = Reference2DoubleArrayMap.this.size; i < max; i++) {
            action.accept(new AbstractReference2DoubleMap.BasicEntry<>((K)Reference2DoubleArrayMap.this.key[i], Reference2DoubleArrayMap.this.value[i]));
         }
      }

      @Override
      public void fastForEach(Consumer<? super Reference2DoubleMap.Entry<K>> action) {
         AbstractReference2DoubleMap.BasicEntry<K> entry = new AbstractReference2DoubleMap.BasicEntry<>();
         int i = 0;

         for (int max = Reference2DoubleArrayMap.this.size; i < max; i++) {
            entry.key = (K)Reference2DoubleArrayMap.this.key[i];
            entry.value = Reference2DoubleArrayMap.this.value[i];
            action.accept(entry);
         }
      }

      @Override
      public int size() {
         return Reference2DoubleArrayMap.this.size;
      }

      @Override
      public boolean contains(Object o) {
         if (!(o instanceof Entry)) {
            return false;
         } else {
            Entry<?, ?> e = (Entry<?, ?>)o;
            if (e.getValue() != null && e.getValue() instanceof Double) {
               K k = (K)e.getKey();
               return Reference2DoubleArrayMap.this.containsKey(k)
                  && Double.doubleToLongBits(Reference2DoubleArrayMap.this.getDouble(k)) == Double.doubleToLongBits((Double)e.getValue());
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
            if (e.getValue() != null && e.getValue() instanceof Double) {
               K k = (K)e.getKey();
               double v = (Double)e.getValue();
               int oldPos = Reference2DoubleArrayMap.this.findKey(k);
               if (oldPos != -1 && Double.doubleToLongBits(v) == Double.doubleToLongBits(Reference2DoubleArrayMap.this.value[oldPos])) {
                  int tail = Reference2DoubleArrayMap.this.size - oldPos - 1;
                  System.arraycopy(Reference2DoubleArrayMap.this.key, oldPos + 1, Reference2DoubleArrayMap.this.key, oldPos, tail);
                  System.arraycopy(Reference2DoubleArrayMap.this.value, oldPos + 1, Reference2DoubleArrayMap.this.value, oldPos, tail);
                  Reference2DoubleArrayMap.this.size--;
                  Reference2DoubleArrayMap.this.key[Reference2DoubleArrayMap.this.size] = null;
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
         extends ObjectSpliterators.EarlyBindingSizeIndexBasedSpliterator<Reference2DoubleMap.Entry<K>>
         implements ObjectSpliterator<Reference2DoubleMap.Entry<K>> {
         EntrySetSpliterator(int pos, int maxPos) {
            super(pos, maxPos);
         }

         @Override
         public int characteristics() {
            return 16465;
         }

         protected final Reference2DoubleMap.Entry<K> get(int location) {
            return new AbstractReference2DoubleMap.BasicEntry<>((K)Reference2DoubleArrayMap.this.key[location], Reference2DoubleArrayMap.this.value[location]);
         }

         protected final Reference2DoubleArrayMap<K>.EntrySet.EntrySetSpliterator makeForSplit(int pos, int maxPos) {
            return EntrySet.this.new EntrySetSpliterator(pos, maxPos);
         }
      }
   }

   private final class KeySet extends AbstractReferenceSet<K> {
      private KeySet() {
      }

      @Override
      public boolean contains(Object k) {
         return Reference2DoubleArrayMap.this.findKey(k) != -1;
      }

      @Override
      public boolean remove(Object k) {
         int oldPos = Reference2DoubleArrayMap.this.findKey(k);
         if (oldPos == -1) {
            return false;
         } else {
            int tail = Reference2DoubleArrayMap.this.size - oldPos - 1;
            System.arraycopy(Reference2DoubleArrayMap.this.key, oldPos + 1, Reference2DoubleArrayMap.this.key, oldPos, tail);
            System.arraycopy(Reference2DoubleArrayMap.this.value, oldPos + 1, Reference2DoubleArrayMap.this.value, oldPos, tail);
            Reference2DoubleArrayMap.this.size--;
            Reference2DoubleArrayMap.this.key[Reference2DoubleArrayMap.this.size] = null;
            return true;
         }
      }

      @Override
      public ObjectIterator<K> iterator() {
         return new ObjectIterator<K>() {
            int pos = 0;

            @Override
            public boolean hasNext() {
               return this.pos < Reference2DoubleArrayMap.this.size;
            }

            @Override
            public K next() {
               if (!this.hasNext()) {
                  throw new NoSuchElementException();
               } else {
                  return (K)Reference2DoubleArrayMap.this.key[this.pos++];
               }
            }

            @Override
            public void remove() {
               if (this.pos == 0) {
                  throw new IllegalStateException();
               } else {
                  int tail = Reference2DoubleArrayMap.this.size - this.pos;
                  System.arraycopy(Reference2DoubleArrayMap.this.key, this.pos, Reference2DoubleArrayMap.this.key, this.pos - 1, tail);
                  System.arraycopy(Reference2DoubleArrayMap.this.value, this.pos, Reference2DoubleArrayMap.this.value, this.pos - 1, tail);
                  Reference2DoubleArrayMap.this.size--;
                  this.pos--;
                  Reference2DoubleArrayMap.this.key[Reference2DoubleArrayMap.this.size] = null;
               }
            }

            @Override
            public void forEachRemaining(Consumer<? super K> action) {
               int max = Reference2DoubleArrayMap.this.size;

               while (this.pos < max) {
                  action.accept((K)Reference2DoubleArrayMap.this.key[this.pos++]);
               }
            }
         };
      }

      @Override
      public ObjectSpliterator<K> spliterator() {
         return new Reference2DoubleArrayMap.KeySet.KeySetSpliterator(0, Reference2DoubleArrayMap.this.size);
      }

      @Override
      public void forEach(Consumer<? super K> action) {
         int i = 0;

         for (int max = Reference2DoubleArrayMap.this.size; i < max; i++) {
            action.accept((K)Reference2DoubleArrayMap.this.key[i]);
         }
      }

      @Override
      public int size() {
         return Reference2DoubleArrayMap.this.size;
      }

      @Override
      public void clear() {
         Reference2DoubleArrayMap.this.clear();
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
            return (K)Reference2DoubleArrayMap.this.key[location];
         }

         protected final Reference2DoubleArrayMap<K>.KeySet.KeySetSpliterator makeForSplit(int pos, int maxPos) {
            return KeySet.this.new KeySetSpliterator(pos, maxPos);
         }

         @Override
         public void forEachRemaining(Consumer<? super K> action) {
            int max = Reference2DoubleArrayMap.this.size;

            while (this.pos < max) {
               action.accept((K)Reference2DoubleArrayMap.this.key[this.pos++]);
            }
         }
      }
   }

   private final class ValuesCollection extends AbstractDoubleCollection {
      private ValuesCollection() {
      }

      @Override
      public boolean contains(double v) {
         return Reference2DoubleArrayMap.this.containsValue(v);
      }

      @Override
      public DoubleIterator iterator() {
         return new DoubleIterator() {
            int pos = 0;

            @Override
            public boolean hasNext() {
               return this.pos < Reference2DoubleArrayMap.this.size;
            }

            @Override
            public double nextDouble() {
               if (!this.hasNext()) {
                  throw new NoSuchElementException();
               } else {
                  return Reference2DoubleArrayMap.this.value[this.pos++];
               }
            }

            @Override
            public void remove() {
               if (this.pos == 0) {
                  throw new IllegalStateException();
               } else {
                  int tail = Reference2DoubleArrayMap.this.size - this.pos;
                  System.arraycopy(Reference2DoubleArrayMap.this.key, this.pos, Reference2DoubleArrayMap.this.key, this.pos - 1, tail);
                  System.arraycopy(Reference2DoubleArrayMap.this.value, this.pos, Reference2DoubleArrayMap.this.value, this.pos - 1, tail);
                  Reference2DoubleArrayMap.this.size--;
                  this.pos--;
                  Reference2DoubleArrayMap.this.key[Reference2DoubleArrayMap.this.size] = null;
               }
            }

            @Override
            public void forEachRemaining(DoubleConsumer action) {
               int max = Reference2DoubleArrayMap.this.size;

               while (this.pos < max) {
                  action.accept(Reference2DoubleArrayMap.this.value[this.pos++]);
               }
            }
         };
      }

      @Override
      public DoubleSpliterator spliterator() {
         return new Reference2DoubleArrayMap.ValuesCollection.ValuesSpliterator(0, Reference2DoubleArrayMap.this.size);
      }

      @Override
      public void forEach(DoubleConsumer action) {
         int i = 0;

         for (int max = Reference2DoubleArrayMap.this.size; i < max; i++) {
            action.accept(Reference2DoubleArrayMap.this.value[i]);
         }
      }

      @Override
      public int size() {
         return Reference2DoubleArrayMap.this.size;
      }

      @Override
      public void clear() {
         Reference2DoubleArrayMap.this.clear();
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
            return Reference2DoubleArrayMap.this.value[location];
         }

         protected final Reference2DoubleArrayMap<K>.ValuesCollection.ValuesSpliterator makeForSplit(int pos, int maxPos) {
            return ValuesCollection.this.new ValuesSpliterator(pos, maxPos);
         }

         @Override
         public void forEachRemaining(DoubleConsumer action) {
            int max = Reference2DoubleArrayMap.this.size;

            while (this.pos < max) {
               action.accept(Reference2DoubleArrayMap.this.value[this.pos++]);
            }
         }
      }
   }
}
