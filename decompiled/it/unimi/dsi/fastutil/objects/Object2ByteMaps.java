package it.unimi.dsi.fastutil.objects;

import it.unimi.dsi.fastutil.bytes.ByteCollection;
import it.unimi.dsi.fastutil.bytes.ByteCollections;
import it.unimi.dsi.fastutil.bytes.ByteSets;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.ToIntFunction;

public final class Object2ByteMaps {
   public static final Object2ByteMaps.EmptyMap EMPTY_MAP = new Object2ByteMaps.EmptyMap();

   private Object2ByteMaps() {
   }

   public static <K> ObjectIterator<Object2ByteMap.Entry<K>> fastIterator(Object2ByteMap<K> map) {
      ObjectSet<Object2ByteMap.Entry<K>> entries = map.object2ByteEntrySet();
      return entries instanceof Object2ByteMap.FastEntrySet ? ((Object2ByteMap.FastEntrySet)entries).fastIterator() : entries.iterator();
   }

   public static <K> void fastForEach(Object2ByteMap<K> map, Consumer<? super Object2ByteMap.Entry<K>> consumer) {
      ObjectSet<Object2ByteMap.Entry<K>> entries = map.object2ByteEntrySet();
      if (entries instanceof Object2ByteMap.FastEntrySet) {
         ((Object2ByteMap.FastEntrySet)entries).fastForEach(consumer);
      } else {
         entries.forEach(consumer);
      }
   }

   public static <K> ObjectIterable<Object2ByteMap.Entry<K>> fastIterable(Object2ByteMap<K> map) {
      final ObjectSet<Object2ByteMap.Entry<K>> entries = map.object2ByteEntrySet();
      return (ObjectIterable<Object2ByteMap.Entry<K>>)(entries instanceof Object2ByteMap.FastEntrySet ? new ObjectIterable<Object2ByteMap.Entry<K>>() {
         @Override
         public ObjectIterator<Object2ByteMap.Entry<K>> iterator() {
            return ((Object2ByteMap.FastEntrySet)entries).fastIterator();
         }

         @Override
         public ObjectSpliterator<Object2ByteMap.Entry<K>> spliterator() {
            return entries.spliterator();
         }

         @Override
         public void forEach(Consumer<? super Object2ByteMap.Entry<K>> consumer) {
            ((Object2ByteMap.FastEntrySet)entries).fastForEach(consumer);
         }
      } : entries);
   }

   public static <K> Object2ByteMap<K> emptyMap() {
      return EMPTY_MAP;
   }

   public static <K> Object2ByteMap<K> singleton(K key, byte value) {
      return new Object2ByteMaps.Singleton<>(key, value);
   }

   public static <K> Object2ByteMap<K> singleton(K key, Byte value) {
      return new Object2ByteMaps.Singleton<>(key, value);
   }

   public static <K> Object2ByteMap<K> synchronize(Object2ByteMap<K> m) {
      return new Object2ByteMaps.SynchronizedMap<>(m);
   }

   public static <K> Object2ByteMap<K> synchronize(Object2ByteMap<K> m, Object sync) {
      return new Object2ByteMaps.SynchronizedMap<>(m, sync);
   }

   public static <K> Object2ByteMap<K> unmodifiable(Object2ByteMap<? extends K> m) {
      return new Object2ByteMaps.UnmodifiableMap<>(m);
   }

   public static class EmptyMap<K> extends Object2ByteFunctions.EmptyFunction<K> implements Object2ByteMap<K>, Serializable, Cloneable {
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
      public byte getOrDefault(Object key, byte defaultValue) {
         return defaultValue;
      }

      @Deprecated
      @Override
      public boolean containsValue(Object ov) {
         return false;
      }

      @Override
      public void putAll(Map<? extends K, ? extends Byte> m) {
         throw new UnsupportedOperationException();
      }

      @Override
      public ObjectSet<Object2ByteMap.Entry<K>> object2ByteEntrySet() {
         return ObjectSets.EMPTY_SET;
      }

      @Override
      public ObjectSet<K> keySet() {
         return ObjectSets.EMPTY_SET;
      }

      @Override
      public ByteCollection values() {
         return ByteSets.EMPTY_SET;
      }

      @Override
      public void forEach(BiConsumer<? super K, ? super Byte> consumer) {
      }

