package it.unimi.dsi.fastutil.objects;

import it.unimi.dsi.fastutil.bytes.Byte2ByteFunction;
import it.unimi.dsi.fastutil.bytes.Byte2CharFunction;
import it.unimi.dsi.fastutil.bytes.Byte2DoubleFunction;
import it.unimi.dsi.fastutil.bytes.Byte2FloatFunction;
import it.unimi.dsi.fastutil.bytes.Byte2IntFunction;
import it.unimi.dsi.fastutil.bytes.Byte2LongFunction;
import it.unimi.dsi.fastutil.bytes.Byte2ObjectFunction;
import it.unimi.dsi.fastutil.bytes.Byte2ReferenceFunction;
import it.unimi.dsi.fastutil.bytes.Byte2ShortFunction;
import it.unimi.dsi.fastutil.chars.Char2ByteFunction;
import it.unimi.dsi.fastutil.chars.Char2ReferenceFunction;
import it.unimi.dsi.fastutil.doubles.Double2ByteFunction;
import it.unimi.dsi.fastutil.doubles.Double2ReferenceFunction;
import it.unimi.dsi.fastutil.floats.Float2ByteFunction;
import it.unimi.dsi.fastutil.floats.Float2ReferenceFunction;
import it.unimi.dsi.fastutil.ints.Int2ByteFunction;
import it.unimi.dsi.fastutil.ints.Int2ReferenceFunction;
import it.unimi.dsi.fastutil.longs.Long2ByteFunction;
import it.unimi.dsi.fastutil.longs.Long2ReferenceFunction;
import it.unimi.dsi.fastutil.shorts.Short2ByteFunction;
import it.unimi.dsi.fastutil.shorts.Short2ReferenceFunction;
import java.util.function.Function;
import java.util.function.ToIntFunction;

@FunctionalInterface
public interface Reference2ByteFunction<K> extends it.unimi.dsi.fastutil.Function<K, Byte>, ToIntFunction<K> {
   @Override
   default int applyAsInt(K operand) {
      return this.getByte(operand);
   }

   default byte put(K key, byte value) {
      throw new UnsupportedOperationException();
   }

   byte getByte(Object var1);

   default byte getOrDefault(Object key, byte defaultValue) {
      byte v;
      return (v = this.getByte(key)) == this.defaultReturnValue() && !this.containsKey(key) ? defaultValue : v;
   }

   default byte removeByte(Object key) {
      throw new UnsupportedOperationException();
   }

   @Deprecated
   default Byte put(K key, Byte value) {
      boolean containsKey = this.containsKey(key);
      byte v = this.put(key, value.byteValue());
      return containsKey ? v : null;
   }

   @Deprecated
   default Byte get(Object key) {
      byte v;
      return (v = this.getByte(key)) == this.defaultReturnValue() && !this.containsKey(key) ? null : v;
   }

   @Deprecated
   default Byte getOrDefault(Object key, Byte defaultValue) {
      byte v = this.getByte(key);
      return v == this.defaultReturnValue() && !this.containsKey(key) ? defaultValue : v;
   }

   @Deprecated
   default Byte remove(Object key) {
      return this.containsKey(key) ? this.removeByte(key) : null;
   }

   default void defaultReturnValue(byte rv) {
      throw new UnsupportedOperationException();
   }

   default byte defaultReturnValue() {
      return 0;
   }

   @Deprecated
   @Override
   default <T> Function<K, T> andThen(Function<? super Byte, ? extends T> after) {
      return it.unimi.dsi.fastutil.Function.super.andThen(after);
   }

   default Reference2ByteFunction<K> andThenByte(Byte2ByteFunction after) {
      return k -> after.get(this.getByte(k));
   }

   default Byte2ByteFunction composeByte(Byte2ReferenceFunction<K> before) {
      return k -> this.getByte(before.get(k));
   }

   default Reference2ShortFunction<K> andThenShort(Byte2ShortFunction after) {
      return k -> after.get(this.getByte(k));
   }

   default Short2ByteFunction composeShort(Short2ReferenceFunction<K> before) {
      return k -> this.getByte(before.get(k));
   }

   default Reference2IntFunction<K> andThenInt(Byte2IntFunction after) {
      return k -> after.get(this.getByte(k));
   }

   default Int2ByteFunction composeInt(Int2ReferenceFunction<K> before) {
      return k -> this.getByte(before.get(k));
   }

   default Reference2LongFunction<K> andThenLong(Byte2LongFunction after) {
      return k -> after.get(this.getByte(k));
   }

   default Long2ByteFunction composeLong(Long2ReferenceFunction<K> before) {
      return k -> this.getByte(before.get(k));
   }

   default Reference2CharFunction<K> andThenChar(Byte2CharFunction after) {
      return k -> after.get(this.getByte(k));
   }

   default Char2ByteFunction composeChar(Char2ReferenceFunction<K> before) {
      return k -> this.getByte(before.get(k));
   }

   default Reference2FloatFunction<K> andThenFloat(Byte2FloatFunction after) {
      return k -> after.get(this.getByte(k));
   }

   default Float2ByteFunction composeFloat(Float2ReferenceFunction<K> before) {
      return k -> this.getByte(before.get(k));
   }

   default Reference2DoubleFunction<K> andThenDouble(Byte2DoubleFunction after) {
      return k -> after.get(this.getByte(k));
   }

   default Double2ByteFunction composeDouble(Double2ReferenceFunction<K> before) {
      return k -> this.getByte(before.get(k));
   }

   default <T> Reference2ObjectFunction<K, T> andThenObject(Byte2ObjectFunction<? extends T> after) {
      return k -> (T)after.get(this.getByte(k));
   }

   default <T> Object2ByteFunction<T> composeObject(Object2ReferenceFunction<? super T, ? extends K> before) {
      return k -> this.getByte(before.get(k));
   }

   default <T> Reference2ReferenceFunction<K, T> andThenReference(Byte2ReferenceFunction<? extends T> after) {
      return k -> (T)after.get(this.getByte(k));
   }

   default <T> Reference2ByteFunction<T> composeReference(Reference2ReferenceFunction<? super T, ? extends K> before) {
      return k -> this.getByte(before.get(k));
   }
}
