package it.unimi.dsi.fastutil.chars;

import it.unimi.dsi.fastutil.SafeMath;
import it.unimi.dsi.fastutil.floats.FloatBinaryOperator;
import it.unimi.dsi.fastutil.floats.FloatCollection;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.DoubleBinaryOperator;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.IntToDoubleFunction;

public interface Char2FloatMap extends Char2FloatFunction, Map<Character, Float> {
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

   ObjectSet<Char2FloatMap.Entry> char2FloatEntrySet();

   @Deprecated
   default ObjectSet<java.util.Map.Entry<Character, Float>> entrySet() {
      return this.char2FloatEntrySet();
   }

   @Deprecated
   @Override
   default Float put(Character key, Float value) {
      return Char2FloatFunction.super.put(key, value);
   }

   @Deprecated
   @Override
   default Float get(Object key) {
      return Char2FloatFunction.super.get(key);
   }

   @Deprecated
   @Override
   default Float remove(Object key) {
      return Char2FloatFunction.super.remove(key);
   }

   CharSet keySet();

   FloatCollection values();

   @Override
   boolean containsKey(char var1);

   @Deprecated
   @Override
   default boolean containsKey(Object key) {
      return Char2FloatFunction.super.containsKey(key);
   }

   boolean containsValue(float var1);

   @Deprecated
   @Override
   default boolean containsValue(Object value) {
      return value == null ? false : this.containsValue(((Float)value).floatValue());
   }

   @Override
   default void forEach(BiConsumer<? super Character, ? super Float> consumer) {
      ObjectSet<Char2FloatMap.Entry> entrySet = this.char2FloatEntrySet();
      Consumer<Char2FloatMap.Entry> wrappingConsumer = entry -> consumer.accept(entry.getCharKey(), entry.getFloatValue());
      if (entrySet instanceof Char2FloatMap.FastEntrySet) {
         ((Char2FloatMap.FastEntrySet)entrySet).fastForEach(wrappingConsumer);
      } else {
         entrySet.forEach(wrappingConsumer);
      }
   }

   @Override
   default float getOrDefault(char key, float defaultValue) {
      float v;
      return (v = this.get(key)) == this.defaultReturnValue() && !this.containsKey(key) ? defaultValue : v;
   }

   @Deprecated
   @Override
   default Float getOrDefault(Object key, Float defaultValue) {
      return Map.super.getOrDefault(key, defaultValue);
   }

   default float putIfAbsent(char key, float value) {
      float v = this.get(key);
      float drv = this.defaultReturnValue();
      if (v == drv && !this.containsKey(key)) {
         this.put(key, value);
         return drv;
      } else {
         return v;
      }
   }

   default boolean remove(char key, float value) {
      float curValue = this.get(key);
      if (Float.floatToIntBits(curValue) == Float.floatToIntBits(value) && (curValue != this.defaultReturnValue() || this.containsKey(key))) {
         this.remove(key);
         return true;
      } else {
         return false;
      }
   }

   default boolean replace(char key, float oldValue, float newValue) {
      float curValue = this.get(key);
      if (Float.floatToIntBits(curValue) == Float.floatToIntBits(oldValue) && (curValue != this.defaultReturnValue() || this.containsKey(key))) {
         this.put(key, newValue);
         return true;
      } else {
         return false;
      }
   }

   default float replace(char key, float value) {
      return this.containsKey(key) ? this.put(key, value) : this.defaultReturnValue();
   }

   default float computeIfAbsent(char key, IntToDoubleFunction mappingFunction) {
      Objects.requireNonNull(mappingFunction);
      float v = this.get(key);
      if (v == this.defaultReturnValue() && !this.containsKey(key)) {
         float newValue = SafeMath.safeDoubleToFloat(mappingFunction.applyAsDouble(key));
         this.put(key, newValue);
         return newValue;
      } else {
         return v;
      }
   }

   default float computeIfAbsentNullable(char key, IntFunction<? extends Float> mappingFunction) {
      Objects.requireNonNull(mappingFunction);
      float v = this.get(key);
      float drv = this.defaultReturnValue();
      if (v == drv && !this.containsKey(key)) {
         Float mappedValue = mappingFunction.apply(key);
         if (mappedValue == null) {
            return drv;
         } else {
            float newValue = mappedValue;
            this.put(key, newValue);
            return newValue;
         }
      } else {
         return v;
      }
   }

   default float computeIfAbsent(char key, Char2FloatFunction mappingFunction) {
      Objects.requireNonNull(mappingFunction);
      float v = this.get(key);
      float drv = this.defaultReturnValue();
      if (v != drv || this.containsKey(key)) {
         return v;
      } else if (!mappingFunction.containsKey(key)) {
         return drv;
      } else {
         float newValue = mappingFunction.get(key);
         this.put(key, newValue);
         return newValue;
      }
   }

