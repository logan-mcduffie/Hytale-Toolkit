package it.unimi.dsi.fastutil.chars;

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

public final class Char2CharMaps {
   public static final Char2CharMaps.EmptyMap EMPTY_MAP = new Char2CharMaps.EmptyMap();

   private Char2CharMaps() {
   }

   public static ObjectIterator<Char2CharMap.Entry> fastIterator(Char2CharMap map) {
      ObjectSet<Char2CharMap.Entry> entries = map.char2CharEntrySet();
      return entries instanceof Char2CharMap.FastEntrySet ? ((Char2CharMap.FastEntrySet)entries).fastIterator() : entries.iterator();
   }

   public static void fastForEach(Char2CharMap map, Consumer<? super Char2CharMap.Entry> consumer) {
      ObjectSet<Char2CharMap.Entry> entries = map.char2CharEntrySet();
      if (entries instanceof Char2CharMap.FastEntrySet) {
         ((Char2CharMap.FastEntrySet)entries).fastForEach(consumer);
      } else {
         entries.forEach(consumer);
      }
   }

   public static ObjectIterable<Char2CharMap.Entry> fastIterable(Char2CharMap map) {
      final ObjectSet<Char2CharMap.Entry> entries = map.char2CharEntrySet();
      return (ObjectIterable<Char2CharMap.Entry>)(entries instanceof Char2CharMap.FastEntrySet ? new ObjectIterable<Char2CharMap.Entry>() {
         @Override
         public ObjectIterator<Char2CharMap.Entry> iterator() {
            return ((Char2CharMap.FastEntrySet)entries).fastIterator();
         }

         @Override
         public ObjectSpliterator<Char2CharMap.Entry> spliterator() {
            return entries.spliterator();
         }

         @Override
         public void forEach(Consumer<? super Char2CharMap.Entry> consumer) {
            ((Char2CharMap.FastEntrySet)entries).fastForEach(consumer);
         }
      } : entries);
   }

   public static Char2CharMap singleton(char key, char value) {
      return new Char2CharMaps.Singleton(key, value);
   }

   public static Char2CharMap singleton(Character key, Character value) {
      return new Char2CharMaps.Singleton(key, value);
   }

   public static Char2CharMap synchronize(Char2CharMap m) {
      return new Char2CharMaps.SynchronizedMap(m);
   }

   public static Char2CharMap synchronize(Char2CharMap m, Object sync) {
      return new Char2CharMaps.SynchronizedMap(m, sync);
   }

   public static Char2CharMap unmodifiable(Char2CharMap m) {
      return new Char2CharMaps.UnmodifiableMap(m);
   }

   public static class EmptyMap extends Char2CharFunctions.EmptyFunction implements Char2CharMap, Serializable, Cloneable {
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
      public char getOrDefault(char key, char defaultValue) {
         return defaultValue;
      }

      @Deprecated
      @Override
      public boolean containsValue(Object ov) {
         return false;
      }

      @Override
      public void putAll(Map<? extends Character, ? extends Character> m) {
         throw new UnsupportedOperationException();
      }

      @Override
      public ObjectSet<Char2CharMap.Entry> char2CharEntrySet() {
         return ObjectSets.EMPTY_SET;
      }

      @Override
      public CharSet keySet() {
         return CharSets.EMPTY_SET;
      }

      @Override
      public CharCollection values() {
         return CharSets.EMPTY_SET;
      }

      @Override
      public void forEach(BiConsumer<? super Character, ? super Character> consumer) {
      }

      @Override
      public Object clone() {
         return Char2CharMaps.EMPTY_MAP;
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

   public static class Singleton extends Char2CharFunctions.Singleton implements Char2CharMap, Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected transient ObjectSet<Char2CharMap.Entry> entries;
      protected transient CharSet keys;
      protected transient CharCollection values;

      protected Singleton(char key, char value) {
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
      public void putAll(Map<? extends Character, ? extends Character> m) {
         throw new UnsupportedOperationException();
      }

      @Override
      public ObjectSet<Char2CharMap.Entry> char2CharEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSets.singleton(new AbstractChar2CharMap.BasicEntry(this.key, this.value));
         }

         return this.entries;
      }

