package it.unimi.dsi.fastutil.chars;

import it.unimi.dsi.fastutil.SafeMath;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.IntToDoubleFunction;

public final class Char2FloatFunctions {
   public static final Char2FloatFunctions.EmptyFunction EMPTY_FUNCTION = new Char2FloatFunctions.EmptyFunction();

   private Char2FloatFunctions() {
   }

   public static Char2FloatFunction singleton(char key, float value) {
      return new Char2FloatFunctions.Singleton(key, value);
   }

   public static Char2FloatFunction singleton(Character key, Float value) {
      return new Char2FloatFunctions.Singleton(key, value);
   }

   public static Char2FloatFunction synchronize(Char2FloatFunction f) {
      return new Char2FloatFunctions.SynchronizedFunction(f);
   }

   public static Char2FloatFunction synchronize(Char2FloatFunction f, Object sync) {
      return new Char2FloatFunctions.SynchronizedFunction(f, sync);
   }

   public static Char2FloatFunction unmodifiable(Char2FloatFunction f) {
      return new Char2FloatFunctions.UnmodifiableFunction(f);
   }

   public static Char2FloatFunction primitive(Function<? super Character, ? extends Float> f) {
      Objects.requireNonNull(f);
      if (f instanceof Char2FloatFunction) {
         return (Char2FloatFunction)f;
      } else {
         return (Char2FloatFunction)(f instanceof IntToDoubleFunction
            ? key -> SafeMath.safeDoubleToFloat(((IntToDoubleFunction)f).applyAsDouble(key))
            : new Char2FloatFunctions.PrimitiveFunction(f));
      }
   }

   public static class EmptyFunction extends AbstractChar2FloatFunction implements Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;

      protected EmptyFunction() {
      }

      @Override
      public float get(char k) {
         return 0.0F;
      }

      @Override
      public float getOrDefault(char k, float defaultValue) {
         return defaultValue;
      }

      @Override
      public boolean containsKey(char k) {
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
         return Char2FloatFunctions.EMPTY_FUNCTION;
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
         return Char2FloatFunctions.EMPTY_FUNCTION;
      }
   }

   public static class PrimitiveFunction implements Char2FloatFunction {
      protected final Function<? super Character, ? extends Float> function;

      protected PrimitiveFunction(Function<? super Character, ? extends Float> function) {
         this.function = function;
      }

      @Override
      public boolean containsKey(char key) {
         return this.function.apply(key) != null;
      }

      @Deprecated
      @Override
      public boolean containsKey(Object key) {
         return key == null ? false : this.function.apply((Character)key) != null;
      }

      @Override
      public float get(char key) {
         Float v = this.function.apply(key);
         return v == null ? this.defaultReturnValue() : v;
      }

      @Override
      public float getOrDefault(char key, float defaultValue) {
         Float v = this.function.apply(key);
         return v == null ? defaultValue : v;
      }

      @Deprecated
      @Override
      public Float get(Object key) {
         return key == null ? null : this.function.apply((Character)key);
      }

      @Deprecated
      @Override
      public Float getOrDefault(Object key, Float defaultValue) {
         if (key == null) {
            return defaultValue;
         } else {
            Float v;
            return (v = this.function.apply((Character)key)) == null ? defaultValue : v;
         }
      }

      @Deprecated
      @Override
      public Float put(Character key, Float value) {
         throw new UnsupportedOperationException();
      }
   }

   public static class Singleton extends AbstractChar2FloatFunction implements Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final char key;
      protected final float value;

      protected Singleton(char key, float value) {
         this.key = key;
         this.value = value;
      }

      @Override
      public boolean containsKey(char k) {
         return this.key == k;
      }

      @Override
      public float get(char k) {
         return this.key == k ? this.value : this.defRetValue;
      }

      @Override
      public float getOrDefault(char k, float defaultValue) {
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

   public static class SynchronizedFunction implements Char2FloatFunction, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Char2FloatFunction function;
      protected final Object sync;

      protected SynchronizedFunction(Char2FloatFunction f, Object sync) {
         if (f == null) {
            throw new NullPointerException();
         } else {
            this.function = f;
            this.sync = sync;
         }
      }

      protected SynchronizedFunction(Char2FloatFunction f) {
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
      public Float apply(Character key) {
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
      public boolean containsKey(char k) {
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
      public float put(char k, float v) {
         synchronized (this.sync) {
            return this.function.put(k, v);
         }
      }

      @Override
      public float get(char k) {
         synchronized (this.sync) {
            return this.function.get(k);
         }
      }

      @Override
      public float getOrDefault(char k, float defaultValue) {
         synchronized (this.sync) {
            return this.function.getOrDefault(k, defaultValue);
         }
      }

      @Override
      public float remove(char k) {
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
      public Float put(Character k, Float v) {
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

   public static class UnmodifiableFunction extends AbstractChar2FloatFunction implements Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Char2FloatFunction function;

      protected UnmodifiableFunction(Char2FloatFunction f) {
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
      public boolean containsKey(char k) {
         return this.function.containsKey(k);
      }

      @Override
      public float put(char k, float v) {
         throw new UnsupportedOperationException();
      }

      @Override
      public float get(char k) {
         return this.function.get(k);
      }

      @Override
      public float getOrDefault(char k, float defaultValue) {
         return this.function.getOrDefault(k, defaultValue);
      }

      @Override
      public float remove(char k) {
         throw new UnsupportedOperationException();
      }

      @Override
      public void clear() {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      @Override
      public Float put(Character k, Float v) {
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
