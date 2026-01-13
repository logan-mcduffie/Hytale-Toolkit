package it.unimi.dsi.fastutil.floats;

import it.unimi.dsi.fastutil.SafeMath;
import it.unimi.dsi.fastutil.bytes.Byte2ByteFunction;
import it.unimi.dsi.fastutil.bytes.Byte2CharFunction;
import it.unimi.dsi.fastutil.bytes.Byte2DoubleFunction;
import it.unimi.dsi.fastutil.bytes.Byte2FloatFunction;
import it.unimi.dsi.fastutil.bytes.Byte2IntFunction;
import it.unimi.dsi.fastutil.bytes.Byte2LongFunction;
import it.unimi.dsi.fastutil.bytes.Byte2ObjectFunction;
import it.unimi.dsi.fastutil.bytes.Byte2ReferenceFunction;
import it.unimi.dsi.fastutil.bytes.Byte2ShortFunction;
import it.unimi.dsi.fastutil.chars.Char2ByteFunction;
import it.unimi.dsi.fastutil.chars.Char2FloatFunction;
import it.unimi.dsi.fastutil.doubles.Double2ByteFunction;
import it.unimi.dsi.fastutil.doubles.Double2FloatFunction;
import it.unimi.dsi.fastutil.ints.Int2ByteFunction;
import it.unimi.dsi.fastutil.ints.Int2FloatFunction;
import it.unimi.dsi.fastutil.longs.Long2ByteFunction;
import it.unimi.dsi.fastutil.longs.Long2FloatFunction;
import it.unimi.dsi.fastutil.objects.Object2ByteFunction;
import it.unimi.dsi.fastutil.objects.Object2FloatFunction;
import it.unimi.dsi.fastutil.objects.Reference2ByteFunction;
import it.unimi.dsi.fastutil.objects.Reference2FloatFunction;
import it.unimi.dsi.fastutil.shorts.Short2ByteFunction;
import it.unimi.dsi.fastutil.shorts.Short2FloatFunction;
import java.util.function.DoubleToIntFunction;
import java.util.function.Function;

@FunctionalInterface
public interface Float2ByteFunction extends it.unimi.dsi.fastutil.Function<Float, Byte>, DoubleToIntFunction {
   @Deprecated
   @Override
   default int applyAsInt(double operand) {
      return this.get(SafeMath.safeDoubleToFloat(operand));
   }

   default byte put(float key, byte value) {
      throw new UnsupportedOperationException();
   }

   byte get(float var1);

   default byte getOrDefault(float key, byte defaultValue) {
      byte v;
      return (v = this.get(key)) == this.defaultReturnValue() && !this.containsKey(key) ? defaultValue : v;
   }

   default byte remove(float key) {
      throw new UnsupportedOperationException();
   }

   @Deprecated
   default Byte put(Float key, Byte value) {
      float k = key;
      boolean containsKey = this.containsKey(k);
      byte v = this.put(k, value.byteValue());
      return containsKey ? v : null;
   }

   @Deprecated
   default Byte get(Object key) {
      if (key == null) {
         return null;
      } else {
         float k = (Float)key;
         byte v;
         return (v = this.get(k)) == this.defaultReturnValue() && !this.containsKey(k) ? null : v;
      }
   }

   @Deprecated
   default Byte getOrDefault(Object key, Byte defaultValue) {
      if (key == null) {
         return defaultValue;
      } else {
         float k = (Float)key;
         byte v = this.get(k);
         return v == this.defaultReturnValue() && !this.containsKey(k) ? defaultValue : v;
      }
   }

   @Deprecated
   default Byte remove(Object key) {
      if (key == null) {
         return null;
      } else {
         float k = (Float)key;
         return this.containsKey(k) ? this.remove(k) : null;
      }
   }

   default boolean containsKey(float key) {
      return true;
   }

   @Deprecated
   @Override
   default boolean containsKey(Object key) {
      return key == null ? false : this.containsKey(((Float)key).floatValue());
   }

   default void defaultReturnValue(byte rv) {
      throw new UnsupportedOperationException();
   }

   default byte defaultReturnValue() {
      return 0;
   }

   @Deprecated
   @Override
   default <T> Function<T, Byte> compose(Function<? super T, ? extends Float> before) {
      return it.unimi.dsi.fastutil.Function.super.compose(before);
   }

   @Deprecated
   @Override
   default <T> Function<Float, T> andThen(Function<? super Byte, ? extends T> after) {
      return it.unimi.dsi.fastutil.Function.super.andThen(after);
   }

   default Float2ByteFunction andThenByte(Byte2ByteFunction after) {
      return k -> after.get(this.get(k));
   }

   default Byte2ByteFunction composeByte(Byte2FloatFunction before) {
      return k -> this.get(before.get(k));
   }

   default Float2ShortFunction andThenShort(Byte2ShortFunction after) {
      return k -> after.get(this.get(k));
   }

   default Short2ByteFunction composeShort(Short2FloatFunction before) {
      return k -> this.get(before.get(k));
   }

   default Float2IntFunction andThenInt(Byte2IntFunction after) {
      return k -> after.get(this.get(k));
   }

   default Int2ByteFunction composeInt(Int2FloatFunction before) {
      return k -> this.get(before.get(k));
   }

   default Float2LongFunction andThenLong(Byte2LongFunction after) {
      return k -> after.get(this.get(k));
   }

   default Long2ByteFunction composeLong(Long2FloatFunction before) {
      return k -> this.get(before.get(k));
   }

   default Float2CharFunction andThenChar(Byte2CharFunction after) {
      return k -> after.get(this.get(k));
   }

   default Char2ByteFunction composeChar(Char2FloatFunction before) {
      return k -> this.get(before.get(k));
   }

   default Float2FloatFunction andThenFloat(Byte2FloatFunction after) {
      return k -> after.get(this.get(k));
   }

   default Float2ByteFunction composeFloat(Float2FloatFunction before) {
      return k -> this.get(before.get(k));
   }

   default Float2DoubleFunction andThenDouble(Byte2DoubleFunction after) {
      return k -> after.get(this.get(k));
   }

   default Double2ByteFunction composeDouble(Double2FloatFunction before) {
      return k -> this.get(before.get(k));
   }

   default <T> Float2ObjectFunction<T> andThenObject(Byte2ObjectFunction<? extends T> after) {
      return k -> (T)after.get(this.get(k));
   }

   default <T> Object2ByteFunction<T> composeObject(Object2FloatFunction<? super T> before) {
      return k -> this.get(before.getFloat(k));
   }

   default <T> Float2ReferenceFunction<T> andThenReference(Byte2ReferenceFunction<? extends T> after) {
      return k -> (T)after.get(this.get(k));
   }

   default <T> Reference2ByteFunction<T> composeReference(Reference2FloatFunction<? super T> before) {
      return k -> this.get(before.getFloat(k));
   }
}
