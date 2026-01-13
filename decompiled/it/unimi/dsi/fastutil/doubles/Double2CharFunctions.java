package it.unimi.dsi.fastutil.doubles;

import it.unimi.dsi.fastutil.SafeMath;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Objects;
import java.util.function.DoubleToIntFunction;
import java.util.function.Function;

public final class Double2CharFunctions {
   public static final Double2CharFunctions.EmptyFunction EMPTY_FUNCTION = new Double2CharFunctions.EmptyFunction();

   private Double2CharFunctions() {
   }

   public static Double2CharFunction singleton(double key, char value) {
      return new Double2CharFunctions.Singleton(key, value);
   }

   public static Double2CharFunction singleton(Double key, Character value) {
      return new Double2CharFunctions.Singleton(key, value);
   }

   public static Double2CharFunction synchronize(Double2CharFunction f) {
      return new Double2CharFunctions.SynchronizedFunction(f);
   }

   public static Double2CharFunction synchronize(Double2CharFunction f, Object sync) {
      return new Double2CharFunctions.SynchronizedFunction(f, sync);
   }

   public static Double2CharFunction unmodifiable(Double2CharFunction f) {
      return new Double2CharFunctions.UnmodifiableFunction(f);
   }

   public static Double2CharFunction primitive(Function<? super Double, ? extends Character> f) {
      Objects.requireNonNull(f);
      if (f instanceof Double2CharFunction) {
         return (Double2CharFunction)f;
      } else {
         return (Double2CharFunction)(f instanceof DoubleToIntFunction
            ? key -> SafeMath.safeIntToChar(((DoubleToIntFunction)f).applyAsInt(key))
            : new Double2CharFunctions.PrimitiveFunction(f));
      }
   }

   public static class EmptyFunction extends AbstractDouble2CharFunction implements Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;

      protected EmptyFunction() {
      }

      @Override
      public char get(double k) {
         return '\u0000';
      }

      @Override
      public char getOrDefault(double k, char defaultValue) {
         return defaultValue;
      }

      @Override
      public boolean containsKey(double k) {
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
         return Double2CharFunctions.EMPTY_FUNCTION;
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
         return Double2CharFunctions.EMPTY_FUNCTION;
      }
   }

   public static class PrimitiveFunction implements Double2CharFunction {
      protected final Function<? super Double, ? extends Character> function;

      protected PrimitiveFunction(Function<? super Double, ? extends Character> function) {
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
      public char get(double key) {
         Character v = this.function.apply(key);
         return v == null ? this.defaultReturnValue() : v;
      }

      @Override
      public char getOrDefault(double key, char defaultValue) {
         Character v = this.function.apply(key);
         return v == null ? defaultValue : v;
      }

      @Deprecated
      @Override
      public Character get(Object key) {
         return key == null ? null : this.function.apply((Double)key);
      }

      @Deprecated
      @Override
      public Character getOrDefault(Object key, Character defaultValue) {
         if (key == null) {
            return defaultValue;
         } else {
            Character v;
            return (v = this.function.apply((Double)key)) == null ? defaultValue : v;
         }
      }

      @Deprecated
      @Override
      public Character put(Double key, Character value) {
         throw new UnsupportedOperationException();
      }
   }

   public static class Singleton extends AbstractDouble2CharFunction implements Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final double key;
      protected final char value;

      protected Singleton(double key, char value) {
         this.key = key;
         this.value = value;
      }

      @Override
      public boolean containsKey(double k) {
         return Double.doubleToLongBits(this.key) == Double.doubleToLongBits(k);
      }

      @Override
      public char get(double k) {
         return Double.doubleToLongBits(this.key) == Double.doubleToLongBits(k) ? this.value : this.defRetValue;
      }

      @Override
      public char getOrDefault(double k, char defaultValue) {
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

   public static class SynchronizedFunction implements Double2CharFunction, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Double2CharFunction function;
      protected final Object sync;

      protected SynchronizedFunction(Double2CharFunction f, Object sync) {
         if (f == null) {
            throw new NullPointerException();
         } else {
            this.function = f;
            this.sync = sync;
         }
      }

      protected SynchronizedFunction(Double2CharFunction f) {
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
      public Character apply(Double key) {
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
      public char put(double k, char v) {
         synchronized (this.sync) {
            return this.function.put(k, v);
         }
      }

      @Override
      public char get(double k) {
         synchronized (this.sync) {
            return this.function.get(k);
         }
      }

      @Override
      public char getOrDefault(double k, char defaultValue) {
         synchronized (this.sync) {
            return this.function.getOrDefault(k, defaultValue);
         }
      }

      @Override
      public char remove(double k) {
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
      public Character put(Double k, Character v) {
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

   public static class UnmodifiableFunction extends AbstractDouble2CharFunction implements Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Double2CharFunction function;

      protected UnmodifiableFunction(Double2CharFunction f) {
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
      public boolean containsKey(double k) {
         return this.function.containsKey(k);
      }

      @Override
      public char put(double k, char v) {
         throw new UnsupportedOperationException();
      }

      @Override
      public char get(double k) {
         return this.function.get(k);
      }

      @Override
      public char getOrDefault(double k, char defaultValue) {
         return this.function.getOrDefault(k, defaultValue);
      }

      @Override
      public char remove(double k) {
         throw new UnsupportedOperationException();
      }

      @Override
      public void clear() {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      @Override
      public Character put(Double k, Character v) {
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
