package org.bouncycastle.crypto;

public class Commitment {
   private final byte[] secret;
   private final byte[] commitment;

   public Commitment(byte[] var1, byte[] var2) {
      this.secret = var1;
      this.commitment = var2;
   }

   public byte[] getSecret() {
      return this.secret;
   }

   public byte[] getCommitment() {
      return this.commitment;
   }
}
