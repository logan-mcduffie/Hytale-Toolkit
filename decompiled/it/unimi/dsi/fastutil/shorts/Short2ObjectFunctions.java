package it.unimi.dsi.fastutil.shorts;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.IntFunction;

public final class Short2ObjectFunctions {
   public static final Short2ObjectFunctions.EmptyFunction EMPTY_FUNCTION = new Short2ObjectFunctions.EmptyFunction();

   private Short2ObjectFunctions() {
   }

   public static <V> Short2ObjectFunction<V> singleton(short key, V value) {
      return new Short2ObjectFunctions.Singleton<>(key, value);
   }

   public static <V> Short2ObjectFunction<V> singleton(Short key, V value) {
      return new Short2ObjectFunctions.Singleton<>(key, value);
   }

   public static <V> Short2ObjectFunction<V> synchronize(Short2ObjectFunction<V> f) {
      return new Short2ObjectFunctions.SynchronizedFunction<>(f);
   }

   public static <V> Short2ObjectFunction<V> synchronize(Short2ObjectFunction<V> f, Object sync) {
      return new Short2ObjectFunctions.SynchronizedFunction<>(f, sync);
   }

   public static <V> Short2ObjectFunction<V> unmodifiable(Short2ObjectFunction<? extends V> f) {
      return new Short2ObjectFunctions.UnmodifiableFunction<>(f);
   }

   public static <V> Short2ObjectFunction<V> primitive(Function<? super Short, ? extends V> f) {
      Objects.requireNonNull(f);
      if (f instanceof Short2ObjectFunction) {
         return (Short2ObjectFunction<V>)f;
      } else {
         return (Short2ObjectFunction<V>)(f instanceof IntFunction ? ((IntFunction)f)::apply : new Short2ObjectFunctions.PrimitiveFunction<>(f));
      }
   }

   public static class EmptyFunction<V> extends AbstractShort2ObjectFunction<V> implements Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;

      protected EmptyFunction() {
      }

      @Override
      public V get(short k) {
         return null;
      }

      @Override
      public V getOrDefault(short k, V defaultValue) {
         return defaultValue;
      }

      @Override
      public boolean containsKey(short k) {
         return false;
      }

      @Override
      public V defaultReturnValue() {
         return null;
      }

