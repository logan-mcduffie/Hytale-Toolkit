package it.unimi.dsi.fastutil.objects;

import it.unimi.dsi.fastutil.chars.CharCollection;
import it.unimi.dsi.fastutil.chars.CharCollections;
import it.unimi.dsi.fastutil.chars.CharSets;
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

public final class Object2CharMaps {
   public static final Object2CharMaps.EmptyMap EMPTY_MAP = new Object2CharMaps.EmptyMap();

   private Object2CharMaps() {
   }

   public static <K> ObjectIterator<Object2CharMap.Entry<K>> fastIterator(Object2CharMap<K> map) {
      ObjectSet<Object2CharMap.Entry<K>> entries = map.object2CharEntrySet();
      return entries instanceof Object2CharMap.FastEntrySet ? ((Object2CharMap.FastEntrySet)entries).fastIterator() : entries.iterator();
   }

   public static <K> void fastForEach(Object2CharMap<K> map, Consumer<? super Object2CharMap.Entry<K>> consumer) {
      ObjectSet<Object2CharMap.Entry<K>> entries = map.object2CharEntrySet();
      if (entries instanceof Object2CharMap.FastEntrySet) {
         ((Object2CharMap.FastEntrySet)entries).fastForEach(consumer);
      } else {
         entries.forEach(consumer);
      }
   }

   public static <K> ObjectIterable<Object2CharMap.Entry<K>> fastIterable(Object2CharMap<K> map) {
      final ObjectSet<Object2CharMap.Entry<K>> entries = map.object2CharEntrySet();
      return (ObjectIterable<Object2CharMap.Entry<K>>)(entries instanceof Object2CharMap.FastEntrySet ? new ObjectIterable<Object2CharMap.Entry<K>>() {
         @Override
         public ObjectIterator<Object2CharMap.Entry<K>> iterator() {
            return ((Object2CharMap.FastEntrySet)entries).fastIterator();
         }

         @Override
         public ObjectSpliterator<Object2CharMap.Entry<K>> spliterator() {
            return entries.spliterator();
         }

         @Override
         public void forEach(Consumer<? super Object2CharMap.Entry<K>> consumer) {
            ((Object2CharMap.FastEntrySet)entries).fastForEach(consumer);
         }
      } : entries);
   }

   public static <K> Object2CharMap<K> emptyMap() {
      return EMPTY_MAP;
   }

   public static <K> Object2CharMap<K> singleton(K key, char value) {
      return new Object2CharMaps.Singleton<>(key, value);
   }

   public static <K> Object2CharMap<K> singleton(K key, Character value) {
      return new Object2CharMaps.Singleton<>(key, value);
   }

   public static <K> Object2CharMap<K> synchronize(Object2CharMap<K> m) {
      return new Object2CharMaps.SynchronizedMap<>(m);
   }

   public static <K> Object2CharMap<K> synchronize(Object2CharMap<K> m, Object sync) {
      return new Object2CharMaps.SynchronizedMap<>(m, sync);
   }

   public static <K> Object2CharMap<K> unmodifiable(Object2CharMap<? extends K> m) {
      return new Object2CharMaps.UnmodifiableMap<>(m);
   }

   public static class EmptyMap<K> extends Object2CharFunctions.EmptyFunction<K> implements Object2CharMap<K>, Serializable, Cloneable {
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
      public char getOrDefault(Object key, char defaultValue) {
         return defaultValue;
      }

      @Deprecated
      @Override
      public boolean containsValue(Object ov) {
         return false;
      }

      @Override
      public void putAll(Map<? extends K, ? extends Character> m) {
         throw new UnsupportedOperationException();
      }

      @Override
      public ObjectSet<Object2CharMap.Entry<K>> object2CharEntrySet() {
         return ObjectSets.EMPTY_SET;
      }

      @Override
      public ObjectSet<K> keySet() {
         return ObjectSets.EMPTY_SET;
      }

      @Override
      public CharCollection values() {
         return CharSets.EMPTY_SET;
      }

      @Override
      public void forEach(BiConsumer<? super K, ? super Character> consumer) {
      }

