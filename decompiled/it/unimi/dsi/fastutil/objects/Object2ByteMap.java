package it.unimi.dsi.fastutil.objects;

import it.unimi.dsi.fastutil.SafeMath;
import it.unimi.dsi.fastutil.bytes.ByteBinaryOperator;
import it.unimi.dsi.fastutil.bytes.ByteCollection;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.IntBinaryOperator;
import java.util.function.ToIntFunction;

public interface Object2ByteMap<K> extends Object2ByteFunction<K>, Map<K, Byte> {
   @Override
   int size();

   @Override
   default void clear() {
      throw new UnsupportedOperationException();
   }

   @Override
   void defaultReturnValue(byte var1);

   @Override
   byte defaultReturnValue();

   ObjectSet<Object2ByteMap.Entry<K>> object2ByteEntrySet();

   @Deprecated
   default ObjectSet<java.util.Map.Entry<K, Byte>> entrySet() {
      return this.object2ByteEntrySet();
   }

   @Deprecated
   @Override
   default Byte put(K key, Byte value) {
      return Object2ByteFunction.super.put(key, value);
   }

   @Deprecated
   @Override
   default Byte get(Object key) {
      return Object2ByteFunction.super.get(key);
   }

   @Deprecated
   @Override
   default Byte remove(Object key) {
      return Object2ByteFunction.super.remove(key);
   }

   ObjectSet<K> keySet();

   ByteCollection values();

   @Override
   boolean containsKey(Object var1);

   boolean containsValue(byte var1);

   @Deprecated
   @Override
   default boolean containsValue(Object value) {
      return value == null ? false : this.containsValue(((Byte)value).byteValue());
   }

   @Override
   default void forEach(BiConsumer<? super K, ? super Byte> consumer) {
      ObjectSet<Object2ByteMap.Entry<K>> entrySet = this.object2ByteEntrySet();
      Consumer<Object2ByteMap.Entry<K>> wrappingConsumer = entry -> consumer.accept(entry.getKey(), entry.getByteValue());
      if (entrySet instanceof Object2ByteMap.FastEntrySet) {
         ((Object2ByteMap.FastEntrySet)entrySet).fastForEach(wrappingConsumer);
      } else {
         entrySet.forEach(wrappingConsumer);
      }
   }

   @Override
   default byte getOrDefault(Object key, byte defaultValue) {
      byte v;
      return (v = this.getByte(key)) == this.defaultReturnValue() && !this.containsKey(key) ? defaultValue : v;
   }

   @Deprecated
   @Override
   default Byte getOrDefault(Object key, Byte defaultValue) {
      return Map.super.getOrDefault(key, defaultValue);
   }

   default byte putIfAbsent(K key, byte value) {
      byte v = this.getByte(key);
      byte drv = this.defaultReturnValue();
      if (v == drv && !this.containsKey(key)) {
         this.put(key, value);
         return drv;
      } else {
         return v;
      }
   }

   default boolean remove(Object key, byte value) {
      byte curValue = this.getByte(key);
      if (curValue == value && (curValue != this.defaultReturnValue() || this.containsKey(key))) {
         this.removeByte(key);
         return true;
      } else {
         return false;
      }
   }

   default boolean replace(K key, byte oldValue, byte newValue) {
      byte curValue = this.getByte(key);
      if (curValue == oldValue && (curValue != this.defaultReturnValue() || this.containsKey(key))) {
         this.put(key, newValue);
         return true;
      } else {
         return false;
      }
   }

   default byte replace(K key, byte value) {
      return this.containsKey(key) ? this.put(key, value) : this.defaultReturnValue();
   }

   default byte computeIfAbsent(K key, ToIntFunction<? super K> mappingFunction) {
      Objects.requireNonNull(mappingFunction);
      byte v = this.getByte(key);
      if (v == this.defaultReturnValue() && !this.containsKey(key)) {
         byte newValue = SafeMath.safeIntToByte(mappingFunction.applyAsInt(key));
         this.put(key, newValue);
         return newValue;
      } else {
         return v;
      }
   }

   @Deprecated
   default byte computeByteIfAbsent(K key, ToIntFunction<? super K> mappingFunction) {
      return this.computeIfAbsent(key, mappingFunction);
   }

   default byte computeIfAbsent(K key, Object2ByteFunction<? super K> mappingFunction) {
      Objects.requireNonNull(mappingFunction);
      byte v = this.getByte(key);
      byte drv = this.defaultReturnValue();
      if (v != drv || this.containsKey(key)) {
         return v;
      } else if (!mappingFunction.containsKey(key)) {
         return drv;
      } else {
         byte newValue = mappingFunction.getByte(key);
         this.put(key, newValue);
         return newValue;
      }
   }

