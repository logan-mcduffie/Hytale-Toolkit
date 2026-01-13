package it.unimi.dsi.fastutil.objects;

import it.unimi.dsi.fastutil.doubles.DoubleCollection;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.DoubleBinaryOperator;
import java.util.function.ToDoubleFunction;

public interface Reference2DoubleMap<K> extends Reference2DoubleFunction<K>, Map<K, Double> {
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

   ObjectSet<Reference2DoubleMap.Entry<K>> reference2DoubleEntrySet();

   @Deprecated
   default ObjectSet<java.util.Map.Entry<K, Double>> entrySet() {
      return this.reference2DoubleEntrySet();
   }

   @Deprecated
   @Override
   default Double put(K key, Double value) {
      return Reference2DoubleFunction.super.put(key, value);
   }

   @Deprecated
   @Override
   default Double get(Object key) {
      return Reference2DoubleFunction.super.get(key);
   }

   @Deprecated
   @Override
   default Double remove(Object key) {
      return Reference2DoubleFunction.super.remove(key);
   }

   ReferenceSet<K> keySet();

   DoubleCollection values();

   @Override
   boolean containsKey(Object var1);

   boolean containsValue(double var1);

   @Deprecated
   @Override
   default boolean containsValue(Object value) {
      return value == null ? false : this.containsValue(((Double)value).doubleValue());
   }

   @Override
   default void forEach(BiConsumer<? super K, ? super Double> consumer) {
      ObjectSet<Reference2DoubleMap.Entry<K>> entrySet = this.reference2DoubleEntrySet();
      Consumer<Reference2DoubleMap.Entry<K>> wrappingConsumer = entry -> consumer.accept(entry.getKey(), entry.getDoubleValue());
      if (entrySet instanceof Reference2DoubleMap.FastEntrySet) {
         ((Reference2DoubleMap.FastEntrySet)entrySet).fastForEach(wrappingConsumer);
      } else {
         entrySet.forEach(wrappingConsumer);
      }
   }

   @Override
   default double getOrDefault(Object key, double defaultValue) {
      double v;
      return (v = this.getDouble(key)) == this.defaultReturnValue() && !this.containsKey(key) ? defaultValue : v;
   }

   @Deprecated
   @Override
   default Double getOrDefault(Object key, Double defaultValue) {
      return Map.super.getOrDefault(key, defaultValue);
   }

   default double putIfAbsent(K key, double value) {
      double v = this.getDouble(key);
      double drv = this.defaultReturnValue();
      if (v == drv && !this.containsKey(key)) {
         this.put(key, value);
         return drv;
      } else {
         return v;
      }
   }

   default boolean remove(Object key, double value) {
      double curValue = this.getDouble(key);
      if (Double.doubleToLongBits(curValue) == Double.doubleToLongBits(value) && (curValue != this.defaultReturnValue() || this.containsKey(key))) {
         this.removeDouble(key);
         return true;
      } else {
         return false;
      }
   }

   default boolean replace(K key, double oldValue, double newValue) {
      double curValue = this.getDouble(key);
      if (Double.doubleToLongBits(curValue) == Double.doubleToLongBits(oldValue) && (curValue != this.defaultReturnValue() || this.containsKey(key))) {
         this.put(key, newValue);
         return true;
      } else {
         return false;
      }
   }

   default double replace(K key, double value) {
      return this.containsKey(key) ? this.put(key, value) : this.defaultReturnValue();
   }

   default double computeIfAbsent(K key, ToDoubleFunction<? super K> mappingFunction) {
      Objects.requireNonNull(mappingFunction);
      double v = this.getDouble(key);
      if (v == this.defaultReturnValue() && !this.containsKey(key)) {
         double newValue = mappingFunction.applyAsDouble(key);
         this.put(key, newValue);
         return newValue;
      } else {
         return v;
      }
   }

   @Deprecated
   default double computeDoubleIfAbsent(K key, ToDoubleFunction<? super K> mappingFunction) {
      return this.computeIfAbsent(key, mappingFunction);
   }

