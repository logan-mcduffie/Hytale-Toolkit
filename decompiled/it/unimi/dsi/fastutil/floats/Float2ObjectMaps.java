package it.unimi.dsi.fastutil.floats;

import it.unimi.dsi.fastutil.HashCommon;
import it.unimi.dsi.fastutil.objects.ObjectCollection;
import it.unimi.dsi.fastutil.objects.ObjectCollections;
import it.unimi.dsi.fastutil.objects.ObjectIterable;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import it.unimi.dsi.fastutil.objects.ObjectSets;
import it.unimi.dsi.fastutil.objects.ObjectSpliterator;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Map;
import java.util.Objects;
import java.util.Map.Entry;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.DoubleFunction;
import java.util.function.Function;

public final class Float2ObjectMaps {
   public static final Float2ObjectMaps.EmptyMap EMPTY_MAP = new Float2ObjectMaps.EmptyMap();

   private Float2ObjectMaps() {
   }

   public static <V> ObjectIterator<Float2ObjectMap.Entry<V>> fastIterator(Float2ObjectMap<V> map) {
      ObjectSet<Float2ObjectMap.Entry<V>> entries = map.float2ObjectEntrySet();
      return entries instanceof Float2ObjectMap.FastEntrySet ? ((Float2ObjectMap.FastEntrySet)entries).fastIterator() : entries.iterator();
   }

   public static <V> void fastForEach(Float2ObjectMap<V> map, Consumer<? super Float2ObjectMap.Entry<V>> consumer) {
      ObjectSet<Float2ObjectMap.Entry<V>> entries = map.float2ObjectEntrySet();
      if (entries instanceof Float2ObjectMap.FastEntrySet) {
         ((Float2ObjectMap.FastEntrySet)entries).fastForEach(consumer);
      } else {
         entries.forEach(consumer);
      }
   }

   public static <V> ObjectIterable<Float2ObjectMap.Entry<V>> fastIterable(Float2ObjectMap<V> map) {
      final ObjectSet<Float2ObjectMap.Entry<V>> entries = map.float2ObjectEntrySet();
      return (ObjectIterable<Float2ObjectMap.Entry<V>>)(entries instanceof Float2ObjectMap.FastEntrySet ? new ObjectIterable<Float2ObjectMap.Entry<V>>() {
         @Override
         public ObjectIterator<Float2ObjectMap.Entry<V>> iterator() {
            return ((Float2ObjectMap.FastEntrySet)entries).fastIterator();
         }

         @Override
         public ObjectSpliterator<Float2ObjectMap.Entry<V>> spliterator() {
            return entries.spliterator();
         }

         @Override
         public void forEach(Consumer<? super Float2ObjectMap.Entry<V>> consumer) {
            ((Float2ObjectMap.FastEntrySet)entries).fastForEach(consumer);
         }
      } : entries);
   }

   public static <V> Float2ObjectMap<V> emptyMap() {
      return EMPTY_MAP;
   }

   public static <V> Float2ObjectMap<V> singleton(float key, V value) {
      return new Float2ObjectMaps.Singleton<>(key, value);
   }

   public static <V> Float2ObjectMap<V> singleton(Float key, V value) {
      return new Float2ObjectMaps.Singleton<>(key, value);
   }

   public static <V> Float2ObjectMap<V> synchronize(Float2ObjectMap<V> m) {
      return new Float2ObjectMaps.SynchronizedMap<>(m);
   }

   public static <V> Float2ObjectMap<V> synchronize(Float2ObjectMap<V> m, Object sync) {
      return new Float2ObjectMaps.SynchronizedMap<>(m, sync);
   }

   public static <V> Float2ObjectMap<V> unmodifiable(Float2ObjectMap<? extends V> m) {
      return new Float2ObjectMaps.UnmodifiableMap<>(m);
   }

   public static class EmptyMap<V> extends Float2ObjectFunctions.EmptyFunction<V> implements Float2ObjectMap<V>, Serializable, Cloneable {
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
      public V getOrDefault(float key, V defaultValue) {
         return defaultValue;
      }

      @Override
      public void putAll(Map<? extends Float, ? extends V> m) {
         throw new UnsupportedOperationException();
      }

      @Override
      public ObjectSet<Float2ObjectMap.Entry<V>> float2ObjectEntrySet() {
         return ObjectSets.EMPTY_SET;
      }

      @Override
      public FloatSet keySet() {
         return FloatSets.EMPTY_SET;
      }

      @Override
      public ObjectCollection<V> values() {
         return ObjectSets.EMPTY_SET;
      }

      @Override
      public void forEach(BiConsumer<? super Float, ? super V> consumer) {
      }

