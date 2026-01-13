package org.bouncycastle.math.ec.tools;

import java.math.BigInteger;
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
import org.bouncycastle.util.Strings;

public class F2mSqrtOptimizer {
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
               implPrintRootZ(var5);
            }
         }
      }
   }

   public static void printRootZ(ECCurve var0) {
      if (!ECAlgorithms.isF2mCurve(var0)) {
         throw new IllegalArgumentException("Sqrt optimization only defined over characteristic-2 fields");
      } else {
         implPrintRootZ(var0);
      }
   }

   private static void implPrintRootZ(ECCurve var0) {
      ECFieldElement var1 = var0.fromBigInteger(BigInteger.valueOf(2L));
      ECFieldElement var2 = var1.sqrt();
      System.out.println(Strings.toUpperCase(var2.toBigInteger().toString(16)));
      if (!var2.square().equals(var1)) {
         throw new IllegalStateException("Optimized-sqrt sanity check failed");
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
