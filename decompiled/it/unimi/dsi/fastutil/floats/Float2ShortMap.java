package it.unimi.dsi.fastutil.floats;

import it.unimi.dsi.fastutil.SafeMath;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import it.unimi.dsi.fastutil.shorts.ShortBinaryOperator;
import it.unimi.dsi.fastutil.shorts.ShortCollection;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.DoubleFunction;
import java.util.function.DoubleToIntFunction;
import java.util.function.Function;
import java.util.function.IntBinaryOperator;

public interface Float2ShortMap extends Float2ShortFunction, Map<Float, Short> {
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

   ObjectSet<Float2ShortMap.Entry> float2ShortEntrySet();

   @Deprecated
   default ObjectSet<java.util.Map.Entry<Float, Short>> entrySet() {
      return this.float2ShortEntrySet();
   }

   @Deprecated
   @Override
   default Short put(Float key, Short value) {
      return Float2ShortFunction.super.put(key, value);
   }

   @Deprecated
   @Override
   default Short get(Object key) {
      return Float2ShortFunction.super.get(key);
   }

   @Deprecated
   @Override
   default Short remove(Object key) {
      return Float2ShortFunction.super.remove(key);
   }

   FloatSet keySet();

   ShortCollection values();

   @Override
   boolean containsKey(float var1);

   @Deprecated
   @Override
   default boolean containsKey(Object key) {
      return Float2ShortFunction.super.containsKey(key);
   }

   boolean containsValue(short var1);

   @Deprecated
   @Override
   default boolean containsValue(Object value) {
      return value == null ? false : this.containsValue(((Short)value).shortValue());
   }

   @Override
   default void forEach(BiConsumer<? super Float, ? super Short> consumer) {
      ObjectSet<Float2ShortMap.Entry> entrySet = this.float2ShortEntrySet();
      Consumer<Float2ShortMap.Entry> wrappingConsumer = entry -> consumer.accept(entry.getFloatKey(), entry.getShortValue());
      if (entrySet instanceof Float2ShortMap.FastEntrySet) {
         ((Float2ShortMap.FastEntrySet)entrySet).fastForEach(wrappingConsumer);
      } else {
         entrySet.forEach(wrappingConsumer);
      }
   }

   @Override
   default short getOrDefault(float key, short defaultValue) {
      short v;
      return (v = this.get(key)) == this.defaultReturnValue() && !this.containsKey(key) ? defaultValue : v;
   }

   @Deprecated
   @Override
   default Short getOrDefault(Object key, Short defaultValue) {
      return Map.super.getOrDefault(key, defaultValue);
   }

   default short putIfAbsent(float key, short value) {
      short v = this.get(key);
      short drv = this.defaultReturnValue();
      if (v == drv && !this.containsKey(key)) {
         this.put(key, value);
         return drv;
      } else {
         return v;
      }
   }

   default boolean remove(float key, short value) {
      short curValue = this.get(key);
      if (curValue == value && (curValue != this.defaultReturnValue() || this.containsKey(key))) {
         this.remove(key);
         return true;
      } else {
         return false;
      }
   }

   default boolean replace(float key, short oldValue, short newValue) {
      short curValue = this.get(key);
      if (curValue == oldValue && (curValue != this.defaultReturnValue() || this.containsKey(key))) {
         this.put(key, newValue);
         return true;
      } else {
         return false;
      }
   }

   default short replace(float key, short value) {
      return this.containsKey(key) ? this.put(key, value) : this.defaultReturnValue();
   }

   default short computeIfAbsent(float key, DoubleToIntFunction mappingFunction) {
      Objects.requireNonNull(mappingFunction);
      short v = this.get(key);
      if (v == this.defaultReturnValue() && !this.containsKey(key)) {
         short newValue = SafeMath.safeIntToShort(mappingFunction.applyAsInt(key));
         this.put(key, newValue);
         return newValue;
      } else {
         return v;
      }
   }

   default short computeIfAbsentNullable(float key, DoubleFunction<? extends Short> mappingFunction) {
      Objects.requireNonNull(mappingFunction);
      short v = this.get(key);
      short drv = this.defaultReturnValue();
      if (v == drv && !this.containsKey(key)) {
         Short mappedValue = mappingFunction.apply(key);
         if (mappedValue == null) {
            return drv;
         } else {
            short newValue = mappedValue;
            this.put(key, newValue);
            return newValue;
         }
      } else {
         return v;
      }
   }

   default short computeIfAbsent(float key, Float2ShortFunction mappingFunction) {
      Objects.requireNonNull(mappingFunction);
      short v = this.get(key);
      short drv = this.defaultReturnValue();
      if (v != drv || this.containsKey(key)) {
         return v;
      } else if (!mappingFunction.containsKey(key)) {
         return drv;
      } else {
         short newValue = mappingFunction.get(key);
         this.put(key, newValue);
         return newValue;
      }
   }

