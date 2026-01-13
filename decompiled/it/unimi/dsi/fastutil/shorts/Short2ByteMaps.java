package it.unimi.dsi.fastutil.shorts;

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
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.IntUnaryOperator;

public final class Short2ByteMaps {
   public static final Short2ByteMaps.EmptyMap EMPTY_MAP = new Short2ByteMaps.EmptyMap();

   private Short2ByteMaps() {
   }

   public static ObjectIterator<Short2ByteMap.Entry> fastIterator(Short2ByteMap map) {
      ObjectSet<Short2ByteMap.Entry> entries = map.short2ByteEntrySet();
      return entries instanceof Short2ByteMap.FastEntrySet ? ((Short2ByteMap.FastEntrySet)entries).fastIterator() : entries.iterator();
   }

   public static void fastForEach(Short2ByteMap map, Consumer<? super Short2ByteMap.Entry> consumer) {
      ObjectSet<Short2ByteMap.Entry> entries = map.short2ByteEntrySet();
      if (entries instanceof Short2ByteMap.FastEntrySet) {
         ((Short2ByteMap.FastEntrySet)entries).fastForEach(consumer);
      } else {
         entries.forEach(consumer);
      }
   }

   public static ObjectIterable<Short2ByteMap.Entry> fastIterable(Short2ByteMap map) {
      final ObjectSet<Short2ByteMap.Entry> entries = map.short2ByteEntrySet();
      return (ObjectIterable<Short2ByteMap.Entry>)(entries instanceof Short2ByteMap.FastEntrySet ? new ObjectIterable<Short2ByteMap.Entry>() {
         @Override
         public ObjectIterator<Short2ByteMap.Entry> iterator() {
            return ((Short2ByteMap.FastEntrySet)entries).fastIterator();
         }

         @Override
         public ObjectSpliterator<Short2ByteMap.Entry> spliterator() {
            return entries.spliterator();
         }

         @Override
         public void forEach(Consumer<? super Short2ByteMap.Entry> consumer) {
            ((Short2ByteMap.FastEntrySet)entries).fastForEach(consumer);
         }
      } : entries);
   }

   public static Short2ByteMap singleton(short key, byte value) {
      return new Short2ByteMaps.Singleton(key, value);
   }

   public static Short2ByteMap singleton(Short key, Byte value) {
      return new Short2ByteMaps.Singleton(key, value);
   }

   public static Short2ByteMap synchronize(Short2ByteMap m) {
      return new Short2ByteMaps.SynchronizedMap(m);
   }

   public static Short2ByteMap synchronize(Short2ByteMap m, Object sync) {
      return new Short2ByteMaps.SynchronizedMap(m, sync);
   }

   public static Short2ByteMap unmodifiable(Short2ByteMap m) {
      return new Short2ByteMaps.UnmodifiableMap(m);
   }

   public static class EmptyMap extends Short2ByteFunctions.EmptyFunction implements Short2ByteMap, Serializable, Cloneable {
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
      public byte getOrDefault(short key, byte defaultValue) {
         return defaultValue;
      }

      @Deprecated
      @Override
      public boolean containsValue(Object ov) {
         return false;
      }

      @Override
      public void putAll(Map<? extends Short, ? extends Byte> m) {
         throw new UnsupportedOperationException();
      }

      @Override
      public ObjectSet<Short2ByteMap.Entry> short2ByteEntrySet() {
         return ObjectSets.EMPTY_SET;
      }

      @Override
      public ShortSet keySet() {
         return ShortSets.EMPTY_SET;
      }

      @Override
      public ByteCollection values() {
         return ByteSets.EMPTY_SET;
      }

      @Override
      public void forEach(BiConsumer<? super Short, ? super Byte> consumer) {
      }

      @Override
      public Object clone() {
         return Short2ByteMaps.EMPTY_MAP;
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

   public static class Singleton extends Short2ByteFunctions.Singleton implements Short2ByteMap, Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected transient ObjectSet<Short2ByteMap.Entry> entries;
      protected transient ShortSet keys;
      protected transient ByteCollection values;

      protected Singleton(short key, byte value) {
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
      public void putAll(Map<? extends Short, ? extends Byte> m) {
         throw new UnsupportedOperationException();
      }

      @Override
      public ObjectSet<Short2ByteMap.Entry> short2ByteEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSets.singleton(new AbstractShort2ByteMap.BasicEntry(this.key, this.value));
         }

         return this.entries;
      }

