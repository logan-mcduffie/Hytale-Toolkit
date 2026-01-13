package it.unimi.dsi.fastutil.shorts;

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
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.IntUnaryOperator;

public final class Short2CharMaps {
   public static final Short2CharMaps.EmptyMap EMPTY_MAP = new Short2CharMaps.EmptyMap();

   private Short2CharMaps() {
   }

   public static ObjectIterator<Short2CharMap.Entry> fastIterator(Short2CharMap map) {
      ObjectSet<Short2CharMap.Entry> entries = map.short2CharEntrySet();
      return entries instanceof Short2CharMap.FastEntrySet ? ((Short2CharMap.FastEntrySet)entries).fastIterator() : entries.iterator();
   }

   public static void fastForEach(Short2CharMap map, Consumer<? super Short2CharMap.Entry> consumer) {
      ObjectSet<Short2CharMap.Entry> entries = map.short2CharEntrySet();
      if (entries instanceof Short2CharMap.FastEntrySet) {
         ((Short2CharMap.FastEntrySet)entries).fastForEach(consumer);
      } else {
         entries.forEach(consumer);
      }
   }

   public static ObjectIterable<Short2CharMap.Entry> fastIterable(Short2CharMap map) {
      final ObjectSet<Short2CharMap.Entry> entries = map.short2CharEntrySet();
      return (ObjectIterable<Short2CharMap.Entry>)(entries instanceof Short2CharMap.FastEntrySet ? new ObjectIterable<Short2CharMap.Entry>() {
         @Override
         public ObjectIterator<Short2CharMap.Entry> iterator() {
            return ((Short2CharMap.FastEntrySet)entries).fastIterator();
         }

         @Override
         public ObjectSpliterator<Short2CharMap.Entry> spliterator() {
            return entries.spliterator();
         }

         @Override
         public void forEach(Consumer<? super Short2CharMap.Entry> consumer) {
            ((Short2CharMap.FastEntrySet)entries).fastForEach(consumer);
         }
      } : entries);
   }

   public static Short2CharMap singleton(short key, char value) {
      return new Short2CharMaps.Singleton(key, value);
   }

   public static Short2CharMap singleton(Short key, Character value) {
      return new Short2CharMaps.Singleton(key, value);
   }

   public static Short2CharMap synchronize(Short2CharMap m) {
      return new Short2CharMaps.SynchronizedMap(m);
   }

   public static Short2CharMap synchronize(Short2CharMap m, Object sync) {
      return new Short2CharMaps.SynchronizedMap(m, sync);
   }

   public static Short2CharMap unmodifiable(Short2CharMap m) {
      return new Short2CharMaps.UnmodifiableMap(m);
   }

   public static class EmptyMap extends Short2CharFunctions.EmptyFunction implements Short2CharMap, Serializable, Cloneable {
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
      public char getOrDefault(short key, char defaultValue) {
         return defaultValue;
      }

      @Deprecated
      @Override
      public boolean containsValue(Object ov) {
         return false;
      }

      @Override
      public void putAll(Map<? extends Short, ? extends Character> m) {
         throw new UnsupportedOperationException();
      }

      @Override
      public ObjectSet<Short2CharMap.Entry> short2CharEntrySet() {
         return ObjectSets.EMPTY_SET;
      }

      @Override
      public ShortSet keySet() {
         return ShortSets.EMPTY_SET;
      }

      @Override
      public CharCollection values() {
         return CharSets.EMPTY_SET;
      }

      @Override
      public void forEach(BiConsumer<? super Short, ? super Character> consumer) {
      }

      @Override
      public Object clone() {
         return Short2CharMaps.EMPTY_MAP;
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

   public static class Singleton extends Short2CharFunctions.Singleton implements Short2CharMap, Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected transient ObjectSet<Short2CharMap.Entry> entries;
      protected transient ShortSet keys;
      protected transient CharCollection values;

      protected Singleton(short key, char value) {
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
      public void putAll(Map<? extends Short, ? extends Character> m) {
         throw new UnsupportedOperationException();
      }

      @Override
      public ObjectSet<Short2CharMap.Entry> short2CharEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSets.singleton(new AbstractShort2CharMap.BasicEntry(this.key, this.value));
         }

         return this.entries;
      }

