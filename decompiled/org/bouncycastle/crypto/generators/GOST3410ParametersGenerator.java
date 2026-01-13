package org.bouncycastle.crypto.generators;

import java.math.BigInteger;
import java.security.SecureRandom;
import org.bouncycastle.crypto.params.GOST3410Parameters;
import org.bouncycastle.crypto.params.GOST3410ValidationParameters;
import org.bouncycastle.util.BigIntegers;

public class GOST3410ParametersGenerator {
   private int size;
   private int typeproc;
   private SecureRandom init_random;
   private static final BigInteger ONE = BigInteger.valueOf(1L);
   private static final BigInteger TWO = BigInteger.valueOf(2L);

   public void init(int var1, int var2, SecureRandom var3) {
      this.size = var1;
      this.typeproc = var2;
      this.init_random = var3;
   }

   private int procedure_A(int var1, int var2, BigInteger[] var3, int var4) {
      while (var1 < 0 || var1 > 65536) {
         var1 = this.init_random.nextInt() / 32768;
      }

      while (var2 < 0 || var2 > 65536 || var2 / 2 == 0) {
         var2 = this.init_random.nextInt() / 32768 + 1;
      }

      BigInteger var5 = new BigInteger(Integer.toString(var2));
      BigInteger var6 = new BigInteger("19381");
      BigInteger[] var7 = new BigInteger[]{new BigInteger(Integer.toString(var1))};
      int[] var8 = new int[]{var4};
      int var9 = 0;

      for (int var10 = 0; var8[var10] >= 17; var10++) {
         int[] var11 = new int[var8.length + 1];
         System.arraycopy(var8, 0, var11, 0, var8.length);
         var8 = new int[var11.length];
         System.arraycopy(var11, 0, var8, 0, var11.length);
         var8[var10 + 1] = var8[var10] / 2;
         var9 = var10 + 1;
      }

      BigInteger[] var18 = new BigInteger[var9 + 1];
      var18[var9] = new BigInteger("8003", 16);
      int var19 = var9 - 1;

      label67:
      for (int var12 = 0; var12 < var9; var12++) {
         int var13 = var8[var19] / 16;

         while (true) {
            BigInteger[] var14 = new BigInteger[var7.length];
            System.arraycopy(var7, 0, var14, 0, var7.length);
            var7 = new BigInteger[var13 + 1];
            System.arraycopy(var14, 0, var7, 0, var14.length);

            for (int var15 = 0; var15 < var13; var15++) {
               var7[var15 + 1] = var7[var15].multiply(var6).add(var5).mod(TWO.pow(16));
            }

            BigInteger var20 = new BigInteger("0");

            for (int var16 = 0; var16 < var13; var16++) {
               var20 = var20.add(var7[var16].multiply(TWO.pow(16 * var16)));
            }

            var7[0] = var7[var13];
            BigInteger var21 = TWO.pow(var8[var19] - 1)
               .divide(var18[var19 + 1])
               .add(TWO.pow(var8[var19] - 1).multiply(var20).divide(var18[var19 + 1].multiply(TWO.pow(16 * var13))));
            if (var21.mod(TWO).compareTo(ONE) == 0) {
               var21 = var21.add(ONE);
            }

            byte var17 = 0;

            while (true) {
               var18[var19] = var18[var19 + 1].multiply(var21.add(BigInteger.valueOf(var17))).add(ONE);
               if (var18[var19].compareTo(TWO.pow(var8[var19])) == 1) {
                  break;
               }

               if (TWO.modPow(var18[var19 + 1].multiply(var21.add(BigInteger.valueOf(var17))), var18[var19]).compareTo(ONE) == 0
                  && TWO.modPow(var21.add(BigInteger.valueOf(var17)), var18[var19]).compareTo(ONE) != 0) {
                  if (--var19 < 0) {
                     var3[0] = var18[0];
                     var3[1] = var18[1];
                     return var7[0].intValue();
                  }
                  continue label67;
               }

               var17 += 2;
            }
         }
      }

      return var7[0].intValue();
   }

