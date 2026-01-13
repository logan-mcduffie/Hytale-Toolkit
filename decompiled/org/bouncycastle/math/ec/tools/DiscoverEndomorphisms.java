package org.bouncycastle.math.ec.tools;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;
import org.bouncycastle.asn1.x9.ECNamedCurveTable;
import org.bouncycastle.asn1.x9.X9ECParameters;
import org.bouncycastle.crypto.ec.CustomNamedCurves;
import org.bouncycastle.math.ec.ECAlgorithms;
import org.bouncycastle.math.ec.ECConstants;
import org.bouncycastle.math.ec.ECCurve;
import org.bouncycastle.math.ec.ECFieldElement;
import org.bouncycastle.math.ec.ECPoint;
import org.bouncycastle.util.BigIntegers;

public class DiscoverEndomorphisms {
   private static final int radix = 16;

   public static void main(String[] var0) {
      if (var0.length > 0) {
         for (int var1 = 0; var1 < var0.length; var1++) {
            discoverEndomorphisms(var0[var1]);
         }
      } else {
         TreeSet var3 = new TreeSet(enumToList(ECNamedCurveTable.getNames()));
         var3.addAll(enumToList(CustomNamedCurves.getNames()));
         Iterator var2 = var3.iterator();

         while (var2.hasNext()) {
            discoverEndomorphisms((String)var2.next());
         }
      }
   }

   public static void discoverEndomorphisms(X9ECParameters var0) {
      if (var0 == null) {
         throw new NullPointerException("x9");
      } else {
         discoverEndomorphisms(var0, "<UNKNOWN>");
      }
   }

   private static void discoverEndomorphisms(String var0) {
      X9ECParameters var1 = CustomNamedCurves.getByName(var0);
      if (var1 == null) {
         var1 = ECNamedCurveTable.getByName(var0);
         if (var1 == null) {
            System.err.println("Unknown curve: " + var0);
            return;
         }
      }

      discoverEndomorphisms(var1, var0);
   }

   private static void discoverEndomorphisms(X9ECParameters var0, String var1) {
      ECCurve var2 = var0.getCurve();
      if (ECAlgorithms.isFpCurve(var2)) {
         BigInteger var3 = var2.getField().getCharacteristic();
         if (var2.getB().isZero() && var3.mod(ECConstants.FOUR).equals(ECConstants.ONE)) {
            System.out.println("Curve '" + var1 + "' has a 'GLV Type A' endomorphism with these parameters:");
            printGLVTypeAParameters(var0);
         }

         if (var2.getA().isZero() && var3.mod(ECConstants.THREE).equals(ECConstants.ONE)) {
            System.out.println("Curve '" + var1 + "' has a 'GLV Type B' endomorphism with these parameters:");
            printGLVTypeBParameters(var0);
         }
      }
   }

   private static void printGLVTypeAParameters(X9ECParameters var0) {
      BigInteger[] var1 = solveQuadraticEquation(var0.getN(), ECConstants.ONE, ECConstants.ZERO, ECConstants.ONE);
      ECFieldElement[] var2 = findNonTrivialOrder4FieldElements(var0.getCurve());
      printGLVTypeAParameters(var0, var1[0], var2);
      System.out.println("OR");
      printGLVTypeAParameters(var0, var1[1], var2);
   }

   private static void printGLVTypeAParameters(X9ECParameters var0, BigInteger var1, ECFieldElement[] var2) {
      ECPoint var3 = var0.getG().normalize();
      ECPoint var4 = var3.multiply(var1).normalize();
      if (!var3.getXCoord().negate().equals(var4.getXCoord())) {
         throw new IllegalStateException("Derivation of GLV Type A parameters failed unexpectedly");
      } else {
         ECFieldElement var5 = var2[0];
         if (!var3.getYCoord().multiply(var5).equals(var4.getYCoord())) {
            var5 = var2[1];
            if (!var3.getYCoord().multiply(var5).equals(var4.getYCoord())) {
               throw new IllegalStateException("Derivation of GLV Type A parameters failed unexpectedly");
            }
         }

         printProperty("Point map", "lambda * (x, y) = (-x, i * y)");
         printProperty("i", var5.toBigInteger().toString(16));
         printProperty("lambda", var1.toString(16));
         printScalarDecompositionParameters(var0.getN(), var1);
      }
   }

   private static void printGLVTypeBParameters(X9ECParameters var0) {
      BigInteger[] var1 = solveQuadraticEquation(var0.getN(), ECConstants.ONE, ECConstants.ONE, ECConstants.ONE);
      ECFieldElement[] var2 = findNonTrivialOrder3FieldElements(var0.getCurve());
      printGLVTypeBParameters(var0, var1[0], var2);
      System.out.println("OR");
      printGLVTypeBParameters(var0, var1[1], var2);
   }

