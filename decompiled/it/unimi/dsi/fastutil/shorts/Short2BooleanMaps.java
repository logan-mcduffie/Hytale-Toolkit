package it.unimi.dsi.fastutil.shorts;

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
import java.util.function.IntPredicate;

public final class Short2BooleanMaps {
   public static final Short2BooleanMaps.EmptyMap EMPTY_MAP = new Short2BooleanMaps.EmptyMap();

   private Short2BooleanMaps() {
   }

   public static ObjectIterator<Short2BooleanMap.Entry> fastIterator(Short2BooleanMap map) {
      ObjectSet<Short2BooleanMap.Entry> entries = map.short2BooleanEntrySet();
      return entries instanceof Short2BooleanMap.FastEntrySet ? ((Short2BooleanMap.FastEntrySet)entries).fastIterator() : entries.iterator();
   }

   public static void fastForEach(Short2BooleanMap map, Consumer<? super Short2BooleanMap.Entry> consumer) {
      ObjectSet<Short2BooleanMap.Entry> entries = map.short2BooleanEntrySet();
      if (entries instanceof Short2BooleanMap.FastEntrySet) {
         ((Short2BooleanMap.FastEntrySet)entries).fastForEach(consumer);
      } else {
         entries.forEach(consumer);
      }
   }

   public static ObjectIterable<Short2BooleanMap.Entry> fastIterable(Short2BooleanMap map) {
      final ObjectSet<Short2BooleanMap.Entry> entries = map.short2BooleanEntrySet();
      return (ObjectIterable<Short2BooleanMap.Entry>)(entries instanceof Short2BooleanMap.FastEntrySet ? new ObjectIterable<Short2BooleanMap.Entry>() {
         @Override
         public ObjectIterator<Short2BooleanMap.Entry> iterator() {
            return ((Short2BooleanMap.FastEntrySet)entries).fastIterator();
         }

         @Override
         public ObjectSpliterator<Short2BooleanMap.Entry> spliterator() {
            return entries.spliterator();
         }

         @Override
         public void forEach(Consumer<? super Short2BooleanMap.Entry> consumer) {
            ((Short2BooleanMap.FastEntrySet)entries).fastForEach(consumer);
         }
      } : entries);
   }

   public static Short2BooleanMap singleton(short key, boolean value) {
      return new Short2BooleanMaps.Singleton(key, value);
   }

   public static Short2BooleanMap singleton(Short key, Boolean value) {
      return new Short2BooleanMaps.Singleton(key, value);
   }

   public static Short2BooleanMap synchronize(Short2BooleanMap m) {
      return new Short2BooleanMaps.SynchronizedMap(m);
   }

   public static Short2BooleanMap synchronize(Short2BooleanMap m, Object sync) {
      return new Short2BooleanMaps.SynchronizedMap(m, sync);
   }

   public static Short2BooleanMap unmodifiable(Short2BooleanMap m) {
      return new Short2BooleanMaps.UnmodifiableMap(m);
   }

   public static class EmptyMap extends Short2BooleanFunctions.EmptyFunction implements Short2BooleanMap, Serializable, Cloneable {
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
      public boolean getOrDefault(short key, boolean defaultValue) {
         return defaultValue;
      }

      @Deprecated
      @Override
      public boolean containsValue(Object ov) {
         return false;
      }

      @Override
      public void putAll(Map<? extends Short, ? extends Boolean> m) {
         throw new UnsupportedOperationException();
      }

      @Override
      public ObjectSet<Short2BooleanMap.Entry> short2BooleanEntrySet() {
         return ObjectSets.EMPTY_SET;
      }

      @Override
      public ShortSet keySet() {
         return ShortSets.EMPTY_SET;
      }

      @Override
      public BooleanCollection values() {
         return BooleanSets.EMPTY_SET;
      }

      @Override
      public void forEach(BiConsumer<? super Short, ? super Boolean> consumer) {
      }

      @Override
      public Object clone() {
         return Short2BooleanMaps.EMPTY_MAP;
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

   public static class Singleton extends Short2BooleanFunctions.Singleton implements Short2BooleanMap, Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected transient ObjectSet<Short2BooleanMap.Entry> entries;
      protected transient ShortSet keys;
      protected transient BooleanCollection values;

      protected Singleton(short key, boolean value) {
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
      public void putAll(Map<? extends Short, ? extends Boolean> m) {
         throw new UnsupportedOperationException();
      }

      @Override
      public ObjectSet<Short2BooleanMap.Entry> short2BooleanEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSets.singleton(new AbstractShort2BooleanMap.BasicEntry(this.key, this.value));
         }

         return this.entries;
      }

