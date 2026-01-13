package it.unimi.dsi.fastutil.booleans;

import it.unimi.dsi.fastutil.bytes.Byte2BooleanFunction;
import it.unimi.dsi.fastutil.bytes.Byte2ReferenceFunction;
import it.unimi.dsi.fastutil.chars.Char2BooleanFunction;
import it.unimi.dsi.fastutil.chars.Char2ReferenceFunction;
import it.unimi.dsi.fastutil.doubles.Double2BooleanFunction;
import it.unimi.dsi.fastutil.doubles.Double2ReferenceFunction;
import it.unimi.dsi.fastutil.floats.Float2BooleanFunction;
import it.unimi.dsi.fastutil.floats.Float2ReferenceFunction;
import it.unimi.dsi.fastutil.ints.Int2BooleanFunction;
import it.unimi.dsi.fastutil.ints.Int2ReferenceFunction;
import it.unimi.dsi.fastutil.longs.Long2BooleanFunction;
import it.unimi.dsi.fastutil.longs.Long2ReferenceFunction;
import it.unimi.dsi.fastutil.objects.Object2BooleanFunction;
import it.unimi.dsi.fastutil.objects.Object2ReferenceFunction;
import it.unimi.dsi.fastutil.objects.Reference2BooleanFunction;
import it.unimi.dsi.fastutil.objects.Reference2ByteFunction;
import it.unimi.dsi.fastutil.objects.Reference2CharFunction;
import it.unimi.dsi.fastutil.objects.Reference2DoubleFunction;
import it.unimi.dsi.fastutil.objects.Reference2FloatFunction;
import it.unimi.dsi.fastutil.objects.Reference2IntFunction;
import it.unimi.dsi.fastutil.objects.Reference2LongFunction;
import it.unimi.dsi.fastutil.objects.Reference2ObjectFunction;
import it.unimi.dsi.fastutil.objects.Reference2ReferenceFunction;
import it.unimi.dsi.fastutil.objects.Reference2ShortFunction;
import it.unimi.dsi.fastutil.shorts.Short2BooleanFunction;
import it.unimi.dsi.fastutil.shorts.Short2ReferenceFunction;
import java.util.function.Function;

@FunctionalInterface
public interface Boolean2ReferenceFunction<V> extends it.unimi.dsi.fastutil.Function<Boolean, V> {
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

   default Boolean2ByteFunction andThenByte(Reference2ByteFunction<V> after) {
      return k -> after.getByte(this.get(k));
   }

   default Byte2ReferenceFunction<V> composeByte(Byte2BooleanFunction before) {
      return k -> this.get(before.get(k));
   }

   default Boolean2ShortFunction andThenShort(Reference2ShortFunction<V> after) {
      return k -> after.getShort(this.get(k));
   }

   default Short2ReferenceFunction<V> composeShort(Short2BooleanFunction before) {
      return k -> this.get(before.get(k));
   }

   default Boolean2IntFunction andThenInt(Reference2IntFunction<V> after) {
      return k -> after.getInt(this.get(k));
   }

   default Int2ReferenceFunction<V> composeInt(Int2BooleanFunction before) {
      return k -> this.get(before.get(k));
   }

   default Boolean2LongFunction andThenLong(Reference2LongFunction<V> after) {
      return k -> after.getLong(this.get(k));
   }

   default Long2ReferenceFunction<V> composeLong(Long2BooleanFunction before) {
      return k -> this.get(before.get(k));
   }

   default Boolean2CharFunction andThenChar(Reference2CharFunction<V> after) {
      return k -> after.getChar(this.get(k));
   }

   default Char2ReferenceFunction<V> composeChar(Char2BooleanFunction before) {
      return k -> this.get(before.get(k));
   }

   default Boolean2FloatFunction andThenFloat(Reference2FloatFunction<V> after) {
      return k -> after.getFloat(this.get(k));
   }

   default Float2ReferenceFunction<V> composeFloat(Float2BooleanFunction before) {
      return k -> this.get(before.get(k));
   }

   default Boolean2DoubleFunction andThenDouble(Reference2DoubleFunction<V> after) {
      return k -> after.getDouble(this.get(k));
   }

   default Double2ReferenceFunction<V> composeDouble(Double2BooleanFunction before) {
      return k -> this.get(before.get(k));
   }

   default <T> Boolean2ObjectFunction<T> andThenObject(Reference2ObjectFunction<? super V, ? extends T> after) {
      return k -> (T)after.get(this.get(k));
   }

   default <T> Object2ReferenceFunction<T, V> composeObject(Object2BooleanFunction<? super T> before) {
      return k -> this.get(before.getBoolean(k));
   }

   default <T> Boolean2ReferenceFunction<T> andThenReference(Reference2ReferenceFunction<? super V, ? extends T> after) {
      return k -> (T)after.get(this.get(k));
   }

   default <T> Reference2ReferenceFunction<T, V> composeReference(Reference2BooleanFunction<? super T> before) {
      return k -> this.get(before.getBoolean(k));
   }
}
