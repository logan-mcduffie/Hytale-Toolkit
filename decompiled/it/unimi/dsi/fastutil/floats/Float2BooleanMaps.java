package it.unimi.dsi.fastutil.floats;

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
import java.util.function.DoublePredicate;
import java.util.function.Function;

public final class Float2BooleanMaps {
   public static final Float2BooleanMaps.EmptyMap EMPTY_MAP = new Float2BooleanMaps.EmptyMap();

   private Float2BooleanMaps() {
   }

   public static ObjectIterator<Float2BooleanMap.Entry> fastIterator(Float2BooleanMap map) {
      ObjectSet<Float2BooleanMap.Entry> entries = map.float2BooleanEntrySet();
      return entries instanceof Float2BooleanMap.FastEntrySet ? ((Float2BooleanMap.FastEntrySet)entries).fastIterator() : entries.iterator();
   }

   public static void fastForEach(Float2BooleanMap map, Consumer<? super Float2BooleanMap.Entry> consumer) {
      ObjectSet<Float2BooleanMap.Entry> entries = map.float2BooleanEntrySet();
      if (entries instanceof Float2BooleanMap.FastEntrySet) {
         ((Float2BooleanMap.FastEntrySet)entries).fastForEach(consumer);
      } else {
         entries.forEach(consumer);
      }
   }

   public static ObjectIterable<Float2BooleanMap.Entry> fastIterable(Float2BooleanMap map) {
      final ObjectSet<Float2BooleanMap.Entry> entries = map.float2BooleanEntrySet();
      return (ObjectIterable<Float2BooleanMap.Entry>)(entries instanceof Float2BooleanMap.FastEntrySet ? new ObjectIterable<Float2BooleanMap.Entry>() {
         @Override
         public ObjectIterator<Float2BooleanMap.Entry> iterator() {
            return ((Float2BooleanMap.FastEntrySet)entries).fastIterator();
         }

         @Override
         public ObjectSpliterator<Float2BooleanMap.Entry> spliterator() {
            return entries.spliterator();
         }

         @Override
         public void forEach(Consumer<? super Float2BooleanMap.Entry> consumer) {
            ((Float2BooleanMap.FastEntrySet)entries).fastForEach(consumer);
         }
      } : entries);
   }

   public static Float2BooleanMap singleton(float key, boolean value) {
      return new Float2BooleanMaps.Singleton(key, value);
   }

   public static Float2BooleanMap singleton(Float key, Boolean value) {
      return new Float2BooleanMaps.Singleton(key, value);
   }

   public static Float2BooleanMap synchronize(Float2BooleanMap m) {
      return new Float2BooleanMaps.SynchronizedMap(m);
   }

   public static Float2BooleanMap synchronize(Float2BooleanMap m, Object sync) {
      return new Float2BooleanMaps.SynchronizedMap(m, sync);
   }

   public static Float2BooleanMap unmodifiable(Float2BooleanMap m) {
      return new Float2BooleanMaps.UnmodifiableMap(m);
   }

   public static class EmptyMap extends Float2BooleanFunctions.EmptyFunction implements Float2BooleanMap, Serializable, Cloneable {
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
      public boolean getOrDefault(float key, boolean defaultValue) {
         return defaultValue;
      }

      @Deprecated
      @Override
      public boolean containsValue(Object ov) {
         return false;
      }

      @Override
      public void putAll(Map<? extends Float, ? extends Boolean> m) {
         throw new UnsupportedOperationException();
      }

      @Override
      public ObjectSet<Float2BooleanMap.Entry> float2BooleanEntrySet() {
         return ObjectSets.EMPTY_SET;
      }

      @Override
      public FloatSet keySet() {
         return FloatSets.EMPTY_SET;
      }

      @Override
      public BooleanCollection values() {
         return BooleanSets.EMPTY_SET;
      }

      @Override
      public void forEach(BiConsumer<? super Float, ? super Boolean> consumer) {
      }

      @Override
      public Object clone() {
         return Float2BooleanMaps.EMPTY_MAP;
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

   public static class Singleton extends Float2BooleanFunctions.Singleton implements Float2BooleanMap, Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected transient ObjectSet<Float2BooleanMap.Entry> entries;
      protected transient FloatSet keys;
      protected transient BooleanCollection values;

      protected Singleton(float key, boolean value) {
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
      public void putAll(Map<? extends Float, ? extends Boolean> m) {
         throw new UnsupportedOperationException();
      }

      @Override
      public ObjectSet<Float2BooleanMap.Entry> float2BooleanEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSets.singleton(new AbstractFloat2BooleanMap.BasicEntry(this.key, this.value));
         }

         return this.entries;
      }

