package it.unimi.dsi.fastutil.shorts;

import it.unimi.dsi.fastutil.SafeMath;
import it.unimi.dsi.fastutil.bytes.Byte2ObjectFunction;
import it.unimi.dsi.fastutil.bytes.Byte2ShortFunction;
import it.unimi.dsi.fastutil.chars.Char2ObjectFunction;
import it.unimi.dsi.fastutil.chars.Char2ShortFunction;
import it.unimi.dsi.fastutil.doubles.Double2ObjectFunction;
import it.unimi.dsi.fastutil.doubles.Double2ShortFunction;
import it.unimi.dsi.fastutil.floats.Float2ObjectFunction;
import it.unimi.dsi.fastutil.floats.Float2ShortFunction;
import it.unimi.dsi.fastutil.ints.Int2ObjectFunction;
import it.unimi.dsi.fastutil.ints.Int2ShortFunction;
import it.unimi.dsi.fastutil.longs.Long2ObjectFunction;
import it.unimi.dsi.fastutil.longs.Long2ShortFunction;
import it.unimi.dsi.fastutil.objects.Object2ByteFunction;
import it.unimi.dsi.fastutil.objects.Object2CharFunction;
import it.unimi.dsi.fastutil.objects.Object2DoubleFunction;
import it.unimi.dsi.fastutil.objects.Object2FloatFunction;
import it.unimi.dsi.fastutil.objects.Object2IntFunction;
import it.unimi.dsi.fastutil.objects.Object2LongFunction;
import it.unimi.dsi.fastutil.objects.Object2ObjectFunction;
import it.unimi.dsi.fastutil.objects.Object2ReferenceFunction;
import it.unimi.dsi.fastutil.objects.Object2ShortFunction;
import it.unimi.dsi.fastutil.objects.Reference2ObjectFunction;
import it.unimi.dsi.fastutil.objects.Reference2ShortFunction;
import java.util.function.Function;
import java.util.function.IntFunction;

@FunctionalInterface
public interface Short2ObjectFunction<V> extends it.unimi.dsi.fastutil.Function<Short, V>, IntFunction<V> {
   @Deprecated
   @Override
   default V apply(int operand) {
      return this.get(SafeMath.safeIntToShort(operand));
   }

   default V put(short key, V value) {
      throw new UnsupportedOperationException();
   }

   V get(short var1);

   default V getOrDefault(short key, V defaultValue) {
      V v;
      return (v = this.get(key)) == this.defaultReturnValue() && !this.containsKey(key) ? defaultValue : v;
   }

   default V remove(short key) {
      throw new UnsupportedOperationException();
   }

   @Deprecated
   default V put(Short key, V value) {
      short k = key;
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
         short k = (Short)key;
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
         short k = (Short)key;
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

   default void defaultReturnValue(V rv) {
      throw new UnsupportedOperationException();
   }

   default V defaultReturnValue() {
      return null;
   }

   @Deprecated
   @Override
   default <T> Function<T, V> compose(Function<? super T, ? extends Short> before) {
      return (Function<T, V>)it.unimi.dsi.fastutil.Function.super.compose(before);
   }

   default Short2ByteFunction andThenByte(Object2ByteFunction<V> after) {
      return k -> after.getByte(this.get(k));
   }

   default Byte2ObjectFunction<V> composeByte(Byte2ShortFunction before) {
      return k -> this.get(before.get(k));
   }

   default Short2ShortFunction andThenShort(Object2ShortFunction<V> after) {
      return k -> after.getShort(this.get(k));
   }

   default Short2ObjectFunction<V> composeShort(Short2ShortFunction before) {
      return k -> this.get(before.get(k));
   }

   default Short2IntFunction andThenInt(Object2IntFunction<V> after) {
      return k -> after.getInt(this.get(k));
   }

   default Int2ObjectFunction<V> composeInt(Int2ShortFunction before) {
      return k -> this.get(before.get(k));
   }

   default Short2LongFunction andThenLong(Object2LongFunction<V> after) {
      return k -> after.getLong(this.get(k));
   }

   default Long2ObjectFunction<V> composeLong(Long2ShortFunction before) {
      return k -> this.get(before.get(k));
   }

   default Short2CharFunction andThenChar(Object2CharFunction<V> after) {
      return k -> after.getChar(this.get(k));
   }

   default Char2ObjectFunction<V> composeChar(Char2ShortFunction before) {
      return k -> this.get(before.get(k));
   }

   default Short2FloatFunction andThenFloat(Object2FloatFunction<V> after) {
      return k -> after.getFloat(this.get(k));
   }

   default Float2ObjectFunction<V> composeFloat(Float2ShortFunction before) {
      return k -> this.get(before.get(k));
   }

   default Short2DoubleFunction andThenDouble(Object2DoubleFunction<V> after) {
      return k -> after.getDouble(this.get(k));
   }

   default Double2ObjectFunction<V> composeDouble(Double2ShortFunction before) {
      return k -> this.get(before.get(k));
   }

   default <T> Short2ObjectFunction<T> andThenObject(Object2ObjectFunction<? super V, ? extends T> after) {
      return k -> (T)after.get(this.get(k));
   }

   default <T> Object2ObjectFunction<T, V> composeObject(Object2ShortFunction<? super T> before) {
      return k -> this.get(before.getShort(k));
   }

   default <T> Short2ReferenceFunction<T> andThenReference(Object2ReferenceFunction<? super V, ? extends T> after) {
      return k -> (T)after.get(this.get(k));
   }

   default <T> Reference2ObjectFunction<T, V> composeReference(Reference2ShortFunction<? super T> before) {
      return k -> this.get(before.getShort(k));
   }
}
