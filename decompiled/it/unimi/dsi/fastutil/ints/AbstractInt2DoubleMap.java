package it.unimi.dsi.fastutil.ints;

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

public abstract class AbstractInt2DoubleMap extends AbstractInt2DoubleFunction implements Int2DoubleMap, Serializable {
   private static final long serialVersionUID = -4940583368468432370L;

   protected AbstractInt2DoubleMap() {
   }

   @Override
   public boolean containsKey(int k) {
      ObjectIterator<Int2DoubleMap.Entry> i = this.int2DoubleEntrySet().iterator();

      while (i.hasNext()) {
         if (i.next().getIntKey() == k) {
            return true;
         }
      }

      return false;
   }

   @Override
   public boolean containsValue(double v) {
      ObjectIterator<Int2DoubleMap.Entry> i = this.int2DoubleEntrySet().iterator();

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
   public final double mergeDouble(int key, double value, DoubleBinaryOperator remappingFunction) {
      return this.mergeDouble(key, value, remappingFunction);
   }

   @Override
   public IntSet keySet() {
      return new AbstractIntSet() {
         @Override
         public boolean contains(int k) {
            return AbstractInt2DoubleMap.this.containsKey(k);
         }

         @Override
         public int size() {
            return AbstractInt2DoubleMap.this.size();
         }

         @Override
         public void clear() {
            AbstractInt2DoubleMap.this.clear();
         }

         @Override
         public IntIterator iterator() {
            return new IntIterator() {
               private final ObjectIterator<Int2DoubleMap.Entry> i = Int2DoubleMaps.fastIterator(AbstractInt2DoubleMap.this);

               @Override
               public int nextInt() {
                  return this.i.next().getIntKey();
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
               public void forEachRemaining(java.util.function.IntConsumer action) {
                  this.i.forEachRemaining(entry -> action.accept(entry.getIntKey()));
               }
            };
         }

         @Override
         public IntSpliterator spliterator() {
            return IntSpliterators.asSpliterator(this.iterator(), Size64.sizeOf(AbstractInt2DoubleMap.this), 321);
         }
      };
   }

   @Override
   public DoubleCollection values() {
      return new AbstractDoubleCollection() {
         @Override
         public boolean contains(double k) {
            return AbstractInt2DoubleMap.this.containsValue(k);
         }

         @Override
         public int size() {
            return AbstractInt2DoubleMap.this.size();
         }

         @Override
         public void clear() {
            AbstractInt2DoubleMap.this.clear();
         }

         @Override
         public DoubleIterator iterator() {
            return new DoubleIterator() {
               private final ObjectIterator<Int2DoubleMap.Entry> i = Int2DoubleMaps.fastIterator(AbstractInt2DoubleMap.this);

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
            return DoubleSpliterators.asSpliterator(this.iterator(), Size64.sizeOf(AbstractInt2DoubleMap.this), 320);
         }
      };
   }

   @Override
   public void putAll(Map<? extends Integer, ? extends Double> m) {
      if (m instanceof Int2DoubleMap) {
         ObjectIterator<Int2DoubleMap.Entry> i = Int2DoubleMaps.fastIterator((Int2DoubleMap)m);

         while (i.hasNext()) {
            Int2DoubleMap.Entry e = i.next();
            this.put(e.getIntKey(), e.getDoubleValue());
         }
      } else {
         int n = m.size();
         Iterator<? extends Entry<? extends Integer, ? extends Double>> i = m.entrySet().iterator();

         while (n-- != 0) {
            Entry<? extends Integer, ? extends Double> e = (Entry<? extends Integer, ? extends Double>)i.next();
            this.put(e.getKey(), e.getValue());
         }
      }
   }

   @Override
   public int hashCode() {
      int h = 0;
      int n = this.size();
      ObjectIterator<Int2DoubleMap.Entry> i = Int2DoubleMaps.fastIterator(this);

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
         return m.size() != this.size() ? false : this.int2DoubleEntrySet().containsAll(m.entrySet());
      }
   }

   @Override
   public String toString() {
      StringBuilder s = new StringBuilder();
      ObjectIterator<Int2DoubleMap.Entry> i = Int2DoubleMaps.fastIterator(this);
      int n = this.size();
      boolean first = true;
      s.append("{");

      while (n-- != 0) {
         if (first) {
            first = false;
         } else {
            s.append(", ");
         }

         Int2DoubleMap.Entry e = i.next();
         s.append(String.valueOf(e.getIntKey()));
         s.append("=>");
         s.append(String.valueOf(e.getDoubleValue()));
      }

      s.append("}");
      return s.toString();
   }

   public static class BasicEntry implements Int2DoubleMap.Entry {
      protected int key;
      protected double value;

      public BasicEntry() {
      }

      public BasicEntry(Integer key, Double value) {
         this.key = key;
         this.value = value;
      }

      public BasicEntry(int key, double value) {
         this.key = key;
         this.value = value;
      }

      @Override
      public int getIntKey() {
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
         } else if (o instanceof Int2DoubleMap.Entry) {
            Int2DoubleMap.Entry e = (Int2DoubleMap.Entry)o;
            return this.key == e.getIntKey() && Double.doubleToLongBits(this.value) == Double.doubleToLongBits(e.getDoubleValue());
         } else {
            Entry<?, ?> e = (Entry<?, ?>)o;
            Object key = e.getKey();
            if (key != null && key instanceof Integer) {
               Object value = e.getValue();
               return value != null && value instanceof Double
                  ? this.key == (Integer)key && Double.doubleToLongBits(this.value) == Double.doubleToLongBits((Double)value)
                  : false;
            } else {
               return false;
            }
         }
      }

      @Override
      public int hashCode() {
         return this.key ^ HashCommon.double2int(this.value);
      }

      @Override
      public String toString() {
         return this.key + "->" + this.value;
      }
   }

   public abstract static class BasicEntrySet extends AbstractObjectSet<Int2DoubleMap.Entry> {
      protected final Int2DoubleMap map;

      public BasicEntrySet(Int2DoubleMap map) {
         this.map = map;
      }

      @Override
      public boolean contains(Object o) {
         if (!(o instanceof Entry)) {
            return false;
         } else if (o instanceof Int2DoubleMap.Entry) {
            Int2DoubleMap.Entry e = (Int2DoubleMap.Entry)o;
            int k = e.getIntKey();
            return this.map.containsKey(k) && Double.doubleToLongBits(this.map.get(k)) == Double.doubleToLongBits(e.getDoubleValue());
         } else {
            Entry<?, ?> e = (Entry<?, ?>)o;
            Object key = e.getKey();
            if (key != null && key instanceof Integer) {
               int k = (Integer)key;
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
         } else if (o instanceof Int2DoubleMap.Entry) {
            Int2DoubleMap.Entry e = (Int2DoubleMap.Entry)o;
            return this.map.remove(e.getIntKey(), e.getDoubleValue());
         } else {
            Entry<?, ?> e = (Entry<?, ?>)o;
            Object key = e.getKey();
            if (key != null && key instanceof Integer) {
               int k = (Integer)key;
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
      public ObjectSpliterator<Int2DoubleMap.Entry> spliterator() {
         return ObjectSpliterators.asSpliterator(this.iterator(), Size64.sizeOf(this.map), 65);
      }
   }
}
