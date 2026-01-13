package it.unimi.dsi.fastutil.doubles;

import it.unimi.dsi.fastutil.bytes.Byte2DoubleFunction;
import it.unimi.dsi.fastutil.bytes.Byte2ReferenceFunction;
import it.unimi.dsi.fastutil.chars.Char2DoubleFunction;
import it.unimi.dsi.fastutil.chars.Char2ReferenceFunction;
import it.unimi.dsi.fastutil.floats.Float2DoubleFunction;
import it.unimi.dsi.fastutil.floats.Float2ReferenceFunction;
import it.unimi.dsi.fastutil.ints.Int2DoubleFunction;
import it.unimi.dsi.fastutil.ints.Int2ReferenceFunction;
import it.unimi.dsi.fastutil.longs.Long2DoubleFunction;
import it.unimi.dsi.fastutil.longs.Long2ReferenceFunction;
import it.unimi.dsi.fastutil.objects.Object2DoubleFunction;
import it.unimi.dsi.fastutil.objects.Object2ReferenceFunction;
import it.unimi.dsi.fastutil.objects.Reference2ByteFunction;
import it.unimi.dsi.fastutil.objects.Reference2CharFunction;
import it.unimi.dsi.fastutil.objects.Reference2DoubleFunction;
import it.unimi.dsi.fastutil.objects.Reference2FloatFunction;
import it.unimi.dsi.fastutil.objects.Reference2IntFunction;
import it.unimi.dsi.fastutil.objects.Reference2LongFunction;
import it.unimi.dsi.fastutil.objects.Reference2ObjectFunction;
import it.unimi.dsi.fastutil.objects.Reference2ReferenceFunction;
import it.unimi.dsi.fastutil.objects.Reference2ShortFunction;
import it.unimi.dsi.fastutil.shorts.Short2DoubleFunction;
import it.unimi.dsi.fastutil.shorts.Short2ReferenceFunction;
import java.util.function.DoubleFunction;
import java.util.function.Function;

@FunctionalInterface
public interface Double2ReferenceFunction<V> extends it.unimi.dsi.fastutil.Function<Double, V>, DoubleFunction<V> {
   @Override
   default V apply(double operand) {
      return this.get(operand);
   }

   default V put(double key, V value) {
      throw new UnsupportedOperationException();
   }

   V get(double var1);

   default V getOrDefault(double key, V defaultValue) {
      V v;
      return (v = this.get(key)) == this.defaultReturnValue() && !this.containsKey(key) ? defaultValue : v;
   }

   default V remove(double key) {
      throw new UnsupportedOperationException();
   }

   @Deprecated
   default V put(Double key, V value) {
      double k = key;
      boolean containsKey = this.containsKey(k);
      V v = this.put(k, value);
      return containsKey ? v : null;
   }

   @Deprecated
   @Override
   default V get(Object key) {
      if (key == null) {
         return null;
      } else {
         double k = (Double)key;
         V v;
         return (v = this.get(k)) == this.defaultReturnValue() && !this.containsKey(k) ? null : v;
      }
   }

   @Deprecated
   @Override
   default V getOrDefault(Object key, V defaultValue) {
      if (key == null) {
         return defaultValue;
      } else {
         double k = (Double)key;
         V v = this.get(k);
         return v == this.defaultReturnValue() && !this.containsKey(k) ? defaultValue : v;
      }
   }

   @Deprecated
   @Override
   default V remove(Object key) {
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

   default void defaultReturnValue(V rv) {
      throw new UnsupportedOperationException();
   }

   default V defaultReturnValue() {
      return null;
   }

   @Deprecated
   @Override
   default <T> Function<T, V> compose(Function<? super T, ? extends Double> before) {
      return (Function<T, V>)it.unimi.dsi.fastutil.Function.super.compose(before);
   }

   default Double2ByteFunction andThenByte(Reference2ByteFunction<V> after) {
      return k -> after.getByte(this.get(k));
   }

   default Byte2ReferenceFunction<V> composeByte(Byte2DoubleFunction before) {
      return k -> this.get(before.get(k));
   }

   default Double2ShortFunction andThenShort(Reference2ShortFunction<V> after) {
      return k -> after.getShort(this.get(k));
   }

   default Short2ReferenceFunction<V> composeShort(Short2DoubleFunction before) {
      return k -> this.get(before.get(k));
   }

   default Double2IntFunction andThenInt(Reference2IntFunction<V> after) {
      return k -> after.getInt(this.get(k));
   }

   default Int2ReferenceFunction<V> composeInt(Int2DoubleFunction before) {
      return k -> this.get(before.get(k));
   }

   default Double2LongFunction andThenLong(Reference2LongFunction<V> after) {
      return k -> after.getLong(this.get(k));
   }

   default Long2ReferenceFunction<V> composeLong(Long2DoubleFunction before) {
      return k -> this.get(before.get(k));
   }

   default Double2CharFunction andThenChar(Reference2CharFunction<V> after) {
      return k -> after.getChar(this.get(k));
   }

   default Char2ReferenceFunction<V> composeChar(Char2DoubleFunction before) {
      return k -> this.get(before.get(k));
   }

   default Double2FloatFunction andThenFloat(Reference2FloatFunction<V> after) {
      return k -> after.getFloat(this.get(k));
   }

   default Float2ReferenceFunction<V> composeFloat(Float2DoubleFunction before) {
      return k -> this.get(before.get(k));
   }

   default Double2DoubleFunction andThenDouble(Reference2DoubleFunction<V> after) {
      return k -> after.getDouble(this.get(k));
   }

   default Double2ReferenceFunction<V> composeDouble(Double2DoubleFunction before) {
      return k -> this.get(before.get(k));
   }

   default <T> Double2ObjectFunction<T> andThenObject(Reference2ObjectFunction<? super V, ? extends T> after) {
      return k -> (T)after.get(this.get(k));
   }

   default <T> Object2ReferenceFunction<T, V> composeObject(Object2DoubleFunction<? super T> before) {
      return k -> this.get(before.getDouble(k));
   }

   default <T> Double2ReferenceFunction<T> andThenReference(Reference2ReferenceFunction<? super V, ? extends T> after) {
      return k -> (T)after.get(this.get(k));
   }

   default <T> Reference2ReferenceFunction<T, V> composeReference(Reference2DoubleFunction<? super T> before) {
      return k -> this.get(before.getDouble(k));
   }
}
