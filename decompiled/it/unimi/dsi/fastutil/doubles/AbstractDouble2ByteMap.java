package it.unimi.dsi.fastutil.doubles;

import it.unimi.dsi.fastutil.HashCommon;
import it.unimi.dsi.fastutil.Size64;
import it.unimi.dsi.fastutil.bytes.AbstractByteCollection;
import it.unimi.dsi.fastutil.bytes.ByteCollection;
import it.unimi.dsi.fastutil.bytes.ByteConsumer;
import it.unimi.dsi.fastutil.bytes.ByteIterator;
import it.unimi.dsi.fastutil.bytes.ByteSpliterator;
import it.unimi.dsi.fastutil.bytes.ByteSpliterators;
import it.unimi.dsi.fastutil.objects.AbstractObjectSet;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectSpliterator;
import it.unimi.dsi.fastutil.objects.ObjectSpliterators;
import java.io.Serializable;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

public abstract class AbstractDouble2ByteMap extends AbstractDouble2ByteFunction implements Double2ByteMap, Serializable {
   private static final long serialVersionUID = -4940583368468432370L;

   protected AbstractDouble2ByteMap() {
   }

   @Override
   public boolean containsKey(double k) {
      ObjectIterator<Double2ByteMap.Entry> i = this.double2ByteEntrySet().iterator();

      while (i.hasNext()) {
         if (i.next().getDoubleKey() == k) {
            return true;
         }
      }

      return false;
   }

   @Override
   public boolean containsValue(byte v) {
      ObjectIterator<Double2ByteMap.Entry> i = this.double2ByteEntrySet().iterator();

      while (i.hasNext()) {
         if (i.next().getByteValue() == v) {
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
            return AbstractDouble2ByteMap.this.containsKey(k);
         }

         @Override
         public int size() {
            return AbstractDouble2ByteMap.this.size();
         }

         @Override
         public void clear() {
            AbstractDouble2ByteMap.this.clear();
         }

         @Override
         public DoubleIterator iterator() {
            return new DoubleIterator() {
               private final ObjectIterator<Double2ByteMap.Entry> i = Double2ByteMaps.fastIterator(AbstractDouble2ByteMap.this);

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
            return DoubleSpliterators.asSpliterator(this.iterator(), Size64.sizeOf(AbstractDouble2ByteMap.this), 321);
         }
      };
   }

   @Override
   public ByteCollection values() {
      return new AbstractByteCollection() {
         @Override
         public boolean contains(byte k) {
            return AbstractDouble2ByteMap.this.containsValue(k);
         }

         @Override
         public int size() {
            return AbstractDouble2ByteMap.this.size();
         }

         @Override
         public void clear() {
            AbstractDouble2ByteMap.this.clear();
         }

         @Override
         public ByteIterator iterator() {
            return new ByteIterator() {
               private final ObjectIterator<Double2ByteMap.Entry> i = Double2ByteMaps.fastIterator(AbstractDouble2ByteMap.this);

               @Override
               public byte nextByte() {
                  return this.i.next().getByteValue();
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
                  this.i.forEachRemaining(entry -> action.accept(entry.getByteValue()));
               }
            };
         }

         @Override
         public ByteSpliterator spliterator() {
            return ByteSpliterators.asSpliterator(this.iterator(), Size64.sizeOf(AbstractDouble2ByteMap.this), 320);
         }
      };
   }

   @Override
   public void putAll(Map<? extends Double, ? extends Byte> m) {
      if (m instanceof Double2ByteMap) {
         ObjectIterator<Double2ByteMap.Entry> i = Double2ByteMaps.fastIterator((Double2ByteMap)m);

         while (i.hasNext()) {
            Double2ByteMap.Entry e = i.next();
            this.put(e.getDoubleKey(), e.getByteValue());
         }
      } else {
         int n = m.size();
         Iterator<? extends Entry<? extends Double, ? extends Byte>> i = m.entrySet().iterator();

         while (n-- != 0) {
            Entry<? extends Double, ? extends Byte> e = (Entry<? extends Double, ? extends Byte>)i.next();
            this.put(e.getKey(), e.getValue());
         }
      }
   }

   @Override
   public int hashCode() {
      int h = 0;
      int n = this.size();
      ObjectIterator<Double2ByteMap.Entry> i = Double2ByteMaps.fastIterator(this);

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
         return m.size() != this.size() ? false : this.double2ByteEntrySet().containsAll(m.entrySet());
      }
   }

