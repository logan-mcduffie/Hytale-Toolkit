package it.unimi.dsi.fastutil.shorts;

import it.unimi.dsi.fastutil.SafeMath;
import it.unimi.dsi.fastutil.bytes.Byte2FloatFunction;
import it.unimi.dsi.fastutil.bytes.Byte2ShortFunction;
import it.unimi.dsi.fastutil.chars.Char2FloatFunction;
import it.unimi.dsi.fastutil.chars.Char2ShortFunction;
import it.unimi.dsi.fastutil.doubles.Double2FloatFunction;
import it.unimi.dsi.fastutil.doubles.Double2ShortFunction;
import it.unimi.dsi.fastutil.floats.Float2ByteFunction;
import it.unimi.dsi.fastutil.floats.Float2CharFunction;
import it.unimi.dsi.fastutil.floats.Float2DoubleFunction;
import it.unimi.dsi.fastutil.floats.Float2FloatFunction;
import it.unimi.dsi.fastutil.floats.Float2IntFunction;
import it.unimi.dsi.fastutil.floats.Float2LongFunction;
import it.unimi.dsi.fastutil.floats.Float2ObjectFunction;
import it.unimi.dsi.fastutil.floats.Float2ReferenceFunction;
import it.unimi.dsi.fastutil.floats.Float2ShortFunction;
import it.unimi.dsi.fastutil.ints.Int2FloatFunction;
import it.unimi.dsi.fastutil.ints.Int2ShortFunction;
import it.unimi.dsi.fastutil.longs.Long2FloatFunction;
import it.unimi.dsi.fastutil.longs.Long2ShortFunction;
import it.unimi.dsi.fastutil.objects.Object2FloatFunction;
import it.unimi.dsi.fastutil.objects.Object2ShortFunction;
import it.unimi.dsi.fastutil.objects.Reference2FloatFunction;
import it.unimi.dsi.fastutil.objects.Reference2ShortFunction;
import java.util.function.Function;
import java.util.function.IntToDoubleFunction;

@FunctionalInterface
public interface Short2FloatFunction extends it.unimi.dsi.fastutil.Function<Short, Float>, IntToDoubleFunction {
   @Deprecated
   @Override
   default double applyAsDouble(int operand) {
      return this.get(SafeMath.safeIntToShort(operand));
   }

   default float put(short key, float value) {
      throw new UnsupportedOperationException();
   }

   float get(short var1);

   default float getOrDefault(short key, float defaultValue) {
      float v;
      return (v = this.get(key)) == this.defaultReturnValue() && !this.containsKey(key) ? defaultValue : v;
   }

   default float remove(short key) {
      throw new UnsupportedOperationException();
   }

   @Deprecated
   default Float put(Short key, Float value) {
      short k = key;
      boolean containsKey = this.containsKey(k);
      float v = this.put(k, value.floatValue());
      return containsKey ? v : null;
   }

   @Deprecated
   default Float get(Object key) {
      if (key == null) {
         return null;
      } else {
         short k = (Short)key;
         float v;
         return (v = this.get(k)) == this.defaultReturnValue() && !this.containsKey(k) ? null : v;
      }
   }

   @Deprecated
   default Float getOrDefault(Object key, Float defaultValue) {
      if (key == null) {
         return defaultValue;
      } else {
         short k = (Short)key;
         float v = this.get(k);
         return v == this.defaultReturnValue() && !this.containsKey(k) ? defaultValue : v;
      }
   }

   @Deprecated
   default Float remove(Object key) {
      if (key == null) {
         return null;
      } else {
         short k = (Short)key;
         return this.containsKey(k) ? this.remove(k) : null;
      }
   }

   default boolean containsKey(short key) {
      return true;
   }

   @Deprecated
   @Override
   default boolean containsKey(Object key) {
      return key == null ? false : this.containsKey(((Short)key).shortValue());
   }

   default void defaultReturnValue(float rv) {
      throw new UnsupportedOperationException();
   }

   default float defaultReturnValue() {
      return 0.0F;
   }

   @Deprecated
   @Override
   default <T> Function<T, Float> compose(Function<? super T, ? extends Short> before) {
      return it.unimi.dsi.fastutil.Function.super.compose(before);
   }

   @Deprecated
   @Override
   default <T> Function<Short, T> andThen(Function<? super Float, ? extends T> after) {
      return it.unimi.dsi.fastutil.Function.super.andThen(after);
   }

   default Short2ByteFunction andThenByte(Float2ByteFunction after) {
      return k -> after.get(this.get(k));
   }

   default Byte2FloatFunction composeByte(Byte2ShortFunction before) {
      return k -> this.get(before.get(k));
   }

   default Short2ShortFunction andThenShort(Float2ShortFunction after) {
      return k -> after.get(this.get(k));
   }

   default Short2FloatFunction composeShort(Short2ShortFunction before) {
      return k -> this.get(before.get(k));
   }

   default Short2IntFunction andThenInt(Float2IntFunction after) {
      return k -> after.get(this.get(k));
   }

   default Int2FloatFunction composeInt(Int2ShortFunction before) {
      return k -> this.get(before.get(k));
   }

   default Short2LongFunction andThenLong(Float2LongFunction after) {
      return k -> after.get(this.get(k));
   }

   default Long2FloatFunction composeLong(Long2ShortFunction before) {
      return k -> this.get(before.get(k));
   }

   default Short2CharFunction andThenChar(Float2CharFunction after) {
      return k -> after.get(this.get(k));
   }

   default Char2FloatFunction composeChar(Char2ShortFunction before) {
      return k -> this.get(before.get(k));
   }

   default Short2FloatFunction andThenFloat(Float2FloatFunction after) {
      return k -> after.get(this.get(k));
   }

   default Float2FloatFunction composeFloat(Float2ShortFunction before) {
      return k -> this.get(before.get(k));
   }

   default Short2DoubleFunction andThenDouble(Float2DoubleFunction after) {
      return k -> after.get(this.get(k));
   }

   default Double2FloatFunction composeDouble(Double2ShortFunction before) {
      return k -> this.get(before.get(k));
   }

   default <T> Short2ObjectFunction<T> andThenObject(Float2ObjectFunction<? extends T> after) {
      return k -> (T)after.get(this.get(k));
   }

   default <T> Object2FloatFunction<T> composeObject(Object2ShortFunction<? super T> before) {
      return k -> this.get(before.getShort(k));
   }

   default <T> Short2ReferenceFunction<T> andThenReference(Float2ReferenceFunction<? extends T> after) {
      return k -> (T)after.get(this.get(k));
   }

   default <T> Reference2FloatFunction<T> composeReference(Reference2ShortFunction<? super T> before) {
      return k -> this.get(before.getShort(k));
   }
}
