package it.unimi.dsi.fastutil.shorts;

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
import java.util.function.IntFunction;

public final class Short2ReferenceMaps {
   public static final Short2ReferenceMaps.EmptyMap EMPTY_MAP = new Short2ReferenceMaps.EmptyMap();

   private Short2ReferenceMaps() {
   }

   public static <V> ObjectIterator<Short2ReferenceMap.Entry<V>> fastIterator(Short2ReferenceMap<V> map) {
      ObjectSet<Short2ReferenceMap.Entry<V>> entries = map.short2ReferenceEntrySet();
      return entries instanceof Short2ReferenceMap.FastEntrySet ? ((Short2ReferenceMap.FastEntrySet)entries).fastIterator() : entries.iterator();
   }

   public static <V> void fastForEach(Short2ReferenceMap<V> map, Consumer<? super Short2ReferenceMap.Entry<V>> consumer) {
      ObjectSet<Short2ReferenceMap.Entry<V>> entries = map.short2ReferenceEntrySet();
      if (entries instanceof Short2ReferenceMap.FastEntrySet) {
         ((Short2ReferenceMap.FastEntrySet)entries).fastForEach(consumer);
      } else {
         entries.forEach(consumer);
      }
   }

   public static <V> ObjectIterable<Short2ReferenceMap.Entry<V>> fastIterable(Short2ReferenceMap<V> map) {
      final ObjectSet<Short2ReferenceMap.Entry<V>> entries = map.short2ReferenceEntrySet();
      return (ObjectIterable<Short2ReferenceMap.Entry<V>>)(entries instanceof Short2ReferenceMap.FastEntrySet
         ? new ObjectIterable<Short2ReferenceMap.Entry<V>>() {
            @Override
            public ObjectIterator<Short2ReferenceMap.Entry<V>> iterator() {
               return ((Short2ReferenceMap.FastEntrySet)entries).fastIterator();
            }

            @Override
            public ObjectSpliterator<Short2ReferenceMap.Entry<V>> spliterator() {
               return entries.spliterator();
            }

            @Override
            public void forEach(Consumer<? super Short2ReferenceMap.Entry<V>> consumer) {
               ((Short2ReferenceMap.FastEntrySet)entries).fastForEach(consumer);
            }
         }
         : entries);
   }

   public static <V> Short2ReferenceMap<V> emptyMap() {
      return EMPTY_MAP;
   }

   public static <V> Short2ReferenceMap<V> singleton(short key, V value) {
      return new Short2ReferenceMaps.Singleton<>(key, value);
   }

   public static <V> Short2ReferenceMap<V> singleton(Short key, V value) {
      return new Short2ReferenceMaps.Singleton<>(key, value);
   }

   public static <V> Short2ReferenceMap<V> synchronize(Short2ReferenceMap<V> m) {
      return new Short2ReferenceMaps.SynchronizedMap<>(m);
   }

   public static <V> Short2ReferenceMap<V> synchronize(Short2ReferenceMap<V> m, Object sync) {
      return new Short2ReferenceMaps.SynchronizedMap<>(m, sync);
   }

   public static <V> Short2ReferenceMap<V> unmodifiable(Short2ReferenceMap<? extends V> m) {
      return new Short2ReferenceMaps.UnmodifiableMap<>(m);
   }

   public static class EmptyMap<V> extends Short2ReferenceFunctions.EmptyFunction<V> implements Short2ReferenceMap<V>, Serializable, Cloneable {
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
      public V getOrDefault(short key, V defaultValue) {
         return defaultValue;
      }

      @Override
      public void putAll(Map<? extends Short, ? extends V> m) {
         throw new UnsupportedOperationException();
      }

      @Override
      public ObjectSet<Short2ReferenceMap.Entry<V>> short2ReferenceEntrySet() {
         return ObjectSets.EMPTY_SET;
      }

      @Override
      public ShortSet keySet() {
         return ShortSets.EMPTY_SET;
      }

      @Override
      public ReferenceCollection<V> values() {
         return ReferenceSets.EMPTY_SET;
      }

      @Override
      public void forEach(BiConsumer<? super Short, ? super V> consumer) {
      }

