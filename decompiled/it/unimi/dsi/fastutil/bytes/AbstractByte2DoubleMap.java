package it.unimi.dsi.fastutil.bytes;

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

public abstract class AbstractByte2DoubleMap extends AbstractByte2DoubleFunction implements Byte2DoubleMap, Serializable {
   private static final long serialVersionUID = -4940583368468432370L;

   protected AbstractByte2DoubleMap() {
   }

   @Override
   public boolean containsKey(byte k) {
      ObjectIterator<Byte2DoubleMap.Entry> i = this.byte2DoubleEntrySet().iterator();

      while (i.hasNext()) {
         if (i.next().getByteKey() == k) {
            return true;
         }
      }

      return false;
   }

   @Override
   public boolean containsValue(double v) {
      ObjectIterator<Byte2DoubleMap.Entry> i = this.byte2DoubleEntrySet().iterator();

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
   public final double mergeDouble(byte key, double value, DoubleBinaryOperator remappingFunction) {
      return this.mergeDouble(key, value, remappingFunction);
   }

   @Override
   public ByteSet keySet() {
      return new AbstractByteSet() {
         @Override
         public boolean contains(byte k) {
            return AbstractByte2DoubleMap.this.containsKey(k);
         }

         @Override
         public int size() {
            return AbstractByte2DoubleMap.this.size();
         }

         @Override
         public void clear() {
            AbstractByte2DoubleMap.this.clear();
         }

         @Override
         public ByteIterator iterator() {
            return new ByteIterator() {
               private final ObjectIterator<Byte2DoubleMap.Entry> i = Byte2DoubleMaps.fastIterator(AbstractByte2DoubleMap.this);

               @Override
               public byte nextByte() {
                  return this.i.next().getByteKey();
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
               public void forEachRemaining(ByteConsumer action) {
                  this.i.forEachRemaining(entry -> action.accept(entry.getByteKey()));
               }
            };
         }

         @Override
         public ByteSpliterator spliterator() {
            return ByteSpliterators.asSpliterator(this.iterator(), Size64.sizeOf(AbstractByte2DoubleMap.this), 321);
         }
      };
   }

   @Override
   public DoubleCollection values() {
      return new AbstractDoubleCollection() {
         @Override
         public boolean contains(double k) {
            return AbstractByte2DoubleMap.this.containsValue(k);
         }

         @Override
         public int size() {
            return AbstractByte2DoubleMap.this.size();
         }

         @Override
         public void clear() {
            AbstractByte2DoubleMap.this.clear();
         }

         @Override
         public DoubleIterator iterator() {
            return new DoubleIterator() {
               private final ObjectIterator<Byte2DoubleMap.Entry> i = Byte2DoubleMaps.fastIterator(AbstractByte2DoubleMap.this);

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
            return DoubleSpliterators.asSpliterator(this.iterator(), Size64.sizeOf(AbstractByte2DoubleMap.this), 320);
         }
      };
   }

   @Override
   public void putAll(Map<? extends Byte, ? extends Double> m) {
      if (m instanceof Byte2DoubleMap) {
         ObjectIterator<Byte2DoubleMap.Entry> i = Byte2DoubleMaps.fastIterator((Byte2DoubleMap)m);

         while (i.hasNext()) {
            Byte2DoubleMap.Entry e = i.next();
            this.put(e.getByteKey(), e.getDoubleValue());
         }
      } else {
         int n = m.size();
         Iterator<? extends Entry<? extends Byte, ? extends Double>> i = m.entrySet().iterator();

         while (n-- != 0) {
            Entry<? extends Byte, ? extends Double> e = (Entry<? extends Byte, ? extends Double>)i.next();
            this.put(e.getKey(), e.getValue());
         }
      }
   }

   @Override
   public int hashCode() {
      int h = 0;
      int n = this.size();
      ObjectIterator<Byte2DoubleMap.Entry> i = Byte2DoubleMaps.fastIterator(this);

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
         return m.size() != this.size() ? false : this.byte2DoubleEntrySet().containsAll(m.entrySet());
      }
   }

   @Override
   public String toString() {
      StringBuilder s = new StringBuilder();
      ObjectIterator<Byte2DoubleMap.Entry> i = Byte2DoubleMaps.fastIterator(this);
      int n = this.size();
      boolean first = true;
      s.append("{");

      while (n-- != 0) {
         if (first) {
            first = false;
         } else {
            s.append(", ");
         }

         Byte2DoubleMap.Entry e = i.next();
         s.append(String.valueOf(e.getByteKey()));
         s.append("=>");
         s.append(String.valueOf(e.getDoubleValue()));
      }

      s.append("}");
      return s.toString();
   }

   public static class BasicEntry implements Byte2DoubleMap.Entry {
      protected byte key;
      protected double value;

      public BasicEntry() {
      }

      public BasicEntry(Byte key, Double value) {
         this.key = key;
         this.value = value;
      }

      public BasicEntry(byte key, double value) {
         this.key = key;
         this.value = value;
      }

      @Override
      public byte getByteKey() {
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
         } else if (o instanceof Byte2DoubleMap.Entry) {
            Byte2DoubleMap.Entry e = (Byte2DoubleMap.Entry)o;
            return this.key == e.getByteKey() && Double.doubleToLongBits(this.value) == Double.doubleToLongBits(e.getDoubleValue());
         } else {
            Entry<?, ?> e = (Entry<?, ?>)o;
            Object key = e.getKey();
            if (key != null && key instanceof Byte) {
               Object value = e.getValue();
               return value != null && value instanceof Double
                  ? this.key == (Byte)key && Double.doubleToLongBits(this.value) == Double.doubleToLongBits((Double)value)
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

   public abstract static class BasicEntrySet extends AbstractObjectSet<Byte2DoubleMap.Entry> {
      protected final Byte2DoubleMap map;

      public BasicEntrySet(Byte2DoubleMap map) {
         this.map = map;
      }

      @Override
      public boolean contains(Object o) {
         if (!(o instanceof Entry)) {
            return false;
         } else if (o instanceof Byte2DoubleMap.Entry) {
            Byte2DoubleMap.Entry e = (Byte2DoubleMap.Entry)o;
            byte k = e.getByteKey();
            return this.map.containsKey(k) && Double.doubleToLongBits(this.map.get(k)) == Double.doubleToLongBits(e.getDoubleValue());
         } else {
            Entry<?, ?> e = (Entry<?, ?>)o;
            Object key = e.getKey();
            if (key != null && key instanceof Byte) {
               byte k = (Byte)key;
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
         } else if (o instanceof Byte2DoubleMap.Entry) {
            Byte2DoubleMap.Entry e = (Byte2DoubleMap.Entry)o;
            return this.map.remove(e.getByteKey(), e.getDoubleValue());
         } else {
            Entry<?, ?> e = (Entry<?, ?>)o;
            Object key = e.getKey();
            if (key != null && key instanceof Byte) {
               byte k = (Byte)key;
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
      public ObjectSpliterator<Byte2DoubleMap.Entry> spliterator() {
         return ObjectSpliterators.asSpliterator(this.iterator(), Size64.sizeOf(this.map), 65);
      }
   }
}
