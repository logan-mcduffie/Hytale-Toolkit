package it.unimi.dsi.fastutil.bytes;

import it.unimi.dsi.fastutil.doubles.DoubleCollection;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.DoubleBinaryOperator;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.IntToDoubleFunction;

public interface Byte2DoubleMap extends Byte2DoubleFunction, Map<Byte, Double> {
   @Override
   int size();

   @Override
   default void clear() {
      throw new UnsupportedOperationException();
   }

   @Override
   void defaultReturnValue(double var1);

   @Override
   double defaultReturnValue();

   ObjectSet<Byte2DoubleMap.Entry> byte2DoubleEntrySet();

   @Deprecated
   default ObjectSet<java.util.Map.Entry<Byte, Double>> entrySet() {
      return this.byte2DoubleEntrySet();
   }

   @Deprecated
   @Override
   default Double put(Byte key, Double value) {
      return Byte2DoubleFunction.super.put(key, value);
   }

   @Deprecated
   @Override
   default Double get(Object key) {
      return Byte2DoubleFunction.super.get(key);
   }

   @Deprecated
   @Override
   default Double remove(Object key) {
      return Byte2DoubleFunction.super.remove(key);
   }

   ByteSet keySet();

   DoubleCollection values();

   @Override
   boolean containsKey(byte var1);

   @Deprecated
   @Override
   default boolean containsKey(Object key) {
      return Byte2DoubleFunction.super.containsKey(key);
   }

   boolean containsValue(double var1);

   @Deprecated
   @Override
   default boolean containsValue(Object value) {
      return value == null ? false : this.containsValue(((Double)value).doubleValue());
   }

   @Override
   default void forEach(BiConsumer<? super Byte, ? super Double> consumer) {
      ObjectSet<Byte2DoubleMap.Entry> entrySet = this.byte2DoubleEntrySet();
      Consumer<Byte2DoubleMap.Entry> wrappingConsumer = entry -> consumer.accept(entry.getByteKey(), entry.getDoubleValue());
      if (entrySet instanceof Byte2DoubleMap.FastEntrySet) {
         ((Byte2DoubleMap.FastEntrySet)entrySet).fastForEach(wrappingConsumer);
      } else {
         entrySet.forEach(wrappingConsumer);
      }
   }

   @Override
   default double getOrDefault(byte key, double defaultValue) {
      double v;
      return (v = this.get(key)) == this.defaultReturnValue() && !this.containsKey(key) ? defaultValue : v;
   }

   @Deprecated
   @Override
   default Double getOrDefault(Object key, Double defaultValue) {
      return Map.super.getOrDefault(key, defaultValue);
   }

   default double putIfAbsent(byte key, double value) {
      double v = this.get(key);
      double drv = this.defaultReturnValue();
      if (v == drv && !this.containsKey(key)) {
         this.put(key, value);
         return drv;
      } else {
         return v;
      }
   }

   default boolean remove(byte key, double value) {
      double curValue = this.get(key);
      if (Double.doubleToLongBits(curValue) == Double.doubleToLongBits(value) && (curValue != this.defaultReturnValue() || this.containsKey(key))) {
         this.remove(key);
         return true;
      } else {
         return false;
      }
   }

   default boolean replace(byte key, double oldValue, double newValue) {
      double curValue = this.get(key);
      if (Double.doubleToLongBits(curValue) == Double.doubleToLongBits(oldValue) && (curValue != this.defaultReturnValue() || this.containsKey(key))) {
         this.put(key, newValue);
         return true;
      } else {
         return false;
      }
   }

   default double replace(byte key, double value) {
      return this.containsKey(key) ? this.put(key, value) : this.defaultReturnValue();
   }

   default double computeIfAbsent(byte key, IntToDoubleFunction mappingFunction) {
      Objects.requireNonNull(mappingFunction);
      double v = this.get(key);
      if (v == this.defaultReturnValue() && !this.containsKey(key)) {
         double newValue = mappingFunction.applyAsDouble(key);
         this.put(key, newValue);
         return newValue;
      } else {
         return v;
      }
   }

   default double computeIfAbsentNullable(byte key, IntFunction<? extends Double> mappingFunction) {
      Objects.requireNonNull(mappingFunction);
      double v = this.get(key);
      double drv = this.defaultReturnValue();
      if (v == drv && !this.containsKey(key)) {
         Double mappedValue = mappingFunction.apply(key);
         if (mappedValue == null) {
            return drv;
         } else {
            double newValue = mappedValue;
            this.put(key, newValue);
            return newValue;
         }
      } else {
         return v;
      }
   }

   default double computeIfAbsent(byte key, Byte2DoubleFunction mappingFunction) {
      Objects.requireNonNull(mappingFunction);
      double v = this.get(key);
      double drv = this.defaultReturnValue();
      if (v != drv || this.containsKey(key)) {
         return v;
      } else if (!mappingFunction.containsKey(key)) {
         return drv;
      } else {
         double newValue = mappingFunction.get(key);
         this.put(key, newValue);
         return newValue;
      }
   }

