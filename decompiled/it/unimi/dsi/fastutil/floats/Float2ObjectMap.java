package it.unimi.dsi.fastutil.floats;

import it.unimi.dsi.fastutil.objects.ObjectCollection;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.DoubleFunction;

public interface Float2ObjectMap<V> extends Float2ObjectFunction<V>, Map<Float, V> {
   @Override
   int size();

   @Override
   default void clear() {
      throw new UnsupportedOperationException();
   }

   @Override
   void defaultReturnValue(V var1);

   @Override
   V defaultReturnValue();

   ObjectSet<Float2ObjectMap.Entry<V>> float2ObjectEntrySet();

   @Deprecated
   default ObjectSet<java.util.Map.Entry<Float, V>> entrySet() {
      return this.float2ObjectEntrySet();
   }

   @Deprecated
   @Override
   default V put(Float key, V value) {
      return Float2ObjectFunction.super.put(key, value);
   }

   @Deprecated
   @Override
   default V get(Object key) {
      return Float2ObjectFunction.super.get(key);
   }

   @Deprecated
   @Override
   default V remove(Object key) {
      return Float2ObjectFunction.super.remove(key);
   }

   FloatSet keySet();

   ObjectCollection<V> values();

   @Override
   boolean containsKey(float var1);

   @Deprecated
   @Override
   default boolean containsKey(Object key) {
      return Float2ObjectFunction.super.containsKey(key);
   }

   @Override
   default void forEach(BiConsumer<? super Float, ? super V> consumer) {
      ObjectSet<Float2ObjectMap.Entry<V>> entrySet = this.float2ObjectEntrySet();
      Consumer<Float2ObjectMap.Entry<V>> wrappingConsumer = entry -> consumer.accept(entry.getFloatKey(), entry.getValue());
      if (entrySet instanceof Float2ObjectMap.FastEntrySet) {
         ((Float2ObjectMap.FastEntrySet)entrySet).fastForEach(wrappingConsumer);
      } else {
         entrySet.forEach(wrappingConsumer);
      }
   }

   @Override
   default V getOrDefault(float key, V defaultValue) {
      V v;
      return (v = this.get(key)) == this.defaultReturnValue() && !this.containsKey(key) ? defaultValue : v;
   }

   @Deprecated
   @Override
   default V getOrDefault(Object key, V defaultValue) {
      return Map.super.getOrDefault(key, defaultValue);
   }

   default V putIfAbsent(float key, V value) {
      V v = this.get(key);
      V drv = this.defaultReturnValue();
      if (v == drv && !this.containsKey(key)) {
         this.put(key, value);
         return drv;
      } else {
         return v;
      }
   }

   default boolean remove(float key, Object value) {
      V curValue = this.get(key);
      if (Objects.equals(curValue, value) && (curValue != this.defaultReturnValue() || this.containsKey(key))) {
         this.remove(key);
         return true;
      } else {
         return false;
      }
   }

   default boolean replace(float key, V oldValue, V newValue) {
      V curValue = this.get(key);
      if (Objects.equals(curValue, oldValue) && (curValue != this.defaultReturnValue() || this.containsKey(key))) {
         this.put(key, newValue);
         return true;
      } else {
         return false;
      }
   }

   default V replace(float key, V value) {
      return this.containsKey(key) ? this.put(key, value) : this.defaultReturnValue();
   }

   default V computeIfAbsent(float key, DoubleFunction<? extends V> mappingFunction) {
      Objects.requireNonNull(mappingFunction);
      V v = this.get(key);
      if (v == this.defaultReturnValue() && !this.containsKey(key)) {
         V newValue = (V)mappingFunction.apply(key);
         this.put(key, newValue);
         return newValue;
      } else {
         return v;
      }
   }

   default V computeIfAbsent(float key, Float2ObjectFunction<? extends V> mappingFunction) {
      Objects.requireNonNull(mappingFunction);
      V v = this.get(key);
      V drv = this.defaultReturnValue();
      if (v != drv || this.containsKey(key)) {
         return v;
      } else if (!mappingFunction.containsKey(key)) {
         return drv;
      } else {
         V newValue = (V)mappingFunction.get(key);
         this.put(key, newValue);
         return newValue;
      }
   }

   @Deprecated
   default V computeIfAbsentPartial(float key, Float2ObjectFunction<? extends V> mappingFunction) {
      return this.computeIfAbsent(key, mappingFunction);
   }

   default V computeIfPresent(float key, BiFunction<? super Float, ? super V, ? extends V> remappingFunction) {
      Objects.requireNonNull(remappingFunction);
      V oldValue = this.get(key);
      V drv = this.defaultReturnValue();
      if (oldValue == drv && !this.containsKey(key)) {
         return drv;
      } else {
         V newValue = (V)remappingFunction.apply(key, oldValue);
         if (newValue == null) {
            this.remove(key);
            return drv;
         } else {
            this.put(key, newValue);
            return newValue;
         }
      }
   }

   default V compute(float key, BiFunction<? super Float, ? super V, ? extends V> remappingFunction) {
      Objects.requireNonNull(remappingFunction);
      V oldValue = this.get(key);
      V drv = this.defaultReturnValue();
      boolean contained = oldValue != drv || this.containsKey(key);
      V newValue = (V)remappingFunction.apply(key, contained ? oldValue : null);
      if (newValue == null) {
         if (contained) {
            this.remove(key);
         }

         return drv;
      } else {
         this.put(key, newValue);
         return newValue;
      }
   }

   default V merge(float key, V value, BiFunction<? super V, ? super V, ? extends V> remappingFunction) {
      Objects.requireNonNull(remappingFunction);
      Objects.requireNonNull(value);
      V oldValue = this.get(key);
      V drv = this.defaultReturnValue();
      V newValue;
      if (oldValue == drv && !this.containsKey(key)) {
         newValue = value;
      } else {
         V mergedValue = (V)remappingFunction.apply(oldValue, value);
         if (mergedValue == null) {
            this.remove(key);
            return drv;
         }

         newValue = mergedValue;
      }

      this.put(key, newValue);
      return newValue;
   }

   public interface Entry<V> extends java.util.Map.Entry<Float, V> {
      float getFloatKey();

      @Deprecated
      default Float getKey() {
         return this.getFloatKey();
      }
   }

   public interface FastEntrySet<V> extends ObjectSet<Float2ObjectMap.Entry<V>> {
      ObjectIterator<Float2ObjectMap.Entry<V>> fastIterator();

      default void fastForEach(Consumer<? super Float2ObjectMap.Entry<V>> consumer) {
         this.forEach(consumer);
      }
   }
}
