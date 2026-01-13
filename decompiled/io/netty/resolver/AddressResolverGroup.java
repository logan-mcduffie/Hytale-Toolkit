package io.netty.resolver;

import io.netty.util.concurrent.EventExecutor;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.FutureListener;
import io.netty.util.concurrent.GenericFutureListener;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.io.Closeable;
import java.net.SocketAddress;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Map.Entry;

public abstract class AddressResolverGroup<T extends SocketAddress> implements Closeable {
   private static final InternalLogger logger = InternalLoggerFactory.getInstance(AddressResolverGroup.class);
   private final Map<EventExecutor, AddressResolver<T>> resolvers = new IdentityHashMap<>();
   private final Map<EventExecutor, GenericFutureListener<Future<Object>>> executorTerminationListeners = new IdentityHashMap<>();

   protected AddressResolverGroup() {
   }

   public AddressResolver<T> getResolver(final EventExecutor executor) {
      ObjectUtil.checkNotNull(executor, "executor");
      if (executor.isShuttingDown()) {
         throw new IllegalStateException("executor not accepting a task");
      } else {
         synchronized (this.resolvers) {
            AddressResolver<T> r = this.resolvers.get(executor);
            if (r == null) {
               final AddressResolver<T> newResolver;
               try {
                  newResolver = this.newResolver(executor);
               } catch (Exception var7) {
                  throw new IllegalStateException("failed to create a new resolver", var7);
               }

               this.resolvers.put(executor, newResolver);
               FutureListener<Object> terminationListener = new FutureListener<Object>() {
                  @Override
                  public void operationComplete(Future<Object> future) {
                     synchronized (AddressResolverGroup.this.resolvers) {
                        AddressResolverGroup.this.resolvers.remove(executor);
                        AddressResolverGroup.this.executorTerminationListeners.remove(executor);
                     }

                     newResolver.close();
                  }
               };
               this.executorTerminationListeners.put(executor, terminationListener);
               executor.terminationFuture().addListener(terminationListener);
               r = newResolver;
            }

            return r;
         }
      }
   }

   protected abstract AddressResolver<T> newResolver(EventExecutor var1) throws Exception;

   @Override
   public void close() {
      AddressResolver<T>[] rArray;
      Entry<EventExecutor, GenericFutureListener<Future<Object>>>[] listeners;
      synchronized (this.resolvers) {
         rArray = this.resolvers.values().toArray(new AddressResolver[0]);
         this.resolvers.clear();
         listeners = this.executorTerminationListeners.entrySet().toArray(new Entry[0]);
         this.executorTerminationListeners.clear();
      }

      for (Entry<EventExecutor, GenericFutureListener<Future<Object>>> entry : listeners) {
         entry.getKey().terminationFuture().removeListener(entry.getValue());
      }

      for (AddressResolver<T> r : rArray) {
         try {
            r.close();
         } catch (Throwable var8) {
            logger.warn("Failed to close a resolver:", var8);
         }
      }
   }
}
