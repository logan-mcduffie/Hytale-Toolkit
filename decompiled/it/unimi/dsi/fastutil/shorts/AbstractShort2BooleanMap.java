package it.unimi.dsi.fastutil.shorts;

import it.unimi.dsi.fastutil.Size64;
import it.unimi.dsi.fastutil.booleans.AbstractBooleanCollection;
import it.unimi.dsi.fastutil.booleans.BooleanCollection;
import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import it.unimi.dsi.fastutil.booleans.BooleanIterator;
import it.unimi.dsi.fastutil.booleans.BooleanSpliterator;
import it.unimi.dsi.fastutil.booleans.BooleanSpliterators;
import it.unimi.dsi.fastutil.objects.AbstractObjectSet;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectSpliterator;
import it.unimi.dsi.fastutil.objects.ObjectSpliterators;
import java.io.Serializable;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

public abstract class AbstractShort2BooleanMap extends AbstractShort2BooleanFunction implements Short2BooleanMap, Serializable {
   private static final long serialVersionUID = -4940583368468432370L;

   protected AbstractShort2BooleanMap() {
   }

   @Override
   public boolean containsKey(short k) {
      ObjectIterator<Short2BooleanMap.Entry> i = this.short2BooleanEntrySet().iterator();

      while (i.hasNext()) {
         if (i.next().getShortKey() == k) {
            return true;
         }
      }

      return false;
   }

