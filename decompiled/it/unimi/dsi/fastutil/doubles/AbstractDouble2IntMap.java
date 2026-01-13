package it.unimi.dsi.fastutil.doubles;

import it.unimi.dsi.fastutil.HashCommon;
import it.unimi.dsi.fastutil.Size64;
import it.unimi.dsi.fastutil.ints.AbstractIntCollection;
import it.unimi.dsi.fastutil.ints.IntBinaryOperator;
import it.unimi.dsi.fastutil.ints.IntCollection;
import it.unimi.dsi.fastutil.ints.IntIterator;
import it.unimi.dsi.fastutil.ints.IntSpliterator;
import it.unimi.dsi.fastutil.ints.IntSpliterators;
import it.unimi.dsi.fastutil.objects.AbstractObjectSet;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectSpliterator;
import it.unimi.dsi.fastutil.objects.ObjectSpliterators;
import java.io.Serializable;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.IntConsumer;

public abstract class AbstractDouble2IntMap extends AbstractDouble2IntFunction implements Double2IntMap, Serializable {
   private static final long serialVersionUID = -4940583368468432370L;

   protected AbstractDouble2IntMap() {
   }

   @Override
   public boolean containsKey(double k) {
      ObjectIterator<Double2IntMap.Entry> i = this.double2IntEntrySet().iterator();

      while (i.hasNext()) {
         if (i.next().getDoubleKey() == k) {
            return true;
         }
      }

      return false;
   }

