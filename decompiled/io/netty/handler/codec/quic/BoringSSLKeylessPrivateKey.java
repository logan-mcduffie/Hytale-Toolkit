package io.netty.handler.codec.quic;

import io.netty.util.internal.EmptyArrays;
import java.security.PrivateKey;

final class BoringSSLKeylessPrivateKey implements PrivateKey {
   static final BoringSSLKeylessPrivateKey INSTANCE = new BoringSSLKeylessPrivateKey();

   private BoringSSLKeylessPrivateKey() {
   }

   @Override
   public String getAlgorithm() {
      return "keyless";
   }

   @Override
   public String getFormat() {
      return "keyless";
   }

   @Override
   public byte[] getEncoded() {
      return EmptyArrays.EMPTY_BYTES;
   }
}
