package it.unimi.dsi.fastutil.doubles;

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
import java.util.function.DoubleFunction;
import java.util.function.Function;

public final class Double2ReferenceMaps {
   public static final Double2ReferenceMaps.EmptyMap EMPTY_MAP = new Double2ReferenceMaps.EmptyMap();

   private Double2ReferenceMaps() {
   }

   public static <V> ObjectIterator<Double2ReferenceMap.Entry<V>> fastIterator(Double2ReferenceMap<V> map) {
      ObjectSet<Double2ReferenceMap.Entry<V>> entries = map.double2ReferenceEntrySet();
      return entries instanceof Double2ReferenceMap.FastEntrySet ? ((Double2ReferenceMap.FastEntrySet)entries).fastIterator() : entries.iterator();
   }

   public static <V> void fastForEach(Double2ReferenceMap<V> map, Consumer<? super Double2ReferenceMap.Entry<V>> consumer) {
      ObjectSet<Double2ReferenceMap.Entry<V>> entries = map.double2ReferenceEntrySet();
      if (entries instanceof Double2ReferenceMap.FastEntrySet) {
         ((Double2ReferenceMap.FastEntrySet)entries).fastForEach(consumer);
      } else {
         entries.forEach(consumer);
      }
   }

   public static <V> ObjectIterable<Double2ReferenceMap.Entry<V>> fastIterable(Double2ReferenceMap<V> map) {
      final ObjectSet<Double2ReferenceMap.Entry<V>> entries = map.double2ReferenceEntrySet();
      return (ObjectIterable<Double2ReferenceMap.Entry<V>>)(entries instanceof Double2ReferenceMap.FastEntrySet
         ? new ObjectIterable<Double2ReferenceMap.Entry<V>>() {
            @Override
            public ObjectIterator<Double2ReferenceMap.Entry<V>> iterator() {
               return ((Double2ReferenceMap.FastEntrySet)entries).fastIterator();
            }

            @Override
            public ObjectSpliterator<Double2ReferenceMap.Entry<V>> spliterator() {
               return entries.spliterator();
            }

            @Override
            public void forEach(Consumer<? super Double2ReferenceMap.Entry<V>> consumer) {
               ((Double2ReferenceMap.FastEntrySet)entries).fastForEach(consumer);
            }
         }
         : entries);
   }

   public static <V> Double2ReferenceMap<V> emptyMap() {
      return EMPTY_MAP;
   }

   public static <V> Double2ReferenceMap<V> singleton(double key, V value) {
      return new Double2ReferenceMaps.Singleton<>(key, value);
   }

   public static <V> Double2ReferenceMap<V> singleton(Double key, V value) {
      return new Double2ReferenceMaps.Singleton<>(key, value);
   }

   public static <V> Double2ReferenceMap<V> synchronize(Double2ReferenceMap<V> m) {
      return new Double2ReferenceMaps.SynchronizedMap<>(m);
   }

   public static <V> Double2ReferenceMap<V> synchronize(Double2ReferenceMap<V> m, Object sync) {
      return new Double2ReferenceMaps.SynchronizedMap<>(m, sync);
   }

   public static <V> Double2ReferenceMap<V> unmodifiable(Double2ReferenceMap<? extends V> m) {
      return new Double2ReferenceMaps.UnmodifiableMap<>(m);
   }

   public static class EmptyMap<V> extends Double2ReferenceFunctions.EmptyFunction<V> implements Double2ReferenceMap<V>, Serializable, Cloneable {
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
      public V getOrDefault(double key, V defaultValue) {
         return defaultValue;
      }

      @Override
      public void putAll(Map<? extends Double, ? extends V> m) {
         throw new UnsupportedOperationException();
      }

      @Override
      public ObjectSet<Double2ReferenceMap.Entry<V>> double2ReferenceEntrySet() {
         return ObjectSets.EMPTY_SET;
      }

      @Override
      public DoubleSet keySet() {
         return DoubleSets.EMPTY_SET;
      }

      @Override
      public ReferenceCollection<V> values() {
         return ReferenceSets.EMPTY_SET;
      }

      @Override
      public void forEach(BiConsumer<? super Double, ? super V> consumer) {
      }

