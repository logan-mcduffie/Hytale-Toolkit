package it.unimi.dsi.fastutil.chars;

import it.unimi.dsi.fastutil.SafeMath;
import it.unimi.dsi.fastutil.booleans.Boolean2ByteFunction;
import it.unimi.dsi.fastutil.booleans.Boolean2CharFunction;
import it.unimi.dsi.fastutil.booleans.Boolean2DoubleFunction;
import it.unimi.dsi.fastutil.booleans.Boolean2FloatFunction;
import it.unimi.dsi.fastutil.booleans.Boolean2IntFunction;
import it.unimi.dsi.fastutil.booleans.Boolean2LongFunction;
import it.unimi.dsi.fastutil.booleans.Boolean2ObjectFunction;
import it.unimi.dsi.fastutil.booleans.Boolean2ReferenceFunction;
import it.unimi.dsi.fastutil.booleans.Boolean2ShortFunction;
import it.unimi.dsi.fastutil.bytes.Byte2BooleanFunction;
import it.unimi.dsi.fastutil.bytes.Byte2CharFunction;
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
import java.util.function.IntPredicate;

@FunctionalInterface
public interface Char2BooleanFunction extends it.unimi.dsi.fastutil.Function<Character, Boolean>, IntPredicate {
   @Deprecated
   @Override
   default boolean test(int operand) {
      return this.get(SafeMath.safeIntToChar(operand));
   }

   default boolean put(char key, boolean value) {
      throw new UnsupportedOperationException();
   }

   boolean get(char var1);

   default boolean getOrDefault(char key, boolean defaultValue) {
      boolean v;
      return (v = this.get(key)) == this.defaultReturnValue() && !this.containsKey(key) ? defaultValue : v;
   }

   default boolean remove(char key) {
      throw new UnsupportedOperationException();
   }

   @Deprecated
   default Boolean put(Character key, Boolean value) {
      char k = key;
      boolean containsKey = this.containsKey(k);
      boolean v = this.put(k, value.booleanValue());
      return containsKey ? v : null;
   }

   @Deprecated
   default Boolean get(Object key) {
      if (key == null) {
         return null;
      } else {
         char k = (Character)key;
         boolean v;
         return (v = this.get(k)) == this.defaultReturnValue() && !this.containsKey(k) ? null : v;
      }
   }

   @Deprecated
   default Boolean getOrDefault(Object key, Boolean defaultValue) {
      if (key == null) {
         return defaultValue;
      } else {
         char k = (Character)key;
         boolean v = this.get(k);
         return v == this.defaultReturnValue() && !this.containsKey(k) ? defaultValue : v;
      }
   }

   @Deprecated
   default Boolean remove(Object key) {
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

   default void defaultReturnValue(boolean rv) {
      throw new UnsupportedOperationException();
   }

   default boolean defaultReturnValue() {
      return false;
   }

   @Deprecated
   @Override
   default <T> Function<T, Boolean> compose(Function<? super T, ? extends Character> before) {
      return it.unimi.dsi.fastutil.Function.super.compose(before);
   }

   @Deprecated
   @Override
   default <T> Function<Character, T> andThen(Function<? super Boolean, ? extends T> after) {
      return it.unimi.dsi.fastutil.Function.super.andThen(after);
   }

   default Char2ByteFunction andThenByte(Boolean2ByteFunction after) {
      return k -> after.get(this.get(k));
   }

   default Byte2BooleanFunction composeByte(Byte2CharFunction before) {
      return k -> this.get(before.get(k));
   }

   default Char2ShortFunction andThenShort(Boolean2ShortFunction after) {
      return k -> after.get(this.get(k));
   }

   default Short2BooleanFunction composeShort(Short2CharFunction before) {
      return k -> this.get(before.get(k));
   }

   default Char2IntFunction andThenInt(Boolean2IntFunction after) {
      return k -> after.get(this.get(k));
   }

   default Int2BooleanFunction composeInt(Int2CharFunction before) {
      return k -> this.get(before.get(k));
   }

   default Char2LongFunction andThenLong(Boolean2LongFunction after) {
      return k -> after.get(this.get(k));
   }

   default Long2BooleanFunction composeLong(Long2CharFunction before) {
      return k -> this.get(before.get(k));
   }

   default Char2CharFunction andThenChar(Boolean2CharFunction after) {
      return k -> after.get(this.get(k));
   }

   default Char2BooleanFunction composeChar(Char2CharFunction before) {
      return k -> this.get(before.get(k));
   }

   default Char2FloatFunction andThenFloat(Boolean2FloatFunction after) {
      return k -> after.get(this.get(k));
   }

   default Float2BooleanFunction composeFloat(Float2CharFunction before) {
      return k -> this.get(before.get(k));
   }

   default Char2DoubleFunction andThenDouble(Boolean2DoubleFunction after) {
      return k -> after.get(this.get(k));
   }

   default Double2BooleanFunction composeDouble(Double2CharFunction before) {
      return k -> this.get(before.get(k));
   }

   default <T> Char2ObjectFunction<T> andThenObject(Boolean2ObjectFunction<? extends T> after) {
      return k -> (T)after.get(this.get(k));
   }

   default <T> Object2BooleanFunction<T> composeObject(Object2CharFunction<? super T> before) {
      return k -> this.get(before.getChar(k));
   }

   default <T> Char2ReferenceFunction<T> andThenReference(Boolean2ReferenceFunction<? extends T> after) {
      return k -> (T)after.get(this.get(k));
   }

   default <T> Reference2BooleanFunction<T> composeReference(Reference2CharFunction<? super T> before) {
      return k -> this.get(before.getChar(k));
   }
}
