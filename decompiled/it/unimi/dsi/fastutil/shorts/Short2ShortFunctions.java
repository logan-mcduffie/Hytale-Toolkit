package it.unimi.dsi.fastutil.shorts;

import it.unimi.dsi.fastutil.SafeMath;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.IntUnaryOperator;

public final class Short2ShortFunctions {
   public static final Short2ShortFunctions.EmptyFunction EMPTY_FUNCTION = new Short2ShortFunctions.EmptyFunction();

   private Short2ShortFunctions() {
   }

   public static Short2ShortFunction singleton(short key, short value) {
      return new Short2ShortFunctions.Singleton(key, value);
   }

   public static Short2ShortFunction singleton(Short key, Short value) {
      return new Short2ShortFunctions.Singleton(key, value);
   }

   public static Short2ShortFunction synchronize(Short2ShortFunction f) {
      return new Short2ShortFunctions.SynchronizedFunction(f);
   }

   public static Short2ShortFunction synchronize(Short2ShortFunction f, Object sync) {
      return new Short2ShortFunctions.SynchronizedFunction(f, sync);
   }

   public static Short2ShortFunction unmodifiable(Short2ShortFunction f) {
      return new Short2ShortFunctions.UnmodifiableFunction(f);
   }

   public static Short2ShortFunction primitive(Function<? super Short, ? extends Short> f) {
      Objects.requireNonNull(f);
      if (f instanceof Short2ShortFunction) {
         return (Short2ShortFunction)f;
      } else {
         return (Short2ShortFunction)(f instanceof IntUnaryOperator
            ? key -> SafeMath.safeIntToShort(((IntUnaryOperator)f).applyAsInt(key))
            : new Short2ShortFunctions.PrimitiveFunction(f));
      }
   }

   public static class EmptyFunction extends AbstractShort2ShortFunction implements Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;

      protected EmptyFunction() {
      }

      @Override
      public short get(short k) {
         return 0;
      }

      @Override
      public short getOrDefault(short k, short defaultValue) {
         return defaultValue;
      }

      @Override
      public boolean containsKey(short k) {
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
         return Short2ShortFunctions.EMPTY_FUNCTION;
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
         return Short2ShortFunctions.EMPTY_FUNCTION;
      }
   }

   public static class PrimitiveFunction implements Short2ShortFunction {
      protected final Function<? super Short, ? extends Short> function;

      protected PrimitiveFunction(Function<? super Short, ? extends Short> function) {
         this.function = function;
      }

      @Override
      public boolean containsKey(short key) {
         return this.function.apply(key) != null;
      }

      @Deprecated
      @Override
      public boolean containsKey(Object key) {
         return key == null ? false : this.function.apply((Short)key) != null;
      }

      @Override
      public short get(short key) {
         Short v = this.function.apply(key);
         return v == null ? this.defaultReturnValue() : v;
      }

      @Override
      public short getOrDefault(short key, short defaultValue) {
         Short v = this.function.apply(key);
         return v == null ? defaultValue : v;
      }

      @Deprecated
      @Override
      public Short get(Object key) {
         return key == null ? null : this.function.apply((Short)key);
      }

      @Deprecated
      @Override
      public Short getOrDefault(Object key, Short defaultValue) {
         if (key == null) {
            return defaultValue;
         } else {
            Short v;
            return (v = this.function.apply((Short)key)) == null ? defaultValue : v;
         }
      }

      @Deprecated
      @Override
      public Short put(Short key, Short value) {
         throw new UnsupportedOperationException();
      }
   }

   public static class Singleton extends AbstractShort2ShortFunction implements Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final short key;
      protected final short value;

      protected Singleton(short key, short value) {
         this.key = key;
         this.value = value;
      }

      @Override
      public boolean containsKey(short k) {
         return this.key == k;
      }

      @Override
      public short get(short k) {
         return this.key == k ? this.value : this.defRetValue;
      }

      @Override
      public short getOrDefault(short k, short defaultValue) {
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

   public static class SynchronizedFunction implements Short2ShortFunction, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Short2ShortFunction function;
      protected final Object sync;

      protected SynchronizedFunction(Short2ShortFunction f, Object sync) {
         if (f == null) {
            throw new NullPointerException();
         } else {
            this.function = f;
            this.sync = sync;
         }
      }

      protected SynchronizedFunction(Short2ShortFunction f) {
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
      public Short apply(Short key) {
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
      public boolean containsKey(short k) {
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
      public short put(short k, short v) {
         synchronized (this.sync) {
            return this.function.put(k, v);
         }
      }

      @Override
      public short get(short k) {
         synchronized (this.sync) {
            return this.function.get(k);
         }
      }

      @Override
      public short getOrDefault(short k, short defaultValue) {
         synchronized (this.sync) {
            return this.function.getOrDefault(k, defaultValue);
         }
      }

      @Override
      public short remove(short k) {
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
      public Short put(Short k, Short v) {
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

   public static class UnmodifiableFunction extends AbstractShort2ShortFunction implements Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Short2ShortFunction function;

      protected UnmodifiableFunction(Short2ShortFunction f) {
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
      public boolean containsKey(short k) {
         return this.function.containsKey(k);
      }

      @Override
      public short put(short k, short v) {
         throw new UnsupportedOperationException();
      }

      @Override
      public short get(short k) {
         return this.function.get(k);
      }

      @Override
      public short getOrDefault(short k, short defaultValue) {
         return this.function.getOrDefault(k, defaultValue);
      }

      @Override
      public short remove(short k) {
         throw new UnsupportedOperationException();
      }

      @Override
      public void clear() {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      @Override
      public Short put(Short k, Short v) {
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
