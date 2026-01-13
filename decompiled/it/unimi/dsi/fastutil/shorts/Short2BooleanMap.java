package it.unimi.dsi.fastutil.shorts;

import it.unimi.dsi.fastutil.booleans.BooleanCollection;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.IntFunction;
import java.util.function.IntPredicate;

public interface Short2BooleanMap extends Short2BooleanFunction, Map<Short, Boolean> {
   @Override
   int size();

   @Override
   default void clear() {
      throw new UnsupportedOperationException();
   }

   @Override
   void defaultReturnValue(boolean var1);

   @Override
   boolean defaultReturnValue();

   ObjectSet<Short2BooleanMap.Entry> short2BooleanEntrySet();

   @Deprecated
   default ObjectSet<java.util.Map.Entry<Short, Boolean>> entrySet() {
      return this.short2BooleanEntrySet();
   }

   @Deprecated
   @Override
   default Boolean put(Short key, Boolean value) {
      return Short2BooleanFunction.super.put(key, value);
   }

   @Deprecated
   @Override
   default Boolean get(Object key) {
      return Short2BooleanFunction.super.get(key);
   }

   @Deprecated
   @Override
   default Boolean remove(Object key) {
      return Short2BooleanFunction.super.remove(key);
   }

   ShortSet keySet();

   BooleanCollection values();

   @Override
   boolean containsKey(short var1);

   @Deprecated
   @Override
   default boolean containsKey(Object key) {
      return Short2BooleanFunction.super.containsKey(key);
   }

   boolean containsValue(boolean var1);

   @Deprecated
   @Override
   default boolean containsValue(Object value) {
      return value == null ? false : this.containsValue(((Boolean)value).booleanValue());
   }

   @Override
   default void forEach(BiConsumer<? super Short, ? super Boolean> consumer) {
      ObjectSet<Short2BooleanMap.Entry> entrySet = this.short2BooleanEntrySet();
      Consumer<Short2BooleanMap.Entry> wrappingConsumer = entry -> consumer.accept(entry.getShortKey(), entry.getBooleanValue());
      if (entrySet instanceof Short2BooleanMap.FastEntrySet) {
         ((Short2BooleanMap.FastEntrySet)entrySet).fastForEach(wrappingConsumer);
      } else {
         entrySet.forEach(wrappingConsumer);
      }
   }

   @Override
   default boolean getOrDefault(short key, boolean defaultValue) {
      boolean v;
      return (v = this.get(key)) == this.defaultReturnValue() && !this.containsKey(key) ? defaultValue : v;
   }

   @Deprecated
   @Override
   default Boolean getOrDefault(Object key, Boolean defaultValue) {
      return Map.super.getOrDefault(key, defaultValue);
   }

   default boolean putIfAbsent(short key, boolean value) {
      boolean v = this.get(key);
      boolean drv = this.defaultReturnValue();
      if (v == drv && !this.containsKey(key)) {
         this.put(key, value);
         return drv;
      } else {
         return v;
      }
   }

   default boolean remove(short key, boolean value) {
      boolean curValue = this.get(key);
      if (curValue == value && (curValue != this.defaultReturnValue() || this.containsKey(key))) {
         this.remove(key);
         return true;
      } else {
         return false;
      }
   }

   default boolean replace(short key, boolean oldValue, boolean newValue) {
      boolean curValue = this.get(key);
      if (curValue == oldValue && (curValue != this.defaultReturnValue() || this.containsKey(key))) {
         this.put(key, newValue);
         return true;
      } else {
         return false;
      }
   }

   default boolean replace(short key, boolean value) {
      return this.containsKey(key) ? this.put(key, value) : this.defaultReturnValue();
   }

   default boolean computeIfAbsent(short key, IntPredicate mappingFunction) {
      Objects.requireNonNull(mappingFunction);
      boolean v = this.get(key);
      if (v == this.defaultReturnValue() && !this.containsKey(key)) {
         boolean newValue = mappingFunction.test(key);
         this.put(key, newValue);
         return newValue;
      } else {
         return v;
      }
   }

