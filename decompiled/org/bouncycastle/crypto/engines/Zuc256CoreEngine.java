package org.bouncycastle.crypto.engines;

import org.bouncycastle.util.Memoable;

public class Zuc256CoreEngine extends Zuc128CoreEngine {
   private static final byte[] EK_d = new byte[]{34, 47, 36, 42, 109, 64, 64, 64, 64, 64, 64, 64, 64, 82, 16, 48};
   private static final byte[] EK_d32 = new byte[]{34, 47, 37, 42, 109, 64, 64, 64, 64, 64, 64, 64, 64, 82, 16, 48};
   private static final byte[] EK_d64 = new byte[]{35, 47, 36, 42, 109, 64, 64, 64, 64, 64, 64, 64, 64, 82, 16, 48};
   private static final byte[] EK_d128 = new byte[]{35, 47, 37, 42, 109, 64, 64, 64, 64, 64, 64, 64, 64, 82, 16, 48};
   private byte[] theD;

   protected Zuc256CoreEngine() {
      this.theD = EK_d;
   }

   protected Zuc256CoreEngine(int var1) {
      switch (var1) {
         case 32:
            this.theD = EK_d32;
            break;
         case 64:
            this.theD = EK_d64;
            break;
         case 128:
            this.theD = EK_d128;
            break;
         default:
            throw new IllegalArgumentException("Unsupported length: " + var1);
      }
   }

   protected Zuc256CoreEngine(Zuc256CoreEngine var1) {
      super(var1);
   }

   @Override
   protected int getMaxIterations() {
      return 625;
   }

   @Override
   public String getAlgorithmName() {
      return "Zuc-256";
   }

   private static int MAKEU31(byte var0, byte var1, byte var2, byte var3) {
      return (var0 & 0xFF) << 23 | (var1 & 0xFF) << 16 | (var2 & 0xFF) << 8 | var3 & 0xFF;
   }

   @Override
   protected void setKeyAndIV(int[] var1, byte[] var2, byte[] var3) {
      if (var2 == null || var2.length != 32) {
         throw new IllegalArgumentException("A key of 32 bytes is needed");
      } else if (var3 != null && var3.length == 25) {
         var1[0] = MAKEU31(var2[0], this.theD[0], var2[21], var2[16]);
         var1[1] = MAKEU31(var2[1], this.theD[1], var2[22], var2[17]);
         var1[2] = MAKEU31(var2[2], this.theD[2], var2[23], var2[18]);
         var1[3] = MAKEU31(var2[3], this.theD[3], var2[24], var2[19]);
         var1[4] = MAKEU31(var2[4], this.theD[4], var2[25], var2[20]);
         var1[5] = MAKEU31(var3[0], (byte)(this.theD[5] | var3[17] & 63), var2[5], var2[26]);
         var1[6] = MAKEU31(var3[1], (byte)(this.theD[6] | var3[18] & 63), var2[6], var2[27]);
         var1[7] = MAKEU31(var3[10], (byte)(this.theD[7] | var3[19] & 63), var2[7], var3[2]);
         var1[8] = MAKEU31(var2[8], (byte)(this.theD[8] | var3[20] & 63), var3[3], var3[11]);
         var1[9] = MAKEU31(var2[9], (byte)(this.theD[9] | var3[21] & 63), var3[12], var3[4]);
         var1[10] = MAKEU31(var3[5], (byte)(this.theD[10] | var3[22] & 63), var2[10], var2[28]);
         var1[11] = MAKEU31(var2[11], (byte)(this.theD[11] | var3[23] & 63), var3[6], var3[13]);
         var1[12] = MAKEU31(var2[12], (byte)(this.theD[12] | var3[24] & 63), var3[7], var3[14]);
         var1[13] = MAKEU31(var2[13], this.theD[13], var3[15], var3[8]);
         var1[14] = MAKEU31(var2[14], (byte)(this.theD[14] | var2[31] >>> 4 & 15), var3[16], var3[9]);
         var1[15] = MAKEU31(var2[15], (byte)(this.theD[15] | var2[31] & 15), var2[30], var2[29]);
      } else {
         throw new IllegalArgumentException("An IV of 25 bytes is needed");
      }
   }

   @Override
   public Memoable copy() {
      return new Zuc256CoreEngine(this);
   }

   @Override
   public void reset(Memoable var1) {
      Zuc256CoreEngine var2 = (Zuc256CoreEngine)var1;
      super.reset(var1);
      this.theD = var2.theD;
   }
}
