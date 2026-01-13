package it.unimi.dsi.fastutil.objects;

import it.unimi.dsi.fastutil.HashCommon;
import it.unimi.dsi.fastutil.Size64;
import it.unimi.dsi.fastutil.doubles.AbstractDoubleCollection;
import it.unimi.dsi.fastutil.doubles.DoubleBinaryOperator;
import it.unimi.dsi.fastutil.doubles.DoubleCollection;
import it.unimi.dsi.fastutil.doubles.DoubleIterator;
import it.unimi.dsi.fastutil.doubles.DoubleSpliterator;
import it.unimi.dsi.fastutil.doubles.DoubleSpliterators;
import java.io.Serializable;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Consumer;
import java.util.function.DoubleConsumer;

public abstract class AbstractReference2DoubleMap<K> extends AbstractReference2DoubleFunction<K> implements Reference2DoubleMap<K>, Serializable {
   private static final long serialVersionUID = -4940583368468432370L;

   protected AbstractReference2DoubleMap() {
   }

   @Override
   public boolean containsKey(Object k) {
      ObjectIterator<Reference2DoubleMap.Entry<K>> i = this.reference2DoubleEntrySet().iterator();

      while (i.hasNext()) {
         if (i.next().getKey() == k) {
            return true;
         }
      }

      return false;
   }

