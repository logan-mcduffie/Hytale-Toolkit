package it.unimi.dsi.fastutil.longs;

import it.unimi.dsi.fastutil.HashCommon;
import it.unimi.dsi.fastutil.objects.ObjectIterable;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import it.unimi.dsi.fastutil.objects.ObjectSets;
import it.unimi.dsi.fastutil.objects.ObjectSpliterator;
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

public final class Long2LongMaps {
   public static final Long2LongMaps.EmptyMap EMPTY_MAP = new Long2LongMaps.EmptyMap();

   private Long2LongMaps() {
   }

   public static ObjectIterator<Long2LongMap.Entry> fastIterator(Long2LongMap map) {
      ObjectSet<Long2LongMap.Entry> entries = map.long2LongEntrySet();
      return entries instanceof Long2LongMap.FastEntrySet ? ((Long2LongMap.FastEntrySet)entries).fastIterator() : entries.iterator();
   }

   public static void fastForEach(Long2LongMap map, Consumer<? super Long2LongMap.Entry> consumer) {
      ObjectSet<Long2LongMap.Entry> entries = map.long2LongEntrySet();
      if (entries instanceof Long2LongMap.FastEntrySet) {
         ((Long2LongMap.FastEntrySet)entries).fastForEach(consumer);
      } else {
         entries.forEach(consumer);
      }
   }

   public static ObjectIterable<Long2LongMap.Entry> fastIterable(Long2LongMap map) {
      final ObjectSet<Long2LongMap.Entry> entries = map.long2LongEntrySet();
      return (ObjectIterable<Long2LongMap.Entry>)(entries instanceof Long2LongMap.FastEntrySet ? new ObjectIterable<Long2LongMap.Entry>() {
         @Override
         public ObjectIterator<Long2LongMap.Entry> iterator() {
            return ((Long2LongMap.FastEntrySet)entries).fastIterator();
         }

         @Override
         public ObjectSpliterator<Long2LongMap.Entry> spliterator() {
            return entries.spliterator();
         }

         @Override
         public void forEach(Consumer<? super Long2LongMap.Entry> consumer) {
            ((Long2LongMap.FastEntrySet)entries).fastForEach(consumer);
         }
      } : entries);
   }

   public static Long2LongMap singleton(long key, long value) {
      return new Long2LongMaps.Singleton(key, value);
   }

   public static Long2LongMap singleton(Long key, Long value) {
      return new Long2LongMaps.Singleton(key, value);
   }

   public static Long2LongMap synchronize(Long2LongMap m) {
      return new Long2LongMaps.SynchronizedMap(m);
   }

   public static Long2LongMap synchronize(Long2LongMap m, Object sync) {
      return new Long2LongMaps.SynchronizedMap(m, sync);
   }

   public static Long2LongMap unmodifiable(Long2LongMap m) {
      return new Long2LongMaps.UnmodifiableMap(m);
   }

   public static class EmptyMap extends Long2LongFunctions.EmptyFunction implements Long2LongMap, Serializable, Cloneable {
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
      public long getOrDefault(long key, long defaultValue) {
         return defaultValue;
      }

      @Deprecated
      @Override
      public boolean containsValue(Object ov) {
         return false;
      }

      @Override
      public void putAll(Map<? extends Long, ? extends Long> m) {
         throw new UnsupportedOperationException();
      }

      @Override
      public ObjectSet<Long2LongMap.Entry> long2LongEntrySet() {
         return ObjectSets.EMPTY_SET;
      }

      @Override
      public LongSet keySet() {
         return LongSets.EMPTY_SET;
      }

      @Override
      public LongCollection values() {
         return LongSets.EMPTY_SET;
      }

      @Override
      public void forEach(BiConsumer<? super Long, ? super Long> consumer) {
      }

      @Override
      public Object clone() {
         return Long2LongMaps.EMPTY_MAP;
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

   public static class Singleton extends Long2LongFunctions.Singleton implements Long2LongMap, Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected transient ObjectSet<Long2LongMap.Entry> entries;
      protected transient LongSet keys;
      protected transient LongCollection values;

      protected Singleton(long key, long value) {
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
      public void putAll(Map<? extends Long, ? extends Long> m) {
         throw new UnsupportedOperationException();
      }

      @Override
      public ObjectSet<Long2LongMap.Entry> long2LongEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSets.singleton(new AbstractLong2LongMap.BasicEntry(this.key, this.value));
         }

         return this.entries;
      }

