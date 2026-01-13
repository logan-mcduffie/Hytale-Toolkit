package io.netty.channel;

public interface IoHandler {
   default void initialize() {
   }

   int run(IoHandlerContext var1);

   default void prepareToDestroy() {
   }

   default void destroy() {
   }

   IoRegistration register(IoHandle var1) throws Exception;

   void wakeup();

   boolean isCompatible(Class<? extends IoHandle> var1);
}
