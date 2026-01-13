package it.unimi.dsi.fastutil.bytes;

import it.unimi.dsi.fastutil.Size64;
import it.unimi.dsi.fastutil.objects.AbstractObjectSet;
import it.unimi.dsi.fastutil.objects.AbstractReferenceCollection;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectSpliterator;
import it.unimi.dsi.fastutil.objects.ObjectSpliterators;
import it.unimi.dsi.fastutil.objects.ReferenceCollection;
import java.io.Serializable;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Consumer;

public abstract class AbstractByte2ReferenceMap<V> extends AbstractByte2ReferenceFunction<V> implements Byte2ReferenceMap<V>, Serializable {
   private static final long serialVersionUID = -4940583368468432370L;

   protected AbstractByte2ReferenceMap() {
   }

   @Override
   public boolean containsKey(byte k) {
      ObjectIterator<Byte2ReferenceMap.Entry<V>> i = this.byte2ReferenceEntrySet().iterator();

      while (i.hasNext()) {
         if (i.next().getByteKey() == k) {
            return true;
         }
      }

      return false;
   }

   @Override
   public boolean containsValue(Object v) {
      ObjectIterator<Byte2ReferenceMap.Entry<V>> i = this.byte2ReferenceEntrySet().iterator();

      while (i.hasNext()) {
         if (i.next().getValue() == v) {
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
   public ByteSet keySet() {
      return new AbstractByteSet() {
         @Override
         public boolean contains(byte k) {
            return AbstractByte2ReferenceMap.this.containsKey(k);
         }

         @Override
         public int size() {
            return AbstractByte2ReferenceMap.this.size();
         }

         @Override
         public void clear() {
            AbstractByte2ReferenceMap.this.clear();
         }

         @Override
         public ByteIterator iterator() {
            return new ByteIterator() {
               private final ObjectIterator<Byte2ReferenceMap.Entry<V>> i = Byte2ReferenceMaps.fastIterator(AbstractByte2ReferenceMap.this);

               @Override
               public byte nextByte() {
                  return this.i.next().getByteKey();
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
                  this.i.forEachRemaining(entry -> action.accept(entry.getByteKey()));
               }
            };
         }

         @Override
         public ByteSpliterator spliterator() {
            return ByteSpliterators.asSpliterator(this.iterator(), Size64.sizeOf(AbstractByte2ReferenceMap.this), 321);
         }
      };
   }

   @Override
   public ReferenceCollection<V> values() {
      return new AbstractReferenceCollection<V>() {
         @Override
         public boolean contains(Object k) {
            return AbstractByte2ReferenceMap.this.containsValue(k);
         }

         @Override
         public int size() {
            return AbstractByte2ReferenceMap.this.size();
         }

         @Override
         public void clear() {
            AbstractByte2ReferenceMap.this.clear();
         }

         @Override
         public ObjectIterator<V> iterator() {
            return new ObjectIterator<V>() {
               private final ObjectIterator<Byte2ReferenceMap.Entry<V>> i = Byte2ReferenceMaps.fastIterator(AbstractByte2ReferenceMap.this);

               @Override
               public V next() {
                  return this.i.next().getValue();
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
               public void forEachRemaining(Consumer<? super V> action) {
                  this.i.forEachRemaining(entry -> action.accept(entry.getValue()));
               }
            };
         }

         @Override
         public ObjectSpliterator<V> spliterator() {
            return ObjectSpliterators.asSpliterator(this.iterator(), Size64.sizeOf(AbstractByte2ReferenceMap.this), 64);
         }
      };
   }

   @Override
   public void putAll(Map<? extends Byte, ? extends V> m) {
      if (m instanceof Byte2ReferenceMap) {
         ObjectIterator<Byte2ReferenceMap.Entry<V>> i = Byte2ReferenceMaps.fastIterator((Byte2ReferenceMap<V>)m);

         while (i.hasNext()) {
            Byte2ReferenceMap.Entry<? extends V> e = i.next();
            this.put(e.getByteKey(), (V)e.getValue());
         }
      } else {
         int n = m.size();
         Iterator<? extends Entry<? extends Byte, ? extends V>> i = m.entrySet().iterator();

         while (n-- != 0) {
            Entry<? extends Byte, ? extends V> e = (Entry<? extends Byte, ? extends V>)i.next();
            this.put(e.getKey(), (V)e.getValue());
         }
      }
   }

   @Override
   public int hashCode() {
      int h = 0;
      int n = this.size();
      ObjectIterator<Byte2ReferenceMap.Entry<V>> i = Byte2ReferenceMaps.fastIterator(this);

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
         return m.size() != this.size() ? false : this.byte2ReferenceEntrySet().containsAll(m.entrySet());
      }
   }

   @Override
   public String toString() {
      StringBuilder s = new StringBuilder();
      ObjectIterator<Byte2ReferenceMap.Entry<V>> i = Byte2ReferenceMaps.fastIterator(this);
      int n = this.size();
      boolean first = true;
      s.append("{");

      while (n-- != 0) {
         if (first) {
            first = false;
         } else {
            s.append(", ");
         }

         Byte2ReferenceMap.Entry<V> e = i.next();
         s.append(String.valueOf(e.getByteKey()));
         s.append("=>");
         if (this == e.getValue()) {
            s.append("(this map)");
         } else {
            s.append(String.valueOf(e.getValue()));
         }
      }

      s.append("}");
      return s.toString();
   }

   public static class BasicEntry<V> implements Byte2ReferenceMap.Entry<V> {
      protected byte key;
      protected V value;

      public BasicEntry() {
      }

      public BasicEntry(Byte key, V value) {
         this.key = key;
         this.value = value;
      }

      public BasicEntry(byte key, V value) {
         this.key = key;
         this.value = value;
      }

      @Override
      public byte getByteKey() {
         return this.key;
      }

      @Override
      public V getValue() {
         return this.value;
      }

      @Override
      public V setValue(V value) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean equals(Object o) {
         if (!(o instanceof Entry)) {
            return false;
         } else if (o instanceof Byte2ReferenceMap.Entry) {
            Byte2ReferenceMap.Entry<V> e = (Byte2ReferenceMap.Entry<V>)o;
            return this.key == e.getByteKey() && this.value == e.getValue();
         } else {
            Entry<?, ?> e = (Entry<?, ?>)o;
            Object key = e.getKey();
            if (key != null && key instanceof Byte) {
               Object value = e.getValue();
               return this.key == (Byte)key && this.value == value;
            } else {
               return false;
            }
         }
      }

      @Override
      public int hashCode() {
         return this.key ^ (this.value == null ? 0 : System.identityHashCode(this.value));
      }

      @Override
      public String toString() {
         return this.key + "->" + this.value;
      }
   }

   public abstract static class BasicEntrySet<V> extends AbstractObjectSet<Byte2ReferenceMap.Entry<V>> {
      protected final Byte2ReferenceMap<V> map;

      public BasicEntrySet(Byte2ReferenceMap<V> map) {
         this.map = map;
      }

      @Override
      public boolean contains(Object o) {
         if (!(o instanceof Entry)) {
            return false;
         } else if (o instanceof Byte2ReferenceMap.Entry) {
            Byte2ReferenceMap.Entry<V> e = (Byte2ReferenceMap.Entry<V>)o;
            byte k = e.getByteKey();
            return this.map.containsKey(k) && this.map.get(k) == e.getValue();
         } else {
            Entry<?, ?> e = (Entry<?, ?>)o;
            Object key = e.getKey();
            if (key != null && key instanceof Byte) {
               byte k = (Byte)key;
               Object value = e.getValue();
               return this.map.containsKey(k) && this.map.get(k) == value;
            } else {
               return false;
            }
         }
      }

      @Override
      public boolean remove(Object o) {
         if (!(o instanceof Entry)) {
            return false;
         } else if (o instanceof Byte2ReferenceMap.Entry) {
            Byte2ReferenceMap.Entry<V> e = (Byte2ReferenceMap.Entry<V>)o;
            return this.map.remove(e.getByteKey(), e.getValue());
         } else {
            Entry<?, ?> e = (Entry<?, ?>)o;
            Object key = e.getKey();
            if (key != null && key instanceof Byte) {
               byte k = (Byte)key;
               Object v = e.getValue();
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
      public ObjectSpliterator<Byte2ReferenceMap.Entry<V>> spliterator() {
         return ObjectSpliterators.asSpliterator(this.iterator(), Size64.sizeOf(this.map), 65);
      }
   }
}
