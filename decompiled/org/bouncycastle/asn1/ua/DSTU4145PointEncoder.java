package org.bouncycastle.asn1.ua;

import java.math.BigInteger;
import java.util.Random;
import org.bouncycastle.math.ec.ECConstants;
import org.bouncycastle.math.ec.ECCurve;
import org.bouncycastle.math.ec.ECFieldElement;
import org.bouncycastle.math.ec.ECPoint;

public abstract class DSTU4145PointEncoder {
   private static ECFieldElement trace(ECFieldElement var0) {
      ECFieldElement var1 = var0;

      for (int var2 = 1; var2 < var0.getFieldSize(); var2++) {
         var1 = var1.square().add(var0);
      }

      return var1;
   }

   private static ECFieldElement solveQuadraticEquation(ECCurve var0, ECFieldElement var1) {
      if (var1.isZero()) {
         return var1;
      } else {
         ECFieldElement var2 = var0.fromBigInteger(ECConstants.ZERO);
         ECFieldElement var3 = null;
         Object var4 = null;
         Random var5 = new Random();
         int var6 = var1.getFieldSize();

         do {
            ECFieldElement var7 = var0.fromBigInteger(new BigInteger(var6, var5));
            var3 = var2;
            ECFieldElement var8 = var1;

            for (int var9 = 1; var9 <= var6 - 1; var9++) {
               ECFieldElement var10 = var8.square();
               var3 = var3.square().add(var10.multiply(var7));
               var8 = var10.add(var1);
            }

            if (!var8.isZero()) {
               return null;
            }

            var4 = var3.square().add(var3);
         } while (((ECFieldElement)var4).isZero());

         return var3;
      }
   }

   public static byte[] encodePoint(ECPoint var0) {
      var0 = var0.normalize();
      ECFieldElement var1 = var0.getAffineXCoord();
      byte[] var2 = var1.getEncoded();
      if (!var1.isZero()) {
         ECFieldElement var3 = var0.getAffineYCoord().divide(var1);
         if (trace(var3).isOne()) {
            var2[var2.length - 1] = (byte)(var2[var2.length - 1] | 1);
         } else {
            var2[var2.length - 1] = (byte)(var2[var2.length - 1] & 254);
         }
      }

      return var2;
   }

   public static ECPoint decodePoint(ECCurve var0, byte[] var1) {
      ECFieldElement var2 = var0.fromBigInteger(BigInteger.valueOf(var1[var1.length - 1] & 1));
      ECFieldElement var3 = var0.fromBigInteger(new BigInteger(1, var1));
      if (!trace(var3).equals(var0.getA())) {
         var3 = var3.addOne();
      }

      ECFieldElement var4 = null;
      if (var3.isZero()) {
         var4 = var0.getB().sqrt();
      } else {
         ECFieldElement var5 = var3.square().invert().multiply(var0.getB()).add(var0.getA()).add(var3);
         ECFieldElement var6 = solveQuadraticEquation(var0, var5);
         if (var6 != null) {
            if (!trace(var6).equals(var2)) {
               var6 = var6.addOne();
            }

            var4 = var3.multiply(var6);
         }
      }

      if (var4 == null) {
         throw new IllegalArgumentException("Invalid point compression");
      } else {
         return var0.validatePoint(var3.toBigInteger(), var4.toBigInteger());
      }
   }
}
