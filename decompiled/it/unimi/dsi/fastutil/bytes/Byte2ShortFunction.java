package it.unimi.dsi.fastutil.bytes;

import it.unimi.dsi.fastutil.SafeMath;
import it.unimi.dsi.fastutil.chars.Char2ByteFunction;
import it.unimi.dsi.fastutil.chars.Char2ShortFunction;
import it.unimi.dsi.fastutil.doubles.Double2ByteFunction;
import it.unimi.dsi.fastutil.doubles.Double2ShortFunction;
import it.unimi.dsi.fastutil.floats.Float2ByteFunction;
import it.unimi.dsi.fastutil.floats.Float2ShortFunction;
import it.unimi.dsi.fastutil.ints.Int2ByteFunction;
import it.unimi.dsi.fastutil.ints.Int2ShortFunction;
import it.unimi.dsi.fastutil.longs.Long2ByteFunction;
import it.unimi.dsi.fastutil.longs.Long2ShortFunction;
import it.unimi.dsi.fastutil.objects.Object2ByteFunction;
import it.unimi.dsi.fastutil.objects.Object2ShortFunction;
import it.unimi.dsi.fastutil.objects.Reference2ByteFunction;
import it.unimi.dsi.fastutil.objects.Reference2ShortFunction;
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
import java.util.function.IntUnaryOperator;

@FunctionalInterface
public interface Byte2ShortFunction extends it.unimi.dsi.fastutil.Function<Byte, Short>, IntUnaryOperator {
   @Deprecated
   @Override
   default int applyAsInt(int operand) {
      return this.get(SafeMath.safeIntToByte(operand));
   }

   default short put(byte key, short value) {
      throw new UnsupportedOperationException();
   }

   short get(byte var1);

   default short getOrDefault(byte key, short defaultValue) {
      short v;
      return (v = this.get(key)) == this.defaultReturnValue() && !this.containsKey(key) ? defaultValue : v;
   }

   default short remove(byte key) {
      throw new UnsupportedOperationException();
   }

   @Deprecated
   default Short put(Byte key, Short value) {
      byte k = key;
      boolean containsKey = this.containsKey(k);
      short v = this.put(k, value.shortValue());
      return containsKey ? v : null;
   }

   @Deprecated
   default Short get(Object key) {
      if (key == null) {
         return null;
      } else {
         byte k = (Byte)key;
         short v;
         return (v = this.get(k)) == this.defaultReturnValue() && !this.containsKey(k) ? null : v;
      }
   }

   @Deprecated
   default Short getOrDefault(Object key, Short defaultValue) {
      if (key == null) {
         return defaultValue;
      } else {
         byte k = (Byte)key;
         short v = this.get(k);
         return v == this.defaultReturnValue() && !this.containsKey(k) ? defaultValue : v;
      }
   }

   @Deprecated
   default Short remove(Object key) {
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

   default void defaultReturnValue(short rv) {
      throw new UnsupportedOperationException();
   }

   default short defaultReturnValue() {
      return 0;
   }

   @Deprecated
   @Override
   default <T> Function<T, Short> compose(Function<? super T, ? extends Byte> before) {
      return it.unimi.dsi.fastutil.Function.super.compose(before);
   }

   @Deprecated
   @Override
   default <T> Function<Byte, T> andThen(Function<? super Short, ? extends T> after) {
      return it.unimi.dsi.fastutil.Function.super.andThen(after);
   }

   default Byte2ByteFunction andThenByte(Short2ByteFunction after) {
      return k -> after.get(this.get(k));
   }

   default Byte2ShortFunction composeByte(Byte2ByteFunction before) {
      return k -> this.get(before.get(k));
   }

   default Byte2ShortFunction andThenShort(Short2ShortFunction after) {
      return k -> after.get(this.get(k));
   }

   default Short2ShortFunction composeShort(Short2ByteFunction before) {
      return k -> this.get(before.get(k));
   }

   default Byte2IntFunction andThenInt(Short2IntFunction after) {
      return k -> after.get(this.get(k));
   }

   default Int2ShortFunction composeInt(Int2ByteFunction before) {
      return k -> this.get(before.get(k));
   }

   default Byte2LongFunction andThenLong(Short2LongFunction after) {
      return k -> after.get(this.get(k));
   }

   default Long2ShortFunction composeLong(Long2ByteFunction before) {
      return k -> this.get(before.get(k));
   }

   default Byte2CharFunction andThenChar(Short2CharFunction after) {
      return k -> after.get(this.get(k));
   }

   default Char2ShortFunction composeChar(Char2ByteFunction before) {
      return k -> this.get(before.get(k));
   }

   default Byte2FloatFunction andThenFloat(Short2FloatFunction after) {
      return k -> after.get(this.get(k));
   }

   default Float2ShortFunction composeFloat(Float2ByteFunction before) {
      return k -> this.get(before.get(k));
   }

   default Byte2DoubleFunction andThenDouble(Short2DoubleFunction after) {
      return k -> after.get(this.get(k));
   }

   default Double2ShortFunction composeDouble(Double2ByteFunction before) {
      return k -> this.get(before.get(k));
   }

   default <T> Byte2ObjectFunction<T> andThenObject(Short2ObjectFunction<? extends T> after) {
      return k -> (T)after.get(this.get(k));
   }

   default <T> Object2ShortFunction<T> composeObject(Object2ByteFunction<? super T> before) {
      return k -> this.get(before.getByte(k));
   }

   default <T> Byte2ReferenceFunction<T> andThenReference(Short2ReferenceFunction<? extends T> after) {
      return k -> (T)after.get(this.get(k));
   }

   default <T> Reference2ShortFunction<T> composeReference(Reference2ByteFunction<? super T> before) {
      return k -> this.get(before.getByte(k));
   }
}
