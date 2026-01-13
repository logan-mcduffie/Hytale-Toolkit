package it.unimi.dsi.fastutil.shorts;

import it.unimi.dsi.fastutil.Size64;
import it.unimi.dsi.fastutil.objects.AbstractObjectSet;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectSpliterator;
import it.unimi.dsi.fastutil.objects.ObjectSpliterators;
import java.io.Serializable;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

public abstract class AbstractShort2ShortMap extends AbstractShort2ShortFunction implements Short2ShortMap, Serializable {
   private static final long serialVersionUID = -4940583368468432370L;

   protected AbstractShort2ShortMap() {
   }

   @Override
   public boolean containsKey(short k) {
      ObjectIterator<Short2ShortMap.Entry> i = this.short2ShortEntrySet().iterator();

      while (i.hasNext()) {
         if (i.next().getShortKey() == k) {
            return true;
         }
      }

      return false;
   }

   @Override
   public boolean containsValue(short v) {
      ObjectIterator<Short2ShortMap.Entry> i = this.short2ShortEntrySet().iterator();

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
   public ShortSet keySet() {
      return new AbstractShortSet() {
         @Override
         public boolean contains(short k) {
            return AbstractShort2ShortMap.this.containsKey(k);
         }

         @Override
         public int size() {
            return AbstractShort2ShortMap.this.size();
         }

         @Override
         public void clear() {
            AbstractShort2ShortMap.this.clear();
         }

         @Override
         public ShortIterator iterator() {
            return new ShortIterator() {
               private final ObjectIterator<Short2ShortMap.Entry> i = Short2ShortMaps.fastIterator(AbstractShort2ShortMap.this);

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
            return ShortSpliterators.asSpliterator(this.iterator(), Size64.sizeOf(AbstractShort2ShortMap.this), 321);
         }
      };
   }

   @Override
   public ShortCollection values() {
      return new AbstractShortCollection() {
         @Override
         public boolean contains(short k) {
            return AbstractShort2ShortMap.this.containsValue(k);
         }

         @Override
         public int size() {
            return AbstractShort2ShortMap.this.size();
         }

         @Override
         public void clear() {
            AbstractShort2ShortMap.this.clear();
         }

         @Override
         public ShortIterator iterator() {
            return new ShortIterator() {
               private final ObjectIterator<Short2ShortMap.Entry> i = Short2ShortMaps.fastIterator(AbstractShort2ShortMap.this);

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
            return ShortSpliterators.asSpliterator(this.iterator(), Size64.sizeOf(AbstractShort2ShortMap.this), 320);
         }
      };
   }

   @Override
   public void putAll(Map<? extends Short, ? extends Short> m) {
      if (m instanceof Short2ShortMap) {
         ObjectIterator<Short2ShortMap.Entry> i = Short2ShortMaps.fastIterator((Short2ShortMap)m);

         while (i.hasNext()) {
            Short2ShortMap.Entry e = i.next();
            this.put(e.getShortKey(), e.getShortValue());
         }
      } else {
         int n = m.size();
         Iterator<? extends Entry<? extends Short, ? extends Short>> i = m.entrySet().iterator();

         while (n-- != 0) {
            Entry<? extends Short, ? extends Short> e = (Entry<? extends Short, ? extends Short>)i.next();
            this.put(e.getKey(), e.getValue());
         }
      }
   }

   @Override
   public int hashCode() {
      int h = 0;
      int n = this.size();
      ObjectIterator<Short2ShortMap.Entry> i = Short2ShortMaps.fastIterator(this);

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
         return m.size() != this.size() ? false : this.short2ShortEntrySet().containsAll(m.entrySet());
      }
   }

   @Override
   public String toString() {
      StringBuilder s = new StringBuilder();
      ObjectIterator<Short2ShortMap.Entry> i = Short2ShortMaps.fastIterator(this);
      int n = this.size();
      boolean first = true;
      s.append("{");

      while (n-- != 0) {
         if (first) {
            first = false;
         } else {
            s.append(", ");
         }

         Short2ShortMap.Entry e = i.next();
         s.append(String.valueOf(e.getShortKey()));
         s.append("=>");
         s.append(String.valueOf(e.getShortValue()));
      }

      s.append("}");
      return s.toString();
   }

   public static class BasicEntry implements Short2ShortMap.Entry {
      protected short key;
      protected short value;

      public BasicEntry() {
      }

      public BasicEntry(Short key, Short value) {
         this.key = key;
         this.value = value;
      }

      public BasicEntry(short key, short value) {
         this.key = key;
         this.value = value;
      }

      @Override
      public short getShortKey() {
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
         } else if (o instanceof Short2ShortMap.Entry) {
            Short2ShortMap.Entry e = (Short2ShortMap.Entry)o;
            return this.key == e.getShortKey() && this.value == e.getShortValue();
         } else {
            Entry<?, ?> e = (Entry<?, ?>)o;
            Object key = e.getKey();
            if (key != null && key instanceof Short) {
               Object value = e.getValue();
               return value != null && value instanceof Short ? this.key == (Short)key && this.value == (Short)value : false;
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

   public abstract static class BasicEntrySet extends AbstractObjectSet<Short2ShortMap.Entry> {
      protected final Short2ShortMap map;

      public BasicEntrySet(Short2ShortMap map) {
         this.map = map;
      }

      @Override
      public boolean contains(Object o) {
         if (!(o instanceof Entry)) {
            return false;
         } else if (o instanceof Short2ShortMap.Entry) {
            Short2ShortMap.Entry e = (Short2ShortMap.Entry)o;
            short k = e.getShortKey();
            return this.map.containsKey(k) && this.map.get(k) == e.getShortValue();
         } else {
            Entry<?, ?> e = (Entry<?, ?>)o;
            Object key = e.getKey();
            if (key != null && key instanceof Short) {
               short k = (Short)key;
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
         } else if (o instanceof Short2ShortMap.Entry) {
            Short2ShortMap.Entry e = (Short2ShortMap.Entry)o;
            return this.map.remove(e.getShortKey(), e.getShortValue());
         } else {
            Entry<?, ?> e = (Entry<?, ?>)o;
            Object key = e.getKey();
            if (key != null && key instanceof Short) {
               short k = (Short)key;
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
      public ObjectSpliterator<Short2ShortMap.Entry> spliterator() {
         return ObjectSpliterators.asSpliterator(this.iterator(), Size64.sizeOf(this.map), 65);
      }
   }
}
