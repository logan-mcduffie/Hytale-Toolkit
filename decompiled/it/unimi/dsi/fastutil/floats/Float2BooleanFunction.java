package it.unimi.dsi.fastutil.floats;

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
import it.unimi.dsi.fastutil.bytes.Byte2FloatFunction;
import it.unimi.dsi.fastutil.chars.Char2BooleanFunction;
import it.unimi.dsi.fastutil.chars.Char2FloatFunction;
import it.unimi.dsi.fastutil.doubles.Double2BooleanFunction;
import it.unimi.dsi.fastutil.doubles.Double2FloatFunction;
import it.unimi.dsi.fastutil.ints.Int2BooleanFunction;
import it.unimi.dsi.fastutil.ints.Int2FloatFunction;
import it.unimi.dsi.fastutil.longs.Long2BooleanFunction;
import it.unimi.dsi.fastutil.longs.Long2FloatFunction;
import it.unimi.dsi.fastutil.objects.Object2BooleanFunction;
import it.unimi.dsi.fastutil.objects.Object2FloatFunction;
import it.unimi.dsi.fastutil.objects.Reference2BooleanFunction;
import it.unimi.dsi.fastutil.objects.Reference2FloatFunction;
import it.unimi.dsi.fastutil.shorts.Short2BooleanFunction;
import it.unimi.dsi.fastutil.shorts.Short2FloatFunction;
import java.util.function.DoublePredicate;
import java.util.function.Function;

@FunctionalInterface
public interface Float2BooleanFunction extends it.unimi.dsi.fastutil.Function<Float, Boolean>, DoublePredicate {
   @Deprecated
   @Override
   default boolean test(double operand) {
      return this.get(SafeMath.safeDoubleToFloat(operand));
   }

   default boolean put(float key, boolean value) {
      throw new UnsupportedOperationException();
   }

   boolean get(float var1);

   default boolean getOrDefault(float key, boolean defaultValue) {
      boolean v;
      return (v = this.get(key)) == this.defaultReturnValue() && !this.containsKey(key) ? defaultValue : v;
   }

   default boolean remove(float key) {
      throw new UnsupportedOperationException();
   }

   @Deprecated
   default Boolean put(Float key, Boolean value) {
      float k = key;
      boolean containsKey = this.containsKey(k);
      boolean v = this.put(k, value.booleanValue());
      return containsKey ? v : null;
   }

   @Deprecated
   default Boolean get(Object key) {
      if (key == null) {
         return null;
      } else {
         float k = (Float)key;
         boolean v;
         return (v = this.get(k)) == this.defaultReturnValue() && !this.containsKey(k) ? null : v;
      }
   }

   @Deprecated
   default Boolean getOrDefault(Object key, Boolean defaultValue) {
      if (key == null) {
         return defaultValue;
      } else {
         float k = (Float)key;
         boolean v = this.get(k);
         return v == this.defaultReturnValue() && !this.containsKey(k) ? defaultValue : v;
      }
   }

   @Deprecated
   default Boolean remove(Object key) {
      if (key == null) {
         return null;
      } else {
         float k = (Float)key;
         return this.containsKey(k) ? this.remove(k) : null;
      }
   }

   default boolean containsKey(float key) {
      return true;
   }

   @Deprecated
   @Override
   default boolean containsKey(Object key) {
      return key == null ? false : this.containsKey(((Float)key).floatValue());
   }

   default void defaultReturnValue(boolean rv) {
      throw new UnsupportedOperationException();
   }

   default boolean defaultReturnValue() {
      return false;
   }

   @Deprecated
   @Override
   default <T> Function<T, Boolean> compose(Function<? super T, ? extends Float> before) {
      return it.unimi.dsi.fastutil.Function.super.compose(before);
   }

   @Deprecated
   @Override
   default <T> Function<Float, T> andThen(Function<? super Boolean, ? extends T> after) {
      return it.unimi.dsi.fastutil.Function.super.andThen(after);
   }

   default Float2ByteFunction andThenByte(Boolean2ByteFunction after) {
      return k -> after.get(this.get(k));
   }

   default Byte2BooleanFunction composeByte(Byte2FloatFunction before) {
      return k -> this.get(before.get(k));
   }

   default Float2ShortFunction andThenShort(Boolean2ShortFunction after) {
      return k -> after.get(this.get(k));
   }

   default Short2BooleanFunction composeShort(Short2FloatFunction before) {
      return k -> this.get(before.get(k));
   }

   default Float2IntFunction andThenInt(Boolean2IntFunction after) {
      return k -> after.get(this.get(k));
   }

   default Int2BooleanFunction composeInt(Int2FloatFunction before) {
      return k -> this.get(before.get(k));
   }

   default Float2LongFunction andThenLong(Boolean2LongFunction after) {
      return k -> after.get(this.get(k));
   }

   default Long2BooleanFunction composeLong(Long2FloatFunction before) {
      return k -> this.get(before.get(k));
   }

   default Float2CharFunction andThenChar(Boolean2CharFunction after) {
      return k -> after.get(this.get(k));
   }

   default Char2BooleanFunction composeChar(Char2FloatFunction before) {
      return k -> this.get(before.get(k));
   }

   default Float2FloatFunction andThenFloat(Boolean2FloatFunction after) {
      return k -> after.get(this.get(k));
   }

   default Float2BooleanFunction composeFloat(Float2FloatFunction before) {
      return k -> this.get(before.get(k));
   }

   default Float2DoubleFunction andThenDouble(Boolean2DoubleFunction after) {
      return k -> after.get(this.get(k));
   }

   default Double2BooleanFunction composeDouble(Double2FloatFunction before) {
      return k -> this.get(before.get(k));
   }

   default <T> Float2ObjectFunction<T> andThenObject(Boolean2ObjectFunction<? extends T> after) {
      return k -> (T)after.get(this.get(k));
   }

   default <T> Object2BooleanFunction<T> composeObject(Object2FloatFunction<? super T> before) {
      return k -> this.get(before.getFloat(k));
   }

   default <T> Float2ReferenceFunction<T> andThenReference(Boolean2ReferenceFunction<? extends T> after) {
      return k -> (T)after.get(this.get(k));
   }

   default <T> Reference2BooleanFunction<T> composeReference(Reference2FloatFunction<? super T> before) {
      return k -> this.get(before.getFloat(k));
   }
}
