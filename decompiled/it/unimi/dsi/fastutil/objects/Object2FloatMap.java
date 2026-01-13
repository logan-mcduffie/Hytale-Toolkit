package it.unimi.dsi.fastutil.objects;

import it.unimi.dsi.fastutil.SafeMath;
import it.unimi.dsi.fastutil.floats.FloatBinaryOperator;
import it.unimi.dsi.fastutil.floats.FloatCollection;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.DoubleBinaryOperator;
import java.util.function.ToDoubleFunction;

public interface Object2FloatMap<K> extends Object2FloatFunction<K>, Map<K, Float> {
   @Override
   int size();

   @Override
   default void clear() {
      throw new UnsupportedOperationException();
   }

   @Override
   void defaultReturnValue(float var1);

   @Override
   float defaultReturnValue();

   ObjectSet<Object2FloatMap.Entry<K>> object2FloatEntrySet();

   @Deprecated
   default ObjectSet<java.util.Map.Entry<K, Float>> entrySet() {
      return this.object2FloatEntrySet();
   }

   @Deprecated
   @Override
   default Float put(K key, Float value) {
      return Object2FloatFunction.super.put(key, value);
   }

   @Deprecated
   @Override
   default Float get(Object key) {
      return Object2FloatFunction.super.get(key);
   }

   @Deprecated
   @Override
   default Float remove(Object key) {
      return Object2FloatFunction.super.remove(key);
   }

   ObjectSet<K> keySet();

   FloatCollection values();

   @Override
   boolean containsKey(Object var1);

   boolean containsValue(float var1);

   @Deprecated
   @Override
   default boolean containsValue(Object value) {
      return value == null ? false : this.containsValue(((Float)value).floatValue());
   }

   @Override
   default void forEach(BiConsumer<? super K, ? super Float> consumer) {
      ObjectSet<Object2FloatMap.Entry<K>> entrySet = this.object2FloatEntrySet();
      Consumer<Object2FloatMap.Entry<K>> wrappingConsumer = entry -> consumer.accept(entry.getKey(), entry.getFloatValue());
      if (entrySet instanceof Object2FloatMap.FastEntrySet) {
         ((Object2FloatMap.FastEntrySet)entrySet).fastForEach(wrappingConsumer);
      } else {
         entrySet.forEach(wrappingConsumer);
      }
   }

   @Override
   default float getOrDefault(Object key, float defaultValue) {
      float v;
      return (v = this.getFloat(key)) == this.defaultReturnValue() && !this.containsKey(key) ? defaultValue : v;
   }

   @Deprecated
   @Override
   default Float getOrDefault(Object key, Float defaultValue) {
      return Map.super.getOrDefault(key, defaultValue);
   }

   default float putIfAbsent(K key, float value) {
      float v = this.getFloat(key);
      float drv = this.defaultReturnValue();
      if (v == drv && !this.containsKey(key)) {
         this.put(key, value);
         return drv;
      } else {
         return v;
      }
   }

   default boolean remove(Object key, float value) {
      float curValue = this.getFloat(key);
      if (Float.floatToIntBits(curValue) == Float.floatToIntBits(value) && (curValue != this.defaultReturnValue() || this.containsKey(key))) {
         this.removeFloat(key);
         return true;
      } else {
         return false;
      }
   }

   default boolean replace(K key, float oldValue, float newValue) {
      float curValue = this.getFloat(key);
      if (Float.floatToIntBits(curValue) == Float.floatToIntBits(oldValue) && (curValue != this.defaultReturnValue() || this.containsKey(key))) {
         this.put(key, newValue);
         return true;
      } else {
         return false;
      }
   }

   default float replace(K key, float value) {
      return this.containsKey(key) ? this.put(key, value) : this.defaultReturnValue();
   }

   default float computeIfAbsent(K key, ToDoubleFunction<? super K> mappingFunction) {
      Objects.requireNonNull(mappingFunction);
      float v = this.getFloat(key);
      if (v == this.defaultReturnValue() && !this.containsKey(key)) {
         float newValue = SafeMath.safeDoubleToFloat(mappingFunction.applyAsDouble(key));
         this.put(key, newValue);
         return newValue;
      } else {
         return v;
      }
   }

   @Deprecated
   default float computeFloatIfAbsent(K key, ToDoubleFunction<? super K> mappingFunction) {
      return this.computeIfAbsent(key, mappingFunction);
   }