   @Deprecated
   default byte computeByteIfAbsentPartial(K key, Object2ByteFunction<? super K> mappingFunction) {
      return this.computeIfAbsent(key, mappingFunction);
   }

   default byte computeByteIfPresent(K key, BiFunction<? super K, ? super Byte, ? extends Byte> remappingFunction) {
      Objects.requireNonNull(remappingFunction);
      byte oldValue = this.getByte(key);
      byte drv = this.defaultReturnValue();
      if (oldValue == drv && !this.containsKey(key)) {
         return drv;
      } else {
         Byte newValue = remappingFunction.apply(key, oldValue);
         if (newValue == null) {
            this.removeByte(key);
            return drv;
         } else {
            byte newVal = newValue;
            this.put(key, newVal);
            return newVal;
         }
      }
   }

   default byte computeByte(K key, BiFunction<? super K, ? super Byte, ? extends Byte> remappingFunction) {
      Objects.requireNonNull(remappingFunction);
      byte oldValue = this.getByte(key);
      byte drv = this.defaultReturnValue();
      boolean contained = oldValue != drv || this.containsKey(key);
      Byte newValue = remappingFunction.apply(key, contained ? oldValue : null);
      if (newValue == null) {
         if (contained) {
            this.removeByte(key);
         }

         return drv;
      } else {
         byte newVal = newValue;
         this.put(key, newVal);
         return newVal;
      }
   }

   default byte merge(K key, byte value, BiFunction<? super Byte, ? super Byte, ? extends Byte> remappingFunction) {
      Objects.requireNonNull(remappingFunction);
      byte oldValue = this.getByte(key);
      byte drv = this.defaultReturnValue();
      byte newValue;
      if (oldValue == drv && !this.containsKey(key)) {
         newValue = value;
      } else {
         Byte mergedValue = remappingFunction.apply(oldValue, value);
         if (mergedValue == null) {
            this.removeByte(key);
            return drv;
         }

         newValue = mergedValue;
      }

      this.put(key, newValue);
      return newValue;
   }

   default byte mergeByte(K key, byte value, ByteBinaryOperator remappingFunction) {
      Objects.requireNonNull(remappingFunction);
      byte oldValue = this.getByte(key);
      byte drv = this.defaultReturnValue();
      byte newValue = oldValue == drv && !this.containsKey(key) ? value : remappingFunction.apply(oldValue, value);
      this.put(key, newValue);
      return newValue;
   }

   default byte mergeByte(K key, byte value, IntBinaryOperator remappingFunction) {
      return this.mergeByte(
         key,
         value,
         remappingFunction instanceof ByteBinaryOperator
            ? (ByteBinaryOperator)remappingFunction
            : (x, y) -> SafeMath.safeIntToByte(remappingFunction.applyAsInt(x, y))
      );
   }

   @Deprecated
   default byte mergeByte(K key, byte value, BiFunction<? super Byte, ? super Byte, ? extends Byte> remappingFunction) {
      return this.merge(key, value, remappingFunction);
   }

   @Deprecated
   default Byte putIfAbsent(K key, Byte value) {
      return Map.super.putIfAbsent(key, value);
   }

   @Deprecated
   @Override
   default boolean remove(Object key, Object value) {
      return Map.super.remove(key, value);
   }

   @Deprecated
   default boolean replace(K key, Byte oldValue, Byte newValue) {
      return Map.super.replace(key, oldValue, newValue);
   }

   @Deprecated
   default Byte replace(K key, Byte value) {
      return Map.super.replace(key, value);
   }

   @Deprecated
   default Byte merge(K key, Byte value, BiFunction<? super Byte, ? super Byte, ? extends Byte> remappingFunction) {
      return Map.super.merge(key, value, remappingFunction);
   }

   public interface Entry<K> extends java.util.Map.Entry<K, Byte> {
      byte getByteValue();

      byte setValue(byte var1);

      @Deprecated
      default Byte getValue() {
         return this.getByteValue();
      }

      @Deprecated
      default Byte setValue(Byte value) {
         return this.setValue(value.byteValue());
      }
   }

   public interface FastEntrySet<K> extends ObjectSet<Object2ByteMap.Entry<K>> {
      ObjectIterator<Object2ByteMap.Entry<K>> fastIterator();

      default void fastForEach(Consumer<? super Object2ByteMap.Entry<K>> consumer) {
         this.forEach(consumer);
      }
   }
}