      @Override
      public Object clone() {
         return Object2ByteMaps.EMPTY_MAP;
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

   public static class Singleton<K> extends Object2ByteFunctions.Singleton<K> implements Object2ByteMap<K>, Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected transient ObjectSet<Object2ByteMap.Entry<K>> entries;
      protected transient ObjectSet<K> keys;
      protected transient ByteCollection values;

      protected Singleton(K key, byte value) {
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
      public void putAll(Map<? extends K, ? extends Byte> m) {
         throw new UnsupportedOperationException();
      }

      @Override
      public ObjectSet<Object2ByteMap.Entry<K>> object2ByteEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSets.singleton(new AbstractObject2ByteMap.BasicEntry<>(this.key, this.value));
         }

         return this.entries;
      }

      @Deprecated
      @Override
      public ObjectSet<Entry<K, Byte>> entrySet() {
         return this.object2ByteEntrySet();
      }

      @Override
      public ObjectSet<K> keySet() {
         if (this.keys == null) {
            this.keys = ObjectSets.singleton(this.key);
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
         return (this.key == null ? 0 : this.key.hashCode()) ^ this.value;
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

   public static class SynchronizedMap<K> extends Object2ByteFunctions.SynchronizedFunction<K> implements Object2ByteMap<K>, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Object2ByteMap<K> map;
      protected transient ObjectSet<Object2ByteMap.Entry<K>> entries;
      protected transient ObjectSet<K> keys;
      protected transient ByteCollection values;

      protected SynchronizedMap(Object2ByteMap<K> m, Object sync) {
         super(m, sync);
         this.map = m;
      }

      protected SynchronizedMap(Object2ByteMap<K> m) {
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
      public void putAll(Map<? extends K, ? extends Byte> m) {
         synchronized (this.sync) {
            this.map.putAll(m);
         }
      }

      @Override
      public ObjectSet<Object2ByteMap.Entry<K>> object2ByteEntrySet() {
         synchronized (this.sync) {
            if (this.entries == null) {
               this.entries = ObjectSets.synchronize(this.map.object2ByteEntrySet(), this.sync);
            }

            return this.entries;
         }
      }

      @Deprecated
      @Override
      public ObjectSet<Entry<K, Byte>> entrySet() {
         return this.object2ByteEntrySet();
      }

      @Override
      public ObjectSet<K> keySet() {
         synchronized (this.sync) {
            if (this.keys == null) {
               this.keys = ObjectSets.synchronize(this.map.keySet(), this.sync);
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
      public byte getOrDefault(Object key, byte defaultValue) {
         synchronized (this.sync) {
            return this.map.getOrDefault(key, defaultValue);
         }
      }

      @Override
      public void forEach(BiConsumer<? super K, ? super Byte> action) {
         synchronized (this.sync) {
            this.map.forEach(action);
         }
      }

      @Override
      public void replaceAll(BiFunction<? super K, ? super Byte, ? extends Byte> function) {
         synchronized (this.sync) {
            this.map.replaceAll(function);
         }
      }

      @Override
      public byte putIfAbsent(K key, byte value) {
         synchronized (this.sync) {
            return this.map.putIfAbsent(key, value);
         }
      }

      @Override
      public boolean remove(Object key, byte value) {
         synchronized (this.sync) {
            return this.map.remove(key, value);
         }
      }

      @Override
      public byte replace(K key, byte value) {
         synchronized (this.sync) {
            return this.map.replace(key, value);
         }
      }

      @Override
      public boolean replace(K key, byte oldValue, byte newValue) {
         synchronized (this.sync) {
            return this.map.replace(key, oldValue, newValue);
         }
      }

      @Override
      public byte computeIfAbsent(K key, ToIntFunction<? super K> mappingFunction) {
         synchronized (this.sync) {
            return this.map.computeIfAbsent(key, mappingFunction);
         }
      }

      @Override
      public byte computeIfAbsent(K key, Object2ByteFunction<? super K> mappingFunction) {
         synchronized (this.sync) {
            return this.map.computeIfAbsent(key, mappingFunction);
         }
      }

      @Override
      public byte computeByteIfPresent(K key, BiFunction<? super K, ? super Byte, ? extends Byte> remappingFunction) {
         synchronized (this.sync) {
            return this.map.computeByteIfPresent(key, remappingFunction);
         }
      }

      @Override
      public byte computeByte(K key, BiFunction<? super K, ? super Byte, ? extends Byte> remappingFunction) {
         synchronized (this.sync) {
            return this.map.computeByte(key, remappingFunction);
         }
      }

      @Override
      public byte merge(K key, byte value, BiFunction<? super Byte, ? super Byte, ? extends Byte> remappingFunction) {
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
      public Byte replace(K key, Byte value) {
         synchronized (this.sync) {
            return this.map.replace(key, value);
         }
      }

      @Deprecated
      @Override
      public boolean replace(K key, Byte oldValue, Byte newValue) {
         synchronized (this.sync) {
            return this.map.replace(key, oldValue, newValue);
         }
      }

      @Deprecated
      @Override
      public Byte putIfAbsent(K key, Byte value) {
         synchronized (this.sync) {
            return this.map.putIfAbsent(key, value);
         }
      }

      public Byte computeIfAbsent(K key, Function<? super K, ? extends Byte> mappingFunction) {
         synchronized (this.sync) {
            return this.map.computeIfAbsent(key, mappingFunction);
         }
      }

      public Byte computeIfPresent(K key, BiFunction<? super K, ? super Byte, ? extends Byte> remappingFunction) {
         synchronized (this.sync) {
            return this.map.computeIfPresent(key, remappingFunction);
         }
      }

      public Byte compute(K key, BiFunction<? super K, ? super Byte, ? extends Byte> remappingFunction) {
         synchronized (this.sync) {
            return this.map.compute(key, remappingFunction);
         }
      }

      @Deprecated
      @Override
      public Byte merge(K key, Byte value, BiFunction<? super Byte, ? super Byte, ? extends Byte> remappingFunction) {
         synchronized (this.sync) {
            return this.map.merge(key, value, remappingFunction);
         }
      }
   }

   public static class UnmodifiableMap<K> extends Object2ByteFunctions.UnmodifiableFunction<K> implements Object2ByteMap<K>, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Object2ByteMap<? extends K> map;
      protected transient ObjectSet<Object2ByteMap.Entry<K>> entries;
      protected transient ObjectSet<K> keys;
      protected transient ByteCollection values;

      protected UnmodifiableMap(Object2ByteMap<? extends K> m) {
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
      public void putAll(Map<? extends K, ? extends Byte> m) {
         throw new UnsupportedOperationException();
      }

      @Override
      public ObjectSet<Object2ByteMap.Entry<K>> object2ByteEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSets.unmodifiable(this.map.object2ByteEntrySet());
         }

         return this.entries;
      }

      @Deprecated
      @Override
      public ObjectSet<Entry<K, Byte>> entrySet() {
         return this.object2ByteEntrySet();
      }

      @Override
      public ObjectSet<K> keySet() {
         if (this.keys == null) {
            this.keys = ObjectSets.unmodifiable(this.map.keySet());
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
      public byte getOrDefault(Object key, byte defaultValue) {
         return this.map.getOrDefault(key, defaultValue);
      }

      @Override
      public void forEach(BiConsumer<? super K, ? super Byte> action) {
         this.map.forEach(action);
      }

      @Override
      public void replaceAll(BiFunction<? super K, ? super Byte, ? extends Byte> function) {
         throw new UnsupportedOperationException();
      }

      @Override
      public byte putIfAbsent(K key, byte value) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean remove(Object key, byte value) {
         throw new UnsupportedOperationException();
      }

      @Override
      public byte replace(K key, byte value) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean replace(K key, byte oldValue, byte newValue) {
         throw new UnsupportedOperationException();
      }

      @Override
      public byte computeIfAbsent(K key, ToIntFunction<? super K> mappingFunction) {
         throw new UnsupportedOperationException();
      }

      @Override
      public byte computeIfAbsent(K key, Object2ByteFunction<? super K> mappingFunction) {
         throw new UnsupportedOperationException();
      }

      @Override
      public byte computeByteIfPresent(K key, BiFunction<? super K, ? super Byte, ? extends Byte> remappingFunction) {
         throw new UnsupportedOperationException();
      }

      @Override
      public byte computeByte(K key, BiFunction<? super K, ? super Byte, ? extends Byte> remappingFunction) {
         throw new UnsupportedOperationException();
      }

      @Override
      public byte merge(K key, byte value, BiFunction<? super Byte, ? super Byte, ? extends Byte> remappingFunction) {
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
      public Byte replace(K key, Byte value) {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      @Override
      public boolean replace(K key, Byte oldValue, Byte newValue) {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      @Override
      public Byte putIfAbsent(K key, Byte value) {
         throw new UnsupportedOperationException();
      }

      public Byte computeIfAbsent(K key, Function<? super K, ? extends Byte> mappingFunction) {
         throw new UnsupportedOperationException();
      }

      public Byte computeIfPresent(K key, BiFunction<? super K, ? super Byte, ? extends Byte> remappingFunction) {
         throw new UnsupportedOperationException();
      }

      public Byte compute(K key, BiFunction<? super K, ? super Byte, ? extends Byte> remappingFunction) {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      @Override
      public Byte merge(K key, Byte value, BiFunction<? super Byte, ? super Byte, ? extends Byte> remappingFunction) {
         throw new UnsupportedOperationException();
      }
   }
}
