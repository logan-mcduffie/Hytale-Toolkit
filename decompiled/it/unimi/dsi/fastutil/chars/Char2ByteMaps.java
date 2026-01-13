package it.unimi.dsi.fastutil.chars;

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

public final class Char2ByteMaps {
   public static final Char2ByteMaps.EmptyMap EMPTY_MAP = new Char2ByteMaps.EmptyMap();

   private Char2ByteMaps() {
   }

   public static ObjectIterator<Char2ByteMap.Entry> fastIterator(Char2ByteMap map) {
      ObjectSet<Char2ByteMap.Entry> entries = map.char2ByteEntrySet();
      return entries instanceof Char2ByteMap.FastEntrySet ? ((Char2ByteMap.FastEntrySet)entries).fastIterator() : entries.iterator();
   }

   public static void fastForEach(Char2ByteMap map, Consumer<? super Char2ByteMap.Entry> consumer) {
      ObjectSet<Char2ByteMap.Entry> entries = map.char2ByteEntrySet();
      if (entries instanceof Char2ByteMap.FastEntrySet) {
         ((Char2ByteMap.FastEntrySet)entries).fastForEach(consumer);
      } else {
         entries.forEach(consumer);
      }
   }

   public static ObjectIterable<Char2ByteMap.Entry> fastIterable(Char2ByteMap map) {
      final ObjectSet<Char2ByteMap.Entry> entries = map.char2ByteEntrySet();
      return (ObjectIterable<Char2ByteMap.Entry>)(entries instanceof Char2ByteMap.FastEntrySet ? new ObjectIterable<Char2ByteMap.Entry>() {
         @Override
         public ObjectIterator<Char2ByteMap.Entry> iterator() {
            return ((Char2ByteMap.FastEntrySet)entries).fastIterator();
         }

         @Override
         public ObjectSpliterator<Char2ByteMap.Entry> spliterator() {
            return entries.spliterator();
         }

         @Override
         public void forEach(Consumer<? super Char2ByteMap.Entry> consumer) {
            ((Char2ByteMap.FastEntrySet)entries).fastForEach(consumer);
         }
      } : entries);
   }

   public static Char2ByteMap singleton(char key, byte value) {
      return new Char2ByteMaps.Singleton(key, value);
   }

   public static Char2ByteMap singleton(Character key, Byte value) {
      return new Char2ByteMaps.Singleton(key, value);
   }

   public static Char2ByteMap synchronize(Char2ByteMap m) {
      return new Char2ByteMaps.SynchronizedMap(m);
   }

   public static Char2ByteMap synchronize(Char2ByteMap m, Object sync) {
      return new Char2ByteMaps.SynchronizedMap(m, sync);
   }

   public static Char2ByteMap unmodifiable(Char2ByteMap m) {
      return new Char2ByteMaps.UnmodifiableMap(m);
   }

   public static class EmptyMap extends Char2ByteFunctions.EmptyFunction implements Char2ByteMap, Serializable, Cloneable {
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
      public byte getOrDefault(char key, byte defaultValue) {
         return defaultValue;
      }

      @Deprecated
      @Override
      public boolean containsValue(Object ov) {
         return false;
      }

      @Override
      public void putAll(Map<? extends Character, ? extends Byte> m) {
         throw new UnsupportedOperationException();
      }

      @Override
      public ObjectSet<Char2ByteMap.Entry> char2ByteEntrySet() {
         return ObjectSets.EMPTY_SET;
      }

      @Override
      public CharSet keySet() {
         return CharSets.EMPTY_SET;
      }

      @Override
      public ByteCollection values() {
         return ByteSets.EMPTY_SET;
      }

      @Override
      public void forEach(BiConsumer<? super Character, ? super Byte> consumer) {
      }

      @Override
      public Object clone() {
         return Char2ByteMaps.EMPTY_MAP;
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

   public static class Singleton extends Char2ByteFunctions.Singleton implements Char2ByteMap, Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected transient ObjectSet<Char2ByteMap.Entry> entries;
      protected transient CharSet keys;
      protected transient ByteCollection values;

      protected Singleton(char key, byte value) {
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
      public void putAll(Map<? extends Character, ? extends Byte> m) {
         throw new UnsupportedOperationException();
      }

      @Override
      public ObjectSet<Char2ByteMap.Entry> char2ByteEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSets.singleton(new AbstractChar2ByteMap.BasicEntry(this.key, this.value));
         }

         return this.entries;
      }

