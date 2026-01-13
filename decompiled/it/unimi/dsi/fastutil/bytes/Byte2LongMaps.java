package it.unimi.dsi.fastutil.bytes;

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

public final class Byte2LongMaps {
   public static final Byte2LongMaps.EmptyMap EMPTY_MAP = new Byte2LongMaps.EmptyMap();

   private Byte2LongMaps() {
   }

   public static ObjectIterator<Byte2LongMap.Entry> fastIterator(Byte2LongMap map) {
      ObjectSet<Byte2LongMap.Entry> entries = map.byte2LongEntrySet();
      return entries instanceof Byte2LongMap.FastEntrySet ? ((Byte2LongMap.FastEntrySet)entries).fastIterator() : entries.iterator();
   }

   public static void fastForEach(Byte2LongMap map, Consumer<? super Byte2LongMap.Entry> consumer) {
      ObjectSet<Byte2LongMap.Entry> entries = map.byte2LongEntrySet();
      if (entries instanceof Byte2LongMap.FastEntrySet) {
         ((Byte2LongMap.FastEntrySet)entries).fastForEach(consumer);
      } else {
         entries.forEach(consumer);
      }
   }

   public static ObjectIterable<Byte2LongMap.Entry> fastIterable(Byte2LongMap map) {
      final ObjectSet<Byte2LongMap.Entry> entries = map.byte2LongEntrySet();
      return (ObjectIterable<Byte2LongMap.Entry>)(entries instanceof Byte2LongMap.FastEntrySet ? new ObjectIterable<Byte2LongMap.Entry>() {
         @Override
         public ObjectIterator<Byte2LongMap.Entry> iterator() {
            return ((Byte2LongMap.FastEntrySet)entries).fastIterator();
         }

         @Override
         public ObjectSpliterator<Byte2LongMap.Entry> spliterator() {
            return entries.spliterator();
         }

         @Override
         public void forEach(Consumer<? super Byte2LongMap.Entry> consumer) {
            ((Byte2LongMap.FastEntrySet)entries).fastForEach(consumer);
         }
      } : entries);
   }

   public static Byte2LongMap singleton(byte key, long value) {
      return new Byte2LongMaps.Singleton(key, value);
   }

   public static Byte2LongMap singleton(Byte key, Long value) {
      return new Byte2LongMaps.Singleton(key, value);
   }

   public static Byte2LongMap synchronize(Byte2LongMap m) {
      return new Byte2LongMaps.SynchronizedMap(m);
   }

   public static Byte2LongMap synchronize(Byte2LongMap m, Object sync) {
      return new Byte2LongMaps.SynchronizedMap(m, sync);
   }

   public static Byte2LongMap unmodifiable(Byte2LongMap m) {
      return new Byte2LongMaps.UnmodifiableMap(m);
   }

   public static class EmptyMap extends Byte2LongFunctions.EmptyFunction implements Byte2LongMap, Serializable, Cloneable {
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
      public long getOrDefault(byte key, long defaultValue) {
         return defaultValue;
      }

      @Deprecated
      @Override
      public boolean containsValue(Object ov) {
         return false;
      }

      @Override
      public void putAll(Map<? extends Byte, ? extends Long> m) {
         throw new UnsupportedOperationException();
      }

      @Override
      public ObjectSet<Byte2LongMap.Entry> byte2LongEntrySet() {
         return ObjectSets.EMPTY_SET;
      }

      @Override
      public ByteSet keySet() {
         return ByteSets.EMPTY_SET;
      }

      @Override
      public LongCollection values() {
         return LongSets.EMPTY_SET;
      }

      @Override
      public void forEach(BiConsumer<? super Byte, ? super Long> consumer) {
      }

      @Override
      public Object clone() {
         return Byte2LongMaps.EMPTY_MAP;
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

   public static class Singleton extends Byte2LongFunctions.Singleton implements Byte2LongMap, Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected transient ObjectSet<Byte2LongMap.Entry> entries;
      protected transient ByteSet keys;
      protected transient LongCollection values;

      protected Singleton(byte key, long value) {
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
      public void putAll(Map<? extends Byte, ? extends Long> m) {
         throw new UnsupportedOperationException();
      }

      @Override
      public ObjectSet<Byte2LongMap.Entry> byte2LongEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSets.singleton(new AbstractByte2LongMap.BasicEntry(this.key, this.value));
         }

         return this.entries;
      }

