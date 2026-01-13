package it.unimi.dsi.fastutil.floats;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Objects;
import java.util.function.DoubleUnaryOperator;
import java.util.function.Function;

public final class Float2DoubleFunctions {
   public static final Float2DoubleFunctions.EmptyFunction EMPTY_FUNCTION = new Float2DoubleFunctions.EmptyFunction();

   private Float2DoubleFunctions() {
   }

   public static Float2DoubleFunction singleton(float key, double value) {
      return new Float2DoubleFunctions.Singleton(key, value);
   }

   public static Float2DoubleFunction singleton(Float key, Double value) {
      return new Float2DoubleFunctions.Singleton(key, value);
   }

   public static Float2DoubleFunction synchronize(Float2DoubleFunction f) {
      return new Float2DoubleFunctions.SynchronizedFunction(f);
   }

   public static Float2DoubleFunction synchronize(Float2DoubleFunction f, Object sync) {
      return new Float2DoubleFunctions.SynchronizedFunction(f, sync);
   }

   public static Float2DoubleFunction unmodifiable(Float2DoubleFunction f) {
      return new Float2DoubleFunctions.UnmodifiableFunction(f);
   }

   public static Float2DoubleFunction primitive(Function<? super Float, ? extends Double> f) {
      Objects.requireNonNull(f);
      if (f instanceof Float2DoubleFunction) {
         return (Float2DoubleFunction)f;
      } else {
         return (Float2DoubleFunction)(f instanceof DoubleUnaryOperator
            ? ((DoubleUnaryOperator)f)::applyAsDouble
            : new Float2DoubleFunctions.PrimitiveFunction(f));
      }
   }

   public static class EmptyFunction extends AbstractFloat2DoubleFunction implements Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;

      protected EmptyFunction() {
      }

      @Override
      public double get(float k) {
         return 0.0;
      }

      @Override
      public double getOrDefault(float k, double defaultValue) {
         return defaultValue;
      }

      @Override
      public boolean containsKey(float k) {
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
         return Float2DoubleFunctions.EMPTY_FUNCTION;
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
         return Float2DoubleFunctions.EMPTY_FUNCTION;
      }
   }

   public static class PrimitiveFunction implements Float2DoubleFunction {
      protected final Function<? super Float, ? extends Double> function;

      protected PrimitiveFunction(Function<? super Float, ? extends Double> function) {
         this.function = function;
      }

      @Override
      public boolean containsKey(float key) {
         return this.function.apply(key) != null;
      }

      @Deprecated
      @Override
      public boolean containsKey(Object key) {
         return key == null ? false : this.function.apply((Float)key) != null;
      }

      @Override
      public double get(float key) {
         Double v = this.function.apply(key);
         return v == null ? this.defaultReturnValue() : v;
      }

      @Override
      public double getOrDefault(float key, double defaultValue) {
         Double v = this.function.apply(key);
         return v == null ? defaultValue : v;
      }

      @Deprecated
      @Override
      public Double get(Object key) {
         return key == null ? null : this.function.apply((Float)key);
      }

      @Deprecated
      @Override
      public Double getOrDefault(Object key, Double defaultValue) {
         if (key == null) {
            return defaultValue;
         } else {
            Double v;
            return (v = this.function.apply((Float)key)) == null ? defaultValue : v;
         }
      }

      @Deprecated
      @Override
      public Double put(Float key, Double value) {
         throw new UnsupportedOperationException();
      }
   }

   public static class Singleton extends AbstractFloat2DoubleFunction implements Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final float key;
      protected final double value;

      protected Singleton(float key, double value) {
         this.key = key;
         this.value = value;
      }

      @Override
      public boolean containsKey(float k) {
         return Float.floatToIntBits(this.key) == Float.floatToIntBits(k);
      }

      @Override
      public double get(float k) {
         return Float.floatToIntBits(this.key) == Float.floatToIntBits(k) ? this.value : this.defRetValue;
      }

      @Override
      public double getOrDefault(float k, double defaultValue) {
         return Float.floatToIntBits(this.key) == Float.floatToIntBits(k) ? this.value : defaultValue;
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

   public static class SynchronizedFunction implements Float2DoubleFunction, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Float2DoubleFunction function;
      protected final Object sync;

      protected SynchronizedFunction(Float2DoubleFunction f, Object sync) {
         if (f == null) {
            throw new NullPointerException();
         } else {
            this.function = f;
            this.sync = sync;
         }
      }

      protected SynchronizedFunction(Float2DoubleFunction f) {
         if (f == null) {
            throw new NullPointerException();
         } else {
            this.function = f;
            this.sync = this;
         }
      }

      @Deprecated
      @Override
      public double applyAsDouble(double operand) {
         synchronized (this.sync) {
            return this.function.applyAsDouble(operand);
         }
      }

      @Deprecated
      public Double apply(Float key) {
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
      public boolean containsKey(float k) {
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
      public double put(float k, double v) {
         synchronized (this.sync) {
            return this.function.put(k, v);
         }
      }

      @Override
      public double get(float k) {
         synchronized (this.sync) {
            return this.function.get(k);
         }
      }

      @Override
      public double getOrDefault(float k, double defaultValue) {
         synchronized (this.sync) {
            return this.function.getOrDefault(k, defaultValue);
         }
      }

      @Override
      public double remove(float k) {
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
      public Double put(Float k, Double v) {
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

   public static class UnmodifiableFunction extends AbstractFloat2DoubleFunction implements Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Float2DoubleFunction function;

      protected UnmodifiableFunction(Float2DoubleFunction f) {
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
      public boolean containsKey(float k) {
         return this.function.containsKey(k);
      }

      @Override
      public double put(float k, double v) {
         throw new UnsupportedOperationException();
      }

      @Override
      public double get(float k) {
         return this.function.get(k);
      }

      @Override
      public double getOrDefault(float k, double defaultValue) {
         return this.function.getOrDefault(k, defaultValue);
      }

      @Override
      public double remove(float k) {
         throw new UnsupportedOperationException();
      }

      @Override
      public void clear() {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      @Override
      public Double put(Float k, Double v) {
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
