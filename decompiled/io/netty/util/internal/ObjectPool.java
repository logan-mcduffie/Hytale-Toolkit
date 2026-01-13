package io.netty.util.internal;

import io.netty.util.Recycler;

public abstract class ObjectPool<T> {
   ObjectPool() {
   }

   @Deprecated
   public abstract T get();

   @Deprecated
   public static <T> ObjectPool<T> newPool(ObjectPool.ObjectCreator<T> creator) {
      return new ObjectPool.RecyclerObjectPool<>(ObjectUtil.checkNotNull(creator, "creator"));
   }

   public interface Handle<T> {
      void recycle(T var1);
   }

   @Deprecated
   public interface ObjectCreator<T> {
      T newObject(ObjectPool.Handle<T> var1);
   }

   @Deprecated
   private static final class RecyclerObjectPool<T> extends ObjectPool<T> {
      private final Recycler<T> recycler;

      RecyclerObjectPool(final ObjectPool.ObjectCreator<T> creator) {
         this.recycler = new Recycler<T>() {
            @Override
            protected T newObject(Recycler.Handle<T> handle) {
               return creator.newObject(handle);
            }
         };
      }

      @Override
      public T get() {
         return this.recycler.get();
      }
   }
}