   @Override
   public String toString() {
      StringBuilder s = new StringBuilder();
      ObjectIterator<Double2ByteMap.Entry> i = Double2ByteMaps.fastIterator(this);
      int n = this.size();
      boolean first = true;
      s.append("{");

      while (n-- != 0) {
         if (first) {
            first = false;
         } else {
            s.append(", ");
         }

         Double2ByteMap.Entry e = i.next();
         s.append(String.valueOf(e.getDoubleKey()));
         s.append("=>");
         s.append(String.valueOf(e.getByteValue()));
      }

      s.append("}");
      return s.toString();
   }

   public static class BasicEntry implements Double2ByteMap.Entry {
      protected double key;
      protected byte value;

      public BasicEntry() {
      }

      public BasicEntry(Double key, Byte value) {
         this.key = key;
         this.value = value;
      }

      public BasicEntry(double key, byte value) {
         this.key = key;
         this.value = value;
      }

      @Override
      public double getDoubleKey() {
         return this.key;
      }

      @Override
      public byte getByteValue() {
         return this.value;
      }

      @Override
      public byte setValue(byte value) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean equals(Object o) {
         if (!(o instanceof Entry)) {
            return false;
         } else if (o instanceof Double2ByteMap.Entry) {
            Double2ByteMap.Entry e = (Double2ByteMap.Entry)o;
            return Double.doubleToLongBits(this.key) == Double.doubleToLongBits(e.getDoubleKey()) && this.value == e.getByteValue();
         } else {
            Entry<?, ?> e = (Entry<?, ?>)o;
            Object key = e.getKey();
            if (key != null && key instanceof Double) {
               Object value = e.getValue();
               return value != null && value instanceof Byte
                  ? Double.doubleToLongBits(this.key) == Double.doubleToLongBits((Double)key) && this.value == (Byte)value
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

   public abstract static class BasicEntrySet extends AbstractObjectSet<Double2ByteMap.Entry> {
      protected final Double2ByteMap map;

      public BasicEntrySet(Double2ByteMap map) {
         this.map = map;
      }

      @Override
      public boolean contains(Object o) {
         if (!(o instanceof Entry)) {
            return false;
         } else if (o instanceof Double2ByteMap.Entry) {
            Double2ByteMap.Entry e = (Double2ByteMap.Entry)o;
            double k = e.getDoubleKey();
            return this.map.containsKey(k) && this.map.get(k) == e.getByteValue();
         } else {
            Entry<?, ?> e = (Entry<?, ?>)o;
            Object key = e.getKey();
            if (key != null && key instanceof Double) {
               double k = (Double)key;
               Object value = e.getValue();
               return value != null && value instanceof Byte ? this.map.containsKey(k) && this.map.get(k) == (Byte)value : false;
            } else {
               return false;
            }
         }
      }

      @Override
      public boolean remove(Object o) {
         if (!(o instanceof Entry)) {
            return false;
         } else if (o instanceof Double2ByteMap.Entry) {
            Double2ByteMap.Entry e = (Double2ByteMap.Entry)o;
            return this.map.remove(e.getDoubleKey(), e.getByteValue());
         } else {
            Entry<?, ?> e = (Entry<?, ?>)o;
            Object key = e.getKey();
            if (key != null && key instanceof Double) {
               double k = (Double)key;
               Object value = e.getValue();
               if (value != null && value instanceof Byte) {
                  byte v = (Byte)value;
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
      public ObjectSpliterator<Double2ByteMap.Entry> spliterator() {
         return ObjectSpliterators.asSpliterator(this.iterator(), Size64.sizeOf(this.map), 65);
      }
   }
}
