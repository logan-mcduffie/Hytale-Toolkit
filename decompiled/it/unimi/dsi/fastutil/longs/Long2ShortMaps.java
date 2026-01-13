package it.unimi.dsi.fastutil.longs;

import it.unimi.dsi.fastutil.HashCommon;
import it.unimi.dsi.fastutil.objects.ObjectIterable;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import it.unimi.dsi.fastutil.objects.ObjectSets;
import it.unimi.dsi.fastutil.objects.ObjectSpliterator;
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
import java.util.function.LongFunction;
import java.util.function.LongToIntFunction;

public final class Long2ShortMaps {
   public static final Long2ShortMaps.EmptyMap EMPTY_MAP = new Long2ShortMaps.EmptyMap();

   private Long2ShortMaps() {
   }

   public static ObjectIterator<Long2ShortMap.Entry> fastIterator(Long2ShortMap map) {
      ObjectSet<Long2ShortMap.Entry> entries = map.long2ShortEntrySet();
      return entries instanceof Long2ShortMap.FastEntrySet ? ((Long2ShortMap.FastEntrySet)entries).fastIterator() : entries.iterator();
   }

   public static void fastForEach(Long2ShortMap map, Consumer<? super Long2ShortMap.Entry> consumer) {
      ObjectSet<Long2ShortMap.Entry> entries = map.long2ShortEntrySet();
      if (entries instanceof Long2ShortMap.FastEntrySet) {
         ((Long2ShortMap.FastEntrySet)entries).fastForEach(consumer);
      } else {
         entries.forEach(consumer);
      }
   }

   public static ObjectIterable<Long2ShortMap.Entry> fastIterable(Long2ShortMap map) {
      final ObjectSet<Long2ShortMap.Entry> entries = map.long2ShortEntrySet();
      return (ObjectIterable<Long2ShortMap.Entry>)(entries instanceof Long2ShortMap.FastEntrySet ? new ObjectIterable<Long2ShortMap.Entry>() {
         @Override
         public ObjectIterator<Long2ShortMap.Entry> iterator() {
            return ((Long2ShortMap.FastEntrySet)entries).fastIterator();
         }

         @Override
         public ObjectSpliterator<Long2ShortMap.Entry> spliterator() {
            return entries.spliterator();
         }

         @Override
         public void forEach(Consumer<? super Long2ShortMap.Entry> consumer) {
            ((Long2ShortMap.FastEntrySet)entries).fastForEach(consumer);
         }
      } : entries);
   }

   public static Long2ShortMap singleton(long key, short value) {
      return new Long2ShortMaps.Singleton(key, value);
   }

   public static Long2ShortMap singleton(Long key, Short value) {
      return new Long2ShortMaps.Singleton(key, value);
   }

   public static Long2ShortMap synchronize(Long2ShortMap m) {
      return new Long2ShortMaps.SynchronizedMap(m);
   }

   public static Long2ShortMap synchronize(Long2ShortMap m, Object sync) {
      return new Long2ShortMaps.SynchronizedMap(m, sync);
   }

   public static Long2ShortMap unmodifiable(Long2ShortMap m) {
      return new Long2ShortMaps.UnmodifiableMap(m);
   }

   public static class EmptyMap extends Long2ShortFunctions.EmptyFunction implements Long2ShortMap, Serializable, Cloneable {
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
      public short getOrDefault(long key, short defaultValue) {
         return defaultValue;
      }

      @Deprecated
      @Override
      public boolean containsValue(Object ov) {
         return false;
      }

      @Override
      public void putAll(Map<? extends Long, ? extends Short> m) {
         throw new UnsupportedOperationException();
      }

      @Override
      public ObjectSet<Long2ShortMap.Entry> long2ShortEntrySet() {
         return ObjectSets.EMPTY_SET;
      }

      @Override
      public LongSet keySet() {
         return LongSets.EMPTY_SET;
      }

      @Override
      public ShortCollection values() {
         return ShortSets.EMPTY_SET;
      }

      @Override
      public void forEach(BiConsumer<? super Long, ? super Short> consumer) {
      }

      @Override
      public Object clone() {
         return Long2ShortMaps.EMPTY_MAP;
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

   public static class Singleton extends Long2ShortFunctions.Singleton implements Long2ShortMap, Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected transient ObjectSet<Long2ShortMap.Entry> entries;
      protected transient LongSet keys;
      protected transient ShortCollection values;

      protected Singleton(long key, short value) {
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
      public void putAll(Map<? extends Long, ? extends Short> m) {
         throw new UnsupportedOperationException();
      }

      @Override
      public ObjectSet<Long2ShortMap.Entry> long2ShortEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSets.singleton(new AbstractLong2ShortMap.BasicEntry(this.key, this.value));
         }

         return this.entries;
      }

