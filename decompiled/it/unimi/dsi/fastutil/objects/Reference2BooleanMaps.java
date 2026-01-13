package it.unimi.dsi.fastutil.objects;

import it.unimi.dsi.fastutil.booleans.BooleanCollection;
import it.unimi.dsi.fastutil.booleans.BooleanCollections;
import it.unimi.dsi.fastutil.booleans.BooleanSets;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

public final class Reference2BooleanMaps {
   public static final Reference2BooleanMaps.EmptyMap EMPTY_MAP = new Reference2BooleanMaps.EmptyMap();

   private Reference2BooleanMaps() {
   }

   public static <K> ObjectIterator<Reference2BooleanMap.Entry<K>> fastIterator(Reference2BooleanMap<K> map) {
      ObjectSet<Reference2BooleanMap.Entry<K>> entries = map.reference2BooleanEntrySet();
      return entries instanceof Reference2BooleanMap.FastEntrySet ? ((Reference2BooleanMap.FastEntrySet)entries).fastIterator() : entries.iterator();
   }

   public static <K> void fastForEach(Reference2BooleanMap<K> map, Consumer<? super Reference2BooleanMap.Entry<K>> consumer) {
      ObjectSet<Reference2BooleanMap.Entry<K>> entries = map.reference2BooleanEntrySet();
      if (entries instanceof Reference2BooleanMap.FastEntrySet) {
         ((Reference2BooleanMap.FastEntrySet)entries).fastForEach(consumer);
      } else {
         entries.forEach(consumer);
      }
   }

   public static <K> ObjectIterable<Reference2BooleanMap.Entry<K>> fastIterable(Reference2BooleanMap<K> map) {
      final ObjectSet<Reference2BooleanMap.Entry<K>> entries = map.reference2BooleanEntrySet();
      return (ObjectIterable<Reference2BooleanMap.Entry<K>>)(entries instanceof Reference2BooleanMap.FastEntrySet
         ? new ObjectIterable<Reference2BooleanMap.Entry<K>>() {
            @Override
            public ObjectIterator<Reference2BooleanMap.Entry<K>> iterator() {
               return ((Reference2BooleanMap.FastEntrySet)entries).fastIterator();
            }

            @Override
            public ObjectSpliterator<Reference2BooleanMap.Entry<K>> spliterator() {
               return entries.spliterator();
            }

            @Override
            public void forEach(Consumer<? super Reference2BooleanMap.Entry<K>> consumer) {
               ((Reference2BooleanMap.FastEntrySet)entries).fastForEach(consumer);
            }
         }
         : entries);
   }

   public static <K> Reference2BooleanMap<K> emptyMap() {
      return EMPTY_MAP;
   }

   public static <K> Reference2BooleanMap<K> singleton(K key, boolean value) {
      return new Reference2BooleanMaps.Singleton<>(key, value);
   }

   public static <K> Reference2BooleanMap<K> singleton(K key, Boolean value) {
      return new Reference2BooleanMaps.Singleton<>(key, value);
   }

   public static <K> Reference2BooleanMap<K> synchronize(Reference2BooleanMap<K> m) {
      return new Reference2BooleanMaps.SynchronizedMap<>(m);
   }

   public static <K> Reference2BooleanMap<K> synchronize(Reference2BooleanMap<K> m, Object sync) {
      return new Reference2BooleanMaps.SynchronizedMap<>(m, sync);
   }

   public static <K> Reference2BooleanMap<K> unmodifiable(Reference2BooleanMap<? extends K> m) {
      return new Reference2BooleanMaps.UnmodifiableMap<>(m);
   }

   public static class EmptyMap<K> extends Reference2BooleanFunctions.EmptyFunction<K> implements Reference2BooleanMap<K>, Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;

      protected EmptyMap() {
      }

      @Override
      public boolean containsValue(boolean v) {
         return false;
      }

      @Deprecated
      @Override
      public Boolean getOrDefault(Object key, Boolean defaultValue) {
         return defaultValue;
      }

