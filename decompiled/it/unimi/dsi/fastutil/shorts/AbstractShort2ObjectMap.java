package it.unimi.dsi.fastutil.shorts;

import it.unimi.dsi.fastutil.Size64;
import it.unimi.dsi.fastutil.objects.AbstractObjectCollection;
import it.unimi.dsi.fastutil.objects.AbstractObjectSet;
import it.unimi.dsi.fastutil.objects.ObjectCollection;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectSpliterator;
import it.unimi.dsi.fastutil.objects.ObjectSpliterators;
import java.io.Serializable;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Map.Entry;
import java.util.function.Consumer;

public abstract class AbstractShort2ObjectMap<V> extends AbstractShort2ObjectFunction<V> implements Short2ObjectMap<V>, Serializable {
   private static final long serialVersionUID = -4940583368468432370L;

   protected AbstractShort2ObjectMap() {
   }

   @Override
   public boolean containsKey(short k) {
      ObjectIterator<Short2ObjectMap.Entry<V>> i = this.short2ObjectEntrySet().iterator();

      while (i.hasNext()) {
         if (i.next().getShortKey() == k) {
            return true;
         }
      }

      return false;
   }

   @Override
   public boolean containsValue(Object v) {
      ObjectIterator<Short2ObjectMap.Entry<V>> i = this.short2ObjectEntrySet().iterator();

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
   public ShortSet keySet() {
      return new AbstractShortSet() {
         @Override
         public boolean contains(short k) {
            return AbstractShort2ObjectMap.this.containsKey(k);
         }

         @Override
         public int size() {
            return AbstractShort2ObjectMap.this.size();
         }

         @Override
         public void clear() {
            AbstractShort2ObjectMap.this.clear();
         }

         @Override
         public ShortIterator iterator() {
            return new ShortIterator() {
               private final ObjectIterator<Short2ObjectMap.Entry<V>> i = Short2ObjectMaps.fastIterator(AbstractShort2ObjectMap.this);

               @Override
               public short nextShort() {
                  return this.i.next().getShortKey();
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
                  this.i.forEachRemaining(entry -> action.accept(entry.getShortKey()));
               }
            };
         }

         @Override
         public ShortSpliterator spliterator() {
            return ShortSpliterators.asSpliterator(this.iterator(), Size64.sizeOf(AbstractShort2ObjectMap.this), 321);
         }
      };
   }

   @Override
   public ObjectCollection<V> values() {
      return new AbstractObjectCollection<V>() {
         @Override
         public boolean contains(Object k) {
            return AbstractShort2ObjectMap.this.containsValue(k);
         }

         @Override
         public int size() {
            return AbstractShort2ObjectMap.this.size();
         }

         @Override
         public void clear() {
            AbstractShort2ObjectMap.this.clear();
         }

         @Override
         public ObjectIterator<V> iterator() {
            return new ObjectIterator<V>() {
               private final ObjectIterator<Short2ObjectMap.Entry<V>> i = Short2ObjectMaps.fastIterator(AbstractShort2ObjectMap.this);

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
            return ObjectSpliterators.asSpliterator(this.iterator(), Size64.sizeOf(AbstractShort2ObjectMap.this), 64);
         }
      };
   }

   @Override
   public void putAll(Map<? extends Short, ? extends V> m) {
      if (m instanceof Short2ObjectMap) {
         ObjectIterator<Short2ObjectMap.Entry<V>> i = Short2ObjectMaps.fastIterator((Short2ObjectMap<V>)m);

         while (i.hasNext()) {
            Short2ObjectMap.Entry<? extends V> e = i.next();
            this.put(e.getShortKey(), (V)e.getValue());
         }
      } else {
         int n = m.size();
         Iterator<? extends Entry<? extends Short, ? extends V>> i = m.entrySet().iterator();

         while (n-- != 0) {
            Entry<? extends Short, ? extends V> e = (Entry<? extends Short, ? extends V>)i.next();
            this.put(e.getKey(), (V)e.getValue());
         }
      }
   }

   @Override
   public int hashCode() {
      int h = 0;
      int n = this.size();
      ObjectIterator<Short2ObjectMap.Entry<V>> i = Short2ObjectMaps.fastIterator(this);

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
         return m.size() != this.size() ? false : this.short2ObjectEntrySet().containsAll(m.entrySet());
      }
   }

   @Override
   public String toString() {
      StringBuilder s = new StringBuilder();
      ObjectIterator<Short2ObjectMap.Entry<V>> i = Short2ObjectMaps.fastIterator(this);
      int n = this.size();
      boolean first = true;
      s.append("{");

      while (n-- != 0) {
         if (first) {
            first = false;
         } else {
            s.append(", ");
         }

         Short2ObjectMap.Entry<V> e = i.next();
         s.append(String.valueOf(e.getShortKey()));
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

   public static class BasicEntry<V> implements Short2ObjectMap.Entry<V> {
      protected short key;
      protected V value;

      public BasicEntry() {
      }

      public BasicEntry(Short key, V value) {
         this.key = key;
         this.value = value;
      }

      public BasicEntry(short key, V value) {
         this.key = key;
         this.value = value;
      }

      @Override
      public short getShortKey() {
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
         } else if (o instanceof Short2ObjectMap.Entry) {
            Short2ObjectMap.Entry<V> e = (Short2ObjectMap.Entry<V>)o;
            return this.key == e.getShortKey() && Objects.equals(this.value, e.getValue());
         } else {
            Entry<?, ?> e = (Entry<?, ?>)o;
            Object key = e.getKey();
            if (key != null && key instanceof Short) {
               Object value = e.getValue();
               return this.key == (Short)key && Objects.equals(this.value, value);
            } else {
               return false;
            }
         }
      }

      @Override
      public int hashCode() {
         return this.key ^ (this.value == null ? 0 : this.value.hashCode());
      }

      @Override
      public String toString() {
         return this.key + "->" + this.value;
      }
   }

   public abstract static class BasicEntrySet<V> extends AbstractObjectSet<Short2ObjectMap.Entry<V>> {
      protected final Short2ObjectMap<V> map;

      public BasicEntrySet(Short2ObjectMap<V> map) {
         this.map = map;
      }

      @Override
      public boolean contains(Object o) {
         if (!(o instanceof Entry)) {
            return false;
         } else if (o instanceof Short2ObjectMap.Entry) {
            Short2ObjectMap.Entry<V> e = (Short2ObjectMap.Entry<V>)o;
            short k = e.getShortKey();
            return this.map.containsKey(k) && Objects.equals(this.map.get(k), e.getValue());
         } else {
            Entry<?, ?> e = (Entry<?, ?>)o;
            Object key = e.getKey();
            if (key != null && key instanceof Short) {
               short k = (Short)key;
               Object value = e.getValue();
               return this.map.containsKey(k) && Objects.equals(this.map.get(k), value);
            } else {
               return false;
            }
         }
      }

      @Override
      public boolean remove(Object o) {
         if (!(o instanceof Entry)) {
            return false;
         } else if (o instanceof Short2ObjectMap.Entry) {
            Short2ObjectMap.Entry<V> e = (Short2ObjectMap.Entry<V>)o;
            return this.map.remove(e.getShortKey(), e.getValue());
         } else {
            Entry<?, ?> e = (Entry<?, ?>)o;
            Object key = e.getKey();
            if (key != null && key instanceof Short) {
               short k = (Short)key;
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
      public ObjectSpliterator<Short2ObjectMap.Entry<V>> spliterator() {
         return ObjectSpliterators.asSpliterator(this.iterator(), Size64.sizeOf(this.map), 65);
      }
   }
}
