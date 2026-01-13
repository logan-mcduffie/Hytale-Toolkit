package it.unimi.dsi.fastutil.ints;

import it.unimi.dsi.fastutil.HashCommon;
import it.unimi.dsi.fastutil.doubles.DoubleCollection;
import it.unimi.dsi.fastutil.doubles.DoubleCollections;
import it.unimi.dsi.fastutil.doubles.DoubleSets;
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

public final class Int2DoubleMaps {
   public static final Int2DoubleMaps.EmptyMap EMPTY_MAP = new Int2DoubleMaps.EmptyMap();

   private Int2DoubleMaps() {
   }

   public static ObjectIterator<Int2DoubleMap.Entry> fastIterator(Int2DoubleMap map) {
      ObjectSet<Int2DoubleMap.Entry> entries = map.int2DoubleEntrySet();
      return entries instanceof Int2DoubleMap.FastEntrySet ? ((Int2DoubleMap.FastEntrySet)entries).fastIterator() : entries.iterator();
   }

   public static void fastForEach(Int2DoubleMap map, Consumer<? super Int2DoubleMap.Entry> consumer) {
      ObjectSet<Int2DoubleMap.Entry> entries = map.int2DoubleEntrySet();
      if (entries instanceof Int2DoubleMap.FastEntrySet) {
         ((Int2DoubleMap.FastEntrySet)entries).fastForEach(consumer);
      } else {
         entries.forEach(consumer);
      }
   }

   public static ObjectIterable<Int2DoubleMap.Entry> fastIterable(Int2DoubleMap map) {
      final ObjectSet<Int2DoubleMap.Entry> entries = map.int2DoubleEntrySet();
      return (ObjectIterable<Int2DoubleMap.Entry>)(entries instanceof Int2DoubleMap.FastEntrySet ? new ObjectIterable<Int2DoubleMap.Entry>() {
         @Override
         public ObjectIterator<Int2DoubleMap.Entry> iterator() {
            return ((Int2DoubleMap.FastEntrySet)entries).fastIterator();
         }

         @Override
         public ObjectSpliterator<Int2DoubleMap.Entry> spliterator() {
            return entries.spliterator();
         }

         @Override
         public void forEach(Consumer<? super Int2DoubleMap.Entry> consumer) {
            ((Int2DoubleMap.FastEntrySet)entries).fastForEach(consumer);
         }
      } : entries);
   }

   public static Int2DoubleMap singleton(int key, double value) {
      return new Int2DoubleMaps.Singleton(key, value);
   }

   public static Int2DoubleMap singleton(Integer key, Double value) {
      return new Int2DoubleMaps.Singleton(key, value);
   }

   public static Int2DoubleMap synchronize(Int2DoubleMap m) {
      return new Int2DoubleMaps.SynchronizedMap(m);
   }

   public static Int2DoubleMap synchronize(Int2DoubleMap m, Object sync) {
      return new Int2DoubleMaps.SynchronizedMap(m, sync);
   }

   public static Int2DoubleMap unmodifiable(Int2DoubleMap m) {
      return new Int2DoubleMaps.UnmodifiableMap(m);
   }

   public static class EmptyMap extends Int2DoubleFunctions.EmptyFunction implements Int2DoubleMap, Serializable, Cloneable {
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
      public double getOrDefault(int key, double defaultValue) {
         return defaultValue;
      }

      @Deprecated
      @Override
      public boolean containsValue(Object ov) {
         return false;
      }

      @Override
      public void putAll(Map<? extends Integer, ? extends Double> m) {
         throw new UnsupportedOperationException();
      }

      @Override
      public ObjectSet<Int2DoubleMap.Entry> int2DoubleEntrySet() {
         return ObjectSets.EMPTY_SET;
      }

      @Override
      public IntSet keySet() {
         return IntSets.EMPTY_SET;
      }

      @Override
      public DoubleCollection values() {
         return DoubleSets.EMPTY_SET;
      }

      @Override
      public void forEach(BiConsumer<? super Integer, ? super Double> consumer) {
      }

      @Override
      public Object clone() {
         return Int2DoubleMaps.EMPTY_MAP;
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

   public static class Singleton extends Int2DoubleFunctions.Singleton implements Int2DoubleMap, Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected transient ObjectSet<Int2DoubleMap.Entry> entries;
      protected transient IntSet keys;
      protected transient DoubleCollection values;

      protected Singleton(int key, double value) {
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
      public void putAll(Map<? extends Integer, ? extends Double> m) {
         throw new UnsupportedOperationException();
      }

      @Override
      public ObjectSet<Int2DoubleMap.Entry> int2DoubleEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSets.singleton(new AbstractInt2DoubleMap.BasicEntry(this.key, this.value));
         }

         return this.entries;
      }

