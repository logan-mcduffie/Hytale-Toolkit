package it.unimi.dsi.fastutil.doubles;

import it.unimi.dsi.fastutil.HashCommon;
import it.unimi.dsi.fastutil.booleans.BooleanCollection;
import it.unimi.dsi.fastutil.booleans.BooleanCollections;
import it.unimi.dsi.fastutil.booleans.BooleanSets;
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

public final class Double2BooleanMaps {
   public static final Double2BooleanMaps.EmptyMap EMPTY_MAP = new Double2BooleanMaps.EmptyMap();

   private Double2BooleanMaps() {
   }

   public static ObjectIterator<Double2BooleanMap.Entry> fastIterator(Double2BooleanMap map) {
      ObjectSet<Double2BooleanMap.Entry> entries = map.double2BooleanEntrySet();
      return entries instanceof Double2BooleanMap.FastEntrySet ? ((Double2BooleanMap.FastEntrySet)entries).fastIterator() : entries.iterator();
   }

   public static void fastForEach(Double2BooleanMap map, Consumer<? super Double2BooleanMap.Entry> consumer) {
      ObjectSet<Double2BooleanMap.Entry> entries = map.double2BooleanEntrySet();
      if (entries instanceof Double2BooleanMap.FastEntrySet) {
         ((Double2BooleanMap.FastEntrySet)entries).fastForEach(consumer);
      } else {
         entries.forEach(consumer);
      }
   }

   public static ObjectIterable<Double2BooleanMap.Entry> fastIterable(Double2BooleanMap map) {
      final ObjectSet<Double2BooleanMap.Entry> entries = map.double2BooleanEntrySet();
      return (ObjectIterable<Double2BooleanMap.Entry>)(entries instanceof Double2BooleanMap.FastEntrySet ? new ObjectIterable<Double2BooleanMap.Entry>() {
         @Override
         public ObjectIterator<Double2BooleanMap.Entry> iterator() {
            return ((Double2BooleanMap.FastEntrySet)entries).fastIterator();
         }

         @Override
         public ObjectSpliterator<Double2BooleanMap.Entry> spliterator() {
            return entries.spliterator();
         }

         @Override
         public void forEach(Consumer<? super Double2BooleanMap.Entry> consumer) {
            ((Double2BooleanMap.FastEntrySet)entries).fastForEach(consumer);
         }
      } : entries);
   }

   public static Double2BooleanMap singleton(double key, boolean value) {
      return new Double2BooleanMaps.Singleton(key, value);
   }

   public static Double2BooleanMap singleton(Double key, Boolean value) {
      return new Double2BooleanMaps.Singleton(key, value);
   }

   public static Double2BooleanMap synchronize(Double2BooleanMap m) {
      return new Double2BooleanMaps.SynchronizedMap(m);
   }

   public static Double2BooleanMap synchronize(Double2BooleanMap m, Object sync) {
      return new Double2BooleanMaps.SynchronizedMap(m, sync);
   }

   public static Double2BooleanMap unmodifiable(Double2BooleanMap m) {
      return new Double2BooleanMaps.UnmodifiableMap(m);
   }

   public static class EmptyMap extends Double2BooleanFunctions.EmptyFunction implements Double2BooleanMap, Serializable, Cloneable {
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
      public boolean getOrDefault(double key, boolean defaultValue) {
         return defaultValue;
      }

      @Deprecated
      @Override
      public boolean containsValue(Object ov) {
         return false;
      }

      @Override
      public void putAll(Map<? extends Double, ? extends Boolean> m) {
         throw new UnsupportedOperationException();
      }

      @Override
      public ObjectSet<Double2BooleanMap.Entry> double2BooleanEntrySet() {
         return ObjectSets.EMPTY_SET;
      }

      @Override
      public DoubleSet keySet() {
         return DoubleSets.EMPTY_SET;
      }

      @Override
      public BooleanCollection values() {
         return BooleanSets.EMPTY_SET;
      }

      @Override
      public void forEach(BiConsumer<? super Double, ? super Boolean> consumer) {
      }

      @Override
      public Object clone() {
         return Double2BooleanMaps.EMPTY_MAP;
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

   public static class Singleton extends Double2BooleanFunctions.Singleton implements Double2BooleanMap, Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected transient ObjectSet<Double2BooleanMap.Entry> entries;
      protected transient DoubleSet keys;
      protected transient BooleanCollection values;

      protected Singleton(double key, boolean value) {
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
      public void putAll(Map<? extends Double, ? extends Boolean> m) {
         throw new UnsupportedOperationException();
      }

      @Override
      public ObjectSet<Double2BooleanMap.Entry> double2BooleanEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSets.singleton(new AbstractDouble2BooleanMap.BasicEntry(this.key, this.value));
         }

         return this.entries;
      }

