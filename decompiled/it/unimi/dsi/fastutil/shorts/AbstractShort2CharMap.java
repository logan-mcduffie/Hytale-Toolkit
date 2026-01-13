package it.unimi.dsi.fastutil.shorts;

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

public abstract class AbstractShort2CharMap extends AbstractShort2CharFunction implements Short2CharMap, Serializable {
   private static final long serialVersionUID = -4940583368468432370L;

   protected AbstractShort2CharMap() {
   }

   @Override
   public boolean containsKey(short k) {
      ObjectIterator<Short2CharMap.Entry> i = this.short2CharEntrySet().iterator();

      while (i.hasNext()) {
         if (i.next().getShortKey() == k) {
            return true;
         }
      }

      return false;
   }

   @Override
   public boolean containsValue(char v) {
      ObjectIterator<Short2CharMap.Entry> i = this.short2CharEntrySet().iterator();

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
   public ShortSet keySet() {
      return new AbstractShortSet() {
         @Override
         public boolean contains(short k) {
            return AbstractShort2CharMap.this.containsKey(k);
         }

         @Override
         public int size() {
            return AbstractShort2CharMap.this.size();
         }

         @Override
         public void clear() {
            AbstractShort2CharMap.this.clear();
         }

         @Override
         public ShortIterator iterator() {
            return new ShortIterator() {
               private final ObjectIterator<Short2CharMap.Entry> i = Short2CharMaps.fastIterator(AbstractShort2CharMap.this);

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
            return ShortSpliterators.asSpliterator(this.iterator(), Size64.sizeOf(AbstractShort2CharMap.this), 321);
         }
      };
   }

   @Override
   public CharCollection values() {
      return new AbstractCharCollection() {
         @Override
         public boolean contains(char k) {
            return AbstractShort2CharMap.this.containsValue(k);
         }

         @Override
         public int size() {
            return AbstractShort2CharMap.this.size();
         }

         @Override
         public void clear() {
            AbstractShort2CharMap.this.clear();
         }

         @Override
         public CharIterator iterator() {
            return new CharIterator() {
               private final ObjectIterator<Short2CharMap.Entry> i = Short2CharMaps.fastIterator(AbstractShort2CharMap.this);

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
            return CharSpliterators.asSpliterator(this.iterator(), Size64.sizeOf(AbstractShort2CharMap.this), 320);
         }
      };
   }

   @Override
   public void putAll(Map<? extends Short, ? extends Character> m) {
      if (m instanceof Short2CharMap) {
         ObjectIterator<Short2CharMap.Entry> i = Short2CharMaps.fastIterator((Short2CharMap)m);

         while (i.hasNext()) {
            Short2CharMap.Entry e = i.next();
            this.put(e.getShortKey(), e.getCharValue());
         }
      } else {
         int n = m.size();
         Iterator<? extends Entry<? extends Short, ? extends Character>> i = m.entrySet().iterator();

         while (n-- != 0) {
            Entry<? extends Short, ? extends Character> e = (Entry<? extends Short, ? extends Character>)i.next();
            this.put(e.getKey(), e.getValue());
         }
      }
   }

   @Override
   public int hashCode() {
      int h = 0;
      int n = this.size();
      ObjectIterator<Short2CharMap.Entry> i = Short2CharMaps.fastIterator(this);

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
         return m.size() != this.size() ? false : this.short2CharEntrySet().containsAll(m.entrySet());
      }
   }

   @Override
   public String toString() {
      StringBuilder s = new StringBuilder();
      ObjectIterator<Short2CharMap.Entry> i = Short2CharMaps.fastIterator(this);
      int n = this.size();
      boolean first = true;
      s.append("{");

      while (n-- != 0) {
         if (first) {
            first = false;
         } else {
            s.append(", ");
         }

         Short2CharMap.Entry e = i.next();
         s.append(String.valueOf(e.getShortKey()));
         s.append("=>");
         s.append(String.valueOf(e.getCharValue()));
      }

      s.append("}");
      return s.toString();
   }

   public static class BasicEntry implements Short2CharMap.Entry {
      protected short key;
      protected char value;

      public BasicEntry() {
      }

      public BasicEntry(Short key, Character value) {
         this.key = key;
         this.value = value;
      }

      public BasicEntry(short key, char value) {
         this.key = key;
         this.value = value;
      }

      @Override
      public short getShortKey() {
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
         } else if (o instanceof Short2CharMap.Entry) {
            Short2CharMap.Entry e = (Short2CharMap.Entry)o;
            return this.key == e.getShortKey() && this.value == e.getCharValue();
         } else {
            Entry<?, ?> e = (Entry<?, ?>)o;
            Object key = e.getKey();
            if (key != null && key instanceof Short) {
               Object value = e.getValue();
               return value != null && value instanceof Character ? this.key == (Short)key && this.value == (Character)value : false;
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

   public abstract static class BasicEntrySet extends AbstractObjectSet<Short2CharMap.Entry> {
      protected final Short2CharMap map;

      public BasicEntrySet(Short2CharMap map) {
         this.map = map;
      }

      @Override
      public boolean contains(Object o) {
         if (!(o instanceof Entry)) {
            return false;
         } else if (o instanceof Short2CharMap.Entry) {
            Short2CharMap.Entry e = (Short2CharMap.Entry)o;
            short k = e.getShortKey();
            return this.map.containsKey(k) && this.map.get(k) == e.getCharValue();
         } else {
            Entry<?, ?> e = (Entry<?, ?>)o;
            Object key = e.getKey();
            if (key != null && key instanceof Short) {
               short k = (Short)key;
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
         } else if (o instanceof Short2CharMap.Entry) {
            Short2CharMap.Entry e = (Short2CharMap.Entry)o;
            return this.map.remove(e.getShortKey(), e.getCharValue());
         } else {
            Entry<?, ?> e = (Entry<?, ?>)o;
            Object key = e.getKey();
            if (key != null && key instanceof Short) {
               short k = (Short)key;
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
      public ObjectSpliterator<Short2CharMap.Entry> spliterator() {
         return ObjectSpliterators.asSpliterator(this.iterator(), Size64.sizeOf(this.map), 65);
      }
   }
}
