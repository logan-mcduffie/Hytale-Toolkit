package it.unimi.dsi.fastutil.ints;

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

public abstract class AbstractInt2FloatMap extends AbstractInt2FloatFunction implements Int2FloatMap, Serializable {
   private static final long serialVersionUID = -4940583368468432370L;

   protected AbstractInt2FloatMap() {
   }

   @Override
   public boolean containsKey(int k) {
      ObjectIterator<Int2FloatMap.Entry> i = this.int2FloatEntrySet().iterator();

      while (i.hasNext()) {
         if (i.next().getIntKey() == k) {
            return true;
         }
      }

      return false;
   }

   @Override
   public boolean containsValue(float v) {
      ObjectIterator<Int2FloatMap.Entry> i = this.int2FloatEntrySet().iterator();

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
   public IntSet keySet() {
      return new AbstractIntSet() {
         @Override
         public boolean contains(int k) {
            return AbstractInt2FloatMap.this.containsKey(k);
         }

         @Override
         public int size() {
            return AbstractInt2FloatMap.this.size();
         }

         @Override
         public void clear() {
            AbstractInt2FloatMap.this.clear();
         }

         @Override
         public IntIterator iterator() {
            return new IntIterator() {
               private final ObjectIterator<Int2FloatMap.Entry> i = Int2FloatMaps.fastIterator(AbstractInt2FloatMap.this);

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
            return IntSpliterators.asSpliterator(this.iterator(), Size64.sizeOf(AbstractInt2FloatMap.this), 321);
         }
      };
   }

   @Override
   public FloatCollection values() {
      return new AbstractFloatCollection() {
         @Override
         public boolean contains(float k) {
            return AbstractInt2FloatMap.this.containsValue(k);
         }

         @Override
         public int size() {
            return AbstractInt2FloatMap.this.size();
         }

         @Override
         public void clear() {
            AbstractInt2FloatMap.this.clear();
         }

         @Override
         public FloatIterator iterator() {
            return new FloatIterator() {
               private final ObjectIterator<Int2FloatMap.Entry> i = Int2FloatMaps.fastIterator(AbstractInt2FloatMap.this);

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
            return FloatSpliterators.asSpliterator(this.iterator(), Size64.sizeOf(AbstractInt2FloatMap.this), 320);
         }
      };
   }

   @Override
   public void putAll(Map<? extends Integer, ? extends Float> m) {
      if (m instanceof Int2FloatMap) {
         ObjectIterator<Int2FloatMap.Entry> i = Int2FloatMaps.fastIterator((Int2FloatMap)m);

         while (i.hasNext()) {
            Int2FloatMap.Entry e = i.next();
            this.put(e.getIntKey(), e.getFloatValue());
         }
      } else {
         int n = m.size();
         Iterator<? extends Entry<? extends Integer, ? extends Float>> i = m.entrySet().iterator();

         while (n-- != 0) {
            Entry<? extends Integer, ? extends Float> e = (Entry<? extends Integer, ? extends Float>)i.next();
            this.put(e.getKey(), e.getValue());
         }
      }
   }

   @Override
   public int hashCode() {
      int h = 0;
      int n = this.size();
      ObjectIterator<Int2FloatMap.Entry> i = Int2FloatMaps.fastIterator(this);

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
         return m.size() != this.size() ? false : this.int2FloatEntrySet().containsAll(m.entrySet());
      }
   }

   @Override
   public String toString() {
      StringBuilder s = new StringBuilder();
      ObjectIterator<Int2FloatMap.Entry> i = Int2FloatMaps.fastIterator(this);
      int n = this.size();
      boolean first = true;
      s.append("{");

      while (n-- != 0) {
         if (first) {
            first = false;
         } else {
            s.append(", ");
         }

         Int2FloatMap.Entry e = i.next();
         s.append(String.valueOf(e.getIntKey()));
         s.append("=>");
         s.append(String.valueOf(e.getFloatValue()));
      }

      s.append("}");
      return s.toString();
   }

   public static class BasicEntry implements Int2FloatMap.Entry {
      protected int key;
      protected float value;

      public BasicEntry() {
      }

      public BasicEntry(Integer key, Float value) {
         this.key = key;
         this.value = value;
      }

      public BasicEntry(int key, float value) {
         this.key = key;
         this.value = value;
      }

      @Override
      public int getIntKey() {
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
         } else if (o instanceof Int2FloatMap.Entry) {
            Int2FloatMap.Entry e = (Int2FloatMap.Entry)o;
            return this.key == e.getIntKey() && Float.floatToIntBits(this.value) == Float.floatToIntBits(e.getFloatValue());
         } else {
            Entry<?, ?> e = (Entry<?, ?>)o;
            Object key = e.getKey();
            if (key != null && key instanceof Integer) {
               Object value = e.getValue();
               return value != null && value instanceof Float
                  ? this.key == (Integer)key && Float.floatToIntBits(this.value) == Float.floatToIntBits((Float)value)
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

   public abstract static class BasicEntrySet extends AbstractObjectSet<Int2FloatMap.Entry> {
      protected final Int2FloatMap map;

      public BasicEntrySet(Int2FloatMap map) {
         this.map = map;
      }

      @Override
      public boolean contains(Object o) {
         if (!(o instanceof Entry)) {
            return false;
         } else if (o instanceof Int2FloatMap.Entry) {
            Int2FloatMap.Entry e = (Int2FloatMap.Entry)o;
            int k = e.getIntKey();
            return this.map.containsKey(k) && Float.floatToIntBits(this.map.get(k)) == Float.floatToIntBits(e.getFloatValue());
         } else {
            Entry<?, ?> e = (Entry<?, ?>)o;
            Object key = e.getKey();
            if (key != null && key instanceof Integer) {
               int k = (Integer)key;
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
         } else if (o instanceof Int2FloatMap.Entry) {
            Int2FloatMap.Entry e = (Int2FloatMap.Entry)o;
            return this.map.remove(e.getIntKey(), e.getFloatValue());
         } else {
            Entry<?, ?> e = (Entry<?, ?>)o;
            Object key = e.getKey();
            if (key != null && key instanceof Integer) {
               int k = (Integer)key;
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
      public ObjectSpliterator<Int2FloatMap.Entry> spliterator() {
         return ObjectSpliterators.asSpliterator(this.iterator(), Size64.sizeOf(this.map), 65);
      }
   }
}
