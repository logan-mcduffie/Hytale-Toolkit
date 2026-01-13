package it.unimi.dsi.fastutil.ints;

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
import java.util.function.IntFunction;
import java.util.function.IntToDoubleFunction;

public final class Int2FloatMaps {
   public static final Int2FloatMaps.EmptyMap EMPTY_MAP = new Int2FloatMaps.EmptyMap();

   private Int2FloatMaps() {
   }

   public static ObjectIterator<Int2FloatMap.Entry> fastIterator(Int2FloatMap map) {
      ObjectSet<Int2FloatMap.Entry> entries = map.int2FloatEntrySet();
      return entries instanceof Int2FloatMap.FastEntrySet ? ((Int2FloatMap.FastEntrySet)entries).fastIterator() : entries.iterator();
   }

   public static void fastForEach(Int2FloatMap map, Consumer<? super Int2FloatMap.Entry> consumer) {
      ObjectSet<Int2FloatMap.Entry> entries = map.int2FloatEntrySet();
      if (entries instanceof Int2FloatMap.FastEntrySet) {
         ((Int2FloatMap.FastEntrySet)entries).fastForEach(consumer);
      } else {
         entries.forEach(consumer);
      }
   }

   public static ObjectIterable<Int2FloatMap.Entry> fastIterable(Int2FloatMap map) {
      final ObjectSet<Int2FloatMap.Entry> entries = map.int2FloatEntrySet();
      return (ObjectIterable<Int2FloatMap.Entry>)(entries instanceof Int2FloatMap.FastEntrySet ? new ObjectIterable<Int2FloatMap.Entry>() {
         @Override
         public ObjectIterator<Int2FloatMap.Entry> iterator() {
            return ((Int2FloatMap.FastEntrySet)entries).fastIterator();
         }

         @Override
         public ObjectSpliterator<Int2FloatMap.Entry> spliterator() {
            return entries.spliterator();
         }

         @Override
         public void forEach(Consumer<? super Int2FloatMap.Entry> consumer) {
            ((Int2FloatMap.FastEntrySet)entries).fastForEach(consumer);
         }
      } : entries);
   }

   public static Int2FloatMap singleton(int key, float value) {
      return new Int2FloatMaps.Singleton(key, value);
   }

   public static Int2FloatMap singleton(Integer key, Float value) {
      return new Int2FloatMaps.Singleton(key, value);
   }

   public static Int2FloatMap synchronize(Int2FloatMap m) {
      return new Int2FloatMaps.SynchronizedMap(m);
   }

   public static Int2FloatMap synchronize(Int2FloatMap m, Object sync) {
      return new Int2FloatMaps.SynchronizedMap(m, sync);
   }

   public static Int2FloatMap unmodifiable(Int2FloatMap m) {
      return new Int2FloatMaps.UnmodifiableMap(m);
   }

   public static class EmptyMap extends Int2FloatFunctions.EmptyFunction implements Int2FloatMap, Serializable, Cloneable {
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
      public float getOrDefault(int key, float defaultValue) {
         return defaultValue;
      }

      @Deprecated
      @Override
      public boolean containsValue(Object ov) {
         return false;
      }

      @Override
      public void putAll(Map<? extends Integer, ? extends Float> m) {
         throw new UnsupportedOperationException();
      }

      @Override
      public ObjectSet<Int2FloatMap.Entry> int2FloatEntrySet() {
         return ObjectSets.EMPTY_SET;
      }

      @Override
      public IntSet keySet() {
         return IntSets.EMPTY_SET;
      }

      @Override
      public FloatCollection values() {
         return FloatSets.EMPTY_SET;
      }

      @Override
      public void forEach(BiConsumer<? super Integer, ? super Float> consumer) {
      }

      @Override
      public Object clone() {
         return Int2FloatMaps.EMPTY_MAP;
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

   public static class Singleton extends Int2FloatFunctions.Singleton implements Int2FloatMap, Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected transient ObjectSet<Int2FloatMap.Entry> entries;
      protected transient IntSet keys;
      protected transient FloatCollection values;

      protected Singleton(int key, float value) {
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
      public void putAll(Map<? extends Integer, ? extends Float> m) {
         throw new UnsupportedOperationException();
      }

      @Override
      public ObjectSet<Int2FloatMap.Entry> int2FloatEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSets.singleton(new AbstractInt2FloatMap.BasicEntry(this.key, this.value));
         }

         return this.entries;
      }

