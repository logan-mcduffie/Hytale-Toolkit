package it.unimi.dsi.fastutil.floats;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Objects;
import java.util.function.DoublePredicate;
import java.util.function.Function;

public final class Float2BooleanFunctions {
   public static final Float2BooleanFunctions.EmptyFunction EMPTY_FUNCTION = new Float2BooleanFunctions.EmptyFunction();

   private Float2BooleanFunctions() {
   }

   public static Float2BooleanFunction singleton(float key, boolean value) {
      return new Float2BooleanFunctions.Singleton(key, value);
   }

   public static Float2BooleanFunction singleton(Float key, Boolean value) {
      return new Float2BooleanFunctions.Singleton(key, value);
   }

   public static Float2BooleanFunction synchronize(Float2BooleanFunction f) {
      return new Float2BooleanFunctions.SynchronizedFunction(f);
   }

   public static Float2BooleanFunction synchronize(Float2BooleanFunction f, Object sync) {
      return new Float2BooleanFunctions.SynchronizedFunction(f, sync);
   }

   public static Float2BooleanFunction unmodifiable(Float2BooleanFunction f) {
      return new Float2BooleanFunctions.UnmodifiableFunction(f);
   }

   public static Float2BooleanFunction primitive(Function<? super Float, ? extends Boolean> f) {
      Objects.requireNonNull(f);
      if (f instanceof Float2BooleanFunction) {
         return (Float2BooleanFunction)f;
      } else {
         return (Float2BooleanFunction)(f instanceof DoublePredicate ? ((DoublePredicate)f)::test : new Float2BooleanFunctions.PrimitiveFunction(f));
      }
   }

   public static class EmptyFunction extends AbstractFloat2BooleanFunction implements Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;

      protected EmptyFunction() {
      }

      @Override
      public boolean get(float k) {
         return false;
      }

      @Override
      public boolean getOrDefault(float k, boolean defaultValue) {
         return defaultValue;
      }

      @Override
      public boolean containsKey(float k) {
         return false;
      }

      @Override
      public boolean defaultReturnValue() {
         return false;
      }

      @Override
      public void defaultReturnValue(boolean defRetValue) {
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
         return Float2BooleanFunctions.EMPTY_FUNCTION;
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
         return Float2BooleanFunctions.EMPTY_FUNCTION;
      }
   }

   public static class PrimitiveFunction implements Float2BooleanFunction {
      protected final Function<? super Float, ? extends Boolean> function;

      protected PrimitiveFunction(Function<? super Float, ? extends Boolean> function) {
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
      public boolean get(float key) {
         Boolean v = this.function.apply(key);
         return v == null ? this.defaultReturnValue() : v;
      }

      @Override
      public boolean getOrDefault(float key, boolean defaultValue) {
         Boolean v = this.function.apply(key);
         return v == null ? defaultValue : v;
      }

      @Deprecated
      @Override
      public Boolean get(Object key) {
         return key == null ? null : this.function.apply((Float)key);
      }

      @Deprecated
      @Override
      public Boolean getOrDefault(Object key, Boolean defaultValue) {
         if (key == null) {
            return defaultValue;
         } else {
            Boolean v;
            return (v = this.function.apply((Float)key)) == null ? defaultValue : v;
         }
      }

      @Deprecated
      @Override
      public Boolean put(Float key, Boolean value) {
         throw new UnsupportedOperationException();
      }
   }

   public static class Singleton extends AbstractFloat2BooleanFunction implements Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final float key;
      protected final boolean value;

      protected Singleton(float key, boolean value) {
         this.key = key;
         this.value = value;
      }

      @Override
      public boolean containsKey(float k) {
         return Float.floatToIntBits(this.key) == Float.floatToIntBits(k);
      }

      @Override
      public boolean get(float k) {
         return Float.floatToIntBits(this.key) == Float.floatToIntBits(k) ? this.value : this.defRetValue;
      }

      @Override
      public boolean getOrDefault(float k, boolean defaultValue) {
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

   public static class SynchronizedFunction implements Float2BooleanFunction, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Float2BooleanFunction function;
      protected final Object sync;

      protected SynchronizedFunction(Float2BooleanFunction f, Object sync) {
         if (f == null) {
            throw new NullPointerException();
         } else {
            this.function = f;
            this.sync = sync;
         }
      }

      protected SynchronizedFunction(Float2BooleanFunction f) {
         if (f == null) {
            throw new NullPointerException();
         } else {
            this.function = f;
            this.sync = this;
         }
      }

      @Deprecated
      @Override
      public boolean test(double operand) {
         synchronized (this.sync) {
            return this.function.test(operand);
         }
      }

      @Deprecated
      public Boolean apply(Float key) {
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
      public boolean defaultReturnValue() {
         synchronized (this.sync) {
            return this.function.defaultReturnValue();
         }
      }

      @Override
      public void defaultReturnValue(boolean defRetValue) {
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
      public boolean put(float k, boolean v) {
         synchronized (this.sync) {
            return this.function.put(k, v);
         }
      }

      @Override
      public boolean get(float k) {
         synchronized (this.sync) {
            return this.function.get(k);
         }
      }

      @Override
      public boolean getOrDefault(float k, boolean defaultValue) {
         synchronized (this.sync) {
            return this.function.getOrDefault(k, defaultValue);
         }
      }

      @Override
      public boolean remove(float k) {
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
      public Boolean put(Float k, Boolean v) {
         synchronized (this.sync) {
            return this.function.put(k, v);
         }
      }

      @Deprecated
      @Override
      public Boolean get(Object k) {
         synchronized (this.sync) {
            return this.function.get(k);
         }
      }

      @Deprecated
      @Override
      public Boolean getOrDefault(Object k, Boolean defaultValue) {
         synchronized (this.sync) {
            return this.function.getOrDefault(k, defaultValue);
         }
      }

      @Deprecated
      @Override
      public Boolean remove(Object k) {
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

   public static class UnmodifiableFunction extends AbstractFloat2BooleanFunction implements Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Float2BooleanFunction function;

      protected UnmodifiableFunction(Float2BooleanFunction f) {
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
      public boolean defaultReturnValue() {
         return this.function.defaultReturnValue();
      }

      @Override
      public void defaultReturnValue(boolean defRetValue) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean containsKey(float k) {
         return this.function.containsKey(k);
      }

      @Override
      public boolean put(float k, boolean v) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean get(float k) {
         return this.function.get(k);
      }

      @Override
      public boolean getOrDefault(float k, boolean defaultValue) {
         return this.function.getOrDefault(k, defaultValue);
      }

      @Override
      public boolean remove(float k) {
         throw new UnsupportedOperationException();
      }

      @Override
      public void clear() {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      @Override
      public Boolean put(Float k, Boolean v) {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      @Override
      public Boolean get(Object k) {
         return this.function.get(k);
      }

      @Deprecated
      @Override
      public Boolean getOrDefault(Object k, Boolean defaultValue) {
         return this.function.getOrDefault(k, defaultValue);
      }

      @Deprecated
      @Override
      public Boolean remove(Object k) {
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
