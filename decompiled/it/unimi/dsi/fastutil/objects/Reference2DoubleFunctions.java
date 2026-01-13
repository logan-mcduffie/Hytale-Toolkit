package it.unimi.dsi.fastutil.objects;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.ToDoubleFunction;

public final class Reference2DoubleFunctions {
   public static final Reference2DoubleFunctions.EmptyFunction EMPTY_FUNCTION = new Reference2DoubleFunctions.EmptyFunction();

   private Reference2DoubleFunctions() {
   }

   public static <K> Reference2DoubleFunction<K> singleton(K key, double value) {
      return new Reference2DoubleFunctions.Singleton<>(key, value);
   }

   public static <K> Reference2DoubleFunction<K> singleton(K key, Double value) {
      return new Reference2DoubleFunctions.Singleton<>(key, value);
   }

   public static <K> Reference2DoubleFunction<K> synchronize(Reference2DoubleFunction<K> f) {
      return new Reference2DoubleFunctions.SynchronizedFunction<>(f);
   }

   public static <K> Reference2DoubleFunction<K> synchronize(Reference2DoubleFunction<K> f, Object sync) {
      return new Reference2DoubleFunctions.SynchronizedFunction<>(f, sync);
   }

   public static <K> Reference2DoubleFunction<K> unmodifiable(Reference2DoubleFunction<? extends K> f) {
      return new Reference2DoubleFunctions.UnmodifiableFunction<>(f);
   }

   public static <K> Reference2DoubleFunction<K> primitive(Function<? super K, ? extends Double> f) {
      Objects.requireNonNull(f);
      if (f instanceof Reference2DoubleFunction) {
         return (Reference2DoubleFunction<K>)f;
      } else {
         return (Reference2DoubleFunction<K>)(f instanceof ToDoubleFunction
            ? key -> ((ToDoubleFunction)f).applyAsDouble((K)key)
            : new Reference2DoubleFunctions.PrimitiveFunction<>(f));
      }
   }

   public static class EmptyFunction<K> extends AbstractReference2DoubleFunction<K> implements Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;

      protected EmptyFunction() {
      }

      @Override
      public double getDouble(Object k) {
         return 0.0;
      }

      @Override
      public double getOrDefault(Object k, double defaultValue) {
         return defaultValue;
      }

      @Override
      public boolean containsKey(Object k) {
         return false;
      }

      @Override
      public double defaultReturnValue() {
         return 0.0;
      }

      @Override
      public void defaultReturnValue(double defRetValue) {
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
         return Reference2DoubleFunctions.EMPTY_FUNCTION;
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
         return Reference2DoubleFunctions.EMPTY_FUNCTION;
      }
   }

   public static class PrimitiveFunction<K> implements Reference2DoubleFunction<K> {
      protected final Function<? super K, ? extends Double> function;

      protected PrimitiveFunction(Function<? super K, ? extends Double> function) {
         this.function = function;
      }

      @Override
      public boolean containsKey(Object key) {
         return this.function.apply((K)key) != null;
      }

      @Override
      public double getDouble(Object key) {
         Double v = this.function.apply((K)key);
         return v == null ? this.defaultReturnValue() : v;
      }

      @Override
      public double getOrDefault(Object key, double defaultValue) {
         Double v = this.function.apply((K)key);
         return v == null ? defaultValue : v;
      }

      @Deprecated
      @Override
      public Double get(Object key) {
         return this.function.apply((K)key);
      }

      @Deprecated
      @Override
      public Double getOrDefault(Object key, Double defaultValue) {
         Double v;
         return (v = this.function.apply((K)key)) == null ? defaultValue : v;
      }

      @Deprecated
      @Override
      public Double put(K key, Double value) {
         throw new UnsupportedOperationException();
      }
   }

   public static class Singleton<K> extends AbstractReference2DoubleFunction<K> implements Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final K key;
      protected final double value;

      protected Singleton(K key, double value) {
         this.key = key;
         this.value = value;
      }

      @Override
      public boolean containsKey(Object k) {
         return this.key == k;
      }

      @Override
      public double getDouble(Object k) {
         return this.key == k ? this.value : this.defRetValue;
      }

      @Override
      public double getOrDefault(Object k, double defaultValue) {
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

   public static class SynchronizedFunction<K> implements Reference2DoubleFunction<K>, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Reference2DoubleFunction<K> function;
      protected final Object sync;

      protected SynchronizedFunction(Reference2DoubleFunction<K> f, Object sync) {
         if (f == null) {
            throw new NullPointerException();
         } else {
            this.function = f;
            this.sync = sync;
         }
      }

      protected SynchronizedFunction(Reference2DoubleFunction<K> f) {
         if (f == null) {
            throw new NullPointerException();
         } else {
            this.function = f;
            this.sync = this;
         }
      }

      @Override
      public double applyAsDouble(K operand) {
         synchronized (this.sync) {
            return this.function.applyAsDouble(operand);
         }
      }

      @Deprecated
      public Double apply(K key) {
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
      public double defaultReturnValue() {
         synchronized (this.sync) {
            return this.function.defaultReturnValue();
         }
      }

      @Override
      public void defaultReturnValue(double defRetValue) {
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
      public double put(K k, double v) {
         synchronized (this.sync) {
            return this.function.put(k, v);
         }
      }

      @Override
      public double getDouble(Object k) {
         synchronized (this.sync) {
            return this.function.getDouble(k);
         }
      }

      @Override
      public double getOrDefault(Object k, double defaultValue) {
         synchronized (this.sync) {
            return this.function.getOrDefault(k, defaultValue);
         }
      }

      @Override
      public double removeDouble(Object k) {
         synchronized (this.sync) {
            return this.function.removeDouble(k);
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
      public Double put(K k, Double v) {
         synchronized (this.sync) {
            return this.function.put(k, v);
         }
      }

      @Deprecated
      @Override
      public Double get(Object k) {
         synchronized (this.sync) {
            return this.function.get(k);
         }
      }

      @Deprecated
      @Override
      public Double getOrDefault(Object k, Double defaultValue) {
         synchronized (this.sync) {
            return this.function.getOrDefault(k, defaultValue);
         }
      }

      @Deprecated
      @Override
      public Double remove(Object k) {
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

   public static class UnmodifiableFunction<K> extends AbstractReference2DoubleFunction<K> implements Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Reference2DoubleFunction<? extends K> function;

      protected UnmodifiableFunction(Reference2DoubleFunction<? extends K> f) {
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
      public double defaultReturnValue() {
         return this.function.defaultReturnValue();
      }

      @Override
      public void defaultReturnValue(double defRetValue) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean containsKey(Object k) {
         return this.function.containsKey(k);
      }

      @Override
      public double put(K k, double v) {
         throw new UnsupportedOperationException();
      }

      @Override
      public double getDouble(Object k) {
         return this.function.getDouble(k);
      }

      @Override
      public double getOrDefault(Object k, double defaultValue) {
         return this.function.getOrDefault(k, defaultValue);
      }

      @Override
      public double removeDouble(Object k) {
         throw new UnsupportedOperationException();
      }

      @Override
      public void clear() {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      @Override
      public Double put(K k, Double v) {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      @Override
      public Double get(Object k) {
         return this.function.get(k);
      }

      @Deprecated
      @Override
      public Double getOrDefault(Object k, Double defaultValue) {
         return this.function.getOrDefault(k, defaultValue);
      }

      @Deprecated
      @Override
      public Double remove(Object k) {
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
