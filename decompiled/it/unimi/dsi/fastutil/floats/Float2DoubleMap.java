package it.unimi.dsi.fastutil.floats;

import it.unimi.dsi.fastutil.doubles.DoubleCollection;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.DoubleBinaryOperator;
import java.util.function.DoubleFunction;
import java.util.function.DoubleUnaryOperator;
import java.util.function.Function;

public interface Float2DoubleMap extends Float2DoubleFunction, Map<Float, Double> {
   @Override
   int size();

   @Override
   default void clear() {
      throw new UnsupportedOperationException();
   }

   @Override
   void defaultReturnValue(double var1);

   @Override
   double defaultReturnValue();

   ObjectSet<Float2DoubleMap.Entry> float2DoubleEntrySet();

   @Deprecated
   default ObjectSet<java.util.Map.Entry<Float, Double>> entrySet() {
      return this.float2DoubleEntrySet();
   }

   @Deprecated
   @Override
   default Double put(Float key, Double value) {
      return Float2DoubleFunction.super.put(key, value);
   }

   @Deprecated
   @Override
   default Double get(Object key) {
      return Float2DoubleFunction.super.get(key);
   }

   @Deprecated
   @Override
   default Double remove(Object key) {
      return Float2DoubleFunction.super.remove(key);
   }

   FloatSet keySet();

   DoubleCollection values();

   @Override
   boolean containsKey(float var1);

   @Deprecated
   @Override
   default boolean containsKey(Object key) {
      return Float2DoubleFunction.super.containsKey(key);
   }

   boolean containsValue(double var1);

   @Deprecated
   @Override
   default boolean containsValue(Object value) {
      return value == null ? false : this.containsValue(((Double)value).doubleValue());
   }

   @Override
   default void forEach(BiConsumer<? super Float, ? super Double> consumer) {
      ObjectSet<Float2DoubleMap.Entry> entrySet = this.float2DoubleEntrySet();
      Consumer<Float2DoubleMap.Entry> wrappingConsumer = entry -> consumer.accept(entry.getFloatKey(), entry.getDoubleValue());
      if (entrySet instanceof Float2DoubleMap.FastEntrySet) {
         ((Float2DoubleMap.FastEntrySet)entrySet).fastForEach(wrappingConsumer);
      } else {
         entrySet.forEach(wrappingConsumer);
      }
   }

   @Override
   default double getOrDefault(float key, double defaultValue) {
      double v;
      return (v = this.get(key)) == this.defaultReturnValue() && !this.containsKey(key) ? defaultValue : v;
   }

   @Deprecated
   @Override
   default Double getOrDefault(Object key, Double defaultValue) {
      return Map.super.getOrDefault(key, defaultValue);
   }

   default double putIfAbsent(float key, double value) {
      double v = this.get(key);
      double drv = this.defaultReturnValue();
      if (v == drv && !this.containsKey(key)) {
         this.put(key, value);
         return drv;
      } else {
         return v;
      }
   }

   default boolean remove(float key, double value) {
      double curValue = this.get(key);
      if (Double.doubleToLongBits(curValue) == Double.doubleToLongBits(value) && (curValue != this.defaultReturnValue() || this.containsKey(key))) {
         this.remove(key);
         return true;
      } else {
         return false;
      }
   }

   default boolean replace(float key, double oldValue, double newValue) {
      double curValue = this.get(key);
      if (Double.doubleToLongBits(curValue) == Double.doubleToLongBits(oldValue) && (curValue != this.defaultReturnValue() || this.containsKey(key))) {
         this.put(key, newValue);
         return true;
      } else {
         return false;
      }
   }

   default double replace(float key, double value) {
      return this.containsKey(key) ? this.put(key, value) : this.defaultReturnValue();
   }

   default double computeIfAbsent(float key, DoubleUnaryOperator mappingFunction) {
      Objects.requireNonNull(mappingFunction);
      double v = this.get(key);
      if (v == this.defaultReturnValue() && !this.containsKey(key)) {
         double newValue = mappingFunction.applyAsDouble(key);
         this.put(key, newValue);
         return newValue;
      } else {
         return v;
      }
   }

   default double computeIfAbsentNullable(float key, DoubleFunction<? extends Double> mappingFunction) {
      Objects.requireNonNull(mappingFunction);
      double v = this.get(key);
      double drv = this.defaultReturnValue();
      if (v == drv && !this.containsKey(key)) {
         Double mappedValue = mappingFunction.apply(key);
         if (mappedValue == null) {
            return drv;
         } else {
            double newValue = mappedValue;
            this.put(key, newValue);
            return newValue;
         }
      } else {
         return v;
      }
   }

