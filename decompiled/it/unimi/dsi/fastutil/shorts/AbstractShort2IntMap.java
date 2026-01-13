package it.unimi.dsi.fastutil.shorts;

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

public abstract class AbstractShort2IntMap extends AbstractShort2IntFunction implements Short2IntMap, Serializable {
   private static final long serialVersionUID = -4940583368468432370L;

   protected AbstractShort2IntMap() {
   }

   @Override
   public boolean containsKey(short k) {
      ObjectIterator<Short2IntMap.Entry> i = this.short2IntEntrySet().iterator();

      while (i.hasNext()) {
         if (i.next().getShortKey() == k) {
            return true;
         }
      }

      return false;
   }

   @Override
   public boolean containsValue(int v) {
      ObjectIterator<Short2IntMap.Entry> i = this.short2IntEntrySet().iterator();

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
   public final int mergeInt(short key, int value, IntBinaryOperator remappingFunction) {
      return this.mergeInt(key, value, remappingFunction);
   }

   @Override
   public ShortSet keySet() {
      return new AbstractShortSet() {
         @Override
         public boolean contains(short k) {
            return AbstractShort2IntMap.this.containsKey(k);
         }

         @Override
         public int size() {
            return AbstractShort2IntMap.this.size();
         }

         @Override
         public void clear() {
            AbstractShort2IntMap.this.clear();
         }

         @Override
         public ShortIterator iterator() {
            return new ShortIterator() {
               private final ObjectIterator<Short2IntMap.Entry> i = Short2IntMaps.fastIterator(AbstractShort2IntMap.this);

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
            return ShortSpliterators.asSpliterator(this.iterator(), Size64.sizeOf(AbstractShort2IntMap.this), 321);
         }
      };
   }

   @Override
   public IntCollection values() {
      return new AbstractIntCollection() {
         @Override
         public boolean contains(int k) {
            return AbstractShort2IntMap.this.containsValue(k);
         }

         @Override
         public int size() {
            return AbstractShort2IntMap.this.size();
         }

         @Override
         public void clear() {
            AbstractShort2IntMap.this.clear();
         }

         @Override
         public IntIterator iterator() {
            return new IntIterator() {
               private final ObjectIterator<Short2IntMap.Entry> i = Short2IntMaps.fastIterator(AbstractShort2IntMap.this);

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
            return IntSpliterators.asSpliterator(this.iterator(), Size64.sizeOf(AbstractShort2IntMap.this), 320);
         }
      };
   }

   @Override
   public void putAll(Map<? extends Short, ? extends Integer> m) {
      if (m instanceof Short2IntMap) {
         ObjectIterator<Short2IntMap.Entry> i = Short2IntMaps.fastIterator((Short2IntMap)m);

         while (i.hasNext()) {
            Short2IntMap.Entry e = i.next();
            this.put(e.getShortKey(), e.getIntValue());
         }
      } else {
         int n = m.size();
         Iterator<? extends Entry<? extends Short, ? extends Integer>> i = m.entrySet().iterator();

         while (n-- != 0) {
            Entry<? extends Short, ? extends Integer> e = (Entry<? extends Short, ? extends Integer>)i.next();
            this.put(e.getKey(), e.getValue());
         }
      }
   }

   @Override
   public int hashCode() {
      int h = 0;
      int n = this.size();
      ObjectIterator<Short2IntMap.Entry> i = Short2IntMaps.fastIterator(this);

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
         return m.size() != this.size() ? false : this.short2IntEntrySet().containsAll(m.entrySet());
      }
   }

   @Override
   public String toString() {
      StringBuilder s = new StringBuilder();
      ObjectIterator<Short2IntMap.Entry> i = Short2IntMaps.fastIterator(this);
      int n = this.size();
      boolean first = true;
      s.append("{");

      while (n-- != 0) {
         if (first) {
            first = false;
         } else {
            s.append(", ");
         }

         Short2IntMap.Entry e = i.next();
         s.append(String.valueOf(e.getShortKey()));
         s.append("=>");
         s.append(String.valueOf(e.getIntValue()));
      }

      s.append("}");
      return s.toString();
   }

   public static class BasicEntry implements Short2IntMap.Entry {
      protected short key;
      protected int value;

      public BasicEntry() {
      }

      public BasicEntry(Short key, Integer value) {
         this.key = key;
         this.value = value;
      }

      public BasicEntry(short key, int value) {
         this.key = key;
         this.value = value;
      }

      @Override
      public short getShortKey() {
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
         } else if (o instanceof Short2IntMap.Entry) {
            Short2IntMap.Entry e = (Short2IntMap.Entry)o;
            return this.key == e.getShortKey() && this.value == e.getIntValue();
         } else {
            Entry<?, ?> e = (Entry<?, ?>)o;
            Object key = e.getKey();
            if (key != null && key instanceof Short) {
               Object value = e.getValue();
               return value != null && value instanceof Integer ? this.key == (Short)key && this.value == (Integer)value : false;
            } else {
               return false;
            }
         }
      }

      @Override
      public int hashCode() {
         return this.key ^ this.value;
      }

      @Override
      public String toString() {
         return this.key + "->" + this.value;
      }
   }

   public abstract static class BasicEntrySet extends AbstractObjectSet<Short2IntMap.Entry> {
      protected final Short2IntMap map;

      public BasicEntrySet(Short2IntMap map) {
         this.map = map;
      }

      @Override
      public boolean contains(Object o) {
         if (!(o instanceof Entry)) {
            return false;
         } else if (o instanceof Short2IntMap.Entry) {
            Short2IntMap.Entry e = (Short2IntMap.Entry)o;
            short k = e.getShortKey();
            return this.map.containsKey(k) && this.map.get(k) == e.getIntValue();
         } else {
            Entry<?, ?> e = (Entry<?, ?>)o;
            Object key = e.getKey();
            if (key != null && key instanceof Short) {
               short k = (Short)key;
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
         } else if (o instanceof Short2IntMap.Entry) {
            Short2IntMap.Entry e = (Short2IntMap.Entry)o;
            return this.map.remove(e.getShortKey(), e.getIntValue());
         } else {
            Entry<?, ?> e = (Entry<?, ?>)o;
            Object key = e.getKey();
            if (key != null && key instanceof Short) {
               short k = (Short)key;
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
      public ObjectSpliterator<Short2IntMap.Entry> spliterator() {
         return ObjectSpliterators.asSpliterator(this.iterator(), Size64.sizeOf(this.map), 65);
      }
   }
}
