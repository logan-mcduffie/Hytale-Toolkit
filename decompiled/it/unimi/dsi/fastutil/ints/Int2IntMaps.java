package it.unimi.dsi.fastutil.ints;

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

public final class Int2IntMaps {
   public static final Int2IntMaps.EmptyMap EMPTY_MAP = new Int2IntMaps.EmptyMap();

   private Int2IntMaps() {
   }

   public static ObjectIterator<Int2IntMap.Entry> fastIterator(Int2IntMap map) {
      ObjectSet<Int2IntMap.Entry> entries = map.int2IntEntrySet();
      return entries instanceof Int2IntMap.FastEntrySet ? ((Int2IntMap.FastEntrySet)entries).fastIterator() : entries.iterator();
   }

   public static void fastForEach(Int2IntMap map, Consumer<? super Int2IntMap.Entry> consumer) {
      ObjectSet<Int2IntMap.Entry> entries = map.int2IntEntrySet();
      if (entries instanceof Int2IntMap.FastEntrySet) {
         ((Int2IntMap.FastEntrySet)entries).fastForEach(consumer);
      } else {
         entries.forEach(consumer);
      }
   }

   public static ObjectIterable<Int2IntMap.Entry> fastIterable(Int2IntMap map) {
      final ObjectSet<Int2IntMap.Entry> entries = map.int2IntEntrySet();
      return (ObjectIterable<Int2IntMap.Entry>)(entries instanceof Int2IntMap.FastEntrySet ? new ObjectIterable<Int2IntMap.Entry>() {
         @Override
         public ObjectIterator<Int2IntMap.Entry> iterator() {
            return ((Int2IntMap.FastEntrySet)entries).fastIterator();
         }

         @Override
         public ObjectSpliterator<Int2IntMap.Entry> spliterator() {
            return entries.spliterator();
         }

         @Override
         public void forEach(Consumer<? super Int2IntMap.Entry> consumer) {
            ((Int2IntMap.FastEntrySet)entries).fastForEach(consumer);
         }
      } : entries);
   }

   public static Int2IntMap singleton(int key, int value) {
      return new Int2IntMaps.Singleton(key, value);
   }

   public static Int2IntMap singleton(Integer key, Integer value) {
      return new Int2IntMaps.Singleton(key, value);
   }

   public static Int2IntMap synchronize(Int2IntMap m) {
      return new Int2IntMaps.SynchronizedMap(m);
   }

   public static Int2IntMap synchronize(Int2IntMap m, Object sync) {
      return new Int2IntMaps.SynchronizedMap(m, sync);
   }

   public static Int2IntMap unmodifiable(Int2IntMap m) {
      return new Int2IntMaps.UnmodifiableMap(m);
   }

   public static class EmptyMap extends Int2IntFunctions.EmptyFunction implements Int2IntMap, Serializable, Cloneable {
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
      public int getOrDefault(int key, int defaultValue) {
         return defaultValue;
      }

      @Deprecated
      @Override
      public boolean containsValue(Object ov) {
         return false;
      }

      @Override
      public void putAll(Map<? extends Integer, ? extends Integer> m) {
         throw new UnsupportedOperationException();
      }

      @Override
      public ObjectSet<Int2IntMap.Entry> int2IntEntrySet() {
         return ObjectSets.EMPTY_SET;
      }

      @Override
      public IntSet keySet() {
         return IntSets.EMPTY_SET;
      }

      @Override
      public IntCollection values() {
         return IntSets.EMPTY_SET;
      }

      @Override
      public void forEach(BiConsumer<? super Integer, ? super Integer> consumer) {
      }

      @Override
      public Object clone() {
         return Int2IntMaps.EMPTY_MAP;
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

   public static class Singleton extends Int2IntFunctions.Singleton implements Int2IntMap, Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected transient ObjectSet<Int2IntMap.Entry> entries;
      protected transient IntSet keys;
      protected transient IntCollection values;

      protected Singleton(int key, int value) {
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
      public void putAll(Map<? extends Integer, ? extends Integer> m) {
         throw new UnsupportedOperationException();
      }

      @Override
      public ObjectSet<Int2IntMap.Entry> int2IntEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSets.singleton(new AbstractInt2IntMap.BasicEntry(this.key, this.value));
         }

         return this.entries;
      }

