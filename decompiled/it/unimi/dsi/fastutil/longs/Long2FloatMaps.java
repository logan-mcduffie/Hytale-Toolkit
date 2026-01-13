package it.unimi.dsi.fastutil.longs;

import it.unimi.dsi.fastutil.HashCommon;
import it.unimi.dsi.fastutil.floats.FloatCollection;
import it.unimi.dsi.fastutil.floats.FloatCollections;
import it.unimi.dsi.fastutil.floats.FloatSets;
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
import java.util.function.LongToDoubleFunction;

public final class Long2FloatMaps {
   public static final Long2FloatMaps.EmptyMap EMPTY_MAP = new Long2FloatMaps.EmptyMap();

   private Long2FloatMaps() {
   }

   public static ObjectIterator<Long2FloatMap.Entry> fastIterator(Long2FloatMap map) {
      ObjectSet<Long2FloatMap.Entry> entries = map.long2FloatEntrySet();
      return entries instanceof Long2FloatMap.FastEntrySet ? ((Long2FloatMap.FastEntrySet)entries).fastIterator() : entries.iterator();
   }

   public static void fastForEach(Long2FloatMap map, Consumer<? super Long2FloatMap.Entry> consumer) {
      ObjectSet<Long2FloatMap.Entry> entries = map.long2FloatEntrySet();
      if (entries instanceof Long2FloatMap.FastEntrySet) {
         ((Long2FloatMap.FastEntrySet)entries).fastForEach(consumer);
      } else {
         entries.forEach(consumer);
      }
   }

   public static ObjectIterable<Long2FloatMap.Entry> fastIterable(Long2FloatMap map) {
      final ObjectSet<Long2FloatMap.Entry> entries = map.long2FloatEntrySet();
      return (ObjectIterable<Long2FloatMap.Entry>)(entries instanceof Long2FloatMap.FastEntrySet ? new ObjectIterable<Long2FloatMap.Entry>() {
         @Override
         public ObjectIterator<Long2FloatMap.Entry> iterator() {
            return ((Long2FloatMap.FastEntrySet)entries).fastIterator();
         }

         @Override
         public ObjectSpliterator<Long2FloatMap.Entry> spliterator() {
            return entries.spliterator();
         }

         @Override
         public void forEach(Consumer<? super Long2FloatMap.Entry> consumer) {
            ((Long2FloatMap.FastEntrySet)entries).fastForEach(consumer);
         }
      } : entries);
   }

   public static Long2FloatMap singleton(long key, float value) {
      return new Long2FloatMaps.Singleton(key, value);
   }

   public static Long2FloatMap singleton(Long key, Float value) {
      return new Long2FloatMaps.Singleton(key, value);
   }

   public static Long2FloatMap synchronize(Long2FloatMap m) {
      return new Long2FloatMaps.SynchronizedMap(m);
   }

   public static Long2FloatMap synchronize(Long2FloatMap m, Object sync) {
      return new Long2FloatMaps.SynchronizedMap(m, sync);
   }

   public static Long2FloatMap unmodifiable(Long2FloatMap m) {
      return new Long2FloatMaps.UnmodifiableMap(m);
   }

   public static class EmptyMap extends Long2FloatFunctions.EmptyFunction implements Long2FloatMap, Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;

      protected EmptyMap() {
      }

      @Override
      public boolean containsValue(float v) {
         return false;
      }

      @Deprecated
      @Override
      public Float getOrDefault(Object key, Float defaultValue) {
         return defaultValue;
      }

      @Override
      public float getOrDefault(long key, float defaultValue) {
         return defaultValue;
      }

      @Deprecated
      @Override
      public boolean containsValue(Object ov) {
         return false;
      }

      @Override
      public void putAll(Map<? extends Long, ? extends Float> m) {
         throw new UnsupportedOperationException();
      }

      @Override
      public ObjectSet<Long2FloatMap.Entry> long2FloatEntrySet() {
         return ObjectSets.EMPTY_SET;
      }

      @Override
      public LongSet keySet() {
         return LongSets.EMPTY_SET;
      }

      @Override
      public FloatCollection values() {
         return FloatSets.EMPTY_SET;
      }

      @Override
      public void forEach(BiConsumer<? super Long, ? super Float> consumer) {
      }

      @Override
      public Object clone() {
         return Long2FloatMaps.EMPTY_MAP;
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

   public static class Singleton extends Long2FloatFunctions.Singleton implements Long2FloatMap, Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected transient ObjectSet<Long2FloatMap.Entry> entries;
      protected transient LongSet keys;
      protected transient FloatCollection values;

      protected Singleton(long key, float value) {
         super(key, value);
      }

      @Override
      public boolean containsValue(float v) {
         return Float.floatToIntBits(this.value) == Float.floatToIntBits(v);
      }

      @Deprecated
      @Override
      public boolean containsValue(Object ov) {
         return Float.floatToIntBits((Float)ov) == Float.floatToIntBits(this.value);
      }

      @Override
      public void putAll(Map<? extends Long, ? extends Float> m) {
         throw new UnsupportedOperationException();
      }

      @Override
      public ObjectSet<Long2FloatMap.Entry> long2FloatEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSets.singleton(new AbstractLong2FloatMap.BasicEntry(this.key, this.value));
         }

