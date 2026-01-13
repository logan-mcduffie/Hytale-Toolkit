package it.unimi.dsi.fastutil.objects;

import it.unimi.dsi.fastutil.bytes.Byte2CharFunction;
import it.unimi.dsi.fastutil.bytes.Byte2ReferenceFunction;
import it.unimi.dsi.fastutil.chars.Char2ByteFunction;
import it.unimi.dsi.fastutil.chars.Char2CharFunction;
import it.unimi.dsi.fastutil.chars.Char2DoubleFunction;
import it.unimi.dsi.fastutil.chars.Char2FloatFunction;
import it.unimi.dsi.fastutil.chars.Char2IntFunction;
import it.unimi.dsi.fastutil.chars.Char2LongFunction;
import it.unimi.dsi.fastutil.chars.Char2ObjectFunction;
import it.unimi.dsi.fastutil.chars.Char2ReferenceFunction;
import it.unimi.dsi.fastutil.chars.Char2ShortFunction;
import it.unimi.dsi.fastutil.doubles.Double2CharFunction;
import it.unimi.dsi.fastutil.doubles.Double2ReferenceFunction;
import it.unimi.dsi.fastutil.floats.Float2CharFunction;
import it.unimi.dsi.fastutil.floats.Float2ReferenceFunction;
import it.unimi.dsi.fastutil.ints.Int2CharFunction;
import it.unimi.dsi.fastutil.ints.Int2ReferenceFunction;
import it.unimi.dsi.fastutil.longs.Long2CharFunction;
import it.unimi.dsi.fastutil.longs.Long2ReferenceFunction;
import it.unimi.dsi.fastutil.shorts.Short2CharFunction;
import it.unimi.dsi.fastutil.shorts.Short2ReferenceFunction;
import java.util.function.Function;
import java.util.function.ToIntFunction;

@FunctionalInterface
public interface Reference2CharFunction<K> extends it.unimi.dsi.fastutil.Function<K, Character>, ToIntFunction<K> {
   @Override
   default int applyAsInt(K operand) {
      return this.getChar(operand);
   }

   default char put(K key, char value) {
      throw new UnsupportedOperationException();
   }

   char getChar(Object var1);

   default char getOrDefault(Object key, char defaultValue) {
      char v;
      return (v = this.getChar(key)) == this.defaultReturnValue() && !this.containsKey(key) ? defaultValue : v;
   }

   default char removeChar(Object key) {
      throw new UnsupportedOperationException();
   }

   @Deprecated
   default Character put(K key, Character value) {
      boolean containsKey = this.containsKey(key);
      char v = this.put(key, value.charValue());
      return containsKey ? v : null;
   }

   @Deprecated
   default Character get(Object key) {
      char v;
      return (v = this.getChar(key)) == this.defaultReturnValue() && !this.containsKey(key) ? null : v;
   }

   @Deprecated
   default Character getOrDefault(Object key, Character defaultValue) {
      char v = this.getChar(key);
      return v == this.defaultReturnValue() && !this.containsKey(key) ? defaultValue : v;
   }

   @Deprecated
   default Character remove(Object key) {
      return this.containsKey(key) ? this.removeChar(key) : null;
   }

   default void defaultReturnValue(char rv) {
      throw new UnsupportedOperationException();
   }

   default char defaultReturnValue() {
      return '\u0000';
   }

   @Deprecated
   @Override
   default <T> Function<K, T> andThen(Function<? super Character, ? extends T> after) {
      return it.unimi.dsi.fastutil.Function.super.andThen(after);
   }

   default Reference2ByteFunction<K> andThenByte(Char2ByteFunction after) {
      return k -> after.get(this.getChar(k));
   }

   default Byte2CharFunction composeByte(Byte2ReferenceFunction<K> before) {
      return k -> this.getChar(before.get(k));
   }

   default Reference2ShortFunction<K> andThenShort(Char2ShortFunction after) {
      return k -> after.get(this.getChar(k));
   }

   default Short2CharFunction composeShort(Short2ReferenceFunction<K> before) {
      return k -> this.getChar(before.get(k));
   }

   default Reference2IntFunction<K> andThenInt(Char2IntFunction after) {
      return k -> after.get(this.getChar(k));
   }

   default Int2CharFunction composeInt(Int2ReferenceFunction<K> before) {
      return k -> this.getChar(before.get(k));
   }

   default Reference2LongFunction<K> andThenLong(Char2LongFunction after) {
      return k -> after.get(this.getChar(k));
   }

   default Long2CharFunction composeLong(Long2ReferenceFunction<K> before) {
      return k -> this.getChar(before.get(k));
   }

   default Reference2CharFunction<K> andThenChar(Char2CharFunction after) {
      return k -> after.get(this.getChar(k));
   }

   default Char2CharFunction composeChar(Char2ReferenceFunction<K> before) {
      return k -> this.getChar(before.get(k));
   }

   default Reference2FloatFunction<K> andThenFloat(Char2FloatFunction after) {
      return k -> after.get(this.getChar(k));
   }

   default Float2CharFunction composeFloat(Float2ReferenceFunction<K> before) {
      return k -> this.getChar(before.get(k));
   }

   default Reference2DoubleFunction<K> andThenDouble(Char2DoubleFunction after) {
      return k -> after.get(this.getChar(k));
   }

   default Double2CharFunction composeDouble(Double2ReferenceFunction<K> before) {
      return k -> this.getChar(before.get(k));
   }

   default <T> Reference2ObjectFunction<K, T> andThenObject(Char2ObjectFunction<? extends T> after) {
      return k -> (T)after.get(this.getChar(k));
   }

   default <T> Object2CharFunction<T> composeObject(Object2ReferenceFunction<? super T, ? extends K> before) {
      return k -> this.getChar(before.get(k));
   }

   default <T> Reference2ReferenceFunction<K, T> andThenReference(Char2ReferenceFunction<? extends T> after) {
      return k -> (T)after.get(this.getChar(k));
   }

   default <T> Reference2CharFunction<T> composeReference(Reference2ReferenceFunction<? super T, ? extends K> before) {
      return k -> this.getChar(before.get(k));
   }
}
