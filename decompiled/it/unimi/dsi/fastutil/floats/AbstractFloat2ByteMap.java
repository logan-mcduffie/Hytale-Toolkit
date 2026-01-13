package it.unimi.dsi.fastutil.floats;

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

public abstract class AbstractFloat2ByteMap extends AbstractFloat2ByteFunction implements Float2ByteMap, Serializable {
   private static final long serialVersionUID = -4940583368468432370L;

   protected AbstractFloat2ByteMap() {
   }

   @Override
   public boolean containsKey(float k) {
      ObjectIterator<Float2ByteMap.Entry> i = this.float2ByteEntrySet().iterator();

      while (i.hasNext()) {
         if (i.next().getFloatKey() == k) {
            return true;
         }
      }

      return false;
   }

   @Override
   public boolean containsValue(byte v) {
      ObjectIterator<Float2ByteMap.Entry> i = this.float2ByteEntrySet().iterator();

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
   public FloatSet keySet() {
      return new AbstractFloatSet() {
         @Override
         public boolean contains(float k) {
            return AbstractFloat2ByteMap.this.containsKey(k);
         }

         @Override
         public int size() {
            return AbstractFloat2ByteMap.this.size();
         }

         @Override
         public void clear() {
            AbstractFloat2ByteMap.this.clear();
         }

         @Override
         public FloatIterator iterator() {
            return new FloatIterator() {
               private final ObjectIterator<Float2ByteMap.Entry> i = Float2ByteMaps.fastIterator(AbstractFloat2ByteMap.this);

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
            return FloatSpliterators.asSpliterator(this.iterator(), Size64.sizeOf(AbstractFloat2ByteMap.this), 321);
         }
      };
   }

   @Override
   public ByteCollection values() {
      return new AbstractByteCollection() {
         @Override
         public boolean contains(byte k) {
            return AbstractFloat2ByteMap.this.containsValue(k);
         }

         @Override
         public int size() {
            return AbstractFloat2ByteMap.this.size();
         }

         @Override
         public void clear() {
            AbstractFloat2ByteMap.this.clear();
         }

         @Override
         public ByteIterator iterator() {
            return new ByteIterator() {
               private final ObjectIterator<Float2ByteMap.Entry> i = Float2ByteMaps.fastIterator(AbstractFloat2ByteMap.this);

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
            return ByteSpliterators.asSpliterator(this.iterator(), Size64.sizeOf(AbstractFloat2ByteMap.this), 320);
         }
      };
   }

   @Override
   public void putAll(Map<? extends Float, ? extends Byte> m) {
      if (m instanceof Float2ByteMap) {
         ObjectIterator<Float2ByteMap.Entry> i = Float2ByteMaps.fastIterator((Float2ByteMap)m);

         while (i.hasNext()) {
            Float2ByteMap.Entry e = i.next();
            this.put(e.getFloatKey(), e.getByteValue());
         }
      } else {
         int n = m.size();
         Iterator<? extends Entry<? extends Float, ? extends Byte>> i = m.entrySet().iterator();

         while (n-- != 0) {
            Entry<? extends Float, ? extends Byte> e = (Entry<? extends Float, ? extends Byte>)i.next();
            this.put(e.getKey(), e.getValue());
         }
      }
   }

   @Override
   public int hashCode() {
      int h = 0;
      int n = this.size();
      ObjectIterator<Float2ByteMap.Entry> i = Float2ByteMaps.fastIterator(this);

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
         return m.size() != this.size() ? false : this.float2ByteEntrySet().containsAll(m.entrySet());
      }
   }

   @Override
   public String toString() {
      StringBuilder s = new StringBuilder();
      ObjectIterator<Float2ByteMap.Entry> i = Float2ByteMaps.fastIterator(this);
      int n = this.size();
      boolean first = true;
      s.append("{");

      while (n-- != 0) {
         if (first) {
            first = false;
         } else {
            s.append(", ");
         }

         Float2ByteMap.Entry e = i.next();
         s.append(String.valueOf(e.getFloatKey()));
         s.append("=>");
         s.append(String.valueOf(e.getByteValue()));
      }

      s.append("}");
      return s.toString();
   }

   public static class BasicEntry implements Float2ByteMap.Entry {
      protected float key;
      protected byte value;

      public BasicEntry() {
      }

      public BasicEntry(Float key, Byte value) {
         this.key = key;
         this.value = value;
      }

      public BasicEntry(float key, byte value) {
         this.key = key;
         this.value = value;
      }

      @Override
      public float getFloatKey() {
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
         } else if (o instanceof Float2ByteMap.Entry) {
            Float2ByteMap.Entry e = (Float2ByteMap.Entry)o;
            return Float.floatToIntBits(this.key) == Float.floatToIntBits(e.getFloatKey()) && this.value == e.getByteValue();
         } else {
            Entry<?, ?> e = (Entry<?, ?>)o;
            Object key = e.getKey();
            if (key != null && key instanceof Float) {
               Object value = e.getValue();
               return value != null && value instanceof Byte
                  ? Float.floatToIntBits(this.key) == Float.floatToIntBits((Float)key) && this.value == (Byte)value
                  : false;
            } else {
               return false;
            }
         }
      }

      @Override
      public int hashCode() {
         return HashCommon.float2int(this.key) ^ this.value;
      }

      @Override
      public String toString() {
         return this.key + "->" + this.value;
      }
   }

   public abstract static class BasicEntrySet extends AbstractObjectSet<Float2ByteMap.Entry> {
      protected final Float2ByteMap map;

      public BasicEntrySet(Float2ByteMap map) {
         this.map = map;
      }

      @Override
      public boolean contains(Object o) {
         if (!(o instanceof Entry)) {
            return false;
         } else if (o instanceof Float2ByteMap.Entry) {
            Float2ByteMap.Entry e = (Float2ByteMap.Entry)o;
            float k = e.getFloatKey();
            return this.map.containsKey(k) && this.map.get(k) == e.getByteValue();
         } else {
            Entry<?, ?> e = (Entry<?, ?>)o;
            Object key = e.getKey();
            if (key != null && key instanceof Float) {
               float k = (Float)key;
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
         } else if (o instanceof Float2ByteMap.Entry) {
            Float2ByteMap.Entry e = (Float2ByteMap.Entry)o;
            return this.map.remove(e.getFloatKey(), e.getByteValue());
         } else {
            Entry<?, ?> e = (Entry<?, ?>)o;
            Object key = e.getKey();
            if (key != null && key instanceof Float) {
               float k = (Float)key;
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
      public ObjectSpliterator<Float2ByteMap.Entry> spliterator() {
         return ObjectSpliterators.asSpliterator(this.iterator(), Size64.sizeOf(this.map), 65);
      }
   }
}
