package it.unimi.dsi.fastutil.chars;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.IntPredicate;

public final class Char2BooleanFunctions {
   public static final Char2BooleanFunctions.EmptyFunction EMPTY_FUNCTION = new Char2BooleanFunctions.EmptyFunction();

   private Char2BooleanFunctions() {
   }

   public static Char2BooleanFunction singleton(char key, boolean value) {
      return new Char2BooleanFunctions.Singleton(key, value);
   }

   public static Char2BooleanFunction singleton(Character key, Boolean value) {
      return new Char2BooleanFunctions.Singleton(key, value);
   }

   public static Char2BooleanFunction synchronize(Char2BooleanFunction f) {
      return new Char2BooleanFunctions.SynchronizedFunction(f);
   }

   public static Char2BooleanFunction synchronize(Char2BooleanFunction f, Object sync) {
      return new Char2BooleanFunctions.SynchronizedFunction(f, sync);
   }

   public static Char2BooleanFunction unmodifiable(Char2BooleanFunction f) {
      return new Char2BooleanFunctions.UnmodifiableFunction(f);
   }

   public static Char2BooleanFunction primitive(Function<? super Character, ? extends Boolean> f) {
      Objects.requireNonNull(f);
      if (f instanceof Char2BooleanFunction) {
         return (Char2BooleanFunction)f;
      } else {
         return (Char2BooleanFunction)(f instanceof IntPredicate ? ((IntPredicate)f)::test : new Char2BooleanFunctions.PrimitiveFunction(f));
      }
   }

   public static class EmptyFunction extends AbstractChar2BooleanFunction implements Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;

      protected EmptyFunction() {
      }

      @Override
      public boolean get(char k) {
         return false;
      }

      @Override
      public boolean getOrDefault(char k, boolean defaultValue) {
         return defaultValue;
      }

      @Override
      public boolean containsKey(char k) {
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
         return Char2BooleanFunctions.EMPTY_FUNCTION;
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
         return Char2BooleanFunctions.EMPTY_FUNCTION;
      }
   }

   public static class PrimitiveFunction implements Char2BooleanFunction {
      protected final Function<? super Character, ? extends Boolean> function;

      protected PrimitiveFunction(Function<? super Character, ? extends Boolean> function) {
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
      public boolean get(char key) {
         Boolean v = this.function.apply(key);
         return v == null ? this.defaultReturnValue() : v;
      }

      @Override
      public boolean getOrDefault(char key, boolean defaultValue) {
         Boolean v = this.function.apply(key);
         return v == null ? defaultValue : v;
      }

      @Deprecated
      @Override
      public Boolean get(Object key) {
         return key == null ? null : this.function.apply((Character)key);
      }

      @Deprecated
      @Override
      public Boolean getOrDefault(Object key, Boolean defaultValue) {
         if (key == null) {
            return defaultValue;
         } else {
            Boolean v;
            return (v = this.function.apply((Character)key)) == null ? defaultValue : v;
         }
      }

      @Deprecated
      @Override
      public Boolean put(Character key, Boolean value) {
         throw new UnsupportedOperationException();
      }
   }

   public static class Singleton extends AbstractChar2BooleanFunction implements Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final char key;
      protected final boolean value;

      protected Singleton(char key, boolean value) {
         this.key = key;
         this.value = value;
      }

      @Override
      public boolean containsKey(char k) {
         return this.key == k;
      }

      @Override
      public boolean get(char k) {
         return this.key == k ? this.value : this.defRetValue;
      }

      @Override
      public boolean getOrDefault(char k, boolean defaultValue) {
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

   public static class SynchronizedFunction implements Char2BooleanFunction, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Char2BooleanFunction function;
      protected final Object sync;

      protected SynchronizedFunction(Char2BooleanFunction f, Object sync) {
         if (f == null) {
            throw new NullPointerException();
         } else {
            this.function = f;
            this.sync = sync;
         }
      }

      protected SynchronizedFunction(Char2BooleanFunction f) {
         if (f == null) {
            throw new NullPointerException();
         } else {
            this.function = f;
            this.sync = this;
         }
      }

      @Deprecated
      @Override
      public boolean test(int operand) {
         synchronized (this.sync) {
            return this.function.test(operand);
         }
      }

      @Deprecated
      public Boolean apply(Character key) {
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
      public boolean put(char k, boolean v) {
         synchronized (this.sync) {
            return this.function.put(k, v);
         }
      }

      @Override
      public boolean get(char k) {
         synchronized (this.sync) {
            return this.function.get(k);
         }
      }

      @Override
      public boolean getOrDefault(char k, boolean defaultValue) {
         synchronized (this.sync) {
            return this.function.getOrDefault(k, defaultValue);
         }
      }

      @Override
      public boolean remove(char k) {
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
      public Boolean put(Character k, Boolean v) {
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

   public static class UnmodifiableFunction extends AbstractChar2BooleanFunction implements Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Char2BooleanFunction function;

      protected UnmodifiableFunction(Char2BooleanFunction f) {
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
      public boolean containsKey(char k) {
         return this.function.containsKey(k);
      }

      @Override
      public boolean put(char k, boolean v) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean get(char k) {
         return this.function.get(k);
      }

      @Override
      public boolean getOrDefault(char k, boolean defaultValue) {
         return this.function.getOrDefault(k, defaultValue);
      }

      @Override
      public boolean remove(char k) {
         throw new UnsupportedOperationException();
      }

      @Override
      public void clear() {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      @Override
      public Boolean put(Character k, Boolean v) {
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
