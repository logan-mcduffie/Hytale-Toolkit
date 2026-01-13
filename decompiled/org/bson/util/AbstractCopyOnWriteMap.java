package org.bson.util;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import org.bson.assertions.Assertions;

abstract class AbstractCopyOnWriteMap<K, V, M extends Map<K, V>> implements ConcurrentMap<K, V> {
   private volatile M delegate;
   private final transient Lock lock = new ReentrantLock();
   private final AbstractCopyOnWriteMap.View<K, V> view;

   protected <N extends Map<? extends K, ? extends V>> AbstractCopyOnWriteMap(N map, AbstractCopyOnWriteMap.View.Type viewType) {
      this.delegate = Assertions.notNull("delegate", this.copy(Assertions.notNull("map", map)));
      this.view = Assertions.notNull("viewType", viewType).get(this);
   }

   abstract <N extends Map<? extends K, ? extends V>> M copy(N var1);

   @Override
   public final void clear() {
      this.lock.lock();

      try {
         this.set(this.copy(Collections.emptyMap()));
      } finally {
         this.lock.unlock();
      }
   }

   @Override
   public final V remove(Object key) {
      this.lock.lock();

      M map;
      try {
         if (this.delegate.containsKey(key)) {
            map = this.copy();

            try {
               return map.remove(key);
            } finally {
               this.set(map);
            }
         }

         map = null;
      } finally {
         this.lock.unlock();
      }

      return (V)map;
   }

   @Override
   public boolean remove(Object key, Object value) {
      this.lock.lock();

      boolean var4;
      try {
         if (!this.delegate.containsKey(key) || !this.equals(value, this.delegate.get(key))) {
            return false;
         }

         M map = this.copy();
         map.remove(key);
         this.set(map);
         var4 = true;
      } finally {
         this.lock.unlock();
      }

      return var4;
   }

   @Override
   public boolean replace(K key, V oldValue, V newValue) {
      this.lock.lock();

      boolean map;
      try {
         if (this.delegate.containsKey(key) && this.equals(oldValue, this.delegate.get(key))) {
            M mapx = this.copy();
            mapx.put(key, newValue);
            this.set(mapx);
            return true;
         }

         map = false;
      } finally {
         this.lock.unlock();
      }

      return map;
   }

   @Override
   public V replace(K key, V value) {
      this.lock.lock();

      M map;
      try {
         if (this.delegate.containsKey(key)) {
            map = this.copy();

            try {
               return map.put(key, value);
            } finally {
               this.set(map);
            }
         }

         map = null;
      } finally {
         this.lock.unlock();
      }

      return (V)map;
   }

   @Override
   public final V put(K key, V value) {
      this.lock.lock();

      Object var4;
      try {
         M map = this.copy();

         try {
            var4 = map.put(key, value);
         } finally {
            this.set(map);
         }
      } finally {
         this.lock.unlock();
      }

      return (V)var4;
   }

   @Override
   public V putIfAbsent(K key, V value) {
      this.lock.lock();

      Object var4;
      try {
         if (this.delegate.containsKey(key)) {
            return this.delegate.get(key);
         }

         M map = this.copy();

         try {
            var4 = map.put(key, value);
         } finally {
            this.set(map);
         }
      } finally {
         this.lock.unlock();
      }

      return (V)var4;
   }

   @Override
   public final void putAll(Map<? extends K, ? extends V> t) {
      this.lock.lock();

      try {
         M map = this.copy();
         map.putAll(t);
         this.set(map);
      } finally {
         this.lock.unlock();
      }
   }

   protected M copy() {
      this.lock.lock();

      Map var1;
      try {
         var1 = this.copy(this.delegate);
      } finally {
         this.lock.unlock();
      }

      return (M)var1;
   }

   protected void set(M map) {
      this.delegate = map;
   }

   @Override
   public final Set<Entry<K, V>> entrySet() {
      return this.view.entrySet();
   }

   @Override
   public final Set<K> keySet() {
      return this.view.keySet();
   }

   @Override
   public final Collection<V> values() {
      return this.view.values();
   }

   @Override
   public final boolean containsKey(Object key) {
      return this.delegate.containsKey(key);
   }

   @Override
   public final boolean containsValue(Object value) {
      return this.delegate.containsValue(value);
   }

   @Override
   public final V get(Object key) {
      return this.delegate.get(key);
   }

   @Override
   public final boolean isEmpty() {
      return this.delegate.isEmpty();
   }

   @Override
   public final int size() {
      return this.delegate.size();
   }

   @Override
   public final boolean equals(Object o) {
      return this.delegate.equals(o);
   }

   @Override
   public final int hashCode() {
      return this.delegate.hashCode();
   }

   protected final M getDelegate() {
      return this.delegate;
   }

   @Override
   public String toString() {
      return this.delegate.toString();
   }

   private boolean equals(Object o1, Object o2) {
      return o1 == null ? o2 == null : o1.equals(o2);
   }

