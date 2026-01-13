package io.netty.channel;

import io.netty.util.concurrent.Future;

public interface IoEventLoopGroup extends EventLoopGroup {
   IoEventLoop next();

   @Deprecated
   @Override
   default ChannelFuture register(Channel channel) {
      return this.next().register(channel);
   }

   @Deprecated
   @Override
   default ChannelFuture register(ChannelPromise promise) {
      return this.next().register(promise);
   }

   default Future<IoRegistration> register(IoHandle handle) {
      return this.next().register(handle);
   }

   default boolean isCompatible(Class<? extends IoHandle> handleType) {
      return this.next().isCompatible(handleType);
   }

   default boolean isIoType(Class<? extends IoHandler> handlerType) {
      return this.next().isIoType(handlerType);
   }
}
