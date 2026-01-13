package it.unimi.dsi.fastutil.doubles;

import it.unimi.dsi.fastutil.HashCommon;
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

public abstract class AbstractDouble2CharMap extends AbstractDouble2CharFunction implements Double2CharMap, Serializable {
   private static final long serialVersionUID = -4940583368468432370L;

   protected AbstractDouble2CharMap() {
   }

   @Override
   public boolean containsKey(double k) {
      ObjectIterator<Double2CharMap.Entry> i = this.double2CharEntrySet().iterator();

      while (i.hasNext()) {
         if (i.next().getDoubleKey() == k) {
            return true;
         }
      }

      return false;
   }

   @Override
   public boolean containsValue(char v) {
      ObjectIterator<Double2CharMap.Entry> i = this.double2CharEntrySet().iterator();

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
   public DoubleSet keySet() {
      return new AbstractDoubleSet() {
         @Override
         public boolean contains(double k) {
            return AbstractDouble2CharMap.this.containsKey(k);
         }

         @Override
         public int size() {
            return AbstractDouble2CharMap.this.size();
         }

         @Override
         public void clear() {
            AbstractDouble2CharMap.this.clear();
         }

         @Override
         public DoubleIterator iterator() {
            return new DoubleIterator() {
               private final ObjectIterator<Double2CharMap.Entry> i = Double2CharMaps.fastIterator(AbstractDouble2CharMap.this);

               @Override
               public double nextDouble() {
                  return this.i.next().getDoubleKey();
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
               public void forEachRemaining(java.util.function.DoubleConsumer action) {
                  this.i.forEachRemaining(entry -> action.accept(entry.getDoubleKey()));
               }
            };
         }

         @Override
         public DoubleSpliterator spliterator() {
            return DoubleSpliterators.asSpliterator(this.iterator(), Size64.sizeOf(AbstractDouble2CharMap.this), 321);
         }
      };
   }

   @Override
   public CharCollection values() {
      return new AbstractCharCollection() {
         @Override
         public boolean contains(char k) {
            return AbstractDouble2CharMap.this.containsValue(k);
         }

         @Override
         public int size() {
            return AbstractDouble2CharMap.this.size();
         }

         @Override
         public void clear() {
            AbstractDouble2CharMap.this.clear();
         }

         @Override
         public CharIterator iterator() {
            return new CharIterator() {
               private final ObjectIterator<Double2CharMap.Entry> i = Double2CharMaps.fastIterator(AbstractDouble2CharMap.this);

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
            return CharSpliterators.asSpliterator(this.iterator(), Size64.sizeOf(AbstractDouble2CharMap.this), 320);
         }
      };
   }

   @Override
   public void putAll(Map<? extends Double, ? extends Character> m) {
      if (m instanceof Double2CharMap) {
         ObjectIterator<Double2CharMap.Entry> i = Double2CharMaps.fastIterator((Double2CharMap)m);

         while (i.hasNext()) {
            Double2CharMap.Entry e = i.next();
            this.put(e.getDoubleKey(), e.getCharValue());
         }
      } else {
         int n = m.size();
         Iterator<? extends Entry<? extends Double, ? extends Character>> i = m.entrySet().iterator();

         while (n-- != 0) {
            Entry<? extends Double, ? extends Character> e = (Entry<? extends Double, ? extends Character>)i.next();
            this.put(e.getKey(), e.getValue());
         }
      }
   }

   @Override
   public int hashCode() {
      int h = 0;
      int n = this.size();
      ObjectIterator<Double2CharMap.Entry> i = Double2CharMaps.fastIterator(this);

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
         return m.size() != this.size() ? false : this.double2CharEntrySet().containsAll(m.entrySet());
      }
   }

   @Override
   public String toString() {
      StringBuilder s = new StringBuilder();
      ObjectIterator<Double2CharMap.Entry> i = Double2CharMaps.fastIterator(this);
      int n = this.size();
      boolean first = true;
      s.append("{");

      while (n-- != 0) {
         if (first) {
            first = false;
         } else {
            s.append(", ");
         }

         Double2CharMap.Entry e = i.next();
         s.append(String.valueOf(e.getDoubleKey()));
         s.append("=>");
         s.append(String.valueOf(e.getCharValue()));
      }

      s.append("}");
      return s.toString();
   }

   public static class BasicEntry implements Double2CharMap.Entry {
      protected double key;
      protected char value;

      public BasicEntry() {
      }

      public BasicEntry(Double key, Character value) {
         this.key = key;
         this.value = value;
      }

      public BasicEntry(double key, char value) {
         this.key = key;
         this.value = value;
      }

      @Override
      public double getDoubleKey() {
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
         } else if (o instanceof Double2CharMap.Entry) {
            Double2CharMap.Entry e = (Double2CharMap.Entry)o;
            return Double.doubleToLongBits(this.key) == Double.doubleToLongBits(e.getDoubleKey()) && this.value == e.getCharValue();
         } else {
            Entry<?, ?> e = (Entry<?, ?>)o;
            Object key = e.getKey();
            if (key != null && key instanceof Double) {
               Object value = e.getValue();
               return value != null && value instanceof Character
                  ? Double.doubleToLongBits(this.key) == Double.doubleToLongBits((Double)key) && this.value == (Character)value
                  : false;
            } else {
               return false;
            }
         }
      }

      @Override
      public int hashCode() {
         return HashCommon.double2int(this.key) ^ this.value;
      }

      @Override
      public String toString() {
         return this.key + "->" + this.value;
      }
   }

   public abstract static class BasicEntrySet extends AbstractObjectSet<Double2CharMap.Entry> {
      protected final Double2CharMap map;

      public BasicEntrySet(Double2CharMap map) {
         this.map = map;
      }

      @Override
      public boolean contains(Object o) {
         if (!(o instanceof Entry)) {
            return false;
         } else if (o instanceof Double2CharMap.Entry) {
            Double2CharMap.Entry e = (Double2CharMap.Entry)o;
            double k = e.getDoubleKey();
            return this.map.containsKey(k) && this.map.get(k) == e.getCharValue();
         } else {
            Entry<?, ?> e = (Entry<?, ?>)o;
            Object key = e.getKey();
            if (key != null && key instanceof Double) {
               double k = (Double)key;
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
         } else if (o instanceof Double2CharMap.Entry) {
            Double2CharMap.Entry e = (Double2CharMap.Entry)o;
            return this.map.remove(e.getDoubleKey(), e.getCharValue());
         } else {
            Entry<?, ?> e = (Entry<?, ?>)o;
            Object key = e.getKey();
            if (key != null && key instanceof Double) {
               double k = (Double)key;
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
      public ObjectSpliterator<Double2CharMap.Entry> spliterator() {
         return ObjectSpliterators.asSpliterator(this.iterator(), Size64.sizeOf(this.map), 65);
      }
   }
}
