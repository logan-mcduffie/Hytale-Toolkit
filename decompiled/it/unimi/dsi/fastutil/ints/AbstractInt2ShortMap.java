package it.unimi.dsi.fastutil.ints;

import it.unimi.dsi.fastutil.Size64;
import it.unimi.dsi.fastutil.objects.AbstractObjectSet;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectSpliterator;
import it.unimi.dsi.fastutil.objects.ObjectSpliterators;
import it.unimi.dsi.fastutil.shorts.AbstractShortCollection;
import it.unimi.dsi.fastutil.shorts.ShortCollection;
import it.unimi.dsi.fastutil.shorts.ShortConsumer;
import it.unimi.dsi.fastutil.shorts.ShortIterator;
import it.unimi.dsi.fastutil.shorts.ShortSpliterator;
import it.unimi.dsi.fastutil.shorts.ShortSpliterators;
import java.io.Serializable;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

public abstract class AbstractInt2ShortMap extends AbstractInt2ShortFunction implements Int2ShortMap, Serializable {
   private static final long serialVersionUID = -4940583368468432370L;

   protected AbstractInt2ShortMap() {
   }

   @Override
   public boolean containsKey(int k) {
      ObjectIterator<Int2ShortMap.Entry> i = this.int2ShortEntrySet().iterator();

      while (i.hasNext()) {
         if (i.next().getIntKey() == k) {
            return true;
         }
      }

      return false;
   }

   @Override
   public boolean containsValue(short v) {
      ObjectIterator<Int2ShortMap.Entry> i = this.int2ShortEntrySet().iterator();

      while (i.hasNext()) {
         if (i.next().getShortValue() == v) {
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
            return AbstractInt2ShortMap.this.containsKey(k);
         }

         @Override
         public int size() {
            return AbstractInt2ShortMap.this.size();
         }

         @Override
         public void clear() {
            AbstractInt2ShortMap.this.clear();
         }

         @Override
         public IntIterator iterator() {
            return new IntIterator() {
               private final ObjectIterator<Int2ShortMap.Entry> i = Int2ShortMaps.fastIterator(AbstractInt2ShortMap.this);

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
            return IntSpliterators.asSpliterator(this.iterator(), Size64.sizeOf(AbstractInt2ShortMap.this), 321);
         }
      };
   }

   @Override
   public ShortCollection values() {
      return new AbstractShortCollection() {
         @Override
         public boolean contains(short k) {
            return AbstractInt2ShortMap.this.containsValue(k);
         }

         @Override
         public int size() {
            return AbstractInt2ShortMap.this.size();
         }

         @Override
         public void clear() {
            AbstractInt2ShortMap.this.clear();
         }

         @Override
         public ShortIterator iterator() {
            return new ShortIterator() {
               private final ObjectIterator<Int2ShortMap.Entry> i = Int2ShortMaps.fastIterator(AbstractInt2ShortMap.this);

               @Override
               public short nextShort() {
                  return this.i.next().getShortValue();
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
                  this.i.forEachRemaining(entry -> action.accept(entry.getShortValue()));
               }
            };
         }

         @Override
         public ShortSpliterator spliterator() {
            return ShortSpliterators.asSpliterator(this.iterator(), Size64.sizeOf(AbstractInt2ShortMap.this), 320);
         }
      };
   }

   @Override
   public void putAll(Map<? extends Integer, ? extends Short> m) {
      if (m instanceof Int2ShortMap) {
         ObjectIterator<Int2ShortMap.Entry> i = Int2ShortMaps.fastIterator((Int2ShortMap)m);

         while (i.hasNext()) {
            Int2ShortMap.Entry e = i.next();
            this.put(e.getIntKey(), e.getShortValue());
         }
      } else {
         int n = m.size();
         Iterator<? extends Entry<? extends Integer, ? extends Short>> i = m.entrySet().iterator();

         while (n-- != 0) {
            Entry<? extends Integer, ? extends Short> e = (Entry<? extends Integer, ? extends Short>)i.next();
            this.put(e.getKey(), e.getValue());
         }
      }
   }

   @Override
   public int hashCode() {
      int h = 0;
      int n = this.size();
      ObjectIterator<Int2ShortMap.Entry> i = Int2ShortMaps.fastIterator(this);

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
         return m.size() != this.size() ? false : this.int2ShortEntrySet().containsAll(m.entrySet());
      }
   }

