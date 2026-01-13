package it.unimi.dsi.fastutil.bytes;

import it.unimi.dsi.fastutil.objects.ObjectCollection;
import it.unimi.dsi.fastutil.objects.ObjectCollections;
import it.unimi.dsi.fastutil.objects.ObjectIterable;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import it.unimi.dsi.fastutil.objects.ObjectSets;
import it.unimi.dsi.fastutil.objects.ObjectSpliterator;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Map;
import java.util.Objects;
import java.util.Map.Entry;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.IntFunction;

public final class Byte2ObjectMaps {
   public static final Byte2ObjectMaps.EmptyMap EMPTY_MAP = new Byte2ObjectMaps.EmptyMap();

   private Byte2ObjectMaps() {
   }

   public static <V> ObjectIterator<Byte2ObjectMap.Entry<V>> fastIterator(Byte2ObjectMap<V> map) {
      ObjectSet<Byte2ObjectMap.Entry<V>> entries = map.byte2ObjectEntrySet();
      return entries instanceof Byte2ObjectMap.FastEntrySet ? ((Byte2ObjectMap.FastEntrySet)entries).fastIterator() : entries.iterator();
   }

   public static <V> void fastForEach(Byte2ObjectMap<V> map, Consumer<? super Byte2ObjectMap.Entry<V>> consumer) {
      ObjectSet<Byte2ObjectMap.Entry<V>> entries = map.byte2ObjectEntrySet();
      if (entries instanceof Byte2ObjectMap.FastEntrySet) {
         ((Byte2ObjectMap.FastEntrySet)entries).fastForEach(consumer);
      } else {
         entries.forEach(consumer);
      }
   }

   public static <V> ObjectIterable<Byte2ObjectMap.Entry<V>> fastIterable(Byte2ObjectMap<V> map) {
      final ObjectSet<Byte2ObjectMap.Entry<V>> entries = map.byte2ObjectEntrySet();
      return (ObjectIterable<Byte2ObjectMap.Entry<V>>)(entries instanceof Byte2ObjectMap.FastEntrySet ? new ObjectIterable<Byte2ObjectMap.Entry<V>>() {
         @Override
         public ObjectIterator<Byte2ObjectMap.Entry<V>> iterator() {
            return ((Byte2ObjectMap.FastEntrySet)entries).fastIterator();
         }

         @Override
         public ObjectSpliterator<Byte2ObjectMap.Entry<V>> spliterator() {
            return entries.spliterator();
         }

         @Override
         public void forEach(Consumer<? super Byte2ObjectMap.Entry<V>> consumer) {
            ((Byte2ObjectMap.FastEntrySet)entries).fastForEach(consumer);
         }
      } : entries);
   }

   public static <V> Byte2ObjectMap<V> emptyMap() {
      return EMPTY_MAP;
   }

   public static <V> Byte2ObjectMap<V> singleton(byte key, V value) {
      return new Byte2ObjectMaps.Singleton<>(key, value);
   }

   public static <V> Byte2ObjectMap<V> singleton(Byte key, V value) {
      return new Byte2ObjectMaps.Singleton<>(key, value);
   }

   public static <V> Byte2ObjectMap<V> synchronize(Byte2ObjectMap<V> m) {
      return new Byte2ObjectMaps.SynchronizedMap<>(m);
   }

   public static <V> Byte2ObjectMap<V> synchronize(Byte2ObjectMap<V> m, Object sync) {
      return new Byte2ObjectMaps.SynchronizedMap<>(m, sync);
   }

   public static <V> Byte2ObjectMap<V> unmodifiable(Byte2ObjectMap<? extends V> m) {
      return new Byte2ObjectMaps.UnmodifiableMap<>(m);
   }

   public static class EmptyMap<V> extends Byte2ObjectFunctions.EmptyFunction<V> implements Byte2ObjectMap<V>, Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;

      protected EmptyMap() {
      }

      @Override
      public boolean containsValue(Object v) {
         return false;
      }

