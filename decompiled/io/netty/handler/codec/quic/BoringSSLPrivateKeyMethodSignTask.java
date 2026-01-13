package io.netty.handler.codec.quic;

import java.util.function.BiConsumer;

final class BoringSSLPrivateKeyMethodSignTask extends BoringSSLPrivateKeyMethodTask {
   private final int signatureAlgorithm;
   private final byte[] digest;

   BoringSSLPrivateKeyMethodSignTask(long ssl, int signatureAlgorithm, byte[] digest, BoringSSLPrivateKeyMethod method) {
      super(ssl, method);
      this.signatureAlgorithm = signatureAlgorithm;
      this.digest = digest;
   }

   @Override
   protected void runMethod(long ssl, BoringSSLPrivateKeyMethod method, BiConsumer<byte[], Throwable> callback) {
      method.sign(ssl, this.signatureAlgorithm, this.digest, callback);
   }
}
