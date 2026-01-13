package it.unimi.dsi.fastutil.bytes;

import it.unimi.dsi.fastutil.HashCommon;
import it.unimi.dsi.fastutil.Size64;
import it.unimi.dsi.fastutil.longs.AbstractLongCollection;
import it.unimi.dsi.fastutil.longs.LongBinaryOperator;
import it.unimi.dsi.fastutil.longs.LongCollection;
import it.unimi.dsi.fastutil.longs.LongIterator;
import it.unimi.dsi.fastutil.longs.LongSpliterator;
import it.unimi.dsi.fastutil.longs.LongSpliterators;
import it.unimi.dsi.fastutil.objects.AbstractObjectSet;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectSpliterator;
import it.unimi.dsi.fastutil.objects.ObjectSpliterators;
import java.io.Serializable;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.LongConsumer;

public abstract class AbstractByte2LongMap extends AbstractByte2LongFunction implements Byte2LongMap, Serializable {
   private static final long serialVersionUID = -4940583368468432370L;

   protected AbstractByte2LongMap() {
   }

   @Override
   public boolean containsKey(byte k) {
      ObjectIterator<Byte2LongMap.Entry> i = this.byte2LongEntrySet().iterator();

      while (i.hasNext()) {
         if (i.next().getByteKey() == k) {
            return true;
         }
      }

      return false;
   }

