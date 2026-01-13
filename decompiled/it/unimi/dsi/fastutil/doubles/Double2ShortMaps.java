package it.unimi.dsi.fastutil.doubles;

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
import java.util.function.DoubleFunction;
import java.util.function.DoubleToIntFunction;
import java.util.function.Function;

public final class Double2ShortMaps {
   public static final Double2ShortMaps.EmptyMap EMPTY_MAP = new Double2ShortMaps.EmptyMap();

   private Double2ShortMaps() {
   }

   public static ObjectIterator<Double2ShortMap.Entry> fastIterator(Double2ShortMap map) {
      ObjectSet<Double2ShortMap.Entry> entries = map.double2ShortEntrySet();
      return entries instanceof Double2ShortMap.FastEntrySet ? ((Double2ShortMap.FastEntrySet)entries).fastIterator() : entries.iterator();
   }

   public static void fastForEach(Double2ShortMap map, Consumer<? super Double2ShortMap.Entry> consumer) {
      ObjectSet<Double2ShortMap.Entry> entries = map.double2ShortEntrySet();
      if (entries instanceof Double2ShortMap.FastEntrySet) {
         ((Double2ShortMap.FastEntrySet)entries).fastForEach(consumer);
      } else {
         entries.forEach(consumer);
      }
   }

   public static ObjectIterable<Double2ShortMap.Entry> fastIterable(Double2ShortMap map) {
      final ObjectSet<Double2ShortMap.Entry> entries = map.double2ShortEntrySet();
      return (ObjectIterable<Double2ShortMap.Entry>)(entries instanceof Double2ShortMap.FastEntrySet ? new ObjectIterable<Double2ShortMap.Entry>() {
         @Override
         public ObjectIterator<Double2ShortMap.Entry> iterator() {
            return ((Double2ShortMap.FastEntrySet)entries).fastIterator();
         }

         @Override
         public ObjectSpliterator<Double2ShortMap.Entry> spliterator() {
            return entries.spliterator();
         }

         @Override
         public void forEach(Consumer<? super Double2ShortMap.Entry> consumer) {
            ((Double2ShortMap.FastEntrySet)entries).fastForEach(consumer);
         }
      } : entries);
   }

   public static Double2ShortMap singleton(double key, short value) {
      return new Double2ShortMaps.Singleton(key, value);
   }

   public static Double2ShortMap singleton(Double key, Short value) {
      return new Double2ShortMaps.Singleton(key, value);
   }

   public static Double2ShortMap synchronize(Double2ShortMap m) {
      return new Double2ShortMaps.SynchronizedMap(m);
   }

   public static Double2ShortMap synchronize(Double2ShortMap m, Object sync) {
      return new Double2ShortMaps.SynchronizedMap(m, sync);
   }

   public static Double2ShortMap unmodifiable(Double2ShortMap m) {
      return new Double2ShortMaps.UnmodifiableMap(m);
   }

   public static class EmptyMap extends Double2ShortFunctions.EmptyFunction implements Double2ShortMap, Serializable, Cloneable {
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
      public short getOrDefault(double key, short defaultValue) {
         return defaultValue;
      }

      @Deprecated
      @Override
      public boolean containsValue(Object ov) {
         return false;
      }

      @Override
      public void putAll(Map<? extends Double, ? extends Short> m) {
         throw new UnsupportedOperationException();
      }

      @Override
      public ObjectSet<Double2ShortMap.Entry> double2ShortEntrySet() {
         return ObjectSets.EMPTY_SET;
      }

      @Override
      public DoubleSet keySet() {
         return DoubleSets.EMPTY_SET;
      }

      @Override
      public ShortCollection values() {
         return ShortSets.EMPTY_SET;
      }

      @Override
      public void forEach(BiConsumer<? super Double, ? super Short> consumer) {
      }

      @Override
      public Object clone() {
         return Double2ShortMaps.EMPTY_MAP;
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

   public static class Singleton extends Double2ShortFunctions.Singleton implements Double2ShortMap, Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected transient ObjectSet<Double2ShortMap.Entry> entries;
      protected transient DoubleSet keys;
      protected transient ShortCollection values;

      protected Singleton(double key, short value) {
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
      public void putAll(Map<? extends Double, ? extends Short> m) {
         throw new UnsupportedOperationException();
      }

      @Override
      public ObjectSet<Double2ShortMap.Entry> double2ShortEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSets.singleton(new AbstractDouble2ShortMap.BasicEntry(this.key, this.value));
         }

         return this.entries;
      }

