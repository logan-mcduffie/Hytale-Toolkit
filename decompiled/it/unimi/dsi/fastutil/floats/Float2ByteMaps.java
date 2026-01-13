package it.unimi.dsi.fastutil.floats;

import it.unimi.dsi.fastutil.HashCommon;
import it.unimi.dsi.fastutil.bytes.ByteCollection;
import it.unimi.dsi.fastutil.bytes.ByteCollections;
import it.unimi.dsi.fastutil.bytes.ByteSets;
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

public final class Float2ByteMaps {
   public static final Float2ByteMaps.EmptyMap EMPTY_MAP = new Float2ByteMaps.EmptyMap();

   private Float2ByteMaps() {
   }

   public static ObjectIterator<Float2ByteMap.Entry> fastIterator(Float2ByteMap map) {
      ObjectSet<Float2ByteMap.Entry> entries = map.float2ByteEntrySet();
      return entries instanceof Float2ByteMap.FastEntrySet ? ((Float2ByteMap.FastEntrySet)entries).fastIterator() : entries.iterator();
   }

   public static void fastForEach(Float2ByteMap map, Consumer<? super Float2ByteMap.Entry> consumer) {
      ObjectSet<Float2ByteMap.Entry> entries = map.float2ByteEntrySet();
      if (entries instanceof Float2ByteMap.FastEntrySet) {
         ((Float2ByteMap.FastEntrySet)entries).fastForEach(consumer);
      } else {
         entries.forEach(consumer);
      }
   }

   public static ObjectIterable<Float2ByteMap.Entry> fastIterable(Float2ByteMap map) {
      final ObjectSet<Float2ByteMap.Entry> entries = map.float2ByteEntrySet();
      return (ObjectIterable<Float2ByteMap.Entry>)(entries instanceof Float2ByteMap.FastEntrySet ? new ObjectIterable<Float2ByteMap.Entry>() {
         @Override
         public ObjectIterator<Float2ByteMap.Entry> iterator() {
            return ((Float2ByteMap.FastEntrySet)entries).fastIterator();
         }

         @Override
         public ObjectSpliterator<Float2ByteMap.Entry> spliterator() {
            return entries.spliterator();
         }

         @Override
         public void forEach(Consumer<? super Float2ByteMap.Entry> consumer) {
            ((Float2ByteMap.FastEntrySet)entries).fastForEach(consumer);
         }
      } : entries);
   }

   public static Float2ByteMap singleton(float key, byte value) {
      return new Float2ByteMaps.Singleton(key, value);
   }

   public static Float2ByteMap singleton(Float key, Byte value) {
      return new Float2ByteMaps.Singleton(key, value);
   }

   public static Float2ByteMap synchronize(Float2ByteMap m) {
      return new Float2ByteMaps.SynchronizedMap(m);
   }

   public static Float2ByteMap synchronize(Float2ByteMap m, Object sync) {
      return new Float2ByteMaps.SynchronizedMap(m, sync);
   }

   public static Float2ByteMap unmodifiable(Float2ByteMap m) {
      return new Float2ByteMaps.UnmodifiableMap(m);
   }

   public static class EmptyMap extends Float2ByteFunctions.EmptyFunction implements Float2ByteMap, Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;

      protected EmptyMap() {
      }

      @Override
      public boolean containsValue(byte v) {
         return false;
      }

      @Deprecated
      @Override
      public Byte getOrDefault(Object key, Byte defaultValue) {
         return defaultValue;
      }

      @Override
      public byte getOrDefault(float key, byte defaultValue) {
         return defaultValue;
      }

      @Deprecated
      @Override
      public boolean containsValue(Object ov) {
         return false;
      }

      @Override
      public void putAll(Map<? extends Float, ? extends Byte> m) {
         throw new UnsupportedOperationException();
      }

      @Override
      public ObjectSet<Float2ByteMap.Entry> float2ByteEntrySet() {
         return ObjectSets.EMPTY_SET;
      }

      @Override
      public FloatSet keySet() {
         return FloatSets.EMPTY_SET;
      }

      @Override
      public ByteCollection values() {
         return ByteSets.EMPTY_SET;
      }

      @Override
      public void forEach(BiConsumer<? super Float, ? super Byte> consumer) {
      }

      @Override
      public Object clone() {
         return Float2ByteMaps.EMPTY_MAP;
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

   public static class Singleton extends Float2ByteFunctions.Singleton implements Float2ByteMap, Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected transient ObjectSet<Float2ByteMap.Entry> entries;
      protected transient FloatSet keys;
      protected transient ByteCollection values;

      protected Singleton(float key, byte value) {
         super(key, value);
      }

      @Override
      public boolean containsValue(byte v) {
         return this.value == v;
      }

      @Deprecated
      @Override
      public boolean containsValue(Object ov) {
         return (Byte)ov == this.value;
      }

      @Override
      public void putAll(Map<? extends Float, ? extends Byte> m) {
         throw new UnsupportedOperationException();
      }

      @Override
      public ObjectSet<Float2ByteMap.Entry> float2ByteEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSets.singleton(new AbstractFloat2ByteMap.BasicEntry(this.key, this.value));
         }

