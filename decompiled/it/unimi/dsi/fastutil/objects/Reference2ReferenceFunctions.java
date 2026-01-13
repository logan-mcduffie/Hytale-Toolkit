package it.unimi.dsi.fastutil.objects;

import it.unimi.dsi.fastutil.Function;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public final class Reference2ReferenceFunctions {
   public static final Reference2ReferenceFunctions.EmptyFunction EMPTY_FUNCTION = new Reference2ReferenceFunctions.EmptyFunction();

   private Reference2ReferenceFunctions() {
   }

   public static <K, V> Reference2ReferenceFunction<K, V> singleton(K key, V value) {
      return new Reference2ReferenceFunctions.Singleton<>(key, value);
   }

   public static <K, V> Reference2ReferenceFunction<K, V> synchronize(Reference2ReferenceFunction<K, V> f) {
      return new Reference2ReferenceFunctions.SynchronizedFunction<>(f);
   }

   public static <K, V> Reference2ReferenceFunction<K, V> synchronize(Reference2ReferenceFunction<K, V> f, Object sync) {
      return new Reference2ReferenceFunctions.SynchronizedFunction<>(f, sync);
   }

   public static <K, V> Reference2ReferenceFunction<K, V> unmodifiable(Reference2ReferenceFunction<? extends K, ? extends V> f) {
      return new Reference2ReferenceFunctions.UnmodifiableFunction<>(f);
   }

   public static class EmptyFunction<K, V> extends AbstractReference2ReferenceFunction<K, V> implements Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;

      protected EmptyFunction() {
      }

      @Override
      public V get(Object k) {
         return null;
      }

      @Override
      public V getOrDefault(Object k, V defaultValue) {
         return defaultValue;
      }

      @Override
      public boolean containsKey(Object k) {
         return false;
      }

      @Override
      public V defaultReturnValue() {
         return null;
      }

      @Override
      public void defaultReturnValue(V defRetValue) {
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
         return Reference2ReferenceFunctions.EMPTY_FUNCTION;
      }

      @Override
      public int hashCode() {
         return 0;
      }

      @Override
      public boolean equals(Object o) {
         return !(o instanceof Function) ? false : ((Function)o).size() == 0;
      }

      @Override
      public String toString() {
         return "{}";
      }

      private Object readResolve() {
         return Reference2ReferenceFunctions.EMPTY_FUNCTION;
      }
   }

   public static class Singleton<K, V> extends AbstractReference2ReferenceFunction<K, V> implements Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final K key;
      protected final V value;

      protected Singleton(K key, V value) {
         this.key = key;
         this.value = value;
      }

      @Override
      public boolean containsKey(Object k) {
         return this.key == k;
      }

      @Override
      public V get(Object k) {
         return this.key == k ? this.value : this.defRetValue;
      }

      @Override
      public V getOrDefault(Object k, V defaultValue) {
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

   public static class SynchronizedFunction<K, V> implements Reference2ReferenceFunction<K, V>, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Reference2ReferenceFunction<K, V> function;
      protected final Object sync;

      protected SynchronizedFunction(Reference2ReferenceFunction<K, V> f, Object sync) {
         if (f == null) {
            throw new NullPointerException();
         } else {
            this.function = f;
            this.sync = sync;
         }
      }

      protected SynchronizedFunction(Reference2ReferenceFunction<K, V> f) {
         if (f == null) {
            throw new NullPointerException();
         } else {
            this.function = f;
            this.sync = this;
         }
      }

      @Override
      public V apply(K key) {
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
      public V defaultReturnValue() {
         synchronized (this.sync) {
            return this.function.defaultReturnValue();
         }
      }

      @Override
      public void defaultReturnValue(V defRetValue) {
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
      public V put(K k, V v) {
         synchronized (this.sync) {
            return this.function.put(k, v);
         }
      }

      @Override
      public V get(Object k) {
         synchronized (this.sync) {
            return this.function.get(k);
         }
      }

      @Override
      public V getOrDefault(Object k, V defaultValue) {
         synchronized (this.sync) {
            return this.function.getOrDefault(k, defaultValue);
         }
      }

      @Override
      public V remove(Object k) {
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

   public static class UnmodifiableFunction<K, V> extends AbstractReference2ReferenceFunction<K, V> implements Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Reference2ReferenceFunction<? extends K, ? extends V> function;

      protected UnmodifiableFunction(Reference2ReferenceFunction<? extends K, ? extends V> f) {
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
      public V defaultReturnValue() {
         return (V)this.function.defaultReturnValue();
      }

      @Override
      public void defaultReturnValue(V defRetValue) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean containsKey(Object k) {
         return this.function.containsKey(k);
      }

      @Override
      public V put(K k, V v) {
         throw new UnsupportedOperationException();
      }

      @Override
      public V get(Object k) {
         return (V)this.function.get(k);
      }

      @Override
      public V getOrDefault(Object k, V defaultValue) {
         return (V)this.function.getOrDefault(k, defaultValue);
      }

      @Override
      public V remove(Object k) {
         throw new UnsupportedOperationException();
      }

      @Override
      public void clear() {
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
