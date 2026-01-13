package it.unimi.dsi.fastutil.ints;

import it.unimi.dsi.fastutil.HashCommon;
import it.unimi.dsi.fastutil.longs.LongCollection;
import it.unimi.dsi.fastutil.longs.LongCollections;
import it.unimi.dsi.fastutil.longs.LongSets;
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
import java.util.function.IntToLongFunction;

public final class Int2LongMaps {
   public static final Int2LongMaps.EmptyMap EMPTY_MAP = new Int2LongMaps.EmptyMap();

   private Int2LongMaps() {
   }

   public static ObjectIterator<Int2LongMap.Entry> fastIterator(Int2LongMap map) {
      ObjectSet<Int2LongMap.Entry> entries = map.int2LongEntrySet();
      return entries instanceof Int2LongMap.FastEntrySet ? ((Int2LongMap.FastEntrySet)entries).fastIterator() : entries.iterator();
   }

   public static void fastForEach(Int2LongMap map, Consumer<? super Int2LongMap.Entry> consumer) {
      ObjectSet<Int2LongMap.Entry> entries = map.int2LongEntrySet();
      if (entries instanceof Int2LongMap.FastEntrySet) {
         ((Int2LongMap.FastEntrySet)entries).fastForEach(consumer);
      } else {
         entries.forEach(consumer);
      }
   }

   public static ObjectIterable<Int2LongMap.Entry> fastIterable(Int2LongMap map) {
      final ObjectSet<Int2LongMap.Entry> entries = map.int2LongEntrySet();
      return (ObjectIterable<Int2LongMap.Entry>)(entries instanceof Int2LongMap.FastEntrySet ? new ObjectIterable<Int2LongMap.Entry>() {
         @Override
         public ObjectIterator<Int2LongMap.Entry> iterator() {
            return ((Int2LongMap.FastEntrySet)entries).fastIterator();
         }

         @Override
         public ObjectSpliterator<Int2LongMap.Entry> spliterator() {
            return entries.spliterator();
         }

         @Override
         public void forEach(Consumer<? super Int2LongMap.Entry> consumer) {
            ((Int2LongMap.FastEntrySet)entries).fastForEach(consumer);
         }
      } : entries);
   }

   public static Int2LongMap singleton(int key, long value) {
      return new Int2LongMaps.Singleton(key, value);
   }

   public static Int2LongMap singleton(Integer key, Long value) {
      return new Int2LongMaps.Singleton(key, value);
   }

   public static Int2LongMap synchronize(Int2LongMap m) {
      return new Int2LongMaps.SynchronizedMap(m);
   }

   public static Int2LongMap synchronize(Int2LongMap m, Object sync) {
      return new Int2LongMaps.SynchronizedMap(m, sync);
   }

   public static Int2LongMap unmodifiable(Int2LongMap m) {
      return new Int2LongMaps.UnmodifiableMap(m);
   }

   public static class EmptyMap extends Int2LongFunctions.EmptyFunction implements Int2LongMap, Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;

      protected EmptyMap() {
      }

      @Override
      public boolean containsValue(long v) {
         return false;
      }

      @Deprecated
      @Override
      public Long getOrDefault(Object key, Long defaultValue) {
         return defaultValue;
      }

      @Override
      public long getOrDefault(int key, long defaultValue) {
         return defaultValue;
      }

      @Deprecated
      @Override
      public boolean containsValue(Object ov) {
         return false;
      }

      @Override
      public void putAll(Map<? extends Integer, ? extends Long> m) {
         throw new UnsupportedOperationException();
      }

      @Override
      public ObjectSet<Int2LongMap.Entry> int2LongEntrySet() {
         return ObjectSets.EMPTY_SET;
      }

      @Override
      public IntSet keySet() {
         return IntSets.EMPTY_SET;
      }

      @Override
      public LongCollection values() {
         return LongSets.EMPTY_SET;
      }

      @Override
      public void forEach(BiConsumer<? super Integer, ? super Long> consumer) {
      }

      @Override
      public Object clone() {
         return Int2LongMaps.EMPTY_MAP;
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

   public static class Singleton extends Int2LongFunctions.Singleton implements Int2LongMap, Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected transient ObjectSet<Int2LongMap.Entry> entries;
      protected transient IntSet keys;
      protected transient LongCollection values;

      protected Singleton(int key, long value) {
         super(key, value);
      }

      @Override
      public boolean containsValue(long v) {
         return this.value == v;
      }

      @Deprecated
      @Override
      public boolean containsValue(Object ov) {
         return (Long)ov == this.value;
      }

      @Override
      public void putAll(Map<? extends Integer, ? extends Long> m) {
         throw new UnsupportedOperationException();
      }

      @Override
      public ObjectSet<Int2LongMap.Entry> int2LongEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSets.singleton(new AbstractInt2LongMap.BasicEntry(this.key, this.value));
         }

