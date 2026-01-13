package it.unimi.dsi.fastutil.doubles;

import it.unimi.dsi.fastutil.SafeMath;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Objects;
import java.util.function.DoubleToIntFunction;
import java.util.function.Function;

public final class Double2ShortFunctions {
   public static final Double2ShortFunctions.EmptyFunction EMPTY_FUNCTION = new Double2ShortFunctions.EmptyFunction();

   private Double2ShortFunctions() {
   }

   public static Double2ShortFunction singleton(double key, short value) {
      return new Double2ShortFunctions.Singleton(key, value);
   }

   public static Double2ShortFunction singleton(Double key, Short value) {
      return new Double2ShortFunctions.Singleton(key, value);
   }

   public static Double2ShortFunction synchronize(Double2ShortFunction f) {
      return new Double2ShortFunctions.SynchronizedFunction(f);
   }

   public static Double2ShortFunction synchronize(Double2ShortFunction f, Object sync) {
      return new Double2ShortFunctions.SynchronizedFunction(f, sync);
   }

   public static Double2ShortFunction unmodifiable(Double2ShortFunction f) {
      return new Double2ShortFunctions.UnmodifiableFunction(f);
   }

   public static Double2ShortFunction primitive(Function<? super Double, ? extends Short> f) {
      Objects.requireNonNull(f);
      if (f instanceof Double2ShortFunction) {
         return (Double2ShortFunction)f;
      } else {
         return (Double2ShortFunction)(f instanceof DoubleToIntFunction
            ? key -> SafeMath.safeIntToShort(((DoubleToIntFunction)f).applyAsInt(key))
            : new Double2ShortFunctions.PrimitiveFunction(f));
      }
   }

   public static class EmptyFunction extends AbstractDouble2ShortFunction implements Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;

      protected EmptyFunction() {
      }

      @Override
      public short get(double k) {
         return 0;
      }

      @Override
      public short getOrDefault(double k, short defaultValue) {
         return defaultValue;
      }

      @Override
      public boolean containsKey(double k) {
         return false;
      }

      @Override
      public short defaultReturnValue() {
         return 0;
      }

      @Override
      public void defaultReturnValue(short defRetValue) {
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
         return Double2ShortFunctions.EMPTY_FUNCTION;
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
         return Double2ShortFunctions.EMPTY_FUNCTION;
      }
   }

   public static class PrimitiveFunction implements Double2ShortFunction {
      protected final Function<? super Double, ? extends Short> function;

      protected PrimitiveFunction(Function<? super Double, ? extends Short> function) {
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
      public short get(double key) {
         Short v = this.function.apply(key);
         return v == null ? this.defaultReturnValue() : v;
      }

      @Override
      public short getOrDefault(double key, short defaultValue) {
         Short v = this.function.apply(key);
         return v == null ? defaultValue : v;
      }

      @Deprecated
      @Override
      public Short get(Object key) {
         return key == null ? null : this.function.apply((Double)key);
      }

      @Deprecated
      @Override
      public Short getOrDefault(Object key, Short defaultValue) {
         if (key == null) {
            return defaultValue;
         } else {
            Short v;
            return (v = this.function.apply((Double)key)) == null ? defaultValue : v;
         }
      }

      @Deprecated
      @Override
      public Short put(Double key, Short value) {
         throw new UnsupportedOperationException();
      }
   }

   public static class Singleton extends AbstractDouble2ShortFunction implements Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final double key;
      protected final short value;

      protected Singleton(double key, short value) {
         this.key = key;
         this.value = value;
      }

      @Override
      public boolean containsKey(double k) {
         return Double.doubleToLongBits(this.key) == Double.doubleToLongBits(k);
      }

      @Override
      public short get(double k) {
         return Double.doubleToLongBits(this.key) == Double.doubleToLongBits(k) ? this.value : this.defRetValue;
      }

      @Override
      public short getOrDefault(double k, short defaultValue) {
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

   public static class SynchronizedFunction implements Double2ShortFunction, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Double2ShortFunction function;
      protected final Object sync;

      protected SynchronizedFunction(Double2ShortFunction f, Object sync) {
         if (f == null) {
            throw new NullPointerException();
         } else {
            this.function = f;
            this.sync = sync;
         }
      }

      protected SynchronizedFunction(Double2ShortFunction f) {
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
      public Short apply(Double key) {
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
      public short defaultReturnValue() {
         synchronized (this.sync) {
            return this.function.defaultReturnValue();
         }
      }

      @Override
      public void defaultReturnValue(short defRetValue) {
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
      public short put(double k, short v) {
         synchronized (this.sync) {
            return this.function.put(k, v);
         }
      }

      @Override
      public short get(double k) {
         synchronized (this.sync) {
            return this.function.get(k);
         }
      }

      @Override
      public short getOrDefault(double k, short defaultValue) {
         synchronized (this.sync) {
            return this.function.getOrDefault(k, defaultValue);
         }
      }

      @Override
      public short remove(double k) {
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
      public Short put(Double k, Short v) {
         synchronized (this.sync) {
            return this.function.put(k, v);
         }
      }

      @Deprecated
      @Override
      public Short get(Object k) {
         synchronized (this.sync) {
            return this.function.get(k);
         }
      }

      @Deprecated
      @Override
      public Short getOrDefault(Object k, Short defaultValue) {
         synchronized (this.sync) {
            return this.function.getOrDefault(k, defaultValue);
         }
      }

      @Deprecated
      @Override
      public Short remove(Object k) {
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

   public static class UnmodifiableFunction extends AbstractDouble2ShortFunction implements Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Double2ShortFunction function;

      protected UnmodifiableFunction(Double2ShortFunction f) {
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
      public short defaultReturnValue() {
         return this.function.defaultReturnValue();
      }

      @Override
      public void defaultReturnValue(short defRetValue) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean containsKey(double k) {
         return this.function.containsKey(k);
      }

      @Override
      public short put(double k, short v) {
         throw new UnsupportedOperationException();
      }

      @Override
      public short get(double k) {
         return this.function.get(k);
      }

      @Override
      public short getOrDefault(double k, short defaultValue) {
         return this.function.getOrDefault(k, defaultValue);
      }

      @Override
      public short remove(double k) {
         throw new UnsupportedOperationException();
      }

      @Override
      public void clear() {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      @Override
      public Short put(Double k, Short v) {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      @Override
      public Short get(Object k) {
         return this.function.get(k);
      }

      @Deprecated
      @Override
      public Short getOrDefault(Object k, Short defaultValue) {
         return this.function.getOrDefault(k, defaultValue);
      }

      @Deprecated
      @Override
      public Short remove(Object k) {
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