   protected abstract static class CollectionView<E> implements Collection<E> {
      abstract Collection<E> getDelegate();

      @Override
      public final boolean contains(Object o) {
         return this.getDelegate().contains(o);
      }

      @Override
      public final boolean containsAll(Collection<?> c) {
         return this.getDelegate().containsAll(c);
      }

      @Override
      public final Iterator<E> iterator() {
         return new AbstractCopyOnWriteMap.UnmodifiableIterator<>(this.getDelegate().iterator());
      }

      @Override
      public final boolean isEmpty() {
         return this.getDelegate().isEmpty();
      }

      @Override
      public final int size() {
         return this.getDelegate().size();
      }

      @Override
      public final Object[] toArray() {
         return this.getDelegate().toArray();
      }

      @Override
      public final <T> T[] toArray(T[] a) {
         return (T[])this.getDelegate().toArray(a);
      }

      @Override
      public int hashCode() {
         return this.getDelegate().hashCode();
      }

      @Override
      public boolean equals(Object obj) {
         return this.getDelegate().equals(obj);
      }

      @Override
      public String toString() {
         return this.getDelegate().toString();
      }

      @Override
      public final boolean add(E o) {
         throw new UnsupportedOperationException();
      }

      @Override
      public final boolean addAll(Collection<? extends E> c) {
         throw new UnsupportedOperationException();
      }
   }

   private class EntrySet extends AbstractCopyOnWriteMap.CollectionView<Entry<K, V>> implements Set<Entry<K, V>> {
      private EntrySet() {
      }

      @Override
      Collection<Entry<K, V>> getDelegate() {
         return AbstractCopyOnWriteMap.this.delegate.entrySet();
      }

      @Override
      public void clear() {
         AbstractCopyOnWriteMap.this.lock.lock();

         try {
            M map = AbstractCopyOnWriteMap.this.copy();
            map.entrySet().clear();
            AbstractCopyOnWriteMap.this.set(map);
         } finally {
            AbstractCopyOnWriteMap.this.lock.unlock();
         }
      }

      @Override
      public boolean remove(Object o) {
         AbstractCopyOnWriteMap.this.lock.lock();

         boolean map;
         try {
            if (this.contains(o)) {
               M mapx = AbstractCopyOnWriteMap.this.copy();

               try {
                  return mapx.entrySet().remove(o);
               } finally {
                  AbstractCopyOnWriteMap.this.set(mapx);
               }
            }

            map = false;
         } finally {
            AbstractCopyOnWriteMap.this.lock.unlock();
         }

         return map;
      }

      @Override
      public boolean removeAll(Collection<?> c) {
         AbstractCopyOnWriteMap.this.lock.lock();

         boolean var3;
         try {
            M map = AbstractCopyOnWriteMap.this.copy();

            try {
               var3 = map.entrySet().removeAll(c);
            } finally {
               AbstractCopyOnWriteMap.this.set(map);
            }
         } finally {
            AbstractCopyOnWriteMap.this.lock.unlock();
         }

         return var3;
      }

      @Override
      public boolean retainAll(Collection<?> c) {
         AbstractCopyOnWriteMap.this.lock.lock();

         boolean var3;
         try {
            M map = AbstractCopyOnWriteMap.this.copy();

            try {
               var3 = map.entrySet().retainAll(c);
            } finally {
               AbstractCopyOnWriteMap.this.set(map);
            }
         } finally {
            AbstractCopyOnWriteMap.this.lock.unlock();
         }

         return var3;
      }
   }

   final class Immutable extends AbstractCopyOnWriteMap.View<K, V> {
      @Override
      public Set<K> keySet() {
         return Collections.unmodifiableSet(AbstractCopyOnWriteMap.this.delegate.keySet());
      }

      @Override
      public Set<Entry<K, V>> entrySet() {
         return Collections.unmodifiableSet(AbstractCopyOnWriteMap.this.delegate.entrySet());
      }

      @Override
      public Collection<V> values() {
         return Collections.unmodifiableCollection(AbstractCopyOnWriteMap.this.delegate.values());
      }
   }

   private class KeySet extends AbstractCopyOnWriteMap.CollectionView<K> implements Set<K> {
      private KeySet() {
      }

      @Override
      Collection<K> getDelegate() {
         return AbstractCopyOnWriteMap.this.delegate.keySet();
      }

      @Override
      public void clear() {
         AbstractCopyOnWriteMap.this.lock.lock();

         try {
            M map = AbstractCopyOnWriteMap.this.copy();
            map.keySet().clear();
            AbstractCopyOnWriteMap.this.set(map);
         } finally {
            AbstractCopyOnWriteMap.this.lock.unlock();
         }
      }

      @Override
      public boolean remove(Object o) {
         return AbstractCopyOnWriteMap.this.remove(o) != null;
      }

