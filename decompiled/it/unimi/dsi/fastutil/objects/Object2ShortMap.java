package it.unimi.dsi.fastutil.objects;

import it.unimi.dsi.fastutil.SafeMath;
import it.unimi.dsi.fastutil.shorts.ShortBinaryOperator;
import it.unimi.dsi.fastutil.shorts.ShortCollection;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.IntBinaryOperator;
import java.util.function.ToIntFunction;

public interface Object2ShortMap<K> extends Object2ShortFunction<K>, Map<K, Short> {
   @Override
   int size();

   @Override
   default void clear() {
      throw new UnsupportedOperationException();
   }

   @Override
   void defaultReturnValue(short var1);

   @Override
   short defaultReturnValue();

   ObjectSet<Object2ShortMap.Entry<K>> object2ShortEntrySet();

   @Deprecated
   default ObjectSet<java.util.Map.Entry<K, Short>> entrySet() {
      return this.object2ShortEntrySet();
   }

   @Deprecated
   @Override
   default Short put(K key, Short value) {
      return Object2ShortFunction.super.put(key, value);
   }

   @Deprecated
   @Override
   default Short get(Object key) {
      return Object2ShortFunction.super.get(key);
   }

   @Deprecated
   @Override
   default Short remove(Object key) {
      return Object2ShortFunction.super.remove(key);
   }

   ObjectSet<K> keySet();

   ShortCollection values();

   @Override
   boolean containsKey(Object var1);

   boolean containsValue(short var1);

   @Deprecated
   @Override
   default boolean containsValue(Object value) {
      return value == null ? false : this.containsValue(((Short)value).shortValue());
   }

   @Override
   default void forEach(BiConsumer<? super K, ? super Short> consumer) {
      ObjectSet<Object2ShortMap.Entry<K>> entrySet = this.object2ShortEntrySet();
      Consumer<Object2ShortMap.Entry<K>> wrappingConsumer = entry -> consumer.accept(entry.getKey(), entry.getShortValue());
      if (entrySet instanceof Object2ShortMap.FastEntrySet) {
         ((Object2ShortMap.FastEntrySet)entrySet).fastForEach(wrappingConsumer);
      } else {
         entrySet.forEach(wrappingConsumer);
      }
   }

   @Override
   default short getOrDefault(Object key, short defaultValue) {
      short v;
      return (v = this.getShort(key)) == this.defaultReturnValue() && !this.containsKey(key) ? defaultValue : v;
   }

   @Deprecated
   @Override
   default Short getOrDefault(Object key, Short defaultValue) {
      return Map.super.getOrDefault(key, defaultValue);
   }

   default short putIfAbsent(K key, short value) {
      short v = this.getShort(key);
      short drv = this.defaultReturnValue();
      if (v == drv && !this.containsKey(key)) {
         this.put(key, value);
         return drv;
      } else {
         return v;
      }
   }

   default boolean remove(Object key, short value) {
      short curValue = this.getShort(key);
      if (curValue == value && (curValue != this.defaultReturnValue() || this.containsKey(key))) {
         this.removeShort(key);
         return true;
      } else {
         return false;
      }
   }

   default boolean replace(K key, short oldValue, short newValue) {
      short curValue = this.getShort(key);
      if (curValue == oldValue && (curValue != this.defaultReturnValue() || this.containsKey(key))) {
         this.put(key, newValue);
         return true;
      } else {
         return false;
      }
   }

   default short replace(K key, short value) {
      return this.containsKey(key) ? this.put(key, value) : this.defaultReturnValue();
   }

   default short computeIfAbsent(K key, ToIntFunction<? super K> mappingFunction) {
      Objects.requireNonNull(mappingFunction);
      short v = this.getShort(key);
      if (v == this.defaultReturnValue() && !this.containsKey(key)) {
         short newValue = SafeMath.safeIntToShort(mappingFunction.applyAsInt(key));
         this.put(key, newValue);
         return newValue;
      } else {
         return v;
      }
   }

   @Deprecated
   default short computeShortIfAbsent(K key, ToIntFunction<? super K> mappingFunction) {
      return this.computeIfAbsent(key, mappingFunction);
   }

   default short computeIfAbsent(K key, Object2ShortFunction<? super K> mappingFunction) {
      Objects.requireNonNull(mappingFunction);
      short v = this.getShort(key);
      short drv = this.defaultReturnValue();
      if (v != drv || this.containsKey(key)) {
         return v;
      } else if (!mappingFunction.containsKey(key)) {
         return drv;
      } else {
         short newValue = mappingFunction.getShort(key);
         this.put(key, newValue);
         return newValue;
      }
   }

