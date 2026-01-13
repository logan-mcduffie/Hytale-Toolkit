package it.unimi.dsi.fastutil.longs;

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

public abstract class AbstractLong2IntMap extends AbstractLong2IntFunction implements Long2IntMap, Serializable {
   private static final long serialVersionUID = -4940583368468432370L;

   protected AbstractLong2IntMap() {
   }

   @Override
   public boolean containsKey(long k) {
      ObjectIterator<Long2IntMap.Entry> i = this.long2IntEntrySet().iterator();

      while (i.hasNext()) {
         if (i.next().getLongKey() == k) {
            return true;
         }
      }

      return false;
   }

   @Override
   public boolean containsValue(int v) {
      ObjectIterator<Long2IntMap.Entry> i = this.long2IntEntrySet().iterator();

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
   public final int mergeInt(long key, int value, IntBinaryOperator remappingFunction) {
      return this.mergeInt(key, value, remappingFunction);
   }

   @Override
   public LongSet keySet() {
      return new AbstractLongSet() {
         @Override
         public boolean contains(long k) {
            return AbstractLong2IntMap.this.containsKey(k);
         }

         @Override
         public int size() {
            return AbstractLong2IntMap.this.size();
         }

         @Override
         public void clear() {
            AbstractLong2IntMap.this.clear();
         }

         @Override
         public LongIterator iterator() {
            return new LongIterator() {
               private final ObjectIterator<Long2IntMap.Entry> i = Long2IntMaps.fastIterator(AbstractLong2IntMap.this);

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
            return LongSpliterators.asSpliterator(this.iterator(), Size64.sizeOf(AbstractLong2IntMap.this), 321);
         }
      };
   }

   @Override
   public IntCollection values() {
      return new AbstractIntCollection() {
         @Override
         public boolean contains(int k) {
            return AbstractLong2IntMap.this.containsValue(k);
         }

         @Override
         public int size() {
            return AbstractLong2IntMap.this.size();
         }

         @Override
         public void clear() {
            AbstractLong2IntMap.this.clear();
         }

         @Override
         public IntIterator iterator() {
            return new IntIterator() {
               private final ObjectIterator<Long2IntMap.Entry> i = Long2IntMaps.fastIterator(AbstractLong2IntMap.this);

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
            return IntSpliterators.asSpliterator(this.iterator(), Size64.sizeOf(AbstractLong2IntMap.this), 320);
         }
      };
   }

   @Override
   public void putAll(Map<? extends Long, ? extends Integer> m) {
      if (m instanceof Long2IntMap) {
         ObjectIterator<Long2IntMap.Entry> i = Long2IntMaps.fastIterator((Long2IntMap)m);

         while (i.hasNext()) {
            Long2IntMap.Entry e = i.next();
            this.put(e.getLongKey(), e.getIntValue());
         }
      } else {
         int n = m.size();
         Iterator<? extends Entry<? extends Long, ? extends Integer>> i = m.entrySet().iterator();

         while (n-- != 0) {
            Entry<? extends Long, ? extends Integer> e = (Entry<? extends Long, ? extends Integer>)i.next();
            this.put(e.getKey(), e.getValue());
         }
      }
   }

   @Override
   public int hashCode() {
      int h = 0;
      int n = this.size();
      ObjectIterator<Long2IntMap.Entry> i = Long2IntMaps.fastIterator(this);

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
         return m.size() != this.size() ? false : this.long2IntEntrySet().containsAll(m.entrySet());
      }
   }

   @Override
   public String toString() {
      StringBuilder s = new StringBuilder();
      ObjectIterator<Long2IntMap.Entry> i = Long2IntMaps.fastIterator(this);
      int n = this.size();
      boolean first = true;
      s.append("{");

      while (n-- != 0) {
         if (first) {
            first = false;
         } else {
            s.append(", ");
         }

         Long2IntMap.Entry e = i.next();
         s.append(String.valueOf(e.getLongKey()));
         s.append("=>");
         s.append(String.valueOf(e.getIntValue()));
      }

      s.append("}");
      return s.toString();
   }

   public static class BasicEntry implements Long2IntMap.Entry {
      protected long key;
      protected int value;

      public BasicEntry() {
      }

      public BasicEntry(Long key, Integer value) {
         this.key = key;
         this.value = value;
      }

      public BasicEntry(long key, int value) {
         this.key = key;
         this.value = value;
      }

      @Override
      public long getLongKey() {
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
         } else if (o instanceof Long2IntMap.Entry) {
            Long2IntMap.Entry e = (Long2IntMap.Entry)o;
            return this.key == e.getLongKey() && this.value == e.getIntValue();
         } else {
            Entry<?, ?> e = (Entry<?, ?>)o;
            Object key = e.getKey();
            if (key != null && key instanceof Long) {
               Object value = e.getValue();
               return value != null && value instanceof Integer ? this.key == (Long)key && this.value == (Integer)value : false;
            } else {
               return false;
            }
         }
      }

      @Override
      public int hashCode() {
         return HashCommon.long2int(this.key) ^ this.value;
      }

      @Override
      public String toString() {
         return this.key + "->" + this.value;
      }
   }

   public abstract static class BasicEntrySet extends AbstractObjectSet<Long2IntMap.Entry> {
      protected final Long2IntMap map;

      public BasicEntrySet(Long2IntMap map) {
         this.map = map;
      }

      @Override
      public boolean contains(Object o) {
         if (!(o instanceof Entry)) {
            return false;
         } else if (o instanceof Long2IntMap.Entry) {
            Long2IntMap.Entry e = (Long2IntMap.Entry)o;
            long k = e.getLongKey();
            return this.map.containsKey(k) && this.map.get(k) == e.getIntValue();
         } else {
            Entry<?, ?> e = (Entry<?, ?>)o;
            Object key = e.getKey();
            if (key != null && key instanceof Long) {
               long k = (Long)key;
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
         } else if (o instanceof Long2IntMap.Entry) {
            Long2IntMap.Entry e = (Long2IntMap.Entry)o;
            return this.map.remove(e.getLongKey(), e.getIntValue());
         } else {
            Entry<?, ?> e = (Entry<?, ?>)o;
            Object key = e.getKey();
            if (key != null && key instanceof Long) {
               long k = (Long)key;
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
      public ObjectSpliterator<Long2IntMap.Entry> spliterator() {
         return ObjectSpliterators.asSpliterator(this.iterator(), Size64.sizeOf(this.map), 65);
      }
   }
}
