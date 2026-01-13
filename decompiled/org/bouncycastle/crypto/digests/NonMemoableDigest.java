package org.bouncycastle.crypto.digests;

import org.bouncycastle.crypto.ExtendedDigest;

public class NonMemoableDigest implements ExtendedDigest {
   private ExtendedDigest baseDigest;

   public NonMemoableDigest(ExtendedDigest var1) {
      if (var1 == null) {
         throw new IllegalArgumentException("baseDigest must not be null");
      } else {
         this.baseDigest = var1;
      }
   }

   @Override
   public String getAlgorithmName() {
      return this.baseDigest.getAlgorithmName();
   }

   @Override
   public int getDigestSize() {
      return this.baseDigest.getDigestSize();
   }

   @Override
   public void update(byte var1) {
      this.baseDigest.update(var1);
   }

   @Override
   public void update(byte[] var1, int var2, int var3) {
      this.baseDigest.update(var1, var2, var3);
   }

   @Override
   public int doFinal(byte[] var1, int var2) {
      return this.baseDigest.doFinal(var1, var2);
   }

   @Override
   public void reset() {
      this.baseDigest.reset();
   }

   @Override
   public int getByteLength() {
      return this.baseDigest.getByteLength();
   }
}