   @Override
   public boolean containsValue(int v) {
      ObjectIterator<Double2IntMap.Entry> i = this.double2IntEntrySet().iterator();

      while (i.hasNext()) {
         if (i.next().getIntValue() == v) {
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
   public final int mergeInt(double key, int value, IntBinaryOperator remappingFunction) {
      return this.mergeInt(key, value, remappingFunction);
   }

   @Override
   public DoubleSet keySet() {
      return new AbstractDoubleSet() {
         @Override
         public boolean contains(double k) {
            return AbstractDouble2IntMap.this.containsKey(k);
         }

         @Override
         public int size() {
            return AbstractDouble2IntMap.this.size();
         }

         @Override
         public void clear() {
            AbstractDouble2IntMap.this.clear();
         }

         @Override
         public DoubleIterator iterator() {
            return new DoubleIterator() {
               private final ObjectIterator<Double2IntMap.Entry> i = Double2IntMaps.fastIterator(AbstractDouble2IntMap.this);

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
            return DoubleSpliterators.asSpliterator(this.iterator(), Size64.sizeOf(AbstractDouble2IntMap.this), 321);
         }
      };
   }

   @Override
   public IntCollection values() {
      return new AbstractIntCollection() {
         @Override
         public boolean contains(int k) {
            return AbstractDouble2IntMap.this.containsValue(k);
         }

         @Override
         public int size() {
            return AbstractDouble2IntMap.this.size();
         }

         @Override
         public void clear() {
            AbstractDouble2IntMap.this.clear();
         }

         @Override
         public IntIterator iterator() {
            return new IntIterator() {
               private final ObjectIterator<Double2IntMap.Entry> i = Double2IntMaps.fastIterator(AbstractDouble2IntMap.this);

               @Override
               public int nextInt() {
                  return this.i.next().getIntValue();
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
               public void forEachRemaining(IntConsumer action) {
                  this.i.forEachRemaining(entry -> action.accept(entry.getIntValue()));
               }
            };
         }

         @Override
         public IntSpliterator spliterator() {
            return IntSpliterators.asSpliterator(this.iterator(), Size64.sizeOf(AbstractDouble2IntMap.this), 320);
         }
      };
   }

   @Override
   public void putAll(Map<? extends Double, ? extends Integer> m) {
      if (m instanceof Double2IntMap) {
         ObjectIterator<Double2IntMap.Entry> i = Double2IntMaps.fastIterator((Double2IntMap)m);

         while (i.hasNext()) {
            Double2IntMap.Entry e = i.next();
            this.put(e.getDoubleKey(), e.getIntValue());
         }
      } else {
         int n = m.size();
         Iterator<? extends Entry<? extends Double, ? extends Integer>> i = m.entrySet().iterator();

         while (n-- != 0) {
            Entry<? extends Double, ? extends Integer> e = (Entry<? extends Double, ? extends Integer>)i.next();
            this.put(e.getKey(), e.getValue());
         }
      }
   }

   @Override
   public int hashCode() {
      int h = 0;
      int n = this.size();
      ObjectIterator<Double2IntMap.Entry> i = Double2IntMaps.fastIterator(this);

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
         return m.size() != this.size() ? false : this.double2IntEntrySet().containsAll(m.entrySet());
      }
   }

   @Override
   public String toString() {
      StringBuilder s = new StringBuilder();
      ObjectIterator<Double2IntMap.Entry> i = Double2IntMaps.fastIterator(this);
      int n = this.size();
      boolean first = true;
      s.append("{");

      while (n-- != 0) {
         if (first) {
            first = false;
         } else {
            s.append(", ");
         }

         Double2IntMap.Entry e = i.next();
         s.append(String.valueOf(e.getDoubleKey()));
         s.append("=>");
         s.append(String.valueOf(e.getIntValue()));
      }

      s.append("}");
      return s.toString();
   }

   public static class BasicEntry implements Double2IntMap.Entry {
      protected double key;
      protected int value;

      public BasicEntry() {
      }

      public BasicEntry(Double key, Integer value) {
         this.key = key;
         this.value = value;
      }

      public BasicEntry(double key, int value) {
         this.key = key;
         this.value = value;
      }

      @Override
      public double getDoubleKey() {
         return this.key;
      }

      @Override
      public int getIntValue() {
         return this.value;
      }

      @Override
      public int setValue(int value) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean equals(Object o) {
         if (!(o instanceof Entry)) {
            return false;
         } else if (o instanceof Double2IntMap.Entry) {
            Double2IntMap.Entry e = (Double2IntMap.Entry)o;
            return Double.doubleToLongBits(this.key) == Double.doubleToLongBits(e.getDoubleKey()) && this.value == e.getIntValue();
         } else {
            Entry<?, ?> e = (Entry<?, ?>)o;
            Object key = e.getKey();
            if (key != null && key instanceof Double) {
               Object value = e.getValue();
               return value != null && value instanceof Integer
                  ? Double.doubleToLongBits(this.key) == Double.doubleToLongBits((Double)key) && this.value == (Integer)value
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

   public abstract static class BasicEntrySet extends AbstractObjectSet<Double2IntMap.Entry> {
      protected final Double2IntMap map;

      public BasicEntrySet(Double2IntMap map) {
         this.map = map;
      }

      @Override
      public boolean contains(Object o) {
         if (!(o instanceof Entry)) {
            return false;
         } else if (o instanceof Double2IntMap.Entry) {
            Double2IntMap.Entry e = (Double2IntMap.Entry)o;
            double k = e.getDoubleKey();
            return this.map.containsKey(k) && this.map.get(k) == e.getIntValue();
         } else {
            Entry<?, ?> e = (Entry<?, ?>)o;
            Object key = e.getKey();
            if (key != null && key instanceof Double) {
               double k = (Double)key;
               Object value = e.getValue();
               return value != null && value instanceof Integer ? this.map.containsKey(k) && this.map.get(k) == (Integer)value : false;
            } else {
               return false;
            }
         }
      }

      @Override
      public boolean remove(Object o) {
         if (!(o instanceof Entry)) {
            return false;
         } else if (o instanceof Double2IntMap.Entry) {
            Double2IntMap.Entry e = (Double2IntMap.Entry)o;
            return this.map.remove(e.getDoubleKey(), e.getIntValue());
         } else {
            Entry<?, ?> e = (Entry<?, ?>)o;
            Object key = e.getKey();
            if (key != null && key instanceof Double) {
               double k = (Double)key;
               Object value = e.getValue();
               if (value != null && value instanceof Integer) {
                  int v = (Integer)value;
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
      public ObjectSpliterator<Double2IntMap.Entry> spliterator() {
         return ObjectSpliterators.asSpliterator(this.iterator(), Size64.sizeOf(this.map), 65);
      }
   }
}
