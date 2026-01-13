package it.unimi.dsi.fastutil.bytes;

import it.unimi.dsi.fastutil.SafeMath;
import it.unimi.dsi.fastutil.chars.Char2ByteFunction;
import it.unimi.dsi.fastutil.chars.Char2CharFunction;
import it.unimi.dsi.fastutil.chars.Char2DoubleFunction;
import it.unimi.dsi.fastutil.chars.Char2FloatFunction;
import it.unimi.dsi.fastutil.chars.Char2IntFunction;
import it.unimi.dsi.fastutil.chars.Char2LongFunction;
import it.unimi.dsi.fastutil.chars.Char2ObjectFunction;
import it.unimi.dsi.fastutil.chars.Char2ReferenceFunction;
import it.unimi.dsi.fastutil.chars.Char2ShortFunction;
import it.unimi.dsi.fastutil.doubles.Double2ByteFunction;
import it.unimi.dsi.fastutil.doubles.Double2CharFunction;
import it.unimi.dsi.fastutil.floats.Float2ByteFunction;
import it.unimi.dsi.fastutil.floats.Float2CharFunction;
import it.unimi.dsi.fastutil.ints.Int2ByteFunction;
import it.unimi.dsi.fastutil.ints.Int2CharFunction;
import it.unimi.dsi.fastutil.longs.Long2ByteFunction;
import it.unimi.dsi.fastutil.longs.Long2CharFunction;
import it.unimi.dsi.fastutil.objects.Object2ByteFunction;
import it.unimi.dsi.fastutil.objects.Object2CharFunction;
import it.unimi.dsi.fastutil.objects.Reference2ByteFunction;
import it.unimi.dsi.fastutil.objects.Reference2CharFunction;
import it.unimi.dsi.fastutil.shorts.Short2ByteFunction;
import it.unimi.dsi.fastutil.shorts.Short2CharFunction;
import java.util.function.Function;
import java.util.function.IntUnaryOperator;

@FunctionalInterface
public interface Byte2CharFunction extends it.unimi.dsi.fastutil.Function<Byte, Character>, IntUnaryOperator {
   @Deprecated
   @Override
   default int applyAsInt(int operand) {
      return this.get(SafeMath.safeIntToByte(operand));
   }

   default char put(byte key, char value) {
      throw new UnsupportedOperationException();
   }

   char get(byte var1);

   default char getOrDefault(byte key, char defaultValue) {
      char v;
      return (v = this.get(key)) == this.defaultReturnValue() && !this.containsKey(key) ? defaultValue : v;
   }

   default char remove(byte key) {
      throw new UnsupportedOperationException();
   }

   @Deprecated
   default Character put(Byte key, Character value) {
      byte k = key;
      boolean containsKey = this.containsKey(k);
      char v = this.put(k, value.charValue());
      return containsKey ? v : null;
   }

   @Deprecated
   default Character get(Object key) {
      if (key == null) {
         return null;
      } else {
         byte k = (Byte)key;
         char v;
         return (v = this.get(k)) == this.defaultReturnValue() && !this.containsKey(k) ? null : v;
      }
   }

   @Deprecated
   default Character getOrDefault(Object key, Character defaultValue) {
      if (key == null) {
         return defaultValue;
      } else {
         byte k = (Byte)key;
         char v = this.get(k);
         return v == this.defaultReturnValue() && !this.containsKey(k) ? defaultValue : v;
      }
   }

   @Deprecated
   default Character remove(Object key) {
      if (key == null) {
         return null;
      } else {
         byte k = (Byte)key;
         return this.containsKey(k) ? this.remove(k) : null;
      }
   }

   default boolean containsKey(byte key) {
      return true;
   }

   @Deprecated
   @Override
   default boolean containsKey(Object key) {
      return key == null ? false : this.containsKey(((Byte)key).byteValue());
   }

   default void defaultReturnValue(char rv) {
      throw new UnsupportedOperationException();
   }

   default char defaultReturnValue() {
      return '\u0000';
   }

   @Deprecated
   @Override
   default <T> Function<T, Character> compose(Function<? super T, ? extends Byte> before) {
      return it.unimi.dsi.fastutil.Function.super.compose(before);
   }

   @Deprecated
   @Override
   default <T> Function<Byte, T> andThen(Function<? super Character, ? extends T> after) {
      return it.unimi.dsi.fastutil.Function.super.andThen(after);
   }

   default Byte2ByteFunction andThenByte(Char2ByteFunction after) {
      return k -> after.get(this.get(k));
   }

   default Byte2CharFunction composeByte(Byte2ByteFunction before) {
      return k -> this.get(before.get(k));
   }

   default Byte2ShortFunction andThenShort(Char2ShortFunction after) {
      return k -> after.get(this.get(k));
   }

   default Short2CharFunction composeShort(Short2ByteFunction before) {
      return k -> this.get(before.get(k));
   }

   default Byte2IntFunction andThenInt(Char2IntFunction after) {
      return k -> after.get(this.get(k));
   }

   default Int2CharFunction composeInt(Int2ByteFunction before) {
      return k -> this.get(before.get(k));
   }

   default Byte2LongFunction andThenLong(Char2LongFunction after) {
      return k -> after.get(this.get(k));
   }

   default Long2CharFunction composeLong(Long2ByteFunction before) {
      return k -> this.get(before.get(k));
   }

   default Byte2CharFunction andThenChar(Char2CharFunction after) {
      return k -> after.get(this.get(k));
   }

   default Char2CharFunction composeChar(Char2ByteFunction before) {
      return k -> this.get(before.get(k));
   }

   default Byte2FloatFunction andThenFloat(Char2FloatFunction after) {
      return k -> after.get(this.get(k));
   }

   default Float2CharFunction composeFloat(Float2ByteFunction before) {
      return k -> this.get(before.get(k));
   }

   default Byte2DoubleFunction andThenDouble(Char2DoubleFunction after) {
      return k -> after.get(this.get(k));
   }

   default Double2CharFunction composeDouble(Double2ByteFunction before) {
      return k -> this.get(before.get(k));
   }

   default <T> Byte2ObjectFunction<T> andThenObject(Char2ObjectFunction<? extends T> after) {
      return k -> (T)after.get(this.get(k));
   }

   default <T> Object2CharFunction<T> composeObject(Object2ByteFunction<? super T> before) {
      return k -> this.get(before.getByte(k));
   }

   default <T> Byte2ReferenceFunction<T> andThenReference(Char2ReferenceFunction<? extends T> after) {
      return k -> (T)after.get(this.get(k));
   }

   default <T> Reference2CharFunction<T> composeReference(Reference2ByteFunction<? super T> before) {
      return k -> this.get(before.getByte(k));
   }
}
