package it.unimi.dsi.fastutil.bytes;

import it.unimi.dsi.fastutil.Size64;
import it.unimi.dsi.fastutil.ints.AbstractIntCollection;
import it.unimi.dsi.fastutil.ints.IntBinaryOperator;
import it.unimi.dsi.fastutil.ints.IntCollection;
import it.unimi.dsi.fastutil.ints.IntIterator;
import it.unimi.dsi.fastutil.ints.IntSpliterator;
import it.unimi.dsi.fastutil.ints.IntSpliterators;
import it.unimi.dsi.fastutil.objects.AbstractObjectSet;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectSpliterator;
import it.unimi.dsi.fastutil.objects.ObjectSpliterators;
import java.io.Serializable;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.IntConsumer;

public abstract class AbstractByte2IntMap extends AbstractByte2IntFunction implements Byte2IntMap, Serializable {
   private static final long serialVersionUID = -4940583368468432370L;

   protected AbstractByte2IntMap() {
   }

   @Override
   public boolean containsKey(byte k) {
      ObjectIterator<Byte2IntMap.Entry> i = this.byte2IntEntrySet().iterator();

      while (i.hasNext()) {
         if (i.next().getByteKey() == k) {
            return true;
         }
      }

      return false;
   }

   @Override
   public boolean containsValue(int v) {
      ObjectIterator<Byte2IntMap.Entry> i = this.byte2IntEntrySet().iterator();

      while (i.hasNext()) {
         if (i.next().getIntValue() == v) {
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
   public final int mergeInt(byte key, int value, IntBinaryOperator remappingFunction) {
      return this.mergeInt(key, value, remappingFunction);
   }

   @Override
   public ByteSet keySet() {
      return new AbstractByteSet() {
         @Override
         public boolean contains(byte k) {
            return AbstractByte2IntMap.this.containsKey(k);
         }

         @Override
         public int size() {
            return AbstractByte2IntMap.this.size();
         }

         @Override
         public void clear() {
            AbstractByte2IntMap.this.clear();
         }

         @Override
         public ByteIterator iterator() {
            return new ByteIterator() {
               private final ObjectIterator<Byte2IntMap.Entry> i = Byte2IntMaps.fastIterator(AbstractByte2IntMap.this);

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
            return ByteSpliterators.asSpliterator(this.iterator(), Size64.sizeOf(AbstractByte2IntMap.this), 321);
         }
      };
   }

   @Override
   public IntCollection values() {
      return new AbstractIntCollection() {
         @Override
         public boolean contains(int k) {
            return AbstractByte2IntMap.this.containsValue(k);
         }

         @Override
         public int size() {
            return AbstractByte2IntMap.this.size();
         }

         @Override
         public void clear() {
            AbstractByte2IntMap.this.clear();
         }

         @Override
         public IntIterator iterator() {
            return new IntIterator() {
               private final ObjectIterator<Byte2IntMap.Entry> i = Byte2IntMaps.fastIterator(AbstractByte2IntMap.this);

               @Override
               public int nextInt() {
                  return this.i.next().getIntValue();
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
               public void forEachRemaining(IntConsumer action) {
                  this.i.forEachRemaining(entry -> action.accept(entry.getIntValue()));
               }
            };
         }

         @Override
         public IntSpliterator spliterator() {
            return IntSpliterators.asSpliterator(this.iterator(), Size64.sizeOf(AbstractByte2IntMap.this), 320);
         }
      };
   }

   @Override
   public void putAll(Map<? extends Byte, ? extends Integer> m) {
      if (m instanceof Byte2IntMap) {
         ObjectIterator<Byte2IntMap.Entry> i = Byte2IntMaps.fastIterator((Byte2IntMap)m);

         while (i.hasNext()) {
            Byte2IntMap.Entry e = i.next();
            this.put(e.getByteKey(), e.getIntValue());
         }
      } else {
         int n = m.size();
         Iterator<? extends Entry<? extends Byte, ? extends Integer>> i = m.entrySet().iterator();

         while (n-- != 0) {
            Entry<? extends Byte, ? extends Integer> e = (Entry<? extends Byte, ? extends Integer>)i.next();
            this.put(e.getKey(), e.getValue());
         }
      }
   }

   @Override
   public int hashCode() {
      int h = 0;
      int n = this.size();
      ObjectIterator<Byte2IntMap.Entry> i = Byte2IntMaps.fastIterator(this);

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
         return m.size() != this.size() ? false : this.byte2IntEntrySet().containsAll(m.entrySet());
      }
   }

   @Override
   public String toString() {
      StringBuilder s = new StringBuilder();
      ObjectIterator<Byte2IntMap.Entry> i = Byte2IntMaps.fastIterator(this);
      int n = this.size();
      boolean first = true;
      s.append("{");

      while (n-- != 0) {
         if (first) {
            first = false;
         } else {
            s.append(", ");
         }

         Byte2IntMap.Entry e = i.next();
         s.append(String.valueOf(e.getByteKey()));
         s.append("=>");
         s.append(String.valueOf(e.getIntValue()));
      }

      s.append("}");
      return s.toString();
   }

   public static class BasicEntry implements Byte2IntMap.Entry {
      protected byte key;
      protected int value;

      public BasicEntry() {
      }

      public BasicEntry(Byte key, Integer value) {
         this.key = key;
         this.value = value;
      }

      public BasicEntry(byte key, int value) {
         this.key = key;
         this.value = value;
      }

      @Override
      public byte getByteKey() {
         return this.key;
      }

      @Override
      public int getIntValue() {
         return this.value;
      }

      @Override
      public int setValue(int value) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean equals(Object o) {
         if (!(o instanceof Entry)) {
            return false;
         } else if (o instanceof Byte2IntMap.Entry) {
            Byte2IntMap.Entry e = (Byte2IntMap.Entry)o;
            return this.key == e.getByteKey() && this.value == e.getIntValue();
         } else {
            Entry<?, ?> e = (Entry<?, ?>)o;
            Object key = e.getKey();
            if (key != null && key instanceof Byte) {
               Object value = e.getValue();
               return value != null && value instanceof Integer ? this.key == (Byte)key && this.value == (Integer)value : false;
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

   public abstract static class BasicEntrySet extends AbstractObjectSet<Byte2IntMap.Entry> {
      protected final Byte2IntMap map;

      public BasicEntrySet(Byte2IntMap map) {
         this.map = map;
      }

      @Override
      public boolean contains(Object o) {
         if (!(o instanceof Entry)) {
            return false;
         } else if (o instanceof Byte2IntMap.Entry) {
            Byte2IntMap.Entry e = (Byte2IntMap.Entry)o;
            byte k = e.getByteKey();
            return this.map.containsKey(k) && this.map.get(k) == e.getIntValue();
         } else {
            Entry<?, ?> e = (Entry<?, ?>)o;
            Object key = e.getKey();
            if (key != null && key instanceof Byte) {
               byte k = (Byte)key;
               Object value = e.getValue();
               return value != null && value instanceof Integer ? this.map.containsKey(k) && this.map.get(k) == (Integer)value : false;
            } else {
               return false;
            }
         }
      }

      @Override
      public boolean remove(Object o) {
         if (!(o instanceof Entry)) {
            return false;
         } else if (o instanceof Byte2IntMap.Entry) {
            Byte2IntMap.Entry e = (Byte2IntMap.Entry)o;
            return this.map.remove(e.getByteKey(), e.getIntValue());
         } else {
            Entry<?, ?> e = (Entry<?, ?>)o;
            Object key = e.getKey();
            if (key != null && key instanceof Byte) {
               byte k = (Byte)key;
               Object value = e.getValue();
               if (value != null && value instanceof Integer) {
                  int v = (Integer)value;
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
      public ObjectSpliterator<Byte2IntMap.Entry> spliterator() {
         return ObjectSpliterators.asSpliterator(this.iterator(), Size64.sizeOf(this.map), 65);
      }
   }
}
