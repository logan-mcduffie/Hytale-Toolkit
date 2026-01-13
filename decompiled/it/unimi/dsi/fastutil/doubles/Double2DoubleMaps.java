package it.unimi.dsi.fastutil.doubles;

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
import java.util.function.Function;

public final class Double2DoubleMaps {
   public static final Double2DoubleMaps.EmptyMap EMPTY_MAP = new Double2DoubleMaps.EmptyMap();

   private Double2DoubleMaps() {
   }

   public static ObjectIterator<Double2DoubleMap.Entry> fastIterator(Double2DoubleMap map) {
      ObjectSet<Double2DoubleMap.Entry> entries = map.double2DoubleEntrySet();
      return entries instanceof Double2DoubleMap.FastEntrySet ? ((Double2DoubleMap.FastEntrySet)entries).fastIterator() : entries.iterator();
   }

   public static void fastForEach(Double2DoubleMap map, Consumer<? super Double2DoubleMap.Entry> consumer) {
      ObjectSet<Double2DoubleMap.Entry> entries = map.double2DoubleEntrySet();
      if (entries instanceof Double2DoubleMap.FastEntrySet) {
         ((Double2DoubleMap.FastEntrySet)entries).fastForEach(consumer);
      } else {
         entries.forEach(consumer);
      }
   }

   public static ObjectIterable<Double2DoubleMap.Entry> fastIterable(Double2DoubleMap map) {
      final ObjectSet<Double2DoubleMap.Entry> entries = map.double2DoubleEntrySet();
      return (ObjectIterable<Double2DoubleMap.Entry>)(entries instanceof Double2DoubleMap.FastEntrySet ? new ObjectIterable<Double2DoubleMap.Entry>() {
         @Override
         public ObjectIterator<Double2DoubleMap.Entry> iterator() {
            return ((Double2DoubleMap.FastEntrySet)entries).fastIterator();
         }

         @Override
         public ObjectSpliterator<Double2DoubleMap.Entry> spliterator() {
            return entries.spliterator();
         }

         @Override
         public void forEach(Consumer<? super Double2DoubleMap.Entry> consumer) {
            ((Double2DoubleMap.FastEntrySet)entries).fastForEach(consumer);
         }
      } : entries);
   }

   public static Double2DoubleMap singleton(double key, double value) {
      return new Double2DoubleMaps.Singleton(key, value);
   }

   public static Double2DoubleMap singleton(Double key, Double value) {
      return new Double2DoubleMaps.Singleton(key, value);
   }

   public static Double2DoubleMap synchronize(Double2DoubleMap m) {
      return new Double2DoubleMaps.SynchronizedMap(m);
   }

   public static Double2DoubleMap synchronize(Double2DoubleMap m, Object sync) {
      return new Double2DoubleMaps.SynchronizedMap(m, sync);
   }

   public static Double2DoubleMap unmodifiable(Double2DoubleMap m) {
      return new Double2DoubleMaps.UnmodifiableMap(m);
   }

   public static class EmptyMap extends Double2DoubleFunctions.EmptyFunction implements Double2DoubleMap, Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;

      protected EmptyMap() {
      }

      @Override
      public boolean containsValue(double v) {
         return false;
      }

      @Deprecated
      @Override
      public Double getOrDefault(Object key, Double defaultValue) {
         return defaultValue;
      }

      @Override
      public double getOrDefault(double key, double defaultValue) {
         return defaultValue;
      }

      @Deprecated
      @Override
      public boolean containsValue(Object ov) {
         return false;
      }

      @Override
      public void putAll(Map<? extends Double, ? extends Double> m) {
         throw new UnsupportedOperationException();
      }

      @Override
      public ObjectSet<Double2DoubleMap.Entry> double2DoubleEntrySet() {
         return ObjectSets.EMPTY_SET;
      }

      @Override
      public DoubleSet keySet() {
         return DoubleSets.EMPTY_SET;
      }

      @Override
      public DoubleCollection values() {
         return DoubleSets.EMPTY_SET;
      }

      @Override
      public void forEach(BiConsumer<? super Double, ? super Double> consumer) {
      }

      @Override
      public Object clone() {
         return Double2DoubleMaps.EMPTY_MAP;
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

   public static class Singleton extends Double2DoubleFunctions.Singleton implements Double2DoubleMap, Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected transient ObjectSet<Double2DoubleMap.Entry> entries;
      protected transient DoubleSet keys;
      protected transient DoubleCollection values;

      protected Singleton(double key, double value) {
         super(key, value);
      }

      @Override
      public boolean containsValue(double v) {
         return Double.doubleToLongBits(this.value) == Double.doubleToLongBits(v);
      }

      @Deprecated
      @Override
      public boolean containsValue(Object ov) {
         return Double.doubleToLongBits((Double)ov) == Double.doubleToLongBits(this.value);
      }

      @Override
      public void putAll(Map<? extends Double, ? extends Double> m) {
         throw new UnsupportedOperationException();
      }

      @Override
      public ObjectSet<Double2DoubleMap.Entry> double2DoubleEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSets.singleton(new AbstractDouble2DoubleMap.BasicEntry(this.key, this.value));
         }

