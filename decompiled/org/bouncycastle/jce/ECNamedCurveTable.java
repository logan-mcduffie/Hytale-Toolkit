package org.bouncycastle.jce;

import java.util.Enumeration;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.x9.X9ECParameters;
import org.bouncycastle.crypto.ec.CustomNamedCurves;
import org.bouncycastle.jce.spec.ECNamedCurveParameterSpec;

public class ECNamedCurveTable {
   public static ECNamedCurveParameterSpec getParameterSpec(String var0) {
      ASN1ObjectIdentifier var1;
      try {
         var1 = possibleOID(var0) ? new ASN1ObjectIdentifier(var0) : null;
      } catch (IllegalArgumentException var3) {
         var1 = null;
      }

      X9ECParameters var2;
      if (var1 != null) {
         var2 = CustomNamedCurves.getByOID(var1);
      } else {
         var2 = CustomNamedCurves.getByName(var0);
      }

      if (var2 == null) {
         if (var1 != null) {
            var2 = org.bouncycastle.asn1.x9.ECNamedCurveTable.getByOID(var1);
         } else {
            var2 = org.bouncycastle.asn1.x9.ECNamedCurveTable.getByName(var0);
         }
      }

      return var2 == null ? null : new ECNamedCurveParameterSpec(var0, var2.getCurve(), var2.getG(), var2.getN(), var2.getH(), var2.getSeed());
   }

   public static Enumeration getNames() {
      return org.bouncycastle.asn1.x9.ECNamedCurveTable.getNames();
   }

   private static boolean possibleOID(String var0) {
      if (var0.length() >= 3 && var0.charAt(1) == '.') {
         char var1 = var0.charAt(0);
         return var1 >= '0' && var1 <= '2';
      } else {
         return false;
      }
   }
}