      @Deprecated
      @Override
      public V getOrDefault(Object key, V defaultValue) {
         return defaultValue;
      }

      @Override
      public V getOrDefault(byte key, V defaultValue) {
         return defaultValue;
      }

      @Override
      public void putAll(Map<? extends Byte, ? extends V> m) {
         throw new UnsupportedOperationException();
      }

      @Override
      public ObjectSet<Byte2ObjectMap.Entry<V>> byte2ObjectEntrySet() {
         return ObjectSets.EMPTY_SET;
      }

      @Override
      public ByteSet keySet() {
         return ByteSets.EMPTY_SET;
      }

      @Override
      public ObjectCollection<V> values() {
         return ObjectSets.EMPTY_SET;
      }

      @Override
      public void forEach(BiConsumer<? super Byte, ? super V> consumer) {
      }

      @Override
      public Object clone() {
         return Byte2ObjectMaps.EMPTY_MAP;
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

   public static class Singleton<V> extends Byte2ObjectFunctions.Singleton<V> implements Byte2ObjectMap<V>, Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected transient ObjectSet<Byte2ObjectMap.Entry<V>> entries;
      protected transient ByteSet keys;
      protected transient ObjectCollection<V> values;

      protected Singleton(byte key, V value) {
         super(key, value);
      }

      @Override
      public boolean containsValue(Object v) {
         return Objects.equals(this.value, v);
      }

      @Override
      public void putAll(Map<? extends Byte, ? extends V> m) {
         throw new UnsupportedOperationException();
      }

      @Override
      public ObjectSet<Byte2ObjectMap.Entry<V>> byte2ObjectEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSets.singleton(new AbstractByte2ObjectMap.BasicEntry<>(this.key, this.value));
         }

