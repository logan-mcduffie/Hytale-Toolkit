package it.unimi.dsi.fastutil.chars;

import it.unimi.dsi.fastutil.Size64;
import it.unimi.dsi.fastutil.objects.AbstractObjectSet;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectSpliterator;
import it.unimi.dsi.fastutil.objects.ObjectSpliterators;
import java.io.Serializable;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

public abstract class AbstractChar2CharMap extends AbstractChar2CharFunction implements Char2CharMap, Serializable {
   private static final long serialVersionUID = -4940583368468432370L;

   protected AbstractChar2CharMap() {
   }

   @Override
   public boolean containsKey(char k) {
      ObjectIterator<Char2CharMap.Entry> i = this.char2CharEntrySet().iterator();

      while (i.hasNext()) {
         if (i.next().getCharKey() == k) {
            return true;
         }
      }

      return false;
   }

   @Override
   public boolean containsValue(char v) {
      ObjectIterator<Char2CharMap.Entry> i = this.char2CharEntrySet().iterator();

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
   public CharSet keySet() {
      return new AbstractCharSet() {
         @Override
         public boolean contains(char k) {
            return AbstractChar2CharMap.this.containsKey(k);
         }

         @Override
         public int size() {
            return AbstractChar2CharMap.this.size();
         }

         @Override
         public void clear() {
            AbstractChar2CharMap.this.clear();
         }

         @Override
         public CharIterator iterator() {
            return new CharIterator() {
               private final ObjectIterator<Char2CharMap.Entry> i = Char2CharMaps.fastIterator(AbstractChar2CharMap.this);

               @Override
               public char nextChar() {
                  return this.i.next().getCharKey();
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
                  this.i.forEachRemaining(entry -> action.accept(entry.getCharKey()));
               }
            };
         }

         @Override
         public CharSpliterator spliterator() {
            return CharSpliterators.asSpliterator(this.iterator(), Size64.sizeOf(AbstractChar2CharMap.this), 321);
         }
      };
   }

   @Override
   public CharCollection values() {
      return new AbstractCharCollection() {
         @Override
         public boolean contains(char k) {
            return AbstractChar2CharMap.this.containsValue(k);
         }

         @Override
         public int size() {
            return AbstractChar2CharMap.this.size();
         }

         @Override
         public void clear() {
            AbstractChar2CharMap.this.clear();
         }

         @Override
         public CharIterator iterator() {
            return new CharIterator() {
               private final ObjectIterator<Char2CharMap.Entry> i = Char2CharMaps.fastIterator(AbstractChar2CharMap.this);

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
            return CharSpliterators.asSpliterator(this.iterator(), Size64.sizeOf(AbstractChar2CharMap.this), 320);
         }
      };
   }

   @Override
   public void putAll(Map<? extends Character, ? extends Character> m) {
      if (m instanceof Char2CharMap) {
         ObjectIterator<Char2CharMap.Entry> i = Char2CharMaps.fastIterator((Char2CharMap)m);

         while (i.hasNext()) {
            Char2CharMap.Entry e = i.next();
            this.put(e.getCharKey(), e.getCharValue());
         }
      } else {
         int n = m.size();
         Iterator<? extends Entry<? extends Character, ? extends Character>> i = m.entrySet().iterator();

         while (n-- != 0) {
            Entry<? extends Character, ? extends Character> e = (Entry<? extends Character, ? extends Character>)i.next();
            this.put(e.getKey(), e.getValue());
         }
      }
   }

   @Override
   public int hashCode() {
      int h = 0;
      int n = this.size();
      ObjectIterator<Char2CharMap.Entry> i = Char2CharMaps.fastIterator(this);

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
         return m.size() != this.size() ? false : this.char2CharEntrySet().containsAll(m.entrySet());
      }
   }

   @Override
   public String toString() {
      StringBuilder s = new StringBuilder();
      ObjectIterator<Char2CharMap.Entry> i = Char2CharMaps.fastIterator(this);
      int n = this.size();
      boolean first = true;
      s.append("{");

      while (n-- != 0) {
         if (first) {
            first = false;
         } else {
            s.append(", ");
         }

         Char2CharMap.Entry e = i.next();
         s.append(String.valueOf(e.getCharKey()));
         s.append("=>");
         s.append(String.valueOf(e.getCharValue()));
      }

      s.append("}");
      return s.toString();
   }

   public static class BasicEntry implements Char2CharMap.Entry {
      protected char key;
      protected char value;

      public BasicEntry() {
      }

      public BasicEntry(Character key, Character value) {
         this.key = key;
         this.value = value;
      }

      public BasicEntry(char key, char value) {
         this.key = key;
         this.value = value;
      }

      @Override
      public char getCharKey() {
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
         } else if (o instanceof Char2CharMap.Entry) {
            Char2CharMap.Entry e = (Char2CharMap.Entry)o;
            return this.key == e.getCharKey() && this.value == e.getCharValue();
         } else {
            Entry<?, ?> e = (Entry<?, ?>)o;
            Object key = e.getKey();
            if (key != null && key instanceof Character) {
               Object value = e.getValue();
               return value != null && value instanceof Character ? this.key == (Character)key && this.value == (Character)value : false;
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

   public abstract static class BasicEntrySet extends AbstractObjectSet<Char2CharMap.Entry> {
      protected final Char2CharMap map;

      public BasicEntrySet(Char2CharMap map) {
         this.map = map;
      }

      @Override
      public boolean contains(Object o) {
         if (!(o instanceof Entry)) {
            return false;
         } else if (o instanceof Char2CharMap.Entry) {
            Char2CharMap.Entry e = (Char2CharMap.Entry)o;
            char k = e.getCharKey();
            return this.map.containsKey(k) && this.map.get(k) == e.getCharValue();
         } else {
            Entry<?, ?> e = (Entry<?, ?>)o;
            Object key = e.getKey();
            if (key != null && key instanceof Character) {
               char k = (Character)key;
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
         } else if (o instanceof Char2CharMap.Entry) {
            Char2CharMap.Entry e = (Char2CharMap.Entry)o;
            return this.map.remove(e.getCharKey(), e.getCharValue());
         } else {
            Entry<?, ?> e = (Entry<?, ?>)o;
            Object key = e.getKey();
            if (key != null && key instanceof Character) {
               char k = (Character)key;
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
      public ObjectSpliterator<Char2CharMap.Entry> spliterator() {
         return ObjectSpliterators.asSpliterator(this.iterator(), Size64.sizeOf(this.map), 65);
      }
   }
}
