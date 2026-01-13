package it.unimi.dsi.fastutil.objects;

import it.unimi.dsi.fastutil.shorts.ShortCollection;
import it.unimi.dsi.fastutil.shorts.ShortCollections;
import it.unimi.dsi.fastutil.shorts.ShortSets;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.ToIntFunction;

public final class Reference2ShortMaps {
   public static final Reference2ShortMaps.EmptyMap EMPTY_MAP = new Reference2ShortMaps.EmptyMap();

   private Reference2ShortMaps() {
   }

   public static <K> ObjectIterator<Reference2ShortMap.Entry<K>> fastIterator(Reference2ShortMap<K> map) {
      ObjectSet<Reference2ShortMap.Entry<K>> entries = map.reference2ShortEntrySet();
      return entries instanceof Reference2ShortMap.FastEntrySet ? ((Reference2ShortMap.FastEntrySet)entries).fastIterator() : entries.iterator();
   }

   public static <K> void fastForEach(Reference2ShortMap<K> map, Consumer<? super Reference2ShortMap.Entry<K>> consumer) {
      ObjectSet<Reference2ShortMap.Entry<K>> entries = map.reference2ShortEntrySet();
      if (entries instanceof Reference2ShortMap.FastEntrySet) {
         ((Reference2ShortMap.FastEntrySet)entries).fastForEach(consumer);
      } else {
         entries.forEach(consumer);
      }
   }

   public static <K> ObjectIterable<Reference2ShortMap.Entry<K>> fastIterable(Reference2ShortMap<K> map) {
      final ObjectSet<Reference2ShortMap.Entry<K>> entries = map.reference2ShortEntrySet();
      return (ObjectIterable<Reference2ShortMap.Entry<K>>)(entries instanceof Reference2ShortMap.FastEntrySet
         ? new ObjectIterable<Reference2ShortMap.Entry<K>>() {
            @Override
            public ObjectIterator<Reference2ShortMap.Entry<K>> iterator() {
               return ((Reference2ShortMap.FastEntrySet)entries).fastIterator();
            }

            @Override
            public ObjectSpliterator<Reference2ShortMap.Entry<K>> spliterator() {
               return entries.spliterator();
            }

            @Override
            public void forEach(Consumer<? super Reference2ShortMap.Entry<K>> consumer) {
               ((Reference2ShortMap.FastEntrySet)entries).fastForEach(consumer);
            }
         }
         : entries);
   }

   public static <K> Reference2ShortMap<K> emptyMap() {
      return EMPTY_MAP;
   }

   public static <K> Reference2ShortMap<K> singleton(K key, short value) {
      return new Reference2ShortMaps.Singleton<>(key, value);
   }

   public static <K> Reference2ShortMap<K> singleton(K key, Short value) {
      return new Reference2ShortMaps.Singleton<>(key, value);
   }

   public static <K> Reference2ShortMap<K> synchronize(Reference2ShortMap<K> m) {
      return new Reference2ShortMaps.SynchronizedMap<>(m);
   }

   public static <K> Reference2ShortMap<K> synchronize(Reference2ShortMap<K> m, Object sync) {
      return new Reference2ShortMaps.SynchronizedMap<>(m, sync);
   }

   public static <K> Reference2ShortMap<K> unmodifiable(Reference2ShortMap<? extends K> m) {
      return new Reference2ShortMaps.UnmodifiableMap<>(m);
   }

   public static class EmptyMap<K> extends Reference2ShortFunctions.EmptyFunction<K> implements Reference2ShortMap<K>, Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;

      protected EmptyMap() {
      }

      @Override
      public boolean containsValue(short v) {
         return false;
      }

      @Deprecated
      @Override
      public Short getOrDefault(Object key, Short defaultValue) {
         return defaultValue;
      }

      @Override
      public short getOrDefault(Object key, short defaultValue) {
         return defaultValue;
      }

      @Deprecated
      @Override
      public boolean containsValue(Object ov) {
         return false;
      }

