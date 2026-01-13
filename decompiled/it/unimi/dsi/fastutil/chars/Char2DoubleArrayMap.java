package it.unimi.dsi.fastutil.chars;

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

public class Char2DoubleArrayMap extends AbstractChar2DoubleMap implements Serializable, Cloneable {
   private static final long serialVersionUID = 1L;
   protected transient char[] key;
   protected transient double[] value;
   protected int size;
   protected transient Char2DoubleMap.FastEntrySet entries;
   protected transient CharSet keys;
   protected transient DoubleCollection values;

   public Char2DoubleArrayMap(char[] key, double[] value) {
      this.key = key;
      this.value = value;
      this.size = key.length;
      if (key.length != value.length) {
         throw new IllegalArgumentException("Keys and values have different lengths (" + key.length + ", " + value.length + ")");
      }
   }

   public Char2DoubleArrayMap() {
      this.key = CharArrays.EMPTY_ARRAY;
      this.value = DoubleArrays.EMPTY_ARRAY;
   }

   public Char2DoubleArrayMap(int capacity) {
      this.key = new char[capacity];
      this.value = new double[capacity];
   }

   public Char2DoubleArrayMap(Char2DoubleMap m) {
      this(m.size());
      int i = 0;

      for (Char2DoubleMap.Entry e : m.char2DoubleEntrySet()) {
         this.key[i] = e.getCharKey();
         this.value[i] = e.getDoubleValue();
         i++;
      }

      this.size = i;
   }

   public Char2DoubleArrayMap(Map<? extends Character, ? extends Double> m) {
      this(m.size());
      int i = 0;

      for (Entry<? extends Character, ? extends Double> e : m.entrySet()) {
         this.key[i] = e.getKey();
         this.value[i] = e.getValue();
         i++;
      }

      this.size = i;
   }

   public Char2DoubleArrayMap(char[] key, double[] value, int size) {
      this.key = key;
      this.value = value;
      this.size = size;
      if (key.length != value.length) {
         throw new IllegalArgumentException("Keys and values have different lengths (" + key.length + ", " + value.length + ")");
      } else if (size > key.length) {
         throw new IllegalArgumentException("The provided size (" + size + ") is larger than or equal to the backing-arrays size (" + key.length + ")");
      }
   }

   public Char2DoubleMap.FastEntrySet char2DoubleEntrySet() {
      if (this.entries == null) {
         this.entries = new Char2DoubleArrayMap.EntrySet();
      }

      return this.entries;
   }

   private int findKey(char k) {
      char[] key = this.key;
      int i = this.size;

      while (i-- != 0) {
         if (key[i] == k) {
            return i;
         }
      }

      return -1;
   }

