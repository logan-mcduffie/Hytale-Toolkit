package it.unimi.dsi.fastutil.longs;

import it.unimi.dsi.fastutil.ints.IntCollection;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.IntBinaryOperator;
import java.util.function.LongFunction;
import java.util.function.LongToIntFunction;

public interface Long2IntMap extends Long2IntFunction, Map<Long, Integer> {
   @Override
   int size();

   @Override
   default void clear() {
      throw new UnsupportedOperationException();
   }

   @Override
   void defaultReturnValue(int var1);

   @Override
   int defaultReturnValue();

   ObjectSet<Long2IntMap.Entry> long2IntEntrySet();

   @Deprecated
   default ObjectSet<java.util.Map.Entry<Long, Integer>> entrySet() {
      return this.long2IntEntrySet();
   }

   @Deprecated
   @Override
   default Integer put(Long key, Integer value) {
      return Long2IntFunction.super.put(key, value);
   }

   @Deprecated
   @Override
   default Integer get(Object key) {
      return Long2IntFunction.super.get(key);
   }

   @Deprecated
   @Override
   default Integer remove(Object key) {
      return Long2IntFunction.super.remove(key);
   }

   LongSet keySet();

   IntCollection values();

   @Override
   boolean containsKey(long var1);

   @Deprecated
   @Override
   default boolean containsKey(Object key) {
      return Long2IntFunction.super.containsKey(key);
   }

   boolean containsValue(int var1);

   @Deprecated
   @Override
   default boolean containsValue(Object value) {
      return value == null ? false : this.containsValue(((Integer)value).intValue());
   }

   @Override
   default void forEach(BiConsumer<? super Long, ? super Integer> consumer) {
      ObjectSet<Long2IntMap.Entry> entrySet = this.long2IntEntrySet();
      Consumer<Long2IntMap.Entry> wrappingConsumer = entry -> consumer.accept(entry.getLongKey(), entry.getIntValue());
      if (entrySet instanceof Long2IntMap.FastEntrySet) {
         ((Long2IntMap.FastEntrySet)entrySet).fastForEach(wrappingConsumer);
      } else {
         entrySet.forEach(wrappingConsumer);
      }
   }

   @Override
   default int getOrDefault(long key, int defaultValue) {
      int v;
      return (v = this.get(key)) == this.defaultReturnValue() && !this.containsKey(key) ? defaultValue : v;
   }

   @Deprecated
   @Override
   default Integer getOrDefault(Object key, Integer defaultValue) {
      return Map.super.getOrDefault(key, defaultValue);
   }

   default int putIfAbsent(long key, int value) {
      int v = this.get(key);
      int drv = this.defaultReturnValue();
      if (v == drv && !this.containsKey(key)) {
         this.put(key, value);
         return drv;
      } else {
         return v;
      }
   }

   default boolean remove(long key, int value) {
      int curValue = this.get(key);
      if (curValue == value && (curValue != this.defaultReturnValue() || this.containsKey(key))) {
         this.remove(key);
         return true;
      } else {
         return false;
      }
   }

   default boolean replace(long key, int oldValue, int newValue) {
      int curValue = this.get(key);
      if (curValue == oldValue && (curValue != this.defaultReturnValue() || this.containsKey(key))) {
         this.put(key, newValue);
         return true;
      } else {
         return false;
      }
   }

   default int replace(long key, int value) {
      return this.containsKey(key) ? this.put(key, value) : this.defaultReturnValue();
   }

   default int computeIfAbsent(long key, LongToIntFunction mappingFunction) {
      Objects.requireNonNull(mappingFunction);
      int v = this.get(key);
      if (v == this.defaultReturnValue() && !this.containsKey(key)) {
         int newValue = mappingFunction.applyAsInt(key);
         this.put(key, newValue);
         return newValue;
      } else {
         return v;
      }
   }

   default int computeIfAbsentNullable(long key, LongFunction<? extends Integer> mappingFunction) {
      Objects.requireNonNull(mappingFunction);
      int v = this.get(key);
      int drv = this.defaultReturnValue();
      if (v == drv && !this.containsKey(key)) {
         Integer mappedValue = mappingFunction.apply(key);
         if (mappedValue == null) {
            return drv;
         } else {
            int newValue = mappedValue;
            this.put(key, newValue);
            return newValue;
         }
      } else {
         return v;
      }
   }

   default int computeIfAbsent(long key, Long2IntFunction mappingFunction) {
      Objects.requireNonNull(mappingFunction);
      int v = this.get(key);
      int drv = this.defaultReturnValue();
      if (v != drv || this.containsKey(key)) {
         return v;
      } else if (!mappingFunction.containsKey(key)) {
         return drv;
      } else {
         int newValue = mappingFunction.get(key);
         this.put(key, newValue);
         return newValue;
      }
   }

