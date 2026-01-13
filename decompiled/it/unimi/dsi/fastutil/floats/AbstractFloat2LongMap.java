package it.unimi.dsi.fastutil.floats;

import it.unimi.dsi.fastutil.HashCommon;
import it.unimi.dsi.fastutil.Size64;
import it.unimi.dsi.fastutil.longs.AbstractLongCollection;
import it.unimi.dsi.fastutil.longs.LongBinaryOperator;
import it.unimi.dsi.fastutil.longs.LongCollection;
import it.unimi.dsi.fastutil.longs.LongIterator;
import it.unimi.dsi.fastutil.longs.LongSpliterator;
import it.unimi.dsi.fastutil.longs.LongSpliterators;
import it.unimi.dsi.fastutil.objects.AbstractObjectSet;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectSpliterator;
import it.unimi.dsi.fastutil.objects.ObjectSpliterators;
import java.io.Serializable;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.LongConsumer;

public abstract class AbstractFloat2LongMap extends AbstractFloat2LongFunction implements Float2LongMap, Serializable {
   private static final long serialVersionUID = -4940583368468432370L;

   protected AbstractFloat2LongMap() {
   }

   @Override
   public boolean containsKey(float k) {
      ObjectIterator<Float2LongMap.Entry> i = this.float2LongEntrySet().iterator();

      while (i.hasNext()) {
         if (i.next().getFloatKey() == k) {
            return true;
         }
      }

      return false;
   }