   @Override
   public double get(char k) {
      char[] key = this.key;
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
   public boolean containsKey(char k) {
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
   public double put(char k, double v) {
      int oldKey = this.findKey(k);
      if (oldKey != -1) {
         double oldValue = this.value[oldKey];
         this.value[oldKey] = v;
         return oldValue;
      } else {
         if (this.size == this.key.length) {
            char[] newKey = new char[this.size == 0 ? 2 : this.size * 2];
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
   public double remove(char k) {
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
   public CharSet keySet() {
      if (this.keys == null) {
         this.keys = new Char2DoubleArrayMap.KeySet();
      }

      return this.keys;
   }

   @Override
   public DoubleCollection values() {
      if (this.values == null) {
         this.values = new Char2DoubleArrayMap.ValuesCollection();
      }

      return this.values;
   }

   public Char2DoubleArrayMap clone() {
      Char2DoubleArrayMap c;
      try {
         c = (Char2DoubleArrayMap)super.clone();
      } catch (CloneNotSupportedException var3) {
         throw new InternalError();
      }

      c.key = (char[])this.key.clone();
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
         s.writeChar(this.key[i]);
         s.writeDouble(this.value[i]);
      }
   }

   private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
      s.defaultReadObject();
      this.key = new char[this.size];
      this.value = new double[this.size];

      for (int i = 0; i < this.size; i++) {
         this.key[i] = s.readChar();
         this.value[i] = s.readDouble();
      }
   }

   private final class EntrySet extends AbstractObjectSet<Char2DoubleMap.Entry> implements Char2DoubleMap.FastEntrySet {
      private EntrySet() {
      }

      @Override
      public ObjectIterator<Char2DoubleMap.Entry> iterator() {
         return new ObjectIterator<Char2DoubleMap.Entry>() {
            int curr = -1;
            int next = 0;

            @Override
            public boolean hasNext() {
               return this.next < Char2DoubleArrayMap.this.size;
            }

            public Char2DoubleMap.Entry next() {
               if (!this.hasNext()) {
                  throw new NoSuchElementException();
               } else {
                  return new AbstractChar2DoubleMap.BasicEntry(Char2DoubleArrayMap.this.key[this.curr = this.next], Char2DoubleArrayMap.this.value[this.next++]);
               }
            }

            @Override
            public void remove() {
               if (this.curr == -1) {
                  throw new IllegalStateException();
               } else {
                  this.curr = -1;
                  int tail = Char2DoubleArrayMap.this.size-- - this.next--;
                  System.arraycopy(Char2DoubleArrayMap.this.key, this.next + 1, Char2DoubleArrayMap.this.key, this.next, tail);
                  System.arraycopy(Char2DoubleArrayMap.this.value, this.next + 1, Char2DoubleArrayMap.this.value, this.next, tail);
               }
            }

            @Override
            public void forEachRemaining(Consumer<? super Char2DoubleMap.Entry> action) {
               int max = Char2DoubleArrayMap.this.size;

               while (this.next < max) {
                  action.accept(
                     new AbstractChar2DoubleMap.BasicEntry(Char2DoubleArrayMap.this.key[this.curr = this.next], Char2DoubleArrayMap.this.value[this.next++])
                  );
               }
            }
         };
      }

      @Override
      public ObjectIterator<Char2DoubleMap.Entry> fastIterator() {
         return new ObjectIterator<Char2DoubleMap.Entry>() {
            int next = 0;
            int curr = -1;
            final AbstractChar2DoubleMap.BasicEntry entry = new AbstractChar2DoubleMap.BasicEntry();

            @Override
            public boolean hasNext() {
               return this.next < Char2DoubleArrayMap.this.size;
            }

            public Char2DoubleMap.Entry next() {
               if (!this.hasNext()) {
                  throw new NoSuchElementException();
               } else {
                  this.entry.key = Char2DoubleArrayMap.this.key[this.curr = this.next];
                  this.entry.value = Char2DoubleArrayMap.this.value[this.next++];
                  return this.entry;
               }
            }

            @Override
            public void remove() {
               if (this.curr == -1) {
                  throw new IllegalStateException();
               } else {
                  this.curr = -1;
                  int tail = Char2DoubleArrayMap.this.size-- - this.next--;
                  System.arraycopy(Char2DoubleArrayMap.this.key, this.next + 1, Char2DoubleArrayMap.this.key, this.next, tail);
                  System.arraycopy(Char2DoubleArrayMap.this.value, this.next + 1, Char2DoubleArrayMap.this.value, this.next, tail);
               }
            }

            @Override
            public void forEachRemaining(Consumer<? super Char2DoubleMap.Entry> action) {
               int max = Char2DoubleArrayMap.this.size;

               while (this.next < max) {
                  this.entry.key = Char2DoubleArrayMap.this.key[this.curr = this.next];
                  this.entry.value = Char2DoubleArrayMap.this.value[this.next++];
                  action.accept(this.entry);
               }
            }
         };
      }

      @Override
      public ObjectSpliterator<Char2DoubleMap.Entry> spliterator() {
         return new Char2DoubleArrayMap.EntrySet.EntrySetSpliterator(0, Char2DoubleArrayMap.this.size);
      }

      @Override
      public void forEach(Consumer<? super Char2DoubleMap.Entry> action) {
         int i = 0;

         for (int max = Char2DoubleArrayMap.this.size; i < max; i++) {
            action.accept(new AbstractChar2DoubleMap.BasicEntry(Char2DoubleArrayMap.this.key[i], Char2DoubleArrayMap.this.value[i]));
         }
      }

      @Override
      public void fastForEach(Consumer<? super Char2DoubleMap.Entry> action) {
         AbstractChar2DoubleMap.BasicEntry entry = new AbstractChar2DoubleMap.BasicEntry();
         int i = 0;

         for (int max = Char2DoubleArrayMap.this.size; i < max; i++) {
            entry.key = Char2DoubleArrayMap.this.key[i];
            entry.value = Char2DoubleArrayMap.this.value[i];
            action.accept(entry);
         }
      }

      @Override
      public int size() {
         return Char2DoubleArrayMap.this.size;
      }

      @Override
      public boolean contains(Object o) {
         if (!(o instanceof Entry)) {
            return false;
         } else {
            Entry<?, ?> e = (Entry<?, ?>)o;
            if (e.getKey() == null || !(e.getKey() instanceof Character)) {
               return false;
            } else if (e.getValue() != null && e.getValue() instanceof Double) {
               char k = (Character)e.getKey();
               return Char2DoubleArrayMap.this.containsKey(k)
                  && Double.doubleToLongBits(Char2DoubleArrayMap.this.get(k)) == Double.doubleToLongBits((Double)e.getValue());
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
            if (e.getKey() == null || !(e.getKey() instanceof Character)) {
               return false;
            } else if (e.getValue() != null && e.getValue() instanceof Double) {
               char k = (Character)e.getKey();
               double v = (Double)e.getValue();
               int oldPos = Char2DoubleArrayMap.this.findKey(k);
               if (oldPos != -1 && Double.doubleToLongBits(v) == Double.doubleToLongBits(Char2DoubleArrayMap.this.value[oldPos])) {
                  int tail = Char2DoubleArrayMap.this.size - oldPos - 1;
                  System.arraycopy(Char2DoubleArrayMap.this.key, oldPos + 1, Char2DoubleArrayMap.this.key, oldPos, tail);
                  System.arraycopy(Char2DoubleArrayMap.this.value, oldPos + 1, Char2DoubleArrayMap.this.value, oldPos, tail);
                  Char2DoubleArrayMap.this.size--;
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
         extends ObjectSpliterators.EarlyBindingSizeIndexBasedSpliterator<Char2DoubleMap.Entry>
         implements ObjectSpliterator<Char2DoubleMap.Entry> {
         EntrySetSpliterator(int pos, int maxPos) {
            super(pos, maxPos);
         }

         @Override
         public int characteristics() {
            return 16465;
         }

         protected final Char2DoubleMap.Entry get(int location) {
            return new AbstractChar2DoubleMap.BasicEntry(Char2DoubleArrayMap.this.key[location], Char2DoubleArrayMap.this.value[location]);
         }

         protected final Char2DoubleArrayMap.EntrySet.EntrySetSpliterator makeForSplit(int pos, int maxPos) {
            return EntrySet.this.new EntrySetSpliterator(pos, maxPos);
         }
      }
   }

   private final class KeySet extends AbstractCharSet {
      private KeySet() {
      }

      @Override
      public boolean contains(char k) {
         return Char2DoubleArrayMap.this.findKey(k) != -1;
      }

      @Override
      public boolean remove(char k) {
         int oldPos = Char2DoubleArrayMap.this.findKey(k);
         if (oldPos == -1) {
            return false;
         } else {
            int tail = Char2DoubleArrayMap.this.size - oldPos - 1;
            System.arraycopy(Char2DoubleArrayMap.this.key, oldPos + 1, Char2DoubleArrayMap.this.key, oldPos, tail);
            System.arraycopy(Char2DoubleArrayMap.this.value, oldPos + 1, Char2DoubleArrayMap.this.value, oldPos, tail);
            Char2DoubleArrayMap.this.size--;
            return true;
         }
      }

      @Override
      public CharIterator iterator() {
         return new CharIterator() {
            int pos = 0;

            @Override
            public boolean hasNext() {
               return this.pos < Char2DoubleArrayMap.this.size;
            }

            @Override
            public char nextChar() {
               if (!this.hasNext()) {
                  throw new NoSuchElementException();
               } else {
                  return Char2DoubleArrayMap.this.key[this.pos++];
               }
            }

            @Override
            public void remove() {
               if (this.pos == 0) {
                  throw new IllegalStateException();
               } else {
                  int tail = Char2DoubleArrayMap.this.size - this.pos;
                  System.arraycopy(Char2DoubleArrayMap.this.key, this.pos, Char2DoubleArrayMap.this.key, this.pos - 1, tail);
                  System.arraycopy(Char2DoubleArrayMap.this.value, this.pos, Char2DoubleArrayMap.this.value, this.pos - 1, tail);
                  Char2DoubleArrayMap.this.size--;
                  this.pos--;
               }
            }

            @Override
            public void forEachRemaining(CharConsumer action) {
               int max = Char2DoubleArrayMap.this.size;

               while (this.pos < max) {
                  action.accept(Char2DoubleArrayMap.this.key[this.pos++]);
               }
            }
         };
      }

      @Override
      public CharSpliterator spliterator() {
         return new Char2DoubleArrayMap.KeySet.KeySetSpliterator(0, Char2DoubleArrayMap.this.size);
      }

      @Override
      public void forEach(CharConsumer action) {
         int i = 0;

         for (int max = Char2DoubleArrayMap.this.size; i < max; i++) {
            action.accept(Char2DoubleArrayMap.this.key[i]);
         }
      }

      @Override
      public int size() {
         return Char2DoubleArrayMap.this.size;
      }

      @Override
      public void clear() {
         Char2DoubleArrayMap.this.clear();
      }

      final class KeySetSpliterator extends CharSpliterators.EarlyBindingSizeIndexBasedSpliterator implements CharSpliterator {
         KeySetSpliterator(int pos, int maxPos) {
            super(pos, maxPos);
         }

         @Override
         public int characteristics() {
            return 16721;
         }

         @Override
         protected final char get(int location) {
            return Char2DoubleArrayMap.this.key[location];
         }

         protected final Char2DoubleArrayMap.KeySet.KeySetSpliterator makeForSplit(int pos, int maxPos) {
            return KeySet.this.new KeySetSpliterator(pos, maxPos);
         }

         @Override
         public void forEachRemaining(CharConsumer action) {
            int max = Char2DoubleArrayMap.this.size;

            while (this.pos < max) {
               action.accept(Char2DoubleArrayMap.this.key[this.pos++]);
            }
         }
      }
   }

   private final class ValuesCollection extends AbstractDoubleCollection {
      private ValuesCollection() {
      }

      @Override
      public boolean contains(double v) {
         return Char2DoubleArrayMap.this.containsValue(v);
      }

      @Override
      public DoubleIterator iterator() {
         return new DoubleIterator() {
            int pos = 0;

            @Override
            public boolean hasNext() {
               return this.pos < Char2DoubleArrayMap.this.size;
            }

            @Override
            public double nextDouble() {
               if (!this.hasNext()) {
                  throw new NoSuchElementException();
               } else {
                  return Char2DoubleArrayMap.this.value[this.pos++];
               }
            }

            @Override
            public void remove() {
               if (this.pos == 0) {
                  throw new IllegalStateException();
               } else {
                  int tail = Char2DoubleArrayMap.this.size - this.pos;
                  System.arraycopy(Char2DoubleArrayMap.this.key, this.pos, Char2DoubleArrayMap.this.key, this.pos - 1, tail);
                  System.arraycopy(Char2DoubleArrayMap.this.value, this.pos, Char2DoubleArrayMap.this.value, this.pos - 1, tail);
                  Char2DoubleArrayMap.this.size--;
                  this.pos--;
               }
            }

            @Override
            public void forEachRemaining(DoubleConsumer action) {
               int max = Char2DoubleArrayMap.this.size;

               while (this.pos < max) {
                  action.accept(Char2DoubleArrayMap.this.value[this.pos++]);
               }
            }
         };
      }

      @Override
      public DoubleSpliterator spliterator() {
         return new Char2DoubleArrayMap.ValuesCollection.ValuesSpliterator(0, Char2DoubleArrayMap.this.size);
      }

      @Override
      public void forEach(DoubleConsumer action) {
         int i = 0;

         for (int max = Char2DoubleArrayMap.this.size; i < max; i++) {
            action.accept(Char2DoubleArrayMap.this.value[i]);
         }
      }

      @Override
      public int size() {
         return Char2DoubleArrayMap.this.size;
      }

      @Override
      public void clear() {
         Char2DoubleArrayMap.this.clear();
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
            return Char2DoubleArrayMap.this.value[location];
         }

         protected final Char2DoubleArrayMap.ValuesCollection.ValuesSpliterator makeForSplit(int pos, int maxPos) {
            return ValuesCollection.this.new ValuesSpliterator(pos, maxPos);
         }

         @Override
         public void forEachRemaining(DoubleConsumer action) {
            int max = Char2DoubleArrayMap.this.size;

            while (this.pos < max) {
               action.accept(Char2DoubleArrayMap.this.value[this.pos++]);
            }
         }
      }
   }
}
