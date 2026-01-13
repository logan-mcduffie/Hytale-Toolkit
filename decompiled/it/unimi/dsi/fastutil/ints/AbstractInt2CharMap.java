package it.unimi.dsi.fastutil.ints;

import it.unimi.dsi.fastutil.Size64;
import it.unimi.dsi.fastutil.chars.AbstractCharCollection;
import it.unimi.dsi.fastutil.chars.CharCollection;
import it.unimi.dsi.fastutil.chars.CharConsumer;
import it.unimi.dsi.fastutil.chars.CharIterator;
import it.unimi.dsi.fastutil.chars.CharSpliterator;
import it.unimi.dsi.fastutil.chars.CharSpliterators;
import it.unimi.dsi.fastutil.objects.AbstractObjectSet;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectSpliterator;
import it.unimi.dsi.fastutil.objects.ObjectSpliterators;
import java.io.Serializable;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

public abstract class AbstractInt2CharMap extends AbstractInt2CharFunction implements Int2CharMap, Serializable {
   private static final long serialVersionUID = -4940583368468432370L;

   protected AbstractInt2CharMap() {
   }

   @Override
   public boolean containsKey(int k) {
      ObjectIterator<Int2CharMap.Entry> i = this.int2CharEntrySet().iterator();

      while (i.hasNext()) {
         if (i.next().getIntKey() == k) {
            return true;
         }
      }

      return false;
   }

