package io.netty.handler.codec.quic;

import java.util.function.BiConsumer;

final class BoringSSLPrivateKeyMethodDecryptTask extends BoringSSLPrivateKeyMethodTask {
   private final byte[] input;

   BoringSSLPrivateKeyMethodDecryptTask(long ssl, byte[] input, BoringSSLPrivateKeyMethod method) {
      super(ssl, method);
      this.input = input;
   }

   @Override
   protected void runMethod(long ssl, BoringSSLPrivateKeyMethod method, BiConsumer<byte[], Throwable> consumer) {
      method.decrypt(ssl, this.input, consumer);
   }
}
