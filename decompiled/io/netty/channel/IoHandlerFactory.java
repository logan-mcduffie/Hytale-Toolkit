package io.netty.channel;

import io.netty.util.concurrent.ThreadAwareExecutor;

public interface IoHandlerFactory {
   IoHandler newHandler(ThreadAwareExecutor var1);

   default boolean isChangingThreadSupported() {
      return false;
   }
}