      @Override
      public Object clone() {
         return Double2ReferenceMaps.EMPTY_MAP;
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

   public static class Singleton<V> extends Double2ReferenceFunctions.Singleton<V> implements Double2ReferenceMap<V>, Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected transient ObjectSet<Double2ReferenceMap.Entry<V>> entries;
      protected transient DoubleSet keys;
      protected transient ReferenceCollection<V> values;

      protected Singleton(double key, V value) {
         super(key, value);
      }

      @Override
      public boolean containsValue(Object v) {
         return this.value == v;
      }

      @Override
      public void putAll(Map<? extends Double, ? extends V> m) {
         throw new UnsupportedOperationException();
      }

      @Override
      public ObjectSet<Double2ReferenceMap.Entry<V>> double2ReferenceEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSets.singleton(new AbstractDouble2ReferenceMap.BasicEntry<>(this.key, this.value));
         }

         return this.entries;
      }

      @Deprecated
      @Override
      public ObjectSet<Entry<Double, V>> entrySet() {
         return this.double2ReferenceEntrySet();
      }

      @Override
      public DoubleSet keySet() {
         if (this.keys == null) {
            this.keys = DoubleSets.singleton(this.key);
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
         return HashCommon.double2int(this.key) ^ (this.value == null ? 0 : System.identityHashCode(this.value));
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

   public static class SynchronizedMap<V> extends Double2ReferenceFunctions.SynchronizedFunction<V> implements Double2ReferenceMap<V>, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Double2ReferenceMap<V> map;
      protected transient ObjectSet<Double2ReferenceMap.Entry<V>> entries;
      protected transient DoubleSet keys;
      protected transient ReferenceCollection<V> values;

      protected SynchronizedMap(Double2ReferenceMap<V> m, Object sync) {
         super(m, sync);
         this.map = m;
      }

      protected SynchronizedMap(Double2ReferenceMap<V> m) {
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
      public void putAll(Map<? extends Double, ? extends V> m) {
         synchronized (this.sync) {
            this.map.putAll(m);
         }
      }

      @Override
      public ObjectSet<Double2ReferenceMap.Entry<V>> double2ReferenceEntrySet() {
         synchronized (this.sync) {
            if (this.entries == null) {
               this.entries = ObjectSets.synchronize(this.map.double2ReferenceEntrySet(), this.sync);
            }

            return this.entries;
         }
      }

      @Deprecated
      @Override
      public ObjectSet<Entry<Double, V>> entrySet() {
         return this.double2ReferenceEntrySet();
      }

      @Override
      public DoubleSet keySet() {
         synchronized (this.sync) {
            if (this.keys == null) {
               this.keys = DoubleSets.synchronize(this.map.keySet(), this.sync);
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
      public V getOrDefault(double key, V defaultValue) {
         synchronized (this.sync) {
            return this.map.getOrDefault(key, defaultValue);
         }
      }

      @Override
      public void forEach(BiConsumer<? super Double, ? super V> action) {
         synchronized (this.sync) {
            this.map.forEach(action);
         }
      }

      @Override
      public void replaceAll(BiFunction<? super Double, ? super V, ? extends V> function) {
         synchronized (this.sync) {
            this.map.replaceAll(function);
         }
      }

      @Override
      public V putIfAbsent(double key, V value) {
         synchronized (this.sync) {
            return this.map.putIfAbsent(key, value);
         }
      }

      @Override
      public boolean remove(double key, Object value) {
         synchronized (this.sync) {
            return this.map.remove(key, value);
         }
      }

      @Override
      public V replace(double key, V value) {
         synchronized (this.sync) {
            return this.map.replace(key, value);
         }
      }

      @Override
      public boolean replace(double key, V oldValue, V newValue) {
         synchronized (this.sync) {
            return this.map.replace(key, oldValue, newValue);
         }
      }

      @Override
      public V computeIfAbsent(double key, DoubleFunction<? extends V> mappingFunction) {
         synchronized (this.sync) {
            return this.map.computeIfAbsent(key, mappingFunction);
         }
      }

      @Override
      public V computeIfAbsent(double key, Double2ReferenceFunction<? extends V> mappingFunction) {
         synchronized (this.sync) {
            return this.map.computeIfAbsent(key, mappingFunction);
         }
      }

      @Override
      public V computeIfPresent(double key, BiFunction<? super Double, ? super V, ? extends V> remappingFunction) {
         synchronized (this.sync) {
            return this.map.computeIfPresent(key, remappingFunction);
         }
      }

      @Override
      public V compute(double key, BiFunction<? super Double, ? super V, ? extends V> remappingFunction) {
         synchronized (this.sync) {
            return this.map.compute(key, remappingFunction);
         }
      }

      @Override
      public V merge(double key, V value, BiFunction<? super V, ? super V, ? extends V> remappingFunction) {
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
      public V replace(Double key, V value) {
         synchronized (this.sync) {
            return this.map.replace(key, value);
         }
      }

      @Deprecated
      public boolean replace(Double key, V oldValue, V newValue) {
         synchronized (this.sync) {
            return this.map.replace(key, oldValue, newValue);
         }
      }

      @Deprecated
      public V putIfAbsent(Double key, V value) {
         synchronized (this.sync) {
            return this.map.putIfAbsent(key, value);
         }
      }

      @Deprecated
      public V computeIfAbsent(Double key, Function<? super Double, ? extends V> mappingFunction) {
         synchronized (this.sync) {
            return this.map.computeIfAbsent(key, mappingFunction);
         }
      }

      @Deprecated
      public V computeIfPresent(Double key, BiFunction<? super Double, ? super V, ? extends V> remappingFunction) {
         synchronized (this.sync) {
            return this.map.computeIfPresent(key, remappingFunction);
         }
      }

      @Deprecated
      public V compute(Double key, BiFunction<? super Double, ? super V, ? extends V> remappingFunction) {
         synchronized (this.sync) {
            return this.map.compute(key, remappingFunction);
         }
      }

      @Deprecated
      public V merge(Double key, V value, BiFunction<? super V, ? super V, ? extends V> remappingFunction) {
         synchronized (this.sync) {
            return this.map.merge(key, value, remappingFunction);
         }
      }
   }

   public static class UnmodifiableMap<V> extends Double2ReferenceFunctions.UnmodifiableFunction<V> implements Double2ReferenceMap<V>, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Double2ReferenceMap<? extends V> map;
      protected transient ObjectSet<Double2ReferenceMap.Entry<V>> entries;
      protected transient DoubleSet keys;
      protected transient ReferenceCollection<V> values;

      protected UnmodifiableMap(Double2ReferenceMap<? extends V> m) {
         super(m);
         this.map = m;
      }

      @Override
      public boolean containsValue(Object v) {
         return this.map.containsValue(v);
      }

      @Override
      public void putAll(Map<? extends Double, ? extends V> m) {
         throw new UnsupportedOperationException();
      }

      @Override
      public ObjectSet<Double2ReferenceMap.Entry<V>> double2ReferenceEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSets.unmodifiable(this.map.double2ReferenceEntrySet());
         }

         return this.entries;
      }

      @Deprecated
      @Override
      public ObjectSet<Entry<Double, V>> entrySet() {
         return this.double2ReferenceEntrySet();
      }

      @Override
      public DoubleSet keySet() {
         if (this.keys == null) {
            this.keys = DoubleSets.unmodifiable(this.map.keySet());
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
      public V getOrDefault(double key, V defaultValue) {
         return (V)this.map.getOrDefault(key, defaultValue);
      }

      @Override
      public void forEach(BiConsumer<? super Double, ? super V> action) {
         this.map.forEach(action);
      }

      @Override
      public void replaceAll(BiFunction<? super Double, ? super V, ? extends V> function) {
         throw new UnsupportedOperationException();
      }

      @Override
      public V putIfAbsent(double key, V value) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean remove(double key, Object value) {
         throw new UnsupportedOperationException();
      }

      @Override
      public V replace(double key, V value) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean replace(double key, V oldValue, V newValue) {
         throw new UnsupportedOperationException();
      }

      @Override
      public V computeIfAbsent(double key, DoubleFunction<? extends V> mappingFunction) {
         throw new UnsupportedOperationException();
      }

      @Override
      public V computeIfAbsent(double key, Double2ReferenceFunction<? extends V> mappingFunction) {
         throw new UnsupportedOperationException();
      }

      @Override
      public V computeIfPresent(double key, BiFunction<? super Double, ? super V, ? extends V> remappingFunction) {
         throw new UnsupportedOperationException();
      }

      @Override
      public V compute(double key, BiFunction<? super Double, ? super V, ? extends V> remappingFunction) {
         throw new UnsupportedOperationException();
      }

      @Override
      public V merge(double key, V value, BiFunction<? super V, ? super V, ? extends V> remappingFunction) {
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
      public V replace(Double key, V value) {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      public boolean replace(Double key, V oldValue, V newValue) {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      public V putIfAbsent(Double key, V value) {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      public V computeIfAbsent(Double key, Function<? super Double, ? extends V> mappingFunction) {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      public V computeIfPresent(Double key, BiFunction<? super Double, ? super V, ? extends V> remappingFunction) {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      public V compute(Double key, BiFunction<? super Double, ? super V, ? extends V> remappingFunction) {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      public V merge(Double key, V value, BiFunction<? super V, ? super V, ? extends V> remappingFunction) {
         throw new UnsupportedOperationException();
      }
   }
}