   @Override
   public boolean containsValue(long v) {
      ObjectIterator<Float2LongMap.Entry> i = this.float2LongEntrySet().iterator();

      while (i.hasNext()) {
         if (i.next().getLongValue() == v) {
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
   public final long mergeLong(float key, long value, LongBinaryOperator remappingFunction) {
      return this.mergeLong(key, value, remappingFunction);
   }

   @Override
   public FloatSet keySet() {
      return new AbstractFloatSet() {
         @Override
         public boolean contains(float k) {
            return AbstractFloat2LongMap.this.containsKey(k);
         }

         @Override
         public int size() {
            return AbstractFloat2LongMap.this.size();
         }

         @Override
         public void clear() {
            AbstractFloat2LongMap.this.clear();
         }

         @Override
         public FloatIterator iterator() {
            return new FloatIterator() {
               private final ObjectIterator<Float2LongMap.Entry> i = Float2LongMaps.fastIterator(AbstractFloat2LongMap.this);

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
            return FloatSpliterators.asSpliterator(this.iterator(), Size64.sizeOf(AbstractFloat2LongMap.this), 321);
         }
      };
   }

   @Override
   public LongCollection values() {
      return new AbstractLongCollection() {
         @Override
         public boolean contains(long k) {
            return AbstractFloat2LongMap.this.containsValue(k);
         }

         @Override
         public int size() {
            return AbstractFloat2LongMap.this.size();
         }

         @Override
         public void clear() {
            AbstractFloat2LongMap.this.clear();
         }

         @Override
         public LongIterator iterator() {
            return new LongIterator() {
               private final ObjectIterator<Float2LongMap.Entry> i = Float2LongMaps.fastIterator(AbstractFloat2LongMap.this);

               @Override
               public long nextLong() {
                  return this.i.next().getLongValue();
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
               public void forEachRemaining(LongConsumer action) {
                  this.i.forEachRemaining(entry -> action.accept(entry.getLongValue()));
               }
            };
         }

         @Override
         public LongSpliterator spliterator() {
            return LongSpliterators.asSpliterator(this.iterator(), Size64.sizeOf(AbstractFloat2LongMap.this), 320);
         }
      };
   }

   @Override
   public void putAll(Map<? extends Float, ? extends Long> m) {
      if (m instanceof Float2LongMap) {
         ObjectIterator<Float2LongMap.Entry> i = Float2LongMaps.fastIterator((Float2LongMap)m);

         while (i.hasNext()) {
            Float2LongMap.Entry e = i.next();
            this.put(e.getFloatKey(), e.getLongValue());
         }
      } else {
         int n = m.size();
         Iterator<? extends Entry<? extends Float, ? extends Long>> i = m.entrySet().iterator();

         while (n-- != 0) {
            Entry<? extends Float, ? extends Long> e = (Entry<? extends Float, ? extends Long>)i.next();
            this.put(e.getKey(), e.getValue());
         }
      }
   }

   @Override
   public int hashCode() {
      int h = 0;
      int n = this.size();
      ObjectIterator<Float2LongMap.Entry> i = Float2LongMaps.fastIterator(this);

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
         return m.size() != this.size() ? false : this.float2LongEntrySet().containsAll(m.entrySet());
      }
   }

   @Override
   public String toString() {
      StringBuilder s = new StringBuilder();
      ObjectIterator<Float2LongMap.Entry> i = Float2LongMaps.fastIterator(this);
      int n = this.size();
      boolean first = true;
      s.append("{");

      while (n-- != 0) {
         if (first) {
            first = false;
         } else {
            s.append(", ");
         }

         Float2LongMap.Entry e = i.next();
         s.append(String.valueOf(e.getFloatKey()));
         s.append("=>");
         s.append(String.valueOf(e.getLongValue()));
      }

      s.append("}");
      return s.toString();
   }

   public static class BasicEntry implements Float2LongMap.Entry {
      protected float key;
      protected long value;

      public BasicEntry() {
      }

      public BasicEntry(Float key, Long value) {
         this.key = key;
         this.value = value;
      }

      public BasicEntry(float key, long value) {
         this.key = key;
         this.value = value;
      }

      @Override
      public float getFloatKey() {
         return this.key;
      }

      @Override
      public long getLongValue() {
         return this.value;
      }

      @Override
      public long setValue(long value) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean equals(Object o) {
         if (!(o instanceof Entry)) {
            return false;
         } else if (o instanceof Float2LongMap.Entry) {
            Float2LongMap.Entry e = (Float2LongMap.Entry)o;
            return Float.floatToIntBits(this.key) == Float.floatToIntBits(e.getFloatKey()) && this.value == e.getLongValue();
         } else {
            Entry<?, ?> e = (Entry<?, ?>)o;
            Object key = e.getKey();
            if (key != null && key instanceof Float) {
               Object value = e.getValue();
               return value != null && value instanceof Long
                  ? Float.floatToIntBits(this.key) == Float.floatToIntBits((Float)key) && this.value == (Long)value
                  : false;
            } else {
               return false;
            }
         }
      }

      @Override
      public int hashCode() {
         return HashCommon.float2int(this.key) ^ HashCommon.long2int(this.value);
      }

      @Override
      public String toString() {
         return this.key + "->" + this.value;
      }
   }

   public abstract static class BasicEntrySet extends AbstractObjectSet<Float2LongMap.Entry> {
      protected final Float2LongMap map;

      public BasicEntrySet(Float2LongMap map) {
         this.map = map;
      }

      @Override
      public boolean contains(Object o) {
         if (!(o instanceof Entry)) {
            return false;
         } else if (o instanceof Float2LongMap.Entry) {
            Float2LongMap.Entry e = (Float2LongMap.Entry)o;
            float k = e.getFloatKey();
            return this.map.containsKey(k) && this.map.get(k) == e.getLongValue();
         } else {
            Entry<?, ?> e = (Entry<?, ?>)o;
            Object key = e.getKey();
            if (key != null && key instanceof Float) {
               float k = (Float)key;
               Object value = e.getValue();
               return value != null && value instanceof Long ? this.map.containsKey(k) && this.map.get(k) == (Long)value : false;
            } else {
               return false;
            }
         }
      }

      @Override
      public boolean remove(Object o) {
         if (!(o instanceof Entry)) {
            return false;
         } else if (o instanceof Float2LongMap.Entry) {
            Float2LongMap.Entry e = (Float2LongMap.Entry)o;
            return this.map.remove(e.getFloatKey(), e.getLongValue());
         } else {
            Entry<?, ?> e = (Entry<?, ?>)o;
            Object key = e.getKey();
            if (key != null && key instanceof Float) {
               float k = (Float)key;
               Object value = e.getValue();
               if (value != null && value instanceof Long) {
                  long v = (Long)value;
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
      public ObjectSpliterator<Float2LongMap.Entry> spliterator() {
         return ObjectSpliterators.asSpliterator(this.iterator(), Size64.sizeOf(this.map), 65);
      }
   }
}
