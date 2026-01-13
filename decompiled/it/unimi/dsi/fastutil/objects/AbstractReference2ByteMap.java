package it.unimi.dsi.fastutil.objects;

import it.unimi.dsi.fastutil.Size64;
import it.unimi.dsi.fastutil.bytes.AbstractByteCollection;
import it.unimi.dsi.fastutil.bytes.ByteCollection;
import it.unimi.dsi.fastutil.bytes.ByteConsumer;
import it.unimi.dsi.fastutil.bytes.ByteIterator;
import it.unimi.dsi.fastutil.bytes.ByteSpliterator;
import it.unimi.dsi.fastutil.bytes.ByteSpliterators;
import java.io.Serializable;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Consumer;

public abstract class AbstractReference2ByteMap<K> extends AbstractReference2ByteFunction<K> implements Reference2ByteMap<K>, Serializable {
   private static final long serialVersionUID = -4940583368468432370L;

   protected AbstractReference2ByteMap() {
   }

   @Override
   public boolean containsKey(Object k) {
      ObjectIterator<Reference2ByteMap.Entry<K>> i = this.reference2ByteEntrySet().iterator();

      while (i.hasNext()) {
         if (i.next().getKey() == k) {
            return true;
         }
      }

      return false;
   }

   @Override
   public boolean containsValue(byte v) {
      ObjectIterator<Reference2ByteMap.Entry<K>> i = this.reference2ByteEntrySet().iterator();

      while (i.hasNext()) {
         if (i.next().getByteValue() == v) {
            return true;
         }
      }

      return false;
   }

   @Override
   public boolean isEmpty() {
      return this.size() == 0;
   }

   @Override
   public ReferenceSet<K> keySet() {
      return new AbstractReferenceSet<K>() {
         @Override
         public boolean contains(Object k) {
            return AbstractReference2ByteMap.this.containsKey(k);
         }

         @Override
         public int size() {
            return AbstractReference2ByteMap.this.size();
         }

         @Override
         public void clear() {
            AbstractReference2ByteMap.this.clear();
         }

         @Override
         public ObjectIterator<K> iterator() {
            return new ObjectIterator<K>() {
               private final ObjectIterator<Reference2ByteMap.Entry<K>> i = Reference2ByteMaps.fastIterator(AbstractReference2ByteMap.this);

               @Override
               public K next() {
                  return this.i.next().getKey();
               }

               @Override
               public boolean hasNext() {
                  return this.i.hasNext();
               }

               @Override
               public void remove() {
                  this.i.remove();
               }

               @Override
               public void forEachRemaining(Consumer<? super K> action) {
                  this.i.forEachRemaining(entry -> action.accept(entry.getKey()));
               }
            };
         }

         @Override
         public ObjectSpliterator<K> spliterator() {
            return ObjectSpliterators.asSpliterator(this.iterator(), Size64.sizeOf(AbstractReference2ByteMap.this), 65);
         }
      };
   }

   @Override
   public ByteCollection values() {
      return new AbstractByteCollection() {
         @Override
         public boolean contains(byte k) {
            return AbstractReference2ByteMap.this.containsValue(k);
         }

         @Override
         public int size() {
            return AbstractReference2ByteMap.this.size();
         }

         @Override
         public void clear() {
            AbstractReference2ByteMap.this.clear();
         }

         @Override
         public ByteIterator iterator() {
            return new ByteIterator() {
               private final ObjectIterator<Reference2ByteMap.Entry<K>> i = Reference2ByteMaps.fastIterator(AbstractReference2ByteMap.this);

               @Override
               public byte nextByte() {
                  return this.i.next().getByteValue();
               }

               @Override
               public boolean hasNext() {
                  return this.i.hasNext();
               }

               @Override
               public void remove() {
                  this.i.remove();
               }

               @Override
               public void forEachRemaining(ByteConsumer action) {
                  this.i.forEachRemaining(entry -> action.accept(entry.getByteValue()));
               }
            };
         }

         @Override
         public ByteSpliterator spliterator() {
            return ByteSpliterators.asSpliterator(this.iterator(), Size64.sizeOf(AbstractReference2ByteMap.this), 320);
         }
      };
   }

   @Override
   public void putAll(Map<? extends K, ? extends Byte> m) {
      if (m instanceof Reference2ByteMap) {
         ObjectIterator<Reference2ByteMap.Entry<K>> i = Reference2ByteMaps.fastIterator((Reference2ByteMap<K>)m);

         while (i.hasNext()) {
            Reference2ByteMap.Entry<? extends K> e = i.next();
            this.put((K)e.getKey(), e.getByteValue());
         }
      } else {
         int n = m.size();
         Iterator<? extends Entry<? extends K, ? extends Byte>> i = m.entrySet().iterator();

         while (n-- != 0) {
            Entry<? extends K, ? extends Byte> e = (Entry<? extends K, ? extends Byte>)i.next();
            this.put((K)e.getKey(), e.getValue());
         }
      }
   }

