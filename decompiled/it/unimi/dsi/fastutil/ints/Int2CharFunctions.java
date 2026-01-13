package it.unimi.dsi.fastutil.ints;

import it.unimi.dsi.fastutil.SafeMath;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Objects;
import java.util.function.Function;

public final class Int2CharFunctions {
   public static final Int2CharFunctions.EmptyFunction EMPTY_FUNCTION = new Int2CharFunctions.EmptyFunction();

   private Int2CharFunctions() {
   }

   public static Int2CharFunction singleton(int key, char value) {
      return new Int2CharFunctions.Singleton(key, value);
   }

   public static Int2CharFunction singleton(Integer key, Character value) {
      return new Int2CharFunctions.Singleton(key, value);
   }

   public static Int2CharFunction synchronize(Int2CharFunction f) {
      return new Int2CharFunctions.SynchronizedFunction(f);
   }

   public static Int2CharFunction synchronize(Int2CharFunction f, Object sync) {
      return new Int2CharFunctions.SynchronizedFunction(f, sync);
   }

   public static Int2CharFunction unmodifiable(Int2CharFunction f) {
      return new Int2CharFunctions.UnmodifiableFunction(f);
   }

   public static Int2CharFunction primitive(Function<? super Integer, ? extends Character> f) {
      Objects.requireNonNull(f);
      if (f instanceof Int2CharFunction) {
         return (Int2CharFunction)f;
      } else {
         return (Int2CharFunction)(f instanceof java.util.function.IntUnaryOperator
            ? key -> SafeMath.safeIntToChar(((java.util.function.IntUnaryOperator)f).applyAsInt(key))
            : new Int2CharFunctions.PrimitiveFunction(f));
      }
   }

   public static class EmptyFunction extends AbstractInt2CharFunction implements Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;

      protected EmptyFunction() {
      }

      @Override
      public char get(int k) {
         return '\u0000';
      }

      @Override
      public char getOrDefault(int k, char defaultValue) {
         return defaultValue;
      }

      @Override
      public boolean containsKey(int k) {
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
         return Int2CharFunctions.EMPTY_FUNCTION;
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
         return Int2CharFunctions.EMPTY_FUNCTION;
      }
   }

   public static class PrimitiveFunction implements Int2CharFunction {
      protected final Function<? super Integer, ? extends Character> function;

      protected PrimitiveFunction(Function<? super Integer, ? extends Character> function) {
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
      public char get(int key) {
         Character v = this.function.apply(key);
         return v == null ? this.defaultReturnValue() : v;
      }

      @Override
      public char getOrDefault(int key, char defaultValue) {
         Character v = this.function.apply(key);
         return v == null ? defaultValue : v;
      }

      @Deprecated
      @Override
      public Character get(Object key) {
         return key == null ? null : this.function.apply((Integer)key);
      }

      @Deprecated
      @Override
      public Character getOrDefault(Object key, Character defaultValue) {
         if (key == null) {
            return defaultValue;
         } else {
            Character v;
            return (v = this.function.apply((Integer)key)) == null ? defaultValue : v;
         }
      }

      @Deprecated
      @Override
      public Character put(Integer key, Character value) {
         throw new UnsupportedOperationException();
      }
   }

   public static class Singleton extends AbstractInt2CharFunction implements Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final int key;
      protected final char value;

      protected Singleton(int key, char value) {
         this.key = key;
         this.value = value;
      }

      @Override
      public boolean containsKey(int k) {
         return this.key == k;
      }

      @Override
      public char get(int k) {
         return this.key == k ? this.value : this.defRetValue;
      }

      @Override
      public char getOrDefault(int k, char defaultValue) {
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

   public static class SynchronizedFunction implements Int2CharFunction, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Int2CharFunction function;
      protected final Object sync;

      protected SynchronizedFunction(Int2CharFunction f, Object sync) {
         if (f == null) {
            throw new NullPointerException();
         } else {
            this.function = f;
            this.sync = sync;
         }
      }

      protected SynchronizedFunction(Int2CharFunction f) {
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
      public Character apply(Integer key) {
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
      public char put(int k, char v) {
         synchronized (this.sync) {
            return this.function.put(k, v);
         }
      }

      @Override
      public char get(int k) {
         synchronized (this.sync) {
            return this.function.get(k);
         }
      }

      @Override
      public char getOrDefault(int k, char defaultValue) {
         synchronized (this.sync) {
            return this.function.getOrDefault(k, defaultValue);
         }
      }

      @Override
      public char remove(int k) {
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
      public Character put(Integer k, Character v) {
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

   public static class UnmodifiableFunction extends AbstractInt2CharFunction implements Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Int2CharFunction function;

      protected UnmodifiableFunction(Int2CharFunction f) {
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
      public boolean containsKey(int k) {
         return this.function.containsKey(k);
      }

      @Override
      public char put(int k, char v) {
         throw new UnsupportedOperationException();
      }

      @Override
      public char get(int k) {
         return this.function.get(k);
      }

      @Override
      public char getOrDefault(int k, char defaultValue) {
         return this.function.getOrDefault(k, defaultValue);
      }

      @Override
      public char remove(int k) {
         throw new UnsupportedOperationException();
      }

      @Override
      public void clear() {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      @Override
      public Character put(Integer k, Character v) {
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
