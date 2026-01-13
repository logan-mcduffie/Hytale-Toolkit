package it.unimi.dsi.fastutil.chars;

import it.unimi.dsi.fastutil.Size64;
import it.unimi.dsi.fastutil.bytes.AbstractByteCollection;
import it.unimi.dsi.fastutil.bytes.ByteCollection;
import it.unimi.dsi.fastutil.bytes.ByteConsumer;
import it.unimi.dsi.fastutil.bytes.ByteIterator;
import it.unimi.dsi.fastutil.bytes.ByteSpliterator;
import it.unimi.dsi.fastutil.bytes.ByteSpliterators;
import it.unimi.dsi.fastutil.objects.AbstractObjectSet;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectSpliterator;
import it.unimi.dsi.fastutil.objects.ObjectSpliterators;
import java.io.Serializable;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

public abstract class AbstractChar2ByteMap extends AbstractChar2ByteFunction implements Char2ByteMap, Serializable {
   private static final long serialVersionUID = -4940583368468432370L;

   protected AbstractChar2ByteMap() {
   }

   @Override
   public boolean containsKey(char k) {
      ObjectIterator<Char2ByteMap.Entry> i = this.char2ByteEntrySet().iterator();

      while (i.hasNext()) {
         if (i.next().getCharKey() == k) {
            return true;
         }
      }

      return false;
   }

