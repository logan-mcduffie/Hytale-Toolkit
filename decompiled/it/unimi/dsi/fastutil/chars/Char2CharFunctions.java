package it.unimi.dsi.fastutil.chars;

import it.unimi.dsi.fastutil.SafeMath;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.IntUnaryOperator;

public final class Char2CharFunctions {
   public static final Char2CharFunctions.EmptyFunction EMPTY_FUNCTION = new Char2CharFunctions.EmptyFunction();

   private Char2CharFunctions() {
   }

   public static Char2CharFunction singleton(char key, char value) {
      return new Char2CharFunctions.Singleton(key, value);
   }

   public static Char2CharFunction singleton(Character key, Character value) {
      return new Char2CharFunctions.Singleton(key, value);
   }

   public static Char2CharFunction synchronize(Char2CharFunction f) {
      return new Char2CharFunctions.SynchronizedFunction(f);
   }

   public static Char2CharFunction synchronize(Char2CharFunction f, Object sync) {
      return new Char2CharFunctions.SynchronizedFunction(f, sync);
   }

   public static Char2CharFunction unmodifiable(Char2CharFunction f) {
      return new Char2CharFunctions.UnmodifiableFunction(f);
   }

   public static Char2CharFunction primitive(Function<? super Character, ? extends Character> f) {
      Objects.requireNonNull(f);
      if (f instanceof Char2CharFunction) {
         return (Char2CharFunction)f;
      } else {
         return (Char2CharFunction)(f instanceof IntUnaryOperator
            ? key -> SafeMath.safeIntToChar(((IntUnaryOperator)f).applyAsInt(key))
            : new Char2CharFunctions.PrimitiveFunction(f));
      }
   }

   public static class EmptyFunction extends AbstractChar2CharFunction implements Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;

      protected EmptyFunction() {
      }

      @Override
      public char get(char k) {
         return '\u0000';
      }

      @Override
      public char getOrDefault(char k, char defaultValue) {
         return defaultValue;
      }

      @Override
      public boolean containsKey(char k) {
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
         return Char2CharFunctions.EMPTY_FUNCTION;
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
         return Char2CharFunctions.EMPTY_FUNCTION;
      }
   }

   public static class PrimitiveFunction implements Char2CharFunction {
      protected final Function<? super Character, ? extends Character> function;

      protected PrimitiveFunction(Function<? super Character, ? extends Character> function) {
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
      public char get(char key) {
         Character v = this.function.apply(key);
         return v == null ? this.defaultReturnValue() : v;
      }

      @Override
      public char getOrDefault(char key, char defaultValue) {
         Character v = this.function.apply(key);
         return v == null ? defaultValue : v;
      }

      @Deprecated
      @Override
      public Character get(Object key) {
         return key == null ? null : this.function.apply((Character)key);
      }

      @Deprecated
      @Override
      public Character getOrDefault(Object key, Character defaultValue) {
         if (key == null) {
            return defaultValue;
         } else {
            Character v;
            return (v = this.function.apply((Character)key)) == null ? defaultValue : v;
         }
      }

      @Deprecated
      @Override
      public Character put(Character key, Character value) {
         throw new UnsupportedOperationException();
      }
   }

   public static class Singleton extends AbstractChar2CharFunction implements Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final char key;
      protected final char value;

      protected Singleton(char key, char value) {
         this.key = key;
         this.value = value;
      }

      @Override
      public boolean containsKey(char k) {
         return this.key == k;
      }

      @Override
      public char get(char k) {
         return this.key == k ? this.value : this.defRetValue;
      }

      @Override
      public char getOrDefault(char k, char defaultValue) {
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

   public static class SynchronizedFunction implements Char2CharFunction, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Char2CharFunction function;
      protected final Object sync;

      protected SynchronizedFunction(Char2CharFunction f, Object sync) {
         if (f == null) {
            throw new NullPointerException();
         } else {
            this.function = f;
            this.sync = sync;
         }
      }

      protected SynchronizedFunction(Char2CharFunction f) {
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
      public Character apply(Character key) {
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
      public char put(char k, char v) {
         synchronized (this.sync) {
            return this.function.put(k, v);
         }
      }

      @Override
      public char get(char k) {
         synchronized (this.sync) {
            return this.function.get(k);
         }
      }

      @Override
      public char getOrDefault(char k, char defaultValue) {
         synchronized (this.sync) {
            return this.function.getOrDefault(k, defaultValue);
         }
      }

      @Override
      public char remove(char k) {
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
      public Character put(Character k, Character v) {
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

   public static class UnmodifiableFunction extends AbstractChar2CharFunction implements Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Char2CharFunction function;

      protected UnmodifiableFunction(Char2CharFunction f) {
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
      public boolean containsKey(char k) {
         return this.function.containsKey(k);
      }

      @Override
      public char put(char k, char v) {
         throw new UnsupportedOperationException();
      }

      @Override
      public char get(char k) {
         return this.function.get(k);
      }

      @Override
      public char getOrDefault(char k, char defaultValue) {
         return this.function.getOrDefault(k, defaultValue);
      }

      @Override
      public char remove(char k) {
         throw new UnsupportedOperationException();
      }

      @Override
      public void clear() {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      @Override
      public Character put(Character k, Character v) {
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