      @Override
      public Object clone() {
         return Float2ObjectMaps.EMPTY_MAP;
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

   public static class Singleton<V> extends Float2ObjectFunctions.Singleton<V> implements Float2ObjectMap<V>, Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected transient ObjectSet<Float2ObjectMap.Entry<V>> entries;
      protected transient FloatSet keys;
      protected transient ObjectCollection<V> values;

      protected Singleton(float key, V value) {
         super(key, value);
      }

      @Override
      public boolean containsValue(Object v) {
         return Objects.equals(this.value, v);
      }

      @Override
      public void putAll(Map<? extends Float, ? extends V> m) {
         throw new UnsupportedOperationException();
      }

      @Override
      public ObjectSet<Float2ObjectMap.Entry<V>> float2ObjectEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSets.singleton(new AbstractFloat2ObjectMap.BasicEntry<>(this.key, this.value));
         }

         return this.entries;
      }

      @Deprecated
      @Override
      public ObjectSet<Entry<Float, V>> entrySet() {
         return this.float2ObjectEntrySet();
      }

      @Override
      public FloatSet keySet() {
         if (this.keys == null) {
            this.keys = FloatSets.singleton(this.key);
         }

         return this.keys;
      }

      @Override
      public ObjectCollection<V> values() {
         if (this.values == null) {
            this.values = ObjectSets.singleton(this.value);
         }

         return this.values;
      }

      @Override
      public boolean isEmpty() {
         return false;
      }

