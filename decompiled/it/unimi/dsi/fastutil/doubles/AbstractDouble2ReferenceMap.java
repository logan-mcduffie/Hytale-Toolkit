package it.unimi.dsi.fastutil.doubles;

import it.unimi.dsi.fastutil.HashCommon;
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

public abstract class AbstractDouble2ReferenceMap<V> extends AbstractDouble2ReferenceFunction<V> implements Double2ReferenceMap<V>, Serializable {
   private static final long serialVersionUID = -4940583368468432370L;

   protected AbstractDouble2ReferenceMap() {
   }

   @Override
   public boolean containsKey(double k) {
      ObjectIterator<Double2ReferenceMap.Entry<V>> i = this.double2ReferenceEntrySet().iterator();

      while (i.hasNext()) {
         if (i.next().getDoubleKey() == k) {
            return true;
         }
      }

      return false;
   }

   @Override
   public boolean containsValue(Object v) {
      ObjectIterator<Double2ReferenceMap.Entry<V>> i = this.double2ReferenceEntrySet().iterator();

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
            return AbstractDouble2ReferenceMap.this.containsKey(k);
         }

         @Override
         public int size() {
            return AbstractDouble2ReferenceMap.this.size();
         }

         @Override
         public void clear() {
            AbstractDouble2ReferenceMap.this.clear();
         }

         @Override
         public DoubleIterator iterator() {
            return new DoubleIterator() {
               private final ObjectIterator<Double2ReferenceMap.Entry<V>> i = Double2ReferenceMaps.fastIterator(AbstractDouble2ReferenceMap.this);

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
            return DoubleSpliterators.asSpliterator(this.iterator(), Size64.sizeOf(AbstractDouble2ReferenceMap.this), 321);
         }
      };
   }

   @Override
   public ReferenceCollection<V> values() {
      return new AbstractReferenceCollection<V>() {
         @Override
         public boolean contains(Object k) {
            return AbstractDouble2ReferenceMap.this.containsValue(k);
         }

         @Override
         public int size() {
            return AbstractDouble2ReferenceMap.this.size();
         }

         @Override
         public void clear() {
            AbstractDouble2ReferenceMap.this.clear();
         }

         @Override
         public ObjectIterator<V> iterator() {
            return new ObjectIterator<V>() {
               private final ObjectIterator<Double2ReferenceMap.Entry<V>> i = Double2ReferenceMaps.fastIterator(AbstractDouble2ReferenceMap.this);

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
            return ObjectSpliterators.asSpliterator(this.iterator(), Size64.sizeOf(AbstractDouble2ReferenceMap.this), 64);
         }
      };
   }

   @Override
   public void putAll(Map<? extends Double, ? extends V> m) {
      if (m instanceof Double2ReferenceMap) {
         ObjectIterator<Double2ReferenceMap.Entry<V>> i = Double2ReferenceMaps.fastIterator((Double2ReferenceMap<V>)m);

         while (i.hasNext()) {
            Double2ReferenceMap.Entry<? extends V> e = i.next();
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
      ObjectIterator<Double2ReferenceMap.Entry<V>> i = Double2ReferenceMaps.fastIterator(this);

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
         return m.size() != this.size() ? false : this.double2ReferenceEntrySet().containsAll(m.entrySet());
      }
   }

   @Override
   public String toString() {
      StringBuilder s = new StringBuilder();
      ObjectIterator<Double2ReferenceMap.Entry<V>> i = Double2ReferenceMaps.fastIterator(this);
      int n = this.size();
      boolean first = true;
      s.append("{");

      while (n-- != 0) {
         if (first) {
            first = false;
         } else {
            s.append(", ");
         }

         Double2ReferenceMap.Entry<V> e = i.next();
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

   public static class BasicEntry<V> implements Double2ReferenceMap.Entry<V> {
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
         } else if (o instanceof Double2ReferenceMap.Entry) {
            Double2ReferenceMap.Entry<V> e = (Double2ReferenceMap.Entry<V>)o;
            return Double.doubleToLongBits(this.key) == Double.doubleToLongBits(e.getDoubleKey()) && this.value == e.getValue();
         } else {
            Entry<?, ?> e = (Entry<?, ?>)o;
            Object key = e.getKey();
            if (key != null && key instanceof Double) {
               Object value = e.getValue();
               return Double.doubleToLongBits(this.key) == Double.doubleToLongBits((Double)key) && this.value == value;
            } else {
               return false;
            }
         }
      }

      @Override
      public int hashCode() {
         return HashCommon.double2int(this.key) ^ (this.value == null ? 0 : System.identityHashCode(this.value));
      }

      @Override
      public String toString() {
         return this.key + "->" + this.value;
      }
   }

   public abstract static class BasicEntrySet<V> extends AbstractObjectSet<Double2ReferenceMap.Entry<V>> {
      protected final Double2ReferenceMap<V> map;

      public BasicEntrySet(Double2ReferenceMap<V> map) {
         this.map = map;
      }

      @Override
      public boolean contains(Object o) {
         if (!(o instanceof Entry)) {
            return false;
         } else if (o instanceof Double2ReferenceMap.Entry) {
            Double2ReferenceMap.Entry<V> e = (Double2ReferenceMap.Entry<V>)o;
            double k = e.getDoubleKey();
            return this.map.containsKey(k) && this.map.get(k) == e.getValue();
         } else {
            Entry<?, ?> e = (Entry<?, ?>)o;
            Object key = e.getKey();
            if (key != null && key instanceof Double) {
               double k = (Double)key;
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
         } else if (o instanceof Double2ReferenceMap.Entry) {
            Double2ReferenceMap.Entry<V> e = (Double2ReferenceMap.Entry<V>)o;
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
      public ObjectSpliterator<Double2ReferenceMap.Entry<V>> spliterator() {
         return ObjectSpliterators.asSpliterator(this.iterator(), Size64.sizeOf(this.map), 65);
      }
   }
}
