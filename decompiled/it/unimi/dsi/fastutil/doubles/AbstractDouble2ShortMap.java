package it.unimi.dsi.fastutil.doubles;

import it.unimi.dsi.fastutil.HashCommon;
import it.unimi.dsi.fastutil.Size64;
import it.unimi.dsi.fastutil.objects.AbstractObjectSet;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectSpliterator;
import it.unimi.dsi.fastutil.objects.ObjectSpliterators;
import it.unimi.dsi.fastutil.shorts.AbstractShortCollection;
import it.unimi.dsi.fastutil.shorts.ShortCollection;
import it.unimi.dsi.fastutil.shorts.ShortConsumer;
import it.unimi.dsi.fastutil.shorts.ShortIterator;
import it.unimi.dsi.fastutil.shorts.ShortSpliterator;
import it.unimi.dsi.fastutil.shorts.ShortSpliterators;
import java.io.Serializable;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

public abstract class AbstractDouble2ShortMap extends AbstractDouble2ShortFunction implements Double2ShortMap, Serializable {
   private static final long serialVersionUID = -4940583368468432370L;

   protected AbstractDouble2ShortMap() {
   }

   @Override
   public boolean containsKey(double k) {
      ObjectIterator<Double2ShortMap.Entry> i = this.double2ShortEntrySet().iterator();

      while (i.hasNext()) {
         if (i.next().getDoubleKey() == k) {
            return true;
         }
      }

      return false;
   }

