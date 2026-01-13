package it.unimi.dsi.fastutil.doubles;

import it.unimi.dsi.fastutil.bytes.Byte2DoubleFunction;
import it.unimi.dsi.fastutil.chars.Char2DoubleFunction;
import it.unimi.dsi.fastutil.floats.Float2DoubleFunction;
import it.unimi.dsi.fastutil.ints.Int2DoubleFunction;
import it.unimi.dsi.fastutil.longs.Long2DoubleFunction;
import it.unimi.dsi.fastutil.objects.Object2DoubleFunction;
import it.unimi.dsi.fastutil.objects.Reference2DoubleFunction;
import it.unimi.dsi.fastutil.shorts.Short2DoubleFunction;
import java.util.function.Function;

@FunctionalInterface
public interface Double2DoubleFunction extends it.unimi.dsi.fastutil.Function<Double, Double>, java.util.function.DoubleUnaryOperator {
   @Override
   default double applyAsDouble(double operand) {
      return this.get(operand);
   }

   default double put(double key, double value) {
      throw new UnsupportedOperationException();
   }

   double get(double var1);

   default double getOrDefault(double key, double defaultValue) {
      double v;
      return (v = this.get(key)) == this.defaultReturnValue() && !this.containsKey(key) ? defaultValue : v;
   }

   default double remove(double key) {
      throw new UnsupportedOperationException();
   }

   @Deprecated
   default Double put(Double key, Double value) {
      double k = key;
      boolean containsKey = this.containsKey(k);
      double v = this.put(k, value.doubleValue());
      return containsKey ? v : null;
   }

   @Deprecated
   default Double get(Object key) {
      if (key == null) {
         return null;
      } else {
         double k = (Double)key;
         double v;
         return (v = this.get(k)) == this.defaultReturnValue() && !this.containsKey(k) ? null : v;
      }
   }

   @Deprecated
   default Double getOrDefault(Object key, Double defaultValue) {
      if (key == null) {
         return defaultValue;
      } else {
         double k = (Double)key;
         double v = this.get(k);
         return v == this.defaultReturnValue() && !this.containsKey(k) ? defaultValue : v;
      }
   }

   @Deprecated
   default Double remove(Object key) {
      if (key == null) {
         return null;
      } else {
         double k = (Double)key;
         return this.containsKey(k) ? this.remove(k) : null;
      }
   }

   default boolean containsKey(double key) {
      return true;
   }

   @Deprecated
   @Override
   default boolean containsKey(Object key) {
      return key == null ? false : this.containsKey(((Double)key).doubleValue());
   }

   default void defaultReturnValue(double rv) {
      throw new UnsupportedOperationException();
   }

   default double defaultReturnValue() {
      return 0.0;
   }

   static Double2DoubleFunction identity() {
      return k -> k;
   }

   @Deprecated
   @Override
   default <T> Function<T, Double> compose(Function<? super T, ? extends Double> before) {
      return it.unimi.dsi.fastutil.Function.super.compose(before);
   }

   @Deprecated
   @Override
   default <T> Function<Double, T> andThen(Function<? super Double, ? extends T> after) {
      return it.unimi.dsi.fastutil.Function.super.andThen(after);
   }

   default Double2ByteFunction andThenByte(Double2ByteFunction after) {
      return k -> after.get(this.get(k));
   }

   default Byte2DoubleFunction composeByte(Byte2DoubleFunction before) {
      return k -> this.get(before.get(k));
   }

   default Double2ShortFunction andThenShort(Double2ShortFunction after) {
      return k -> after.get(this.get(k));
   }

   default Short2DoubleFunction composeShort(Short2DoubleFunction before) {
      return k -> this.get(before.get(k));
   }

   default Double2IntFunction andThenInt(Double2IntFunction after) {
      return k -> after.get(this.get(k));
   }

   default Int2DoubleFunction composeInt(Int2DoubleFunction before) {
      return k -> this.get(before.get(k));
   }

   default Double2LongFunction andThenLong(Double2LongFunction after) {
      return k -> after.get(this.get(k));
   }

   default Long2DoubleFunction composeLong(Long2DoubleFunction before) {
      return k -> this.get(before.get(k));
   }

   default Double2CharFunction andThenChar(Double2CharFunction after) {
      return k -> after.get(this.get(k));
   }

   default Char2DoubleFunction composeChar(Char2DoubleFunction before) {
      return k -> this.get(before.get(k));
   }

   default Double2FloatFunction andThenFloat(Double2FloatFunction after) {
      return k -> after.get(this.get(k));
   }

   default Float2DoubleFunction composeFloat(Float2DoubleFunction before) {
      return k -> this.get(before.get(k));
   }

   default Double2DoubleFunction andThenDouble(Double2DoubleFunction after) {
      return k -> after.get(this.get(k));
   }

   default Double2DoubleFunction composeDouble(Double2DoubleFunction before) {
      return k -> this.get(before.get(k));
   }

   default <T> Double2ObjectFunction<T> andThenObject(Double2ObjectFunction<? extends T> after) {
      return k -> (T)after.get(this.get(k));
   }

   default <T> Object2DoubleFunction<T> composeObject(Object2DoubleFunction<? super T> before) {
      return k -> this.get(before.getDouble(k));
   }

   default <T> Double2ReferenceFunction<T> andThenReference(Double2ReferenceFunction<? extends T> after) {
      return k -> (T)after.get(this.get(k));
   }

   default <T> Reference2DoubleFunction<T> composeReference(Reference2DoubleFunction<? super T> before) {
      return k -> this.get(before.getDouble(k));
   }
}
