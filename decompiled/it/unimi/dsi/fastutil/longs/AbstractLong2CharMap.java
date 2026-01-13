package it.unimi.dsi.fastutil.longs;

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

public abstract class AbstractLong2CharMap extends AbstractLong2CharFunction implements Long2CharMap, Serializable {
   private static final long serialVersionUID = -4940583368468432370L;

   protected AbstractLong2CharMap() {
   }

   @Override
   public boolean containsKey(long k) {
      ObjectIterator<Long2CharMap.Entry> i = this.long2CharEntrySet().iterator();

      while (i.hasNext()) {
         if (i.next().getLongKey() == k) {
            return true;
         }
      }

      return false;
   }

   @Override
   public boolean containsValue(char v) {
      ObjectIterator<Long2CharMap.Entry> i = this.long2CharEntrySet().iterator();

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
   public LongSet keySet() {
      return new AbstractLongSet() {
         @Override
         public boolean contains(long k) {
            return AbstractLong2CharMap.this.containsKey(k);
         }

         @Override
         public int size() {
            return AbstractLong2CharMap.this.size();
         }

         @Override
         public void clear() {
            AbstractLong2CharMap.this.clear();
         }

         @Override
         public LongIterator iterator() {
            return new LongIterator() {
               private final ObjectIterator<Long2CharMap.Entry> i = Long2CharMaps.fastIterator(AbstractLong2CharMap.this);

               @Override
               public long nextLong() {
                  return this.i.next().getLongKey();
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
               public void forEachRemaining(java.util.function.LongConsumer action) {
                  this.i.forEachRemaining(entry -> action.accept(entry.getLongKey()));
               }
            };
         }

         @Override
         public LongSpliterator spliterator() {
            return LongSpliterators.asSpliterator(this.iterator(), Size64.sizeOf(AbstractLong2CharMap.this), 321);
         }
      };
   }

   @Override
   public CharCollection values() {
      return new AbstractCharCollection() {
         @Override
         public boolean contains(char k) {
            return AbstractLong2CharMap.this.containsValue(k);
         }

         @Override
         public int size() {
            return AbstractLong2CharMap.this.size();
         }

         @Override
         public void clear() {
            AbstractLong2CharMap.this.clear();
         }

         @Override
         public CharIterator iterator() {
            return new CharIterator() {
               private final ObjectIterator<Long2CharMap.Entry> i = Long2CharMaps.fastIterator(AbstractLong2CharMap.this);

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
            return CharSpliterators.asSpliterator(this.iterator(), Size64.sizeOf(AbstractLong2CharMap.this), 320);
         }
      };
   }

   @Override
   public void putAll(Map<? extends Long, ? extends Character> m) {
      if (m instanceof Long2CharMap) {
         ObjectIterator<Long2CharMap.Entry> i = Long2CharMaps.fastIterator((Long2CharMap)m);

         while (i.hasNext()) {
            Long2CharMap.Entry e = i.next();
            this.put(e.getLongKey(), e.getCharValue());
         }
      } else {
         int n = m.size();
         Iterator<? extends Entry<? extends Long, ? extends Character>> i = m.entrySet().iterator();

         while (n-- != 0) {
            Entry<? extends Long, ? extends Character> e = (Entry<? extends Long, ? extends Character>)i.next();
            this.put(e.getKey(), e.getValue());
         }
      }
   }

   @Override
   public int hashCode() {
      int h = 0;
      int n = this.size();
      ObjectIterator<Long2CharMap.Entry> i = Long2CharMaps.fastIterator(this);

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
         return m.size() != this.size() ? false : this.long2CharEntrySet().containsAll(m.entrySet());
      }
   }

   @Override
   public String toString() {
      StringBuilder s = new StringBuilder();
      ObjectIterator<Long2CharMap.Entry> i = Long2CharMaps.fastIterator(this);
      int n = this.size();
      boolean first = true;
      s.append("{");

      while (n-- != 0) {
         if (first) {
            first = false;
         } else {
            s.append(", ");
         }

         Long2CharMap.Entry e = i.next();
         s.append(String.valueOf(e.getLongKey()));
         s.append("=>");
         s.append(String.valueOf(e.getCharValue()));
      }

      s.append("}");
      return s.toString();
   }

   public static class BasicEntry implements Long2CharMap.Entry {
      protected long key;
      protected char value;

      public BasicEntry() {
      }

      public BasicEntry(Long key, Character value) {
         this.key = key;
         this.value = value;
      }

      public BasicEntry(long key, char value) {
         this.key = key;
         this.value = value;
      }

      @Override
      public long getLongKey() {
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
         } else if (o instanceof Long2CharMap.Entry) {
            Long2CharMap.Entry e = (Long2CharMap.Entry)o;
            return this.key == e.getLongKey() && this.value == e.getCharValue();
         } else {
            Entry<?, ?> e = (Entry<?, ?>)o;
            Object key = e.getKey();
            if (key != null && key instanceof Long) {
               Object value = e.getValue();
               return value != null && value instanceof Character ? this.key == (Long)key && this.value == (Character)value : false;
            } else {
               return false;
            }
         }
      }

      @Override
      public int hashCode() {
         return HashCommon.long2int(this.key) ^ this.value;
      }

      @Override
      public String toString() {
         return this.key + "->" + this.value;
      }
   }

   public abstract static class BasicEntrySet extends AbstractObjectSet<Long2CharMap.Entry> {
      protected final Long2CharMap map;

      public BasicEntrySet(Long2CharMap map) {
         this.map = map;
      }

      @Override
      public boolean contains(Object o) {
         if (!(o instanceof Entry)) {
            return false;
         } else if (o instanceof Long2CharMap.Entry) {
            Long2CharMap.Entry e = (Long2CharMap.Entry)o;
            long k = e.getLongKey();
            return this.map.containsKey(k) && this.map.get(k) == e.getCharValue();
         } else {
            Entry<?, ?> e = (Entry<?, ?>)o;
            Object key = e.getKey();
            if (key != null && key instanceof Long) {
               long k = (Long)key;
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
         } else if (o instanceof Long2CharMap.Entry) {
            Long2CharMap.Entry e = (Long2CharMap.Entry)o;
            return this.map.remove(e.getLongKey(), e.getCharValue());
         } else {
            Entry<?, ?> e = (Entry<?, ?>)o;
            Object key = e.getKey();
            if (key != null && key instanceof Long) {
               long k = (Long)key;
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
      public ObjectSpliterator<Long2CharMap.Entry> spliterator() {
         return ObjectSpliterators.asSpliterator(this.iterator(), Size64.sizeOf(this.map), 65);
      }
   }
}
