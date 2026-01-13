package it.unimi.dsi.fastutil.objects;

import it.unimi.dsi.fastutil.SafeMath;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.ToDoubleFunction;

public final class Reference2FloatFunctions {
   public static final Reference2FloatFunctions.EmptyFunction EMPTY_FUNCTION = new Reference2FloatFunctions.EmptyFunction();

   private Reference2FloatFunctions() {
   }

   public static <K> Reference2FloatFunction<K> singleton(K key, float value) {
      return new Reference2FloatFunctions.Singleton<>(key, value);
   }

   public static <K> Reference2FloatFunction<K> singleton(K key, Float value) {
      return new Reference2FloatFunctions.Singleton<>(key, value);
   }

   public static <K> Reference2FloatFunction<K> synchronize(Reference2FloatFunction<K> f) {
      return new Reference2FloatFunctions.SynchronizedFunction<>(f);
   }

   public static <K> Reference2FloatFunction<K> synchronize(Reference2FloatFunction<K> f, Object sync) {
      return new Reference2FloatFunctions.SynchronizedFunction<>(f, sync);
   }

   public static <K> Reference2FloatFunction<K> unmodifiable(Reference2FloatFunction<? extends K> f) {
      return new Reference2FloatFunctions.UnmodifiableFunction<>(f);
   }

   public static <K> Reference2FloatFunction<K> primitive(Function<? super K, ? extends Float> f) {
      Objects.requireNonNull(f);
      if (f instanceof Reference2FloatFunction) {
         return (Reference2FloatFunction<K>)f;
      } else {
         return (Reference2FloatFunction<K>)(f instanceof ToDoubleFunction
            ? key -> SafeMath.safeDoubleToFloat(((ToDoubleFunction)f).applyAsDouble((K)key))
            : new Reference2FloatFunctions.PrimitiveFunction<>(f));
      }
   }

   public static class EmptyFunction<K> extends AbstractReference2FloatFunction<K> implements Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;

      protected EmptyFunction() {
      }

      @Override
      public float getFloat(Object k) {
         return 0.0F;
      }

      @Override
      public float getOrDefault(Object k, float defaultValue) {
         return defaultValue;
      }

      @Override
      public boolean containsKey(Object k) {
         return false;
      }

      @Override
      public float defaultReturnValue() {
         return 0.0F;
      }

      @Override
      public void defaultReturnValue(float defRetValue) {
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
         return Reference2FloatFunctions.EMPTY_FUNCTION;
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
         return Reference2FloatFunctions.EMPTY_FUNCTION;
      }
   }

   public static class PrimitiveFunction<K> implements Reference2FloatFunction<K> {
      protected final Function<? super K, ? extends Float> function;

      protected PrimitiveFunction(Function<? super K, ? extends Float> function) {
         this.function = function;
      }

      @Override
      public boolean containsKey(Object key) {
         return this.function.apply((K)key) != null;
      }

      @Override
      public float getFloat(Object key) {
         Float v = this.function.apply((K)key);
         return v == null ? this.defaultReturnValue() : v;
      }

      @Override
      public float getOrDefault(Object key, float defaultValue) {
         Float v = this.function.apply((K)key);
         return v == null ? defaultValue : v;
      }

      @Deprecated
      @Override
      public Float get(Object key) {
         return this.function.apply((K)key);
      }

      @Deprecated
      @Override
      public Float getOrDefault(Object key, Float defaultValue) {
         Float v;
         return (v = this.function.apply((K)key)) == null ? defaultValue : v;
      }

      @Deprecated
      @Override
      public Float put(K key, Float value) {
         throw new UnsupportedOperationException();
      }
   }

   public static class Singleton<K> extends AbstractReference2FloatFunction<K> implements Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final K key;
      protected final float value;

      protected Singleton(K key, float value) {
         this.key = key;
         this.value = value;
      }

      @Override
      public boolean containsKey(Object k) {
         return this.key == k;
      }

      @Override
      public float getFloat(Object k) {
         return this.key == k ? this.value : this.defRetValue;
      }

      @Override
      public float getOrDefault(Object k, float defaultValue) {
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

   public static class SynchronizedFunction<K> implements Reference2FloatFunction<K>, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Reference2FloatFunction<K> function;
      protected final Object sync;

      protected SynchronizedFunction(Reference2FloatFunction<K> f, Object sync) {
         if (f == null) {
            throw new NullPointerException();
         } else {
            this.function = f;
            this.sync = sync;
         }
      }

      protected SynchronizedFunction(Reference2FloatFunction<K> f) {
         if (f == null) {
            throw new NullPointerException();
         } else {
            this.function = f;
            this.sync = this;
         }
      }

      @Override
      public double applyAsDouble(K operand) {
         synchronized (this.sync) {
            return this.function.applyAsDouble(operand);
         }
      }

      @Deprecated
      public Float apply(K key) {
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
      public float defaultReturnValue() {
         synchronized (this.sync) {
            return this.function.defaultReturnValue();
         }
      }

      @Override
      public void defaultReturnValue(float defRetValue) {
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
      public float put(K k, float v) {
         synchronized (this.sync) {
            return this.function.put(k, v);
         }
      }

      @Override
      public float getFloat(Object k) {
         synchronized (this.sync) {
            return this.function.getFloat(k);
         }
      }

      @Override
      public float getOrDefault(Object k, float defaultValue) {
         synchronized (this.sync) {
            return this.function.getOrDefault(k, defaultValue);
         }
      }

      @Override
      public float removeFloat(Object k) {
         synchronized (this.sync) {
            return this.function.removeFloat(k);
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
      public Float put(K k, Float v) {
         synchronized (this.sync) {
            return this.function.put(k, v);
         }
      }

      @Deprecated
      @Override
      public Float get(Object k) {
         synchronized (this.sync) {
            return this.function.get(k);
         }
      }

      @Deprecated
      @Override
      public Float getOrDefault(Object k, Float defaultValue) {
         synchronized (this.sync) {
            return this.function.getOrDefault(k, defaultValue);
         }
      }

      @Deprecated
      @Override
      public Float remove(Object k) {
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

   public static class UnmodifiableFunction<K> extends AbstractReference2FloatFunction<K> implements Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Reference2FloatFunction<? extends K> function;

      protected UnmodifiableFunction(Reference2FloatFunction<? extends K> f) {
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
      public float defaultReturnValue() {
         return this.function.defaultReturnValue();
      }

      @Override
      public void defaultReturnValue(float defRetValue) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean containsKey(Object k) {
         return this.function.containsKey(k);
      }

      @Override
      public float put(K k, float v) {
         throw new UnsupportedOperationException();
      }

      @Override
      public float getFloat(Object k) {
         return this.function.getFloat(k);
      }

      @Override
      public float getOrDefault(Object k, float defaultValue) {
         return this.function.getOrDefault(k, defaultValue);
      }

      @Override
      public float removeFloat(Object k) {
         throw new UnsupportedOperationException();
      }

      @Override
      public void clear() {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      @Override
      public Float put(K k, Float v) {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      @Override
      public Float get(Object k) {
         return this.function.get(k);
      }

      @Deprecated
      @Override
      public Float getOrDefault(Object k, Float defaultValue) {
         return this.function.getOrDefault(k, defaultValue);
      }

      @Deprecated
      @Override
      public Float remove(Object k) {
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
