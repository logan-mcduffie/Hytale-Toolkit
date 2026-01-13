package it.unimi.dsi.fastutil.bytes;

import it.unimi.dsi.fastutil.Size64;
import it.unimi.dsi.fastutil.objects.AbstractObjectSet;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectSpliterator;
import it.unimi.dsi.fastutil.objects.ObjectSpliterators;
import java.io.Serializable;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

public abstract class AbstractByte2ByteMap extends AbstractByte2ByteFunction implements Byte2ByteMap, Serializable {
   private static final long serialVersionUID = -4940583368468432370L;

   protected AbstractByte2ByteMap() {
   }

   @Override
   public boolean containsKey(byte k) {
      ObjectIterator<Byte2ByteMap.Entry> i = this.byte2ByteEntrySet().iterator();

      while (i.hasNext()) {
         if (i.next().getByteKey() == k) {
            return true;
         }
      }

      return false;
   }

   @Override
   public boolean containsValue(byte v) {
      ObjectIterator<Byte2ByteMap.Entry> i = this.byte2ByteEntrySet().iterator();

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
   public ByteSet keySet() {
      return new AbstractByteSet() {
         @Override
         public boolean contains(byte k) {
            return AbstractByte2ByteMap.this.containsKey(k);
         }

         @Override
         public int size() {
            return AbstractByte2ByteMap.this.size();
         }

         @Override
         public void clear() {
            AbstractByte2ByteMap.this.clear();
         }

         @Override
         public ByteIterator iterator() {
            return new ByteIterator() {
               private final ObjectIterator<Byte2ByteMap.Entry> i = Byte2ByteMaps.fastIterator(AbstractByte2ByteMap.this);

               @Override
               public byte nextByte() {
                  return this.i.next().getByteKey();
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
                  this.i.forEachRemaining(entry -> action.accept(entry.getByteKey()));
               }
            };
         }

         @Override
         public ByteSpliterator spliterator() {
            return ByteSpliterators.asSpliterator(this.iterator(), Size64.sizeOf(AbstractByte2ByteMap.this), 321);
         }
      };
   }

   @Override
   public ByteCollection values() {
      return new AbstractByteCollection() {
         @Override
         public boolean contains(byte k) {
            return AbstractByte2ByteMap.this.containsValue(k);
         }

         @Override
         public int size() {
            return AbstractByte2ByteMap.this.size();
         }

         @Override
         public void clear() {
            AbstractByte2ByteMap.this.clear();
         }

         @Override
         public ByteIterator iterator() {
            return new ByteIterator() {
               private final ObjectIterator<Byte2ByteMap.Entry> i = Byte2ByteMaps.fastIterator(AbstractByte2ByteMap.this);

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
            return ByteSpliterators.asSpliterator(this.iterator(), Size64.sizeOf(AbstractByte2ByteMap.this), 320);
         }
      };
   }

   @Override
   public void putAll(Map<? extends Byte, ? extends Byte> m) {
      if (m instanceof Byte2ByteMap) {
         ObjectIterator<Byte2ByteMap.Entry> i = Byte2ByteMaps.fastIterator((Byte2ByteMap)m);

         while (i.hasNext()) {
            Byte2ByteMap.Entry e = i.next();
            this.put(e.getByteKey(), e.getByteValue());
         }
      } else {
         int n = m.size();
         Iterator<? extends Entry<? extends Byte, ? extends Byte>> i = m.entrySet().iterator();

         while (n-- != 0) {
            Entry<? extends Byte, ? extends Byte> e = (Entry<? extends Byte, ? extends Byte>)i.next();
            this.put(e.getKey(), e.getValue());
         }
      }
   }

   @Override
   public int hashCode() {
      int h = 0;
      int n = this.size();
      ObjectIterator<Byte2ByteMap.Entry> i = Byte2ByteMaps.fastIterator(this);

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
         return m.size() != this.size() ? false : this.byte2ByteEntrySet().containsAll(m.entrySet());
      }
   }

   @Override
   public String toString() {
      StringBuilder s = new StringBuilder();
      ObjectIterator<Byte2ByteMap.Entry> i = Byte2ByteMaps.fastIterator(this);
      int n = this.size();
      boolean first = true;
      s.append("{");

      while (n-- != 0) {
         if (first) {
            first = false;
         } else {
            s.append(", ");
         }

         Byte2ByteMap.Entry e = i.next();
         s.append(String.valueOf(e.getByteKey()));
         s.append("=>");
         s.append(String.valueOf(e.getByteValue()));
      }

      s.append("}");
      return s.toString();
   }

   public static class BasicEntry implements Byte2ByteMap.Entry {
      protected byte key;
      protected byte value;

      public BasicEntry() {
      }

      public BasicEntry(Byte key, Byte value) {
         this.key = key;
         this.value = value;
      }

      public BasicEntry(byte key, byte value) {
         this.key = key;
         this.value = value;
      }

      @Override
      public byte getByteKey() {
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
         } else if (o instanceof Byte2ByteMap.Entry) {
            Byte2ByteMap.Entry e = (Byte2ByteMap.Entry)o;
            return this.key == e.getByteKey() && this.value == e.getByteValue();
         } else {
            Entry<?, ?> e = (Entry<?, ?>)o;
            Object key = e.getKey();
            if (key != null && key instanceof Byte) {
               Object value = e.getValue();
               return value != null && value instanceof Byte ? this.key == (Byte)key && this.value == (Byte)value : false;
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

   public abstract static class BasicEntrySet extends AbstractObjectSet<Byte2ByteMap.Entry> {
      protected final Byte2ByteMap map;

      public BasicEntrySet(Byte2ByteMap map) {
         this.map = map;
      }

      @Override
      public boolean contains(Object o) {
         if (!(o instanceof Entry)) {
            return false;
         } else if (o instanceof Byte2ByteMap.Entry) {
            Byte2ByteMap.Entry e = (Byte2ByteMap.Entry)o;
            byte k = e.getByteKey();
            return this.map.containsKey(k) && this.map.get(k) == e.getByteValue();
         } else {
            Entry<?, ?> e = (Entry<?, ?>)o;
            Object key = e.getKey();
            if (key != null && key instanceof Byte) {
               byte k = (Byte)key;
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
         } else if (o instanceof Byte2ByteMap.Entry) {
            Byte2ByteMap.Entry e = (Byte2ByteMap.Entry)o;
            return this.map.remove(e.getByteKey(), e.getByteValue());
         } else {
            Entry<?, ?> e = (Entry<?, ?>)o;
            Object key = e.getKey();
            if (key != null && key instanceof Byte) {
               byte k = (Byte)key;
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
      public ObjectSpliterator<Byte2ByteMap.Entry> spliterator() {
         return ObjectSpliterators.asSpliterator(this.iterator(), Size64.sizeOf(this.map), 65);
      }
   }
}