      @Deprecated
      @Override
      public ObjectSet<Entry<Short, Character>> entrySet() {
         return this.short2CharEntrySet();
      }

      @Override
      public ShortSet keySet() {
         if (this.keys == null) {
            this.keys = ShortSets.singleton(this.key);
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

   public static class SynchronizedMap extends Short2CharFunctions.SynchronizedFunction implements Short2CharMap, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Short2CharMap map;
      protected transient ObjectSet<Short2CharMap.Entry> entries;
      protected transient ShortSet keys;
      protected transient CharCollection values;

      protected SynchronizedMap(Short2CharMap m, Object sync) {
         super(m, sync);
         this.map = m;
      }

      protected SynchronizedMap(Short2CharMap m) {
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
      public void putAll(Map<? extends Short, ? extends Character> m) {
         synchronized (this.sync) {
            this.map.putAll(m);
         }
      }

      @Override
      public ObjectSet<Short2CharMap.Entry> short2CharEntrySet() {
         synchronized (this.sync) {
            if (this.entries == null) {
               this.entries = ObjectSets.synchronize(this.map.short2CharEntrySet(), this.sync);
            }

            return this.entries;
         }
      }

      @Deprecated
      @Override
      public ObjectSet<Entry<Short, Character>> entrySet() {
         return this.short2CharEntrySet();
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
      public char getOrDefault(short key, char defaultValue) {
         synchronized (this.sync) {
            return this.map.getOrDefault(key, defaultValue);
         }
      }

      @Override
      public void forEach(BiConsumer<? super Short, ? super Character> action) {
         synchronized (this.sync) {
            this.map.forEach(action);
         }
      }

      @Override
      public void replaceAll(BiFunction<? super Short, ? super Character, ? extends Character> function) {
         synchronized (this.sync) {
            this.map.replaceAll(function);
         }
      }

      @Override
      public char putIfAbsent(short key, char value) {
         synchronized (this.sync) {
            return this.map.putIfAbsent(key, value);
         }
      }

      @Override
      public boolean remove(short key, char value) {
         synchronized (this.sync) {
            return this.map.remove(key, value);
         }
      }

      @Override
      public char replace(short key, char value) {
         synchronized (this.sync) {
            return this.map.replace(key, value);
         }
      }

      @Override
      public boolean replace(short key, char oldValue, char newValue) {
         synchronized (this.sync) {
            return this.map.replace(key, oldValue, newValue);
         }
      }

      @Override
      public char computeIfAbsent(short key, IntUnaryOperator mappingFunction) {
         synchronized (this.sync) {
            return this.map.computeIfAbsent(key, mappingFunction);
         }
      }

      @Override
      public char computeIfAbsentNullable(short key, IntFunction<? extends Character> mappingFunction) {
         synchronized (this.sync) {
            return this.map.computeIfAbsentNullable(key, mappingFunction);
         }
      }

      @Override
      public char computeIfAbsent(short key, Short2CharFunction mappingFunction) {
         synchronized (this.sync) {
            return this.map.computeIfAbsent(key, mappingFunction);
         }
      }

      @Override
      public char computeIfPresent(short key, BiFunction<? super Short, ? super Character, ? extends Character> remappingFunction) {
         synchronized (this.sync) {
            return this.map.computeIfPresent(key, remappingFunction);
         }
      }

      @Override
      public char compute(short key, BiFunction<? super Short, ? super Character, ? extends Character> remappingFunction) {
         synchronized (this.sync) {
            return this.map.compute(key, remappingFunction);
         }
      }

      @Override
      public char merge(short key, char value, BiFunction<? super Character, ? super Character, ? extends Character> remappingFunction) {
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
      public Character replace(Short key, Character value) {
         synchronized (this.sync) {
            return this.map.replace(key, value);
         }
      }

      @Deprecated
      @Override
      public boolean replace(Short key, Character oldValue, Character newValue) {
         synchronized (this.sync) {
            return this.map.replace(key, oldValue, newValue);
         }
      }

      @Deprecated
      @Override
      public Character putIfAbsent(Short key, Character value) {
         synchronized (this.sync) {
            return this.map.putIfAbsent(key, value);
         }
      }

      @Deprecated
      @Override
      public Character computeIfAbsent(Short key, Function<? super Short, ? extends Character> mappingFunction) {
         synchronized (this.sync) {
            return this.map.computeIfAbsent(key, mappingFunction);
         }
      }

      @Deprecated
      @Override
      public Character computeIfPresent(Short key, BiFunction<? super Short, ? super Character, ? extends Character> remappingFunction) {
         synchronized (this.sync) {
            return this.map.computeIfPresent(key, remappingFunction);
         }
      }

      @Deprecated
      @Override
      public Character compute(Short key, BiFunction<? super Short, ? super Character, ? extends Character> remappingFunction) {
         synchronized (this.sync) {
            return this.map.compute(key, remappingFunction);
         }
      }

      @Deprecated
      @Override
      public Character merge(Short key, Character value, BiFunction<? super Character, ? super Character, ? extends Character> remappingFunction) {
         synchronized (this.sync) {
            return this.map.merge(key, value, remappingFunction);
         }
      }
   }

   public static class UnmodifiableMap extends Short2CharFunctions.UnmodifiableFunction implements Short2CharMap, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Short2CharMap map;
      protected transient ObjectSet<Short2CharMap.Entry> entries;
      protected transient ShortSet keys;
      protected transient CharCollection values;

      protected UnmodifiableMap(Short2CharMap m) {
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
      public void putAll(Map<? extends Short, ? extends Character> m) {
         throw new UnsupportedOperationException();
      }

      @Override
      public ObjectSet<Short2CharMap.Entry> short2CharEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSets.unmodifiable(this.map.short2CharEntrySet());
         }

         return this.entries;
      }

      @Deprecated
      @Override
      public ObjectSet<Entry<Short, Character>> entrySet() {
         return this.short2CharEntrySet();
      }

      @Override
      public ShortSet keySet() {
         if (this.keys == null) {
            this.keys = ShortSets.unmodifiable(this.map.keySet());
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
      public char getOrDefault(short key, char defaultValue) {
         return this.map.getOrDefault(key, defaultValue);
      }

      @Override
      public void forEach(BiConsumer<? super Short, ? super Character> action) {
         this.map.forEach(action);
      }

      @Override
      public void replaceAll(BiFunction<? super Short, ? super Character, ? extends Character> function) {
         throw new UnsupportedOperationException();
      }

      @Override
      public char putIfAbsent(short key, char value) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean remove(short key, char value) {
         throw new UnsupportedOperationException();
      }

      @Override
      public char replace(short key, char value) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean replace(short key, char oldValue, char newValue) {
         throw new UnsupportedOperationException();
      }

      @Override
      public char computeIfAbsent(short key, IntUnaryOperator mappingFunction) {
         throw new UnsupportedOperationException();
      }

      @Override
      public char computeIfAbsentNullable(short key, IntFunction<? extends Character> mappingFunction) {
         throw new UnsupportedOperationException();
      }

      @Override
      public char computeIfAbsent(short key, Short2CharFunction mappingFunction) {
         throw new UnsupportedOperationException();
      }

      @Override
      public char computeIfPresent(short key, BiFunction<? super Short, ? super Character, ? extends Character> remappingFunction) {
         throw new UnsupportedOperationException();
      }

      @Override
      public char compute(short key, BiFunction<? super Short, ? super Character, ? extends Character> remappingFunction) {
         throw new UnsupportedOperationException();
      }

      @Override
      public char merge(short key, char value, BiFunction<? super Character, ? super Character, ? extends Character> remappingFunction) {
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
      public Character replace(Short key, Character value) {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      @Override
      public boolean replace(Short key, Character oldValue, Character newValue) {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      @Override
      public Character putIfAbsent(Short key, Character value) {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      @Override
      public Character computeIfAbsent(Short key, Function<? super Short, ? extends Character> mappingFunction) {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      @Override
      public Character computeIfPresent(Short key, BiFunction<? super Short, ? super Character, ? extends Character> remappingFunction) {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      @Override
      public Character compute(Short key, BiFunction<? super Short, ? super Character, ? extends Character> remappingFunction) {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      @Override
      public Character merge(Short key, Character value, BiFunction<? super Character, ? super Character, ? extends Character> remappingFunction) {
         throw new UnsupportedOperationException();
      }
   }
}