      @Override
      public Object clone() {
         return Short2ReferenceMaps.EMPTY_MAP;
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

   public static class Singleton<V> extends Short2ReferenceFunctions.Singleton<V> implements Short2ReferenceMap<V>, Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected transient ObjectSet<Short2ReferenceMap.Entry<V>> entries;
      protected transient ShortSet keys;
      protected transient ReferenceCollection<V> values;

      protected Singleton(short key, V value) {
         super(key, value);
      }

      @Override
      public boolean containsValue(Object v) {
         return this.value == v;
      }

      @Override
      public void putAll(Map<? extends Short, ? extends V> m) {
         throw new UnsupportedOperationException();
      }

      @Override
      public ObjectSet<Short2ReferenceMap.Entry<V>> short2ReferenceEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSets.singleton(new AbstractShort2ReferenceMap.BasicEntry<>(this.key, this.value));
         }

         return this.entries;
      }

      @Deprecated
      @Override
      public ObjectSet<Entry<Short, V>> entrySet() {
         return this.short2ReferenceEntrySet();
      }

      @Override
      public ShortSet keySet() {
         if (this.keys == null) {
            this.keys = ShortSets.singleton(this.key);
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
         return this.key ^ (this.value == null ? 0 : System.identityHashCode(this.value));
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

   public static class SynchronizedMap<V> extends Short2ReferenceFunctions.SynchronizedFunction<V> implements Short2ReferenceMap<V>, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Short2ReferenceMap<V> map;
      protected transient ObjectSet<Short2ReferenceMap.Entry<V>> entries;
      protected transient ShortSet keys;
      protected transient ReferenceCollection<V> values;

      protected SynchronizedMap(Short2ReferenceMap<V> m, Object sync) {
         super(m, sync);
         this.map = m;
      }

      protected SynchronizedMap(Short2ReferenceMap<V> m) {
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
      public void putAll(Map<? extends Short, ? extends V> m) {
         synchronized (this.sync) {
            this.map.putAll(m);
         }
      }

      @Override
      public ObjectSet<Short2ReferenceMap.Entry<V>> short2ReferenceEntrySet() {
         synchronized (this.sync) {
            if (this.entries == null) {
               this.entries = ObjectSets.synchronize(this.map.short2ReferenceEntrySet(), this.sync);
            }

            return this.entries;
         }
      }

      @Deprecated
      @Override
      public ObjectSet<Entry<Short, V>> entrySet() {
         return this.short2ReferenceEntrySet();
      }

      @Override
      public ShortSet keySet() {
         synchronized (this.sync) {
            if (this.keys == null) {
               this.keys = ShortSets.synchronize(this.map.keySet(), this.sync);
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
      public V getOrDefault(short key, V defaultValue) {
         synchronized (this.sync) {
            return this.map.getOrDefault(key, defaultValue);
         }
      }

      @Override
      public void forEach(BiConsumer<? super Short, ? super V> action) {
         synchronized (this.sync) {
            this.map.forEach(action);
         }
      }

      @Override
      public void replaceAll(BiFunction<? super Short, ? super V, ? extends V> function) {
         synchronized (this.sync) {
            this.map.replaceAll(function);
         }
      }

      @Override
      public V putIfAbsent(short key, V value) {
         synchronized (this.sync) {
            return this.map.putIfAbsent(key, value);
         }
      }

      @Override
      public boolean remove(short key, Object value) {
         synchronized (this.sync) {
            return this.map.remove(key, value);
         }
      }

      @Override
      public V replace(short key, V value) {
         synchronized (this.sync) {
            return this.map.replace(key, value);
         }
      }

      @Override
      public boolean replace(short key, V oldValue, V newValue) {
         synchronized (this.sync) {
            return this.map.replace(key, oldValue, newValue);
         }
      }

      @Override
      public V computeIfAbsent(short key, IntFunction<? extends V> mappingFunction) {
         synchronized (this.sync) {
            return this.map.computeIfAbsent(key, mappingFunction);
         }
      }

      @Override
      public V computeIfAbsent(short key, Short2ReferenceFunction<? extends V> mappingFunction) {
         synchronized (this.sync) {
            return this.map.computeIfAbsent(key, mappingFunction);
         }
      }

      @Override
      public V computeIfPresent(short key, BiFunction<? super Short, ? super V, ? extends V> remappingFunction) {
         synchronized (this.sync) {
            return this.map.computeIfPresent(key, remappingFunction);
         }
      }

      @Override
      public V compute(short key, BiFunction<? super Short, ? super V, ? extends V> remappingFunction) {
         synchronized (this.sync) {
            return this.map.compute(key, remappingFunction);
         }
      }

      @Override
      public V merge(short key, V value, BiFunction<? super V, ? super V, ? extends V> remappingFunction) {
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
      public V replace(Short key, V value) {
         synchronized (this.sync) {
            return this.map.replace(key, value);
         }
      }

      @Deprecated
      public boolean replace(Short key, V oldValue, V newValue) {
         synchronized (this.sync) {
            return this.map.replace(key, oldValue, newValue);
         }
      }

      @Deprecated
      public V putIfAbsent(Short key, V value) {
         synchronized (this.sync) {
            return this.map.putIfAbsent(key, value);
         }
      }

      @Deprecated
      public V computeIfAbsent(Short key, Function<? super Short, ? extends V> mappingFunction) {
         synchronized (this.sync) {
            return this.map.computeIfAbsent(key, mappingFunction);
         }
      }

      @Deprecated
      public V computeIfPresent(Short key, BiFunction<? super Short, ? super V, ? extends V> remappingFunction) {
         synchronized (this.sync) {
            return this.map.computeIfPresent(key, remappingFunction);
         }
      }

      @Deprecated
      public V compute(Short key, BiFunction<? super Short, ? super V, ? extends V> remappingFunction) {
         synchronized (this.sync) {
            return this.map.compute(key, remappingFunction);
         }
      }

      @Deprecated
      public V merge(Short key, V value, BiFunction<? super V, ? super V, ? extends V> remappingFunction) {
         synchronized (this.sync) {
            return this.map.merge(key, value, remappingFunction);
         }
      }
   }

   public static class UnmodifiableMap<V> extends Short2ReferenceFunctions.UnmodifiableFunction<V> implements Short2ReferenceMap<V>, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Short2ReferenceMap<? extends V> map;
      protected transient ObjectSet<Short2ReferenceMap.Entry<V>> entries;
      protected transient ShortSet keys;
      protected transient ReferenceCollection<V> values;

      protected UnmodifiableMap(Short2ReferenceMap<? extends V> m) {
         super(m);
         this.map = m;
      }

      @Override
      public boolean containsValue(Object v) {
         return this.map.containsValue(v);
      }

      @Override
      public void putAll(Map<? extends Short, ? extends V> m) {
         throw new UnsupportedOperationException();
      }

      @Override
      public ObjectSet<Short2ReferenceMap.Entry<V>> short2ReferenceEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSets.unmodifiable(this.map.short2ReferenceEntrySet());
         }

         return this.entries;
      }

      @Deprecated
      @Override
      public ObjectSet<Entry<Short, V>> entrySet() {
         return this.short2ReferenceEntrySet();
      }

      @Override
      public ShortSet keySet() {
         if (this.keys == null) {
            this.keys = ShortSets.unmodifiable(this.map.keySet());
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
      public V getOrDefault(short key, V defaultValue) {
         return (V)this.map.getOrDefault(key, defaultValue);
      }

      @Override
      public void forEach(BiConsumer<? super Short, ? super V> action) {
         this.map.forEach(action);
      }

      @Override
      public void replaceAll(BiFunction<? super Short, ? super V, ? extends V> function) {
         throw new UnsupportedOperationException();
      }

      @Override
      public V putIfAbsent(short key, V value) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean remove(short key, Object value) {
         throw new UnsupportedOperationException();
      }

      @Override
      public V replace(short key, V value) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean replace(short key, V oldValue, V newValue) {
         throw new UnsupportedOperationException();
      }

      @Override
      public V computeIfAbsent(short key, IntFunction<? extends V> mappingFunction) {
         throw new UnsupportedOperationException();
      }

      @Override
      public V computeIfAbsent(short key, Short2ReferenceFunction<? extends V> mappingFunction) {
         throw new UnsupportedOperationException();
      }

      @Override
      public V computeIfPresent(short key, BiFunction<? super Short, ? super V, ? extends V> remappingFunction) {
         throw new UnsupportedOperationException();
      }

      @Override
      public V compute(short key, BiFunction<? super Short, ? super V, ? extends V> remappingFunction) {
         throw new UnsupportedOperationException();
      }

      @Override
      public V merge(short key, V value, BiFunction<? super V, ? super V, ? extends V> remappingFunction) {
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
      public V replace(Short key, V value) {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      public boolean replace(Short key, V oldValue, V newValue) {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      public V putIfAbsent(Short key, V value) {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      public V computeIfAbsent(Short key, Function<? super Short, ? extends V> mappingFunction) {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      public V computeIfPresent(Short key, BiFunction<? super Short, ? super V, ? extends V> remappingFunction) {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      public V compute(Short key, BiFunction<? super Short, ? super V, ? extends V> remappingFunction) {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      public V merge(Short key, V value, BiFunction<? super V, ? super V, ? extends V> remappingFunction) {
         throw new UnsupportedOperationException();
      }
   }
}
