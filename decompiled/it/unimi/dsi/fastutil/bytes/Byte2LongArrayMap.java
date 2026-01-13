package it.unimi.dsi.fastutil.bytes;

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

public class Byte2LongArrayMap extends AbstractByte2LongMap implements Serializable, Cloneable {
   private static final long serialVersionUID = 1L;
   protected transient byte[] key;
   protected transient long[] value;
   protected int size;
   protected transient Byte2LongMap.FastEntrySet entries;
   protected transient ByteSet keys;
   protected transient LongCollection values;

   public Byte2LongArrayMap(byte[] key, long[] value) {
      this.key = key;
      this.value = value;
      this.size = key.length;
      if (key.length != value.length) {
         throw new IllegalArgumentException("Keys and values have different lengths (" + key.length + ", " + value.length + ")");
      }
   }

   public Byte2LongArrayMap() {
      this.key = ByteArrays.EMPTY_ARRAY;
      this.value = LongArrays.EMPTY_ARRAY;
   }

   public Byte2LongArrayMap(int capacity) {
      this.key = new byte[capacity];
      this.value = new long[capacity];
   }

   public Byte2LongArrayMap(Byte2LongMap m) {
      this(m.size());
      int i = 0;

      for (Byte2LongMap.Entry e : m.byte2LongEntrySet()) {
         this.key[i] = e.getByteKey();
         this.value[i] = e.getLongValue();
         i++;
      }

      this.size = i;
   }

   public Byte2LongArrayMap(Map<? extends Byte, ? extends Long> m) {
      this(m.size());
      int i = 0;

      for (Entry<? extends Byte, ? extends Long> e : m.entrySet()) {
         this.key[i] = e.getKey();
         this.value[i] = e.getValue();
         i++;
      }

      this.size = i;
   }

   public Byte2LongArrayMap(byte[] key, long[] value, int size) {
      this.key = key;
      this.value = value;
      this.size = size;
      if (key.length != value.length) {
         throw new IllegalArgumentException("Keys and values have different lengths (" + key.length + ", " + value.length + ")");
      } else if (size > key.length) {
         throw new IllegalArgumentException("The provided size (" + size + ") is larger than or equal to the backing-arrays size (" + key.length + ")");
      }
   }

