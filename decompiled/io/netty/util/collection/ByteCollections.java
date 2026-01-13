package io.netty.util.collection;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.Map.Entry;

public final class ByteCollections {
   private static final ByteObjectMap<Object> EMPTY_MAP = new ByteCollections.EmptyMap();

   private ByteCollections() {
   }

   public static <V> ByteObjectMap<V> emptyMap() {
      return (ByteObjectMap<V>)EMPTY_MAP;
   }

   public static <V> ByteObjectMap<V> unmodifiableMap(ByteObjectMap<V> map) {
      return new ByteCollections.UnmodifiableMap<>(map);
   }

   private static final class EmptyMap implements ByteObjectMap<Object> {
      private EmptyMap() {
      }

      @Override
      public Object get(byte key) {
         return null;
      }

      @Override
      public Object put(byte key, Object value) {
         throw new UnsupportedOperationException("put");
      }

      @Override
      public Object remove(byte key) {
         return null;
      }

      @Override
      public int size() {
         return 0;
      }

      @Override
      public boolean isEmpty() {
         return true;
      }

      @Override
      public boolean containsKey(Object key) {
         return false;
      }

      @Override
      public void clear() {
      }

      @Override
      public Set<Byte> keySet() {
         return Collections.emptySet();
      }

      @Override
      public boolean containsKey(byte key) {
         return false;
      }

      @Override
      public boolean containsValue(Object value) {
         return false;
      }

      @Override
      public Iterable<ByteObjectMap.PrimitiveEntry<Object>> entries() {
         return Collections.emptySet();
      }

      @Override
      public Object get(Object key) {
         return null;
      }

      public Object put(Byte key, Object value) {
         throw new UnsupportedOperationException();
      }

      @Override
      public Object remove(Object key) {
         return null;
      }

      @Override
      public void putAll(Map<? extends Byte, ?> m) {
         throw new UnsupportedOperationException();
      }

      @Override
      public Collection<Object> values() {
         return Collections.emptyList();
      }

      @Override
      public Set<Entry<Byte, Object>> entrySet() {
         return Collections.emptySet();
      }
   }

   private static final class UnmodifiableMap<V> implements ByteObjectMap<V> {
      private final ByteObjectMap<V> map;
      private Set<Byte> keySet;
      private Set<Entry<Byte, V>> entrySet;
      private Collection<V> values;
      private Iterable<ByteObjectMap.PrimitiveEntry<V>> entries;

      UnmodifiableMap(ByteObjectMap<V> map) {
         this.map = map;
      }

      @Override
      public V get(byte key) {
         return this.map.get(key);
      }

      @Override
      public V put(byte key, V value) {
         throw new UnsupportedOperationException("put");
      }

      @Override
      public V remove(byte key) {
         throw new UnsupportedOperationException("remove");
      }

      @Override
      public int size() {
         return this.map.size();
      }

      @Override
      public boolean isEmpty() {
         return this.map.isEmpty();
      }

      @Override
      public void clear() {
         throw new UnsupportedOperationException("clear");
      }

      @Override
      public boolean containsKey(byte key) {
         return this.map.containsKey(key);
      }

      @Override
      public boolean containsValue(Object value) {
         return this.map.containsValue(value);
      }

      @Override
      public boolean containsKey(Object key) {
         return this.map.containsKey(key);
      }

      @Override
      public V get(Object key) {
         return this.map.get(key);
      }

      public V put(Byte key, V value) {
         throw new UnsupportedOperationException("put");
      }

      @Override
      public V remove(Object key) {
         throw new UnsupportedOperationException("remove");
      }

      @Override
      public void putAll(Map<? extends Byte, ? extends V> m) {
         throw new UnsupportedOperationException("putAll");
      }

      @Override
      public Iterable<ByteObjectMap.PrimitiveEntry<V>> entries() {
         if (this.entries == null) {
            this.entries = new Iterable<ByteObjectMap.PrimitiveEntry<V>>() {
               @Override
               public Iterator<ByteObjectMap.PrimitiveEntry<V>> iterator() {
                  return UnmodifiableMap.this.new IteratorImpl(UnmodifiableMap.this.map.entries().iterator());
               }
            };
         }

         return this.entries;
      }

      @Override
      public Set<Byte> keySet() {
         if (this.keySet == null) {
            this.keySet = Collections.unmodifiableSet(this.map.keySet());
         }

         return this.keySet;
      }

      @Override
      public Set<Entry<Byte, V>> entrySet() {
         if (this.entrySet == null) {
            this.entrySet = Collections.unmodifiableSet(this.map.entrySet());
         }

         return this.entrySet;
      }

      @Override
      public Collection<V> values() {
         if (this.values == null) {
            this.values = Collections.unmodifiableCollection(this.map.values());
         }

         return this.values;
      }

      private class EntryImpl implements ByteObjectMap.PrimitiveEntry<V> {
         private final ByteObjectMap.PrimitiveEntry<V> entry;

         EntryImpl(ByteObjectMap.PrimitiveEntry<V> entry) {
            this.entry = entry;
         }

         @Override
         public byte key() {
            return this.entry.key();
         }

         @Override
         public V value() {
            return this.entry.value();
         }

         @Override
         public void setValue(V value) {
            throw new UnsupportedOperationException("setValue");
         }
      }

      private class IteratorImpl implements Iterator<ByteObjectMap.PrimitiveEntry<V>> {
         final Iterator<ByteObjectMap.PrimitiveEntry<V>> iter;

         IteratorImpl(Iterator<ByteObjectMap.PrimitiveEntry<V>> iter) {
            this.iter = iter;
         }

         @Override
         public boolean hasNext() {
            return this.iter.hasNext();
         }

         public ByteObjectMap.PrimitiveEntry<V> next() {
            if (!this.hasNext()) {
               throw new NoSuchElementException();
            } else {
               return UnmodifiableMap.this.new EntryImpl(this.iter.next());
            }
         }

         @Override
         public void remove() {
            throw new UnsupportedOperationException("remove");
         }
      }
   }
}
