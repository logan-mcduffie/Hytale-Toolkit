package com.nimbusds.jose.crypto.utils;

import java.math.BigInteger;
import java.security.interfaces.ECPrivateKey;
import java.security.interfaces.ECPublicKey;
import java.security.spec.ECFieldFp;
import java.security.spec.ECParameterSpec;
import java.security.spec.ECPoint;
import java.security.spec.EllipticCurve;

public class ECChecks {
   public static boolean isPointOnCurve(ECPublicKey publicKey, ECPrivateKey privateKey) {
      return isPointOnCurve(publicKey, privateKey.getParams());
   }

   public static boolean isPointOnCurve(ECPublicKey publicKey, ECParameterSpec ecParameterSpec) {
      ECPoint point = publicKey.getW();
      return isPointOnCurve(point.getAffineX(), point.getAffineY(), ecParameterSpec);
   }

   public static boolean isPointOnCurve(BigInteger x, BigInteger y, ECParameterSpec ecParameterSpec) {
      EllipticCurve curve = ecParameterSpec.getCurve();
      BigInteger a = curve.getA();
      BigInteger b = curve.getB();
      BigInteger p = ((ECFieldFp)curve.getField()).getP();
      BigInteger leftSide = y.pow(2).mod(p);
      BigInteger rightSide = x.pow(3).add(a.multiply(x)).add(b).mod(p);
      return leftSide.equals(rightSide);
   }

   private ECChecks() {
   }
}