      @Deprecated
      @Override
      public ObjectSet<Entry<Long, Short>> entrySet() {
         return this.long2ShortEntrySet();
      }

      @Override
      public LongSet keySet() {
         if (this.keys == null) {
            this.keys = LongSets.singleton(this.key);
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
         return HashCommon.long2int(this.key) ^ this.value;
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

   public static class SynchronizedMap extends Long2ShortFunctions.SynchronizedFunction implements Long2ShortMap, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Long2ShortMap map;
      protected transient ObjectSet<Long2ShortMap.Entry> entries;
      protected transient LongSet keys;
      protected transient ShortCollection values;

      protected SynchronizedMap(Long2ShortMap m, Object sync) {
         super(m, sync);
         this.map = m;
      }

      protected SynchronizedMap(Long2ShortMap m) {
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
      public void putAll(Map<? extends Long, ? extends Short> m) {
         synchronized (this.sync) {
            this.map.putAll(m);
         }
      }

      @Override
      public ObjectSet<Long2ShortMap.Entry> long2ShortEntrySet() {
         synchronized (this.sync) {
            if (this.entries == null) {
               this.entries = ObjectSets.synchronize(this.map.long2ShortEntrySet(), this.sync);
            }

            return this.entries;
         }
      }

      @Deprecated
      @Override
      public ObjectSet<Entry<Long, Short>> entrySet() {
         return this.long2ShortEntrySet();
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
      public short getOrDefault(long key, short defaultValue) {
         synchronized (this.sync) {
            return this.map.getOrDefault(key, defaultValue);
         }
      }

      @Override
      public void forEach(BiConsumer<? super Long, ? super Short> action) {
         synchronized (this.sync) {
            this.map.forEach(action);
         }
      }

      @Override
      public void replaceAll(BiFunction<? super Long, ? super Short, ? extends Short> function) {
         synchronized (this.sync) {
            this.map.replaceAll(function);
         }
      }

      @Override
      public short putIfAbsent(long key, short value) {
         synchronized (this.sync) {
            return this.map.putIfAbsent(key, value);
         }
      }

      @Override
      public boolean remove(long key, short value) {
         synchronized (this.sync) {
            return this.map.remove(key, value);
         }
      }

      @Override
      public short replace(long key, short value) {
         synchronized (this.sync) {
            return this.map.replace(key, value);
         }
      }

      @Override
      public boolean replace(long key, short oldValue, short newValue) {
         synchronized (this.sync) {
            return this.map.replace(key, oldValue, newValue);
         }
      }

      @Override
      public short computeIfAbsent(long key, LongToIntFunction mappingFunction) {
         synchronized (this.sync) {
            return this.map.computeIfAbsent(key, mappingFunction);
         }
      }

      @Override
      public short computeIfAbsentNullable(long key, LongFunction<? extends Short> mappingFunction) {
         synchronized (this.sync) {
            return this.map.computeIfAbsentNullable(key, mappingFunction);
         }
      }

      @Override
      public short computeIfAbsent(long key, Long2ShortFunction mappingFunction) {
         synchronized (this.sync) {
            return this.map.computeIfAbsent(key, mappingFunction);
         }
      }

      @Override
      public short computeIfPresent(long key, BiFunction<? super Long, ? super Short, ? extends Short> remappingFunction) {
         synchronized (this.sync) {
            return this.map.computeIfPresent(key, remappingFunction);
         }
      }

      @Override
      public short compute(long key, BiFunction<? super Long, ? super Short, ? extends Short> remappingFunction) {
         synchronized (this.sync) {
            return this.map.compute(key, remappingFunction);
         }
      }

      @Override
      public short merge(long key, short value, BiFunction<? super Short, ? super Short, ? extends Short> remappingFunction) {
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
      public Short replace(Long key, Short value) {
         synchronized (this.sync) {
            return this.map.replace(key, value);
         }
      }

      @Deprecated
      @Override
      public boolean replace(Long key, Short oldValue, Short newValue) {
         synchronized (this.sync) {
            return this.map.replace(key, oldValue, newValue);
         }
      }

      @Deprecated
      @Override
      public Short putIfAbsent(Long key, Short value) {
         synchronized (this.sync) {
            return this.map.putIfAbsent(key, value);
         }
      }

      @Deprecated
      @Override
      public Short computeIfAbsent(Long key, Function<? super Long, ? extends Short> mappingFunction) {
         synchronized (this.sync) {
            return this.map.computeIfAbsent(key, mappingFunction);
         }
      }

      @Deprecated
      @Override
      public Short computeIfPresent(Long key, BiFunction<? super Long, ? super Short, ? extends Short> remappingFunction) {
         synchronized (this.sync) {
            return this.map.computeIfPresent(key, remappingFunction);
         }
      }

      @Deprecated
      @Override
      public Short compute(Long key, BiFunction<? super Long, ? super Short, ? extends Short> remappingFunction) {
         synchronized (this.sync) {
            return this.map.compute(key, remappingFunction);
         }
      }

      @Deprecated
      @Override
      public Short merge(Long key, Short value, BiFunction<? super Short, ? super Short, ? extends Short> remappingFunction) {
         synchronized (this.sync) {
            return this.map.merge(key, value, remappingFunction);
         }
      }
   }

   public static class UnmodifiableMap extends Long2ShortFunctions.UnmodifiableFunction implements Long2ShortMap, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Long2ShortMap map;
      protected transient ObjectSet<Long2ShortMap.Entry> entries;
      protected transient LongSet keys;
      protected transient ShortCollection values;

      protected UnmodifiableMap(Long2ShortMap m) {
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
      public void putAll(Map<? extends Long, ? extends Short> m) {
         throw new UnsupportedOperationException();
      }

      @Override
      public ObjectSet<Long2ShortMap.Entry> long2ShortEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSets.unmodifiable(this.map.long2ShortEntrySet());
         }

         return this.entries;
      }

      @Deprecated
      @Override
      public ObjectSet<Entry<Long, Short>> entrySet() {
         return this.long2ShortEntrySet();
      }

      @Override
      public LongSet keySet() {
         if (this.keys == null) {
            this.keys = LongSets.unmodifiable(this.map.keySet());
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
      public short getOrDefault(long key, short defaultValue) {
         return this.map.getOrDefault(key, defaultValue);
      }

      @Override
      public void forEach(BiConsumer<? super Long, ? super Short> action) {
         this.map.forEach(action);
      }

      @Override
      public void replaceAll(BiFunction<? super Long, ? super Short, ? extends Short> function) {
         throw new UnsupportedOperationException();
      }

      @Override
      public short putIfAbsent(long key, short value) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean remove(long key, short value) {
         throw new UnsupportedOperationException();
      }

      @Override
      public short replace(long key, short value) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean replace(long key, short oldValue, short newValue) {
         throw new UnsupportedOperationException();
      }

      @Override
      public short computeIfAbsent(long key, LongToIntFunction mappingFunction) {
         throw new UnsupportedOperationException();
      }

      @Override
      public short computeIfAbsentNullable(long key, LongFunction<? extends Short> mappingFunction) {
         throw new UnsupportedOperationException();
      }

      @Override
      public short computeIfAbsent(long key, Long2ShortFunction mappingFunction) {
         throw new UnsupportedOperationException();
      }

      @Override
      public short computeIfPresent(long key, BiFunction<? super Long, ? super Short, ? extends Short> remappingFunction) {
         throw new UnsupportedOperationException();
      }

      @Override
      public short compute(long key, BiFunction<? super Long, ? super Short, ? extends Short> remappingFunction) {
         throw new UnsupportedOperationException();
      }

      @Override
      public short merge(long key, short value, BiFunction<? super Short, ? super Short, ? extends Short> remappingFunction) {
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
      public Short replace(Long key, Short value) {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      @Override
      public boolean replace(Long key, Short oldValue, Short newValue) {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      @Override
      public Short putIfAbsent(Long key, Short value) {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      @Override
      public Short computeIfAbsent(Long key, Function<? super Long, ? extends Short> mappingFunction) {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      @Override
      public Short computeIfPresent(Long key, BiFunction<? super Long, ? super Short, ? extends Short> remappingFunction) {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      @Override
      public Short compute(Long key, BiFunction<? super Long, ? super Short, ? extends Short> remappingFunction) {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      @Override
      public Short merge(Long key, Short value, BiFunction<? super Short, ? super Short, ? extends Short> remappingFunction) {
         throw new UnsupportedOperationException();
      }
   }
}
