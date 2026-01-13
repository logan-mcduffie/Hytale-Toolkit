package org.bouncycastle.crypto;

public class EphemeralKeyPair {
   private AsymmetricCipherKeyPair keyPair;
   private KeyEncoder publicKeyEncoder;

   public EphemeralKeyPair(AsymmetricCipherKeyPair var1, KeyEncoder var2) {
      this.keyPair = var1;
      this.publicKeyEncoder = var2;
   }

   public AsymmetricCipherKeyPair getKeyPair() {
      return this.keyPair;
   }

   public byte[] getEncodedPublicKey() {
      return this.publicKeyEncoder.getEncoded(this.keyPair.getPublic());
   }
}
