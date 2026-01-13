package it.unimi.dsi.fastutil.longs;

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

public abstract class AbstractLong2DoubleMap extends AbstractLong2DoubleFunction implements Long2DoubleMap, Serializable {
   private static final long serialVersionUID = -4940583368468432370L;

   protected AbstractLong2DoubleMap() {
   }

   @Override
   public boolean containsKey(long k) {
      ObjectIterator<Long2DoubleMap.Entry> i = this.long2DoubleEntrySet().iterator();

      while (i.hasNext()) {
         if (i.next().getLongKey() == k) {
            return true;
         }
      }

      return false;
   }

   @Override
   public boolean containsValue(double v) {
      ObjectIterator<Long2DoubleMap.Entry> i = this.long2DoubleEntrySet().iterator();

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
   public final double mergeDouble(long key, double value, DoubleBinaryOperator remappingFunction) {
      return this.mergeDouble(key, value, remappingFunction);
   }

   @Override
   public LongSet keySet() {
      return new AbstractLongSet() {
         @Override
         public boolean contains(long k) {
            return AbstractLong2DoubleMap.this.containsKey(k);
         }

         @Override
         public int size() {
            return AbstractLong2DoubleMap.this.size();
         }

         @Override
         public void clear() {
            AbstractLong2DoubleMap.this.clear();
         }

         @Override
         public LongIterator iterator() {
            return new LongIterator() {
               private final ObjectIterator<Long2DoubleMap.Entry> i = Long2DoubleMaps.fastIterator(AbstractLong2DoubleMap.this);

               @Override
               public long nextLong() {
                  return this.i.next().getLongKey();
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
               public void forEachRemaining(java.util.function.LongConsumer action) {
                  this.i.forEachRemaining(entry -> action.accept(entry.getLongKey()));
               }
            };
         }

         @Override
         public LongSpliterator spliterator() {
            return LongSpliterators.asSpliterator(this.iterator(), Size64.sizeOf(AbstractLong2DoubleMap.this), 321);
         }
      };
   }

   @Override
   public DoubleCollection values() {
      return new AbstractDoubleCollection() {
         @Override
         public boolean contains(double k) {
            return AbstractLong2DoubleMap.this.containsValue(k);
         }

         @Override
         public int size() {
            return AbstractLong2DoubleMap.this.size();
         }

         @Override
         public void clear() {
            AbstractLong2DoubleMap.this.clear();
         }

         @Override
         public DoubleIterator iterator() {
            return new DoubleIterator() {
               private final ObjectIterator<Long2DoubleMap.Entry> i = Long2DoubleMaps.fastIterator(AbstractLong2DoubleMap.this);

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
            return DoubleSpliterators.asSpliterator(this.iterator(), Size64.sizeOf(AbstractLong2DoubleMap.this), 320);
         }
      };
   }

   @Override
   public void putAll(Map<? extends Long, ? extends Double> m) {
      if (m instanceof Long2DoubleMap) {
         ObjectIterator<Long2DoubleMap.Entry> i = Long2DoubleMaps.fastIterator((Long2DoubleMap)m);

         while (i.hasNext()) {
            Long2DoubleMap.Entry e = i.next();
            this.put(e.getLongKey(), e.getDoubleValue());
         }
      } else {
         int n = m.size();
         Iterator<? extends Entry<? extends Long, ? extends Double>> i = m.entrySet().iterator();

         while (n-- != 0) {
            Entry<? extends Long, ? extends Double> e = (Entry<? extends Long, ? extends Double>)i.next();
            this.put(e.getKey(), e.getValue());
         }
      }
   }

   @Override
   public int hashCode() {
      int h = 0;
      int n = this.size();
      ObjectIterator<Long2DoubleMap.Entry> i = Long2DoubleMaps.fastIterator(this);

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
         return m.size() != this.size() ? false : this.long2DoubleEntrySet().containsAll(m.entrySet());
      }
   }

   @Override
   public String toString() {
      StringBuilder s = new StringBuilder();
      ObjectIterator<Long2DoubleMap.Entry> i = Long2DoubleMaps.fastIterator(this);
      int n = this.size();
      boolean first = true;
      s.append("{");

      while (n-- != 0) {
         if (first) {
            first = false;
         } else {
            s.append(", ");
         }

         Long2DoubleMap.Entry e = i.next();
         s.append(String.valueOf(e.getLongKey()));
         s.append("=>");
         s.append(String.valueOf(e.getDoubleValue()));
      }

      s.append("}");
      return s.toString();
   }

   public static class BasicEntry implements Long2DoubleMap.Entry {
      protected long key;
      protected double value;

      public BasicEntry() {
      }

      public BasicEntry(Long key, Double value) {
         this.key = key;
         this.value = value;
      }

      public BasicEntry(long key, double value) {
         this.key = key;
         this.value = value;
      }

      @Override
      public long getLongKey() {
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
         } else if (o instanceof Long2DoubleMap.Entry) {
            Long2DoubleMap.Entry e = (Long2DoubleMap.Entry)o;
            return this.key == e.getLongKey() && Double.doubleToLongBits(this.value) == Double.doubleToLongBits(e.getDoubleValue());
         } else {
            Entry<?, ?> e = (Entry<?, ?>)o;
            Object key = e.getKey();
            if (key != null && key instanceof Long) {
               Object value = e.getValue();
               return value != null && value instanceof Double
                  ? this.key == (Long)key && Double.doubleToLongBits(this.value) == Double.doubleToLongBits((Double)value)
                  : false;
            } else {
               return false;
            }
         }
      }

      @Override
      public int hashCode() {
         return HashCommon.long2int(this.key) ^ HashCommon.double2int(this.value);
      }

      @Override
      public String toString() {
         return this.key + "->" + this.value;
      }
   }

   public abstract static class BasicEntrySet extends AbstractObjectSet<Long2DoubleMap.Entry> {
      protected final Long2DoubleMap map;

      public BasicEntrySet(Long2DoubleMap map) {
         this.map = map;
      }

      @Override
      public boolean contains(Object o) {
         if (!(o instanceof Entry)) {
            return false;
         } else if (o instanceof Long2DoubleMap.Entry) {
            Long2DoubleMap.Entry e = (Long2DoubleMap.Entry)o;
            long k = e.getLongKey();
            return this.map.containsKey(k) && Double.doubleToLongBits(this.map.get(k)) == Double.doubleToLongBits(e.getDoubleValue());
         } else {
            Entry<?, ?> e = (Entry<?, ?>)o;
            Object key = e.getKey();
            if (key != null && key instanceof Long) {
               long k = (Long)key;
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
         } else if (o instanceof Long2DoubleMap.Entry) {
            Long2DoubleMap.Entry e = (Long2DoubleMap.Entry)o;
            return this.map.remove(e.getLongKey(), e.getDoubleValue());
         } else {
            Entry<?, ?> e = (Entry<?, ?>)o;
            Object key = e.getKey();
            if (key != null && key instanceof Long) {
               long k = (Long)key;
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
      public ObjectSpliterator<Long2DoubleMap.Entry> spliterator() {
         return ObjectSpliterators.asSpliterator(this.iterator(), Size64.sizeOf(this.map), 65);
      }
   }
}
