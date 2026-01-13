package it.unimi.dsi.fastutil.shorts;

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
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.IntUnaryOperator;

public final class Short2IntMaps {
   public static final Short2IntMaps.EmptyMap EMPTY_MAP = new Short2IntMaps.EmptyMap();

   private Short2IntMaps() {
   }

   public static ObjectIterator<Short2IntMap.Entry> fastIterator(Short2IntMap map) {
      ObjectSet<Short2IntMap.Entry> entries = map.short2IntEntrySet();
      return entries instanceof Short2IntMap.FastEntrySet ? ((Short2IntMap.FastEntrySet)entries).fastIterator() : entries.iterator();
   }

   public static void fastForEach(Short2IntMap map, Consumer<? super Short2IntMap.Entry> consumer) {
      ObjectSet<Short2IntMap.Entry> entries = map.short2IntEntrySet();
      if (entries instanceof Short2IntMap.FastEntrySet) {
         ((Short2IntMap.FastEntrySet)entries).fastForEach(consumer);
      } else {
         entries.forEach(consumer);
      }
   }

   public static ObjectIterable<Short2IntMap.Entry> fastIterable(Short2IntMap map) {
      final ObjectSet<Short2IntMap.Entry> entries = map.short2IntEntrySet();
      return (ObjectIterable<Short2IntMap.Entry>)(entries instanceof Short2IntMap.FastEntrySet ? new ObjectIterable<Short2IntMap.Entry>() {
         @Override
         public ObjectIterator<Short2IntMap.Entry> iterator() {
            return ((Short2IntMap.FastEntrySet)entries).fastIterator();
         }

         @Override
         public ObjectSpliterator<Short2IntMap.Entry> spliterator() {
            return entries.spliterator();
         }

         @Override
         public void forEach(Consumer<? super Short2IntMap.Entry> consumer) {
            ((Short2IntMap.FastEntrySet)entries).fastForEach(consumer);
         }
      } : entries);
   }

   public static Short2IntMap singleton(short key, int value) {
      return new Short2IntMaps.Singleton(key, value);
   }

   public static Short2IntMap singleton(Short key, Integer value) {
      return new Short2IntMaps.Singleton(key, value);
   }

   public static Short2IntMap synchronize(Short2IntMap m) {
      return new Short2IntMaps.SynchronizedMap(m);
   }

   public static Short2IntMap synchronize(Short2IntMap m, Object sync) {
      return new Short2IntMaps.SynchronizedMap(m, sync);
   }

   public static Short2IntMap unmodifiable(Short2IntMap m) {
      return new Short2IntMaps.UnmodifiableMap(m);
   }

   public static class EmptyMap extends Short2IntFunctions.EmptyFunction implements Short2IntMap, Serializable, Cloneable {
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
      public int getOrDefault(short key, int defaultValue) {
         return defaultValue;
      }

      @Deprecated
      @Override
      public boolean containsValue(Object ov) {
         return false;
      }

      @Override
      public void putAll(Map<? extends Short, ? extends Integer> m) {
         throw new UnsupportedOperationException();
      }

      @Override
      public ObjectSet<Short2IntMap.Entry> short2IntEntrySet() {
         return ObjectSets.EMPTY_SET;
      }

      @Override
      public ShortSet keySet() {
         return ShortSets.EMPTY_SET;
      }

      @Override
      public IntCollection values() {
         return IntSets.EMPTY_SET;
      }

      @Override
      public void forEach(BiConsumer<? super Short, ? super Integer> consumer) {
      }

      @Override
      public Object clone() {
         return Short2IntMaps.EMPTY_MAP;
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

   public static class Singleton extends Short2IntFunctions.Singleton implements Short2IntMap, Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected transient ObjectSet<Short2IntMap.Entry> entries;
      protected transient ShortSet keys;
      protected transient IntCollection values;

      protected Singleton(short key, int value) {
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
      public void putAll(Map<? extends Short, ? extends Integer> m) {
         throw new UnsupportedOperationException();
      }

      @Override
      public ObjectSet<Short2IntMap.Entry> short2IntEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSets.singleton(new AbstractShort2IntMap.BasicEntry(this.key, this.value));
         }

         return this.entries;
      }

      @Deprecated
      @Override
      public ObjectSet<Entry<Short, Integer>> entrySet() {
         return this.short2IntEntrySet();
      }

