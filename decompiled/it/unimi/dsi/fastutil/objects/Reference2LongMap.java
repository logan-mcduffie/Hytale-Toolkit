package it.unimi.dsi.fastutil.objects;

import it.unimi.dsi.fastutil.longs.LongCollection;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.LongBinaryOperator;
import java.util.function.ToLongFunction;

public interface Reference2LongMap<K> extends Reference2LongFunction<K>, Map<K, Long> {
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

   ObjectSet<Reference2LongMap.Entry<K>> reference2LongEntrySet();

   @Deprecated
   default ObjectSet<java.util.Map.Entry<K, Long>> entrySet() {
      return this.reference2LongEntrySet();
   }

   @Deprecated
   @Override
   default Long put(K key, Long value) {
      return Reference2LongFunction.super.put(key, value);
   }

   @Deprecated
   @Override
   default Long get(Object key) {
      return Reference2LongFunction.super.get(key);
   }

   @Deprecated
   @Override
   default Long remove(Object key) {
      return Reference2LongFunction.super.remove(key);
   }

   ReferenceSet<K> keySet();

   LongCollection values();

   @Override
   boolean containsKey(Object var1);

   boolean containsValue(long var1);

   @Deprecated
   @Override
   default boolean containsValue(Object value) {
      return value == null ? false : this.containsValue(((Long)value).longValue());
   }

   @Override
   default void forEach(BiConsumer<? super K, ? super Long> consumer) {
      ObjectSet<Reference2LongMap.Entry<K>> entrySet = this.reference2LongEntrySet();
      Consumer<Reference2LongMap.Entry<K>> wrappingConsumer = entry -> consumer.accept(entry.getKey(), entry.getLongValue());
      if (entrySet instanceof Reference2LongMap.FastEntrySet) {
         ((Reference2LongMap.FastEntrySet)entrySet).fastForEach(wrappingConsumer);
      } else {
         entrySet.forEach(wrappingConsumer);
      }
   }

   @Override
   default long getOrDefault(Object key, long defaultValue) {
      long v;
      return (v = this.getLong(key)) == this.defaultReturnValue() && !this.containsKey(key) ? defaultValue : v;
   }

   @Deprecated
   @Override
   default Long getOrDefault(Object key, Long defaultValue) {
      return Map.super.getOrDefault(key, defaultValue);
   }

   default long putIfAbsent(K key, long value) {
      long v = this.getLong(key);
      long drv = this.defaultReturnValue();
      if (v == drv && !this.containsKey(key)) {
         this.put(key, value);
         return drv;
      } else {
         return v;
      }
   }

   default boolean remove(Object key, long value) {
      long curValue = this.getLong(key);
      if (curValue == value && (curValue != this.defaultReturnValue() || this.containsKey(key))) {
         this.removeLong(key);
         return true;
      } else {
         return false;
      }
   }

   default boolean replace(K key, long oldValue, long newValue) {
      long curValue = this.getLong(key);
      if (curValue == oldValue && (curValue != this.defaultReturnValue() || this.containsKey(key))) {
         this.put(key, newValue);
         return true;
      } else {
         return false;
      }
   }

   default long replace(K key, long value) {
      return this.containsKey(key) ? this.put(key, value) : this.defaultReturnValue();
   }

   default long computeIfAbsent(K key, ToLongFunction<? super K> mappingFunction) {
      Objects.requireNonNull(mappingFunction);
      long v = this.getLong(key);
      if (v == this.defaultReturnValue() && !this.containsKey(key)) {
         long newValue = mappingFunction.applyAsLong(key);
         this.put(key, newValue);
         return newValue;
      } else {
         return v;
      }
   }

   @Deprecated
   default long computeLongIfAbsent(K key, ToLongFunction<? super K> mappingFunction) {
      return this.computeIfAbsent(key, mappingFunction);
   }

   default long computeIfAbsent(K key, Reference2LongFunction<? super K> mappingFunction) {
      Objects.requireNonNull(mappingFunction);
      long v = this.getLong(key);
      long drv = this.defaultReturnValue();
      if (v != drv || this.containsKey(key)) {
         return v;
      } else if (!mappingFunction.containsKey(key)) {
         return drv;
      } else {
         long newValue = mappingFunction.getLong(key);
         this.put(key, newValue);
         return newValue;
      }
   }