      @Deprecated
      @Override
      public ObjectSet<Entry<Short, Boolean>> entrySet() {
         return this.short2BooleanEntrySet();
      }

      @Override
      public ShortSet keySet() {
         if (this.keys == null) {
            this.keys = ShortSets.singleton(this.key);
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

   public static class SynchronizedMap extends Short2BooleanFunctions.SynchronizedFunction implements Short2BooleanMap, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Short2BooleanMap map;
      protected transient ObjectSet<Short2BooleanMap.Entry> entries;
      protected transient ShortSet keys;
      protected transient BooleanCollection values;

      protected SynchronizedMap(Short2BooleanMap m, Object sync) {
         super(m, sync);
         this.map = m;
      }

      protected SynchronizedMap(Short2BooleanMap m) {
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
      public void putAll(Map<? extends Short, ? extends Boolean> m) {
         synchronized (this.sync) {
            this.map.putAll(m);
         }
      }

      @Override
      public ObjectSet<Short2BooleanMap.Entry> short2BooleanEntrySet() {
         synchronized (this.sync) {
            if (this.entries == null) {
               this.entries = ObjectSets.synchronize(this.map.short2BooleanEntrySet(), this.sync);
            }

            return this.entries;
         }
      }

      @Deprecated
      @Override
      public ObjectSet<Entry<Short, Boolean>> entrySet() {
         return this.short2BooleanEntrySet();
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
      public boolean getOrDefault(short key, boolean defaultValue) {
         synchronized (this.sync) {
            return this.map.getOrDefault(key, defaultValue);
         }
      }

      @Override
      public void forEach(BiConsumer<? super Short, ? super Boolean> action) {
         synchronized (this.sync) {
            this.map.forEach(action);
         }
      }

      @Override
      public void replaceAll(BiFunction<? super Short, ? super Boolean, ? extends Boolean> function) {
         synchronized (this.sync) {
            this.map.replaceAll(function);
         }
      }

      @Override
      public boolean putIfAbsent(short key, boolean value) {
         synchronized (this.sync) {
            return this.map.putIfAbsent(key, value);
         }
      }

      @Override
      public boolean remove(short key, boolean value) {
         synchronized (this.sync) {
            return this.map.remove(key, value);
         }
      }

      @Override
      public boolean replace(short key, boolean value) {
         synchronized (this.sync) {
            return this.map.replace(key, value);
         }
      }

      @Override
      public boolean replace(short key, boolean oldValue, boolean newValue) {
         synchronized (this.sync) {
            return this.map.replace(key, oldValue, newValue);
         }
      }

      @Override
      public boolean computeIfAbsent(short key, IntPredicate mappingFunction) {
         synchronized (this.sync) {
            return this.map.computeIfAbsent(key, mappingFunction);
         }
      }

      @Override
      public boolean computeIfAbsentNullable(short key, IntFunction<? extends Boolean> mappingFunction) {
         synchronized (this.sync) {
            return this.map.computeIfAbsentNullable(key, mappingFunction);
         }
      }

      @Override
      public boolean computeIfAbsent(short key, Short2BooleanFunction mappingFunction) {
         synchronized (this.sync) {
            return this.map.computeIfAbsent(key, mappingFunction);
         }
      }

      @Override
      public boolean computeIfPresent(short key, BiFunction<? super Short, ? super Boolean, ? extends Boolean> remappingFunction) {
         synchronized (this.sync) {
            return this.map.computeIfPresent(key, remappingFunction);
         }
      }

      @Override
      public boolean compute(short key, BiFunction<? super Short, ? super Boolean, ? extends Boolean> remappingFunction) {
         synchronized (this.sync) {
            return this.map.compute(key, remappingFunction);
         }
      }

      @Override
      public boolean merge(short key, boolean value, BiFunction<? super Boolean, ? super Boolean, ? extends Boolean> remappingFunction) {
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
      public Boolean replace(Short key, Boolean value) {
         synchronized (this.sync) {
            return this.map.replace(key, value);
         }
      }

      @Deprecated
      public boolean replace(Short key, Boolean oldValue, Boolean newValue) {
         synchronized (this.sync) {
            return this.map.replace(key, oldValue, newValue);
         }
      }

      @Deprecated
      public Boolean putIfAbsent(Short key, Boolean value) {
         synchronized (this.sync) {
            return this.map.putIfAbsent(key, value);
         }
      }

      @Deprecated
      public Boolean computeIfAbsent(Short key, Function<? super Short, ? extends Boolean> mappingFunction) {
         synchronized (this.sync) {
            return this.map.computeIfAbsent(key, mappingFunction);
         }
      }

      @Deprecated
      public Boolean computeIfPresent(Short key, BiFunction<? super Short, ? super Boolean, ? extends Boolean> remappingFunction) {
         synchronized (this.sync) {
            return this.map.computeIfPresent(key, remappingFunction);
         }
      }

      @Deprecated
      public Boolean compute(Short key, BiFunction<? super Short, ? super Boolean, ? extends Boolean> remappingFunction) {
         synchronized (this.sync) {
            return this.map.compute(key, remappingFunction);
         }
      }

      @Deprecated
      public Boolean merge(Short key, Boolean value, BiFunction<? super Boolean, ? super Boolean, ? extends Boolean> remappingFunction) {
         synchronized (this.sync) {
            return this.map.merge(key, value, remappingFunction);
         }
      }
   }

   public static class UnmodifiableMap extends Short2BooleanFunctions.UnmodifiableFunction implements Short2BooleanMap, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Short2BooleanMap map;
      protected transient ObjectSet<Short2BooleanMap.Entry> entries;
      protected transient ShortSet keys;
      protected transient BooleanCollection values;

      protected UnmodifiableMap(Short2BooleanMap m) {
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
      public void putAll(Map<? extends Short, ? extends Boolean> m) {
         throw new UnsupportedOperationException();
      }

      @Override
      public ObjectSet<Short2BooleanMap.Entry> short2BooleanEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSets.unmodifiable(this.map.short2BooleanEntrySet());
         }

         return this.entries;
      }

      @Deprecated
      @Override
      public ObjectSet<Entry<Short, Boolean>> entrySet() {
         return this.short2BooleanEntrySet();
      }

      @Override
      public ShortSet keySet() {
         if (this.keys == null) {
            this.keys = ShortSets.unmodifiable(this.map.keySet());
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
      public boolean getOrDefault(short key, boolean defaultValue) {
         return this.map.getOrDefault(key, defaultValue);
      }

      @Override
      public void forEach(BiConsumer<? super Short, ? super Boolean> action) {
         this.map.forEach(action);
      }

      @Override
      public void replaceAll(BiFunction<? super Short, ? super Boolean, ? extends Boolean> function) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean putIfAbsent(short key, boolean value) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean remove(short key, boolean value) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean replace(short key, boolean value) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean replace(short key, boolean oldValue, boolean newValue) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean computeIfAbsent(short key, IntPredicate mappingFunction) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean computeIfAbsentNullable(short key, IntFunction<? extends Boolean> mappingFunction) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean computeIfAbsent(short key, Short2BooleanFunction mappingFunction) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean computeIfPresent(short key, BiFunction<? super Short, ? super Boolean, ? extends Boolean> remappingFunction) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean compute(short key, BiFunction<? super Short, ? super Boolean, ? extends Boolean> remappingFunction) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean merge(short key, boolean value, BiFunction<? super Boolean, ? super Boolean, ? extends Boolean> remappingFunction) {
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
      public Boolean replace(Short key, Boolean value) {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      public boolean replace(Short key, Boolean oldValue, Boolean newValue) {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      public Boolean putIfAbsent(Short key, Boolean value) {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      public Boolean computeIfAbsent(Short key, Function<? super Short, ? extends Boolean> mappingFunction) {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      public Boolean computeIfPresent(Short key, BiFunction<? super Short, ? super Boolean, ? extends Boolean> remappingFunction) {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      public Boolean compute(Short key, BiFunction<? super Short, ? super Boolean, ? extends Boolean> remappingFunction) {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      public Boolean merge(Short key, Boolean value, BiFunction<? super Boolean, ? super Boolean, ? extends Boolean> remappingFunction) {
         throw new UnsupportedOperationException();
      }
   }
}
