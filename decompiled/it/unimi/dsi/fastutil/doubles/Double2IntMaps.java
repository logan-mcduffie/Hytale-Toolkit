package it.unimi.dsi.fastutil.doubles;

import it.unimi.dsi.fastutil.HashCommon;
import it.unimi.dsi.fastutil.ints.IntCollection;
import it.unimi.dsi.fastutil.ints.IntCollections;
import it.unimi.dsi.fastutil.ints.IntSets;
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
import java.util.function.DoubleToIntFunction;
import java.util.function.Function;

public final class Double2IntMaps {
   public static final Double2IntMaps.EmptyMap EMPTY_MAP = new Double2IntMaps.EmptyMap();

   private Double2IntMaps() {
   }

   public static ObjectIterator<Double2IntMap.Entry> fastIterator(Double2IntMap map) {
      ObjectSet<Double2IntMap.Entry> entries = map.double2IntEntrySet();
      return entries instanceof Double2IntMap.FastEntrySet ? ((Double2IntMap.FastEntrySet)entries).fastIterator() : entries.iterator();
   }

   public static void fastForEach(Double2IntMap map, Consumer<? super Double2IntMap.Entry> consumer) {
      ObjectSet<Double2IntMap.Entry> entries = map.double2IntEntrySet();
      if (entries instanceof Double2IntMap.FastEntrySet) {
         ((Double2IntMap.FastEntrySet)entries).fastForEach(consumer);
      } else {
         entries.forEach(consumer);
      }
   }

   public static ObjectIterable<Double2IntMap.Entry> fastIterable(Double2IntMap map) {
      final ObjectSet<Double2IntMap.Entry> entries = map.double2IntEntrySet();
      return (ObjectIterable<Double2IntMap.Entry>)(entries instanceof Double2IntMap.FastEntrySet ? new ObjectIterable<Double2IntMap.Entry>() {
         @Override
         public ObjectIterator<Double2IntMap.Entry> iterator() {
            return ((Double2IntMap.FastEntrySet)entries).fastIterator();
         }

         @Override
         public ObjectSpliterator<Double2IntMap.Entry> spliterator() {
            return entries.spliterator();
         }

         @Override
         public void forEach(Consumer<? super Double2IntMap.Entry> consumer) {
            ((Double2IntMap.FastEntrySet)entries).fastForEach(consumer);
         }
      } : entries);
   }

   public static Double2IntMap singleton(double key, int value) {
      return new Double2IntMaps.Singleton(key, value);
   }

   public static Double2IntMap singleton(Double key, Integer value) {
      return new Double2IntMaps.Singleton(key, value);
   }

   public static Double2IntMap synchronize(Double2IntMap m) {
      return new Double2IntMaps.SynchronizedMap(m);
   }

   public static Double2IntMap synchronize(Double2IntMap m, Object sync) {
      return new Double2IntMaps.SynchronizedMap(m, sync);
   }

   public static Double2IntMap unmodifiable(Double2IntMap m) {
      return new Double2IntMaps.UnmodifiableMap(m);
   }

   public static class EmptyMap extends Double2IntFunctions.EmptyFunction implements Double2IntMap, Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;

      protected EmptyMap() {
      }

      @Override
      public boolean containsValue(int v) {
         return false;
      }

      @Deprecated
      @Override
      public Integer getOrDefault(Object key, Integer defaultValue) {
         return defaultValue;
      }

      @Override
      public int getOrDefault(double key, int defaultValue) {
         return defaultValue;
      }

      @Deprecated
      @Override
      public boolean containsValue(Object ov) {
         return false;
      }

      @Override
      public void putAll(Map<? extends Double, ? extends Integer> m) {
         throw new UnsupportedOperationException();
      }

      @Override
      public ObjectSet<Double2IntMap.Entry> double2IntEntrySet() {
         return ObjectSets.EMPTY_SET;
      }

      @Override
      public DoubleSet keySet() {
         return DoubleSets.EMPTY_SET;
      }

      @Override
      public IntCollection values() {
         return IntSets.EMPTY_SET;
      }

      @Override
      public void forEach(BiConsumer<? super Double, ? super Integer> consumer) {
      }

      @Override
      public Object clone() {
         return Double2IntMaps.EMPTY_MAP;
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

   public static class Singleton extends Double2IntFunctions.Singleton implements Double2IntMap, Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected transient ObjectSet<Double2IntMap.Entry> entries;
      protected transient DoubleSet keys;
      protected transient IntCollection values;

      protected Singleton(double key, int value) {
         super(key, value);
      }

      @Override
      public boolean containsValue(int v) {
         return this.value == v;
      }

      @Deprecated
      @Override
      public boolean containsValue(Object ov) {
         return (Integer)ov == this.value;
      }

      @Override
      public void putAll(Map<? extends Double, ? extends Integer> m) {
         throw new UnsupportedOperationException();
      }

      @Override
      public ObjectSet<Double2IntMap.Entry> double2IntEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSets.singleton(new AbstractDouble2IntMap.BasicEntry(this.key, this.value));
         }