      @Deprecated
      @Override
      public ObjectSet<Entry<Integer, Float>> entrySet() {
         return this.int2FloatEntrySet();
      }

      @Override
      public IntSet keySet() {
         if (this.keys == null) {
            this.keys = IntSets.singleton(this.key);
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
         return this.key ^ HashCommon.float2int(this.value);
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

   public static class SynchronizedMap extends Int2FloatFunctions.SynchronizedFunction implements Int2FloatMap, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Int2FloatMap map;
      protected transient ObjectSet<Int2FloatMap.Entry> entries;
      protected transient IntSet keys;
      protected transient FloatCollection values;

      protected SynchronizedMap(Int2FloatMap m, Object sync) {
         super(m, sync);
         this.map = m;
      }

      protected SynchronizedMap(Int2FloatMap m) {
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
      public void putAll(Map<? extends Integer, ? extends Float> m) {
         synchronized (this.sync) {
            this.map.putAll(m);
         }
      }

      @Override
      public ObjectSet<Int2FloatMap.Entry> int2FloatEntrySet() {
         synchronized (this.sync) {
            if (this.entries == null) {
               this.entries = ObjectSets.synchronize(this.map.int2FloatEntrySet(), this.sync);
            }

            return this.entries;
         }
      }

      @Deprecated
      @Override
      public ObjectSet<Entry<Integer, Float>> entrySet() {
         return this.int2FloatEntrySet();
      }

      @Override
      public IntSet keySet() {
         synchronized (this.sync) {
            if (this.keys == null) {
               this.keys = IntSets.synchronize(this.map.keySet(), this.sync);
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
      public float getOrDefault(int key, float defaultValue) {
         synchronized (this.sync) {
            return this.map.getOrDefault(key, defaultValue);
         }
      }

      @Override
      public void forEach(BiConsumer<? super Integer, ? super Float> action) {
         synchronized (this.sync) {
            this.map.forEach(action);
         }
      }

      @Override
      public void replaceAll(BiFunction<? super Integer, ? super Float, ? extends Float> function) {
         synchronized (this.sync) {
            this.map.replaceAll(function);
         }
      }

      @Override
      public float putIfAbsent(int key, float value) {
         synchronized (this.sync) {
            return this.map.putIfAbsent(key, value);
         }
      }

      @Override
      public boolean remove(int key, float value) {
         synchronized (this.sync) {
            return this.map.remove(key, value);
         }
      }

      @Override
      public float replace(int key, float value) {
         synchronized (this.sync) {
            return this.map.replace(key, value);
         }
      }

      @Override
      public boolean replace(int key, float oldValue, float newValue) {
         synchronized (this.sync) {
            return this.map.replace(key, oldValue, newValue);
         }
      }

      @Override
      public float computeIfAbsent(int key, IntToDoubleFunction mappingFunction) {
         synchronized (this.sync) {
            return this.map.computeIfAbsent(key, mappingFunction);
         }
      }

      @Override
      public float computeIfAbsentNullable(int key, IntFunction<? extends Float> mappingFunction) {
         synchronized (this.sync) {
            return this.map.computeIfAbsentNullable(key, mappingFunction);
         }
      }

      @Override
      public float computeIfAbsent(int key, Int2FloatFunction mappingFunction) {
         synchronized (this.sync) {
            return this.map.computeIfAbsent(key, mappingFunction);
         }
      }

      @Override
      public float computeIfPresent(int key, BiFunction<? super Integer, ? super Float, ? extends Float> remappingFunction) {
         synchronized (this.sync) {
            return this.map.computeIfPresent(key, remappingFunction);
         }
      }

      @Override
      public float compute(int key, BiFunction<? super Integer, ? super Float, ? extends Float> remappingFunction) {
         synchronized (this.sync) {
            return this.map.compute(key, remappingFunction);
         }
      }

      @Override
      public float merge(int key, float value, BiFunction<? super Float, ? super Float, ? extends Float> remappingFunction) {
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
      public Float replace(Integer key, Float value) {
         synchronized (this.sync) {
            return this.map.replace(key, value);
         }
      }

      @Deprecated
      @Override
      public boolean replace(Integer key, Float oldValue, Float newValue) {
         synchronized (this.sync) {
            return this.map.replace(key, oldValue, newValue);
         }
      }

      @Deprecated
      @Override
      public Float putIfAbsent(Integer key, Float value) {
         synchronized (this.sync) {
            return this.map.putIfAbsent(key, value);
         }
      }

      @Deprecated
      @Override
      public Float computeIfAbsent(Integer key, Function<? super Integer, ? extends Float> mappingFunction) {
         synchronized (this.sync) {
            return this.map.computeIfAbsent(key, mappingFunction);
         }
      }

      @Deprecated
      @Override
      public Float computeIfPresent(Integer key, BiFunction<? super Integer, ? super Float, ? extends Float> remappingFunction) {
         synchronized (this.sync) {
            return this.map.computeIfPresent(key, remappingFunction);
         }
      }

      @Deprecated
      @Override
      public Float compute(Integer key, BiFunction<? super Integer, ? super Float, ? extends Float> remappingFunction) {
         synchronized (this.sync) {
            return this.map.compute(key, remappingFunction);
         }
      }

      @Deprecated
      @Override
      public Float merge(Integer key, Float value, BiFunction<? super Float, ? super Float, ? extends Float> remappingFunction) {
         synchronized (this.sync) {
            return this.map.merge(key, value, remappingFunction);
         }
      }
   }

   public static class UnmodifiableMap extends Int2FloatFunctions.UnmodifiableFunction implements Int2FloatMap, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Int2FloatMap map;
      protected transient ObjectSet<Int2FloatMap.Entry> entries;
      protected transient IntSet keys;
      protected transient FloatCollection values;

      protected UnmodifiableMap(Int2FloatMap m) {
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
      public void putAll(Map<? extends Integer, ? extends Float> m) {
         throw new UnsupportedOperationException();
      }

      @Override
      public ObjectSet<Int2FloatMap.Entry> int2FloatEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSets.unmodifiable(this.map.int2FloatEntrySet());
         }

         return this.entries;
      }

      @Deprecated
      @Override
      public ObjectSet<Entry<Integer, Float>> entrySet() {
         return this.int2FloatEntrySet();
      }

      @Override
      public IntSet keySet() {
         if (this.keys == null) {
            this.keys = IntSets.unmodifiable(this.map.keySet());
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
      public float getOrDefault(int key, float defaultValue) {
         return this.map.getOrDefault(key, defaultValue);
      }

      @Override
      public void forEach(BiConsumer<? super Integer, ? super Float> action) {
         this.map.forEach(action);
      }

      @Override
      public void replaceAll(BiFunction<? super Integer, ? super Float, ? extends Float> function) {
         throw new UnsupportedOperationException();
      }

      @Override
      public float putIfAbsent(int key, float value) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean remove(int key, float value) {
         throw new UnsupportedOperationException();
      }

      @Override
      public float replace(int key, float value) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean replace(int key, float oldValue, float newValue) {
         throw new UnsupportedOperationException();
      }

      @Override
      public float computeIfAbsent(int key, IntToDoubleFunction mappingFunction) {
         throw new UnsupportedOperationException();
      }

      @Override
      public float computeIfAbsentNullable(int key, IntFunction<? extends Float> mappingFunction) {
         throw new UnsupportedOperationException();
      }

      @Override
      public float computeIfAbsent(int key, Int2FloatFunction mappingFunction) {
         throw new UnsupportedOperationException();
      }

      @Override
      public float computeIfPresent(int key, BiFunction<? super Integer, ? super Float, ? extends Float> remappingFunction) {
         throw new UnsupportedOperationException();
      }

      @Override
      public float compute(int key, BiFunction<? super Integer, ? super Float, ? extends Float> remappingFunction) {
         throw new UnsupportedOperationException();
      }

      @Override
      public float merge(int key, float value, BiFunction<? super Float, ? super Float, ? extends Float> remappingFunction) {
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
      public Float replace(Integer key, Float value) {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      @Override
      public boolean replace(Integer key, Float oldValue, Float newValue) {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      @Override
      public Float putIfAbsent(Integer key, Float value) {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      @Override
      public Float computeIfAbsent(Integer key, Function<? super Integer, ? extends Float> mappingFunction) {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      @Override
      public Float computeIfPresent(Integer key, BiFunction<? super Integer, ? super Float, ? extends Float> remappingFunction) {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      @Override
      public Float compute(Integer key, BiFunction<? super Integer, ? super Float, ? extends Float> remappingFunction) {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      @Override
      public Float merge(Integer key, Float value, BiFunction<? super Float, ? super Float, ? extends Float> remappingFunction) {
         throw new UnsupportedOperationException();
      }
   }
}
