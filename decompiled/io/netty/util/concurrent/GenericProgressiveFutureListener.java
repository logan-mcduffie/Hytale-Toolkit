package io.netty.util.concurrent;

public interface GenericProgressiveFutureListener<F extends ProgressiveFuture<?>> extends GenericFutureListener<F> {
   void operationProgressed(F var1, long var2, long var4) throws Exception;
}
