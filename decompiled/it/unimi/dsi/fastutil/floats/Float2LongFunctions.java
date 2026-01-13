package it.unimi.dsi.fastutil.floats;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Objects;
import java.util.function.DoubleToLongFunction;
import java.util.function.Function;

public final class Float2LongFunctions {
   public static final Float2LongFunctions.EmptyFunction EMPTY_FUNCTION = new Float2LongFunctions.EmptyFunction();

   private Float2LongFunctions() {
   }

   public static Float2LongFunction singleton(float key, long value) {
      return new Float2LongFunctions.Singleton(key, value);
   }

   public static Float2LongFunction singleton(Float key, Long value) {
      return new Float2LongFunctions.Singleton(key, value);
   }

   public static Float2LongFunction synchronize(Float2LongFunction f) {
      return new Float2LongFunctions.SynchronizedFunction(f);
   }

   public static Float2LongFunction synchronize(Float2LongFunction f, Object sync) {
      return new Float2LongFunctions.SynchronizedFunction(f, sync);
   }

   public static Float2LongFunction unmodifiable(Float2LongFunction f) {
      return new Float2LongFunctions.UnmodifiableFunction(f);
   }

   public static Float2LongFunction primitive(Function<? super Float, ? extends Long> f) {
      Objects.requireNonNull(f);
      if (f instanceof Float2LongFunction) {
         return (Float2LongFunction)f;
      } else {
         return (Float2LongFunction)(f instanceof DoubleToLongFunction ? ((DoubleToLongFunction)f)::applyAsLong : new Float2LongFunctions.PrimitiveFunction(f));
      }
   }

   public static class EmptyFunction extends AbstractFloat2LongFunction implements Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;

      protected EmptyFunction() {
      }

      @Override
      public long get(float k) {
         return 0L;
      }

      @Override
      public long getOrDefault(float k, long defaultValue) {
         return defaultValue;
      }

      @Override
      public boolean containsKey(float k) {
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
         return Float2LongFunctions.EMPTY_FUNCTION;
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
         return Float2LongFunctions.EMPTY_FUNCTION;
      }
   }

   public static class PrimitiveFunction implements Float2LongFunction {
      protected final Function<? super Float, ? extends Long> function;

      protected PrimitiveFunction(Function<? super Float, ? extends Long> function) {
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
      public long get(float key) {
         Long v = this.function.apply(key);
         return v == null ? this.defaultReturnValue() : v;
      }

      @Override
      public long getOrDefault(float key, long defaultValue) {
         Long v = this.function.apply(key);
         return v == null ? defaultValue : v;
      }

      @Deprecated
      @Override
      public Long get(Object key) {
         return key == null ? null : this.function.apply((Float)key);
      }

      @Deprecated
      @Override
      public Long getOrDefault(Object key, Long defaultValue) {
         if (key == null) {
            return defaultValue;
         } else {
            Long v;
            return (v = this.function.apply((Float)key)) == null ? defaultValue : v;
         }
      }

      @Deprecated
      @Override
      public Long put(Float key, Long value) {
         throw new UnsupportedOperationException();
      }
   }

   public static class Singleton extends AbstractFloat2LongFunction implements Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final float key;
      protected final long value;

      protected Singleton(float key, long value) {
         this.key = key;
         this.value = value;
      }

      @Override
      public boolean containsKey(float k) {
         return Float.floatToIntBits(this.key) == Float.floatToIntBits(k);
      }

      @Override
      public long get(float k) {
         return Float.floatToIntBits(this.key) == Float.floatToIntBits(k) ? this.value : this.defRetValue;
      }

      @Override
      public long getOrDefault(float k, long defaultValue) {
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

   public static class SynchronizedFunction implements Float2LongFunction, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Float2LongFunction function;
      protected final Object sync;

      protected SynchronizedFunction(Float2LongFunction f, Object sync) {
         if (f == null) {
            throw new NullPointerException();
         } else {
            this.function = f;
            this.sync = sync;
         }
      }

      protected SynchronizedFunction(Float2LongFunction f) {
         if (f == null) {
            throw new NullPointerException();
         } else {
            this.function = f;
            this.sync = this;
         }
      }

      @Deprecated
      @Override
      public long applyAsLong(double operand) {
         synchronized (this.sync) {
            return this.function.applyAsLong(operand);
         }
      }

      @Deprecated
      public Long apply(Float key) {
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
      public long put(float k, long v) {
         synchronized (this.sync) {
            return this.function.put(k, v);
         }
      }

      @Override
      public long get(float k) {
         synchronized (this.sync) {
            return this.function.get(k);
         }
      }

      @Override
      public long getOrDefault(float k, long defaultValue) {
         synchronized (this.sync) {
            return this.function.getOrDefault(k, defaultValue);
         }
      }

      @Override
      public long remove(float k) {
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
      public Long put(Float k, Long v) {
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

   public static class UnmodifiableFunction extends AbstractFloat2LongFunction implements Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Float2LongFunction function;

      protected UnmodifiableFunction(Float2LongFunction f) {
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
      public boolean containsKey(float k) {
         return this.function.containsKey(k);
      }

      @Override
      public long put(float k, long v) {
         throw new UnsupportedOperationException();
      }

      @Override
      public long get(float k) {
         return this.function.get(k);
      }

      @Override
      public long getOrDefault(float k, long defaultValue) {
         return this.function.getOrDefault(k, defaultValue);
      }

      @Override
      public long remove(float k) {
         throw new UnsupportedOperationException();
      }

      @Override
      public void clear() {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      @Override
      public Long put(Float k, Long v) {
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
