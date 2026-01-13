package it.unimi.dsi.fastutil.bytes;

import it.unimi.dsi.fastutil.objects.AbstractObjectSet;
import it.unimi.dsi.fastutil.objects.AbstractReferenceCollection;
import it.unimi.dsi.fastutil.objects.ObjectArrays;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectSpliterator;
import it.unimi.dsi.fastutil.objects.ObjectSpliterators;
import it.unimi.dsi.fastutil.objects.ReferenceCollection;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Map.Entry;
import java.util.function.Consumer;

public class Byte2ReferenceArrayMap<V> extends AbstractByte2ReferenceMap<V> implements Serializable, Cloneable {
   private static final long serialVersionUID = 1L;
   protected transient byte[] key;
   protected transient Object[] value;
   protected int size;
   protected transient Byte2ReferenceMap.FastEntrySet<V> entries;
   protected transient ByteSet keys;
   protected transient ReferenceCollection<V> values;

   public Byte2ReferenceArrayMap(byte[] key, Object[] value) {
      this.key = key;
      this.value = value;
      this.size = key.length;
      if (key.length != value.length) {
         throw new IllegalArgumentException("Keys and values have different lengths (" + key.length + ", " + value.length + ")");
      }
   }

   public Byte2ReferenceArrayMap() {
      this.key = ByteArrays.EMPTY_ARRAY;
      this.value = ObjectArrays.EMPTY_ARRAY;
   }

   public Byte2ReferenceArrayMap(int capacity) {
      this.key = new byte[capacity];
      this.value = new Object[capacity];
   }

   public Byte2ReferenceArrayMap(Byte2ReferenceMap<V> m) {
      this(m.size());
      int i = 0;

      for (Byte2ReferenceMap.Entry<V> e : m.byte2ReferenceEntrySet()) {
         this.key[i] = e.getByteKey();
         this.value[i] = e.getValue();
         i++;
      }

      this.size = i;
   }

   public Byte2ReferenceArrayMap(Map<? extends Byte, ? extends V> m) {
      this(m.size());
      int i = 0;

      for (Entry<? extends Byte, ? extends V> e : m.entrySet()) {
         this.key[i] = e.getKey();
         this.value[i] = e.getValue();
         i++;
      }

      this.size = i;
   }

   public Byte2ReferenceArrayMap(byte[] key, Object[] value, int size) {
      this.key = key;
      this.value = value;
      this.size = size;
      if (key.length != value.length) {
         throw new IllegalArgumentException("Keys and values have different lengths (" + key.length + ", " + value.length + ")");
      } else if (size > key.length) {
         throw new IllegalArgumentException("The provided size (" + size + ") is larger than or equal to the backing-arrays size (" + key.length + ")");
      }
   }

