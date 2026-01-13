package org.bouncycastle.crypto.params;

import java.math.BigInteger;
import org.bouncycastle.crypto.CryptoServicesRegistrar;
import org.bouncycastle.math.Primes;
import org.bouncycastle.util.BigIntegers;
import org.bouncycastle.util.Properties;

public class RSAKeyParameters extends AsymmetricKeyParameter {
   private static final BigIntegers.Cache validated = new BigIntegers.Cache();
   private static final BigInteger SMALL_PRIMES_PRODUCT = new BigInteger(
      "8138e8a0fcf3a4e84a771d40fd305d7f4aa59306d7251de54d98af8fe95729a1f73d893fa424cd2edc8636a6c3285e022b0e3866a565ae8108eed8591cd4fe8d2ce86165a978d719ebf647f362d33fca29cd179fb42401cbaf3df0c614056f9c8f3cfd51e474afb6bc6974f78db8aba8e9e517fded658591ab7502bd41849462f",
      16
   );
   private BigInteger modulus;
   private BigInteger exponent;

   public RSAKeyParameters(boolean var1, BigInteger var2, BigInteger var3) {
      this(var1, var2, var3, false);
   }

   public RSAKeyParameters(boolean var1, BigInteger var2, BigInteger var3, boolean var4) {
      super(var1);
      if (!var1 && (var3.intValue() & 1) == 0) {
         throw new IllegalArgumentException("RSA publicExponent is even");
      } else {
         this.modulus = validated.contains(var2) ? var2 : validate(var2, var4);
         this.exponent = var3;
      }
   }

   private static boolean hasAnySmallFactors(BigInteger var0) {
      BigInteger var1 = var0;
      BigInteger var2 = SMALL_PRIMES_PRODUCT;
      if (var0.bitLength() < SMALL_PRIMES_PRODUCT.bitLength()) {
         var1 = SMALL_PRIMES_PRODUCT;
         var2 = var0;
      }

      return !BigIntegers.modOddIsCoprimeVar(var1, var2);
   }

   private static BigInteger validate(BigInteger var0, boolean var1) {
      if (var1) {
         validated.add(var0);
         return var0;
      } else if ((var0.intValue() & 1) == 0) {
         throw new IllegalArgumentException("RSA modulus is even");
      } else if (Properties.isOverrideSet("org.bouncycastle.rsa.allow_unsafe_mod")) {
         return var0;
      } else {
         int var2 = Properties.asInteger("org.bouncycastle.rsa.max_size", 16384);
         if (var2 < var0.bitLength()) {
            throw new IllegalArgumentException("RSA modulus out of range");
         } else if (hasAnySmallFactors(var0)) {
            throw new IllegalArgumentException("RSA modulus has a small prime factor");
         } else {
            int var3 = var0.bitLength() / 2;
            int var4 = Properties.asInteger("org.bouncycastle.rsa.max_mr_tests", getMRIterations(var3));
            if (var4 > 0) {
               Primes.MROutput var5 = Primes.enhancedMRProbablePrimeTest(var0, CryptoServicesRegistrar.getSecureRandom(), var4);
               if (!var5.isProvablyComposite()) {
                  throw new IllegalArgumentException("RSA modulus is not composite");
               }
            }

            validated.add(var0);
            return var0;
         }
      }
   }

   private static int getMRIterations(int var0) {
      return var0 >= 1536 ? 3 : (var0 >= 1024 ? 4 : (var0 >= 512 ? 7 : 50));
   }

   public BigInteger getModulus() {
      return this.modulus;
   }

   public BigInteger getExponent() {
      return this.exponent;
   }
}
