package it.unimi.dsi.fastutil.chars;

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
import java.util.function.Function;
import java.util.function.IntBinaryOperator;
import java.util.function.IntFunction;
import java.util.function.IntUnaryOperator;

public interface Char2ShortMap extends Char2ShortFunction, Map<Character, Short> {
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

   ObjectSet<Char2ShortMap.Entry> char2ShortEntrySet();

   @Deprecated
   default ObjectSet<java.util.Map.Entry<Character, Short>> entrySet() {
      return this.char2ShortEntrySet();
   }

   @Deprecated
   @Override
   default Short put(Character key, Short value) {
      return Char2ShortFunction.super.put(key, value);
   }

   @Deprecated
   @Override
   default Short get(Object key) {
      return Char2ShortFunction.super.get(key);
   }

   @Deprecated
   @Override
   default Short remove(Object key) {
      return Char2ShortFunction.super.remove(key);
   }

   CharSet keySet();

   ShortCollection values();

   @Override
   boolean containsKey(char var1);

   @Deprecated
   @Override
   default boolean containsKey(Object key) {
      return Char2ShortFunction.super.containsKey(key);
   }

   boolean containsValue(short var1);

   @Deprecated
   @Override
   default boolean containsValue(Object value) {
      return value == null ? false : this.containsValue(((Short)value).shortValue());
   }

   @Override
   default void forEach(BiConsumer<? super Character, ? super Short> consumer) {
      ObjectSet<Char2ShortMap.Entry> entrySet = this.char2ShortEntrySet();
      Consumer<Char2ShortMap.Entry> wrappingConsumer = entry -> consumer.accept(entry.getCharKey(), entry.getShortValue());
      if (entrySet instanceof Char2ShortMap.FastEntrySet) {
         ((Char2ShortMap.FastEntrySet)entrySet).fastForEach(wrappingConsumer);
      } else {
         entrySet.forEach(wrappingConsumer);
      }
   }

   @Override
   default short getOrDefault(char key, short defaultValue) {
      short v;
      return (v = this.get(key)) == this.defaultReturnValue() && !this.containsKey(key) ? defaultValue : v;
   }

   @Deprecated
   @Override
   default Short getOrDefault(Object key, Short defaultValue) {
      return Map.super.getOrDefault(key, defaultValue);
   }

   default short putIfAbsent(char key, short value) {
      short v = this.get(key);
      short drv = this.defaultReturnValue();
      if (v == drv && !this.containsKey(key)) {
         this.put(key, value);
         return drv;
      } else {
         return v;
      }
   }

   default boolean remove(char key, short value) {
      short curValue = this.get(key);
      if (curValue == value && (curValue != this.defaultReturnValue() || this.containsKey(key))) {
         this.remove(key);
         return true;
      } else {
         return false;
      }
   }

   default boolean replace(char key, short oldValue, short newValue) {
      short curValue = this.get(key);
      if (curValue == oldValue && (curValue != this.defaultReturnValue() || this.containsKey(key))) {
         this.put(key, newValue);
         return true;
      } else {
         return false;
      }
   }

   default short replace(char key, short value) {
      return this.containsKey(key) ? this.put(key, value) : this.defaultReturnValue();
   }

   default short computeIfAbsent(char key, IntUnaryOperator mappingFunction) {
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

   default short computeIfAbsentNullable(char key, IntFunction<? extends Short> mappingFunction) {
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

   default short computeIfAbsent(char key, Char2ShortFunction mappingFunction) {
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
   default short computeIfAbsentPartial(char key, Char2ShortFunction mappingFunction) {
      return this.computeIfAbsent(key, mappingFunction);
   }

   default short computeIfPresent(char key, BiFunction<? super Character, ? super Short, ? extends Short> remappingFunction) {
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

   default short compute(char key, BiFunction<? super Character, ? super Short, ? extends Short> remappingFunction) {
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

   default short merge(char key, short value, BiFunction<? super Short, ? super Short, ? extends Short> remappingFunction) {
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

   default short mergeShort(char key, short value, ShortBinaryOperator remappingFunction) {
      Objects.requireNonNull(remappingFunction);
      short oldValue = this.get(key);
      short drv = this.defaultReturnValue();
      short newValue = oldValue == drv && !this.containsKey(key) ? value : remappingFunction.apply(oldValue, value);
      this.put(key, newValue);
      return newValue;
   }

   default short mergeShort(char key, short value, IntBinaryOperator remappingFunction) {
      return this.mergeShort(
         key,
         value,
         remappingFunction instanceof ShortBinaryOperator
            ? (ShortBinaryOperator)remappingFunction
            : (x, y) -> SafeMath.safeIntToShort(remappingFunction.applyAsInt(x, y))
      );
   }

   @Deprecated
   default Short putIfAbsent(Character key, Short value) {
      return Map.super.putIfAbsent(key, value);
   }

   @Deprecated
   @Override
   default boolean remove(Object key, Object value) {
      return Map.super.remove(key, value);
   }

   @Deprecated
   default boolean replace(Character key, Short oldValue, Short newValue) {
      return Map.super.replace(key, oldValue, newValue);
   }

   @Deprecated
   default Short replace(Character key, Short value) {
      return Map.super.replace(key, value);
   }

   @Deprecated
   default Short computeIfAbsent(Character key, Function<? super Character, ? extends Short> mappingFunction) {
      return Map.super.computeIfAbsent(key, mappingFunction);
   }

   @Deprecated
   default Short computeIfPresent(Character key, BiFunction<? super Character, ? super Short, ? extends Short> remappingFunction) {
      return Map.super.computeIfPresent(key, remappingFunction);
   }

   @Deprecated
   default Short compute(Character key, BiFunction<? super Character, ? super Short, ? extends Short> remappingFunction) {
      return Map.super.compute(key, remappingFunction);
   }

   @Deprecated
   default Short merge(Character key, Short value, BiFunction<? super Short, ? super Short, ? extends Short> remappingFunction) {
      return Map.super.merge(key, value, remappingFunction);
   }

   public interface Entry extends java.util.Map.Entry<Character, Short> {
      char getCharKey();

      @Deprecated
      default Character getKey() {
         return this.getCharKey();
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

   public interface FastEntrySet extends ObjectSet<Char2ShortMap.Entry> {
      ObjectIterator<Char2ShortMap.Entry> fastIterator();

      default void fastForEach(Consumer<? super Char2ShortMap.Entry> consumer) {
         this.forEach(consumer);
      }
   }
}
