package it.unimi.dsi.fastutil.shorts;

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

public abstract class AbstractShort2LongMap extends AbstractShort2LongFunction implements Short2LongMap, Serializable {
   private static final long serialVersionUID = -4940583368468432370L;

   protected AbstractShort2LongMap() {
   }

   @Override
   public boolean containsKey(short k) {
      ObjectIterator<Short2LongMap.Entry> i = this.short2LongEntrySet().iterator();

      while (i.hasNext()) {
         if (i.next().getShortKey() == k) {
            return true;
         }
      }

      return false;
   }

   @Override
   public boolean containsValue(long v) {
      ObjectIterator<Short2LongMap.Entry> i = this.short2LongEntrySet().iterator();

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
   public final long mergeLong(short key, long value, LongBinaryOperator remappingFunction) {
      return this.mergeLong(key, value, remappingFunction);
   }

   @Override
   public ShortSet keySet() {
      return new AbstractShortSet() {
         @Override
         public boolean contains(short k) {
            return AbstractShort2LongMap.this.containsKey(k);
         }

         @Override
         public int size() {
            return AbstractShort2LongMap.this.size();
         }

         @Override
         public void clear() {
            AbstractShort2LongMap.this.clear();
         }

         @Override
         public ShortIterator iterator() {
            return new ShortIterator() {
               private final ObjectIterator<Short2LongMap.Entry> i = Short2LongMaps.fastIterator(AbstractShort2LongMap.this);

               @Override
               public short nextShort() {
                  return this.i.next().getShortKey();
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
               public void forEachRemaining(ShortConsumer action) {
                  this.i.forEachRemaining(entry -> action.accept(entry.getShortKey()));
               }
            };
         }

         @Override
         public ShortSpliterator spliterator() {
            return ShortSpliterators.asSpliterator(this.iterator(), Size64.sizeOf(AbstractShort2LongMap.this), 321);
         }
      };
   }

   @Override
   public LongCollection values() {
      return new AbstractLongCollection() {
         @Override
         public boolean contains(long k) {
            return AbstractShort2LongMap.this.containsValue(k);
         }

         @Override
         public int size() {
            return AbstractShort2LongMap.this.size();
         }

         @Override
         public void clear() {
            AbstractShort2LongMap.this.clear();
         }

         @Override
         public LongIterator iterator() {
            return new LongIterator() {
               private final ObjectIterator<Short2LongMap.Entry> i = Short2LongMaps.fastIterator(AbstractShort2LongMap.this);

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
            return LongSpliterators.asSpliterator(this.iterator(), Size64.sizeOf(AbstractShort2LongMap.this), 320);
         }
      };
   }

   @Override
   public void putAll(Map<? extends Short, ? extends Long> m) {
      if (m instanceof Short2LongMap) {
         ObjectIterator<Short2LongMap.Entry> i = Short2LongMaps.fastIterator((Short2LongMap)m);

         while (i.hasNext()) {
            Short2LongMap.Entry e = i.next();
            this.put(e.getShortKey(), e.getLongValue());
         }
      } else {
         int n = m.size();
         Iterator<? extends Entry<? extends Short, ? extends Long>> i = m.entrySet().iterator();

         while (n-- != 0) {
            Entry<? extends Short, ? extends Long> e = (Entry<? extends Short, ? extends Long>)i.next();
            this.put(e.getKey(), e.getValue());
         }
      }
   }

   @Override
   public int hashCode() {
      int h = 0;
      int n = this.size();
      ObjectIterator<Short2LongMap.Entry> i = Short2LongMaps.fastIterator(this);

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
         return m.size() != this.size() ? false : this.short2LongEntrySet().containsAll(m.entrySet());
      }
   }

   @Override
   public String toString() {
      StringBuilder s = new StringBuilder();
      ObjectIterator<Short2LongMap.Entry> i = Short2LongMaps.fastIterator(this);
      int n = this.size();
      boolean first = true;
      s.append("{");

      while (n-- != 0) {
         if (first) {
            first = false;
         } else {
            s.append(", ");
         }

         Short2LongMap.Entry e = i.next();
         s.append(String.valueOf(e.getShortKey()));
         s.append("=>");
         s.append(String.valueOf(e.getLongValue()));
      }

      s.append("}");
      return s.toString();
   }

   public static class BasicEntry implements Short2LongMap.Entry {
      protected short key;
      protected long value;

      public BasicEntry() {
      }

      public BasicEntry(Short key, Long value) {
         this.key = key;
         this.value = value;
      }

      public BasicEntry(short key, long value) {
         this.key = key;
         this.value = value;
      }

      @Override
      public short getShortKey() {
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
         } else if (o instanceof Short2LongMap.Entry) {
            Short2LongMap.Entry e = (Short2LongMap.Entry)o;
            return this.key == e.getShortKey() && this.value == e.getLongValue();
         } else {
            Entry<?, ?> e = (Entry<?, ?>)o;
            Object key = e.getKey();
            if (key != null && key instanceof Short) {
               Object value = e.getValue();
               return value != null && value instanceof Long ? this.key == (Short)key && this.value == (Long)value : false;
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

   public abstract static class BasicEntrySet extends AbstractObjectSet<Short2LongMap.Entry> {
      protected final Short2LongMap map;

      public BasicEntrySet(Short2LongMap map) {
         this.map = map;
      }

      @Override
      public boolean contains(Object o) {
         if (!(o instanceof Entry)) {
            return false;
         } else if (o instanceof Short2LongMap.Entry) {
            Short2LongMap.Entry e = (Short2LongMap.Entry)o;
            short k = e.getShortKey();
            return this.map.containsKey(k) && this.map.get(k) == e.getLongValue();
         } else {
            Entry<?, ?> e = (Entry<?, ?>)o;
            Object key = e.getKey();
            if (key != null && key instanceof Short) {
               short k = (Short)key;
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
         } else if (o instanceof Short2LongMap.Entry) {
            Short2LongMap.Entry e = (Short2LongMap.Entry)o;
            return this.map.remove(e.getShortKey(), e.getLongValue());
         } else {
            Entry<?, ?> e = (Entry<?, ?>)o;
            Object key = e.getKey();
            if (key != null && key instanceof Short) {
               short k = (Short)key;
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
      public ObjectSpliterator<Short2LongMap.Entry> spliterator() {
         return ObjectSpliterators.asSpliterator(this.iterator(), Size64.sizeOf(this.map), 65);
      }
   }
}
