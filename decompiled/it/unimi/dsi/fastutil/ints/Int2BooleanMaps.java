package it.unimi.dsi.fastutil.ints;

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
import java.util.function.Function;
import java.util.function.IntFunction;

public final class Int2BooleanMaps {
   public static final Int2BooleanMaps.EmptyMap EMPTY_MAP = new Int2BooleanMaps.EmptyMap();

   private Int2BooleanMaps() {
   }

   public static ObjectIterator<Int2BooleanMap.Entry> fastIterator(Int2BooleanMap map) {
      ObjectSet<Int2BooleanMap.Entry> entries = map.int2BooleanEntrySet();
      return entries instanceof Int2BooleanMap.FastEntrySet ? ((Int2BooleanMap.FastEntrySet)entries).fastIterator() : entries.iterator();
   }

   public static void fastForEach(Int2BooleanMap map, Consumer<? super Int2BooleanMap.Entry> consumer) {
      ObjectSet<Int2BooleanMap.Entry> entries = map.int2BooleanEntrySet();
      if (entries instanceof Int2BooleanMap.FastEntrySet) {
         ((Int2BooleanMap.FastEntrySet)entries).fastForEach(consumer);
      } else {
         entries.forEach(consumer);
      }
   }

   public static ObjectIterable<Int2BooleanMap.Entry> fastIterable(Int2BooleanMap map) {
      final ObjectSet<Int2BooleanMap.Entry> entries = map.int2BooleanEntrySet();
      return (ObjectIterable<Int2BooleanMap.Entry>)(entries instanceof Int2BooleanMap.FastEntrySet ? new ObjectIterable<Int2BooleanMap.Entry>() {
         @Override
         public ObjectIterator<Int2BooleanMap.Entry> iterator() {
            return ((Int2BooleanMap.FastEntrySet)entries).fastIterator();
         }

         @Override
         public ObjectSpliterator<Int2BooleanMap.Entry> spliterator() {
            return entries.spliterator();
         }

         @Override
         public void forEach(Consumer<? super Int2BooleanMap.Entry> consumer) {
            ((Int2BooleanMap.FastEntrySet)entries).fastForEach(consumer);
         }
      } : entries);
   }

   public static Int2BooleanMap singleton(int key, boolean value) {
      return new Int2BooleanMaps.Singleton(key, value);
   }

   public static Int2BooleanMap singleton(Integer key, Boolean value) {
      return new Int2BooleanMaps.Singleton(key, value);
   }

   public static Int2BooleanMap synchronize(Int2BooleanMap m) {
      return new Int2BooleanMaps.SynchronizedMap(m);
   }

   public static Int2BooleanMap synchronize(Int2BooleanMap m, Object sync) {
      return new Int2BooleanMaps.SynchronizedMap(m, sync);
   }

   public static Int2BooleanMap unmodifiable(Int2BooleanMap m) {
      return new Int2BooleanMaps.UnmodifiableMap(m);
   }

   public static class EmptyMap extends Int2BooleanFunctions.EmptyFunction implements Int2BooleanMap, Serializable, Cloneable {
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
      public boolean getOrDefault(int key, boolean defaultValue) {
         return defaultValue;
      }

      @Deprecated
      @Override
      public boolean containsValue(Object ov) {
         return false;
      }

      @Override
      public void putAll(Map<? extends Integer, ? extends Boolean> m) {
         throw new UnsupportedOperationException();
      }

      @Override
      public ObjectSet<Int2BooleanMap.Entry> int2BooleanEntrySet() {
         return ObjectSets.EMPTY_SET;
      }

      @Override
      public IntSet keySet() {
         return IntSets.EMPTY_SET;
      }

      @Override
      public BooleanCollection values() {
         return BooleanSets.EMPTY_SET;
      }

      @Override
      public void forEach(BiConsumer<? super Integer, ? super Boolean> consumer) {
      }

      @Override
      public Object clone() {
         return Int2BooleanMaps.EMPTY_MAP;
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

   public static class Singleton extends Int2BooleanFunctions.Singleton implements Int2BooleanMap, Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected transient ObjectSet<Int2BooleanMap.Entry> entries;
      protected transient IntSet keys;
      protected transient BooleanCollection values;

      protected Singleton(int key, boolean value) {
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
      public void putAll(Map<? extends Integer, ? extends Boolean> m) {
         throw new UnsupportedOperationException();
      }

      @Override
      public ObjectSet<Int2BooleanMap.Entry> int2BooleanEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSets.singleton(new AbstractInt2BooleanMap.BasicEntry(this.key, this.value));
         }

         return this.entries;
      }

