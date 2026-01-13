package it.unimi.dsi.fastutil.objects;

import it.unimi.dsi.fastutil.Size64;
import it.unimi.dsi.fastutil.shorts.AbstractShortCollection;
import it.unimi.dsi.fastutil.shorts.ShortCollection;
import it.unimi.dsi.fastutil.shorts.ShortConsumer;
import it.unimi.dsi.fastutil.shorts.ShortIterator;
import it.unimi.dsi.fastutil.shorts.ShortSpliterator;
import it.unimi.dsi.fastutil.shorts.ShortSpliterators;
import java.io.Serializable;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Map.Entry;
import java.util.function.Consumer;

public abstract class AbstractObject2ShortMap<K> extends AbstractObject2ShortFunction<K> implements Object2ShortMap<K>, Serializable {
   private static final long serialVersionUID = -4940583368468432370L;

   protected AbstractObject2ShortMap() {
   }

   @Override
   public boolean containsKey(Object k) {
      ObjectIterator<Object2ShortMap.Entry<K>> i = this.object2ShortEntrySet().iterator();

      while (i.hasNext()) {
         if (i.next().getKey() == k) {
            return true;
         }
      }

      return false;
   }

   @Override
   public boolean containsValue(short v) {
      ObjectIterator<Object2ShortMap.Entry<K>> i = this.object2ShortEntrySet().iterator();

      while (i.hasNext()) {
         if (i.next().getShortValue() == v) {
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
   public ObjectSet<K> keySet() {
      return new AbstractObjectSet<K>() {
         @Override
         public boolean contains(Object k) {
            return AbstractObject2ShortMap.this.containsKey(k);
         }

         @Override
         public int size() {
            return AbstractObject2ShortMap.this.size();
         }

         @Override
         public void clear() {
            AbstractObject2ShortMap.this.clear();
         }

         @Override
         public ObjectIterator<K> iterator() {
            return new ObjectIterator<K>() {
               private final ObjectIterator<Object2ShortMap.Entry<K>> i = Object2ShortMaps.fastIterator(AbstractObject2ShortMap.this);

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
            return ObjectSpliterators.asSpliterator(this.iterator(), Size64.sizeOf(AbstractObject2ShortMap.this), 65);
         }
      };
   }

   @Override
   public ShortCollection values() {
      return new AbstractShortCollection() {
         @Override
         public boolean contains(short k) {
            return AbstractObject2ShortMap.this.containsValue(k);
         }

         @Override
         public int size() {
            return AbstractObject2ShortMap.this.size();
         }

         @Override
         public void clear() {
            AbstractObject2ShortMap.this.clear();
         }

         @Override
         public ShortIterator iterator() {
            return new ShortIterator() {
               private final ObjectIterator<Object2ShortMap.Entry<K>> i = Object2ShortMaps.fastIterator(AbstractObject2ShortMap.this);

               @Override
               public short nextShort() {
                  return this.i.next().getShortValue();
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
               public void forEachRemaining(ShortConsumer action) {
                  this.i.forEachRemaining(entry -> action.accept(entry.getShortValue()));
               }
            };
         }

         @Override
         public ShortSpliterator spliterator() {
            return ShortSpliterators.asSpliterator(this.iterator(), Size64.sizeOf(AbstractObject2ShortMap.this), 320);
         }
      };
   }

   @Override
   public void putAll(Map<? extends K, ? extends Short> m) {
      if (m instanceof Object2ShortMap) {
         ObjectIterator<Object2ShortMap.Entry<K>> i = Object2ShortMaps.fastIterator((Object2ShortMap<K>)m);

         while (i.hasNext()) {
            Object2ShortMap.Entry<? extends K> e = i.next();
            this.put((K)e.getKey(), e.getShortValue());
         }
      } else {
         int n = m.size();
         Iterator<? extends Entry<? extends K, ? extends Short>> i = m.entrySet().iterator();

         while (n-- != 0) {
            Entry<? extends K, ? extends Short> e = (Entry<? extends K, ? extends Short>)i.next();
            this.put((K)e.getKey(), e.getValue());
         }
      }
   }

   @Override
   public int hashCode() {
      int h = 0;
      int n = this.size();
      ObjectIterator<Object2ShortMap.Entry<K>> i = Object2ShortMaps.fastIterator(this);

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
         return m.size() != this.size() ? false : this.object2ShortEntrySet().containsAll(m.entrySet());
      }
   }

   @Override
   public String toString() {
      StringBuilder s = new StringBuilder();
      ObjectIterator<Object2ShortMap.Entry<K>> i = Object2ShortMaps.fastIterator(this);
      int n = this.size();
      boolean first = true;
      s.append("{");

      while (n-- != 0) {
         if (first) {
            first = false;
         } else {
            s.append(", ");
         }

         Object2ShortMap.Entry<K> e = i.next();
         if (this == e.getKey()) {
            s.append("(this map)");
         } else {
            s.append(String.valueOf(e.getKey()));
         }

         s.append("=>");
         s.append(String.valueOf(e.getShortValue()));
      }

      s.append("}");
      return s.toString();
   }

   public static class BasicEntry<K> implements Object2ShortMap.Entry<K> {
      protected K key;
      protected short value;

      public BasicEntry() {
      }

      public BasicEntry(K key, Short value) {
         this.key = key;
         this.value = value;
      }

      public BasicEntry(K key, short value) {
         this.key = key;
         this.value = value;
      }

      @Override
      public K getKey() {
         return this.key;
      }

      @Override
      public short getShortValue() {
         return this.value;
      }

      @Override
      public short setValue(short value) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean equals(Object o) {
         if (!(o instanceof Entry)) {
            return false;
         } else if (o instanceof Object2ShortMap.Entry) {
            Object2ShortMap.Entry<K> e = (Object2ShortMap.Entry<K>)o;
            return Objects.equals(this.key, e.getKey()) && this.value == e.getShortValue();
         } else {
            Entry<?, ?> e = (Entry<?, ?>)o;
            Object key = e.getKey();
            Object value = e.getValue();
            return value != null && value instanceof Short ? Objects.equals(this.key, key) && this.value == (Short)value : false;
         }
      }

      @Override
      public int hashCode() {
         return (this.key == null ? 0 : this.key.hashCode()) ^ this.value;
      }

      @Override
      public String toString() {
         return this.key + "->" + this.value;
      }
   }

   public abstract static class BasicEntrySet<K> extends AbstractObjectSet<Object2ShortMap.Entry<K>> {
      protected final Object2ShortMap<K> map;

      public BasicEntrySet(Object2ShortMap<K> map) {
         this.map = map;
      }

      @Override
      public boolean contains(Object o) {
         if (!(o instanceof Entry)) {
            return false;
         } else if (o instanceof Object2ShortMap.Entry) {
            Object2ShortMap.Entry<K> e = (Object2ShortMap.Entry<K>)o;
            K k = e.getKey();
            return this.map.containsKey(k) && this.map.getShort(k) == e.getShortValue();
         } else {
            Entry<?, ?> e = (Entry<?, ?>)o;
            Object k = e.getKey();
            Object value = e.getValue();
            return value != null && value instanceof Short ? this.map.containsKey(k) && this.map.getShort(k) == (Short)value : false;
         }
      }

      @Override
      public boolean remove(Object o) {
         if (!(o instanceof Entry)) {
            return false;
         } else if (o instanceof Object2ShortMap.Entry) {
            Object2ShortMap.Entry<K> e = (Object2ShortMap.Entry<K>)o;
            return this.map.remove(e.getKey(), e.getShortValue());
         } else {
            Entry<?, ?> e = (Entry<?, ?>)o;
            Object k = e.getKey();
            Object value = e.getValue();
            if (value != null && value instanceof Short) {
               short v = (Short)value;
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
      public ObjectSpliterator<Object2ShortMap.Entry<K>> spliterator() {
         return ObjectSpliterators.asSpliterator(this.iterator(), Size64.sizeOf(this.map), 65);
      }
   }
}
