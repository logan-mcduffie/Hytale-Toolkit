package org.bouncycastle.pqc.crypto.rainbow;

import org.bouncycastle.util.Arrays;

class RainbowPublicMap {
   private ComputeInField cf;
   private RainbowParameters params;
   private final int num_gf_elements = 256;

   public RainbowPublicMap(RainbowParameters var1) {
      this.cf = new ComputeInField();
      this.params = var1;
   }

   private short[][] compute_accumulator(short[] var1, short[] var2, short[][][] var3, int var4) {
      short[][] var5 = new short[256][var4];
      if (var2.length == var3[0].length && var1.length == var3[0][0].length && var3.length == var4) {
         for (int var7 = 0; var7 < var2.length; var7++) {
            short[] var6 = this.cf.multVect(var2[var7], var1);

            for (int var8 = 0; var8 < var1.length; var8++) {
               for (int var9 = 0; var9 < var3.length; var9++) {
                  short var10 = var6[var8];
                  if (var10 != 0) {
                     var5[var10][var9] = GF2Field.addElem(var5[var10][var9], var3[var9][var7][var8]);
                  }
               }
            }
         }

         return var5;
      } else {
         throw new RuntimeException("Accumulator calculation not possible!");
      }
   }

   private short[] add_and_reduce(short[][] var1) {
      int var2 = this.params.getM();
      short[] var4 = new short[var2];

      for (int var5 = 0; var5 < 8; var5++) {
         int var6 = (int)Math.pow(2.0, var5);
         short[] var3 = new short[var2];

         for (int var7 = var6; var7 < 256; var7 += var6 * 2) {
            for (int var8 = 0; var8 < var6; var8++) {
               var3 = this.cf.addVect(var3, var1[var7 + var8]);
            }
         }

         var4 = this.cf.addVect(var4, this.cf.multVect((short)var6, var3));
      }

      return var4;
   }

   public short[] publicMap(RainbowPublicKeyParameters var1, short[] var2) {
      short[][] var3 = this.compute_accumulator(var2, var2, var1.pk, this.params.getM());
      return this.add_and_reduce(var3);
   }

   public short[] publicMap_cyclic(RainbowPublicKeyParameters var1, short[] var2) {
      int var3 = this.params.getV1();
      int var4 = this.params.getO1();
      int var5 = this.params.getO2();
      short[][] var9 = new short[256][var4 + var5];
      short[] var10 = Arrays.copyOfRange(var2, 0, var3);
      short[] var11 = Arrays.copyOfRange(var2, var3, var3 + var4);
      short[] var12 = Arrays.copyOfRange(var2, var3 + var4, var2.length);
      RainbowDRBG var13 = new RainbowDRBG(var1.pk_seed, var1.getParameters().getHash_algo());
      short[][][] var6 = RainbowUtil.generate_random(var13, var4, var3, var3, true);
      short[][] var7 = this.compute_accumulator(var10, var10, var6, var4);
      var6 = RainbowUtil.generate_random(var13, var4, var3, var4, false);
      var7 = this.cf.addMatrix(var7, this.compute_accumulator(var11, var10, var6, var4));
      var7 = this.cf.addMatrix(var7, this.compute_accumulator(var12, var10, var1.l1_Q3, var4));
      var7 = this.cf.addMatrix(var7, this.compute_accumulator(var11, var11, var1.l1_Q5, var4));
      var7 = this.cf.addMatrix(var7, this.compute_accumulator(var12, var11, var1.l1_Q6, var4));
      var7 = this.cf.addMatrix(var7, this.compute_accumulator(var12, var12, var1.l1_Q9, var4));
      var6 = RainbowUtil.generate_random(var13, var5, var3, var3, true);
      short[][] var8 = this.compute_accumulator(var10, var10, var6, var5);
      var6 = RainbowUtil.generate_random(var13, var5, var3, var4, false);
      var8 = this.cf.addMatrix(var8, this.compute_accumulator(var11, var10, var6, var5));
      var6 = RainbowUtil.generate_random(var13, var5, var3, var5, false);
      var8 = this.cf.addMatrix(var8, this.compute_accumulator(var12, var10, var6, var5));
      var6 = RainbowUtil.generate_random(var13, var5, var4, var4, true);
      var8 = this.cf.addMatrix(var8, this.compute_accumulator(var11, var11, var6, var5));
      var6 = RainbowUtil.generate_random(var13, var5, var4, var5, false);
      var8 = this.cf.addMatrix(var8, this.compute_accumulator(var12, var11, var6, var5));
      var8 = this.cf.addMatrix(var8, this.compute_accumulator(var12, var12, var1.l2_Q9, var5));

      for (int var14 = 0; var14 < 256; var14++) {
         var9[var14] = Arrays.concatenate(var7[var14], var8[var14]);
      }

      return this.add_and_reduce(var9);
   }
}
