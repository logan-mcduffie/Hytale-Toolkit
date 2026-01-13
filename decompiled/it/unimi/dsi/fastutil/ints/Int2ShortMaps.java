package it.unimi.dsi.fastutil.ints;

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
import java.util.function.Function;
import java.util.function.IntFunction;

public final class Int2ShortMaps {
   public static final Int2ShortMaps.EmptyMap EMPTY_MAP = new Int2ShortMaps.EmptyMap();

   private Int2ShortMaps() {
   }

   public static ObjectIterator<Int2ShortMap.Entry> fastIterator(Int2ShortMap map) {
      ObjectSet<Int2ShortMap.Entry> entries = map.int2ShortEntrySet();
      return entries instanceof Int2ShortMap.FastEntrySet ? ((Int2ShortMap.FastEntrySet)entries).fastIterator() : entries.iterator();
   }

   public static void fastForEach(Int2ShortMap map, Consumer<? super Int2ShortMap.Entry> consumer) {
      ObjectSet<Int2ShortMap.Entry> entries = map.int2ShortEntrySet();
      if (entries instanceof Int2ShortMap.FastEntrySet) {
         ((Int2ShortMap.FastEntrySet)entries).fastForEach(consumer);
      } else {
         entries.forEach(consumer);
      }
   }

   public static ObjectIterable<Int2ShortMap.Entry> fastIterable(Int2ShortMap map) {
      final ObjectSet<Int2ShortMap.Entry> entries = map.int2ShortEntrySet();
      return (ObjectIterable<Int2ShortMap.Entry>)(entries instanceof Int2ShortMap.FastEntrySet ? new ObjectIterable<Int2ShortMap.Entry>() {
         @Override
         public ObjectIterator<Int2ShortMap.Entry> iterator() {
            return ((Int2ShortMap.FastEntrySet)entries).fastIterator();
         }

         @Override
         public ObjectSpliterator<Int2ShortMap.Entry> spliterator() {
            return entries.spliterator();
         }

         @Override
         public void forEach(Consumer<? super Int2ShortMap.Entry> consumer) {
            ((Int2ShortMap.FastEntrySet)entries).fastForEach(consumer);
         }
      } : entries);
   }

   public static Int2ShortMap singleton(int key, short value) {
      return new Int2ShortMaps.Singleton(key, value);
   }

   public static Int2ShortMap singleton(Integer key, Short value) {
      return new Int2ShortMaps.Singleton(key, value);
   }

   public static Int2ShortMap synchronize(Int2ShortMap m) {
      return new Int2ShortMaps.SynchronizedMap(m);
   }

   public static Int2ShortMap synchronize(Int2ShortMap m, Object sync) {
      return new Int2ShortMaps.SynchronizedMap(m, sync);
   }

   public static Int2ShortMap unmodifiable(Int2ShortMap m) {
      return new Int2ShortMaps.UnmodifiableMap(m);
   }

   public static class EmptyMap extends Int2ShortFunctions.EmptyFunction implements Int2ShortMap, Serializable, Cloneable {
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
      public short getOrDefault(int key, short defaultValue) {
         return defaultValue;
      }

      @Deprecated
      @Override
      public boolean containsValue(Object ov) {
         return false;
      }

      @Override
      public void putAll(Map<? extends Integer, ? extends Short> m) {
         throw new UnsupportedOperationException();
      }

      @Override
      public ObjectSet<Int2ShortMap.Entry> int2ShortEntrySet() {
         return ObjectSets.EMPTY_SET;
      }

      @Override
      public IntSet keySet() {
         return IntSets.EMPTY_SET;
      }

      @Override
      public ShortCollection values() {
         return ShortSets.EMPTY_SET;
      }

      @Override
      public void forEach(BiConsumer<? super Integer, ? super Short> consumer) {
      }

      @Override
      public Object clone() {
         return Int2ShortMaps.EMPTY_MAP;
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

   public static class Singleton extends Int2ShortFunctions.Singleton implements Int2ShortMap, Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected transient ObjectSet<Int2ShortMap.Entry> entries;
      protected transient IntSet keys;
      protected transient ShortCollection values;

      protected Singleton(int key, short value) {
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
      public void putAll(Map<? extends Integer, ? extends Short> m) {
         throw new UnsupportedOperationException();
      }

      @Override
      public ObjectSet<Int2ShortMap.Entry> int2ShortEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSets.singleton(new AbstractInt2ShortMap.BasicEntry(this.key, this.value));
         }

         return this.entries;
      }