      @Deprecated
      @Override
      public ObjectSet<Entry<Double, Boolean>> entrySet() {
         return this.double2BooleanEntrySet();
      }

      @Override
      public DoubleSet keySet() {
         if (this.keys == null) {
            this.keys = DoubleSets.singleton(this.key);
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
         return HashCommon.double2int(this.key) ^ (this.value ? 1231 : 1237);
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

   public static class SynchronizedMap extends Double2BooleanFunctions.SynchronizedFunction implements Double2BooleanMap, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Double2BooleanMap map;
      protected transient ObjectSet<Double2BooleanMap.Entry> entries;
      protected transient DoubleSet keys;
      protected transient BooleanCollection values;

      protected SynchronizedMap(Double2BooleanMap m, Object sync) {
         super(m, sync);
         this.map = m;
      }

      protected SynchronizedMap(Double2BooleanMap m) {
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
      public void putAll(Map<? extends Double, ? extends Boolean> m) {
         synchronized (this.sync) {
            this.map.putAll(m);
         }
      }

      @Override
      public ObjectSet<Double2BooleanMap.Entry> double2BooleanEntrySet() {
         synchronized (this.sync) {
            if (this.entries == null) {
               this.entries = ObjectSets.synchronize(this.map.double2BooleanEntrySet(), this.sync);
            }

            return this.entries;
         }
      }

      @Deprecated
      @Override
      public ObjectSet<Entry<Double, Boolean>> entrySet() {
         return this.double2BooleanEntrySet();
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
      public boolean getOrDefault(double key, boolean defaultValue) {
         synchronized (this.sync) {
            return this.map.getOrDefault(key, defaultValue);
         }
      }

      @Override
      public void forEach(BiConsumer<? super Double, ? super Boolean> action) {
         synchronized (this.sync) {
            this.map.forEach(action);
         }
      }

      @Override
      public void replaceAll(BiFunction<? super Double, ? super Boolean, ? extends Boolean> function) {
         synchronized (this.sync) {
            this.map.replaceAll(function);
         }
      }

      @Override
      public boolean putIfAbsent(double key, boolean value) {
         synchronized (this.sync) {
            return this.map.putIfAbsent(key, value);
         }
      }

      @Override
      public boolean remove(double key, boolean value) {
         synchronized (this.sync) {
            return this.map.remove(key, value);
         }
      }

      @Override
      public boolean replace(double key, boolean value) {
         synchronized (this.sync) {
            return this.map.replace(key, value);
         }
      }

      @Override
      public boolean replace(double key, boolean oldValue, boolean newValue) {
         synchronized (this.sync) {
            return this.map.replace(key, oldValue, newValue);
         }
      }

      @Override
      public boolean computeIfAbsent(double key, java.util.function.DoublePredicate mappingFunction) {
         synchronized (this.sync) {
            return this.map.computeIfAbsent(key, mappingFunction);
         }
      }

      @Override
      public boolean computeIfAbsentNullable(double key, DoubleFunction<? extends Boolean> mappingFunction) {
         synchronized (this.sync) {
            return this.map.computeIfAbsentNullable(key, mappingFunction);
         }
      }

      @Override
      public boolean computeIfAbsent(double key, Double2BooleanFunction mappingFunction) {
         synchronized (this.sync) {
            return this.map.computeIfAbsent(key, mappingFunction);
         }
      }

      @Override
      public boolean computeIfPresent(double key, BiFunction<? super Double, ? super Boolean, ? extends Boolean> remappingFunction) {
         synchronized (this.sync) {
            return this.map.computeIfPresent(key, remappingFunction);
         }
      }

      @Override
      public boolean compute(double key, BiFunction<? super Double, ? super Boolean, ? extends Boolean> remappingFunction) {
         synchronized (this.sync) {
            return this.map.compute(key, remappingFunction);
         }
      }

      @Override
      public boolean merge(double key, boolean value, BiFunction<? super Boolean, ? super Boolean, ? extends Boolean> remappingFunction) {
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
      public Boolean replace(Double key, Boolean value) {
         synchronized (this.sync) {
            return this.map.replace(key, value);
         }
      }

      @Deprecated
      public boolean replace(Double key, Boolean oldValue, Boolean newValue) {
         synchronized (this.sync) {
            return this.map.replace(key, oldValue, newValue);
         }
      }

      @Deprecated
      public Boolean putIfAbsent(Double key, Boolean value) {
         synchronized (this.sync) {
            return this.map.putIfAbsent(key, value);
         }
      }

      @Deprecated
      public Boolean computeIfAbsent(Double key, Function<? super Double, ? extends Boolean> mappingFunction) {
         synchronized (this.sync) {
            return this.map.computeIfAbsent(key, mappingFunction);
         }
      }

      @Deprecated
      public Boolean computeIfPresent(Double key, BiFunction<? super Double, ? super Boolean, ? extends Boolean> remappingFunction) {
         synchronized (this.sync) {
            return this.map.computeIfPresent(key, remappingFunction);
         }
      }

      @Deprecated
      public Boolean compute(Double key, BiFunction<? super Double, ? super Boolean, ? extends Boolean> remappingFunction) {
         synchronized (this.sync) {
            return this.map.compute(key, remappingFunction);
         }
      }

      @Deprecated
      public Boolean merge(Double key, Boolean value, BiFunction<? super Boolean, ? super Boolean, ? extends Boolean> remappingFunction) {
         synchronized (this.sync) {
            return this.map.merge(key, value, remappingFunction);
         }
      }
   }

   public static class UnmodifiableMap extends Double2BooleanFunctions.UnmodifiableFunction implements Double2BooleanMap, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Double2BooleanMap map;
      protected transient ObjectSet<Double2BooleanMap.Entry> entries;
      protected transient DoubleSet keys;
      protected transient BooleanCollection values;

      protected UnmodifiableMap(Double2BooleanMap m) {
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
      public void putAll(Map<? extends Double, ? extends Boolean> m) {
         throw new UnsupportedOperationException();
      }

      @Override
      public ObjectSet<Double2BooleanMap.Entry> double2BooleanEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSets.unmodifiable(this.map.double2BooleanEntrySet());
         }

         return this.entries;
      }

      @Deprecated
      @Override
      public ObjectSet<Entry<Double, Boolean>> entrySet() {
         return this.double2BooleanEntrySet();
      }

      @Override
      public DoubleSet keySet() {
         if (this.keys == null) {
            this.keys = DoubleSets.unmodifiable(this.map.keySet());
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
      public boolean getOrDefault(double key, boolean defaultValue) {
         return this.map.getOrDefault(key, defaultValue);
      }

      @Override
      public void forEach(BiConsumer<? super Double, ? super Boolean> action) {
         this.map.forEach(action);
      }

      @Override
      public void replaceAll(BiFunction<? super Double, ? super Boolean, ? extends Boolean> function) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean putIfAbsent(double key, boolean value) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean remove(double key, boolean value) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean replace(double key, boolean value) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean replace(double key, boolean oldValue, boolean newValue) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean computeIfAbsent(double key, java.util.function.DoublePredicate mappingFunction) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean computeIfAbsentNullable(double key, DoubleFunction<? extends Boolean> mappingFunction) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean computeIfAbsent(double key, Double2BooleanFunction mappingFunction) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean computeIfPresent(double key, BiFunction<? super Double, ? super Boolean, ? extends Boolean> remappingFunction) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean compute(double key, BiFunction<? super Double, ? super Boolean, ? extends Boolean> remappingFunction) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean merge(double key, boolean value, BiFunction<? super Boolean, ? super Boolean, ? extends Boolean> remappingFunction) {
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
      public Boolean replace(Double key, Boolean value) {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      public boolean replace(Double key, Boolean oldValue, Boolean newValue) {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      public Boolean putIfAbsent(Double key, Boolean value) {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      public Boolean computeIfAbsent(Double key, Function<? super Double, ? extends Boolean> mappingFunction) {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      public Boolean computeIfPresent(Double key, BiFunction<? super Double, ? super Boolean, ? extends Boolean> remappingFunction) {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      public Boolean compute(Double key, BiFunction<? super Double, ? super Boolean, ? extends Boolean> remappingFunction) {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      public Boolean merge(Double key, Boolean value, BiFunction<? super Boolean, ? super Boolean, ? extends Boolean> remappingFunction) {
         throw new UnsupportedOperationException();
      }
   }
}
