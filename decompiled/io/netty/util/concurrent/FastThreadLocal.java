package io.netty.util.concurrent;

import io.netty.util.internal.InternalThreadLocalMap;
import io.netty.util.internal.PlatformDependent;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Set;

public class FastThreadLocal<V> {
   private final int index = InternalThreadLocalMap.nextVariableIndex();

   public static void removeAll() {
      InternalThreadLocalMap threadLocalMap = InternalThreadLocalMap.getIfSet();
      if (threadLocalMap != null) {
         try {
            Object v = threadLocalMap.indexedVariable(InternalThreadLocalMap.VARIABLES_TO_REMOVE_INDEX);
            if (v != null && v != InternalThreadLocalMap.UNSET) {
               Set<FastThreadLocal<?>> variablesToRemove = (Set<FastThreadLocal<?>>)v;
               FastThreadLocal<?>[] variablesToRemoveArray = variablesToRemove.toArray(new FastThreadLocal[0]);

               for (FastThreadLocal<?> tlv : variablesToRemoveArray) {
                  tlv.remove(threadLocalMap);
               }
            }
         } finally {
            InternalThreadLocalMap.remove();
         }
      }
   }

   public static int size() {
      InternalThreadLocalMap threadLocalMap = InternalThreadLocalMap.getIfSet();
      return threadLocalMap == null ? 0 : threadLocalMap.size();
   }

   public static void destroy() {
      InternalThreadLocalMap.destroy();
   }

   private static void addToVariablesToRemove(InternalThreadLocalMap threadLocalMap, FastThreadLocal<?> variable) {
      Object v = threadLocalMap.indexedVariable(InternalThreadLocalMap.VARIABLES_TO_REMOVE_INDEX);
      Set<FastThreadLocal<?>> variablesToRemove;
      if (v != InternalThreadLocalMap.UNSET && v != null) {
         variablesToRemove = (Set<FastThreadLocal<?>>)v;
      } else {
         variablesToRemove = Collections.newSetFromMap(new IdentityHashMap<>());
         threadLocalMap.setIndexedVariable(InternalThreadLocalMap.VARIABLES_TO_REMOVE_INDEX, variablesToRemove);
      }

      variablesToRemove.add(variable);
   }

   private static void removeFromVariablesToRemove(InternalThreadLocalMap threadLocalMap, FastThreadLocal<?> variable) {
      Object v = threadLocalMap.indexedVariable(InternalThreadLocalMap.VARIABLES_TO_REMOVE_INDEX);
      if (v != InternalThreadLocalMap.UNSET && v != null) {
         Set<FastThreadLocal<?>> variablesToRemove = (Set<FastThreadLocal<?>>)v;
         variablesToRemove.remove(variable);
      }
   }

   public final V get() {
      InternalThreadLocalMap threadLocalMap = InternalThreadLocalMap.get();
      Object v = threadLocalMap.indexedVariable(this.index);
      return (V)(v != InternalThreadLocalMap.UNSET ? v : this.initialize(threadLocalMap));
   }

   public final V getIfExists() {
      InternalThreadLocalMap threadLocalMap = InternalThreadLocalMap.getIfSet();
      if (threadLocalMap != null) {
         Object v = threadLocalMap.indexedVariable(this.index);
         if (v != InternalThreadLocalMap.UNSET) {
            return (V)v;
         }
      }

      return null;
   }

   public final V get(InternalThreadLocalMap threadLocalMap) {
      Object v = threadLocalMap.indexedVariable(this.index);
      return (V)(v != InternalThreadLocalMap.UNSET ? v : this.initialize(threadLocalMap));
   }

   private V initialize(InternalThreadLocalMap threadLocalMap) {
      V v = null;

      try {
         v = this.initialValue();
         if (v == InternalThreadLocalMap.UNSET) {
            throw new IllegalArgumentException("InternalThreadLocalMap.UNSET can not be initial value.");
         }
      } catch (Exception var4) {
         PlatformDependent.throwException(var4);
      }

      threadLocalMap.setIndexedVariable(this.index, v);
      addToVariablesToRemove(threadLocalMap, this);
      return v;
   }

   public final void set(V value) {
      this.getAndSet(value);
   }

   public final void set(InternalThreadLocalMap threadLocalMap, V value) {
      this.getAndSet(threadLocalMap, value);
   }

   public V getAndSet(V value) {
      if (value != InternalThreadLocalMap.UNSET) {
         InternalThreadLocalMap threadLocalMap = InternalThreadLocalMap.get();
         return this.setKnownNotUnset(threadLocalMap, value);
      } else {
         return this.removeAndGet(InternalThreadLocalMap.getIfSet());
      }
   }

   public V getAndSet(InternalThreadLocalMap threadLocalMap, V value) {
      return value != InternalThreadLocalMap.UNSET ? this.setKnownNotUnset(threadLocalMap, value) : this.removeAndGet(threadLocalMap);
   }

   private V setKnownNotUnset(InternalThreadLocalMap threadLocalMap, V value) {
      V old = (V)threadLocalMap.getAndSetIndexedVariable(this.index, value);
      if (old == InternalThreadLocalMap.UNSET) {
         addToVariablesToRemove(threadLocalMap, this);
         return null;
      } else {
         return old;
      }
   }

   public final boolean isSet() {
      return this.isSet(InternalThreadLocalMap.getIfSet());
   }

   public final boolean isSet(InternalThreadLocalMap threadLocalMap) {
      return threadLocalMap != null && threadLocalMap.isIndexedVariableSet(this.index);
   }

   public final void remove() {
      this.remove(InternalThreadLocalMap.getIfSet());
   }

   public final void remove(InternalThreadLocalMap threadLocalMap) {
      this.removeAndGet(threadLocalMap);
   }

   private V removeAndGet(InternalThreadLocalMap threadLocalMap) {
      if (threadLocalMap == null) {
         return null;
      } else {
         Object v = threadLocalMap.removeIndexedVariable(this.index);
         if (v != InternalThreadLocalMap.UNSET) {
            removeFromVariablesToRemove(threadLocalMap, this);

            try {
               this.onRemoval((V)v);
            } catch (Exception var4) {
               PlatformDependent.throwException(var4);
            }

            return (V)v;
         } else {
            return null;
         }
      }
   }

   protected V initialValue() throws Exception {
      return null;
   }

   protected void onRemoval(V value) throws Exception {
   }
}
