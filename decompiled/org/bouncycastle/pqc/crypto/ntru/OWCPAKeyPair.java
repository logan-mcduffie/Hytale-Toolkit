package org.bouncycastle.pqc.crypto.ntru;

class OWCPAKeyPair {
   public final byte[] publicKey;
   public final byte[] privateKey;

   public OWCPAKeyPair(byte[] var1, byte[] var2) {
      this.publicKey = var1;
      this.privateKey = var2;
   }
}