   @Override
   public boolean containsValue(char v) {
      ObjectIterator<Int2CharMap.Entry> i = this.int2CharEntrySet().iterator();

      while (i.hasNext()) {
         if (i.next().getCharValue() == v) {
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
            return AbstractInt2CharMap.this.containsKey(k);
         }

         @Override
         public int size() {
            return AbstractInt2CharMap.this.size();
         }

         @Override
         public void clear() {
            AbstractInt2CharMap.this.clear();
         }

         @Override
         public IntIterator iterator() {
            return new IntIterator() {
               private final ObjectIterator<Int2CharMap.Entry> i = Int2CharMaps.fastIterator(AbstractInt2CharMap.this);

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
            return IntSpliterators.asSpliterator(this.iterator(), Size64.sizeOf(AbstractInt2CharMap.this), 321);
         }
      };
   }

   @Override
   public CharCollection values() {
      return new AbstractCharCollection() {
         @Override
         public boolean contains(char k) {
            return AbstractInt2CharMap.this.containsValue(k);
         }

         @Override
         public int size() {
            return AbstractInt2CharMap.this.size();
         }

         @Override
         public void clear() {
            AbstractInt2CharMap.this.clear();
         }

         @Override
         public CharIterator iterator() {
            return new CharIterator() {
               private final ObjectIterator<Int2CharMap.Entry> i = Int2CharMaps.fastIterator(AbstractInt2CharMap.this);

               @Override
               public char nextChar() {
                  return this.i.next().getCharValue();
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
               public void forEachRemaining(CharConsumer action) {
                  this.i.forEachRemaining(entry -> action.accept(entry.getCharValue()));
               }
            };
         }

         @Override
         public CharSpliterator spliterator() {
            return CharSpliterators.asSpliterator(this.iterator(), Size64.sizeOf(AbstractInt2CharMap.this), 320);
         }
      };
   }

   @Override
   public void putAll(Map<? extends Integer, ? extends Character> m) {
      if (m instanceof Int2CharMap) {
         ObjectIterator<Int2CharMap.Entry> i = Int2CharMaps.fastIterator((Int2CharMap)m);

         while (i.hasNext()) {
            Int2CharMap.Entry e = i.next();
            this.put(e.getIntKey(), e.getCharValue());
         }
      } else {
         int n = m.size();
         Iterator<? extends Entry<? extends Integer, ? extends Character>> i = m.entrySet().iterator();

         while (n-- != 0) {
            Entry<? extends Integer, ? extends Character> e = (Entry<? extends Integer, ? extends Character>)i.next();
            this.put(e.getKey(), e.getValue());
         }
      }
   }

   @Override
   public int hashCode() {
      int h = 0;
      int n = this.size();
      ObjectIterator<Int2CharMap.Entry> i = Int2CharMaps.fastIterator(this);

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
         return m.size() != this.size() ? false : this.int2CharEntrySet().containsAll(m.entrySet());
      }
   }

   @Override
   public String toString() {
      StringBuilder s = new StringBuilder();
      ObjectIterator<Int2CharMap.Entry> i = Int2CharMaps.fastIterator(this);
      int n = this.size();
      boolean first = true;
      s.append("{");

      while (n-- != 0) {
         if (first) {
            first = false;
         } else {
            s.append(", ");
         }

         Int2CharMap.Entry e = i.next();
         s.append(String.valueOf(e.getIntKey()));
         s.append("=>");
         s.append(String.valueOf(e.getCharValue()));
      }

      s.append("}");
      return s.toString();
   }

   public static class BasicEntry implements Int2CharMap.Entry {
      protected int key;
      protected char value;

      public BasicEntry() {
      }

      public BasicEntry(Integer key, Character value) {
         this.key = key;
         this.value = value;
      }

      public BasicEntry(int key, char value) {
         this.key = key;
         this.value = value;
      }

      @Override
      public int getIntKey() {
         return this.key;
      }

      @Override
      public char getCharValue() {
         return this.value;
      }

      @Override
      public char setValue(char value) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean equals(Object o) {
         if (!(o instanceof Entry)) {
            return false;
         } else if (o instanceof Int2CharMap.Entry) {
            Int2CharMap.Entry e = (Int2CharMap.Entry)o;
            return this.key == e.getIntKey() && this.value == e.getCharValue();
         } else {
            Entry<?, ?> e = (Entry<?, ?>)o;
            Object key = e.getKey();
            if (key != null && key instanceof Integer) {
               Object value = e.getValue();
               return value != null && value instanceof Character ? this.key == (Integer)key && this.value == (Character)value : false;
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

   public abstract static class BasicEntrySet extends AbstractObjectSet<Int2CharMap.Entry> {
      protected final Int2CharMap map;

      public BasicEntrySet(Int2CharMap map) {
         this.map = map;
      }

      @Override
      public boolean contains(Object o) {
         if (!(o instanceof Entry)) {
            return false;
         } else if (o instanceof Int2CharMap.Entry) {
            Int2CharMap.Entry e = (Int2CharMap.Entry)o;
            int k = e.getIntKey();
            return this.map.containsKey(k) && this.map.get(k) == e.getCharValue();
         } else {
            Entry<?, ?> e = (Entry<?, ?>)o;
            Object key = e.getKey();
            if (key != null && key instanceof Integer) {
               int k = (Integer)key;
               Object value = e.getValue();
               return value != null && value instanceof Character ? this.map.containsKey(k) && this.map.get(k) == (Character)value : false;
            } else {
               return false;
            }
         }
      }

      @Override
      public boolean remove(Object o) {
         if (!(o instanceof Entry)) {
            return false;
         } else if (o instanceof Int2CharMap.Entry) {
            Int2CharMap.Entry e = (Int2CharMap.Entry)o;
            return this.map.remove(e.getIntKey(), e.getCharValue());
         } else {
            Entry<?, ?> e = (Entry<?, ?>)o;
            Object key = e.getKey();
            if (key != null && key instanceof Integer) {
               int k = (Integer)key;
               Object value = e.getValue();
               if (value != null && value instanceof Character) {
                  char v = (Character)value;
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
      public ObjectSpliterator<Int2CharMap.Entry> spliterator() {
         return ObjectSpliterators.asSpliterator(this.iterator(), Size64.sizeOf(this.map), 65);
      }
   }
}
