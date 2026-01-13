package it.unimi.dsi.fastutil.bytes;

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

public interface Byte2ShortMap extends Byte2ShortFunction, Map<Byte, Short> {
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

   ObjectSet<Byte2ShortMap.Entry> byte2ShortEntrySet();

   @Deprecated
   default ObjectSet<java.util.Map.Entry<Byte, Short>> entrySet() {
      return this.byte2ShortEntrySet();
   }

   @Deprecated
   @Override
   default Short put(Byte key, Short value) {
      return Byte2ShortFunction.super.put(key, value);
   }

   @Deprecated
   @Override
   default Short get(Object key) {
      return Byte2ShortFunction.super.get(key);
   }

   @Deprecated
   @Override
   default Short remove(Object key) {
      return Byte2ShortFunction.super.remove(key);
   }

   ByteSet keySet();

   ShortCollection values();

   @Override
   boolean containsKey(byte var1);

   @Deprecated
   @Override
   default boolean containsKey(Object key) {
      return Byte2ShortFunction.super.containsKey(key);
   }

   boolean containsValue(short var1);

   @Deprecated
   @Override
   default boolean containsValue(Object value) {
      return value == null ? false : this.containsValue(((Short)value).shortValue());
   }

   @Override
   default void forEach(BiConsumer<? super Byte, ? super Short> consumer) {
      ObjectSet<Byte2ShortMap.Entry> entrySet = this.byte2ShortEntrySet();
      Consumer<Byte2ShortMap.Entry> wrappingConsumer = entry -> consumer.accept(entry.getByteKey(), entry.getShortValue());
      if (entrySet instanceof Byte2ShortMap.FastEntrySet) {
         ((Byte2ShortMap.FastEntrySet)entrySet).fastForEach(wrappingConsumer);
      } else {
         entrySet.forEach(wrappingConsumer);
      }
   }

   @Override
   default short getOrDefault(byte key, short defaultValue) {
      short v;
      return (v = this.get(key)) == this.defaultReturnValue() && !this.containsKey(key) ? defaultValue : v;
   }

   @Deprecated
   @Override
   default Short getOrDefault(Object key, Short defaultValue) {
      return Map.super.getOrDefault(key, defaultValue);
   }

   default short putIfAbsent(byte key, short value) {
      short v = this.get(key);
      short drv = this.defaultReturnValue();
      if (v == drv && !this.containsKey(key)) {
         this.put(key, value);
         return drv;
      } else {
         return v;
      }
   }

   default boolean remove(byte key, short value) {
      short curValue = this.get(key);
      if (curValue == value && (curValue != this.defaultReturnValue() || this.containsKey(key))) {
         this.remove(key);
         return true;
      } else {
         return false;
      }
   }

   default boolean replace(byte key, short oldValue, short newValue) {
      short curValue = this.get(key);
      if (curValue == oldValue && (curValue != this.defaultReturnValue() || this.containsKey(key))) {
         this.put(key, newValue);
         return true;
      } else {
         return false;
      }
   }

   default short replace(byte key, short value) {
      return this.containsKey(key) ? this.put(key, value) : this.defaultReturnValue();
   }

   default short computeIfAbsent(byte key, IntUnaryOperator mappingFunction) {
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

   default short computeIfAbsentNullable(byte key, IntFunction<? extends Short> mappingFunction) {
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

   default short computeIfAbsent(byte key, Byte2ShortFunction mappingFunction) {
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
   default short computeIfAbsentPartial(byte key, Byte2ShortFunction mappingFunction) {
      return this.computeIfAbsent(key, mappingFunction);
   }

   default short computeIfPresent(byte key, BiFunction<? super Byte, ? super Short, ? extends Short> remappingFunction) {
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

   default short compute(byte key, BiFunction<? super Byte, ? super Short, ? extends Short> remappingFunction) {
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

   default short merge(byte key, short value, BiFunction<? super Short, ? super Short, ? extends Short> remappingFunction) {
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

   default short mergeShort(byte key, short value, ShortBinaryOperator remappingFunction) {
      Objects.requireNonNull(remappingFunction);
      short oldValue = this.get(key);
      short drv = this.defaultReturnValue();
      short newValue = oldValue == drv && !this.containsKey(key) ? value : remappingFunction.apply(oldValue, value);
      this.put(key, newValue);
      return newValue;
   }

   default short mergeShort(byte key, short value, IntBinaryOperator remappingFunction) {
      return this.mergeShort(
         key,
         value,
         remappingFunction instanceof ShortBinaryOperator
            ? (ShortBinaryOperator)remappingFunction
            : (x, y) -> SafeMath.safeIntToShort(remappingFunction.applyAsInt(x, y))
      );
   }

   @Deprecated
   default Short putIfAbsent(Byte key, Short value) {
      return Map.super.putIfAbsent(key, value);
   }

   @Deprecated
   @Override
   default boolean remove(Object key, Object value) {
      return Map.super.remove(key, value);
   }

   @Deprecated
   default boolean replace(Byte key, Short oldValue, Short newValue) {
      return Map.super.replace(key, oldValue, newValue);
   }

   @Deprecated
   default Short replace(Byte key, Short value) {
      return Map.super.replace(key, value);
   }

   @Deprecated
   default Short computeIfAbsent(Byte key, Function<? super Byte, ? extends Short> mappingFunction) {
      return Map.super.computeIfAbsent(key, mappingFunction);
   }

   @Deprecated
   default Short computeIfPresent(Byte key, BiFunction<? super Byte, ? super Short, ? extends Short> remappingFunction) {
      return Map.super.computeIfPresent(key, remappingFunction);
   }

   @Deprecated
   default Short compute(Byte key, BiFunction<? super Byte, ? super Short, ? extends Short> remappingFunction) {
      return Map.super.compute(key, remappingFunction);
   }

   @Deprecated
   default Short merge(Byte key, Short value, BiFunction<? super Short, ? super Short, ? extends Short> remappingFunction) {
      return Map.super.merge(key, value, remappingFunction);
   }

   public interface Entry extends java.util.Map.Entry<Byte, Short> {
      byte getByteKey();

      @Deprecated
      default Byte getKey() {
         return this.getByteKey();
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

   public interface FastEntrySet extends ObjectSet<Byte2ShortMap.Entry> {
      ObjectIterator<Byte2ShortMap.Entry> fastIterator();

      default void fastForEach(Consumer<? super Byte2ShortMap.Entry> consumer) {
         this.forEach(consumer);
      }
   }
}