   @Deprecated
   default short computeShortIfAbsentPartial(K key, Object2ShortFunction<? super K> mappingFunction) {
      return this.computeIfAbsent(key, mappingFunction);
   }

   default short computeShortIfPresent(K key, BiFunction<? super K, ? super Short, ? extends Short> remappingFunction) {
      Objects.requireNonNull(remappingFunction);
      short oldValue = this.getShort(key);
      short drv = this.defaultReturnValue();
      if (oldValue == drv && !this.containsKey(key)) {
         return drv;
      } else {
         Short newValue = remappingFunction.apply(key, oldValue);
         if (newValue == null) {
            this.removeShort(key);
            return drv;
         } else {
            short newVal = newValue;
            this.put(key, newVal);
            return newVal;
         }
      }
   }

   default short computeShort(K key, BiFunction<? super K, ? super Short, ? extends Short> remappingFunction) {
      Objects.requireNonNull(remappingFunction);
      short oldValue = this.getShort(key);
      short drv = this.defaultReturnValue();
      boolean contained = oldValue != drv || this.containsKey(key);
      Short newValue = remappingFunction.apply(key, contained ? oldValue : null);
      if (newValue == null) {
         if (contained) {
            this.removeShort(key);
         }

         return drv;
      } else {
         short newVal = newValue;
         this.put(key, newVal);
         return newVal;
      }
   }

   default short merge(K key, short value, BiFunction<? super Short, ? super Short, ? extends Short> remappingFunction) {
      Objects.requireNonNull(remappingFunction);
      short oldValue = this.getShort(key);
      short drv = this.defaultReturnValue();
      short newValue;
      if (oldValue == drv && !this.containsKey(key)) {
         newValue = value;
      } else {
         Short mergedValue = remappingFunction.apply(oldValue, value);
         if (mergedValue == null) {
            this.removeShort(key);
            return drv;
         }

         newValue = mergedValue;
      }

      this.put(key, newValue);
      return newValue;
   }

   default short mergeShort(K key, short value, ShortBinaryOperator remappingFunction) {
      Objects.requireNonNull(remappingFunction);
      short oldValue = this.getShort(key);
      short drv = this.defaultReturnValue();
      short newValue = oldValue == drv && !this.containsKey(key) ? value : remappingFunction.apply(oldValue, value);
      this.put(key, newValue);
      return newValue;
   }

   default short mergeShort(K key, short value, IntBinaryOperator remappingFunction) {
      return this.mergeShort(
         key,
         value,
         remappingFunction instanceof ShortBinaryOperator
            ? (ShortBinaryOperator)remappingFunction
            : (x, y) -> SafeMath.safeIntToShort(remappingFunction.applyAsInt(x, y))
      );
   }

   @Deprecated
   default short mergeShort(K key, short value, BiFunction<? super Short, ? super Short, ? extends Short> remappingFunction) {
      return this.merge(key, value, remappingFunction);
   }

   @Deprecated
   default Short putIfAbsent(K key, Short value) {
      return Map.super.putIfAbsent(key, value);
   }

   @Deprecated
   @Override
   default boolean remove(Object key, Object value) {
      return Map.super.remove(key, value);
   }

   @Deprecated
   default boolean replace(K key, Short oldValue, Short newValue) {
      return Map.super.replace(key, oldValue, newValue);
   }

   @Deprecated
   default Short replace(K key, Short value) {
      return Map.super.replace(key, value);
   }

   @Deprecated
   default Short merge(K key, Short value, BiFunction<? super Short, ? super Short, ? extends Short> remappingFunction) {
      return Map.super.merge(key, value, remappingFunction);
   }

   public interface Entry<K> extends java.util.Map.Entry<K, Short> {
      short getShortValue();

      short setValue(short var1);

      @Deprecated
      default Short getValue() {
         return this.getShortValue();
      }

      @Deprecated
      default Short setValue(Short value) {
         return this.setValue(value.shortValue());
      }
   }

   public interface FastEntrySet<K> extends ObjectSet<Object2ShortMap.Entry<K>> {
      ObjectIterator<Object2ShortMap.Entry<K>> fastIterator();

      default void fastForEach(Consumer<? super Object2ShortMap.Entry<K>> consumer) {
         this.forEach(consumer);
      }
   }
}
