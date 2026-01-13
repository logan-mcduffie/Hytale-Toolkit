package it.unimi.dsi.fastutil.objects;

import it.unimi.dsi.fastutil.Size64;
import it.unimi.dsi.fastutil.ints.AbstractIntCollection;
import it.unimi.dsi.fastutil.ints.IntBinaryOperator;
import it.unimi.dsi.fastutil.ints.IntCollection;
import it.unimi.dsi.fastutil.ints.IntIterator;
import it.unimi.dsi.fastutil.ints.IntSpliterator;
import it.unimi.dsi.fastutil.ints.IntSpliterators;
import java.io.Serializable;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Consumer;
import java.util.function.IntConsumer;

public abstract class AbstractReference2IntMap<K> extends AbstractReference2IntFunction<K> implements Reference2IntMap<K>, Serializable {
   private static final long serialVersionUID = -4940583368468432370L;

   protected AbstractReference2IntMap() {
   }

   @Override
   public boolean containsKey(Object k) {
      ObjectIterator<Reference2IntMap.Entry<K>> i = this.reference2IntEntrySet().iterator();

      while (i.hasNext()) {
         if (i.next().getKey() == k) {
            return true;
         }
      }

      return false;
   }

   @Override
   public boolean containsValue(int v) {
      ObjectIterator<Reference2IntMap.Entry<K>> i = this.reference2IntEntrySet().iterator();

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
   public final int mergeInt(K key, int value, IntBinaryOperator remappingFunction) {
      return this.mergeInt(key, value, remappingFunction);
   }

   @Override
   public ReferenceSet<K> keySet() {
      return new AbstractReferenceSet<K>() {
         @Override
         public boolean contains(Object k) {
            return AbstractReference2IntMap.this.containsKey(k);
         }

         @Override
         public int size() {
            return AbstractReference2IntMap.this.size();
         }

         @Override
         public void clear() {
            AbstractReference2IntMap.this.clear();
         }

         @Override
         public ObjectIterator<K> iterator() {
            return new ObjectIterator<K>() {
               private final ObjectIterator<Reference2IntMap.Entry<K>> i = Reference2IntMaps.fastIterator(AbstractReference2IntMap.this);

               @Override
               public K next() {
                  return this.i.next().getKey();
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
               public void forEachRemaining(Consumer<? super K> action) {
                  this.i.forEachRemaining(entry -> action.accept(entry.getKey()));
               }
            };
         }

         @Override
         public ObjectSpliterator<K> spliterator() {
            return ObjectSpliterators.asSpliterator(this.iterator(), Size64.sizeOf(AbstractReference2IntMap.this), 65);
         }
      };
   }

   @Override
   public IntCollection values() {
      return new AbstractIntCollection() {
         @Override
         public boolean contains(int k) {
            return AbstractReference2IntMap.this.containsValue(k);
         }

         @Override
         public int size() {
            return AbstractReference2IntMap.this.size();
         }

         @Override
         public void clear() {
            AbstractReference2IntMap.this.clear();
         }

         @Override
         public IntIterator iterator() {
            return new IntIterator() {
               private final ObjectIterator<Reference2IntMap.Entry<K>> i = Reference2IntMaps.fastIterator(AbstractReference2IntMap.this);

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
            return IntSpliterators.asSpliterator(this.iterator(), Size64.sizeOf(AbstractReference2IntMap.this), 320);
         }
      };
   }

   @Override
   public void putAll(Map<? extends K, ? extends Integer> m) {
      if (m instanceof Reference2IntMap) {
         ObjectIterator<Reference2IntMap.Entry<K>> i = Reference2IntMaps.fastIterator((Reference2IntMap<K>)m);

         while (i.hasNext()) {
            Reference2IntMap.Entry<? extends K> e = i.next();
            this.put((K)e.getKey(), e.getIntValue());
         }
      } else {
         int n = m.size();
         Iterator<? extends Entry<? extends K, ? extends Integer>> i = m.entrySet().iterator();

         while (n-- != 0) {
            Entry<? extends K, ? extends Integer> e = (Entry<? extends K, ? extends Integer>)i.next();
            this.put((K)e.getKey(), e.getValue());
         }
      }
   }

   @Override
   public int hashCode() {
      int h = 0;
      int n = this.size();
      ObjectIterator<Reference2IntMap.Entry<K>> i = Reference2IntMaps.fastIterator(this);

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
         return m.size() != this.size() ? false : this.reference2IntEntrySet().containsAll(m.entrySet());
      }
   }

   @Override
   public String toString() {
      StringBuilder s = new StringBuilder();
      ObjectIterator<Reference2IntMap.Entry<K>> i = Reference2IntMaps.fastIterator(this);
      int n = this.size();
      boolean first = true;
      s.append("{");

      while (n-- != 0) {
         if (first) {
            first = false;
         } else {
            s.append(", ");
         }

         Reference2IntMap.Entry<K> e = i.next();
         if (this == e.getKey()) {
            s.append("(this map)");
         } else {
            s.append(String.valueOf(e.getKey()));
         }

         s.append("=>");
         s.append(String.valueOf(e.getIntValue()));
      }

      s.append("}");
      return s.toString();
   }

   public static class BasicEntry<K> implements Reference2IntMap.Entry<K> {
      protected K key;
      protected int value;

      public BasicEntry() {
      }

      public BasicEntry(K key, Integer value) {
         this.key = key;
         this.value = value;
      }

      public BasicEntry(K key, int value) {
         this.key = key;
         this.value = value;
      }

      @Override
      public K getKey() {
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
         } else if (o instanceof Reference2IntMap.Entry) {
            Reference2IntMap.Entry<K> e = (Reference2IntMap.Entry<K>)o;
            return this.key == e.getKey() && this.value == e.getIntValue();
         } else {
            Entry<?, ?> e = (Entry<?, ?>)o;
            Object key = e.getKey();
            Object value = e.getValue();
            return value != null && value instanceof Integer ? this.key == key && this.value == (Integer)value : false;
         }
      }

      @Override
      public int hashCode() {
         return System.identityHashCode(this.key) ^ this.value;
      }

      @Override
      public String toString() {
         return this.key + "->" + this.value;
      }
   }

   public abstract static class BasicEntrySet<K> extends AbstractObjectSet<Reference2IntMap.Entry<K>> {
      protected final Reference2IntMap<K> map;

      public BasicEntrySet(Reference2IntMap<K> map) {
         this.map = map;
      }

      @Override
      public boolean contains(Object o) {
         if (!(o instanceof Entry)) {
            return false;
         } else if (o instanceof Reference2IntMap.Entry) {
            Reference2IntMap.Entry<K> e = (Reference2IntMap.Entry<K>)o;
            K k = e.getKey();
            return this.map.containsKey(k) && this.map.getInt(k) == e.getIntValue();
         } else {
            Entry<?, ?> e = (Entry<?, ?>)o;
            Object k = e.getKey();
            Object value = e.getValue();
            return value != null && value instanceof Integer ? this.map.containsKey(k) && this.map.getInt(k) == (Integer)value : false;
         }
      }

      @Override
      public boolean remove(Object o) {
         if (!(o instanceof Entry)) {
            return false;
         } else if (o instanceof Reference2IntMap.Entry) {
            Reference2IntMap.Entry<K> e = (Reference2IntMap.Entry<K>)o;
            return this.map.remove(e.getKey(), e.getIntValue());
         } else {
            Entry<?, ?> e = (Entry<?, ?>)o;
            Object k = e.getKey();
            Object value = e.getValue();
            if (value != null && value instanceof Integer) {
               int v = (Integer)value;
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
      public ObjectSpliterator<Reference2IntMap.Entry<K>> spliterator() {
         return ObjectSpliterators.asSpliterator(this.iterator(), Size64.sizeOf(this.map), 65);
      }
   }
}
