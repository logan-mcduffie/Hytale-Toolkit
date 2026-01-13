package org.bouncycastle.crypto.digests;

import org.bouncycastle.crypto.CryptoServiceProperties;
import org.bouncycastle.crypto.CryptoServicePurpose;
import org.bouncycastle.crypto.SavableDigest;
import org.bouncycastle.crypto.Xof;
import org.bouncycastle.util.Memoable;

public class SHAKEDigest extends KeccakDigest implements Xof, SavableDigest {
   private static int checkBitLength(int var0) {
      switch (var0) {
         case 128:
         case 256:
            return var0;
         default:
            throw new IllegalArgumentException("'bitStrength' " + var0 + " not supported for SHAKE");
      }
   }

   public SHAKEDigest() {
      this(128);
   }

   public SHAKEDigest(CryptoServicePurpose var1) {
      this(128, var1);
   }

   public SHAKEDigest(int var1) {
      super(checkBitLength(var1), CryptoServicePurpose.ANY);
   }

   public SHAKEDigest(int var1, CryptoServicePurpose var2) {
      super(checkBitLength(var1), var2);
   }

   public SHAKEDigest(SHAKEDigest var1) {
      super(var1);
   }

   public SHAKEDigest(byte[] var1) {
      super(var1);
   }

   @Override
   public String getAlgorithmName() {
      return "SHAKE" + this.fixedOutputLength;
   }

   @Override
   public int getDigestSize() {
      return this.fixedOutputLength / 4;
   }

   @Override
   public int doFinal(byte[] var1, int var2) {
      return this.doFinal(var1, var2, this.getDigestSize());
   }

   @Override
   public int doFinal(byte[] var1, int var2, int var3) {
      int var4 = this.doOutput(var1, var2, var3);
      this.reset();
      return var4;
   }

   @Override
   public int doOutput(byte[] var1, int var2, int var3) {
      if (!this.squeezing) {
         this.absorbBits(15, 4);
      }

      this.squeeze(var1, var2, var3 * 8L);
      return var3;
   }

   @Override
   protected int doFinal(byte[] var1, int var2, byte var3, int var4) {
      return this.doFinal(var1, var2, this.getDigestSize(), var3, var4);
   }

   protected int doFinal(byte[] var1, int var2, int var3, byte var4, int var5) {
      if (var5 >= 0 && var5 <= 7) {
         int var6 = var4 & (1 << var5) - 1 | 15 << var5;
         int var7 = var5 + 4;
         if (var7 >= 8) {
            this.absorb((byte)var6);
            var7 -= 8;
            var6 >>>= 8;
         }

         if (var7 > 0) {
            this.absorbBits(var6, var7);
         }

         this.squeeze(var1, var2, var3 * 8L);
         this.reset();
         return var3;
      } else {
         throw new IllegalArgumentException("'partialBits' must be in the range [0,7]");
      }
   }

   @Override
   protected CryptoServiceProperties cryptoServiceProperties() {
      return Utils.getDefaultProperties(this, this.purpose);
   }

   @Override
   public byte[] getEncodedState() {
      byte[] var1 = new byte[this.state.length * 8 + this.dataQueue.length + 12 + 2];
      super.getEncodedState(var1);
      return var1;
   }

   @Override
   public Memoable copy() {
      return new SHAKEDigest(this);
   }

   @Override
   public void reset(Memoable var1) {
      SHAKEDigest var2 = (SHAKEDigest)var1;
      this.copyIn(var2);
   }
}
