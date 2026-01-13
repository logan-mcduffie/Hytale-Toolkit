package it.unimi.dsi.fastutil.shorts;

import it.unimi.dsi.fastutil.SafeMath;
import it.unimi.dsi.fastutil.bytes.Byte2CharFunction;
import it.unimi.dsi.fastutil.bytes.Byte2ShortFunction;
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
import it.unimi.dsi.fastutil.doubles.Double2ShortFunction;
import it.unimi.dsi.fastutil.floats.Float2CharFunction;
import it.unimi.dsi.fastutil.floats.Float2ShortFunction;
import it.unimi.dsi.fastutil.ints.Int2CharFunction;
import it.unimi.dsi.fastutil.ints.Int2ShortFunction;
import it.unimi.dsi.fastutil.longs.Long2CharFunction;
import it.unimi.dsi.fastutil.longs.Long2ShortFunction;
import it.unimi.dsi.fastutil.objects.Object2CharFunction;
import it.unimi.dsi.fastutil.objects.Object2ShortFunction;
import it.unimi.dsi.fastutil.objects.Reference2CharFunction;
import it.unimi.dsi.fastutil.objects.Reference2ShortFunction;
import java.util.function.Function;
import java.util.function.IntUnaryOperator;

@FunctionalInterface
public interface Short2CharFunction extends it.unimi.dsi.fastutil.Function<Short, Character>, IntUnaryOperator {
   @Deprecated
   @Override
   default int applyAsInt(int operand) {
      return this.get(SafeMath.safeIntToShort(operand));
   }

   default char put(short key, char value) {
      throw new UnsupportedOperationException();
   }

   char get(short var1);

   default char getOrDefault(short key, char defaultValue) {
      char v;
      return (v = this.get(key)) == this.defaultReturnValue() && !this.containsKey(key) ? defaultValue : v;
   }

   default char remove(short key) {
      throw new UnsupportedOperationException();
   }

   @Deprecated
   default Character put(Short key, Character value) {
      short k = key;
      boolean containsKey = this.containsKey(k);
      char v = this.put(k, value.charValue());
      return containsKey ? v : null;
   }

   @Deprecated
   default Character get(Object key) {
      if (key == null) {
         return null;
      } else {
         short k = (Short)key;
         char v;
         return (v = this.get(k)) == this.defaultReturnValue() && !this.containsKey(k) ? null : v;
      }
   }

   @Deprecated
   default Character getOrDefault(Object key, Character defaultValue) {
      if (key == null) {
         return defaultValue;
      } else {
         short k = (Short)key;
         char v = this.get(k);
         return v == this.defaultReturnValue() && !this.containsKey(k) ? defaultValue : v;
      }
   }

   @Deprecated
   default Character remove(Object key) {
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

   default void defaultReturnValue(char rv) {
      throw new UnsupportedOperationException();
   }

   default char defaultReturnValue() {
      return '\u0000';
   }

   @Deprecated
   @Override
   default <T> Function<T, Character> compose(Function<? super T, ? extends Short> before) {
      return it.unimi.dsi.fastutil.Function.super.compose(before);
   }

   @Deprecated
   @Override
   default <T> Function<Short, T> andThen(Function<? super Character, ? extends T> after) {
      return it.unimi.dsi.fastutil.Function.super.andThen(after);
   }

   default Short2ByteFunction andThenByte(Char2ByteFunction after) {
      return k -> after.get(this.get(k));
   }

   default Byte2CharFunction composeByte(Byte2ShortFunction before) {
      return k -> this.get(before.get(k));
   }

   default Short2ShortFunction andThenShort(Char2ShortFunction after) {
      return k -> after.get(this.get(k));
   }

   default Short2CharFunction composeShort(Short2ShortFunction before) {
      return k -> this.get(before.get(k));
   }

   default Short2IntFunction andThenInt(Char2IntFunction after) {
      return k -> after.get(this.get(k));
   }

   default Int2CharFunction composeInt(Int2ShortFunction before) {
      return k -> this.get(before.get(k));
   }

   default Short2LongFunction andThenLong(Char2LongFunction after) {
      return k -> after.get(this.get(k));
   }

   default Long2CharFunction composeLong(Long2ShortFunction before) {
      return k -> this.get(before.get(k));
   }

   default Short2CharFunction andThenChar(Char2CharFunction after) {
      return k -> after.get(this.get(k));
   }

   default Char2CharFunction composeChar(Char2ShortFunction before) {
      return k -> this.get(before.get(k));
   }

   default Short2FloatFunction andThenFloat(Char2FloatFunction after) {
      return k -> after.get(this.get(k));
   }

   default Float2CharFunction composeFloat(Float2ShortFunction before) {
      return k -> this.get(before.get(k));
   }

   default Short2DoubleFunction andThenDouble(Char2DoubleFunction after) {
      return k -> after.get(this.get(k));
   }

   default Double2CharFunction composeDouble(Double2ShortFunction before) {
      return k -> this.get(before.get(k));
   }

   default <T> Short2ObjectFunction<T> andThenObject(Char2ObjectFunction<? extends T> after) {
      return k -> (T)after.get(this.get(k));
   }

   default <T> Object2CharFunction<T> composeObject(Object2ShortFunction<? super T> before) {
      return k -> this.get(before.getShort(k));
   }

   default <T> Short2ReferenceFunction<T> andThenReference(Char2ReferenceFunction<? extends T> after) {
      return k -> (T)after.get(this.get(k));
   }

   default <T> Reference2CharFunction<T> composeReference(Reference2ShortFunction<? super T> before) {
      return k -> this.get(before.getShort(k));
   }
}
