package io.netty.channel.pool;

import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import io.netty.util.concurrent.GlobalEventExecutor;
import io.netty.util.concurrent.Promise;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.ReadOnlyIterator;
import java.io.Closeable;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public abstract class AbstractChannelPoolMap<K, P extends ChannelPool> implements ChannelPoolMap<K, P>, Iterable<Entry<K, P>>, Closeable {
   private final ConcurrentMap<K, P> map = new ConcurrentHashMap<>();

   @Override
   public final P get(K key) {
      P pool = this.map.get(ObjectUtil.checkNotNull(key, "key"));
      if (pool == null) {
         pool = this.newPool(key);
         P old = this.map.putIfAbsent(key, pool);
         if (old != null) {
            poolCloseAsyncIfSupported(pool);
            pool = old;
         }
      }

      return pool;
   }

   public final boolean remove(K key) {
      P pool = this.map.remove(ObjectUtil.checkNotNull(key, "key"));
      if (pool != null) {
         poolCloseAsyncIfSupported(pool);
         return true;
      } else {
         return false;
      }
   }

   private Future<Boolean> removeAsyncIfSupported(K key) {
      P pool = this.map.remove(ObjectUtil.checkNotNull(key, "key"));
      if (pool != null) {
         final Promise<Boolean> removePromise = GlobalEventExecutor.INSTANCE.newPromise();
         poolCloseAsyncIfSupported(pool).addListener(new GenericFutureListener<Future<? super Void>>() {
            @Override
            public void operationComplete(Future<? super Void> future) throws Exception {
               if (future.isSuccess()) {
                  removePromise.setSuccess(Boolean.TRUE);
               } else {
                  removePromise.setFailure(future.cause());
               }
            }
         });
         return removePromise;
      } else {
         return GlobalEventExecutor.INSTANCE.newSucceededFuture(Boolean.FALSE);
      }
   }

   private static Future<Void> poolCloseAsyncIfSupported(ChannelPool pool) {
      if (pool instanceof SimpleChannelPool) {
         return ((SimpleChannelPool)pool).closeAsync();
      } else {
         try {
            pool.close();
            return GlobalEventExecutor.INSTANCE.newSucceededFuture(null);
         } catch (Exception var2) {
            return GlobalEventExecutor.INSTANCE.newFailedFuture(var2);
         }
      }
   }

   @Override
   public final Iterator<Entry<K, P>> iterator() {
      return new ReadOnlyIterator<>(this.map.entrySet().iterator());
   }

   public final int size() {
      return this.map.size();
   }

   public final boolean isEmpty() {
      return this.map.isEmpty();
   }

   @Override
   public final boolean contains(K key) {
      return this.map.containsKey(ObjectUtil.checkNotNull(key, "key"));
   }

   protected abstract P newPool(K var1);

   @Override
   public final void close() {
      for (K key : this.map.keySet()) {
         this.removeAsyncIfSupported(key).syncUninterruptibly();
      }
   }
}
