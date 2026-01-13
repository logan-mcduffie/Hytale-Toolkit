package it.unimi.dsi.fastutil.objects;

import it.unimi.dsi.fastutil.booleans.BooleanCollection;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Predicate;

public interface Reference2BooleanMap<K> extends Reference2BooleanFunction<K>, Map<K, Boolean> {
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

   ObjectSet<Reference2BooleanMap.Entry<K>> reference2BooleanEntrySet();

   @Deprecated
   default ObjectSet<java.util.Map.Entry<K, Boolean>> entrySet() {
      return this.reference2BooleanEntrySet();
   }

   @Deprecated
   @Override
   default Boolean put(K key, Boolean value) {
      return Reference2BooleanFunction.super.put(key, value);
   }

   @Deprecated
   @Override
   default Boolean get(Object key) {
      return Reference2BooleanFunction.super.get(key);
   }

   @Deprecated
   @Override
   default Boolean remove(Object key) {
      return Reference2BooleanFunction.super.remove(key);
   }

   ReferenceSet<K> keySet();

   BooleanCollection values();

   @Override
   boolean containsKey(Object var1);

   boolean containsValue(boolean var1);

   @Deprecated
   @Override
   default boolean containsValue(Object value) {
      return value == null ? false : this.containsValue(((Boolean)value).booleanValue());
   }

   @Override
   default void forEach(BiConsumer<? super K, ? super Boolean> consumer) {
      ObjectSet<Reference2BooleanMap.Entry<K>> entrySet = this.reference2BooleanEntrySet();
      Consumer<Reference2BooleanMap.Entry<K>> wrappingConsumer = entry -> consumer.accept(entry.getKey(), entry.getBooleanValue());
      if (entrySet instanceof Reference2BooleanMap.FastEntrySet) {
         ((Reference2BooleanMap.FastEntrySet)entrySet).fastForEach(wrappingConsumer);
      } else {
         entrySet.forEach(wrappingConsumer);
      }
   }

   @Override
   default boolean getOrDefault(Object key, boolean defaultValue) {
      boolean v;
      return (v = this.getBoolean(key)) == this.defaultReturnValue() && !this.containsKey(key) ? defaultValue : v;
   }

   @Deprecated
   @Override
   default Boolean getOrDefault(Object key, Boolean defaultValue) {
      return Map.super.getOrDefault(key, defaultValue);
   }

   default boolean putIfAbsent(K key, boolean value) {
      boolean v = this.getBoolean(key);
      boolean drv = this.defaultReturnValue();
      if (v == drv && !this.containsKey(key)) {
         this.put(key, value);
         return drv;
      } else {
         return v;
      }
   }

   default boolean remove(Object key, boolean value) {
      boolean curValue = this.getBoolean(key);
      if (curValue == value && (curValue != this.defaultReturnValue() || this.containsKey(key))) {
         this.removeBoolean(key);
         return true;
      } else {
         return false;
      }
   }

   default boolean replace(K key, boolean oldValue, boolean newValue) {
      boolean curValue = this.getBoolean(key);
      if (curValue == oldValue && (curValue != this.defaultReturnValue() || this.containsKey(key))) {
         this.put(key, newValue);
         return true;
      } else {
         return false;
      }
   }

   default boolean replace(K key, boolean value) {
      return this.containsKey(key) ? this.put(key, value) : this.defaultReturnValue();
   }

   default boolean computeIfAbsent(K key, Predicate<? super K> mappingFunction) {
      Objects.requireNonNull(mappingFunction);
      boolean v = this.getBoolean(key);
      if (v == this.defaultReturnValue() && !this.containsKey(key)) {
         boolean newValue = mappingFunction.test(key);
         this.put(key, newValue);
         return newValue;
      } else {
         return v;
      }
   }

   @Deprecated
   default boolean computeBooleanIfAbsent(K key, Predicate<? super K> mappingFunction) {
      return this.computeIfAbsent(key, mappingFunction);
   }

   default boolean computeIfAbsent(K key, Reference2BooleanFunction<? super K> mappingFunction) {
      Objects.requireNonNull(mappingFunction);
      boolean v = this.getBoolean(key);
      boolean drv = this.defaultReturnValue();
      if (v != drv || this.containsKey(key)) {
         return v;
      } else if (!mappingFunction.containsKey(key)) {
         return drv;
      } else {
         boolean newValue = mappingFunction.getBoolean(key);
         this.put(key, newValue);
         return newValue;
      }
   }

   @Deprecated
   default boolean computeBooleanIfAbsentPartial(K key, Reference2BooleanFunction<? super K> mappingFunction) {
      return this.computeIfAbsent(key, mappingFunction);
   }

   default boolean computeBooleanIfPresent(K key, BiFunction<? super K, ? super Boolean, ? extends Boolean> remappingFunction) {
      Objects.requireNonNull(remappingFunction);
      boolean oldValue = this.getBoolean(key);
      boolean drv = this.defaultReturnValue();
      if (oldValue == drv && !this.containsKey(key)) {
         return drv;
      } else {
         Boolean newValue = remappingFunction.apply(key, oldValue);
         if (newValue == null) {
            this.removeBoolean(key);
            return drv;
         } else {
            boolean newVal = newValue;
            this.put(key, newVal);
            return newVal;
         }
      }
   }

   default boolean computeBoolean(K key, BiFunction<? super K, ? super Boolean, ? extends Boolean> remappingFunction) {
      Objects.requireNonNull(remappingFunction);
      boolean oldValue = this.getBoolean(key);
      boolean drv = this.defaultReturnValue();
      boolean contained = oldValue != drv || this.containsKey(key);
      Boolean newValue = remappingFunction.apply(key, contained ? oldValue : null);
      if (newValue == null) {
         if (contained) {
            this.removeBoolean(key);
         }

         return drv;
      } else {
         boolean newVal = newValue;
         this.put(key, newVal);
         return newVal;
      }
   }

   default boolean merge(K key, boolean value, BiFunction<? super Boolean, ? super Boolean, ? extends Boolean> remappingFunction) {
      Objects.requireNonNull(remappingFunction);
      boolean oldValue = this.getBoolean(key);
      boolean drv = this.defaultReturnValue();
      boolean newValue;
      if (oldValue == drv && !this.containsKey(key)) {
         newValue = value;
      } else {
         Boolean mergedValue = remappingFunction.apply(oldValue, value);
         if (mergedValue == null) {
            this.removeBoolean(key);
            return drv;
         }

         newValue = mergedValue;
      }

      this.put(key, newValue);
      return newValue;
   }

   public interface Entry<K> extends java.util.Map.Entry<K, Boolean> {
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

   public interface FastEntrySet<K> extends ObjectSet<Reference2BooleanMap.Entry<K>> {
      ObjectIterator<Reference2BooleanMap.Entry<K>> fastIterator();

      default void fastForEach(Consumer<? super Reference2BooleanMap.Entry<K>> consumer) {
         this.forEach(consumer);
      }
   }
}
