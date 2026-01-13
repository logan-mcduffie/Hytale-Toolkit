package it.unimi.dsi.fastutil.bytes;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.IntFunction;

public final class Byte2ReferenceFunctions {
   public static final Byte2ReferenceFunctions.EmptyFunction EMPTY_FUNCTION = new Byte2ReferenceFunctions.EmptyFunction();

   private Byte2ReferenceFunctions() {
   }

   public static <V> Byte2ReferenceFunction<V> singleton(byte key, V value) {
      return new Byte2ReferenceFunctions.Singleton<>(key, value);
   }

   public static <V> Byte2ReferenceFunction<V> singleton(Byte key, V value) {
      return new Byte2ReferenceFunctions.Singleton<>(key, value);
   }

   public static <V> Byte2ReferenceFunction<V> synchronize(Byte2ReferenceFunction<V> f) {
      return new Byte2ReferenceFunctions.SynchronizedFunction<>(f);
   }

   public static <V> Byte2ReferenceFunction<V> synchronize(Byte2ReferenceFunction<V> f, Object sync) {
      return new Byte2ReferenceFunctions.SynchronizedFunction<>(f, sync);
   }

   public static <V> Byte2ReferenceFunction<V> unmodifiable(Byte2ReferenceFunction<? extends V> f) {
      return new Byte2ReferenceFunctions.UnmodifiableFunction<>(f);
   }

   public static <V> Byte2ReferenceFunction<V> primitive(Function<? super Byte, ? extends V> f) {
      Objects.requireNonNull(f);
      if (f instanceof Byte2ReferenceFunction) {
         return (Byte2ReferenceFunction<V>)f;
      } else {
         return (Byte2ReferenceFunction<V>)(f instanceof IntFunction ? ((IntFunction)f)::apply : new Byte2ReferenceFunctions.PrimitiveFunction<>(f));
      }
   }

   public static class EmptyFunction<V> extends AbstractByte2ReferenceFunction<V> implements Serializable, Cloneable {
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
         return Byte2ReferenceFunctions.EMPTY_FUNCTION;
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
         return Byte2ReferenceFunctions.EMPTY_FUNCTION;
      }
   }

   public static class PrimitiveFunction<V> implements Byte2ReferenceFunction<V> {
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

   public static class Singleton<V> extends AbstractByte2ReferenceFunction<V> implements Serializable, Cloneable {
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

   public static class SynchronizedFunction<V> implements Byte2ReferenceFunction<V>, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Byte2ReferenceFunction<V> function;
      protected final Object sync;

      protected SynchronizedFunction(Byte2ReferenceFunction<V> f, Object sync) {
         if (f == null) {
            throw new NullPointerException();
         } else {
            this.function = f;
            this.sync = sync;
         }
      }

      protected SynchronizedFunction(Byte2ReferenceFunction<V> f) {
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

   public static class UnmodifiableFunction<V> extends AbstractByte2ReferenceFunction<V> implements Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Byte2ReferenceFunction<? extends V> function;

      protected UnmodifiableFunction(Byte2ReferenceFunction<? extends V> f) {
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
