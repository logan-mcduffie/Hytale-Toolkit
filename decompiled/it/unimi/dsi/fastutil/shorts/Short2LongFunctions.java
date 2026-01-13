package it.unimi.dsi.fastutil.shorts;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.IntToLongFunction;

public final class Short2LongFunctions {
   public static final Short2LongFunctions.EmptyFunction EMPTY_FUNCTION = new Short2LongFunctions.EmptyFunction();

   private Short2LongFunctions() {
   }

   public static Short2LongFunction singleton(short key, long value) {
      return new Short2LongFunctions.Singleton(key, value);
   }

   public static Short2LongFunction singleton(Short key, Long value) {
      return new Short2LongFunctions.Singleton(key, value);
   }

   public static Short2LongFunction synchronize(Short2LongFunction f) {
      return new Short2LongFunctions.SynchronizedFunction(f);
   }

   public static Short2LongFunction synchronize(Short2LongFunction f, Object sync) {
      return new Short2LongFunctions.SynchronizedFunction(f, sync);
   }

   public static Short2LongFunction unmodifiable(Short2LongFunction f) {
      return new Short2LongFunctions.UnmodifiableFunction(f);
   }

   public static Short2LongFunction primitive(Function<? super Short, ? extends Long> f) {
      Objects.requireNonNull(f);
      if (f instanceof Short2LongFunction) {
         return (Short2LongFunction)f;
      } else {
         return (Short2LongFunction)(f instanceof IntToLongFunction ? ((IntToLongFunction)f)::applyAsLong : new Short2LongFunctions.PrimitiveFunction(f));
      }
   }

   public static class EmptyFunction extends AbstractShort2LongFunction implements Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;

      protected EmptyFunction() {
      }

      @Override
      public long get(short k) {
         return 0L;
      }

      @Override
      public long getOrDefault(short k, long defaultValue) {
         return defaultValue;
      }

      @Override
      public boolean containsKey(short k) {
         return false;
      }

      @Override
      public long defaultReturnValue() {
         return 0L;
      }

      @Override
      public void defaultReturnValue(long defRetValue) {
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
         return Short2LongFunctions.EMPTY_FUNCTION;
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
         return Short2LongFunctions.EMPTY_FUNCTION;
      }
   }

   public static class PrimitiveFunction implements Short2LongFunction {
      protected final Function<? super Short, ? extends Long> function;

      protected PrimitiveFunction(Function<? super Short, ? extends Long> function) {
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
      public long get(short key) {
         Long v = this.function.apply(key);
         return v == null ? this.defaultReturnValue() : v;
      }

      @Override
      public long getOrDefault(short key, long defaultValue) {
         Long v = this.function.apply(key);
         return v == null ? defaultValue : v;
      }

      @Deprecated
      @Override
      public Long get(Object key) {
         return key == null ? null : this.function.apply((Short)key);
      }

      @Deprecated
      @Override
      public Long getOrDefault(Object key, Long defaultValue) {
         if (key == null) {
            return defaultValue;
         } else {
            Long v;
            return (v = this.function.apply((Short)key)) == null ? defaultValue : v;
         }
      }

      @Deprecated
      @Override
      public Long put(Short key, Long value) {
         throw new UnsupportedOperationException();
      }
   }

   public static class Singleton extends AbstractShort2LongFunction implements Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final short key;
      protected final long value;

      protected Singleton(short key, long value) {
         this.key = key;
         this.value = value;
      }

      @Override
      public boolean containsKey(short k) {
         return this.key == k;
      }

      @Override
      public long get(short k) {
         return this.key == k ? this.value : this.defRetValue;
      }

      @Override
      public long getOrDefault(short k, long defaultValue) {
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

   public static class SynchronizedFunction implements Short2LongFunction, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Short2LongFunction function;
      protected final Object sync;

      protected SynchronizedFunction(Short2LongFunction f, Object sync) {
         if (f == null) {
            throw new NullPointerException();
         } else {
            this.function = f;
            this.sync = sync;
         }
      }

      protected SynchronizedFunction(Short2LongFunction f) {
         if (f == null) {
            throw new NullPointerException();
         } else {
            this.function = f;
            this.sync = this;
         }
      }

      @Deprecated
      @Override
      public long applyAsLong(int operand) {
         synchronized (this.sync) {
            return this.function.applyAsLong(operand);
         }
      }

      @Deprecated
      public Long apply(Short key) {
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
      public long defaultReturnValue() {
         synchronized (this.sync) {
            return this.function.defaultReturnValue();
         }
      }

      @Override
      public void defaultReturnValue(long defRetValue) {
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
      public long put(short k, long v) {
         synchronized (this.sync) {
            return this.function.put(k, v);
         }
      }

      @Override
      public long get(short k) {
         synchronized (this.sync) {
            return this.function.get(k);
         }
      }

      @Override
      public long getOrDefault(short k, long defaultValue) {
         synchronized (this.sync) {
            return this.function.getOrDefault(k, defaultValue);
         }
      }

      @Override
      public long remove(short k) {
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
      public Long put(Short k, Long v) {
         synchronized (this.sync) {
            return this.function.put(k, v);
         }
      }

      @Deprecated
      @Override
      public Long get(Object k) {
         synchronized (this.sync) {
            return this.function.get(k);
         }
      }

      @Deprecated
      @Override
      public Long getOrDefault(Object k, Long defaultValue) {
         synchronized (this.sync) {
            return this.function.getOrDefault(k, defaultValue);
         }
      }

      @Deprecated
      @Override
      public Long remove(Object k) {
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

   public static class UnmodifiableFunction extends AbstractShort2LongFunction implements Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Short2LongFunction function;

      protected UnmodifiableFunction(Short2LongFunction f) {
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
      public long defaultReturnValue() {
         return this.function.defaultReturnValue();
      }

      @Override
      public void defaultReturnValue(long defRetValue) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean containsKey(short k) {
         return this.function.containsKey(k);
      }

      @Override
      public long put(short k, long v) {
         throw new UnsupportedOperationException();
      }

      @Override
      public long get(short k) {
         return this.function.get(k);
      }

      @Override
      public long getOrDefault(short k, long defaultValue) {
         return this.function.getOrDefault(k, defaultValue);
      }

      @Override
      public long remove(short k) {
         throw new UnsupportedOperationException();
      }

      @Override
      public void clear() {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      @Override
      public Long put(Short k, Long v) {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      @Override
      public Long get(Object k) {
         return this.function.get(k);
      }

      @Deprecated
      @Override
      public Long getOrDefault(Object k, Long defaultValue) {
         return this.function.getOrDefault(k, defaultValue);
      }

      @Deprecated
      @Override
      public Long remove(Object k) {
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
