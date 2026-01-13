package it.unimi.dsi.fastutil.objects;

import it.unimi.dsi.fastutil.Size64;
import java.io.Serializable;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Consumer;

public abstract class AbstractReference2ReferenceMap<K, V>
   extends AbstractReference2ReferenceFunction<K, V>
   implements Reference2ReferenceMap<K, V>,
   Serializable {
   private static final long serialVersionUID = -4940583368468432370L;

   protected AbstractReference2ReferenceMap() {
   }

   @Override
   public boolean containsKey(Object k) {
      ObjectIterator<Reference2ReferenceMap.Entry<K, V>> i = this.reference2ReferenceEntrySet().iterator();

      while (i.hasNext()) {
         if (i.next().getKey() == k) {
            return true;
         }
      }

      return false;
   }

   @Override
   public boolean containsValue(Object v) {
      ObjectIterator<Reference2ReferenceMap.Entry<K, V>> i = this.reference2ReferenceEntrySet().iterator();

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
   public ReferenceSet<K> keySet() {
      return new AbstractReferenceSet<K>() {
         @Override
         public boolean contains(Object k) {
            return AbstractReference2ReferenceMap.this.containsKey(k);
         }

         @Override
         public int size() {
            return AbstractReference2ReferenceMap.this.size();
         }

         @Override
         public void clear() {
            AbstractReference2ReferenceMap.this.clear();
         }

         @Override
         public ObjectIterator<K> iterator() {
            return new ObjectIterator<K>() {
               private final ObjectIterator<Reference2ReferenceMap.Entry<K, V>> i = Reference2ReferenceMaps.fastIterator(AbstractReference2ReferenceMap.this);

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
            return ObjectSpliterators.asSpliterator(this.iterator(), Size64.sizeOf(AbstractReference2ReferenceMap.this), 65);
         }
      };
   }

   @Override
   public ReferenceCollection<V> values() {
      return new AbstractReferenceCollection<V>() {
         @Override
         public boolean contains(Object k) {
            return AbstractReference2ReferenceMap.this.containsValue(k);
         }

         @Override
         public int size() {
            return AbstractReference2ReferenceMap.this.size();
         }

         @Override
         public void clear() {
            AbstractReference2ReferenceMap.this.clear();
         }

         @Override
         public ObjectIterator<V> iterator() {
            return new ObjectIterator<V>() {
               private final ObjectIterator<Reference2ReferenceMap.Entry<K, V>> i = Reference2ReferenceMaps.fastIterator(AbstractReference2ReferenceMap.this);

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
            return ObjectSpliterators.asSpliterator(this.iterator(), Size64.sizeOf(AbstractReference2ReferenceMap.this), 64);
         }
      };
   }

   @Override
   public void putAll(Map<? extends K, ? extends V> m) {
      if (m instanceof Reference2ReferenceMap) {
         ObjectIterator<Reference2ReferenceMap.Entry<K, V>> i = Reference2ReferenceMaps.fastIterator((Reference2ReferenceMap<K, V>)m);

         while (i.hasNext()) {
            Reference2ReferenceMap.Entry<? extends K, ? extends V> e = i.next();
            this.put((K)e.getKey(), (V)e.getValue());
         }
      } else {
         int n = m.size();
         Iterator<? extends Entry<? extends K, ? extends V>> i = m.entrySet().iterator();

         while (n-- != 0) {
            Entry<? extends K, ? extends V> e = (Entry<? extends K, ? extends V>)i.next();
            this.put((K)e.getKey(), (V)e.getValue());
         }
      }
   }

   @Override
   public int hashCode() {
      int h = 0;
      int n = this.size();
      ObjectIterator<Reference2ReferenceMap.Entry<K, V>> i = Reference2ReferenceMaps.fastIterator(this);

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
         return m.size() != this.size() ? false : this.reference2ReferenceEntrySet().containsAll(m.entrySet());
      }
   }

   @Override
   public String toString() {
      StringBuilder s = new StringBuilder();
      ObjectIterator<Reference2ReferenceMap.Entry<K, V>> i = Reference2ReferenceMaps.fastIterator(this);
      int n = this.size();
      boolean first = true;
      s.append("{");

      while (n-- != 0) {
         if (first) {
            first = false;
         } else {
            s.append(", ");
         }

         Reference2ReferenceMap.Entry<K, V> e = i.next();
         if (this == e.getKey()) {
            s.append("(this map)");
         } else {
            s.append(String.valueOf(e.getKey()));
         }

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

   public static class BasicEntry<K, V> implements Reference2ReferenceMap.Entry<K, V> {
      protected K key;
      protected V value;

      public BasicEntry() {
      }

      public BasicEntry(K key, V value) {
         this.key = key;
         this.value = value;
      }

      @Override
      public K getKey() {
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
         } else if (o instanceof Reference2ReferenceMap.Entry) {
            Reference2ReferenceMap.Entry<K, V> e = (Reference2ReferenceMap.Entry<K, V>)o;
            return this.key == e.getKey() && this.value == e.getValue();
         } else {
            Entry<?, ?> e = (Entry<?, ?>)o;
            Object key = e.getKey();
            Object value = e.getValue();
            return this.key == key && this.value == value;
         }
      }

      @Override
      public int hashCode() {
         return System.identityHashCode(this.key) ^ (this.value == null ? 0 : System.identityHashCode(this.value));
      }

      @Override
      public String toString() {
         return this.key + "->" + this.value;
      }
   }

   public abstract static class BasicEntrySet<K, V> extends AbstractObjectSet<Reference2ReferenceMap.Entry<K, V>> {
      protected final Reference2ReferenceMap<K, V> map;

      public BasicEntrySet(Reference2ReferenceMap<K, V> map) {
         this.map = map;
      }

      @Override
      public boolean contains(Object o) {
         if (!(o instanceof Entry)) {
            return false;
         } else if (o instanceof Reference2ReferenceMap.Entry) {
            Reference2ReferenceMap.Entry<K, V> e = (Reference2ReferenceMap.Entry<K, V>)o;
            K k = e.getKey();
            return this.map.containsKey(k) && this.map.get(k) == e.getValue();
         } else {
            Entry<?, ?> e = (Entry<?, ?>)o;
            Object k = e.getKey();
            Object value = e.getValue();
            return this.map.containsKey(k) && this.map.get(k) == value;
         }
      }

      @Override
      public boolean remove(Object o) {
         if (!(o instanceof Entry)) {
            return false;
         } else if (o instanceof Reference2ReferenceMap.Entry) {
            Reference2ReferenceMap.Entry<K, V> e = (Reference2ReferenceMap.Entry<K, V>)o;
            return this.map.remove(e.getKey(), e.getValue());
         } else {
            Entry<?, ?> e = (Entry<?, ?>)o;
            Object k = e.getKey();
            Object v = e.getValue();
            return this.map.remove(k, v);
         }
      }

      @Override
      public int size() {
         return this.map.size();
      }

      @Override
      public ObjectSpliterator<Reference2ReferenceMap.Entry<K, V>> spliterator() {
         return ObjectSpliterators.asSpliterator(this.iterator(), Size64.sizeOf(this.map), 65);
      }
   }
}