   public Byte2LongMap.FastEntrySet byte2LongEntrySet() {
      if (this.entries == null) {
         this.entries = new Byte2LongArrayMap.EntrySet();
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
   public long get(byte k) {
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
   public long put(byte k, long v) {
      int oldKey = this.findKey(k);
      if (oldKey != -1) {
         long oldValue = this.value[oldKey];
         this.value[oldKey] = v;
         return oldValue;
      } else {
         if (this.size == this.key.length) {
            byte[] newKey = new byte[this.size == 0 ? 2 : this.size * 2];
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
   public long remove(byte k) {
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
   public ByteSet keySet() {
      if (this.keys == null) {
         this.keys = new Byte2LongArrayMap.KeySet();
      }

      return this.keys;
   }

   @Override
   public LongCollection values() {
      if (this.values == null) {
         this.values = new Byte2LongArrayMap.ValuesCollection();
      }

      return this.values;
   }

   public Byte2LongArrayMap clone() {
      Byte2LongArrayMap c;
      try {
         c = (Byte2LongArrayMap)super.clone();
      } catch (CloneNotSupportedException var3) {
         throw new InternalError();
      }

      c.key = (byte[])this.key.clone();
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
         s.writeByte(this.key[i]);
         s.writeLong(this.value[i]);
      }
   }

   private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
      s.defaultReadObject();
      this.key = new byte[this.size];
      this.value = new long[this.size];

      for (int i = 0; i < this.size; i++) {
         this.key[i] = s.readByte();
         this.value[i] = s.readLong();
      }
   }

   private final class EntrySet extends AbstractObjectSet<Byte2LongMap.Entry> implements Byte2LongMap.FastEntrySet {
      private EntrySet() {
      }

      @Override
      public ObjectIterator<Byte2LongMap.Entry> iterator() {
         return new ObjectIterator<Byte2LongMap.Entry>() {
            int curr = -1;
            int next = 0;

            @Override
            public boolean hasNext() {
               return this.next < Byte2LongArrayMap.this.size;
            }

            public Byte2LongMap.Entry next() {
               if (!this.hasNext()) {
                  throw new NoSuchElementException();
               } else {
                  return new AbstractByte2LongMap.BasicEntry(Byte2LongArrayMap.this.key[this.curr = this.next], Byte2LongArrayMap.this.value[this.next++]);
               }
            }

            @Override
            public void remove() {
               if (this.curr == -1) {
                  throw new IllegalStateException();
               } else {
                  this.curr = -1;
                  int tail = Byte2LongArrayMap.this.size-- - this.next--;
                  System.arraycopy(Byte2LongArrayMap.this.key, this.next + 1, Byte2LongArrayMap.this.key, this.next, tail);
                  System.arraycopy(Byte2LongArrayMap.this.value, this.next + 1, Byte2LongArrayMap.this.value, this.next, tail);
               }
            }

            @Override
            public void forEachRemaining(Consumer<? super Byte2LongMap.Entry> action) {
               int max = Byte2LongArrayMap.this.size;

               while (this.next < max) {
                  action.accept(
                     new AbstractByte2LongMap.BasicEntry(Byte2LongArrayMap.this.key[this.curr = this.next], Byte2LongArrayMap.this.value[this.next++])
                  );
               }
            }
         };
      }

      @Override
      public ObjectIterator<Byte2LongMap.Entry> fastIterator() {
         return new ObjectIterator<Byte2LongMap.Entry>() {
            int next = 0;
            int curr = -1;
            final AbstractByte2LongMap.BasicEntry entry = new AbstractByte2LongMap.BasicEntry();

            @Override
            public boolean hasNext() {
               return this.next < Byte2LongArrayMap.this.size;
            }

            public Byte2LongMap.Entry next() {
               if (!this.hasNext()) {
                  throw new NoSuchElementException();
               } else {
                  this.entry.key = Byte2LongArrayMap.this.key[this.curr = this.next];
                  this.entry.value = Byte2LongArrayMap.this.value[this.next++];
                  return this.entry;
               }
            }

            @Override
            public void remove() {
               if (this.curr == -1) {
                  throw new IllegalStateException();
               } else {
                  this.curr = -1;
                  int tail = Byte2LongArrayMap.this.size-- - this.next--;
                  System.arraycopy(Byte2LongArrayMap.this.key, this.next + 1, Byte2LongArrayMap.this.key, this.next, tail);
                  System.arraycopy(Byte2LongArrayMap.this.value, this.next + 1, Byte2LongArrayMap.this.value, this.next, tail);
               }
            }

            @Override
            public void forEachRemaining(Consumer<? super Byte2LongMap.Entry> action) {
               int max = Byte2LongArrayMap.this.size;

               while (this.next < max) {
                  this.entry.key = Byte2LongArrayMap.this.key[this.curr = this.next];
                  this.entry.value = Byte2LongArrayMap.this.value[this.next++];
                  action.accept(this.entry);
               }
            }
         };
      }

      @Override
      public ObjectSpliterator<Byte2LongMap.Entry> spliterator() {
         return new Byte2LongArrayMap.EntrySet.EntrySetSpliterator(0, Byte2LongArrayMap.this.size);
      }

      @Override
      public void forEach(Consumer<? super Byte2LongMap.Entry> action) {
         int i = 0;

         for (int max = Byte2LongArrayMap.this.size; i < max; i++) {
            action.accept(new AbstractByte2LongMap.BasicEntry(Byte2LongArrayMap.this.key[i], Byte2LongArrayMap.this.value[i]));
         }
      }

      @Override
      public void fastForEach(Consumer<? super Byte2LongMap.Entry> action) {
         AbstractByte2LongMap.BasicEntry entry = new AbstractByte2LongMap.BasicEntry();
         int i = 0;

         for (int max = Byte2LongArrayMap.this.size; i < max; i++) {
            entry.key = Byte2LongArrayMap.this.key[i];
            entry.value = Byte2LongArrayMap.this.value[i];
            action.accept(entry);
         }
      }

      @Override
      public int size() {
         return Byte2LongArrayMap.this.size;
      }

      @Override
      public boolean contains(Object o) {
         if (!(o instanceof Entry)) {
            return false;
         } else {
            Entry<?, ?> e = (Entry<?, ?>)o;
            if (e.getKey() == null || !(e.getKey() instanceof Byte)) {
               return false;
            } else if (e.getValue() != null && e.getValue() instanceof Long) {
               byte k = (Byte)e.getKey();
               return Byte2LongArrayMap.this.containsKey(k) && Byte2LongArrayMap.this.get(k) == (Long)e.getValue();
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
            } else if (e.getValue() != null && e.getValue() instanceof Long) {
               byte k = (Byte)e.getKey();
               long v = (Long)e.getValue();
               int oldPos = Byte2LongArrayMap.this.findKey(k);
               if (oldPos != -1 && v == Byte2LongArrayMap.this.value[oldPos]) {
                  int tail = Byte2LongArrayMap.this.size - oldPos - 1;
                  System.arraycopy(Byte2LongArrayMap.this.key, oldPos + 1, Byte2LongArrayMap.this.key, oldPos, tail);
                  System.arraycopy(Byte2LongArrayMap.this.value, oldPos + 1, Byte2LongArrayMap.this.value, oldPos, tail);
                  Byte2LongArrayMap.this.size--;
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
         extends ObjectSpliterators.EarlyBindingSizeIndexBasedSpliterator<Byte2LongMap.Entry>
         implements ObjectSpliterator<Byte2LongMap.Entry> {
         EntrySetSpliterator(int pos, int maxPos) {
            super(pos, maxPos);
         }

         @Override
         public int characteristics() {
            return 16465;
         }

         protected final Byte2LongMap.Entry get(int location) {
            return new AbstractByte2LongMap.BasicEntry(Byte2LongArrayMap.this.key[location], Byte2LongArrayMap.this.value[location]);
         }

         protected final Byte2LongArrayMap.EntrySet.EntrySetSpliterator makeForSplit(int pos, int maxPos) {
            return EntrySet.this.new EntrySetSpliterator(pos, maxPos);
         }
      }
   }

   private final class KeySet extends AbstractByteSet {
      private KeySet() {
      }

      @Override
      public boolean contains(byte k) {
         return Byte2LongArrayMap.this.findKey(k) != -1;
      }

      @Override
      public boolean remove(byte k) {
         int oldPos = Byte2LongArrayMap.this.findKey(k);
         if (oldPos == -1) {
            return false;
         } else {
            int tail = Byte2LongArrayMap.this.size - oldPos - 1;
            System.arraycopy(Byte2LongArrayMap.this.key, oldPos + 1, Byte2LongArrayMap.this.key, oldPos, tail);
            System.arraycopy(Byte2LongArrayMap.this.value, oldPos + 1, Byte2LongArrayMap.this.value, oldPos, tail);
            Byte2LongArrayMap.this.size--;
            return true;
         }
      }

      @Override
      public ByteIterator iterator() {
         return new ByteIterator() {
            int pos = 0;

            @Override
            public boolean hasNext() {
               return this.pos < Byte2LongArrayMap.this.size;
            }

            @Override
            public byte nextByte() {
               if (!this.hasNext()) {
                  throw new NoSuchElementException();
               } else {
                  return Byte2LongArrayMap.this.key[this.pos++];
               }
            }

            @Override
            public void remove() {
               if (this.pos == 0) {
                  throw new IllegalStateException();
               } else {
                  int tail = Byte2LongArrayMap.this.size - this.pos;
                  System.arraycopy(Byte2LongArrayMap.this.key, this.pos, Byte2LongArrayMap.this.key, this.pos - 1, tail);
                  System.arraycopy(Byte2LongArrayMap.this.value, this.pos, Byte2LongArrayMap.this.value, this.pos - 1, tail);
                  Byte2LongArrayMap.this.size--;
                  this.pos--;
               }
            }

            @Override
            public void forEachRemaining(ByteConsumer action) {
               int max = Byte2LongArrayMap.this.size;

               while (this.pos < max) {
                  action.accept(Byte2LongArrayMap.this.key[this.pos++]);
               }
            }
         };
      }

      @Override
      public ByteSpliterator spliterator() {
         return new Byte2LongArrayMap.KeySet.KeySetSpliterator(0, Byte2LongArrayMap.this.size);
      }

      @Override
      public void forEach(ByteConsumer action) {
         int i = 0;

         for (int max = Byte2LongArrayMap.this.size; i < max; i++) {
            action.accept(Byte2LongArrayMap.this.key[i]);
         }
      }

      @Override
      public int size() {
         return Byte2LongArrayMap.this.size;
      }

      @Override
      public void clear() {
         Byte2LongArrayMap.this.clear();
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
            return Byte2LongArrayMap.this.key[location];
         }

         protected final Byte2LongArrayMap.KeySet.KeySetSpliterator makeForSplit(int pos, int maxPos) {
            return KeySet.this.new KeySetSpliterator(pos, maxPos);
         }

         @Override
         public void forEachRemaining(ByteConsumer action) {
            int max = Byte2LongArrayMap.this.size;

            while (this.pos < max) {
               action.accept(Byte2LongArrayMap.this.key[this.pos++]);
            }
         }
      }
   }

   private final class ValuesCollection extends AbstractLongCollection {
      private ValuesCollection() {
      }

      @Override
      public boolean contains(long v) {
         return Byte2LongArrayMap.this.containsValue(v);
      }

      @Override
      public LongIterator iterator() {
         return new LongIterator() {
            int pos = 0;

            @Override
            public boolean hasNext() {
               return this.pos < Byte2LongArrayMap.this.size;
            }

            @Override
            public long nextLong() {
               if (!this.hasNext()) {
                  throw new NoSuchElementException();
               } else {
                  return Byte2LongArrayMap.this.value[this.pos++];
               }
            }

            @Override
            public void remove() {
               if (this.pos == 0) {
                  throw new IllegalStateException();
               } else {
                  int tail = Byte2LongArrayMap.this.size - this.pos;
                  System.arraycopy(Byte2LongArrayMap.this.key, this.pos, Byte2LongArrayMap.this.key, this.pos - 1, tail);
                  System.arraycopy(Byte2LongArrayMap.this.value, this.pos, Byte2LongArrayMap.this.value, this.pos - 1, tail);
                  Byte2LongArrayMap.this.size--;
                  this.pos--;
               }
            }

            @Override
            public void forEachRemaining(LongConsumer action) {
               int max = Byte2LongArrayMap.this.size;

               while (this.pos < max) {
                  action.accept(Byte2LongArrayMap.this.value[this.pos++]);
               }
            }
         };
      }

      @Override
      public LongSpliterator spliterator() {
         return new Byte2LongArrayMap.ValuesCollection.ValuesSpliterator(0, Byte2LongArrayMap.this.size);
      }

      @Override
      public void forEach(LongConsumer action) {
         int i = 0;

         for (int max = Byte2LongArrayMap.this.size; i < max; i++) {
            action.accept(Byte2LongArrayMap.this.value[i]);
         }
      }

      @Override
      public int size() {
         return Byte2LongArrayMap.this.size;
      }

      @Override
      public void clear() {
         Byte2LongArrayMap.this.clear();
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
            return Byte2LongArrayMap.this.value[location];
         }

         protected final Byte2LongArrayMap.ValuesCollection.ValuesSpliterator makeForSplit(int pos, int maxPos) {
            return ValuesCollection.this.new ValuesSpliterator(pos, maxPos);
         }

         @Override
         public void forEachRemaining(LongConsumer action) {
            int max = Byte2LongArrayMap.this.size;

            while (this.pos < max) {
               action.accept(Byte2LongArrayMap.this.value[this.pos++]);
            }
         }
      }
   }
}