   @Override
   public String toString() {
      StringBuilder s = new StringBuilder();
      ObjectIterator<Int2ShortMap.Entry> i = Int2ShortMaps.fastIterator(this);
      int n = this.size();
      boolean first = true;
      s.append("{");

      while (n-- != 0) {
         if (first) {
            first = false;
         } else {
            s.append(", ");
         }

         Int2ShortMap.Entry e = i.next();
         s.append(String.valueOf(e.getIntKey()));
         s.append("=>");
         s.append(String.valueOf(e.getShortValue()));
      }

      s.append("}");
      return s.toString();
   }

   public static class BasicEntry implements Int2ShortMap.Entry {
      protected int key;
      protected short value;

      public BasicEntry() {
      }

      public BasicEntry(Integer key, Short value) {
         this.key = key;
         this.value = value;
      }

      public BasicEntry(int key, short value) {
         this.key = key;
         this.value = value;
      }

      @Override
      public int getIntKey() {
         return this.key;
      }

      @Override
      public short getShortValue() {
         return this.value;
      }

      @Override
      public short setValue(short value) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean equals(Object o) {
         if (!(o instanceof Entry)) {
            return false;
         } else if (o instanceof Int2ShortMap.Entry) {
            Int2ShortMap.Entry e = (Int2ShortMap.Entry)o;
            return this.key == e.getIntKey() && this.value == e.getShortValue();
         } else {
            Entry<?, ?> e = (Entry<?, ?>)o;
            Object key = e.getKey();
            if (key != null && key instanceof Integer) {
               Object value = e.getValue();
               return value != null && value instanceof Short ? this.key == (Integer)key && this.value == (Short)value : false;
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

   public abstract static class BasicEntrySet extends AbstractObjectSet<Int2ShortMap.Entry> {
      protected final Int2ShortMap map;

      public BasicEntrySet(Int2ShortMap map) {
         this.map = map;
      }

      @Override
      public boolean contains(Object o) {
         if (!(o instanceof Entry)) {
            return false;
         } else if (o instanceof Int2ShortMap.Entry) {
            Int2ShortMap.Entry e = (Int2ShortMap.Entry)o;
            int k = e.getIntKey();
            return this.map.containsKey(k) && this.map.get(k) == e.getShortValue();
         } else {
            Entry<?, ?> e = (Entry<?, ?>)o;
            Object key = e.getKey();
            if (key != null && key instanceof Integer) {
               int k = (Integer)key;
               Object value = e.getValue();
               return value != null && value instanceof Short ? this.map.containsKey(k) && this.map.get(k) == (Short)value : false;
            } else {
               return false;
            }
         }
      }

      @Override
      public boolean remove(Object o) {
         if (!(o instanceof Entry)) {
            return false;
         } else if (o instanceof Int2ShortMap.Entry) {
            Int2ShortMap.Entry e = (Int2ShortMap.Entry)o;
            return this.map.remove(e.getIntKey(), e.getShortValue());
         } else {
            Entry<?, ?> e = (Entry<?, ?>)o;
            Object key = e.getKey();
            if (key != null && key instanceof Integer) {
               int k = (Integer)key;
               Object value = e.getValue();
               if (value != null && value instanceof Short) {
                  short v = (Short)value;
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
      public ObjectSpliterator<Int2ShortMap.Entry> spliterator() {
         return ObjectSpliterators.asSpliterator(this.iterator(), Size64.sizeOf(this.map), 65);
      }
   }
}