      @Override
      public Object clone() {
         return Object2CharMaps.EMPTY_MAP;
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

   public static class Singleton<K> extends Object2CharFunctions.Singleton<K> implements Object2CharMap<K>, Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected transient ObjectSet<Object2CharMap.Entry<K>> entries;
      protected transient ObjectSet<K> keys;
      protected transient CharCollection values;

      protected Singleton(K key, char value) {
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
      public void putAll(Map<? extends K, ? extends Character> m) {
         throw new UnsupportedOperationException();
      }

      @Override
      public ObjectSet<Object2CharMap.Entry<K>> object2CharEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSets.singleton(new AbstractObject2CharMap.BasicEntry<>(this.key, this.value));
         }

         return this.entries;
      }

      @Deprecated
      @Override
      public ObjectSet<Entry<K, Character>> entrySet() {
         return this.object2CharEntrySet();
      }

      @Override
      public ObjectSet<K> keySet() {
         if (this.keys == null) {
            this.keys = ObjectSets.singleton(this.key);
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

   public static class SynchronizedMap<K> extends Object2CharFunctions.SynchronizedFunction<K> implements Object2CharMap<K>, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Object2CharMap<K> map;
      protected transient ObjectSet<Object2CharMap.Entry<K>> entries;
      protected transient ObjectSet<K> keys;
      protected transient CharCollection values;

      protected SynchronizedMap(Object2CharMap<K> m, Object sync) {
         super(m, sync);
         this.map = m;
      }

      protected SynchronizedMap(Object2CharMap<K> m) {
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
      public void putAll(Map<? extends K, ? extends Character> m) {
         synchronized (this.sync) {
            this.map.putAll(m);
         }
      }

      @Override
      public ObjectSet<Object2CharMap.Entry<K>> object2CharEntrySet() {
         synchronized (this.sync) {
            if (this.entries == null) {
               this.entries = ObjectSets.synchronize(this.map.object2CharEntrySet(), this.sync);
            }

            return this.entries;
         }
      }

      @Deprecated
      @Override
      public ObjectSet<Entry<K, Character>> entrySet() {
         return this.object2CharEntrySet();
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
      public char getOrDefault(Object key, char defaultValue) {
         synchronized (this.sync) {
            return this.map.getOrDefault(key, defaultValue);
         }
      }

      @Override
      public void forEach(BiConsumer<? super K, ? super Character> action) {
         synchronized (this.sync) {
            this.map.forEach(action);
         }
      }

      @Override
      public void replaceAll(BiFunction<? super K, ? super Character, ? extends Character> function) {
         synchronized (this.sync) {
            this.map.replaceAll(function);
         }
      }

      @Override
      public char putIfAbsent(K key, char value) {
         synchronized (this.sync) {
            return this.map.putIfAbsent(key, value);
         }
      }

      @Override
      public boolean remove(Object key, char value) {
         synchronized (this.sync) {
            return this.map.remove(key, value);
         }
      }

      @Override
      public char replace(K key, char value) {
         synchronized (this.sync) {
            return this.map.replace(key, value);
         }
      }

      @Override
      public boolean replace(K key, char oldValue, char newValue) {
         synchronized (this.sync) {
            return this.map.replace(key, oldValue, newValue);
         }
      }

      @Override
      public char computeIfAbsent(K key, ToIntFunction<? super K> mappingFunction) {
         synchronized (this.sync) {
            return this.map.computeIfAbsent(key, mappingFunction);
         }
      }

      @Override
      public char computeIfAbsent(K key, Object2CharFunction<? super K> mappingFunction) {
         synchronized (this.sync) {
            return this.map.computeIfAbsent(key, mappingFunction);
         }
      }

      @Override
      public char computeCharIfPresent(K key, BiFunction<? super K, ? super Character, ? extends Character> remappingFunction) {
         synchronized (this.sync) {
            return this.map.computeCharIfPresent(key, remappingFunction);
         }
      }

      @Override
      public char computeChar(K key, BiFunction<? super K, ? super Character, ? extends Character> remappingFunction) {
         synchronized (this.sync) {
            return this.map.computeChar(key, remappingFunction);
         }
      }

      @Override
      public char merge(K key, char value, BiFunction<? super Character, ? super Character, ? extends Character> remappingFunction) {
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
      public Character replace(K key, Character value) {
         synchronized (this.sync) {
            return this.map.replace(key, value);
         }
      }

      @Deprecated
      @Override
      public boolean replace(K key, Character oldValue, Character newValue) {
         synchronized (this.sync) {
            return this.map.replace(key, oldValue, newValue);
         }
      }

      @Deprecated
      @Override
      public Character putIfAbsent(K key, Character value) {
         synchronized (this.sync) {
            return this.map.putIfAbsent(key, value);
         }
      }

      public Character computeIfAbsent(K key, Function<? super K, ? extends Character> mappingFunction) {
         synchronized (this.sync) {
            return this.map.computeIfAbsent(key, mappingFunction);
         }
      }

      public Character computeIfPresent(K key, BiFunction<? super K, ? super Character, ? extends Character> remappingFunction) {
         synchronized (this.sync) {
            return this.map.computeIfPresent(key, remappingFunction);
         }
      }

      public Character compute(K key, BiFunction<? super K, ? super Character, ? extends Character> remappingFunction) {
         synchronized (this.sync) {
            return this.map.compute(key, remappingFunction);
         }
      }

      @Deprecated
      @Override
      public Character merge(K key, Character value, BiFunction<? super Character, ? super Character, ? extends Character> remappingFunction) {
         synchronized (this.sync) {
            return this.map.merge(key, value, remappingFunction);
         }
      }
   }

   public static class UnmodifiableMap<K> extends Object2CharFunctions.UnmodifiableFunction<K> implements Object2CharMap<K>, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Object2CharMap<? extends K> map;
      protected transient ObjectSet<Object2CharMap.Entry<K>> entries;
      protected transient ObjectSet<K> keys;
      protected transient CharCollection values;

      protected UnmodifiableMap(Object2CharMap<? extends K> m) {
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
      public void putAll(Map<? extends K, ? extends Character> m) {
         throw new UnsupportedOperationException();
      }

      @Override
      public ObjectSet<Object2CharMap.Entry<K>> object2CharEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSets.unmodifiable(this.map.object2CharEntrySet());
         }

         return this.entries;
      }

      @Deprecated
      @Override
      public ObjectSet<Entry<K, Character>> entrySet() {
         return this.object2CharEntrySet();
      }

      @Override
      public ObjectSet<K> keySet() {
         if (this.keys == null) {
            this.keys = ObjectSets.unmodifiable(this.map.keySet());
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
      public char getOrDefault(Object key, char defaultValue) {
         return this.map.getOrDefault(key, defaultValue);
      }

      @Override
      public void forEach(BiConsumer<? super K, ? super Character> action) {
         this.map.forEach(action);
      }

      @Override
      public void replaceAll(BiFunction<? super K, ? super Character, ? extends Character> function) {
         throw new UnsupportedOperationException();
      }

      @Override
      public char putIfAbsent(K key, char value) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean remove(Object key, char value) {
         throw new UnsupportedOperationException();
      }

      @Override
      public char replace(K key, char value) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean replace(K key, char oldValue, char newValue) {
         throw new UnsupportedOperationException();
      }

      @Override
      public char computeIfAbsent(K key, ToIntFunction<? super K> mappingFunction) {
         throw new UnsupportedOperationException();
      }

      @Override
      public char computeIfAbsent(K key, Object2CharFunction<? super K> mappingFunction) {
         throw new UnsupportedOperationException();
      }

      @Override
      public char computeCharIfPresent(K key, BiFunction<? super K, ? super Character, ? extends Character> remappingFunction) {
         throw new UnsupportedOperationException();
      }

      @Override
      public char computeChar(K key, BiFunction<? super K, ? super Character, ? extends Character> remappingFunction) {
         throw new UnsupportedOperationException();
      }

      @Override
      public char merge(K key, char value, BiFunction<? super Character, ? super Character, ? extends Character> remappingFunction) {
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
      public Character replace(K key, Character value) {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      @Override
      public boolean replace(K key, Character oldValue, Character newValue) {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      @Override
      public Character putIfAbsent(K key, Character value) {
         throw new UnsupportedOperationException();
      }

      public Character computeIfAbsent(K key, Function<? super K, ? extends Character> mappingFunction) {
         throw new UnsupportedOperationException();
      }

      public Character computeIfPresent(K key, BiFunction<? super K, ? super Character, ? extends Character> remappingFunction) {
         throw new UnsupportedOperationException();
      }

      public Character compute(K key, BiFunction<? super K, ? super Character, ? extends Character> remappingFunction) {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      @Override
      public Character merge(K key, Character value, BiFunction<? super Character, ? super Character, ? extends Character> remappingFunction) {
         throw new UnsupportedOperationException();
      }
   }
}
