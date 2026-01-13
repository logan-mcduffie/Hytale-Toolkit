package it.unimi.dsi.fastutil.chars;

import it.unimi.dsi.fastutil.HashCommon;
import it.unimi.dsi.fastutil.doubles.DoubleCollection;
import it.unimi.dsi.fastutil.doubles.DoubleCollections;
import it.unimi.dsi.fastutil.doubles.DoubleSets;
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
import java.util.function.IntToDoubleFunction;

public final class Char2DoubleMaps {
   public static final Char2DoubleMaps.EmptyMap EMPTY_MAP = new Char2DoubleMaps.EmptyMap();

   private Char2DoubleMaps() {
   }

   public static ObjectIterator<Char2DoubleMap.Entry> fastIterator(Char2DoubleMap map) {
      ObjectSet<Char2DoubleMap.Entry> entries = map.char2DoubleEntrySet();
      return entries instanceof Char2DoubleMap.FastEntrySet ? ((Char2DoubleMap.FastEntrySet)entries).fastIterator() : entries.iterator();
   }

   public static void fastForEach(Char2DoubleMap map, Consumer<? super Char2DoubleMap.Entry> consumer) {
      ObjectSet<Char2DoubleMap.Entry> entries = map.char2DoubleEntrySet();
      if (entries instanceof Char2DoubleMap.FastEntrySet) {
         ((Char2DoubleMap.FastEntrySet)entries).fastForEach(consumer);
      } else {
         entries.forEach(consumer);
      }
   }

   public static ObjectIterable<Char2DoubleMap.Entry> fastIterable(Char2DoubleMap map) {
      final ObjectSet<Char2DoubleMap.Entry> entries = map.char2DoubleEntrySet();
      return (ObjectIterable<Char2DoubleMap.Entry>)(entries instanceof Char2DoubleMap.FastEntrySet ? new ObjectIterable<Char2DoubleMap.Entry>() {
         @Override
         public ObjectIterator<Char2DoubleMap.Entry> iterator() {
            return ((Char2DoubleMap.FastEntrySet)entries).fastIterator();
         }

         @Override
         public ObjectSpliterator<Char2DoubleMap.Entry> spliterator() {
            return entries.spliterator();
         }

         @Override
         public void forEach(Consumer<? super Char2DoubleMap.Entry> consumer) {
            ((Char2DoubleMap.FastEntrySet)entries).fastForEach(consumer);
         }
      } : entries);
   }

   public static Char2DoubleMap singleton(char key, double value) {
      return new Char2DoubleMaps.Singleton(key, value);
   }

   public static Char2DoubleMap singleton(Character key, Double value) {
      return new Char2DoubleMaps.Singleton(key, value);
   }

   public static Char2DoubleMap synchronize(Char2DoubleMap m) {
      return new Char2DoubleMaps.SynchronizedMap(m);
   }

   public static Char2DoubleMap synchronize(Char2DoubleMap m, Object sync) {
      return new Char2DoubleMaps.SynchronizedMap(m, sync);
   }

   public static Char2DoubleMap unmodifiable(Char2DoubleMap m) {
      return new Char2DoubleMaps.UnmodifiableMap(m);
   }

   public static class EmptyMap extends Char2DoubleFunctions.EmptyFunction implements Char2DoubleMap, Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;

      protected EmptyMap() {
      }

      @Override
      public boolean containsValue(double v) {
         return false;
      }

      @Deprecated
      @Override
      public Double getOrDefault(Object key, Double defaultValue) {
         return defaultValue;
      }

      @Override
      public double getOrDefault(char key, double defaultValue) {
         return defaultValue;
      }

      @Deprecated
      @Override
      public boolean containsValue(Object ov) {
         return false;
      }

      @Override
      public void putAll(Map<? extends Character, ? extends Double> m) {
         throw new UnsupportedOperationException();
      }

      @Override
      public ObjectSet<Char2DoubleMap.Entry> char2DoubleEntrySet() {
         return ObjectSets.EMPTY_SET;
      }

      @Override
      public CharSet keySet() {
         return CharSets.EMPTY_SET;
      }

      @Override
      public DoubleCollection values() {
         return DoubleSets.EMPTY_SET;
      }

      @Override
      public void forEach(BiConsumer<? super Character, ? super Double> consumer) {
      }

      @Override
      public Object clone() {
         return Char2DoubleMaps.EMPTY_MAP;
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

   public static class Singleton extends Char2DoubleFunctions.Singleton implements Char2DoubleMap, Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected transient ObjectSet<Char2DoubleMap.Entry> entries;
      protected transient CharSet keys;
      protected transient DoubleCollection values;

      protected Singleton(char key, double value) {
         super(key, value);
      }

      @Override
      public boolean containsValue(double v) {
         return Double.doubleToLongBits(this.value) == Double.doubleToLongBits(v);
      }

      @Deprecated
      @Override
      public boolean containsValue(Object ov) {
         return Double.doubleToLongBits((Double)ov) == Double.doubleToLongBits(this.value);
      }

      @Override
      public void putAll(Map<? extends Character, ? extends Double> m) {
         throw new UnsupportedOperationException();
      }

      @Override
      public ObjectSet<Char2DoubleMap.Entry> char2DoubleEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSets.singleton(new AbstractChar2DoubleMap.BasicEntry(this.key, this.value));
         }