   @Deprecated
   default short computeIfAbsentPartial(float key, Float2ShortFunction mappingFunction) {
      return this.computeIfAbsent(key, mappingFunction);
   }

   default short computeIfPresent(float key, BiFunction<? super Float, ? super Short, ? extends Short> remappingFunction) {
      Objects.requireNonNull(remappingFunction);
      short oldValue = this.get(key);
      short drv = this.defaultReturnValue();
      if (oldValue == drv && !this.containsKey(key)) {
         return drv;
      } else {
         Short newValue = remappingFunction.apply(key, oldValue);
         if (newValue == null) {
            this.remove(key);
            return drv;
         } else {
            short newVal = newValue;
            this.put(key, newVal);
            return newVal;
         }
      }
   }

   default short compute(float key, BiFunction<? super Float, ? super Short, ? extends Short> remappingFunction) {
      Objects.requireNonNull(remappingFunction);
      short oldValue = this.get(key);
      short drv = this.defaultReturnValue();
      boolean contained = oldValue != drv || this.containsKey(key);
      Short newValue = remappingFunction.apply(key, contained ? oldValue : null);
      if (newValue == null) {
         if (contained) {
            this.remove(key);
         }

         return drv;
      } else {
         short newVal = newValue;
         this.put(key, newVal);
         return newVal;
      }
   }

   default short merge(float key, short value, BiFunction<? super Short, ? super Short, ? extends Short> remappingFunction) {
      Objects.requireNonNull(remappingFunction);
      short oldValue = this.get(key);
      short drv = this.defaultReturnValue();
      short newValue;
      if (oldValue == drv && !this.containsKey(key)) {
         newValue = value;
      } else {
         Short mergedValue = remappingFunction.apply(oldValue, value);
         if (mergedValue == null) {
            this.remove(key);
            return drv;
         }

         newValue = mergedValue;
      }

      this.put(key, newValue);
      return newValue;
   }

   default short mergeShort(float key, short value, ShortBinaryOperator remappingFunction) {
      Objects.requireNonNull(remappingFunction);
      short oldValue = this.get(key);
      short drv = this.defaultReturnValue();
      short newValue = oldValue == drv && !this.containsKey(key) ? value : remappingFunction.apply(oldValue, value);
      this.put(key, newValue);
      return newValue;
   }

   default short mergeShort(float key, short value, IntBinaryOperator remappingFunction) {
      return this.mergeShort(
         key,
         value,
         remappingFunction instanceof ShortBinaryOperator
            ? (ShortBinaryOperator)remappingFunction
            : (x, y) -> SafeMath.safeIntToShort(remappingFunction.applyAsInt(x, y))
      );
   }

   @Deprecated
   default Short putIfAbsent(Float key, Short value) {
      return Map.super.putIfAbsent(key, value);
   }

   @Deprecated
   @Override
   default boolean remove(Object key, Object value) {
      return Map.super.remove(key, value);
   }

   @Deprecated
   default boolean replace(Float key, Short oldValue, Short newValue) {
      return Map.super.replace(key, oldValue, newValue);
   }

   @Deprecated
   default Short replace(Float key, Short value) {
      return Map.super.replace(key, value);
   }

   @Deprecated
   default Short computeIfAbsent(Float key, Function<? super Float, ? extends Short> mappingFunction) {
      return Map.super.computeIfAbsent(key, mappingFunction);
   }

   @Deprecated
   default Short computeIfPresent(Float key, BiFunction<? super Float, ? super Short, ? extends Short> remappingFunction) {
      return Map.super.computeIfPresent(key, remappingFunction);
   }

   @Deprecated
   default Short compute(Float key, BiFunction<? super Float, ? super Short, ? extends Short> remappingFunction) {
      return Map.super.compute(key, remappingFunction);
   }

   @Deprecated
   default Short merge(Float key, Short value, BiFunction<? super Short, ? super Short, ? extends Short> remappingFunction) {
      return Map.super.merge(key, value, remappingFunction);
   }

   public interface Entry extends java.util.Map.Entry<Float, Short> {
      float getFloatKey();

      @Deprecated
      default Float getKey() {
         return this.getFloatKey();
      }

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

   public interface FastEntrySet extends ObjectSet<Float2ShortMap.Entry> {
      ObjectIterator<Float2ShortMap.Entry> fastIterator();

      default void fastForEach(Consumer<? super Float2ShortMap.Entry> consumer) {
         this.forEach(consumer);
      }
   }
}