   @Deprecated
   default float computeIfAbsentPartial(char key, Char2FloatFunction mappingFunction) {
      return this.computeIfAbsent(key, mappingFunction);
   }

   default float computeIfPresent(char key, BiFunction<? super Character, ? super Float, ? extends Float> remappingFunction) {
      Objects.requireNonNull(remappingFunction);
      float oldValue = this.get(key);
      float drv = this.defaultReturnValue();
      if (oldValue == drv && !this.containsKey(key)) {
         return drv;
      } else {
         Float newValue = remappingFunction.apply(key, oldValue);
         if (newValue == null) {
            this.remove(key);
            return drv;
         } else {
            float newVal = newValue;
            this.put(key, newVal);
            return newVal;
         }
      }
   }

   default float compute(char key, BiFunction<? super Character, ? super Float, ? extends Float> remappingFunction) {
      Objects.requireNonNull(remappingFunction);
      float oldValue = this.get(key);
      float drv = this.defaultReturnValue();
      boolean contained = oldValue != drv || this.containsKey(key);
      Float newValue = remappingFunction.apply(key, contained ? oldValue : null);
      if (newValue == null) {
         if (contained) {
            this.remove(key);
         }

         return drv;
      } else {
         float newVal = newValue;
         this.put(key, newVal);
         return newVal;
      }
   }

   default float merge(char key, float value, BiFunction<? super Float, ? super Float, ? extends Float> remappingFunction) {
      Objects.requireNonNull(remappingFunction);
      float oldValue = this.get(key);
      float drv = this.defaultReturnValue();
      float newValue;
      if (oldValue == drv && !this.containsKey(key)) {
         newValue = value;
      } else {
         Float mergedValue = remappingFunction.apply(oldValue, value);
         if (mergedValue == null) {
            this.remove(key);
            return drv;
         }

         newValue = mergedValue;
      }

      this.put(key, newValue);
      return newValue;
   }

   default float mergeFloat(char key, float value, FloatBinaryOperator remappingFunction) {
      Objects.requireNonNull(remappingFunction);
      float oldValue = this.get(key);
      float drv = this.defaultReturnValue();
      float newValue = oldValue == drv && !this.containsKey(key) ? value : remappingFunction.apply(oldValue, value);
      this.put(key, newValue);
      return newValue;
   }

   default float mergeFloat(char key, float value, DoubleBinaryOperator remappingFunction) {
      return this.mergeFloat(
         key,
         value,
         remappingFunction instanceof FloatBinaryOperator
            ? (FloatBinaryOperator)remappingFunction
            : (x, y) -> SafeMath.safeDoubleToFloat(remappingFunction.applyAsDouble(x, y))
      );
   }

   @Deprecated
   default Float putIfAbsent(Character key, Float value) {
      return Map.super.putIfAbsent(key, value);
   }

   @Deprecated
   @Override
   default boolean remove(Object key, Object value) {
      return Map.super.remove(key, value);
   }

   @Deprecated
   default boolean replace(Character key, Float oldValue, Float newValue) {
      return Map.super.replace(key, oldValue, newValue);
   }

   @Deprecated
   default Float replace(Character key, Float value) {
      return Map.super.replace(key, value);
   }

   @Deprecated
   default Float computeIfAbsent(Character key, Function<? super Character, ? extends Float> mappingFunction) {
      return Map.super.computeIfAbsent(key, mappingFunction);
   }

   @Deprecated
   default Float computeIfPresent(Character key, BiFunction<? super Character, ? super Float, ? extends Float> remappingFunction) {
      return Map.super.computeIfPresent(key, remappingFunction);
   }

   @Deprecated
   default Float compute(Character key, BiFunction<? super Character, ? super Float, ? extends Float> remappingFunction) {
      return Map.super.compute(key, remappingFunction);
   }

   @Deprecated
   default Float merge(Character key, Float value, BiFunction<? super Float, ? super Float, ? extends Float> remappingFunction) {
      return Map.super.merge(key, value, remappingFunction);
   }

   public interface Entry extends java.util.Map.Entry<Character, Float> {
      char getCharKey();

      @Deprecated
      default Character getKey() {
         return this.getCharKey();
      }

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

   public interface FastEntrySet extends ObjectSet<Char2FloatMap.Entry> {
      ObjectIterator<Char2FloatMap.Entry> fastIterator();

      default void fastForEach(Consumer<? super Char2FloatMap.Entry> consumer) {
         this.forEach(consumer);
      }
   }
}
