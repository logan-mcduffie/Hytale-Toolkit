package it.unimi.dsi.fastutil.objects;

import it.unimi.dsi.fastutil.booleans.AbstractBooleanCollection;
import it.unimi.dsi.fastutil.booleans.BooleanArrays;
import it.unimi.dsi.fastutil.booleans.BooleanCollection;
import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import it.unimi.dsi.fastutil.booleans.BooleanIterator;
import it.unimi.dsi.fastutil.booleans.BooleanSpliterator;
import it.unimi.dsi.fastutil.booleans.BooleanSpliterators;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Map.Entry;
import java.util.function.Consumer;

public class Reference2BooleanArrayMap<K> extends AbstractReference2BooleanMap<K> implements Serializable, Cloneable {
   private static final long serialVersionUID = 1L;
   protected transient Object[] key;
   protected transient boolean[] value;
   protected int size;
   protected transient Reference2BooleanMap.FastEntrySet<K> entries;
   protected transient ReferenceSet<K> keys;
   protected transient BooleanCollection values;

   public Reference2BooleanArrayMap(Object[] key, boolean[] value) {
      this.key = key;
      this.value = value;
      this.size = key.length;
      if (key.length != value.length) {
         throw new IllegalArgumentException("Keys and values have different lengths (" + key.length + ", " + value.length + ")");
      }
   }

   public Reference2BooleanArrayMap() {
      this.key = ObjectArrays.EMPTY_ARRAY;
      this.value = BooleanArrays.EMPTY_ARRAY;
   }

   public Reference2BooleanArrayMap(int capacity) {
      this.key = new Object[capacity];
      this.value = new boolean[capacity];
   }

   public Reference2BooleanArrayMap(Reference2BooleanMap<K> m) {
      this(m.size());
      int i = 0;

      for (Reference2BooleanMap.Entry<K> e : m.reference2BooleanEntrySet()) {
         this.key[i] = e.getKey();
         this.value[i] = e.getBooleanValue();
         i++;
      }

      this.size = i;
   }

   public Reference2BooleanArrayMap(Map<? extends K, ? extends Boolean> m) {
      this(m.size());
      int i = 0;

      for (Entry<? extends K, ? extends Boolean> e : m.entrySet()) {
         this.key[i] = e.getKey();
         this.value[i] = e.getValue();
         i++;
      }

      this.size = i;
   }

   public Reference2BooleanArrayMap(Object[] key, boolean[] value, int size) {
      this.key = key;
      this.value = value;
      this.size = size;
      if (key.length != value.length) {
         throw new IllegalArgumentException("Keys and values have different lengths (" + key.length + ", " + value.length + ")");
      } else if (size > key.length) {
         throw new IllegalArgumentException("The provided size (" + size + ") is larger than or equal to the backing-arrays size (" + key.length + ")");
      }
   }

