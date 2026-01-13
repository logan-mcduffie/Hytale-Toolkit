package it.unimi.dsi.fastutil.bytes;

import it.unimi.dsi.fastutil.SafeMath;
import it.unimi.dsi.fastutil.chars.Char2ByteFunction;
import it.unimi.dsi.fastutil.chars.Char2DoubleFunction;
import it.unimi.dsi.fastutil.doubles.Double2ByteFunction;
import it.unimi.dsi.fastutil.doubles.Double2CharFunction;
import it.unimi.dsi.fastutil.doubles.Double2DoubleFunction;
import it.unimi.dsi.fastutil.doubles.Double2FloatFunction;
import it.unimi.dsi.fastutil.doubles.Double2IntFunction;
import it.unimi.dsi.fastutil.doubles.Double2LongFunction;
import it.unimi.dsi.fastutil.doubles.Double2ObjectFunction;
import it.unimi.dsi.fastutil.doubles.Double2ReferenceFunction;
import it.unimi.dsi.fastutil.doubles.Double2ShortFunction;
import it.unimi.dsi.fastutil.floats.Float2ByteFunction;
import it.unimi.dsi.fastutil.floats.Float2DoubleFunction;
import it.unimi.dsi.fastutil.ints.Int2ByteFunction;
import it.unimi.dsi.fastutil.ints.Int2DoubleFunction;
import it.unimi.dsi.fastutil.longs.Long2ByteFunction;
import it.unimi.dsi.fastutil.longs.Long2DoubleFunction;
import it.unimi.dsi.fastutil.objects.Object2ByteFunction;
import it.unimi.dsi.fastutil.objects.Object2DoubleFunction;
import it.unimi.dsi.fastutil.objects.Reference2ByteFunction;
import it.unimi.dsi.fastutil.objects.Reference2DoubleFunction;
import it.unimi.dsi.fastutil.shorts.Short2ByteFunction;
import it.unimi.dsi.fastutil.shorts.Short2DoubleFunction;
import java.util.function.Function;
import java.util.function.IntToDoubleFunction;

@FunctionalInterface
public interface Byte2DoubleFunction extends it.unimi.dsi.fastutil.Function<Byte, Double>, IntToDoubleFunction {
   @Deprecated
   @Override
   default double applyAsDouble(int operand) {
      return this.get(SafeMath.safeIntToByte(operand));
   }

   default double put(byte key, double value) {
      throw new UnsupportedOperationException();
   }

   double get(byte var1);

   default double getOrDefault(byte key, double defaultValue) {
      double v;
      return (v = this.get(key)) == this.defaultReturnValue() && !this.containsKey(key) ? defaultValue : v;
   }

   default double remove(byte key) {
      throw new UnsupportedOperationException();
   }

   @Deprecated
   default Double put(Byte key, Double value) {
      byte k = key;
      boolean containsKey = this.containsKey(k);
      double v = this.put(k, value.doubleValue());
      return containsKey ? v : null;
   }

   @Deprecated
   default Double get(Object key) {
      if (key == null) {
         return null;
      } else {
         byte k = (Byte)key;
         double v;
         return (v = this.get(k)) == this.defaultReturnValue() && !this.containsKey(k) ? null : v;
      }
   }

   @Deprecated
   default Double getOrDefault(Object key, Double defaultValue) {
      if (key == null) {
         return defaultValue;
      } else {
         byte k = (Byte)key;
         double v = this.get(k);
         return v == this.defaultReturnValue() && !this.containsKey(k) ? defaultValue : v;
      }
   }

   @Deprecated
   default Double remove(Object key) {
      if (key == null) {
         return null;
      } else {
         byte k = (Byte)key;
         return this.containsKey(k) ? this.remove(k) : null;
      }
   }

   default boolean containsKey(byte key) {
      return true;
   }

   @Deprecated
   @Override
   default boolean containsKey(Object key) {
      return key == null ? false : this.containsKey(((Byte)key).byteValue());
   }

   default void defaultReturnValue(double rv) {
      throw new UnsupportedOperationException();
   }

   default double defaultReturnValue() {
      return 0.0;
   }

   @Deprecated
   @Override
   default <T> Function<T, Double> compose(Function<? super T, ? extends Byte> before) {
      return it.unimi.dsi.fastutil.Function.super.compose(before);
   }

   @Deprecated
   @Override
   default <T> Function<Byte, T> andThen(Function<? super Double, ? extends T> after) {
      return it.unimi.dsi.fastutil.Function.super.andThen(after);
   }

   default Byte2ByteFunction andThenByte(Double2ByteFunction after) {
      return k -> after.get(this.get(k));
   }

   default Byte2DoubleFunction composeByte(Byte2ByteFunction before) {
      return k -> this.get(before.get(k));
   }

   default Byte2ShortFunction andThenShort(Double2ShortFunction after) {
      return k -> after.get(this.get(k));
   }

   default Short2DoubleFunction composeShort(Short2ByteFunction before) {
      return k -> this.get(before.get(k));
   }

   default Byte2IntFunction andThenInt(Double2IntFunction after) {
      return k -> after.get(this.get(k));
   }

   default Int2DoubleFunction composeInt(Int2ByteFunction before) {
      return k -> this.get(before.get(k));
   }

   default Byte2LongFunction andThenLong(Double2LongFunction after) {
      return k -> after.get(this.get(k));
   }

   default Long2DoubleFunction composeLong(Long2ByteFunction before) {
      return k -> this.get(before.get(k));
   }

   default Byte2CharFunction andThenChar(Double2CharFunction after) {
      return k -> after.get(this.get(k));
   }

   default Char2DoubleFunction composeChar(Char2ByteFunction before) {
      return k -> this.get(before.get(k));
   }

   default Byte2FloatFunction andThenFloat(Double2FloatFunction after) {
      return k -> after.get(this.get(k));
   }

   default Float2DoubleFunction composeFloat(Float2ByteFunction before) {
      return k -> this.get(before.get(k));
   }

   default Byte2DoubleFunction andThenDouble(Double2DoubleFunction after) {
      return k -> after.get(this.get(k));
   }

   default Double2DoubleFunction composeDouble(Double2ByteFunction before) {
      return k -> this.get(before.get(k));
   }

   default <T> Byte2ObjectFunction<T> andThenObject(Double2ObjectFunction<? extends T> after) {
      return k -> (T)after.get(this.get(k));
   }

   default <T> Object2DoubleFunction<T> composeObject(Object2ByteFunction<? super T> before) {
      return k -> this.get(before.getByte(k));
   }

   default <T> Byte2ReferenceFunction<T> andThenReference(Double2ReferenceFunction<? extends T> after) {
      return k -> (T)after.get(this.get(k));
   }

   default <T> Reference2DoubleFunction<T> composeReference(Reference2ByteFunction<? super T> before) {
      return k -> this.get(before.getByte(k));
   }
}
