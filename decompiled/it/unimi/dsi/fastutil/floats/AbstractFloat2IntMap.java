package it.unimi.dsi.fastutil.floats;

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

public abstract class AbstractFloat2IntMap extends AbstractFloat2IntFunction implements Float2IntMap, Serializable {
   private static final long serialVersionUID = -4940583368468432370L;

   protected AbstractFloat2IntMap() {
   }

   @Override
   public boolean containsKey(float k) {
      ObjectIterator<Float2IntMap.Entry> i = this.float2IntEntrySet().iterator();

      while (i.hasNext()) {
         if (i.next().getFloatKey() == k) {
            return true;
         }
      }

      return false;
   }

   @Override
   public boolean containsValue(int v) {
      ObjectIterator<Float2IntMap.Entry> i = this.float2IntEntrySet().iterator();

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
   public final int mergeInt(float key, int value, IntBinaryOperator remappingFunction) {
      return this.mergeInt(key, value, remappingFunction);
   }

   @Override
   public FloatSet keySet() {
      return new AbstractFloatSet() {
         @Override
         public boolean contains(float k) {
            return AbstractFloat2IntMap.this.containsKey(k);
         }

         @Override
         public int size() {
            return AbstractFloat2IntMap.this.size();
         }

         @Override
         public void clear() {
            AbstractFloat2IntMap.this.clear();
         }

         @Override
         public FloatIterator iterator() {
            return new FloatIterator() {
               private final ObjectIterator<Float2IntMap.Entry> i = Float2IntMaps.fastIterator(AbstractFloat2IntMap.this);

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
            return FloatSpliterators.asSpliterator(this.iterator(), Size64.sizeOf(AbstractFloat2IntMap.this), 321);
         }
      };
   }

   @Override
   public IntCollection values() {
      return new AbstractIntCollection() {
         @Override
         public boolean contains(int k) {
            return AbstractFloat2IntMap.this.containsValue(k);
         }

         @Override
         public int size() {
            return AbstractFloat2IntMap.this.size();
         }

         @Override
         public void clear() {
            AbstractFloat2IntMap.this.clear();
         }

         @Override
         public IntIterator iterator() {
            return new IntIterator() {
               private final ObjectIterator<Float2IntMap.Entry> i = Float2IntMaps.fastIterator(AbstractFloat2IntMap.this);

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
            return IntSpliterators.asSpliterator(this.iterator(), Size64.sizeOf(AbstractFloat2IntMap.this), 320);
         }
      };
   }

   @Override
   public void putAll(Map<? extends Float, ? extends Integer> m) {
      if (m instanceof Float2IntMap) {
         ObjectIterator<Float2IntMap.Entry> i = Float2IntMaps.fastIterator((Float2IntMap)m);

         while (i.hasNext()) {
            Float2IntMap.Entry e = i.next();
            this.put(e.getFloatKey(), e.getIntValue());
         }
      } else {
         int n = m.size();
         Iterator<? extends Entry<? extends Float, ? extends Integer>> i = m.entrySet().iterator();

         while (n-- != 0) {
            Entry<? extends Float, ? extends Integer> e = (Entry<? extends Float, ? extends Integer>)i.next();
            this.put(e.getKey(), e.getValue());
         }
      }
   }

   @Override
   public int hashCode() {
      int h = 0;
      int n = this.size();
      ObjectIterator<Float2IntMap.Entry> i = Float2IntMaps.fastIterator(this);

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
         return m.size() != this.size() ? false : this.float2IntEntrySet().containsAll(m.entrySet());
      }
   }

   @Override
   public String toString() {
      StringBuilder s = new StringBuilder();
      ObjectIterator<Float2IntMap.Entry> i = Float2IntMaps.fastIterator(this);
      int n = this.size();
      boolean first = true;
      s.append("{");

      while (n-- != 0) {
         if (first) {
            first = false;
         } else {
            s.append(", ");
         }

         Float2IntMap.Entry e = i.next();
         s.append(String.valueOf(e.getFloatKey()));
         s.append("=>");
         s.append(String.valueOf(e.getIntValue()));
      }

      s.append("}");
      return s.toString();
   }

   public static class BasicEntry implements Float2IntMap.Entry {
      protected float key;
      protected int value;

      public BasicEntry() {
      }

      public BasicEntry(Float key, Integer value) {
         this.key = key;
         this.value = value;
      }

      public BasicEntry(float key, int value) {
         this.key = key;
         this.value = value;
      }

      @Override
      public float getFloatKey() {
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
         } else if (o instanceof Float2IntMap.Entry) {
            Float2IntMap.Entry e = (Float2IntMap.Entry)o;
            return Float.floatToIntBits(this.key) == Float.floatToIntBits(e.getFloatKey()) && this.value == e.getIntValue();
         } else {
            Entry<?, ?> e = (Entry<?, ?>)o;
            Object key = e.getKey();
            if (key != null && key instanceof Float) {
               Object value = e.getValue();
               return value != null && value instanceof Integer
                  ? Float.floatToIntBits(this.key) == Float.floatToIntBits((Float)key) && this.value == (Integer)value
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

   public abstract static class BasicEntrySet extends AbstractObjectSet<Float2IntMap.Entry> {
      protected final Float2IntMap map;

      public BasicEntrySet(Float2IntMap map) {
         this.map = map;
      }

      @Override
      public boolean contains(Object o) {
         if (!(o instanceof Entry)) {
            return false;
         } else if (o instanceof Float2IntMap.Entry) {
            Float2IntMap.Entry e = (Float2IntMap.Entry)o;
            float k = e.getFloatKey();
            return this.map.containsKey(k) && this.map.get(k) == e.getIntValue();
         } else {
            Entry<?, ?> e = (Entry<?, ?>)o;
            Object key = e.getKey();
            if (key != null && key instanceof Float) {
               float k = (Float)key;
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
         } else if (o instanceof Float2IntMap.Entry) {
            Float2IntMap.Entry e = (Float2IntMap.Entry)o;
            return this.map.remove(e.getFloatKey(), e.getIntValue());
         } else {
            Entry<?, ?> e = (Entry<?, ?>)o;
            Object key = e.getKey();
            if (key != null && key instanceof Float) {
               float k = (Float)key;
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
      public ObjectSpliterator<Float2IntMap.Entry> spliterator() {
         return ObjectSpliterators.asSpliterator(this.iterator(), Size64.sizeOf(this.map), 65);
      }
   }
}