   public Reference2BooleanMap.FastEntrySet<K> reference2BooleanEntrySet() {
      if (this.entries == null) {
         this.entries = new Reference2BooleanArrayMap.EntrySet();
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
   public boolean getBoolean(Object k) {
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
   public boolean containsValue(boolean v) {
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
   public boolean put(K k, boolean v) {
      int oldKey = this.findKey(k);
      if (oldKey != -1) {
         boolean oldValue = this.value[oldKey];
         this.value[oldKey] = v;
         return oldValue;
      } else {
         if (this.size == this.key.length) {
            Object[] newKey = new Object[this.size == 0 ? 2 : this.size * 2];
            boolean[] newValue = new boolean[this.size == 0 ? 2 : this.size * 2];

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
   public boolean removeBoolean(Object k) {
      int oldPos = this.findKey(k);
      if (oldPos == -1) {
         return this.defRetValue;
      } else {
         boolean oldValue = this.value[oldPos];
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
         this.keys = new Reference2BooleanArrayMap.KeySet();
      }

      return this.keys;
   }

   @Override
   public BooleanCollection values() {
      if (this.values == null) {
         this.values = new Reference2BooleanArrayMap.ValuesCollection();
      }

      return this.values;
   }

   public Reference2BooleanArrayMap<K> clone() {
      Reference2BooleanArrayMap<K> c;
      try {
         c = (Reference2BooleanArrayMap<K>)super.clone();
      } catch (CloneNotSupportedException var3) {
         throw new InternalError();
      }

      c.key = (Object[])this.key.clone();
      c.value = (boolean[])this.value.clone();
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
         s.writeBoolean(this.value[i]);
      }
   }

   private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
      s.defaultReadObject();
      this.key = new Object[this.size];
      this.value = new boolean[this.size];

      for (int i = 0; i < this.size; i++) {
         this.key[i] = s.readObject();
         this.value[i] = s.readBoolean();
      }
   }

   private final class EntrySet extends AbstractObjectSet<Reference2BooleanMap.Entry<K>> implements Reference2BooleanMap.FastEntrySet<K> {
      private EntrySet() {
      }

      @Override
      public ObjectIterator<Reference2BooleanMap.Entry<K>> iterator() {
         return new ObjectIterator<Reference2BooleanMap.Entry<K>>() {
            int curr = -1;
            int next = 0;

            @Override
            public boolean hasNext() {
               return this.next < Reference2BooleanArrayMap.this.size;
            }

            public Reference2BooleanMap.Entry<K> next() {
               if (!this.hasNext()) {
                  throw new NoSuchElementException();
               } else {
                  return new AbstractReference2BooleanMap.BasicEntry<>(
                     (K)Reference2BooleanArrayMap.this.key[this.curr = this.next], Reference2BooleanArrayMap.this.value[this.next++]
                  );
               }
            }

            @Override
            public void remove() {
               if (this.curr == -1) {
                  throw new IllegalStateException();
               } else {
                  this.curr = -1;
                  int tail = Reference2BooleanArrayMap.this.size-- - this.next--;
                  System.arraycopy(Reference2BooleanArrayMap.this.key, this.next + 1, Reference2BooleanArrayMap.this.key, this.next, tail);
                  System.arraycopy(Reference2BooleanArrayMap.this.value, this.next + 1, Reference2BooleanArrayMap.this.value, this.next, tail);
                  Reference2BooleanArrayMap.this.key[Reference2BooleanArrayMap.this.size] = null;
               }
            }

            @Override
            public void forEachRemaining(Consumer<? super Reference2BooleanMap.Entry<K>> action) {
               int max = Reference2BooleanArrayMap.this.size;

               while (this.next < max) {
                  action.accept(
                     new AbstractReference2BooleanMap.BasicEntry<>(
                        (K)Reference2BooleanArrayMap.this.key[this.curr = this.next], Reference2BooleanArrayMap.this.value[this.next++]
                     )
                  );
               }
            }
         };
      }

      @Override
      public ObjectIterator<Reference2BooleanMap.Entry<K>> fastIterator() {
         return new ObjectIterator<Reference2BooleanMap.Entry<K>>() {
            int next = 0;
            int curr = -1;
            final AbstractReference2BooleanMap.BasicEntry<K> entry = new AbstractReference2BooleanMap.BasicEntry<>();

            @Override
            public boolean hasNext() {
               return this.next < Reference2BooleanArrayMap.this.size;
            }

            public Reference2BooleanMap.Entry<K> next() {
               if (!this.hasNext()) {
                  throw new NoSuchElementException();
               } else {
                  this.entry.key = (K)Reference2BooleanArrayMap.this.key[this.curr = this.next];
                  this.entry.value = Reference2BooleanArrayMap.this.value[this.next++];
                  return this.entry;
               }
            }

            @Override
            public void remove() {
               if (this.curr == -1) {
                  throw new IllegalStateException();
               } else {
                  this.curr = -1;
                  int tail = Reference2BooleanArrayMap.this.size-- - this.next--;
                  System.arraycopy(Reference2BooleanArrayMap.this.key, this.next + 1, Reference2BooleanArrayMap.this.key, this.next, tail);
                  System.arraycopy(Reference2BooleanArrayMap.this.value, this.next + 1, Reference2BooleanArrayMap.this.value, this.next, tail);
                  Reference2BooleanArrayMap.this.key[Reference2BooleanArrayMap.this.size] = null;
               }
            }

            @Override
            public void forEachRemaining(Consumer<? super Reference2BooleanMap.Entry<K>> action) {
               int max = Reference2BooleanArrayMap.this.size;

               while (this.next < max) {
                  this.entry.key = (K)Reference2BooleanArrayMap.this.key[this.curr = this.next];
                  this.entry.value = Reference2BooleanArrayMap.this.value[this.next++];
                  action.accept(this.entry);
               }
            }
         };
      }

      @Override
      public ObjectSpliterator<Reference2BooleanMap.Entry<K>> spliterator() {
         return new Reference2BooleanArrayMap.EntrySet.EntrySetSpliterator(0, Reference2BooleanArrayMap.this.size);
      }

      @Override
      public void forEach(Consumer<? super Reference2BooleanMap.Entry<K>> action) {
         int i = 0;

         for (int max = Reference2BooleanArrayMap.this.size; i < max; i++) {
            action.accept(new AbstractReference2BooleanMap.BasicEntry<>((K)Reference2BooleanArrayMap.this.key[i], Reference2BooleanArrayMap.this.value[i]));
         }
      }

      @Override
      public void fastForEach(Consumer<? super Reference2BooleanMap.Entry<K>> action) {
         AbstractReference2BooleanMap.BasicEntry<K> entry = new AbstractReference2BooleanMap.BasicEntry<>();
         int i = 0;

         for (int max = Reference2BooleanArrayMap.this.size; i < max; i++) {
            entry.key = (K)Reference2BooleanArrayMap.this.key[i];
            entry.value = Reference2BooleanArrayMap.this.value[i];
            action.accept(entry);
         }
      }

      @Override
      public int size() {
         return Reference2BooleanArrayMap.this.size;
      }

      @Override
      public boolean contains(Object o) {
         if (!(o instanceof Entry)) {
            return false;
         } else {
            Entry<?, ?> e = (Entry<?, ?>)o;
            if (e.getValue() != null && e.getValue() instanceof Boolean) {
               K k = (K)e.getKey();
               return Reference2BooleanArrayMap.this.containsKey(k) && Reference2BooleanArrayMap.this.getBoolean(k) == (Boolean)e.getValue();
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
            if (e.getValue() != null && e.getValue() instanceof Boolean) {
               K k = (K)e.getKey();
               boolean v = (Boolean)e.getValue();
               int oldPos = Reference2BooleanArrayMap.this.findKey(k);
               if (oldPos != -1 && v == Reference2BooleanArrayMap.this.value[oldPos]) {
                  int tail = Reference2BooleanArrayMap.this.size - oldPos - 1;
                  System.arraycopy(Reference2BooleanArrayMap.this.key, oldPos + 1, Reference2BooleanArrayMap.this.key, oldPos, tail);
                  System.arraycopy(Reference2BooleanArrayMap.this.value, oldPos + 1, Reference2BooleanArrayMap.this.value, oldPos, tail);
                  Reference2BooleanArrayMap.this.size--;
                  Reference2BooleanArrayMap.this.key[Reference2BooleanArrayMap.this.size] = null;
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
         extends ObjectSpliterators.EarlyBindingSizeIndexBasedSpliterator<Reference2BooleanMap.Entry<K>>
         implements ObjectSpliterator<Reference2BooleanMap.Entry<K>> {
         EntrySetSpliterator(int pos, int maxPos) {
            super(pos, maxPos);
         }

         @Override
         public int characteristics() {
            return 16465;
         }

         protected final Reference2BooleanMap.Entry<K> get(int location) {
            return new AbstractReference2BooleanMap.BasicEntry<>(
               (K)Reference2BooleanArrayMap.this.key[location], Reference2BooleanArrayMap.this.value[location]
            );
         }

         protected final Reference2BooleanArrayMap<K>.EntrySet.EntrySetSpliterator makeForSplit(int pos, int maxPos) {
            return EntrySet.this.new EntrySetSpliterator(pos, maxPos);
         }
      }
   }

   private final class KeySet extends AbstractReferenceSet<K> {
      private KeySet() {
      }

      @Override
      public boolean contains(Object k) {
         return Reference2BooleanArrayMap.this.findKey(k) != -1;
      }

      @Override
      public boolean remove(Object k) {
         int oldPos = Reference2BooleanArrayMap.this.findKey(k);
         if (oldPos == -1) {
            return false;
         } else {
            int tail = Reference2BooleanArrayMap.this.size - oldPos - 1;
            System.arraycopy(Reference2BooleanArrayMap.this.key, oldPos + 1, Reference2BooleanArrayMap.this.key, oldPos, tail);
            System.arraycopy(Reference2BooleanArrayMap.this.value, oldPos + 1, Reference2BooleanArrayMap.this.value, oldPos, tail);
            Reference2BooleanArrayMap.this.size--;
            Reference2BooleanArrayMap.this.key[Reference2BooleanArrayMap.this.size] = null;
            return true;
         }
      }

      @Override
      public ObjectIterator<K> iterator() {
         return new ObjectIterator<K>() {
            int pos = 0;

            @Override
            public boolean hasNext() {
               return this.pos < Reference2BooleanArrayMap.this.size;
            }

            @Override
            public K next() {
               if (!this.hasNext()) {
                  throw new NoSuchElementException();
               } else {
                  return (K)Reference2BooleanArrayMap.this.key[this.pos++];
               }
            }

            @Override
            public void remove() {
               if (this.pos == 0) {
                  throw new IllegalStateException();
               } else {
                  int tail = Reference2BooleanArrayMap.this.size - this.pos;
                  System.arraycopy(Reference2BooleanArrayMap.this.key, this.pos, Reference2BooleanArrayMap.this.key, this.pos - 1, tail);
                  System.arraycopy(Reference2BooleanArrayMap.this.value, this.pos, Reference2BooleanArrayMap.this.value, this.pos - 1, tail);
                  Reference2BooleanArrayMap.this.size--;
                  this.pos--;
                  Reference2BooleanArrayMap.this.key[Reference2BooleanArrayMap.this.size] = null;
               }
            }

            @Override
            public void forEachRemaining(Consumer<? super K> action) {
               int max = Reference2BooleanArrayMap.this.size;

               while (this.pos < max) {
                  action.accept((K)Reference2BooleanArrayMap.this.key[this.pos++]);
               }
            }
         };
      }

      @Override
      public ObjectSpliterator<K> spliterator() {
         return new Reference2BooleanArrayMap.KeySet.KeySetSpliterator(0, Reference2BooleanArrayMap.this.size);
      }

      @Override
      public void forEach(Consumer<? super K> action) {
         int i = 0;

         for (int max = Reference2BooleanArrayMap.this.size; i < max; i++) {
            action.accept((K)Reference2BooleanArrayMap.this.key[i]);
         }
      }

      @Override
      public int size() {
         return Reference2BooleanArrayMap.this.size;
      }

      @Override
      public void clear() {
         Reference2BooleanArrayMap.this.clear();
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
            return (K)Reference2BooleanArrayMap.this.key[location];
         }

         protected final Reference2BooleanArrayMap<K>.KeySet.KeySetSpliterator makeForSplit(int pos, int maxPos) {
            return KeySet.this.new KeySetSpliterator(pos, maxPos);
         }

         @Override
         public void forEachRemaining(Consumer<? super K> action) {
            int max = Reference2BooleanArrayMap.this.size;

            while (this.pos < max) {
               action.accept((K)Reference2BooleanArrayMap.this.key[this.pos++]);
            }
         }
      }
   }

   private final class ValuesCollection extends AbstractBooleanCollection {
      private ValuesCollection() {
      }

      @Override
      public boolean contains(boolean v) {
         return Reference2BooleanArrayMap.this.containsValue(v);
      }

      @Override
      public BooleanIterator iterator() {
         return new BooleanIterator() {
            int pos = 0;

            @Override
            public boolean hasNext() {
               return this.pos < Reference2BooleanArrayMap.this.size;
            }

            @Override
            public boolean nextBoolean() {
               if (!this.hasNext()) {
                  throw new NoSuchElementException();
               } else {
                  return Reference2BooleanArrayMap.this.value[this.pos++];
               }
            }

            @Override
            public void remove() {
               if (this.pos == 0) {
                  throw new IllegalStateException();
               } else {
                  int tail = Reference2BooleanArrayMap.this.size - this.pos;
                  System.arraycopy(Reference2BooleanArrayMap.this.key, this.pos, Reference2BooleanArrayMap.this.key, this.pos - 1, tail);
                  System.arraycopy(Reference2BooleanArrayMap.this.value, this.pos, Reference2BooleanArrayMap.this.value, this.pos - 1, tail);
                  Reference2BooleanArrayMap.this.size--;
                  this.pos--;
                  Reference2BooleanArrayMap.this.key[Reference2BooleanArrayMap.this.size] = null;
               }
            }

            @Override
            public void forEachRemaining(BooleanConsumer action) {
               int max = Reference2BooleanArrayMap.this.size;

               while (this.pos < max) {
                  action.accept(Reference2BooleanArrayMap.this.value[this.pos++]);
               }
            }
         };
      }

      @Override
      public BooleanSpliterator spliterator() {
         return new Reference2BooleanArrayMap.ValuesCollection.ValuesSpliterator(0, Reference2BooleanArrayMap.this.size);
      }

      @Override
      public void forEach(BooleanConsumer action) {
         int i = 0;

         for (int max = Reference2BooleanArrayMap.this.size; i < max; i++) {
            action.accept(Reference2BooleanArrayMap.this.value[i]);
         }
      }

      @Override
      public int size() {
         return Reference2BooleanArrayMap.this.size;
      }

      @Override
      public void clear() {
         Reference2BooleanArrayMap.this.clear();
      }

      final class ValuesSpliterator extends BooleanSpliterators.EarlyBindingSizeIndexBasedSpliterator implements BooleanSpliterator {
         ValuesSpliterator(int pos, int maxPos) {
            super(pos, maxPos);
         }

         @Override
         public int characteristics() {
            return 16720;
         }

         @Override
         protected final boolean get(int location) {
            return Reference2BooleanArrayMap.this.value[location];
         }

         protected final Reference2BooleanArrayMap<K>.ValuesCollection.ValuesSpliterator makeForSplit(int pos, int maxPos) {
            return ValuesCollection.this.new ValuesSpliterator(pos, maxPos);
         }

         @Override
         public void forEachRemaining(BooleanConsumer action) {
            int max = Reference2BooleanArrayMap.this.size;

            while (this.pos < max) {
               action.accept(Reference2BooleanArrayMap.this.value[this.pos++]);
            }
         }
      }
   }
}