   @Override
   public boolean containsValue(long v) {
      ObjectIterator<Byte2LongMap.Entry> i = this.byte2LongEntrySet().iterator();

      while (i.hasNext()) {
         if (i.next().getLongValue() == v) {
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
   public final long mergeLong(byte key, long value, LongBinaryOperator remappingFunction) {
      return this.mergeLong(key, value, remappingFunction);
   }

   @Override
   public ByteSet keySet() {
      return new AbstractByteSet() {
         @Override
         public boolean contains(byte k) {
            return AbstractByte2LongMap.this.containsKey(k);
         }

         @Override
         public int size() {
            return AbstractByte2LongMap.this.size();
         }

         @Override
         public void clear() {
            AbstractByte2LongMap.this.clear();
         }

         @Override
         public ByteIterator iterator() {
            return new ByteIterator() {
               private final ObjectIterator<Byte2LongMap.Entry> i = Byte2LongMaps.fastIterator(AbstractByte2LongMap.this);

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
            return ByteSpliterators.asSpliterator(this.iterator(), Size64.sizeOf(AbstractByte2LongMap.this), 321);
         }
      };
   }

   @Override
   public LongCollection values() {
      return new AbstractLongCollection() {
         @Override
         public boolean contains(long k) {
            return AbstractByte2LongMap.this.containsValue(k);
         }

         @Override
         public int size() {
            return AbstractByte2LongMap.this.size();
         }

         @Override
         public void clear() {
            AbstractByte2LongMap.this.clear();
         }

         @Override
         public LongIterator iterator() {
            return new LongIterator() {
               private final ObjectIterator<Byte2LongMap.Entry> i = Byte2LongMaps.fastIterator(AbstractByte2LongMap.this);

               @Override
               public long nextLong() {
                  return this.i.next().getLongValue();
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
               public void forEachRemaining(LongConsumer action) {
                  this.i.forEachRemaining(entry -> action.accept(entry.getLongValue()));
               }
            };
         }

         @Override
         public LongSpliterator spliterator() {
            return LongSpliterators.asSpliterator(this.iterator(), Size64.sizeOf(AbstractByte2LongMap.this), 320);
         }
      };
   }

   @Override
   public void putAll(Map<? extends Byte, ? extends Long> m) {
      if (m instanceof Byte2LongMap) {
         ObjectIterator<Byte2LongMap.Entry> i = Byte2LongMaps.fastIterator((Byte2LongMap)m);

         while (i.hasNext()) {
            Byte2LongMap.Entry e = i.next();
            this.put(e.getByteKey(), e.getLongValue());
         }
      } else {
         int n = m.size();
         Iterator<? extends Entry<? extends Byte, ? extends Long>> i = m.entrySet().iterator();

         while (n-- != 0) {
            Entry<? extends Byte, ? extends Long> e = (Entry<? extends Byte, ? extends Long>)i.next();
            this.put(e.getKey(), e.getValue());
         }
      }
   }

   @Override
   public int hashCode() {
      int h = 0;
      int n = this.size();
      ObjectIterator<Byte2LongMap.Entry> i = Byte2LongMaps.fastIterator(this);

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
         return m.size() != this.size() ? false : this.byte2LongEntrySet().containsAll(m.entrySet());
      }
   }

   @Override
   public String toString() {
      StringBuilder s = new StringBuilder();
      ObjectIterator<Byte2LongMap.Entry> i = Byte2LongMaps.fastIterator(this);
      int n = this.size();
      boolean first = true;
      s.append("{");

      while (n-- != 0) {
         if (first) {
            first = false;
         } else {
            s.append(", ");
         }

         Byte2LongMap.Entry e = i.next();
         s.append(String.valueOf(e.getByteKey()));
         s.append("=>");
         s.append(String.valueOf(e.getLongValue()));
      }

      s.append("}");
      return s.toString();
   }

   public static class BasicEntry implements Byte2LongMap.Entry {
      protected byte key;
      protected long value;

      public BasicEntry() {
      }

      public BasicEntry(Byte key, Long value) {
         this.key = key;
         this.value = value;
      }

      public BasicEntry(byte key, long value) {
         this.key = key;
         this.value = value;
      }

      @Override
      public byte getByteKey() {
         return this.key;
      }

      @Override
      public long getLongValue() {
         return this.value;
      }

      @Override
      public long setValue(long value) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean equals(Object o) {
         if (!(o instanceof Entry)) {
            return false;
         } else if (o instanceof Byte2LongMap.Entry) {
            Byte2LongMap.Entry e = (Byte2LongMap.Entry)o;
            return this.key == e.getByteKey() && this.value == e.getLongValue();
         } else {
            Entry<?, ?> e = (Entry<?, ?>)o;
            Object key = e.getKey();
            if (key != null && key instanceof Byte) {
               Object value = e.getValue();
               return value != null && value instanceof Long ? this.key == (Byte)key && this.value == (Long)value : false;
            } else {
               return false;
            }
         }
      }

      @Override
      public int hashCode() {
         return this.key ^ HashCommon.long2int(this.value);
      }

      @Override
      public String toString() {
         return this.key + "->" + this.value;
      }
   }

   public abstract static class BasicEntrySet extends AbstractObjectSet<Byte2LongMap.Entry> {
      protected final Byte2LongMap map;

      public BasicEntrySet(Byte2LongMap map) {
         this.map = map;
      }

      @Override
      public boolean contains(Object o) {
         if (!(o instanceof Entry)) {
            return false;
         } else if (o instanceof Byte2LongMap.Entry) {
            Byte2LongMap.Entry e = (Byte2LongMap.Entry)o;
            byte k = e.getByteKey();
            return this.map.containsKey(k) && this.map.get(k) == e.getLongValue();
         } else {
            Entry<?, ?> e = (Entry<?, ?>)o;
            Object key = e.getKey();
            if (key != null && key instanceof Byte) {
               byte k = (Byte)key;
               Object value = e.getValue();
               return value != null && value instanceof Long ? this.map.containsKey(k) && this.map.get(k) == (Long)value : false;
            } else {
               return false;
            }
         }
      }

      @Override
      public boolean remove(Object o) {
         if (!(o instanceof Entry)) {
            return false;
         } else if (o instanceof Byte2LongMap.Entry) {
            Byte2LongMap.Entry e = (Byte2LongMap.Entry)o;
            return this.map.remove(e.getByteKey(), e.getLongValue());
         } else {
            Entry<?, ?> e = (Entry<?, ?>)o;
            Object key = e.getKey();
            if (key != null && key instanceof Byte) {
               byte k = (Byte)key;
               Object value = e.getValue();
               if (value != null && value instanceof Long) {
                  long v = (Long)value;
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
      public ObjectSpliterator<Byte2LongMap.Entry> spliterator() {
         return ObjectSpliterators.asSpliterator(this.iterator(), Size64.sizeOf(this.map), 65);
      }
   }
}
