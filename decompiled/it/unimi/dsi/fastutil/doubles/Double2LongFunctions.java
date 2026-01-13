package it.unimi.dsi.fastutil.doubles;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Objects;
import java.util.function.DoubleToLongFunction;
import java.util.function.Function;

public final class Double2LongFunctions {
   public static final Double2LongFunctions.EmptyFunction EMPTY_FUNCTION = new Double2LongFunctions.EmptyFunction();

   private Double2LongFunctions() {
   }

   public static Double2LongFunction singleton(double key, long value) {
      return new Double2LongFunctions.Singleton(key, value);
   }

   public static Double2LongFunction singleton(Double key, Long value) {
      return new Double2LongFunctions.Singleton(key, value);
   }

   public static Double2LongFunction synchronize(Double2LongFunction f) {
      return new Double2LongFunctions.SynchronizedFunction(f);
   }

   public static Double2LongFunction synchronize(Double2LongFunction f, Object sync) {
      return new Double2LongFunctions.SynchronizedFunction(f, sync);
   }

   public static Double2LongFunction unmodifiable(Double2LongFunction f) {
      return new Double2LongFunctions.UnmodifiableFunction(f);
   }

   public static Double2LongFunction primitive(Function<? super Double, ? extends Long> f) {
      Objects.requireNonNull(f);
      if (f instanceof Double2LongFunction) {
         return (Double2LongFunction)f;
      } else {
         return (Double2LongFunction)(f instanceof DoubleToLongFunction
            ? ((DoubleToLongFunction)f)::applyAsLong
            : new Double2LongFunctions.PrimitiveFunction(f));
      }
   }

   public static class EmptyFunction extends AbstractDouble2LongFunction implements Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;

      protected EmptyFunction() {
      }

      @Override
      public long get(double k) {
         return 0L;
      }

      @Override
      public long getOrDefault(double k, long defaultValue) {
         return defaultValue;
      }

      @Override
      public boolean containsKey(double k) {
         return false;
      }

      @Override
      public long defaultReturnValue() {
         return 0L;
      }

      @Override
      public void defaultReturnValue(long defRetValue) {
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
         return Double2LongFunctions.EMPTY_FUNCTION;
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
         return Double2LongFunctions.EMPTY_FUNCTION;
      }
   }

   public static class PrimitiveFunction implements Double2LongFunction {
      protected final Function<? super Double, ? extends Long> function;

      protected PrimitiveFunction(Function<? super Double, ? extends Long> function) {
         this.function = function;
      }

      @Override
      public boolean containsKey(double key) {
         return this.function.apply(key) != null;
      }

      @Deprecated
      @Override
      public boolean containsKey(Object key) {
         return key == null ? false : this.function.apply((Double)key) != null;
      }

      @Override
      public long get(double key) {
         Long v = this.function.apply(key);
         return v == null ? this.defaultReturnValue() : v;
      }

      @Override
      public long getOrDefault(double key, long defaultValue) {
         Long v = this.function.apply(key);
         return v == null ? defaultValue : v;
      }

      @Deprecated
      @Override
      public Long get(Object key) {
         return key == null ? null : this.function.apply((Double)key);
      }

      @Deprecated
      @Override
      public Long getOrDefault(Object key, Long defaultValue) {
         if (key == null) {
            return defaultValue;
         } else {
            Long v;
            return (v = this.function.apply((Double)key)) == null ? defaultValue : v;
         }
      }

      @Deprecated
      @Override
      public Long put(Double key, Long value) {
         throw new UnsupportedOperationException();
      }
   }

   public static class Singleton extends AbstractDouble2LongFunction implements Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final double key;
      protected final long value;

      protected Singleton(double key, long value) {
         this.key = key;
         this.value = value;
      }

      @Override
      public boolean containsKey(double k) {
         return Double.doubleToLongBits(this.key) == Double.doubleToLongBits(k);
      }

      @Override
      public long get(double k) {
         return Double.doubleToLongBits(this.key) == Double.doubleToLongBits(k) ? this.value : this.defRetValue;
      }

      @Override
      public long getOrDefault(double k, long defaultValue) {
         return Double.doubleToLongBits(this.key) == Double.doubleToLongBits(k) ? this.value : defaultValue;
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

   public static class SynchronizedFunction implements Double2LongFunction, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Double2LongFunction function;
      protected final Object sync;

      protected SynchronizedFunction(Double2LongFunction f, Object sync) {
         if (f == null) {
            throw new NullPointerException();
         } else {
            this.function = f;
            this.sync = sync;
         }
      }

      protected SynchronizedFunction(Double2LongFunction f) {
         if (f == null) {
            throw new NullPointerException();
         } else {
            this.function = f;
            this.sync = this;
         }
      }

      @Override
      public long applyAsLong(double operand) {
         synchronized (this.sync) {
            return this.function.applyAsLong(operand);
         }
      }

      @Deprecated
      public Long apply(Double key) {
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
      public long defaultReturnValue() {
         synchronized (this.sync) {
            return this.function.defaultReturnValue();
         }
      }

      @Override
      public void defaultReturnValue(long defRetValue) {
         synchronized (this.sync) {
            this.function.defaultReturnValue(defRetValue);
         }
      }

      @Override
      public boolean containsKey(double k) {
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
      public long put(double k, long v) {
         synchronized (this.sync) {
            return this.function.put(k, v);
         }
      }

      @Override
      public long get(double k) {
         synchronized (this.sync) {
            return this.function.get(k);
         }
      }

      @Override
      public long getOrDefault(double k, long defaultValue) {
         synchronized (this.sync) {
            return this.function.getOrDefault(k, defaultValue);
         }
      }

      @Override
      public long remove(double k) {
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
      public Long put(Double k, Long v) {
         synchronized (this.sync) {
            return this.function.put(k, v);
         }
      }

      @Deprecated
      @Override
      public Long get(Object k) {
         synchronized (this.sync) {
            return this.function.get(k);
         }
      }

      @Deprecated
      @Override
      public Long getOrDefault(Object k, Long defaultValue) {
         synchronized (this.sync) {
            return this.function.getOrDefault(k, defaultValue);
         }
      }

      @Deprecated
      @Override
      public Long remove(Object k) {
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

   public static class UnmodifiableFunction extends AbstractDouble2LongFunction implements Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Double2LongFunction function;

      protected UnmodifiableFunction(Double2LongFunction f) {
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
      public long defaultReturnValue() {
         return this.function.defaultReturnValue();
      }

      @Override
      public void defaultReturnValue(long defRetValue) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean containsKey(double k) {
         return this.function.containsKey(k);
      }

      @Override
      public long put(double k, long v) {
         throw new UnsupportedOperationException();
      }

      @Override
      public long get(double k) {
         return this.function.get(k);
      }

      @Override
      public long getOrDefault(double k, long defaultValue) {
         return this.function.getOrDefault(k, defaultValue);
      }

      @Override
      public long remove(double k) {
         throw new UnsupportedOperationException();
      }

      @Override
      public void clear() {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      @Override
      public Long put(Double k, Long v) {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      @Override
      public Long get(Object k) {
         return this.function.get(k);
      }

      @Deprecated
      @Override
      public Long getOrDefault(Object k, Long defaultValue) {
         return this.function.getOrDefault(k, defaultValue);
      }

      @Deprecated
      @Override
      public Long remove(Object k) {
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
