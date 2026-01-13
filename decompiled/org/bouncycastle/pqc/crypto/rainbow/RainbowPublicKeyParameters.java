package org.bouncycastle.pqc.crypto.rainbow;

import org.bouncycastle.util.Arrays;

public class RainbowPublicKeyParameters extends RainbowKeyParameters {
   short[][][] pk;
   byte[] pk_seed;
   short[][][] l1_Q3;
   short[][][] l1_Q5;
   short[][][] l1_Q6;
   short[][][] l1_Q9;
   short[][][] l2_Q9;

   RainbowPublicKeyParameters(
      RainbowParameters var1,
      short[][][] var2,
      short[][][] var3,
      short[][][] var4,
      short[][][] var5,
      short[][][] var6,
      short[][][] var7,
      short[][][] var8,
      short[][][] var9,
      short[][][] var10,
      short[][][] var11,
      short[][][] var12,
      short[][][] var13
   ) {
      super(false, var1);
      int var14 = var1.getV1();
      int var15 = var1.getO1();
      int var16 = var1.getO2();
      this.pk = new short[var1.getM()][var1.getN()][var1.getN()];

      for (int var17 = 0; var17 < var15; var17++) {
         for (int var18 = 0; var18 < var14; var18++) {
            System.arraycopy(var2[var17][var18], 0, this.pk[var17][var18], 0, var14);
            System.arraycopy(var3[var17][var18], 0, this.pk[var17][var18], var14, var15);
            System.arraycopy(var4[var17][var18], 0, this.pk[var17][var18], var14 + var15, var16);
         }

         for (int var20 = 0; var20 < var15; var20++) {
            System.arraycopy(var5[var17][var20], 0, this.pk[var17][var20 + var14], var14, var15);
            System.arraycopy(var6[var17][var20], 0, this.pk[var17][var20 + var14], var14 + var15, var16);
         }

         for (int var21 = 0; var21 < var16; var21++) {
            System.arraycopy(var7[var17][var21], 0, this.pk[var17][var21 + var14 + var15], var14 + var15, var16);
         }
      }

      for (int var19 = 0; var19 < var16; var19++) {
         for (int var22 = 0; var22 < var14; var22++) {
            System.arraycopy(var8[var19][var22], 0, this.pk[var19 + var15][var22], 0, var14);
            System.arraycopy(var9[var19][var22], 0, this.pk[var19 + var15][var22], var14, var15);
            System.arraycopy(var10[var19][var22], 0, this.pk[var19 + var15][var22], var14 + var15, var16);
         }

         for (int var23 = 0; var23 < var15; var23++) {
            System.arraycopy(var11[var19][var23], 0, this.pk[var19 + var15][var23 + var14], var14, var15);
            System.arraycopy(var12[var19][var23], 0, this.pk[var19 + var15][var23 + var14], var14 + var15, var16);
         }

         for (int var24 = 0; var24 < var16; var24++) {
            System.arraycopy(var13[var19][var24], 0, this.pk[var19 + var15][var24 + var14 + var15], var14 + var15, var16);
         }
      }
   }

   RainbowPublicKeyParameters(RainbowParameters var1, byte[] var2, short[][][] var3, short[][][] var4, short[][][] var5, short[][][] var6, short[][][] var7) {
      super(false, var1);
      this.pk_seed = (byte[])var2.clone();
      this.l1_Q3 = RainbowUtil.cloneArray(var3);
      this.l1_Q5 = RainbowUtil.cloneArray(var4);
      this.l1_Q6 = RainbowUtil.cloneArray(var5);
      this.l1_Q9 = RainbowUtil.cloneArray(var6);
      this.l2_Q9 = RainbowUtil.cloneArray(var7);
   }

   public RainbowPublicKeyParameters(RainbowParameters var1, byte[] var2) {
      super(false, var1);
      int var3 = var1.getM();
      int var4 = var1.getN();
      if (this.getParameters().getVersion() == Version.CLASSIC) {
         this.pk = new short[var3][var4][var4];
         int var5 = 0;

         for (int var6 = 0; var6 < var4; var6++) {
            for (int var7 = 0; var7 < var4; var7++) {
               for (int var8 = 0; var8 < var3; var8++) {
                  if (var6 > var7) {
                     this.pk[var8][var6][var7] = 0;
                  } else {
                     this.pk[var8][var6][var7] = (short)(var2[var5] & 255);
                     var5++;
                  }
               }
            }
         }
      } else {
         this.pk_seed = Arrays.copyOfRange(var2, 0, var1.getLen_pkseed());
         this.l1_Q3 = new short[var1.getO1()][var1.getV1()][var1.getO2()];
         this.l1_Q5 = new short[var1.getO1()][var1.getO1()][var1.getO1()];
         this.l1_Q6 = new short[var1.getO1()][var1.getO1()][var1.getO2()];
         this.l1_Q9 = new short[var1.getO1()][var1.getO2()][var1.getO2()];
         this.l2_Q9 = new short[var1.getO2()][var1.getO2()][var1.getO2()];
         int var9 = var1.getLen_pkseed();
         var9 += RainbowUtil.loadEncoded(this.l1_Q3, var2, var9, false);
         var9 += RainbowUtil.loadEncoded(this.l1_Q5, var2, var9, true);
         var9 += RainbowUtil.loadEncoded(this.l1_Q6, var2, var9, false);
         var9 += RainbowUtil.loadEncoded(this.l1_Q9, var2, var9, true);
         var9 += RainbowUtil.loadEncoded(this.l2_Q9, var2, var9, true);
         if (var9 != var2.length) {
            throw new IllegalArgumentException("unparsed data in key encoding");
         }
      }
   }

   public short[][][] getPk() {
      return RainbowUtil.cloneArray(this.pk);
   }

   public byte[] getEncoded() {
      if (this.getParameters().getVersion() != Version.CLASSIC) {
         byte[] var1 = this.pk_seed;
         var1 = Arrays.concatenate(var1, RainbowUtil.getEncoded(this.l1_Q3, false));
         var1 = Arrays.concatenate(var1, RainbowUtil.getEncoded(this.l1_Q5, true));
         var1 = Arrays.concatenate(var1, RainbowUtil.getEncoded(this.l1_Q6, false));
         var1 = Arrays.concatenate(var1, RainbowUtil.getEncoded(this.l1_Q9, true));
         return Arrays.concatenate(var1, RainbowUtil.getEncoded(this.l2_Q9, true));
      } else {
         return RainbowUtil.getEncoded(this.pk, true);
      }
   }
}