   @Override
   public boolean containsValue(double v) {
      ObjectIterator<Reference2DoubleMap.Entry<K>> i = this.reference2DoubleEntrySet().iterator();

      while (i.hasNext()) {
         if (i.next().getDoubleValue() == v) {
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
   public final double mergeDouble(K key, double value, DoubleBinaryOperator remappingFunction) {
      return this.mergeDouble(key, value, remappingFunction);
   }

   @Override
   public ReferenceSet<K> keySet() {
      return new AbstractReferenceSet<K>() {
         @Override
         public boolean contains(Object k) {
            return AbstractReference2DoubleMap.this.containsKey(k);
         }

         @Override
         public int size() {
            return AbstractReference2DoubleMap.this.size();
         }

         @Override
         public void clear() {
            AbstractReference2DoubleMap.this.clear();
         }

         @Override
         public ObjectIterator<K> iterator() {
            return new ObjectIterator<K>() {
               private final ObjectIterator<Reference2DoubleMap.Entry<K>> i = Reference2DoubleMaps.fastIterator(AbstractReference2DoubleMap.this);

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
            return ObjectSpliterators.asSpliterator(this.iterator(), Size64.sizeOf(AbstractReference2DoubleMap.this), 65);
         }
      };
   }

   @Override
   public DoubleCollection values() {
      return new AbstractDoubleCollection() {
         @Override
         public boolean contains(double k) {
            return AbstractReference2DoubleMap.this.containsValue(k);
         }

         @Override
         public int size() {
            return AbstractReference2DoubleMap.this.size();
         }

         @Override
         public void clear() {
            AbstractReference2DoubleMap.this.clear();
         }

         @Override
         public DoubleIterator iterator() {
            return new DoubleIterator() {
               private final ObjectIterator<Reference2DoubleMap.Entry<K>> i = Reference2DoubleMaps.fastIterator(AbstractReference2DoubleMap.this);

               @Override
               public double nextDouble() {
                  return this.i.next().getDoubleValue();
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
               public void forEachRemaining(DoubleConsumer action) {
                  this.i.forEachRemaining(entry -> action.accept(entry.getDoubleValue()));
               }
            };
         }

         @Override
         public DoubleSpliterator spliterator() {
            return DoubleSpliterators.asSpliterator(this.iterator(), Size64.sizeOf(AbstractReference2DoubleMap.this), 320);
         }
      };
   }

   @Override
   public void putAll(Map<? extends K, ? extends Double> m) {
      if (m instanceof Reference2DoubleMap) {
         ObjectIterator<Reference2DoubleMap.Entry<K>> i = Reference2DoubleMaps.fastIterator((Reference2DoubleMap<K>)m);

         while (i.hasNext()) {
            Reference2DoubleMap.Entry<? extends K> e = i.next();
            this.put((K)e.getKey(), e.getDoubleValue());
         }
      } else {
         int n = m.size();
         Iterator<? extends Entry<? extends K, ? extends Double>> i = m.entrySet().iterator();

         while (n-- != 0) {
            Entry<? extends K, ? extends Double> e = (Entry<? extends K, ? extends Double>)i.next();
            this.put((K)e.getKey(), e.getValue());
         }
      }
   }

   @Override
   public int hashCode() {
      int h = 0;
      int n = this.size();
      ObjectIterator<Reference2DoubleMap.Entry<K>> i = Reference2DoubleMaps.fastIterator(this);

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
         return m.size() != this.size() ? false : this.reference2DoubleEntrySet().containsAll(m.entrySet());
      }
   }

   @Override
   public String toString() {
      StringBuilder s = new StringBuilder();
      ObjectIterator<Reference2DoubleMap.Entry<K>> i = Reference2DoubleMaps.fastIterator(this);
      int n = this.size();
      boolean first = true;
      s.append("{");

      while (n-- != 0) {
         if (first) {
            first = false;
         } else {
            s.append(", ");
         }

         Reference2DoubleMap.Entry<K> e = i.next();
         if (this == e.getKey()) {
            s.append("(this map)");
         } else {
            s.append(String.valueOf(e.getKey()));
         }

         s.append("=>");
         s.append(String.valueOf(e.getDoubleValue()));
      }

      s.append("}");
      return s.toString();
   }

   public static class BasicEntry<K> implements Reference2DoubleMap.Entry<K> {
      protected K key;
      protected double value;

      public BasicEntry() {
      }

      public BasicEntry(K key, Double value) {
         this.key = key;
         this.value = value;
      }

      public BasicEntry(K key, double value) {
         this.key = key;
         this.value = value;
      }

      @Override
      public K getKey() {
         return this.key;
      }

      @Override
      public double getDoubleValue() {
         return this.value;
      }

      @Override
      public double setValue(double value) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean equals(Object o) {
         if (!(o instanceof Entry)) {
            return false;
         } else if (o instanceof Reference2DoubleMap.Entry) {
            Reference2DoubleMap.Entry<K> e = (Reference2DoubleMap.Entry<K>)o;
            return this.key == e.getKey() && Double.doubleToLongBits(this.value) == Double.doubleToLongBits(e.getDoubleValue());
         } else {
            Entry<?, ?> e = (Entry<?, ?>)o;
            Object key = e.getKey();
            Object value = e.getValue();
            return value != null && value instanceof Double
               ? this.key == key && Double.doubleToLongBits(this.value) == Double.doubleToLongBits((Double)value)
               : false;
         }
      }

      @Override
      public int hashCode() {
         return System.identityHashCode(this.key) ^ HashCommon.double2int(this.value);
      }

      @Override
      public String toString() {
         return this.key + "->" + this.value;
      }
   }

   public abstract static class BasicEntrySet<K> extends AbstractObjectSet<Reference2DoubleMap.Entry<K>> {
      protected final Reference2DoubleMap<K> map;

      public BasicEntrySet(Reference2DoubleMap<K> map) {
         this.map = map;
      }

      @Override
      public boolean contains(Object o) {
         if (!(o instanceof Entry)) {
            return false;
         } else if (o instanceof Reference2DoubleMap.Entry) {
            Reference2DoubleMap.Entry<K> e = (Reference2DoubleMap.Entry<K>)o;
            K k = e.getKey();
            return this.map.containsKey(k) && Double.doubleToLongBits(this.map.getDouble(k)) == Double.doubleToLongBits(e.getDoubleValue());
         } else {
            Entry<?, ?> e = (Entry<?, ?>)o;
            Object k = e.getKey();
            Object value = e.getValue();
            return value != null && value instanceof Double
               ? this.map.containsKey(k) && Double.doubleToLongBits(this.map.getDouble(k)) == Double.doubleToLongBits((Double)value)
               : false;
         }
      }

      @Override
      public boolean remove(Object o) {
         if (!(o instanceof Entry)) {
            return false;
         } else if (o instanceof Reference2DoubleMap.Entry) {
            Reference2DoubleMap.Entry<K> e = (Reference2DoubleMap.Entry<K>)o;
            return this.map.remove(e.getKey(), e.getDoubleValue());
         } else {
            Entry<?, ?> e = (Entry<?, ?>)o;
            Object k = e.getKey();
            Object value = e.getValue();
            if (value != null && value instanceof Double) {
               double v = (Double)value;
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
      public ObjectSpliterator<Reference2DoubleMap.Entry<K>> spliterator() {
         return ObjectSpliterators.asSpliterator(this.iterator(), Size64.sizeOf(this.map), 65);
      }
   }
}
