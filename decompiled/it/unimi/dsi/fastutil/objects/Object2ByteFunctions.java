package it.unimi.dsi.fastutil.objects;

import it.unimi.dsi.fastutil.SafeMath;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.ToIntFunction;

public final class Object2ByteFunctions {
   public static final Object2ByteFunctions.EmptyFunction EMPTY_FUNCTION = new Object2ByteFunctions.EmptyFunction();

   private Object2ByteFunctions() {
   }

   public static <K> Object2ByteFunction<K> singleton(K key, byte value) {
      return new Object2ByteFunctions.Singleton<>(key, value);
   }

   public static <K> Object2ByteFunction<K> singleton(K key, Byte value) {
      return new Object2ByteFunctions.Singleton<>(key, value);
   }

   public static <K> Object2ByteFunction<K> synchronize(Object2ByteFunction<K> f) {
      return new Object2ByteFunctions.SynchronizedFunction<>(f);
   }

   public static <K> Object2ByteFunction<K> synchronize(Object2ByteFunction<K> f, Object sync) {
      return new Object2ByteFunctions.SynchronizedFunction<>(f, sync);
   }

   public static <K> Object2ByteFunction<K> unmodifiable(Object2ByteFunction<? extends K> f) {
      return new Object2ByteFunctions.UnmodifiableFunction<>(f);
   }

   public static <K> Object2ByteFunction<K> primitive(Function<? super K, ? extends Byte> f) {
      Objects.requireNonNull(f);
      if (f instanceof Object2ByteFunction) {
         return (Object2ByteFunction<K>)f;
      } else {
         return (Object2ByteFunction<K>)(f instanceof ToIntFunction
            ? key -> SafeMath.safeIntToByte(((ToIntFunction)f).applyAsInt((K)key))
            : new Object2ByteFunctions.PrimitiveFunction<>(f));
      }
   }

   public static class EmptyFunction<K> extends AbstractObject2ByteFunction<K> implements Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;

      protected EmptyFunction() {
      }

      @Override
      public byte getByte(Object k) {
         return 0;
      }

      @Override
      public byte getOrDefault(Object k, byte defaultValue) {
         return defaultValue;
      }

      @Override
      public boolean containsKey(Object k) {
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
         return Object2ByteFunctions.EMPTY_FUNCTION;
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
         return Object2ByteFunctions.EMPTY_FUNCTION;
      }
   }

   public static class PrimitiveFunction<K> implements Object2ByteFunction<K> {
      protected final Function<? super K, ? extends Byte> function;

      protected PrimitiveFunction(Function<? super K, ? extends Byte> function) {
         this.function = function;
      }

      @Override
      public boolean containsKey(Object key) {
         return this.function.apply((K)key) != null;
      }

      @Override
      public byte getByte(Object key) {
         Byte v = this.function.apply((K)key);
         return v == null ? this.defaultReturnValue() : v;
      }

      @Override
      public byte getOrDefault(Object key, byte defaultValue) {
         Byte v = this.function.apply((K)key);
         return v == null ? defaultValue : v;
      }

      @Deprecated
      @Override
      public Byte get(Object key) {
         return this.function.apply((K)key);
      }

      @Deprecated
      @Override
      public Byte getOrDefault(Object key, Byte defaultValue) {
         Byte v;
         return (v = this.function.apply((K)key)) == null ? defaultValue : v;
      }

      @Deprecated
      @Override
      public Byte put(K key, Byte value) {
         throw new UnsupportedOperationException();
      }
   }

   public static class Singleton<K> extends AbstractObject2ByteFunction<K> implements Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final K key;
      protected final byte value;

      protected Singleton(K key, byte value) {
         this.key = key;
         this.value = value;
      }

      @Override
      public boolean containsKey(Object k) {
         return Objects.equals(this.key, k);
      }

      @Override
      public byte getByte(Object k) {
         return Objects.equals(this.key, k) ? this.value : this.defRetValue;
      }

      @Override
      public byte getOrDefault(Object k, byte defaultValue) {
         return Objects.equals(this.key, k) ? this.value : defaultValue;
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

   public static class SynchronizedFunction<K> implements Object2ByteFunction<K>, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Object2ByteFunction<K> function;
      protected final Object sync;

      protected SynchronizedFunction(Object2ByteFunction<K> f, Object sync) {
         if (f == null) {
            throw new NullPointerException();
         } else {
            this.function = f;
            this.sync = sync;
         }
      }

      protected SynchronizedFunction(Object2ByteFunction<K> f) {
         if (f == null) {
            throw new NullPointerException();
         } else {
            this.function = f;
            this.sync = this;
         }
      }

      @Override
      public int applyAsInt(K operand) {
         synchronized (this.sync) {
            return this.function.applyAsInt(operand);
         }
      }

      @Deprecated
      public Byte apply(K key) {
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
      public boolean containsKey(Object k) {
         synchronized (this.sync) {
            return this.function.containsKey(k);
         }
      }

      @Override
      public byte put(K k, byte v) {
         synchronized (this.sync) {
            return this.function.put(k, v);
         }
      }

      @Override
      public byte getByte(Object k) {
         synchronized (this.sync) {
            return this.function.getByte(k);
         }
      }

      @Override
      public byte getOrDefault(Object k, byte defaultValue) {
         synchronized (this.sync) {
            return this.function.getOrDefault(k, defaultValue);
         }
      }

      @Override
      public byte removeByte(Object k) {
         synchronized (this.sync) {
            return this.function.removeByte(k);
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
      public Byte put(K k, Byte v) {
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

   public static class UnmodifiableFunction<K> extends AbstractObject2ByteFunction<K> implements Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Object2ByteFunction<? extends K> function;

      protected UnmodifiableFunction(Object2ByteFunction<? extends K> f) {
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
      public boolean containsKey(Object k) {
         return this.function.containsKey(k);
      }

      @Override
      public byte put(K k, byte v) {
         throw new UnsupportedOperationException();
      }

      @Override
      public byte getByte(Object k) {
         return this.function.getByte(k);
      }

      @Override
      public byte getOrDefault(Object k, byte defaultValue) {
         return this.function.getOrDefault(k, defaultValue);
      }

      @Override
      public byte removeByte(Object k) {
         throw new UnsupportedOperationException();
      }

      @Override
      public void clear() {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      @Override
      public Byte put(K k, Byte v) {
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
