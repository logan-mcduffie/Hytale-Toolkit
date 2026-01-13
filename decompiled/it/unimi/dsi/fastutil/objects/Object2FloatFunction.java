package it.unimi.dsi.fastutil.objects;

import it.unimi.dsi.fastutil.bytes.Byte2FloatFunction;
import it.unimi.dsi.fastutil.bytes.Byte2ObjectFunction;
import it.unimi.dsi.fastutil.chars.Char2FloatFunction;
import it.unimi.dsi.fastutil.chars.Char2ObjectFunction;
import it.unimi.dsi.fastutil.doubles.Double2FloatFunction;
import it.unimi.dsi.fastutil.doubles.Double2ObjectFunction;
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
import it.unimi.dsi.fastutil.ints.Int2ObjectFunction;
import it.unimi.dsi.fastutil.longs.Long2FloatFunction;
import it.unimi.dsi.fastutil.longs.Long2ObjectFunction;
import it.unimi.dsi.fastutil.shorts.Short2FloatFunction;
import it.unimi.dsi.fastutil.shorts.Short2ObjectFunction;
import java.util.function.Function;
import java.util.function.ToDoubleFunction;

@FunctionalInterface
public interface Object2FloatFunction<K> extends it.unimi.dsi.fastutil.Function<K, Float>, ToDoubleFunction<K> {
   @Override
   default double applyAsDouble(K operand) {
      return this.getFloat(operand);
   }

   default float put(K key, float value) {
      throw new UnsupportedOperationException();
   }

   float getFloat(Object var1);

   default float getOrDefault(Object key, float defaultValue) {
      float v;
      return (v = this.getFloat(key)) == this.defaultReturnValue() && !this.containsKey(key) ? defaultValue : v;
   }

   default float removeFloat(Object key) {
      throw new UnsupportedOperationException();
   }

   @Deprecated
   default Float put(K key, Float value) {
      boolean containsKey = this.containsKey(key);
      float v = this.put(key, value.floatValue());
      return containsKey ? v : null;
   }

   @Deprecated
   default Float get(Object key) {
      float v;
      return (v = this.getFloat(key)) == this.defaultReturnValue() && !this.containsKey(key) ? null : v;
   }

   @Deprecated
   default Float getOrDefault(Object key, Float defaultValue) {
      float v = this.getFloat(key);
      return v == this.defaultReturnValue() && !this.containsKey(key) ? defaultValue : v;
   }

   @Deprecated
   default Float remove(Object key) {
      return this.containsKey(key) ? this.removeFloat(key) : null;
   }

   default void defaultReturnValue(float rv) {
      throw new UnsupportedOperationException();
   }

   default float defaultReturnValue() {
      return 0.0F;
   }

   @Deprecated
   @Override
   default <T> Function<K, T> andThen(Function<? super Float, ? extends T> after) {
      return it.unimi.dsi.fastutil.Function.super.andThen(after);
   }

   default Object2ByteFunction<K> andThenByte(Float2ByteFunction after) {
      return k -> after.get(this.getFloat(k));
   }

   default Byte2FloatFunction composeByte(Byte2ObjectFunction<K> before) {
      return k -> this.getFloat(before.get(k));
   }

   default Object2ShortFunction<K> andThenShort(Float2ShortFunction after) {
      return k -> after.get(this.getFloat(k));
   }

   default Short2FloatFunction composeShort(Short2ObjectFunction<K> before) {
      return k -> this.getFloat(before.get(k));
   }

   default Object2IntFunction<K> andThenInt(Float2IntFunction after) {
      return k -> after.get(this.getFloat(k));
   }

   default Int2FloatFunction composeInt(Int2ObjectFunction<K> before) {
      return k -> this.getFloat(before.get(k));
   }

   default Object2LongFunction<K> andThenLong(Float2LongFunction after) {
      return k -> after.get(this.getFloat(k));
   }

   default Long2FloatFunction composeLong(Long2ObjectFunction<K> before) {
      return k -> this.getFloat(before.get(k));
   }

   default Object2CharFunction<K> andThenChar(Float2CharFunction after) {
      return k -> after.get(this.getFloat(k));
   }

   default Char2FloatFunction composeChar(Char2ObjectFunction<K> before) {
      return k -> this.getFloat(before.get(k));
   }

   default Object2FloatFunction<K> andThenFloat(Float2FloatFunction after) {
      return k -> after.get(this.getFloat(k));
   }

   default Float2FloatFunction composeFloat(Float2ObjectFunction<K> before) {
      return k -> this.getFloat(before.get(k));
   }

   default Object2DoubleFunction<K> andThenDouble(Float2DoubleFunction after) {
      return k -> after.get(this.getFloat(k));
   }

   default Double2FloatFunction composeDouble(Double2ObjectFunction<K> before) {
      return k -> this.getFloat(before.get(k));
   }

   default <T> Object2ObjectFunction<K, T> andThenObject(Float2ObjectFunction<? extends T> after) {
      return k -> (T)after.get(this.getFloat(k));
   }

   default <T> Object2FloatFunction<T> composeObject(Object2ObjectFunction<? super T, ? extends K> before) {
      return k -> this.getFloat(before.get(k));
   }

   default <T> Object2ReferenceFunction<K, T> andThenReference(Float2ReferenceFunction<? extends T> after) {
      return k -> (T)after.get(this.getFloat(k));
   }

   default <T> Reference2FloatFunction<T> composeReference(Reference2ObjectFunction<? super T, ? extends K> before) {
      return k -> this.getFloat(before.get(k));
   }
}
