package it.unimi.dsi.fastutil.doubles;

import it.unimi.dsi.fastutil.bytes.Byte2DoubleFunction;
import it.unimi.dsi.fastutil.bytes.Byte2LongFunction;
import it.unimi.dsi.fastutil.chars.Char2DoubleFunction;
import it.unimi.dsi.fastutil.chars.Char2LongFunction;
import it.unimi.dsi.fastutil.floats.Float2DoubleFunction;
import it.unimi.dsi.fastutil.floats.Float2LongFunction;
import it.unimi.dsi.fastutil.ints.Int2DoubleFunction;
import it.unimi.dsi.fastutil.ints.Int2LongFunction;
import it.unimi.dsi.fastutil.longs.Long2ByteFunction;
import it.unimi.dsi.fastutil.longs.Long2CharFunction;
import it.unimi.dsi.fastutil.longs.Long2DoubleFunction;
import it.unimi.dsi.fastutil.longs.Long2FloatFunction;
import it.unimi.dsi.fastutil.longs.Long2IntFunction;
import it.unimi.dsi.fastutil.longs.Long2LongFunction;
import it.unimi.dsi.fastutil.longs.Long2ObjectFunction;
import it.unimi.dsi.fastutil.longs.Long2ReferenceFunction;
import it.unimi.dsi.fastutil.longs.Long2ShortFunction;
import it.unimi.dsi.fastutil.objects.Object2DoubleFunction;
import it.unimi.dsi.fastutil.objects.Object2LongFunction;
import it.unimi.dsi.fastutil.objects.Reference2DoubleFunction;
import it.unimi.dsi.fastutil.objects.Reference2LongFunction;
import it.unimi.dsi.fastutil.shorts.Short2DoubleFunction;
import it.unimi.dsi.fastutil.shorts.Short2LongFunction;
import java.util.function.DoubleToLongFunction;
import java.util.function.Function;

@FunctionalInterface
public interface Double2LongFunction extends it.unimi.dsi.fastutil.Function<Double, Long>, DoubleToLongFunction {
   @Override
   default long applyAsLong(double operand) {
      return this.get(operand);
   }

   default long put(double key, long value) {
      throw new UnsupportedOperationException();
   }

   long get(double var1);

   default long getOrDefault(double key, long defaultValue) {
      long v;
      return (v = this.get(key)) == this.defaultReturnValue() && !this.containsKey(key) ? defaultValue : v;
   }

   default long remove(double key) {
      throw new UnsupportedOperationException();
   }

   @Deprecated
   default Long put(Double key, Long value) {
      double k = key;
      boolean containsKey = this.containsKey(k);
      long v = this.put(k, value.longValue());
      return containsKey ? v : null;
   }

   @Deprecated
   default Long get(Object key) {
      if (key == null) {
         return null;
      } else {
         double k = (Double)key;
         long v;
         return (v = this.get(k)) == this.defaultReturnValue() && !this.containsKey(k) ? null : v;
      }
   }

   @Deprecated
   default Long getOrDefault(Object key, Long defaultValue) {
      if (key == null) {
         return defaultValue;
      } else {
         double k = (Double)key;
         long v = this.get(k);
         return v == this.defaultReturnValue() && !this.containsKey(k) ? defaultValue : v;
      }
   }

   @Deprecated
   default Long remove(Object key) {
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

   default void defaultReturnValue(long rv) {
      throw new UnsupportedOperationException();
   }

   default long defaultReturnValue() {
      return 0L;
   }

   @Deprecated
   @Override
   default <T> Function<T, Long> compose(Function<? super T, ? extends Double> before) {
      return it.unimi.dsi.fastutil.Function.super.compose(before);
   }

   @Deprecated
   @Override
   default <T> Function<Double, T> andThen(Function<? super Long, ? extends T> after) {
      return it.unimi.dsi.fastutil.Function.super.andThen(after);
   }

   default Double2ByteFunction andThenByte(Long2ByteFunction after) {
      return k -> after.get(this.get(k));
   }

   default Byte2LongFunction composeByte(Byte2DoubleFunction before) {
      return k -> this.get(before.get(k));
   }

   default Double2ShortFunction andThenShort(Long2ShortFunction after) {
      return k -> after.get(this.get(k));
   }

   default Short2LongFunction composeShort(Short2DoubleFunction before) {
      return k -> this.get(before.get(k));
   }

   default Double2IntFunction andThenInt(Long2IntFunction after) {
      return k -> after.get(this.get(k));
   }

   default Int2LongFunction composeInt(Int2DoubleFunction before) {
      return k -> this.get(before.get(k));
   }

   default Double2LongFunction andThenLong(Long2LongFunction after) {
      return k -> after.get(this.get(k));
   }

   default Long2LongFunction composeLong(Long2DoubleFunction before) {
      return k -> this.get(before.get(k));
   }

   default Double2CharFunction andThenChar(Long2CharFunction after) {
      return k -> after.get(this.get(k));
   }

   default Char2LongFunction composeChar(Char2DoubleFunction before) {
      return k -> this.get(before.get(k));
   }

   default Double2FloatFunction andThenFloat(Long2FloatFunction after) {
      return k -> after.get(this.get(k));
   }

   default Float2LongFunction composeFloat(Float2DoubleFunction before) {
      return k -> this.get(before.get(k));
   }

   default Double2DoubleFunction andThenDouble(Long2DoubleFunction after) {
      return k -> after.get(this.get(k));
   }

   default Double2LongFunction composeDouble(Double2DoubleFunction before) {
      return k -> this.get(before.get(k));
   }

   default <T> Double2ObjectFunction<T> andThenObject(Long2ObjectFunction<? extends T> after) {
      return k -> (T)after.get(this.get(k));
   }

   default <T> Object2LongFunction<T> composeObject(Object2DoubleFunction<? super T> before) {
      return k -> this.get(before.getDouble(k));
   }

   default <T> Double2ReferenceFunction<T> andThenReference(Long2ReferenceFunction<? extends T> after) {
      return k -> (T)after.get(this.get(k));
   }

   default <T> Reference2LongFunction<T> composeReference(Reference2DoubleFunction<? super T> before) {
      return k -> this.get(before.getDouble(k));
   }
}
