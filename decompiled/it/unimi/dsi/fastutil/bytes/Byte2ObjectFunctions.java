package it.unimi.dsi.fastutil.bytes;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.IntFunction;

public final class Byte2ObjectFunctions {
   public static final Byte2ObjectFunctions.EmptyFunction EMPTY_FUNCTION = new Byte2ObjectFunctions.EmptyFunction();

   private Byte2ObjectFunctions() {
   }

   public static <V> Byte2ObjectFunction<V> singleton(byte key, V value) {
      return new Byte2ObjectFunctions.Singleton<>(key, value);
   }

   public static <V> Byte2ObjectFunction<V> singleton(Byte key, V value) {
      return new Byte2ObjectFunctions.Singleton<>(key, value);
   }

   public static <V> Byte2ObjectFunction<V> synchronize(Byte2ObjectFunction<V> f) {
      return new Byte2ObjectFunctions.SynchronizedFunction<>(f);
   }

   public static <V> Byte2ObjectFunction<V> synchronize(Byte2ObjectFunction<V> f, Object sync) {
      return new Byte2ObjectFunctions.SynchronizedFunction<>(f, sync);
   }

   public static <V> Byte2ObjectFunction<V> unmodifiable(Byte2ObjectFunction<? extends V> f) {
      return new Byte2ObjectFunctions.UnmodifiableFunction<>(f);
   }

   public static <V> Byte2ObjectFunction<V> primitive(Function<? super Byte, ? extends V> f) {
      Objects.requireNonNull(f);
      if (f instanceof Byte2ObjectFunction) {
         return (Byte2ObjectFunction<V>)f;
      } else {
         return (Byte2ObjectFunction<V>)(f instanceof IntFunction ? ((IntFunction)f)::apply : new Byte2ObjectFunctions.PrimitiveFunction<>(f));
      }
   }

   public static class EmptyFunction<V> extends AbstractByte2ObjectFunction<V> implements Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;

      protected EmptyFunction() {
      }

      @Override
      public V get(byte k) {
         return null;
      }

      @Override
      public V getOrDefault(byte k, V defaultValue) {
         return defaultValue;
      }

      @Override
      public boolean containsKey(byte k) {
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
         return Byte2ObjectFunctions.EMPTY_FUNCTION;
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
         return Byte2ObjectFunctions.EMPTY_FUNCTION;
      }
   }

   public static class PrimitiveFunction<V> implements Byte2ObjectFunction<V> {
      protected final Function<? super Byte, ? extends V> function;

      protected PrimitiveFunction(Function<? super Byte, ? extends V> function) {
         this.function = function;
      }

      @Override
      public boolean containsKey(byte key) {
         return this.function.apply(key) != null;
      }

      @Deprecated
      @Override
      public boolean containsKey(Object key) {
         return key == null ? false : this.function.apply((Byte)key) != null;
      }

      @Override
      public V get(byte key) {
         V v = (V)this.function.apply(key);
         return v == null ? null : v;
      }

      @Override
      public V getOrDefault(byte key, V defaultValue) {
         V v = (V)this.function.apply(key);
         return v == null ? defaultValue : v;
      }

      @Deprecated
      @Override
      public V get(Object key) {
         return (V)(key == null ? null : this.function.apply((Byte)key));
      }

      @Deprecated
      @Override
      public V getOrDefault(Object key, V defaultValue) {
         if (key == null) {
            return defaultValue;
         } else {
            V v;
            return (v = (V)this.function.apply((Byte)key)) == null ? defaultValue : v;
         }
      }

      @Deprecated
      @Override
      public V put(Byte key, V value) {
         throw new UnsupportedOperationException();
      }
   }

   public static class Singleton<V> extends AbstractByte2ObjectFunction<V> implements Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final byte key;
      protected final V value;

      protected Singleton(byte key, V value) {
         this.key = key;
         this.value = value;
      }

      @Override
      public boolean containsKey(byte k) {
         return this.key == k;
      }

      @Override
      public V get(byte k) {
         return this.key == k ? this.value : this.defRetValue;
      }

      @Override
      public V getOrDefault(byte k, V defaultValue) {
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

   public static class SynchronizedFunction<V> implements Byte2ObjectFunction<V>, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Byte2ObjectFunction<V> function;
      protected final Object sync;

      protected SynchronizedFunction(Byte2ObjectFunction<V> f, Object sync) {
         if (f == null) {
            throw new NullPointerException();
         } else {
            this.function = f;
            this.sync = sync;
         }
      }

      protected SynchronizedFunction(Byte2ObjectFunction<V> f) {
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
      public V apply(Byte key) {
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
      public boolean containsKey(byte k) {
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
      public V put(byte k, V v) {
         synchronized (this.sync) {
            return this.function.put(k, v);
         }
      }

      @Override
      public V get(byte k) {
         synchronized (this.sync) {
            return this.function.get(k);
         }
      }

      @Override
      public V getOrDefault(byte k, V defaultValue) {
         synchronized (this.sync) {
            return this.function.getOrDefault(k, defaultValue);
         }
      }

      @Override
      public V remove(byte k) {
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
      public V put(Byte k, V v) {
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

   public static class UnmodifiableFunction<V> extends AbstractByte2ObjectFunction<V> implements Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Byte2ObjectFunction<? extends V> function;

      protected UnmodifiableFunction(Byte2ObjectFunction<? extends V> f) {
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
      public boolean containsKey(byte k) {
         return this.function.containsKey(k);
      }

      @Override
      public V put(byte k, V v) {
         throw new UnsupportedOperationException();
      }

      @Override
      public V get(byte k) {
         return (V)this.function.get(k);
      }

      @Override
      public V getOrDefault(byte k, V defaultValue) {
         return (V)this.function.getOrDefault(k, defaultValue);
      }

      @Override
      public V remove(byte k) {
         throw new UnsupportedOperationException();
      }

      @Override
      public void clear() {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      @Override
      public V put(Byte k, V v) {
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
