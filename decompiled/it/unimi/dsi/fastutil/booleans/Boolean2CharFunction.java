package it.unimi.dsi.fastutil.booleans;

import it.unimi.dsi.fastutil.bytes.Byte2BooleanFunction;
import it.unimi.dsi.fastutil.bytes.Byte2CharFunction;
import it.unimi.dsi.fastutil.chars.Char2BooleanFunction;
import it.unimi.dsi.fastutil.chars.Char2ByteFunction;
import it.unimi.dsi.fastutil.chars.Char2CharFunction;
import it.unimi.dsi.fastutil.chars.Char2DoubleFunction;
import it.unimi.dsi.fastutil.chars.Char2FloatFunction;
import it.unimi.dsi.fastutil.chars.Char2IntFunction;
import it.unimi.dsi.fastutil.chars.Char2LongFunction;
import it.unimi.dsi.fastutil.chars.Char2ObjectFunction;
import it.unimi.dsi.fastutil.chars.Char2ReferenceFunction;
import it.unimi.dsi.fastutil.chars.Char2ShortFunction;
import it.unimi.dsi.fastutil.doubles.Double2BooleanFunction;
import it.unimi.dsi.fastutil.doubles.Double2CharFunction;
import it.unimi.dsi.fastutil.floats.Float2BooleanFunction;
import it.unimi.dsi.fastutil.floats.Float2CharFunction;
import it.unimi.dsi.fastutil.ints.Int2BooleanFunction;
import it.unimi.dsi.fastutil.ints.Int2CharFunction;
import it.unimi.dsi.fastutil.longs.Long2BooleanFunction;
import it.unimi.dsi.fastutil.longs.Long2CharFunction;
import it.unimi.dsi.fastutil.objects.Object2BooleanFunction;
import it.unimi.dsi.fastutil.objects.Object2CharFunction;
import it.unimi.dsi.fastutil.objects.Reference2BooleanFunction;
import it.unimi.dsi.fastutil.objects.Reference2CharFunction;
import it.unimi.dsi.fastutil.shorts.Short2BooleanFunction;
import it.unimi.dsi.fastutil.shorts.Short2CharFunction;
import java.util.function.Function;

@FunctionalInterface
public interface Boolean2CharFunction extends it.unimi.dsi.fastutil.Function<Boolean, Character> {
   default char put(boolean key, char value) {
      throw new UnsupportedOperationException();
   }

   char get(boolean var1);

   default char getOrDefault(boolean key, char defaultValue) {
      char v;
      return (v = this.get(key)) == this.defaultReturnValue() && !this.containsKey(key) ? defaultValue : v;
   }

   default char remove(boolean key) {
      throw new UnsupportedOperationException();
   }

   @Deprecated
   default Character put(Boolean key, Character value) {
      boolean k = key;
      boolean containsKey = this.containsKey(k);
      char v = this.put(k, value.charValue());
      return containsKey ? v : null;
   }

   @Deprecated
   default Character get(Object key) {
      if (key == null) {
         return null;
      } else {
         boolean k = (Boolean)key;
         char v;
         return (v = this.get(k)) == this.defaultReturnValue() && !this.containsKey(k) ? null : v;
      }
   }

   @Deprecated
   default Character getOrDefault(Object key, Character defaultValue) {
      if (key == null) {
         return defaultValue;
      } else {
         boolean k = (Boolean)key;
         char v = this.get(k);
         return v == this.defaultReturnValue() && !this.containsKey(k) ? defaultValue : v;
      }
   }

   @Deprecated
   default Character remove(Object key) {
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

   default void defaultReturnValue(char rv) {
      throw new UnsupportedOperationException();
   }

   default char defaultReturnValue() {
      return '\u0000';
   }

   @Deprecated
   @Override
   default <T> Function<T, Character> compose(Function<? super T, ? extends Boolean> before) {
      return it.unimi.dsi.fastutil.Function.super.compose(before);
   }

   @Deprecated
   @Override
   default <T> Function<Boolean, T> andThen(Function<? super Character, ? extends T> after) {
      return it.unimi.dsi.fastutil.Function.super.andThen(after);
   }

   default Boolean2ByteFunction andThenByte(Char2ByteFunction after) {
      return k -> after.get(this.get(k));
   }

   default Byte2CharFunction composeByte(Byte2BooleanFunction before) {
      return k -> this.get(before.get(k));
   }

   default Boolean2ShortFunction andThenShort(Char2ShortFunction after) {
      return k -> after.get(this.get(k));
   }

   default Short2CharFunction composeShort(Short2BooleanFunction before) {
      return k -> this.get(before.get(k));
   }

   default Boolean2IntFunction andThenInt(Char2IntFunction after) {
      return k -> after.get(this.get(k));
   }

   default Int2CharFunction composeInt(Int2BooleanFunction before) {
      return k -> this.get(before.get(k));
   }

   default Boolean2LongFunction andThenLong(Char2LongFunction after) {
      return k -> after.get(this.get(k));
   }

   default Long2CharFunction composeLong(Long2BooleanFunction before) {
      return k -> this.get(before.get(k));
   }

   default Boolean2CharFunction andThenChar(Char2CharFunction after) {
      return k -> after.get(this.get(k));
   }

   default Char2CharFunction composeChar(Char2BooleanFunction before) {
      return k -> this.get(before.get(k));
   }

   default Boolean2FloatFunction andThenFloat(Char2FloatFunction after) {
      return k -> after.get(this.get(k));
   }

   default Float2CharFunction composeFloat(Float2BooleanFunction before) {
      return k -> this.get(before.get(k));
   }

   default Boolean2DoubleFunction andThenDouble(Char2DoubleFunction after) {
      return k -> after.get(this.get(k));
   }

   default Double2CharFunction composeDouble(Double2BooleanFunction before) {
      return k -> this.get(before.get(k));
   }

   default <T> Boolean2ObjectFunction<T> andThenObject(Char2ObjectFunction<? extends T> after) {
      return k -> (T)after.get(this.get(k));
   }

   default <T> Object2CharFunction<T> composeObject(Object2BooleanFunction<? super T> before) {
      return k -> this.get(before.getBoolean(k));
   }

   default <T> Boolean2ReferenceFunction<T> andThenReference(Char2ReferenceFunction<? extends T> after) {
      return k -> (T)after.get(this.get(k));
   }

   default <T> Reference2CharFunction<T> composeReference(Reference2BooleanFunction<? super T> before) {
      return k -> this.get(before.getBoolean(k));
   }
}