      @Deprecated
      @Override
      public ObjectSet<Entry<Long, Long>> entrySet() {
         return this.long2LongEntrySet();
      }

      @Override
      public LongSet keySet() {
         if (this.keys == null) {
            this.keys = LongSets.singleton(this.key);
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
         return HashCommon.long2int(this.key) ^ HashCommon.long2int(this.value);
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

   public static class SynchronizedMap extends Long2LongFunctions.SynchronizedFunction implements Long2LongMap, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Long2LongMap map;
      protected transient ObjectSet<Long2LongMap.Entry> entries;
      protected transient LongSet keys;
      protected transient LongCollection values;

      protected SynchronizedMap(Long2LongMap m, Object sync) {
         super(m, sync);
         this.map = m;
      }

      protected SynchronizedMap(Long2LongMap m) {
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
      public void putAll(Map<? extends Long, ? extends Long> m) {
         synchronized (this.sync) {
            this.map.putAll(m);
         }
      }

      @Override
      public ObjectSet<Long2LongMap.Entry> long2LongEntrySet() {
         synchronized (this.sync) {
            if (this.entries == null) {
               this.entries = ObjectSets.synchronize(this.map.long2LongEntrySet(), this.sync);
            }

            return this.entries;
         }
      }

      @Deprecated
      @Override
      public ObjectSet<Entry<Long, Long>> entrySet() {
         return this.long2LongEntrySet();
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
      public long getOrDefault(long key, long defaultValue) {
         synchronized (this.sync) {
            return this.map.getOrDefault(key, defaultValue);
         }
      }

      @Override
      public void forEach(BiConsumer<? super Long, ? super Long> action) {
         synchronized (this.sync) {
            this.map.forEach(action);
         }
      }

      @Override
      public void replaceAll(BiFunction<? super Long, ? super Long, ? extends Long> function) {
         synchronized (this.sync) {
            this.map.replaceAll(function);
         }
      }

      @Override
      public long putIfAbsent(long key, long value) {
         synchronized (this.sync) {
            return this.map.putIfAbsent(key, value);
         }
      }

      @Override
      public boolean remove(long key, long value) {
         synchronized (this.sync) {
            return this.map.remove(key, value);
         }
      }

      @Override
      public long replace(long key, long value) {
         synchronized (this.sync) {
            return this.map.replace(key, value);
         }
      }

      @Override
      public boolean replace(long key, long oldValue, long newValue) {
         synchronized (this.sync) {
            return this.map.replace(key, oldValue, newValue);
         }
      }

      @Override
      public long computeIfAbsent(long key, java.util.function.LongUnaryOperator mappingFunction) {
         synchronized (this.sync) {
            return this.map.computeIfAbsent(key, mappingFunction);
         }
      }

      @Override
      public long computeIfAbsentNullable(long key, LongFunction<? extends Long> mappingFunction) {
         synchronized (this.sync) {
            return this.map.computeIfAbsentNullable(key, mappingFunction);
         }
      }

      @Override
      public long computeIfAbsent(long key, Long2LongFunction mappingFunction) {
         synchronized (this.sync) {
            return this.map.computeIfAbsent(key, mappingFunction);
         }
      }

      @Override
      public long computeIfPresent(long key, BiFunction<? super Long, ? super Long, ? extends Long> remappingFunction) {
         synchronized (this.sync) {
            return this.map.computeIfPresent(key, remappingFunction);
         }
      }

      @Override
      public long compute(long key, BiFunction<? super Long, ? super Long, ? extends Long> remappingFunction) {
         synchronized (this.sync) {
            return this.map.compute(key, remappingFunction);
         }
      }

      @Override
      public long merge(long key, long value, BiFunction<? super Long, ? super Long, ? extends Long> remappingFunction) {
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
      public Long replace(Long key, Long value) {
         synchronized (this.sync) {
            return this.map.replace(key, value);
         }
      }

      @Deprecated
      @Override
      public boolean replace(Long key, Long oldValue, Long newValue) {
         synchronized (this.sync) {
            return this.map.replace(key, oldValue, newValue);
         }
      }

      @Deprecated
      @Override
      public Long putIfAbsent(Long key, Long value) {
         synchronized (this.sync) {
            return this.map.putIfAbsent(key, value);
         }
      }

      @Deprecated
      @Override
      public Long computeIfAbsent(Long key, Function<? super Long, ? extends Long> mappingFunction) {
         synchronized (this.sync) {
            return this.map.computeIfAbsent(key, mappingFunction);
         }
      }

      @Deprecated
      @Override
      public Long computeIfPresent(Long key, BiFunction<? super Long, ? super Long, ? extends Long> remappingFunction) {
         synchronized (this.sync) {
            return this.map.computeIfPresent(key, remappingFunction);
         }
      }

      @Deprecated
      @Override
      public Long compute(Long key, BiFunction<? super Long, ? super Long, ? extends Long> remappingFunction) {
         synchronized (this.sync) {
            return this.map.compute(key, remappingFunction);
         }
      }

      @Deprecated
      @Override
      public Long merge(Long key, Long value, BiFunction<? super Long, ? super Long, ? extends Long> remappingFunction) {
         synchronized (this.sync) {
            return this.map.merge(key, value, remappingFunction);
         }
      }
   }

   public static class UnmodifiableMap extends Long2LongFunctions.UnmodifiableFunction implements Long2LongMap, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Long2LongMap map;
      protected transient ObjectSet<Long2LongMap.Entry> entries;
      protected transient LongSet keys;
      protected transient LongCollection values;

      protected UnmodifiableMap(Long2LongMap m) {
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
      public void putAll(Map<? extends Long, ? extends Long> m) {
         throw new UnsupportedOperationException();
      }

      @Override
      public ObjectSet<Long2LongMap.Entry> long2LongEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSets.unmodifiable(this.map.long2LongEntrySet());
         }

         return this.entries;
      }

      @Deprecated
      @Override
      public ObjectSet<Entry<Long, Long>> entrySet() {
         return this.long2LongEntrySet();
      }

      @Override
      public LongSet keySet() {
         if (this.keys == null) {
            this.keys = LongSets.unmodifiable(this.map.keySet());
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
      public long getOrDefault(long key, long defaultValue) {
         return this.map.getOrDefault(key, defaultValue);
      }

      @Override
      public void forEach(BiConsumer<? super Long, ? super Long> action) {
         this.map.forEach(action);
      }

      @Override
      public void replaceAll(BiFunction<? super Long, ? super Long, ? extends Long> function) {
         throw new UnsupportedOperationException();
      }

      @Override
      public long putIfAbsent(long key, long value) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean remove(long key, long value) {
         throw new UnsupportedOperationException();
      }

      @Override
      public long replace(long key, long value) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean replace(long key, long oldValue, long newValue) {
         throw new UnsupportedOperationException();
      }

      @Override
      public long computeIfAbsent(long key, java.util.function.LongUnaryOperator mappingFunction) {
         throw new UnsupportedOperationException();
      }

      @Override
      public long computeIfAbsentNullable(long key, LongFunction<? extends Long> mappingFunction) {
         throw new UnsupportedOperationException();
      }

      @Override
      public long computeIfAbsent(long key, Long2LongFunction mappingFunction) {
         throw new UnsupportedOperationException();
      }

      @Override
      public long computeIfPresent(long key, BiFunction<? super Long, ? super Long, ? extends Long> remappingFunction) {
         throw new UnsupportedOperationException();
      }

      @Override
      public long compute(long key, BiFunction<? super Long, ? super Long, ? extends Long> remappingFunction) {
         throw new UnsupportedOperationException();
      }

      @Override
      public long merge(long key, long value, BiFunction<? super Long, ? super Long, ? extends Long> remappingFunction) {
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
      public Long replace(Long key, Long value) {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      @Override
      public boolean replace(Long key, Long oldValue, Long newValue) {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      @Override
      public Long putIfAbsent(Long key, Long value) {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      @Override
      public Long computeIfAbsent(Long key, Function<? super Long, ? extends Long> mappingFunction) {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      @Override
      public Long computeIfPresent(Long key, BiFunction<? super Long, ? super Long, ? extends Long> remappingFunction) {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      @Override
      public Long compute(Long key, BiFunction<? super Long, ? super Long, ? extends Long> remappingFunction) {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      @Override
      public Long merge(Long key, Long value, BiFunction<? super Long, ? super Long, ? extends Long> remappingFunction) {
         throw new UnsupportedOperationException();
      }
   }
}
