package it.unimi.dsi.fastutil.longs;

import it.unimi.dsi.fastutil.HashCommon;
import it.unimi.dsi.fastutil.objects.ObjectIterable;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import it.unimi.dsi.fastutil.objects.ObjectSets;
import it.unimi.dsi.fastutil.objects.ObjectSpliterator;
import it.unimi.dsi.fastutil.objects.ReferenceCollection;
import it.unimi.dsi.fastutil.objects.ReferenceCollections;
import it.unimi.dsi.fastutil.objects.ReferenceSets;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.LongFunction;

public final class Long2ReferenceMaps {
   public static final Long2ReferenceMaps.EmptyMap EMPTY_MAP = new Long2ReferenceMaps.EmptyMap();

   private Long2ReferenceMaps() {
   }

   public static <V> ObjectIterator<Long2ReferenceMap.Entry<V>> fastIterator(Long2ReferenceMap<V> map) {
      ObjectSet<Long2ReferenceMap.Entry<V>> entries = map.long2ReferenceEntrySet();
      return entries instanceof Long2ReferenceMap.FastEntrySet ? ((Long2ReferenceMap.FastEntrySet)entries).fastIterator() : entries.iterator();
   }

   public static <V> void fastForEach(Long2ReferenceMap<V> map, Consumer<? super Long2ReferenceMap.Entry<V>> consumer) {
      ObjectSet<Long2ReferenceMap.Entry<V>> entries = map.long2ReferenceEntrySet();
      if (entries instanceof Long2ReferenceMap.FastEntrySet) {
         ((Long2ReferenceMap.FastEntrySet)entries).fastForEach(consumer);
      } else {
         entries.forEach(consumer);
      }
   }

   public static <V> ObjectIterable<Long2ReferenceMap.Entry<V>> fastIterable(Long2ReferenceMap<V> map) {
      final ObjectSet<Long2ReferenceMap.Entry<V>> entries = map.long2ReferenceEntrySet();
      return (ObjectIterable<Long2ReferenceMap.Entry<V>>)(entries instanceof Long2ReferenceMap.FastEntrySet
         ? new ObjectIterable<Long2ReferenceMap.Entry<V>>() {
            @Override
            public ObjectIterator<Long2ReferenceMap.Entry<V>> iterator() {
               return ((Long2ReferenceMap.FastEntrySet)entries).fastIterator();
            }

            @Override
            public ObjectSpliterator<Long2ReferenceMap.Entry<V>> spliterator() {
               return entries.spliterator();
            }

            @Override
            public void forEach(Consumer<? super Long2ReferenceMap.Entry<V>> consumer) {
               ((Long2ReferenceMap.FastEntrySet)entries).fastForEach(consumer);
            }
         }
         : entries);
   }

   public static <V> Long2ReferenceMap<V> emptyMap() {
      return EMPTY_MAP;
   }

   public static <V> Long2ReferenceMap<V> singleton(long key, V value) {
      return new Long2ReferenceMaps.Singleton<>(key, value);
   }

   public static <V> Long2ReferenceMap<V> singleton(Long key, V value) {
      return new Long2ReferenceMaps.Singleton<>(key, value);
   }

   public static <V> Long2ReferenceMap<V> synchronize(Long2ReferenceMap<V> m) {
      return new Long2ReferenceMaps.SynchronizedMap<>(m);
   }

   public static <V> Long2ReferenceMap<V> synchronize(Long2ReferenceMap<V> m, Object sync) {
      return new Long2ReferenceMaps.SynchronizedMap<>(m, sync);
   }

   public static <V> Long2ReferenceMap<V> unmodifiable(Long2ReferenceMap<? extends V> m) {
      return new Long2ReferenceMaps.UnmodifiableMap<>(m);
   }

   public static class EmptyMap<V> extends Long2ReferenceFunctions.EmptyFunction<V> implements Long2ReferenceMap<V>, Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;

      protected EmptyMap() {
      }

      @Override
      public boolean containsValue(Object v) {
         return false;
      }

      @Deprecated
      @Override
      public V getOrDefault(Object key, V defaultValue) {
         return defaultValue;
      }

      @Override
      public V getOrDefault(long key, V defaultValue) {
         return defaultValue;
      }

      @Override
      public void putAll(Map<? extends Long, ? extends V> m) {
         throw new UnsupportedOperationException();
      }

