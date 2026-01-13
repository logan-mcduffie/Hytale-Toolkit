package it.unimi.dsi.fastutil.longs;

import it.unimi.dsi.fastutil.HashCommon;
import it.unimi.dsi.fastutil.Size64;
import it.unimi.dsi.fastutil.objects.AbstractObjectSet;
import it.unimi.dsi.fastutil.objects.AbstractReferenceCollection;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectSpliterator;
import it.unimi.dsi.fastutil.objects.ObjectSpliterators;
import it.unimi.dsi.fastutil.objects.ReferenceCollection;
import java.io.Serializable;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Consumer;

public abstract class AbstractLong2ReferenceMap<V> extends AbstractLong2ReferenceFunction<V> implements Long2ReferenceMap<V>, Serializable {
   private static final long serialVersionUID = -4940583368468432370L;

   protected AbstractLong2ReferenceMap() {
   }

   @Override
   public boolean containsKey(long k) {
      ObjectIterator<Long2ReferenceMap.Entry<V>> i = this.long2ReferenceEntrySet().iterator();

      while (i.hasNext()) {
         if (i.next().getLongKey() == k) {
            return true;
         }
      }

      return false;
   }

   @Override
   public boolean containsValue(Object v) {
      ObjectIterator<Long2ReferenceMap.Entry<V>> i = this.long2ReferenceEntrySet().iterator();

      while (i.hasNext()) {
         if (i.next().getValue() == v) {
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
            return AbstractLong2ReferenceMap.this.containsKey(k);
         }

         @Override
         public int size() {
            return AbstractLong2ReferenceMap.this.size();
         }

         @Override
         public void clear() {
            AbstractLong2ReferenceMap.this.clear();
         }

         @Override
         public LongIterator iterator() {
            return new LongIterator() {
               private final ObjectIterator<Long2ReferenceMap.Entry<V>> i = Long2ReferenceMaps.fastIterator(AbstractLong2ReferenceMap.this);

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
            return LongSpliterators.asSpliterator(this.iterator(), Size64.sizeOf(AbstractLong2ReferenceMap.this), 321);
         }
      };
   }

   @Override
   public ReferenceCollection<V> values() {
      return new AbstractReferenceCollection<V>() {
         @Override
         public boolean contains(Object k) {
            return AbstractLong2ReferenceMap.this.containsValue(k);
         }

         @Override
         public int size() {
            return AbstractLong2ReferenceMap.this.size();
         }

         @Override
         public void clear() {
            AbstractLong2ReferenceMap.this.clear();
         }

         @Override
         public ObjectIterator<V> iterator() {
            return new ObjectIterator<V>() {
               private final ObjectIterator<Long2ReferenceMap.Entry<V>> i = Long2ReferenceMaps.fastIterator(AbstractLong2ReferenceMap.this);

               @Override
               public V next() {
                  return this.i.next().getValue();
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
               public void forEachRemaining(Consumer<? super V> action) {
                  this.i.forEachRemaining(entry -> action.accept(entry.getValue()));
               }
            };
         }

         @Override
         public ObjectSpliterator<V> spliterator() {
            return ObjectSpliterators.asSpliterator(this.iterator(), Size64.sizeOf(AbstractLong2ReferenceMap.this), 64);
         }
      };
   }

   @Override
   public void putAll(Map<? extends Long, ? extends V> m) {
      if (m instanceof Long2ReferenceMap) {
         ObjectIterator<Long2ReferenceMap.Entry<V>> i = Long2ReferenceMaps.fastIterator((Long2ReferenceMap<V>)m);

         while (i.hasNext()) {
            Long2ReferenceMap.Entry<? extends V> e = i.next();
            this.put(e.getLongKey(), (V)e.getValue());
         }
      } else {
         int n = m.size();
         Iterator<? extends Entry<? extends Long, ? extends V>> i = m.entrySet().iterator();

         while (n-- != 0) {
            Entry<? extends Long, ? extends V> e = (Entry<? extends Long, ? extends V>)i.next();
            this.put(e.getKey(), (V)e.getValue());
         }
      }
   }

   @Override
   public int hashCode() {
      int h = 0;
      int n = this.size();
      ObjectIterator<Long2ReferenceMap.Entry<V>> i = Long2ReferenceMaps.fastIterator(this);

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
         return m.size() != this.size() ? false : this.long2ReferenceEntrySet().containsAll(m.entrySet());
      }
   }

   @Override
   public String toString() {
      StringBuilder s = new StringBuilder();
      ObjectIterator<Long2ReferenceMap.Entry<V>> i = Long2ReferenceMaps.fastIterator(this);
      int n = this.size();
      boolean first = true;
      s.append("{");

      while (n-- != 0) {
         if (first) {
            first = false;
         } else {
            s.append(", ");
         }

         Long2ReferenceMap.Entry<V> e = i.next();
         s.append(String.valueOf(e.getLongKey()));
         s.append("=>");
         if (this == e.getValue()) {
            s.append("(this map)");
         } else {
            s.append(String.valueOf(e.getValue()));
         }
      }

      s.append("}");
      return s.toString();
   }

   public static class BasicEntry<V> implements Long2ReferenceMap.Entry<V> {
      protected long key;
      protected V value;

      public BasicEntry() {
      }

      public BasicEntry(Long key, V value) {
         this.key = key;
         this.value = value;
      }

      public BasicEntry(long key, V value) {
         this.key = key;
         this.value = value;
      }

      @Override
      public long getLongKey() {
         return this.key;
      }

      @Override
      public V getValue() {
         return this.value;
      }

      @Override
      public V setValue(V value) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean equals(Object o) {
         if (!(o instanceof Entry)) {
            return false;
         } else if (o instanceof Long2ReferenceMap.Entry) {
            Long2ReferenceMap.Entry<V> e = (Long2ReferenceMap.Entry<V>)o;
            return this.key == e.getLongKey() && this.value == e.getValue();
         } else {
            Entry<?, ?> e = (Entry<?, ?>)o;
            Object key = e.getKey();
            if (key != null && key instanceof Long) {
               Object value = e.getValue();
               return this.key == (Long)key && this.value == value;
            } else {
               return false;
            }
         }
      }

      @Override
      public int hashCode() {
         return HashCommon.long2int(this.key) ^ (this.value == null ? 0 : System.identityHashCode(this.value));
      }

      @Override
      public String toString() {
         return this.key + "->" + this.value;
      }
   }

   public abstract static class BasicEntrySet<V> extends AbstractObjectSet<Long2ReferenceMap.Entry<V>> {
      protected final Long2ReferenceMap<V> map;

      public BasicEntrySet(Long2ReferenceMap<V> map) {
         this.map = map;
      }

      @Override
      public boolean contains(Object o) {
         if (!(o instanceof Entry)) {
            return false;
         } else if (o instanceof Long2ReferenceMap.Entry) {
            Long2ReferenceMap.Entry<V> e = (Long2ReferenceMap.Entry<V>)o;
            long k = e.getLongKey();
            return this.map.containsKey(k) && this.map.get(k) == e.getValue();
         } else {
            Entry<?, ?> e = (Entry<?, ?>)o;
            Object key = e.getKey();
            if (key != null && key instanceof Long) {
               long k = (Long)key;
               Object value = e.getValue();
               return this.map.containsKey(k) && this.map.get(k) == value;
            } else {
               return false;
            }
         }
      }

      @Override
      public boolean remove(Object o) {
         if (!(o instanceof Entry)) {
            return false;
         } else if (o instanceof Long2ReferenceMap.Entry) {
            Long2ReferenceMap.Entry<V> e = (Long2ReferenceMap.Entry<V>)o;
            return this.map.remove(e.getLongKey(), e.getValue());
         } else {
            Entry<?, ?> e = (Entry<?, ?>)o;
            Object key = e.getKey();
            if (key != null && key instanceof Long) {
               long k = (Long)key;
               Object v = e.getValue();
               return this.map.remove(k, v);
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
      public ObjectSpliterator<Long2ReferenceMap.Entry<V>> spliterator() {
         return ObjectSpliterators.asSpliterator(this.iterator(), Size64.sizeOf(this.map), 65);
      }
   }
}