         return this.entries;
      }

      @Deprecated
      @Override
      public ObjectSet<Entry<Double, Double>> entrySet() {
         return this.double2DoubleEntrySet();
      }

      @Override
      public DoubleSet keySet() {
         if (this.keys == null) {
            this.keys = DoubleSets.singleton(this.key);
         }

         return this.keys;
      }

      @Override
      public DoubleCollection values() {
         if (this.values == null) {
            this.values = DoubleSets.singleton(this.value);
         }

         return this.values;
      }

      @Override
      public boolean isEmpty() {
         return false;
      }

      @Override
      public int hashCode() {
         return HashCommon.double2int(this.key) ^ HashCommon.double2int(this.value);
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

   public static class SynchronizedMap extends Double2DoubleFunctions.SynchronizedFunction implements Double2DoubleMap, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Double2DoubleMap map;
      protected transient ObjectSet<Double2DoubleMap.Entry> entries;
      protected transient DoubleSet keys;
      protected transient DoubleCollection values;

      protected SynchronizedMap(Double2DoubleMap m, Object sync) {
         super(m, sync);
         this.map = m;
      }

      protected SynchronizedMap(Double2DoubleMap m) {
         super(m);
         this.map = m;
      }

      @Override
      public boolean containsValue(double v) {
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
      public void putAll(Map<? extends Double, ? extends Double> m) {
         synchronized (this.sync) {
            this.map.putAll(m);
         }
      }

      @Override
      public ObjectSet<Double2DoubleMap.Entry> double2DoubleEntrySet() {
         synchronized (this.sync) {
            if (this.entries == null) {
               this.entries = ObjectSets.synchronize(this.map.double2DoubleEntrySet(), this.sync);
            }

            return this.entries;
         }
      }

      @Deprecated
      @Override
      public ObjectSet<Entry<Double, Double>> entrySet() {
         return this.double2DoubleEntrySet();
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
      public DoubleCollection values() {
         synchronized (this.sync) {
            if (this.values == null) {
               this.values = DoubleCollections.synchronize(this.map.values(), this.sync);
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
      public double getOrDefault(double key, double defaultValue) {
         synchronized (this.sync) {
            return this.map.getOrDefault(key, defaultValue);
         }
      }

      @Override
      public void forEach(BiConsumer<? super Double, ? super Double> action) {
         synchronized (this.sync) {
            this.map.forEach(action);
         }
      }

      @Override
      public void replaceAll(BiFunction<? super Double, ? super Double, ? extends Double> function) {
         synchronized (this.sync) {
            this.map.replaceAll(function);
         }
      }

      @Override
      public double putIfAbsent(double key, double value) {
         synchronized (this.sync) {
            return this.map.putIfAbsent(key, value);
         }
      }

      @Override
      public boolean remove(double key, double value) {
         synchronized (this.sync) {
            return this.map.remove(key, value);
         }
      }

      @Override
      public double replace(double key, double value) {
         synchronized (this.sync) {
            return this.map.replace(key, value);
         }
      }

      @Override
      public boolean replace(double key, double oldValue, double newValue) {
         synchronized (this.sync) {
            return this.map.replace(key, oldValue, newValue);
         }
      }

      @Override
      public double computeIfAbsent(double key, java.util.function.DoubleUnaryOperator mappingFunction) {
         synchronized (this.sync) {
            return this.map.computeIfAbsent(key, mappingFunction);
         }
      }

      @Override
      public double computeIfAbsentNullable(double key, DoubleFunction<? extends Double> mappingFunction) {
         synchronized (this.sync) {
            return this.map.computeIfAbsentNullable(key, mappingFunction);
         }
      }

      @Override
      public double computeIfAbsent(double key, Double2DoubleFunction mappingFunction) {
         synchronized (this.sync) {
            return this.map.computeIfAbsent(key, mappingFunction);
         }
      }

      @Override
      public double computeIfPresent(double key, BiFunction<? super Double, ? super Double, ? extends Double> remappingFunction) {
         synchronized (this.sync) {
            return this.map.computeIfPresent(key, remappingFunction);
         }
      }

      @Override
      public double compute(double key, BiFunction<? super Double, ? super Double, ? extends Double> remappingFunction) {
         synchronized (this.sync) {
            return this.map.compute(key, remappingFunction);
         }
      }

      @Override
      public double merge(double key, double value, BiFunction<? super Double, ? super Double, ? extends Double> remappingFunction) {
         synchronized (this.sync) {
            return this.map.merge(key, value, remappingFunction);
         }
      }

      @Deprecated
      @Override
      public Double getOrDefault(Object key, Double defaultValue) {
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
      public Double replace(Double key, Double value) {
         synchronized (this.sync) {
            return this.map.replace(key, value);
         }
      }

      @Deprecated
      @Override
      public boolean replace(Double key, Double oldValue, Double newValue) {
         synchronized (this.sync) {
            return this.map.replace(key, oldValue, newValue);
         }
      }

      @Deprecated
      @Override
      public Double putIfAbsent(Double key, Double value) {
         synchronized (this.sync) {
            return this.map.putIfAbsent(key, value);
         }
      }

      @Deprecated
      @Override
      public Double computeIfAbsent(Double key, Function<? super Double, ? extends Double> mappingFunction) {
         synchronized (this.sync) {
            return this.map.computeIfAbsent(key, mappingFunction);
         }
      }

      @Deprecated
      @Override
      public Double computeIfPresent(Double key, BiFunction<? super Double, ? super Double, ? extends Double> remappingFunction) {
         synchronized (this.sync) {
            return this.map.computeIfPresent(key, remappingFunction);
         }
      }

      @Deprecated
      @Override
      public Double compute(Double key, BiFunction<? super Double, ? super Double, ? extends Double> remappingFunction) {
         synchronized (this.sync) {
            return this.map.compute(key, remappingFunction);
         }
      }

      @Deprecated
      @Override
      public Double merge(Double key, Double value, BiFunction<? super Double, ? super Double, ? extends Double> remappingFunction) {
         synchronized (this.sync) {
            return this.map.merge(key, value, remappingFunction);
         }
      }
   }

   public static class UnmodifiableMap extends Double2DoubleFunctions.UnmodifiableFunction implements Double2DoubleMap, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Double2DoubleMap map;
      protected transient ObjectSet<Double2DoubleMap.Entry> entries;
      protected transient DoubleSet keys;
      protected transient DoubleCollection values;

      protected UnmodifiableMap(Double2DoubleMap m) {
         super(m);
         this.map = m;
      }

      @Override
      public boolean containsValue(double v) {
         return this.map.containsValue(v);
      }

      @Deprecated
      @Override
      public boolean containsValue(Object ov) {
         return this.map.containsValue(ov);
      }

      @Override
      public void putAll(Map<? extends Double, ? extends Double> m) {
         throw new UnsupportedOperationException();
      }

      @Override
      public ObjectSet<Double2DoubleMap.Entry> double2DoubleEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSets.unmodifiable(this.map.double2DoubleEntrySet());
         }

         return this.entries;
      }

      @Deprecated
      @Override
      public ObjectSet<Entry<Double, Double>> entrySet() {
         return this.double2DoubleEntrySet();
      }

      @Override
      public DoubleSet keySet() {
         if (this.keys == null) {
            this.keys = DoubleSets.unmodifiable(this.map.keySet());
         }

         return this.keys;
      }

      @Override
      public DoubleCollection values() {
         if (this.values == null) {
            this.values = DoubleCollections.unmodifiable(this.map.values());
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
      public double getOrDefault(double key, double defaultValue) {
         return this.map.getOrDefault(key, defaultValue);
      }

      @Override
      public void forEach(BiConsumer<? super Double, ? super Double> action) {
         this.map.forEach(action);
      }

      @Override
      public void replaceAll(BiFunction<? super Double, ? super Double, ? extends Double> function) {
         throw new UnsupportedOperationException();
      }

      @Override
      public double putIfAbsent(double key, double value) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean remove(double key, double value) {
         throw new UnsupportedOperationException();
      }

      @Override
      public double replace(double key, double value) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean replace(double key, double oldValue, double newValue) {
         throw new UnsupportedOperationException();
      }

      @Override
      public double computeIfAbsent(double key, java.util.function.DoubleUnaryOperator mappingFunction) {
         throw new UnsupportedOperationException();
      }

      @Override
      public double computeIfAbsentNullable(double key, DoubleFunction<? extends Double> mappingFunction) {
         throw new UnsupportedOperationException();
      }

      @Override
      public double computeIfAbsent(double key, Double2DoubleFunction mappingFunction) {
         throw new UnsupportedOperationException();
      }

      @Override
      public double computeIfPresent(double key, BiFunction<? super Double, ? super Double, ? extends Double> remappingFunction) {
         throw new UnsupportedOperationException();
      }

      @Override
      public double compute(double key, BiFunction<? super Double, ? super Double, ? extends Double> remappingFunction) {
         throw new UnsupportedOperationException();
      }

      @Override
      public double merge(double key, double value, BiFunction<? super Double, ? super Double, ? extends Double> remappingFunction) {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      @Override
      public Double getOrDefault(Object key, Double defaultValue) {
         return this.map.getOrDefault(key, defaultValue);
      }

      @Deprecated
      @Override
      public boolean remove(Object key, Object value) {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      @Override
      public Double replace(Double key, Double value) {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      @Override
      public boolean replace(Double key, Double oldValue, Double newValue) {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      @Override
      public Double putIfAbsent(Double key, Double value) {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      @Override
      public Double computeIfAbsent(Double key, Function<? super Double, ? extends Double> mappingFunction) {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      @Override
      public Double computeIfPresent(Double key, BiFunction<? super Double, ? super Double, ? extends Double> remappingFunction) {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      @Override
      public Double compute(Double key, BiFunction<? super Double, ? super Double, ? extends Double> remappingFunction) {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      @Override
      public Double merge(Double key, Double value, BiFunction<? super Double, ? super Double, ? extends Double> remappingFunction) {
         throw new UnsupportedOperationException();
      }
   }
}