         return this.entries;
      }

      @Deprecated
      @Override
      public ObjectSet<Entry<Byte, V>> entrySet() {
         return this.byte2ObjectEntrySet();
      }

      @Override
      public ByteSet keySet() {
         if (this.keys == null) {
            this.keys = ByteSets.singleton(this.key);
         }

         return this.keys;
      }

      @Override
      public ObjectCollection<V> values() {
         if (this.values == null) {
            this.values = ObjectSets.singleton(this.value);
         }

         return this.values;
      }

      @Override
      public boolean isEmpty() {
         return false;
      }

      @Override
      public int hashCode() {
         return this.key ^ (this.value == null ? 0 : this.value.hashCode());
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

   public static class SynchronizedMap<V> extends Byte2ObjectFunctions.SynchronizedFunction<V> implements Byte2ObjectMap<V>, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Byte2ObjectMap<V> map;
      protected transient ObjectSet<Byte2ObjectMap.Entry<V>> entries;
      protected transient ByteSet keys;
      protected transient ObjectCollection<V> values;

      protected SynchronizedMap(Byte2ObjectMap<V> m, Object sync) {
         super(m, sync);
         this.map = m;
      }

      protected SynchronizedMap(Byte2ObjectMap<V> m) {
         super(m);
         this.map = m;
      }

      @Override
      public boolean containsValue(Object v) {
         synchronized (this.sync) {
            return this.map.containsValue(v);
         }
      }

      @Override
      public void putAll(Map<? extends Byte, ? extends V> m) {
         synchronized (this.sync) {
            this.map.putAll(m);
         }
      }

      @Override
      public ObjectSet<Byte2ObjectMap.Entry<V>> byte2ObjectEntrySet() {
         synchronized (this.sync) {
            if (this.entries == null) {
               this.entries = ObjectSets.synchronize(this.map.byte2ObjectEntrySet(), this.sync);
            }

            return this.entries;
         }
      }

      @Deprecated
      @Override
      public ObjectSet<Entry<Byte, V>> entrySet() {
         return this.byte2ObjectEntrySet();
      }

      @Override
      public ByteSet keySet() {
         synchronized (this.sync) {
            if (this.keys == null) {
               this.keys = ByteSets.synchronize(this.map.keySet(), this.sync);
            }

            return this.keys;
         }
      }

      @Override
      public ObjectCollection<V> values() {
         synchronized (this.sync) {
            if (this.values == null) {
               this.values = ObjectCollections.synchronize(this.map.values(), this.sync);
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
      public V getOrDefault(byte key, V defaultValue) {
         synchronized (this.sync) {
            return this.map.getOrDefault(key, defaultValue);
         }
      }

      @Override
      public void forEach(BiConsumer<? super Byte, ? super V> action) {
         synchronized (this.sync) {
            this.map.forEach(action);
         }
      }

      @Override
      public void replaceAll(BiFunction<? super Byte, ? super V, ? extends V> function) {
         synchronized (this.sync) {
            this.map.replaceAll(function);
         }
      }

      @Override
      public V putIfAbsent(byte key, V value) {
         synchronized (this.sync) {
            return this.map.putIfAbsent(key, value);
         }
      }

      @Override
      public boolean remove(byte key, Object value) {
         synchronized (this.sync) {
            return this.map.remove(key, value);
         }
      }

      @Override
      public V replace(byte key, V value) {
         synchronized (this.sync) {
            return this.map.replace(key, value);
         }
      }

      @Override
      public boolean replace(byte key, V oldValue, V newValue) {
         synchronized (this.sync) {
            return this.map.replace(key, oldValue, newValue);
         }
      }

      @Override
      public V computeIfAbsent(byte key, IntFunction<? extends V> mappingFunction) {
         synchronized (this.sync) {
            return this.map.computeIfAbsent(key, mappingFunction);
         }
      }

      @Override
      public V computeIfAbsent(byte key, Byte2ObjectFunction<? extends V> mappingFunction) {
         synchronized (this.sync) {
            return this.map.computeIfAbsent(key, mappingFunction);
         }
      }

      @Override
      public V computeIfPresent(byte key, BiFunction<? super Byte, ? super V, ? extends V> remappingFunction) {
         synchronized (this.sync) {
            return this.map.computeIfPresent(key, remappingFunction);
         }
      }

      @Override
      public V compute(byte key, BiFunction<? super Byte, ? super V, ? extends V> remappingFunction) {
         synchronized (this.sync) {
            return this.map.compute(key, remappingFunction);
         }
      }

      @Override
      public V merge(byte key, V value, BiFunction<? super V, ? super V, ? extends V> remappingFunction) {
         synchronized (this.sync) {
            return this.map.merge(key, value, remappingFunction);
         }
      }

      @Deprecated
      @Override
      public V getOrDefault(Object key, V defaultValue) {
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
      public V replace(Byte key, V value) {
         synchronized (this.sync) {
            return this.map.replace(key, value);
         }
      }

      @Deprecated
      public boolean replace(Byte key, V oldValue, V newValue) {
         synchronized (this.sync) {
            return this.map.replace(key, oldValue, newValue);
         }
      }

      @Deprecated
      public V putIfAbsent(Byte key, V value) {
         synchronized (this.sync) {
            return this.map.putIfAbsent(key, value);
         }
      }

      @Deprecated
      public V computeIfAbsent(Byte key, Function<? super Byte, ? extends V> mappingFunction) {
         synchronized (this.sync) {
            return this.map.computeIfAbsent(key, mappingFunction);
         }
      }

      @Deprecated
      public V computeIfPresent(Byte key, BiFunction<? super Byte, ? super V, ? extends V> remappingFunction) {
         synchronized (this.sync) {
            return this.map.computeIfPresent(key, remappingFunction);
         }
      }

      @Deprecated
      public V compute(Byte key, BiFunction<? super Byte, ? super V, ? extends V> remappingFunction) {
         synchronized (this.sync) {
            return this.map.compute(key, remappingFunction);
         }
      }

      @Deprecated
      public V merge(Byte key, V value, BiFunction<? super V, ? super V, ? extends V> remappingFunction) {
         synchronized (this.sync) {
            return this.map.merge(key, value, remappingFunction);
         }
      }
   }

   public static class UnmodifiableMap<V> extends Byte2ObjectFunctions.UnmodifiableFunction<V> implements Byte2ObjectMap<V>, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Byte2ObjectMap<? extends V> map;
      protected transient ObjectSet<Byte2ObjectMap.Entry<V>> entries;
      protected transient ByteSet keys;
      protected transient ObjectCollection<V> values;

      protected UnmodifiableMap(Byte2ObjectMap<? extends V> m) {
         super(m);
         this.map = m;
      }

      @Override
      public boolean containsValue(Object v) {
         return this.map.containsValue(v);
      }

      @Override
      public void putAll(Map<? extends Byte, ? extends V> m) {
         throw new UnsupportedOperationException();
      }

      @Override
      public ObjectSet<Byte2ObjectMap.Entry<V>> byte2ObjectEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSets.unmodifiable(this.map.byte2ObjectEntrySet());
         }

         return this.entries;
      }

      @Deprecated
      @Override
      public ObjectSet<Entry<Byte, V>> entrySet() {
         return this.byte2ObjectEntrySet();
      }

      @Override
      public ByteSet keySet() {
         if (this.keys == null) {
            this.keys = ByteSets.unmodifiable(this.map.keySet());
         }

         return this.keys;
      }

      @Override
      public ObjectCollection<V> values() {
         if (this.values == null) {
            this.values = ObjectCollections.unmodifiable(this.map.values());
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
      public V getOrDefault(byte key, V defaultValue) {
         return (V)this.map.getOrDefault(key, defaultValue);
      }

      @Override
      public void forEach(BiConsumer<? super Byte, ? super V> action) {
         this.map.forEach(action);
      }

      @Override
      public void replaceAll(BiFunction<? super Byte, ? super V, ? extends V> function) {
         throw new UnsupportedOperationException();
      }

      @Override
      public V putIfAbsent(byte key, V value) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean remove(byte key, Object value) {
         throw new UnsupportedOperationException();
      }

      @Override
      public V replace(byte key, V value) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean replace(byte key, V oldValue, V newValue) {
         throw new UnsupportedOperationException();
      }

      @Override
      public V computeIfAbsent(byte key, IntFunction<? extends V> mappingFunction) {
         throw new UnsupportedOperationException();
      }

      @Override
      public V computeIfAbsent(byte key, Byte2ObjectFunction<? extends V> mappingFunction) {
         throw new UnsupportedOperationException();
      }

      @Override
      public V computeIfPresent(byte key, BiFunction<? super Byte, ? super V, ? extends V> remappingFunction) {
         throw new UnsupportedOperationException();
      }

      @Override
      public V compute(byte key, BiFunction<? super Byte, ? super V, ? extends V> remappingFunction) {
         throw new UnsupportedOperationException();
      }

      @Override
      public V merge(byte key, V value, BiFunction<? super V, ? super V, ? extends V> remappingFunction) {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      @Override
      public V getOrDefault(Object key, V defaultValue) {
         return (V)this.map.getOrDefault(key, defaultValue);
      }

      @Deprecated
      @Override
      public boolean remove(Object key, Object value) {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      public V replace(Byte key, V value) {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      public boolean replace(Byte key, V oldValue, V newValue) {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      public V putIfAbsent(Byte key, V value) {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      public V computeIfAbsent(Byte key, Function<? super Byte, ? extends V> mappingFunction) {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      public V computeIfPresent(Byte key, BiFunction<? super Byte, ? super V, ? extends V> remappingFunction) {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      public V compute(Byte key, BiFunction<? super Byte, ? super V, ? extends V> remappingFunction) {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      public V merge(Byte key, V value, BiFunction<? super V, ? super V, ? extends V> remappingFunction) {
         throw new UnsupportedOperationException();
      }
   }
}
