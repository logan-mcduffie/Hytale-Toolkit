package io.netty.channel;

import io.netty.util.concurrent.Future;

public interface IoEventLoop extends EventLoop, IoEventLoopGroup {
   @Override
   default IoEventLoop next() {
      return this;
   }

   @Override
   Future<IoRegistration> register(IoHandle var1);

   @Override
   boolean isCompatible(Class<? extends IoHandle> var1);

   @Override
   boolean isIoType(Class<? extends IoHandler> var1);
}
