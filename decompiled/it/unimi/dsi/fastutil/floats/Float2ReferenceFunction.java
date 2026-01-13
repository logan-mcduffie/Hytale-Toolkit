package it.unimi.dsi.fastutil.floats;

import it.unimi.dsi.fastutil.SafeMath;
import it.unimi.dsi.fastutil.bytes.Byte2FloatFunction;
import it.unimi.dsi.fastutil.bytes.Byte2ReferenceFunction;
import it.unimi.dsi.fastutil.chars.Char2FloatFunction;
import it.unimi.dsi.fastutil.chars.Char2ReferenceFunction;
import it.unimi.dsi.fastutil.doubles.Double2FloatFunction;
import it.unimi.dsi.fastutil.doubles.Double2ReferenceFunction;
import it.unimi.dsi.fastutil.ints.Int2FloatFunction;
import it.unimi.dsi.fastutil.ints.Int2ReferenceFunction;
import it.unimi.dsi.fastutil.longs.Long2FloatFunction;
import it.unimi.dsi.fastutil.longs.Long2ReferenceFunction;
import it.unimi.dsi.fastutil.objects.Object2FloatFunction;
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
import it.unimi.dsi.fastutil.shorts.Short2FloatFunction;
import it.unimi.dsi.fastutil.shorts.Short2ReferenceFunction;
import java.util.function.DoubleFunction;
import java.util.function.Function;

@FunctionalInterface
public interface Float2ReferenceFunction<V> extends it.unimi.dsi.fastutil.Function<Float, V>, DoubleFunction<V> {
   @Deprecated
   @Override
   default V apply(double operand) {
      return this.get(SafeMath.safeDoubleToFloat(operand));
   }

   default V put(float key, V value) {
      throw new UnsupportedOperationException();
   }

   V get(float var1);

   default V getOrDefault(float key, V defaultValue) {
      V v;
      return (v = this.get(key)) == this.defaultReturnValue() && !this.containsKey(key) ? defaultValue : v;
   }

   default V remove(float key) {
      throw new UnsupportedOperationException();
   }

   @Deprecated
   default V put(Float key, V value) {
      float k = key;
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
         float k = (Float)key;
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
         float k = (Float)key;
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

   default void defaultReturnValue(V rv) {
      throw new UnsupportedOperationException();
   }

   default V defaultReturnValue() {
      return null;
   }

   @Deprecated
   @Override
   default <T> Function<T, V> compose(Function<? super T, ? extends Float> before) {
      return (Function<T, V>)it.unimi.dsi.fastutil.Function.super.compose(before);
   }

   default Float2ByteFunction andThenByte(Reference2ByteFunction<V> after) {
      return k -> after.getByte(this.get(k));
   }

   default Byte2ReferenceFunction<V> composeByte(Byte2FloatFunction before) {
      return k -> this.get(before.get(k));
   }

   default Float2ShortFunction andThenShort(Reference2ShortFunction<V> after) {
      return k -> after.getShort(this.get(k));
   }

   default Short2ReferenceFunction<V> composeShort(Short2FloatFunction before) {
      return k -> this.get(before.get(k));
   }

   default Float2IntFunction andThenInt(Reference2IntFunction<V> after) {
      return k -> after.getInt(this.get(k));
   }

   default Int2ReferenceFunction<V> composeInt(Int2FloatFunction before) {
      return k -> this.get(before.get(k));
   }

   default Float2LongFunction andThenLong(Reference2LongFunction<V> after) {
      return k -> after.getLong(this.get(k));
   }

   default Long2ReferenceFunction<V> composeLong(Long2FloatFunction before) {
      return k -> this.get(before.get(k));
   }

   default Float2CharFunction andThenChar(Reference2CharFunction<V> after) {
      return k -> after.getChar(this.get(k));
   }

   default Char2ReferenceFunction<V> composeChar(Char2FloatFunction before) {
      return k -> this.get(before.get(k));
   }

   default Float2FloatFunction andThenFloat(Reference2FloatFunction<V> after) {
      return k -> after.getFloat(this.get(k));
   }

   default Float2ReferenceFunction<V> composeFloat(Float2FloatFunction before) {
      return k -> this.get(before.get(k));
   }

   default Float2DoubleFunction andThenDouble(Reference2DoubleFunction<V> after) {
      return k -> after.getDouble(this.get(k));
   }

   default Double2ReferenceFunction<V> composeDouble(Double2FloatFunction before) {
      return k -> this.get(before.get(k));
   }

   default <T> Float2ObjectFunction<T> andThenObject(Reference2ObjectFunction<? super V, ? extends T> after) {
      return k -> (T)after.get(this.get(k));
   }

   default <T> Object2ReferenceFunction<T, V> composeObject(Object2FloatFunction<? super T> before) {
      return k -> this.get(before.getFloat(k));
   }

   default <T> Float2ReferenceFunction<T> andThenReference(Reference2ReferenceFunction<? super V, ? extends T> after) {
      return k -> (T)after.get(this.get(k));
   }

   default <T> Reference2ReferenceFunction<T, V> composeReference(Reference2FloatFunction<? super T> before) {
      return k -> this.get(before.getFloat(k));
   }
}