      @Override
      public void putAll(Map<? extends K, ? extends Short> m) {
         throw new UnsupportedOperationException();
      }

      @Override
      public ObjectSet<Reference2ShortMap.Entry<K>> reference2ShortEntrySet() {
         return ObjectSets.EMPTY_SET;
      }

      @Override
      public ReferenceSet<K> keySet() {
         return ReferenceSets.EMPTY_SET;
      }

      @Override
      public ShortCollection values() {
         return ShortSets.EMPTY_SET;
      }

      @Override
      public void forEach(BiConsumer<? super K, ? super Short> consumer) {
      }

      @Override
      public Object clone() {
         return Reference2ShortMaps.EMPTY_MAP;
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

   public static class Singleton<K> extends Reference2ShortFunctions.Singleton<K> implements Reference2ShortMap<K>, Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected transient ObjectSet<Reference2ShortMap.Entry<K>> entries;
      protected transient ReferenceSet<K> keys;
      protected transient ShortCollection values;

      protected Singleton(K key, short value) {
         super(key, value);
      }

      @Override
      public boolean containsValue(short v) {
         return this.value == v;
      }

      @Deprecated
      @Override
      public boolean containsValue(Object ov) {
         return (Short)ov == this.value;
      }

      @Override
      public void putAll(Map<? extends K, ? extends Short> m) {
         throw new UnsupportedOperationException();
      }

      @Override
      public ObjectSet<Reference2ShortMap.Entry<K>> reference2ShortEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSets.singleton(new AbstractReference2ShortMap.BasicEntry<>(this.key, this.value));
         }

