package it.unimi.dsi.fastutil.doubles;

import it.unimi.dsi.fastutil.HashCommon;
import it.unimi.dsi.fastutil.Size64;
import it.unimi.dsi.fastutil.floats.AbstractFloatCollection;
import it.unimi.dsi.fastutil.floats.FloatCollection;
import it.unimi.dsi.fastutil.floats.FloatConsumer;
import it.unimi.dsi.fastutil.floats.FloatIterator;
import it.unimi.dsi.fastutil.floats.FloatSpliterator;
import it.unimi.dsi.fastutil.floats.FloatSpliterators;
import it.unimi.dsi.fastutil.objects.AbstractObjectSet;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectSpliterator;
import it.unimi.dsi.fastutil.objects.ObjectSpliterators;
import java.io.Serializable;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

public abstract class AbstractDouble2FloatMap extends AbstractDouble2FloatFunction implements Double2FloatMap, Serializable {
   private static final long serialVersionUID = -4940583368468432370L;

   protected AbstractDouble2FloatMap() {
   }

   @Override
   public boolean containsKey(double k) {
      ObjectIterator<Double2FloatMap.Entry> i = this.double2FloatEntrySet().iterator();

      while (i.hasNext()) {
         if (i.next().getDoubleKey() == k) {
            return true;
         }
      }

      return false;
   }

   @Override
   public boolean containsValue(float v) {
      ObjectIterator<Double2FloatMap.Entry> i = this.double2FloatEntrySet().iterator();

      while (i.hasNext()) {
         if (i.next().getFloatValue() == v) {
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
            return AbstractDouble2FloatMap.this.containsKey(k);
         }

         @Override
         public int size() {
            return AbstractDouble2FloatMap.this.size();
         }

         @Override
         public void clear() {
            AbstractDouble2FloatMap.this.clear();
         }

         @Override
         public DoubleIterator iterator() {
            return new DoubleIterator() {
               private final ObjectIterator<Double2FloatMap.Entry> i = Double2FloatMaps.fastIterator(AbstractDouble2FloatMap.this);

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
            return DoubleSpliterators.asSpliterator(this.iterator(), Size64.sizeOf(AbstractDouble2FloatMap.this), 321);
         }
      };
   }

   @Override
   public FloatCollection values() {
      return new AbstractFloatCollection() {
         @Override
         public boolean contains(float k) {
            return AbstractDouble2FloatMap.this.containsValue(k);
         }

         @Override
         public int size() {
            return AbstractDouble2FloatMap.this.size();
         }

         @Override
         public void clear() {
            AbstractDouble2FloatMap.this.clear();
         }

         @Override
         public FloatIterator iterator() {
            return new FloatIterator() {
               private final ObjectIterator<Double2FloatMap.Entry> i = Double2FloatMaps.fastIterator(AbstractDouble2FloatMap.this);

               @Override
               public float nextFloat() {
                  return this.i.next().getFloatValue();
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
                  this.i.forEachRemaining(entry -> action.accept(entry.getFloatValue()));
               }
            };
         }

         @Override
         public FloatSpliterator spliterator() {
            return FloatSpliterators.asSpliterator(this.iterator(), Size64.sizeOf(AbstractDouble2FloatMap.this), 320);
         }
      };
   }

   @Override
   public void putAll(Map<? extends Double, ? extends Float> m) {
      if (m instanceof Double2FloatMap) {
         ObjectIterator<Double2FloatMap.Entry> i = Double2FloatMaps.fastIterator((Double2FloatMap)m);

         while (i.hasNext()) {
            Double2FloatMap.Entry e = i.next();
            this.put(e.getDoubleKey(), e.getFloatValue());
         }
      } else {
         int n = m.size();
         Iterator<? extends Entry<? extends Double, ? extends Float>> i = m.entrySet().iterator();

         while (n-- != 0) {
            Entry<? extends Double, ? extends Float> e = (Entry<? extends Double, ? extends Float>)i.next();
            this.put(e.getKey(), e.getValue());
         }
      }
   }

   @Override
   public int hashCode() {
      int h = 0;
      int n = this.size();
      ObjectIterator<Double2FloatMap.Entry> i = Double2FloatMaps.fastIterator(this);

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
         return m.size() != this.size() ? false : this.double2FloatEntrySet().containsAll(m.entrySet());
      }
   }