   @Deprecated
   default long computeLongIfAbsentPartial(K key, Reference2LongFunction<? super K> mappingFunction) {
      return this.computeIfAbsent(key, mappingFunction);
   }

   default long computeLongIfPresent(K key, BiFunction<? super K, ? super Long, ? extends Long> remappingFunction) {
      Objects.requireNonNull(remappingFunction);
      long oldValue = this.getLong(key);
      long drv = this.defaultReturnValue();
      if (oldValue == drv && !this.containsKey(key)) {
         return drv;
      } else {
         Long newValue = remappingFunction.apply(key, oldValue);
         if (newValue == null) {
            this.removeLong(key);
            return drv;
         } else {
            long newVal = newValue;
            this.put(key, newVal);
            return newVal;
         }
      }
   }

   default long computeLong(K key, BiFunction<? super K, ? super Long, ? extends Long> remappingFunction) {
      Objects.requireNonNull(remappingFunction);
      long oldValue = this.getLong(key);
      long drv = this.defaultReturnValue();
      boolean contained = oldValue != drv || this.containsKey(key);
      Long newValue = remappingFunction.apply(key, contained ? oldValue : null);
      if (newValue == null) {
         if (contained) {
            this.removeLong(key);
         }

         return drv;
      } else {
         long newVal = newValue;
         this.put(key, newVal);
         return newVal;
      }
   }

   default long merge(K key, long value, BiFunction<? super Long, ? super Long, ? extends Long> remappingFunction) {
      Objects.requireNonNull(remappingFunction);
      long oldValue = this.getLong(key);
      long drv = this.defaultReturnValue();
      long newValue;
      if (oldValue == drv && !this.containsKey(key)) {
         newValue = value;
      } else {
         Long mergedValue = remappingFunction.apply(oldValue, value);
         if (mergedValue == null) {
            this.removeLong(key);
            return drv;
         }

         newValue = mergedValue;
      }

      this.put(key, newValue);
      return newValue;
   }

   default long mergeLong(K key, long value, LongBinaryOperator remappingFunction) {
      Objects.requireNonNull(remappingFunction);
      long oldValue = this.getLong(key);
      long drv = this.defaultReturnValue();
      long newValue = oldValue == drv && !this.containsKey(key) ? value : remappingFunction.applyAsLong(oldValue, value);
      this.put(key, newValue);
      return newValue;
   }

   default long mergeLong(K key, long value, it.unimi.dsi.fastutil.longs.LongBinaryOperator remappingFunction) {
      return this.mergeLong(key, value, (LongBinaryOperator)remappingFunction);
   }

   @Deprecated
   default long mergeLong(K key, long value, BiFunction<? super Long, ? super Long, ? extends Long> remappingFunction) {
      return this.merge(key, value, remappingFunction);
   }

   @Deprecated
   default Long putIfAbsent(K key, Long value) {
      return Map.super.putIfAbsent(key, value);
   }

   @Deprecated
   @Override
   default boolean remove(Object key, Object value) {
      return Map.super.remove(key, value);
   }

   @Deprecated
   default boolean replace(K key, Long oldValue, Long newValue) {
      return Map.super.replace(key, oldValue, newValue);
   }

   @Deprecated
   default Long replace(K key, Long value) {
      return Map.super.replace(key, value);
   }

   @Deprecated
   default Long merge(K key, Long value, BiFunction<? super Long, ? super Long, ? extends Long> remappingFunction) {
      return Map.super.merge(key, value, remappingFunction);
   }

   public interface Entry<K> extends java.util.Map.Entry<K, Long> {
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

   public interface FastEntrySet<K> extends ObjectSet<Reference2LongMap.Entry<K>> {
      ObjectIterator<Reference2LongMap.Entry<K>> fastIterator();

      default void fastForEach(Consumer<? super Reference2LongMap.Entry<K>> consumer) {
         this.forEach(consumer);
      }
   }
}