         return this.entries;
      }

      @Deprecated
      @Override
      public ObjectSet<Entry<Integer, Long>> entrySet() {
         return this.int2LongEntrySet();
      }

      @Override
      public IntSet keySet() {
         if (this.keys == null) {
            this.keys = IntSets.singleton(this.key);
         }

         return this.keys;
      }

      @Override
      public LongCollection values() {
         if (this.values == null) {
            this.values = LongSets.singleton(this.value);
         }

         return this.values;
      }

      @Override
      public boolean isEmpty() {
         return false;
      }

      @Override
      public int hashCode() {
         return this.key ^ HashCommon.long2int(this.value);
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

   public static class SynchronizedMap extends Int2LongFunctions.SynchronizedFunction implements Int2LongMap, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Int2LongMap map;
      protected transient ObjectSet<Int2LongMap.Entry> entries;
      protected transient IntSet keys;
      protected transient LongCollection values;

      protected SynchronizedMap(Int2LongMap m, Object sync) {
         super(m, sync);
         this.map = m;
      }

      protected SynchronizedMap(Int2LongMap m) {
         super(m);
         this.map = m;
      }

      @Override
      public boolean containsValue(long v) {
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
      public void putAll(Map<? extends Integer, ? extends Long> m) {
         synchronized (this.sync) {
            this.map.putAll(m);
         }
      }

      @Override
      public ObjectSet<Int2LongMap.Entry> int2LongEntrySet() {
         synchronized (this.sync) {
            if (this.entries == null) {
               this.entries = ObjectSets.synchronize(this.map.int2LongEntrySet(), this.sync);
            }

            return this.entries;
         }
      }

      @Deprecated
      @Override
      public ObjectSet<Entry<Integer, Long>> entrySet() {
         return this.int2LongEntrySet();
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
      public LongCollection values() {
         synchronized (this.sync) {
            if (this.values == null) {
               this.values = LongCollections.synchronize(this.map.values(), this.sync);
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
      public long getOrDefault(int key, long defaultValue) {
         synchronized (this.sync) {
            return this.map.getOrDefault(key, defaultValue);
         }
      }

      @Override
      public void forEach(BiConsumer<? super Integer, ? super Long> action) {
         synchronized (this.sync) {
            this.map.forEach(action);
         }
      }

      @Override
      public void replaceAll(BiFunction<? super Integer, ? super Long, ? extends Long> function) {
         synchronized (this.sync) {
            this.map.replaceAll(function);
         }
      }

      @Override
      public long putIfAbsent(int key, long value) {
         synchronized (this.sync) {
            return this.map.putIfAbsent(key, value);
         }
      }

      @Override
      public boolean remove(int key, long value) {
         synchronized (this.sync) {
            return this.map.remove(key, value);
         }
      }

      @Override
      public long replace(int key, long value) {
         synchronized (this.sync) {
            return this.map.replace(key, value);
         }
      }

      @Override
      public boolean replace(int key, long oldValue, long newValue) {
         synchronized (this.sync) {
            return this.map.replace(key, oldValue, newValue);
         }
      }

      @Override
      public long computeIfAbsent(int key, IntToLongFunction mappingFunction) {
         synchronized (this.sync) {
            return this.map.computeIfAbsent(key, mappingFunction);
         }
      }

      @Override
      public long computeIfAbsentNullable(int key, IntFunction<? extends Long> mappingFunction) {
         synchronized (this.sync) {
            return this.map.computeIfAbsentNullable(key, mappingFunction);
         }
      }

      @Override
      public long computeIfAbsent(int key, Int2LongFunction mappingFunction) {
         synchronized (this.sync) {
            return this.map.computeIfAbsent(key, mappingFunction);
         }
      }

      @Override
      public long computeIfPresent(int key, BiFunction<? super Integer, ? super Long, ? extends Long> remappingFunction) {
         synchronized (this.sync) {
            return this.map.computeIfPresent(key, remappingFunction);
         }
      }

      @Override
      public long compute(int key, BiFunction<? super Integer, ? super Long, ? extends Long> remappingFunction) {
         synchronized (this.sync) {
            return this.map.compute(key, remappingFunction);
         }
      }

      @Override
      public long merge(int key, long value, BiFunction<? super Long, ? super Long, ? extends Long> remappingFunction) {
         synchronized (this.sync) {
            return this.map.merge(key, value, remappingFunction);
         }
      }

      @Deprecated
      @Override
      public Long getOrDefault(Object key, Long defaultValue) {
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
      public Long replace(Integer key, Long value) {
         synchronized (this.sync) {
            return this.map.replace(key, value);
         }
      }

      @Deprecated
      @Override
      public boolean replace(Integer key, Long oldValue, Long newValue) {
         synchronized (this.sync) {
            return this.map.replace(key, oldValue, newValue);
         }
      }

      @Deprecated
      @Override
      public Long putIfAbsent(Integer key, Long value) {
         synchronized (this.sync) {
            return this.map.putIfAbsent(key, value);
         }
      }

      @Deprecated
      @Override
      public Long computeIfAbsent(Integer key, Function<? super Integer, ? extends Long> mappingFunction) {
         synchronized (this.sync) {
            return this.map.computeIfAbsent(key, mappingFunction);
         }
      }

      @Deprecated
      @Override
      public Long computeIfPresent(Integer key, BiFunction<? super Integer, ? super Long, ? extends Long> remappingFunction) {
         synchronized (this.sync) {
            return this.map.computeIfPresent(key, remappingFunction);
         }
      }

      @Deprecated
      @Override
      public Long compute(Integer key, BiFunction<? super Integer, ? super Long, ? extends Long> remappingFunction) {
         synchronized (this.sync) {
            return this.map.compute(key, remappingFunction);
         }
      }

      @Deprecated
      @Override
      public Long merge(Integer key, Long value, BiFunction<? super Long, ? super Long, ? extends Long> remappingFunction) {
         synchronized (this.sync) {
            return this.map.merge(key, value, remappingFunction);
         }
      }
   }

   public static class UnmodifiableMap extends Int2LongFunctions.UnmodifiableFunction implements Int2LongMap, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Int2LongMap map;
      protected transient ObjectSet<Int2LongMap.Entry> entries;
      protected transient IntSet keys;
      protected transient LongCollection values;

      protected UnmodifiableMap(Int2LongMap m) {
         super(m);
         this.map = m;
      }

      @Override
      public boolean containsValue(long v) {
         return this.map.containsValue(v);
      }

      @Deprecated
      @Override
      public boolean containsValue(Object ov) {
         return this.map.containsValue(ov);
      }

      @Override
      public void putAll(Map<? extends Integer, ? extends Long> m) {
         throw new UnsupportedOperationException();
      }

      @Override
      public ObjectSet<Int2LongMap.Entry> int2LongEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSets.unmodifiable(this.map.int2LongEntrySet());
         }

         return this.entries;
      }

      @Deprecated
      @Override
      public ObjectSet<Entry<Integer, Long>> entrySet() {
         return this.int2LongEntrySet();
      }

      @Override
      public IntSet keySet() {
         if (this.keys == null) {
            this.keys = IntSets.unmodifiable(this.map.keySet());
         }

         return this.keys;
      }

      @Override
      public LongCollection values() {
         if (this.values == null) {
            this.values = LongCollections.unmodifiable(this.map.values());
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
      public long getOrDefault(int key, long defaultValue) {
         return this.map.getOrDefault(key, defaultValue);
      }

      @Override
      public void forEach(BiConsumer<? super Integer, ? super Long> action) {
         this.map.forEach(action);
      }

      @Override
      public void replaceAll(BiFunction<? super Integer, ? super Long, ? extends Long> function) {
         throw new UnsupportedOperationException();
      }

      @Override
      public long putIfAbsent(int key, long value) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean remove(int key, long value) {
         throw new UnsupportedOperationException();
      }

      @Override
      public long replace(int key, long value) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean replace(int key, long oldValue, long newValue) {
         throw new UnsupportedOperationException();
      }

      @Override
      public long computeIfAbsent(int key, IntToLongFunction mappingFunction) {
         throw new UnsupportedOperationException();
      }

      @Override
      public long computeIfAbsentNullable(int key, IntFunction<? extends Long> mappingFunction) {
         throw new UnsupportedOperationException();
      }

      @Override
      public long computeIfAbsent(int key, Int2LongFunction mappingFunction) {
         throw new UnsupportedOperationException();
      }

      @Override
      public long computeIfPresent(int key, BiFunction<? super Integer, ? super Long, ? extends Long> remappingFunction) {
         throw new UnsupportedOperationException();
      }

      @Override
      public long compute(int key, BiFunction<? super Integer, ? super Long, ? extends Long> remappingFunction) {
         throw new UnsupportedOperationException();
      }

      @Override
      public long merge(int key, long value, BiFunction<? super Long, ? super Long, ? extends Long> remappingFunction) {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      @Override
      public Long getOrDefault(Object key, Long defaultValue) {
         return this.map.getOrDefault(key, defaultValue);
      }

      @Deprecated
      @Override
      public boolean remove(Object key, Object value) {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      @Override
      public Long replace(Integer key, Long value) {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      @Override
      public boolean replace(Integer key, Long oldValue, Long newValue) {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      @Override
      public Long putIfAbsent(Integer key, Long value) {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      @Override
      public Long computeIfAbsent(Integer key, Function<? super Integer, ? extends Long> mappingFunction) {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      @Override
      public Long computeIfPresent(Integer key, BiFunction<? super Integer, ? super Long, ? extends Long> remappingFunction) {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      @Override
      public Long compute(Integer key, BiFunction<? super Integer, ? super Long, ? extends Long> remappingFunction) {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      @Override
      public Long merge(Integer key, Long value, BiFunction<? super Long, ? super Long, ? extends Long> remappingFunction) {
         throw new UnsupportedOperationException();
      }
   }
}