      @Override
      public ObjectSet<Long2ReferenceMap.Entry<V>> long2ReferenceEntrySet() {
         return ObjectSets.EMPTY_SET;
      }

      @Override
      public LongSet keySet() {
         return LongSets.EMPTY_SET;
      }

      @Override
      public ReferenceCollection<V> values() {
         return ReferenceSets.EMPTY_SET;
      }

      @Override
      public void forEach(BiConsumer<? super Long, ? super V> consumer) {
      }

      @Override
      public Object clone() {
         return Long2ReferenceMaps.EMPTY_MAP;
      }

      @Override
      public boolean isEmpty() {
         return true;
      }

      @Override
      public int hashCode() {
         return 0;
      }

      @Override
      public boolean equals(Object o) {
         return !(o instanceof Map) ? false : ((Map)o).isEmpty();
      }

      @Override
      public String toString() {
         return "{}";
      }
   }

   public static class Singleton<V> extends Long2ReferenceFunctions.Singleton<V> implements Long2ReferenceMap<V>, Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected transient ObjectSet<Long2ReferenceMap.Entry<V>> entries;
      protected transient LongSet keys;
      protected transient ReferenceCollection<V> values;

      protected Singleton(long key, V value) {
         super(key, value);
      }

      @Override
      public boolean containsValue(Object v) {
         return this.value == v;
      }

      @Override
      public void putAll(Map<? extends Long, ? extends V> m) {
         throw new UnsupportedOperationException();
      }

      @Override
      public ObjectSet<Long2ReferenceMap.Entry<V>> long2ReferenceEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSets.singleton(new AbstractLong2ReferenceMap.BasicEntry<>(this.key, this.value));
         }

         return this.entries;
      }

      @Deprecated
      @Override
      public ObjectSet<Entry<Long, V>> entrySet() {
         return this.long2ReferenceEntrySet();
      }

      @Override
      public LongSet keySet() {
         if (this.keys == null) {
            this.keys = LongSets.singleton(this.key);
         }

         return this.keys;
      }

      @Override
      public ReferenceCollection<V> values() {
         if (this.values == null) {
            this.values = ReferenceSets.singleton(this.value);
         }

         return this.values;
      }

      @Override
      public boolean isEmpty() {
         return false;
      }

      @Override
      public int hashCode() {
         return HashCommon.long2int(this.key) ^ (this.value == null ? 0 : System.identityHashCode(this.value));
      }

      @Override
      public boolean equals(Object o) {
         if (o == this) {
            return true;
         } else if (!(o instanceof Map)) {
            return false;
         } else {
            Map<?, ?> m = (Map<?, ?>)o;
            return m.size() != 1 ? false : m.entrySet().iterator().next().equals(this.entrySet().iterator().next());
         }
      }

      @Override
      public String toString() {
         return "{" + this.key + "=>" + this.value + "}";
      }
   }

   public static class SynchronizedMap<V> extends Long2ReferenceFunctions.SynchronizedFunction<V> implements Long2ReferenceMap<V>, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Long2ReferenceMap<V> map;
      protected transient ObjectSet<Long2ReferenceMap.Entry<V>> entries;
      protected transient LongSet keys;
      protected transient ReferenceCollection<V> values;

      protected SynchronizedMap(Long2ReferenceMap<V> m, Object sync) {
         super(m, sync);
         this.map = m;
      }

      protected SynchronizedMap(Long2ReferenceMap<V> m) {
         super(m);
         this.map = m;
      }

      @Override
      public boolean containsValue(Object v) {
         synchronized (this.sync) {
            return this.map.containsValue(v);
         }
      }

      @Override
      public void putAll(Map<? extends Long, ? extends V> m) {
         synchronized (this.sync) {
            this.map.putAll(m);
         }
      }

      @Override
      public ObjectSet<Long2ReferenceMap.Entry<V>> long2ReferenceEntrySet() {
         synchronized (this.sync) {
            if (this.entries == null) {
               this.entries = ObjectSets.synchronize(this.map.long2ReferenceEntrySet(), this.sync);
            }

            return this.entries;
         }
      }

      @Deprecated
      @Override
      public ObjectSet<Entry<Long, V>> entrySet() {
         return this.long2ReferenceEntrySet();
      }

      @Override
      public LongSet keySet() {
         synchronized (this.sync) {
            if (this.keys == null) {
               this.keys = LongSets.synchronize(this.map.keySet(), this.sync);
            }

            return this.keys;
         }
      }

      @Override
      public ReferenceCollection<V> values() {
         synchronized (this.sync) {
            if (this.values == null) {
               this.values = ReferenceCollections.synchronize(this.map.values(), this.sync);
            }

            return this.values;
         }
      }

      @Override
      public boolean isEmpty() {
         synchronized (this.sync) {
            return this.map.isEmpty();
         }
      }

      @Override
      public int hashCode() {
         synchronized (this.sync) {
            return this.map.hashCode();
         }
      }

      @Override
      public boolean equals(Object o) {
         if (o == this) {
            return true;
         } else {
            synchronized (this.sync) {
               return this.map.equals(o);
            }
         }
      }

      private void writeObject(ObjectOutputStream s) throws IOException {
         synchronized (this.sync) {
            s.defaultWriteObject();
         }
      }

      @Override
      public V getOrDefault(long key, V defaultValue) {
         synchronized (this.sync) {
            return this.map.getOrDefault(key, defaultValue);
         }
      }

      @Override
      public void forEach(BiConsumer<? super Long, ? super V> action) {
         synchronized (this.sync) {
            this.map.forEach(action);
         }
      }

      @Override
      public void replaceAll(BiFunction<? super Long, ? super V, ? extends V> function) {
         synchronized (this.sync) {
            this.map.replaceAll(function);
         }
      }

      @Override
      public V putIfAbsent(long key, V value) {
         synchronized (this.sync) {
            return this.map.putIfAbsent(key, value);
         }
      }

      @Override
      public boolean remove(long key, Object value) {
         synchronized (this.sync) {
            return this.map.remove(key, value);
         }
      }

      @Override
      public V replace(long key, V value) {
         synchronized (this.sync) {
            return this.map.replace(key, value);
         }
      }

      @Override
      public boolean replace(long key, V oldValue, V newValue) {
         synchronized (this.sync) {
            return this.map.replace(key, oldValue, newValue);
         }
      }

      @Override
      public V computeIfAbsent(long key, LongFunction<? extends V> mappingFunction) {
         synchronized (this.sync) {
            return this.map.computeIfAbsent(key, mappingFunction);
         }
      }

      @Override
      public V computeIfAbsent(long key, Long2ReferenceFunction<? extends V> mappingFunction) {
         synchronized (this.sync) {
            return this.map.computeIfAbsent(key, mappingFunction);
         }
      }

      @Override
      public V computeIfPresent(long key, BiFunction<? super Long, ? super V, ? extends V> remappingFunction) {
         synchronized (this.sync) {
            return this.map.computeIfPresent(key, remappingFunction);
         }
      }

      @Override
      public V compute(long key, BiFunction<? super Long, ? super V, ? extends V> remappingFunction) {
         synchronized (this.sync) {
            return this.map.compute(key, remappingFunction);
         }
      }

      @Override
      public V merge(long key, V value, BiFunction<? super V, ? super V, ? extends V> remappingFunction) {
         synchronized (this.sync) {
            return this.map.merge(key, value, remappingFunction);
         }
      }

      @Deprecated
      @Override
      public V getOrDefault(Object key, V defaultValue) {
         synchronized (this.sync) {
            return this.map.getOrDefault(key, defaultValue);
         }
      }

      @Deprecated
      @Override
      public boolean remove(Object key, Object value) {
         synchronized (this.sync) {
            return this.map.remove(key, value);
         }
      }

      @Deprecated
      public V replace(Long key, V value) {
         synchronized (this.sync) {
            return this.map.replace(key, value);
         }
      }

      @Deprecated
      public boolean replace(Long key, V oldValue, V newValue) {
         synchronized (this.sync) {
            return this.map.replace(key, oldValue, newValue);
         }
      }

      @Deprecated
      public V putIfAbsent(Long key, V value) {
         synchronized (this.sync) {
            return this.map.putIfAbsent(key, value);
         }
      }

      @Deprecated
      public V computeIfAbsent(Long key, Function<? super Long, ? extends V> mappingFunction) {
         synchronized (this.sync) {
            return this.map.computeIfAbsent(key, mappingFunction);
         }
      }

      @Deprecated
      public V computeIfPresent(Long key, BiFunction<? super Long, ? super V, ? extends V> remappingFunction) {
         synchronized (this.sync) {
            return this.map.computeIfPresent(key, remappingFunction);
         }
      }

      @Deprecated
      public V compute(Long key, BiFunction<? super Long, ? super V, ? extends V> remappingFunction) {
         synchronized (this.sync) {
            return this.map.compute(key, remappingFunction);
         }
      }

      @Deprecated
      public V merge(Long key, V value, BiFunction<? super V, ? super V, ? extends V> remappingFunction) {
         synchronized (this.sync) {
            return this.map.merge(key, value, remappingFunction);
         }
      }
   }

   public static class UnmodifiableMap<V> extends Long2ReferenceFunctions.UnmodifiableFunction<V> implements Long2ReferenceMap<V>, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Long2ReferenceMap<? extends V> map;
      protected transient ObjectSet<Long2ReferenceMap.Entry<V>> entries;
      protected transient LongSet keys;
      protected transient ReferenceCollection<V> values;

      protected UnmodifiableMap(Long2ReferenceMap<? extends V> m) {
         super(m);
         this.map = m;
      }

      @Override
      public boolean containsValue(Object v) {
         return this.map.containsValue(v);
      }

      @Override
      public void putAll(Map<? extends Long, ? extends V> m) {
         throw new UnsupportedOperationException();
      }

      @Override
      public ObjectSet<Long2ReferenceMap.Entry<V>> long2ReferenceEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSets.unmodifiable(this.map.long2ReferenceEntrySet());
         }

         return this.entries;
      }

      @Deprecated
      @Override
      public ObjectSet<Entry<Long, V>> entrySet() {
         return this.long2ReferenceEntrySet();
      }

      @Override
      public LongSet keySet() {
         if (this.keys == null) {
            this.keys = LongSets.unmodifiable(this.map.keySet());
         }

         return this.keys;
      }

      @Override
      public ReferenceCollection<V> values() {
         if (this.values == null) {
            this.values = ReferenceCollections.unmodifiable(this.map.values());
         }

         return this.values;
      }

      @Override
      public boolean isEmpty() {
         return this.map.isEmpty();
      }

      @Override
      public int hashCode() {
         return this.map.hashCode();
      }

      @Override
      public boolean equals(Object o) {
         return o == this ? true : this.map.equals(o);
      }

      @Override
      public V getOrDefault(long key, V defaultValue) {
         return (V)this.map.getOrDefault(key, defaultValue);
      }

      @Override
      public void forEach(BiConsumer<? super Long, ? super V> action) {
         this.map.forEach(action);
      }

      @Override
      public void replaceAll(BiFunction<? super Long, ? super V, ? extends V> function) {
         throw new UnsupportedOperationException();
      }

      @Override
      public V putIfAbsent(long key, V value) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean remove(long key, Object value) {
         throw new UnsupportedOperationException();
      }

      @Override
      public V replace(long key, V value) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean replace(long key, V oldValue, V newValue) {
         throw new UnsupportedOperationException();
      }

      @Override
      public V computeIfAbsent(long key, LongFunction<? extends V> mappingFunction) {
         throw new UnsupportedOperationException();
      }

      @Override
      public V computeIfAbsent(long key, Long2ReferenceFunction<? extends V> mappingFunction) {
         throw new UnsupportedOperationException();
      }

      @Override
      public V computeIfPresent(long key, BiFunction<? super Long, ? super V, ? extends V> remappingFunction) {
         throw new UnsupportedOperationException();
      }

      @Override
      public V compute(long key, BiFunction<? super Long, ? super V, ? extends V> remappingFunction) {
         throw new UnsupportedOperationException();
      }

      @Override
      public V merge(long key, V value, BiFunction<? super V, ? super V, ? extends V> remappingFunction) {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      @Override
      public V getOrDefault(Object key, V defaultValue) {
         return (V)this.map.getOrDefault(key, defaultValue);
      }

      @Deprecated
      @Override
      public boolean remove(Object key, Object value) {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      public V replace(Long key, V value) {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      public boolean replace(Long key, V oldValue, V newValue) {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      public V putIfAbsent(Long key, V value) {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      public V computeIfAbsent(Long key, Function<? super Long, ? extends V> mappingFunction) {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      public V computeIfPresent(Long key, BiFunction<? super Long, ? super V, ? extends V> remappingFunction) {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      public V compute(Long key, BiFunction<? super Long, ? super V, ? extends V> remappingFunction) {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      public V merge(Long key, V value, BiFunction<? super V, ? super V, ? extends V> remappingFunction) {
         throw new UnsupportedOperationException();
      }
   }
}
