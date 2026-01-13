package it.unimi.dsi.fastutil.longs;

import it.unimi.dsi.fastutil.SafeMath;
import it.unimi.dsi.fastutil.chars.CharBinaryOperator;
import it.unimi.dsi.fastutil.chars.CharCollection;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.IntBinaryOperator;
import java.util.function.LongFunction;
import java.util.function.LongToIntFunction;

public interface Long2CharMap extends Long2CharFunction, Map<Long, Character> {
   @Override
   int size();

   @Override
   default void clear() {
      throw new UnsupportedOperationException();
   }

   @Override
   void defaultReturnValue(char var1);

   @Override
   char defaultReturnValue();

   ObjectSet<Long2CharMap.Entry> long2CharEntrySet();

   @Deprecated
   default ObjectSet<java.util.Map.Entry<Long, Character>> entrySet() {
      return this.long2CharEntrySet();
   }

   @Deprecated
   @Override
   default Character put(Long key, Character value) {
      return Long2CharFunction.super.put(key, value);
   }

   @Deprecated
   @Override
   default Character get(Object key) {
      return Long2CharFunction.super.get(key);
   }

   @Deprecated
   @Override
   default Character remove(Object key) {
      return Long2CharFunction.super.remove(key);
   }

   LongSet keySet();

   CharCollection values();

   @Override
   boolean containsKey(long var1);

   @Deprecated
   @Override
   default boolean containsKey(Object key) {
      return Long2CharFunction.super.containsKey(key);
   }

   boolean containsValue(char var1);

   @Deprecated
   @Override
   default boolean containsValue(Object value) {
      return value == null ? false : this.containsValue(((Character)value).charValue());
   }

   @Override
   default void forEach(BiConsumer<? super Long, ? super Character> consumer) {
      ObjectSet<Long2CharMap.Entry> entrySet = this.long2CharEntrySet();
      Consumer<Long2CharMap.Entry> wrappingConsumer = entry -> consumer.accept(entry.getLongKey(), entry.getCharValue());
      if (entrySet instanceof Long2CharMap.FastEntrySet) {
         ((Long2CharMap.FastEntrySet)entrySet).fastForEach(wrappingConsumer);
      } else {
         entrySet.forEach(wrappingConsumer);
      }
   }

   @Override
   default char getOrDefault(long key, char defaultValue) {
      char v;
      return (v = this.get(key)) == this.defaultReturnValue() && !this.containsKey(key) ? defaultValue : v;
   }

   @Deprecated
   @Override
   default Character getOrDefault(Object key, Character defaultValue) {
      return Map.super.getOrDefault(key, defaultValue);
   }

   default char putIfAbsent(long key, char value) {
      char v = this.get(key);
      char drv = this.defaultReturnValue();
      if (v == drv && !this.containsKey(key)) {
         this.put(key, value);
         return drv;
      } else {
         return v;
      }
   }

   default boolean remove(long key, char value) {
      char curValue = this.get(key);
      if (curValue == value && (curValue != this.defaultReturnValue() || this.containsKey(key))) {
         this.remove(key);
         return true;
      } else {
         return false;
      }
   }

   default boolean replace(long key, char oldValue, char newValue) {
      char curValue = this.get(key);
      if (curValue == oldValue && (curValue != this.defaultReturnValue() || this.containsKey(key))) {
         this.put(key, newValue);
         return true;
      } else {
         return false;
      }
   }

   default char replace(long key, char value) {
      return this.containsKey(key) ? this.put(key, value) : this.defaultReturnValue();
   }

   default char computeIfAbsent(long key, LongToIntFunction mappingFunction) {
      Objects.requireNonNull(mappingFunction);
      char v = this.get(key);
      if (v == this.defaultReturnValue() && !this.containsKey(key)) {
         char newValue = SafeMath.safeIntToChar(mappingFunction.applyAsInt(key));
         this.put(key, newValue);
         return newValue;
      } else {
         return v;
      }
   }

   default char computeIfAbsentNullable(long key, LongFunction<? extends Character> mappingFunction) {
      Objects.requireNonNull(mappingFunction);
      char v = this.get(key);
      char drv = this.defaultReturnValue();
      if (v == drv && !this.containsKey(key)) {
         Character mappedValue = mappingFunction.apply(key);
         if (mappedValue == null) {
            return drv;
         } else {
            char newValue = mappedValue;
            this.put(key, newValue);
            return newValue;
         }
      } else {
         return v;
      }
   }

   default char computeIfAbsent(long key, Long2CharFunction mappingFunction) {
      Objects.requireNonNull(mappingFunction);
      char v = this.get(key);
      char drv = this.defaultReturnValue();
      if (v != drv || this.containsKey(key)) {
         return v;
      } else if (!mappingFunction.containsKey(key)) {
         return drv;
      } else {
         char newValue = mappingFunction.get(key);
         this.put(key, newValue);
         return newValue;
      }
   }