   default float computeIfAbsent(K key, Object2FloatFunction<? super K> mappingFunction) {
      Objects.requireNonNull(mappingFunction);
      float v = this.getFloat(key);
      float drv = this.defaultReturnValue();
      if (v != drv || this.containsKey(key)) {
         return v;
      } else if (!mappingFunction.containsKey(key)) {
         return drv;
      } else {
         float newValue = mappingFunction.getFloat(key);
         this.put(key, newValue);
         return newValue;
      }
   }

   @Deprecated
   default float computeFloatIfAbsentPartial(K key, Object2FloatFunction<? super K> mappingFunction) {
      return this.computeIfAbsent(key, mappingFunction);
   }

   default float computeFloatIfPresent(K key, BiFunction<? super K, ? super Float, ? extends Float> remappingFunction) {
      Objects.requireNonNull(remappingFunction);
      float oldValue = this.getFloat(key);
      float drv = this.defaultReturnValue();
      if (oldValue == drv && !this.containsKey(key)) {
         return drv;
      } else {
         Float newValue = remappingFunction.apply(key, oldValue);
         if (newValue == null) {
            this.removeFloat(key);
            return drv;
         } else {
            float newVal = newValue;
            this.put(key, newVal);
            return newVal;
         }
      }
   }

   default float computeFloat(K key, BiFunction<? super K, ? super Float, ? extends Float> remappingFunction) {
      Objects.requireNonNull(remappingFunction);
      float oldValue = this.getFloat(key);
      float drv = this.defaultReturnValue();
      boolean contained = oldValue != drv || this.containsKey(key);
      Float newValue = remappingFunction.apply(key, contained ? oldValue : null);
      if (newValue == null) {
         if (contained) {
            this.removeFloat(key);
         }

         return drv;
      } else {
         float newVal = newValue;
         this.put(key, newVal);
         return newVal;
      }
   }

   default float merge(K key, float value, BiFunction<? super Float, ? super Float, ? extends Float> remappingFunction) {
      Objects.requireNonNull(remappingFunction);
      float oldValue = this.getFloat(key);
      float drv = this.defaultReturnValue();
      float newValue;
      if (oldValue == drv && !this.containsKey(key)) {
         newValue = value;
      } else {
         Float mergedValue = remappingFunction.apply(oldValue, value);
         if (mergedValue == null) {
            this.removeFloat(key);
            return drv;
         }

         newValue = mergedValue;
      }

      this.put(key, newValue);
      return newValue;
   }

   default float mergeFloat(K key, float value, FloatBinaryOperator remappingFunction) {
      Objects.requireNonNull(remappingFunction);
      float oldValue = this.getFloat(key);
      float drv = this.defaultReturnValue();
      float newValue = oldValue == drv && !this.containsKey(key) ? value : remappingFunction.apply(oldValue, value);
      this.put(key, newValue);
      return newValue;
   }

   default float mergeFloat(K key, float value, DoubleBinaryOperator remappingFunction) {
      return this.mergeFloat(
         key,
         value,
         remappingFunction instanceof FloatBinaryOperator
            ? (FloatBinaryOperator)remappingFunction
            : (x, y) -> SafeMath.safeDoubleToFloat(remappingFunction.applyAsDouble(x, y))
      );
   }

   @Deprecated
   default float mergeFloat(K key, float value, BiFunction<? super Float, ? super Float, ? extends Float> remappingFunction) {
      return this.merge(key, value, remappingFunction);
   }

   @Deprecated
   default Float putIfAbsent(K key, Float value) {
      return Map.super.putIfAbsent(key, value);
   }

   @Deprecated
   @Override
   default boolean remove(Object key, Object value) {
      return Map.super.remove(key, value);
   }

   @Deprecated
   default boolean replace(K key, Float oldValue, Float newValue) {
      return Map.super.replace(key, oldValue, newValue);
   }

   @Deprecated
   default Float replace(K key, Float value) {
      return Map.super.replace(key, value);
   }

   @Deprecated
   default Float merge(K key, Float value, BiFunction<? super Float, ? super Float, ? extends Float> remappingFunction) {
      return Map.super.merge(key, value, remappingFunction);
   }

   public interface Entry<K> extends java.util.Map.Entry<K, Float> {
      float getFloatValue();

      float setValue(float var1);

      @Deprecated
      default Float getValue() {
         return this.getFloatValue();
      }

      @Deprecated
      default Float setValue(Float value) {
         return this.setValue(value.floatValue());
      }
   }

   public interface FastEntrySet<K> extends ObjectSet<Object2FloatMap.Entry<K>> {
      ObjectIterator<Object2FloatMap.Entry<K>> fastIterator();

      default void fastForEach(Consumer<? super Object2FloatMap.Entry<K>> consumer) {
         this.forEach(consumer);
      }
   }
}
