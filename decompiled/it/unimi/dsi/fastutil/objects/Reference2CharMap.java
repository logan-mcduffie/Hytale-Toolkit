package it.unimi.dsi.fastutil.objects;

import it.unimi.dsi.fastutil.SafeMath;
import it.unimi.dsi.fastutil.chars.CharBinaryOperator;
import it.unimi.dsi.fastutil.chars.CharCollection;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.IntBinaryOperator;
import java.util.function.ToIntFunction;

public interface Reference2CharMap<K> extends Reference2CharFunction<K>, Map<K, Character> {
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

   ObjectSet<Reference2CharMap.Entry<K>> reference2CharEntrySet();

   @Deprecated
   default ObjectSet<java.util.Map.Entry<K, Character>> entrySet() {
      return this.reference2CharEntrySet();
   }

   @Deprecated
   @Override
   default Character put(K key, Character value) {
      return Reference2CharFunction.super.put(key, value);
   }

   @Deprecated
   @Override
   default Character get(Object key) {
      return Reference2CharFunction.super.get(key);
   }

   @Deprecated
   @Override
   default Character remove(Object key) {
      return Reference2CharFunction.super.remove(key);
   }

   ReferenceSet<K> keySet();

   CharCollection values();

   @Override
   boolean containsKey(Object var1);

   boolean containsValue(char var1);

   @Deprecated
   @Override
   default boolean containsValue(Object value) {
      return value == null ? false : this.containsValue(((Character)value).charValue());
   }

   @Override
   default void forEach(BiConsumer<? super K, ? super Character> consumer) {
      ObjectSet<Reference2CharMap.Entry<K>> entrySet = this.reference2CharEntrySet();
      Consumer<Reference2CharMap.Entry<K>> wrappingConsumer = entry -> consumer.accept(entry.getKey(), entry.getCharValue());
      if (entrySet instanceof Reference2CharMap.FastEntrySet) {
         ((Reference2CharMap.FastEntrySet)entrySet).fastForEach(wrappingConsumer);
      } else {
         entrySet.forEach(wrappingConsumer);
      }
   }

   @Override
   default char getOrDefault(Object key, char defaultValue) {
      char v;
      return (v = this.getChar(key)) == this.defaultReturnValue() && !this.containsKey(key) ? defaultValue : v;
   }

   @Deprecated
   @Override
   default Character getOrDefault(Object key, Character defaultValue) {
      return Map.super.getOrDefault(key, defaultValue);
   }

   default char putIfAbsent(K key, char value) {
      char v = this.getChar(key);
      char drv = this.defaultReturnValue();
      if (v == drv && !this.containsKey(key)) {
         this.put(key, value);
         return drv;
      } else {
         return v;
      }
   }

   default boolean remove(Object key, char value) {
      char curValue = this.getChar(key);
      if (curValue == value && (curValue != this.defaultReturnValue() || this.containsKey(key))) {
         this.removeChar(key);
         return true;
      } else {
         return false;
      }
   }

   default boolean replace(K key, char oldValue, char newValue) {
      char curValue = this.getChar(key);
      if (curValue == oldValue && (curValue != this.defaultReturnValue() || this.containsKey(key))) {
         this.put(key, newValue);
         return true;
      } else {
         return false;
      }
   }

   default char replace(K key, char value) {
      return this.containsKey(key) ? this.put(key, value) : this.defaultReturnValue();
   }

   default char computeIfAbsent(K key, ToIntFunction<? super K> mappingFunction) {
      Objects.requireNonNull(mappingFunction);
      char v = this.getChar(key);
      if (v == this.defaultReturnValue() && !this.containsKey(key)) {
         char newValue = SafeMath.safeIntToChar(mappingFunction.applyAsInt(key));
         this.put(key, newValue);
         return newValue;
      } else {
         return v;
      }
   }

   @Deprecated
   default char computeCharIfAbsent(K key, ToIntFunction<? super K> mappingFunction) {
      return this.computeIfAbsent(key, mappingFunction);
   }

   default char computeIfAbsent(K key, Reference2CharFunction<? super K> mappingFunction) {
      Objects.requireNonNull(mappingFunction);
      char v = this.getChar(key);
      char drv = this.defaultReturnValue();
      if (v != drv || this.containsKey(key)) {
         return v;
      } else if (!mappingFunction.containsKey(key)) {
         return drv;
      } else {
         char newValue = mappingFunction.getChar(key);
         this.put(key, newValue);
         return newValue;
      }
   }

