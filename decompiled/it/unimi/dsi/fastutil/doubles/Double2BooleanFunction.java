package it.unimi.dsi.fastutil.doubles;

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
import it.unimi.dsi.fastutil.bytes.Byte2DoubleFunction;
import it.unimi.dsi.fastutil.chars.Char2BooleanFunction;
import it.unimi.dsi.fastutil.chars.Char2DoubleFunction;
import it.unimi.dsi.fastutil.floats.Float2BooleanFunction;
import it.unimi.dsi.fastutil.floats.Float2DoubleFunction;
import it.unimi.dsi.fastutil.ints.Int2BooleanFunction;
import it.unimi.dsi.fastutil.ints.Int2DoubleFunction;
import it.unimi.dsi.fastutil.longs.Long2BooleanFunction;
import it.unimi.dsi.fastutil.longs.Long2DoubleFunction;
import it.unimi.dsi.fastutil.objects.Object2BooleanFunction;
import it.unimi.dsi.fastutil.objects.Object2DoubleFunction;
import it.unimi.dsi.fastutil.objects.Reference2BooleanFunction;
import it.unimi.dsi.fastutil.objects.Reference2DoubleFunction;
import it.unimi.dsi.fastutil.shorts.Short2BooleanFunction;
import it.unimi.dsi.fastutil.shorts.Short2DoubleFunction;
import java.util.function.Function;

@FunctionalInterface
public interface Double2BooleanFunction extends it.unimi.dsi.fastutil.Function<Double, Boolean>, java.util.function.DoublePredicate {
   @Override
   default boolean test(double operand) {
      return this.get(operand);
   }

   default boolean put(double key, boolean value) {
      throw new UnsupportedOperationException();
   }

   boolean get(double var1);

   default boolean getOrDefault(double key, boolean defaultValue) {
      boolean v;
      return (v = this.get(key)) == this.defaultReturnValue() && !this.containsKey(key) ? defaultValue : v;
   }

   default boolean remove(double key) {
      throw new UnsupportedOperationException();
   }

   @Deprecated
   default Boolean put(Double key, Boolean value) {
      double k = key;
      boolean containsKey = this.containsKey(k);
      boolean v = this.put(k, value.booleanValue());
      return containsKey ? v : null;
   }

   @Deprecated
   default Boolean get(Object key) {
      if (key == null) {
         return null;
      } else {
         double k = (Double)key;
         boolean v;
         return (v = this.get(k)) == this.defaultReturnValue() && !this.containsKey(k) ? null : v;
      }
   }

   @Deprecated
   default Boolean getOrDefault(Object key, Boolean defaultValue) {
      if (key == null) {
         return defaultValue;
      } else {
         double k = (Double)key;
         boolean v = this.get(k);
         return v == this.defaultReturnValue() && !this.containsKey(k) ? defaultValue : v;
      }
   }

   @Deprecated
   default Boolean remove(Object key) {
      if (key == null) {
         return null;
      } else {
         double k = (Double)key;
         return this.containsKey(k) ? this.remove(k) : null;
      }
   }

   default boolean containsKey(double key) {
      return true;
   }

   @Deprecated
   @Override
   default boolean containsKey(Object key) {
      return key == null ? false : this.containsKey(((Double)key).doubleValue());
   }

   default void defaultReturnValue(boolean rv) {
      throw new UnsupportedOperationException();
   }

   default boolean defaultReturnValue() {
      return false;
   }

   @Deprecated
   @Override
   default <T> Function<T, Boolean> compose(Function<? super T, ? extends Double> before) {
      return it.unimi.dsi.fastutil.Function.super.compose(before);
   }

   @Deprecated
   @Override
   default <T> Function<Double, T> andThen(Function<? super Boolean, ? extends T> after) {
      return it.unimi.dsi.fastutil.Function.super.andThen(after);
   }

   default Double2ByteFunction andThenByte(Boolean2ByteFunction after) {
      return k -> after.get(this.get(k));
   }

   default Byte2BooleanFunction composeByte(Byte2DoubleFunction before) {
      return k -> this.get(before.get(k));
   }

   default Double2ShortFunction andThenShort(Boolean2ShortFunction after) {
      return k -> after.get(this.get(k));
   }

   default Short2BooleanFunction composeShort(Short2DoubleFunction before) {
      return k -> this.get(before.get(k));
   }

   default Double2IntFunction andThenInt(Boolean2IntFunction after) {
      return k -> after.get(this.get(k));
   }

   default Int2BooleanFunction composeInt(Int2DoubleFunction before) {
      return k -> this.get(before.get(k));
   }

   default Double2LongFunction andThenLong(Boolean2LongFunction after) {
      return k -> after.get(this.get(k));
   }

   default Long2BooleanFunction composeLong(Long2DoubleFunction before) {
      return k -> this.get(before.get(k));
   }

   default Double2CharFunction andThenChar(Boolean2CharFunction after) {
      return k -> after.get(this.get(k));
   }

   default Char2BooleanFunction composeChar(Char2DoubleFunction before) {
      return k -> this.get(before.get(k));
   }

   default Double2FloatFunction andThenFloat(Boolean2FloatFunction after) {
      return k -> after.get(this.get(k));
   }

   default Float2BooleanFunction composeFloat(Float2DoubleFunction before) {
      return k -> this.get(before.get(k));
   }

   default Double2DoubleFunction andThenDouble(Boolean2DoubleFunction after) {
      return k -> after.get(this.get(k));
   }

   default Double2BooleanFunction composeDouble(Double2DoubleFunction before) {
      return k -> this.get(before.get(k));
   }

   default <T> Double2ObjectFunction<T> andThenObject(Boolean2ObjectFunction<? extends T> after) {
      return k -> (T)after.get(this.get(k));
   }

   default <T> Object2BooleanFunction<T> composeObject(Object2DoubleFunction<? super T> before) {
      return k -> this.get(before.getDouble(k));
   }

   default <T> Double2ReferenceFunction<T> andThenReference(Boolean2ReferenceFunction<? extends T> after) {
      return k -> (T)after.get(this.get(k));
   }

   default <T> Reference2BooleanFunction<T> composeReference(Reference2DoubleFunction<? super T> before) {
      return k -> this.get(before.getDouble(k));
   }
}
