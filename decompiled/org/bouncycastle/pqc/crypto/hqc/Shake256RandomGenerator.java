package org.bouncycastle.pqc.crypto.hqc;

import org.bouncycastle.crypto.digests.SHAKEDigest;

class Shake256RandomGenerator {
   private final SHAKEDigest digest = new SHAKEDigest(256);

   public Shake256RandomGenerator(byte[] var1, byte var2) {
      this.digest.update(var1, 0, var1.length);
      this.digest.update(var2);
   }

   public Shake256RandomGenerator(byte[] var1, int var2, int var3, byte var4) {
      this.digest.update(var1, var2, var3);
      this.digest.update(var4);
   }

   public void init(byte[] var1, int var2, int var3, byte var4) {
      this.digest.reset();
      this.digest.update(var1, var2, var3);
      this.digest.update(var4);
   }

   public void nextBytes(byte[] var1) {
      this.digest.doOutput(var1, 0, var1.length);
   }

   public void nextBytes(byte[] var1, int var2, int var3) {
      this.digest.doOutput(var1, var2, var3);
   }

   public void xofGetBytes(byte[] var1, int var2) {
      int var3 = var2 & 7;
      int var4 = var2 - var3;
      this.digest.doOutput(var1, 0, var4);
      if (var3 != 0) {
         byte[] var5 = new byte[8];
         this.digest.doOutput(var5, 0, 8);
         System.arraycopy(var5, 0, var1, var4, var3);
      }
   }
}
