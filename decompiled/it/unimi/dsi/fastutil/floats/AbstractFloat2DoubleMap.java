package it.unimi.dsi.fastutil.floats;

import it.unimi.dsi.fastutil.HashCommon;
import it.unimi.dsi.fastutil.Size64;
import it.unimi.dsi.fastutil.doubles.AbstractDoubleCollection;
import it.unimi.dsi.fastutil.doubles.DoubleBinaryOperator;
import it.unimi.dsi.fastutil.doubles.DoubleCollection;
import it.unimi.dsi.fastutil.doubles.DoubleIterator;
import it.unimi.dsi.fastutil.doubles.DoubleSpliterator;
import it.unimi.dsi.fastutil.doubles.DoubleSpliterators;
import it.unimi.dsi.fastutil.objects.AbstractObjectSet;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectSpliterator;
import it.unimi.dsi.fastutil.objects.ObjectSpliterators;
import java.io.Serializable;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.DoubleConsumer;

public abstract class AbstractFloat2DoubleMap extends AbstractFloat2DoubleFunction implements Float2DoubleMap, Serializable {
   private static final long serialVersionUID = -4940583368468432370L;

   protected AbstractFloat2DoubleMap() {
   }

   @Override
   public boolean containsKey(float k) {
      ObjectIterator<Float2DoubleMap.Entry> i = this.float2DoubleEntrySet().iterator();

      while (i.hasNext()) {
         if (i.next().getFloatKey() == k) {
            return true;
         }
      }

      return false;
   }

