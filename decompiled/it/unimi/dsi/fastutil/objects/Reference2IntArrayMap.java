package it.unimi.dsi.fastutil.objects;

import it.unimi.dsi.fastutil.ints.AbstractIntCollection;
import it.unimi.dsi.fastutil.ints.IntArrays;
import it.unimi.dsi.fastutil.ints.IntCollection;
import it.unimi.dsi.fastutil.ints.IntIterator;
import it.unimi.dsi.fastutil.ints.IntSpliterator;
import it.unimi.dsi.fastutil.ints.IntSpliterators;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Map.Entry;
import java.util.function.Consumer;
import java.util.function.IntConsumer;

public class Reference2IntArrayMap<K> extends AbstractReference2IntMap<K> implements Serializable, Cloneable {
   private static final long serialVersionUID = 1L;
   protected transient Object[] key;
   protected transient int[] value;
   protected int size;
   protected transient Reference2IntMap.FastEntrySet<K> entries;
   protected transient ReferenceSet<K> keys;
   protected transient IntCollection values;

   public Reference2IntArrayMap(Object[] key, int[] value) {
      this.key = key;
      this.value = value;
      this.size = key.length;
      if (key.length != value.length) {
         throw new IllegalArgumentException("Keys and values have different lengths (" + key.length + ", " + value.length + ")");
      }
   }

   public Reference2IntArrayMap() {
      this.key = ObjectArrays.EMPTY_ARRAY;
      this.value = IntArrays.EMPTY_ARRAY;
   }

   public Reference2IntArrayMap(int capacity) {
      this.key = new Object[capacity];
      this.value = new int[capacity];
   }

   public Reference2IntArrayMap(Reference2IntMap<K> m) {
      this(m.size());
      int i = 0;

      for (Reference2IntMap.Entry<K> e : m.reference2IntEntrySet()) {
         this.key[i] = e.getKey();
         this.value[i] = e.getIntValue();
         i++;
      }

      this.size = i;
   }

   public Reference2IntArrayMap(Map<? extends K, ? extends Integer> m) {
      this(m.size());
      int i = 0;

      for (Entry<? extends K, ? extends Integer> e : m.entrySet()) {
         this.key[i] = e.getKey();
         this.value[i] = e.getValue();
         i++;
      }

      this.size = i;
   }

   public Reference2IntArrayMap(Object[] key, int[] value, int size) {
      this.key = key;
      this.value = value;
      this.size = size;
      if (key.length != value.length) {
         throw new IllegalArgumentException("Keys and values have different lengths (" + key.length + ", " + value.length + ")");
      } else if (size > key.length) {
         throw new IllegalArgumentException("The provided size (" + size + ") is larger than or equal to the backing-arrays size (" + key.length + ")");
      }
   }

