package it.unimi.dsi.fastutil.doubles;

import it.unimi.dsi.fastutil.HashCommon;
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

public abstract class AbstractDouble2ObjectMap<V> extends AbstractDouble2ObjectFunction<V> implements Double2ObjectMap<V>, Serializable {
   private static final long serialVersionUID = -4940583368468432370L;

   protected AbstractDouble2ObjectMap() {
   }

   @Override
   public boolean containsKey(double k) {
      ObjectIterator<Double2ObjectMap.Entry<V>> i = this.double2ObjectEntrySet().iterator();

      while (i.hasNext()) {
         if (i.next().getDoubleKey() == k) {
            return true;
         }
      }

      return false;
   }

   @Override
   public boolean containsValue(Object v) {
      ObjectIterator<Double2ObjectMap.Entry<V>> i = this.double2ObjectEntrySet().iterator();

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
   public DoubleSet keySet() {
      return new AbstractDoubleSet() {
         @Override
         public boolean contains(double k) {
            return AbstractDouble2ObjectMap.this.containsKey(k);
         }

         @Override
         public int size() {
            return AbstractDouble2ObjectMap.this.size();
         }

         @Override
         public void clear() {
            AbstractDouble2ObjectMap.this.clear();
         }

         @Override
         public DoubleIterator iterator() {
            return new DoubleIterator() {
               private final ObjectIterator<Double2ObjectMap.Entry<V>> i = Double2ObjectMaps.fastIterator(AbstractDouble2ObjectMap.this);

               @Override
               public double nextDouble() {
                  return this.i.next().getDoubleKey();
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
               public void forEachRemaining(java.util.function.DoubleConsumer action) {
                  this.i.forEachRemaining(entry -> action.accept(entry.getDoubleKey()));
               }
            };
         }

         @Override
         public DoubleSpliterator spliterator() {
            return DoubleSpliterators.asSpliterator(this.iterator(), Size64.sizeOf(AbstractDouble2ObjectMap.this), 321);
         }
      };
   }

   @Override
   public ObjectCollection<V> values() {
      return new AbstractObjectCollection<V>() {
         @Override
         public boolean contains(Object k) {
            return AbstractDouble2ObjectMap.this.containsValue(k);
         }

         @Override
         public int size() {
            return AbstractDouble2ObjectMap.this.size();
         }

         @Override
         public void clear() {
            AbstractDouble2ObjectMap.this.clear();
         }

         @Override
         public ObjectIterator<V> iterator() {
            return new ObjectIterator<V>() {
               private final ObjectIterator<Double2ObjectMap.Entry<V>> i = Double2ObjectMaps.fastIterator(AbstractDouble2ObjectMap.this);

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
            return ObjectSpliterators.asSpliterator(this.iterator(), Size64.sizeOf(AbstractDouble2ObjectMap.this), 64);
         }
      };
   }

   @Override
   public void putAll(Map<? extends Double, ? extends V> m) {
      if (m instanceof Double2ObjectMap) {
         ObjectIterator<Double2ObjectMap.Entry<V>> i = Double2ObjectMaps.fastIterator((Double2ObjectMap<V>)m);

         while (i.hasNext()) {
            Double2ObjectMap.Entry<? extends V> e = i.next();
            this.put(e.getDoubleKey(), (V)e.getValue());
         }
      } else {
         int n = m.size();
         Iterator<? extends Entry<? extends Double, ? extends V>> i = m.entrySet().iterator();

         while (n-- != 0) {
            Entry<? extends Double, ? extends V> e = (Entry<? extends Double, ? extends V>)i.next();
            this.put(e.getKey(), (V)e.getValue());
         }
      }
   }

   @Override
   public int hashCode() {
      int h = 0;
      int n = this.size();
      ObjectIterator<Double2ObjectMap.Entry<V>> i = Double2ObjectMaps.fastIterator(this);

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
         return m.size() != this.size() ? false : this.double2ObjectEntrySet().containsAll(m.entrySet());
      }
   }

   @Override
   public String toString() {
      StringBuilder s = new StringBuilder();
      ObjectIterator<Double2ObjectMap.Entry<V>> i = Double2ObjectMaps.fastIterator(this);
      int n = this.size();
      boolean first = true;
      s.append("{");

      while (n-- != 0) {
         if (first) {
            first = false;
         } else {
            s.append(", ");
         }

         Double2ObjectMap.Entry<V> e = i.next();
         s.append(String.valueOf(e.getDoubleKey()));
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

   public static class BasicEntry<V> implements Double2ObjectMap.Entry<V> {
      protected double key;
      protected V value;

      public BasicEntry() {
      }

      public BasicEntry(Double key, V value) {
         this.key = key;
         this.value = value;
      }

      public BasicEntry(double key, V value) {
         this.key = key;
         this.value = value;
      }

      @Override
      public double getDoubleKey() {
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
         } else if (o instanceof Double2ObjectMap.Entry) {
            Double2ObjectMap.Entry<V> e = (Double2ObjectMap.Entry<V>)o;
            return Double.doubleToLongBits(this.key) == Double.doubleToLongBits(e.getDoubleKey()) && Objects.equals(this.value, e.getValue());
         } else {
            Entry<?, ?> e = (Entry<?, ?>)o;
            Object key = e.getKey();
            if (key != null && key instanceof Double) {
               Object value = e.getValue();
               return Double.doubleToLongBits(this.key) == Double.doubleToLongBits((Double)key) && Objects.equals(this.value, value);
            } else {
               return false;
            }
         }
      }

      @Override
      public int hashCode() {
         return HashCommon.double2int(this.key) ^ (this.value == null ? 0 : this.value.hashCode());
      }

      @Override
      public String toString() {
         return this.key + "->" + this.value;
      }
   }

   public abstract static class BasicEntrySet<V> extends AbstractObjectSet<Double2ObjectMap.Entry<V>> {
      protected final Double2ObjectMap<V> map;

      public BasicEntrySet(Double2ObjectMap<V> map) {
         this.map = map;
      }

      @Override
      public boolean contains(Object o) {
         if (!(o instanceof Entry)) {
            return false;
         } else if (o instanceof Double2ObjectMap.Entry) {
            Double2ObjectMap.Entry<V> e = (Double2ObjectMap.Entry<V>)o;
            double k = e.getDoubleKey();
            return this.map.containsKey(k) && Objects.equals(this.map.get(k), e.getValue());
         } else {
            Entry<?, ?> e = (Entry<?, ?>)o;
            Object key = e.getKey();
            if (key != null && key instanceof Double) {
               double k = (Double)key;
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
         } else if (o instanceof Double2ObjectMap.Entry) {
            Double2ObjectMap.Entry<V> e = (Double2ObjectMap.Entry<V>)o;
            return this.map.remove(e.getDoubleKey(), e.getValue());
         } else {
            Entry<?, ?> e = (Entry<?, ?>)o;
            Object key = e.getKey();
            if (key != null && key instanceof Double) {
               double k = (Double)key;
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
      public ObjectSpliterator<Double2ObjectMap.Entry<V>> spliterator() {
         return ObjectSpliterators.asSpliterator(this.iterator(), Size64.sizeOf(this.map), 65);
      }
   }
}