   @Deprecated
   default char computeIfAbsentPartial(long key, Long2CharFunction mappingFunction) {
      return this.computeIfAbsent(key, mappingFunction);
   }

   default char computeIfPresent(long key, BiFunction<? super Long, ? super Character, ? extends Character> remappingFunction) {
      Objects.requireNonNull(remappingFunction);
      char oldValue = this.get(key);
      char drv = this.defaultReturnValue();
      if (oldValue == drv && !this.containsKey(key)) {
         return drv;
      } else {
         Character newValue = remappingFunction.apply(key, oldValue);
         if (newValue == null) {
            this.remove(key);
            return drv;
         } else {
            char newVal = newValue;
            this.put(key, newVal);
            return newVal;
         }
      }
   }

   default char compute(long key, BiFunction<? super Long, ? super Character, ? extends Character> remappingFunction) {
      Objects.requireNonNull(remappingFunction);
      char oldValue = this.get(key);
      char drv = this.defaultReturnValue();
      boolean contained = oldValue != drv || this.containsKey(key);
      Character newValue = remappingFunction.apply(key, contained ? oldValue : null);
      if (newValue == null) {
         if (contained) {
            this.remove(key);
         }

         return drv;
      } else {
         char newVal = newValue;
         this.put(key, newVal);
         return newVal;
      }
   }

   default char merge(long key, char value, BiFunction<? super Character, ? super Character, ? extends Character> remappingFunction) {
      Objects.requireNonNull(remappingFunction);
      char oldValue = this.get(key);
      char drv = this.defaultReturnValue();
      char newValue;
      if (oldValue == drv && !this.containsKey(key)) {
         newValue = value;
      } else {
         Character mergedValue = remappingFunction.apply(oldValue, value);
         if (mergedValue == null) {
            this.remove(key);
            return drv;
         }

         newValue = mergedValue;
      }

      this.put(key, newValue);
      return newValue;
   }

   default char mergeChar(long key, char value, CharBinaryOperator remappingFunction) {
      Objects.requireNonNull(remappingFunction);
      char oldValue = this.get(key);
      char drv = this.defaultReturnValue();
      char newValue = oldValue == drv && !this.containsKey(key) ? value : remappingFunction.apply(oldValue, value);
      this.put(key, newValue);
      return newValue;
   }

   default char mergeChar(long key, char value, IntBinaryOperator remappingFunction) {
      return this.mergeChar(
         key,
         value,
         remappingFunction instanceof CharBinaryOperator
            ? (CharBinaryOperator)remappingFunction
            : (x, y) -> SafeMath.safeIntToChar(remappingFunction.applyAsInt(x, y))
      );
   }

   @Deprecated
   default Character putIfAbsent(Long key, Character value) {
      return Map.super.putIfAbsent(key, value);
   }

   @Deprecated
   @Override
   default boolean remove(Object key, Object value) {
      return Map.super.remove(key, value);
   }

   @Deprecated
   default boolean replace(Long key, Character oldValue, Character newValue) {
      return Map.super.replace(key, oldValue, newValue);
   }

   @Deprecated
   default Character replace(Long key, Character value) {
      return Map.super.replace(key, value);
   }

   @Deprecated
   default Character computeIfAbsent(Long key, Function<? super Long, ? extends Character> mappingFunction) {
      return Map.super.computeIfAbsent(key, mappingFunction);
   }

   @Deprecated
   default Character computeIfPresent(Long key, BiFunction<? super Long, ? super Character, ? extends Character> remappingFunction) {
      return Map.super.computeIfPresent(key, remappingFunction);
   }

   @Deprecated
   default Character compute(Long key, BiFunction<? super Long, ? super Character, ? extends Character> remappingFunction) {
      return Map.super.compute(key, remappingFunction);
   }

   @Deprecated
   default Character merge(Long key, Character value, BiFunction<? super Character, ? super Character, ? extends Character> remappingFunction) {
      return Map.super.merge(key, value, remappingFunction);
   }

   public interface Entry extends java.util.Map.Entry<Long, Character> {
      long getLongKey();

      @Deprecated
      default Long getKey() {
         return this.getLongKey();
      }

      char getCharValue();

      char setValue(char var1);

      @Deprecated
      default Character getValue() {
         return this.getCharValue();
      }

      @Deprecated
      default Character setValue(Character value) {
         return this.setValue(value.charValue());
      }
   }

   public interface FastEntrySet extends ObjectSet<Long2CharMap.Entry> {
      ObjectIterator<Long2CharMap.Entry> fastIterator();

      default void fastForEach(Consumer<? super Long2CharMap.Entry> consumer) {
         this.forEach(consumer);
      }
   }
}
