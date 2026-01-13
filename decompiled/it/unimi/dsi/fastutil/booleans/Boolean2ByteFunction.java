package it.unimi.dsi.fastutil.booleans;

import it.unimi.dsi.fastutil.bytes.Byte2BooleanFunction;
import it.unimi.dsi.fastutil.bytes.Byte2ByteFunction;
import it.unimi.dsi.fastutil.bytes.Byte2CharFunction;
import it.unimi.dsi.fastutil.bytes.Byte2DoubleFunction;
import it.unimi.dsi.fastutil.bytes.Byte2FloatFunction;
import it.unimi.dsi.fastutil.bytes.Byte2IntFunction;
import it.unimi.dsi.fastutil.bytes.Byte2LongFunction;
import it.unimi.dsi.fastutil.bytes.Byte2ObjectFunction;
import it.unimi.dsi.fastutil.bytes.Byte2ReferenceFunction;
import it.unimi.dsi.fastutil.bytes.Byte2ShortFunction;
import it.unimi.dsi.fastutil.chars.Char2BooleanFunction;
import it.unimi.dsi.fastutil.chars.Char2ByteFunction;
import it.unimi.dsi.fastutil.doubles.Double2BooleanFunction;
import it.unimi.dsi.fastutil.doubles.Double2ByteFunction;
import it.unimi.dsi.fastutil.floats.Float2BooleanFunction;
import it.unimi.dsi.fastutil.floats.Float2ByteFunction;
import it.unimi.dsi.fastutil.ints.Int2BooleanFunction;
import it.unimi.dsi.fastutil.ints.Int2ByteFunction;
import it.unimi.dsi.fastutil.longs.Long2BooleanFunction;
import it.unimi.dsi.fastutil.longs.Long2ByteFunction;
import it.unimi.dsi.fastutil.objects.Object2BooleanFunction;
import it.unimi.dsi.fastutil.objects.Object2ByteFunction;
import it.unimi.dsi.fastutil.objects.Reference2BooleanFunction;
import it.unimi.dsi.fastutil.objects.Reference2ByteFunction;
import it.unimi.dsi.fastutil.shorts.Short2BooleanFunction;
import it.unimi.dsi.fastutil.shorts.Short2ByteFunction;
import java.util.function.Function;

@FunctionalInterface
public interface Boolean2ByteFunction extends it.unimi.dsi.fastutil.Function<Boolean, Byte> {
   default byte put(boolean key, byte value) {
      throw new UnsupportedOperationException();
   }

   byte get(boolean var1);

   default byte getOrDefault(boolean key, byte defaultValue) {
      byte v;
      return (v = this.get(key)) == this.defaultReturnValue() && !this.containsKey(key) ? defaultValue : v;
   }

   default byte remove(boolean key) {
      throw new UnsupportedOperationException();
   }

   @Deprecated
   default Byte put(Boolean key, Byte value) {
      boolean k = key;
      boolean containsKey = this.containsKey(k);
      byte v = this.put(k, value.byteValue());
      return containsKey ? v : null;
   }

   @Deprecated
   default Byte get(Object key) {
      if (key == null) {
         return null;
      } else {
         boolean k = (Boolean)key;
         byte v;
         return (v = this.get(k)) == this.defaultReturnValue() && !this.containsKey(k) ? null : v;
      }
   }

   @Deprecated
   default Byte getOrDefault(Object key, Byte defaultValue) {
      if (key == null) {
         return defaultValue;
      } else {
         boolean k = (Boolean)key;
         byte v = this.get(k);
         return v == this.defaultReturnValue() && !this.containsKey(k) ? defaultValue : v;
      }
   }

   @Deprecated
   default Byte remove(Object key) {
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

   default void defaultReturnValue(byte rv) {
      throw new UnsupportedOperationException();
   }

   default byte defaultReturnValue() {
      return 0;
   }

   @Deprecated
   @Override
   default <T> Function<T, Byte> compose(Function<? super T, ? extends Boolean> before) {
      return it.unimi.dsi.fastutil.Function.super.compose(before);
   }

   @Deprecated
   @Override
   default <T> Function<Boolean, T> andThen(Function<? super Byte, ? extends T> after) {
      return it.unimi.dsi.fastutil.Function.super.andThen(after);
   }

   default Boolean2ByteFunction andThenByte(Byte2ByteFunction after) {
      return k -> after.get(this.get(k));
   }

   default Byte2ByteFunction composeByte(Byte2BooleanFunction before) {
      return k -> this.get(before.get(k));
   }

   default Boolean2ShortFunction andThenShort(Byte2ShortFunction after) {
      return k -> after.get(this.get(k));
   }

   default Short2ByteFunction composeShort(Short2BooleanFunction before) {
      return k -> this.get(before.get(k));
   }

   default Boolean2IntFunction andThenInt(Byte2IntFunction after) {
      return k -> after.get(this.get(k));
   }

   default Int2ByteFunction composeInt(Int2BooleanFunction before) {
      return k -> this.get(before.get(k));
   }

   default Boolean2LongFunction andThenLong(Byte2LongFunction after) {
      return k -> after.get(this.get(k));
   }

   default Long2ByteFunction composeLong(Long2BooleanFunction before) {
      return k -> this.get(before.get(k));
   }

   default Boolean2CharFunction andThenChar(Byte2CharFunction after) {
      return k -> after.get(this.get(k));
   }

   default Char2ByteFunction composeChar(Char2BooleanFunction before) {
      return k -> this.get(before.get(k));
   }

   default Boolean2FloatFunction andThenFloat(Byte2FloatFunction after) {
      return k -> after.get(this.get(k));
   }

   default Float2ByteFunction composeFloat(Float2BooleanFunction before) {
      return k -> this.get(before.get(k));
   }

   default Boolean2DoubleFunction andThenDouble(Byte2DoubleFunction after) {
      return k -> after.get(this.get(k));
   }

   default Double2ByteFunction composeDouble(Double2BooleanFunction before) {
      return k -> this.get(before.get(k));
   }

   default <T> Boolean2ObjectFunction<T> andThenObject(Byte2ObjectFunction<? extends T> after) {
      return k -> (T)after.get(this.get(k));
   }

   default <T> Object2ByteFunction<T> composeObject(Object2BooleanFunction<? super T> before) {
      return k -> this.get(before.getBoolean(k));
   }

   default <T> Boolean2ReferenceFunction<T> andThenReference(Byte2ReferenceFunction<? extends T> after) {
      return k -> (T)after.get(this.get(k));
   }

   default <T> Reference2ByteFunction<T> composeReference(Reference2BooleanFunction<? super T> before) {
      return k -> this.get(before.getBoolean(k));
   }
}