      @Deprecated
      @Override
      public ObjectSet<Entry<Float, Boolean>> entrySet() {
         return this.float2BooleanEntrySet();
      }

      @Override
      public FloatSet keySet() {
         if (this.keys == null) {
            this.keys = FloatSets.singleton(this.key);
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
         return HashCommon.float2int(this.key) ^ (this.value ? 1231 : 1237);
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

   public static class SynchronizedMap extends Float2BooleanFunctions.SynchronizedFunction implements Float2BooleanMap, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Float2BooleanMap map;
      protected transient ObjectSet<Float2BooleanMap.Entry> entries;
      protected transient FloatSet keys;
      protected transient BooleanCollection values;

      protected SynchronizedMap(Float2BooleanMap m, Object sync) {
         super(m, sync);
         this.map = m;
      }

      protected SynchronizedMap(Float2BooleanMap m) {
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
      public void putAll(Map<? extends Float, ? extends Boolean> m) {
         synchronized (this.sync) {
            this.map.putAll(m);
         }
      }

      @Override
      public ObjectSet<Float2BooleanMap.Entry> float2BooleanEntrySet() {
         synchronized (this.sync) {
            if (this.entries == null) {
               this.entries = ObjectSets.synchronize(this.map.float2BooleanEntrySet(), this.sync);
            }

            return this.entries;
         }
      }

      @Deprecated
      @Override
      public ObjectSet<Entry<Float, Boolean>> entrySet() {
         return this.float2BooleanEntrySet();
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
      public boolean getOrDefault(float key, boolean defaultValue) {
         synchronized (this.sync) {
            return this.map.getOrDefault(key, defaultValue);
         }
      }

      @Override
      public void forEach(BiConsumer<? super Float, ? super Boolean> action) {
         synchronized (this.sync) {
            this.map.forEach(action);
         }
      }

      @Override
      public void replaceAll(BiFunction<? super Float, ? super Boolean, ? extends Boolean> function) {
         synchronized (this.sync) {
            this.map.replaceAll(function);
         }
      }

      @Override
      public boolean putIfAbsent(float key, boolean value) {
         synchronized (this.sync) {
            return this.map.putIfAbsent(key, value);
         }
      }

      @Override
      public boolean remove(float key, boolean value) {
         synchronized (this.sync) {
            return this.map.remove(key, value);
         }
      }

      @Override
      public boolean replace(float key, boolean value) {
         synchronized (this.sync) {
            return this.map.replace(key, value);
         }
      }

      @Override
      public boolean replace(float key, boolean oldValue, boolean newValue) {
         synchronized (this.sync) {
            return this.map.replace(key, oldValue, newValue);
         }
      }

      @Override
      public boolean computeIfAbsent(float key, DoublePredicate mappingFunction) {
         synchronized (this.sync) {
            return this.map.computeIfAbsent(key, mappingFunction);
         }
      }

      @Override
      public boolean computeIfAbsentNullable(float key, DoubleFunction<? extends Boolean> mappingFunction) {
         synchronized (this.sync) {
            return this.map.computeIfAbsentNullable(key, mappingFunction);
         }
      }

      @Override
      public boolean computeIfAbsent(float key, Float2BooleanFunction mappingFunction) {
         synchronized (this.sync) {
            return this.map.computeIfAbsent(key, mappingFunction);
         }
      }

      @Override
      public boolean computeIfPresent(float key, BiFunction<? super Float, ? super Boolean, ? extends Boolean> remappingFunction) {
         synchronized (this.sync) {
            return this.map.computeIfPresent(key, remappingFunction);
         }
      }

      @Override
      public boolean compute(float key, BiFunction<? super Float, ? super Boolean, ? extends Boolean> remappingFunction) {
         synchronized (this.sync) {
            return this.map.compute(key, remappingFunction);
         }
      }

      @Override
      public boolean merge(float key, boolean value, BiFunction<? super Boolean, ? super Boolean, ? extends Boolean> remappingFunction) {
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
      public Boolean replace(Float key, Boolean value) {
         synchronized (this.sync) {
            return this.map.replace(key, value);
         }
      }

      @Deprecated
      public boolean replace(Float key, Boolean oldValue, Boolean newValue) {
         synchronized (this.sync) {
            return this.map.replace(key, oldValue, newValue);
         }
      }

      @Deprecated
      public Boolean putIfAbsent(Float key, Boolean value) {
         synchronized (this.sync) {
            return this.map.putIfAbsent(key, value);
         }
      }

      @Deprecated
      public Boolean computeIfAbsent(Float key, Function<? super Float, ? extends Boolean> mappingFunction) {
         synchronized (this.sync) {
            return this.map.computeIfAbsent(key, mappingFunction);
         }
      }

      @Deprecated
      public Boolean computeIfPresent(Float key, BiFunction<? super Float, ? super Boolean, ? extends Boolean> remappingFunction) {
         synchronized (this.sync) {
            return this.map.computeIfPresent(key, remappingFunction);
         }
      }

      @Deprecated
      public Boolean compute(Float key, BiFunction<? super Float, ? super Boolean, ? extends Boolean> remappingFunction) {
         synchronized (this.sync) {
            return this.map.compute(key, remappingFunction);
         }
      }

      @Deprecated
      public Boolean merge(Float key, Boolean value, BiFunction<? super Boolean, ? super Boolean, ? extends Boolean> remappingFunction) {
         synchronized (this.sync) {
            return this.map.merge(key, value, remappingFunction);
         }
      }
   }

   public static class UnmodifiableMap extends Float2BooleanFunctions.UnmodifiableFunction implements Float2BooleanMap, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Float2BooleanMap map;
      protected transient ObjectSet<Float2BooleanMap.Entry> entries;
      protected transient FloatSet keys;
      protected transient BooleanCollection values;

      protected UnmodifiableMap(Float2BooleanMap m) {
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
      public void putAll(Map<? extends Float, ? extends Boolean> m) {
         throw new UnsupportedOperationException();
      }

      @Override
      public ObjectSet<Float2BooleanMap.Entry> float2BooleanEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSets.unmodifiable(this.map.float2BooleanEntrySet());
         }

         return this.entries;
      }

      @Deprecated
      @Override
      public ObjectSet<Entry<Float, Boolean>> entrySet() {
         return this.float2BooleanEntrySet();
      }

      @Override
      public FloatSet keySet() {
         if (this.keys == null) {
            this.keys = FloatSets.unmodifiable(this.map.keySet());
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
      public boolean getOrDefault(float key, boolean defaultValue) {
         return this.map.getOrDefault(key, defaultValue);
      }

      @Override
      public void forEach(BiConsumer<? super Float, ? super Boolean> action) {
         this.map.forEach(action);
      }

      @Override
      public void replaceAll(BiFunction<? super Float, ? super Boolean, ? extends Boolean> function) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean putIfAbsent(float key, boolean value) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean remove(float key, boolean value) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean replace(float key, boolean value) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean replace(float key, boolean oldValue, boolean newValue) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean computeIfAbsent(float key, DoublePredicate mappingFunction) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean computeIfAbsentNullable(float key, DoubleFunction<? extends Boolean> mappingFunction) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean computeIfAbsent(float key, Float2BooleanFunction mappingFunction) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean computeIfPresent(float key, BiFunction<? super Float, ? super Boolean, ? extends Boolean> remappingFunction) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean compute(float key, BiFunction<? super Float, ? super Boolean, ? extends Boolean> remappingFunction) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean merge(float key, boolean value, BiFunction<? super Boolean, ? super Boolean, ? extends Boolean> remappingFunction) {
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
      public Boolean replace(Float key, Boolean value) {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      public boolean replace(Float key, Boolean oldValue, Boolean newValue) {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      public Boolean putIfAbsent(Float key, Boolean value) {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      public Boolean computeIfAbsent(Float key, Function<? super Float, ? extends Boolean> mappingFunction) {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      public Boolean computeIfPresent(Float key, BiFunction<? super Float, ? super Boolean, ? extends Boolean> remappingFunction) {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      public Boolean compute(Float key, BiFunction<? super Float, ? super Boolean, ? extends Boolean> remappingFunction) {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      public Boolean merge(Float key, Boolean value, BiFunction<? super Boolean, ? super Boolean, ? extends Boolean> remappingFunction) {
         throw new UnsupportedOperationException();
      }
   }
}