      @Deprecated
      @Override
      public ObjectSet<Entry<Byte, Long>> entrySet() {
         return this.byte2LongEntrySet();
      }

      @Override
      public ByteSet keySet() {
         if (this.keys == null) {
            this.keys = ByteSets.singleton(this.key);
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

   public static class SynchronizedMap extends Byte2LongFunctions.SynchronizedFunction implements Byte2LongMap, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Byte2LongMap map;
      protected transient ObjectSet<Byte2LongMap.Entry> entries;
      protected transient ByteSet keys;
      protected transient LongCollection values;

      protected SynchronizedMap(Byte2LongMap m, Object sync) {
         super(m, sync);
         this.map = m;
      }

      protected SynchronizedMap(Byte2LongMap m) {
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
      public void putAll(Map<? extends Byte, ? extends Long> m) {
         synchronized (this.sync) {
            this.map.putAll(m);
         }
      }

      @Override
      public ObjectSet<Byte2LongMap.Entry> byte2LongEntrySet() {
         synchronized (this.sync) {
            if (this.entries == null) {
               this.entries = ObjectSets.synchronize(this.map.byte2LongEntrySet(), this.sync);
            }

            return this.entries;
         }
      }

      @Deprecated
      @Override
      public ObjectSet<Entry<Byte, Long>> entrySet() {
         return this.byte2LongEntrySet();
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
      public long getOrDefault(byte key, long defaultValue) {
         synchronized (this.sync) {
            return this.map.getOrDefault(key, defaultValue);
         }
      }

      @Override
      public void forEach(BiConsumer<? super Byte, ? super Long> action) {
         synchronized (this.sync) {
            this.map.forEach(action);
         }
      }

      @Override
      public void replaceAll(BiFunction<? super Byte, ? super Long, ? extends Long> function) {
         synchronized (this.sync) {
            this.map.replaceAll(function);
         }
      }

      @Override
      public long putIfAbsent(byte key, long value) {
         synchronized (this.sync) {
            return this.map.putIfAbsent(key, value);
         }
      }

      @Override
      public boolean remove(byte key, long value) {
         synchronized (this.sync) {
            return this.map.remove(key, value);
         }
      }

      @Override
      public long replace(byte key, long value) {
         synchronized (this.sync) {
            return this.map.replace(key, value);
         }
      }

      @Override
      public boolean replace(byte key, long oldValue, long newValue) {
         synchronized (this.sync) {
            return this.map.replace(key, oldValue, newValue);
         }
      }

      @Override
      public long computeIfAbsent(byte key, IntToLongFunction mappingFunction) {
         synchronized (this.sync) {
            return this.map.computeIfAbsent(key, mappingFunction);
         }
      }

      @Override
      public long computeIfAbsentNullable(byte key, IntFunction<? extends Long> mappingFunction) {
         synchronized (this.sync) {
            return this.map.computeIfAbsentNullable(key, mappingFunction);
         }
      }

      @Override
      public long computeIfAbsent(byte key, Byte2LongFunction mappingFunction) {
         synchronized (this.sync) {
            return this.map.computeIfAbsent(key, mappingFunction);
         }
      }

      @Override
      public long computeIfPresent(byte key, BiFunction<? super Byte, ? super Long, ? extends Long> remappingFunction) {
         synchronized (this.sync) {
            return this.map.computeIfPresent(key, remappingFunction);
         }
      }

      @Override
      public long compute(byte key, BiFunction<? super Byte, ? super Long, ? extends Long> remappingFunction) {
         synchronized (this.sync) {
            return this.map.compute(key, remappingFunction);
         }
      }

      @Override
      public long merge(byte key, long value, BiFunction<? super Long, ? super Long, ? extends Long> remappingFunction) {
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
      public Long replace(Byte key, Long value) {
         synchronized (this.sync) {
            return this.map.replace(key, value);
         }
      }

      @Deprecated
      @Override
      public boolean replace(Byte key, Long oldValue, Long newValue) {
         synchronized (this.sync) {
            return this.map.replace(key, oldValue, newValue);
         }
      }

      @Deprecated
      @Override
      public Long putIfAbsent(Byte key, Long value) {
         synchronized (this.sync) {
            return this.map.putIfAbsent(key, value);
         }
      }

      @Deprecated
      @Override
      public Long computeIfAbsent(Byte key, Function<? super Byte, ? extends Long> mappingFunction) {
         synchronized (this.sync) {
            return this.map.computeIfAbsent(key, mappingFunction);
         }
      }

      @Deprecated
      @Override
      public Long computeIfPresent(Byte key, BiFunction<? super Byte, ? super Long, ? extends Long> remappingFunction) {
         synchronized (this.sync) {
            return this.map.computeIfPresent(key, remappingFunction);
         }
      }

      @Deprecated
      @Override
      public Long compute(Byte key, BiFunction<? super Byte, ? super Long, ? extends Long> remappingFunction) {
         synchronized (this.sync) {
            return this.map.compute(key, remappingFunction);
         }
      }

      @Deprecated
      @Override
      public Long merge(Byte key, Long value, BiFunction<? super Long, ? super Long, ? extends Long> remappingFunction) {
         synchronized (this.sync) {
            return this.map.merge(key, value, remappingFunction);
         }
      }
   }

   public static class UnmodifiableMap extends Byte2LongFunctions.UnmodifiableFunction implements Byte2LongMap, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Byte2LongMap map;
      protected transient ObjectSet<Byte2LongMap.Entry> entries;
      protected transient ByteSet keys;
      protected transient LongCollection values;

      protected UnmodifiableMap(Byte2LongMap m) {
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
      public void putAll(Map<? extends Byte, ? extends Long> m) {
         throw new UnsupportedOperationException();
      }

      @Override
      public ObjectSet<Byte2LongMap.Entry> byte2LongEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSets.unmodifiable(this.map.byte2LongEntrySet());
         }

         return this.entries;
      }

      @Deprecated
      @Override
      public ObjectSet<Entry<Byte, Long>> entrySet() {
         return this.byte2LongEntrySet();
      }

      @Override
      public ByteSet keySet() {
         if (this.keys == null) {
            this.keys = ByteSets.unmodifiable(this.map.keySet());
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
      public long getOrDefault(byte key, long defaultValue) {
         return this.map.getOrDefault(key, defaultValue);
      }

      @Override
      public void forEach(BiConsumer<? super Byte, ? super Long> action) {
         this.map.forEach(action);
      }

      @Override
      public void replaceAll(BiFunction<? super Byte, ? super Long, ? extends Long> function) {
         throw new UnsupportedOperationException();
      }

      @Override
      public long putIfAbsent(byte key, long value) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean remove(byte key, long value) {
         throw new UnsupportedOperationException();
      }

      @Override
      public long replace(byte key, long value) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean replace(byte key, long oldValue, long newValue) {
         throw new UnsupportedOperationException();
      }

      @Override
      public long computeIfAbsent(byte key, IntToLongFunction mappingFunction) {
         throw new UnsupportedOperationException();
      }

      @Override
      public long computeIfAbsentNullable(byte key, IntFunction<? extends Long> mappingFunction) {
         throw new UnsupportedOperationException();
      }

      @Override
      public long computeIfAbsent(byte key, Byte2LongFunction mappingFunction) {
         throw new UnsupportedOperationException();
      }

      @Override
      public long computeIfPresent(byte key, BiFunction<? super Byte, ? super Long, ? extends Long> remappingFunction) {
         throw new UnsupportedOperationException();
      }

      @Override
      public long compute(byte key, BiFunction<? super Byte, ? super Long, ? extends Long> remappingFunction) {
         throw new UnsupportedOperationException();
      }

      @Override
      public long merge(byte key, long value, BiFunction<? super Long, ? super Long, ? extends Long> remappingFunction) {
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
      public Long replace(Byte key, Long value) {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      @Override
      public boolean replace(Byte key, Long oldValue, Long newValue) {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      @Override
      public Long putIfAbsent(Byte key, Long value) {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      @Override
      public Long computeIfAbsent(Byte key, Function<? super Byte, ? extends Long> mappingFunction) {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      @Override
      public Long computeIfPresent(Byte key, BiFunction<? super Byte, ? super Long, ? extends Long> remappingFunction) {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      @Override
      public Long compute(Byte key, BiFunction<? super Byte, ? super Long, ? extends Long> remappingFunction) {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      @Override
      public Long merge(Byte key, Long value, BiFunction<? super Long, ? super Long, ? extends Long> remappingFunction) {
         throw new UnsupportedOperationException();
      }
   }
}
