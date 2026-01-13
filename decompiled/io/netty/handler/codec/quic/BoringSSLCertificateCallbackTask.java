package io.netty.handler.codec.quic;

final class BoringSSLCertificateCallbackTask extends BoringSSLTask {
   private final byte[] keyTypeBytes;
   private final byte[][] asn1DerEncodedPrincipals;
   private final String[] authMethods;
   private final BoringSSLCertificateCallback callback;
   private long key;
   private long chain;

   BoringSSLCertificateCallbackTask(
      long ssl, byte[] keyTypeBytes, byte[][] asn1DerEncodedPrincipals, String[] authMethods, BoringSSLCertificateCallback callback
   ) {
      super(ssl);
      this.keyTypeBytes = keyTypeBytes;
      this.asn1DerEncodedPrincipals = asn1DerEncodedPrincipals;
      this.authMethods = authMethods;
      this.callback = callback;
   }

   @Override
   protected void runTask(long ssl, BoringSSLTask.TaskCallback taskCallback) {
      try {
         long[] result = this.callback.handle(ssl, this.keyTypeBytes, this.asn1DerEncodedPrincipals, this.authMethods);
         if (result == null) {
            taskCallback.onResult(ssl, 0);
         } else {
            this.key = result[0];
            this.chain = result[1];
            taskCallback.onResult(ssl, 1);
         }
      } catch (Exception var5) {
         taskCallback.onResult(ssl, 0);
      }
   }

   @Override
   protected void destroy() {
      if (this.key != 0L) {
         BoringSSL.EVP_PKEY_free(this.key);
         this.key = 0L;
      }

      if (this.chain != 0L) {
         BoringSSL.CRYPTO_BUFFER_stack_free(this.chain);
         this.chain = 0L;
      }
   }
}
