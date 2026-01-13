package it.unimi.dsi.fastutil.floats;

import it.unimi.dsi.fastutil.booleans.BooleanCollection;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.DoubleFunction;
import java.util.function.DoublePredicate;

public interface Float2BooleanMap extends Float2BooleanFunction, Map<Float, Boolean> {
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

   ObjectSet<Float2BooleanMap.Entry> float2BooleanEntrySet();

   @Deprecated
   default ObjectSet<java.util.Map.Entry<Float, Boolean>> entrySet() {
      return this.float2BooleanEntrySet();
   }

   @Deprecated
   @Override
   default Boolean put(Float key, Boolean value) {
      return Float2BooleanFunction.super.put(key, value);
   }

   @Deprecated
   @Override
   default Boolean get(Object key) {
      return Float2BooleanFunction.super.get(key);
   }

   @Deprecated
   @Override
   default Boolean remove(Object key) {
      return Float2BooleanFunction.super.remove(key);
   }

   FloatSet keySet();

   BooleanCollection values();

   @Override
   boolean containsKey(float var1);

   @Deprecated
   @Override
   default boolean containsKey(Object key) {
      return Float2BooleanFunction.super.containsKey(key);
   }

   boolean containsValue(boolean var1);

   @Deprecated
   @Override
   default boolean containsValue(Object value) {
      return value == null ? false : this.containsValue(((Boolean)value).booleanValue());
   }

   @Override
   default void forEach(BiConsumer<? super Float, ? super Boolean> consumer) {
      ObjectSet<Float2BooleanMap.Entry> entrySet = this.float2BooleanEntrySet();
      Consumer<Float2BooleanMap.Entry> wrappingConsumer = entry -> consumer.accept(entry.getFloatKey(), entry.getBooleanValue());
      if (entrySet instanceof Float2BooleanMap.FastEntrySet) {
         ((Float2BooleanMap.FastEntrySet)entrySet).fastForEach(wrappingConsumer);
      } else {
         entrySet.forEach(wrappingConsumer);
      }
   }

   @Override
   default boolean getOrDefault(float key, boolean defaultValue) {
      boolean v;
      return (v = this.get(key)) == this.defaultReturnValue() && !this.containsKey(key) ? defaultValue : v;
   }

   @Deprecated
   @Override
   default Boolean getOrDefault(Object key, Boolean defaultValue) {
      return Map.super.getOrDefault(key, defaultValue);
   }

   default boolean putIfAbsent(float key, boolean value) {
      boolean v = this.get(key);
      boolean drv = this.defaultReturnValue();
      if (v == drv && !this.containsKey(key)) {
         this.put(key, value);
         return drv;
      } else {
         return v;
      }
   }

   default boolean remove(float key, boolean value) {
      boolean curValue = this.get(key);
      if (curValue == value && (curValue != this.defaultReturnValue() || this.containsKey(key))) {
         this.remove(key);
         return true;
      } else {
         return false;
      }
   }

   default boolean replace(float key, boolean oldValue, boolean newValue) {
      boolean curValue = this.get(key);
      if (curValue == oldValue && (curValue != this.defaultReturnValue() || this.containsKey(key))) {
         this.put(key, newValue);
         return true;
      } else {
         return false;
      }
   }

   default boolean replace(float key, boolean value) {
      return this.containsKey(key) ? this.put(key, value) : this.defaultReturnValue();
   }

   default boolean computeIfAbsent(float key, DoublePredicate mappingFunction) {
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

   default boolean computeIfAbsentNullable(float key, DoubleFunction<? extends Boolean> mappingFunction) {
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

   default boolean computeIfAbsent(float key, Float2BooleanFunction mappingFunction) {
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
   default boolean computeIfAbsentPartial(float key, Float2BooleanFunction mappingFunction) {
      return this.computeIfAbsent(key, mappingFunction);
   }

   default boolean computeIfPresent(float key, BiFunction<? super Float, ? super Boolean, ? extends Boolean> remappingFunction) {
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

   default boolean compute(float key, BiFunction<? super Float, ? super Boolean, ? extends Boolean> remappingFunction) {
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

   default boolean merge(float key, boolean value, BiFunction<? super Boolean, ? super Boolean, ? extends Boolean> remappingFunction) {
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

   public interface Entry extends java.util.Map.Entry<Float, Boolean> {
      float getFloatKey();

      @Deprecated
      default Float getKey() {
         return this.getFloatKey();
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

   public interface FastEntrySet extends ObjectSet<Float2BooleanMap.Entry> {
      ObjectIterator<Float2BooleanMap.Entry> fastIterator();

      default void fastForEach(Consumer<? super Float2BooleanMap.Entry> consumer) {
         this.forEach(consumer);
      }
   }
}
