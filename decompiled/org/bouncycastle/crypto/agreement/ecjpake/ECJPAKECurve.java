package org.bouncycastle.crypto.agreement.ecjpake;

import java.math.BigInteger;
import org.bouncycastle.math.ec.ECCurve;
import org.bouncycastle.math.ec.ECPoint;

public class ECJPAKECurve {
   private final ECCurve.AbstractFp curve;
   private final ECPoint g;

   public ECJPAKECurve(BigInteger var1, BigInteger var2, BigInteger var3, BigInteger var4, BigInteger var5, BigInteger var6, BigInteger var7) {
      ECJPAKEUtil.validateNotNull(var2, "a");
      ECJPAKEUtil.validateNotNull(var3, "b");
      ECJPAKEUtil.validateNotNull(var1, "q");
      ECJPAKEUtil.validateNotNull(var4, "n");
      ECJPAKEUtil.validateNotNull(var5, "h");
      ECJPAKEUtil.validateNotNull(var6, "g_x");
      ECJPAKEUtil.validateNotNull(var7, "g_y");
      if (!var1.isProbablePrime(20)) {
         throw new IllegalArgumentException("Field size q must be prime");
      } else if (var2.compareTo(BigInteger.ZERO) < 0 || var2.compareTo(var1) >= 0) {
         throw new IllegalArgumentException("The parameter 'a' is not in the field [0, q-1]");
      } else if (var3.compareTo(BigInteger.ZERO) >= 0 && var3.compareTo(var1) < 0) {
         BigInteger var8 = calculateDeterminant(var1, var2, var3);
         if (var8.equals(BigInteger.ZERO)) {
            throw new IllegalArgumentException("The curve is singular, i.e the discriminant is equal to 0 mod q.");
         } else if (!var4.isProbablePrime(20)) {
            throw new IllegalArgumentException("The order n must be prime");
         } else {
            this.curve = new ECCurve.Fp(var1, var2, var3, var4, var5);
            this.g = this.curve.validatePoint(var6, var7);
         }
      } else {
         throw new IllegalArgumentException("The parameter 'b' is not in the field [0, q-1]");
      }
   }

   ECJPAKECurve(ECCurve.AbstractFp var1, ECPoint var2) {
      ECJPAKEUtil.validateNotNull(var1, "curve");
      ECJPAKEUtil.validateNotNull(var2, "g");
      ECJPAKEUtil.validateNotNull(var1.getOrder(), "n");
      ECJPAKEUtil.validateNotNull(var1.getCofactor(), "h");
      this.curve = var1;
      this.g = var2;
   }

   public ECCurve.AbstractFp getCurve() {
      return this.curve;
   }

   public ECPoint getG() {
      return this.g;
   }

   public BigInteger getA() {
      return this.curve.getA().toBigInteger();
   }

   public BigInteger getB() {
      return this.curve.getB().toBigInteger();
   }

   public BigInteger getN() {
      return this.curve.getOrder();
   }

   public BigInteger getH() {
      return this.curve.getCofactor();
   }

   public BigInteger getQ() {
      return this.curve.getQ();
   }

   private static BigInteger calculateDeterminant(BigInteger var0, BigInteger var1, BigInteger var2) {
      BigInteger var3 = var1.multiply(var1).mod(var0).multiply(var1).mod(var0).shiftLeft(2);
      BigInteger var4 = var2.multiply(var2).mod(var0).multiply(BigInteger.valueOf(27L));
      return var3.add(var4).mod(var0);
   }
}