         return this.entries;
      }

      @Deprecated
      @Override
      public ObjectSet<Entry<K, Short>> entrySet() {
         return this.reference2ShortEntrySet();
      }

      @Override
      public ReferenceSet<K> keySet() {
         if (this.keys == null) {
            this.keys = ReferenceSets.singleton(this.key);
         }

         return this.keys;
      }

      @Override
      public ShortCollection values() {
         if (this.values == null) {
            this.values = ShortSets.singleton(this.value);
         }

         return this.values;
      }

      @Override
      public boolean isEmpty() {
         return false;
      }

      @Override
      public int hashCode() {
         return System.identityHashCode(this.key) ^ this.value;
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

   public static class SynchronizedMap<K> extends Reference2ShortFunctions.SynchronizedFunction<K> implements Reference2ShortMap<K>, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Reference2ShortMap<K> map;
      protected transient ObjectSet<Reference2ShortMap.Entry<K>> entries;
      protected transient ReferenceSet<K> keys;
      protected transient ShortCollection values;

      protected SynchronizedMap(Reference2ShortMap<K> m, Object sync) {
         super(m, sync);
         this.map = m;
      }

      protected SynchronizedMap(Reference2ShortMap<K> m) {
         super(m);
         this.map = m;
      }

      @Override
      public boolean containsValue(short v) {
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
      public void putAll(Map<? extends K, ? extends Short> m) {
         synchronized (this.sync) {
            this.map.putAll(m);
         }
      }

      @Override
      public ObjectSet<Reference2ShortMap.Entry<K>> reference2ShortEntrySet() {
         synchronized (this.sync) {
            if (this.entries == null) {
               this.entries = ObjectSets.synchronize(this.map.reference2ShortEntrySet(), this.sync);
            }

            return this.entries;
         }
      }

      @Deprecated
      @Override
      public ObjectSet<Entry<K, Short>> entrySet() {
         return this.reference2ShortEntrySet();
      }

      @Override
      public ReferenceSet<K> keySet() {
         synchronized (this.sync) {
            if (this.keys == null) {
               this.keys = ReferenceSets.synchronize(this.map.keySet(), this.sync);
            }

            return this.keys;
         }
      }

      @Override
      public ShortCollection values() {
         synchronized (this.sync) {
            if (this.values == null) {
               this.values = ShortCollections.synchronize(this.map.values(), this.sync);
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
      public short getOrDefault(Object key, short defaultValue) {
         synchronized (this.sync) {
            return this.map.getOrDefault(key, defaultValue);
         }
      }

      @Override
      public void forEach(BiConsumer<? super K, ? super Short> action) {
         synchronized (this.sync) {
            this.map.forEach(action);
         }
      }

      @Override
      public void replaceAll(BiFunction<? super K, ? super Short, ? extends Short> function) {
         synchronized (this.sync) {
            this.map.replaceAll(function);
         }
      }

      @Override
      public short putIfAbsent(K key, short value) {
         synchronized (this.sync) {
            return this.map.putIfAbsent(key, value);
         }
      }

      @Override
      public boolean remove(Object key, short value) {
         synchronized (this.sync) {
            return this.map.remove(key, value);
         }
      }

      @Override
      public short replace(K key, short value) {
         synchronized (this.sync) {
            return this.map.replace(key, value);
         }
      }

      @Override
      public boolean replace(K key, short oldValue, short newValue) {
         synchronized (this.sync) {
            return this.map.replace(key, oldValue, newValue);
         }
      }

      @Override
      public short computeIfAbsent(K key, ToIntFunction<? super K> mappingFunction) {
         synchronized (this.sync) {
            return this.map.computeIfAbsent(key, mappingFunction);
         }
      }

      @Override
      public short computeIfAbsent(K key, Reference2ShortFunction<? super K> mappingFunction) {
         synchronized (this.sync) {
            return this.map.computeIfAbsent(key, mappingFunction);
         }
      }

      @Override
      public short computeShortIfPresent(K key, BiFunction<? super K, ? super Short, ? extends Short> remappingFunction) {
         synchronized (this.sync) {
            return this.map.computeShortIfPresent(key, remappingFunction);
         }
      }

      @Override
      public short computeShort(K key, BiFunction<? super K, ? super Short, ? extends Short> remappingFunction) {
         synchronized (this.sync) {
            return this.map.computeShort(key, remappingFunction);
         }
      }

      @Override
      public short merge(K key, short value, BiFunction<? super Short, ? super Short, ? extends Short> remappingFunction) {
         synchronized (this.sync) {
            return this.map.merge(key, value, remappingFunction);
         }
      }

      @Deprecated
      @Override
      public Short getOrDefault(Object key, Short defaultValue) {
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
      public Short replace(K key, Short value) {
         synchronized (this.sync) {
            return this.map.replace(key, value);
         }
      }

      @Deprecated
      @Override
      public boolean replace(K key, Short oldValue, Short newValue) {
         synchronized (this.sync) {
            return this.map.replace(key, oldValue, newValue);
         }
      }

      @Deprecated
      @Override
      public Short putIfAbsent(K key, Short value) {
         synchronized (this.sync) {
            return this.map.putIfAbsent(key, value);
         }
      }

      public Short computeIfAbsent(K key, Function<? super K, ? extends Short> mappingFunction) {
         synchronized (this.sync) {
            return this.map.computeIfAbsent(key, mappingFunction);
         }
      }

      public Short computeIfPresent(K key, BiFunction<? super K, ? super Short, ? extends Short> remappingFunction) {
         synchronized (this.sync) {
            return this.map.computeIfPresent(key, remappingFunction);
         }
      }

      public Short compute(K key, BiFunction<? super K, ? super Short, ? extends Short> remappingFunction) {
         synchronized (this.sync) {
            return this.map.compute(key, remappingFunction);
         }
      }

      @Deprecated
      @Override
      public Short merge(K key, Short value, BiFunction<? super Short, ? super Short, ? extends Short> remappingFunction) {
         synchronized (this.sync) {
            return this.map.merge(key, value, remappingFunction);
         }
      }
   }

   public static class UnmodifiableMap<K> extends Reference2ShortFunctions.UnmodifiableFunction<K> implements Reference2ShortMap<K>, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Reference2ShortMap<? extends K> map;
      protected transient ObjectSet<Reference2ShortMap.Entry<K>> entries;
      protected transient ReferenceSet<K> keys;
      protected transient ShortCollection values;

      protected UnmodifiableMap(Reference2ShortMap<? extends K> m) {
         super(m);
         this.map = m;
      }

      @Override
      public boolean containsValue(short v) {
         return this.map.containsValue(v);
      }

      @Deprecated
      @Override
      public boolean containsValue(Object ov) {
         return this.map.containsValue(ov);
      }

      @Override
      public void putAll(Map<? extends K, ? extends Short> m) {
         throw new UnsupportedOperationException();
      }

      @Override
      public ObjectSet<Reference2ShortMap.Entry<K>> reference2ShortEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSets.unmodifiable(this.map.reference2ShortEntrySet());
         }

         return this.entries;
      }

      @Deprecated
      @Override
      public ObjectSet<Entry<K, Short>> entrySet() {
         return this.reference2ShortEntrySet();
      }

      @Override
      public ReferenceSet<K> keySet() {
         if (this.keys == null) {
            this.keys = ReferenceSets.unmodifiable(this.map.keySet());
         }

         return this.keys;
      }

      @Override
      public ShortCollection values() {
         if (this.values == null) {
            this.values = ShortCollections.unmodifiable(this.map.values());
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
      public short getOrDefault(Object key, short defaultValue) {
         return this.map.getOrDefault(key, defaultValue);
      }

      @Override
      public void forEach(BiConsumer<? super K, ? super Short> action) {
         this.map.forEach(action);
      }

      @Override
      public void replaceAll(BiFunction<? super K, ? super Short, ? extends Short> function) {
         throw new UnsupportedOperationException();
      }

      @Override
      public short putIfAbsent(K key, short value) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean remove(Object key, short value) {
         throw new UnsupportedOperationException();
      }

      @Override
      public short replace(K key, short value) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean replace(K key, short oldValue, short newValue) {
         throw new UnsupportedOperationException();
      }

      @Override
      public short computeIfAbsent(K key, ToIntFunction<? super K> mappingFunction) {
         throw new UnsupportedOperationException();
      }

      @Override
      public short computeIfAbsent(K key, Reference2ShortFunction<? super K> mappingFunction) {
         throw new UnsupportedOperationException();
      }

      @Override
      public short computeShortIfPresent(K key, BiFunction<? super K, ? super Short, ? extends Short> remappingFunction) {
         throw new UnsupportedOperationException();
      }

      @Override
      public short computeShort(K key, BiFunction<? super K, ? super Short, ? extends Short> remappingFunction) {
         throw new UnsupportedOperationException();
      }

      @Override
      public short merge(K key, short value, BiFunction<? super Short, ? super Short, ? extends Short> remappingFunction) {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      @Override
      public Short getOrDefault(Object key, Short defaultValue) {
         return this.map.getOrDefault(key, defaultValue);
      }

      @Deprecated
      @Override
      public boolean remove(Object key, Object value) {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      @Override
      public Short replace(K key, Short value) {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      @Override
      public boolean replace(K key, Short oldValue, Short newValue) {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      @Override
      public Short putIfAbsent(K key, Short value) {
         throw new UnsupportedOperationException();
      }

      public Short computeIfAbsent(K key, Function<? super K, ? extends Short> mappingFunction) {
         throw new UnsupportedOperationException();
      }

      public Short computeIfPresent(K key, BiFunction<? super K, ? super Short, ? extends Short> remappingFunction) {
         throw new UnsupportedOperationException();
      }

      public Short compute(K key, BiFunction<? super K, ? super Short, ? extends Short> remappingFunction) {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      @Override
      public Short merge(K key, Short value, BiFunction<? super Short, ? super Short, ? extends Short> remappingFunction) {
         throw new UnsupportedOperationException();
      }
   }
}
