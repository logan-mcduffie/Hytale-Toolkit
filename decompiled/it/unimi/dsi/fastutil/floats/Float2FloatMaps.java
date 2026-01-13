package it.unimi.dsi.fastutil.floats;

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
import java.util.function.DoubleFunction;
import java.util.function.DoubleUnaryOperator;
import java.util.function.Function;

public final class Float2FloatMaps {
   public static final Float2FloatMaps.EmptyMap EMPTY_MAP = new Float2FloatMaps.EmptyMap();

   private Float2FloatMaps() {
   }

   public static ObjectIterator<Float2FloatMap.Entry> fastIterator(Float2FloatMap map) {
      ObjectSet<Float2FloatMap.Entry> entries = map.float2FloatEntrySet();
      return entries instanceof Float2FloatMap.FastEntrySet ? ((Float2FloatMap.FastEntrySet)entries).fastIterator() : entries.iterator();
   }

   public static void fastForEach(Float2FloatMap map, Consumer<? super Float2FloatMap.Entry> consumer) {
      ObjectSet<Float2FloatMap.Entry> entries = map.float2FloatEntrySet();
      if (entries instanceof Float2FloatMap.FastEntrySet) {
         ((Float2FloatMap.FastEntrySet)entries).fastForEach(consumer);
      } else {
         entries.forEach(consumer);
      }
   }

   public static ObjectIterable<Float2FloatMap.Entry> fastIterable(Float2FloatMap map) {
      final ObjectSet<Float2FloatMap.Entry> entries = map.float2FloatEntrySet();
      return (ObjectIterable<Float2FloatMap.Entry>)(entries instanceof Float2FloatMap.FastEntrySet ? new ObjectIterable<Float2FloatMap.Entry>() {
         @Override
         public ObjectIterator<Float2FloatMap.Entry> iterator() {
            return ((Float2FloatMap.FastEntrySet)entries).fastIterator();
         }

         @Override
         public ObjectSpliterator<Float2FloatMap.Entry> spliterator() {
            return entries.spliterator();
         }

         @Override
         public void forEach(Consumer<? super Float2FloatMap.Entry> consumer) {
            ((Float2FloatMap.FastEntrySet)entries).fastForEach(consumer);
         }
      } : entries);
   }

   public static Float2FloatMap singleton(float key, float value) {
      return new Float2FloatMaps.Singleton(key, value);
   }

   public static Float2FloatMap singleton(Float key, Float value) {
      return new Float2FloatMaps.Singleton(key, value);
   }

   public static Float2FloatMap synchronize(Float2FloatMap m) {
      return new Float2FloatMaps.SynchronizedMap(m);
   }

   public static Float2FloatMap synchronize(Float2FloatMap m, Object sync) {
      return new Float2FloatMaps.SynchronizedMap(m, sync);
   }

   public static Float2FloatMap unmodifiable(Float2FloatMap m) {
      return new Float2FloatMaps.UnmodifiableMap(m);
   }

   public static class EmptyMap extends Float2FloatFunctions.EmptyFunction implements Float2FloatMap, Serializable, Cloneable {
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
      public float getOrDefault(float key, float defaultValue) {
         return defaultValue;
      }

      @Deprecated
      @Override
      public boolean containsValue(Object ov) {
         return false;
      }

      @Override
      public void putAll(Map<? extends Float, ? extends Float> m) {
         throw new UnsupportedOperationException();
      }

      @Override
      public ObjectSet<Float2FloatMap.Entry> float2FloatEntrySet() {
         return ObjectSets.EMPTY_SET;
      }

      @Override
      public FloatSet keySet() {
         return FloatSets.EMPTY_SET;
      }

      @Override
      public FloatCollection values() {
         return FloatSets.EMPTY_SET;
      }

      @Override
      public void forEach(BiConsumer<? super Float, ? super Float> consumer) {
      }

      @Override
      public Object clone() {
         return Float2FloatMaps.EMPTY_MAP;
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

   public static class Singleton extends Float2FloatFunctions.Singleton implements Float2FloatMap, Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected transient ObjectSet<Float2FloatMap.Entry> entries;
      protected transient FloatSet keys;
      protected transient FloatCollection values;

      protected Singleton(float key, float value) {
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
      public void putAll(Map<? extends Float, ? extends Float> m) {
         throw new UnsupportedOperationException();
      }

      @Override
      public ObjectSet<Float2FloatMap.Entry> float2FloatEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSets.singleton(new AbstractFloat2FloatMap.BasicEntry(this.key, this.value));
         }

         return this.entries;
      }