   default double computeIfAbsent(float key, Float2DoubleFunction mappingFunction) {
      Objects.requireNonNull(mappingFunction);
      double v = this.get(key);
      double drv = this.defaultReturnValue();
      if (v != drv || this.containsKey(key)) {
         return v;
      } else if (!mappingFunction.containsKey(key)) {
         return drv;
      } else {
         double newValue = mappingFunction.get(key);
         this.put(key, newValue);
         return newValue;
      }
   }

   @Deprecated
   default double computeIfAbsentPartial(float key, Float2DoubleFunction mappingFunction) {
      return this.computeIfAbsent(key, mappingFunction);
   }

   default double computeIfPresent(float key, BiFunction<? super Float, ? super Double, ? extends Double> remappingFunction) {
      Objects.requireNonNull(remappingFunction);
      double oldValue = this.get(key);
      double drv = this.defaultReturnValue();
      if (oldValue == drv && !this.containsKey(key)) {
         return drv;
      } else {
         Double newValue = remappingFunction.apply(key, oldValue);
         if (newValue == null) {
            this.remove(key);
            return drv;
         } else {
            double newVal = newValue;
            this.put(key, newVal);
            return newVal;
         }
      }
   }

   default double compute(float key, BiFunction<? super Float, ? super Double, ? extends Double> remappingFunction) {
      Objects.requireNonNull(remappingFunction);
      double oldValue = this.get(key);
      double drv = this.defaultReturnValue();
      boolean contained = oldValue != drv || this.containsKey(key);
      Double newValue = remappingFunction.apply(key, contained ? oldValue : null);
      if (newValue == null) {
         if (contained) {
            this.remove(key);
         }

         return drv;
      } else {
         double newVal = newValue;
         this.put(key, newVal);
         return newVal;
      }
   }

   default double merge(float key, double value, BiFunction<? super Double, ? super Double, ? extends Double> remappingFunction) {
      Objects.requireNonNull(remappingFunction);
      double oldValue = this.get(key);
      double drv = this.defaultReturnValue();
      double newValue;
      if (oldValue == drv && !this.containsKey(key)) {
         newValue = value;
      } else {
         Double mergedValue = remappingFunction.apply(oldValue, value);
         if (mergedValue == null) {
            this.remove(key);
            return drv;
         }

         newValue = mergedValue;
      }

      this.put(key, newValue);
      return newValue;
   }

   default double mergeDouble(float key, double value, DoubleBinaryOperator remappingFunction) {
      Objects.requireNonNull(remappingFunction);
      double oldValue = this.get(key);
      double drv = this.defaultReturnValue();
      double newValue = oldValue == drv && !this.containsKey(key) ? value : remappingFunction.applyAsDouble(oldValue, value);
      this.put(key, newValue);
      return newValue;
   }

   default double mergeDouble(float key, double value, it.unimi.dsi.fastutil.doubles.DoubleBinaryOperator remappingFunction) {
      return this.mergeDouble(key, value, (DoubleBinaryOperator)remappingFunction);
   }

   @Deprecated
   default Double putIfAbsent(Float key, Double value) {
      return Map.super.putIfAbsent(key, value);
   }

   @Deprecated
   @Override
   default boolean remove(Object key, Object value) {
      return Map.super.remove(key, value);
   }

   @Deprecated
   default boolean replace(Float key, Double oldValue, Double newValue) {
      return Map.super.replace(key, oldValue, newValue);
   }

   @Deprecated
   default Double replace(Float key, Double value) {
      return Map.super.replace(key, value);
   }

   @Deprecated
   default Double computeIfAbsent(Float key, Function<? super Float, ? extends Double> mappingFunction) {
      return Map.super.computeIfAbsent(key, mappingFunction);
   }

   @Deprecated
   default Double computeIfPresent(Float key, BiFunction<? super Float, ? super Double, ? extends Double> remappingFunction) {
      return Map.super.computeIfPresent(key, remappingFunction);
   }

   @Deprecated
   default Double compute(Float key, BiFunction<? super Float, ? super Double, ? extends Double> remappingFunction) {
      return Map.super.compute(key, remappingFunction);
   }

   @Deprecated
   default Double merge(Float key, Double value, BiFunction<? super Double, ? super Double, ? extends Double> remappingFunction) {
      return Map.super.merge(key, value, remappingFunction);
   }

   public interface Entry extends java.util.Map.Entry<Float, Double> {
      float getFloatKey();

      @Deprecated
      default Float getKey() {
         return this.getFloatKey();
      }

      double getDoubleValue();

      double setValue(double var1);

      @Deprecated
      default Double getValue() {
         return this.getDoubleValue();
      }

      @Deprecated
      default Double setValue(Double value) {
         return this.setValue(value.doubleValue());
      }
   }

   public interface FastEntrySet extends ObjectSet<Float2DoubleMap.Entry> {
      ObjectIterator<Float2DoubleMap.Entry> fastIterator();

      default void fastForEach(Consumer<? super Float2DoubleMap.Entry> consumer) {
         this.forEach(consumer);
      }
   }
}
