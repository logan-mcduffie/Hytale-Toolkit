package it.unimi.dsi.fastutil.chars;

import it.unimi.dsi.fastutil.SafeMath;
import it.unimi.dsi.fastutil.bytes.Byte2CharFunction;
import it.unimi.dsi.fastutil.bytes.Byte2FloatFunction;
import it.unimi.dsi.fastutil.doubles.Double2CharFunction;
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
import it.unimi.dsi.fastutil.ints.Int2CharFunction;
import it.unimi.dsi.fastutil.ints.Int2FloatFunction;
import it.unimi.dsi.fastutil.longs.Long2CharFunction;
import it.unimi.dsi.fastutil.longs.Long2FloatFunction;
import it.unimi.dsi.fastutil.objects.Object2CharFunction;
import it.unimi.dsi.fastutil.objects.Object2FloatFunction;
import it.unimi.dsi.fastutil.objects.Reference2CharFunction;
import it.unimi.dsi.fastutil.objects.Reference2FloatFunction;
import it.unimi.dsi.fastutil.shorts.Short2CharFunction;
import it.unimi.dsi.fastutil.shorts.Short2FloatFunction;
import java.util.function.Function;
import java.util.function.IntToDoubleFunction;

@FunctionalInterface
public interface Char2FloatFunction extends it.unimi.dsi.fastutil.Function<Character, Float>, IntToDoubleFunction {
   @Deprecated
   @Override
   default double applyAsDouble(int operand) {
      return this.get(SafeMath.safeIntToChar(operand));
   }

   default float put(char key, float value) {
      throw new UnsupportedOperationException();
   }

   float get(char var1);

   default float getOrDefault(char key, float defaultValue) {
      float v;
      return (v = this.get(key)) == this.defaultReturnValue() && !this.containsKey(key) ? defaultValue : v;
   }

   default float remove(char key) {
      throw new UnsupportedOperationException();
   }

   @Deprecated
   default Float put(Character key, Float value) {
      char k = key;
      boolean containsKey = this.containsKey(k);
      float v = this.put(k, value.floatValue());
      return containsKey ? v : null;
   }

   @Deprecated
   default Float get(Object key) {
      if (key == null) {
         return null;
      } else {
         char k = (Character)key;
         float v;
         return (v = this.get(k)) == this.defaultReturnValue() && !this.containsKey(k) ? null : v;
      }
   }

   @Deprecated
   default Float getOrDefault(Object key, Float defaultValue) {
      if (key == null) {
         return defaultValue;
      } else {
         char k = (Character)key;
         float v = this.get(k);
         return v == this.defaultReturnValue() && !this.containsKey(k) ? defaultValue : v;
      }
   }

   @Deprecated
   default Float remove(Object key) {
      if (key == null) {
         return null;
      } else {
         char k = (Character)key;
         return this.containsKey(k) ? this.remove(k) : null;
      }
   }

   default boolean containsKey(char key) {
      return true;
   }

   @Deprecated
   @Override
   default boolean containsKey(Object key) {
      return key == null ? false : this.containsKey(((Character)key).charValue());
   }

   default void defaultReturnValue(float rv) {
      throw new UnsupportedOperationException();
   }

   default float defaultReturnValue() {
      return 0.0F;
   }

   @Deprecated
   @Override
   default <T> Function<T, Float> compose(Function<? super T, ? extends Character> before) {
      return it.unimi.dsi.fastutil.Function.super.compose(before);
   }

   @Deprecated
   @Override
   default <T> Function<Character, T> andThen(Function<? super Float, ? extends T> after) {
      return it.unimi.dsi.fastutil.Function.super.andThen(after);
   }

   default Char2ByteFunction andThenByte(Float2ByteFunction after) {
      return k -> after.get(this.get(k));
   }

   default Byte2FloatFunction composeByte(Byte2CharFunction before) {
      return k -> this.get(before.get(k));
   }

   default Char2ShortFunction andThenShort(Float2ShortFunction after) {
      return k -> after.get(this.get(k));
   }

   default Short2FloatFunction composeShort(Short2CharFunction before) {
      return k -> this.get(before.get(k));
   }

   default Char2IntFunction andThenInt(Float2IntFunction after) {
      return k -> after.get(this.get(k));
   }

   default Int2FloatFunction composeInt(Int2CharFunction before) {
      return k -> this.get(before.get(k));
   }

   default Char2LongFunction andThenLong(Float2LongFunction after) {
      return k -> after.get(this.get(k));
   }

   default Long2FloatFunction composeLong(Long2CharFunction before) {
      return k -> this.get(before.get(k));
   }

   default Char2CharFunction andThenChar(Float2CharFunction after) {
      return k -> after.get(this.get(k));
   }

   default Char2FloatFunction composeChar(Char2CharFunction before) {
      return k -> this.get(before.get(k));
   }

   default Char2FloatFunction andThenFloat(Float2FloatFunction after) {
      return k -> after.get(this.get(k));
   }

   default Float2FloatFunction composeFloat(Float2CharFunction before) {
      return k -> this.get(before.get(k));
   }

   default Char2DoubleFunction andThenDouble(Float2DoubleFunction after) {
      return k -> after.get(this.get(k));
   }

   default Double2FloatFunction composeDouble(Double2CharFunction before) {
      return k -> this.get(before.get(k));
   }

   default <T> Char2ObjectFunction<T> andThenObject(Float2ObjectFunction<? extends T> after) {
      return k -> (T)after.get(this.get(k));
   }

   default <T> Object2FloatFunction<T> composeObject(Object2CharFunction<? super T> before) {
      return k -> this.get(before.getChar(k));
   }

   default <T> Char2ReferenceFunction<T> andThenReference(Float2ReferenceFunction<? extends T> after) {
      return k -> (T)after.get(this.get(k));
   }

   default <T> Reference2FloatFunction<T> composeReference(Reference2CharFunction<? super T> before) {
      return k -> this.get(before.getChar(k));
   }
}
