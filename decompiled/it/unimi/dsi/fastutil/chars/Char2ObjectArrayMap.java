package it.unimi.dsi.fastutil.chars;

import it.unimi.dsi.fastutil.objects.AbstractObjectCollection;
import it.unimi.dsi.fastutil.objects.AbstractObjectSet;
import it.unimi.dsi.fastutil.objects.ObjectArrays;
import it.unimi.dsi.fastutil.objects.ObjectCollection;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectSpliterator;
import it.unimi.dsi.fastutil.objects.ObjectSpliterators;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Map.Entry;
import java.util.function.Consumer;

public class Char2ObjectArrayMap<V> extends AbstractChar2ObjectMap<V> implements Serializable, Cloneable {
   private static final long serialVersionUID = 1L;
   protected transient char[] key;
   protected transient Object[] value;
   protected int size;
   protected transient Char2ObjectMap.FastEntrySet<V> entries;
   protected transient CharSet keys;
   protected transient ObjectCollection<V> values;

   public Char2ObjectArrayMap(char[] key, Object[] value) {
      this.key = key;
      this.value = value;
      this.size = key.length;
      if (key.length != value.length) {
         throw new IllegalArgumentException("Keys and values have different lengths (" + key.length + ", " + value.length + ")");
      }
   }

   public Char2ObjectArrayMap() {
      this.key = CharArrays.EMPTY_ARRAY;
      this.value = ObjectArrays.EMPTY_ARRAY;
   }

   public Char2ObjectArrayMap(int capacity) {
      this.key = new char[capacity];
      this.value = new Object[capacity];
   }

   public Char2ObjectArrayMap(Char2ObjectMap<V> m) {
      this(m.size());
      int i = 0;

      for (Char2ObjectMap.Entry<V> e : m.char2ObjectEntrySet()) {
         this.key[i] = e.getCharKey();
         this.value[i] = e.getValue();
         i++;
      }

      this.size = i;
   }

   public Char2ObjectArrayMap(Map<? extends Character, ? extends V> m) {
      this(m.size());
      int i = 0;

      for (Entry<? extends Character, ? extends V> e : m.entrySet()) {
         this.key[i] = e.getKey();
         this.value[i] = e.getValue();
         i++;
      }

      this.size = i;
   }

   public Char2ObjectArrayMap(char[] key, Object[] value, int size) {
      this.key = key;
      this.value = value;
      this.size = size;
      if (key.length != value.length) {
         throw new IllegalArgumentException("Keys and values have different lengths (" + key.length + ", " + value.length + ")");
      } else if (size > key.length) {
         throw new IllegalArgumentException("The provided size (" + size + ") is larger than or equal to the backing-arrays size (" + key.length + ")");
      }
   }

