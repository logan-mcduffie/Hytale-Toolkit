package it.unimi.dsi.fastutil.objects;

import it.unimi.dsi.fastutil.HashCommon;
import it.unimi.dsi.fastutil.longs.LongCollection;
import it.unimi.dsi.fastutil.longs.LongCollections;
import it.unimi.dsi.fastutil.longs.LongSets;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.ToLongFunction;

public final class Object2LongMaps {
   public static final Object2LongMaps.EmptyMap EMPTY_MAP = new Object2LongMaps.EmptyMap();

   private Object2LongMaps() {
   }

   public static <K> ObjectIterator<Object2LongMap.Entry<K>> fastIterator(Object2LongMap<K> map) {
      ObjectSet<Object2LongMap.Entry<K>> entries = map.object2LongEntrySet();
      return entries instanceof Object2LongMap.FastEntrySet ? ((Object2LongMap.FastEntrySet)entries).fastIterator() : entries.iterator();
   }

   public static <K> void fastForEach(Object2LongMap<K> map, Consumer<? super Object2LongMap.Entry<K>> consumer) {
      ObjectSet<Object2LongMap.Entry<K>> entries = map.object2LongEntrySet();
      if (entries instanceof Object2LongMap.FastEntrySet) {
         ((Object2LongMap.FastEntrySet)entries).fastForEach(consumer);
      } else {
         entries.forEach(consumer);
      }
   }

   public static <K> ObjectIterable<Object2LongMap.Entry<K>> fastIterable(Object2LongMap<K> map) {
      final ObjectSet<Object2LongMap.Entry<K>> entries = map.object2LongEntrySet();
      return (ObjectIterable<Object2LongMap.Entry<K>>)(entries instanceof Object2LongMap.FastEntrySet ? new ObjectIterable<Object2LongMap.Entry<K>>() {
         @Override
         public ObjectIterator<Object2LongMap.Entry<K>> iterator() {
            return ((Object2LongMap.FastEntrySet)entries).fastIterator();
         }

         @Override
         public ObjectSpliterator<Object2LongMap.Entry<K>> spliterator() {
            return entries.spliterator();
         }

         @Override
         public void forEach(Consumer<? super Object2LongMap.Entry<K>> consumer) {
            ((Object2LongMap.FastEntrySet)entries).fastForEach(consumer);
         }
      } : entries);
   }

   public static <K> Object2LongMap<K> emptyMap() {
      return EMPTY_MAP;
   }

   public static <K> Object2LongMap<K> singleton(K key, long value) {
      return new Object2LongMaps.Singleton<>(key, value);
   }

   public static <K> Object2LongMap<K> singleton(K key, Long value) {
      return new Object2LongMaps.Singleton<>(key, value);
   }

   public static <K> Object2LongMap<K> synchronize(Object2LongMap<K> m) {
      return new Object2LongMaps.SynchronizedMap<>(m);
   }

   public static <K> Object2LongMap<K> synchronize(Object2LongMap<K> m, Object sync) {
      return new Object2LongMaps.SynchronizedMap<>(m, sync);
   }

   public static <K> Object2LongMap<K> unmodifiable(Object2LongMap<? extends K> m) {
      return new Object2LongMaps.UnmodifiableMap<>(m);
   }

   public static class EmptyMap<K> extends Object2LongFunctions.EmptyFunction<K> implements Object2LongMap<K>, Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;

      protected EmptyMap() {
      }

      @Override
      public boolean containsValue(long v) {
         return false;
      }

      @Deprecated
      @Override
      public Long getOrDefault(Object key, Long defaultValue) {
         return defaultValue;
      }

      @Override
      public long getOrDefault(Object key, long defaultValue) {
         return defaultValue;
      }

      @Deprecated
      @Override
      public boolean containsValue(Object ov) {
         return false;
      }

      @Override
      public void putAll(Map<? extends K, ? extends Long> m) {
         throw new UnsupportedOperationException();
      }

