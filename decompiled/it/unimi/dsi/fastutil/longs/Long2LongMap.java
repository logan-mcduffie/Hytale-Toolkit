package it.unimi.dsi.fastutil.longs;

import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.LongFunction;

public interface Long2LongMap extends Long2LongFunction, Map<Long, Long> {
   @Override
   int size();

   @Override
   default void clear() {
      throw new UnsupportedOperationException();
   }

   @Override
   void defaultReturnValue(long var1);

   @Override
   long defaultReturnValue();

   ObjectSet<Long2LongMap.Entry> long2LongEntrySet();

   @Deprecated
   default ObjectSet<java.util.Map.Entry<Long, Long>> entrySet() {
      return this.long2LongEntrySet();
   }

   @Deprecated
   @Override
   default Long put(Long key, Long value) {
      return Long2LongFunction.super.put(key, value);
   }

   @Deprecated
   @Override
   default Long get(Object key) {
      return Long2LongFunction.super.get(key);
   }

   @Deprecated
   @Override
   default Long remove(Object key) {
      return Long2LongFunction.super.remove(key);
   }

   LongSet keySet();

   LongCollection values();

   @Override
   boolean containsKey(long var1);

   @Deprecated
   @Override
   default boolean containsKey(Object key) {
      return Long2LongFunction.super.containsKey(key);
   }

   boolean containsValue(long var1);

   @Deprecated
   @Override
   default boolean containsValue(Object value) {
      return value == null ? false : this.containsValue(((Long)value).longValue());
   }

   @Override
   default void forEach(BiConsumer<? super Long, ? super Long> consumer) {
      ObjectSet<Long2LongMap.Entry> entrySet = this.long2LongEntrySet();
      Consumer<Long2LongMap.Entry> wrappingConsumer = entry -> consumer.accept(entry.getLongKey(), entry.getLongValue());
      if (entrySet instanceof Long2LongMap.FastEntrySet) {
         ((Long2LongMap.FastEntrySet)entrySet).fastForEach(wrappingConsumer);
      } else {
         entrySet.forEach(wrappingConsumer);
      }
   }

   @Override
   default long getOrDefault(long key, long defaultValue) {
      long v;
      return (v = this.get(key)) == this.defaultReturnValue() && !this.containsKey(key) ? defaultValue : v;
   }

   @Deprecated
   @Override
   default Long getOrDefault(Object key, Long defaultValue) {
      return Map.super.getOrDefault(key, defaultValue);
   }

   default long putIfAbsent(long key, long value) {
      long v = this.get(key);
      long drv = this.defaultReturnValue();
      if (v == drv && !this.containsKey(key)) {
         this.put(key, value);
         return drv;
      } else {
         return v;
      }
   }

   default boolean remove(long key, long value) {
      long curValue = this.get(key);
      if (curValue == value && (curValue != this.defaultReturnValue() || this.containsKey(key))) {
         this.remove(key);
         return true;
      } else {
         return false;
      }
   }

   default boolean replace(long key, long oldValue, long newValue) {
      long curValue = this.get(key);
      if (curValue == oldValue && (curValue != this.defaultReturnValue() || this.containsKey(key))) {
         this.put(key, newValue);
         return true;
      } else {
         return false;
      }
   }

   default long replace(long key, long value) {
      return this.containsKey(key) ? this.put(key, value) : this.defaultReturnValue();
   }

   default long computeIfAbsent(long key, java.util.function.LongUnaryOperator mappingFunction) {
      Objects.requireNonNull(mappingFunction);
      long v = this.get(key);
      if (v == this.defaultReturnValue() && !this.containsKey(key)) {
         long newValue = mappingFunction.applyAsLong(key);
         this.put(key, newValue);
         return newValue;
      } else {
         return v;
      }
   }

   default long computeIfAbsentNullable(long key, LongFunction<? extends Long> mappingFunction) {
      Objects.requireNonNull(mappingFunction);
      long v = this.get(key);
      long drv = this.defaultReturnValue();
      if (v == drv && !this.containsKey(key)) {
         Long mappedValue = mappingFunction.apply(key);
         if (mappedValue == null) {
            return drv;
         } else {
            long newValue = mappedValue;
            this.put(key, newValue);
            return newValue;
         }
      } else {
         return v;
      }
   }

   default long computeIfAbsent(long key, Long2LongFunction mappingFunction) {
      Objects.requireNonNull(mappingFunction);
      long v = this.get(key);
      long drv = this.defaultReturnValue();
      if (v != drv || this.containsKey(key)) {
         return v;
      } else if (!mappingFunction.containsKey(key)) {
         return drv;
      } else {
         long newValue = mappingFunction.get(key);
         this.put(key, newValue);
         return newValue;
      }
   }

