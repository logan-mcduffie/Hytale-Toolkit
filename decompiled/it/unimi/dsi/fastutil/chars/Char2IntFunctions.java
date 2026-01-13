package it.unimi.dsi.fastutil.chars;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.IntUnaryOperator;

public final class Char2IntFunctions {
   public static final Char2IntFunctions.EmptyFunction EMPTY_FUNCTION = new Char2IntFunctions.EmptyFunction();

   private Char2IntFunctions() {
   }

   public static Char2IntFunction singleton(char key, int value) {
      return new Char2IntFunctions.Singleton(key, value);
   }

   public static Char2IntFunction singleton(Character key, Integer value) {
      return new Char2IntFunctions.Singleton(key, value);
   }

   public static Char2IntFunction synchronize(Char2IntFunction f) {
      return new Char2IntFunctions.SynchronizedFunction(f);
   }

   public static Char2IntFunction synchronize(Char2IntFunction f, Object sync) {
      return new Char2IntFunctions.SynchronizedFunction(f, sync);
   }

   public static Char2IntFunction unmodifiable(Char2IntFunction f) {
      return new Char2IntFunctions.UnmodifiableFunction(f);
   }

   public static Char2IntFunction primitive(Function<? super Character, ? extends Integer> f) {
      Objects.requireNonNull(f);
      if (f instanceof Char2IntFunction) {
         return (Char2IntFunction)f;
      } else {
         return (Char2IntFunction)(f instanceof IntUnaryOperator ? ((IntUnaryOperator)f)::applyAsInt : new Char2IntFunctions.PrimitiveFunction(f));
      }
   }

   public static class EmptyFunction extends AbstractChar2IntFunction implements Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;

      protected EmptyFunction() {
      }

      @Override
      public int get(char k) {
         return 0;
      }

      @Override
      public int getOrDefault(char k, int defaultValue) {
         return defaultValue;
      }

      @Override
      public boolean containsKey(char k) {
         return false;
      }

      @Override
      public int defaultReturnValue() {
         return 0;
      }

      @Override
      public void defaultReturnValue(int defRetValue) {
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
         return Char2IntFunctions.EMPTY_FUNCTION;
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
         return Char2IntFunctions.EMPTY_FUNCTION;
      }
   }

   public static class PrimitiveFunction implements Char2IntFunction {
      protected final Function<? super Character, ? extends Integer> function;

      protected PrimitiveFunction(Function<? super Character, ? extends Integer> function) {
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
      public int get(char key) {
         Integer v = this.function.apply(key);
         return v == null ? this.defaultReturnValue() : v;
      }

      @Override
      public int getOrDefault(char key, int defaultValue) {
         Integer v = this.function.apply(key);
         return v == null ? defaultValue : v;
      }

      @Deprecated
      @Override
      public Integer get(Object key) {
         return key == null ? null : this.function.apply((Character)key);
      }

      @Deprecated
      @Override
      public Integer getOrDefault(Object key, Integer defaultValue) {
         if (key == null) {
            return defaultValue;
         } else {
            Integer v;
            return (v = this.function.apply((Character)key)) == null ? defaultValue : v;
         }
      }

      @Deprecated
      @Override
      public Integer put(Character key, Integer value) {
         throw new UnsupportedOperationException();
      }
   }

   public static class Singleton extends AbstractChar2IntFunction implements Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final char key;
      protected final int value;

      protected Singleton(char key, int value) {
         this.key = key;
         this.value = value;
      }

      @Override
      public boolean containsKey(char k) {
         return this.key == k;
      }

      @Override
      public int get(char k) {
         return this.key == k ? this.value : this.defRetValue;
      }

      @Override
      public int getOrDefault(char k, int defaultValue) {
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

   public static class SynchronizedFunction implements Char2IntFunction, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Char2IntFunction function;
      protected final Object sync;

      protected SynchronizedFunction(Char2IntFunction f, Object sync) {
         if (f == null) {
            throw new NullPointerException();
         } else {
            this.function = f;
            this.sync = sync;
         }
      }

      protected SynchronizedFunction(Char2IntFunction f) {
         if (f == null) {
            throw new NullPointerException();
         } else {
            this.function = f;
            this.sync = this;
         }
      }

      @Deprecated
      @Override
      public int applyAsInt(int operand) {
         synchronized (this.sync) {
            return this.function.applyAsInt(operand);
         }
      }

      @Deprecated
      public Integer apply(Character key) {
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
      public int defaultReturnValue() {
         synchronized (this.sync) {
            return this.function.defaultReturnValue();
         }
      }

      @Override
      public void defaultReturnValue(int defRetValue) {
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
      public int put(char k, int v) {
         synchronized (this.sync) {
            return this.function.put(k, v);
         }
      }

      @Override
      public int get(char k) {
         synchronized (this.sync) {
            return this.function.get(k);
         }
      }

      @Override
      public int getOrDefault(char k, int defaultValue) {
         synchronized (this.sync) {
            return this.function.getOrDefault(k, defaultValue);
         }
      }

      @Override
      public int remove(char k) {
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
      public Integer put(Character k, Integer v) {
         synchronized (this.sync) {
            return this.function.put(k, v);
         }
      }

      @Deprecated
      @Override
      public Integer get(Object k) {
         synchronized (this.sync) {
            return this.function.get(k);
         }
      }

      @Deprecated
      @Override
      public Integer getOrDefault(Object k, Integer defaultValue) {
         synchronized (this.sync) {
            return this.function.getOrDefault(k, defaultValue);
         }
      }

      @Deprecated
      @Override
      public Integer remove(Object k) {
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

   public static class UnmodifiableFunction extends AbstractChar2IntFunction implements Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Char2IntFunction function;

      protected UnmodifiableFunction(Char2IntFunction f) {
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
      public int defaultReturnValue() {
         return this.function.defaultReturnValue();
      }

      @Override
      public void defaultReturnValue(int defRetValue) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean containsKey(char k) {
         return this.function.containsKey(k);
      }

      @Override
      public int put(char k, int v) {
         throw new UnsupportedOperationException();
      }

      @Override
      public int get(char k) {
         return this.function.get(k);
      }

      @Override
      public int getOrDefault(char k, int defaultValue) {
         return this.function.getOrDefault(k, defaultValue);
      }

      @Override
      public int remove(char k) {
         throw new UnsupportedOperationException();
      }

      @Override
      public void clear() {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      @Override
      public Integer put(Character k, Integer v) {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      @Override
      public Integer get(Object k) {
         return this.function.get(k);
      }

      @Deprecated
      @Override
      public Integer getOrDefault(Object k, Integer defaultValue) {
         return this.function.getOrDefault(k, defaultValue);
      }

      @Deprecated
      @Override
      public Integer remove(Object k) {
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
