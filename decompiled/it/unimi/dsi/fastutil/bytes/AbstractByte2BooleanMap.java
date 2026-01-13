package it.unimi.dsi.fastutil.bytes;

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

public abstract class AbstractByte2BooleanMap extends AbstractByte2BooleanFunction implements Byte2BooleanMap, Serializable {
   private static final long serialVersionUID = -4940583368468432370L;

   protected AbstractByte2BooleanMap() {
   }

   @Override
   public boolean containsKey(byte k) {
      ObjectIterator<Byte2BooleanMap.Entry> i = this.byte2BooleanEntrySet().iterator();

      while (i.hasNext()) {
         if (i.next().getByteKey() == k) {
            return true;
         }
      }

      return false;
   }

   @Override
   public boolean containsValue(boolean v) {
      ObjectIterator<Byte2BooleanMap.Entry> i = this.byte2BooleanEntrySet().iterator();

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
   public ByteSet keySet() {
      return new AbstractByteSet() {
         @Override
         public boolean contains(byte k) {
            return AbstractByte2BooleanMap.this.containsKey(k);
         }

         @Override
         public int size() {
            return AbstractByte2BooleanMap.this.size();
         }

         @Override
         public void clear() {
            AbstractByte2BooleanMap.this.clear();
         }

         @Override
         public ByteIterator iterator() {
            return new ByteIterator() {
               private final ObjectIterator<Byte2BooleanMap.Entry> i = Byte2BooleanMaps.fastIterator(AbstractByte2BooleanMap.this);

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
            return ByteSpliterators.asSpliterator(this.iterator(), Size64.sizeOf(AbstractByte2BooleanMap.this), 321);
         }
      };
   }

   @Override
   public BooleanCollection values() {
      return new AbstractBooleanCollection() {
         @Override
         public boolean contains(boolean k) {
            return AbstractByte2BooleanMap.this.containsValue(k);
         }

         @Override
         public int size() {
            return AbstractByte2BooleanMap.this.size();
         }

         @Override
         public void clear() {
            AbstractByte2BooleanMap.this.clear();
         }

         @Override
         public BooleanIterator iterator() {
            return new BooleanIterator() {
               private final ObjectIterator<Byte2BooleanMap.Entry> i = Byte2BooleanMaps.fastIterator(AbstractByte2BooleanMap.this);

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
            return BooleanSpliterators.asSpliterator(this.iterator(), Size64.sizeOf(AbstractByte2BooleanMap.this), 320);
         }
      };
   }

   @Override
   public void putAll(Map<? extends Byte, ? extends Boolean> m) {
      if (m instanceof Byte2BooleanMap) {
         ObjectIterator<Byte2BooleanMap.Entry> i = Byte2BooleanMaps.fastIterator((Byte2BooleanMap)m);

         while (i.hasNext()) {
            Byte2BooleanMap.Entry e = i.next();
            this.put(e.getByteKey(), e.getBooleanValue());
         }
      } else {
         int n = m.size();
         Iterator<? extends Entry<? extends Byte, ? extends Boolean>> i = m.entrySet().iterator();

         while (n-- != 0) {
            Entry<? extends Byte, ? extends Boolean> e = (Entry<? extends Byte, ? extends Boolean>)i.next();
            this.put(e.getKey(), e.getValue());
         }
      }
   }

   @Override
   public int hashCode() {
      int h = 0;
      int n = this.size();
      ObjectIterator<Byte2BooleanMap.Entry> i = Byte2BooleanMaps.fastIterator(this);

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
         return m.size() != this.size() ? false : this.byte2BooleanEntrySet().containsAll(m.entrySet());
      }
   }

   @Override
   public String toString() {
      StringBuilder s = new StringBuilder();
      ObjectIterator<Byte2BooleanMap.Entry> i = Byte2BooleanMaps.fastIterator(this);
      int n = this.size();
      boolean first = true;
      s.append("{");

      while (n-- != 0) {
         if (first) {
            first = false;
         } else {
            s.append(", ");
         }

         Byte2BooleanMap.Entry e = i.next();
         s.append(String.valueOf(e.getByteKey()));
         s.append("=>");
         s.append(String.valueOf(e.getBooleanValue()));
      }

      s.append("}");
      return s.toString();
   }

   public static class BasicEntry implements Byte2BooleanMap.Entry {
      protected byte key;
      protected boolean value;

      public BasicEntry() {
      }

      public BasicEntry(Byte key, Boolean value) {
         this.key = key;
         this.value = value;
      }

      public BasicEntry(byte key, boolean value) {
         this.key = key;
         this.value = value;
      }

      @Override
      public byte getByteKey() {
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
         } else if (o instanceof Byte2BooleanMap.Entry) {
            Byte2BooleanMap.Entry e = (Byte2BooleanMap.Entry)o;
            return this.key == e.getByteKey() && this.value == e.getBooleanValue();
         } else {
            Entry<?, ?> e = (Entry<?, ?>)o;
            Object key = e.getKey();
            if (key != null && key instanceof Byte) {
               Object value = e.getValue();
               return value != null && value instanceof Boolean ? this.key == (Byte)key && this.value == (Boolean)value : false;
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

   public abstract static class BasicEntrySet extends AbstractObjectSet<Byte2BooleanMap.Entry> {
      protected final Byte2BooleanMap map;

      public BasicEntrySet(Byte2BooleanMap map) {
         this.map = map;
      }

      @Override
      public boolean contains(Object o) {
         if (!(o instanceof Entry)) {
            return false;
         } else if (o instanceof Byte2BooleanMap.Entry) {
            Byte2BooleanMap.Entry e = (Byte2BooleanMap.Entry)o;
            byte k = e.getByteKey();
            return this.map.containsKey(k) && this.map.get(k) == e.getBooleanValue();
         } else {
            Entry<?, ?> e = (Entry<?, ?>)o;
            Object key = e.getKey();
            if (key != null && key instanceof Byte) {
               byte k = (Byte)key;
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
         } else if (o instanceof Byte2BooleanMap.Entry) {
            Byte2BooleanMap.Entry e = (Byte2BooleanMap.Entry)o;
            return this.map.remove(e.getByteKey(), e.getBooleanValue());
         } else {
            Entry<?, ?> e = (Entry<?, ?>)o;
            Object key = e.getKey();
            if (key != null && key instanceof Byte) {
               byte k = (Byte)key;
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
      public ObjectSpliterator<Byte2BooleanMap.Entry> spliterator() {
         return ObjectSpliterators.asSpliterator(this.iterator(), Size64.sizeOf(this.map), 65);
      }
   }
}
