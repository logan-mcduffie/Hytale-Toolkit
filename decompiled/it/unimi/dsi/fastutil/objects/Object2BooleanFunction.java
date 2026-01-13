package it.unimi.dsi.fastutil.objects;

import it.unimi.dsi.fastutil.booleans.Boolean2ByteFunction;
import it.unimi.dsi.fastutil.booleans.Boolean2CharFunction;
import it.unimi.dsi.fastutil.booleans.Boolean2DoubleFunction;
import it.unimi.dsi.fastutil.booleans.Boolean2FloatFunction;
import it.unimi.dsi.fastutil.booleans.Boolean2IntFunction;
import it.unimi.dsi.fastutil.booleans.Boolean2LongFunction;
import it.unimi.dsi.fastutil.booleans.Boolean2ObjectFunction;
import it.unimi.dsi.fastutil.booleans.Boolean2ReferenceFunction;
import it.unimi.dsi.fastutil.booleans.Boolean2ShortFunction;
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
import it.unimi.dsi.fastutil.shorts.Short2BooleanFunction;
import it.unimi.dsi.fastutil.shorts.Short2ObjectFunction;
import java.util.function.Function;
import java.util.function.Predicate;

@FunctionalInterface
public interface Object2BooleanFunction<K> extends it.unimi.dsi.fastutil.Function<K, Boolean>, Predicate<K> {
   @Override
   default boolean test(K operand) {
      return this.getBoolean(operand);
   }

   default boolean put(K key, boolean value) {
      throw new UnsupportedOperationException();
   }

   boolean getBoolean(Object var1);

   default boolean getOrDefault(Object key, boolean defaultValue) {
      boolean v;
      return (v = this.getBoolean(key)) == this.defaultReturnValue() && !this.containsKey(key) ? defaultValue : v;
   }

   default boolean removeBoolean(Object key) {
      throw new UnsupportedOperationException();
   }

   @Deprecated
   default Boolean put(K key, Boolean value) {
      boolean containsKey = this.containsKey(key);
      boolean v = this.put(key, value.booleanValue());
      return containsKey ? v : null;
   }

   @Deprecated
   default Boolean get(Object key) {
      boolean v;
      return (v = this.getBoolean(key)) == this.defaultReturnValue() && !this.containsKey(key) ? null : v;
   }

   @Deprecated
   default Boolean getOrDefault(Object key, Boolean defaultValue) {
      boolean v = this.getBoolean(key);
      return v == this.defaultReturnValue() && !this.containsKey(key) ? defaultValue : v;
   }

   @Deprecated
   default Boolean remove(Object key) {
      return this.containsKey(key) ? this.removeBoolean(key) : null;
   }

   default void defaultReturnValue(boolean rv) {
      throw new UnsupportedOperationException();
   }

   default boolean defaultReturnValue() {
      return false;
   }

   @Deprecated
   @Override
   default <T> Function<K, T> andThen(Function<? super Boolean, ? extends T> after) {
      return it.unimi.dsi.fastutil.Function.super.andThen(after);
   }

   default Object2ByteFunction<K> andThenByte(Boolean2ByteFunction after) {
      return k -> after.get(this.getBoolean(k));
   }

   default Byte2BooleanFunction composeByte(Byte2ObjectFunction<K> before) {
      return k -> this.getBoolean(before.get(k));
   }

   default Object2ShortFunction<K> andThenShort(Boolean2ShortFunction after) {
      return k -> after.get(this.getBoolean(k));
   }

   default Short2BooleanFunction composeShort(Short2ObjectFunction<K> before) {
      return k -> this.getBoolean(before.get(k));
   }

   default Object2IntFunction<K> andThenInt(Boolean2IntFunction after) {
      return k -> after.get(this.getBoolean(k));
   }

   default Int2BooleanFunction composeInt(Int2ObjectFunction<K> before) {
      return k -> this.getBoolean(before.get(k));
   }

   default Object2LongFunction<K> andThenLong(Boolean2LongFunction after) {
      return k -> after.get(this.getBoolean(k));
   }

   default Long2BooleanFunction composeLong(Long2ObjectFunction<K> before) {
      return k -> this.getBoolean(before.get(k));
   }

   default Object2CharFunction<K> andThenChar(Boolean2CharFunction after) {
      return k -> after.get(this.getBoolean(k));
   }

   default Char2BooleanFunction composeChar(Char2ObjectFunction<K> before) {
      return k -> this.getBoolean(before.get(k));
   }

   default Object2FloatFunction<K> andThenFloat(Boolean2FloatFunction after) {
      return k -> after.get(this.getBoolean(k));
   }

   default Float2BooleanFunction composeFloat(Float2ObjectFunction<K> before) {
      return k -> this.getBoolean(before.get(k));
   }

   default Object2DoubleFunction<K> andThenDouble(Boolean2DoubleFunction after) {
      return k -> after.get(this.getBoolean(k));
   }

   default Double2BooleanFunction composeDouble(Double2ObjectFunction<K> before) {
      return k -> this.getBoolean(before.get(k));
   }

   default <T> Object2ObjectFunction<K, T> andThenObject(Boolean2ObjectFunction<? extends T> after) {
      return k -> (T)after.get(this.getBoolean(k));
   }

   default <T> Object2BooleanFunction<T> composeObject(Object2ObjectFunction<? super T, ? extends K> before) {
      return k -> this.getBoolean(before.get(k));
   }

   default <T> Object2ReferenceFunction<K, T> andThenReference(Boolean2ReferenceFunction<? extends T> after) {
      return k -> (T)after.get(this.getBoolean(k));
   }

   default <T> Reference2BooleanFunction<T> composeReference(Reference2ObjectFunction<? super T, ? extends K> before) {
      return k -> this.getBoolean(before.get(k));
   }
}
