package it.unimi.dsi.fastutil.chars;

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

public abstract class AbstractChar2BooleanMap extends AbstractChar2BooleanFunction implements Char2BooleanMap, Serializable {
   private static final long serialVersionUID = -4940583368468432370L;

   protected AbstractChar2BooleanMap() {
   }

   @Override
   public boolean containsKey(char k) {
      ObjectIterator<Char2BooleanMap.Entry> i = this.char2BooleanEntrySet().iterator();

      while (i.hasNext()) {
         if (i.next().getCharKey() == k) {
            return true;
         }
      }

      return false;
   }

   @Override
   public boolean containsValue(boolean v) {
      ObjectIterator<Char2BooleanMap.Entry> i = this.char2BooleanEntrySet().iterator();

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
   public CharSet keySet() {
      return new AbstractCharSet() {
         @Override
         public boolean contains(char k) {
            return AbstractChar2BooleanMap.this.containsKey(k);
         }

         @Override
         public int size() {
            return AbstractChar2BooleanMap.this.size();
         }

         @Override
         public void clear() {
            AbstractChar2BooleanMap.this.clear();
         }

         @Override
         public CharIterator iterator() {
            return new CharIterator() {
               private final ObjectIterator<Char2BooleanMap.Entry> i = Char2BooleanMaps.fastIterator(AbstractChar2BooleanMap.this);

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
            return CharSpliterators.asSpliterator(this.iterator(), Size64.sizeOf(AbstractChar2BooleanMap.this), 321);
         }
      };
   }

   @Override
   public BooleanCollection values() {
      return new AbstractBooleanCollection() {
         @Override
         public boolean contains(boolean k) {
            return AbstractChar2BooleanMap.this.containsValue(k);
         }

         @Override
         public int size() {
            return AbstractChar2BooleanMap.this.size();
         }

         @Override
         public void clear() {
            AbstractChar2BooleanMap.this.clear();
         }

         @Override
         public BooleanIterator iterator() {
            return new BooleanIterator() {
               private final ObjectIterator<Char2BooleanMap.Entry> i = Char2BooleanMaps.fastIterator(AbstractChar2BooleanMap.this);

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
            return BooleanSpliterators.asSpliterator(this.iterator(), Size64.sizeOf(AbstractChar2BooleanMap.this), 320);
         }
      };
   }

   @Override
   public void putAll(Map<? extends Character, ? extends Boolean> m) {
      if (m instanceof Char2BooleanMap) {
         ObjectIterator<Char2BooleanMap.Entry> i = Char2BooleanMaps.fastIterator((Char2BooleanMap)m);

         while (i.hasNext()) {
            Char2BooleanMap.Entry e = i.next();
            this.put(e.getCharKey(), e.getBooleanValue());
         }
      } else {
         int n = m.size();
         Iterator<? extends Entry<? extends Character, ? extends Boolean>> i = m.entrySet().iterator();

         while (n-- != 0) {
            Entry<? extends Character, ? extends Boolean> e = (Entry<? extends Character, ? extends Boolean>)i.next();
            this.put(e.getKey(), e.getValue());
         }
      }
   }

   @Override
   public int hashCode() {
      int h = 0;
      int n = this.size();
      ObjectIterator<Char2BooleanMap.Entry> i = Char2BooleanMaps.fastIterator(this);

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
         return m.size() != this.size() ? false : this.char2BooleanEntrySet().containsAll(m.entrySet());
      }
   }

   @Override
   public String toString() {
      StringBuilder s = new StringBuilder();
      ObjectIterator<Char2BooleanMap.Entry> i = Char2BooleanMaps.fastIterator(this);
      int n = this.size();
      boolean first = true;
      s.append("{");

      while (n-- != 0) {
         if (first) {
            first = false;
         } else {
            s.append(", ");
         }

         Char2BooleanMap.Entry e = i.next();
         s.append(String.valueOf(e.getCharKey()));
         s.append("=>");
         s.append(String.valueOf(e.getBooleanValue()));
      }

      s.append("}");
      return s.toString();
   }

   public static class BasicEntry implements Char2BooleanMap.Entry {
      protected char key;
      protected boolean value;

      public BasicEntry() {
      }

      public BasicEntry(Character key, Boolean value) {
         this.key = key;
         this.value = value;
      }

      public BasicEntry(char key, boolean value) {
         this.key = key;
         this.value = value;
      }

      @Override
      public char getCharKey() {
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
         } else if (o instanceof Char2BooleanMap.Entry) {
            Char2BooleanMap.Entry e = (Char2BooleanMap.Entry)o;
            return this.key == e.getCharKey() && this.value == e.getBooleanValue();
         } else {
            Entry<?, ?> e = (Entry<?, ?>)o;
            Object key = e.getKey();
            if (key != null && key instanceof Character) {
               Object value = e.getValue();
               return value != null && value instanceof Boolean ? this.key == (Character)key && this.value == (Boolean)value : false;
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

   public abstract static class BasicEntrySet extends AbstractObjectSet<Char2BooleanMap.Entry> {
      protected final Char2BooleanMap map;

      public BasicEntrySet(Char2BooleanMap map) {
         this.map = map;
      }

      @Override
      public boolean contains(Object o) {
         if (!(o instanceof Entry)) {
            return false;
         } else if (o instanceof Char2BooleanMap.Entry) {
            Char2BooleanMap.Entry e = (Char2BooleanMap.Entry)o;
            char k = e.getCharKey();
            return this.map.containsKey(k) && this.map.get(k) == e.getBooleanValue();
         } else {
            Entry<?, ?> e = (Entry<?, ?>)o;
            Object key = e.getKey();
            if (key != null && key instanceof Character) {
               char k = (Character)key;
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
         } else if (o instanceof Char2BooleanMap.Entry) {
            Char2BooleanMap.Entry e = (Char2BooleanMap.Entry)o;
            return this.map.remove(e.getCharKey(), e.getBooleanValue());
         } else {
            Entry<?, ?> e = (Entry<?, ?>)o;
            Object key = e.getKey();
            if (key != null && key instanceof Character) {
               char k = (Character)key;
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
      public ObjectSpliterator<Char2BooleanMap.Entry> spliterator() {
         return ObjectSpliterators.asSpliterator(this.iterator(), Size64.sizeOf(this.map), 65);
      }
   }
}