   public Char2ObjectMap.FastEntrySet<V> char2ObjectEntrySet() {
      if (this.entries == null) {
         this.entries = new Char2ObjectArrayMap.EntrySet();
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
   public V get(char k) {
      char[] key = this.key;
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
   public boolean containsKey(char k) {
      return this.findKey(k) != -1;
   }

   @Override
   public boolean containsValue(Object v) {
      int i = this.size;

      while (i-- != 0) {
         if (Objects.equals(this.value[i], v)) {
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
   public V put(char k, V v) {
      int oldKey = this.findKey(k);
      if (oldKey != -1) {
         V oldValue = (V)this.value[oldKey];
         this.value[oldKey] = v;
         return oldValue;
      } else {
         if (this.size == this.key.length) {
            char[] newKey = new char[this.size == 0 ? 2 : this.size * 2];
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
   public V remove(char k) {
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
   public CharSet keySet() {
      if (this.keys == null) {
         this.keys = new Char2ObjectArrayMap.KeySet();
      }

      return this.keys;
   }

   @Override
   public ObjectCollection<V> values() {
      if (this.values == null) {
         this.values = new Char2ObjectArrayMap.ValuesCollection();
      }

      return this.values;
   }

   public Char2ObjectArrayMap<V> clone() {
      Char2ObjectArrayMap<V> c;
      try {
         c = (Char2ObjectArrayMap<V>)super.clone();
      } catch (CloneNotSupportedException var3) {
         throw new InternalError();
      }

      c.key = (char[])this.key.clone();
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
         s.writeChar(this.key[i]);
         s.writeObject(this.value[i]);
      }
   }

   private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
      s.defaultReadObject();
      this.key = new char[this.size];
      this.value = new Object[this.size];

      for (int i = 0; i < this.size; i++) {
         this.key[i] = s.readChar();
         this.value[i] = s.readObject();
      }
   }

   private final class EntrySet extends AbstractObjectSet<Char2ObjectMap.Entry<V>> implements Char2ObjectMap.FastEntrySet<V> {
      private EntrySet() {
      }

      @Override
      public ObjectIterator<Char2ObjectMap.Entry<V>> iterator() {
         return new ObjectIterator<Char2ObjectMap.Entry<V>>() {
            int curr = -1;
            int next = 0;

            @Override
            public boolean hasNext() {
               return this.next < Char2ObjectArrayMap.this.size;
            }

            public Char2ObjectMap.Entry<V> next() {
               if (!this.hasNext()) {
                  throw new NoSuchElementException();
               } else {
                  return new AbstractChar2ObjectMap.BasicEntry<>(
                     Char2ObjectArrayMap.this.key[this.curr = this.next], (V)Char2ObjectArrayMap.this.value[this.next++]
                  );
               }
            }

            @Override
            public void remove() {
               if (this.curr == -1) {
                  throw new IllegalStateException();
               } else {
                  this.curr = -1;
                  int tail = Char2ObjectArrayMap.this.size-- - this.next--;
                  System.arraycopy(Char2ObjectArrayMap.this.key, this.next + 1, Char2ObjectArrayMap.this.key, this.next, tail);
                  System.arraycopy(Char2ObjectArrayMap.this.value, this.next + 1, Char2ObjectArrayMap.this.value, this.next, tail);
                  Char2ObjectArrayMap.this.value[Char2ObjectArrayMap.this.size] = null;
               }
            }

            @Override
            public void forEachRemaining(Consumer<? super Char2ObjectMap.Entry<V>> action) {
               int max = Char2ObjectArrayMap.this.size;

               while (this.next < max) {
                  action.accept(
                     new AbstractChar2ObjectMap.BasicEntry<>(
                        Char2ObjectArrayMap.this.key[this.curr = this.next], (V)Char2ObjectArrayMap.this.value[this.next++]
                     )
                  );
               }
            }
         };
      }

      @Override
      public ObjectIterator<Char2ObjectMap.Entry<V>> fastIterator() {
         return new ObjectIterator<Char2ObjectMap.Entry<V>>() {
            int next = 0;
            int curr = -1;
            final AbstractChar2ObjectMap.BasicEntry<V> entry = new AbstractChar2ObjectMap.BasicEntry<>();

            @Override
            public boolean hasNext() {
               return this.next < Char2ObjectArrayMap.this.size;
            }

            public Char2ObjectMap.Entry<V> next() {
               if (!this.hasNext()) {
                  throw new NoSuchElementException();
               } else {
                  this.entry.key = Char2ObjectArrayMap.this.key[this.curr = this.next];
                  this.entry.value = (V)Char2ObjectArrayMap.this.value[this.next++];
                  return this.entry;
               }
            }

            @Override
            public void remove() {
               if (this.curr == -1) {
                  throw new IllegalStateException();
               } else {
                  this.curr = -1;
                  int tail = Char2ObjectArrayMap.this.size-- - this.next--;
                  System.arraycopy(Char2ObjectArrayMap.this.key, this.next + 1, Char2ObjectArrayMap.this.key, this.next, tail);
                  System.arraycopy(Char2ObjectArrayMap.this.value, this.next + 1, Char2ObjectArrayMap.this.value, this.next, tail);
                  Char2ObjectArrayMap.this.value[Char2ObjectArrayMap.this.size] = null;
               }
            }

            @Override
            public void forEachRemaining(Consumer<? super Char2ObjectMap.Entry<V>> action) {
               int max = Char2ObjectArrayMap.this.size;

               while (this.next < max) {
                  this.entry.key = Char2ObjectArrayMap.this.key[this.curr = this.next];
                  this.entry.value = (V)Char2ObjectArrayMap.this.value[this.next++];
                  action.accept(this.entry);
               }
            }
         };
      }

      @Override
      public ObjectSpliterator<Char2ObjectMap.Entry<V>> spliterator() {
         return new Char2ObjectArrayMap.EntrySet.EntrySetSpliterator(0, Char2ObjectArrayMap.this.size);
      }

      @Override
      public void forEach(Consumer<? super Char2ObjectMap.Entry<V>> action) {
         int i = 0;

         for (int max = Char2ObjectArrayMap.this.size; i < max; i++) {
            action.accept(new AbstractChar2ObjectMap.BasicEntry<>(Char2ObjectArrayMap.this.key[i], (V)Char2ObjectArrayMap.this.value[i]));
         }
      }

      @Override
      public void fastForEach(Consumer<? super Char2ObjectMap.Entry<V>> action) {
         AbstractChar2ObjectMap.BasicEntry<V> entry = new AbstractChar2ObjectMap.BasicEntry<>();
         int i = 0;

         for (int max = Char2ObjectArrayMap.this.size; i < max; i++) {
            entry.key = Char2ObjectArrayMap.this.key[i];
            entry.value = (V)Char2ObjectArrayMap.this.value[i];
            action.accept(entry);
         }
      }

      @Override
      public int size() {
         return Char2ObjectArrayMap.this.size;
      }

      @Override
      public boolean contains(Object o) {
         if (!(o instanceof Entry)) {
            return false;
         } else {
            Entry<?, ?> e = (Entry<?, ?>)o;
            if (e.getKey() != null && e.getKey() instanceof Character) {
               char k = (Character)e.getKey();
               return Char2ObjectArrayMap.this.containsKey(k) && Objects.equals(Char2ObjectArrayMap.this.get(k), e.getValue());
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
            if (e.getKey() != null && e.getKey() instanceof Character) {
               char k = (Character)e.getKey();
               V v = (V)e.getValue();
               int oldPos = Char2ObjectArrayMap.this.findKey(k);
               if (oldPos != -1 && Objects.equals(v, Char2ObjectArrayMap.this.value[oldPos])) {
                  int tail = Char2ObjectArrayMap.this.size - oldPos - 1;
                  System.arraycopy(Char2ObjectArrayMap.this.key, oldPos + 1, Char2ObjectArrayMap.this.key, oldPos, tail);
                  System.arraycopy(Char2ObjectArrayMap.this.value, oldPos + 1, Char2ObjectArrayMap.this.value, oldPos, tail);
                  Char2ObjectArrayMap.this.size--;
                  Char2ObjectArrayMap.this.value[Char2ObjectArrayMap.this.size] = null;
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
         extends ObjectSpliterators.EarlyBindingSizeIndexBasedSpliterator<Char2ObjectMap.Entry<V>>
         implements ObjectSpliterator<Char2ObjectMap.Entry<V>> {
         EntrySetSpliterator(int pos, int maxPos) {
            super(pos, maxPos);
         }

         @Override
         public int characteristics() {
            return 16465;
         }

         protected final Char2ObjectMap.Entry<V> get(int location) {
            return new AbstractChar2ObjectMap.BasicEntry<>(Char2ObjectArrayMap.this.key[location], (V)Char2ObjectArrayMap.this.value[location]);
         }

         protected final Char2ObjectArrayMap<V>.EntrySet.EntrySetSpliterator makeForSplit(int pos, int maxPos) {
            return EntrySet.this.new EntrySetSpliterator(pos, maxPos);
         }
      }
   }

   private final class KeySet extends AbstractCharSet {
      private KeySet() {
      }

      @Override
      public boolean contains(char k) {
         return Char2ObjectArrayMap.this.findKey(k) != -1;
      }

      @Override
      public boolean remove(char k) {
         int oldPos = Char2ObjectArrayMap.this.findKey(k);
         if (oldPos == -1) {
            return false;
         } else {
            int tail = Char2ObjectArrayMap.this.size - oldPos - 1;
            System.arraycopy(Char2ObjectArrayMap.this.key, oldPos + 1, Char2ObjectArrayMap.this.key, oldPos, tail);
            System.arraycopy(Char2ObjectArrayMap.this.value, oldPos + 1, Char2ObjectArrayMap.this.value, oldPos, tail);
            Char2ObjectArrayMap.this.size--;
            Char2ObjectArrayMap.this.value[Char2ObjectArrayMap.this.size] = null;
            return true;
         }
      }

      @Override
      public CharIterator iterator() {
         return new CharIterator() {
            int pos = 0;

            @Override
            public boolean hasNext() {
               return this.pos < Char2ObjectArrayMap.this.size;
            }

            @Override
            public char nextChar() {
               if (!this.hasNext()) {
                  throw new NoSuchElementException();
               } else {
                  return Char2ObjectArrayMap.this.key[this.pos++];
               }
            }

            @Override
            public void remove() {
               if (this.pos == 0) {
                  throw new IllegalStateException();
               } else {
                  int tail = Char2ObjectArrayMap.this.size - this.pos;
                  System.arraycopy(Char2ObjectArrayMap.this.key, this.pos, Char2ObjectArrayMap.this.key, this.pos - 1, tail);
                  System.arraycopy(Char2ObjectArrayMap.this.value, this.pos, Char2ObjectArrayMap.this.value, this.pos - 1, tail);
                  Char2ObjectArrayMap.this.size--;
                  this.pos--;
                  Char2ObjectArrayMap.this.value[Char2ObjectArrayMap.this.size] = null;
               }
            }

            @Override
            public void forEachRemaining(CharConsumer action) {
               int max = Char2ObjectArrayMap.this.size;

               while (this.pos < max) {
                  action.accept(Char2ObjectArrayMap.this.key[this.pos++]);
               }
            }
         };
      }

      @Override
      public CharSpliterator spliterator() {
         return new Char2ObjectArrayMap.KeySet.KeySetSpliterator(0, Char2ObjectArrayMap.this.size);
      }

      @Override
      public void forEach(CharConsumer action) {
         int i = 0;

         for (int max = Char2ObjectArrayMap.this.size; i < max; i++) {
            action.accept(Char2ObjectArrayMap.this.key[i]);
         }
      }

      @Override
      public int size() {
         return Char2ObjectArrayMap.this.size;
      }

      @Override
      public void clear() {
         Char2ObjectArrayMap.this.clear();
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
            return Char2ObjectArrayMap.this.key[location];
         }

         protected final Char2ObjectArrayMap<V>.KeySet.KeySetSpliterator makeForSplit(int pos, int maxPos) {
            return KeySet.this.new KeySetSpliterator(pos, maxPos);
         }

         @Override
         public void forEachRemaining(CharConsumer action) {
            int max = Char2ObjectArrayMap.this.size;

            while (this.pos < max) {
               action.accept(Char2ObjectArrayMap.this.key[this.pos++]);
            }
         }
      }
   }

   private final class ValuesCollection extends AbstractObjectCollection<V> {
      private ValuesCollection() {
      }

      @Override
      public boolean contains(Object v) {
         return Char2ObjectArrayMap.this.containsValue(v);
      }

      @Override
      public ObjectIterator<V> iterator() {
         return new ObjectIterator<V>() {
            int pos = 0;

            @Override
            public boolean hasNext() {
               return this.pos < Char2ObjectArrayMap.this.size;
            }

            @Override
            public V next() {
               if (!this.hasNext()) {
                  throw new NoSuchElementException();
               } else {
                  return (V)Char2ObjectArrayMap.this.value[this.pos++];
               }
            }

            @Override
            public void remove() {
               if (this.pos == 0) {
                  throw new IllegalStateException();
               } else {
                  int tail = Char2ObjectArrayMap.this.size - this.pos;
                  System.arraycopy(Char2ObjectArrayMap.this.key, this.pos, Char2ObjectArrayMap.this.key, this.pos - 1, tail);
                  System.arraycopy(Char2ObjectArrayMap.this.value, this.pos, Char2ObjectArrayMap.this.value, this.pos - 1, tail);
                  Char2ObjectArrayMap.this.size--;
                  this.pos--;
                  Char2ObjectArrayMap.this.value[Char2ObjectArrayMap.this.size] = null;
               }
            }

            @Override
            public void forEachRemaining(Consumer<? super V> action) {
               int max = Char2ObjectArrayMap.this.size;

               while (this.pos < max) {
                  action.accept((V)Char2ObjectArrayMap.this.value[this.pos++]);
               }
            }
         };
      }

      @Override
      public ObjectSpliterator<V> spliterator() {
         return new Char2ObjectArrayMap.ValuesCollection.ValuesSpliterator(0, Char2ObjectArrayMap.this.size);
      }

      @Override
      public void forEach(Consumer<? super V> action) {
         int i = 0;

         for (int max = Char2ObjectArrayMap.this.size; i < max; i++) {
            action.accept((V)Char2ObjectArrayMap.this.value[i]);
         }
      }

      @Override
      public int size() {
         return Char2ObjectArrayMap.this.size;
      }

      @Override
      public void clear() {
         Char2ObjectArrayMap.this.clear();
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
            return (V)Char2ObjectArrayMap.this.value[location];
         }

         protected final Char2ObjectArrayMap<V>.ValuesCollection.ValuesSpliterator makeForSplit(int pos, int maxPos) {
            return ValuesCollection.this.new ValuesSpliterator(pos, maxPos);
         }

         @Override
         public void forEachRemaining(Consumer<? super V> action) {
            int max = Char2ObjectArrayMap.this.size;

            while (this.pos < max) {
               action.accept((V)Char2ObjectArrayMap.this.value[this.pos++]);
            }
         }
      }
   }
}
