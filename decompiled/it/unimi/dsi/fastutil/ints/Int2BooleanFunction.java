package it.unimi.dsi.fastutil.ints;

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
import it.unimi.dsi.fastutil.bytes.Byte2IntFunction;
import it.unimi.dsi.fastutil.chars.Char2BooleanFunction;
import it.unimi.dsi.fastutil.chars.Char2IntFunction;
import it.unimi.dsi.fastutil.doubles.Double2BooleanFunction;
import it.unimi.dsi.fastutil.doubles.Double2IntFunction;
import it.unimi.dsi.fastutil.floats.Float2BooleanFunction;
import it.unimi.dsi.fastutil.floats.Float2IntFunction;
import it.unimi.dsi.fastutil.longs.Long2BooleanFunction;
import it.unimi.dsi.fastutil.longs.Long2IntFunction;
import it.unimi.dsi.fastutil.objects.Object2BooleanFunction;
import it.unimi.dsi.fastutil.objects.Object2IntFunction;
import it.unimi.dsi.fastutil.objects.Reference2BooleanFunction;
import it.unimi.dsi.fastutil.objects.Reference2IntFunction;
import it.unimi.dsi.fastutil.shorts.Short2BooleanFunction;
import it.unimi.dsi.fastutil.shorts.Short2IntFunction;
import java.util.function.Function;

@FunctionalInterface
public interface Int2BooleanFunction extends it.unimi.dsi.fastutil.Function<Integer, Boolean>, java.util.function.IntPredicate {
   @Override
   default boolean test(int operand) {
      return this.get(operand);
   }

   default boolean put(int key, boolean value) {
      throw new UnsupportedOperationException();
   }

   boolean get(int var1);

   default boolean getOrDefault(int key, boolean defaultValue) {
      boolean v;
      return (v = this.get(key)) == this.defaultReturnValue() && !this.containsKey(key) ? defaultValue : v;
   }

   default boolean remove(int key) {
      throw new UnsupportedOperationException();
   }

   @Deprecated
   default Boolean put(Integer key, Boolean value) {
      int k = key;
      boolean containsKey = this.containsKey(k);
      boolean v = this.put(k, value.booleanValue());
      return containsKey ? v : null;
   }

   @Deprecated
   default Boolean get(Object key) {
      if (key == null) {
         return null;
      } else {
         int k = (Integer)key;
         boolean v;
         return (v = this.get(k)) == this.defaultReturnValue() && !this.containsKey(k) ? null : v;
      }
   }

   @Deprecated
   default Boolean getOrDefault(Object key, Boolean defaultValue) {
      if (key == null) {
         return defaultValue;
      } else {
         int k = (Integer)key;
         boolean v = this.get(k);
         return v == this.defaultReturnValue() && !this.containsKey(k) ? defaultValue : v;
      }
   }

   @Deprecated
   default Boolean remove(Object key) {
      if (key == null) {
         return null;
      } else {
         int k = (Integer)key;
         return this.containsKey(k) ? this.remove(k) : null;
      }
   }

   default boolean containsKey(int key) {
      return true;
   }

   @Deprecated
   @Override
   default boolean containsKey(Object key) {
      return key == null ? false : this.containsKey(((Integer)key).intValue());
   }

   default void defaultReturnValue(boolean rv) {
      throw new UnsupportedOperationException();
   }

   default boolean defaultReturnValue() {
      return false;
   }

   @Deprecated
   @Override
   default <T> Function<T, Boolean> compose(Function<? super T, ? extends Integer> before) {
      return it.unimi.dsi.fastutil.Function.super.compose(before);
   }

   @Deprecated
   @Override
   default <T> Function<Integer, T> andThen(Function<? super Boolean, ? extends T> after) {
      return it.unimi.dsi.fastutil.Function.super.andThen(after);
   }

   default Int2ByteFunction andThenByte(Boolean2ByteFunction after) {
      return k -> after.get(this.get(k));
   }

   default Byte2BooleanFunction composeByte(Byte2IntFunction before) {
      return k -> this.get(before.get(k));
   }

   default Int2ShortFunction andThenShort(Boolean2ShortFunction after) {
      return k -> after.get(this.get(k));
   }

   default Short2BooleanFunction composeShort(Short2IntFunction before) {
      return k -> this.get(before.get(k));
   }

   default Int2IntFunction andThenInt(Boolean2IntFunction after) {
      return k -> after.get(this.get(k));
   }

   default Int2BooleanFunction composeInt(Int2IntFunction before) {
      return k -> this.get(before.get(k));
   }

   default Int2LongFunction andThenLong(Boolean2LongFunction after) {
      return k -> after.get(this.get(k));
   }

   default Long2BooleanFunction composeLong(Long2IntFunction before) {
      return k -> this.get(before.get(k));
   }

   default Int2CharFunction andThenChar(Boolean2CharFunction after) {
      return k -> after.get(this.get(k));
   }

   default Char2BooleanFunction composeChar(Char2IntFunction before) {
      return k -> this.get(before.get(k));
   }

   default Int2FloatFunction andThenFloat(Boolean2FloatFunction after) {
      return k -> after.get(this.get(k));
   }

   default Float2BooleanFunction composeFloat(Float2IntFunction before) {
      return k -> this.get(before.get(k));
   }

   default Int2DoubleFunction andThenDouble(Boolean2DoubleFunction after) {
      return k -> after.get(this.get(k));
   }

   default Double2BooleanFunction composeDouble(Double2IntFunction before) {
      return k -> this.get(before.get(k));
   }

   default <T> Int2ObjectFunction<T> andThenObject(Boolean2ObjectFunction<? extends T> after) {
      return k -> (T)after.get(this.get(k));
   }

   default <T> Object2BooleanFunction<T> composeObject(Object2IntFunction<? super T> before) {
      return k -> this.get(before.getInt(k));
   }

   default <T> Int2ReferenceFunction<T> andThenReference(Boolean2ReferenceFunction<? extends T> after) {
      return k -> (T)after.get(this.get(k));
   }

   default <T> Reference2BooleanFunction<T> composeReference(Reference2IntFunction<? super T> before) {
      return k -> this.get(before.getInt(k));
   }
}