   @Deprecated
   default long computeIfAbsentPartial(long key, Long2LongFunction mappingFunction) {
      return this.computeIfAbsent(key, mappingFunction);
   }

   default long computeIfPresent(long key, BiFunction<? super Long, ? super Long, ? extends Long> remappingFunction) {
      Objects.requireNonNull(remappingFunction);
      long oldValue = this.get(key);
      long drv = this.defaultReturnValue();
      if (oldValue == drv && !this.containsKey(key)) {
         return drv;
      } else {
         Long newValue = remappingFunction.apply(key, oldValue);
         if (newValue == null) {
            this.remove(key);
            return drv;
         } else {
            long newVal = newValue;
            this.put(key, newVal);
            return newVal;
         }
      }
   }

   default long compute(long key, BiFunction<? super Long, ? super Long, ? extends Long> remappingFunction) {
      Objects.requireNonNull(remappingFunction);
      long oldValue = this.get(key);
      long drv = this.defaultReturnValue();
      boolean contained = oldValue != drv || this.containsKey(key);
      Long newValue = remappingFunction.apply(key, contained ? oldValue : null);
      if (newValue == null) {
         if (contained) {
            this.remove(key);
         }

         return drv;
      } else {
         long newVal = newValue;
         this.put(key, newVal);
         return newVal;
      }
   }

   default long merge(long key, long value, BiFunction<? super Long, ? super Long, ? extends Long> remappingFunction) {
      Objects.requireNonNull(remappingFunction);
      long oldValue = this.get(key);
      long drv = this.defaultReturnValue();
      long newValue;
      if (oldValue == drv && !this.containsKey(key)) {
         newValue = value;
      } else {
         Long mergedValue = remappingFunction.apply(oldValue, value);
         if (mergedValue == null) {
            this.remove(key);
            return drv;
         }

         newValue = mergedValue;
      }

      this.put(key, newValue);
      return newValue;
   }

   default long mergeLong(long key, long value, java.util.function.LongBinaryOperator remappingFunction) {
      Objects.requireNonNull(remappingFunction);
      long oldValue = this.get(key);
      long drv = this.defaultReturnValue();
      long newValue = oldValue == drv && !this.containsKey(key) ? value : remappingFunction.applyAsLong(oldValue, value);
      this.put(key, newValue);
      return newValue;
   }

   default long mergeLong(long key, long value, LongBinaryOperator remappingFunction) {
      return this.mergeLong(key, value, (java.util.function.LongBinaryOperator)remappingFunction);
   }

   @Deprecated
   default Long putIfAbsent(Long key, Long value) {
      return Map.super.putIfAbsent(key, value);
   }

   @Deprecated
   @Override
   default boolean remove(Object key, Object value) {
      return Map.super.remove(key, value);
   }

   @Deprecated
   default boolean replace(Long key, Long oldValue, Long newValue) {
      return Map.super.replace(key, oldValue, newValue);
   }

   @Deprecated
   default Long replace(Long key, Long value) {
      return Map.super.replace(key, value);
   }

   @Deprecated
   default Long computeIfAbsent(Long key, Function<? super Long, ? extends Long> mappingFunction) {
      return Map.super.computeIfAbsent(key, mappingFunction);
   }

   @Deprecated
   default Long computeIfPresent(Long key, BiFunction<? super Long, ? super Long, ? extends Long> remappingFunction) {
      return Map.super.computeIfPresent(key, remappingFunction);
   }

   @Deprecated
   default Long compute(Long key, BiFunction<? super Long, ? super Long, ? extends Long> remappingFunction) {
      return Map.super.compute(key, remappingFunction);
   }

   @Deprecated
   default Long merge(Long key, Long value, BiFunction<? super Long, ? super Long, ? extends Long> remappingFunction) {
      return Map.super.merge(key, value, remappingFunction);
   }

   public interface Entry extends java.util.Map.Entry<Long, Long> {
      long getLongKey();

      @Deprecated
      default Long getKey() {
         return this.getLongKey();
      }

      long getLongValue();

      long setValue(long var1);

      @Deprecated
      default Long getValue() {
         return this.getLongValue();
      }

      @Deprecated
      default Long setValue(Long value) {
         return this.setValue(value.longValue());
      }
   }

   public interface FastEntrySet extends ObjectSet<Long2LongMap.Entry> {
      ObjectIterator<Long2LongMap.Entry> fastIterator();

      default void fastForEach(Consumer<? super Long2LongMap.Entry> consumer) {
         this.forEach(consumer);
      }
   }
}