   public Reference2IntMap.FastEntrySet<K> reference2IntEntrySet() {
      if (this.entries == null) {
         this.entries = new Reference2IntArrayMap.EntrySet();
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
   public int getInt(Object k) {
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
   public int put(K k, int v) {
      int oldKey = this.findKey(k);
      if (oldKey != -1) {
         int oldValue = this.value[oldKey];
         this.value[oldKey] = v;
         return oldValue;
      } else {
         if (this.size == this.key.length) {
            Object[] newKey = new Object[this.size == 0 ? 2 : this.size * 2];
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
   public int removeInt(Object k) {
      int oldPos = this.findKey(k);
      if (oldPos == -1) {
         return this.defRetValue;
      } else {
         int oldValue = this.value[oldPos];
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
         this.keys = new Reference2IntArrayMap.KeySet();
      }

      return this.keys;
   }

   @Override
   public IntCollection values() {
      if (this.values == null) {
         this.values = new Reference2IntArrayMap.ValuesCollection();
      }

      return this.values;
   }

   public Reference2IntArrayMap<K> clone() {
      Reference2IntArrayMap<K> c;
      try {
         c = (Reference2IntArrayMap<K>)super.clone();
      } catch (CloneNotSupportedException var3) {
         throw new InternalError();
      }

      c.key = (Object[])this.key.clone();
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
         s.writeObject(this.key[i]);
         s.writeInt(this.value[i]);
      }
   }

   private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
      s.defaultReadObject();
      this.key = new Object[this.size];
      this.value = new int[this.size];

      for (int i = 0; i < this.size; i++) {
         this.key[i] = s.readObject();
         this.value[i] = s.readInt();
      }
   }

   private final class EntrySet extends AbstractObjectSet<Reference2IntMap.Entry<K>> implements Reference2IntMap.FastEntrySet<K> {
      private EntrySet() {
      }

      @Override
      public ObjectIterator<Reference2IntMap.Entry<K>> iterator() {
         return new ObjectIterator<Reference2IntMap.Entry<K>>() {
            int curr = -1;
            int next = 0;

            @Override
            public boolean hasNext() {
               return this.next < Reference2IntArrayMap.this.size;
            }

            public Reference2IntMap.Entry<K> next() {
               if (!this.hasNext()) {
                  throw new NoSuchElementException();
               } else {
                  return new AbstractReference2IntMap.BasicEntry<>(
                     (K)Reference2IntArrayMap.this.key[this.curr = this.next], Reference2IntArrayMap.this.value[this.next++]
                  );
               }
            }

            @Override
            public void remove() {
               if (this.curr == -1) {
                  throw new IllegalStateException();
               } else {
                  this.curr = -1;
                  int tail = Reference2IntArrayMap.this.size-- - this.next--;
                  System.arraycopy(Reference2IntArrayMap.this.key, this.next + 1, Reference2IntArrayMap.this.key, this.next, tail);
                  System.arraycopy(Reference2IntArrayMap.this.value, this.next + 1, Reference2IntArrayMap.this.value, this.next, tail);
                  Reference2IntArrayMap.this.key[Reference2IntArrayMap.this.size] = null;
               }
            }

            @Override
            public void forEachRemaining(Consumer<? super Reference2IntMap.Entry<K>> action) {
               int max = Reference2IntArrayMap.this.size;

               while (this.next < max) {
                  action.accept(
                     new AbstractReference2IntMap.BasicEntry<>(
                        (K)Reference2IntArrayMap.this.key[this.curr = this.next], Reference2IntArrayMap.this.value[this.next++]
                     )
                  );
               }
            }
         };
      }

      @Override
      public ObjectIterator<Reference2IntMap.Entry<K>> fastIterator() {
         return new ObjectIterator<Reference2IntMap.Entry<K>>() {
            int next = 0;
            int curr = -1;
            final AbstractReference2IntMap.BasicEntry<K> entry = new AbstractReference2IntMap.BasicEntry<>();

            @Override
            public boolean hasNext() {
               return this.next < Reference2IntArrayMap.this.size;
            }

            public Reference2IntMap.Entry<K> next() {
               if (!this.hasNext()) {
                  throw new NoSuchElementException();
               } else {
                  this.entry.key = (K)Reference2IntArrayMap.this.key[this.curr = this.next];
                  this.entry.value = Reference2IntArrayMap.this.value[this.next++];
                  return this.entry;
               }
            }

            @Override
            public void remove() {
               if (this.curr == -1) {
                  throw new IllegalStateException();
               } else {
                  this.curr = -1;
                  int tail = Reference2IntArrayMap.this.size-- - this.next--;
                  System.arraycopy(Reference2IntArrayMap.this.key, this.next + 1, Reference2IntArrayMap.this.key, this.next, tail);
                  System.arraycopy(Reference2IntArrayMap.this.value, this.next + 1, Reference2IntArrayMap.this.value, this.next, tail);
                  Reference2IntArrayMap.this.key[Reference2IntArrayMap.this.size] = null;
               }
            }

            @Override
            public void forEachRemaining(Consumer<? super Reference2IntMap.Entry<K>> action) {
               int max = Reference2IntArrayMap.this.size;

               while (this.next < max) {
                  this.entry.key = (K)Reference2IntArrayMap.this.key[this.curr = this.next];
                  this.entry.value = Reference2IntArrayMap.this.value[this.next++];
                  action.accept(this.entry);
               }
            }
         };
      }

      @Override
      public ObjectSpliterator<Reference2IntMap.Entry<K>> spliterator() {
         return new Reference2IntArrayMap.EntrySet.EntrySetSpliterator(0, Reference2IntArrayMap.this.size);
      }

      @Override
      public void forEach(Consumer<? super Reference2IntMap.Entry<K>> action) {
         int i = 0;

         for (int max = Reference2IntArrayMap.this.size; i < max; i++) {
            action.accept(new AbstractReference2IntMap.BasicEntry<>((K)Reference2IntArrayMap.this.key[i], Reference2IntArrayMap.this.value[i]));
         }
      }

      @Override
      public void fastForEach(Consumer<? super Reference2IntMap.Entry<K>> action) {
         AbstractReference2IntMap.BasicEntry<K> entry = new AbstractReference2IntMap.BasicEntry<>();
         int i = 0;

         for (int max = Reference2IntArrayMap.this.size; i < max; i++) {
            entry.key = (K)Reference2IntArrayMap.this.key[i];
            entry.value = Reference2IntArrayMap.this.value[i];
            action.accept(entry);
         }
      }

      @Override
      public int size() {
         return Reference2IntArrayMap.this.size;
      }

      @Override
      public boolean contains(Object o) {
         if (!(o instanceof Entry)) {
            return false;
         } else {
            Entry<?, ?> e = (Entry<?, ?>)o;
            if (e.getValue() != null && e.getValue() instanceof Integer) {
               K k = (K)e.getKey();
               return Reference2IntArrayMap.this.containsKey(k) && Reference2IntArrayMap.this.getInt(k) == (Integer)e.getValue();
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
            if (e.getValue() != null && e.getValue() instanceof Integer) {
               K k = (K)e.getKey();
               int v = (Integer)e.getValue();
               int oldPos = Reference2IntArrayMap.this.findKey(k);
               if (oldPos != -1 && v == Reference2IntArrayMap.this.value[oldPos]) {
                  int tail = Reference2IntArrayMap.this.size - oldPos - 1;
                  System.arraycopy(Reference2IntArrayMap.this.key, oldPos + 1, Reference2IntArrayMap.this.key, oldPos, tail);
                  System.arraycopy(Reference2IntArrayMap.this.value, oldPos + 1, Reference2IntArrayMap.this.value, oldPos, tail);
                  Reference2IntArrayMap.this.size--;
                  Reference2IntArrayMap.this.key[Reference2IntArrayMap.this.size] = null;
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
         extends ObjectSpliterators.EarlyBindingSizeIndexBasedSpliterator<Reference2IntMap.Entry<K>>
         implements ObjectSpliterator<Reference2IntMap.Entry<K>> {
         EntrySetSpliterator(int pos, int maxPos) {
            super(pos, maxPos);
         }

         @Override
         public int characteristics() {
            return 16465;
         }

         protected final Reference2IntMap.Entry<K> get(int location) {
            return new AbstractReference2IntMap.BasicEntry<>((K)Reference2IntArrayMap.this.key[location], Reference2IntArrayMap.this.value[location]);
         }

         protected final Reference2IntArrayMap<K>.EntrySet.EntrySetSpliterator makeForSplit(int pos, int maxPos) {
            return EntrySet.this.new EntrySetSpliterator(pos, maxPos);
         }
      }
   }

   private final class KeySet extends AbstractReferenceSet<K> {
      private KeySet() {
      }

      @Override
      public boolean contains(Object k) {
         return Reference2IntArrayMap.this.findKey(k) != -1;
      }

      @Override
      public boolean remove(Object k) {
         int oldPos = Reference2IntArrayMap.this.findKey(k);
         if (oldPos == -1) {
            return false;
         } else {
            int tail = Reference2IntArrayMap.this.size - oldPos - 1;
            System.arraycopy(Reference2IntArrayMap.this.key, oldPos + 1, Reference2IntArrayMap.this.key, oldPos, tail);
            System.arraycopy(Reference2IntArrayMap.this.value, oldPos + 1, Reference2IntArrayMap.this.value, oldPos, tail);
            Reference2IntArrayMap.this.size--;
            Reference2IntArrayMap.this.key[Reference2IntArrayMap.this.size] = null;
            return true;
         }
      }

      @Override
      public ObjectIterator<K> iterator() {
         return new ObjectIterator<K>() {
            int pos = 0;

            @Override
            public boolean hasNext() {
               return this.pos < Reference2IntArrayMap.this.size;
            }

            @Override
            public K next() {
               if (!this.hasNext()) {
                  throw new NoSuchElementException();
               } else {
                  return (K)Reference2IntArrayMap.this.key[this.pos++];
               }
            }

            @Override
            public void remove() {
               if (this.pos == 0) {
                  throw new IllegalStateException();
               } else {
                  int tail = Reference2IntArrayMap.this.size - this.pos;
                  System.arraycopy(Reference2IntArrayMap.this.key, this.pos, Reference2IntArrayMap.this.key, this.pos - 1, tail);
                  System.arraycopy(Reference2IntArrayMap.this.value, this.pos, Reference2IntArrayMap.this.value, this.pos - 1, tail);
                  Reference2IntArrayMap.this.size--;
                  this.pos--;
                  Reference2IntArrayMap.this.key[Reference2IntArrayMap.this.size] = null;
               }
            }

            @Override
            public void forEachRemaining(Consumer<? super K> action) {
               int max = Reference2IntArrayMap.this.size;

               while (this.pos < max) {
                  action.accept((K)Reference2IntArrayMap.this.key[this.pos++]);
               }
            }
         };
      }

      @Override
      public ObjectSpliterator<K> spliterator() {
         return new Reference2IntArrayMap.KeySet.KeySetSpliterator(0, Reference2IntArrayMap.this.size);
      }

      @Override
      public void forEach(Consumer<? super K> action) {
         int i = 0;

         for (int max = Reference2IntArrayMap.this.size; i < max; i++) {
            action.accept((K)Reference2IntArrayMap.this.key[i]);
         }
      }

      @Override
      public int size() {
         return Reference2IntArrayMap.this.size;
      }

      @Override
      public void clear() {
         Reference2IntArrayMap.this.clear();
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
            return (K)Reference2IntArrayMap.this.key[location];
         }

         protected final Reference2IntArrayMap<K>.KeySet.KeySetSpliterator makeForSplit(int pos, int maxPos) {
            return KeySet.this.new KeySetSpliterator(pos, maxPos);
         }

         @Override
         public void forEachRemaining(Consumer<? super K> action) {
            int max = Reference2IntArrayMap.this.size;

            while (this.pos < max) {
               action.accept((K)Reference2IntArrayMap.this.key[this.pos++]);
            }
         }
      }
   }

   private final class ValuesCollection extends AbstractIntCollection {
      private ValuesCollection() {
      }

      @Override
      public boolean contains(int v) {
         return Reference2IntArrayMap.this.containsValue(v);
      }

      @Override
      public IntIterator iterator() {
         return new IntIterator() {
            int pos = 0;

            @Override
            public boolean hasNext() {
               return this.pos < Reference2IntArrayMap.this.size;
            }

            @Override
            public int nextInt() {
               if (!this.hasNext()) {
                  throw new NoSuchElementException();
               } else {
                  return Reference2IntArrayMap.this.value[this.pos++];
               }
            }

            @Override
            public void remove() {
               if (this.pos == 0) {
                  throw new IllegalStateException();
               } else {
                  int tail = Reference2IntArrayMap.this.size - this.pos;
                  System.arraycopy(Reference2IntArrayMap.this.key, this.pos, Reference2IntArrayMap.this.key, this.pos - 1, tail);
                  System.arraycopy(Reference2IntArrayMap.this.value, this.pos, Reference2IntArrayMap.this.value, this.pos - 1, tail);
                  Reference2IntArrayMap.this.size--;
                  this.pos--;
                  Reference2IntArrayMap.this.key[Reference2IntArrayMap.this.size] = null;
               }
            }

            @Override
            public void forEachRemaining(IntConsumer action) {
               int max = Reference2IntArrayMap.this.size;

               while (this.pos < max) {
                  action.accept(Reference2IntArrayMap.this.value[this.pos++]);
               }
            }
         };
      }

      @Override
      public IntSpliterator spliterator() {
         return new Reference2IntArrayMap.ValuesCollection.ValuesSpliterator(0, Reference2IntArrayMap.this.size);
      }

      @Override
      public void forEach(IntConsumer action) {
         int i = 0;

         for (int max = Reference2IntArrayMap.this.size; i < max; i++) {
            action.accept(Reference2IntArrayMap.this.value[i]);
         }
      }

      @Override
      public int size() {
         return Reference2IntArrayMap.this.size;
      }

      @Override
      public void clear() {
         Reference2IntArrayMap.this.clear();
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
            return Reference2IntArrayMap.this.value[location];
         }

         protected final Reference2IntArrayMap<K>.ValuesCollection.ValuesSpliterator makeForSplit(int pos, int maxPos) {
            return ValuesCollection.this.new ValuesSpliterator(pos, maxPos);
         }

         @Override
         public void forEachRemaining(IntConsumer action) {
            int max = Reference2IntArrayMap.this.size;

            while (this.pos < max) {
               action.accept(Reference2IntArrayMap.this.value[this.pos++]);
            }
         }
      }
   }
}
