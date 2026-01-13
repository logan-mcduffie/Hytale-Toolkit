package org.bouncycastle.crypto.generators;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Vector;
import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.AsymmetricCipherKeyPairGenerator;
import org.bouncycastle.crypto.CryptoServicePurpose;
import org.bouncycastle.crypto.CryptoServicesRegistrar;
import org.bouncycastle.crypto.KeyGenerationParameters;
import org.bouncycastle.crypto.constraints.ConstraintUtils;
import org.bouncycastle.crypto.constraints.DefaultServiceProperties;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.crypto.params.NaccacheSternKeyGenerationParameters;
import org.bouncycastle.crypto.params.NaccacheSternKeyParameters;
import org.bouncycastle.crypto.params.NaccacheSternPrivateKeyParameters;
import org.bouncycastle.util.BigIntegers;

public class NaccacheSternKeyPairGenerator implements AsymmetricCipherKeyPairGenerator {
   private static int[] smallPrimes = new int[]{
      3,
      5,
      7,
      11,
      13,
      17,
      19,
      23,
      29,
      31,
      37,
      41,
      43,
      47,
      53,
      59,
      61,
      67,
      71,
      73,
      79,
      83,
      89,
      97,
      101,
      103,
      107,
      109,
      113,
      127,
      131,
      137,
      139,
      149,
      151,
      157,
      163,
      167,
      173,
      179,
      181,
      191,
      193,
      197,
      199,
      211,
      223,
      227,
      229,
      233,
      239,
      241,
      251,
      257,
      263,
      269,
      271,
      277,
      281,
      283,
      293,
      307,
      311,
      313,
      317,
      331,
      337,
      347,
      349,
      353,
      359,
      367,
      373,
      379,
      383,
      389,
      397,
      401,
      409,
      419,
      421,
      431,
      433,
      439,
      443,
      449,
      457,
      461,
      463,
      467,
      479,
      487,
      491,
      499,
      503,
      509,
      521,
      523,
      541,
      547,
      557
   };
   private NaccacheSternKeyGenerationParameters param;
   private static final BigInteger ONE = BigInteger.valueOf(1L);

   @Override
   public void init(KeyGenerationParameters var1) {
      this.param = (NaccacheSternKeyGenerationParameters)var1;
      CryptoServicesRegistrar.checkConstraints(
         new DefaultServiceProperties("NaccacheStern KeyGen", ConstraintUtils.bitsOfSecurityForFF(var1.getStrength()), var1, CryptoServicePurpose.KEYGEN)
      );
   }

