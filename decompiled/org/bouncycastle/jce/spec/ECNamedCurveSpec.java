package org.bouncycastle.jce.spec;

import java.math.BigInteger;
import java.security.spec.ECField;
import java.security.spec.ECFieldF2m;
import java.security.spec.ECFieldFp;
import java.security.spec.EllipticCurve;
import org.bouncycastle.jcajce.provider.asymmetric.util.EC5Util;
import org.bouncycastle.math.ec.ECAlgorithms;
import org.bouncycastle.math.ec.ECCurve;
import org.bouncycastle.math.ec.ECPoint;
import org.bouncycastle.math.field.FiniteField;
import org.bouncycastle.math.field.Polynomial;
import org.bouncycastle.math.field.PolynomialExtensionField;
import org.bouncycastle.util.Arrays;

public class ECNamedCurveSpec extends java.security.spec.ECParameterSpec {
   private String name;

   private static EllipticCurve convertCurve(ECCurve var0, byte[] var1) {
      ECField var2 = convertField(var0.getField());
      BigInteger var3 = var0.getA().toBigInteger();
      BigInteger var4 = var0.getB().toBigInteger();
      return new EllipticCurve(var2, var3, var4, var1);
   }

   private static ECField convertField(FiniteField var0) {
      if (ECAlgorithms.isFpField(var0)) {
         return new ECFieldFp(var0.getCharacteristic());
      } else {
         Polynomial var1 = ((PolynomialExtensionField)var0).getMinimalPolynomial();
         int[] var2 = var1.getExponentsPresent();
         int[] var3 = Arrays.reverseInPlace(Arrays.copyOfRange(var2, 1, var2.length - 1));
         return new ECFieldF2m(var1.getDegree(), var3);
      }
   }

   public ECNamedCurveSpec(String var1, ECCurve var2, ECPoint var3, BigInteger var4) {
      super(convertCurve(var2, null), EC5Util.convertPoint(var3), var4, 1);
      this.name = var1;
   }

   public ECNamedCurveSpec(String var1, EllipticCurve var2, java.security.spec.ECPoint var3, BigInteger var4) {
      super(var2, var3, var4, 1);
      this.name = var1;
   }

   public ECNamedCurveSpec(String var1, ECCurve var2, ECPoint var3, BigInteger var4, BigInteger var5) {
      super(convertCurve(var2, null), EC5Util.convertPoint(var3), var4, var5.intValue());
      this.name = var1;
   }

   public ECNamedCurveSpec(String var1, EllipticCurve var2, java.security.spec.ECPoint var3, BigInteger var4, BigInteger var5) {
      super(var2, var3, var4, var5.intValue());
      this.name = var1;
   }

   public ECNamedCurveSpec(String var1, ECCurve var2, ECPoint var3, BigInteger var4, BigInteger var5, byte[] var6) {
      super(convertCurve(var2, var6), EC5Util.convertPoint(var3), var4, var5.intValue());
      this.name = var1;
   }

   public String getName() {
      return this.name;
   }
}