      @Override
      public int hashCode() {
         return HashCommon.float2int(this.key) ^ (this.value == null ? 0 : this.value.hashCode());
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

   public static class SynchronizedMap<V> extends Float2ObjectFunctions.SynchronizedFunction<V> implements Float2ObjectMap<V>, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Float2ObjectMap<V> map;
      protected transient ObjectSet<Float2ObjectMap.Entry<V>> entries;
      protected transient FloatSet keys;
      protected transient ObjectCollection<V> values;

      protected SynchronizedMap(Float2ObjectMap<V> m, Object sync) {
         super(m, sync);
         this.map = m;
      }

      protected SynchronizedMap(Float2ObjectMap<V> m) {
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
      public void putAll(Map<? extends Float, ? extends V> m) {
         synchronized (this.sync) {
            this.map.putAll(m);
         }
      }

      @Override
      public ObjectSet<Float2ObjectMap.Entry<V>> float2ObjectEntrySet() {
         synchronized (this.sync) {
            if (this.entries == null) {
               this.entries = ObjectSets.synchronize(this.map.float2ObjectEntrySet(), this.sync);
            }

            return this.entries;
         }
      }

      @Deprecated
      @Override
      public ObjectSet<Entry<Float, V>> entrySet() {
         return this.float2ObjectEntrySet();
      }

      @Override
      public FloatSet keySet() {
         synchronized (this.sync) {
            if (this.keys == null) {
               this.keys = FloatSets.synchronize(this.map.keySet(), this.sync);
            }

            return this.keys;
         }
      }

      @Override
      public ObjectCollection<V> values() {
         synchronized (this.sync) {
            if (this.values == null) {
               this.values = ObjectCollections.synchronize(this.map.values(), this.sync);
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
      public V getOrDefault(float key, V defaultValue) {
         synchronized (this.sync) {
            return this.map.getOrDefault(key, defaultValue);
         }
      }

      @Override
      public void forEach(BiConsumer<? super Float, ? super V> action) {
         synchronized (this.sync) {
            this.map.forEach(action);
         }
      }

      @Override
      public void replaceAll(BiFunction<? super Float, ? super V, ? extends V> function) {
         synchronized (this.sync) {
            this.map.replaceAll(function);
         }
      }

      @Override
      public V putIfAbsent(float key, V value) {
         synchronized (this.sync) {
            return this.map.putIfAbsent(key, value);
         }
      }

      @Override
      public boolean remove(float key, Object value) {
         synchronized (this.sync) {
            return this.map.remove(key, value);
         }
      }

      @Override
      public V replace(float key, V value) {
         synchronized (this.sync) {
            return this.map.replace(key, value);
         }
      }

      @Override
      public boolean replace(float key, V oldValue, V newValue) {
         synchronized (this.sync) {
            return this.map.replace(key, oldValue, newValue);
         }
      }

      @Override
      public V computeIfAbsent(float key, DoubleFunction<? extends V> mappingFunction) {
         synchronized (this.sync) {
            return this.map.computeIfAbsent(key, mappingFunction);
         }
      }

      @Override
      public V computeIfAbsent(float key, Float2ObjectFunction<? extends V> mappingFunction) {
         synchronized (this.sync) {
            return this.map.computeIfAbsent(key, mappingFunction);
         }
      }

      @Override
      public V computeIfPresent(float key, BiFunction<? super Float, ? super V, ? extends V> remappingFunction) {
         synchronized (this.sync) {
            return this.map.computeIfPresent(key, remappingFunction);
         }
      }

      @Override
      public V compute(float key, BiFunction<? super Float, ? super V, ? extends V> remappingFunction) {
         synchronized (this.sync) {
            return this.map.compute(key, remappingFunction);
         }
      }

      @Override
      public V merge(float key, V value, BiFunction<? super V, ? super V, ? extends V> remappingFunction) {
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
      public V replace(Float key, V value) {
         synchronized (this.sync) {
            return this.map.replace(key, value);
         }
      }

      @Deprecated
      public boolean replace(Float key, V oldValue, V newValue) {
         synchronized (this.sync) {
            return this.map.replace(key, oldValue, newValue);
         }
      }

      @Deprecated
      public V putIfAbsent(Float key, V value) {
         synchronized (this.sync) {
            return this.map.putIfAbsent(key, value);
         }
      }

      @Deprecated
      public V computeIfAbsent(Float key, Function<? super Float, ? extends V> mappingFunction) {
         synchronized (this.sync) {
            return this.map.computeIfAbsent(key, mappingFunction);
         }
      }

      @Deprecated
      public V computeIfPresent(Float key, BiFunction<? super Float, ? super V, ? extends V> remappingFunction) {
         synchronized (this.sync) {
            return this.map.computeIfPresent(key, remappingFunction);
         }
      }

      @Deprecated
      public V compute(Float key, BiFunction<? super Float, ? super V, ? extends V> remappingFunction) {
         synchronized (this.sync) {
            return this.map.compute(key, remappingFunction);
         }
      }

      @Deprecated
      public V merge(Float key, V value, BiFunction<? super V, ? super V, ? extends V> remappingFunction) {
         synchronized (this.sync) {
            return this.map.merge(key, value, remappingFunction);
         }
      }
   }

   public static class UnmodifiableMap<V> extends Float2ObjectFunctions.UnmodifiableFunction<V> implements Float2ObjectMap<V>, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Float2ObjectMap<? extends V> map;
      protected transient ObjectSet<Float2ObjectMap.Entry<V>> entries;
      protected transient FloatSet keys;
      protected transient ObjectCollection<V> values;

      protected UnmodifiableMap(Float2ObjectMap<? extends V> m) {
         super(m);
         this.map = m;
      }

      @Override
      public boolean containsValue(Object v) {
         return this.map.containsValue(v);
      }

      @Override
      public void putAll(Map<? extends Float, ? extends V> m) {
         throw new UnsupportedOperationException();
      }

      @Override
      public ObjectSet<Float2ObjectMap.Entry<V>> float2ObjectEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSets.unmodifiable(this.map.float2ObjectEntrySet());
         }

         return this.entries;
      }

      @Deprecated
      @Override
      public ObjectSet<Entry<Float, V>> entrySet() {
         return this.float2ObjectEntrySet();
      }

      @Override
      public FloatSet keySet() {
         if (this.keys == null) {
            this.keys = FloatSets.unmodifiable(this.map.keySet());
         }

         return this.keys;
      }

      @Override
      public ObjectCollection<V> values() {
         if (this.values == null) {
            this.values = ObjectCollections.unmodifiable(this.map.values());
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
      public V getOrDefault(float key, V defaultValue) {
         return (V)this.map.getOrDefault(key, defaultValue);
      }

      @Override
      public void forEach(BiConsumer<? super Float, ? super V> action) {
         this.map.forEach(action);
      }

      @Override
      public void replaceAll(BiFunction<? super Float, ? super V, ? extends V> function) {
         throw new UnsupportedOperationException();
      }

      @Override
      public V putIfAbsent(float key, V value) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean remove(float key, Object value) {
         throw new UnsupportedOperationException();
      }

      @Override
      public V replace(float key, V value) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean replace(float key, V oldValue, V newValue) {
         throw new UnsupportedOperationException();
      }

      @Override
      public V computeIfAbsent(float key, DoubleFunction<? extends V> mappingFunction) {
         throw new UnsupportedOperationException();
      }

      @Override
      public V computeIfAbsent(float key, Float2ObjectFunction<? extends V> mappingFunction) {
         throw new UnsupportedOperationException();
      }

      @Override
      public V computeIfPresent(float key, BiFunction<? super Float, ? super V, ? extends V> remappingFunction) {
         throw new UnsupportedOperationException();
      }

      @Override
      public V compute(float key, BiFunction<? super Float, ? super V, ? extends V> remappingFunction) {
         throw new UnsupportedOperationException();
      }

      @Override
      public V merge(float key, V value, BiFunction<? super V, ? super V, ? extends V> remappingFunction) {
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
      public V replace(Float key, V value) {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      public boolean replace(Float key, V oldValue, V newValue) {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      public V putIfAbsent(Float key, V value) {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      public V computeIfAbsent(Float key, Function<? super Float, ? extends V> mappingFunction) {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      public V computeIfPresent(Float key, BiFunction<? super Float, ? super V, ? extends V> remappingFunction) {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      public V compute(Float key, BiFunction<? super Float, ? super V, ? extends V> remappingFunction) {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      public V merge(Float key, V value, BiFunction<? super V, ? super V, ? extends V> remappingFunction) {
         throw new UnsupportedOperationException();
      }
   }
}