   private static void printGLVTypeBParameters(X9ECParameters var0, BigInteger var1, ECFieldElement[] var2) {
      ECPoint var3 = var0.getG().normalize();
      ECPoint var4 = var3.multiply(var1).normalize();
      if (!var3.getYCoord().equals(var4.getYCoord())) {
         throw new IllegalStateException("Derivation of GLV Type B parameters failed unexpectedly");
      } else {
         ECFieldElement var5 = var2[0];
         if (!var3.getXCoord().multiply(var5).equals(var4.getXCoord())) {
            var5 = var2[1];
            if (!var3.getXCoord().multiply(var5).equals(var4.getXCoord())) {
               throw new IllegalStateException("Derivation of GLV Type B parameters failed unexpectedly");
            }
         }

         printProperty("Point map", "lambda * (x, y) = (beta * x, y)");
         printProperty("beta", var5.toBigInteger().toString(16));
         printProperty("lambda", var1.toString(16));
         printScalarDecompositionParameters(var0.getN(), var1);
      }
   }

   private static void printProperty(String var0, Object var1) {
      StringBuilder var2 = new StringBuilder("  ");
      var2.append(var0);

      while (var2.length() < 20) {
         var2.append(' ');
      }

      var2.append(": ");
      var2.append(var1.toString());
      System.out.println(var2.toString());
   }

   private static void printScalarDecompositionParameters(BigInteger var0, BigInteger var1) {
      Object var2 = null;
      Object var3 = null;
      BigInteger[] var4 = extEuclidGLV(var0, var1);
      var2 = new BigInteger[]{var4[2], var4[3].negate()};
      var3 = chooseShortest(new BigInteger[]{var4[0], var4[1].negate()}, new BigInteger[]{var4[4], var4[5].negate()});
      if (!isVectorBoundedBySqrt((BigInteger[])var3, var0) && areRelativelyPrime((BigInteger)((Object[])var2)[0], (BigInteger)((Object[])var2)[1])) {
         BigInteger var5 = (BigInteger)((Object[])var2)[0];
         BigInteger var6 = (BigInteger)((Object[])var2)[1];
         BigInteger var7 = var5.add(var6.multiply(var1)).divide(var0);
         BigInteger[] var8 = extEuclidBezout(new BigInteger[]{var7.abs(), var6.abs()});
         if (var8 != null) {
            BigInteger var9 = var8[0];
            BigInteger var10 = var8[1];
            if (var7.signum() < 0) {
               var9 = var9.negate();
            }

            if (var6.signum() > 0) {
               var10 = var10.negate();
            }

            BigInteger var11 = var7.multiply(var9).subtract(var6.multiply(var10));
            if (!var11.equals(ECConstants.ONE)) {
               throw new IllegalStateException();
            }

            BigInteger var12 = var10.multiply(var0).subtract(var9.multiply(var1));
            BigInteger var13 = var9.negate();
            BigInteger var14 = var12.negate();
            BigInteger var15 = isqrt(var0.subtract(ECConstants.ONE)).add(ECConstants.ONE);
            BigInteger[] var16 = calculateRange(var13, var15, var6);
            BigInteger[] var17 = calculateRange(var14, var15, var5);
            BigInteger[] var18 = intersect(var16, var17);
            if (var18 != null) {
               for (BigInteger var19 = var18[0]; var19.compareTo(var18[1]) <= 0; var19 = var19.add(ECConstants.ONE)) {
                  BigInteger[] var20 = new BigInteger[]{var12.add(var19.multiply(var5)), var9.add(var19.multiply(var6))};
                  if (isShorter(var20, (BigInteger[])var3)) {
                     var3 = var20;
                  }
               }
            }
         }
      }

      BigInteger var23 = ((Object[])var2)[0].multiply((BigInteger)((Object[])var3)[1]).subtract(((Object[])var2)[1].multiply((BigInteger)((Object[])var3)[0]));
      int var24 = var0.bitLength() + 16 - (var0.bitLength() & 7);
      BigInteger var25 = roundQuotient(((Object[])var3)[1].shiftLeft(var24), var23);
      BigInteger var26 = roundQuotient(((Object[])var2)[1].shiftLeft(var24), var23).negate();
      printProperty("v1", "{ " + ((Object[])var2)[0].toString(16) + ", " + ((Object[])var2)[1].toString(16) + " }");
      printProperty("v2", "{ " + ((Object[])var3)[0].toString(16) + ", " + ((Object[])var3)[1].toString(16) + " }");
      printProperty("d", var23.toString(16));
      printProperty("(OPT) g1", var25.toString(16));
      printProperty("(OPT) g2", var26.toString(16));
      printProperty("(OPT) bits", Integer.toString(var24));
   }