         return this.entries;
      }

      @Deprecated
      @Override
      public ObjectSet<Entry<Character, Double>> entrySet() {
         return this.char2DoubleEntrySet();
      }

      @Override
      public CharSet keySet() {
         if (this.keys == null) {
            this.keys = CharSets.singleton(this.key);
         }

         return this.keys;
      }

      @Override
      public DoubleCollection values() {
         if (this.values == null) {
            this.values = DoubleSets.singleton(this.value);
         }

         return this.values;
      }

      @Override
      public boolean isEmpty() {
         return false;
      }

      @Override
      public int hashCode() {
         return this.key ^ HashCommon.double2int(this.value);
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

   public static class SynchronizedMap extends Char2DoubleFunctions.SynchronizedFunction implements Char2DoubleMap, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Char2DoubleMap map;
      protected transient ObjectSet<Char2DoubleMap.Entry> entries;
      protected transient CharSet keys;
      protected transient DoubleCollection values;

      protected SynchronizedMap(Char2DoubleMap m, Object sync) {
         super(m, sync);
         this.map = m;
      }

      protected SynchronizedMap(Char2DoubleMap m) {
         super(m);
         this.map = m;
      }

      @Override
      public boolean containsValue(double v) {
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
      public void putAll(Map<? extends Character, ? extends Double> m) {
         synchronized (this.sync) {
            this.map.putAll(m);
         }
      }

      @Override
      public ObjectSet<Char2DoubleMap.Entry> char2DoubleEntrySet() {
         synchronized (this.sync) {
            if (this.entries == null) {
               this.entries = ObjectSets.synchronize(this.map.char2DoubleEntrySet(), this.sync);
            }

            return this.entries;
         }
      }

      @Deprecated
      @Override
      public ObjectSet<Entry<Character, Double>> entrySet() {
         return this.char2DoubleEntrySet();
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
      public DoubleCollection values() {
         synchronized (this.sync) {
            if (this.values == null) {
               this.values = DoubleCollections.synchronize(this.map.values(), this.sync);
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
      public double getOrDefault(char key, double defaultValue) {
         synchronized (this.sync) {
            return this.map.getOrDefault(key, defaultValue);
         }
      }

      @Override
      public void forEach(BiConsumer<? super Character, ? super Double> action) {
         synchronized (this.sync) {
            this.map.forEach(action);
         }
      }

      @Override
      public void replaceAll(BiFunction<? super Character, ? super Double, ? extends Double> function) {
         synchronized (this.sync) {
            this.map.replaceAll(function);
         }
      }

      @Override
      public double putIfAbsent(char key, double value) {
         synchronized (this.sync) {
            return this.map.putIfAbsent(key, value);
         }
      }

      @Override
      public boolean remove(char key, double value) {
         synchronized (this.sync) {
            return this.map.remove(key, value);
         }
      }

      @Override
      public double replace(char key, double value) {
         synchronized (this.sync) {
            return this.map.replace(key, value);
         }
      }

      @Override
      public boolean replace(char key, double oldValue, double newValue) {
         synchronized (this.sync) {
            return this.map.replace(key, oldValue, newValue);
         }
      }

      @Override
      public double computeIfAbsent(char key, IntToDoubleFunction mappingFunction) {
         synchronized (this.sync) {
            return this.map.computeIfAbsent(key, mappingFunction);
         }
      }

      @Override
      public double computeIfAbsentNullable(char key, IntFunction<? extends Double> mappingFunction) {
         synchronized (this.sync) {
            return this.map.computeIfAbsentNullable(key, mappingFunction);
         }
      }

      @Override
      public double computeIfAbsent(char key, Char2DoubleFunction mappingFunction) {
         synchronized (this.sync) {
            return this.map.computeIfAbsent(key, mappingFunction);
         }
      }

      @Override
      public double computeIfPresent(char key, BiFunction<? super Character, ? super Double, ? extends Double> remappingFunction) {
         synchronized (this.sync) {
            return this.map.computeIfPresent(key, remappingFunction);
         }
      }

      @Override
      public double compute(char key, BiFunction<? super Character, ? super Double, ? extends Double> remappingFunction) {
         synchronized (this.sync) {
            return this.map.compute(key, remappingFunction);
         }
      }

      @Override
      public double merge(char key, double value, BiFunction<? super Double, ? super Double, ? extends Double> remappingFunction) {
         synchronized (this.sync) {
            return this.map.merge(key, value, remappingFunction);
         }
      }

      @Deprecated
      @Override
      public Double getOrDefault(Object key, Double defaultValue) {
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
      public Double replace(Character key, Double value) {
         synchronized (this.sync) {
            return this.map.replace(key, value);
         }
      }

      @Deprecated
      @Override
      public boolean replace(Character key, Double oldValue, Double newValue) {
         synchronized (this.sync) {
            return this.map.replace(key, oldValue, newValue);
         }
      }

      @Deprecated
      @Override
      public Double putIfAbsent(Character key, Double value) {
         synchronized (this.sync) {
            return this.map.putIfAbsent(key, value);
         }
      }

      @Deprecated
      @Override
      public Double computeIfAbsent(Character key, Function<? super Character, ? extends Double> mappingFunction) {
         synchronized (this.sync) {
            return this.map.computeIfAbsent(key, mappingFunction);
         }
      }

      @Deprecated
      @Override
      public Double computeIfPresent(Character key, BiFunction<? super Character, ? super Double, ? extends Double> remappingFunction) {
         synchronized (this.sync) {
            return this.map.computeIfPresent(key, remappingFunction);
         }
      }

      @Deprecated
      @Override
      public Double compute(Character key, BiFunction<? super Character, ? super Double, ? extends Double> remappingFunction) {
         synchronized (this.sync) {
            return this.map.compute(key, remappingFunction);
         }
      }

      @Deprecated
      @Override
      public Double merge(Character key, Double value, BiFunction<? super Double, ? super Double, ? extends Double> remappingFunction) {
         synchronized (this.sync) {
            return this.map.merge(key, value, remappingFunction);
         }
      }
   }

   public static class UnmodifiableMap extends Char2DoubleFunctions.UnmodifiableFunction implements Char2DoubleMap, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Char2DoubleMap map;
      protected transient ObjectSet<Char2DoubleMap.Entry> entries;
      protected transient CharSet keys;
      protected transient DoubleCollection values;

      protected UnmodifiableMap(Char2DoubleMap m) {
         super(m);
         this.map = m;
      }

      @Override
      public boolean containsValue(double v) {
         return this.map.containsValue(v);
      }

      @Deprecated
      @Override
      public boolean containsValue(Object ov) {
         return this.map.containsValue(ov);
      }

      @Override
      public void putAll(Map<? extends Character, ? extends Double> m) {
         throw new UnsupportedOperationException();
      }

      @Override
      public ObjectSet<Char2DoubleMap.Entry> char2DoubleEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSets.unmodifiable(this.map.char2DoubleEntrySet());
         }

         return this.entries;
      }

      @Deprecated
      @Override
      public ObjectSet<Entry<Character, Double>> entrySet() {
         return this.char2DoubleEntrySet();
      }

      @Override
      public CharSet keySet() {
         if (this.keys == null) {
            this.keys = CharSets.unmodifiable(this.map.keySet());
         }

         return this.keys;
      }

      @Override
      public DoubleCollection values() {
         if (this.values == null) {
            this.values = DoubleCollections.unmodifiable(this.map.values());
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
      public double getOrDefault(char key, double defaultValue) {
         return this.map.getOrDefault(key, defaultValue);
      }

      @Override
      public void forEach(BiConsumer<? super Character, ? super Double> action) {
         this.map.forEach(action);
      }

      @Override
      public void replaceAll(BiFunction<? super Character, ? super Double, ? extends Double> function) {
         throw new UnsupportedOperationException();
      }

      @Override
      public double putIfAbsent(char key, double value) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean remove(char key, double value) {
         throw new UnsupportedOperationException();
      }

      @Override
      public double replace(char key, double value) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean replace(char key, double oldValue, double newValue) {
         throw new UnsupportedOperationException();
      }

      @Override
      public double computeIfAbsent(char key, IntToDoubleFunction mappingFunction) {
         throw new UnsupportedOperationException();
      }

      @Override
      public double computeIfAbsentNullable(char key, IntFunction<? extends Double> mappingFunction) {
         throw new UnsupportedOperationException();
      }

      @Override
      public double computeIfAbsent(char key, Char2DoubleFunction mappingFunction) {
         throw new UnsupportedOperationException();
      }

      @Override
      public double computeIfPresent(char key, BiFunction<? super Character, ? super Double, ? extends Double> remappingFunction) {
         throw new UnsupportedOperationException();
      }

      @Override
      public double compute(char key, BiFunction<? super Character, ? super Double, ? extends Double> remappingFunction) {
         throw new UnsupportedOperationException();
      }

      @Override
      public double merge(char key, double value, BiFunction<? super Double, ? super Double, ? extends Double> remappingFunction) {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      @Override
      public Double getOrDefault(Object key, Double defaultValue) {
         return this.map.getOrDefault(key, defaultValue);
      }

      @Deprecated
      @Override
      public boolean remove(Object key, Object value) {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      @Override
      public Double replace(Character key, Double value) {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      @Override
      public boolean replace(Character key, Double oldValue, Double newValue) {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      @Override
      public Double putIfAbsent(Character key, Double value) {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      @Override
      public Double computeIfAbsent(Character key, Function<? super Character, ? extends Double> mappingFunction) {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      @Override
      public Double computeIfPresent(Character key, BiFunction<? super Character, ? super Double, ? extends Double> remappingFunction) {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      @Override
      public Double compute(Character key, BiFunction<? super Character, ? super Double, ? extends Double> remappingFunction) {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      @Override
      public Double merge(Character key, Double value, BiFunction<? super Double, ? super Double, ? extends Double> remappingFunction) {
         throw new UnsupportedOperationException();
      }
   }
}
