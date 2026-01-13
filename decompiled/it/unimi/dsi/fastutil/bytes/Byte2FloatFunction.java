package it.unimi.dsi.fastutil.bytes;

import it.unimi.dsi.fastutil.SafeMath;
import it.unimi.dsi.fastutil.chars.Char2ByteFunction;
import it.unimi.dsi.fastutil.chars.Char2FloatFunction;
import it.unimi.dsi.fastutil.doubles.Double2ByteFunction;
import it.unimi.dsi.fastutil.doubles.Double2FloatFunction;
import it.unimi.dsi.fastutil.floats.Float2ByteFunction;
import it.unimi.dsi.fastutil.floats.Float2CharFunction;
import it.unimi.dsi.fastutil.floats.Float2DoubleFunction;
import it.unimi.dsi.fastutil.floats.Float2FloatFunction;
import it.unimi.dsi.fastutil.floats.Float2IntFunction;
import it.unimi.dsi.fastutil.floats.Float2LongFunction;
import it.unimi.dsi.fastutil.floats.Float2ObjectFunction;
import it.unimi.dsi.fastutil.floats.Float2ReferenceFunction;
import it.unimi.dsi.fastutil.floats.Float2ShortFunction;
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
import java.util.function.Function;
import java.util.function.IntToDoubleFunction;

@FunctionalInterface
public interface Byte2FloatFunction extends it.unimi.dsi.fastutil.Function<Byte, Float>, IntToDoubleFunction {
   @Deprecated
   @Override
   default double applyAsDouble(int operand) {
      return this.get(SafeMath.safeIntToByte(operand));
   }

   default float put(byte key, float value) {
      throw new UnsupportedOperationException();
   }

   float get(byte var1);

   default float getOrDefault(byte key, float defaultValue) {
      float v;
      return (v = this.get(key)) == this.defaultReturnValue() && !this.containsKey(key) ? defaultValue : v;
   }

   default float remove(byte key) {
      throw new UnsupportedOperationException();
   }

   @Deprecated
   default Float put(Byte key, Float value) {
      byte k = key;
      boolean containsKey = this.containsKey(k);
      float v = this.put(k, value.floatValue());
      return containsKey ? v : null;
   }

   @Deprecated
   default Float get(Object key) {
      if (key == null) {
         return null;
      } else {
         byte k = (Byte)key;
         float v;
         return (v = this.get(k)) == this.defaultReturnValue() && !this.containsKey(k) ? null : v;
      }
   }

   @Deprecated
   default Float getOrDefault(Object key, Float defaultValue) {
      if (key == null) {
         return defaultValue;
      } else {
         byte k = (Byte)key;
         float v = this.get(k);
         return v == this.defaultReturnValue() && !this.containsKey(k) ? defaultValue : v;
      }
   }

   @Deprecated
   default Float remove(Object key) {
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

   default void defaultReturnValue(float rv) {
      throw new UnsupportedOperationException();
   }

   default float defaultReturnValue() {
      return 0.0F;
   }

   @Deprecated
   @Override
   default <T> Function<T, Float> compose(Function<? super T, ? extends Byte> before) {
      return it.unimi.dsi.fastutil.Function.super.compose(before);
   }

   @Deprecated
   @Override
   default <T> Function<Byte, T> andThen(Function<? super Float, ? extends T> after) {
      return it.unimi.dsi.fastutil.Function.super.andThen(after);
   }

   default Byte2ByteFunction andThenByte(Float2ByteFunction after) {
      return k -> after.get(this.get(k));
   }

   default Byte2FloatFunction composeByte(Byte2ByteFunction before) {
      return k -> this.get(before.get(k));
   }

   default Byte2ShortFunction andThenShort(Float2ShortFunction after) {
      return k -> after.get(this.get(k));
   }

   default Short2FloatFunction composeShort(Short2ByteFunction before) {
      return k -> this.get(before.get(k));
   }

   default Byte2IntFunction andThenInt(Float2IntFunction after) {
      return k -> after.get(this.get(k));
   }

   default Int2FloatFunction composeInt(Int2ByteFunction before) {
      return k -> this.get(before.get(k));
   }

   default Byte2LongFunction andThenLong(Float2LongFunction after) {
      return k -> after.get(this.get(k));
   }

   default Long2FloatFunction composeLong(Long2ByteFunction before) {
      return k -> this.get(before.get(k));
   }

   default Byte2CharFunction andThenChar(Float2CharFunction after) {
      return k -> after.get(this.get(k));
   }

   default Char2FloatFunction composeChar(Char2ByteFunction before) {
      return k -> this.get(before.get(k));
   }

   default Byte2FloatFunction andThenFloat(Float2FloatFunction after) {
      return k -> after.get(this.get(k));
   }

   default Float2FloatFunction composeFloat(Float2ByteFunction before) {
      return k -> this.get(before.get(k));
   }

   default Byte2DoubleFunction andThenDouble(Float2DoubleFunction after) {
      return k -> after.get(this.get(k));
   }

   default Double2FloatFunction composeDouble(Double2ByteFunction before) {
      return k -> this.get(before.get(k));
   }

   default <T> Byte2ObjectFunction<T> andThenObject(Float2ObjectFunction<? extends T> after) {
      return k -> (T)after.get(this.get(k));
   }

   default <T> Object2FloatFunction<T> composeObject(Object2ByteFunction<? super T> before) {
      return k -> this.get(before.getByte(k));
   }

   default <T> Byte2ReferenceFunction<T> andThenReference(Float2ReferenceFunction<? extends T> after) {
      return k -> (T)after.get(this.get(k));
   }

   default <T> Reference2FloatFunction<T> composeReference(Reference2ByteFunction<? super T> before) {
      return k -> this.get(before.getByte(k));
   }
}