      @Deprecated
      @Override
      public ObjectSet<Entry<Integer, Integer>> entrySet() {
         return this.int2IntEntrySet();
      }

      @Override
      public IntSet keySet() {
         if (this.keys == null) {
            this.keys = IntSets.singleton(this.key);
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
         return this.key ^ this.value;
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

   public static class SynchronizedMap extends Int2IntFunctions.SynchronizedFunction implements Int2IntMap, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Int2IntMap map;
      protected transient ObjectSet<Int2IntMap.Entry> entries;
      protected transient IntSet keys;
      protected transient IntCollection values;

      protected SynchronizedMap(Int2IntMap m, Object sync) {
         super(m, sync);
         this.map = m;
      }

      protected SynchronizedMap(Int2IntMap m) {
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
      public void putAll(Map<? extends Integer, ? extends Integer> m) {
         synchronized (this.sync) {
            this.map.putAll(m);
         }
      }

      @Override
      public ObjectSet<Int2IntMap.Entry> int2IntEntrySet() {
         synchronized (this.sync) {
            if (this.entries == null) {
               this.entries = ObjectSets.synchronize(this.map.int2IntEntrySet(), this.sync);
            }

            return this.entries;
         }
      }

      @Deprecated
      @Override
      public ObjectSet<Entry<Integer, Integer>> entrySet() {
         return this.int2IntEntrySet();
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
      public int getOrDefault(int key, int defaultValue) {
         synchronized (this.sync) {
            return this.map.getOrDefault(key, defaultValue);
         }
      }

      @Override
      public void forEach(BiConsumer<? super Integer, ? super Integer> action) {
         synchronized (this.sync) {
            this.map.forEach(action);
         }
      }

      @Override
      public void replaceAll(BiFunction<? super Integer, ? super Integer, ? extends Integer> function) {
         synchronized (this.sync) {
            this.map.replaceAll(function);
         }
      }

      @Override
      public int putIfAbsent(int key, int value) {
         synchronized (this.sync) {
            return this.map.putIfAbsent(key, value);
         }
      }

      @Override
      public boolean remove(int key, int value) {
         synchronized (this.sync) {
            return this.map.remove(key, value);
         }
      }

      @Override
      public int replace(int key, int value) {
         synchronized (this.sync) {
            return this.map.replace(key, value);
         }
      }

      @Override
      public boolean replace(int key, int oldValue, int newValue) {
         synchronized (this.sync) {
            return this.map.replace(key, oldValue, newValue);
         }
      }

      @Override
      public int computeIfAbsent(int key, java.util.function.IntUnaryOperator mappingFunction) {
         synchronized (this.sync) {
            return this.map.computeIfAbsent(key, mappingFunction);
         }
      }

      @Override
      public int computeIfAbsentNullable(int key, IntFunction<? extends Integer> mappingFunction) {
         synchronized (this.sync) {
            return this.map.computeIfAbsentNullable(key, mappingFunction);
         }
      }

      @Override
      public int computeIfAbsent(int key, Int2IntFunction mappingFunction) {
         synchronized (this.sync) {
            return this.map.computeIfAbsent(key, mappingFunction);
         }
      }

      @Override
      public int computeIfPresent(int key, BiFunction<? super Integer, ? super Integer, ? extends Integer> remappingFunction) {
         synchronized (this.sync) {
            return this.map.computeIfPresent(key, remappingFunction);
         }
      }

      @Override
      public int compute(int key, BiFunction<? super Integer, ? super Integer, ? extends Integer> remappingFunction) {
         synchronized (this.sync) {
            return this.map.compute(key, remappingFunction);
         }
      }

      @Override
      public int merge(int key, int value, BiFunction<? super Integer, ? super Integer, ? extends Integer> remappingFunction) {
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
      public Integer replace(Integer key, Integer value) {
         synchronized (this.sync) {
            return this.map.replace(key, value);
         }
      }

      @Deprecated
      @Override
      public boolean replace(Integer key, Integer oldValue, Integer newValue) {
         synchronized (this.sync) {
            return this.map.replace(key, oldValue, newValue);
         }
      }

      @Deprecated
      @Override
      public Integer putIfAbsent(Integer key, Integer value) {
         synchronized (this.sync) {
            return this.map.putIfAbsent(key, value);
         }
      }

      @Deprecated
      @Override
      public Integer computeIfAbsent(Integer key, Function<? super Integer, ? extends Integer> mappingFunction) {
         synchronized (this.sync) {
            return this.map.computeIfAbsent(key, mappingFunction);
         }
      }

      @Deprecated
      @Override
      public Integer computeIfPresent(Integer key, BiFunction<? super Integer, ? super Integer, ? extends Integer> remappingFunction) {
         synchronized (this.sync) {
            return this.map.computeIfPresent(key, remappingFunction);
         }
      }

      @Deprecated
      @Override
      public Integer compute(Integer key, BiFunction<? super Integer, ? super Integer, ? extends Integer> remappingFunction) {
         synchronized (this.sync) {
            return this.map.compute(key, remappingFunction);
         }
      }

      @Deprecated
      @Override
      public Integer merge(Integer key, Integer value, BiFunction<? super Integer, ? super Integer, ? extends Integer> remappingFunction) {
         synchronized (this.sync) {
            return this.map.merge(key, value, remappingFunction);
         }
      }
   }

   public static class UnmodifiableMap extends Int2IntFunctions.UnmodifiableFunction implements Int2IntMap, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Int2IntMap map;
      protected transient ObjectSet<Int2IntMap.Entry> entries;
      protected transient IntSet keys;
      protected transient IntCollection values;

      protected UnmodifiableMap(Int2IntMap m) {
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
      public void putAll(Map<? extends Integer, ? extends Integer> m) {
         throw new UnsupportedOperationException();
      }

      @Override
      public ObjectSet<Int2IntMap.Entry> int2IntEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSets.unmodifiable(this.map.int2IntEntrySet());
         }

         return this.entries;
      }

      @Deprecated
      @Override
      public ObjectSet<Entry<Integer, Integer>> entrySet() {
         return this.int2IntEntrySet();
      }

      @Override
      public IntSet keySet() {
         if (this.keys == null) {
            this.keys = IntSets.unmodifiable(this.map.keySet());
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
      public int getOrDefault(int key, int defaultValue) {
         return this.map.getOrDefault(key, defaultValue);
      }

      @Override
      public void forEach(BiConsumer<? super Integer, ? super Integer> action) {
         this.map.forEach(action);
      }

      @Override
      public void replaceAll(BiFunction<? super Integer, ? super Integer, ? extends Integer> function) {
         throw new UnsupportedOperationException();
      }

      @Override
      public int putIfAbsent(int key, int value) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean remove(int key, int value) {
         throw new UnsupportedOperationException();
      }

      @Override
      public int replace(int key, int value) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean replace(int key, int oldValue, int newValue) {
         throw new UnsupportedOperationException();
      }

      @Override
      public int computeIfAbsent(int key, java.util.function.IntUnaryOperator mappingFunction) {
         throw new UnsupportedOperationException();
      }

      @Override
      public int computeIfAbsentNullable(int key, IntFunction<? extends Integer> mappingFunction) {
         throw new UnsupportedOperationException();
      }

      @Override
      public int computeIfAbsent(int key, Int2IntFunction mappingFunction) {
         throw new UnsupportedOperationException();
      }

      @Override
      public int computeIfPresent(int key, BiFunction<? super Integer, ? super Integer, ? extends Integer> remappingFunction) {
         throw new UnsupportedOperationException();
      }

      @Override
      public int compute(int key, BiFunction<? super Integer, ? super Integer, ? extends Integer> remappingFunction) {
         throw new UnsupportedOperationException();
      }

      @Override
      public int merge(int key, int value, BiFunction<? super Integer, ? super Integer, ? extends Integer> remappingFunction) {
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
      public Integer replace(Integer key, Integer value) {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      @Override
      public boolean replace(Integer key, Integer oldValue, Integer newValue) {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      @Override
      public Integer putIfAbsent(Integer key, Integer value) {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      @Override
      public Integer computeIfAbsent(Integer key, Function<? super Integer, ? extends Integer> mappingFunction) {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      @Override
      public Integer computeIfPresent(Integer key, BiFunction<? super Integer, ? super Integer, ? extends Integer> remappingFunction) {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      @Override
      public Integer compute(Integer key, BiFunction<? super Integer, ? super Integer, ? extends Integer> remappingFunction) {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      @Override
      public Integer merge(Integer key, Integer value, BiFunction<? super Integer, ? super Integer, ? extends Integer> remappingFunction) {
         throw new UnsupportedOperationException();
      }
   }
}