      @Override
      public boolean removeAll(Collection<?> c) {
         AbstractCopyOnWriteMap.this.lock.lock();

         boolean var3;
         try {
            M map = AbstractCopyOnWriteMap.this.copy();

            try {
               var3 = map.keySet().removeAll(c);
            } finally {
               AbstractCopyOnWriteMap.this.set(map);
            }
         } finally {
            AbstractCopyOnWriteMap.this.lock.unlock();
         }

         return var3;
      }

      @Override
      public boolean retainAll(Collection<?> c) {
         AbstractCopyOnWriteMap.this.lock.lock();

         boolean var3;
         try {
            M map = AbstractCopyOnWriteMap.this.copy();

            try {
               var3 = map.keySet().retainAll(c);
            } finally {
               AbstractCopyOnWriteMap.this.set(map);
            }
         } finally {
            AbstractCopyOnWriteMap.this.lock.unlock();
         }

         return var3;
      }
   }

   final class Mutable extends AbstractCopyOnWriteMap.View<K, V> {
      private final transient AbstractCopyOnWriteMap<K, V, M>.KeySet keySet = AbstractCopyOnWriteMap.this.new KeySet();
      private final transient AbstractCopyOnWriteMap<K, V, M>.EntrySet entrySet = AbstractCopyOnWriteMap.this.new EntrySet();
      private final transient AbstractCopyOnWriteMap<K, V, M>.Values values = AbstractCopyOnWriteMap.this.new Values();

      @Override
      public Set<K> keySet() {
         return this.keySet;
      }

      @Override
      public Set<Entry<K, V>> entrySet() {
         return this.entrySet;
      }

      @Override
      public Collection<V> values() {
         return this.values;
      }
   }

   private static class UnmodifiableIterator<T> implements Iterator<T> {
      private final Iterator<T> delegate;

      UnmodifiableIterator(Iterator<T> delegate) {
         this.delegate = delegate;
      }

      @Override
      public boolean hasNext() {
         return this.delegate.hasNext();
      }

      @Override
      public T next() {
         return this.delegate.next();
      }

      @Override
      public void remove() {
         throw new UnsupportedOperationException();
      }
   }

   private final class Values extends AbstractCopyOnWriteMap.CollectionView<V> {
      private Values() {
      }

      @Override
      Collection<V> getDelegate() {
         return AbstractCopyOnWriteMap.this.delegate.values();
      }

      @Override
      public void clear() {
         AbstractCopyOnWriteMap.this.lock.lock();

         try {
            M map = AbstractCopyOnWriteMap.this.copy();
            map.values().clear();
            AbstractCopyOnWriteMap.this.set(map);
         } finally {
            AbstractCopyOnWriteMap.this.lock.unlock();
         }
      }

      @Override
      public boolean remove(Object o) {
         AbstractCopyOnWriteMap.this.lock.lock();

         boolean map;
         try {
            if (this.contains(o)) {
               M mapx = AbstractCopyOnWriteMap.this.copy();

               try {
                  return mapx.values().remove(o);
               } finally {
                  AbstractCopyOnWriteMap.this.set(mapx);
               }
            }

            map = false;
         } finally {
            AbstractCopyOnWriteMap.this.lock.unlock();
         }

         return map;
      }

      @Override
      public boolean removeAll(Collection<?> c) {
         AbstractCopyOnWriteMap.this.lock.lock();

         boolean var3;
         try {
            M map = AbstractCopyOnWriteMap.this.copy();

            try {
               var3 = map.values().removeAll(c);
            } finally {
               AbstractCopyOnWriteMap.this.set(map);
            }
         } finally {
            AbstractCopyOnWriteMap.this.lock.unlock();
         }

         return var3;
      }

      @Override
      public boolean retainAll(Collection<?> c) {
         AbstractCopyOnWriteMap.this.lock.lock();

         boolean var3;
         try {
            M map = AbstractCopyOnWriteMap.this.copy();

            try {
               var3 = map.values().retainAll(c);
            } finally {
               AbstractCopyOnWriteMap.this.set(map);
            }
         } finally {
            AbstractCopyOnWriteMap.this.lock.unlock();
         }

         return var3;
      }
   }

   public abstract static class View<K, V> {
      View() {
      }

      abstract Set<K> keySet();

      abstract Set<Entry<K, V>> entrySet();

      abstract Collection<V> values();

      public static enum Type {
         STABLE {
            @Override
            <K, V, M extends Map<K, V>> AbstractCopyOnWriteMap.View<K, V> get(AbstractCopyOnWriteMap<K, V, M> host) {
               return host.new Immutable();
            }
         },
         LIVE {
            @Override
            <K, V, M extends Map<K, V>> AbstractCopyOnWriteMap.View<K, V> get(AbstractCopyOnWriteMap<K, V, M> host) {
               return host.new Mutable();
            }
         };

         private Type() {
         }

         abstract <K, V, M extends Map<K, V>> AbstractCopyOnWriteMap.View<K, V> get(AbstractCopyOnWriteMap<K, V, M> var1);
      }
   }
}