      @Override
      public ShortSet keySet() {
         if (this.keys == null) {
            this.keys = ShortSets.singleton(this.key);
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

   public static class SynchronizedMap extends Short2IntFunctions.SynchronizedFunction implements Short2IntMap, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Short2IntMap map;
      protected transient ObjectSet<Short2IntMap.Entry> entries;
      protected transient ShortSet keys;
      protected transient IntCollection values;

      protected SynchronizedMap(Short2IntMap m, Object sync) {
         super(m, sync);
         this.map = m;
      }

      protected SynchronizedMap(Short2IntMap m) {
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
      public void putAll(Map<? extends Short, ? extends Integer> m) {
         synchronized (this.sync) {
            this.map.putAll(m);
         }
      }

      @Override
      public ObjectSet<Short2IntMap.Entry> short2IntEntrySet() {
         synchronized (this.sync) {
            if (this.entries == null) {
               this.entries = ObjectSets.synchronize(this.map.short2IntEntrySet(), this.sync);
            }

            return this.entries;
         }
      }

      @Deprecated
      @Override
      public ObjectSet<Entry<Short, Integer>> entrySet() {
         return this.short2IntEntrySet();
      }

      @Override
      public ShortSet keySet() {
         synchronized (this.sync) {
            if (this.keys == null) {
               this.keys = ShortSets.synchronize(this.map.keySet(), this.sync);
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
      public int getOrDefault(short key, int defaultValue) {
         synchronized (this.sync) {
            return this.map.getOrDefault(key, defaultValue);
         }
      }

      @Override
      public void forEach(BiConsumer<? super Short, ? super Integer> action) {
         synchronized (this.sync) {
            this.map.forEach(action);
         }
      }

      @Override
      public void replaceAll(BiFunction<? super Short, ? super Integer, ? extends Integer> function) {
         synchronized (this.sync) {
            this.map.replaceAll(function);
         }
      }

      @Override
      public int putIfAbsent(short key, int value) {
         synchronized (this.sync) {
            return this.map.putIfAbsent(key, value);
         }
      }

      @Override
      public boolean remove(short key, int value) {
         synchronized (this.sync) {
            return this.map.remove(key, value);
         }
      }

      @Override
      public int replace(short key, int value) {
         synchronized (this.sync) {
            return this.map.replace(key, value);
         }
      }

      @Override
      public boolean replace(short key, int oldValue, int newValue) {
         synchronized (this.sync) {
            return this.map.replace(key, oldValue, newValue);
         }
      }

      @Override
      public int computeIfAbsent(short key, IntUnaryOperator mappingFunction) {
         synchronized (this.sync) {
            return this.map.computeIfAbsent(key, mappingFunction);
         }
      }

      @Override
      public int computeIfAbsentNullable(short key, IntFunction<? extends Integer> mappingFunction) {
         synchronized (this.sync) {
            return this.map.computeIfAbsentNullable(key, mappingFunction);
         }
      }

      @Override
      public int computeIfAbsent(short key, Short2IntFunction mappingFunction) {
         synchronized (this.sync) {
            return this.map.computeIfAbsent(key, mappingFunction);
         }
      }

      @Override
      public int computeIfPresent(short key, BiFunction<? super Short, ? super Integer, ? extends Integer> remappingFunction) {
         synchronized (this.sync) {
            return this.map.computeIfPresent(key, remappingFunction);
         }
      }

      @Override
      public int compute(short key, BiFunction<? super Short, ? super Integer, ? extends Integer> remappingFunction) {
         synchronized (this.sync) {
            return this.map.compute(key, remappingFunction);
         }
      }

      @Override
      public int merge(short key, int value, BiFunction<? super Integer, ? super Integer, ? extends Integer> remappingFunction) {
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
      public Integer replace(Short key, Integer value) {
         synchronized (this.sync) {
            return this.map.replace(key, value);
         }
      }

      @Deprecated
      @Override
      public boolean replace(Short key, Integer oldValue, Integer newValue) {
         synchronized (this.sync) {
            return this.map.replace(key, oldValue, newValue);
         }
      }

      @Deprecated
      @Override
      public Integer putIfAbsent(Short key, Integer value) {
         synchronized (this.sync) {
            return this.map.putIfAbsent(key, value);
         }
      }

      @Deprecated
      @Override
      public Integer computeIfAbsent(Short key, Function<? super Short, ? extends Integer> mappingFunction) {
         synchronized (this.sync) {
            return this.map.computeIfAbsent(key, mappingFunction);
         }
      }

      @Deprecated
      @Override
      public Integer computeIfPresent(Short key, BiFunction<? super Short, ? super Integer, ? extends Integer> remappingFunction) {
         synchronized (this.sync) {
            return this.map.computeIfPresent(key, remappingFunction);
         }
      }

      @Deprecated
      @Override
      public Integer compute(Short key, BiFunction<? super Short, ? super Integer, ? extends Integer> remappingFunction) {
         synchronized (this.sync) {
            return this.map.compute(key, remappingFunction);
         }
      }

      @Deprecated
      @Override
      public Integer merge(Short key, Integer value, BiFunction<? super Integer, ? super Integer, ? extends Integer> remappingFunction) {
         synchronized (this.sync) {
            return this.map.merge(key, value, remappingFunction);
         }
      }
   }

   public static class UnmodifiableMap extends Short2IntFunctions.UnmodifiableFunction implements Short2IntMap, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Short2IntMap map;
      protected transient ObjectSet<Short2IntMap.Entry> entries;
      protected transient ShortSet keys;
      protected transient IntCollection values;

      protected UnmodifiableMap(Short2IntMap m) {
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
      public void putAll(Map<? extends Short, ? extends Integer> m) {
         throw new UnsupportedOperationException();
      }

      @Override
      public ObjectSet<Short2IntMap.Entry> short2IntEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSets.unmodifiable(this.map.short2IntEntrySet());
         }

         return this.entries;
      }

      @Deprecated
      @Override
      public ObjectSet<Entry<Short, Integer>> entrySet() {
         return this.short2IntEntrySet();
      }

      @Override
      public ShortSet keySet() {
         if (this.keys == null) {
            this.keys = ShortSets.unmodifiable(this.map.keySet());
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
      public int getOrDefault(short key, int defaultValue) {
         return this.map.getOrDefault(key, defaultValue);
      }

      @Override
      public void forEach(BiConsumer<? super Short, ? super Integer> action) {
         this.map.forEach(action);
      }

      @Override
      public void replaceAll(BiFunction<? super Short, ? super Integer, ? extends Integer> function) {
         throw new UnsupportedOperationException();
      }

      @Override
      public int putIfAbsent(short key, int value) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean remove(short key, int value) {
         throw new UnsupportedOperationException();
      }

      @Override
      public int replace(short key, int value) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean replace(short key, int oldValue, int newValue) {
         throw new UnsupportedOperationException();
      }

      @Override
      public int computeIfAbsent(short key, IntUnaryOperator mappingFunction) {
         throw new UnsupportedOperationException();
      }

      @Override
      public int computeIfAbsentNullable(short key, IntFunction<? extends Integer> mappingFunction) {
         throw new UnsupportedOperationException();
      }

      @Override
      public int computeIfAbsent(short key, Short2IntFunction mappingFunction) {
         throw new UnsupportedOperationException();
      }

      @Override
      public int computeIfPresent(short key, BiFunction<? super Short, ? super Integer, ? extends Integer> remappingFunction) {
         throw new UnsupportedOperationException();
      }

      @Override
      public int compute(short key, BiFunction<? super Short, ? super Integer, ? extends Integer> remappingFunction) {
         throw new UnsupportedOperationException();
      }

      @Override
      public int merge(short key, int value, BiFunction<? super Integer, ? super Integer, ? extends Integer> remappingFunction) {
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
      public Integer replace(Short key, Integer value) {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      @Override
      public boolean replace(Short key, Integer oldValue, Integer newValue) {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      @Override
      public Integer putIfAbsent(Short key, Integer value) {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      @Override
      public Integer computeIfAbsent(Short key, Function<? super Short, ? extends Integer> mappingFunction) {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      @Override
      public Integer computeIfPresent(Short key, BiFunction<? super Short, ? super Integer, ? extends Integer> remappingFunction) {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      @Override
      public Integer compute(Short key, BiFunction<? super Short, ? super Integer, ? extends Integer> remappingFunction) {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      @Override
      public Integer merge(Short key, Integer value, BiFunction<? super Integer, ? super Integer, ? extends Integer> remappingFunction) {
         throw new UnsupportedOperationException();
      }
   }
}
