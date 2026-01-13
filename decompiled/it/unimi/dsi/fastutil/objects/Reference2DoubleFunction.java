package it.unimi.dsi.fastutil.objects;

import it.unimi.dsi.fastutil.bytes.Byte2DoubleFunction;
import it.unimi.dsi.fastutil.bytes.Byte2ReferenceFunction;
import it.unimi.dsi.fastutil.chars.Char2DoubleFunction;
import it.unimi.dsi.fastutil.chars.Char2ReferenceFunction;
import it.unimi.dsi.fastutil.doubles.Double2ByteFunction;
import it.unimi.dsi.fastutil.doubles.Double2CharFunction;
import it.unimi.dsi.fastutil.doubles.Double2DoubleFunction;
import it.unimi.dsi.fastutil.doubles.Double2FloatFunction;
import it.unimi.dsi.fastutil.doubles.Double2IntFunction;
import it.unimi.dsi.fastutil.doubles.Double2LongFunction;
import it.unimi.dsi.fastutil.doubles.Double2ObjectFunction;
import it.unimi.dsi.fastutil.doubles.Double2ReferenceFunction;
import it.unimi.dsi.fastutil.doubles.Double2ShortFunction;
import it.unimi.dsi.fastutil.floats.Float2DoubleFunction;
import it.unimi.dsi.fastutil.floats.Float2ReferenceFunction;
import it.unimi.dsi.fastutil.ints.Int2DoubleFunction;
import it.unimi.dsi.fastutil.ints.Int2ReferenceFunction;
import it.unimi.dsi.fastutil.longs.Long2DoubleFunction;
import it.unimi.dsi.fastutil.longs.Long2ReferenceFunction;
import it.unimi.dsi.fastutil.shorts.Short2DoubleFunction;
import it.unimi.dsi.fastutil.shorts.Short2ReferenceFunction;
import java.util.function.Function;
import java.util.function.ToDoubleFunction;

@FunctionalInterface
public interface Reference2DoubleFunction<K> extends it.unimi.dsi.fastutil.Function<K, Double>, ToDoubleFunction<K> {
   @Override
   default double applyAsDouble(K operand) {
      return this.getDouble(operand);
   }

   default double put(K key, double value) {
      throw new UnsupportedOperationException();
   }

   double getDouble(Object var1);

   default double getOrDefault(Object key, double defaultValue) {
      double v;
      return (v = this.getDouble(key)) == this.defaultReturnValue() && !this.containsKey(key) ? defaultValue : v;
   }

   default double removeDouble(Object key) {
      throw new UnsupportedOperationException();
   }

   @Deprecated
   default Double put(K key, Double value) {
      boolean containsKey = this.containsKey(key);
      double v = this.put(key, value.doubleValue());
      return containsKey ? v : null;
   }

   @Deprecated
   default Double get(Object key) {
      double v;
      return (v = this.getDouble(key)) == this.defaultReturnValue() && !this.containsKey(key) ? null : v;
   }

   @Deprecated
   default Double getOrDefault(Object key, Double defaultValue) {
      double v = this.getDouble(key);
      return v == this.defaultReturnValue() && !this.containsKey(key) ? defaultValue : v;
   }

   @Deprecated
   default Double remove(Object key) {
      return this.containsKey(key) ? this.removeDouble(key) : null;
   }

   default void defaultReturnValue(double rv) {
      throw new UnsupportedOperationException();
   }

   default double defaultReturnValue() {
      return 0.0;
   }

   @Deprecated
   @Override
   default <T> Function<K, T> andThen(Function<? super Double, ? extends T> after) {
      return it.unimi.dsi.fastutil.Function.super.andThen(after);
   }

   default Reference2ByteFunction<K> andThenByte(Double2ByteFunction after) {
      return k -> after.get(this.getDouble(k));
   }

   default Byte2DoubleFunction composeByte(Byte2ReferenceFunction<K> before) {
      return k -> this.getDouble(before.get(k));
   }

   default Reference2ShortFunction<K> andThenShort(Double2ShortFunction after) {
      return k -> after.get(this.getDouble(k));
   }

   default Short2DoubleFunction composeShort(Short2ReferenceFunction<K> before) {
      return k -> this.getDouble(before.get(k));
   }

   default Reference2IntFunction<K> andThenInt(Double2IntFunction after) {
      return k -> after.get(this.getDouble(k));
   }

   default Int2DoubleFunction composeInt(Int2ReferenceFunction<K> before) {
      return k -> this.getDouble(before.get(k));
   }

   default Reference2LongFunction<K> andThenLong(Double2LongFunction after) {
      return k -> after.get(this.getDouble(k));
   }

   default Long2DoubleFunction composeLong(Long2ReferenceFunction<K> before) {
      return k -> this.getDouble(before.get(k));
   }

   default Reference2CharFunction<K> andThenChar(Double2CharFunction after) {
      return k -> after.get(this.getDouble(k));
   }

   default Char2DoubleFunction composeChar(Char2ReferenceFunction<K> before) {
      return k -> this.getDouble(before.get(k));
   }

   default Reference2FloatFunction<K> andThenFloat(Double2FloatFunction after) {
      return k -> after.get(this.getDouble(k));
   }

   default Float2DoubleFunction composeFloat(Float2ReferenceFunction<K> before) {
      return k -> this.getDouble(before.get(k));
   }

   default Reference2DoubleFunction<K> andThenDouble(Double2DoubleFunction after) {
      return k -> after.get(this.getDouble(k));
   }

   default Double2DoubleFunction composeDouble(Double2ReferenceFunction<K> before) {
      return k -> this.getDouble(before.get(k));
   }

   default <T> Reference2ObjectFunction<K, T> andThenObject(Double2ObjectFunction<? extends T> after) {
      return k -> (T)after.get(this.getDouble(k));
   }

   default <T> Object2DoubleFunction<T> composeObject(Object2ReferenceFunction<? super T, ? extends K> before) {
      return k -> this.getDouble(before.get(k));
   }

   default <T> Reference2ReferenceFunction<K, T> andThenReference(Double2ReferenceFunction<? extends T> after) {
      return k -> (T)after.get(this.getDouble(k));
   }

   default <T> Reference2DoubleFunction<T> composeReference(Reference2ReferenceFunction<? super T, ? extends K> before) {
      return k -> this.getDouble(before.get(k));
   }
}
