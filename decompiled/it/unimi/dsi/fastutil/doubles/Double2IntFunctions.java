package it.unimi.dsi.fastutil.doubles;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Objects;
import java.util.function.DoubleToIntFunction;
import java.util.function.Function;

public final class Double2IntFunctions {
   public static final Double2IntFunctions.EmptyFunction EMPTY_FUNCTION = new Double2IntFunctions.EmptyFunction();

   private Double2IntFunctions() {
   }

   public static Double2IntFunction singleton(double key, int value) {
      return new Double2IntFunctions.Singleton(key, value);
   }

   public static Double2IntFunction singleton(Double key, Integer value) {
      return new Double2IntFunctions.Singleton(key, value);
   }

   public static Double2IntFunction synchronize(Double2IntFunction f) {
      return new Double2IntFunctions.SynchronizedFunction(f);
   }

   public static Double2IntFunction synchronize(Double2IntFunction f, Object sync) {
      return new Double2IntFunctions.SynchronizedFunction(f, sync);
   }

   public static Double2IntFunction unmodifiable(Double2IntFunction f) {
      return new Double2IntFunctions.UnmodifiableFunction(f);
   }

   public static Double2IntFunction primitive(Function<? super Double, ? extends Integer> f) {
      Objects.requireNonNull(f);
      if (f instanceof Double2IntFunction) {
         return (Double2IntFunction)f;
      } else {
         return (Double2IntFunction)(f instanceof DoubleToIntFunction ? ((DoubleToIntFunction)f)::applyAsInt : new Double2IntFunctions.PrimitiveFunction(f));
      }
   }

   public static class EmptyFunction extends AbstractDouble2IntFunction implements Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;

      protected EmptyFunction() {
      }

      @Override
      public int get(double k) {
         return 0;
      }

      @Override
      public int getOrDefault(double k, int defaultValue) {
         return defaultValue;
      }

      @Override
      public boolean containsKey(double k) {
         return false;
      }

      @Override
      public int defaultReturnValue() {
         return 0;
      }

      @Override
      public void defaultReturnValue(int defRetValue) {
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
         return Double2IntFunctions.EMPTY_FUNCTION;
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
         return Double2IntFunctions.EMPTY_FUNCTION;
      }
   }

   public static class PrimitiveFunction implements Double2IntFunction {
      protected final Function<? super Double, ? extends Integer> function;

      protected PrimitiveFunction(Function<? super Double, ? extends Integer> function) {
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
      public int get(double key) {
         Integer v = this.function.apply(key);
         return v == null ? this.defaultReturnValue() : v;
      }

      @Override
      public int getOrDefault(double key, int defaultValue) {
         Integer v = this.function.apply(key);
         return v == null ? defaultValue : v;
      }

      @Deprecated
      @Override
      public Integer get(Object key) {
         return key == null ? null : this.function.apply((Double)key);
      }

      @Deprecated
      @Override
      public Integer getOrDefault(Object key, Integer defaultValue) {
         if (key == null) {
            return defaultValue;
         } else {
            Integer v;
            return (v = this.function.apply((Double)key)) == null ? defaultValue : v;
         }
      }

      @Deprecated
      @Override
      public Integer put(Double key, Integer value) {
         throw new UnsupportedOperationException();
      }
   }

   public static class Singleton extends AbstractDouble2IntFunction implements Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final double key;
      protected final int value;

      protected Singleton(double key, int value) {
         this.key = key;
         this.value = value;
      }

      @Override
      public boolean containsKey(double k) {
         return Double.doubleToLongBits(this.key) == Double.doubleToLongBits(k);
      }

      @Override
      public int get(double k) {
         return Double.doubleToLongBits(this.key) == Double.doubleToLongBits(k) ? this.value : this.defRetValue;
      }

      @Override
      public int getOrDefault(double k, int defaultValue) {
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

   public static class SynchronizedFunction implements Double2IntFunction, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Double2IntFunction function;
      protected final Object sync;

      protected SynchronizedFunction(Double2IntFunction f, Object sync) {
         if (f == null) {
            throw new NullPointerException();
         } else {
            this.function = f;
            this.sync = sync;
         }
      }

      protected SynchronizedFunction(Double2IntFunction f) {
         if (f == null) {
            throw new NullPointerException();
         } else {
            this.function = f;
            this.sync = this;
         }
      }

      @Override
      public int applyAsInt(double operand) {
         synchronized (this.sync) {
            return this.function.applyAsInt(operand);
         }
      }

      @Deprecated
      public Integer apply(Double key) {
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
      public int defaultReturnValue() {
         synchronized (this.sync) {
            return this.function.defaultReturnValue();
         }
      }

      @Override
      public void defaultReturnValue(int defRetValue) {
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
      public int put(double k, int v) {
         synchronized (this.sync) {
            return this.function.put(k, v);
         }
      }

      @Override
      public int get(double k) {
         synchronized (this.sync) {
            return this.function.get(k);
         }
      }

      @Override
      public int getOrDefault(double k, int defaultValue) {
         synchronized (this.sync) {
            return this.function.getOrDefault(k, defaultValue);
         }
      }

      @Override
      public int remove(double k) {
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
      public Integer put(Double k, Integer v) {
         synchronized (this.sync) {
            return this.function.put(k, v);
         }
      }

      @Deprecated
      @Override
      public Integer get(Object k) {
         synchronized (this.sync) {
            return this.function.get(k);
         }
      }

      @Deprecated
      @Override
      public Integer getOrDefault(Object k, Integer defaultValue) {
         synchronized (this.sync) {
            return this.function.getOrDefault(k, defaultValue);
         }
      }

      @Deprecated
      @Override
      public Integer remove(Object k) {
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

   public static class UnmodifiableFunction extends AbstractDouble2IntFunction implements Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Double2IntFunction function;

      protected UnmodifiableFunction(Double2IntFunction f) {
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
      public int defaultReturnValue() {
         return this.function.defaultReturnValue();
      }

      @Override
      public void defaultReturnValue(int defRetValue) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean containsKey(double k) {
         return this.function.containsKey(k);
      }

      @Override
      public int put(double k, int v) {
         throw new UnsupportedOperationException();
      }

      @Override
      public int get(double k) {
         return this.function.get(k);
      }

      @Override
      public int getOrDefault(double k, int defaultValue) {
         return this.function.getOrDefault(k, defaultValue);
      }

      @Override
      public int remove(double k) {
         throw new UnsupportedOperationException();
      }

      @Override
      public void clear() {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      @Override
      public Integer put(Double k, Integer v) {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      @Override
      public Integer get(Object k) {
         return this.function.get(k);
      }

      @Deprecated
      @Override
      public Integer getOrDefault(Object k, Integer defaultValue) {
         return this.function.getOrDefault(k, defaultValue);
      }

      @Deprecated
      @Override
      public Integer remove(Object k) {
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
