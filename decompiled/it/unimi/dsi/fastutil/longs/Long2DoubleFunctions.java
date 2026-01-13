package it.unimi.dsi.fastutil.longs;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.LongToDoubleFunction;

public final class Long2DoubleFunctions {
   public static final Long2DoubleFunctions.EmptyFunction EMPTY_FUNCTION = new Long2DoubleFunctions.EmptyFunction();

   private Long2DoubleFunctions() {
   }

   public static Long2DoubleFunction singleton(long key, double value) {
      return new Long2DoubleFunctions.Singleton(key, value);
   }

   public static Long2DoubleFunction singleton(Long key, Double value) {
      return new Long2DoubleFunctions.Singleton(key, value);
   }

   public static Long2DoubleFunction synchronize(Long2DoubleFunction f) {
      return new Long2DoubleFunctions.SynchronizedFunction(f);
   }

   public static Long2DoubleFunction synchronize(Long2DoubleFunction f, Object sync) {
      return new Long2DoubleFunctions.SynchronizedFunction(f, sync);
   }

   public static Long2DoubleFunction unmodifiable(Long2DoubleFunction f) {
      return new Long2DoubleFunctions.UnmodifiableFunction(f);
   }

   public static Long2DoubleFunction primitive(Function<? super Long, ? extends Double> f) {
      Objects.requireNonNull(f);
      if (f instanceof Long2DoubleFunction) {
         return (Long2DoubleFunction)f;
      } else {
         return (Long2DoubleFunction)(f instanceof LongToDoubleFunction
            ? ((LongToDoubleFunction)f)::applyAsDouble
            : new Long2DoubleFunctions.PrimitiveFunction(f));
      }
   }

   public static class EmptyFunction extends AbstractLong2DoubleFunction implements Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;

      protected EmptyFunction() {
      }

      @Override
      public double get(long k) {
         return 0.0;
      }

      @Override
      public double getOrDefault(long k, double defaultValue) {
         return defaultValue;
      }

      @Override
      public boolean containsKey(long k) {
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
         return Long2DoubleFunctions.EMPTY_FUNCTION;
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
         return Long2DoubleFunctions.EMPTY_FUNCTION;
      }
   }

   public static class PrimitiveFunction implements Long2DoubleFunction {
      protected final Function<? super Long, ? extends Double> function;

      protected PrimitiveFunction(Function<? super Long, ? extends Double> function) {
         this.function = function;
      }

      @Override
      public boolean containsKey(long key) {
         return this.function.apply(key) != null;
      }

      @Deprecated
      @Override
      public boolean containsKey(Object key) {
         return key == null ? false : this.function.apply((Long)key) != null;
      }

      @Override
      public double get(long key) {
         Double v = this.function.apply(key);
         return v == null ? this.defaultReturnValue() : v;
      }

      @Override
      public double getOrDefault(long key, double defaultValue) {
         Double v = this.function.apply(key);
         return v == null ? defaultValue : v;
      }

      @Deprecated
      @Override
      public Double get(Object key) {
         return key == null ? null : this.function.apply((Long)key);
      }

      @Deprecated
      @Override
      public Double getOrDefault(Object key, Double defaultValue) {
         if (key == null) {
            return defaultValue;
         } else {
            Double v;
            return (v = this.function.apply((Long)key)) == null ? defaultValue : v;
         }
      }

      @Deprecated
      @Override
      public Double put(Long key, Double value) {
         throw new UnsupportedOperationException();
      }
   }

   public static class Singleton extends AbstractLong2DoubleFunction implements Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final long key;
      protected final double value;

      protected Singleton(long key, double value) {
         this.key = key;
         this.value = value;
      }

      @Override
      public boolean containsKey(long k) {
         return this.key == k;
      }

      @Override
      public double get(long k) {
         return this.key == k ? this.value : this.defRetValue;
      }

      @Override
      public double getOrDefault(long k, double defaultValue) {
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

   public static class SynchronizedFunction implements Long2DoubleFunction, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Long2DoubleFunction function;
      protected final Object sync;

      protected SynchronizedFunction(Long2DoubleFunction f, Object sync) {
         if (f == null) {
            throw new NullPointerException();
         } else {
            this.function = f;
            this.sync = sync;
         }
      }

      protected SynchronizedFunction(Long2DoubleFunction f) {
         if (f == null) {
            throw new NullPointerException();
         } else {
            this.function = f;
            this.sync = this;
         }
      }

      @Override
      public double applyAsDouble(long operand) {
         synchronized (this.sync) {
            return this.function.applyAsDouble(operand);
         }
      }

      @Deprecated
      public Double apply(Long key) {
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
      public boolean containsKey(long k) {
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
      public double put(long k, double v) {
         synchronized (this.sync) {
            return this.function.put(k, v);
         }
      }

      @Override
      public double get(long k) {
         synchronized (this.sync) {
            return this.function.get(k);
         }
      }

      @Override
      public double getOrDefault(long k, double defaultValue) {
         synchronized (this.sync) {
            return this.function.getOrDefault(k, defaultValue);
         }
      }

      @Override
      public double remove(long k) {
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
      public Double put(Long k, Double v) {
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

   public static class UnmodifiableFunction extends AbstractLong2DoubleFunction implements Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Long2DoubleFunction function;

      protected UnmodifiableFunction(Long2DoubleFunction f) {
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
      public boolean containsKey(long k) {
         return this.function.containsKey(k);
      }

      @Override
      public double put(long k, double v) {
         throw new UnsupportedOperationException();
      }

      @Override
      public double get(long k) {
         return this.function.get(k);
      }

      @Override
      public double getOrDefault(long k, double defaultValue) {
         return this.function.getOrDefault(k, defaultValue);
      }

      @Override
      public double remove(long k) {
         throw new UnsupportedOperationException();
      }

      @Override
      public void clear() {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      @Override
      public Double put(Long k, Double v) {
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