      @Deprecated
      @Override
      public ObjectSet<Entry<Integer, Short>> entrySet() {
         return this.int2ShortEntrySet();
      }

      @Override
      public IntSet keySet() {
         if (this.keys == null) {
            this.keys = IntSets.singleton(this.key);
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

   public static class SynchronizedMap extends Int2ShortFunctions.SynchronizedFunction implements Int2ShortMap, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Int2ShortMap map;
      protected transient ObjectSet<Int2ShortMap.Entry> entries;
      protected transient IntSet keys;
      protected transient ShortCollection values;

      protected SynchronizedMap(Int2ShortMap m, Object sync) {
         super(m, sync);
         this.map = m;
      }

      protected SynchronizedMap(Int2ShortMap m) {
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
      public void putAll(Map<? extends Integer, ? extends Short> m) {
         synchronized (this.sync) {
            this.map.putAll(m);
         }
      }

      @Override
      public ObjectSet<Int2ShortMap.Entry> int2ShortEntrySet() {
         synchronized (this.sync) {
            if (this.entries == null) {
               this.entries = ObjectSets.synchronize(this.map.int2ShortEntrySet(), this.sync);
            }

            return this.entries;
         }
      }

      @Deprecated
      @Override
      public ObjectSet<Entry<Integer, Short>> entrySet() {
         return this.int2ShortEntrySet();
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
      public short getOrDefault(int key, short defaultValue) {
         synchronized (this.sync) {
            return this.map.getOrDefault(key, defaultValue);
         }
      }

      @Override
      public void forEach(BiConsumer<? super Integer, ? super Short> action) {
         synchronized (this.sync) {
            this.map.forEach(action);
         }
      }

      @Override
      public void replaceAll(BiFunction<? super Integer, ? super Short, ? extends Short> function) {
         synchronized (this.sync) {
            this.map.replaceAll(function);
         }
      }

      @Override
      public short putIfAbsent(int key, short value) {
         synchronized (this.sync) {
            return this.map.putIfAbsent(key, value);
         }
      }

      @Override
      public boolean remove(int key, short value) {
         synchronized (this.sync) {
            return this.map.remove(key, value);
         }
      }

      @Override
      public short replace(int key, short value) {
         synchronized (this.sync) {
            return this.map.replace(key, value);
         }
      }

      @Override
      public boolean replace(int key, short oldValue, short newValue) {
         synchronized (this.sync) {
            return this.map.replace(key, oldValue, newValue);
         }
      }

      @Override
      public short computeIfAbsent(int key, java.util.function.IntUnaryOperator mappingFunction) {
         synchronized (this.sync) {
            return this.map.computeIfAbsent(key, mappingFunction);
         }
      }

      @Override
      public short computeIfAbsentNullable(int key, IntFunction<? extends Short> mappingFunction) {
         synchronized (this.sync) {
            return this.map.computeIfAbsentNullable(key, mappingFunction);
         }
      }

      @Override
      public short computeIfAbsent(int key, Int2ShortFunction mappingFunction) {
         synchronized (this.sync) {
            return this.map.computeIfAbsent(key, mappingFunction);
         }
      }

      @Override
      public short computeIfPresent(int key, BiFunction<? super Integer, ? super Short, ? extends Short> remappingFunction) {
         synchronized (this.sync) {
            return this.map.computeIfPresent(key, remappingFunction);
         }
      }

      @Override
      public short compute(int key, BiFunction<? super Integer, ? super Short, ? extends Short> remappingFunction) {
         synchronized (this.sync) {
            return this.map.compute(key, remappingFunction);
         }
      }

      @Override
      public short merge(int key, short value, BiFunction<? super Short, ? super Short, ? extends Short> remappingFunction) {
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
      public Short replace(Integer key, Short value) {
         synchronized (this.sync) {
            return this.map.replace(key, value);
         }
      }

      @Deprecated
      @Override
      public boolean replace(Integer key, Short oldValue, Short newValue) {
         synchronized (this.sync) {
            return this.map.replace(key, oldValue, newValue);
         }
      }

      @Deprecated
      @Override
      public Short putIfAbsent(Integer key, Short value) {
         synchronized (this.sync) {
            return this.map.putIfAbsent(key, value);
         }
      }

      @Deprecated
      @Override
      public Short computeIfAbsent(Integer key, Function<? super Integer, ? extends Short> mappingFunction) {
         synchronized (this.sync) {
            return this.map.computeIfAbsent(key, mappingFunction);
         }
      }

      @Deprecated
      @Override
      public Short computeIfPresent(Integer key, BiFunction<? super Integer, ? super Short, ? extends Short> remappingFunction) {
         synchronized (this.sync) {
            return this.map.computeIfPresent(key, remappingFunction);
         }
      }

      @Deprecated
      @Override
      public Short compute(Integer key, BiFunction<? super Integer, ? super Short, ? extends Short> remappingFunction) {
         synchronized (this.sync) {
            return this.map.compute(key, remappingFunction);
         }
      }

      @Deprecated
      @Override
      public Short merge(Integer key, Short value, BiFunction<? super Short, ? super Short, ? extends Short> remappingFunction) {
         synchronized (this.sync) {
            return this.map.merge(key, value, remappingFunction);
         }
      }
   }

   public static class UnmodifiableMap extends Int2ShortFunctions.UnmodifiableFunction implements Int2ShortMap, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Int2ShortMap map;
      protected transient ObjectSet<Int2ShortMap.Entry> entries;
      protected transient IntSet keys;
      protected transient ShortCollection values;

      protected UnmodifiableMap(Int2ShortMap m) {
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
      public void putAll(Map<? extends Integer, ? extends Short> m) {
         throw new UnsupportedOperationException();
      }

      @Override
      public ObjectSet<Int2ShortMap.Entry> int2ShortEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSets.unmodifiable(this.map.int2ShortEntrySet());
         }

         return this.entries;
      }

      @Deprecated
      @Override
      public ObjectSet<Entry<Integer, Short>> entrySet() {
         return this.int2ShortEntrySet();
      }

      @Override
      public IntSet keySet() {
         if (this.keys == null) {
            this.keys = IntSets.unmodifiable(this.map.keySet());
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
      public short getOrDefault(int key, short defaultValue) {
         return this.map.getOrDefault(key, defaultValue);
      }

      @Override
      public void forEach(BiConsumer<? super Integer, ? super Short> action) {
         this.map.forEach(action);
      }

      @Override
      public void replaceAll(BiFunction<? super Integer, ? super Short, ? extends Short> function) {
         throw new UnsupportedOperationException();
      }

      @Override
      public short putIfAbsent(int key, short value) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean remove(int key, short value) {
         throw new UnsupportedOperationException();
      }

      @Override
      public short replace(int key, short value) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean replace(int key, short oldValue, short newValue) {
         throw new UnsupportedOperationException();
      }

      @Override
      public short computeIfAbsent(int key, java.util.function.IntUnaryOperator mappingFunction) {
         throw new UnsupportedOperationException();
      }

      @Override
      public short computeIfAbsentNullable(int key, IntFunction<? extends Short> mappingFunction) {
         throw new UnsupportedOperationException();
      }

      @Override
      public short computeIfAbsent(int key, Int2ShortFunction mappingFunction) {
         throw new UnsupportedOperationException();
      }

      @Override
      public short computeIfPresent(int key, BiFunction<? super Integer, ? super Short, ? extends Short> remappingFunction) {
         throw new UnsupportedOperationException();
      }

      @Override
      public short compute(int key, BiFunction<? super Integer, ? super Short, ? extends Short> remappingFunction) {
         throw new UnsupportedOperationException();
      }

      @Override
      public short merge(int key, short value, BiFunction<? super Short, ? super Short, ? extends Short> remappingFunction) {
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
      public Short replace(Integer key, Short value) {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      @Override
      public boolean replace(Integer key, Short oldValue, Short newValue) {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      @Override
      public Short putIfAbsent(Integer key, Short value) {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      @Override
      public Short computeIfAbsent(Integer key, Function<? super Integer, ? extends Short> mappingFunction) {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      @Override
      public Short computeIfPresent(Integer key, BiFunction<? super Integer, ? super Short, ? extends Short> remappingFunction) {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      @Override
      public Short compute(Integer key, BiFunction<? super Integer, ? super Short, ? extends Short> remappingFunction) {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      @Override
      public Short merge(Integer key, Short value, BiFunction<? super Short, ? super Short, ? extends Short> remappingFunction) {
         throw new UnsupportedOperationException();
      }
   }
}
