package it.unimi.dsi.fastutil.ints;

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

public abstract class AbstractInt2LongMap extends AbstractInt2LongFunction implements Int2LongMap, Serializable {
   private static final long serialVersionUID = -4940583368468432370L;

   protected AbstractInt2LongMap() {
   }

   @Override
   public boolean containsKey(int k) {
      ObjectIterator<Int2LongMap.Entry> i = this.int2LongEntrySet().iterator();

      while (i.hasNext()) {
         if (i.next().getIntKey() == k) {
            return true;
         }
      }

      return false;
   }

   @Override
   public boolean containsValue(long v) {
      ObjectIterator<Int2LongMap.Entry> i = this.int2LongEntrySet().iterator();

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
   public final long mergeLong(int key, long value, LongBinaryOperator remappingFunction) {
      return this.mergeLong(key, value, remappingFunction);
   }

   @Override
   public IntSet keySet() {
      return new AbstractIntSet() {
         @Override
         public boolean contains(int k) {
            return AbstractInt2LongMap.this.containsKey(k);
         }

         @Override
         public int size() {
            return AbstractInt2LongMap.this.size();
         }

         @Override
         public void clear() {
            AbstractInt2LongMap.this.clear();
         }

         @Override
         public IntIterator iterator() {
            return new IntIterator() {
               private final ObjectIterator<Int2LongMap.Entry> i = Int2LongMaps.fastIterator(AbstractInt2LongMap.this);

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
            return IntSpliterators.asSpliterator(this.iterator(), Size64.sizeOf(AbstractInt2LongMap.this), 321);
         }
      };
   }

   @Override
   public LongCollection values() {
      return new AbstractLongCollection() {
         @Override
         public boolean contains(long k) {
            return AbstractInt2LongMap.this.containsValue(k);
         }

         @Override
         public int size() {
            return AbstractInt2LongMap.this.size();
         }

         @Override
         public void clear() {
            AbstractInt2LongMap.this.clear();
         }

         @Override
         public LongIterator iterator() {
            return new LongIterator() {
               private final ObjectIterator<Int2LongMap.Entry> i = Int2LongMaps.fastIterator(AbstractInt2LongMap.this);

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
            return LongSpliterators.asSpliterator(this.iterator(), Size64.sizeOf(AbstractInt2LongMap.this), 320);
         }
      };
   }

   @Override
   public void putAll(Map<? extends Integer, ? extends Long> m) {
      if (m instanceof Int2LongMap) {
         ObjectIterator<Int2LongMap.Entry> i = Int2LongMaps.fastIterator((Int2LongMap)m);

         while (i.hasNext()) {
            Int2LongMap.Entry e = i.next();
            this.put(e.getIntKey(), e.getLongValue());
         }
      } else {
         int n = m.size();
         Iterator<? extends Entry<? extends Integer, ? extends Long>> i = m.entrySet().iterator();

         while (n-- != 0) {
            Entry<? extends Integer, ? extends Long> e = (Entry<? extends Integer, ? extends Long>)i.next();
            this.put(e.getKey(), e.getValue());
         }
      }
   }

   @Override
   public int hashCode() {
      int h = 0;
      int n = this.size();
      ObjectIterator<Int2LongMap.Entry> i = Int2LongMaps.fastIterator(this);

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
         return m.size() != this.size() ? false : this.int2LongEntrySet().containsAll(m.entrySet());
      }
   }

   @Override
   public String toString() {
      StringBuilder s = new StringBuilder();
      ObjectIterator<Int2LongMap.Entry> i = Int2LongMaps.fastIterator(this);
      int n = this.size();
      boolean first = true;
      s.append("{");

      while (n-- != 0) {
         if (first) {
            first = false;
         } else {
            s.append(", ");
         }

         Int2LongMap.Entry e = i.next();
         s.append(String.valueOf(e.getIntKey()));
         s.append("=>");
         s.append(String.valueOf(e.getLongValue()));
      }

      s.append("}");
      return s.toString();
   }

   public static class BasicEntry implements Int2LongMap.Entry {
      protected int key;
      protected long value;

      public BasicEntry() {
      }

      public BasicEntry(Integer key, Long value) {
         this.key = key;
         this.value = value;
      }

      public BasicEntry(int key, long value) {
         this.key = key;
         this.value = value;
      }

      @Override
      public int getIntKey() {
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
         } else if (o instanceof Int2LongMap.Entry) {
            Int2LongMap.Entry e = (Int2LongMap.Entry)o;
            return this.key == e.getIntKey() && this.value == e.getLongValue();
         } else {
            Entry<?, ?> e = (Entry<?, ?>)o;
            Object key = e.getKey();
            if (key != null && key instanceof Integer) {
               Object value = e.getValue();
               return value != null && value instanceof Long ? this.key == (Integer)key && this.value == (Long)value : false;
            } else {
               return false;
            }
         }
      }

      @Override
      public int hashCode() {
         return this.key ^ HashCommon.long2int(this.value);
      }

      @Override
      public String toString() {
         return this.key + "->" + this.value;
      }
   }

   public abstract static class BasicEntrySet extends AbstractObjectSet<Int2LongMap.Entry> {
      protected final Int2LongMap map;

      public BasicEntrySet(Int2LongMap map) {
         this.map = map;
      }

      @Override
      public boolean contains(Object o) {
         if (!(o instanceof Entry)) {
            return false;
         } else if (o instanceof Int2LongMap.Entry) {
            Int2LongMap.Entry e = (Int2LongMap.Entry)o;
            int k = e.getIntKey();
            return this.map.containsKey(k) && this.map.get(k) == e.getLongValue();
         } else {
            Entry<?, ?> e = (Entry<?, ?>)o;
            Object key = e.getKey();
            if (key != null && key instanceof Integer) {
               int k = (Integer)key;
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
         } else if (o instanceof Int2LongMap.Entry) {
            Int2LongMap.Entry e = (Int2LongMap.Entry)o;
            return this.map.remove(e.getIntKey(), e.getLongValue());
         } else {
            Entry<?, ?> e = (Entry<?, ?>)o;
            Object key = e.getKey();
            if (key != null && key instanceof Integer) {
               int k = (Integer)key;
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
      public ObjectSpliterator<Int2LongMap.Entry> spliterator() {
         return ObjectSpliterators.asSpliterator(this.iterator(), Size64.sizeOf(this.map), 65);
      }
   }
}
