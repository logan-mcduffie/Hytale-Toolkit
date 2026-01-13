package it.unimi.dsi.fastutil.doubles;

import it.unimi.dsi.fastutil.SafeMath;
import it.unimi.dsi.fastutil.bytes.ByteBinaryOperator;
import it.unimi.dsi.fastutil.bytes.ByteCollection;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.DoubleFunction;
import java.util.function.DoubleToIntFunction;
import java.util.function.Function;
import java.util.function.IntBinaryOperator;

public interface Double2ByteMap extends Double2ByteFunction, Map<Double, Byte> {
   @Override
   int size();

   @Override
   default void clear() {
      throw new UnsupportedOperationException();
   }

   @Override
   void defaultReturnValue(byte var1);

   @Override
   byte defaultReturnValue();

   ObjectSet<Double2ByteMap.Entry> double2ByteEntrySet();

   @Deprecated
   default ObjectSet<java.util.Map.Entry<Double, Byte>> entrySet() {
      return this.double2ByteEntrySet();
   }

   @Deprecated
   @Override
   default Byte put(Double key, Byte value) {
      return Double2ByteFunction.super.put(key, value);
   }

   @Deprecated
   @Override
   default Byte get(Object key) {
      return Double2ByteFunction.super.get(key);
   }

   @Deprecated
   @Override
   default Byte remove(Object key) {
      return Double2ByteFunction.super.remove(key);
   }

   DoubleSet keySet();

   ByteCollection values();

   @Override
   boolean containsKey(double var1);

   @Deprecated
   @Override
   default boolean containsKey(Object key) {
      return Double2ByteFunction.super.containsKey(key);
   }

   boolean containsValue(byte var1);

   @Deprecated
   @Override
   default boolean containsValue(Object value) {
      return value == null ? false : this.containsValue(((Byte)value).byteValue());
   }

   @Override
   default void forEach(BiConsumer<? super Double, ? super Byte> consumer) {
      ObjectSet<Double2ByteMap.Entry> entrySet = this.double2ByteEntrySet();
      Consumer<Double2ByteMap.Entry> wrappingConsumer = entry -> consumer.accept(entry.getDoubleKey(), entry.getByteValue());
      if (entrySet instanceof Double2ByteMap.FastEntrySet) {
         ((Double2ByteMap.FastEntrySet)entrySet).fastForEach(wrappingConsumer);
      } else {
         entrySet.forEach(wrappingConsumer);
      }
   }

   @Override
   default byte getOrDefault(double key, byte defaultValue) {
      byte v;
      return (v = this.get(key)) == this.defaultReturnValue() && !this.containsKey(key) ? defaultValue : v;
   }

   @Deprecated
   @Override
   default Byte getOrDefault(Object key, Byte defaultValue) {
      return Map.super.getOrDefault(key, defaultValue);
   }

   default byte putIfAbsent(double key, byte value) {
      byte v = this.get(key);
      byte drv = this.defaultReturnValue();
      if (v == drv && !this.containsKey(key)) {
         this.put(key, value);
         return drv;
      } else {
         return v;
      }
   }

   default boolean remove(double key, byte value) {
      byte curValue = this.get(key);
      if (curValue == value && (curValue != this.defaultReturnValue() || this.containsKey(key))) {
         this.remove(key);
         return true;
      } else {
         return false;
      }
   }

   default boolean replace(double key, byte oldValue, byte newValue) {
      byte curValue = this.get(key);
      if (curValue == oldValue && (curValue != this.defaultReturnValue() || this.containsKey(key))) {
         this.put(key, newValue);
         return true;
      } else {
         return false;
      }
   }

   default byte replace(double key, byte value) {
      return this.containsKey(key) ? this.put(key, value) : this.defaultReturnValue();
   }

   default byte computeIfAbsent(double key, DoubleToIntFunction mappingFunction) {
      Objects.requireNonNull(mappingFunction);
      byte v = this.get(key);
      if (v == this.defaultReturnValue() && !this.containsKey(key)) {
         byte newValue = SafeMath.safeIntToByte(mappingFunction.applyAsInt(key));
         this.put(key, newValue);
         return newValue;
      } else {
         return v;
      }
   }

   default byte computeIfAbsentNullable(double key, DoubleFunction<? extends Byte> mappingFunction) {
      Objects.requireNonNull(mappingFunction);
      byte v = this.get(key);
      byte drv = this.defaultReturnValue();
      if (v == drv && !this.containsKey(key)) {
         Byte mappedValue = mappingFunction.apply(key);
         if (mappedValue == null) {
            return drv;
         } else {
            byte newValue = mappedValue;
            this.put(key, newValue);
            return newValue;
         }
      } else {
         return v;
      }
   }

   default byte computeIfAbsent(double key, Double2ByteFunction mappingFunction) {
      Objects.requireNonNull(mappingFunction);
      byte v = this.get(key);
      byte drv = this.defaultReturnValue();
      if (v != drv || this.containsKey(key)) {
         return v;
      } else if (!mappingFunction.containsKey(key)) {
         return drv;
      } else {
         byte newValue = mappingFunction.get(key);
         this.put(key, newValue);
         return newValue;
      }
   }

