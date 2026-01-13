package it.unimi.dsi.fastutil.longs;

import it.unimi.dsi.fastutil.bytes.Byte2FloatFunction;
import it.unimi.dsi.fastutil.bytes.Byte2LongFunction;
import it.unimi.dsi.fastutil.chars.Char2FloatFunction;
import it.unimi.dsi.fastutil.chars.Char2LongFunction;
import it.unimi.dsi.fastutil.doubles.Double2FloatFunction;
import it.unimi.dsi.fastutil.doubles.Double2LongFunction;
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
import it.unimi.dsi.fastutil.ints.Int2LongFunction;
import it.unimi.dsi.fastutil.objects.Object2FloatFunction;
import it.unimi.dsi.fastutil.objects.Object2LongFunction;
import it.unimi.dsi.fastutil.objects.Reference2FloatFunction;
import it.unimi.dsi.fastutil.objects.Reference2LongFunction;
import it.unimi.dsi.fastutil.shorts.Short2FloatFunction;
import it.unimi.dsi.fastutil.shorts.Short2LongFunction;
import java.util.function.Function;
import java.util.function.LongToDoubleFunction;

@FunctionalInterface
public interface Long2FloatFunction extends it.unimi.dsi.fastutil.Function<Long, Float>, LongToDoubleFunction {
   @Override
   default double applyAsDouble(long operand) {
      return this.get(operand);
   }

   default float put(long key, float value) {
      throw new UnsupportedOperationException();
   }

   float get(long var1);

   default float getOrDefault(long key, float defaultValue) {
      float v;
      return (v = this.get(key)) == this.defaultReturnValue() && !this.containsKey(key) ? defaultValue : v;
   }

   default float remove(long key) {
      throw new UnsupportedOperationException();
   }

   @Deprecated
   default Float put(Long key, Float value) {
      long k = key;
      boolean containsKey = this.containsKey(k);
      float v = this.put(k, value.floatValue());
      return containsKey ? v : null;
   }

   @Deprecated
   default Float get(Object key) {
      if (key == null) {
         return null;
      } else {
         long k = (Long)key;
         float v;
         return (v = this.get(k)) == this.defaultReturnValue() && !this.containsKey(k) ? null : v;
      }
   }

   @Deprecated
   default Float getOrDefault(Object key, Float defaultValue) {
      if (key == null) {
         return defaultValue;
      } else {
         long k = (Long)key;
         float v = this.get(k);
         return v == this.defaultReturnValue() && !this.containsKey(k) ? defaultValue : v;
      }
   }

   @Deprecated
   default Float remove(Object key) {
      if (key == null) {
         return null;
      } else {
         long k = (Long)key;
         return this.containsKey(k) ? this.remove(k) : null;
      }
   }

   default boolean containsKey(long key) {
      return true;
   }

   @Deprecated
   @Override
   default boolean containsKey(Object key) {
      return key == null ? false : this.containsKey(((Long)key).longValue());
   }

   default void defaultReturnValue(float rv) {
      throw new UnsupportedOperationException();
   }

   default float defaultReturnValue() {
      return 0.0F;
   }

   @Deprecated
   @Override
   default <T> Function<T, Float> compose(Function<? super T, ? extends Long> before) {
      return it.unimi.dsi.fastutil.Function.super.compose(before);
   }

   @Deprecated
   @Override
   default <T> Function<Long, T> andThen(Function<? super Float, ? extends T> after) {
      return it.unimi.dsi.fastutil.Function.super.andThen(after);
   }

   default Long2ByteFunction andThenByte(Float2ByteFunction after) {
      return k -> after.get(this.get(k));
   }

   default Byte2FloatFunction composeByte(Byte2LongFunction before) {
      return k -> this.get(before.get(k));
   }

   default Long2ShortFunction andThenShort(Float2ShortFunction after) {
      return k -> after.get(this.get(k));
   }

   default Short2FloatFunction composeShort(Short2LongFunction before) {
      return k -> this.get(before.get(k));
   }

   default Long2IntFunction andThenInt(Float2IntFunction after) {
      return k -> after.get(this.get(k));
   }

   default Int2FloatFunction composeInt(Int2LongFunction before) {
      return k -> this.get(before.get(k));
   }

   default Long2LongFunction andThenLong(Float2LongFunction after) {
      return k -> after.get(this.get(k));
   }

   default Long2FloatFunction composeLong(Long2LongFunction before) {
      return k -> this.get(before.get(k));
   }

   default Long2CharFunction andThenChar(Float2CharFunction after) {
      return k -> after.get(this.get(k));
   }

   default Char2FloatFunction composeChar(Char2LongFunction before) {
      return k -> this.get(before.get(k));
   }

   default Long2FloatFunction andThenFloat(Float2FloatFunction after) {
      return k -> after.get(this.get(k));
   }

   default Float2FloatFunction composeFloat(Float2LongFunction before) {
      return k -> this.get(before.get(k));
   }

   default Long2DoubleFunction andThenDouble(Float2DoubleFunction after) {
      return k -> after.get(this.get(k));
   }

   default Double2FloatFunction composeDouble(Double2LongFunction before) {
      return k -> this.get(before.get(k));
   }

   default <T> Long2ObjectFunction<T> andThenObject(Float2ObjectFunction<? extends T> after) {
      return k -> (T)after.get(this.get(k));
   }

   default <T> Object2FloatFunction<T> composeObject(Object2LongFunction<? super T> before) {
      return k -> this.get(before.getLong(k));
   }

   default <T> Long2ReferenceFunction<T> andThenReference(Float2ReferenceFunction<? extends T> after) {
      return k -> (T)after.get(this.get(k));
   }

   default <T> Reference2FloatFunction<T> composeReference(Reference2LongFunction<? super T> before) {
      return k -> this.get(before.getLong(k));
   }
}