         return this.entries;
      }

      @Deprecated
      @Override
      public ObjectSet<Entry<Long, Float>> entrySet() {
         return this.long2FloatEntrySet();
      }

      @Override
      public LongSet keySet() {
         if (this.keys == null) {
            this.keys = LongSets.singleton(this.key);
         }

         return this.keys;
      }

      @Override
      public FloatCollection values() {
         if (this.values == null) {
            this.values = FloatSets.singleton(this.value);
         }

         return this.values;
      }

      @Override
      public boolean isEmpty() {
         return false;
      }

      @Override
      public int hashCode() {
         return HashCommon.long2int(this.key) ^ HashCommon.float2int(this.value);
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

   public static class SynchronizedMap extends Long2FloatFunctions.SynchronizedFunction implements Long2FloatMap, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Long2FloatMap map;
      protected transient ObjectSet<Long2FloatMap.Entry> entries;
      protected transient LongSet keys;
      protected transient FloatCollection values;

      protected SynchronizedMap(Long2FloatMap m, Object sync) {
         super(m, sync);
         this.map = m;
      }

      protected SynchronizedMap(Long2FloatMap m) {
         super(m);
         this.map = m;
      }

      @Override
      public boolean containsValue(float v) {
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
      public void putAll(Map<? extends Long, ? extends Float> m) {
         synchronized (this.sync) {
            this.map.putAll(m);
         }
      }

      @Override
      public ObjectSet<Long2FloatMap.Entry> long2FloatEntrySet() {
         synchronized (this.sync) {
            if (this.entries == null) {
               this.entries = ObjectSets.synchronize(this.map.long2FloatEntrySet(), this.sync);
            }

            return this.entries;
         }
      }

      @Deprecated
      @Override
      public ObjectSet<Entry<Long, Float>> entrySet() {
         return this.long2FloatEntrySet();
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
      public FloatCollection values() {
         synchronized (this.sync) {
            if (this.values == null) {
               this.values = FloatCollections.synchronize(this.map.values(), this.sync);
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
      public float getOrDefault(long key, float defaultValue) {
         synchronized (this.sync) {
            return this.map.getOrDefault(key, defaultValue);
         }
      }

      @Override
      public void forEach(BiConsumer<? super Long, ? super Float> action) {
         synchronized (this.sync) {
            this.map.forEach(action);
         }
      }

      @Override
      public void replaceAll(BiFunction<? super Long, ? super Float, ? extends Float> function) {
         synchronized (this.sync) {
            this.map.replaceAll(function);
         }
      }

      @Override
      public float putIfAbsent(long key, float value) {
         synchronized (this.sync) {
            return this.map.putIfAbsent(key, value);
         }
      }

      @Override
      public boolean remove(long key, float value) {
         synchronized (this.sync) {
            return this.map.remove(key, value);
         }
      }

      @Override
      public float replace(long key, float value) {
         synchronized (this.sync) {
            return this.map.replace(key, value);
         }
      }

      @Override
      public boolean replace(long key, float oldValue, float newValue) {
         synchronized (this.sync) {
            return this.map.replace(key, oldValue, newValue);
         }
      }

      @Override
      public float computeIfAbsent(long key, LongToDoubleFunction mappingFunction) {
         synchronized (this.sync) {
            return this.map.computeIfAbsent(key, mappingFunction);
         }
      }

      @Override
      public float computeIfAbsentNullable(long key, LongFunction<? extends Float> mappingFunction) {
         synchronized (this.sync) {
            return this.map.computeIfAbsentNullable(key, mappingFunction);
         }
      }

      @Override
      public float computeIfAbsent(long key, Long2FloatFunction mappingFunction) {
         synchronized (this.sync) {
            return this.map.computeIfAbsent(key, mappingFunction);
         }
      }

      @Override
      public float computeIfPresent(long key, BiFunction<? super Long, ? super Float, ? extends Float> remappingFunction) {
         synchronized (this.sync) {
            return this.map.computeIfPresent(key, remappingFunction);
         }
      }

      @Override
      public float compute(long key, BiFunction<? super Long, ? super Float, ? extends Float> remappingFunction) {
         synchronized (this.sync) {
            return this.map.compute(key, remappingFunction);
         }
      }

      @Override
      public float merge(long key, float value, BiFunction<? super Float, ? super Float, ? extends Float> remappingFunction) {
         synchronized (this.sync) {
            return this.map.merge(key, value, remappingFunction);
         }
      }

      @Deprecated
      @Override
      public Float getOrDefault(Object key, Float defaultValue) {
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
      public Float replace(Long key, Float value) {
         synchronized (this.sync) {
            return this.map.replace(key, value);
         }
      }

      @Deprecated
      @Override
      public boolean replace(Long key, Float oldValue, Float newValue) {
         synchronized (this.sync) {
            return this.map.replace(key, oldValue, newValue);
         }
      }

      @Deprecated
      @Override
      public Float putIfAbsent(Long key, Float value) {
         synchronized (this.sync) {
            return this.map.putIfAbsent(key, value);
         }
      }

      @Deprecated
      @Override
      public Float computeIfAbsent(Long key, Function<? super Long, ? extends Float> mappingFunction) {
         synchronized (this.sync) {
            return this.map.computeIfAbsent(key, mappingFunction);
         }
      }

      @Deprecated
      @Override
      public Float computeIfPresent(Long key, BiFunction<? super Long, ? super Float, ? extends Float> remappingFunction) {
         synchronized (this.sync) {
            return this.map.computeIfPresent(key, remappingFunction);
         }
      }

      @Deprecated
      @Override
      public Float compute(Long key, BiFunction<? super Long, ? super Float, ? extends Float> remappingFunction) {
         synchronized (this.sync) {
            return this.map.compute(key, remappingFunction);
         }
      }

      @Deprecated
      @Override
      public Float merge(Long key, Float value, BiFunction<? super Float, ? super Float, ? extends Float> remappingFunction) {
         synchronized (this.sync) {
            return this.map.merge(key, value, remappingFunction);
         }
      }
   }

   public static class UnmodifiableMap extends Long2FloatFunctions.UnmodifiableFunction implements Long2FloatMap, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Long2FloatMap map;
      protected transient ObjectSet<Long2FloatMap.Entry> entries;
      protected transient LongSet keys;
      protected transient FloatCollection values;

      protected UnmodifiableMap(Long2FloatMap m) {
         super(m);
         this.map = m;
      }

      @Override
      public boolean containsValue(float v) {
         return this.map.containsValue(v);
      }

      @Deprecated
      @Override
      public boolean containsValue(Object ov) {
         return this.map.containsValue(ov);
      }

      @Override
      public void putAll(Map<? extends Long, ? extends Float> m) {
         throw new UnsupportedOperationException();
      }

      @Override
      public ObjectSet<Long2FloatMap.Entry> long2FloatEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSets.unmodifiable(this.map.long2FloatEntrySet());
         }

         return this.entries;
      }

      @Deprecated
      @Override
      public ObjectSet<Entry<Long, Float>> entrySet() {
         return this.long2FloatEntrySet();
      }

      @Override
      public LongSet keySet() {
         if (this.keys == null) {
            this.keys = LongSets.unmodifiable(this.map.keySet());
         }

         return this.keys;
      }

      @Override
      public FloatCollection values() {
         if (this.values == null) {
            this.values = FloatCollections.unmodifiable(this.map.values());
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
      public float getOrDefault(long key, float defaultValue) {
         return this.map.getOrDefault(key, defaultValue);
      }

      @Override
      public void forEach(BiConsumer<? super Long, ? super Float> action) {
         this.map.forEach(action);
      }

      @Override
      public void replaceAll(BiFunction<? super Long, ? super Float, ? extends Float> function) {
         throw new UnsupportedOperationException();
      }

      @Override
      public float putIfAbsent(long key, float value) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean remove(long key, float value) {
         throw new UnsupportedOperationException();
      }

      @Override
      public float replace(long key, float value) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean replace(long key, float oldValue, float newValue) {
         throw new UnsupportedOperationException();
      }

      @Override
      public float computeIfAbsent(long key, LongToDoubleFunction mappingFunction) {
         throw new UnsupportedOperationException();
      }

      @Override
      public float computeIfAbsentNullable(long key, LongFunction<? extends Float> mappingFunction) {
         throw new UnsupportedOperationException();
      }

      @Override
      public float computeIfAbsent(long key, Long2FloatFunction mappingFunction) {
         throw new UnsupportedOperationException();
      }

      @Override
      public float computeIfPresent(long key, BiFunction<? super Long, ? super Float, ? extends Float> remappingFunction) {
         throw new UnsupportedOperationException();
      }

      @Override
      public float compute(long key, BiFunction<? super Long, ? super Float, ? extends Float> remappingFunction) {
         throw new UnsupportedOperationException();
      }

      @Override
      public float merge(long key, float value, BiFunction<? super Float, ? super Float, ? extends Float> remappingFunction) {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      @Override
      public Float getOrDefault(Object key, Float defaultValue) {
         return this.map.getOrDefault(key, defaultValue);
      }

      @Deprecated
      @Override
      public boolean remove(Object key, Object value) {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      @Override
      public Float replace(Long key, Float value) {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      @Override
      public boolean replace(Long key, Float oldValue, Float newValue) {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      @Override
      public Float putIfAbsent(Long key, Float value) {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      @Override
      public Float computeIfAbsent(Long key, Function<? super Long, ? extends Float> mappingFunction) {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      @Override
      public Float computeIfPresent(Long key, BiFunction<? super Long, ? super Float, ? extends Float> remappingFunction) {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      @Override
      public Float compute(Long key, BiFunction<? super Long, ? super Float, ? extends Float> remappingFunction) {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      @Override
      public Float merge(Long key, Float value, BiFunction<? super Float, ? super Float, ? extends Float> remappingFunction) {
         throw new UnsupportedOperationException();
      }
   }
}
