package org.bouncycastle.pqc.crypto.lms;

import org.bouncycastle.crypto.Digest;

class SeedDerive {
   private final byte[] I;
   private final byte[] masterSeed;
   private final Digest digest;
   private int q;
   private int j;

   public SeedDerive(byte[] var1, byte[] var2, Digest var3) {
      this.I = var1;
      this.masterSeed = var2;
      this.digest = var3;
   }

   public int getQ() {
      return this.q;
   }

   public void setQ(int var1) {
      this.q = var1;
   }

   public int getJ() {
      return this.j;
   }

   public void setJ(int var1) {
      this.j = var1;
   }

   public byte[] getI() {
      return this.I;
   }

   public byte[] getMasterSeed() {
      return this.masterSeed;
   }

   public byte[] deriveSeed(byte[] var1, int var2) {
      if (var1.length - var2 < this.digest.getDigestSize()) {
         throw new IllegalArgumentException("target length is less than digest size.");
      } else {
         this.digest.update(this.I, 0, this.I.length);
         this.digest.update((byte)(this.q >>> 24));
         this.digest.update((byte)(this.q >>> 16));
         this.digest.update((byte)(this.q >>> 8));
         this.digest.update((byte)this.q);
         this.digest.update((byte)(this.j >>> 8));
         this.digest.update((byte)this.j);
         this.digest.update((byte)-1);
         this.digest.update(this.masterSeed, 0, this.masterSeed.length);
         this.digest.doFinal(var1, var2);
         return var1;
      }
   }

   public void deriveSeed(byte[] var1, boolean var2) {
      this.deriveSeed(var1, var2, 0);
   }

   public void deriveSeed(byte[] var1, boolean var2, int var3) {
      this.deriveSeed(var1, var3);
      if (var2) {
         this.j++;
      }
   }
}