   public Byte2ReferenceMap.FastEntrySet<V> byte2ReferenceEntrySet() {
      if (this.entries == null) {
         this.entries = new Byte2ReferenceArrayMap.EntrySet();
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
   public V get(byte k) {
      byte[] key = this.key;
      int i = this.size;

      while (i-- != 0) {
         if (key[i] == k) {
            return (V)this.value[i];
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
         this.value[i] = null;
      }

      this.size = 0;
   }

   @Override
   public boolean containsKey(byte k) {
      return this.findKey(k) != -1;
   }

   @Override
   public boolean containsValue(Object v) {
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
   public V put(byte k, V v) {
      int oldKey = this.findKey(k);
      if (oldKey != -1) {
         V oldValue = (V)this.value[oldKey];
         this.value[oldKey] = v;
         return oldValue;
      } else {
         if (this.size == this.key.length) {
            byte[] newKey = new byte[this.size == 0 ? 2 : this.size * 2];
            Object[] newValue = new Object[this.size == 0 ? 2 : this.size * 2];

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
   public V remove(byte k) {
      int oldPos = this.findKey(k);
      if (oldPos == -1) {
         return this.defRetValue;
      } else {
         V oldValue = (V)this.value[oldPos];
         int tail = this.size - oldPos - 1;
         System.arraycopy(this.key, oldPos + 1, this.key, oldPos, tail);
         System.arraycopy(this.value, oldPos + 1, this.value, oldPos, tail);
         this.size--;
         this.value[this.size] = null;
         return oldValue;
      }
   }

   @Override
   public ByteSet keySet() {
      if (this.keys == null) {
         this.keys = new Byte2ReferenceArrayMap.KeySet();
      }

      return this.keys;
   }

   @Override
   public ReferenceCollection<V> values() {
      if (this.values == null) {
         this.values = new Byte2ReferenceArrayMap.ValuesCollection();
      }

      return this.values;
   }

   public Byte2ReferenceArrayMap<V> clone() {
      Byte2ReferenceArrayMap<V> c;
      try {
         c = (Byte2ReferenceArrayMap<V>)super.clone();
      } catch (CloneNotSupportedException var3) {
         throw new InternalError();
      }

      c.key = (byte[])this.key.clone();
      c.value = (Object[])this.value.clone();
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
         s.writeObject(this.value[i]);
      }
   }

   private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
      s.defaultReadObject();
      this.key = new byte[this.size];
      this.value = new Object[this.size];

      for (int i = 0; i < this.size; i++) {
         this.key[i] = s.readByte();
         this.value[i] = s.readObject();
      }
   }

   private final class EntrySet extends AbstractObjectSet<Byte2ReferenceMap.Entry<V>> implements Byte2ReferenceMap.FastEntrySet<V> {
      private EntrySet() {
      }

      @Override
      public ObjectIterator<Byte2ReferenceMap.Entry<V>> iterator() {
         return new ObjectIterator<Byte2ReferenceMap.Entry<V>>() {
            int curr = -1;
            int next = 0;

            @Override
            public boolean hasNext() {
               return this.next < Byte2ReferenceArrayMap.this.size;
            }

            public Byte2ReferenceMap.Entry<V> next() {
               if (!this.hasNext()) {
                  throw new NoSuchElementException();
               } else {
                  return new AbstractByte2ReferenceMap.BasicEntry<>(
                     Byte2ReferenceArrayMap.this.key[this.curr = this.next], (V)Byte2ReferenceArrayMap.this.value[this.next++]
                  );
               }
            }

            @Override
            public void remove() {
               if (this.curr == -1) {
                  throw new IllegalStateException();
               } else {
                  this.curr = -1;
                  int tail = Byte2ReferenceArrayMap.this.size-- - this.next--;
                  System.arraycopy(Byte2ReferenceArrayMap.this.key, this.next + 1, Byte2ReferenceArrayMap.this.key, this.next, tail);
                  System.arraycopy(Byte2ReferenceArrayMap.this.value, this.next + 1, Byte2ReferenceArrayMap.this.value, this.next, tail);
                  Byte2ReferenceArrayMap.this.value[Byte2ReferenceArrayMap.this.size] = null;
               }
            }

            @Override
            public void forEachRemaining(Consumer<? super Byte2ReferenceMap.Entry<V>> action) {
               int max = Byte2ReferenceArrayMap.this.size;

               while (this.next < max) {
                  action.accept(
                     new AbstractByte2ReferenceMap.BasicEntry<>(
                        Byte2ReferenceArrayMap.this.key[this.curr = this.next], (V)Byte2ReferenceArrayMap.this.value[this.next++]
                     )
                  );
               }
            }
         };
      }

      @Override
      public ObjectIterator<Byte2ReferenceMap.Entry<V>> fastIterator() {
         return new ObjectIterator<Byte2ReferenceMap.Entry<V>>() {
            int next = 0;
            int curr = -1;
            final AbstractByte2ReferenceMap.BasicEntry<V> entry = new AbstractByte2ReferenceMap.BasicEntry<>();

            @Override
            public boolean hasNext() {
               return this.next < Byte2ReferenceArrayMap.this.size;
            }

            public Byte2ReferenceMap.Entry<V> next() {
               if (!this.hasNext()) {
                  throw new NoSuchElementException();
               } else {
                  this.entry.key = Byte2ReferenceArrayMap.this.key[this.curr = this.next];
                  this.entry.value = (V)Byte2ReferenceArrayMap.this.value[this.next++];
                  return this.entry;
               }
            }

            @Override
            public void remove() {
               if (this.curr == -1) {
                  throw new IllegalStateException();
               } else {
                  this.curr = -1;
                  int tail = Byte2ReferenceArrayMap.this.size-- - this.next--;
                  System.arraycopy(Byte2ReferenceArrayMap.this.key, this.next + 1, Byte2ReferenceArrayMap.this.key, this.next, tail);
                  System.arraycopy(Byte2ReferenceArrayMap.this.value, this.next + 1, Byte2ReferenceArrayMap.this.value, this.next, tail);
                  Byte2ReferenceArrayMap.this.value[Byte2ReferenceArrayMap.this.size] = null;
               }
            }

            @Override
            public void forEachRemaining(Consumer<? super Byte2ReferenceMap.Entry<V>> action) {
               int max = Byte2ReferenceArrayMap.this.size;

               while (this.next < max) {
                  this.entry.key = Byte2ReferenceArrayMap.this.key[this.curr = this.next];
                  this.entry.value = (V)Byte2ReferenceArrayMap.this.value[this.next++];
                  action.accept(this.entry);
               }
            }
         };
      }

      @Override
      public ObjectSpliterator<Byte2ReferenceMap.Entry<V>> spliterator() {
         return new Byte2ReferenceArrayMap.EntrySet.EntrySetSpliterator(0, Byte2ReferenceArrayMap.this.size);
      }

      @Override
      public void forEach(Consumer<? super Byte2ReferenceMap.Entry<V>> action) {
         int i = 0;

         for (int max = Byte2ReferenceArrayMap.this.size; i < max; i++) {
            action.accept(new AbstractByte2ReferenceMap.BasicEntry<>(Byte2ReferenceArrayMap.this.key[i], (V)Byte2ReferenceArrayMap.this.value[i]));
         }
      }

      @Override
      public void fastForEach(Consumer<? super Byte2ReferenceMap.Entry<V>> action) {
         AbstractByte2ReferenceMap.BasicEntry<V> entry = new AbstractByte2ReferenceMap.BasicEntry<>();
         int i = 0;

         for (int max = Byte2ReferenceArrayMap.this.size; i < max; i++) {
            entry.key = Byte2ReferenceArrayMap.this.key[i];
            entry.value = (V)Byte2ReferenceArrayMap.this.value[i];
            action.accept(entry);
         }
      }

      @Override
      public int size() {
         return Byte2ReferenceArrayMap.this.size;
      }

      @Override
      public boolean contains(Object o) {
         if (!(o instanceof Entry)) {
            return false;
         } else {
            Entry<?, ?> e = (Entry<?, ?>)o;
            if (e.getKey() != null && e.getKey() instanceof Byte) {
               byte k = (Byte)e.getKey();
               return Byte2ReferenceArrayMap.this.containsKey(k) && Byte2ReferenceArrayMap.this.get(k) == e.getValue();
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
            if (e.getKey() != null && e.getKey() instanceof Byte) {
               byte k = (Byte)e.getKey();
               V v = (V)e.getValue();
               int oldPos = Byte2ReferenceArrayMap.this.findKey(k);
               if (oldPos != -1 && v == Byte2ReferenceArrayMap.this.value[oldPos]) {
                  int tail = Byte2ReferenceArrayMap.this.size - oldPos - 1;
                  System.arraycopy(Byte2ReferenceArrayMap.this.key, oldPos + 1, Byte2ReferenceArrayMap.this.key, oldPos, tail);
                  System.arraycopy(Byte2ReferenceArrayMap.this.value, oldPos + 1, Byte2ReferenceArrayMap.this.value, oldPos, tail);
                  Byte2ReferenceArrayMap.this.size--;
                  Byte2ReferenceArrayMap.this.value[Byte2ReferenceArrayMap.this.size] = null;
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
         extends ObjectSpliterators.EarlyBindingSizeIndexBasedSpliterator<Byte2ReferenceMap.Entry<V>>
         implements ObjectSpliterator<Byte2ReferenceMap.Entry<V>> {
         EntrySetSpliterator(int pos, int maxPos) {
            super(pos, maxPos);
         }

         @Override
         public int characteristics() {
            return 16465;
         }

         protected final Byte2ReferenceMap.Entry<V> get(int location) {
            return new AbstractByte2ReferenceMap.BasicEntry<>(Byte2ReferenceArrayMap.this.key[location], (V)Byte2ReferenceArrayMap.this.value[location]);
         }

         protected final Byte2ReferenceArrayMap<V>.EntrySet.EntrySetSpliterator makeForSplit(int pos, int maxPos) {
            return EntrySet.this.new EntrySetSpliterator(pos, maxPos);
         }
      }
   }

   private final class KeySet extends AbstractByteSet {
      private KeySet() {
      }

      @Override
      public boolean contains(byte k) {
         return Byte2ReferenceArrayMap.this.findKey(k) != -1;
      }

      @Override
      public boolean remove(byte k) {
         int oldPos = Byte2ReferenceArrayMap.this.findKey(k);
         if (oldPos == -1) {
            return false;
         } else {
            int tail = Byte2ReferenceArrayMap.this.size - oldPos - 1;
            System.arraycopy(Byte2ReferenceArrayMap.this.key, oldPos + 1, Byte2ReferenceArrayMap.this.key, oldPos, tail);
            System.arraycopy(Byte2ReferenceArrayMap.this.value, oldPos + 1, Byte2ReferenceArrayMap.this.value, oldPos, tail);
            Byte2ReferenceArrayMap.this.size--;
            Byte2ReferenceArrayMap.this.value[Byte2ReferenceArrayMap.this.size] = null;
            return true;
         }
      }

      @Override
      public ByteIterator iterator() {
         return new ByteIterator() {
            int pos = 0;

            @Override
            public boolean hasNext() {
               return this.pos < Byte2ReferenceArrayMap.this.size;
            }

            @Override
            public byte nextByte() {
               if (!this.hasNext()) {
                  throw new NoSuchElementException();
               } else {
                  return Byte2ReferenceArrayMap.this.key[this.pos++];
               }
            }

            @Override
            public void remove() {
               if (this.pos == 0) {
                  throw new IllegalStateException();
               } else {
                  int tail = Byte2ReferenceArrayMap.this.size - this.pos;
                  System.arraycopy(Byte2ReferenceArrayMap.this.key, this.pos, Byte2ReferenceArrayMap.this.key, this.pos - 1, tail);
                  System.arraycopy(Byte2ReferenceArrayMap.this.value, this.pos, Byte2ReferenceArrayMap.this.value, this.pos - 1, tail);
                  Byte2ReferenceArrayMap.this.size--;
                  this.pos--;
                  Byte2ReferenceArrayMap.this.value[Byte2ReferenceArrayMap.this.size] = null;
               }
            }

            @Override
            public void forEachRemaining(ByteConsumer action) {
               int max = Byte2ReferenceArrayMap.this.size;

               while (this.pos < max) {
                  action.accept(Byte2ReferenceArrayMap.this.key[this.pos++]);
               }
            }
         };
      }

      @Override
      public ByteSpliterator spliterator() {
         return new Byte2ReferenceArrayMap.KeySet.KeySetSpliterator(0, Byte2ReferenceArrayMap.this.size);
      }

      @Override
      public void forEach(ByteConsumer action) {
         int i = 0;

         for (int max = Byte2ReferenceArrayMap.this.size; i < max; i++) {
            action.accept(Byte2ReferenceArrayMap.this.key[i]);
         }
      }

      @Override
      public int size() {
         return Byte2ReferenceArrayMap.this.size;
      }

      @Override
      public void clear() {
         Byte2ReferenceArrayMap.this.clear();
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
            return Byte2ReferenceArrayMap.this.key[location];
         }

         protected final Byte2ReferenceArrayMap<V>.KeySet.KeySetSpliterator makeForSplit(int pos, int maxPos) {
            return KeySet.this.new KeySetSpliterator(pos, maxPos);
         }

         @Override
         public void forEachRemaining(ByteConsumer action) {
            int max = Byte2ReferenceArrayMap.this.size;

            while (this.pos < max) {
               action.accept(Byte2ReferenceArrayMap.this.key[this.pos++]);
            }
         }
      }
   }

   private final class ValuesCollection extends AbstractReferenceCollection<V> {
      private ValuesCollection() {
      }

      @Override
      public boolean contains(Object v) {
         return Byte2ReferenceArrayMap.this.containsValue(v);
      }

      @Override
      public ObjectIterator<V> iterator() {
         return new ObjectIterator<V>() {
            int pos = 0;

            @Override
            public boolean hasNext() {
               return this.pos < Byte2ReferenceArrayMap.this.size;
            }

            @Override
            public V next() {
               if (!this.hasNext()) {
                  throw new NoSuchElementException();
               } else {
                  return (V)Byte2ReferenceArrayMap.this.value[this.pos++];
               }
            }

            @Override
            public void remove() {
               if (this.pos == 0) {
                  throw new IllegalStateException();
               } else {
                  int tail = Byte2ReferenceArrayMap.this.size - this.pos;
                  System.arraycopy(Byte2ReferenceArrayMap.this.key, this.pos, Byte2ReferenceArrayMap.this.key, this.pos - 1, tail);
                  System.arraycopy(Byte2ReferenceArrayMap.this.value, this.pos, Byte2ReferenceArrayMap.this.value, this.pos - 1, tail);
                  Byte2ReferenceArrayMap.this.size--;
                  this.pos--;
                  Byte2ReferenceArrayMap.this.value[Byte2ReferenceArrayMap.this.size] = null;
               }
            }

            @Override
            public void forEachRemaining(Consumer<? super V> action) {
               int max = Byte2ReferenceArrayMap.this.size;

               while (this.pos < max) {
                  action.accept((V)Byte2ReferenceArrayMap.this.value[this.pos++]);
               }
            }
         };
      }

      @Override
      public ObjectSpliterator<V> spliterator() {
         return new Byte2ReferenceArrayMap.ValuesCollection.ValuesSpliterator(0, Byte2ReferenceArrayMap.this.size);
      }

      @Override
      public void forEach(Consumer<? super V> action) {
         int i = 0;

         for (int max = Byte2ReferenceArrayMap.this.size; i < max; i++) {
            action.accept((V)Byte2ReferenceArrayMap.this.value[i]);
         }
      }

      @Override
      public int size() {
         return Byte2ReferenceArrayMap.this.size;
      }

      @Override
      public void clear() {
         Byte2ReferenceArrayMap.this.clear();
      }

      final class ValuesSpliterator extends ObjectSpliterators.EarlyBindingSizeIndexBasedSpliterator<V> implements ObjectSpliterator<V> {
         ValuesSpliterator(int pos, int maxPos) {
            super(pos, maxPos);
         }

         @Override
         public int characteristics() {
            return 16464;
         }

         @Override
         protected final V get(int location) {
            return (V)Byte2ReferenceArrayMap.this.value[location];
         }

         protected final Byte2ReferenceArrayMap<V>.ValuesCollection.ValuesSpliterator makeForSplit(int pos, int maxPos) {
            return ValuesCollection.this.new ValuesSpliterator(pos, maxPos);
         }

         @Override
         public void forEachRemaining(Consumer<? super V> action) {
            int max = Byte2ReferenceArrayMap.this.size;

            while (this.pos < max) {
               action.accept((V)Byte2ReferenceArrayMap.this.value[this.pos++]);
            }
         }
      }
   }
}
