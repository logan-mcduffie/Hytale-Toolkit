package it.unimi.dsi.fastutil.floats;

import it.unimi.dsi.fastutil.HashCommon;
import it.unimi.dsi.fastutil.chars.CharCollection;
import it.unimi.dsi.fastutil.chars.CharCollections;
import it.unimi.dsi.fastutil.chars.CharSets;
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

public final class Float2CharMaps {
   public static final Float2CharMaps.EmptyMap EMPTY_MAP = new Float2CharMaps.EmptyMap();

   private Float2CharMaps() {
   }

   public static ObjectIterator<Float2CharMap.Entry> fastIterator(Float2CharMap map) {
      ObjectSet<Float2CharMap.Entry> entries = map.float2CharEntrySet();
      return entries instanceof Float2CharMap.FastEntrySet ? ((Float2CharMap.FastEntrySet)entries).fastIterator() : entries.iterator();
   }

   public static void fastForEach(Float2CharMap map, Consumer<? super Float2CharMap.Entry> consumer) {
      ObjectSet<Float2CharMap.Entry> entries = map.float2CharEntrySet();
      if (entries instanceof Float2CharMap.FastEntrySet) {
         ((Float2CharMap.FastEntrySet)entries).fastForEach(consumer);
      } else {
         entries.forEach(consumer);
      }
   }

   public static ObjectIterable<Float2CharMap.Entry> fastIterable(Float2CharMap map) {
      final ObjectSet<Float2CharMap.Entry> entries = map.float2CharEntrySet();
      return (ObjectIterable<Float2CharMap.Entry>)(entries instanceof Float2CharMap.FastEntrySet ? new ObjectIterable<Float2CharMap.Entry>() {
         @Override
         public ObjectIterator<Float2CharMap.Entry> iterator() {
            return ((Float2CharMap.FastEntrySet)entries).fastIterator();
         }

         @Override
         public ObjectSpliterator<Float2CharMap.Entry> spliterator() {
            return entries.spliterator();
         }

         @Override
         public void forEach(Consumer<? super Float2CharMap.Entry> consumer) {
            ((Float2CharMap.FastEntrySet)entries).fastForEach(consumer);
         }
      } : entries);
   }

   public static Float2CharMap singleton(float key, char value) {
      return new Float2CharMaps.Singleton(key, value);
   }

   public static Float2CharMap singleton(Float key, Character value) {
      return new Float2CharMaps.Singleton(key, value);
   }

   public static Float2CharMap synchronize(Float2CharMap m) {
      return new Float2CharMaps.SynchronizedMap(m);
   }

   public static Float2CharMap synchronize(Float2CharMap m, Object sync) {
      return new Float2CharMaps.SynchronizedMap(m, sync);
   }

   public static Float2CharMap unmodifiable(Float2CharMap m) {
      return new Float2CharMaps.UnmodifiableMap(m);
   }

   public static class EmptyMap extends Float2CharFunctions.EmptyFunction implements Float2CharMap, Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;

      protected EmptyMap() {
      }

      @Override
      public boolean containsValue(char v) {
         return false;
      }

      @Deprecated
      @Override
      public Character getOrDefault(Object key, Character defaultValue) {
         return defaultValue;
      }

      @Override
      public char getOrDefault(float key, char defaultValue) {
         return defaultValue;
      }

      @Deprecated
      @Override
      public boolean containsValue(Object ov) {
         return false;
      }

      @Override
      public void putAll(Map<? extends Float, ? extends Character> m) {
         throw new UnsupportedOperationException();
      }

      @Override
      public ObjectSet<Float2CharMap.Entry> float2CharEntrySet() {
         return ObjectSets.EMPTY_SET;
      }

      @Override
      public FloatSet keySet() {
         return FloatSets.EMPTY_SET;
      }

      @Override
      public CharCollection values() {
         return CharSets.EMPTY_SET;
      }

      @Override
      public void forEach(BiConsumer<? super Float, ? super Character> consumer) {
      }

      @Override
      public Object clone() {
         return Float2CharMaps.EMPTY_MAP;
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

   public static class Singleton extends Float2CharFunctions.Singleton implements Float2CharMap, Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected transient ObjectSet<Float2CharMap.Entry> entries;
      protected transient FloatSet keys;
      protected transient CharCollection values;

      protected Singleton(float key, char value) {
         super(key, value);
      }

      @Override
      public boolean containsValue(char v) {
         return this.value == v;
      }

      @Deprecated
      @Override
      public boolean containsValue(Object ov) {
         return (Character)ov == this.value;
      }

      @Override
      public void putAll(Map<? extends Float, ? extends Character> m) {
         throw new UnsupportedOperationException();
      }

      @Override
      public ObjectSet<Float2CharMap.Entry> float2CharEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSets.singleton(new AbstractFloat2CharMap.BasicEntry(this.key, this.value));
         }