   private long procedure_Aa(long var1, long var3, BigInteger[] var5, int var6) {
      while (var1 < 0L || var1 > 4294967296L) {
         var1 = this.init_random.nextInt() * 2;
      }

      while (var3 < 0L || var3 > 4294967296L || var3 / 2L == 0L) {
         var3 = this.init_random.nextInt() * 2 + 1;
      }

      BigInteger var7 = new BigInteger(Long.toString(var3));
      BigInteger var8 = new BigInteger("97781173");
      BigInteger[] var9 = new BigInteger[]{new BigInteger(Long.toString(var1))};
      int[] var10 = new int[]{var6};
      int var11 = 0;

      for (int var12 = 0; var10[var12] >= 33; var12++) {
         int[] var13 = new int[var10.length + 1];
         System.arraycopy(var10, 0, var13, 0, var10.length);
         var10 = new int[var13.length];
         System.arraycopy(var13, 0, var10, 0, var13.length);
         var10[var12 + 1] = var10[var12] / 2;
         var11 = var12 + 1;
      }

      BigInteger[] var20 = new BigInteger[var11 + 1];
      var20[var11] = new BigInteger("8000000B", 16);
      int var21 = var11 - 1;

      label67:
      for (int var14 = 0; var14 < var11; var14++) {
         int var15 = var10[var21] / 32;

         while (true) {
            BigInteger[] var16 = new BigInteger[var9.length];
            System.arraycopy(var9, 0, var16, 0, var9.length);
            var9 = new BigInteger[var15 + 1];
            System.arraycopy(var16, 0, var9, 0, var16.length);

            for (int var17 = 0; var17 < var15; var17++) {
               var9[var17 + 1] = var9[var17].multiply(var8).add(var7).mod(TWO.pow(32));
            }

            BigInteger var22 = new BigInteger("0");

            for (int var18 = 0; var18 < var15; var18++) {
               var22 = var22.add(var9[var18].multiply(TWO.pow(32 * var18)));
            }

            var9[0] = var9[var15];
            BigInteger var23 = TWO.pow(var10[var21] - 1)
               .divide(var20[var21 + 1])
               .add(TWO.pow(var10[var21] - 1).multiply(var22).divide(var20[var21 + 1].multiply(TWO.pow(32 * var15))));
            if (var23.mod(TWO).compareTo(ONE) == 0) {
               var23 = var23.add(ONE);
            }

            byte var19 = 0;

            while (true) {
               var20[var21] = var20[var21 + 1].multiply(var23.add(BigInteger.valueOf(var19))).add(ONE);
               if (var20[var21].compareTo(TWO.pow(var10[var21])) == 1) {
                  break;
               }

               if (TWO.modPow(var20[var21 + 1].multiply(var23.add(BigInteger.valueOf(var19))), var20[var21]).compareTo(ONE) == 0
                  && TWO.modPow(var23.add(BigInteger.valueOf(var19)), var20[var21]).compareTo(ONE) != 0) {
                  if (--var21 < 0) {
                     var5[0] = var20[0];
                     var5[1] = var20[1];
                     return var9[0].longValue();
                  }
                  continue label67;
               }

               var19 += 2;
            }
         }
      }

      return var9[0].longValue();
   }

   private void procedure_B(int var1, int var2, BigInteger[] var3) {
      while (var1 < 0 || var1 > 65536) {
         var1 = this.init_random.nextInt() / 32768;
      }

      while (var2 < 0 || var2 > 65536 || var2 / 2 == 0) {
         var2 = this.init_random.nextInt() / 32768 + 1;
      }

      BigInteger[] var4 = new BigInteger[2];
      Object var5 = null;
      Object var6 = null;
      Object var7 = null;
      BigInteger var8 = new BigInteger(Integer.toString(var2));
      BigInteger var9 = new BigInteger("19381");
      var1 = this.procedure_A(var1, var2, var4, 256);
      var5 = var4[0];
      var1 = this.procedure_A(var1, var2, var4, 512);
      var6 = var4[0];
      BigInteger[] var10 = new BigInteger[65];
      var10[0] = new BigInteger(Integer.toString(var1));
      short var11 = 1024;

      while (true) {
         for (int var12 = 0; var12 < 64; var12++) {
            var10[var12 + 1] = var10[var12].multiply(var9).add(var8).mod(TWO.pow(16));
         }

         BigInteger var20 = new BigInteger("0");

         for (int var13 = 0; var13 < 64; var13++) {
            var20 = var20.add(var10[var13].multiply(TWO.pow(16 * var13)));
         }

         var10[0] = var10[64];
         BigInteger var21 = TWO.pow(var11 - 1)
            .divide(var5.multiply((BigInteger)var6))
            .add(TWO.pow(var11 - 1).multiply(var20).divide(var5.multiply((BigInteger)var6).multiply(TWO.pow(1024))));
         if (var21.mod(TWO).compareTo(ONE) == 0) {
            var21 = var21.add(ONE);
         }

         byte var14 = 0;

         while (true) {
            var7 = var5.multiply((BigInteger)var6).multiply(var21.add(BigInteger.valueOf(var14))).add(ONE);
            if (var7.compareTo(TWO.pow(var11)) == 1) {
               break;
            }

            if (TWO.modPow(var5.multiply((BigInteger)var6).multiply(var21.add(BigInteger.valueOf(var14))), (BigInteger)var7).compareTo(ONE) == 0
               && TWO.modPow(var5.multiply(var21.add(BigInteger.valueOf(var14))), (BigInteger)var7).compareTo(ONE) != 0) {
               var3[0] = (BigInteger)var7;
               var3[1] = (BigInteger)var5;
               return;
            }

            var14 += 2;
         }
      }
   }

