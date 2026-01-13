package it.unimi.dsi.fastutil.longs;

import it.unimi.dsi.fastutil.SafeMath;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.LongToIntFunction;

public final class Long2ByteFunctions {
   public static final Long2ByteFunctions.EmptyFunction EMPTY_FUNCTION = new Long2ByteFunctions.EmptyFunction();

   private Long2ByteFunctions() {
   }

   public static Long2ByteFunction singleton(long key, byte value) {
      return new Long2ByteFunctions.Singleton(key, value);
   }

   public static Long2ByteFunction singleton(Long key, Byte value) {
      return new Long2ByteFunctions.Singleton(key, value);
   }

   public static Long2ByteFunction synchronize(Long2ByteFunction f) {
      return new Long2ByteFunctions.SynchronizedFunction(f);
   }

   public static Long2ByteFunction synchronize(Long2ByteFunction f, Object sync) {
      return new Long2ByteFunctions.SynchronizedFunction(f, sync);
   }

   public static Long2ByteFunction unmodifiable(Long2ByteFunction f) {
      return new Long2ByteFunctions.UnmodifiableFunction(f);
   }

   public static Long2ByteFunction primitive(Function<? super Long, ? extends Byte> f) {
      Objects.requireNonNull(f);
      if (f instanceof Long2ByteFunction) {
         return (Long2ByteFunction)f;
      } else {
         return (Long2ByteFunction)(f instanceof LongToIntFunction
            ? key -> SafeMath.safeIntToByte(((LongToIntFunction)f).applyAsInt(key))
            : new Long2ByteFunctions.PrimitiveFunction(f));
      }
   }

   public static class EmptyFunction extends AbstractLong2ByteFunction implements Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;

      protected EmptyFunction() {
      }

      @Override
      public byte get(long k) {
         return 0;
      }

      @Override
      public byte getOrDefault(long k, byte defaultValue) {
         return defaultValue;
      }

      @Override
      public boolean containsKey(long k) {
         return false;
      }

      @Override
      public byte defaultReturnValue() {
         return 0;
      }

      @Override
      public void defaultReturnValue(byte defRetValue) {
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
         return Long2ByteFunctions.EMPTY_FUNCTION;
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
         return Long2ByteFunctions.EMPTY_FUNCTION;
      }
   }

   public static class PrimitiveFunction implements Long2ByteFunction {
      protected final Function<? super Long, ? extends Byte> function;

      protected PrimitiveFunction(Function<? super Long, ? extends Byte> function) {
         this.function = function;
      }

      @Override
      public boolean containsKey(long key) {
         return this.function.apply(key) != null;
      }

      @Deprecated
      @Override
      public boolean containsKey(Object key) {
         return key == null ? false : this.function.apply((Long)key) != null;
      }

      @Override
      public byte get(long key) {
         Byte v = this.function.apply(key);
         return v == null ? this.defaultReturnValue() : v;
      }

      @Override
      public byte getOrDefault(long key, byte defaultValue) {
         Byte v = this.function.apply(key);
         return v == null ? defaultValue : v;
      }

      @Deprecated
      @Override
      public Byte get(Object key) {
         return key == null ? null : this.function.apply((Long)key);
      }

      @Deprecated
      @Override
      public Byte getOrDefault(Object key, Byte defaultValue) {
         if (key == null) {
            return defaultValue;
         } else {
            Byte v;
            return (v = this.function.apply((Long)key)) == null ? defaultValue : v;
         }
      }

      @Deprecated
      @Override
      public Byte put(Long key, Byte value) {
         throw new UnsupportedOperationException();
      }
   }

   public static class Singleton extends AbstractLong2ByteFunction implements Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final long key;
      protected final byte value;

      protected Singleton(long key, byte value) {
         this.key = key;
         this.value = value;
      }

      @Override
      public boolean containsKey(long k) {
         return this.key == k;
      }

      @Override
      public byte get(long k) {
         return this.key == k ? this.value : this.defRetValue;
      }

      @Override
      public byte getOrDefault(long k, byte defaultValue) {
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

   public static class SynchronizedFunction implements Long2ByteFunction, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Long2ByteFunction function;
      protected final Object sync;

      protected SynchronizedFunction(Long2ByteFunction f, Object sync) {
         if (f == null) {
            throw new NullPointerException();
         } else {
            this.function = f;
            this.sync = sync;
         }
      }

      protected SynchronizedFunction(Long2ByteFunction f) {
         if (f == null) {
            throw new NullPointerException();
         } else {
            this.function = f;
            this.sync = this;
         }
      }

      @Override
      public int applyAsInt(long operand) {
         synchronized (this.sync) {
            return this.function.applyAsInt(operand);
         }
      }

      @Deprecated
      public Byte apply(Long key) {
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
      public byte defaultReturnValue() {
         synchronized (this.sync) {
            return this.function.defaultReturnValue();
         }
      }

      @Override
      public void defaultReturnValue(byte defRetValue) {
         synchronized (this.sync) {
            this.function.defaultReturnValue(defRetValue);
         }
      }

      @Override
      public boolean containsKey(long k) {
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
      public byte put(long k, byte v) {
         synchronized (this.sync) {
            return this.function.put(k, v);
         }
      }

      @Override
      public byte get(long k) {
         synchronized (this.sync) {
            return this.function.get(k);
         }
      }

      @Override
      public byte getOrDefault(long k, byte defaultValue) {
         synchronized (this.sync) {
            return this.function.getOrDefault(k, defaultValue);
         }
      }

      @Override
      public byte remove(long k) {
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
      public Byte put(Long k, Byte v) {
         synchronized (this.sync) {
            return this.function.put(k, v);
         }
      }

      @Deprecated
      @Override
      public Byte get(Object k) {
         synchronized (this.sync) {
            return this.function.get(k);
         }
      }

      @Deprecated
      @Override
      public Byte getOrDefault(Object k, Byte defaultValue) {
         synchronized (this.sync) {
            return this.function.getOrDefault(k, defaultValue);
         }
      }

      @Deprecated
      @Override
      public Byte remove(Object k) {
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

   public static class UnmodifiableFunction extends AbstractLong2ByteFunction implements Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Long2ByteFunction function;

      protected UnmodifiableFunction(Long2ByteFunction f) {
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
      public byte defaultReturnValue() {
         return this.function.defaultReturnValue();
      }

      @Override
      public void defaultReturnValue(byte defRetValue) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean containsKey(long k) {
         return this.function.containsKey(k);
      }

      @Override
      public byte put(long k, byte v) {
         throw new UnsupportedOperationException();
      }

      @Override
      public byte get(long k) {
         return this.function.get(k);
      }

      @Override
      public byte getOrDefault(long k, byte defaultValue) {
         return this.function.getOrDefault(k, defaultValue);
      }

      @Override
      public byte remove(long k) {
         throw new UnsupportedOperationException();
      }

      @Override
      public void clear() {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      @Override
      public Byte put(Long k, Byte v) {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      @Override
      public Byte get(Object k) {
         return this.function.get(k);
      }

      @Deprecated
      @Override
      public Byte getOrDefault(Object k, Byte defaultValue) {
         return this.function.getOrDefault(k, defaultValue);
      }

      @Deprecated
      @Override
      public Byte remove(Object k) {
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