   @Override
   public boolean containsValue(double v) {
      ObjectIterator<Float2DoubleMap.Entry> i = this.float2DoubleEntrySet().iterator();

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
   public final double mergeDouble(float key, double value, DoubleBinaryOperator remappingFunction) {
      return this.mergeDouble(key, value, remappingFunction);
   }

   @Override
   public FloatSet keySet() {
      return new AbstractFloatSet() {
         @Override
         public boolean contains(float k) {
            return AbstractFloat2DoubleMap.this.containsKey(k);
         }

         @Override
         public int size() {
            return AbstractFloat2DoubleMap.this.size();
         }

         @Override
         public void clear() {
            AbstractFloat2DoubleMap.this.clear();
         }

         @Override
         public FloatIterator iterator() {
            return new FloatIterator() {
               private final ObjectIterator<Float2DoubleMap.Entry> i = Float2DoubleMaps.fastIterator(AbstractFloat2DoubleMap.this);

               @Override
               public float nextFloat() {
                  return this.i.next().getFloatKey();
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
               public void forEachRemaining(FloatConsumer action) {
                  this.i.forEachRemaining(entry -> action.accept(entry.getFloatKey()));
               }
            };
         }

         @Override
         public FloatSpliterator spliterator() {
            return FloatSpliterators.asSpliterator(this.iterator(), Size64.sizeOf(AbstractFloat2DoubleMap.this), 321);
         }
      };
   }

   @Override
   public DoubleCollection values() {
      return new AbstractDoubleCollection() {
         @Override
         public boolean contains(double k) {
            return AbstractFloat2DoubleMap.this.containsValue(k);
         }

         @Override
         public int size() {
            return AbstractFloat2DoubleMap.this.size();
         }

         @Override
         public void clear() {
            AbstractFloat2DoubleMap.this.clear();
         }

         @Override
         public DoubleIterator iterator() {
            return new DoubleIterator() {
               private final ObjectIterator<Float2DoubleMap.Entry> i = Float2DoubleMaps.fastIterator(AbstractFloat2DoubleMap.this);

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
            return DoubleSpliterators.asSpliterator(this.iterator(), Size64.sizeOf(AbstractFloat2DoubleMap.this), 320);
         }
      };
   }

   @Override
   public void putAll(Map<? extends Float, ? extends Double> m) {
      if (m instanceof Float2DoubleMap) {
         ObjectIterator<Float2DoubleMap.Entry> i = Float2DoubleMaps.fastIterator((Float2DoubleMap)m);

         while (i.hasNext()) {
            Float2DoubleMap.Entry e = i.next();
            this.put(e.getFloatKey(), e.getDoubleValue());
         }
      } else {
         int n = m.size();
         Iterator<? extends Entry<? extends Float, ? extends Double>> i = m.entrySet().iterator();

         while (n-- != 0) {
            Entry<? extends Float, ? extends Double> e = (Entry<? extends Float, ? extends Double>)i.next();
            this.put(e.getKey(), e.getValue());
         }
      }
   }

   @Override
   public int hashCode() {
      int h = 0;
      int n = this.size();
      ObjectIterator<Float2DoubleMap.Entry> i = Float2DoubleMaps.fastIterator(this);

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
         return m.size() != this.size() ? false : this.float2DoubleEntrySet().containsAll(m.entrySet());
      }
   }

   @Override
   public String toString() {
      StringBuilder s = new StringBuilder();
      ObjectIterator<Float2DoubleMap.Entry> i = Float2DoubleMaps.fastIterator(this);
      int n = this.size();
      boolean first = true;
      s.append("{");

      while (n-- != 0) {
         if (first) {
            first = false;
         } else {
            s.append(", ");
         }

         Float2DoubleMap.Entry e = i.next();
         s.append(String.valueOf(e.getFloatKey()));
         s.append("=>");
         s.append(String.valueOf(e.getDoubleValue()));
      }

      s.append("}");
      return s.toString();
   }

   public static class BasicEntry implements Float2DoubleMap.Entry {
      protected float key;
      protected double value;

      public BasicEntry() {
      }

      public BasicEntry(Float key, Double value) {
         this.key = key;
         this.value = value;
      }

      public BasicEntry(float key, double value) {
         this.key = key;
         this.value = value;
      }

      @Override
      public float getFloatKey() {
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
         } else if (o instanceof Float2DoubleMap.Entry) {
            Float2DoubleMap.Entry e = (Float2DoubleMap.Entry)o;
            return Float.floatToIntBits(this.key) == Float.floatToIntBits(e.getFloatKey())
               && Double.doubleToLongBits(this.value) == Double.doubleToLongBits(e.getDoubleValue());
         } else {
            Entry<?, ?> e = (Entry<?, ?>)o;
            Object key = e.getKey();
            if (key != null && key instanceof Float) {
               Object value = e.getValue();
               return value != null && value instanceof Double
                  ? Float.floatToIntBits(this.key) == Float.floatToIntBits((Float)key)
                     && Double.doubleToLongBits(this.value) == Double.doubleToLongBits((Double)value)
                  : false;
            } else {
               return false;
            }
         }
      }

      @Override
      public int hashCode() {
         return HashCommon.float2int(this.key) ^ HashCommon.double2int(this.value);
      }

      @Override
      public String toString() {
         return this.key + "->" + this.value;
      }
   }

   public abstract static class BasicEntrySet extends AbstractObjectSet<Float2DoubleMap.Entry> {
      protected final Float2DoubleMap map;

      public BasicEntrySet(Float2DoubleMap map) {
         this.map = map;
      }

      @Override
      public boolean contains(Object o) {
         if (!(o instanceof Entry)) {
            return false;
         } else if (o instanceof Float2DoubleMap.Entry) {
            Float2DoubleMap.Entry e = (Float2DoubleMap.Entry)o;
            float k = e.getFloatKey();
            return this.map.containsKey(k) && Double.doubleToLongBits(this.map.get(k)) == Double.doubleToLongBits(e.getDoubleValue());
         } else {
            Entry<?, ?> e = (Entry<?, ?>)o;
            Object key = e.getKey();
            if (key != null && key instanceof Float) {
               float k = (Float)key;
               Object value = e.getValue();
               return value != null && value instanceof Double
                  ? this.map.containsKey(k) && Double.doubleToLongBits(this.map.get(k)) == Double.doubleToLongBits((Double)value)
                  : false;
            } else {
               return false;
            }
         }
      }

      @Override
      public boolean remove(Object o) {
         if (!(o instanceof Entry)) {
            return false;
         } else if (o instanceof Float2DoubleMap.Entry) {
            Float2DoubleMap.Entry e = (Float2DoubleMap.Entry)o;
            return this.map.remove(e.getFloatKey(), e.getDoubleValue());
         } else {
            Entry<?, ?> e = (Entry<?, ?>)o;
            Object key = e.getKey();
            if (key != null && key instanceof Float) {
               float k = (Float)key;
               Object value = e.getValue();
               if (value != null && value instanceof Double) {
                  double v = (Double)value;
                  return this.map.remove(k, v);
               } else {
                  return false;
               }
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
      public ObjectSpliterator<Float2DoubleMap.Entry> spliterator() {
         return ObjectSpliterators.asSpliterator(this.iterator(), Size64.sizeOf(this.map), 65);
      }
   }
}