      @Override
      public ObjectSet<Object2LongMap.Entry<K>> object2LongEntrySet() {
         return ObjectSets.EMPTY_SET;
      }

      @Override
      public ObjectSet<K> keySet() {
         return ObjectSets.EMPTY_SET;
      }

      @Override
      public LongCollection values() {
         return LongSets.EMPTY_SET;
      }

      @Override
      public void forEach(BiConsumer<? super K, ? super Long> consumer) {
      }

      @Override
      public Object clone() {
         return Object2LongMaps.EMPTY_MAP;
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

   public static class Singleton<K> extends Object2LongFunctions.Singleton<K> implements Object2LongMap<K>, Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected transient ObjectSet<Object2LongMap.Entry<K>> entries;
      protected transient ObjectSet<K> keys;
      protected transient LongCollection values;

      protected Singleton(K key, long value) {
         super(key, value);
      }

      @Override
      public boolean containsValue(long v) {
         return this.value == v;
      }

      @Deprecated
      @Override
      public boolean containsValue(Object ov) {
         return (Long)ov == this.value;
      }

      @Override
      public void putAll(Map<? extends K, ? extends Long> m) {
         throw new UnsupportedOperationException();
      }

      @Override
      public ObjectSet<Object2LongMap.Entry<K>> object2LongEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSets.singleton(new AbstractObject2LongMap.BasicEntry<>(this.key, this.value));
         }