   @Override
   public String toString() {
      StringBuilder s = new StringBuilder();
      ObjectIterator<Double2FloatMap.Entry> i = Double2FloatMaps.fastIterator(this);
      int n = this.size();
      boolean first = true;
      s.append("{");

      while (n-- != 0) {
         if (first) {
            first = false;
         } else {
            s.append(", ");
         }

         Double2FloatMap.Entry e = i.next();
         s.append(String.valueOf(e.getDoubleKey()));
         s.append("=>");
         s.append(String.valueOf(e.getFloatValue()));
      }

      s.append("}");
      return s.toString();
   }

   public static class BasicEntry implements Double2FloatMap.Entry {
      protected double key;
      protected float value;

      public BasicEntry() {
      }

      public BasicEntry(Double key, Float value) {
         this.key = key;
         this.value = value;
      }

      public BasicEntry(double key, float value) {
         this.key = key;
         this.value = value;
      }

      @Override
      public double getDoubleKey() {
         return this.key;
      }

      @Override
      public float getFloatValue() {
         return this.value;
      }

      @Override
      public float setValue(float value) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean equals(Object o) {
         if (!(o instanceof Entry)) {
            return false;
         } else if (o instanceof Double2FloatMap.Entry) {
            Double2FloatMap.Entry e = (Double2FloatMap.Entry)o;
            return Double.doubleToLongBits(this.key) == Double.doubleToLongBits(e.getDoubleKey())
               && Float.floatToIntBits(this.value) == Float.floatToIntBits(e.getFloatValue());
         } else {
            Entry<?, ?> e = (Entry<?, ?>)o;
            Object key = e.getKey();
            if (key != null && key instanceof Double) {
               Object value = e.getValue();
               return value != null && value instanceof Float
                  ? Double.doubleToLongBits(this.key) == Double.doubleToLongBits((Double)key)
                     && Float.floatToIntBits(this.value) == Float.floatToIntBits((Float)value)
                  : false;
            } else {
               return false;
            }
         }
      }

      @Override
      public int hashCode() {
         return HashCommon.double2int(this.key) ^ HashCommon.float2int(this.value);
      }

      @Override
      public String toString() {
         return this.key + "->" + this.value;
      }
   }

   public abstract static class BasicEntrySet extends AbstractObjectSet<Double2FloatMap.Entry> {
      protected final Double2FloatMap map;

      public BasicEntrySet(Double2FloatMap map) {
         this.map = map;
      }

      @Override
      public boolean contains(Object o) {
         if (!(o instanceof Entry)) {
            return false;
         } else if (o instanceof Double2FloatMap.Entry) {
            Double2FloatMap.Entry e = (Double2FloatMap.Entry)o;
            double k = e.getDoubleKey();
            return this.map.containsKey(k) && Float.floatToIntBits(this.map.get(k)) == Float.floatToIntBits(e.getFloatValue());
         } else {
            Entry<?, ?> e = (Entry<?, ?>)o;
            Object key = e.getKey();
            if (key != null && key instanceof Double) {
               double k = (Double)key;
               Object value = e.getValue();
               return value != null && value instanceof Float
                  ? this.map.containsKey(k) && Float.floatToIntBits(this.map.get(k)) == Float.floatToIntBits((Float)value)
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
         } else if (o instanceof Double2FloatMap.Entry) {
            Double2FloatMap.Entry e = (Double2FloatMap.Entry)o;
            return this.map.remove(e.getDoubleKey(), e.getFloatValue());
         } else {
            Entry<?, ?> e = (Entry<?, ?>)o;
            Object key = e.getKey();
            if (key != null && key instanceof Double) {
               double k = (Double)key;
               Object value = e.getValue();
               if (value != null && value instanceof Float) {
                  float v = (Float)value;
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
      public ObjectSpliterator<Double2FloatMap.Entry> spliterator() {
         return ObjectSpliterators.asSpliterator(this.iterator(), Size64.sizeOf(this.map), 65);
      }
   }
}
