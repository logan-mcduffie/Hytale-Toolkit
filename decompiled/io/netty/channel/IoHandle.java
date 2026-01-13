package io.netty.channel;

public interface IoHandle extends AutoCloseable {
   void handle(IoRegistration var1, IoEvent var2);

   default void registered() {
   }

   default void unregistered() {
   }

   @Override
   void close() throws Exception;
}
