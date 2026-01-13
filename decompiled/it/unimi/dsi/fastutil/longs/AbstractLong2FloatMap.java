package it.unimi.dsi.fastutil.longs;

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

public abstract class AbstractLong2FloatMap extends AbstractLong2FloatFunction implements Long2FloatMap, Serializable {
   private static final long serialVersionUID = -4940583368468432370L;

   protected AbstractLong2FloatMap() {
   }

   @Override
   public boolean containsKey(long k) {
      ObjectIterator<Long2FloatMap.Entry> i = this.long2FloatEntrySet().iterator();

      while (i.hasNext()) {
         if (i.next().getLongKey() == k) {
            return true;
         }
      }

      return false;
   }

   @Override
   public boolean containsValue(float v) {
      ObjectIterator<Long2FloatMap.Entry> i = this.long2FloatEntrySet().iterator();

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
   public LongSet keySet() {
      return new AbstractLongSet() {
         @Override
         public boolean contains(long k) {
            return AbstractLong2FloatMap.this.containsKey(k);
         }

         @Override
         public int size() {
            return AbstractLong2FloatMap.this.size();
         }

         @Override
         public void clear() {
            AbstractLong2FloatMap.this.clear();
         }

         @Override
         public LongIterator iterator() {
            return new LongIterator() {
               private final ObjectIterator<Long2FloatMap.Entry> i = Long2FloatMaps.fastIterator(AbstractLong2FloatMap.this);

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
            return LongSpliterators.asSpliterator(this.iterator(), Size64.sizeOf(AbstractLong2FloatMap.this), 321);
         }
      };
   }

   @Override
   public FloatCollection values() {
      return new AbstractFloatCollection() {
         @Override
         public boolean contains(float k) {
            return AbstractLong2FloatMap.this.containsValue(k);
         }

         @Override
         public int size() {
            return AbstractLong2FloatMap.this.size();
         }

         @Override
         public void clear() {
            AbstractLong2FloatMap.this.clear();
         }

         @Override
         public FloatIterator iterator() {
            return new FloatIterator() {
               private final ObjectIterator<Long2FloatMap.Entry> i = Long2FloatMaps.fastIterator(AbstractLong2FloatMap.this);

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
            return FloatSpliterators.asSpliterator(this.iterator(), Size64.sizeOf(AbstractLong2FloatMap.this), 320);
         }
      };
   }

   @Override
   public void putAll(Map<? extends Long, ? extends Float> m) {
      if (m instanceof Long2FloatMap) {
         ObjectIterator<Long2FloatMap.Entry> i = Long2FloatMaps.fastIterator((Long2FloatMap)m);

         while (i.hasNext()) {
            Long2FloatMap.Entry e = i.next();
            this.put(e.getLongKey(), e.getFloatValue());
         }
      } else {
         int n = m.size();
         Iterator<? extends Entry<? extends Long, ? extends Float>> i = m.entrySet().iterator();

         while (n-- != 0) {
            Entry<? extends Long, ? extends Float> e = (Entry<? extends Long, ? extends Float>)i.next();
            this.put(e.getKey(), e.getValue());
         }
      }
   }

   @Override
   public int hashCode() {
      int h = 0;
      int n = this.size();
      ObjectIterator<Long2FloatMap.Entry> i = Long2FloatMaps.fastIterator(this);

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
         return m.size() != this.size() ? false : this.long2FloatEntrySet().containsAll(m.entrySet());
      }
   }

   @Override
   public String toString() {
      StringBuilder s = new StringBuilder();
      ObjectIterator<Long2FloatMap.Entry> i = Long2FloatMaps.fastIterator(this);
      int n = this.size();
      boolean first = true;
      s.append("{");

      while (n-- != 0) {
         if (first) {
            first = false;
         } else {
            s.append(", ");
         }

         Long2FloatMap.Entry e = i.next();
         s.append(String.valueOf(e.getLongKey()));
         s.append("=>");
         s.append(String.valueOf(e.getFloatValue()));
      }

      s.append("}");
      return s.toString();
   }

   public static class BasicEntry implements Long2FloatMap.Entry {
      protected long key;
      protected float value;

      public BasicEntry() {
      }

      public BasicEntry(Long key, Float value) {
         this.key = key;
         this.value = value;
      }

      public BasicEntry(long key, float value) {
         this.key = key;
         this.value = value;
      }

      @Override
      public long getLongKey() {
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
         } else if (o instanceof Long2FloatMap.Entry) {
            Long2FloatMap.Entry e = (Long2FloatMap.Entry)o;
            return this.key == e.getLongKey() && Float.floatToIntBits(this.value) == Float.floatToIntBits(e.getFloatValue());
         } else {
            Entry<?, ?> e = (Entry<?, ?>)o;
            Object key = e.getKey();
            if (key != null && key instanceof Long) {
               Object value = e.getValue();
               return value != null && value instanceof Float
                  ? this.key == (Long)key && Float.floatToIntBits(this.value) == Float.floatToIntBits((Float)value)
                  : false;
            } else {
               return false;
            }
         }
      }

      @Override
      public int hashCode() {
         return HashCommon.long2int(this.key) ^ HashCommon.float2int(this.value);
      }

      @Override
      public String toString() {
         return this.key + "->" + this.value;
      }
   }

   public abstract static class BasicEntrySet extends AbstractObjectSet<Long2FloatMap.Entry> {
      protected final Long2FloatMap map;

      public BasicEntrySet(Long2FloatMap map) {
         this.map = map;
      }

      @Override
      public boolean contains(Object o) {
         if (!(o instanceof Entry)) {
            return false;
         } else if (o instanceof Long2FloatMap.Entry) {
            Long2FloatMap.Entry e = (Long2FloatMap.Entry)o;
            long k = e.getLongKey();
            return this.map.containsKey(k) && Float.floatToIntBits(this.map.get(k)) == Float.floatToIntBits(e.getFloatValue());
         } else {
            Entry<?, ?> e = (Entry<?, ?>)o;
            Object key = e.getKey();
            if (key != null && key instanceof Long) {
               long k = (Long)key;
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
         } else if (o instanceof Long2FloatMap.Entry) {
            Long2FloatMap.Entry e = (Long2FloatMap.Entry)o;
            return this.map.remove(e.getLongKey(), e.getFloatValue());
         } else {
            Entry<?, ?> e = (Entry<?, ?>)o;
            Object key = e.getKey();
            if (key != null && key instanceof Long) {
               long k = (Long)key;
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
      public ObjectSpliterator<Long2FloatMap.Entry> spliterator() {
         return ObjectSpliterators.asSpliterator(this.iterator(), Size64.sizeOf(this.map), 65);
      }
   }
}