      @Override
      public boolean getOrDefault(Object key, boolean defaultValue) {
         return defaultValue;
      }

      @Deprecated
      @Override
      public boolean containsValue(Object ov) {
         return false;
      }

      @Override
      public void putAll(Map<? extends K, ? extends Boolean> m) {
         throw new UnsupportedOperationException();
      }

      @Override
      public ObjectSet<Reference2BooleanMap.Entry<K>> reference2BooleanEntrySet() {
         return ObjectSets.EMPTY_SET;
      }

      @Override
      public ReferenceSet<K> keySet() {
         return ReferenceSets.EMPTY_SET;
      }

      @Override
      public BooleanCollection values() {
         return BooleanSets.EMPTY_SET;
      }

      @Override
      public void forEach(BiConsumer<? super K, ? super Boolean> consumer) {
      }

      @Override
      public Object clone() {
         return Reference2BooleanMaps.EMPTY_MAP;
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

   public static class Singleton<K> extends Reference2BooleanFunctions.Singleton<K> implements Reference2BooleanMap<K>, Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected transient ObjectSet<Reference2BooleanMap.Entry<K>> entries;
      protected transient ReferenceSet<K> keys;
      protected transient BooleanCollection values;

      protected Singleton(K key, boolean value) {
         super(key, value);
      }

      @Override
      public boolean containsValue(boolean v) {
         return this.value == v;
      }

      @Deprecated
      @Override
      public boolean containsValue(Object ov) {
         return (Boolean)ov == this.value;
      }

      @Override
      public void putAll(Map<? extends K, ? extends Boolean> m) {
         throw new UnsupportedOperationException();
      }

      @Override
      public ObjectSet<Reference2BooleanMap.Entry<K>> reference2BooleanEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSets.singleton(new AbstractReference2BooleanMap.BasicEntry<>(this.key, this.value));
         }

