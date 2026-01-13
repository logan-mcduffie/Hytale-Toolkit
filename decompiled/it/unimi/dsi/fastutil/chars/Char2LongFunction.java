package it.unimi.dsi.fastutil.chars;

import it.unimi.dsi.fastutil.SafeMath;
import it.unimi.dsi.fastutil.bytes.Byte2CharFunction;
import it.unimi.dsi.fastutil.bytes.Byte2LongFunction;
import it.unimi.dsi.fastutil.doubles.Double2CharFunction;
import it.unimi.dsi.fastutil.doubles.Double2LongFunction;
import it.unimi.dsi.fastutil.floats.Float2CharFunction;
import it.unimi.dsi.fastutil.floats.Float2LongFunction;
import it.unimi.dsi.fastutil.ints.Int2CharFunction;
import it.unimi.dsi.fastutil.ints.Int2LongFunction;
import it.unimi.dsi.fastutil.longs.Long2ByteFunction;
import it.unimi.dsi.fastutil.longs.Long2CharFunction;
import it.unimi.dsi.fastutil.longs.Long2DoubleFunction;
import it.unimi.dsi.fastutil.longs.Long2FloatFunction;
import it.unimi.dsi.fastutil.longs.Long2IntFunction;
import it.unimi.dsi.fastutil.longs.Long2LongFunction;
import it.unimi.dsi.fastutil.longs.Long2ObjectFunction;
import it.unimi.dsi.fastutil.longs.Long2ReferenceFunction;
import it.unimi.dsi.fastutil.longs.Long2ShortFunction;
import it.unimi.dsi.fastutil.objects.Object2CharFunction;
import it.unimi.dsi.fastutil.objects.Object2LongFunction;
import it.unimi.dsi.fastutil.objects.Reference2CharFunction;
import it.unimi.dsi.fastutil.objects.Reference2LongFunction;
import it.unimi.dsi.fastutil.shorts.Short2CharFunction;
import it.unimi.dsi.fastutil.shorts.Short2LongFunction;
import java.util.function.Function;
import java.util.function.IntToLongFunction;

@FunctionalInterface
public interface Char2LongFunction extends it.unimi.dsi.fastutil.Function<Character, Long>, IntToLongFunction {
   @Deprecated
   @Override
   default long applyAsLong(int operand) {
      return this.get(SafeMath.safeIntToChar(operand));
   }

   default long put(char key, long value) {
      throw new UnsupportedOperationException();
   }

   long get(char var1);

   default long getOrDefault(char key, long defaultValue) {
      long v;
      return (v = this.get(key)) == this.defaultReturnValue() && !this.containsKey(key) ? defaultValue : v;
   }

   default long remove(char key) {
      throw new UnsupportedOperationException();
   }

   @Deprecated
   default Long put(Character key, Long value) {
      char k = key;
      boolean containsKey = this.containsKey(k);
      long v = this.put(k, value.longValue());
      return containsKey ? v : null;
   }

   @Deprecated
   default Long get(Object key) {
      if (key == null) {
         return null;
      } else {
         char k = (Character)key;
         long v;
         return (v = this.get(k)) == this.defaultReturnValue() && !this.containsKey(k) ? null : v;
      }
   }

   @Deprecated
   default Long getOrDefault(Object key, Long defaultValue) {
      if (key == null) {
         return defaultValue;
      } else {
         char k = (Character)key;
         long v = this.get(k);
         return v == this.defaultReturnValue() && !this.containsKey(k) ? defaultValue : v;
      }
   }

   @Deprecated
   default Long remove(Object key) {
      if (key == null) {
         return null;
      } else {
         char k = (Character)key;
         return this.containsKey(k) ? this.remove(k) : null;
      }
   }

   default boolean containsKey(char key) {
      return true;
   }

   @Deprecated
   @Override
   default boolean containsKey(Object key) {
      return key == null ? false : this.containsKey(((Character)key).charValue());
   }

   default void defaultReturnValue(long rv) {
      throw new UnsupportedOperationException();
   }

   default long defaultReturnValue() {
      return 0L;
   }

   @Deprecated
   @Override
   default <T> Function<T, Long> compose(Function<? super T, ? extends Character> before) {
      return it.unimi.dsi.fastutil.Function.super.compose(before);
   }

   @Deprecated
   @Override
   default <T> Function<Character, T> andThen(Function<? super Long, ? extends T> after) {
      return it.unimi.dsi.fastutil.Function.super.andThen(after);
   }

   default Char2ByteFunction andThenByte(Long2ByteFunction after) {
      return k -> after.get(this.get(k));
   }

   default Byte2LongFunction composeByte(Byte2CharFunction before) {
      return k -> this.get(before.get(k));
   }

   default Char2ShortFunction andThenShort(Long2ShortFunction after) {
      return k -> after.get(this.get(k));
   }

   default Short2LongFunction composeShort(Short2CharFunction before) {
      return k -> this.get(before.get(k));
   }

   default Char2IntFunction andThenInt(Long2IntFunction after) {
      return k -> after.get(this.get(k));
   }

   default Int2LongFunction composeInt(Int2CharFunction before) {
      return k -> this.get(before.get(k));
   }

   default Char2LongFunction andThenLong(Long2LongFunction after) {
      return k -> after.get(this.get(k));
   }

   default Long2LongFunction composeLong(Long2CharFunction before) {
      return k -> this.get(before.get(k));
   }

   default Char2CharFunction andThenChar(Long2CharFunction after) {
      return k -> after.get(this.get(k));
   }

   default Char2LongFunction composeChar(Char2CharFunction before) {
      return k -> this.get(before.get(k));
   }

   default Char2FloatFunction andThenFloat(Long2FloatFunction after) {
      return k -> after.get(this.get(k));
   }

   default Float2LongFunction composeFloat(Float2CharFunction before) {
      return k -> this.get(before.get(k));
   }

   default Char2DoubleFunction andThenDouble(Long2DoubleFunction after) {
      return k -> after.get(this.get(k));
   }

   default Double2LongFunction composeDouble(Double2CharFunction before) {
      return k -> this.get(before.get(k));
   }

   default <T> Char2ObjectFunction<T> andThenObject(Long2ObjectFunction<? extends T> after) {
      return k -> (T)after.get(this.get(k));
   }

   default <T> Object2LongFunction<T> composeObject(Object2CharFunction<? super T> before) {
      return k -> this.get(before.getChar(k));
   }

   default <T> Char2ReferenceFunction<T> andThenReference(Long2ReferenceFunction<? extends T> after) {
      return k -> (T)after.get(this.get(k));
   }

   default <T> Reference2LongFunction<T> composeReference(Reference2CharFunction<? super T> before) {
      return k -> this.get(before.getChar(k));
   }
}
