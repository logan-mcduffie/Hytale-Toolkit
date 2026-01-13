package it.unimi.dsi.fastutil.objects;

import it.unimi.dsi.fastutil.SafeMath;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.ToIntFunction;

public final class Object2CharFunctions {
   public static final Object2CharFunctions.EmptyFunction EMPTY_FUNCTION = new Object2CharFunctions.EmptyFunction();

   private Object2CharFunctions() {
   }

   public static <K> Object2CharFunction<K> singleton(K key, char value) {
      return new Object2CharFunctions.Singleton<>(key, value);
   }

   public static <K> Object2CharFunction<K> singleton(K key, Character value) {
      return new Object2CharFunctions.Singleton<>(key, value);
   }

   public static <K> Object2CharFunction<K> synchronize(Object2CharFunction<K> f) {
      return new Object2CharFunctions.SynchronizedFunction<>(f);
   }

   public static <K> Object2CharFunction<K> synchronize(Object2CharFunction<K> f, Object sync) {
      return new Object2CharFunctions.SynchronizedFunction<>(f, sync);
   }

   public static <K> Object2CharFunction<K> unmodifiable(Object2CharFunction<? extends K> f) {
      return new Object2CharFunctions.UnmodifiableFunction<>(f);
   }

   public static <K> Object2CharFunction<K> primitive(Function<? super K, ? extends Character> f) {
      Objects.requireNonNull(f);
      if (f instanceof Object2CharFunction) {
         return (Object2CharFunction<K>)f;
      } else {
         return (Object2CharFunction<K>)(f instanceof ToIntFunction
            ? key -> SafeMath.safeIntToChar(((ToIntFunction)f).applyAsInt((K)key))
            : new Object2CharFunctions.PrimitiveFunction<>(f));
      }
   }

   public static class EmptyFunction<K> extends AbstractObject2CharFunction<K> implements Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;

      protected EmptyFunction() {
      }

      @Override
      public char getChar(Object k) {
         return '\u0000';
      }

      @Override
      public char getOrDefault(Object k, char defaultValue) {
         return defaultValue;
      }

      @Override
      public boolean containsKey(Object k) {
         return false;
      }

      @Override
      public char defaultReturnValue() {
         return '\u0000';
      }

      @Override
      public void defaultReturnValue(char defRetValue) {
         throw new UnsupportedOperationException();
      }

      @Override
      public int size() {
         return 0;
      }

      @Override
      public void clear() {
      }

      @Override
      public Object clone() {
         return Object2CharFunctions.EMPTY_FUNCTION;
      }

      @Override
      public int hashCode() {
         return 0;
      }

      @Override
      public boolean equals(Object o) {
         return !(o instanceof it.unimi.dsi.fastutil.Function) ? false : ((it.unimi.dsi.fastutil.Function)o).size() == 0;
      }

      @Override
      public String toString() {
         return "{}";
      }