         return this.entries;
      }

      @Deprecated
      @Override
      public ObjectSet<Entry<K, Boolean>> entrySet() {
         return this.reference2BooleanEntrySet();
      }

      @Override
      public ReferenceSet<K> keySet() {
         if (this.keys == null) {
            this.keys = ReferenceSets.singleton(this.key);
         }

         return this.keys;
      }

      @Override
      public BooleanCollection values() {
         if (this.values == null) {
            this.values = BooleanSets.singleton(this.value);
         }

         return this.values;
      }

      @Override
      public boolean isEmpty() {
         return false;
      }

      @Override
      public int hashCode() {
         return System.identityHashCode(this.key) ^ (this.value ? 1231 : 1237);
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

   public static class SynchronizedMap<K> extends Reference2BooleanFunctions.SynchronizedFunction<K> implements Reference2BooleanMap<K>, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Reference2BooleanMap<K> map;
      protected transient ObjectSet<Reference2BooleanMap.Entry<K>> entries;
      protected transient ReferenceSet<K> keys;
      protected transient BooleanCollection values;

      protected SynchronizedMap(Reference2BooleanMap<K> m, Object sync) {
         super(m, sync);
         this.map = m;
      }

      protected SynchronizedMap(Reference2BooleanMap<K> m) {
         super(m);
         this.map = m;
      }

      @Override
      public boolean containsValue(boolean v) {
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
      public void putAll(Map<? extends K, ? extends Boolean> m) {
         synchronized (this.sync) {
            this.map.putAll(m);
         }
      }

      @Override
      public ObjectSet<Reference2BooleanMap.Entry<K>> reference2BooleanEntrySet() {
         synchronized (this.sync) {
            if (this.entries == null) {
               this.entries = ObjectSets.synchronize(this.map.reference2BooleanEntrySet(), this.sync);
            }

            return this.entries;
         }
      }

      @Deprecated
      @Override
      public ObjectSet<Entry<K, Boolean>> entrySet() {
         return this.reference2BooleanEntrySet();
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
      public BooleanCollection values() {
         synchronized (this.sync) {
            if (this.values == null) {
               this.values = BooleanCollections.synchronize(this.map.values(), this.sync);
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
      public boolean getOrDefault(Object key, boolean defaultValue) {
         synchronized (this.sync) {
            return this.map.getOrDefault(key, defaultValue);
         }
      }

      @Override
      public void forEach(BiConsumer<? super K, ? super Boolean> action) {
         synchronized (this.sync) {
            this.map.forEach(action);
         }
      }

      @Override
      public void replaceAll(BiFunction<? super K, ? super Boolean, ? extends Boolean> function) {
         synchronized (this.sync) {
            this.map.replaceAll(function);
         }
      }

      @Override
      public boolean putIfAbsent(K key, boolean value) {
         synchronized (this.sync) {
            return this.map.putIfAbsent(key, value);
         }
      }

      @Override
      public boolean remove(Object key, boolean value) {
         synchronized (this.sync) {
            return this.map.remove(key, value);
         }
      }

      @Override
      public boolean replace(K key, boolean value) {
         synchronized (this.sync) {
            return this.map.replace(key, value);
         }
      }

      @Override
      public boolean replace(K key, boolean oldValue, boolean newValue) {
         synchronized (this.sync) {
            return this.map.replace(key, oldValue, newValue);
         }
      }

      @Override
      public boolean computeIfAbsent(K key, Predicate<? super K> mappingFunction) {
         synchronized (this.sync) {
            return this.map.computeIfAbsent(key, mappingFunction);
         }
      }

      @Override
      public boolean computeIfAbsent(K key, Reference2BooleanFunction<? super K> mappingFunction) {
         synchronized (this.sync) {
            return this.map.computeIfAbsent(key, mappingFunction);
         }
      }

      @Override
      public boolean computeBooleanIfPresent(K key, BiFunction<? super K, ? super Boolean, ? extends Boolean> remappingFunction) {
         synchronized (this.sync) {
            return this.map.computeBooleanIfPresent(key, remappingFunction);
         }
      }

      @Override
      public boolean computeBoolean(K key, BiFunction<? super K, ? super Boolean, ? extends Boolean> remappingFunction) {
         synchronized (this.sync) {
            return this.map.computeBoolean(key, remappingFunction);
         }
      }

      @Override
      public boolean merge(K key, boolean value, BiFunction<? super Boolean, ? super Boolean, ? extends Boolean> remappingFunction) {
         synchronized (this.sync) {
            return this.map.merge(key, value, remappingFunction);
         }
      }

      @Deprecated
      @Override
      public Boolean getOrDefault(Object key, Boolean defaultValue) {
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
      public Boolean replace(K key, Boolean value) {
         synchronized (this.sync) {
            return this.map.replace(key, value);
         }
      }

      @Deprecated
      public boolean replace(K key, Boolean oldValue, Boolean newValue) {
         synchronized (this.sync) {
            return this.map.replace(key, oldValue, newValue);
         }
      }

      @Deprecated
      public Boolean putIfAbsent(K key, Boolean value) {
         synchronized (this.sync) {
            return this.map.putIfAbsent(key, value);
         }
      }

      public Boolean computeIfAbsent(K key, Function<? super K, ? extends Boolean> mappingFunction) {
         synchronized (this.sync) {
            return this.map.computeIfAbsent(key, mappingFunction);
         }
      }

      public Boolean computeIfPresent(K key, BiFunction<? super K, ? super Boolean, ? extends Boolean> remappingFunction) {
         synchronized (this.sync) {
            return this.map.computeIfPresent(key, remappingFunction);
         }
      }

      public Boolean compute(K key, BiFunction<? super K, ? super Boolean, ? extends Boolean> remappingFunction) {
         synchronized (this.sync) {
            return this.map.compute(key, remappingFunction);
         }
      }

      @Deprecated
      public Boolean merge(K key, Boolean value, BiFunction<? super Boolean, ? super Boolean, ? extends Boolean> remappingFunction) {
         synchronized (this.sync) {
            return this.map.merge(key, value, remappingFunction);
         }
      }
   }

   public static class UnmodifiableMap<K> extends Reference2BooleanFunctions.UnmodifiableFunction<K> implements Reference2BooleanMap<K>, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Reference2BooleanMap<? extends K> map;
      protected transient ObjectSet<Reference2BooleanMap.Entry<K>> entries;
      protected transient ReferenceSet<K> keys;
      protected transient BooleanCollection values;

      protected UnmodifiableMap(Reference2BooleanMap<? extends K> m) {
         super(m);
         this.map = m;
      }

      @Override
      public boolean containsValue(boolean v) {
         return this.map.containsValue(v);
      }

      @Deprecated
      @Override
      public boolean containsValue(Object ov) {
         return this.map.containsValue(ov);
      }

      @Override
      public void putAll(Map<? extends K, ? extends Boolean> m) {
         throw new UnsupportedOperationException();
      }

      @Override
      public ObjectSet<Reference2BooleanMap.Entry<K>> reference2BooleanEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSets.unmodifiable(this.map.reference2BooleanEntrySet());
         }

         return this.entries;
      }

      @Deprecated
      @Override
      public ObjectSet<Entry<K, Boolean>> entrySet() {
         return this.reference2BooleanEntrySet();
      }

      @Override
      public ReferenceSet<K> keySet() {
         if (this.keys == null) {
            this.keys = ReferenceSets.unmodifiable(this.map.keySet());
         }

         return this.keys;
      }

      @Override
      public BooleanCollection values() {
         if (this.values == null) {
            this.values = BooleanCollections.unmodifiable(this.map.values());
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
      public boolean getOrDefault(Object key, boolean defaultValue) {
         return this.map.getOrDefault(key, defaultValue);
      }

      @Override
      public void forEach(BiConsumer<? super K, ? super Boolean> action) {
         this.map.forEach(action);
      }

      @Override
      public void replaceAll(BiFunction<? super K, ? super Boolean, ? extends Boolean> function) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean putIfAbsent(K key, boolean value) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean remove(Object key, boolean value) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean replace(K key, boolean value) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean replace(K key, boolean oldValue, boolean newValue) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean computeIfAbsent(K key, Predicate<? super K> mappingFunction) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean computeIfAbsent(K key, Reference2BooleanFunction<? super K> mappingFunction) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean computeBooleanIfPresent(K key, BiFunction<? super K, ? super Boolean, ? extends Boolean> remappingFunction) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean computeBoolean(K key, BiFunction<? super K, ? super Boolean, ? extends Boolean> remappingFunction) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean merge(K key, boolean value, BiFunction<? super Boolean, ? super Boolean, ? extends Boolean> remappingFunction) {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      @Override
      public Boolean getOrDefault(Object key, Boolean defaultValue) {
         return this.map.getOrDefault(key, defaultValue);
      }

      @Deprecated
      @Override
      public boolean remove(Object key, Object value) {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      public Boolean replace(K key, Boolean value) {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      public boolean replace(K key, Boolean oldValue, Boolean newValue) {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      public Boolean putIfAbsent(K key, Boolean value) {
         throw new UnsupportedOperationException();
      }

      public Boolean computeIfAbsent(K key, Function<? super K, ? extends Boolean> mappingFunction) {
         throw new UnsupportedOperationException();
      }

      public Boolean computeIfPresent(K key, BiFunction<? super K, ? super Boolean, ? extends Boolean> remappingFunction) {
         throw new UnsupportedOperationException();
      }

      public Boolean compute(K key, BiFunction<? super K, ? super Boolean, ? extends Boolean> remappingFunction) {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      public Boolean merge(K key, Boolean value, BiFunction<? super Boolean, ? super Boolean, ? extends Boolean> remappingFunction) {
         throw new UnsupportedOperationException();
      }
   }
}