   default boolean computeIfAbsentNullable(short key, IntFunction<? extends Boolean> mappingFunction) {
      Objects.requireNonNull(mappingFunction);
      boolean v = this.get(key);
      boolean drv = this.defaultReturnValue();
      if (v == drv && !this.containsKey(key)) {
         Boolean mappedValue = mappingFunction.apply(key);
         if (mappedValue == null) {
            return drv;
         } else {
            boolean newValue = mappedValue;
            this.put(key, newValue);
            return newValue;
         }
      } else {
         return v;
      }
   }

   default boolean computeIfAbsent(short key, Short2BooleanFunction mappingFunction) {
      Objects.requireNonNull(mappingFunction);
      boolean v = this.get(key);
      boolean drv = this.defaultReturnValue();
      if (v != drv || this.containsKey(key)) {
         return v;
      } else if (!mappingFunction.containsKey(key)) {
         return drv;
      } else {
         boolean newValue = mappingFunction.get(key);
         this.put(key, newValue);
         return newValue;
      }
   }

   @Deprecated
   default boolean computeIfAbsentPartial(short key, Short2BooleanFunction mappingFunction) {
      return this.computeIfAbsent(key, mappingFunction);
   }

   default boolean computeIfPresent(short key, BiFunction<? super Short, ? super Boolean, ? extends Boolean> remappingFunction) {
      Objects.requireNonNull(remappingFunction);
      boolean oldValue = this.get(key);
      boolean drv = this.defaultReturnValue();
      if (oldValue == drv && !this.containsKey(key)) {
         return drv;
      } else {
         Boolean newValue = remappingFunction.apply(key, oldValue);
         if (newValue == null) {
            this.remove(key);
            return drv;
         } else {
            boolean newVal = newValue;
            this.put(key, newVal);
            return newVal;
         }
      }
   }

   default boolean compute(short key, BiFunction<? super Short, ? super Boolean, ? extends Boolean> remappingFunction) {
      Objects.requireNonNull(remappingFunction);
      boolean oldValue = this.get(key);
      boolean drv = this.defaultReturnValue();
      boolean contained = oldValue != drv || this.containsKey(key);
      Boolean newValue = remappingFunction.apply(key, contained ? oldValue : null);
      if (newValue == null) {
         if (contained) {
            this.remove(key);
         }

         return drv;
      } else {
         boolean newVal = newValue;
         this.put(key, newVal);
         return newVal;
      }
   }

   default boolean merge(short key, boolean value, BiFunction<? super Boolean, ? super Boolean, ? extends Boolean> remappingFunction) {
      Objects.requireNonNull(remappingFunction);
      boolean oldValue = this.get(key);
      boolean drv = this.defaultReturnValue();
      boolean newValue;
      if (oldValue == drv && !this.containsKey(key)) {
         newValue = value;
      } else {
         Boolean mergedValue = remappingFunction.apply(oldValue, value);
         if (mergedValue == null) {
            this.remove(key);
            return drv;
         }

         newValue = mergedValue;
      }

      this.put(key, newValue);
      return newValue;
   }

   public interface Entry extends java.util.Map.Entry<Short, Boolean> {
      short getShortKey();

      @Deprecated
      default Short getKey() {
         return this.getShortKey();
      }

      boolean getBooleanValue();

      boolean setValue(boolean var1);

      @Deprecated
      default Boolean getValue() {
         return this.getBooleanValue();
      }

      @Deprecated
      default Boolean setValue(Boolean value) {
         return this.setValue(value.booleanValue());
      }
   }

   public interface FastEntrySet extends ObjectSet<Short2BooleanMap.Entry> {
      ObjectIterator<Short2BooleanMap.Entry> fastIterator();

      default void fastForEach(Consumer<? super Short2BooleanMap.Entry> consumer) {
         this.forEach(consumer);
      }
   }
}