      @Override
      public void defaultReturnValue(V defRetValue) {
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
         return Short2ObjectFunctions.EMPTY_FUNCTION;
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
         return Short2ObjectFunctions.EMPTY_FUNCTION;
      }
   }

   public static class PrimitiveFunction<V> implements Short2ObjectFunction<V> {
      protected final Function<? super Short, ? extends V> function;

      protected PrimitiveFunction(Function<? super Short, ? extends V> function) {
         this.function = function;
      }

      @Override
      public boolean containsKey(short key) {
         return this.function.apply(key) != null;
      }

      @Deprecated
      @Override
      public boolean containsKey(Object key) {
         return key == null ? false : this.function.apply((Short)key) != null;
      }

      @Override
      public V get(short key) {
         V v = (V)this.function.apply(key);
         return v == null ? null : v;
      }

      @Override
      public V getOrDefault(short key, V defaultValue) {
         V v = (V)this.function.apply(key);
         return v == null ? defaultValue : v;
      }

      @Deprecated
      @Override
      public V get(Object key) {
         return (V)(key == null ? null : this.function.apply((Short)key));
      }

      @Deprecated
      @Override
      public V getOrDefault(Object key, V defaultValue) {
         if (key == null) {
            return defaultValue;
         } else {
            V v;
            return (v = (V)this.function.apply((Short)key)) == null ? defaultValue : v;
         }
      }

      @Deprecated
      @Override
      public V put(Short key, V value) {
         throw new UnsupportedOperationException();
      }
   }

   public static class Singleton<V> extends AbstractShort2ObjectFunction<V> implements Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final short key;
      protected final V value;

      protected Singleton(short key, V value) {
         this.key = key;
         this.value = value;
      }

      @Override
      public boolean containsKey(short k) {
         return this.key == k;
      }

      @Override
      public V get(short k) {
         return this.key == k ? this.value : this.defRetValue;
      }

      @Override
      public V getOrDefault(short k, V defaultValue) {
         return this.key == k ? this.value : defaultValue;
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

   public static class SynchronizedFunction<V> implements Short2ObjectFunction<V>, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Short2ObjectFunction<V> function;
      protected final Object sync;

      protected SynchronizedFunction(Short2ObjectFunction<V> f, Object sync) {
         if (f == null) {
            throw new NullPointerException();
         } else {
            this.function = f;
            this.sync = sync;
         }
      }

      protected SynchronizedFunction(Short2ObjectFunction<V> f) {
         if (f == null) {
            throw new NullPointerException();
         } else {
            this.function = f;
            this.sync = this;
         }
      }

      @Deprecated
      @Override
      public V apply(int operand) {
         synchronized (this.sync) {
            return this.function.apply(operand);
         }
      }

      @Deprecated
      public V apply(Short key) {
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
      public V defaultReturnValue() {
         synchronized (this.sync) {
            return this.function.defaultReturnValue();
         }
      }

      @Override
      public void defaultReturnValue(V defRetValue) {
         synchronized (this.sync) {
            this.function.defaultReturnValue(defRetValue);
         }
      }

      @Override
      public boolean containsKey(short k) {
         synchronized (this.sync) {
            return this.function.containsKey(k);
         }
      }

      @Deprecated
      @Override
      public boolean containsKey(Object k) {
         synchronized (this.sync) {
            return this.function.containsKey(k);
         }
      }

      @Override
      public V put(short k, V v) {
         synchronized (this.sync) {
            return this.function.put(k, v);
         }
      }

      @Override
      public V get(short k) {
         synchronized (this.sync) {
            return this.function.get(k);
         }
      }

      @Override
      public V getOrDefault(short k, V defaultValue) {
         synchronized (this.sync) {
            return this.function.getOrDefault(k, defaultValue);
         }
      }

      @Override
      public V remove(short k) {
         synchronized (this.sync) {
            return this.function.remove(k);
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
      public V put(Short k, V v) {
         synchronized (this.sync) {
            return this.function.put(k, v);
         }
      }

      @Deprecated
      @Override
      public V get(Object k) {
         synchronized (this.sync) {
            return this.function.get(k);
         }
      }

      @Deprecated
      @Override
      public V getOrDefault(Object k, V defaultValue) {
         synchronized (this.sync) {
            return this.function.getOrDefault(k, defaultValue);
         }
      }

      @Deprecated
      @Override
      public V remove(Object k) {
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

   public static class UnmodifiableFunction<V> extends AbstractShort2ObjectFunction<V> implements Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Short2ObjectFunction<? extends V> function;

      protected UnmodifiableFunction(Short2ObjectFunction<? extends V> f) {
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
      public V defaultReturnValue() {
         return (V)this.function.defaultReturnValue();
      }

      @Override
      public void defaultReturnValue(V defRetValue) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean containsKey(short k) {
         return this.function.containsKey(k);
      }

      @Override
      public V put(short k, V v) {
         throw new UnsupportedOperationException();
      }

      @Override
      public V get(short k) {
         return (V)this.function.get(k);
      }

      @Override
      public V getOrDefault(short k, V defaultValue) {
         return (V)this.function.getOrDefault(k, defaultValue);
      }

      @Override
      public V remove(short k) {
         throw new UnsupportedOperationException();
      }

      @Override
      public void clear() {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      @Override
      public V put(Short k, V v) {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      @Override
      public V get(Object k) {
         return (V)this.function.get(k);
      }

      @Deprecated
      @Override
      public V getOrDefault(Object k, V defaultValue) {
         return (V)this.function.getOrDefault(k, defaultValue);
      }

      @Deprecated
      @Override
      public V remove(Object k) {
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