      @Deprecated
      @Override
      public ObjectSet<Entry<Float, Float>> entrySet() {
         return this.float2FloatEntrySet();
      }

      @Override
      public FloatSet keySet() {
         if (this.keys == null) {
            this.keys = FloatSets.singleton(this.key);
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
         return HashCommon.float2int(this.key) ^ HashCommon.float2int(this.value);
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

   public static class SynchronizedMap extends Float2FloatFunctions.SynchronizedFunction implements Float2FloatMap, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Float2FloatMap map;
      protected transient ObjectSet<Float2FloatMap.Entry> entries;
      protected transient FloatSet keys;
      protected transient FloatCollection values;

      protected SynchronizedMap(Float2FloatMap m, Object sync) {
         super(m, sync);
         this.map = m;
      }

      protected SynchronizedMap(Float2FloatMap m) {
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
      public void putAll(Map<? extends Float, ? extends Float> m) {
         synchronized (this.sync) {
            this.map.putAll(m);
         }
      }

      @Override
      public ObjectSet<Float2FloatMap.Entry> float2FloatEntrySet() {
         synchronized (this.sync) {
            if (this.entries == null) {
               this.entries = ObjectSets.synchronize(this.map.float2FloatEntrySet(), this.sync);
            }

            return this.entries;
         }
      }

      @Deprecated
      @Override
      public ObjectSet<Entry<Float, Float>> entrySet() {
         return this.float2FloatEntrySet();
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
      public float getOrDefault(float key, float defaultValue) {
         synchronized (this.sync) {
            return this.map.getOrDefault(key, defaultValue);
         }
      }

      @Override
      public void forEach(BiConsumer<? super Float, ? super Float> action) {
         synchronized (this.sync) {
            this.map.forEach(action);
         }
      }

      @Override
      public void replaceAll(BiFunction<? super Float, ? super Float, ? extends Float> function) {
         synchronized (this.sync) {
            this.map.replaceAll(function);
         }
      }

      @Override
      public float putIfAbsent(float key, float value) {
         synchronized (this.sync) {
            return this.map.putIfAbsent(key, value);
         }
      }

      @Override
      public boolean remove(float key, float value) {
         synchronized (this.sync) {
            return this.map.remove(key, value);
         }
      }

      @Override
      public float replace(float key, float value) {
         synchronized (this.sync) {
            return this.map.replace(key, value);
         }
      }

      @Override
      public boolean replace(float key, float oldValue, float newValue) {
         synchronized (this.sync) {
            return this.map.replace(key, oldValue, newValue);
         }
      }

      @Override
      public float computeIfAbsent(float key, DoubleUnaryOperator mappingFunction) {
         synchronized (this.sync) {
            return this.map.computeIfAbsent(key, mappingFunction);
         }
      }

      @Override
      public float computeIfAbsentNullable(float key, DoubleFunction<? extends Float> mappingFunction) {
         synchronized (this.sync) {
            return this.map.computeIfAbsentNullable(key, mappingFunction);
         }
      }

      @Override
      public float computeIfAbsent(float key, Float2FloatFunction mappingFunction) {
         synchronized (this.sync) {
            return this.map.computeIfAbsent(key, mappingFunction);
         }
      }

      @Override
      public float computeIfPresent(float key, BiFunction<? super Float, ? super Float, ? extends Float> remappingFunction) {
         synchronized (this.sync) {
            return this.map.computeIfPresent(key, remappingFunction);
         }
      }

      @Override
      public float compute(float key, BiFunction<? super Float, ? super Float, ? extends Float> remappingFunction) {
         synchronized (this.sync) {
            return this.map.compute(key, remappingFunction);
         }
      }

      @Override
      public float merge(float key, float value, BiFunction<? super Float, ? super Float, ? extends Float> remappingFunction) {
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
      public Float replace(Float key, Float value) {
         synchronized (this.sync) {
            return this.map.replace(key, value);
         }
      }

      @Deprecated
      @Override
      public boolean replace(Float key, Float oldValue, Float newValue) {
         synchronized (this.sync) {
            return this.map.replace(key, oldValue, newValue);
         }
      }

      @Deprecated
      @Override
      public Float putIfAbsent(Float key, Float value) {
         synchronized (this.sync) {
            return this.map.putIfAbsent(key, value);
         }
      }

      @Deprecated
      @Override
      public Float computeIfAbsent(Float key, Function<? super Float, ? extends Float> mappingFunction) {
         synchronized (this.sync) {
            return this.map.computeIfAbsent(key, mappingFunction);
         }
      }

      @Deprecated
      @Override
      public Float computeIfPresent(Float key, BiFunction<? super Float, ? super Float, ? extends Float> remappingFunction) {
         synchronized (this.sync) {
            return this.map.computeIfPresent(key, remappingFunction);
         }
      }

      @Deprecated
      @Override
      public Float compute(Float key, BiFunction<? super Float, ? super Float, ? extends Float> remappingFunction) {
         synchronized (this.sync) {
            return this.map.compute(key, remappingFunction);
         }
      }

      @Deprecated
      @Override
      public Float merge(Float key, Float value, BiFunction<? super Float, ? super Float, ? extends Float> remappingFunction) {
         synchronized (this.sync) {
            return this.map.merge(key, value, remappingFunction);
         }
      }
   }

   public static class UnmodifiableMap extends Float2FloatFunctions.UnmodifiableFunction implements Float2FloatMap, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Float2FloatMap map;
      protected transient ObjectSet<Float2FloatMap.Entry> entries;
      protected transient FloatSet keys;
      protected transient FloatCollection values;

      protected UnmodifiableMap(Float2FloatMap m) {
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
      public void putAll(Map<? extends Float, ? extends Float> m) {
         throw new UnsupportedOperationException();
      }

      @Override
      public ObjectSet<Float2FloatMap.Entry> float2FloatEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSets.unmodifiable(this.map.float2FloatEntrySet());
         }

         return this.entries;
      }

      @Deprecated
      @Override
      public ObjectSet<Entry<Float, Float>> entrySet() {
         return this.float2FloatEntrySet();
      }

      @Override
      public FloatSet keySet() {
         if (this.keys == null) {
            this.keys = FloatSets.unmodifiable(this.map.keySet());
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
      public float getOrDefault(float key, float defaultValue) {
         return this.map.getOrDefault(key, defaultValue);
      }

      @Override
      public void forEach(BiConsumer<? super Float, ? super Float> action) {
         this.map.forEach(action);
      }

      @Override
      public void replaceAll(BiFunction<? super Float, ? super Float, ? extends Float> function) {
         throw new UnsupportedOperationException();
      }

      @Override
      public float putIfAbsent(float key, float value) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean remove(float key, float value) {
         throw new UnsupportedOperationException();
      }

      @Override
      public float replace(float key, float value) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean replace(float key, float oldValue, float newValue) {
         throw new UnsupportedOperationException();
      }

      @Override
      public float computeIfAbsent(float key, DoubleUnaryOperator mappingFunction) {
         throw new UnsupportedOperationException();
      }

      @Override
      public float computeIfAbsentNullable(float key, DoubleFunction<? extends Float> mappingFunction) {
         throw new UnsupportedOperationException();
      }

      @Override
      public float computeIfAbsent(float key, Float2FloatFunction mappingFunction) {
         throw new UnsupportedOperationException();
      }

      @Override
      public float computeIfPresent(float key, BiFunction<? super Float, ? super Float, ? extends Float> remappingFunction) {
         throw new UnsupportedOperationException();
      }

      @Override
      public float compute(float key, BiFunction<? super Float, ? super Float, ? extends Float> remappingFunction) {
         throw new UnsupportedOperationException();
      }

      @Override
      public float merge(float key, float value, BiFunction<? super Float, ? super Float, ? extends Float> remappingFunction) {
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
      public Float replace(Float key, Float value) {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      @Override
      public boolean replace(Float key, Float oldValue, Float newValue) {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      @Override
      public Float putIfAbsent(Float key, Float value) {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      @Override
      public Float computeIfAbsent(Float key, Function<? super Float, ? extends Float> mappingFunction) {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      @Override
      public Float computeIfPresent(Float key, BiFunction<? super Float, ? super Float, ? extends Float> remappingFunction) {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      @Override
      public Float compute(Float key, BiFunction<? super Float, ? super Float, ? extends Float> remappingFunction) {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      @Override
      public Float merge(Float key, Float value, BiFunction<? super Float, ? super Float, ? extends Float> remappingFunction) {
         throw new UnsupportedOperationException();
      }
   }
}
