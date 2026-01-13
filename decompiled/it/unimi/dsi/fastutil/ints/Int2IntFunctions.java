package it.unimi.dsi.fastutil.ints;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Objects;
import java.util.function.Function;

public final class Int2IntFunctions {
   public static final Int2IntFunctions.EmptyFunction EMPTY_FUNCTION = new Int2IntFunctions.EmptyFunction();

   private Int2IntFunctions() {
   }

   public static Int2IntFunction singleton(int key, int value) {
      return new Int2IntFunctions.Singleton(key, value);
   }

   public static Int2IntFunction singleton(Integer key, Integer value) {
      return new Int2IntFunctions.Singleton(key, value);
   }

   public static Int2IntFunction synchronize(Int2IntFunction f) {
      return new Int2IntFunctions.SynchronizedFunction(f);
   }

   public static Int2IntFunction synchronize(Int2IntFunction f, Object sync) {
      return new Int2IntFunctions.SynchronizedFunction(f, sync);
   }

   public static Int2IntFunction unmodifiable(Int2IntFunction f) {
      return new Int2IntFunctions.UnmodifiableFunction(f);
   }

   public static Int2IntFunction primitive(Function<? super Integer, ? extends Integer> f) {
      Objects.requireNonNull(f);
      if (f instanceof Int2IntFunction) {
         return (Int2IntFunction)f;
      } else {
         return (Int2IntFunction)(f instanceof java.util.function.IntUnaryOperator
            ? ((java.util.function.IntUnaryOperator)f)::applyAsInt
            : new Int2IntFunctions.PrimitiveFunction(f));
      }
   }

   public static class EmptyFunction extends AbstractInt2IntFunction implements Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;

      protected EmptyFunction() {
      }

      @Override
      public int get(int k) {
         return 0;
      }

      @Override
      public int getOrDefault(int k, int defaultValue) {
         return defaultValue;
      }

      @Override
      public boolean containsKey(int k) {
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
         return Int2IntFunctions.EMPTY_FUNCTION;
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
         return Int2IntFunctions.EMPTY_FUNCTION;
      }
   }

   public static class PrimitiveFunction implements Int2IntFunction {
      protected final Function<? super Integer, ? extends Integer> function;

      protected PrimitiveFunction(Function<? super Integer, ? extends Integer> function) {
         this.function = function;
      }

      @Override
      public boolean containsKey(int key) {
         return this.function.apply(key) != null;
      }

      @Deprecated
      @Override
      public boolean containsKey(Object key) {
         return key == null ? false : this.function.apply((Integer)key) != null;
      }

      @Override
      public int get(int key) {
         Integer v = this.function.apply(key);
         return v == null ? this.defaultReturnValue() : v;
      }

      @Override
      public int getOrDefault(int key, int defaultValue) {
         Integer v = this.function.apply(key);
         return v == null ? defaultValue : v;
      }

      @Deprecated
      @Override
      public Integer get(Object key) {
         return key == null ? null : this.function.apply((Integer)key);
      }

      @Deprecated
      @Override
      public Integer getOrDefault(Object key, Integer defaultValue) {
         if (key == null) {
            return defaultValue;
         } else {
            Integer v;
            return (v = this.function.apply((Integer)key)) == null ? defaultValue : v;
         }
      }

      @Deprecated
      @Override
      public Integer put(Integer key, Integer value) {
         throw new UnsupportedOperationException();
      }
   }

   public static class Singleton extends AbstractInt2IntFunction implements Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final int key;
      protected final int value;

      protected Singleton(int key, int value) {
         this.key = key;
         this.value = value;
      }

      @Override
      public boolean containsKey(int k) {
         return this.key == k;
      }

      @Override
      public int get(int k) {
         return this.key == k ? this.value : this.defRetValue;
      }

      @Override
      public int getOrDefault(int k, int defaultValue) {
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

   public static class SynchronizedFunction implements Int2IntFunction, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Int2IntFunction function;
      protected final Object sync;

      protected SynchronizedFunction(Int2IntFunction f, Object sync) {
         if (f == null) {
            throw new NullPointerException();
         } else {
            this.function = f;
            this.sync = sync;
         }
      }

      protected SynchronizedFunction(Int2IntFunction f) {
         if (f == null) {
            throw new NullPointerException();
         } else {
            this.function = f;
            this.sync = this;
         }
      }

      @Override
      public int applyAsInt(int operand) {
         synchronized (this.sync) {
            return this.function.applyAsInt(operand);
         }
      }

      @Deprecated
      public Integer apply(Integer key) {
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
      public boolean containsKey(int k) {
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
      public int put(int k, int v) {
         synchronized (this.sync) {
            return this.function.put(k, v);
         }
      }

      @Override
      public int get(int k) {
         synchronized (this.sync) {
            return this.function.get(k);
         }
      }

      @Override
      public int getOrDefault(int k, int defaultValue) {
         synchronized (this.sync) {
            return this.function.getOrDefault(k, defaultValue);
         }
      }

      @Override
      public int remove(int k) {
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
      public Integer put(Integer k, Integer v) {
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

   public static class UnmodifiableFunction extends AbstractInt2IntFunction implements Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Int2IntFunction function;

      protected UnmodifiableFunction(Int2IntFunction f) {
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
      public boolean containsKey(int k) {
         return this.function.containsKey(k);
      }

      @Override
      public int put(int k, int v) {
         throw new UnsupportedOperationException();
      }

      @Override
      public int get(int k) {
         return this.function.get(k);
      }

      @Override
      public int getOrDefault(int k, int defaultValue) {
         return this.function.getOrDefault(k, defaultValue);
      }

      @Override
      public int remove(int k) {
         throw new UnsupportedOperationException();
      }

      @Override
      public void clear() {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      @Override
      public Integer put(Integer k, Integer v) {
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
