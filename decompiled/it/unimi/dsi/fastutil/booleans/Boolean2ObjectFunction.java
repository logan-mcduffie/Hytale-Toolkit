package it.unimi.dsi.fastutil.booleans;

import it.unimi.dsi.fastutil.bytes.Byte2BooleanFunction;
import it.unimi.dsi.fastutil.bytes.Byte2ObjectFunction;
import it.unimi.dsi.fastutil.chars.Char2BooleanFunction;
import it.unimi.dsi.fastutil.chars.Char2ObjectFunction;
import it.unimi.dsi.fastutil.doubles.Double2BooleanFunction;
import it.unimi.dsi.fastutil.doubles.Double2ObjectFunction;
import it.unimi.dsi.fastutil.floats.Float2BooleanFunction;
import it.unimi.dsi.fastutil.floats.Float2ObjectFunction;
import it.unimi.dsi.fastutil.ints.Int2BooleanFunction;
import it.unimi.dsi.fastutil.ints.Int2ObjectFunction;
import it.unimi.dsi.fastutil.longs.Long2BooleanFunction;
import it.unimi.dsi.fastutil.longs.Long2ObjectFunction;
import it.unimi.dsi.fastutil.objects.Object2BooleanFunction;
import it.unimi.dsi.fastutil.objects.Object2ByteFunction;
import it.unimi.dsi.fastutil.objects.Object2CharFunction;
import it.unimi.dsi.fastutil.objects.Object2DoubleFunction;
import it.unimi.dsi.fastutil.objects.Object2FloatFunction;
import it.unimi.dsi.fastutil.objects.Object2IntFunction;
import it.unimi.dsi.fastutil.objects.Object2LongFunction;
import it.unimi.dsi.fastutil.objects.Object2ObjectFunction;
import it.unimi.dsi.fastutil.objects.Object2ReferenceFunction;
import it.unimi.dsi.fastutil.objects.Object2ShortFunction;
import it.unimi.dsi.fastutil.objects.Reference2BooleanFunction;
import it.unimi.dsi.fastutil.objects.Reference2ObjectFunction;
import it.unimi.dsi.fastutil.shorts.Short2BooleanFunction;
import it.unimi.dsi.fastutil.shorts.Short2ObjectFunction;
import java.util.function.Function;

@FunctionalInterface
public interface Boolean2ObjectFunction<V> extends it.unimi.dsi.fastutil.Function<Boolean, V> {
   default V put(boolean key, V value) {
      throw new UnsupportedOperationException();
   }

   V get(boolean var1);

   default V getOrDefault(boolean key, V defaultValue) {
      V v;
      return (v = this.get(key)) == this.defaultReturnValue() && !this.containsKey(key) ? defaultValue : v;
   }

   default V remove(boolean key) {
      throw new UnsupportedOperationException();
   }

   @Deprecated
   default V put(Boolean key, V value) {
      boolean k = key;
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
         boolean k = (Boolean)key;
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
         boolean k = (Boolean)key;
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
         boolean k = (Boolean)key;
         return this.containsKey(k) ? this.remove(k) : null;
      }
   }

   default boolean containsKey(boolean key) {
      return true;
   }

   @Deprecated
   @Override
   default boolean containsKey(Object key) {
      return key == null ? false : this.containsKey(((Boolean)key).booleanValue());
   }

   default void defaultReturnValue(V rv) {
      throw new UnsupportedOperationException();
   }

   default V defaultReturnValue() {
      return null;
   }

   @Deprecated
   @Override
   default <T> Function<T, V> compose(Function<? super T, ? extends Boolean> before) {
      return (Function<T, V>)it.unimi.dsi.fastutil.Function.super.compose(before);
   }

   default Boolean2ByteFunction andThenByte(Object2ByteFunction<V> after) {
      return k -> after.getByte(this.get(k));
   }

   default Byte2ObjectFunction<V> composeByte(Byte2BooleanFunction before) {
      return k -> this.get(before.get(k));
   }

   default Boolean2ShortFunction andThenShort(Object2ShortFunction<V> after) {
      return k -> after.getShort(this.get(k));
   }

   default Short2ObjectFunction<V> composeShort(Short2BooleanFunction before) {
      return k -> this.get(before.get(k));
   }

   default Boolean2IntFunction andThenInt(Object2IntFunction<V> after) {
      return k -> after.getInt(this.get(k));
   }

   default Int2ObjectFunction<V> composeInt(Int2BooleanFunction before) {
      return k -> this.get(before.get(k));
   }

   default Boolean2LongFunction andThenLong(Object2LongFunction<V> after) {
      return k -> after.getLong(this.get(k));
   }

   default Long2ObjectFunction<V> composeLong(Long2BooleanFunction before) {
      return k -> this.get(before.get(k));
   }

   default Boolean2CharFunction andThenChar(Object2CharFunction<V> after) {
      return k -> after.getChar(this.get(k));
   }

   default Char2ObjectFunction<V> composeChar(Char2BooleanFunction before) {
      return k -> this.get(before.get(k));
   }

   default Boolean2FloatFunction andThenFloat(Object2FloatFunction<V> after) {
      return k -> after.getFloat(this.get(k));
   }

   default Float2ObjectFunction<V> composeFloat(Float2BooleanFunction before) {
      return k -> this.get(before.get(k));
   }

   default Boolean2DoubleFunction andThenDouble(Object2DoubleFunction<V> after) {
      return k -> after.getDouble(this.get(k));
   }

   default Double2ObjectFunction<V> composeDouble(Double2BooleanFunction before) {
      return k -> this.get(before.get(k));
   }

   default <T> Boolean2ObjectFunction<T> andThenObject(Object2ObjectFunction<? super V, ? extends T> after) {
      return k -> (T)after.get(this.get(k));
   }

   default <T> Object2ObjectFunction<T, V> composeObject(Object2BooleanFunction<? super T> before) {
      return k -> this.get(before.getBoolean(k));
   }

   default <T> Boolean2ReferenceFunction<T> andThenReference(Object2ReferenceFunction<? super V, ? extends T> after) {
      return k -> (T)after.get(this.get(k));
   }

   default <T> Reference2ObjectFunction<T, V> composeReference(Reference2BooleanFunction<? super T> before) {
      return k -> this.get(before.getBoolean(k));
   }
}