   @Override
   public boolean containsValue(boolean v) {
      ObjectIterator<Short2BooleanMap.Entry> i = this.short2BooleanEntrySet().iterator();

      while (i.hasNext()) {
         if (i.next().getBooleanValue() == v) {
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
            return AbstractShort2BooleanMap.this.containsKey(k);
         }

         @Override
         public int size() {
            return AbstractShort2BooleanMap.this.size();
         }

         @Override
         public void clear() {
            AbstractShort2BooleanMap.this.clear();
         }

         @Override
         public ShortIterator iterator() {
            return new ShortIterator() {
               private final ObjectIterator<Short2BooleanMap.Entry> i = Short2BooleanMaps.fastIterator(AbstractShort2BooleanMap.this);

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
            return ShortSpliterators.asSpliterator(this.iterator(), Size64.sizeOf(AbstractShort2BooleanMap.this), 321);
         }
      };
   }

   @Override
   public BooleanCollection values() {
      return new AbstractBooleanCollection() {
         @Override
         public boolean contains(boolean k) {
            return AbstractShort2BooleanMap.this.containsValue(k);
         }

         @Override
         public int size() {
            return AbstractShort2BooleanMap.this.size();
         }

         @Override
         public void clear() {
            AbstractShort2BooleanMap.this.clear();
         }

         @Override
         public BooleanIterator iterator() {
            return new BooleanIterator() {
               private final ObjectIterator<Short2BooleanMap.Entry> i = Short2BooleanMaps.fastIterator(AbstractShort2BooleanMap.this);

               @Override
               public boolean nextBoolean() {
                  return this.i.next().getBooleanValue();
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
               public void forEachRemaining(BooleanConsumer action) {
                  this.i.forEachRemaining(entry -> action.accept(entry.getBooleanValue()));
               }
            };
         }

         @Override
         public BooleanSpliterator spliterator() {
            return BooleanSpliterators.asSpliterator(this.iterator(), Size64.sizeOf(AbstractShort2BooleanMap.this), 320);
         }
      };
   }

   @Override
   public void putAll(Map<? extends Short, ? extends Boolean> m) {
      if (m instanceof Short2BooleanMap) {
         ObjectIterator<Short2BooleanMap.Entry> i = Short2BooleanMaps.fastIterator((Short2BooleanMap)m);

         while (i.hasNext()) {
            Short2BooleanMap.Entry e = i.next();
            this.put(e.getShortKey(), e.getBooleanValue());
         }
      } else {
         int n = m.size();
         Iterator<? extends Entry<? extends Short, ? extends Boolean>> i = m.entrySet().iterator();

         while (n-- != 0) {
            Entry<? extends Short, ? extends Boolean> e = (Entry<? extends Short, ? extends Boolean>)i.next();
            this.put(e.getKey(), e.getValue());
         }
      }
   }

   @Override
   public int hashCode() {
      int h = 0;
      int n = this.size();
      ObjectIterator<Short2BooleanMap.Entry> i = Short2BooleanMaps.fastIterator(this);

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
         return m.size() != this.size() ? false : this.short2BooleanEntrySet().containsAll(m.entrySet());
      }
   }

   @Override
   public String toString() {
      StringBuilder s = new StringBuilder();
      ObjectIterator<Short2BooleanMap.Entry> i = Short2BooleanMaps.fastIterator(this);
      int n = this.size();
      boolean first = true;
      s.append("{");

      while (n-- != 0) {
         if (first) {
            first = false;
         } else {
            s.append(", ");
         }

         Short2BooleanMap.Entry e = i.next();
         s.append(String.valueOf(e.getShortKey()));
         s.append("=>");
         s.append(String.valueOf(e.getBooleanValue()));
      }

      s.append("}");
      return s.toString();
   }

   public static class BasicEntry implements Short2BooleanMap.Entry {
      protected short key;
      protected boolean value;

      public BasicEntry() {
      }

      public BasicEntry(Short key, Boolean value) {
         this.key = key;
         this.value = value;
      }

      public BasicEntry(short key, boolean value) {
         this.key = key;
         this.value = value;
      }

      @Override
      public short getShortKey() {
         return this.key;
      }

      @Override
      public boolean getBooleanValue() {
         return this.value;
      }

      @Override
      public boolean setValue(boolean value) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean equals(Object o) {
         if (!(o instanceof Entry)) {
            return false;
         } else if (o instanceof Short2BooleanMap.Entry) {
            Short2BooleanMap.Entry e = (Short2BooleanMap.Entry)o;
            return this.key == e.getShortKey() && this.value == e.getBooleanValue();
         } else {
            Entry<?, ?> e = (Entry<?, ?>)o;
            Object key = e.getKey();
            if (key != null && key instanceof Short) {
               Object value = e.getValue();
               return value != null && value instanceof Boolean ? this.key == (Short)key && this.value == (Boolean)value : false;
            } else {
               return false;
            }
         }
      }

      @Override
      public int hashCode() {
         return this.key ^ (this.value ? 1231 : 1237);
      }

      @Override
      public String toString() {
         return this.key + "->" + this.value;
      }
   }

   public abstract static class BasicEntrySet extends AbstractObjectSet<Short2BooleanMap.Entry> {
      protected final Short2BooleanMap map;

      public BasicEntrySet(Short2BooleanMap map) {
         this.map = map;
      }

      @Override
      public boolean contains(Object o) {
         if (!(o instanceof Entry)) {
            return false;
         } else if (o instanceof Short2BooleanMap.Entry) {
            Short2BooleanMap.Entry e = (Short2BooleanMap.Entry)o;
            short k = e.getShortKey();
            return this.map.containsKey(k) && this.map.get(k) == e.getBooleanValue();
         } else {
            Entry<?, ?> e = (Entry<?, ?>)o;
            Object key = e.getKey();
            if (key != null && key instanceof Short) {
               short k = (Short)key;
               Object value = e.getValue();
               return value != null && value instanceof Boolean ? this.map.containsKey(k) && this.map.get(k) == (Boolean)value : false;
            } else {
               return false;
            }
         }
      }

      @Override
      public boolean remove(Object o) {
         if (!(o instanceof Entry)) {
            return false;
         } else if (o instanceof Short2BooleanMap.Entry) {
            Short2BooleanMap.Entry e = (Short2BooleanMap.Entry)o;
            return this.map.remove(e.getShortKey(), e.getBooleanValue());
         } else {
            Entry<?, ?> e = (Entry<?, ?>)o;
            Object key = e.getKey();
            if (key != null && key instanceof Short) {
               short k = (Short)key;
               Object value = e.getValue();
               if (value != null && value instanceof Boolean) {
                  boolean v = (Boolean)value;
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
      public ObjectSpliterator<Short2BooleanMap.Entry> spliterator() {
         return ObjectSpliterators.asSpliterator(this.iterator(), Size64.sizeOf(this.map), 65);
      }
   }
}