   @Deprecated
   default double computeIfAbsentPartial(byte key, Byte2DoubleFunction mappingFunction) {
      return this.computeIfAbsent(key, mappingFunction);
   }

   default double computeIfPresent(byte key, BiFunction<? super Byte, ? super Double, ? extends Double> remappingFunction) {
      Objects.requireNonNull(remappingFunction);
      double oldValue = this.get(key);
      double drv = this.defaultReturnValue();
      if (oldValue == drv && !this.containsKey(key)) {
         return drv;
      } else {
         Double newValue = remappingFunction.apply(key, oldValue);
         if (newValue == null) {
            this.remove(key);
            return drv;
         } else {
            double newVal = newValue;
            this.put(key, newVal);
            return newVal;
         }
      }
   }

   default double compute(byte key, BiFunction<? super Byte, ? super Double, ? extends Double> remappingFunction) {
      Objects.requireNonNull(remappingFunction);
      double oldValue = this.get(key);
      double drv = this.defaultReturnValue();
      boolean contained = oldValue != drv || this.containsKey(key);
      Double newValue = remappingFunction.apply(key, contained ? oldValue : null);
      if (newValue == null) {
         if (contained) {
            this.remove(key);
         }

         return drv;
      } else {
         double newVal = newValue;
         this.put(key, newVal);
         return newVal;
      }
   }

   default double merge(byte key, double value, BiFunction<? super Double, ? super Double, ? extends Double> remappingFunction) {
      Objects.requireNonNull(remappingFunction);
      double oldValue = this.get(key);
      double drv = this.defaultReturnValue();
      double newValue;
      if (oldValue == drv && !this.containsKey(key)) {
         newValue = value;
      } else {
         Double mergedValue = remappingFunction.apply(oldValue, value);
         if (mergedValue == null) {
            this.remove(key);
            return drv;
         }

         newValue = mergedValue;
      }

      this.put(key, newValue);
      return newValue;
   }

   default double mergeDouble(byte key, double value, DoubleBinaryOperator remappingFunction) {
      Objects.requireNonNull(remappingFunction);
      double oldValue = this.get(key);
      double drv = this.defaultReturnValue();
      double newValue = oldValue == drv && !this.containsKey(key) ? value : remappingFunction.applyAsDouble(oldValue, value);
      this.put(key, newValue);
      return newValue;
   }

   default double mergeDouble(byte key, double value, it.unimi.dsi.fastutil.doubles.DoubleBinaryOperator remappingFunction) {
      return this.mergeDouble(key, value, (DoubleBinaryOperator)remappingFunction);
   }

   @Deprecated
   default Double putIfAbsent(Byte key, Double value) {
      return Map.super.putIfAbsent(key, value);
   }

   @Deprecated
   @Override
   default boolean remove(Object key, Object value) {
      return Map.super.remove(key, value);
   }

   @Deprecated
   default boolean replace(Byte key, Double oldValue, Double newValue) {
      return Map.super.replace(key, oldValue, newValue);
   }

   @Deprecated
   default Double replace(Byte key, Double value) {
      return Map.super.replace(key, value);
   }

   @Deprecated
   default Double computeIfAbsent(Byte key, Function<? super Byte, ? extends Double> mappingFunction) {
      return Map.super.computeIfAbsent(key, mappingFunction);
   }

   @Deprecated
   default Double computeIfPresent(Byte key, BiFunction<? super Byte, ? super Double, ? extends Double> remappingFunction) {
      return Map.super.computeIfPresent(key, remappingFunction);
   }

   @Deprecated
   default Double compute(Byte key, BiFunction<? super Byte, ? super Double, ? extends Double> remappingFunction) {
      return Map.super.compute(key, remappingFunction);
   }

   @Deprecated
   default Double merge(Byte key, Double value, BiFunction<? super Double, ? super Double, ? extends Double> remappingFunction) {
      return Map.super.merge(key, value, remappingFunction);
   }

   public interface Entry extends java.util.Map.Entry<Byte, Double> {
      byte getByteKey();

      @Deprecated
      default Byte getKey() {
         return this.getByteKey();
      }

      double getDoubleValue();

      double setValue(double var1);

      @Deprecated
      default Double getValue() {
         return this.getDoubleValue();
      }

      @Deprecated
      default Double setValue(Double value) {
         return this.setValue(value.doubleValue());
      }
   }

   public interface FastEntrySet extends ObjectSet<Byte2DoubleMap.Entry> {
      ObjectIterator<Byte2DoubleMap.Entry> fastIterator();

      default void fastForEach(Consumer<? super Byte2DoubleMap.Entry> consumer) {
         this.forEach(consumer);
      }
   }
}
