package it.unimi.dsi.fastutil.doubles;

import it.unimi.dsi.fastutil.objects.ObjectCollection;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.DoubleFunction;

public interface Double2ObjectMap<V> extends Double2ObjectFunction<V>, Map<Double, V> {
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

   ObjectSet<Double2ObjectMap.Entry<V>> double2ObjectEntrySet();

   @Deprecated
   default ObjectSet<java.util.Map.Entry<Double, V>> entrySet() {
      return this.double2ObjectEntrySet();
   }

   @Deprecated
   @Override
   default V put(Double key, V value) {
      return Double2ObjectFunction.super.put(key, value);
   }

   @Deprecated
   @Override
   default V get(Object key) {
      return Double2ObjectFunction.super.get(key);
   }

   @Deprecated
   @Override
   default V remove(Object key) {
      return Double2ObjectFunction.super.remove(key);
   }

   DoubleSet keySet();

   ObjectCollection<V> values();

   @Override
   boolean containsKey(double var1);

   @Deprecated
   @Override
   default boolean containsKey(Object key) {
      return Double2ObjectFunction.super.containsKey(key);
   }

   @Override
   default void forEach(BiConsumer<? super Double, ? super V> consumer) {
      ObjectSet<Double2ObjectMap.Entry<V>> entrySet = this.double2ObjectEntrySet();
      Consumer<Double2ObjectMap.Entry<V>> wrappingConsumer = entry -> consumer.accept(entry.getDoubleKey(), entry.getValue());
      if (entrySet instanceof Double2ObjectMap.FastEntrySet) {
         ((Double2ObjectMap.FastEntrySet)entrySet).fastForEach(wrappingConsumer);
      } else {
         entrySet.forEach(wrappingConsumer);
      }
   }

   @Override
   default V getOrDefault(double key, V defaultValue) {
      V v;
      return (v = this.get(key)) == this.defaultReturnValue() && !this.containsKey(key) ? defaultValue : v;
   }

   @Deprecated
   @Override
   default V getOrDefault(Object key, V defaultValue) {
      return Map.super.getOrDefault(key, defaultValue);
   }

   default V putIfAbsent(double key, V value) {
      V v = this.get(key);
      V drv = this.defaultReturnValue();
      if (v == drv && !this.containsKey(key)) {
         this.put(key, value);
         return drv;
      } else {
         return v;
      }
   }

   default boolean remove(double key, Object value) {
      V curValue = this.get(key);
      if (Objects.equals(curValue, value) && (curValue != this.defaultReturnValue() || this.containsKey(key))) {
         this.remove(key);
         return true;
      } else {
         return false;
      }
   }

   default boolean replace(double key, V oldValue, V newValue) {
      V curValue = this.get(key);
      if (Objects.equals(curValue, oldValue) && (curValue != this.defaultReturnValue() || this.containsKey(key))) {
         this.put(key, newValue);
         return true;
      } else {
         return false;
      }
   }

   default V replace(double key, V value) {
      return this.containsKey(key) ? this.put(key, value) : this.defaultReturnValue();
   }

   default V computeIfAbsent(double key, DoubleFunction<? extends V> mappingFunction) {
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

   default V computeIfAbsent(double key, Double2ObjectFunction<? extends V> mappingFunction) {
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
   default V computeIfAbsentPartial(double key, Double2ObjectFunction<? extends V> mappingFunction) {
      return this.computeIfAbsent(key, mappingFunction);
   }

   default V computeIfPresent(double key, BiFunction<? super Double, ? super V, ? extends V> remappingFunction) {
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

   default V compute(double key, BiFunction<? super Double, ? super V, ? extends V> remappingFunction) {
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

   default V merge(double key, V value, BiFunction<? super V, ? super V, ? extends V> remappingFunction) {
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

   public interface Entry<V> extends java.util.Map.Entry<Double, V> {
      double getDoubleKey();

      @Deprecated
      default Double getKey() {
         return this.getDoubleKey();
      }
   }

   public interface FastEntrySet<V> extends ObjectSet<Double2ObjectMap.Entry<V>> {
      ObjectIterator<Double2ObjectMap.Entry<V>> fastIterator();

      default void fastForEach(Consumer<? super Double2ObjectMap.Entry<V>> consumer) {
         this.forEach(consumer);
      }
   }
}
