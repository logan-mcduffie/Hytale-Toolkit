package org.bouncycastle.its.jcajce;

import java.math.BigInteger;
import java.security.spec.ECField;
import java.security.spec.ECFieldF2m;
import java.security.spec.ECFieldFp;
import java.security.spec.ECParameterSpec;
import java.security.spec.ECPoint;
import java.security.spec.EllipticCurve;
import org.bouncycastle.asn1.x9.X9ECParameters;
import org.bouncycastle.math.ec.ECAlgorithms;
import org.bouncycastle.math.ec.ECCurve;
import org.bouncycastle.math.field.FiniteField;
import org.bouncycastle.math.field.Polynomial;
import org.bouncycastle.math.field.PolynomialExtensionField;
import org.bouncycastle.util.Arrays;

class ECUtil {
   static ECPoint convertPoint(org.bouncycastle.math.ec.ECPoint var0) {
      var0 = var0.normalize();
      return new ECPoint(var0.getAffineXCoord().toBigInteger(), var0.getAffineYCoord().toBigInteger());
   }

   public static EllipticCurve convertCurve(ECCurve var0, byte[] var1) {
      ECField var2 = convertField(var0.getField());
      BigInteger var3 = var0.getA().toBigInteger();
      BigInteger var4 = var0.getB().toBigInteger();
      return new EllipticCurve(var2, var3, var4, null);
   }

   public static ECParameterSpec convertToSpec(X9ECParameters var0) {
      return new ECParameterSpec(convertCurve(var0.getCurve(), null), convertPoint(var0.getG()), var0.getN(), var0.getH().intValue());
   }

   public static ECField convertField(FiniteField var0) {
      if (ECAlgorithms.isFpField(var0)) {
         return new ECFieldFp(var0.getCharacteristic());
      } else {
         Polynomial var1 = ((PolynomialExtensionField)var0).getMinimalPolynomial();
         int[] var2 = var1.getExponentsPresent();
         int[] var3 = Arrays.reverseInPlace(Arrays.copyOfRange(var2, 1, var2.length - 1));
         return new ECFieldF2m(var1.getDegree(), var3);
      }
   }
}