      @Deprecated
      @Override
      public ObjectSet<Entry<Short, Byte>> entrySet() {
         return this.short2ByteEntrySet();
      }

      @Override
      public ShortSet keySet() {
         if (this.keys == null) {
            this.keys = ShortSets.singleton(this.key);
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

   public static class SynchronizedMap extends Short2ByteFunctions.SynchronizedFunction implements Short2ByteMap, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Short2ByteMap map;
      protected transient ObjectSet<Short2ByteMap.Entry> entries;
      protected transient ShortSet keys;
      protected transient ByteCollection values;

      protected SynchronizedMap(Short2ByteMap m, Object sync) {
         super(m, sync);
         this.map = m;
      }

      protected SynchronizedMap(Short2ByteMap m) {
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
      public void putAll(Map<? extends Short, ? extends Byte> m) {
         synchronized (this.sync) {
            this.map.putAll(m);
         }
      }

      @Override
      public ObjectSet<Short2ByteMap.Entry> short2ByteEntrySet() {
         synchronized (this.sync) {
            if (this.entries == null) {
               this.entries = ObjectSets.synchronize(this.map.short2ByteEntrySet(), this.sync);
            }

            return this.entries;
         }
      }

      @Deprecated
      @Override
      public ObjectSet<Entry<Short, Byte>> entrySet() {
         return this.short2ByteEntrySet();
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
      public byte getOrDefault(short key, byte defaultValue) {
         synchronized (this.sync) {
            return this.map.getOrDefault(key, defaultValue);
         }
      }

      @Override
      public void forEach(BiConsumer<? super Short, ? super Byte> action) {
         synchronized (this.sync) {
            this.map.forEach(action);
         }
      }

      @Override
      public void replaceAll(BiFunction<? super Short, ? super Byte, ? extends Byte> function) {
         synchronized (this.sync) {
            this.map.replaceAll(function);
         }
      }

      @Override
      public byte putIfAbsent(short key, byte value) {
         synchronized (this.sync) {
            return this.map.putIfAbsent(key, value);
         }
      }

      @Override
      public boolean remove(short key, byte value) {
         synchronized (this.sync) {
            return this.map.remove(key, value);
         }
      }

      @Override
      public byte replace(short key, byte value) {
         synchronized (this.sync) {
            return this.map.replace(key, value);
         }
      }

      @Override
      public boolean replace(short key, byte oldValue, byte newValue) {
         synchronized (this.sync) {
            return this.map.replace(key, oldValue, newValue);
         }
      }

      @Override
      public byte computeIfAbsent(short key, IntUnaryOperator mappingFunction) {
         synchronized (this.sync) {
            return this.map.computeIfAbsent(key, mappingFunction);
         }
      }

      @Override
      public byte computeIfAbsentNullable(short key, IntFunction<? extends Byte> mappingFunction) {
         synchronized (this.sync) {
            return this.map.computeIfAbsentNullable(key, mappingFunction);
         }
      }

      @Override
      public byte computeIfAbsent(short key, Short2ByteFunction mappingFunction) {
         synchronized (this.sync) {
            return this.map.computeIfAbsent(key, mappingFunction);
         }
      }

      @Override
      public byte computeIfPresent(short key, BiFunction<? super Short, ? super Byte, ? extends Byte> remappingFunction) {
         synchronized (this.sync) {
            return this.map.computeIfPresent(key, remappingFunction);
         }
      }

      @Override
      public byte compute(short key, BiFunction<? super Short, ? super Byte, ? extends Byte> remappingFunction) {
         synchronized (this.sync) {
            return this.map.compute(key, remappingFunction);
         }
      }

      @Override
      public byte merge(short key, byte value, BiFunction<? super Byte, ? super Byte, ? extends Byte> remappingFunction) {
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
      public Byte replace(Short key, Byte value) {
         synchronized (this.sync) {
            return this.map.replace(key, value);
         }
      }

      @Deprecated
      @Override
      public boolean replace(Short key, Byte oldValue, Byte newValue) {
         synchronized (this.sync) {
            return this.map.replace(key, oldValue, newValue);
         }
      }

      @Deprecated
      @Override
      public Byte putIfAbsent(Short key, Byte value) {
         synchronized (this.sync) {
            return this.map.putIfAbsent(key, value);
         }
      }

      @Deprecated
      @Override
      public Byte computeIfAbsent(Short key, Function<? super Short, ? extends Byte> mappingFunction) {
         synchronized (this.sync) {
            return this.map.computeIfAbsent(key, mappingFunction);
         }
      }

      @Deprecated
      @Override
      public Byte computeIfPresent(Short key, BiFunction<? super Short, ? super Byte, ? extends Byte> remappingFunction) {
         synchronized (this.sync) {
            return this.map.computeIfPresent(key, remappingFunction);
         }
      }

      @Deprecated
      @Override
      public Byte compute(Short key, BiFunction<? super Short, ? super Byte, ? extends Byte> remappingFunction) {
         synchronized (this.sync) {
            return this.map.compute(key, remappingFunction);
         }
      }

      @Deprecated
      @Override
      public Byte merge(Short key, Byte value, BiFunction<? super Byte, ? super Byte, ? extends Byte> remappingFunction) {
         synchronized (this.sync) {
            return this.map.merge(key, value, remappingFunction);
         }
      }
   }

   public static class UnmodifiableMap extends Short2ByteFunctions.UnmodifiableFunction implements Short2ByteMap, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Short2ByteMap map;
      protected transient ObjectSet<Short2ByteMap.Entry> entries;
      protected transient ShortSet keys;
      protected transient ByteCollection values;

      protected UnmodifiableMap(Short2ByteMap m) {
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
      public void putAll(Map<? extends Short, ? extends Byte> m) {
         throw new UnsupportedOperationException();
      }

      @Override
      public ObjectSet<Short2ByteMap.Entry> short2ByteEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSets.unmodifiable(this.map.short2ByteEntrySet());
         }

         return this.entries;
      }

      @Deprecated
      @Override
      public ObjectSet<Entry<Short, Byte>> entrySet() {
         return this.short2ByteEntrySet();
      }

      @Override
      public ShortSet keySet() {
         if (this.keys == null) {
            this.keys = ShortSets.unmodifiable(this.map.keySet());
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
      public byte getOrDefault(short key, byte defaultValue) {
         return this.map.getOrDefault(key, defaultValue);
      }

      @Override
      public void forEach(BiConsumer<? super Short, ? super Byte> action) {
         this.map.forEach(action);
      }

      @Override
      public void replaceAll(BiFunction<? super Short, ? super Byte, ? extends Byte> function) {
         throw new UnsupportedOperationException();
      }

      @Override
      public byte putIfAbsent(short key, byte value) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean remove(short key, byte value) {
         throw new UnsupportedOperationException();
      }

      @Override
      public byte replace(short key, byte value) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean replace(short key, byte oldValue, byte newValue) {
         throw new UnsupportedOperationException();
      }

      @Override
      public byte computeIfAbsent(short key, IntUnaryOperator mappingFunction) {
         throw new UnsupportedOperationException();
      }

      @Override
      public byte computeIfAbsentNullable(short key, IntFunction<? extends Byte> mappingFunction) {
         throw new UnsupportedOperationException();
      }

      @Override
      public byte computeIfAbsent(short key, Short2ByteFunction mappingFunction) {
         throw new UnsupportedOperationException();
      }

      @Override
      public byte computeIfPresent(short key, BiFunction<? super Short, ? super Byte, ? extends Byte> remappingFunction) {
         throw new UnsupportedOperationException();
      }

      @Override
      public byte compute(short key, BiFunction<? super Short, ? super Byte, ? extends Byte> remappingFunction) {
         throw new UnsupportedOperationException();
      }

      @Override
      public byte merge(short key, byte value, BiFunction<? super Byte, ? super Byte, ? extends Byte> remappingFunction) {
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
      public Byte replace(Short key, Byte value) {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      @Override
      public boolean replace(Short key, Byte oldValue, Byte newValue) {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      @Override
      public Byte putIfAbsent(Short key, Byte value) {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      @Override
      public Byte computeIfAbsent(Short key, Function<? super Short, ? extends Byte> mappingFunction) {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      @Override
      public Byte computeIfPresent(Short key, BiFunction<? super Short, ? super Byte, ? extends Byte> remappingFunction) {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      @Override
      public Byte compute(Short key, BiFunction<? super Short, ? super Byte, ? extends Byte> remappingFunction) {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      @Override
      public Byte merge(Short key, Byte value, BiFunction<? super Byte, ? super Byte, ? extends Byte> remappingFunction) {
         throw new UnsupportedOperationException();
      }
   }
}
