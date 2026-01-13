package org.bouncycastle.crypto.digests;

import org.bouncycastle.util.Pack;

/** @deprecated */
public class AsconDigest extends AsconBaseDigest {
   AsconDigest.AsconParameters asconParameters;

   public AsconDigest(AsconDigest.AsconParameters var1) {
      this.asconParameters = var1;
      switch (var1) {
         case AsconHash:
            this.ASCON_PB_ROUNDS = 12;
            this.algorithmName = "Ascon-Hash";
            break;
         case AsconHashA:
            this.ASCON_PB_ROUNDS = 8;
            this.algorithmName = "Ascon-HashA";
            break;
         default:
            throw new IllegalArgumentException("Invalid parameter settings for Ascon Hash");
      }

      this.reset();
   }

   @Override
   protected long pad(int var1) {
      return 128L << 56 - (var1 << 3);
   }

   @Override
   protected long loadBytes(byte[] var1, int var2) {
      return Pack.bigEndianToLong(var1, var2);
   }

   @Override
   protected long loadBytes(byte[] var1, int var2, int var3) {
      return Pack.bigEndianToLong(var1, var2, var3);
   }

   @Override
   protected void setBytes(long var1, byte[] var3, int var4) {
      Pack.longToBigEndian(var1, var3, var4);
   }

   @Override
   protected void setBytes(long var1, byte[] var3, int var4, int var5) {
      Pack.longToBigEndian(var1, var3, var4, var5);
   }

   @Override
   public void reset() {
      super.reset();
      switch (this.asconParameters) {
         case AsconHash:
            this.p.set(-1255492011513352131L, -8380609354527731710L, -5437372128236807582L, 4834782570098516968L, 3787428097924915520L);
            break;
         case AsconHashA:
            this.p.set(92044056785660070L, 8326807761760157607L, 3371194088139667532L, -2956994353054992515L, -6828509670848688761L);
      }
   }

   public static enum AsconParameters {
      AsconHash,
      AsconHashA;
   }
}
