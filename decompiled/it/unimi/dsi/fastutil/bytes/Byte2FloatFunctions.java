package it.unimi.dsi.fastutil.bytes;

import it.unimi.dsi.fastutil.SafeMath;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.IntToDoubleFunction;

public final class Byte2FloatFunctions {
   public static final Byte2FloatFunctions.EmptyFunction EMPTY_FUNCTION = new Byte2FloatFunctions.EmptyFunction();

   private Byte2FloatFunctions() {
   }

   public static Byte2FloatFunction singleton(byte key, float value) {
      return new Byte2FloatFunctions.Singleton(key, value);
   }

   public static Byte2FloatFunction singleton(Byte key, Float value) {
      return new Byte2FloatFunctions.Singleton(key, value);
   }

   public static Byte2FloatFunction synchronize(Byte2FloatFunction f) {
      return new Byte2FloatFunctions.SynchronizedFunction(f);
   }

   public static Byte2FloatFunction synchronize(Byte2FloatFunction f, Object sync) {
      return new Byte2FloatFunctions.SynchronizedFunction(f, sync);
   }

   public static Byte2FloatFunction unmodifiable(Byte2FloatFunction f) {
      return new Byte2FloatFunctions.UnmodifiableFunction(f);
   }

   public static Byte2FloatFunction primitive(Function<? super Byte, ? extends Float> f) {
      Objects.requireNonNull(f);
      if (f instanceof Byte2FloatFunction) {
         return (Byte2FloatFunction)f;
      } else {
         return (Byte2FloatFunction)(f instanceof IntToDoubleFunction
            ? key -> SafeMath.safeDoubleToFloat(((IntToDoubleFunction)f).applyAsDouble(key))
            : new Byte2FloatFunctions.PrimitiveFunction(f));
      }
   }

   public static class EmptyFunction extends AbstractByte2FloatFunction implements Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;

      protected EmptyFunction() {
      }

      @Override
      public float get(byte k) {
         return 0.0F;
      }

      @Override
      public float getOrDefault(byte k, float defaultValue) {
         return defaultValue;
      }

      @Override
      public boolean containsKey(byte k) {
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
         return Byte2FloatFunctions.EMPTY_FUNCTION;
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
         return Byte2FloatFunctions.EMPTY_FUNCTION;
      }
   }

   public static class PrimitiveFunction implements Byte2FloatFunction {
      protected final Function<? super Byte, ? extends Float> function;

      protected PrimitiveFunction(Function<? super Byte, ? extends Float> function) {
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
      public float get(byte key) {
         Float v = this.function.apply(key);
         return v == null ? this.defaultReturnValue() : v;
      }

      @Override
      public float getOrDefault(byte key, float defaultValue) {
         Float v = this.function.apply(key);
         return v == null ? defaultValue : v;
      }

      @Deprecated
      @Override
      public Float get(Object key) {
         return key == null ? null : this.function.apply((Byte)key);
      }

      @Deprecated
      @Override
      public Float getOrDefault(Object key, Float defaultValue) {
         if (key == null) {
            return defaultValue;
         } else {
            Float v;
            return (v = this.function.apply((Byte)key)) == null ? defaultValue : v;
         }
      }

      @Deprecated
      @Override
      public Float put(Byte key, Float value) {
         throw new UnsupportedOperationException();
      }
   }

   public static class Singleton extends AbstractByte2FloatFunction implements Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final byte key;
      protected final float value;

      protected Singleton(byte key, float value) {
         this.key = key;
         this.value = value;
      }

      @Override
      public boolean containsKey(byte k) {
         return this.key == k;
      }

      @Override
      public float get(byte k) {
         return this.key == k ? this.value : this.defRetValue;
      }

      @Override
      public float getOrDefault(byte k, float defaultValue) {
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

   public static class SynchronizedFunction implements Byte2FloatFunction, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Byte2FloatFunction function;
      protected final Object sync;

      protected SynchronizedFunction(Byte2FloatFunction f, Object sync) {
         if (f == null) {
            throw new NullPointerException();
         } else {
            this.function = f;
            this.sync = sync;
         }
      }

      protected SynchronizedFunction(Byte2FloatFunction f) {
         if (f == null) {
            throw new NullPointerException();
         } else {
            this.function = f;
            this.sync = this;
         }
      }

      @Deprecated
      @Override
      public double applyAsDouble(int operand) {
         synchronized (this.sync) {
            return this.function.applyAsDouble(operand);
         }
      }

      @Deprecated
      public Float apply(Byte key) {
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
      public float put(byte k, float v) {
         synchronized (this.sync) {
            return this.function.put(k, v);
         }
      }

      @Override
      public float get(byte k) {
         synchronized (this.sync) {
            return this.function.get(k);
         }
      }

      @Override
      public float getOrDefault(byte k, float defaultValue) {
         synchronized (this.sync) {
            return this.function.getOrDefault(k, defaultValue);
         }
      }

      @Override
      public float remove(byte k) {
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
      public Float put(Byte k, Float v) {
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

   public static class UnmodifiableFunction extends AbstractByte2FloatFunction implements Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Byte2FloatFunction function;

      protected UnmodifiableFunction(Byte2FloatFunction f) {
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
      public boolean containsKey(byte k) {
         return this.function.containsKey(k);
      }

      @Override
      public float put(byte k, float v) {
         throw new UnsupportedOperationException();
      }

      @Override
      public float get(byte k) {
         return this.function.get(k);
      }

      @Override
      public float getOrDefault(byte k, float defaultValue) {
         return this.function.getOrDefault(k, defaultValue);
      }

      @Override
      public float remove(byte k) {
         throw new UnsupportedOperationException();
      }

      @Override
      public void clear() {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      @Override
      public Float put(Byte k, Float v) {
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
