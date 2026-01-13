package it.unimi.dsi.fastutil.bytes;

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

public abstract class AbstractByte2FloatMap extends AbstractByte2FloatFunction implements Byte2FloatMap, Serializable {
   private static final long serialVersionUID = -4940583368468432370L;

   protected AbstractByte2FloatMap() {
   }

   @Override
   public boolean containsKey(byte k) {
      ObjectIterator<Byte2FloatMap.Entry> i = this.byte2FloatEntrySet().iterator();

      while (i.hasNext()) {
         if (i.next().getByteKey() == k) {
            return true;
         }
      }

      return false;
   }

   @Override
   public boolean containsValue(float v) {
      ObjectIterator<Byte2FloatMap.Entry> i = this.byte2FloatEntrySet().iterator();

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
   public ByteSet keySet() {
      return new AbstractByteSet() {
         @Override
         public boolean contains(byte k) {
            return AbstractByte2FloatMap.this.containsKey(k);
         }

         @Override
         public int size() {
            return AbstractByte2FloatMap.this.size();
         }

         @Override
         public void clear() {
            AbstractByte2FloatMap.this.clear();
         }

         @Override
         public ByteIterator iterator() {
            return new ByteIterator() {
               private final ObjectIterator<Byte2FloatMap.Entry> i = Byte2FloatMaps.fastIterator(AbstractByte2FloatMap.this);

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
            return ByteSpliterators.asSpliterator(this.iterator(), Size64.sizeOf(AbstractByte2FloatMap.this), 321);
         }
      };
   }

   @Override
   public FloatCollection values() {
      return new AbstractFloatCollection() {
         @Override
         public boolean contains(float k) {
            return AbstractByte2FloatMap.this.containsValue(k);
         }

         @Override
         public int size() {
            return AbstractByte2FloatMap.this.size();
         }

         @Override
         public void clear() {
            AbstractByte2FloatMap.this.clear();
         }

         @Override
         public FloatIterator iterator() {
            return new FloatIterator() {
               private final ObjectIterator<Byte2FloatMap.Entry> i = Byte2FloatMaps.fastIterator(AbstractByte2FloatMap.this);

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
            return FloatSpliterators.asSpliterator(this.iterator(), Size64.sizeOf(AbstractByte2FloatMap.this), 320);
         }
      };
   }

   @Override
   public void putAll(Map<? extends Byte, ? extends Float> m) {
      if (m instanceof Byte2FloatMap) {
         ObjectIterator<Byte2FloatMap.Entry> i = Byte2FloatMaps.fastIterator((Byte2FloatMap)m);

         while (i.hasNext()) {
            Byte2FloatMap.Entry e = i.next();
            this.put(e.getByteKey(), e.getFloatValue());
         }
      } else {
         int n = m.size();
         Iterator<? extends Entry<? extends Byte, ? extends Float>> i = m.entrySet().iterator();

         while (n-- != 0) {
            Entry<? extends Byte, ? extends Float> e = (Entry<? extends Byte, ? extends Float>)i.next();
            this.put(e.getKey(), e.getValue());
         }
      }
   }

   @Override
   public int hashCode() {
      int h = 0;
      int n = this.size();
      ObjectIterator<Byte2FloatMap.Entry> i = Byte2FloatMaps.fastIterator(this);

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
         return m.size() != this.size() ? false : this.byte2FloatEntrySet().containsAll(m.entrySet());
      }
   }

   @Override
   public String toString() {
      StringBuilder s = new StringBuilder();
      ObjectIterator<Byte2FloatMap.Entry> i = Byte2FloatMaps.fastIterator(this);
      int n = this.size();
      boolean first = true;
      s.append("{");

      while (n-- != 0) {
         if (first) {
            first = false;
         } else {
            s.append(", ");
         }

         Byte2FloatMap.Entry e = i.next();
         s.append(String.valueOf(e.getByteKey()));
         s.append("=>");
         s.append(String.valueOf(e.getFloatValue()));
      }

      s.append("}");
      return s.toString();
   }

   public static class BasicEntry implements Byte2FloatMap.Entry {
      protected byte key;
      protected float value;

      public BasicEntry() {
      }

      public BasicEntry(Byte key, Float value) {
         this.key = key;
         this.value = value;
      }

      public BasicEntry(byte key, float value) {
         this.key = key;
         this.value = value;
      }

      @Override
      public byte getByteKey() {
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
         } else if (o instanceof Byte2FloatMap.Entry) {
            Byte2FloatMap.Entry e = (Byte2FloatMap.Entry)o;
            return this.key == e.getByteKey() && Float.floatToIntBits(this.value) == Float.floatToIntBits(e.getFloatValue());
         } else {
            Entry<?, ?> e = (Entry<?, ?>)o;
            Object key = e.getKey();
            if (key != null && key instanceof Byte) {
               Object value = e.getValue();
               return value != null && value instanceof Float
                  ? this.key == (Byte)key && Float.floatToIntBits(this.value) == Float.floatToIntBits((Float)value)
                  : false;
            } else {
               return false;
            }
         }
      }

      @Override
      public int hashCode() {
         return this.key ^ HashCommon.float2int(this.value);
      }

      @Override
      public String toString() {
         return this.key + "->" + this.value;
      }
   }

   public abstract static class BasicEntrySet extends AbstractObjectSet<Byte2FloatMap.Entry> {
      protected final Byte2FloatMap map;

      public BasicEntrySet(Byte2FloatMap map) {
         this.map = map;
      }

      @Override
      public boolean contains(Object o) {
         if (!(o instanceof Entry)) {
            return false;
         } else if (o instanceof Byte2FloatMap.Entry) {
            Byte2FloatMap.Entry e = (Byte2FloatMap.Entry)o;
            byte k = e.getByteKey();
            return this.map.containsKey(k) && Float.floatToIntBits(this.map.get(k)) == Float.floatToIntBits(e.getFloatValue());
         } else {
            Entry<?, ?> e = (Entry<?, ?>)o;
            Object key = e.getKey();
            if (key != null && key instanceof Byte) {
               byte k = (Byte)key;
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
         } else if (o instanceof Byte2FloatMap.Entry) {
            Byte2FloatMap.Entry e = (Byte2FloatMap.Entry)o;
            return this.map.remove(e.getByteKey(), e.getFloatValue());
         } else {
            Entry<?, ?> e = (Entry<?, ?>)o;
            Object key = e.getKey();
            if (key != null && key instanceof Byte) {
               byte k = (Byte)key;
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
      public ObjectSpliterator<Byte2FloatMap.Entry> spliterator() {
         return ObjectSpliterators.asSpliterator(this.iterator(), Size64.sizeOf(this.map), 65);
      }
   }
}
