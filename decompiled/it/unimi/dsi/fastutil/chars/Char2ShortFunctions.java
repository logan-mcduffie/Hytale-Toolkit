package it.unimi.dsi.fastutil.chars;

import it.unimi.dsi.fastutil.SafeMath;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.IntUnaryOperator;

public final class Char2ShortFunctions {
   public static final Char2ShortFunctions.EmptyFunction EMPTY_FUNCTION = new Char2ShortFunctions.EmptyFunction();

   private Char2ShortFunctions() {
   }

   public static Char2ShortFunction singleton(char key, short value) {
      return new Char2ShortFunctions.Singleton(key, value);
   }

   public static Char2ShortFunction singleton(Character key, Short value) {
      return new Char2ShortFunctions.Singleton(key, value);
   }

   public static Char2ShortFunction synchronize(Char2ShortFunction f) {
      return new Char2ShortFunctions.SynchronizedFunction(f);
   }

   public static Char2ShortFunction synchronize(Char2ShortFunction f, Object sync) {
      return new Char2ShortFunctions.SynchronizedFunction(f, sync);
   }

   public static Char2ShortFunction unmodifiable(Char2ShortFunction f) {
      return new Char2ShortFunctions.UnmodifiableFunction(f);
   }

   public static Char2ShortFunction primitive(Function<? super Character, ? extends Short> f) {
      Objects.requireNonNull(f);
      if (f instanceof Char2ShortFunction) {
         return (Char2ShortFunction)f;
      } else {
         return (Char2ShortFunction)(f instanceof IntUnaryOperator
            ? key -> SafeMath.safeIntToShort(((IntUnaryOperator)f).applyAsInt(key))
            : new Char2ShortFunctions.PrimitiveFunction(f));
      }
   }

   public static class EmptyFunction extends AbstractChar2ShortFunction implements Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;

      protected EmptyFunction() {
      }

      @Override
      public short get(char k) {
         return 0;
      }

      @Override
      public short getOrDefault(char k, short defaultValue) {
         return defaultValue;
      }

      @Override
      public boolean containsKey(char k) {
         return false;
      }

      @Override
      public short defaultReturnValue() {
         return 0;
      }

      @Override
      public void defaultReturnValue(short defRetValue) {
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
         return Char2ShortFunctions.EMPTY_FUNCTION;
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
         return Char2ShortFunctions.EMPTY_FUNCTION;
      }
   }

   public static class PrimitiveFunction implements Char2ShortFunction {
      protected final Function<? super Character, ? extends Short> function;

      protected PrimitiveFunction(Function<? super Character, ? extends Short> function) {
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
      public short get(char key) {
         Short v = this.function.apply(key);
         return v == null ? this.defaultReturnValue() : v;
      }

      @Override
      public short getOrDefault(char key, short defaultValue) {
         Short v = this.function.apply(key);
         return v == null ? defaultValue : v;
      }

      @Deprecated
      @Override
      public Short get(Object key) {
         return key == null ? null : this.function.apply((Character)key);
      }

      @Deprecated
      @Override
      public Short getOrDefault(Object key, Short defaultValue) {
         if (key == null) {
            return defaultValue;
         } else {
            Short v;
            return (v = this.function.apply((Character)key)) == null ? defaultValue : v;
         }
      }

      @Deprecated
      @Override
      public Short put(Character key, Short value) {
         throw new UnsupportedOperationException();
      }
   }

   public static class Singleton extends AbstractChar2ShortFunction implements Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final char key;
      protected final short value;

      protected Singleton(char key, short value) {
         this.key = key;
         this.value = value;
      }

      @Override
      public boolean containsKey(char k) {
         return this.key == k;
      }

      @Override
      public short get(char k) {
         return this.key == k ? this.value : this.defRetValue;
      }

      @Override
      public short getOrDefault(char k, short defaultValue) {
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

   public static class SynchronizedFunction implements Char2ShortFunction, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Char2ShortFunction function;
      protected final Object sync;

      protected SynchronizedFunction(Char2ShortFunction f, Object sync) {
         if (f == null) {
            throw new NullPointerException();
         } else {
            this.function = f;
            this.sync = sync;
         }
      }

      protected SynchronizedFunction(Char2ShortFunction f) {
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
      public Short apply(Character key) {
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
      public short defaultReturnValue() {
         synchronized (this.sync) {
            return this.function.defaultReturnValue();
         }
      }

      @Override
      public void defaultReturnValue(short defRetValue) {
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
      public short put(char k, short v) {
         synchronized (this.sync) {
            return this.function.put(k, v);
         }
      }

      @Override
      public short get(char k) {
         synchronized (this.sync) {
            return this.function.get(k);
         }
      }

      @Override
      public short getOrDefault(char k, short defaultValue) {
         synchronized (this.sync) {
            return this.function.getOrDefault(k, defaultValue);
         }
      }

      @Override
      public short remove(char k) {
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
      public Short put(Character k, Short v) {
         synchronized (this.sync) {
            return this.function.put(k, v);
         }
      }

      @Deprecated
      @Override
      public Short get(Object k) {
         synchronized (this.sync) {
            return this.function.get(k);
         }
      }

      @Deprecated
      @Override
      public Short getOrDefault(Object k, Short defaultValue) {
         synchronized (this.sync) {
            return this.function.getOrDefault(k, defaultValue);
         }
      }

      @Deprecated
      @Override
      public Short remove(Object k) {
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

   public static class UnmodifiableFunction extends AbstractChar2ShortFunction implements Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Char2ShortFunction function;

      protected UnmodifiableFunction(Char2ShortFunction f) {
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
      public short defaultReturnValue() {
         return this.function.defaultReturnValue();
      }

      @Override
      public void defaultReturnValue(short defRetValue) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean containsKey(char k) {
         return this.function.containsKey(k);
      }

      @Override
      public short put(char k, short v) {
         throw new UnsupportedOperationException();
      }

      @Override
      public short get(char k) {
         return this.function.get(k);
      }

      @Override
      public short getOrDefault(char k, short defaultValue) {
         return this.function.getOrDefault(k, defaultValue);
      }

      @Override
      public short remove(char k) {
         throw new UnsupportedOperationException();
      }

      @Override
      public void clear() {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      @Override
      public Short put(Character k, Short v) {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      @Override
      public Short get(Object k) {
         return this.function.get(k);
      }

      @Deprecated
      @Override
      public Short getOrDefault(Object k, Short defaultValue) {
         return this.function.getOrDefault(k, defaultValue);
      }

      @Deprecated
      @Override
      public Short remove(Object k) {
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