   private static boolean areRelativelyPrime(BigInteger var0, BigInteger var1) {
      return var0.gcd(var1).equals(ECConstants.ONE);
   }

   private static BigInteger[] calculateRange(BigInteger var0, BigInteger var1, BigInteger var2) {
      BigInteger var3 = var0.subtract(var1).divide(var2);
      BigInteger var4 = var0.add(var1).divide(var2);
      return order(var3, var4);
   }

   private static List enumToList(Enumeration var0) {
      ArrayList var1 = new ArrayList();

      while (var0.hasMoreElements()) {
         var1.add(var0.nextElement());
      }

      return var1;
   }

   private static BigInteger[] extEuclidBezout(BigInteger[] var0) {
      boolean var1 = var0[0].compareTo(var0[1]) < 0;
      if (var1) {
         swap(var0);
      }

      BigInteger var2 = var0[0];
      BigInteger var3 = var0[1];
      BigInteger var4 = ECConstants.ONE;
      BigInteger var5 = ECConstants.ZERO;
      BigInteger var6 = ECConstants.ZERO;
      BigInteger var7 = ECConstants.ONE;

      while (var3.compareTo(ECConstants.ONE) > 0) {
         BigInteger[] var8 = var2.divideAndRemainder(var3);
         BigInteger var9 = var8[0];
         BigInteger var10 = var8[1];
         BigInteger var11 = var4.subtract(var9.multiply(var5));
         BigInteger var12 = var6.subtract(var9.multiply(var7));
         var2 = var3;
         var3 = var10;
         var4 = var5;
         var5 = var11;
         var6 = var7;
         var7 = var12;
      }

      if (var3.signum() <= 0) {
         return null;
      } else {
         BigInteger[] var13 = new BigInteger[]{var5, var7};
         if (var1) {
            swap(var13);
         }

         return var13;
      }
   }

   private static BigInteger[] extEuclidGLV(BigInteger var0, BigInteger var1) {
      BigInteger var2 = var0;
      BigInteger var3 = var1;
      BigInteger var4 = ECConstants.ZERO;
      BigInteger var5 = ECConstants.ONE;

      while (true) {
         BigInteger[] var6 = var2.divideAndRemainder(var3);
         BigInteger var7 = var6[0];
         BigInteger var8 = var6[1];
         BigInteger var9 = var4.subtract(var7.multiply(var5));
         if (isLessThanSqrt(var3, var0)) {
            return new BigInteger[]{var2, var4, var3, var5, var8, var9};
         }

         var2 = var3;
         var3 = var8;
         var4 = var5;
         var5 = var9;
      }
   }

   private static BigInteger[] chooseShortest(BigInteger[] var0, BigInteger[] var1) {
      return isShorter(var0, var1) ? var0 : var1;
   }

   private static BigInteger[] intersect(BigInteger[] var0, BigInteger[] var1) {
      BigInteger var2 = var0[0].max(var1[0]);
      BigInteger var3 = var0[1].min(var1[1]);
      return var2.compareTo(var3) > 0 ? null : new BigInteger[]{var2, var3};
   }

   private static boolean isLessThanSqrt(BigInteger var0, BigInteger var1) {
      var0 = var0.abs();
      var1 = var1.abs();
      int var2 = var1.bitLength();
      int var3 = var0.bitLength() * 2;
      int var4 = var3 - 1;
      return var4 <= var2 && (var3 < var2 || var0.multiply(var0).compareTo(var1) < 0);
   }

   private static boolean isShorter(BigInteger[] var0, BigInteger[] var1) {
      BigInteger var2 = var0[0].abs();
      BigInteger var3 = var0[1].abs();
      BigInteger var4 = var1[0].abs();
      BigInteger var5 = var1[1].abs();
      boolean var6 = var2.compareTo(var4) < 0;
      boolean var7 = var3.compareTo(var5) < 0;
      if (var6 == var7) {
         return var6;
      } else {
         BigInteger var8 = var2.multiply(var2).add(var3.multiply(var3));
         BigInteger var9 = var4.multiply(var4).add(var5.multiply(var5));
         return var8.compareTo(var9) < 0;
      }
   }

   private static boolean isVectorBoundedBySqrt(BigInteger[] var0, BigInteger var1) {
      BigInteger var2 = var0[0].abs().max(var0[1].abs());
      return isLessThanSqrt(var2, var1);
   }

   private static BigInteger[] order(BigInteger var0, BigInteger var1) {
      return var0.compareTo(var1) <= 0 ? new BigInteger[]{var0, var1} : new BigInteger[]{var1, var0};
   }

