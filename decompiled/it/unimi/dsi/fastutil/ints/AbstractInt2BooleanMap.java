package it.unimi.dsi.fastutil.ints;

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

public abstract class AbstractInt2BooleanMap extends AbstractInt2BooleanFunction implements Int2BooleanMap, Serializable {
   private static final long serialVersionUID = -4940583368468432370L;

   protected AbstractInt2BooleanMap() {
   }

   @Override
   public boolean containsKey(int k) {
      ObjectIterator<Int2BooleanMap.Entry> i = this.int2BooleanEntrySet().iterator();

      while (i.hasNext()) {
         if (i.next().getIntKey() == k) {
            return true;
         }
      }

      return false;
   }

   @Override
   public boolean containsValue(boolean v) {
      ObjectIterator<Int2BooleanMap.Entry> i = this.int2BooleanEntrySet().iterator();

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
   public IntSet keySet() {
      return new AbstractIntSet() {
         @Override
         public boolean contains(int k) {
            return AbstractInt2BooleanMap.this.containsKey(k);
         }

         @Override
         public int size() {
            return AbstractInt2BooleanMap.this.size();
         }

         @Override
         public void clear() {
            AbstractInt2BooleanMap.this.clear();
         }

         @Override
         public IntIterator iterator() {
            return new IntIterator() {
               private final ObjectIterator<Int2BooleanMap.Entry> i = Int2BooleanMaps.fastIterator(AbstractInt2BooleanMap.this);

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
            return IntSpliterators.asSpliterator(this.iterator(), Size64.sizeOf(AbstractInt2BooleanMap.this), 321);
         }
      };
   }

   @Override
   public BooleanCollection values() {
      return new AbstractBooleanCollection() {
         @Override
         public boolean contains(boolean k) {
            return AbstractInt2BooleanMap.this.containsValue(k);
         }

         @Override
         public int size() {
            return AbstractInt2BooleanMap.this.size();
         }

         @Override
         public void clear() {
            AbstractInt2BooleanMap.this.clear();
         }

         @Override
         public BooleanIterator iterator() {
            return new BooleanIterator() {
               private final ObjectIterator<Int2BooleanMap.Entry> i = Int2BooleanMaps.fastIterator(AbstractInt2BooleanMap.this);

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
            return BooleanSpliterators.asSpliterator(this.iterator(), Size64.sizeOf(AbstractInt2BooleanMap.this), 320);
         }
      };
   }

   @Override
   public void putAll(Map<? extends Integer, ? extends Boolean> m) {
      if (m instanceof Int2BooleanMap) {
         ObjectIterator<Int2BooleanMap.Entry> i = Int2BooleanMaps.fastIterator((Int2BooleanMap)m);

         while (i.hasNext()) {
            Int2BooleanMap.Entry e = i.next();
            this.put(e.getIntKey(), e.getBooleanValue());
         }
      } else {
         int n = m.size();
         Iterator<? extends Entry<? extends Integer, ? extends Boolean>> i = m.entrySet().iterator();

         while (n-- != 0) {
            Entry<? extends Integer, ? extends Boolean> e = (Entry<? extends Integer, ? extends Boolean>)i.next();
            this.put(e.getKey(), e.getValue());
         }
      }
   }

   @Override
   public int hashCode() {
      int h = 0;
      int n = this.size();
      ObjectIterator<Int2BooleanMap.Entry> i = Int2BooleanMaps.fastIterator(this);

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
         return m.size() != this.size() ? false : this.int2BooleanEntrySet().containsAll(m.entrySet());
      }
   }

   @Override
   public String toString() {
      StringBuilder s = new StringBuilder();
      ObjectIterator<Int2BooleanMap.Entry> i = Int2BooleanMaps.fastIterator(this);
      int n = this.size();
      boolean first = true;
      s.append("{");

      while (n-- != 0) {
         if (first) {
            first = false;
         } else {
            s.append(", ");
         }

         Int2BooleanMap.Entry e = i.next();
         s.append(String.valueOf(e.getIntKey()));
         s.append("=>");
         s.append(String.valueOf(e.getBooleanValue()));
      }

      s.append("}");
      return s.toString();
   }

   public static class BasicEntry implements Int2BooleanMap.Entry {
      protected int key;
      protected boolean value;

      public BasicEntry() {
      }

      public BasicEntry(Integer key, Boolean value) {
         this.key = key;
         this.value = value;
      }

      public BasicEntry(int key, boolean value) {
         this.key = key;
         this.value = value;
      }

      @Override
      public int getIntKey() {
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
         } else if (o instanceof Int2BooleanMap.Entry) {
            Int2BooleanMap.Entry e = (Int2BooleanMap.Entry)o;
            return this.key == e.getIntKey() && this.value == e.getBooleanValue();
         } else {
            Entry<?, ?> e = (Entry<?, ?>)o;
            Object key = e.getKey();
            if (key != null && key instanceof Integer) {
               Object value = e.getValue();
               return value != null && value instanceof Boolean ? this.key == (Integer)key && this.value == (Boolean)value : false;
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

   public abstract static class BasicEntrySet extends AbstractObjectSet<Int2BooleanMap.Entry> {
      protected final Int2BooleanMap map;

      public BasicEntrySet(Int2BooleanMap map) {
         this.map = map;
      }

      @Override
      public boolean contains(Object o) {
         if (!(o instanceof Entry)) {
            return false;
         } else if (o instanceof Int2BooleanMap.Entry) {
            Int2BooleanMap.Entry e = (Int2BooleanMap.Entry)o;
            int k = e.getIntKey();
            return this.map.containsKey(k) && this.map.get(k) == e.getBooleanValue();
         } else {
            Entry<?, ?> e = (Entry<?, ?>)o;
            Object key = e.getKey();
            if (key != null && key instanceof Integer) {
               int k = (Integer)key;
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
         } else if (o instanceof Int2BooleanMap.Entry) {
            Int2BooleanMap.Entry e = (Int2BooleanMap.Entry)o;
            return this.map.remove(e.getIntKey(), e.getBooleanValue());
         } else {
            Entry<?, ?> e = (Entry<?, ?>)o;
            Object key = e.getKey();
            if (key != null && key instanceof Integer) {
               int k = (Integer)key;
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
      public ObjectSpliterator<Int2BooleanMap.Entry> spliterator() {
         return ObjectSpliterators.asSpliterator(this.iterator(), Size64.sizeOf(this.map), 65);
      }
   }
}
