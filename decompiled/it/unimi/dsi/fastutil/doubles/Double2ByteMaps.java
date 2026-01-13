package it.unimi.dsi.fastutil.doubles;

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

public final class Double2ByteMaps {
   public static final Double2ByteMaps.EmptyMap EMPTY_MAP = new Double2ByteMaps.EmptyMap();

   private Double2ByteMaps() {
   }

   public static ObjectIterator<Double2ByteMap.Entry> fastIterator(Double2ByteMap map) {
      ObjectSet<Double2ByteMap.Entry> entries = map.double2ByteEntrySet();
      return entries instanceof Double2ByteMap.FastEntrySet ? ((Double2ByteMap.FastEntrySet)entries).fastIterator() : entries.iterator();
   }

   public static void fastForEach(Double2ByteMap map, Consumer<? super Double2ByteMap.Entry> consumer) {
      ObjectSet<Double2ByteMap.Entry> entries = map.double2ByteEntrySet();
      if (entries instanceof Double2ByteMap.FastEntrySet) {
         ((Double2ByteMap.FastEntrySet)entries).fastForEach(consumer);
      } else {
         entries.forEach(consumer);
      }
   }

   public static ObjectIterable<Double2ByteMap.Entry> fastIterable(Double2ByteMap map) {
      final ObjectSet<Double2ByteMap.Entry> entries = map.double2ByteEntrySet();
      return (ObjectIterable<Double2ByteMap.Entry>)(entries instanceof Double2ByteMap.FastEntrySet ? new ObjectIterable<Double2ByteMap.Entry>() {
         @Override
         public ObjectIterator<Double2ByteMap.Entry> iterator() {
            return ((Double2ByteMap.FastEntrySet)entries).fastIterator();
         }

         @Override
         public ObjectSpliterator<Double2ByteMap.Entry> spliterator() {
            return entries.spliterator();
         }

         @Override
         public void forEach(Consumer<? super Double2ByteMap.Entry> consumer) {
            ((Double2ByteMap.FastEntrySet)entries).fastForEach(consumer);
         }
      } : entries);
   }

   public static Double2ByteMap singleton(double key, byte value) {
      return new Double2ByteMaps.Singleton(key, value);
   }

   public static Double2ByteMap singleton(Double key, Byte value) {
      return new Double2ByteMaps.Singleton(key, value);
   }

   public static Double2ByteMap synchronize(Double2ByteMap m) {
      return new Double2ByteMaps.SynchronizedMap(m);
   }

   public static Double2ByteMap synchronize(Double2ByteMap m, Object sync) {
      return new Double2ByteMaps.SynchronizedMap(m, sync);
   }

   public static Double2ByteMap unmodifiable(Double2ByteMap m) {
      return new Double2ByteMaps.UnmodifiableMap(m);
   }

   public static class EmptyMap extends Double2ByteFunctions.EmptyFunction implements Double2ByteMap, Serializable, Cloneable {
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
      public byte getOrDefault(double key, byte defaultValue) {
         return defaultValue;
      }

      @Deprecated
      @Override
      public boolean containsValue(Object ov) {
         return false;
      }

      @Override
      public void putAll(Map<? extends Double, ? extends Byte> m) {
         throw new UnsupportedOperationException();
      }

      @Override
      public ObjectSet<Double2ByteMap.Entry> double2ByteEntrySet() {
         return ObjectSets.EMPTY_SET;
      }

      @Override
      public DoubleSet keySet() {
         return DoubleSets.EMPTY_SET;
      }

      @Override
      public ByteCollection values() {
         return ByteSets.EMPTY_SET;
      }

      @Override
      public void forEach(BiConsumer<? super Double, ? super Byte> consumer) {
      }

      @Override
      public Object clone() {
         return Double2ByteMaps.EMPTY_MAP;
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

   public static class Singleton extends Double2ByteFunctions.Singleton implements Double2ByteMap, Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected transient ObjectSet<Double2ByteMap.Entry> entries;
      protected transient DoubleSet keys;
      protected transient ByteCollection values;

      protected Singleton(double key, byte value) {
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
      public void putAll(Map<? extends Double, ? extends Byte> m) {
         throw new UnsupportedOperationException();
      }

      @Override
      public ObjectSet<Double2ByteMap.Entry> double2ByteEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSets.singleton(new AbstractDouble2ByteMap.BasicEntry(this.key, this.value));
         }

         return this.entries;
      }

