package it.unimi.dsi.fastutil.objects;

import it.unimi.dsi.fastutil.bytes.Byte2ReferenceFunction;
import it.unimi.dsi.fastutil.bytes.Byte2ShortFunction;
import it.unimi.dsi.fastutil.chars.Char2ReferenceFunction;
import it.unimi.dsi.fastutil.chars.Char2ShortFunction;
import it.unimi.dsi.fastutil.doubles.Double2ReferenceFunction;
import it.unimi.dsi.fastutil.doubles.Double2ShortFunction;
import it.unimi.dsi.fastutil.floats.Float2ReferenceFunction;
import it.unimi.dsi.fastutil.floats.Float2ShortFunction;
import it.unimi.dsi.fastutil.ints.Int2ReferenceFunction;
import it.unimi.dsi.fastutil.ints.Int2ShortFunction;
import it.unimi.dsi.fastutil.longs.Long2ReferenceFunction;
import it.unimi.dsi.fastutil.longs.Long2ShortFunction;
import it.unimi.dsi.fastutil.shorts.Short2ByteFunction;
import it.unimi.dsi.fastutil.shorts.Short2CharFunction;
import it.unimi.dsi.fastutil.shorts.Short2DoubleFunction;
import it.unimi.dsi.fastutil.shorts.Short2FloatFunction;
import it.unimi.dsi.fastutil.shorts.Short2IntFunction;
import it.unimi.dsi.fastutil.shorts.Short2LongFunction;
import it.unimi.dsi.fastutil.shorts.Short2ObjectFunction;
import it.unimi.dsi.fastutil.shorts.Short2ReferenceFunction;
import it.unimi.dsi.fastutil.shorts.Short2ShortFunction;
import java.util.function.Function;
import java.util.function.ToIntFunction;

@FunctionalInterface
public interface Reference2ShortFunction<K> extends it.unimi.dsi.fastutil.Function<K, Short>, ToIntFunction<K> {
   @Override
   default int applyAsInt(K operand) {
      return this.getShort(operand);
   }

   default short put(K key, short value) {
      throw new UnsupportedOperationException();
   }

   short getShort(Object var1);

   default short getOrDefault(Object key, short defaultValue) {
      short v;
      return (v = this.getShort(key)) == this.defaultReturnValue() && !this.containsKey(key) ? defaultValue : v;
   }

   default short removeShort(Object key) {
      throw new UnsupportedOperationException();
   }

   @Deprecated
   default Short put(K key, Short value) {
      boolean containsKey = this.containsKey(key);
      short v = this.put(key, value.shortValue());
      return containsKey ? v : null;
   }

   @Deprecated
   default Short get(Object key) {
      short v;
      return (v = this.getShort(key)) == this.defaultReturnValue() && !this.containsKey(key) ? null : v;
   }

   @Deprecated
   default Short getOrDefault(Object key, Short defaultValue) {
      short v = this.getShort(key);
      return v == this.defaultReturnValue() && !this.containsKey(key) ? defaultValue : v;
   }

   @Deprecated
   default Short remove(Object key) {
      return this.containsKey(key) ? this.removeShort(key) : null;
   }

   default void defaultReturnValue(short rv) {
      throw new UnsupportedOperationException();
   }

   default short defaultReturnValue() {
      return 0;
   }

   @Deprecated
   @Override
   default <T> Function<K, T> andThen(Function<? super Short, ? extends T> after) {
      return it.unimi.dsi.fastutil.Function.super.andThen(after);
   }

   default Reference2ByteFunction<K> andThenByte(Short2ByteFunction after) {
      return k -> after.get(this.getShort(k));
   }

   default Byte2ShortFunction composeByte(Byte2ReferenceFunction<K> before) {
      return k -> this.getShort(before.get(k));
   }

   default Reference2ShortFunction<K> andThenShort(Short2ShortFunction after) {
      return k -> after.get(this.getShort(k));
   }

   default Short2ShortFunction composeShort(Short2ReferenceFunction<K> before) {
      return k -> this.getShort(before.get(k));
   }

   default Reference2IntFunction<K> andThenInt(Short2IntFunction after) {
      return k -> after.get(this.getShort(k));
   }

   default Int2ShortFunction composeInt(Int2ReferenceFunction<K> before) {
      return k -> this.getShort(before.get(k));
   }

   default Reference2LongFunction<K> andThenLong(Short2LongFunction after) {
      return k -> after.get(this.getShort(k));
   }

   default Long2ShortFunction composeLong(Long2ReferenceFunction<K> before) {
      return k -> this.getShort(before.get(k));
   }

   default Reference2CharFunction<K> andThenChar(Short2CharFunction after) {
      return k -> after.get(this.getShort(k));
   }

   default Char2ShortFunction composeChar(Char2ReferenceFunction<K> before) {
      return k -> this.getShort(before.get(k));
   }

   default Reference2FloatFunction<K> andThenFloat(Short2FloatFunction after) {
      return k -> after.get(this.getShort(k));
   }

   default Float2ShortFunction composeFloat(Float2ReferenceFunction<K> before) {
      return k -> this.getShort(before.get(k));
   }

   default Reference2DoubleFunction<K> andThenDouble(Short2DoubleFunction after) {
      return k -> after.get(this.getShort(k));
   }

   default Double2ShortFunction composeDouble(Double2ReferenceFunction<K> before) {
      return k -> this.getShort(before.get(k));
   }

   default <T> Reference2ObjectFunction<K, T> andThenObject(Short2ObjectFunction<? extends T> after) {
      return k -> (T)after.get(this.getShort(k));
   }

   default <T> Object2ShortFunction<T> composeObject(Object2ReferenceFunction<? super T, ? extends K> before) {
      return k -> this.getShort(before.get(k));
   }

   default <T> Reference2ReferenceFunction<K, T> andThenReference(Short2ReferenceFunction<? extends T> after) {
      return k -> (T)after.get(this.getShort(k));
   }

   default <T> Reference2ShortFunction<T> composeReference(Reference2ReferenceFunction<? super T, ? extends K> before) {
      return k -> this.getShort(before.get(k));
   }
}