      private Object readResolve() {
         return Object2CharFunctions.EMPTY_FUNCTION;
      }
   }

   public static class PrimitiveFunction<K> implements Object2CharFunction<K> {
      protected final Function<? super K, ? extends Character> function;

      protected PrimitiveFunction(Function<? super K, ? extends Character> function) {
         this.function = function;
      }

      @Override
      public boolean containsKey(Object key) {
         return this.function.apply((K)key) != null;
      }

      @Override
      public char getChar(Object key) {
         Character v = this.function.apply((K)key);
         return v == null ? this.defaultReturnValue() : v;
      }

      @Override
      public char getOrDefault(Object key, char defaultValue) {
         Character v = this.function.apply((K)key);
         return v == null ? defaultValue : v;
      }

      @Deprecated
      @Override
      public Character get(Object key) {
         return this.function.apply((K)key);
      }

      @Deprecated
      @Override
      public Character getOrDefault(Object key, Character defaultValue) {
         Character v;
         return (v = this.function.apply((K)key)) == null ? defaultValue : v;
      }

      @Deprecated
      @Override
      public Character put(K key, Character value) {
         throw new UnsupportedOperationException();
      }
   }

   public static class Singleton<K> extends AbstractObject2CharFunction<K> implements Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final K key;
      protected final char value;

      protected Singleton(K key, char value) {
         this.key = key;
         this.value = value;
      }

      @Override
      public boolean containsKey(Object k) {
         return Objects.equals(this.key, k);
      }

      @Override
      public char getChar(Object k) {
         return Objects.equals(this.key, k) ? this.value : this.defRetValue;
      }

      @Override
      public char getOrDefault(Object k, char defaultValue) {
         return Objects.equals(this.key, k) ? this.value : defaultValue;
      }

      @Override
      public int size() {
         return 1;
      }

      @Override
      public Object clone() {
         return this;
      }
   }

   public static class SynchronizedFunction<K> implements Object2CharFunction<K>, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Object2CharFunction<K> function;
      protected final Object sync;

      protected SynchronizedFunction(Object2CharFunction<K> f, Object sync) {
         if (f == null) {
            throw new NullPointerException();
         } else {
            this.function = f;
            this.sync = sync;
         }
      }

      protected SynchronizedFunction(Object2CharFunction<K> f) {
         if (f == null) {
            throw new NullPointerException();
         } else {
            this.function = f;
            this.sync = this;
         }
      }

      @Override
      public int applyAsInt(K operand) {
         synchronized (this.sync) {
            return this.function.applyAsInt(operand);
         }
      }

      @Deprecated
      public Character apply(K key) {
         synchronized (this.sync) {
            return this.function.apply(key);
         }
      }

      @Override
      public int size() {
         synchronized (this.sync) {
            return this.function.size();
         }
      }

      @Override
      public char defaultReturnValue() {
         synchronized (this.sync) {
            return this.function.defaultReturnValue();
         }
      }

      @Override
      public void defaultReturnValue(char defRetValue) {
         synchronized (this.sync) {
            this.function.defaultReturnValue(defRetValue);
         }
      }

      @Override
      public boolean containsKey(Object k) {
         synchronized (this.sync) {
            return this.function.containsKey(k);
         }
      }

      @Override
      public char put(K k, char v) {
         synchronized (this.sync) {
            return this.function.put(k, v);
         }
      }

      @Override
      public char getChar(Object k) {
         synchronized (this.sync) {
            return this.function.getChar(k);
         }
      }

      @Override
      public char getOrDefault(Object k, char defaultValue) {
         synchronized (this.sync) {
            return this.function.getOrDefault(k, defaultValue);
         }
      }

      @Override
      public char removeChar(Object k) {
         synchronized (this.sync) {
            return this.function.removeChar(k);
         }
      }

      @Override
      public void clear() {
         synchronized (this.sync) {
            this.function.clear();
         }
      }

      @Deprecated
      @Override
      public Character put(K k, Character v) {
         synchronized (this.sync) {
            return this.function.put(k, v);
         }
      }

      @Deprecated
      @Override
      public Character get(Object k) {
         synchronized (this.sync) {
            return this.function.get(k);
         }
      }

      @Deprecated
      @Override
      public Character getOrDefault(Object k, Character defaultValue) {
         synchronized (this.sync) {
            return this.function.getOrDefault(k, defaultValue);
         }
      }

      @Deprecated
      @Override
      public Character remove(Object k) {
         synchronized (this.sync) {
            return this.function.remove(k);
         }
      }

      @Override
      public int hashCode() {
         synchronized (this.sync) {
            return this.function.hashCode();
         }
      }

      @Override
      public boolean equals(Object o) {
         if (o == this) {
            return true;
         } else {
            synchronized (this.sync) {
               return this.function.equals(o);
            }
         }
      }

      @Override
      public String toString() {
         synchronized (this.sync) {
            return this.function.toString();
         }
      }

      private void writeObject(ObjectOutputStream s) throws IOException {
         synchronized (this.sync) {
            s.defaultWriteObject();
         }
      }
   }

   public static class UnmodifiableFunction<K> extends AbstractObject2CharFunction<K> implements Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Object2CharFunction<? extends K> function;

      protected UnmodifiableFunction(Object2CharFunction<? extends K> f) {
         if (f == null) {
            throw new NullPointerException();
         } else {
            this.function = f;
         }
      }

      @Override
      public int size() {
         return this.function.size();
      }

      @Override
      public char defaultReturnValue() {
         return this.function.defaultReturnValue();
      }

      @Override
      public void defaultReturnValue(char defRetValue) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean containsKey(Object k) {
         return this.function.containsKey(k);
      }

      @Override
      public char put(K k, char v) {
         throw new UnsupportedOperationException();
      }

      @Override
      public char getChar(Object k) {
         return this.function.getChar(k);
      }

      @Override
      public char getOrDefault(Object k, char defaultValue) {
         return this.function.getOrDefault(k, defaultValue);
      }

      @Override
      public char removeChar(Object k) {
         throw new UnsupportedOperationException();
      }

      @Override
      public void clear() {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      @Override
      public Character put(K k, Character v) {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      @Override
      public Character get(Object k) {
         return this.function.get(k);
      }

      @Deprecated
      @Override
      public Character getOrDefault(Object k, Character defaultValue) {
         return this.function.getOrDefault(k, defaultValue);
      }

      @Deprecated
      @Override
      public Character remove(Object k) {
         throw new UnsupportedOperationException();
      }

      @Override
      public int hashCode() {
         return this.function.hashCode();
      }

      @Override
      public boolean equals(Object o) {
         return o == this || this.function.equals(o);
      }

      @Override
      public String toString() {
         return this.function.toString();
      }
   }
}