   default double computeIfAbsent(K key, Reference2DoubleFunction<? super K> mappingFunction) {
      Objects.requireNonNull(mappingFunction);
      double v = this.getDouble(key);
      double drv = this.defaultReturnValue();
      if (v != drv || this.containsKey(key)) {
         return v;
      } else if (!mappingFunction.containsKey(key)) {
         return drv;
      } else {
         double newValue = mappingFunction.getDouble(key);
         this.put(key, newValue);
         return newValue;
      }
   }

   @Deprecated
   default double computeDoubleIfAbsentPartial(K key, Reference2DoubleFunction<? super K> mappingFunction) {
      return this.computeIfAbsent(key, mappingFunction);
   }

   default double computeDoubleIfPresent(K key, BiFunction<? super K, ? super Double, ? extends Double> remappingFunction) {
      Objects.requireNonNull(remappingFunction);
      double oldValue = this.getDouble(key);
      double drv = this.defaultReturnValue();
      if (oldValue == drv && !this.containsKey(key)) {
         return drv;
      } else {
         Double newValue = remappingFunction.apply(key, oldValue);
         if (newValue == null) {
            this.removeDouble(key);
            return drv;
         } else {
            double newVal = newValue;
            this.put(key, newVal);
            return newVal;
         }
      }
   }

   default double computeDouble(K key, BiFunction<? super K, ? super Double, ? extends Double> remappingFunction) {
      Objects.requireNonNull(remappingFunction);
      double oldValue = this.getDouble(key);
      double drv = this.defaultReturnValue();
      boolean contained = oldValue != drv || this.containsKey(key);
      Double newValue = remappingFunction.apply(key, contained ? oldValue : null);
      if (newValue == null) {
         if (contained) {
            this.removeDouble(key);
         }

         return drv;
      } else {
         double newVal = newValue;
         this.put(key, newVal);
         return newVal;
      }
   }

   default double merge(K key, double value, BiFunction<? super Double, ? super Double, ? extends Double> remappingFunction) {
      Objects.requireNonNull(remappingFunction);
      double oldValue = this.getDouble(key);
      double drv = this.defaultReturnValue();
      double newValue;
      if (oldValue == drv && !this.containsKey(key)) {
         newValue = value;
      } else {
         Double mergedValue = remappingFunction.apply(oldValue, value);
         if (mergedValue == null) {
            this.removeDouble(key);
            return drv;
         }

         newValue = mergedValue;
      }

      this.put(key, newValue);
      return newValue;
   }

   default double mergeDouble(K key, double value, DoubleBinaryOperator remappingFunction) {
      Objects.requireNonNull(remappingFunction);
      double oldValue = this.getDouble(key);
      double drv = this.defaultReturnValue();
      double newValue = oldValue == drv && !this.containsKey(key) ? value : remappingFunction.applyAsDouble(oldValue, value);
      this.put(key, newValue);
      return newValue;
   }

   default double mergeDouble(K key, double value, it.unimi.dsi.fastutil.doubles.DoubleBinaryOperator remappingFunction) {
      return this.mergeDouble(key, value, (DoubleBinaryOperator)remappingFunction);
   }

   @Deprecated
   default double mergeDouble(K key, double value, BiFunction<? super Double, ? super Double, ? extends Double> remappingFunction) {
      return this.merge(key, value, remappingFunction);
   }

   @Deprecated
   default Double putIfAbsent(K key, Double value) {
      return Map.super.putIfAbsent(key, value);
   }

   @Deprecated
   @Override
   default boolean remove(Object key, Object value) {
      return Map.super.remove(key, value);
   }

   @Deprecated
   default boolean replace(K key, Double oldValue, Double newValue) {
      return Map.super.replace(key, oldValue, newValue);
   }

   @Deprecated
   default Double replace(K key, Double value) {
      return Map.super.replace(key, value);
   }

   @Deprecated
   default Double merge(K key, Double value, BiFunction<? super Double, ? super Double, ? extends Double> remappingFunction) {
      return Map.super.merge(key, value, remappingFunction);
   }

   public interface Entry<K> extends java.util.Map.Entry<K, Double> {
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

   public interface FastEntrySet<K> extends ObjectSet<Reference2DoubleMap.Entry<K>> {
      ObjectIterator<Reference2DoubleMap.Entry<K>> fastIterator();

      default void fastForEach(Consumer<? super Reference2DoubleMap.Entry<K>> consumer) {
         this.forEach(consumer);
      }
   }
}