   private void procedure_Bb(long var1, long var3, BigInteger[] var5) {
      while (var1 < 0L || var1 > 4294967296L) {
         var1 = this.init_random.nextInt() * 2;
      }

      while (var3 < 0L || var3 > 4294967296L || var3 / 2L == 0L) {
         var3 = this.init_random.nextInt() * 2 + 1;
      }

      BigInteger[] var6 = new BigInteger[2];
      Object var7 = null;
      Object var8 = null;
      Object var9 = null;
      BigInteger var10 = new BigInteger(Long.toString(var3));
      BigInteger var11 = new BigInteger("97781173");
      var1 = this.procedure_Aa(var1, var3, var6, 256);
      var7 = var6[0];
      var1 = this.procedure_Aa(var1, var3, var6, 512);
      var8 = var6[0];
      BigInteger[] var12 = new BigInteger[33];
      var12[0] = new BigInteger(Long.toString(var1));
      short var13 = 1024;

      while (true) {
         for (int var14 = 0; var14 < 32; var14++) {
            var12[var14 + 1] = var12[var14].multiply(var11).add(var10).mod(TWO.pow(32));
         }

         BigInteger var22 = new BigInteger("0");

         for (int var15 = 0; var15 < 32; var15++) {
            var22 = var22.add(var12[var15].multiply(TWO.pow(32 * var15)));
         }

         var12[0] = var12[32];
         BigInteger var23 = TWO.pow(var13 - 1)
            .divide(var7.multiply((BigInteger)var8))
            .add(TWO.pow(var13 - 1).multiply(var22).divide(var7.multiply((BigInteger)var8).multiply(TWO.pow(1024))));
         if (var23.mod(TWO).compareTo(ONE) == 0) {
            var23 = var23.add(ONE);
         }

         byte var16 = 0;

         while (true) {
            var9 = var7.multiply((BigInteger)var8).multiply(var23.add(BigInteger.valueOf(var16))).add(ONE);
            if (var9.compareTo(TWO.pow(var13)) == 1) {
               break;
            }

            if (TWO.modPow(var7.multiply((BigInteger)var8).multiply(var23.add(BigInteger.valueOf(var16))), (BigInteger)var9).compareTo(ONE) == 0
               && TWO.modPow(var7.multiply(var23.add(BigInteger.valueOf(var16))), (BigInteger)var9).compareTo(ONE) != 0) {
               var5[0] = (BigInteger)var9;
               var5[1] = (BigInteger)var7;
               return;
            }

            var16 += 2;
         }
      }
   }

   private BigInteger procedure_C(BigInteger var1, BigInteger var2) {
      BigInteger var3 = var1.subtract(ONE);
      BigInteger var4 = var3.divide(var2);
      int var5 = var1.bitLength();

      while (true) {
         BigInteger var6 = BigIntegers.createRandomBigInteger(var5, this.init_random);
         if (var6.compareTo(ONE) > 0 && var6.compareTo(var3) < 0) {
            BigInteger var7 = var6.modPow(var4, var1);
            if (var7.compareTo(ONE) != 0) {
               return var7;
            }
         }
      }
   }

   public GOST3410Parameters generateParameters() {
      BigInteger[] var1 = new BigInteger[2];
      Object var2 = null;
      Object var3 = null;
      Object var4 = null;
      if (this.typeproc == 1) {
         int var5 = this.init_random.nextInt();
         int var6 = this.init_random.nextInt();
         switch (this.size) {
            case 512:
               this.procedure_A(var5, var6, var1, 512);
               break;
            case 1024:
               this.procedure_B(var5, var6, var1);
               break;
            default:
               throw new IllegalArgumentException("Ooops! key size 512 or 1024 bit.");
         }

         var3 = var1[0];
         var2 = var1[1];
         var4 = this.procedure_C((BigInteger)var3, (BigInteger)var2);
         return new GOST3410Parameters((BigInteger)var3, (BigInteger)var2, (BigInteger)var4, new GOST3410ValidationParameters(var5, var6));
      } else {
         long var7 = this.init_random.nextLong();
         long var9 = this.init_random.nextLong();
         switch (this.size) {
            case 512:
               this.procedure_Aa(var7, var9, var1, 512);
               break;
            case 1024:
               this.procedure_Bb(var7, var9, var1);
               break;
            default:
               throw new IllegalStateException("Ooops! key size 512 or 1024 bit.");
         }

         var3 = var1[0];
         var2 = var1[1];
         var4 = this.procedure_C((BigInteger)var3, (BigInteger)var2);
         return new GOST3410Parameters((BigInteger)var3, (BigInteger)var2, (BigInteger)var4, new GOST3410ValidationParameters(var7, var9));
      }
   }
}
