package it.unimi.dsi.fastutil.floats;

import it.unimi.dsi.fastutil.SafeMath;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Objects;
import java.util.function.DoubleUnaryOperator;
import java.util.function.Function;

public final class Float2FloatFunctions {
   public static final Float2FloatFunctions.EmptyFunction EMPTY_FUNCTION = new Float2FloatFunctions.EmptyFunction();

   private Float2FloatFunctions() {
   }

   public static Float2FloatFunction singleton(float key, float value) {
      return new Float2FloatFunctions.Singleton(key, value);
   }

   public static Float2FloatFunction singleton(Float key, Float value) {
      return new Float2FloatFunctions.Singleton(key, value);
   }

   public static Float2FloatFunction synchronize(Float2FloatFunction f) {
      return new Float2FloatFunctions.SynchronizedFunction(f);
   }

   public static Float2FloatFunction synchronize(Float2FloatFunction f, Object sync) {
      return new Float2FloatFunctions.SynchronizedFunction(f, sync);
   }

   public static Float2FloatFunction unmodifiable(Float2FloatFunction f) {
      return new Float2FloatFunctions.UnmodifiableFunction(f);
   }

   public static Float2FloatFunction primitive(Function<? super Float, ? extends Float> f) {
      Objects.requireNonNull(f);
      if (f instanceof Float2FloatFunction) {
         return (Float2FloatFunction)f;
      } else {
         return (Float2FloatFunction)(f instanceof DoubleUnaryOperator
            ? key -> SafeMath.safeDoubleToFloat(((DoubleUnaryOperator)f).applyAsDouble(key))
            : new Float2FloatFunctions.PrimitiveFunction(f));
      }
   }

   public static class EmptyFunction extends AbstractFloat2FloatFunction implements Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;

      protected EmptyFunction() {
      }

      @Override
      public float get(float k) {
         return 0.0F;
      }

      @Override
      public float getOrDefault(float k, float defaultValue) {
         return defaultValue;
      }

      @Override
      public boolean containsKey(float k) {
         return false;
      }

      @Override
      public float defaultReturnValue() {
         return 0.0F;
      }

      @Override
      public void defaultReturnValue(float defRetValue) {
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
         return Float2FloatFunctions.EMPTY_FUNCTION;
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
         return Float2FloatFunctions.EMPTY_FUNCTION;
      }
   }

   public static class PrimitiveFunction implements Float2FloatFunction {
      protected final Function<? super Float, ? extends Float> function;

      protected PrimitiveFunction(Function<? super Float, ? extends Float> function) {
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
      public float get(float key) {
         Float v = this.function.apply(key);
         return v == null ? this.defaultReturnValue() : v;
      }

      @Override
      public float getOrDefault(float key, float defaultValue) {
         Float v = this.function.apply(key);
         return v == null ? defaultValue : v;
      }

      @Deprecated
      @Override
      public Float get(Object key) {
         return key == null ? null : this.function.apply((Float)key);
      }

      @Deprecated
      @Override
      public Float getOrDefault(Object key, Float defaultValue) {
         if (key == null) {
            return defaultValue;
         } else {
            Float v;
            return (v = this.function.apply((Float)key)) == null ? defaultValue : v;
         }
      }

      @Deprecated
      @Override
      public Float put(Float key, Float value) {
         throw new UnsupportedOperationException();
      }
   }

   public static class Singleton extends AbstractFloat2FloatFunction implements Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final float key;
      protected final float value;

      protected Singleton(float key, float value) {
         this.key = key;
         this.value = value;
      }

      @Override
      public boolean containsKey(float k) {
         return Float.floatToIntBits(this.key) == Float.floatToIntBits(k);
      }

      @Override
      public float get(float k) {
         return Float.floatToIntBits(this.key) == Float.floatToIntBits(k) ? this.value : this.defRetValue;
      }

      @Override
      public float getOrDefault(float k, float defaultValue) {
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

   public static class SynchronizedFunction implements Float2FloatFunction, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Float2FloatFunction function;
      protected final Object sync;

      protected SynchronizedFunction(Float2FloatFunction f, Object sync) {
         if (f == null) {
            throw new NullPointerException();
         } else {
            this.function = f;
            this.sync = sync;
         }
      }

      protected SynchronizedFunction(Float2FloatFunction f) {
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
      public Float apply(Float key) {
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
      public float defaultReturnValue() {
         synchronized (this.sync) {
            return this.function.defaultReturnValue();
         }
      }

      @Override
      public void defaultReturnValue(float defRetValue) {
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
      public float put(float k, float v) {
         synchronized (this.sync) {
            return this.function.put(k, v);
         }
      }

      @Override
      public float get(float k) {
         synchronized (this.sync) {
            return this.function.get(k);
         }
      }

      @Override
      public float getOrDefault(float k, float defaultValue) {
         synchronized (this.sync) {
            return this.function.getOrDefault(k, defaultValue);
         }
      }

      @Override
      public float remove(float k) {
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
      public Float put(Float k, Float v) {
         synchronized (this.sync) {
            return this.function.put(k, v);
         }
      }

      @Deprecated
      @Override
      public Float get(Object k) {
         synchronized (this.sync) {
            return this.function.get(k);
         }
      }

      @Deprecated
      @Override
      public Float getOrDefault(Object k, Float defaultValue) {
         synchronized (this.sync) {
            return this.function.getOrDefault(k, defaultValue);
         }
      }

      @Deprecated
      @Override
      public Float remove(Object k) {
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

   public static class UnmodifiableFunction extends AbstractFloat2FloatFunction implements Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Float2FloatFunction function;

      protected UnmodifiableFunction(Float2FloatFunction f) {
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
      public float defaultReturnValue() {
         return this.function.defaultReturnValue();
      }

      @Override
      public void defaultReturnValue(float defRetValue) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean containsKey(float k) {
         return this.function.containsKey(k);
      }

      @Override
      public float put(float k, float v) {
         throw new UnsupportedOperationException();
      }

      @Override
      public float get(float k) {
         return this.function.get(k);
      }

      @Override
      public float getOrDefault(float k, float defaultValue) {
         return this.function.getOrDefault(k, defaultValue);
      }

      @Override
      public float remove(float k) {
         throw new UnsupportedOperationException();
      }

      @Override
      public void clear() {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      @Override
      public Float put(Float k, Float v) {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      @Override
      public Float get(Object k) {
         return this.function.get(k);
      }

      @Deprecated
      @Override
      public Float getOrDefault(Object k, Float defaultValue) {
         return this.function.getOrDefault(k, defaultValue);
      }

      @Deprecated
      @Override
      public Float remove(Object k) {
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
