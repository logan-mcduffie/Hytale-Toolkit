package it.unimi.dsi.fastutil.floats;

import it.unimi.dsi.fastutil.SafeMath;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Objects;
import java.util.function.DoubleToIntFunction;
import java.util.function.Function;

public final class Float2CharFunctions {
   public static final Float2CharFunctions.EmptyFunction EMPTY_FUNCTION = new Float2CharFunctions.EmptyFunction();

   private Float2CharFunctions() {
   }

   public static Float2CharFunction singleton(float key, char value) {
      return new Float2CharFunctions.Singleton(key, value);
   }

   public static Float2CharFunction singleton(Float key, Character value) {
      return new Float2CharFunctions.Singleton(key, value);
   }

   public static Float2CharFunction synchronize(Float2CharFunction f) {
      return new Float2CharFunctions.SynchronizedFunction(f);
   }

   public static Float2CharFunction synchronize(Float2CharFunction f, Object sync) {
      return new Float2CharFunctions.SynchronizedFunction(f, sync);
   }

   public static Float2CharFunction unmodifiable(Float2CharFunction f) {
      return new Float2CharFunctions.UnmodifiableFunction(f);
   }

   public static Float2CharFunction primitive(Function<? super Float, ? extends Character> f) {
      Objects.requireNonNull(f);
      if (f instanceof Float2CharFunction) {
         return (Float2CharFunction)f;
      } else {
         return (Float2CharFunction)(f instanceof DoubleToIntFunction
            ? key -> SafeMath.safeIntToChar(((DoubleToIntFunction)f).applyAsInt(key))
            : new Float2CharFunctions.PrimitiveFunction(f));
      }
   }

   public static class EmptyFunction extends AbstractFloat2CharFunction implements Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;

      protected EmptyFunction() {
      }

      @Override
      public char get(float k) {
         return '\u0000';
      }

      @Override
      public char getOrDefault(float k, char defaultValue) {
         return defaultValue;
      }

      @Override
      public boolean containsKey(float k) {
         return false;
      }

      @Override
      public char defaultReturnValue() {
         return '\u0000';
      }

      @Override
      public void defaultReturnValue(char defRetValue) {
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
         return Float2CharFunctions.EMPTY_FUNCTION;
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
         return Float2CharFunctions.EMPTY_FUNCTION;
      }
   }

   public static class PrimitiveFunction implements Float2CharFunction {
      protected final Function<? super Float, ? extends Character> function;

      protected PrimitiveFunction(Function<? super Float, ? extends Character> function) {
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
      public char get(float key) {
         Character v = this.function.apply(key);
         return v == null ? this.defaultReturnValue() : v;
      }

      @Override
      public char getOrDefault(float key, char defaultValue) {
         Character v = this.function.apply(key);
         return v == null ? defaultValue : v;
      }

      @Deprecated
      @Override
      public Character get(Object key) {
         return key == null ? null : this.function.apply((Float)key);
      }

      @Deprecated
      @Override
      public Character getOrDefault(Object key, Character defaultValue) {
         if (key == null) {
            return defaultValue;
         } else {
            Character v;
            return (v = this.function.apply((Float)key)) == null ? defaultValue : v;
         }
      }

      @Deprecated
      @Override
      public Character put(Float key, Character value) {
         throw new UnsupportedOperationException();
      }
   }

   public static class Singleton extends AbstractFloat2CharFunction implements Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final float key;
      protected final char value;

      protected Singleton(float key, char value) {
         this.key = key;
         this.value = value;
      }

      @Override
      public boolean containsKey(float k) {
         return Float.floatToIntBits(this.key) == Float.floatToIntBits(k);
      }

      @Override
      public char get(float k) {
         return Float.floatToIntBits(this.key) == Float.floatToIntBits(k) ? this.value : this.defRetValue;
      }

      @Override
      public char getOrDefault(float k, char defaultValue) {
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

   public static class SynchronizedFunction implements Float2CharFunction, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Float2CharFunction function;
      protected final Object sync;

      protected SynchronizedFunction(Float2CharFunction f, Object sync) {
         if (f == null) {
            throw new NullPointerException();
         } else {
            this.function = f;
            this.sync = sync;
         }
      }

      protected SynchronizedFunction(Float2CharFunction f) {
         if (f == null) {
            throw new NullPointerException();
         } else {
            this.function = f;
            this.sync = this;
         }
      }

      @Deprecated
      @Override
      public int applyAsInt(double operand) {
         synchronized (this.sync) {
            return this.function.applyAsInt(operand);
         }
      }

      @Deprecated
      public Character apply(Float key) {
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
      public char defaultReturnValue() {
         synchronized (this.sync) {
            return this.function.defaultReturnValue();
         }
      }

      @Override
      public void defaultReturnValue(char defRetValue) {
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
      public char put(float k, char v) {
         synchronized (this.sync) {
            return this.function.put(k, v);
         }
      }

      @Override
      public char get(float k) {
         synchronized (this.sync) {
            return this.function.get(k);
         }
      }

      @Override
      public char getOrDefault(float k, char defaultValue) {
         synchronized (this.sync) {
            return this.function.getOrDefault(k, defaultValue);
         }
      }

      @Override
      public char remove(float k) {
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
      public Character put(Float k, Character v) {
         synchronized (this.sync) {
            return this.function.put(k, v);
         }
      }

      @Deprecated
      @Override
      public Character get(Object k) {
         synchronized (this.sync) {
            return this.function.get(k);
         }
      }

      @Deprecated
      @Override
      public Character getOrDefault(Object k, Character defaultValue) {
         synchronized (this.sync) {
            return this.function.getOrDefault(k, defaultValue);
         }
      }

      @Deprecated
      @Override
      public Character remove(Object k) {
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

   public static class UnmodifiableFunction extends AbstractFloat2CharFunction implements Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Float2CharFunction function;

      protected UnmodifiableFunction(Float2CharFunction f) {
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
      public char defaultReturnValue() {
         return this.function.defaultReturnValue();
      }

      @Override
      public void defaultReturnValue(char defRetValue) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean containsKey(float k) {
         return this.function.containsKey(k);
      }

      @Override
      public char put(float k, char v) {
         throw new UnsupportedOperationException();
      }

      @Override
      public char get(float k) {
         return this.function.get(k);
      }

      @Override
      public char getOrDefault(float k, char defaultValue) {
         return this.function.getOrDefault(k, defaultValue);
      }

      @Override
      public char remove(float k) {
         throw new UnsupportedOperationException();
      }

      @Override
      public void clear() {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      @Override
      public Character put(Float k, Character v) {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      @Override
      public Character get(Object k) {
         return this.function.get(k);
      }

      @Deprecated
      @Override
      public Character getOrDefault(Object k, Character defaultValue) {
         return this.function.getOrDefault(k, defaultValue);
      }

      @Deprecated
      @Override
      public Character remove(Object k) {
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