      @Deprecated
      @Override
      public ObjectSet<Entry<Integer, Double>> entrySet() {
         return this.int2DoubleEntrySet();
      }

      @Override
      public IntSet keySet() {
         if (this.keys == null) {
            this.keys = IntSets.singleton(this.key);
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
         return this.key ^ HashCommon.double2int(this.value);
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

   public static class SynchronizedMap extends Int2DoubleFunctions.SynchronizedFunction implements Int2DoubleMap, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Int2DoubleMap map;
      protected transient ObjectSet<Int2DoubleMap.Entry> entries;
      protected transient IntSet keys;
      protected transient DoubleCollection values;

      protected SynchronizedMap(Int2DoubleMap m, Object sync) {
         super(m, sync);
         this.map = m;
      }

      protected SynchronizedMap(Int2DoubleMap m) {
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
      public void putAll(Map<? extends Integer, ? extends Double> m) {
         synchronized (this.sync) {
            this.map.putAll(m);
         }
      }

      @Override
      public ObjectSet<Int2DoubleMap.Entry> int2DoubleEntrySet() {
         synchronized (this.sync) {
            if (this.entries == null) {
               this.entries = ObjectSets.synchronize(this.map.int2DoubleEntrySet(), this.sync);
            }

            return this.entries;
         }
      }

      @Deprecated
      @Override
      public ObjectSet<Entry<Integer, Double>> entrySet() {
         return this.int2DoubleEntrySet();
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
      public double getOrDefault(int key, double defaultValue) {
         synchronized (this.sync) {
            return this.map.getOrDefault(key, defaultValue);
         }
      }

      @Override
      public void forEach(BiConsumer<? super Integer, ? super Double> action) {
         synchronized (this.sync) {
            this.map.forEach(action);
         }
      }

      @Override
      public void replaceAll(BiFunction<? super Integer, ? super Double, ? extends Double> function) {
         synchronized (this.sync) {
            this.map.replaceAll(function);
         }
      }

      @Override
      public double putIfAbsent(int key, double value) {
         synchronized (this.sync) {
            return this.map.putIfAbsent(key, value);
         }
      }

      @Override
      public boolean remove(int key, double value) {
         synchronized (this.sync) {
            return this.map.remove(key, value);
         }
      }

      @Override
      public double replace(int key, double value) {
         synchronized (this.sync) {
            return this.map.replace(key, value);
         }
      }

      @Override
      public boolean replace(int key, double oldValue, double newValue) {
         synchronized (this.sync) {
            return this.map.replace(key, oldValue, newValue);
         }
      }

      @Override
      public double computeIfAbsent(int key, IntToDoubleFunction mappingFunction) {
         synchronized (this.sync) {
            return this.map.computeIfAbsent(key, mappingFunction);
         }
      }

      @Override
      public double computeIfAbsentNullable(int key, IntFunction<? extends Double> mappingFunction) {
         synchronized (this.sync) {
            return this.map.computeIfAbsentNullable(key, mappingFunction);
         }
      }

      @Override
      public double computeIfAbsent(int key, Int2DoubleFunction mappingFunction) {
         synchronized (this.sync) {
            return this.map.computeIfAbsent(key, mappingFunction);
         }
      }

      @Override
      public double computeIfPresent(int key, BiFunction<? super Integer, ? super Double, ? extends Double> remappingFunction) {
         synchronized (this.sync) {
            return this.map.computeIfPresent(key, remappingFunction);
         }
      }

      @Override
      public double compute(int key, BiFunction<? super Integer, ? super Double, ? extends Double> remappingFunction) {
         synchronized (this.sync) {
            return this.map.compute(key, remappingFunction);
         }
      }

      @Override
      public double merge(int key, double value, BiFunction<? super Double, ? super Double, ? extends Double> remappingFunction) {
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
      public Double replace(Integer key, Double value) {
         synchronized (this.sync) {
            return this.map.replace(key, value);
         }
      }

      @Deprecated
      @Override
      public boolean replace(Integer key, Double oldValue, Double newValue) {
         synchronized (this.sync) {
            return this.map.replace(key, oldValue, newValue);
         }
      }

      @Deprecated
      @Override
      public Double putIfAbsent(Integer key, Double value) {
         synchronized (this.sync) {
            return this.map.putIfAbsent(key, value);
         }
      }

      @Deprecated
      @Override
      public Double computeIfAbsent(Integer key, Function<? super Integer, ? extends Double> mappingFunction) {
         synchronized (this.sync) {
            return this.map.computeIfAbsent(key, mappingFunction);
         }
      }

      @Deprecated
      @Override
      public Double computeIfPresent(Integer key, BiFunction<? super Integer, ? super Double, ? extends Double> remappingFunction) {
         synchronized (this.sync) {
            return this.map.computeIfPresent(key, remappingFunction);
         }
      }

      @Deprecated
      @Override
      public Double compute(Integer key, BiFunction<? super Integer, ? super Double, ? extends Double> remappingFunction) {
         synchronized (this.sync) {
            return this.map.compute(key, remappingFunction);
         }
      }

      @Deprecated
      @Override
      public Double merge(Integer key, Double value, BiFunction<? super Double, ? super Double, ? extends Double> remappingFunction) {
         synchronized (this.sync) {
            return this.map.merge(key, value, remappingFunction);
         }
      }
   }

   public static class UnmodifiableMap extends Int2DoubleFunctions.UnmodifiableFunction implements Int2DoubleMap, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Int2DoubleMap map;
      protected transient ObjectSet<Int2DoubleMap.Entry> entries;
      protected transient IntSet keys;
      protected transient DoubleCollection values;

      protected UnmodifiableMap(Int2DoubleMap m) {
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
      public void putAll(Map<? extends Integer, ? extends Double> m) {
         throw new UnsupportedOperationException();
      }

      @Override
      public ObjectSet<Int2DoubleMap.Entry> int2DoubleEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSets.unmodifiable(this.map.int2DoubleEntrySet());
         }

         return this.entries;
      }

      @Deprecated
      @Override
      public ObjectSet<Entry<Integer, Double>> entrySet() {
         return this.int2DoubleEntrySet();
      }

      @Override
      public IntSet keySet() {
         if (this.keys == null) {
            this.keys = IntSets.unmodifiable(this.map.keySet());
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
      public double getOrDefault(int key, double defaultValue) {
         return this.map.getOrDefault(key, defaultValue);
      }

      @Override
      public void forEach(BiConsumer<? super Integer, ? super Double> action) {
         this.map.forEach(action);
      }

      @Override
      public void replaceAll(BiFunction<? super Integer, ? super Double, ? extends Double> function) {
         throw new UnsupportedOperationException();
      }

      @Override
      public double putIfAbsent(int key, double value) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean remove(int key, double value) {
         throw new UnsupportedOperationException();
      }

      @Override
      public double replace(int key, double value) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean replace(int key, double oldValue, double newValue) {
         throw new UnsupportedOperationException();
      }

      @Override
      public double computeIfAbsent(int key, IntToDoubleFunction mappingFunction) {
         throw new UnsupportedOperationException();
      }

      @Override
      public double computeIfAbsentNullable(int key, IntFunction<? extends Double> mappingFunction) {
         throw new UnsupportedOperationException();
      }

      @Override
      public double computeIfAbsent(int key, Int2DoubleFunction mappingFunction) {
         throw new UnsupportedOperationException();
      }

      @Override
      public double computeIfPresent(int key, BiFunction<? super Integer, ? super Double, ? extends Double> remappingFunction) {
         throw new UnsupportedOperationException();
      }

      @Override
      public double compute(int key, BiFunction<? super Integer, ? super Double, ? extends Double> remappingFunction) {
         throw new UnsupportedOperationException();
      }

      @Override
      public double merge(int key, double value, BiFunction<? super Double, ? super Double, ? extends Double> remappingFunction) {
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
      public Double replace(Integer key, Double value) {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      @Override
      public boolean replace(Integer key, Double oldValue, Double newValue) {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      @Override
      public Double putIfAbsent(Integer key, Double value) {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      @Override
      public Double computeIfAbsent(Integer key, Function<? super Integer, ? extends Double> mappingFunction) {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      @Override
      public Double computeIfPresent(Integer key, BiFunction<? super Integer, ? super Double, ? extends Double> remappingFunction) {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      @Override
      public Double compute(Integer key, BiFunction<? super Integer, ? super Double, ? extends Double> remappingFunction) {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      @Override
      public Double merge(Integer key, Double value, BiFunction<? super Double, ? super Double, ? extends Double> remappingFunction) {
         throw new UnsupportedOperationException();
      }
   }
}