   @Override
   public boolean containsValue(short v) {
      ObjectIterator<Double2ShortMap.Entry> i = this.double2ShortEntrySet().iterator();

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
   public DoubleSet keySet() {
      return new AbstractDoubleSet() {
         @Override
         public boolean contains(double k) {
            return AbstractDouble2ShortMap.this.containsKey(k);
         }

         @Override
         public int size() {
            return AbstractDouble2ShortMap.this.size();
         }

         @Override
         public void clear() {
            AbstractDouble2ShortMap.this.clear();
         }

         @Override
         public DoubleIterator iterator() {
            return new DoubleIterator() {
               private final ObjectIterator<Double2ShortMap.Entry> i = Double2ShortMaps.fastIterator(AbstractDouble2ShortMap.this);

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
            return DoubleSpliterators.asSpliterator(this.iterator(), Size64.sizeOf(AbstractDouble2ShortMap.this), 321);
         }
      };
   }

   @Override
   public ShortCollection values() {
      return new AbstractShortCollection() {
         @Override
         public boolean contains(short k) {
            return AbstractDouble2ShortMap.this.containsValue(k);
         }

         @Override
         public int size() {
            return AbstractDouble2ShortMap.this.size();
         }

         @Override
         public void clear() {
            AbstractDouble2ShortMap.this.clear();
         }

         @Override
         public ShortIterator iterator() {
            return new ShortIterator() {
               private final ObjectIterator<Double2ShortMap.Entry> i = Double2ShortMaps.fastIterator(AbstractDouble2ShortMap.this);

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
            return ShortSpliterators.asSpliterator(this.iterator(), Size64.sizeOf(AbstractDouble2ShortMap.this), 320);
         }
      };
   }

   @Override
   public void putAll(Map<? extends Double, ? extends Short> m) {
      if (m instanceof Double2ShortMap) {
         ObjectIterator<Double2ShortMap.Entry> i = Double2ShortMaps.fastIterator((Double2ShortMap)m);

         while (i.hasNext()) {
            Double2ShortMap.Entry e = i.next();
            this.put(e.getDoubleKey(), e.getShortValue());
         }
      } else {
         int n = m.size();
         Iterator<? extends Entry<? extends Double, ? extends Short>> i = m.entrySet().iterator();

         while (n-- != 0) {
            Entry<? extends Double, ? extends Short> e = (Entry<? extends Double, ? extends Short>)i.next();
            this.put(e.getKey(), e.getValue());
         }
      }
   }

   @Override
   public int hashCode() {
      int h = 0;
      int n = this.size();
      ObjectIterator<Double2ShortMap.Entry> i = Double2ShortMaps.fastIterator(this);

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
         return m.size() != this.size() ? false : this.double2ShortEntrySet().containsAll(m.entrySet());
      }
   }

   @Override
   public String toString() {
      StringBuilder s = new StringBuilder();
      ObjectIterator<Double2ShortMap.Entry> i = Double2ShortMaps.fastIterator(this);
      int n = this.size();
      boolean first = true;
      s.append("{");

      while (n-- != 0) {
         if (first) {
            first = false;
         } else {
            s.append(", ");
         }

         Double2ShortMap.Entry e = i.next();
         s.append(String.valueOf(e.getDoubleKey()));
         s.append("=>");
         s.append(String.valueOf(e.getShortValue()));
      }

      s.append("}");
      return s.toString();
   }

   public static class BasicEntry implements Double2ShortMap.Entry {
      protected double key;
      protected short value;

      public BasicEntry() {
      }

      public BasicEntry(Double key, Short value) {
         this.key = key;
         this.value = value;
      }

      public BasicEntry(double key, short value) {
         this.key = key;
         this.value = value;
      }

      @Override
      public double getDoubleKey() {
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
         } else if (o instanceof Double2ShortMap.Entry) {
            Double2ShortMap.Entry e = (Double2ShortMap.Entry)o;
            return Double.doubleToLongBits(this.key) == Double.doubleToLongBits(e.getDoubleKey()) && this.value == e.getShortValue();
         } else {
            Entry<?, ?> e = (Entry<?, ?>)o;
            Object key = e.getKey();
            if (key != null && key instanceof Double) {
               Object value = e.getValue();
               return value != null && value instanceof Short
                  ? Double.doubleToLongBits(this.key) == Double.doubleToLongBits((Double)key) && this.value == (Short)value
                  : false;
            } else {
               return false;
            }
         }
      }

      @Override
      public int hashCode() {
         return HashCommon.double2int(this.key) ^ this.value;
      }

      @Override
      public String toString() {
         return this.key + "->" + this.value;
      }
   }

   public abstract static class BasicEntrySet extends AbstractObjectSet<Double2ShortMap.Entry> {
      protected final Double2ShortMap map;

      public BasicEntrySet(Double2ShortMap map) {
         this.map = map;
      }

      @Override
      public boolean contains(Object o) {
         if (!(o instanceof Entry)) {
            return false;
         } else if (o instanceof Double2ShortMap.Entry) {
            Double2ShortMap.Entry e = (Double2ShortMap.Entry)o;
            double k = e.getDoubleKey();
            return this.map.containsKey(k) && this.map.get(k) == e.getShortValue();
         } else {
            Entry<?, ?> e = (Entry<?, ?>)o;
            Object key = e.getKey();
            if (key != null && key instanceof Double) {
               double k = (Double)key;
               Object value = e.getValue();
               return value != null && value instanceof Short ? this.map.containsKey(k) && this.map.get(k) == (Short)value : false;
            } else {
               return false;
            }
         }
      }

      @Override
      public boolean remove(Object o) {
         if (!(o instanceof Entry)) {
            return false;
         } else if (o instanceof Double2ShortMap.Entry) {
            Double2ShortMap.Entry e = (Double2ShortMap.Entry)o;
            return this.map.remove(e.getDoubleKey(), e.getShortValue());
         } else {
            Entry<?, ?> e = (Entry<?, ?>)o;
            Object key = e.getKey();
            if (key != null && key instanceof Double) {
               double k = (Double)key;
               Object value = e.getValue();
               if (value != null && value instanceof Short) {
                  short v = (Short)value;
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
      public ObjectSpliterator<Double2ShortMap.Entry> spliterator() {
         return ObjectSpliterators.asSpliterator(this.iterator(), Size64.sizeOf(this.map), 65);
      }
   }
}
