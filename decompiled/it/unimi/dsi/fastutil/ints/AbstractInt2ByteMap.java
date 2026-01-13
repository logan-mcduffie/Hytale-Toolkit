package it.unimi.dsi.fastutil.ints;

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

public abstract class AbstractInt2ByteMap extends AbstractInt2ByteFunction implements Int2ByteMap, Serializable {
   private static final long serialVersionUID = -4940583368468432370L;

   protected AbstractInt2ByteMap() {
   }

   @Override
   public boolean containsKey(int k) {
      ObjectIterator<Int2ByteMap.Entry> i = this.int2ByteEntrySet().iterator();

      while (i.hasNext()) {
         if (i.next().getIntKey() == k) {
            return true;
         }
      }

      return false;
   }

   @Override
   public boolean containsValue(byte v) {
      ObjectIterator<Int2ByteMap.Entry> i = this.int2ByteEntrySet().iterator();

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
   public IntSet keySet() {
      return new AbstractIntSet() {
         @Override
         public boolean contains(int k) {
            return AbstractInt2ByteMap.this.containsKey(k);
         }

         @Override
         public int size() {
            return AbstractInt2ByteMap.this.size();
         }

         @Override
         public void clear() {
            AbstractInt2ByteMap.this.clear();
         }

         @Override
         public IntIterator iterator() {
            return new IntIterator() {
               private final ObjectIterator<Int2ByteMap.Entry> i = Int2ByteMaps.fastIterator(AbstractInt2ByteMap.this);

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
            return IntSpliterators.asSpliterator(this.iterator(), Size64.sizeOf(AbstractInt2ByteMap.this), 321);
         }
      };
   }

   @Override
   public ByteCollection values() {
      return new AbstractByteCollection() {
         @Override
         public boolean contains(byte k) {
            return AbstractInt2ByteMap.this.containsValue(k);
         }

         @Override
         public int size() {
            return AbstractInt2ByteMap.this.size();
         }

         @Override
         public void clear() {
            AbstractInt2ByteMap.this.clear();
         }

         @Override
         public ByteIterator iterator() {
            return new ByteIterator() {
               private final ObjectIterator<Int2ByteMap.Entry> i = Int2ByteMaps.fastIterator(AbstractInt2ByteMap.this);

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
            return ByteSpliterators.asSpliterator(this.iterator(), Size64.sizeOf(AbstractInt2ByteMap.this), 320);
         }
      };
   }

   @Override
   public void putAll(Map<? extends Integer, ? extends Byte> m) {
      if (m instanceof Int2ByteMap) {
         ObjectIterator<Int2ByteMap.Entry> i = Int2ByteMaps.fastIterator((Int2ByteMap)m);

         while (i.hasNext()) {
            Int2ByteMap.Entry e = i.next();
            this.put(e.getIntKey(), e.getByteValue());
         }
      } else {
         int n = m.size();
         Iterator<? extends Entry<? extends Integer, ? extends Byte>> i = m.entrySet().iterator();

         while (n-- != 0) {
            Entry<? extends Integer, ? extends Byte> e = (Entry<? extends Integer, ? extends Byte>)i.next();
            this.put(e.getKey(), e.getValue());
         }
      }
   }

   @Override
   public int hashCode() {
      int h = 0;
      int n = this.size();
      ObjectIterator<Int2ByteMap.Entry> i = Int2ByteMaps.fastIterator(this);

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
         return m.size() != this.size() ? false : this.int2ByteEntrySet().containsAll(m.entrySet());
      }
   }

   @Override
   public String toString() {
      StringBuilder s = new StringBuilder();
      ObjectIterator<Int2ByteMap.Entry> i = Int2ByteMaps.fastIterator(this);
      int n = this.size();
      boolean first = true;
      s.append("{");

      while (n-- != 0) {
         if (first) {
            first = false;
         } else {
            s.append(", ");
         }

         Int2ByteMap.Entry e = i.next();
         s.append(String.valueOf(e.getIntKey()));
         s.append("=>");
         s.append(String.valueOf(e.getByteValue()));
      }

      s.append("}");
      return s.toString();
   }

   public static class BasicEntry implements Int2ByteMap.Entry {
      protected int key;
      protected byte value;

      public BasicEntry() {
      }

      public BasicEntry(Integer key, Byte value) {
         this.key = key;
         this.value = value;
      }

      public BasicEntry(int key, byte value) {
         this.key = key;
         this.value = value;
      }

      @Override
      public int getIntKey() {
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
         } else if (o instanceof Int2ByteMap.Entry) {
            Int2ByteMap.Entry e = (Int2ByteMap.Entry)o;
            return this.key == e.getIntKey() && this.value == e.getByteValue();
         } else {
            Entry<?, ?> e = (Entry<?, ?>)o;
            Object key = e.getKey();
            if (key != null && key instanceof Integer) {
               Object value = e.getValue();
               return value != null && value instanceof Byte ? this.key == (Integer)key && this.value == (Byte)value : false;
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

   public abstract static class BasicEntrySet extends AbstractObjectSet<Int2ByteMap.Entry> {
      protected final Int2ByteMap map;

      public BasicEntrySet(Int2ByteMap map) {
         this.map = map;
      }

      @Override
      public boolean contains(Object o) {
         if (!(o instanceof Entry)) {
            return false;
         } else if (o instanceof Int2ByteMap.Entry) {
            Int2ByteMap.Entry e = (Int2ByteMap.Entry)o;
            int k = e.getIntKey();
            return this.map.containsKey(k) && this.map.get(k) == e.getByteValue();
         } else {
            Entry<?, ?> e = (Entry<?, ?>)o;
            Object key = e.getKey();
            if (key != null && key instanceof Integer) {
               int k = (Integer)key;
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
         } else if (o instanceof Int2ByteMap.Entry) {
            Int2ByteMap.Entry e = (Int2ByteMap.Entry)o;
            return this.map.remove(e.getIntKey(), e.getByteValue());
         } else {
            Entry<?, ?> e = (Entry<?, ?>)o;
            Object key = e.getKey();
            if (key != null && key instanceof Integer) {
               int k = (Integer)key;
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
      public ObjectSpliterator<Int2ByteMap.Entry> spliterator() {
         return ObjectSpliterators.asSpliterator(this.iterator(), Size64.sizeOf(this.map), 65);
      }
   }
}
