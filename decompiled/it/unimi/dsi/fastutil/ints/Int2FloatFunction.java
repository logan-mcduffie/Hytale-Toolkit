package it.unimi.dsi.fastutil.ints;

import it.unimi.dsi.fastutil.bytes.Byte2FloatFunction;
import it.unimi.dsi.fastutil.bytes.Byte2IntFunction;
import it.unimi.dsi.fastutil.chars.Char2FloatFunction;
import it.unimi.dsi.fastutil.chars.Char2IntFunction;
import it.unimi.dsi.fastutil.doubles.Double2FloatFunction;
import it.unimi.dsi.fastutil.doubles.Double2IntFunction;
import it.unimi.dsi.fastutil.floats.Float2ByteFunction;
import it.unimi.dsi.fastutil.floats.Float2CharFunction;
import it.unimi.dsi.fastutil.floats.Float2DoubleFunction;
import it.unimi.dsi.fastutil.floats.Float2FloatFunction;
import it.unimi.dsi.fastutil.floats.Float2IntFunction;
import it.unimi.dsi.fastutil.floats.Float2LongFunction;
import it.unimi.dsi.fastutil.floats.Float2ObjectFunction;
import it.unimi.dsi.fastutil.floats.Float2ReferenceFunction;
import it.unimi.dsi.fastutil.floats.Float2ShortFunction;
import it.unimi.dsi.fastutil.longs.Long2FloatFunction;
import it.unimi.dsi.fastutil.longs.Long2IntFunction;
import it.unimi.dsi.fastutil.objects.Object2FloatFunction;
import it.unimi.dsi.fastutil.objects.Object2IntFunction;
import it.unimi.dsi.fastutil.objects.Reference2FloatFunction;
import it.unimi.dsi.fastutil.objects.Reference2IntFunction;
import it.unimi.dsi.fastutil.shorts.Short2FloatFunction;
import it.unimi.dsi.fastutil.shorts.Short2IntFunction;
import java.util.function.Function;
import java.util.function.IntToDoubleFunction;

@FunctionalInterface
public interface Int2FloatFunction extends it.unimi.dsi.fastutil.Function<Integer, Float>, IntToDoubleFunction {
   @Override
   default double applyAsDouble(int operand) {
      return this.get(operand);
   }

   default float put(int key, float value) {
      throw new UnsupportedOperationException();
   }

   float get(int var1);

   default float getOrDefault(int key, float defaultValue) {
      float v;
      return (v = this.get(key)) == this.defaultReturnValue() && !this.containsKey(key) ? defaultValue : v;
   }

   default float remove(int key) {
      throw new UnsupportedOperationException();
   }

   @Deprecated
   default Float put(Integer key, Float value) {
      int k = key;
      boolean containsKey = this.containsKey(k);
      float v = this.put(k, value.floatValue());
      return containsKey ? v : null;
   }

   @Deprecated
   default Float get(Object key) {
      if (key == null) {
         return null;
      } else {
         int k = (Integer)key;
         float v;
         return (v = this.get(k)) == this.defaultReturnValue() && !this.containsKey(k) ? null : v;
      }
   }

   @Deprecated
   default Float getOrDefault(Object key, Float defaultValue) {
      if (key == null) {
         return defaultValue;
      } else {
         int k = (Integer)key;
         float v = this.get(k);
         return v == this.defaultReturnValue() && !this.containsKey(k) ? defaultValue : v;
      }
   }

   @Deprecated
   default Float remove(Object key) {
      if (key == null) {
         return null;
      } else {
         int k = (Integer)key;
         return this.containsKey(k) ? this.remove(k) : null;
      }
   }

   default boolean containsKey(int key) {
      return true;
   }

   @Deprecated
   @Override
   default boolean containsKey(Object key) {
      return key == null ? false : this.containsKey(((Integer)key).intValue());
   }

   default void defaultReturnValue(float rv) {
      throw new UnsupportedOperationException();
   }

   default float defaultReturnValue() {
      return 0.0F;
   }

   @Deprecated
   @Override
   default <T> Function<T, Float> compose(Function<? super T, ? extends Integer> before) {
      return it.unimi.dsi.fastutil.Function.super.compose(before);
   }

   @Deprecated
   @Override
   default <T> Function<Integer, T> andThen(Function<? super Float, ? extends T> after) {
      return it.unimi.dsi.fastutil.Function.super.andThen(after);
   }

   default Int2ByteFunction andThenByte(Float2ByteFunction after) {
      return k -> after.get(this.get(k));
   }

   default Byte2FloatFunction composeByte(Byte2IntFunction before) {
      return k -> this.get(before.get(k));
   }

   default Int2ShortFunction andThenShort(Float2ShortFunction after) {
      return k -> after.get(this.get(k));
   }

   default Short2FloatFunction composeShort(Short2IntFunction before) {
      return k -> this.get(before.get(k));
   }

   default Int2IntFunction andThenInt(Float2IntFunction after) {
      return k -> after.get(this.get(k));
   }

   default Int2FloatFunction composeInt(Int2IntFunction before) {
      return k -> this.get(before.get(k));
   }

   default Int2LongFunction andThenLong(Float2LongFunction after) {
      return k -> after.get(this.get(k));
   }

   default Long2FloatFunction composeLong(Long2IntFunction before) {
      return k -> this.get(before.get(k));
   }

   default Int2CharFunction andThenChar(Float2CharFunction after) {
      return k -> after.get(this.get(k));
   }

   default Char2FloatFunction composeChar(Char2IntFunction before) {
      return k -> this.get(before.get(k));
   }

   default Int2FloatFunction andThenFloat(Float2FloatFunction after) {
      return k -> after.get(this.get(k));
   }

   default Float2FloatFunction composeFloat(Float2IntFunction before) {
      return k -> this.get(before.get(k));
   }

   default Int2DoubleFunction andThenDouble(Float2DoubleFunction after) {
      return k -> after.get(this.get(k));
   }

   default Double2FloatFunction composeDouble(Double2IntFunction before) {
      return k -> this.get(before.get(k));
   }

   default <T> Int2ObjectFunction<T> andThenObject(Float2ObjectFunction<? extends T> after) {
      return k -> (T)after.get(this.get(k));
   }

   default <T> Object2FloatFunction<T> composeObject(Object2IntFunction<? super T> before) {
      return k -> this.get(before.getInt(k));
   }

   default <T> Int2ReferenceFunction<T> andThenReference(Float2ReferenceFunction<? extends T> after) {
      return k -> (T)after.get(this.get(k));
   }

   default <T> Reference2FloatFunction<T> composeReference(Reference2IntFunction<? super T> before) {
      return k -> this.get(before.getInt(k));
   }
}