         return this.entries;
      }

      @Deprecated
      @Override
      public ObjectSet<Entry<Float, Character>> entrySet() {
         return this.float2CharEntrySet();
      }

      @Override
      public FloatSet keySet() {
         if (this.keys == null) {
            this.keys = FloatSets.singleton(this.key);
         }

         return this.keys;
      }

      @Override
      public CharCollection values() {
         if (this.values == null) {
            this.values = CharSets.singleton(this.value);
         }

         return this.values;
      }

      @Override
      public boolean isEmpty() {
         return false;
      }

      @Override
      public int hashCode() {
         return HashCommon.float2int(this.key) ^ this.value;
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

   public static class SynchronizedMap extends Float2CharFunctions.SynchronizedFunction implements Float2CharMap, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Float2CharMap map;
      protected transient ObjectSet<Float2CharMap.Entry> entries;
      protected transient FloatSet keys;
      protected transient CharCollection values;

      protected SynchronizedMap(Float2CharMap m, Object sync) {
         super(m, sync);
         this.map = m;
      }

      protected SynchronizedMap(Float2CharMap m) {
         super(m);
         this.map = m;
      }

      @Override
      public boolean containsValue(char v) {
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
      public void putAll(Map<? extends Float, ? extends Character> m) {
         synchronized (this.sync) {
            this.map.putAll(m);
         }
      }

      @Override
      public ObjectSet<Float2CharMap.Entry> float2CharEntrySet() {
         synchronized (this.sync) {
            if (this.entries == null) {
               this.entries = ObjectSets.synchronize(this.map.float2CharEntrySet(), this.sync);
            }

            return this.entries;
         }
      }

      @Deprecated
      @Override
      public ObjectSet<Entry<Float, Character>> entrySet() {
         return this.float2CharEntrySet();
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
      public CharCollection values() {
         synchronized (this.sync) {
            if (this.values == null) {
               this.values = CharCollections.synchronize(this.map.values(), this.sync);
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
      public char getOrDefault(float key, char defaultValue) {
         synchronized (this.sync) {
            return this.map.getOrDefault(key, defaultValue);
         }
      }

      @Override
      public void forEach(BiConsumer<? super Float, ? super Character> action) {
         synchronized (this.sync) {
            this.map.forEach(action);
         }
      }

      @Override
      public void replaceAll(BiFunction<? super Float, ? super Character, ? extends Character> function) {
         synchronized (this.sync) {
            this.map.replaceAll(function);
         }
      }

      @Override
      public char putIfAbsent(float key, char value) {
         synchronized (this.sync) {
            return this.map.putIfAbsent(key, value);
         }
      }

      @Override
      public boolean remove(float key, char value) {
         synchronized (this.sync) {
            return this.map.remove(key, value);
         }
      }

      @Override
      public char replace(float key, char value) {
         synchronized (this.sync) {
            return this.map.replace(key, value);
         }
      }

      @Override
      public boolean replace(float key, char oldValue, char newValue) {
         synchronized (this.sync) {
            return this.map.replace(key, oldValue, newValue);
         }
      }

      @Override
      public char computeIfAbsent(float key, DoubleToIntFunction mappingFunction) {
         synchronized (this.sync) {
            return this.map.computeIfAbsent(key, mappingFunction);
         }
      }

      @Override
      public char computeIfAbsentNullable(float key, DoubleFunction<? extends Character> mappingFunction) {
         synchronized (this.sync) {
            return this.map.computeIfAbsentNullable(key, mappingFunction);
         }
      }

      @Override
      public char computeIfAbsent(float key, Float2CharFunction mappingFunction) {
         synchronized (this.sync) {
            return this.map.computeIfAbsent(key, mappingFunction);
         }
      }

      @Override
      public char computeIfPresent(float key, BiFunction<? super Float, ? super Character, ? extends Character> remappingFunction) {
         synchronized (this.sync) {
            return this.map.computeIfPresent(key, remappingFunction);
         }
      }

      @Override
      public char compute(float key, BiFunction<? super Float, ? super Character, ? extends Character> remappingFunction) {
         synchronized (this.sync) {
            return this.map.compute(key, remappingFunction);
         }
      }

      @Override
      public char merge(float key, char value, BiFunction<? super Character, ? super Character, ? extends Character> remappingFunction) {
         synchronized (this.sync) {
            return this.map.merge(key, value, remappingFunction);
         }
      }

      @Deprecated
      @Override
      public Character getOrDefault(Object key, Character defaultValue) {
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
      public Character replace(Float key, Character value) {
         synchronized (this.sync) {
            return this.map.replace(key, value);
         }
      }

      @Deprecated
      @Override
      public boolean replace(Float key, Character oldValue, Character newValue) {
         synchronized (this.sync) {
            return this.map.replace(key, oldValue, newValue);
         }
      }

      @Deprecated
      @Override
      public Character putIfAbsent(Float key, Character value) {
         synchronized (this.sync) {
            return this.map.putIfAbsent(key, value);
         }
      }

      @Deprecated
      @Override
      public Character computeIfAbsent(Float key, Function<? super Float, ? extends Character> mappingFunction) {
         synchronized (this.sync) {
            return this.map.computeIfAbsent(key, mappingFunction);
         }
      }

      @Deprecated
      @Override
      public Character computeIfPresent(Float key, BiFunction<? super Float, ? super Character, ? extends Character> remappingFunction) {
         synchronized (this.sync) {
            return this.map.computeIfPresent(key, remappingFunction);
         }
      }

      @Deprecated
      @Override
      public Character compute(Float key, BiFunction<? super Float, ? super Character, ? extends Character> remappingFunction) {
         synchronized (this.sync) {
            return this.map.compute(key, remappingFunction);
         }
      }

      @Deprecated
      @Override
      public Character merge(Float key, Character value, BiFunction<? super Character, ? super Character, ? extends Character> remappingFunction) {
         synchronized (this.sync) {
            return this.map.merge(key, value, remappingFunction);
         }
      }
   }

   public static class UnmodifiableMap extends Float2CharFunctions.UnmodifiableFunction implements Float2CharMap, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Float2CharMap map;
      protected transient ObjectSet<Float2CharMap.Entry> entries;
      protected transient FloatSet keys;
      protected transient CharCollection values;

      protected UnmodifiableMap(Float2CharMap m) {
         super(m);
         this.map = m;
      }

      @Override
      public boolean containsValue(char v) {
         return this.map.containsValue(v);
      }

      @Deprecated
      @Override
      public boolean containsValue(Object ov) {
         return this.map.containsValue(ov);
      }

      @Override
      public void putAll(Map<? extends Float, ? extends Character> m) {
         throw new UnsupportedOperationException();
      }

      @Override
      public ObjectSet<Float2CharMap.Entry> float2CharEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSets.unmodifiable(this.map.float2CharEntrySet());
         }

         return this.entries;
      }

      @Deprecated
      @Override
      public ObjectSet<Entry<Float, Character>> entrySet() {
         return this.float2CharEntrySet();
      }

      @Override
      public FloatSet keySet() {
         if (this.keys == null) {
            this.keys = FloatSets.unmodifiable(this.map.keySet());
         }

         return this.keys;
      }

      @Override
      public CharCollection values() {
         if (this.values == null) {
            this.values = CharCollections.unmodifiable(this.map.values());
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
      public char getOrDefault(float key, char defaultValue) {
         return this.map.getOrDefault(key, defaultValue);
      }

      @Override
      public void forEach(BiConsumer<? super Float, ? super Character> action) {
         this.map.forEach(action);
      }

      @Override
      public void replaceAll(BiFunction<? super Float, ? super Character, ? extends Character> function) {
         throw new UnsupportedOperationException();
      }

      @Override
      public char putIfAbsent(float key, char value) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean remove(float key, char value) {
         throw new UnsupportedOperationException();
      }

      @Override
      public char replace(float key, char value) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean replace(float key, char oldValue, char newValue) {
         throw new UnsupportedOperationException();
      }

      @Override
      public char computeIfAbsent(float key, DoubleToIntFunction mappingFunction) {
         throw new UnsupportedOperationException();
      }

      @Override
      public char computeIfAbsentNullable(float key, DoubleFunction<? extends Character> mappingFunction) {
         throw new UnsupportedOperationException();
      }

      @Override
      public char computeIfAbsent(float key, Float2CharFunction mappingFunction) {
         throw new UnsupportedOperationException();
      }

      @Override
      public char computeIfPresent(float key, BiFunction<? super Float, ? super Character, ? extends Character> remappingFunction) {
         throw new UnsupportedOperationException();
      }

      @Override
      public char compute(float key, BiFunction<? super Float, ? super Character, ? extends Character> remappingFunction) {
         throw new UnsupportedOperationException();
      }

      @Override
      public char merge(float key, char value, BiFunction<? super Character, ? super Character, ? extends Character> remappingFunction) {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      @Override
      public Character getOrDefault(Object key, Character defaultValue) {
         return this.map.getOrDefault(key, defaultValue);
      }

      @Deprecated
      @Override
      public boolean remove(Object key, Object value) {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      @Override
      public Character replace(Float key, Character value) {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      @Override
      public boolean replace(Float key, Character oldValue, Character newValue) {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      @Override
      public Character putIfAbsent(Float key, Character value) {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      @Override
      public Character computeIfAbsent(Float key, Function<? super Float, ? extends Character> mappingFunction) {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      @Override
      public Character computeIfPresent(Float key, BiFunction<? super Float, ? super Character, ? extends Character> remappingFunction) {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      @Override
      public Character compute(Float key, BiFunction<? super Float, ? super Character, ? extends Character> remappingFunction) {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      @Override
      public Character merge(Float key, Character value, BiFunction<? super Character, ? super Character, ? extends Character> remappingFunction) {
         throw new UnsupportedOperationException();
      }
   }
}