      @Deprecated
      @Override
      public ObjectSet<Entry<Character, Byte>> entrySet() {
         return this.char2ByteEntrySet();
      }

      @Override
      public CharSet keySet() {
         if (this.keys == null) {
            this.keys = CharSets.singleton(this.key);
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

   public static class SynchronizedMap extends Char2ByteFunctions.SynchronizedFunction implements Char2ByteMap, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Char2ByteMap map;
      protected transient ObjectSet<Char2ByteMap.Entry> entries;
      protected transient CharSet keys;
      protected transient ByteCollection values;

      protected SynchronizedMap(Char2ByteMap m, Object sync) {
         super(m, sync);
         this.map = m;
      }

      protected SynchronizedMap(Char2ByteMap m) {
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
      public void putAll(Map<? extends Character, ? extends Byte> m) {
         synchronized (this.sync) {
            this.map.putAll(m);
         }
      }

      @Override
      public ObjectSet<Char2ByteMap.Entry> char2ByteEntrySet() {
         synchronized (this.sync) {
            if (this.entries == null) {
               this.entries = ObjectSets.synchronize(this.map.char2ByteEntrySet(), this.sync);
            }

            return this.entries;
         }
      }

      @Deprecated
      @Override
      public ObjectSet<Entry<Character, Byte>> entrySet() {
         return this.char2ByteEntrySet();
      }

      @Override
      public CharSet keySet() {
         synchronized (this.sync) {
            if (this.keys == null) {
               this.keys = CharSets.synchronize(this.map.keySet(), this.sync);
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
      public byte getOrDefault(char key, byte defaultValue) {
         synchronized (this.sync) {
            return this.map.getOrDefault(key, defaultValue);
         }
      }

      @Override
      public void forEach(BiConsumer<? super Character, ? super Byte> action) {
         synchronized (this.sync) {
            this.map.forEach(action);
         }
      }

      @Override
      public void replaceAll(BiFunction<? super Character, ? super Byte, ? extends Byte> function) {
         synchronized (this.sync) {
            this.map.replaceAll(function);
         }
      }

      @Override
      public byte putIfAbsent(char key, byte value) {
         synchronized (this.sync) {
            return this.map.putIfAbsent(key, value);
         }
      }

      @Override
      public boolean remove(char key, byte value) {
         synchronized (this.sync) {
            return this.map.remove(key, value);
         }
      }

      @Override
      public byte replace(char key, byte value) {
         synchronized (this.sync) {
            return this.map.replace(key, value);
         }
      }

      @Override
      public boolean replace(char key, byte oldValue, byte newValue) {
         synchronized (this.sync) {
            return this.map.replace(key, oldValue, newValue);
         }
      }

      @Override
      public byte computeIfAbsent(char key, IntUnaryOperator mappingFunction) {
         synchronized (this.sync) {
            return this.map.computeIfAbsent(key, mappingFunction);
         }
      }

      @Override
      public byte computeIfAbsentNullable(char key, IntFunction<? extends Byte> mappingFunction) {
         synchronized (this.sync) {
            return this.map.computeIfAbsentNullable(key, mappingFunction);
         }
      }

      @Override
      public byte computeIfAbsent(char key, Char2ByteFunction mappingFunction) {
         synchronized (this.sync) {
            return this.map.computeIfAbsent(key, mappingFunction);
         }
      }

      @Override
      public byte computeIfPresent(char key, BiFunction<? super Character, ? super Byte, ? extends Byte> remappingFunction) {
         synchronized (this.sync) {
            return this.map.computeIfPresent(key, remappingFunction);
         }
      }

      @Override
      public byte compute(char key, BiFunction<? super Character, ? super Byte, ? extends Byte> remappingFunction) {
         synchronized (this.sync) {
            return this.map.compute(key, remappingFunction);
         }
      }

      @Override
      public byte merge(char key, byte value, BiFunction<? super Byte, ? super Byte, ? extends Byte> remappingFunction) {
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
      public Byte replace(Character key, Byte value) {
         synchronized (this.sync) {
            return this.map.replace(key, value);
         }
      }

      @Deprecated
      @Override
      public boolean replace(Character key, Byte oldValue, Byte newValue) {
         synchronized (this.sync) {
            return this.map.replace(key, oldValue, newValue);
         }
      }

      @Deprecated
      @Override
      public Byte putIfAbsent(Character key, Byte value) {
         synchronized (this.sync) {
            return this.map.putIfAbsent(key, value);
         }
      }

      @Deprecated
      @Override
      public Byte computeIfAbsent(Character key, Function<? super Character, ? extends Byte> mappingFunction) {
         synchronized (this.sync) {
            return this.map.computeIfAbsent(key, mappingFunction);
         }
      }

      @Deprecated
      @Override
      public Byte computeIfPresent(Character key, BiFunction<? super Character, ? super Byte, ? extends Byte> remappingFunction) {
         synchronized (this.sync) {
            return this.map.computeIfPresent(key, remappingFunction);
         }
      }

      @Deprecated
      @Override
      public Byte compute(Character key, BiFunction<? super Character, ? super Byte, ? extends Byte> remappingFunction) {
         synchronized (this.sync) {
            return this.map.compute(key, remappingFunction);
         }
      }

      @Deprecated
      @Override
      public Byte merge(Character key, Byte value, BiFunction<? super Byte, ? super Byte, ? extends Byte> remappingFunction) {
         synchronized (this.sync) {
            return this.map.merge(key, value, remappingFunction);
         }
      }
   }

   public static class UnmodifiableMap extends Char2ByteFunctions.UnmodifiableFunction implements Char2ByteMap, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Char2ByteMap map;
      protected transient ObjectSet<Char2ByteMap.Entry> entries;
      protected transient CharSet keys;
      protected transient ByteCollection values;

      protected UnmodifiableMap(Char2ByteMap m) {
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
      public void putAll(Map<? extends Character, ? extends Byte> m) {
         throw new UnsupportedOperationException();
      }

      @Override
      public ObjectSet<Char2ByteMap.Entry> char2ByteEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSets.unmodifiable(this.map.char2ByteEntrySet());
         }

         return this.entries;
      }

      @Deprecated
      @Override
      public ObjectSet<Entry<Character, Byte>> entrySet() {
         return this.char2ByteEntrySet();
      }

      @Override
      public CharSet keySet() {
         if (this.keys == null) {
            this.keys = CharSets.unmodifiable(this.map.keySet());
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
      public byte getOrDefault(char key, byte defaultValue) {
         return this.map.getOrDefault(key, defaultValue);
      }

      @Override
      public void forEach(BiConsumer<? super Character, ? super Byte> action) {
         this.map.forEach(action);
      }

      @Override
      public void replaceAll(BiFunction<? super Character, ? super Byte, ? extends Byte> function) {
         throw new UnsupportedOperationException();
      }

      @Override
      public byte putIfAbsent(char key, byte value) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean remove(char key, byte value) {
         throw new UnsupportedOperationException();
      }

      @Override
      public byte replace(char key, byte value) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean replace(char key, byte oldValue, byte newValue) {
         throw new UnsupportedOperationException();
      }

      @Override
      public byte computeIfAbsent(char key, IntUnaryOperator mappingFunction) {
         throw new UnsupportedOperationException();
      }

      @Override
      public byte computeIfAbsentNullable(char key, IntFunction<? extends Byte> mappingFunction) {
         throw new UnsupportedOperationException();
      }

      @Override
      public byte computeIfAbsent(char key, Char2ByteFunction mappingFunction) {
         throw new UnsupportedOperationException();
      }

      @Override
      public byte computeIfPresent(char key, BiFunction<? super Character, ? super Byte, ? extends Byte> remappingFunction) {
         throw new UnsupportedOperationException();
      }

      @Override
      public byte compute(char key, BiFunction<? super Character, ? super Byte, ? extends Byte> remappingFunction) {
         throw new UnsupportedOperationException();
      }

      @Override
      public byte merge(char key, byte value, BiFunction<? super Byte, ? super Byte, ? extends Byte> remappingFunction) {
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
      public Byte replace(Character key, Byte value) {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      @Override
      public boolean replace(Character key, Byte oldValue, Byte newValue) {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      @Override
      public Byte putIfAbsent(Character key, Byte value) {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      @Override
      public Byte computeIfAbsent(Character key, Function<? super Character, ? extends Byte> mappingFunction) {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      @Override
      public Byte computeIfPresent(Character key, BiFunction<? super Character, ? super Byte, ? extends Byte> remappingFunction) {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      @Override
      public Byte compute(Character key, BiFunction<? super Character, ? super Byte, ? extends Byte> remappingFunction) {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      @Override
      public Byte merge(Character key, Byte value, BiFunction<? super Byte, ? super Byte, ? extends Byte> remappingFunction) {
         throw new UnsupportedOperationException();
      }
   }
}