   @Deprecated
   default int computeIfAbsentPartial(long key, Long2IntFunction mappingFunction) {
      return this.computeIfAbsent(key, mappingFunction);
   }

   default int computeIfPresent(long key, BiFunction<? super Long, ? super Integer, ? extends Integer> remappingFunction) {
      Objects.requireNonNull(remappingFunction);
      int oldValue = this.get(key);
      int drv = this.defaultReturnValue();
      if (oldValue == drv && !this.containsKey(key)) {
         return drv;
      } else {
         Integer newValue = remappingFunction.apply(key, oldValue);
         if (newValue == null) {
            this.remove(key);
            return drv;
         } else {
            int newVal = newValue;
            this.put(key, newVal);
            return newVal;
         }
      }
   }

   default int compute(long key, BiFunction<? super Long, ? super Integer, ? extends Integer> remappingFunction) {
      Objects.requireNonNull(remappingFunction);
      int oldValue = this.get(key);
      int drv = this.defaultReturnValue();
      boolean contained = oldValue != drv || this.containsKey(key);
      Integer newValue = remappingFunction.apply(key, contained ? oldValue : null);
      if (newValue == null) {
         if (contained) {
            this.remove(key);
         }

         return drv;
      } else {
         int newVal = newValue;
         this.put(key, newVal);
         return newVal;
      }
   }

   default int merge(long key, int value, BiFunction<? super Integer, ? super Integer, ? extends Integer> remappingFunction) {
      Objects.requireNonNull(remappingFunction);
      int oldValue = this.get(key);
      int drv = this.defaultReturnValue();
      int newValue;
      if (oldValue == drv && !this.containsKey(key)) {
         newValue = value;
      } else {
         Integer mergedValue = remappingFunction.apply(oldValue, value);
         if (mergedValue == null) {
            this.remove(key);
            return drv;
         }

         newValue = mergedValue;
      }

      this.put(key, newValue);
      return newValue;
   }

   default int mergeInt(long key, int value, IntBinaryOperator remappingFunction) {
      Objects.requireNonNull(remappingFunction);
      int oldValue = this.get(key);
      int drv = this.defaultReturnValue();
      int newValue = oldValue == drv && !this.containsKey(key) ? value : remappingFunction.applyAsInt(oldValue, value);
      this.put(key, newValue);
      return newValue;
   }

   default int mergeInt(long key, int value, it.unimi.dsi.fastutil.ints.IntBinaryOperator remappingFunction) {
      return this.mergeInt(key, value, (IntBinaryOperator)remappingFunction);
   }

   @Deprecated
   default Integer putIfAbsent(Long key, Integer value) {
      return Map.super.putIfAbsent(key, value);
   }

   @Deprecated
   @Override
   default boolean remove(Object key, Object value) {
      return Map.super.remove(key, value);
   }

   @Deprecated
   default boolean replace(Long key, Integer oldValue, Integer newValue) {
      return Map.super.replace(key, oldValue, newValue);
   }

   @Deprecated
   default Integer replace(Long key, Integer value) {
      return Map.super.replace(key, value);
   }

   @Deprecated
   default Integer computeIfAbsent(Long key, Function<? super Long, ? extends Integer> mappingFunction) {
      return Map.super.computeIfAbsent(key, mappingFunction);
   }

   @Deprecated
   default Integer computeIfPresent(Long key, BiFunction<? super Long, ? super Integer, ? extends Integer> remappingFunction) {
      return Map.super.computeIfPresent(key, remappingFunction);
   }

   @Deprecated
   default Integer compute(Long key, BiFunction<? super Long, ? super Integer, ? extends Integer> remappingFunction) {
      return Map.super.compute(key, remappingFunction);
   }

   @Deprecated
   default Integer merge(Long key, Integer value, BiFunction<? super Integer, ? super Integer, ? extends Integer> remappingFunction) {
      return Map.super.merge(key, value, remappingFunction);
   }

   public interface Entry extends java.util.Map.Entry<Long, Integer> {
      long getLongKey();

      @Deprecated
      default Long getKey() {
         return this.getLongKey();
      }

      int getIntValue();

      int setValue(int var1);

      @Deprecated
      default Integer getValue() {
         return this.getIntValue();
      }

      @Deprecated
      default Integer setValue(Integer value) {
         return this.setValue(value.intValue());
      }
   }

   public interface FastEntrySet extends ObjectSet<Long2IntMap.Entry> {
      ObjectIterator<Long2IntMap.Entry> fastIterator();

      default void fastForEach(Consumer<? super Long2IntMap.Entry> consumer) {
         this.forEach(consumer);
      }
   }
}