      @Deprecated
      @Override
      public ObjectSet<Entry<Double, Short>> entrySet() {
         return this.double2ShortEntrySet();
      }

      @Override
      public DoubleSet keySet() {
         if (this.keys == null) {
            this.keys = DoubleSets.singleton(this.key);
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
         return HashCommon.double2int(this.key) ^ this.value;
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

   public static class SynchronizedMap extends Double2ShortFunctions.SynchronizedFunction implements Double2ShortMap, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Double2ShortMap map;
      protected transient ObjectSet<Double2ShortMap.Entry> entries;
      protected transient DoubleSet keys;
      protected transient ShortCollection values;

      protected SynchronizedMap(Double2ShortMap m, Object sync) {
         super(m, sync);
         this.map = m;
      }

      protected SynchronizedMap(Double2ShortMap m) {
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
      public void putAll(Map<? extends Double, ? extends Short> m) {
         synchronized (this.sync) {
            this.map.putAll(m);
         }
      }

      @Override
      public ObjectSet<Double2ShortMap.Entry> double2ShortEntrySet() {
         synchronized (this.sync) {
            if (this.entries == null) {
               this.entries = ObjectSets.synchronize(this.map.double2ShortEntrySet(), this.sync);
            }

            return this.entries;
         }
      }

      @Deprecated
      @Override
      public ObjectSet<Entry<Double, Short>> entrySet() {
         return this.double2ShortEntrySet();
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
      public short getOrDefault(double key, short defaultValue) {
         synchronized (this.sync) {
            return this.map.getOrDefault(key, defaultValue);
         }
      }

      @Override
      public void forEach(BiConsumer<? super Double, ? super Short> action) {
         synchronized (this.sync) {
            this.map.forEach(action);
         }
      }

      @Override
      public void replaceAll(BiFunction<? super Double, ? super Short, ? extends Short> function) {
         synchronized (this.sync) {
            this.map.replaceAll(function);
         }
      }

      @Override
      public short putIfAbsent(double key, short value) {
         synchronized (this.sync) {
            return this.map.putIfAbsent(key, value);
         }
      }

      @Override
      public boolean remove(double key, short value) {
         synchronized (this.sync) {
            return this.map.remove(key, value);
         }
      }

      @Override
      public short replace(double key, short value) {
         synchronized (this.sync) {
            return this.map.replace(key, value);
         }
      }

      @Override
      public boolean replace(double key, short oldValue, short newValue) {
         synchronized (this.sync) {
            return this.map.replace(key, oldValue, newValue);
         }
      }

      @Override
      public short computeIfAbsent(double key, DoubleToIntFunction mappingFunction) {
         synchronized (this.sync) {
            return this.map.computeIfAbsent(key, mappingFunction);
         }
      }

      @Override
      public short computeIfAbsentNullable(double key, DoubleFunction<? extends Short> mappingFunction) {
         synchronized (this.sync) {
            return this.map.computeIfAbsentNullable(key, mappingFunction);
         }
      }

      @Override
      public short computeIfAbsent(double key, Double2ShortFunction mappingFunction) {
         synchronized (this.sync) {
            return this.map.computeIfAbsent(key, mappingFunction);
         }
      }

      @Override
      public short computeIfPresent(double key, BiFunction<? super Double, ? super Short, ? extends Short> remappingFunction) {
         synchronized (this.sync) {
            return this.map.computeIfPresent(key, remappingFunction);
         }
      }

      @Override
      public short compute(double key, BiFunction<? super Double, ? super Short, ? extends Short> remappingFunction) {
         synchronized (this.sync) {
            return this.map.compute(key, remappingFunction);
         }
      }

      @Override
      public short merge(double key, short value, BiFunction<? super Short, ? super Short, ? extends Short> remappingFunction) {
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
      public Short replace(Double key, Short value) {
         synchronized (this.sync) {
            return this.map.replace(key, value);
         }
      }

      @Deprecated
      @Override
      public boolean replace(Double key, Short oldValue, Short newValue) {
         synchronized (this.sync) {
            return this.map.replace(key, oldValue, newValue);
         }
      }

      @Deprecated
      @Override
      public Short putIfAbsent(Double key, Short value) {
         synchronized (this.sync) {
            return this.map.putIfAbsent(key, value);
         }
      }

      @Deprecated
      @Override
      public Short computeIfAbsent(Double key, Function<? super Double, ? extends Short> mappingFunction) {
         synchronized (this.sync) {
            return this.map.computeIfAbsent(key, mappingFunction);
         }
      }

      @Deprecated
      @Override
      public Short computeIfPresent(Double key, BiFunction<? super Double, ? super Short, ? extends Short> remappingFunction) {
         synchronized (this.sync) {
            return this.map.computeIfPresent(key, remappingFunction);
         }
      }

      @Deprecated
      @Override
      public Short compute(Double key, BiFunction<? super Double, ? super Short, ? extends Short> remappingFunction) {
         synchronized (this.sync) {
            return this.map.compute(key, remappingFunction);
         }
      }

      @Deprecated
      @Override
      public Short merge(Double key, Short value, BiFunction<? super Short, ? super Short, ? extends Short> remappingFunction) {
         synchronized (this.sync) {
            return this.map.merge(key, value, remappingFunction);
         }
      }
   }

   public static class UnmodifiableMap extends Double2ShortFunctions.UnmodifiableFunction implements Double2ShortMap, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Double2ShortMap map;
      protected transient ObjectSet<Double2ShortMap.Entry> entries;
      protected transient DoubleSet keys;
      protected transient ShortCollection values;

      protected UnmodifiableMap(Double2ShortMap m) {
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
      public void putAll(Map<? extends Double, ? extends Short> m) {
         throw new UnsupportedOperationException();
      }

      @Override
      public ObjectSet<Double2ShortMap.Entry> double2ShortEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSets.unmodifiable(this.map.double2ShortEntrySet());
         }

         return this.entries;
      }

      @Deprecated
      @Override
      public ObjectSet<Entry<Double, Short>> entrySet() {
         return this.double2ShortEntrySet();
      }

      @Override
      public DoubleSet keySet() {
         if (this.keys == null) {
            this.keys = DoubleSets.unmodifiable(this.map.keySet());
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
      public short getOrDefault(double key, short defaultValue) {
         return this.map.getOrDefault(key, defaultValue);
      }

      @Override
      public void forEach(BiConsumer<? super Double, ? super Short> action) {
         this.map.forEach(action);
      }

      @Override
      public void replaceAll(BiFunction<? super Double, ? super Short, ? extends Short> function) {
         throw new UnsupportedOperationException();
      }

      @Override
      public short putIfAbsent(double key, short value) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean remove(double key, short value) {
         throw new UnsupportedOperationException();
      }

      @Override
      public short replace(double key, short value) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean replace(double key, short oldValue, short newValue) {
         throw new UnsupportedOperationException();
      }

      @Override
      public short computeIfAbsent(double key, DoubleToIntFunction mappingFunction) {
         throw new UnsupportedOperationException();
      }

      @Override
      public short computeIfAbsentNullable(double key, DoubleFunction<? extends Short> mappingFunction) {
         throw new UnsupportedOperationException();
      }

      @Override
      public short computeIfAbsent(double key, Double2ShortFunction mappingFunction) {
         throw new UnsupportedOperationException();
      }

      @Override
      public short computeIfPresent(double key, BiFunction<? super Double, ? super Short, ? extends Short> remappingFunction) {
         throw new UnsupportedOperationException();
      }

      @Override
      public short compute(double key, BiFunction<? super Double, ? super Short, ? extends Short> remappingFunction) {
         throw new UnsupportedOperationException();
      }

      @Override
      public short merge(double key, short value, BiFunction<? super Short, ? super Short, ? extends Short> remappingFunction) {
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
      public Short replace(Double key, Short value) {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      @Override
      public boolean replace(Double key, Short oldValue, Short newValue) {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      @Override
      public Short putIfAbsent(Double key, Short value) {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      @Override
      public Short computeIfAbsent(Double key, Function<? super Double, ? extends Short> mappingFunction) {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      @Override
      public Short computeIfPresent(Double key, BiFunction<? super Double, ? super Short, ? extends Short> remappingFunction) {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      @Override
      public Short compute(Double key, BiFunction<? super Double, ? super Short, ? extends Short> remappingFunction) {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      @Override
      public Short merge(Double key, Short value, BiFunction<? super Short, ? super Short, ? extends Short> remappingFunction) {
         throw new UnsupportedOperationException();
      }
   }
}
