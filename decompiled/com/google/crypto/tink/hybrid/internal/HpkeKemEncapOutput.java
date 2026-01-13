package com.google.crypto.tink.hybrid.internal;

final class HpkeKemEncapOutput {
   private final byte[] sharedSecret;
   private final byte[] encapsulatedKey;

   HpkeKemEncapOutput(byte[] sharedSecret, byte[] encapsulatedKey) {
      this.sharedSecret = sharedSecret;
      this.encapsulatedKey = encapsulatedKey;
   }

   byte[] getSharedSecret() {
      return this.sharedSecret;
   }

   byte[] getEncapsulatedKey() {
      return this.encapsulatedKey;
   }
}