   @Override
   public boolean containsValue(byte v) {
      ObjectIterator<Char2ByteMap.Entry> i = this.char2ByteEntrySet().iterator();

      while (i.hasNext()) {
         if (i.next().getByteValue() == v) {
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
            return AbstractChar2ByteMap.this.containsKey(k);
         }

         @Override
         public int size() {
            return AbstractChar2ByteMap.this.size();
         }

         @Override
         public void clear() {
            AbstractChar2ByteMap.this.clear();
         }

         @Override
         public CharIterator iterator() {
            return new CharIterator() {
               private final ObjectIterator<Char2ByteMap.Entry> i = Char2ByteMaps.fastIterator(AbstractChar2ByteMap.this);

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
            return CharSpliterators.asSpliterator(this.iterator(), Size64.sizeOf(AbstractChar2ByteMap.this), 321);
         }
      };
   }

   @Override
   public ByteCollection values() {
      return new AbstractByteCollection() {
         @Override
         public boolean contains(byte k) {
            return AbstractChar2ByteMap.this.containsValue(k);
         }

         @Override
         public int size() {
            return AbstractChar2ByteMap.this.size();
         }

         @Override
         public void clear() {
            AbstractChar2ByteMap.this.clear();
         }

         @Override
         public ByteIterator iterator() {
            return new ByteIterator() {
               private final ObjectIterator<Char2ByteMap.Entry> i = Char2ByteMaps.fastIterator(AbstractChar2ByteMap.this);

               @Override
               public byte nextByte() {
                  return this.i.next().getByteValue();
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
               public void forEachRemaining(ByteConsumer action) {
                  this.i.forEachRemaining(entry -> action.accept(entry.getByteValue()));
               }
            };
         }

         @Override
         public ByteSpliterator spliterator() {
            return ByteSpliterators.asSpliterator(this.iterator(), Size64.sizeOf(AbstractChar2ByteMap.this), 320);
         }
      };
   }

   @Override
   public void putAll(Map<? extends Character, ? extends Byte> m) {
      if (m instanceof Char2ByteMap) {
         ObjectIterator<Char2ByteMap.Entry> i = Char2ByteMaps.fastIterator((Char2ByteMap)m);

         while (i.hasNext()) {
            Char2ByteMap.Entry e = i.next();
            this.put(e.getCharKey(), e.getByteValue());
         }
      } else {
         int n = m.size();
         Iterator<? extends Entry<? extends Character, ? extends Byte>> i = m.entrySet().iterator();

         while (n-- != 0) {
            Entry<? extends Character, ? extends Byte> e = (Entry<? extends Character, ? extends Byte>)i.next();
            this.put(e.getKey(), e.getValue());
         }
      }
   }

   @Override
   public int hashCode() {
      int h = 0;
      int n = this.size();
      ObjectIterator<Char2ByteMap.Entry> i = Char2ByteMaps.fastIterator(this);

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
         return m.size() != this.size() ? false : this.char2ByteEntrySet().containsAll(m.entrySet());
      }
   }

   @Override
   public String toString() {
      StringBuilder s = new StringBuilder();
      ObjectIterator<Char2ByteMap.Entry> i = Char2ByteMaps.fastIterator(this);
      int n = this.size();
      boolean first = true;
      s.append("{");

      while (n-- != 0) {
         if (first) {
            first = false;
         } else {
            s.append(", ");
         }

         Char2ByteMap.Entry e = i.next();
         s.append(String.valueOf(e.getCharKey()));
         s.append("=>");
         s.append(String.valueOf(e.getByteValue()));
      }

      s.append("}");
      return s.toString();
   }

   public static class BasicEntry implements Char2ByteMap.Entry {
      protected char key;
      protected byte value;

      public BasicEntry() {
      }

      public BasicEntry(Character key, Byte value) {
         this.key = key;
         this.value = value;
      }

      public BasicEntry(char key, byte value) {
         this.key = key;
         this.value = value;
      }

      @Override
      public char getCharKey() {
         return this.key;
      }

      @Override
      public byte getByteValue() {
         return this.value;
      }

      @Override
      public byte setValue(byte value) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean equals(Object o) {
         if (!(o instanceof Entry)) {
            return false;
         } else if (o instanceof Char2ByteMap.Entry) {
            Char2ByteMap.Entry e = (Char2ByteMap.Entry)o;
            return this.key == e.getCharKey() && this.value == e.getByteValue();
         } else {
            Entry<?, ?> e = (Entry<?, ?>)o;
            Object key = e.getKey();
            if (key != null && key instanceof Character) {
               Object value = e.getValue();
               return value != null && value instanceof Byte ? this.key == (Character)key && this.value == (Byte)value : false;
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

   public abstract static class BasicEntrySet extends AbstractObjectSet<Char2ByteMap.Entry> {
      protected final Char2ByteMap map;

      public BasicEntrySet(Char2ByteMap map) {
         this.map = map;
      }

      @Override
      public boolean contains(Object o) {
         if (!(o instanceof Entry)) {
            return false;
         } else if (o instanceof Char2ByteMap.Entry) {
            Char2ByteMap.Entry e = (Char2ByteMap.Entry)o;
            char k = e.getCharKey();
            return this.map.containsKey(k) && this.map.get(k) == e.getByteValue();
         } else {
            Entry<?, ?> e = (Entry<?, ?>)o;
            Object key = e.getKey();
            if (key != null && key instanceof Character) {
               char k = (Character)key;
               Object value = e.getValue();
               return value != null && value instanceof Byte ? this.map.containsKey(k) && this.map.get(k) == (Byte)value : false;
            } else {
               return false;
            }
         }
      }

      @Override
      public boolean remove(Object o) {
         if (!(o instanceof Entry)) {
            return false;
         } else if (o instanceof Char2ByteMap.Entry) {
            Char2ByteMap.Entry e = (Char2ByteMap.Entry)o;
            return this.map.remove(e.getCharKey(), e.getByteValue());
         } else {
            Entry<?, ?> e = (Entry<?, ?>)o;
            Object key = e.getKey();
            if (key != null && key instanceof Character) {
               char k = (Character)key;
               Object value = e.getValue();
               if (value != null && value instanceof Byte) {
                  byte v = (Byte)value;
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
      public ObjectSpliterator<Char2ByteMap.Entry> spliterator() {
         return ObjectSpliterators.asSpliterator(this.iterator(), Size64.sizeOf(this.map), 65);
      }
   }
}