   @Deprecated
   default byte computeIfAbsentPartial(double key, Double2ByteFunction mappingFunction) {
      return this.computeIfAbsent(key, mappingFunction);
   }

   default byte computeIfPresent(double key, BiFunction<? super Double, ? super Byte, ? extends Byte> remappingFunction) {
      Objects.requireNonNull(remappingFunction);
      byte oldValue = this.get(key);
      byte drv = this.defaultReturnValue();
      if (oldValue == drv && !this.containsKey(key)) {
         return drv;
      } else {
         Byte newValue = remappingFunction.apply(key, oldValue);
         if (newValue == null) {
            this.remove(key);
            return drv;
         } else {
            byte newVal = newValue;
            this.put(key, newVal);
            return newVal;
         }
      }
   }

   default byte compute(double key, BiFunction<? super Double, ? super Byte, ? extends Byte> remappingFunction) {
      Objects.requireNonNull(remappingFunction);
      byte oldValue = this.get(key);
      byte drv = this.defaultReturnValue();
      boolean contained = oldValue != drv || this.containsKey(key);
      Byte newValue = remappingFunction.apply(key, contained ? oldValue : null);
      if (newValue == null) {
         if (contained) {
            this.remove(key);
         }

         return drv;
      } else {
         byte newVal = newValue;
         this.put(key, newVal);
         return newVal;
      }
   }

   default byte merge(double key, byte value, BiFunction<? super Byte, ? super Byte, ? extends Byte> remappingFunction) {
      Objects.requireNonNull(remappingFunction);
      byte oldValue = this.get(key);
      byte drv = this.defaultReturnValue();
      byte newValue;
      if (oldValue == drv && !this.containsKey(key)) {
         newValue = value;
      } else {
         Byte mergedValue = remappingFunction.apply(oldValue, value);
         if (mergedValue == null) {
            this.remove(key);
            return drv;
         }

         newValue = mergedValue;
      }

      this.put(key, newValue);
      return newValue;
   }

   default byte mergeByte(double key, byte value, ByteBinaryOperator remappingFunction) {
      Objects.requireNonNull(remappingFunction);
      byte oldValue = this.get(key);
      byte drv = this.defaultReturnValue();
      byte newValue = oldValue == drv && !this.containsKey(key) ? value : remappingFunction.apply(oldValue, value);
      this.put(key, newValue);
      return newValue;
   }

   default byte mergeByte(double key, byte value, IntBinaryOperator remappingFunction) {
      return this.mergeByte(
         key,
         value,
         remappingFunction instanceof ByteBinaryOperator
            ? (ByteBinaryOperator)remappingFunction
            : (x, y) -> SafeMath.safeIntToByte(remappingFunction.applyAsInt(x, y))
      );
   }

   @Deprecated
   default Byte putIfAbsent(Double key, Byte value) {
      return Map.super.putIfAbsent(key, value);
   }

   @Deprecated
   @Override
   default boolean remove(Object key, Object value) {
      return Map.super.remove(key, value);
   }

   @Deprecated
   default boolean replace(Double key, Byte oldValue, Byte newValue) {
      return Map.super.replace(key, oldValue, newValue);
   }

   @Deprecated
   default Byte replace(Double key, Byte value) {
      return Map.super.replace(key, value);
   }

   @Deprecated
   default Byte computeIfAbsent(Double key, Function<? super Double, ? extends Byte> mappingFunction) {
      return Map.super.computeIfAbsent(key, mappingFunction);
   }

   @Deprecated
   default Byte computeIfPresent(Double key, BiFunction<? super Double, ? super Byte, ? extends Byte> remappingFunction) {
      return Map.super.computeIfPresent(key, remappingFunction);
   }

   @Deprecated
   default Byte compute(Double key, BiFunction<? super Double, ? super Byte, ? extends Byte> remappingFunction) {
      return Map.super.compute(key, remappingFunction);
   }

   @Deprecated
   default Byte merge(Double key, Byte value, BiFunction<? super Byte, ? super Byte, ? extends Byte> remappingFunction) {
      return Map.super.merge(key, value, remappingFunction);
   }

   public interface Entry extends java.util.Map.Entry<Double, Byte> {
      double getDoubleKey();

      @Deprecated
      default Double getKey() {
         return this.getDoubleKey();
      }

      byte getByteValue();

      byte setValue(byte var1);

      @Deprecated
      default Byte getValue() {
         return this.getByteValue();
      }

      @Deprecated
      default Byte setValue(Byte value) {
         return this.setValue(value.byteValue());
      }
   }

   public interface FastEntrySet extends ObjectSet<Double2ByteMap.Entry> {
      ObjectIterator<Double2ByteMap.Entry> fastIterator();

      default void fastForEach(Consumer<? super Double2ByteMap.Entry> consumer) {
         this.forEach(consumer);
      }
   }
}