         return this.entries;
      }

      @Deprecated
      @Override
      public ObjectSet<Entry<Double, Integer>> entrySet() {
         return this.double2IntEntrySet();
      }

      @Override
      public DoubleSet keySet() {
         if (this.keys == null) {
            this.keys = DoubleSets.singleton(this.key);
         }

         return this.keys;
      }

      @Override
      public IntCollection values() {
         if (this.values == null) {
            this.values = IntSets.singleton(this.value);
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

   public static class SynchronizedMap extends Double2IntFunctions.SynchronizedFunction implements Double2IntMap, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Double2IntMap map;
      protected transient ObjectSet<Double2IntMap.Entry> entries;
      protected transient DoubleSet keys;
      protected transient IntCollection values;

      protected SynchronizedMap(Double2IntMap m, Object sync) {
         super(m, sync);
         this.map = m;
      }

      protected SynchronizedMap(Double2IntMap m) {
         super(m);
         this.map = m;
      }

      @Override
      public boolean containsValue(int v) {
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
      public void putAll(Map<? extends Double, ? extends Integer> m) {
         synchronized (this.sync) {
            this.map.putAll(m);
         }
      }

      @Override
      public ObjectSet<Double2IntMap.Entry> double2IntEntrySet() {
         synchronized (this.sync) {
            if (this.entries == null) {
               this.entries = ObjectSets.synchronize(this.map.double2IntEntrySet(), this.sync);
            }

            return this.entries;
         }
      }

      @Deprecated
      @Override
      public ObjectSet<Entry<Double, Integer>> entrySet() {
         return this.double2IntEntrySet();
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
      public IntCollection values() {
         synchronized (this.sync) {
            if (this.values == null) {
               this.values = IntCollections.synchronize(this.map.values(), this.sync);
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
      public int getOrDefault(double key, int defaultValue) {
         synchronized (this.sync) {
            return this.map.getOrDefault(key, defaultValue);
         }
      }

      @Override
      public void forEach(BiConsumer<? super Double, ? super Integer> action) {
         synchronized (this.sync) {
            this.map.forEach(action);
         }
      }

      @Override
      public void replaceAll(BiFunction<? super Double, ? super Integer, ? extends Integer> function) {
         synchronized (this.sync) {
            this.map.replaceAll(function);
         }
      }

      @Override
      public int putIfAbsent(double key, int value) {
         synchronized (this.sync) {
            return this.map.putIfAbsent(key, value);
         }
      }

      @Override
      public boolean remove(double key, int value) {
         synchronized (this.sync) {
            return this.map.remove(key, value);
         }
      }

      @Override
      public int replace(double key, int value) {
         synchronized (this.sync) {
            return this.map.replace(key, value);
         }
      }

      @Override
      public boolean replace(double key, int oldValue, int newValue) {
         synchronized (this.sync) {
            return this.map.replace(key, oldValue, newValue);
         }
      }

      @Override
      public int computeIfAbsent(double key, DoubleToIntFunction mappingFunction) {
         synchronized (this.sync) {
            return this.map.computeIfAbsent(key, mappingFunction);
         }
      }

      @Override
      public int computeIfAbsentNullable(double key, DoubleFunction<? extends Integer> mappingFunction) {
         synchronized (this.sync) {
            return this.map.computeIfAbsentNullable(key, mappingFunction);
         }
      }

      @Override
      public int computeIfAbsent(double key, Double2IntFunction mappingFunction) {
         synchronized (this.sync) {
            return this.map.computeIfAbsent(key, mappingFunction);
         }
      }

      @Override
      public int computeIfPresent(double key, BiFunction<? super Double, ? super Integer, ? extends Integer> remappingFunction) {
         synchronized (this.sync) {
            return this.map.computeIfPresent(key, remappingFunction);
         }
      }

      @Override
      public int compute(double key, BiFunction<? super Double, ? super Integer, ? extends Integer> remappingFunction) {
         synchronized (this.sync) {
            return this.map.compute(key, remappingFunction);
         }
      }

      @Override
      public int merge(double key, int value, BiFunction<? super Integer, ? super Integer, ? extends Integer> remappingFunction) {
         synchronized (this.sync) {
            return this.map.merge(key, value, remappingFunction);
         }
      }

      @Deprecated
      @Override
      public Integer getOrDefault(Object key, Integer defaultValue) {
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
      public Integer replace(Double key, Integer value) {
         synchronized (this.sync) {
            return this.map.replace(key, value);
         }
      }

      @Deprecated
      @Override
      public boolean replace(Double key, Integer oldValue, Integer newValue) {
         synchronized (this.sync) {
            return this.map.replace(key, oldValue, newValue);
         }
      }

      @Deprecated
      @Override
      public Integer putIfAbsent(Double key, Integer value) {
         synchronized (this.sync) {
            return this.map.putIfAbsent(key, value);
         }
      }

      @Deprecated
      @Override
      public Integer computeIfAbsent(Double key, Function<? super Double, ? extends Integer> mappingFunction) {
         synchronized (this.sync) {
            return this.map.computeIfAbsent(key, mappingFunction);
         }
      }

      @Deprecated
      @Override
      public Integer computeIfPresent(Double key, BiFunction<? super Double, ? super Integer, ? extends Integer> remappingFunction) {
         synchronized (this.sync) {
            return this.map.computeIfPresent(key, remappingFunction);
         }
      }

      @Deprecated
      @Override
      public Integer compute(Double key, BiFunction<? super Double, ? super Integer, ? extends Integer> remappingFunction) {
         synchronized (this.sync) {
            return this.map.compute(key, remappingFunction);
         }
      }

      @Deprecated
      @Override
      public Integer merge(Double key, Integer value, BiFunction<? super Integer, ? super Integer, ? extends Integer> remappingFunction) {
         synchronized (this.sync) {
            return this.map.merge(key, value, remappingFunction);
         }
      }
   }

   public static class UnmodifiableMap extends Double2IntFunctions.UnmodifiableFunction implements Double2IntMap, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Double2IntMap map;
      protected transient ObjectSet<Double2IntMap.Entry> entries;
      protected transient DoubleSet keys;
      protected transient IntCollection values;

      protected UnmodifiableMap(Double2IntMap m) {
         super(m);
         this.map = m;
      }

      @Override
      public boolean containsValue(int v) {
         return this.map.containsValue(v);
      }

      @Deprecated
      @Override
      public boolean containsValue(Object ov) {
         return this.map.containsValue(ov);
      }

      @Override
      public void putAll(Map<? extends Double, ? extends Integer> m) {
         throw new UnsupportedOperationException();
      }

      @Override
      public ObjectSet<Double2IntMap.Entry> double2IntEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSets.unmodifiable(this.map.double2IntEntrySet());
         }

         return this.entries;
      }

      @Deprecated
      @Override
      public ObjectSet<Entry<Double, Integer>> entrySet() {
         return this.double2IntEntrySet();
      }

      @Override
      public DoubleSet keySet() {
         if (this.keys == null) {
            this.keys = DoubleSets.unmodifiable(this.map.keySet());
         }

         return this.keys;
      }

      @Override
      public IntCollection values() {
         if (this.values == null) {
            this.values = IntCollections.unmodifiable(this.map.values());
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
      public int getOrDefault(double key, int defaultValue) {
         return this.map.getOrDefault(key, defaultValue);
      }

      @Override
      public void forEach(BiConsumer<? super Double, ? super Integer> action) {
         this.map.forEach(action);
      }

      @Override
      public void replaceAll(BiFunction<? super Double, ? super Integer, ? extends Integer> function) {
         throw new UnsupportedOperationException();
      }

      @Override
      public int putIfAbsent(double key, int value) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean remove(double key, int value) {
         throw new UnsupportedOperationException();
      }

      @Override
      public int replace(double key, int value) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean replace(double key, int oldValue, int newValue) {
         throw new UnsupportedOperationException();
      }

      @Override
      public int computeIfAbsent(double key, DoubleToIntFunction mappingFunction) {
         throw new UnsupportedOperationException();
      }

      @Override
      public int computeIfAbsentNullable(double key, DoubleFunction<? extends Integer> mappingFunction) {
         throw new UnsupportedOperationException();
      }

      @Override
      public int computeIfAbsent(double key, Double2IntFunction mappingFunction) {
         throw new UnsupportedOperationException();
      }

      @Override
      public int computeIfPresent(double key, BiFunction<? super Double, ? super Integer, ? extends Integer> remappingFunction) {
         throw new UnsupportedOperationException();
      }

      @Override
      public int compute(double key, BiFunction<? super Double, ? super Integer, ? extends Integer> remappingFunction) {
         throw new UnsupportedOperationException();
      }

      @Override
      public int merge(double key, int value, BiFunction<? super Integer, ? super Integer, ? extends Integer> remappingFunction) {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      @Override
      public Integer getOrDefault(Object key, Integer defaultValue) {
         return this.map.getOrDefault(key, defaultValue);
      }

      @Deprecated
      @Override
      public boolean remove(Object key, Object value) {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      @Override
      public Integer replace(Double key, Integer value) {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      @Override
      public boolean replace(Double key, Integer oldValue, Integer newValue) {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      @Override
      public Integer putIfAbsent(Double key, Integer value) {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      @Override
      public Integer computeIfAbsent(Double key, Function<? super Double, ? extends Integer> mappingFunction) {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      @Override
      public Integer computeIfPresent(Double key, BiFunction<? super Double, ? super Integer, ? extends Integer> remappingFunction) {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      @Override
      public Integer compute(Double key, BiFunction<? super Double, ? super Integer, ? extends Integer> remappingFunction) {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      @Override
      public Integer merge(Double key, Integer value, BiFunction<? super Integer, ? super Integer, ? extends Integer> remappingFunction) {
         throw new UnsupportedOperationException();
      }
   }
}
