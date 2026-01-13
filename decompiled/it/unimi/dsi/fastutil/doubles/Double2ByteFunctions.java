package it.unimi.dsi.fastutil.doubles;

import it.unimi.dsi.fastutil.SafeMath;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Objects;
import java.util.function.DoubleToIntFunction;
import java.util.function.Function;

public final class Double2ByteFunctions {
   public static final Double2ByteFunctions.EmptyFunction EMPTY_FUNCTION = new Double2ByteFunctions.EmptyFunction();

   private Double2ByteFunctions() {
   }

   public static Double2ByteFunction singleton(double key, byte value) {
      return new Double2ByteFunctions.Singleton(key, value);
   }

   public static Double2ByteFunction singleton(Double key, Byte value) {
      return new Double2ByteFunctions.Singleton(key, value);
   }

   public static Double2ByteFunction synchronize(Double2ByteFunction f) {
      return new Double2ByteFunctions.SynchronizedFunction(f);
   }

   public static Double2ByteFunction synchronize(Double2ByteFunction f, Object sync) {
      return new Double2ByteFunctions.SynchronizedFunction(f, sync);
   }

   public static Double2ByteFunction unmodifiable(Double2ByteFunction f) {
      return new Double2ByteFunctions.UnmodifiableFunction(f);
   }

   public static Double2ByteFunction primitive(Function<? super Double, ? extends Byte> f) {
      Objects.requireNonNull(f);
      if (f instanceof Double2ByteFunction) {
         return (Double2ByteFunction)f;
      } else {
         return (Double2ByteFunction)(f instanceof DoubleToIntFunction
            ? key -> SafeMath.safeIntToByte(((DoubleToIntFunction)f).applyAsInt(key))
            : new Double2ByteFunctions.PrimitiveFunction(f));
      }
   }

   public static class EmptyFunction extends AbstractDouble2ByteFunction implements Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;

      protected EmptyFunction() {
      }

      @Override
      public byte get(double k) {
         return 0;
      }

      @Override
      public byte getOrDefault(double k, byte defaultValue) {
         return defaultValue;
      }

      @Override
      public boolean containsKey(double k) {
         return false;
      }

      @Override
      public byte defaultReturnValue() {
         return 0;
      }

      @Override
      public void defaultReturnValue(byte defRetValue) {
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
         return Double2ByteFunctions.EMPTY_FUNCTION;
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
         return Double2ByteFunctions.EMPTY_FUNCTION;
      }
   }

   public static class PrimitiveFunction implements Double2ByteFunction {
      protected final Function<? super Double, ? extends Byte> function;

      protected PrimitiveFunction(Function<? super Double, ? extends Byte> function) {
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
      public byte get(double key) {
         Byte v = this.function.apply(key);
         return v == null ? this.defaultReturnValue() : v;
      }

      @Override
      public byte getOrDefault(double key, byte defaultValue) {
         Byte v = this.function.apply(key);
         return v == null ? defaultValue : v;
      }

      @Deprecated
      @Override
      public Byte get(Object key) {
         return key == null ? null : this.function.apply((Double)key);
      }

      @Deprecated
      @Override
      public Byte getOrDefault(Object key, Byte defaultValue) {
         if (key == null) {
            return defaultValue;
         } else {
            Byte v;
            return (v = this.function.apply((Double)key)) == null ? defaultValue : v;
         }
      }

      @Deprecated
      @Override
      public Byte put(Double key, Byte value) {
         throw new UnsupportedOperationException();
      }
   }

   public static class Singleton extends AbstractDouble2ByteFunction implements Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final double key;
      protected final byte value;

      protected Singleton(double key, byte value) {
         this.key = key;
         this.value = value;
      }

      @Override
      public boolean containsKey(double k) {
         return Double.doubleToLongBits(this.key) == Double.doubleToLongBits(k);
      }

      @Override
      public byte get(double k) {
         return Double.doubleToLongBits(this.key) == Double.doubleToLongBits(k) ? this.value : this.defRetValue;
      }

      @Override
      public byte getOrDefault(double k, byte defaultValue) {
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

   public static class SynchronizedFunction implements Double2ByteFunction, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Double2ByteFunction function;
      protected final Object sync;

      protected SynchronizedFunction(Double2ByteFunction f, Object sync) {
         if (f == null) {
            throw new NullPointerException();
         } else {
            this.function = f;
            this.sync = sync;
         }
      }

      protected SynchronizedFunction(Double2ByteFunction f) {
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
      public Byte apply(Double key) {
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
      public byte defaultReturnValue() {
         synchronized (this.sync) {
            return this.function.defaultReturnValue();
         }
      }

      @Override
      public void defaultReturnValue(byte defRetValue) {
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
      public byte put(double k, byte v) {
         synchronized (this.sync) {
            return this.function.put(k, v);
         }
      }

      @Override
      public byte get(double k) {
         synchronized (this.sync) {
            return this.function.get(k);
         }
      }

      @Override
      public byte getOrDefault(double k, byte defaultValue) {
         synchronized (this.sync) {
            return this.function.getOrDefault(k, defaultValue);
         }
      }

      @Override
      public byte remove(double k) {
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
      public Byte put(Double k, Byte v) {
         synchronized (this.sync) {
            return this.function.put(k, v);
         }
      }

      @Deprecated
      @Override
      public Byte get(Object k) {
         synchronized (this.sync) {
            return this.function.get(k);
         }
      }

      @Deprecated
      @Override
      public Byte getOrDefault(Object k, Byte defaultValue) {
         synchronized (this.sync) {
            return this.function.getOrDefault(k, defaultValue);
         }
      }

      @Deprecated
      @Override
      public Byte remove(Object k) {
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

   public static class UnmodifiableFunction extends AbstractDouble2ByteFunction implements Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Double2ByteFunction function;

      protected UnmodifiableFunction(Double2ByteFunction f) {
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
      public byte defaultReturnValue() {
         return this.function.defaultReturnValue();
      }

      @Override
      public void defaultReturnValue(byte defRetValue) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean containsKey(double k) {
         return this.function.containsKey(k);
      }

      @Override
      public byte put(double k, byte v) {
         throw new UnsupportedOperationException();
      }

      @Override
      public byte get(double k) {
         return this.function.get(k);
      }

      @Override
      public byte getOrDefault(double k, byte defaultValue) {
         return this.function.getOrDefault(k, defaultValue);
      }

      @Override
      public byte remove(double k) {
         throw new UnsupportedOperationException();
      }

      @Override
      public void clear() {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      @Override
      public Byte put(Double k, Byte v) {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      @Override
      public Byte get(Object k) {
         return this.function.get(k);
      }

      @Deprecated
      @Override
      public Byte getOrDefault(Object k, Byte defaultValue) {
         return this.function.getOrDefault(k, defaultValue);
      }

      @Deprecated
      @Override
      public Byte remove(Object k) {
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
