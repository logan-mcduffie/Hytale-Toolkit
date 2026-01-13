package it.unimi.dsi.fastutil.shorts;

import it.unimi.dsi.fastutil.SafeMath;
import it.unimi.dsi.fastutil.bytes.Byte2ShortFunction;
import it.unimi.dsi.fastutil.chars.Char2ShortFunction;
import it.unimi.dsi.fastutil.doubles.Double2ShortFunction;
import it.unimi.dsi.fastutil.floats.Float2ShortFunction;
import it.unimi.dsi.fastutil.ints.Int2ShortFunction;
import it.unimi.dsi.fastutil.longs.Long2ShortFunction;
import it.unimi.dsi.fastutil.objects.Object2ShortFunction;
import it.unimi.dsi.fastutil.objects.Reference2ShortFunction;
import java.util.function.Function;
import java.util.function.IntUnaryOperator;

@FunctionalInterface
public interface Short2ShortFunction extends it.unimi.dsi.fastutil.Function<Short, Short>, IntUnaryOperator {
   @Deprecated
   @Override
   default int applyAsInt(int operand) {
      return this.get(SafeMath.safeIntToShort(operand));
   }

   default short put(short key, short value) {
      throw new UnsupportedOperationException();
   }

   short get(short var1);

   default short getOrDefault(short key, short defaultValue) {
      short v;
      return (v = this.get(key)) == this.defaultReturnValue() && !this.containsKey(key) ? defaultValue : v;
   }

   default short remove(short key) {
      throw new UnsupportedOperationException();
   }

   @Deprecated
   default Short put(Short key, Short value) {
      short k = key;
      boolean containsKey = this.containsKey(k);
      short v = this.put(k, value.shortValue());
      return containsKey ? v : null;
   }

   @Deprecated
   default Short get(Object key) {
      if (key == null) {
         return null;
      } else {
         short k = (Short)key;
         short v;
         return (v = this.get(k)) == this.defaultReturnValue() && !this.containsKey(k) ? null : v;
      }
   }

   @Deprecated
   default Short getOrDefault(Object key, Short defaultValue) {
      if (key == null) {
         return defaultValue;
      } else {
         short k = (Short)key;
         short v = this.get(k);
         return v == this.defaultReturnValue() && !this.containsKey(k) ? defaultValue : v;
      }
   }

   @Deprecated
   default Short remove(Object key) {
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

   default void defaultReturnValue(short rv) {
      throw new UnsupportedOperationException();
   }

   default short defaultReturnValue() {
      return 0;
   }

   static Short2ShortFunction identity() {
      return k -> k;
   }

   @Deprecated
   @Override
   default <T> Function<T, Short> compose(Function<? super T, ? extends Short> before) {
      return it.unimi.dsi.fastutil.Function.super.compose(before);
   }

   @Deprecated
   @Override
   default <T> Function<Short, T> andThen(Function<? super Short, ? extends T> after) {
      return it.unimi.dsi.fastutil.Function.super.andThen(after);
   }

   default Short2ByteFunction andThenByte(Short2ByteFunction after) {
      return k -> after.get(this.get(k));
   }

   default Byte2ShortFunction composeByte(Byte2ShortFunction before) {
      return k -> this.get(before.get(k));
   }

   default Short2ShortFunction andThenShort(Short2ShortFunction after) {
      return k -> after.get(this.get(k));
   }

   default Short2ShortFunction composeShort(Short2ShortFunction before) {
      return k -> this.get(before.get(k));
   }

   default Short2IntFunction andThenInt(Short2IntFunction after) {
      return k -> after.get(this.get(k));
   }

   default Int2ShortFunction composeInt(Int2ShortFunction before) {
      return k -> this.get(before.get(k));
   }

   default Short2LongFunction andThenLong(Short2LongFunction after) {
      return k -> after.get(this.get(k));
   }

   default Long2ShortFunction composeLong(Long2ShortFunction before) {
      return k -> this.get(before.get(k));
   }

   default Short2CharFunction andThenChar(Short2CharFunction after) {
      return k -> after.get(this.get(k));
   }

   default Char2ShortFunction composeChar(Char2ShortFunction before) {
      return k -> this.get(before.get(k));
   }

   default Short2FloatFunction andThenFloat(Short2FloatFunction after) {
      return k -> after.get(this.get(k));
   }

   default Float2ShortFunction composeFloat(Float2ShortFunction before) {
      return k -> this.get(before.get(k));
   }

   default Short2DoubleFunction andThenDouble(Short2DoubleFunction after) {
      return k -> after.get(this.get(k));
   }

   default Double2ShortFunction composeDouble(Double2ShortFunction before) {
      return k -> this.get(before.get(k));
   }

   default <T> Short2ObjectFunction<T> andThenObject(Short2ObjectFunction<? extends T> after) {
      return k -> (T)after.get(this.get(k));
   }

   default <T> Object2ShortFunction<T> composeObject(Object2ShortFunction<? super T> before) {
      return k -> this.get(before.getShort(k));
   }

   default <T> Short2ReferenceFunction<T> andThenReference(Short2ReferenceFunction<? extends T> after) {
      return k -> (T)after.get(this.get(k));
   }

   default <T> Reference2ShortFunction<T> composeReference(Reference2ShortFunction<? super T> before) {
      return k -> this.get(before.getShort(k));
   }
}