         return this.entries;
      }

      @Deprecated
      @Override
      public ObjectSet<Entry<K, Long>> entrySet() {
         return this.object2LongEntrySet();
      }

      @Override
      public ObjectSet<K> keySet() {
         if (this.keys == null) {
            this.keys = ObjectSets.singleton(this.key);
         }

         return this.keys;
      }

      @Override
      public LongCollection values() {
         if (this.values == null) {
            this.values = LongSets.singleton(this.value);
         }

         return this.values;
      }

      @Override
      public boolean isEmpty() {
         return false;
      }

      @Override
      public int hashCode() {
         return (this.key == null ? 0 : this.key.hashCode()) ^ HashCommon.long2int(this.value);
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

   public static class SynchronizedMap<K> extends Object2LongFunctions.SynchronizedFunction<K> implements Object2LongMap<K>, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Object2LongMap<K> map;
      protected transient ObjectSet<Object2LongMap.Entry<K>> entries;
      protected transient ObjectSet<K> keys;
      protected transient LongCollection values;

      protected SynchronizedMap(Object2LongMap<K> m, Object sync) {
         super(m, sync);
         this.map = m;
      }

      protected SynchronizedMap(Object2LongMap<K> m) {
         super(m);
         this.map = m;
      }

      @Override
      public boolean containsValue(long v) {
         synchronized (this.sync) {
            return this.map.containsValue(v);
         }
      }

      @Deprecated
      @Override
      public boolean containsValue(Object ov) {
         synchronized (this.sync) {
            return this.map.containsValue(ov);
         }
      }

      @Override
      public void putAll(Map<? extends K, ? extends Long> m) {
         synchronized (this.sync) {
            this.map.putAll(m);
         }
      }

      @Override
      public ObjectSet<Object2LongMap.Entry<K>> object2LongEntrySet() {
         synchronized (this.sync) {
            if (this.entries == null) {
               this.entries = ObjectSets.synchronize(this.map.object2LongEntrySet(), this.sync);
            }

            return this.entries;
         }
      }

      @Deprecated
      @Override
      public ObjectSet<Entry<K, Long>> entrySet() {
         return this.object2LongEntrySet();
      }

      @Override
      public ObjectSet<K> keySet() {
         synchronized (this.sync) {
            if (this.keys == null) {
               this.keys = ObjectSets.synchronize(this.map.keySet(), this.sync);
            }

            return this.keys;
         }
      }

      @Override
      public LongCollection values() {
         synchronized (this.sync) {
            if (this.values == null) {
               this.values = LongCollections.synchronize(this.map.values(), this.sync);
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
      public long getOrDefault(Object key, long defaultValue) {
         synchronized (this.sync) {
            return this.map.getOrDefault(key, defaultValue);
         }
      }

      @Override
      public void forEach(BiConsumer<? super K, ? super Long> action) {
         synchronized (this.sync) {
            this.map.forEach(action);
         }
      }

      @Override
      public void replaceAll(BiFunction<? super K, ? super Long, ? extends Long> function) {
         synchronized (this.sync) {
            this.map.replaceAll(function);
         }
      }

      @Override
      public long putIfAbsent(K key, long value) {
         synchronized (this.sync) {
            return this.map.putIfAbsent(key, value);
         }
      }

      @Override
      public boolean remove(Object key, long value) {
         synchronized (this.sync) {
            return this.map.remove(key, value);
         }
      }

      @Override
      public long replace(K key, long value) {
         synchronized (this.sync) {
            return this.map.replace(key, value);
         }
      }

      @Override
      public boolean replace(K key, long oldValue, long newValue) {
         synchronized (this.sync) {
            return this.map.replace(key, oldValue, newValue);
         }
      }

      @Override
      public long computeIfAbsent(K key, ToLongFunction<? super K> mappingFunction) {
         synchronized (this.sync) {
            return this.map.computeIfAbsent(key, mappingFunction);
         }
      }

      @Override
      public long computeIfAbsent(K key, Object2LongFunction<? super K> mappingFunction) {
         synchronized (this.sync) {
            return this.map.computeIfAbsent(key, mappingFunction);
         }
      }

      @Override
      public long computeLongIfPresent(K key, BiFunction<? super K, ? super Long, ? extends Long> remappingFunction) {
         synchronized (this.sync) {
            return this.map.computeLongIfPresent(key, remappingFunction);
         }
      }

      @Override
      public long computeLong(K key, BiFunction<? super K, ? super Long, ? extends Long> remappingFunction) {
         synchronized (this.sync) {
            return this.map.computeLong(key, remappingFunction);
         }
      }

      @Override
      public long merge(K key, long value, BiFunction<? super Long, ? super Long, ? extends Long> remappingFunction) {
         synchronized (this.sync) {
            return this.map.merge(key, value, remappingFunction);
         }
      }

      @Deprecated
      @Override
      public Long getOrDefault(Object key, Long defaultValue) {
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
      @Override
      public Long replace(K key, Long value) {
         synchronized (this.sync) {
            return this.map.replace(key, value);
         }
      }

      @Deprecated
      @Override
      public boolean replace(K key, Long oldValue, Long newValue) {
         synchronized (this.sync) {
            return this.map.replace(key, oldValue, newValue);
         }
      }

      @Deprecated
      @Override
      public Long putIfAbsent(K key, Long value) {
         synchronized (this.sync) {
            return this.map.putIfAbsent(key, value);
         }
      }

      public Long computeIfAbsent(K key, Function<? super K, ? extends Long> mappingFunction) {
         synchronized (this.sync) {
            return this.map.computeIfAbsent(key, mappingFunction);
         }
      }

      public Long computeIfPresent(K key, BiFunction<? super K, ? super Long, ? extends Long> remappingFunction) {
         synchronized (this.sync) {
            return this.map.computeIfPresent(key, remappingFunction);
         }
      }

      public Long compute(K key, BiFunction<? super K, ? super Long, ? extends Long> remappingFunction) {
         synchronized (this.sync) {
            return this.map.compute(key, remappingFunction);
         }
      }

      @Deprecated
      @Override
      public Long merge(K key, Long value, BiFunction<? super Long, ? super Long, ? extends Long> remappingFunction) {
         synchronized (this.sync) {
            return this.map.merge(key, value, remappingFunction);
         }
      }
   }

   public static class UnmodifiableMap<K> extends Object2LongFunctions.UnmodifiableFunction<K> implements Object2LongMap<K>, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Object2LongMap<? extends K> map;
      protected transient ObjectSet<Object2LongMap.Entry<K>> entries;
      protected transient ObjectSet<K> keys;
      protected transient LongCollection values;

      protected UnmodifiableMap(Object2LongMap<? extends K> m) {
         super(m);
         this.map = m;
      }

      @Override
      public boolean containsValue(long v) {
         return this.map.containsValue(v);
      }

      @Deprecated
      @Override
      public boolean containsValue(Object ov) {
         return this.map.containsValue(ov);
      }

      @Override
      public void putAll(Map<? extends K, ? extends Long> m) {
         throw new UnsupportedOperationException();
      }

      @Override
      public ObjectSet<Object2LongMap.Entry<K>> object2LongEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSets.unmodifiable(this.map.object2LongEntrySet());
         }

         return this.entries;
      }

      @Deprecated
      @Override
      public ObjectSet<Entry<K, Long>> entrySet() {
         return this.object2LongEntrySet();
      }

      @Override
      public ObjectSet<K> keySet() {
         if (this.keys == null) {
            this.keys = ObjectSets.unmodifiable(this.map.keySet());
         }

         return this.keys;
      }

      @Override
      public LongCollection values() {
         if (this.values == null) {
            this.values = LongCollections.unmodifiable(this.map.values());
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
      public long getOrDefault(Object key, long defaultValue) {
         return this.map.getOrDefault(key, defaultValue);
      }

      @Override
      public void forEach(BiConsumer<? super K, ? super Long> action) {
         this.map.forEach(action);
      }

      @Override
      public void replaceAll(BiFunction<? super K, ? super Long, ? extends Long> function) {
         throw new UnsupportedOperationException();
      }

      @Override
      public long putIfAbsent(K key, long value) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean remove(Object key, long value) {
         throw new UnsupportedOperationException();
      }

      @Override
      public long replace(K key, long value) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean replace(K key, long oldValue, long newValue) {
         throw new UnsupportedOperationException();
      }

      @Override
      public long computeIfAbsent(K key, ToLongFunction<? super K> mappingFunction) {
         throw new UnsupportedOperationException();
      }

      @Override
      public long computeIfAbsent(K key, Object2LongFunction<? super K> mappingFunction) {
         throw new UnsupportedOperationException();
      }

      @Override
      public long computeLongIfPresent(K key, BiFunction<? super K, ? super Long, ? extends Long> remappingFunction) {
         throw new UnsupportedOperationException();
      }

      @Override
      public long computeLong(K key, BiFunction<? super K, ? super Long, ? extends Long> remappingFunction) {
         throw new UnsupportedOperationException();
      }

      @Override
      public long merge(K key, long value, BiFunction<? super Long, ? super Long, ? extends Long> remappingFunction) {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      @Override
      public Long getOrDefault(Object key, Long defaultValue) {
         return this.map.getOrDefault(key, defaultValue);
      }

      @Deprecated
      @Override
      public boolean remove(Object key, Object value) {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      @Override
      public Long replace(K key, Long value) {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      @Override
      public boolean replace(K key, Long oldValue, Long newValue) {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      @Override
      public Long putIfAbsent(K key, Long value) {
         throw new UnsupportedOperationException();
      }

      public Long computeIfAbsent(K key, Function<? super K, ? extends Long> mappingFunction) {
         throw new UnsupportedOperationException();
      }

      public Long computeIfPresent(K key, BiFunction<? super K, ? super Long, ? extends Long> remappingFunction) {
         throw new UnsupportedOperationException();
      }

      public Long compute(K key, BiFunction<? super K, ? super Long, ? extends Long> remappingFunction) {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      @Override
      public Long merge(K key, Long value, BiFunction<? super Long, ? super Long, ? extends Long> remappingFunction) {
         throw new UnsupportedOperationException();
      }
   }
}
