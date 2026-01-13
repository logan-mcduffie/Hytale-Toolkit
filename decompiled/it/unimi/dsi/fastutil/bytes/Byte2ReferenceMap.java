package it.unimi.dsi.fastutil.bytes;

import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import it.unimi.dsi.fastutil.objects.ReferenceCollection;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.IntFunction;

public interface Byte2ReferenceMap<V> extends Byte2ReferenceFunction<V>, Map<Byte, V> {
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

   ObjectSet<Byte2ReferenceMap.Entry<V>> byte2ReferenceEntrySet();

   @Deprecated
   default ObjectSet<java.util.Map.Entry<Byte, V>> entrySet() {
      return this.byte2ReferenceEntrySet();
   }

   @Deprecated
   @Override
   default V put(Byte key, V value) {
      return Byte2ReferenceFunction.super.put(key, value);
   }

   @Deprecated
   @Override
   default V get(Object key) {
      return Byte2ReferenceFunction.super.get(key);
   }

   @Deprecated
   @Override
   default V remove(Object key) {
      return Byte2ReferenceFunction.super.remove(key);
   }

   ByteSet keySet();

   ReferenceCollection<V> values();

   @Override
   boolean containsKey(byte var1);

   @Deprecated
   @Override
   default boolean containsKey(Object key) {
      return Byte2ReferenceFunction.super.containsKey(key);
   }

   @Override
   default void forEach(BiConsumer<? super Byte, ? super V> consumer) {
      ObjectSet<Byte2ReferenceMap.Entry<V>> entrySet = this.byte2ReferenceEntrySet();
      Consumer<Byte2ReferenceMap.Entry<V>> wrappingConsumer = entry -> consumer.accept(entry.getByteKey(), entry.getValue());
      if (entrySet instanceof Byte2ReferenceMap.FastEntrySet) {
         ((Byte2ReferenceMap.FastEntrySet)entrySet).fastForEach(wrappingConsumer);
      } else {
         entrySet.forEach(wrappingConsumer);
      }
   }

   @Override
   default V getOrDefault(byte key, V defaultValue) {
      V v;
      return (v = this.get(key)) == this.defaultReturnValue() && !this.containsKey(key) ? defaultValue : v;
   }

   @Deprecated
   @Override
   default V getOrDefault(Object key, V defaultValue) {
      return Map.super.getOrDefault(key, defaultValue);
   }

   default V putIfAbsent(byte key, V value) {
      V v = this.get(key);
      V drv = this.defaultReturnValue();
      if (v == drv && !this.containsKey(key)) {
         this.put(key, value);
         return drv;
      } else {
         return v;
      }
   }

   default boolean remove(byte key, Object value) {
      V curValue = this.get(key);
      if (curValue == value && (curValue != this.defaultReturnValue() || this.containsKey(key))) {
         this.remove(key);
         return true;
      } else {
         return false;
      }
   }

   default boolean replace(byte key, V oldValue, V newValue) {
      V curValue = this.get(key);
      if (curValue == oldValue && (curValue != this.defaultReturnValue() || this.containsKey(key))) {
         this.put(key, newValue);
         return true;
      } else {
         return false;
      }
   }

   default V replace(byte key, V value) {
      return this.containsKey(key) ? this.put(key, value) : this.defaultReturnValue();
   }

   default V computeIfAbsent(byte key, IntFunction<? extends V> mappingFunction) {
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

   default V computeIfAbsent(byte key, Byte2ReferenceFunction<? extends V> mappingFunction) {
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
   default V computeIfAbsentPartial(byte key, Byte2ReferenceFunction<? extends V> mappingFunction) {
      return this.computeIfAbsent(key, mappingFunction);
   }

   default V computeIfPresent(byte key, BiFunction<? super Byte, ? super V, ? extends V> remappingFunction) {
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

   default V compute(byte key, BiFunction<? super Byte, ? super V, ? extends V> remappingFunction) {
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

   default V merge(byte key, V value, BiFunction<? super V, ? super V, ? extends V> remappingFunction) {
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

   public interface Entry<V> extends java.util.Map.Entry<Byte, V> {
      byte getByteKey();

      @Deprecated
      default Byte getKey() {
         return this.getByteKey();
      }
   }

   public interface FastEntrySet<V> extends ObjectSet<Byte2ReferenceMap.Entry<V>> {
      ObjectIterator<Byte2ReferenceMap.Entry<V>> fastIterator();

      default void fastForEach(Consumer<? super Byte2ReferenceMap.Entry<V>> consumer) {
         this.forEach(consumer);
      }
   }
}