         return this.entries;
      }

      @Deprecated
      @Override
      public ObjectSet<Entry<Float, Byte>> entrySet() {
         return this.float2ByteEntrySet();
      }

      @Override
      public FloatSet keySet() {
         if (this.keys == null) {
            this.keys = FloatSets.singleton(this.key);
         }

         return this.keys;
      }

      @Override
      public ByteCollection values() {
         if (this.values == null) {
            this.values = ByteSets.singleton(this.value);
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

   public static class SynchronizedMap extends Float2ByteFunctions.SynchronizedFunction implements Float2ByteMap, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Float2ByteMap map;
      protected transient ObjectSet<Float2ByteMap.Entry> entries;
      protected transient FloatSet keys;
      protected transient ByteCollection values;

      protected SynchronizedMap(Float2ByteMap m, Object sync) {
         super(m, sync);
         this.map = m;
      }

      protected SynchronizedMap(Float2ByteMap m) {
         super(m);
         this.map = m;
      }

      @Override
      public boolean containsValue(byte v) {
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
      public void putAll(Map<? extends Float, ? extends Byte> m) {
         synchronized (this.sync) {
            this.map.putAll(m);
         }
      }

      @Override
      public ObjectSet<Float2ByteMap.Entry> float2ByteEntrySet() {
         synchronized (this.sync) {
            if (this.entries == null) {
               this.entries = ObjectSets.synchronize(this.map.float2ByteEntrySet(), this.sync);
            }

            return this.entries;
         }
      }

      @Deprecated
      @Override
      public ObjectSet<Entry<Float, Byte>> entrySet() {
         return this.float2ByteEntrySet();
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
      public ByteCollection values() {
         synchronized (this.sync) {
            if (this.values == null) {
               this.values = ByteCollections.synchronize(this.map.values(), this.sync);
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
      public byte getOrDefault(float key, byte defaultValue) {
         synchronized (this.sync) {
            return this.map.getOrDefault(key, defaultValue);
         }
      }

      @Override
      public void forEach(BiConsumer<? super Float, ? super Byte> action) {
         synchronized (this.sync) {
            this.map.forEach(action);
         }
      }

      @Override
      public void replaceAll(BiFunction<? super Float, ? super Byte, ? extends Byte> function) {
         synchronized (this.sync) {
            this.map.replaceAll(function);
         }
      }

      @Override
      public byte putIfAbsent(float key, byte value) {
         synchronized (this.sync) {
            return this.map.putIfAbsent(key, value);
         }
      }

      @Override
      public boolean remove(float key, byte value) {
         synchronized (this.sync) {
            return this.map.remove(key, value);
         }
      }

      @Override
      public byte replace(float key, byte value) {
         synchronized (this.sync) {
            return this.map.replace(key, value);
         }
      }

      @Override
      public boolean replace(float key, byte oldValue, byte newValue) {
         synchronized (this.sync) {
            return this.map.replace(key, oldValue, newValue);
         }
      }

      @Override
      public byte computeIfAbsent(float key, DoubleToIntFunction mappingFunction) {
         synchronized (this.sync) {
            return this.map.computeIfAbsent(key, mappingFunction);
         }
      }

      @Override
      public byte computeIfAbsentNullable(float key, DoubleFunction<? extends Byte> mappingFunction) {
         synchronized (this.sync) {
            return this.map.computeIfAbsentNullable(key, mappingFunction);
         }
      }

      @Override
      public byte computeIfAbsent(float key, Float2ByteFunction mappingFunction) {
         synchronized (this.sync) {
            return this.map.computeIfAbsent(key, mappingFunction);
         }
      }

      @Override
      public byte computeIfPresent(float key, BiFunction<? super Float, ? super Byte, ? extends Byte> remappingFunction) {
         synchronized (this.sync) {
            return this.map.computeIfPresent(key, remappingFunction);
         }
      }

      @Override
      public byte compute(float key, BiFunction<? super Float, ? super Byte, ? extends Byte> remappingFunction) {
         synchronized (this.sync) {
            return this.map.compute(key, remappingFunction);
         }
      }

      @Override
      public byte merge(float key, byte value, BiFunction<? super Byte, ? super Byte, ? extends Byte> remappingFunction) {
         synchronized (this.sync) {
            return this.map.merge(key, value, remappingFunction);
         }
      }

      @Deprecated
      @Override
      public Byte getOrDefault(Object key, Byte defaultValue) {
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
      public Byte replace(Float key, Byte value) {
         synchronized (this.sync) {
            return this.map.replace(key, value);
         }
      }

      @Deprecated
      @Override
      public boolean replace(Float key, Byte oldValue, Byte newValue) {
         synchronized (this.sync) {
            return this.map.replace(key, oldValue, newValue);
         }
      }

      @Deprecated
      @Override
      public Byte putIfAbsent(Float key, Byte value) {
         synchronized (this.sync) {
            return this.map.putIfAbsent(key, value);
         }
      }

      @Deprecated
      @Override
      public Byte computeIfAbsent(Float key, Function<? super Float, ? extends Byte> mappingFunction) {
         synchronized (this.sync) {
            return this.map.computeIfAbsent(key, mappingFunction);
         }
      }

      @Deprecated
      @Override
      public Byte computeIfPresent(Float key, BiFunction<? super Float, ? super Byte, ? extends Byte> remappingFunction) {
         synchronized (this.sync) {
            return this.map.computeIfPresent(key, remappingFunction);
         }
      }

      @Deprecated
      @Override
      public Byte compute(Float key, BiFunction<? super Float, ? super Byte, ? extends Byte> remappingFunction) {
         synchronized (this.sync) {
            return this.map.compute(key, remappingFunction);
         }
      }

      @Deprecated
      @Override
      public Byte merge(Float key, Byte value, BiFunction<? super Byte, ? super Byte, ? extends Byte> remappingFunction) {
         synchronized (this.sync) {
            return this.map.merge(key, value, remappingFunction);
         }
      }
   }

   public static class UnmodifiableMap extends Float2ByteFunctions.UnmodifiableFunction implements Float2ByteMap, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Float2ByteMap map;
      protected transient ObjectSet<Float2ByteMap.Entry> entries;
      protected transient FloatSet keys;
      protected transient ByteCollection values;

      protected UnmodifiableMap(Float2ByteMap m) {
         super(m);
         this.map = m;
      }

      @Override
      public boolean containsValue(byte v) {
         return this.map.containsValue(v);
      }

      @Deprecated
      @Override
      public boolean containsValue(Object ov) {
         return this.map.containsValue(ov);
      }

      @Override
      public void putAll(Map<? extends Float, ? extends Byte> m) {
         throw new UnsupportedOperationException();
      }

      @Override
      public ObjectSet<Float2ByteMap.Entry> float2ByteEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSets.unmodifiable(this.map.float2ByteEntrySet());
         }

         return this.entries;
      }

      @Deprecated
      @Override
      public ObjectSet<Entry<Float, Byte>> entrySet() {
         return this.float2ByteEntrySet();
      }

      @Override
      public FloatSet keySet() {
         if (this.keys == null) {
            this.keys = FloatSets.unmodifiable(this.map.keySet());
         }

         return this.keys;
      }

      @Override
      public ByteCollection values() {
         if (this.values == null) {
            this.values = ByteCollections.unmodifiable(this.map.values());
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
      public byte getOrDefault(float key, byte defaultValue) {
         return this.map.getOrDefault(key, defaultValue);
      }

      @Override
      public void forEach(BiConsumer<? super Float, ? super Byte> action) {
         this.map.forEach(action);
      }

      @Override
      public void replaceAll(BiFunction<? super Float, ? super Byte, ? extends Byte> function) {
         throw new UnsupportedOperationException();
      }

      @Override
      public byte putIfAbsent(float key, byte value) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean remove(float key, byte value) {
         throw new UnsupportedOperationException();
      }

      @Override
      public byte replace(float key, byte value) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean replace(float key, byte oldValue, byte newValue) {
         throw new UnsupportedOperationException();
      }

      @Override
      public byte computeIfAbsent(float key, DoubleToIntFunction mappingFunction) {
         throw new UnsupportedOperationException();
      }

      @Override
      public byte computeIfAbsentNullable(float key, DoubleFunction<? extends Byte> mappingFunction) {
         throw new UnsupportedOperationException();
      }

      @Override
      public byte computeIfAbsent(float key, Float2ByteFunction mappingFunction) {
         throw new UnsupportedOperationException();
      }

      @Override
      public byte computeIfPresent(float key, BiFunction<? super Float, ? super Byte, ? extends Byte> remappingFunction) {
         throw new UnsupportedOperationException();
      }

      @Override
      public byte compute(float key, BiFunction<? super Float, ? super Byte, ? extends Byte> remappingFunction) {
         throw new UnsupportedOperationException();
      }

      @Override
      public byte merge(float key, byte value, BiFunction<? super Byte, ? super Byte, ? extends Byte> remappingFunction) {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      @Override
      public Byte getOrDefault(Object key, Byte defaultValue) {
         return this.map.getOrDefault(key, defaultValue);
      }

      @Deprecated
      @Override
      public boolean remove(Object key, Object value) {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      @Override
      public Byte replace(Float key, Byte value) {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      @Override
      public boolean replace(Float key, Byte oldValue, Byte newValue) {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      @Override
      public Byte putIfAbsent(Float key, Byte value) {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      @Override
      public Byte computeIfAbsent(Float key, Function<? super Float, ? extends Byte> mappingFunction) {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      @Override
      public Byte computeIfPresent(Float key, BiFunction<? super Float, ? super Byte, ? extends Byte> remappingFunction) {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      @Override
      public Byte compute(Float key, BiFunction<? super Float, ? super Byte, ? extends Byte> remappingFunction) {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      @Override
      public Byte merge(Float key, Byte value, BiFunction<? super Byte, ? super Byte, ? extends Byte> remappingFunction) {
         throw new UnsupportedOperationException();
      }
   }
}