   @Deprecated
   default char computeCharIfAbsentPartial(K key, Reference2CharFunction<? super K> mappingFunction) {
      return this.computeIfAbsent(key, mappingFunction);
   }

   default char computeCharIfPresent(K key, BiFunction<? super K, ? super Character, ? extends Character> remappingFunction) {
      Objects.requireNonNull(remappingFunction);
      char oldValue = this.getChar(key);
      char drv = this.defaultReturnValue();
      if (oldValue == drv && !this.containsKey(key)) {
         return drv;
      } else {
         Character newValue = remappingFunction.apply(key, oldValue);
         if (newValue == null) {
            this.removeChar(key);
            return drv;
         } else {
            char newVal = newValue;
            this.put(key, newVal);
            return newVal;
         }
      }
   }

   default char computeChar(K key, BiFunction<? super K, ? super Character, ? extends Character> remappingFunction) {
      Objects.requireNonNull(remappingFunction);
      char oldValue = this.getChar(key);
      char drv = this.defaultReturnValue();
      boolean contained = oldValue != drv || this.containsKey(key);
      Character newValue = remappingFunction.apply(key, contained ? oldValue : null);
      if (newValue == null) {
         if (contained) {
            this.removeChar(key);
         }

         return drv;
      } else {
         char newVal = newValue;
         this.put(key, newVal);
         return newVal;
      }
   }

   default char merge(K key, char value, BiFunction<? super Character, ? super Character, ? extends Character> remappingFunction) {
      Objects.requireNonNull(remappingFunction);
      char oldValue = this.getChar(key);
      char drv = this.defaultReturnValue();
      char newValue;
      if (oldValue == drv && !this.containsKey(key)) {
         newValue = value;
      } else {
         Character mergedValue = remappingFunction.apply(oldValue, value);
         if (mergedValue == null) {
            this.removeChar(key);
            return drv;
         }

         newValue = mergedValue;
      }

      this.put(key, newValue);
      return newValue;
   }

   default char mergeChar(K key, char value, CharBinaryOperator remappingFunction) {
      Objects.requireNonNull(remappingFunction);
      char oldValue = this.getChar(key);
      char drv = this.defaultReturnValue();
      char newValue = oldValue == drv && !this.containsKey(key) ? value : remappingFunction.apply(oldValue, value);
      this.put(key, newValue);
      return newValue;
   }

   default char mergeChar(K key, char value, IntBinaryOperator remappingFunction) {
      return this.mergeChar(
         key,
         value,
         remappingFunction instanceof CharBinaryOperator
            ? (CharBinaryOperator)remappingFunction
            : (x, y) -> SafeMath.safeIntToChar(remappingFunction.applyAsInt(x, y))
      );
   }

   @Deprecated
   default char mergeChar(K key, char value, BiFunction<? super Character, ? super Character, ? extends Character> remappingFunction) {
      return this.merge(key, value, remappingFunction);
   }

   @Deprecated
   default Character putIfAbsent(K key, Character value) {
      return Map.super.putIfAbsent(key, value);
   }

   @Deprecated
   @Override
   default boolean remove(Object key, Object value) {
      return Map.super.remove(key, value);
   }

   @Deprecated
   default boolean replace(K key, Character oldValue, Character newValue) {
      return Map.super.replace(key, oldValue, newValue);
   }

   @Deprecated
   default Character replace(K key, Character value) {
      return Map.super.replace(key, value);
   }

   @Deprecated
   default Character merge(K key, Character value, BiFunction<? super Character, ? super Character, ? extends Character> remappingFunction) {
      return Map.super.merge(key, value, remappingFunction);
   }

   public interface Entry<K> extends java.util.Map.Entry<K, Character> {
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

   public interface FastEntrySet<K> extends ObjectSet<Reference2CharMap.Entry<K>> {
      ObjectIterator<Reference2CharMap.Entry<K>> fastIterator();

      default void fastForEach(Consumer<? super Reference2CharMap.Entry<K>> consumer) {
         this.forEach(consumer);
      }
   }
}