   @Override
   public int hashCode() {
      int h = 0;
      int n = this.size();
      ObjectIterator<Reference2ByteMap.Entry<K>> i = Reference2ByteMaps.fastIterator(this);

      while (n-- != 0) {
         h += i.next().hashCode();
      }

      return h;
   }

   @Override
   public boolean equals(Object o) {
      if (o == this) {
         return true;
      } else if (!(o instanceof Map)) {
         return false;
      } else {
         Map<?, ?> m = (Map<?, ?>)o;
         return m.size() != this.size() ? false : this.reference2ByteEntrySet().containsAll(m.entrySet());
      }
   }

   @Override
   public String toString() {
      StringBuilder s = new StringBuilder();
      ObjectIterator<Reference2ByteMap.Entry<K>> i = Reference2ByteMaps.fastIterator(this);
      int n = this.size();
      boolean first = true;
      s.append("{");

      while (n-- != 0) {
         if (first) {
            first = false;
         } else {
            s.append(", ");
         }

         Reference2ByteMap.Entry<K> e = i.next();
         if (this == e.getKey()) {
            s.append("(this map)");
         } else {
            s.append(String.valueOf(e.getKey()));
         }

         s.append("=>");
         s.append(String.valueOf(e.getByteValue()));
      }

      s.append("}");
      return s.toString();
   }

   public static class BasicEntry<K> implements Reference2ByteMap.Entry<K> {
      protected K key;
      protected byte value;

      public BasicEntry() {
      }

      public BasicEntry(K key, Byte value) {
         this.key = key;
         this.value = value;
      }

      public BasicEntry(K key, byte value) {
         this.key = key;
         this.value = value;
      }

      @Override
      public K getKey() {
         return this.key;
      }

      @Override
      public byte getByteValue() {
         return this.value;
      }

      @Override
      public byte setValue(byte value) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean equals(Object o) {
         if (!(o instanceof Entry)) {
            return false;
         } else if (o instanceof Reference2ByteMap.Entry) {
            Reference2ByteMap.Entry<K> e = (Reference2ByteMap.Entry<K>)o;
            return this.key == e.getKey() && this.value == e.getByteValue();
         } else {
            Entry<?, ?> e = (Entry<?, ?>)o;
            Object key = e.getKey();
            Object value = e.getValue();
            return value != null && value instanceof Byte ? this.key == key && this.value == (Byte)value : false;
         }
      }

      @Override
      public int hashCode() {
         return System.identityHashCode(this.key) ^ this.value;
      }

      @Override
      public String toString() {
         return this.key + "->" + this.value;
      }
   }

   public abstract static class BasicEntrySet<K> extends AbstractObjectSet<Reference2ByteMap.Entry<K>> {
      protected final Reference2ByteMap<K> map;

      public BasicEntrySet(Reference2ByteMap<K> map) {
         this.map = map;
      }

      @Override
      public boolean contains(Object o) {
         if (!(o instanceof Entry)) {
            return false;
         } else if (o instanceof Reference2ByteMap.Entry) {
            Reference2ByteMap.Entry<K> e = (Reference2ByteMap.Entry<K>)o;
            K k = e.getKey();
            return this.map.containsKey(k) && this.map.getByte(k) == e.getByteValue();
         } else {
            Entry<?, ?> e = (Entry<?, ?>)o;
            Object k = e.getKey();
            Object value = e.getValue();
            return value != null && value instanceof Byte ? this.map.containsKey(k) && this.map.getByte(k) == (Byte)value : false;
         }
      }

      @Override
      public boolean remove(Object o) {
         if (!(o instanceof Entry)) {
            return false;
         } else if (o instanceof Reference2ByteMap.Entry) {
            Reference2ByteMap.Entry<K> e = (Reference2ByteMap.Entry<K>)o;
            return this.map.remove(e.getKey(), e.getByteValue());
         } else {
            Entry<?, ?> e = (Entry<?, ?>)o;
            Object k = e.getKey();
            Object value = e.getValue();
            if (value != null && value instanceof Byte) {
               byte v = (Byte)value;
               return this.map.remove(k, v);
            } else {
               return false;
            }
         }
      }

      @Override
      public int size() {
         return this.map.size();
      }

      @Override
      public ObjectSpliterator<Reference2ByteMap.Entry<K>> spliterator() {
         return ObjectSpliterators.asSpliterator(this.iterator(), Size64.sizeOf(this.map), 65);
      }
   }
}