      @Deprecated
      @Override
      public ObjectSet<Entry<Double, Byte>> entrySet() {
         return this.double2ByteEntrySet();
      }

      @Override
      public DoubleSet keySet() {
         if (this.keys == null) {
            this.keys = DoubleSets.singleton(this.key);
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
         return HashCommon.double2int(this.key) ^ this.value;
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

   public static class SynchronizedMap extends Double2ByteFunctions.SynchronizedFunction implements Double2ByteMap, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Double2ByteMap map;
      protected transient ObjectSet<Double2ByteMap.Entry> entries;
      protected transient DoubleSet keys;
      protected transient ByteCollection values;

      protected SynchronizedMap(Double2ByteMap m, Object sync) {
         super(m, sync);
         this.map = m;
      }

      protected SynchronizedMap(Double2ByteMap m) {
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
      public void putAll(Map<? extends Double, ? extends Byte> m) {
         synchronized (this.sync) {
            this.map.putAll(m);
         }
      }

      @Override
      public ObjectSet<Double2ByteMap.Entry> double2ByteEntrySet() {
         synchronized (this.sync) {
            if (this.entries == null) {
               this.entries = ObjectSets.synchronize(this.map.double2ByteEntrySet(), this.sync);
            }

            return this.entries;
         }
      }

      @Deprecated
      @Override
      public ObjectSet<Entry<Double, Byte>> entrySet() {
         return this.double2ByteEntrySet();
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
      public byte getOrDefault(double key, byte defaultValue) {
         synchronized (this.sync) {
            return this.map.getOrDefault(key, defaultValue);
         }
      }

      @Override
      public void forEach(BiConsumer<? super Double, ? super Byte> action) {
         synchronized (this.sync) {
            this.map.forEach(action);
         }
      }

      @Override
      public void replaceAll(BiFunction<? super Double, ? super Byte, ? extends Byte> function) {
         synchronized (this.sync) {
            this.map.replaceAll(function);
         }
      }

      @Override
      public byte putIfAbsent(double key, byte value) {
         synchronized (this.sync) {
            return this.map.putIfAbsent(key, value);
         }
      }

      @Override
      public boolean remove(double key, byte value) {
         synchronized (this.sync) {
            return this.map.remove(key, value);
         }
      }

      @Override
      public byte replace(double key, byte value) {
         synchronized (this.sync) {
            return this.map.replace(key, value);
         }
      }

      @Override
      public boolean replace(double key, byte oldValue, byte newValue) {
         synchronized (this.sync) {
            return this.map.replace(key, oldValue, newValue);
         }
      }

      @Override
      public byte computeIfAbsent(double key, DoubleToIntFunction mappingFunction) {
         synchronized (this.sync) {
            return this.map.computeIfAbsent(key, mappingFunction);
         }
      }

      @Override
      public byte computeIfAbsentNullable(double key, DoubleFunction<? extends Byte> mappingFunction) {
         synchronized (this.sync) {
            return this.map.computeIfAbsentNullable(key, mappingFunction);
         }
      }

      @Override
      public byte computeIfAbsent(double key, Double2ByteFunction mappingFunction) {
         synchronized (this.sync) {
            return this.map.computeIfAbsent(key, mappingFunction);
         }
      }

      @Override
      public byte computeIfPresent(double key, BiFunction<? super Double, ? super Byte, ? extends Byte> remappingFunction) {
         synchronized (this.sync) {
            return this.map.computeIfPresent(key, remappingFunction);
         }
      }

      @Override
      public byte compute(double key, BiFunction<? super Double, ? super Byte, ? extends Byte> remappingFunction) {
         synchronized (this.sync) {
            return this.map.compute(key, remappingFunction);
         }
      }

      @Override
      public byte merge(double key, byte value, BiFunction<? super Byte, ? super Byte, ? extends Byte> remappingFunction) {
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
      public Byte replace(Double key, Byte value) {
         synchronized (this.sync) {
            return this.map.replace(key, value);
         }
      }

      @Deprecated
      @Override
      public boolean replace(Double key, Byte oldValue, Byte newValue) {
         synchronized (this.sync) {
            return this.map.replace(key, oldValue, newValue);
         }
      }

      @Deprecated
      @Override
      public Byte putIfAbsent(Double key, Byte value) {
         synchronized (this.sync) {
            return this.map.putIfAbsent(key, value);
         }
      }

      @Deprecated
      @Override
      public Byte computeIfAbsent(Double key, Function<? super Double, ? extends Byte> mappingFunction) {
         synchronized (this.sync) {
            return this.map.computeIfAbsent(key, mappingFunction);
         }
      }

      @Deprecated
      @Override
      public Byte computeIfPresent(Double key, BiFunction<? super Double, ? super Byte, ? extends Byte> remappingFunction) {
         synchronized (this.sync) {
            return this.map.computeIfPresent(key, remappingFunction);
         }
      }

      @Deprecated
      @Override
      public Byte compute(Double key, BiFunction<? super Double, ? super Byte, ? extends Byte> remappingFunction) {
         synchronized (this.sync) {
            return this.map.compute(key, remappingFunction);
         }
      }

      @Deprecated
      @Override
      public Byte merge(Double key, Byte value, BiFunction<? super Byte, ? super Byte, ? extends Byte> remappingFunction) {
         synchronized (this.sync) {
            return this.map.merge(key, value, remappingFunction);
         }
      }
   }

   public static class UnmodifiableMap extends Double2ByteFunctions.UnmodifiableFunction implements Double2ByteMap, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Double2ByteMap map;
      protected transient ObjectSet<Double2ByteMap.Entry> entries;
      protected transient DoubleSet keys;
      protected transient ByteCollection values;

      protected UnmodifiableMap(Double2ByteMap m) {
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
      public void putAll(Map<? extends Double, ? extends Byte> m) {
         throw new UnsupportedOperationException();
      }

      @Override
      public ObjectSet<Double2ByteMap.Entry> double2ByteEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSets.unmodifiable(this.map.double2ByteEntrySet());
         }

         return this.entries;
      }

      @Deprecated
      @Override
      public ObjectSet<Entry<Double, Byte>> entrySet() {
         return this.double2ByteEntrySet();
      }

      @Override
      public DoubleSet keySet() {
         if (this.keys == null) {
            this.keys = DoubleSets.unmodifiable(this.map.keySet());
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
      public byte getOrDefault(double key, byte defaultValue) {
         return this.map.getOrDefault(key, defaultValue);
      }

      @Override
      public void forEach(BiConsumer<? super Double, ? super Byte> action) {
         this.map.forEach(action);
      }

      @Override
      public void replaceAll(BiFunction<? super Double, ? super Byte, ? extends Byte> function) {
         throw new UnsupportedOperationException();
      }

      @Override
      public byte putIfAbsent(double key, byte value) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean remove(double key, byte value) {
         throw new UnsupportedOperationException();
      }

      @Override
      public byte replace(double key, byte value) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean replace(double key, byte oldValue, byte newValue) {
         throw new UnsupportedOperationException();
      }

      @Override
      public byte computeIfAbsent(double key, DoubleToIntFunction mappingFunction) {
         throw new UnsupportedOperationException();
      }

      @Override
      public byte computeIfAbsentNullable(double key, DoubleFunction<? extends Byte> mappingFunction) {
         throw new UnsupportedOperationException();
      }

      @Override
      public byte computeIfAbsent(double key, Double2ByteFunction mappingFunction) {
         throw new UnsupportedOperationException();
      }

      @Override
      public byte computeIfPresent(double key, BiFunction<? super Double, ? super Byte, ? extends Byte> remappingFunction) {
         throw new UnsupportedOperationException();
      }

      @Override
      public byte compute(double key, BiFunction<? super Double, ? super Byte, ? extends Byte> remappingFunction) {
         throw new UnsupportedOperationException();
      }

      @Override
      public byte merge(double key, byte value, BiFunction<? super Byte, ? super Byte, ? extends Byte> remappingFunction) {
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
      public Byte replace(Double key, Byte value) {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      @Override
      public boolean replace(Double key, Byte oldValue, Byte newValue) {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      @Override
      public Byte putIfAbsent(Double key, Byte value) {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      @Override
      public Byte computeIfAbsent(Double key, Function<? super Double, ? extends Byte> mappingFunction) {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      @Override
      public Byte computeIfPresent(Double key, BiFunction<? super Double, ? super Byte, ? extends Byte> remappingFunction) {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      @Override
      public Byte compute(Double key, BiFunction<? super Double, ? super Byte, ? extends Byte> remappingFunction) {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      @Override
      public Byte merge(Double key, Byte value, BiFunction<? super Byte, ? super Byte, ? extends Byte> remappingFunction) {
         throw new UnsupportedOperationException();
      }
   }
}