   @Override
   public AsymmetricCipherKeyPair generateKeyPair() {
      int var1 = this.param.getStrength();
      SecureRandom var2 = this.param.getRandom();
      int var3 = this.param.getCertainty();
      boolean var4 = this.param.isDebug();
      if (var4) {
         System.out.println("Fetching first " + this.param.getCntSmallPrimes() + " primes.");
      }

      Vector var5 = findFirstPrimes(this.param.getCntSmallPrimes());
      var5 = permuteList(var5, var2);
      BigInteger var6 = ONE;
      BigInteger var7 = ONE;

      for (int var8 = 0; var8 < var5.size() / 2; var8++) {
         var6 = var6.multiply((BigInteger)var5.elementAt(var8));
      }

      for (int var28 = var5.size() / 2; var28 < var5.size(); var28++) {
         var7 = var7.multiply((BigInteger)var5.elementAt(var28));
      }

      BigInteger var29 = var6.multiply(var7);
      int var9 = var1 - var29.bitLength() - 48;
      BigInteger var10 = generatePrime(var9 / 2 + 1, var3, var2);
      BigInteger var11 = generatePrime(var9 / 2 + 1, var3, var2);
      long var16 = 0L;
      if (var4) {
         System.out.println("generating p and q");
      }

      BigInteger var18 = var10.multiply(var6).shiftLeft(1);
      BigInteger var19 = var11.multiply(var7).shiftLeft(1);

      while (true) {
         var16++;
         BigInteger var12 = generatePrime(24, var3, var2);
         BigInteger var14 = var12.multiply(var18).add(ONE);
         if (var14.isProbablePrime(var3)) {
            while (true) {
               BigInteger var13 = generatePrime(24, var3, var2);
               if (!var12.equals(var13)) {
                  BigInteger var15 = var13.multiply(var19).add(ONE);
                  if (var15.isProbablePrime(var3)) {
                     if (BigIntegers.modOddIsCoprime(var12.multiply(var13), var29)) {
                        BigInteger var20 = var14.multiply(var15);
                        if (var20.bitLength() >= var1) {
                           if (var4) {
                              System.out.println("needed " + var16 + " tries to generate p and q.");
                           }

                           BigInteger var21 = var14.subtract(ONE).multiply(var15.subtract(ONE));
                           var16 = 0L;
                           if (var4) {
                              System.out.println("generating g");
                           }

                           while (true) {
                              Vector var23 = new Vector();

                              for (int var24 = 0; var24 != var5.size(); var24++) {
                                 BigInteger var25 = (BigInteger)var5.elementAt(var24);
                                 BigInteger var26 = var21.divide(var25);

                                 BigInteger var22;
                                 do {
                                    var16++;
                                    var22 = BigIntegers.createRandomPrime(var1, var3, var2);
                                 } while (var22.modPow(var26, var20).equals(ONE));

                                 var23.addElement(var22);
                              }

                              BigInteger var31 = ONE;

                              for (int var32 = 0; var32 < var5.size(); var32++) {
                                 var31 = var31.multiply(((BigInteger)var23.elementAt(var32)).modPow(var29.divide((BigInteger)var5.elementAt(var32)), var20))
                                    .mod(var20);
                              }

                              boolean var33 = false;

                              for (int var34 = 0; var34 < var5.size(); var34++) {
                                 if (var31.modPow(var21.divide((BigInteger)var5.elementAt(var34)), var20).equals(ONE)) {
                                    if (var4) {
                                       System.out.println("g has order phi(n)/" + var5.elementAt(var34) + "\n g: " + var31);
                                    }

                                    var33 = true;
                                    break;
                                 }
                              }

                              if (!var33) {
                                 if (var31.modPow(var21.divide(BigInteger.valueOf(4L)), var20).equals(ONE)) {
                                    if (var4) {
                                       System.out.println("g has order phi(n)/4\n g:" + var31);
                                    }
                                 } else if (var31.modPow(var21.divide(var12), var20).equals(ONE)) {
                                    if (var4) {
                                       System.out.println("g has order phi(n)/p'\n g: " + var31);
                                    }
                                 } else if (var31.modPow(var21.divide(var13), var20).equals(ONE)) {
                                    if (var4) {
                                       System.out.println("g has order phi(n)/q'\n g: " + var31);
                                    }
                                 } else if (var31.modPow(var21.divide(var10), var20).equals(ONE)) {
                                    if (var4) {
                                       System.out.println("g has order phi(n)/a\n g: " + var31);
                                    }
                                 } else {
                                    if (!var31.modPow(var21.divide(var11), var20).equals(ONE)) {
                                       if (var4) {
                                          System.out.println("needed " + var16 + " tries to generate g");
                                          System.out.println();
                                          System.out.println("found new NaccacheStern cipher variables:");
                                          System.out.println("smallPrimes: " + var5);
                                          System.out.println("sigma:...... " + var29 + " (" + var29.bitLength() + " bits)");
                                          System.out.println("a:.......... " + var10);
                                          System.out.println("b:.......... " + var11);
                                          System.out.println("p':......... " + var12);
                                          System.out.println("q':......... " + var13);
                                          System.out.println("p:.......... " + var14);
                                          System.out.println("q:.......... " + var15);
                                          System.out.println("n:.......... " + var20);
                                          System.out.println("phi(n):..... " + var21);
                                          System.out.println("g:.......... " + var31);
                                          System.out.println();
                                       }

                                       return new AsymmetricCipherKeyPair(
                                          (AsymmetricKeyParameter)(new NaccacheSternKeyParameters(false, var31, var20, var29.bitLength())),
                                          (AsymmetricKeyParameter)(new NaccacheSternPrivateKeyParameters(var31, var20, var29.bitLength(), var5, var21))
                                       );
                                    }

                                    if (var4) {
                                       System.out.println("g has order phi(n)/b\n g: " + var31);
                                    }
                                 }
                              }
                           }
                        }

                        if (var4) {
                           System.out.println("key size too small. Should be " + var1 + " but is actually " + var14.multiply(var15).bitLength());
                        }
                     }
                     break;
                  }
               }
            }
         }
      }
   }

   private static BigInteger generatePrime(int var0, int var1, SecureRandom var2) {
      BigInteger var3 = BigIntegers.createRandomPrime(var0, var1, var2);

      while (var3.bitLength() != var0) {
         var3 = BigIntegers.createRandomPrime(var0, var1, var2);
      }

      return var3;
   }

   private static Vector permuteList(Vector var0, SecureRandom var1) {
      Vector var2 = new Vector();
      Vector var3 = new Vector();

      for (int var4 = 0; var4 < var0.size(); var4++) {
         var3.addElement(var0.elementAt(var4));
      }

      var2.addElement(var3.elementAt(0));
      var3.removeElementAt(0);

      while (var3.size() != 0) {
         var2.insertElementAt(var3.elementAt(0), getInt(var1, var2.size() + 1));
         var3.removeElementAt(0);
      }

      return var2;
   }

   private static int getInt(SecureRandom var0, int var1) {
      if ((var1 & -var1) == var1) {
         return (int)((long)var1 * (var0.nextInt() & 2147483647) >> 31);
      } else {
         int var2;
         int var3;
         do {
            var2 = var0.nextInt() & 2147483647;
            var3 = var2 % var1;
         } while (var2 - var3 + (var1 - 1) < 0);

         return var3;
      }
   }

   private static Vector findFirstPrimes(int var0) {
      Vector var1 = new Vector(var0);

      for (int var2 = 0; var2 != var0; var2++) {
         var1.addElement(BigInteger.valueOf(smallPrimes[var2]));
      }

      return var1;
   }
}