      @Deprecated
      @Override
      public ObjectSet<Entry<Integer, Boolean>> entrySet() {
         return this.int2BooleanEntrySet();
      }

      @Override
      public IntSet keySet() {
         if (this.keys == null) {
            this.keys = IntSets.singleton(this.key);
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
         return this.key ^ (this.value ? 1231 : 1237);
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

   public static class SynchronizedMap extends Int2BooleanFunctions.SynchronizedFunction implements Int2BooleanMap, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Int2BooleanMap map;
      protected transient ObjectSet<Int2BooleanMap.Entry> entries;
      protected transient IntSet keys;
      protected transient BooleanCollection values;

      protected SynchronizedMap(Int2BooleanMap m, Object sync) {
         super(m, sync);
         this.map = m;
      }

      protected SynchronizedMap(Int2BooleanMap m) {
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
      public void putAll(Map<? extends Integer, ? extends Boolean> m) {
         synchronized (this.sync) {
            this.map.putAll(m);
         }
      }

      @Override
      public ObjectSet<Int2BooleanMap.Entry> int2BooleanEntrySet() {
         synchronized (this.sync) {
            if (this.entries == null) {
               this.entries = ObjectSets.synchronize(this.map.int2BooleanEntrySet(), this.sync);
            }

            return this.entries;
         }
      }

      @Deprecated
      @Override
      public ObjectSet<Entry<Integer, Boolean>> entrySet() {
         return this.int2BooleanEntrySet();
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
      public boolean getOrDefault(int key, boolean defaultValue) {
         synchronized (this.sync) {
            return this.map.getOrDefault(key, defaultValue);
         }
      }

      @Override
      public void forEach(BiConsumer<? super Integer, ? super Boolean> action) {
         synchronized (this.sync) {
            this.map.forEach(action);
         }
      }

      @Override
      public void replaceAll(BiFunction<? super Integer, ? super Boolean, ? extends Boolean> function) {
         synchronized (this.sync) {
            this.map.replaceAll(function);
         }
      }

      @Override
      public boolean putIfAbsent(int key, boolean value) {
         synchronized (this.sync) {
            return this.map.putIfAbsent(key, value);
         }
      }

      @Override
      public boolean remove(int key, boolean value) {
         synchronized (this.sync) {
            return this.map.remove(key, value);
         }
      }

      @Override
      public boolean replace(int key, boolean value) {
         synchronized (this.sync) {
            return this.map.replace(key, value);
         }
      }

      @Override
      public boolean replace(int key, boolean oldValue, boolean newValue) {
         synchronized (this.sync) {
            return this.map.replace(key, oldValue, newValue);
         }
      }

      @Override
      public boolean computeIfAbsent(int key, java.util.function.IntPredicate mappingFunction) {
         synchronized (this.sync) {
            return this.map.computeIfAbsent(key, mappingFunction);
         }
      }

      @Override
      public boolean computeIfAbsentNullable(int key, IntFunction<? extends Boolean> mappingFunction) {
         synchronized (this.sync) {
            return this.map.computeIfAbsentNullable(key, mappingFunction);
         }
      }

      @Override
      public boolean computeIfAbsent(int key, Int2BooleanFunction mappingFunction) {
         synchronized (this.sync) {
            return this.map.computeIfAbsent(key, mappingFunction);
         }
      }

      @Override
      public boolean computeIfPresent(int key, BiFunction<? super Integer, ? super Boolean, ? extends Boolean> remappingFunction) {
         synchronized (this.sync) {
            return this.map.computeIfPresent(key, remappingFunction);
         }
      }

      @Override
      public boolean compute(int key, BiFunction<? super Integer, ? super Boolean, ? extends Boolean> remappingFunction) {
         synchronized (this.sync) {
            return this.map.compute(key, remappingFunction);
         }
      }

      @Override
      public boolean merge(int key, boolean value, BiFunction<? super Boolean, ? super Boolean, ? extends Boolean> remappingFunction) {
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
      public Boolean replace(Integer key, Boolean value) {
         synchronized (this.sync) {
            return this.map.replace(key, value);
         }
      }

      @Deprecated
      public boolean replace(Integer key, Boolean oldValue, Boolean newValue) {
         synchronized (this.sync) {
            return this.map.replace(key, oldValue, newValue);
         }
      }

      @Deprecated
      public Boolean putIfAbsent(Integer key, Boolean value) {
         synchronized (this.sync) {
            return this.map.putIfAbsent(key, value);
         }
      }

      @Deprecated
      public Boolean computeIfAbsent(Integer key, Function<? super Integer, ? extends Boolean> mappingFunction) {
         synchronized (this.sync) {
            return this.map.computeIfAbsent(key, mappingFunction);
         }
      }

      @Deprecated
      public Boolean computeIfPresent(Integer key, BiFunction<? super Integer, ? super Boolean, ? extends Boolean> remappingFunction) {
         synchronized (this.sync) {
            return this.map.computeIfPresent(key, remappingFunction);
         }
      }

      @Deprecated
      public Boolean compute(Integer key, BiFunction<? super Integer, ? super Boolean, ? extends Boolean> remappingFunction) {
         synchronized (this.sync) {
            return this.map.compute(key, remappingFunction);
         }
      }

      @Deprecated
      public Boolean merge(Integer key, Boolean value, BiFunction<? super Boolean, ? super Boolean, ? extends Boolean> remappingFunction) {
         synchronized (this.sync) {
            return this.map.merge(key, value, remappingFunction);
         }
      }
   }

   public static class UnmodifiableMap extends Int2BooleanFunctions.UnmodifiableFunction implements Int2BooleanMap, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Int2BooleanMap map;
      protected transient ObjectSet<Int2BooleanMap.Entry> entries;
      protected transient IntSet keys;
      protected transient BooleanCollection values;

      protected UnmodifiableMap(Int2BooleanMap m) {
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
      public void putAll(Map<? extends Integer, ? extends Boolean> m) {
         throw new UnsupportedOperationException();
      }

      @Override
      public ObjectSet<Int2BooleanMap.Entry> int2BooleanEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSets.unmodifiable(this.map.int2BooleanEntrySet());
         }

         return this.entries;
      }

      @Deprecated
      @Override
      public ObjectSet<Entry<Integer, Boolean>> entrySet() {
         return this.int2BooleanEntrySet();
      }

      @Override
      public IntSet keySet() {
         if (this.keys == null) {
            this.keys = IntSets.unmodifiable(this.map.keySet());
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
      public boolean getOrDefault(int key, boolean defaultValue) {
         return this.map.getOrDefault(key, defaultValue);
      }

      @Override
      public void forEach(BiConsumer<? super Integer, ? super Boolean> action) {
         this.map.forEach(action);
      }

      @Override
      public void replaceAll(BiFunction<? super Integer, ? super Boolean, ? extends Boolean> function) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean putIfAbsent(int key, boolean value) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean remove(int key, boolean value) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean replace(int key, boolean value) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean replace(int key, boolean oldValue, boolean newValue) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean computeIfAbsent(int key, java.util.function.IntPredicate mappingFunction) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean computeIfAbsentNullable(int key, IntFunction<? extends Boolean> mappingFunction) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean computeIfAbsent(int key, Int2BooleanFunction mappingFunction) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean computeIfPresent(int key, BiFunction<? super Integer, ? super Boolean, ? extends Boolean> remappingFunction) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean compute(int key, BiFunction<? super Integer, ? super Boolean, ? extends Boolean> remappingFunction) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean merge(int key, boolean value, BiFunction<? super Boolean, ? super Boolean, ? extends Boolean> remappingFunction) {
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
      public Boolean replace(Integer key, Boolean value) {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      public boolean replace(Integer key, Boolean oldValue, Boolean newValue) {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      public Boolean putIfAbsent(Integer key, Boolean value) {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      public Boolean computeIfAbsent(Integer key, Function<? super Integer, ? extends Boolean> mappingFunction) {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      public Boolean computeIfPresent(Integer key, BiFunction<? super Integer, ? super Boolean, ? extends Boolean> remappingFunction) {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      public Boolean compute(Integer key, BiFunction<? super Integer, ? super Boolean, ? extends Boolean> remappingFunction) {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      public Boolean merge(Integer key, Boolean value, BiFunction<? super Boolean, ? super Boolean, ? extends Boolean> remappingFunction) {
         throw new UnsupportedOperationException();
      }
   }
}
