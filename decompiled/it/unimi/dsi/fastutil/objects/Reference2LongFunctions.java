package it.unimi.dsi.fastutil.objects;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.ToLongFunction;

public final class Reference2LongFunctions {
   public static final Reference2LongFunctions.EmptyFunction EMPTY_FUNCTION = new Reference2LongFunctions.EmptyFunction();

   private Reference2LongFunctions() {
   }

   public static <K> Reference2LongFunction<K> singleton(K key, long value) {
      return new Reference2LongFunctions.Singleton<>(key, value);
   }

   public static <K> Reference2LongFunction<K> singleton(K key, Long value) {
      return new Reference2LongFunctions.Singleton<>(key, value);
   }

   public static <K> Reference2LongFunction<K> synchronize(Reference2LongFunction<K> f) {
      return new Reference2LongFunctions.SynchronizedFunction<>(f);
   }

   public static <K> Reference2LongFunction<K> synchronize(Reference2LongFunction<K> f, Object sync) {
      return new Reference2LongFunctions.SynchronizedFunction<>(f, sync);
   }

   public static <K> Reference2LongFunction<K> unmodifiable(Reference2LongFunction<? extends K> f) {
      return new Reference2LongFunctions.UnmodifiableFunction<>(f);
   }

   public static <K> Reference2LongFunction<K> primitive(Function<? super K, ? extends Long> f) {
      Objects.requireNonNull(f);
      if (f instanceof Reference2LongFunction) {
         return (Reference2LongFunction<K>)f;
      } else {
         return (Reference2LongFunction<K>)(f instanceof ToLongFunction
            ? key -> ((ToLongFunction)f).applyAsLong((K)key)
            : new Reference2LongFunctions.PrimitiveFunction<>(f));
      }
   }

   public static class EmptyFunction<K> extends AbstractReference2LongFunction<K> implements Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;

      protected EmptyFunction() {
      }

      @Override
      public long getLong(Object k) {
         return 0L;
      }

      @Override
      public long getOrDefault(Object k, long defaultValue) {
         return defaultValue;
      }

      @Override
      public boolean containsKey(Object k) {
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
         return Reference2LongFunctions.EMPTY_FUNCTION;
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
         return Reference2LongFunctions.EMPTY_FUNCTION;
      }
   }

   public static class PrimitiveFunction<K> implements Reference2LongFunction<K> {
      protected final Function<? super K, ? extends Long> function;

      protected PrimitiveFunction(Function<? super K, ? extends Long> function) {
         this.function = function;
      }

      @Override
      public boolean containsKey(Object key) {
         return this.function.apply((K)key) != null;
      }

      @Override
      public long getLong(Object key) {
         Long v = this.function.apply((K)key);
         return v == null ? this.defaultReturnValue() : v;
      }

      @Override
      public long getOrDefault(Object key, long defaultValue) {
         Long v = this.function.apply((K)key);
         return v == null ? defaultValue : v;
      }

      @Deprecated
      @Override
      public Long get(Object key) {
         return this.function.apply((K)key);
      }

      @Deprecated
      @Override
      public Long getOrDefault(Object key, Long defaultValue) {
         Long v;
         return (v = this.function.apply((K)key)) == null ? defaultValue : v;
      }

      @Deprecated
      @Override
      public Long put(K key, Long value) {
         throw new UnsupportedOperationException();
      }
   }

   public static class Singleton<K> extends AbstractReference2LongFunction<K> implements Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final K key;
      protected final long value;

      protected Singleton(K key, long value) {
         this.key = key;
         this.value = value;
      }

      @Override
      public boolean containsKey(Object k) {
         return this.key == k;
      }

      @Override
      public long getLong(Object k) {
         return this.key == k ? this.value : this.defRetValue;
      }

      @Override
      public long getOrDefault(Object k, long defaultValue) {
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

   public static class SynchronizedFunction<K> implements Reference2LongFunction<K>, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Reference2LongFunction<K> function;
      protected final Object sync;

      protected SynchronizedFunction(Reference2LongFunction<K> f, Object sync) {
         if (f == null) {
            throw new NullPointerException();
         } else {
            this.function = f;
            this.sync = sync;
         }
      }

      protected SynchronizedFunction(Reference2LongFunction<K> f) {
         if (f == null) {
            throw new NullPointerException();
         } else {
            this.function = f;
            this.sync = this;
         }
      }

      @Override
      public long applyAsLong(K operand) {
         synchronized (this.sync) {
            return this.function.applyAsLong(operand);
         }
      }

      @Deprecated
      public Long apply(K key) {
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
      public boolean containsKey(Object k) {
         synchronized (this.sync) {
            return this.function.containsKey(k);
         }
      }

      @Override
      public long put(K k, long v) {
         synchronized (this.sync) {
            return this.function.put(k, v);
         }
      }

      @Override
      public long getLong(Object k) {
         synchronized (this.sync) {
            return this.function.getLong(k);
         }
      }

      @Override
      public long getOrDefault(Object k, long defaultValue) {
         synchronized (this.sync) {
            return this.function.getOrDefault(k, defaultValue);
         }
      }

      @Override
      public long removeLong(Object k) {
         synchronized (this.sync) {
            return this.function.removeLong(k);
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
      public Long put(K k, Long v) {
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

   public static class UnmodifiableFunction<K> extends AbstractReference2LongFunction<K> implements Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Reference2LongFunction<? extends K> function;

      protected UnmodifiableFunction(Reference2LongFunction<? extends K> f) {
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
      public boolean containsKey(Object k) {
         return this.function.containsKey(k);
      }

      @Override
      public long put(K k, long v) {
         throw new UnsupportedOperationException();
      }

      @Override
      public long getLong(Object k) {
         return this.function.getLong(k);
      }

      @Override
      public long getOrDefault(Object k, long defaultValue) {
         return this.function.getOrDefault(k, defaultValue);
      }

      @Override
      public long removeLong(Object k) {
         throw new UnsupportedOperationException();
      }

      @Override
      public void clear() {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      @Override
      public Long put(K k, Long v) {
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
