package org.bouncycastle.crypto.digests;

import org.bouncycastle.util.Pack;

/** @deprecated */
public class AsconXof extends AsconXofBase {
   AsconXof.AsconParameters asconParameters;

   public AsconXof(AsconXof.AsconParameters var1) {
      this.BlockSize = 8;
      this.asconParameters = var1;
      switch (var1) {
         case AsconXof:
            this.ASCON_PB_ROUNDS = 12;
            this.algorithmName = "Ascon-Xof";
            break;
         case AsconXofA:
            this.ASCON_PB_ROUNDS = 8;
            this.algorithmName = "Ascon-XofA";
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
         case AsconXof:
            this.p.set(-5368810569253202922L, 3121280575360345120L, 7395939140700676632L, 6533890155656471820L, 5710016986865767350L);
            break;
         case AsconXofA:
            this.p.set(4940560291654768690L, -3635129828240960206L, -597534922722107095L, 2623493988082852443L, -6283826724160825537L);
      }
   }

   public static enum AsconParameters {
      AsconXof,
      AsconXofA;
   }
}