      @Deprecated
      @Override
      public ObjectSet<Entry<Character, Character>> entrySet() {
         return this.char2CharEntrySet();
      }

      @Override
      public CharSet keySet() {
         if (this.keys == null) {
            this.keys = CharSets.singleton(this.key);
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

   public static class SynchronizedMap extends Char2CharFunctions.SynchronizedFunction implements Char2CharMap, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Char2CharMap map;
      protected transient ObjectSet<Char2CharMap.Entry> entries;
      protected transient CharSet keys;
      protected transient CharCollection values;

      protected SynchronizedMap(Char2CharMap m, Object sync) {
         super(m, sync);
         this.map = m;
      }

      protected SynchronizedMap(Char2CharMap m) {
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
      public void putAll(Map<? extends Character, ? extends Character> m) {
         synchronized (this.sync) {
            this.map.putAll(m);
         }
      }

      @Override
      public ObjectSet<Char2CharMap.Entry> char2CharEntrySet() {
         synchronized (this.sync) {
            if (this.entries == null) {
               this.entries = ObjectSets.synchronize(this.map.char2CharEntrySet(), this.sync);
            }

            return this.entries;
         }
      }

      @Deprecated
      @Override
      public ObjectSet<Entry<Character, Character>> entrySet() {
         return this.char2CharEntrySet();
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
      public char getOrDefault(char key, char defaultValue) {
         synchronized (this.sync) {
            return this.map.getOrDefault(key, defaultValue);
         }
      }

      @Override
      public void forEach(BiConsumer<? super Character, ? super Character> action) {
         synchronized (this.sync) {
            this.map.forEach(action);
         }
      }

      @Override
      public void replaceAll(BiFunction<? super Character, ? super Character, ? extends Character> function) {
         synchronized (this.sync) {
            this.map.replaceAll(function);
         }
      }

      @Override
      public char putIfAbsent(char key, char value) {
         synchronized (this.sync) {
            return this.map.putIfAbsent(key, value);
         }
      }

      @Override
      public boolean remove(char key, char value) {
         synchronized (this.sync) {
            return this.map.remove(key, value);
         }
      }

      @Override
      public char replace(char key, char value) {
         synchronized (this.sync) {
            return this.map.replace(key, value);
         }
      }

      @Override
      public boolean replace(char key, char oldValue, char newValue) {
         synchronized (this.sync) {
            return this.map.replace(key, oldValue, newValue);
         }
      }

      @Override
      public char computeIfAbsent(char key, IntUnaryOperator mappingFunction) {
         synchronized (this.sync) {
            return this.map.computeIfAbsent(key, mappingFunction);
         }
      }

      @Override
      public char computeIfAbsentNullable(char key, IntFunction<? extends Character> mappingFunction) {
         synchronized (this.sync) {
            return this.map.computeIfAbsentNullable(key, mappingFunction);
         }
      }

      @Override
      public char computeIfAbsent(char key, Char2CharFunction mappingFunction) {
         synchronized (this.sync) {
            return this.map.computeIfAbsent(key, mappingFunction);
         }
      }

      @Override
      public char computeIfPresent(char key, BiFunction<? super Character, ? super Character, ? extends Character> remappingFunction) {
         synchronized (this.sync) {
            return this.map.computeIfPresent(key, remappingFunction);
         }
      }

      @Override
      public char compute(char key, BiFunction<? super Character, ? super Character, ? extends Character> remappingFunction) {
         synchronized (this.sync) {
            return this.map.compute(key, remappingFunction);
         }
      }

      @Override
      public char merge(char key, char value, BiFunction<? super Character, ? super Character, ? extends Character> remappingFunction) {
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
      public Character replace(Character key, Character value) {
         synchronized (this.sync) {
            return this.map.replace(key, value);
         }
      }

      @Deprecated
      @Override
      public boolean replace(Character key, Character oldValue, Character newValue) {
         synchronized (this.sync) {
            return this.map.replace(key, oldValue, newValue);
         }
      }

      @Deprecated
      @Override
      public Character putIfAbsent(Character key, Character value) {
         synchronized (this.sync) {
            return this.map.putIfAbsent(key, value);
         }
      }

      @Deprecated
      @Override
      public Character computeIfAbsent(Character key, Function<? super Character, ? extends Character> mappingFunction) {
         synchronized (this.sync) {
            return this.map.computeIfAbsent(key, mappingFunction);
         }
      }

      @Deprecated
      @Override
      public Character computeIfPresent(Character key, BiFunction<? super Character, ? super Character, ? extends Character> remappingFunction) {
         synchronized (this.sync) {
            return this.map.computeIfPresent(key, remappingFunction);
         }
      }

      @Deprecated
      @Override
      public Character compute(Character key, BiFunction<? super Character, ? super Character, ? extends Character> remappingFunction) {
         synchronized (this.sync) {
            return this.map.compute(key, remappingFunction);
         }
      }

      @Deprecated
      @Override
      public Character merge(Character key, Character value, BiFunction<? super Character, ? super Character, ? extends Character> remappingFunction) {
         synchronized (this.sync) {
            return this.map.merge(key, value, remappingFunction);
         }
      }
   }

   public static class UnmodifiableMap extends Char2CharFunctions.UnmodifiableFunction implements Char2CharMap, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Char2CharMap map;
      protected transient ObjectSet<Char2CharMap.Entry> entries;
      protected transient CharSet keys;
      protected transient CharCollection values;

      protected UnmodifiableMap(Char2CharMap m) {
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
      public void putAll(Map<? extends Character, ? extends Character> m) {
         throw new UnsupportedOperationException();
      }

      @Override
      public ObjectSet<Char2CharMap.Entry> char2CharEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSets.unmodifiable(this.map.char2CharEntrySet());
         }

         return this.entries;
      }

      @Deprecated
      @Override
      public ObjectSet<Entry<Character, Character>> entrySet() {
         return this.char2CharEntrySet();
      }

      @Override
      public CharSet keySet() {
         if (this.keys == null) {
            this.keys = CharSets.unmodifiable(this.map.keySet());
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
      public char getOrDefault(char key, char defaultValue) {
         return this.map.getOrDefault(key, defaultValue);
      }

      @Override
      public void forEach(BiConsumer<? super Character, ? super Character> action) {
         this.map.forEach(action);
      }

      @Override
      public void replaceAll(BiFunction<? super Character, ? super Character, ? extends Character> function) {
         throw new UnsupportedOperationException();
      }

      @Override
      public char putIfAbsent(char key, char value) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean remove(char key, char value) {
         throw new UnsupportedOperationException();
      }

      @Override
      public char replace(char key, char value) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean replace(char key, char oldValue, char newValue) {
         throw new UnsupportedOperationException();
      }

      @Override
      public char computeIfAbsent(char key, IntUnaryOperator mappingFunction) {
         throw new UnsupportedOperationException();
      }

      @Override
      public char computeIfAbsentNullable(char key, IntFunction<? extends Character> mappingFunction) {
         throw new UnsupportedOperationException();
      }

      @Override
      public char computeIfAbsent(char key, Char2CharFunction mappingFunction) {
         throw new UnsupportedOperationException();
      }

      @Override
      public char computeIfPresent(char key, BiFunction<? super Character, ? super Character, ? extends Character> remappingFunction) {
         throw new UnsupportedOperationException();
      }

      @Override
      public char compute(char key, BiFunction<? super Character, ? super Character, ? extends Character> remappingFunction) {
         throw new UnsupportedOperationException();
      }

      @Override
      public char merge(char key, char value, BiFunction<? super Character, ? super Character, ? extends Character> remappingFunction) {
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
      public Character replace(Character key, Character value) {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      @Override
      public boolean replace(Character key, Character oldValue, Character newValue) {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      @Override
      public Character putIfAbsent(Character key, Character value) {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      @Override
      public Character computeIfAbsent(Character key, Function<? super Character, ? extends Character> mappingFunction) {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      @Override
      public Character computeIfPresent(Character key, BiFunction<? super Character, ? super Character, ? extends Character> remappingFunction) {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      @Override
      public Character compute(Character key, BiFunction<? super Character, ? super Character, ? extends Character> remappingFunction) {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      @Override
      public Character merge(Character key, Character value, BiFunction<? super Character, ? super Character, ? extends Character> remappingFunction) {
         throw new UnsupportedOperationException();
      }
   }
}
