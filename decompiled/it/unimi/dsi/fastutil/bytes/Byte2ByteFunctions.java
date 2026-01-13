package it.unimi.dsi.fastutil.bytes;

import it.unimi.dsi.fastutil.SafeMath;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.IntUnaryOperator;

public final class Byte2ByteFunctions {
   public static final Byte2ByteFunctions.EmptyFunction EMPTY_FUNCTION = new Byte2ByteFunctions.EmptyFunction();

   private Byte2ByteFunctions() {
   }

   public static Byte2ByteFunction singleton(byte key, byte value) {
      return new Byte2ByteFunctions.Singleton(key, value);
   }

   public static Byte2ByteFunction singleton(Byte key, Byte value) {
      return new Byte2ByteFunctions.Singleton(key, value);
   }

   public static Byte2ByteFunction synchronize(Byte2ByteFunction f) {
      return new Byte2ByteFunctions.SynchronizedFunction(f);
   }

   public static Byte2ByteFunction synchronize(Byte2ByteFunction f, Object sync) {
      return new Byte2ByteFunctions.SynchronizedFunction(f, sync);
   }

   public static Byte2ByteFunction unmodifiable(Byte2ByteFunction f) {
      return new Byte2ByteFunctions.UnmodifiableFunction(f);
   }

   public static Byte2ByteFunction primitive(Function<? super Byte, ? extends Byte> f) {
      Objects.requireNonNull(f);
      if (f instanceof Byte2ByteFunction) {
         return (Byte2ByteFunction)f;
      } else {
         return (Byte2ByteFunction)(f instanceof IntUnaryOperator
            ? key -> SafeMath.safeIntToByte(((IntUnaryOperator)f).applyAsInt(key))
            : new Byte2ByteFunctions.PrimitiveFunction(f));
      }
   }

   public static class EmptyFunction extends AbstractByte2ByteFunction implements Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;

      protected EmptyFunction() {
      }

      @Override
      public byte get(byte k) {
         return 0;
      }

      @Override
      public byte getOrDefault(byte k, byte defaultValue) {
         return defaultValue;
      }

      @Override
      public boolean containsKey(byte k) {
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
         return Byte2ByteFunctions.EMPTY_FUNCTION;
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
         return Byte2ByteFunctions.EMPTY_FUNCTION;
      }
   }

   public static class PrimitiveFunction implements Byte2ByteFunction {
      protected final Function<? super Byte, ? extends Byte> function;

      protected PrimitiveFunction(Function<? super Byte, ? extends Byte> function) {
         this.function = function;
      }

      @Override
      public boolean containsKey(byte key) {
         return this.function.apply(key) != null;
      }

      @Deprecated
      @Override
      public boolean containsKey(Object key) {
         return key == null ? false : this.function.apply((Byte)key) != null;
      }

      @Override
      public byte get(byte key) {
         Byte v = this.function.apply(key);
         return v == null ? this.defaultReturnValue() : v;
      }

      @Override
      public byte getOrDefault(byte key, byte defaultValue) {
         Byte v = this.function.apply(key);
         return v == null ? defaultValue : v;
      }

      @Deprecated
      @Override
      public Byte get(Object key) {
         return key == null ? null : this.function.apply((Byte)key);
      }

      @Deprecated
      @Override
      public Byte getOrDefault(Object key, Byte defaultValue) {
         if (key == null) {
            return defaultValue;
         } else {
            Byte v;
            return (v = this.function.apply((Byte)key)) == null ? defaultValue : v;
         }
      }

      @Deprecated
      @Override
      public Byte put(Byte key, Byte value) {
         throw new UnsupportedOperationException();
      }
   }

   public static class Singleton extends AbstractByte2ByteFunction implements Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final byte key;
      protected final byte value;

      protected Singleton(byte key, byte value) {
         this.key = key;
         this.value = value;
      }

      @Override
      public boolean containsKey(byte k) {
         return this.key == k;
      }

      @Override
      public byte get(byte k) {
         return this.key == k ? this.value : this.defRetValue;
      }

      @Override
      public byte getOrDefault(byte k, byte defaultValue) {
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

   public static class SynchronizedFunction implements Byte2ByteFunction, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Byte2ByteFunction function;
      protected final Object sync;

      protected SynchronizedFunction(Byte2ByteFunction f, Object sync) {
         if (f == null) {
            throw new NullPointerException();
         } else {
            this.function = f;
            this.sync = sync;
         }
      }

      protected SynchronizedFunction(Byte2ByteFunction f) {
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
      public Byte apply(Byte key) {
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
      public boolean containsKey(byte k) {
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
      public byte put(byte k, byte v) {
         synchronized (this.sync) {
            return this.function.put(k, v);
         }
      }

      @Override
      public byte get(byte k) {
         synchronized (this.sync) {
            return this.function.get(k);
         }
      }

      @Override
      public byte getOrDefault(byte k, byte defaultValue) {
         synchronized (this.sync) {
            return this.function.getOrDefault(k, defaultValue);
         }
      }

      @Override
      public byte remove(byte k) {
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
      public Byte put(Byte k, Byte v) {
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

   public static class UnmodifiableFunction extends AbstractByte2ByteFunction implements Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Byte2ByteFunction function;

      protected UnmodifiableFunction(Byte2ByteFunction f) {
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
      public boolean containsKey(byte k) {
         return this.function.containsKey(k);
      }

      @Override
      public byte put(byte k, byte v) {
         throw new UnsupportedOperationException();
      }

      @Override
      public byte get(byte k) {
         return this.function.get(k);
      }

      @Override
      public byte getOrDefault(byte k, byte defaultValue) {
         return this.function.getOrDefault(k, defaultValue);
      }

      @Override
      public byte remove(byte k) {
         throw new UnsupportedOperationException();
      }

      @Override
      public void clear() {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      @Override
      public Byte put(Byte k, Byte v) {
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
