package it.unimi.dsi.fastutil.booleans;

import it.unimi.dsi.fastutil.bytes.Byte2BooleanFunction;
import it.unimi.dsi.fastutil.chars.Char2BooleanFunction;
import it.unimi.dsi.fastutil.doubles.Double2BooleanFunction;
import it.unimi.dsi.fastutil.floats.Float2BooleanFunction;
import it.unimi.dsi.fastutil.ints.Int2BooleanFunction;
import it.unimi.dsi.fastutil.longs.Long2BooleanFunction;
import it.unimi.dsi.fastutil.objects.Object2BooleanFunction;
import it.unimi.dsi.fastutil.objects.Reference2BooleanFunction;
import it.unimi.dsi.fastutil.shorts.Short2BooleanFunction;
import java.util.function.Function;

@FunctionalInterface
public interface Boolean2BooleanFunction extends it.unimi.dsi.fastutil.Function<Boolean, Boolean> {
   default boolean put(boolean key, boolean value) {
      throw new UnsupportedOperationException();
   }

   boolean get(boolean var1);

   default boolean getOrDefault(boolean key, boolean defaultValue) {
      boolean v;
      return (v = this.get(key)) == this.defaultReturnValue() && !this.containsKey(key) ? defaultValue : v;
   }

   default boolean remove(boolean key) {
      throw new UnsupportedOperationException();
   }

   @Deprecated
   default Boolean put(Boolean key, Boolean value) {
      boolean k = key;
      boolean containsKey = this.containsKey(k);
      boolean v = this.put(k, value.booleanValue());
      return containsKey ? v : null;
   }

   @Deprecated
   default Boolean get(Object key) {
      if (key == null) {
         return null;
      } else {
         boolean k = (Boolean)key;
         boolean v;
         return (v = this.get(k)) == this.defaultReturnValue() && !this.containsKey(k) ? null : v;
      }
   }

   @Deprecated
   default Boolean getOrDefault(Object key, Boolean defaultValue) {
      if (key == null) {
         return defaultValue;
      } else {
         boolean k = (Boolean)key;
         boolean v = this.get(k);
         return v == this.defaultReturnValue() && !this.containsKey(k) ? defaultValue : v;
      }
   }

   @Deprecated
   default Boolean remove(Object key) {
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

   default void defaultReturnValue(boolean rv) {
      throw new UnsupportedOperationException();
   }

   default boolean defaultReturnValue() {
      return false;
   }

   static Boolean2BooleanFunction identity() {
      return k -> k;
   }

   @Deprecated
   @Override
   default <T> Function<T, Boolean> compose(Function<? super T, ? extends Boolean> before) {
      return it.unimi.dsi.fastutil.Function.super.compose(before);
   }

   @Deprecated
   @Override
   default <T> Function<Boolean, T> andThen(Function<? super Boolean, ? extends T> after) {
      return it.unimi.dsi.fastutil.Function.super.andThen(after);
   }

   default Boolean2ByteFunction andThenByte(Boolean2ByteFunction after) {
      return k -> after.get(this.get(k));
   }

   default Byte2BooleanFunction composeByte(Byte2BooleanFunction before) {
      return k -> this.get(before.get(k));
   }

   default Boolean2ShortFunction andThenShort(Boolean2ShortFunction after) {
      return k -> after.get(this.get(k));
   }

   default Short2BooleanFunction composeShort(Short2BooleanFunction before) {
      return k -> this.get(before.get(k));
   }

   default Boolean2IntFunction andThenInt(Boolean2IntFunction after) {
      return k -> after.get(this.get(k));
   }

   default Int2BooleanFunction composeInt(Int2BooleanFunction before) {
      return k -> this.get(before.get(k));
   }

   default Boolean2LongFunction andThenLong(Boolean2LongFunction after) {
      return k -> after.get(this.get(k));
   }

   default Long2BooleanFunction composeLong(Long2BooleanFunction before) {
      return k -> this.get(before.get(k));
   }

   default Boolean2CharFunction andThenChar(Boolean2CharFunction after) {
      return k -> after.get(this.get(k));
   }

   default Char2BooleanFunction composeChar(Char2BooleanFunction before) {
      return k -> this.get(before.get(k));
   }

   default Boolean2FloatFunction andThenFloat(Boolean2FloatFunction after) {
      return k -> after.get(this.get(k));
   }

   default Float2BooleanFunction composeFloat(Float2BooleanFunction before) {
      return k -> this.get(before.get(k));
   }

   default Boolean2DoubleFunction andThenDouble(Boolean2DoubleFunction after) {
      return k -> after.get(this.get(k));
   }

   default Double2BooleanFunction composeDouble(Double2BooleanFunction before) {
      return k -> this.get(before.get(k));
   }

   default <T> Boolean2ObjectFunction<T> andThenObject(Boolean2ObjectFunction<? extends T> after) {
      return k -> (T)after.get(this.get(k));
   }

   default <T> Object2BooleanFunction<T> composeObject(Object2BooleanFunction<? super T> before) {
      return k -> this.get(before.getBoolean(k));
   }

   default <T> Boolean2ReferenceFunction<T> andThenReference(Boolean2ReferenceFunction<? extends T> after) {
      return k -> (T)after.get(this.get(k));
   }

   default <T> Reference2BooleanFunction<T> composeReference(Reference2BooleanFunction<? super T> before) {
      return k -> this.get(before.getBoolean(k));
   }
}
