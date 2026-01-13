package it.unimi.dsi.fastutil.longs;

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
import it.unimi.dsi.fastutil.bytes.Byte2LongFunction;
import it.unimi.dsi.fastutil.chars.Char2BooleanFunction;
import it.unimi.dsi.fastutil.chars.Char2LongFunction;
import it.unimi.dsi.fastutil.doubles.Double2BooleanFunction;
import it.unimi.dsi.fastutil.doubles.Double2LongFunction;
import it.unimi.dsi.fastutil.floats.Float2BooleanFunction;
import it.unimi.dsi.fastutil.floats.Float2LongFunction;
import it.unimi.dsi.fastutil.ints.Int2BooleanFunction;
import it.unimi.dsi.fastutil.ints.Int2LongFunction;
import it.unimi.dsi.fastutil.objects.Object2BooleanFunction;
import it.unimi.dsi.fastutil.objects.Object2LongFunction;
import it.unimi.dsi.fastutil.objects.Reference2BooleanFunction;
import it.unimi.dsi.fastutil.objects.Reference2LongFunction;
import it.unimi.dsi.fastutil.shorts.Short2BooleanFunction;
import it.unimi.dsi.fastutil.shorts.Short2LongFunction;
import java.util.function.Function;

@FunctionalInterface
public interface Long2BooleanFunction extends it.unimi.dsi.fastutil.Function<Long, Boolean>, java.util.function.LongPredicate {
   @Override
   default boolean test(long operand) {
      return this.get(operand);
   }

   default boolean put(long key, boolean value) {
      throw new UnsupportedOperationException();
   }

   boolean get(long var1);

   default boolean getOrDefault(long key, boolean defaultValue) {
      boolean v;
      return (v = this.get(key)) == this.defaultReturnValue() && !this.containsKey(key) ? defaultValue : v;
   }

   default boolean remove(long key) {
      throw new UnsupportedOperationException();
   }

   @Deprecated
   default Boolean put(Long key, Boolean value) {
      long k = key;
      boolean containsKey = this.containsKey(k);
      boolean v = this.put(k, value.booleanValue());
      return containsKey ? v : null;
   }

   @Deprecated
   default Boolean get(Object key) {
      if (key == null) {
         return null;
      } else {
         long k = (Long)key;
         boolean v;
         return (v = this.get(k)) == this.defaultReturnValue() && !this.containsKey(k) ? null : v;
      }
   }

   @Deprecated
   default Boolean getOrDefault(Object key, Boolean defaultValue) {
      if (key == null) {
         return defaultValue;
      } else {
         long k = (Long)key;
         boolean v = this.get(k);
         return v == this.defaultReturnValue() && !this.containsKey(k) ? defaultValue : v;
      }
   }

   @Deprecated
   default Boolean remove(Object key) {
      if (key == null) {
         return null;
      } else {
         long k = (Long)key;
         return this.containsKey(k) ? this.remove(k) : null;
      }
   }

   default boolean containsKey(long key) {
      return true;
   }

   @Deprecated
   @Override
   default boolean containsKey(Object key) {
      return key == null ? false : this.containsKey(((Long)key).longValue());
   }

   default void defaultReturnValue(boolean rv) {
      throw new UnsupportedOperationException();
   }

   default boolean defaultReturnValue() {
      return false;
   }

   @Deprecated
   @Override
   default <T> Function<T, Boolean> compose(Function<? super T, ? extends Long> before) {
      return it.unimi.dsi.fastutil.Function.super.compose(before);
   }

   @Deprecated
   @Override
   default <T> Function<Long, T> andThen(Function<? super Boolean, ? extends T> after) {
      return it.unimi.dsi.fastutil.Function.super.andThen(after);
   }

   default Long2ByteFunction andThenByte(Boolean2ByteFunction after) {
      return k -> after.get(this.get(k));
   }

   default Byte2BooleanFunction composeByte(Byte2LongFunction before) {
      return k -> this.get(before.get(k));
   }

   default Long2ShortFunction andThenShort(Boolean2ShortFunction after) {
      return k -> after.get(this.get(k));
   }

   default Short2BooleanFunction composeShort(Short2LongFunction before) {
      return k -> this.get(before.get(k));
   }

   default Long2IntFunction andThenInt(Boolean2IntFunction after) {
      return k -> after.get(this.get(k));
   }

   default Int2BooleanFunction composeInt(Int2LongFunction before) {
      return k -> this.get(before.get(k));
   }

   default Long2LongFunction andThenLong(Boolean2LongFunction after) {
      return k -> after.get(this.get(k));
   }

   default Long2BooleanFunction composeLong(Long2LongFunction before) {
      return k -> this.get(before.get(k));
   }

   default Long2CharFunction andThenChar(Boolean2CharFunction after) {
      return k -> after.get(this.get(k));
   }

   default Char2BooleanFunction composeChar(Char2LongFunction before) {
      return k -> this.get(before.get(k));
   }

   default Long2FloatFunction andThenFloat(Boolean2FloatFunction after) {
      return k -> after.get(this.get(k));
   }

   default Float2BooleanFunction composeFloat(Float2LongFunction before) {
      return k -> this.get(before.get(k));
   }

   default Long2DoubleFunction andThenDouble(Boolean2DoubleFunction after) {
      return k -> after.get(this.get(k));
   }

   default Double2BooleanFunction composeDouble(Double2LongFunction before) {
      return k -> this.get(before.get(k));
   }

   default <T> Long2ObjectFunction<T> andThenObject(Boolean2ObjectFunction<? extends T> after) {
      return k -> (T)after.get(this.get(k));
   }

   default <T> Object2BooleanFunction<T> composeObject(Object2LongFunction<? super T> before) {
      return k -> this.get(before.getLong(k));
   }

   default <T> Long2ReferenceFunction<T> andThenReference(Boolean2ReferenceFunction<? extends T> after) {
      return k -> (T)after.get(this.get(k));
   }

   default <T> Reference2BooleanFunction<T> composeReference(Reference2LongFunction<? super T> before) {
      return k -> this.get(before.getLong(k));
   }
}
