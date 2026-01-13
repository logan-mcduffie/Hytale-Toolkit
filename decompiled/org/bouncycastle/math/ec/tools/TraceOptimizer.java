package org.bouncycastle.math.ec.tools;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.TreeSet;
import org.bouncycastle.asn1.x9.ECNamedCurveTable;
import org.bouncycastle.asn1.x9.X9ECParametersHolder;
import org.bouncycastle.crypto.ec.CustomNamedCurves;
import org.bouncycastle.math.ec.ECAlgorithms;
import org.bouncycastle.math.ec.ECCurve;
import org.bouncycastle.math.ec.ECFieldElement;
import org.bouncycastle.util.Integers;

public class TraceOptimizer {
   private static final BigInteger ONE = BigInteger.valueOf(1L);
   private static final SecureRandom R = new SecureRandom();

   public static void main(String[] var0) {
      TreeSet var1 = new TreeSet(enumToList(ECNamedCurveTable.getNames()));
      var1.addAll(enumToList(CustomNamedCurves.getNames()));

      for (String var3 : var1) {
         X9ECParametersHolder var4 = CustomNamedCurves.getByNameLazy(var3);
         if (var4 == null) {
            var4 = ECNamedCurveTable.getByNameLazy(var3);
         }

         if (var4 != null) {
            ECCurve var5 = var4.getCurve();
            if (ECAlgorithms.isF2mCurve(var5)) {
               System.out.print(var3 + ":");
               implPrintNonZeroTraceBits(var5);
            }
         }
      }
   }

   public static void printNonZeroTraceBits(ECCurve var0) {
      if (!ECAlgorithms.isF2mCurve(var0)) {
         throw new IllegalArgumentException("Trace only defined over characteristic-2 fields");
      } else {
         implPrintNonZeroTraceBits(var0);
      }
   }

   public static void implPrintNonZeroTraceBits(ECCurve var0) {
      int var1 = var0.getFieldSize();
      ArrayList var2 = new ArrayList();

      for (int var3 = 0; var3 < var1; var3++) {
         if (0 != (var3 & 1) || 0 == var3) {
            BigInteger var4 = ONE.shiftLeft(var3);
            ECFieldElement var5 = var0.fromBigInteger(var4);
            int var6 = calculateTrace(var5);
            if (var6 != 0) {
               var2.add(Integers.valueOf(var3));
               System.out.print(" " + var3);
            }
         } else if (var2.contains(Integers.valueOf(var3 >>> 1))) {
            var2.add(Integers.valueOf(var3));
            System.out.print(" " + var3);
         }
      }

      System.out.println();

      for (int var10 = 0; var10 < 1000; var10++) {
         BigInteger var11 = new BigInteger(var1, R);
         ECFieldElement var12 = var0.fromBigInteger(var11);
         int var13 = calculateTrace(var12);
         byte var7 = 0;

         for (int var8 = 0; var8 < var2.size(); var8++) {
            int var9 = (Integer)var2.get(var8);
            if (var11.testBit(var9)) {
               var7 ^= 1;
            }
         }

         if (var13 != var7) {
            throw new IllegalStateException("Optimized-trace sanity check failed");
         }
      }
   }

   private static int calculateTrace(ECFieldElement var0) {
      int var1 = var0.getFieldSize();
      int var2 = 31 - Integers.numberOfLeadingZeros(var1);
      int var3 = 1;
      ECFieldElement var4 = var0;

      while (var2 > 0) {
         var4 = var4.squarePow(var3).add(var4);
         var3 = var1 >>> --var2;
         if (0 != (var3 & 1)) {
            var4 = var4.square().add(var0);
         }
      }

      if (var4.isZero()) {
         return 0;
      } else if (var4.isOne()) {
         return 1;
      } else {
         throw new IllegalStateException("Internal error in trace calculation");
      }
   }

   private static List enumToList(Enumeration var0) {
      ArrayList var1 = new ArrayList();

      while (var0.hasMoreElements()) {
         var1.add(var0.nextElement());
      }

      return var1;
   }
}