   private static BigInteger roundQuotient(BigInteger var0, BigInteger var1) {
      boolean var2 = var0.signum() != var1.signum();
      var0 = var0.abs();
      var1 = var1.abs();
      BigInteger var3 = var0.add(var1.shiftRight(1)).divide(var1);
      return var2 ? var3.negate() : var3;
   }

   private static BigInteger[] solveQuadraticEquation(BigInteger var0, BigInteger var1, BigInteger var2, BigInteger var3) {
      BigInteger var4 = var2.multiply(var2).subtract(var1.multiply(var3).shiftLeft(2)).mod(var0);
      BigInteger var5 = modSqrt(var4, var0);
      if (var5 == null) {
         throw new IllegalStateException("Solving quadratic equation failed unexpectedly");
      } else {
         BigInteger var6 = var1.shiftLeft(1).modInverse(var0);
         BigInteger var7 = var5.subtract(var2).multiply(var6).mod(var0);
         BigInteger var8 = var5.negate().subtract(var2).multiply(var6).mod(var0);
         return new BigInteger[]{var7, var8};
      }
   }

   private static ECFieldElement[] findNonTrivialOrder3FieldElements(ECCurve var0) {
      BigInteger var1 = var0.getField().getCharacteristic();
      BigInteger var2 = var1.divide(ECConstants.THREE);
      SecureRandom var3 = new SecureRandom();

      BigInteger var4;
      do {
         BigInteger var5 = BigIntegers.createRandomInRange(ECConstants.TWO, var1.subtract(ECConstants.TWO), var3);
         var4 = var5.modPow(var2, var1);
      } while (var4.equals(ECConstants.ONE));

      ECFieldElement var6 = var0.fromBigInteger(var4);
      return new ECFieldElement[]{var6, var6.square()};
   }

   private static ECFieldElement[] findNonTrivialOrder4FieldElements(ECCurve var0) {
      ECFieldElement var1 = var0.fromBigInteger(ECConstants.ONE).negate().sqrt();
      if (var1 == null) {
         throw new IllegalStateException("Calculation of non-trivial order-4  field elements failed unexpectedly");
      } else {
         return new ECFieldElement[]{var1, var1.negate()};
      }
   }

   private static BigInteger isqrt(BigInteger var0) {
      BigInteger var1 = var0.shiftRight(var0.bitLength() / 2);

      while (true) {
         BigInteger var2 = var1.add(var0.divide(var1)).shiftRight(1);
         if (var2.equals(var1)) {
            return var2;
         }

         var1 = var2;
      }
   }

   private static void swap(BigInteger[] var0) {
      BigInteger var1 = var0[0];
      var0[0] = var0[1];
      var0[1] = var1;
   }

   private static BigInteger modSqrt(BigInteger var0, BigInteger var1) {
      if (!var1.testBit(0)) {
         throw new IllegalStateException();
      } else {
         BigInteger var2 = var1.subtract(ECConstants.ONE).shiftRight(1);
         BigInteger var3 = var2;
         if (!var0.modPow(var2, var1).equals(ECConstants.ONE)) {
            return null;
         } else {
            while (!var3.testBit(0)) {
               var3 = var3.shiftRight(1);
               if (!var0.modPow(var3, var1).equals(ECConstants.ONE)) {
                  return modSqrtComplex(var0, var3, var1, var2);
               }
            }

            var3 = var3.add(ECConstants.ONE).shiftRight(1);
            return var0.modPow(var3, var1);
         }
      }
   }

   private static BigInteger modSqrtComplex(BigInteger var0, BigInteger var1, BigInteger var2, BigInteger var3) {
      BigInteger var4 = firstNonResidue(var2, var3);
      BigInteger var5 = var3;
      BigInteger var6 = var3;

      while (!var1.testBit(0)) {
         var1 = var1.shiftRight(1);
         var5 = var5.shiftRight(1);
         if (!var0.modPow(var1, var2).equals(var4.modPow(var5, var2))) {
            var5 = var5.add(var6);
         }
      }

      var1 = var1.subtract(ECConstants.ONE).shiftRight(1);
      var5 = var5.shiftRight(1);
      BigInteger var7 = var0.modInverse(var2);
      BigInteger var8 = var7.modPow(var1, var2);
      BigInteger var9 = var4.modPow(var5, var2);
      return var8.multiply(var9).mod(var2);
   }

   private static BigInteger firstNonResidue(BigInteger var0, BigInteger var1) {
      for (int var2 = 2; var2 < 1000; var2++) {
         BigInteger var3 = BigInteger.valueOf(var2);
         if (!var3.modPow(var1, var0).equals(ECConstants.ONE)) {
            return var3;
         }
      }

      throw new IllegalStateException();
   }
}
