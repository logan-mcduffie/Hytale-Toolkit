package com.google.crypto.tink.internal;

import java.math.BigInteger;
import java.security.GeneralSecurityException;
import java.security.spec.ECField;
import java.security.spec.ECFieldFp;
import java.security.spec.ECParameterSpec;
import java.security.spec.ECPoint;
import java.security.spec.EllipticCurve;

public final class EllipticCurvesUtil {
   public static final ECParameterSpec NIST_P256_PARAMS = getNistP256Params();
   public static final ECParameterSpec NIST_P384_PARAMS = getNistP384Params();
   public static final ECParameterSpec NIST_P521_PARAMS = getNistP521Params();
   private static final BigInteger TWO = BigInteger.valueOf(2L);
   private static final BigInteger THREE = BigInteger.valueOf(3L);
   private static final BigInteger FOUR = BigInteger.valueOf(4L);
   private static final BigInteger EIGHT = BigInteger.valueOf(8L);

   private static ECParameterSpec getNistP256Params() {
      return getNistCurveSpec(
         "115792089210356248762697446949407573530086143415290314195533631308867097853951",
         "115792089210356248762697446949407573529996955224135760342422259061068512044369",
         "5ac635d8aa3a93e7b3ebbd55769886bc651d06b0cc53b0f63bce3c3e27d2604b",
         "6b17d1f2e12c4247f8bce6e563a440f277037d812deb33a0f4a13945d898c296",
         "4fe342e2fe1a7f9b8ee7eb4a7c0f9e162bce33576b315ececbb6406837bf51f5"
      );
   }

   private static ECParameterSpec getNistP384Params() {
      return getNistCurveSpec(
         "39402006196394479212279040100143613805079739270465446667948293404245721771496870329047266088258938001861606973112319",
         "39402006196394479212279040100143613805079739270465446667946905279627659399113263569398956308152294913554433653942643",
         "b3312fa7e23ee7e4988e056be3f82d19181d9c6efe8141120314088f5013875ac656398d8a2ed19d2a85c8edd3ec2aef",
         "aa87ca22be8b05378eb1c71ef320ad746e1d3b628ba79b9859f741e082542a385502f25dbf55296c3a545e3872760ab7",
         "3617de4a96262c6f5d9e98bf9292dc29f8f41dbd289a147ce9da3113b5f0b8c00a60b1ce1d7e819d7a431d7c90ea0e5f"
      );
   }

   private static ECParameterSpec getNistP521Params() {
      return getNistCurveSpec(
         "6864797660130609714981900799081393217269435300143305409394463459185543183397656052122559640661454554977296311391480858037121987999716643812574028291115057151",
         "6864797660130609714981900799081393217269435300143305409394463459185543183397655394245057746333217197532963996371363321113864768612440380340372808892707005449",
         "051953eb9618e1c9a1f929a21a0b68540eea2da725b99b315f3b8b489918ef109e156193951ec7e937b1652c0bd3bb1bf073573df883d2c34f1ef451fd46b503f00",
         "c6858e06b70404e9cd9e3ecb662395b4429c648139053fb521f828af606b4d3dbaa14b5e77efe75928fe1dc127a2ffa8de3348b3c1856a429bf97e7e31c2e5bd66",
         "11839296a789a3bc0045c8a5fb42c7d1bd998f54449579b446817afbd17273e662c97ee72995ef42640c550b9013fad0761353c7086a272c24088be94769fd16650"
      );
   }

   public static void checkPointOnCurve(ECPoint point, EllipticCurve ec) throws GeneralSecurityException {
      BigInteger p = getModulus(ec);
      BigInteger x = point.getAffineX();
      BigInteger y = point.getAffineY();
      if (x != null && y != null) {
         if (x.signum() != -1 && x.compareTo(p) < 0) {
            if (y.signum() != -1 && y.compareTo(p) < 0) {
               BigInteger lhs = y.multiply(y).mod(p);
               BigInteger rhs = x.multiply(x).add(ec.getA()).multiply(x).add(ec.getB()).mod(p);
               if (!lhs.equals(rhs)) {
                  throw new GeneralSecurityException("Point is not on curve");
               }
            } else {
               throw new GeneralSecurityException("y is out of range");
            }
         } else {
            throw new GeneralSecurityException("x is out of range");
         }
      } else {
         throw new GeneralSecurityException("point is at infinity");
      }
   }

   public static boolean isNistEcParameterSpec(ECParameterSpec spec) {
      return isSameEcParameterSpec(spec, NIST_P256_PARAMS) || isSameEcParameterSpec(spec, NIST_P384_PARAMS) || isSameEcParameterSpec(spec, NIST_P521_PARAMS);
   }

   public static boolean isSameEcParameterSpec(ECParameterSpec one, ECParameterSpec two) {
      return one.getCurve().equals(two.getCurve())
         && one.getGenerator().equals(two.getGenerator())
         && one.getOrder().equals(two.getOrder())
         && one.getCofactor() == two.getCofactor();
   }

   public static BigInteger getModulus(EllipticCurve curve) throws GeneralSecurityException {
      ECField field = curve.getField();
      if (field instanceof ECFieldFp) {
         return ((ECFieldFp)field).getP();
      } else {
         throw new GeneralSecurityException("Only curves over prime order fields are supported");
      }
   }

   private static ECParameterSpec getNistCurveSpec(String decimalP, String decimalN, String hexB, String hexGX, String hexGY) {
      BigInteger p = new BigInteger(decimalP);
      BigInteger n = new BigInteger(decimalN);
      BigInteger three = new BigInteger("3");
      BigInteger a = p.subtract(three);
      BigInteger b = new BigInteger(hexB, 16);
      BigInteger gx = new BigInteger(hexGX, 16);
      BigInteger gy = new BigInteger(hexGY, 16);
      int h = 1;
      ECFieldFp fp = new ECFieldFp(p);
      EllipticCurve curveSpec = new EllipticCurve(fp, a, b);
      ECPoint g = new ECPoint(gx, gy);
      return new ECParameterSpec(curveSpec, g, n, 1);
   }

   public static ECPoint multiplyByGenerator(BigInteger x, ECParameterSpec spec) throws GeneralSecurityException {
      if (!isNistEcParameterSpec(spec)) {
         throw new GeneralSecurityException("spec must be NIST P256, P384 or P521");
      } else if (x.signum() != 1) {
         throw new GeneralSecurityException("k must be positive");
      } else if (x.compareTo(spec.getOrder()) >= 0) {
         throw new GeneralSecurityException("k must be smaller than the order of the generator");
      } else {
         EllipticCurve curve = spec.getCurve();
         ECPoint generator = spec.getGenerator();
         checkPointOnCurve(generator, curve);
         BigInteger a = spec.getCurve().getA();
         BigInteger modulus = getModulus(curve);
         EllipticCurvesUtil.JacobianEcPoint r0 = toJacobianEcPoint(ECPoint.POINT_INFINITY, modulus);
         EllipticCurvesUtil.JacobianEcPoint r1 = toJacobianEcPoint(generator, modulus);

         for (int i = x.bitLength(); i >= 0; i--) {
            if (x.testBit(i)) {
               r0 = addJacobianPoints(r0, r1, a, modulus);
               r1 = doubleJacobianPoint(r1, a, modulus);
            } else {
               r1 = addJacobianPoints(r0, r1, a, modulus);
               r0 = doubleJacobianPoint(r0, a, modulus);
            }
         }

         ECPoint output = r0.toECPoint(modulus);
         checkPointOnCurve(output, curve);
         return output;
      }
   }

   static EllipticCurvesUtil.JacobianEcPoint toJacobianEcPoint(ECPoint p, BigInteger modulus) {
      if (p.equals(ECPoint.POINT_INFINITY)) {
         return EllipticCurvesUtil.JacobianEcPoint.INFINITY;
      } else {
         BigInteger z = new BigInteger(1, com.google.crypto.tink.subtle.Random.randBytes((modulus.bitLength() + 8) / 8)).mod(modulus);
         BigInteger zz = z.multiply(z).mod(modulus);
         BigInteger zzz = zz.multiply(z).mod(modulus);
         return new EllipticCurvesUtil.JacobianEcPoint(p.getAffineX().multiply(zz).mod(modulus), p.getAffineY().multiply(zzz).mod(modulus), z);
      }
   }

   static EllipticCurvesUtil.JacobianEcPoint doubleJacobianPoint(EllipticCurvesUtil.JacobianEcPoint p, BigInteger a, BigInteger modulus) {
      if (p.y.equals(BigInteger.ZERO)) {
         return EllipticCurvesUtil.JacobianEcPoint.INFINITY;
      } else {
         BigInteger xx = p.x.multiply(p.x).mod(modulus);
         BigInteger yy = p.y.multiply(p.y).mod(modulus);
         BigInteger yyyy = yy.multiply(yy).mod(modulus);
         BigInteger zz = p.z.multiply(p.z).mod(modulus);
         BigInteger x1yy = p.x.add(yy);
         BigInteger s = x1yy.multiply(x1yy).mod(modulus).subtract(xx).subtract(yyyy).multiply(TWO);
         BigInteger m = xx.multiply(THREE).add(a.multiply(zz).multiply(zz).mod(modulus));
         BigInteger t = m.multiply(m).mod(modulus).subtract(s.multiply(TWO)).mod(modulus);
         BigInteger y3 = m.multiply(s.subtract(t)).mod(modulus).subtract(yyyy.multiply(EIGHT)).mod(modulus);
         BigInteger y1z1 = p.y.add(p.z);
         BigInteger z3 = y1z1.multiply(y1z1).mod(modulus).subtract(yy).subtract(zz).mod(modulus);
         return new EllipticCurvesUtil.JacobianEcPoint(t, y3, z3);
      }
   }

   static EllipticCurvesUtil.JacobianEcPoint addJacobianPoints(
      EllipticCurvesUtil.JacobianEcPoint p1, EllipticCurvesUtil.JacobianEcPoint p2, BigInteger a, BigInteger modulus
   ) {
      if (p1.isInfinity()) {
         return p2;
      } else if (p2.isInfinity()) {
         return p1;
      } else {
         BigInteger z1z1 = p1.z.multiply(p1.z).mod(modulus);
         BigInteger z2z2 = p2.z.multiply(p2.z).mod(modulus);
         BigInteger u1 = p1.x.multiply(z2z2).mod(modulus);
         BigInteger u2 = p2.x.multiply(z1z1).mod(modulus);
         BigInteger s1 = p1.y.multiply(p2.z).mod(modulus).multiply(z2z2).mod(modulus);
         BigInteger s2 = p2.y.multiply(p1.z).mod(modulus).multiply(z1z1).mod(modulus);
         if (u1.equals(u2)) {
            return !s1.equals(s2) ? EllipticCurvesUtil.JacobianEcPoint.INFINITY : doubleJacobianPoint(p1, a, modulus);
         } else {
            BigInteger h = u2.subtract(u1).mod(modulus);
            BigInteger i = h.multiply(FOUR).multiply(h).mod(modulus);
            BigInteger j = h.multiply(i).mod(modulus);
            BigInteger r = s2.subtract(s1).multiply(TWO).mod(modulus);
            BigInteger v = u1.multiply(i).mod(modulus);
            BigInteger x3 = r.multiply(r).mod(modulus).subtract(j).subtract(v.multiply(TWO)).mod(modulus);
            BigInteger y3 = r.multiply(v.subtract(x3)).subtract(s1.multiply(TWO).multiply(j)).mod(modulus);
            BigInteger z12 = p1.z.add(p2.z);
            BigInteger z3 = z12.multiply(z12).mod(modulus).subtract(z1z1).subtract(z2z2).multiply(h).mod(modulus);
            return new EllipticCurvesUtil.JacobianEcPoint(x3, y3, z3);
         }
      }
   }

   private EllipticCurvesUtil() {
   }

   static class JacobianEcPoint {
      BigInteger x;
      BigInteger y;
      BigInteger z;
      static final EllipticCurvesUtil.JacobianEcPoint INFINITY = new EllipticCurvesUtil.JacobianEcPoint(BigInteger.ONE, BigInteger.ONE, BigInteger.ZERO);

      JacobianEcPoint(BigInteger x, BigInteger y, BigInteger z) {
         this.x = x;
         this.y = y;
         this.z = z;
      }

      boolean isInfinity() {
         return this.z.equals(BigInteger.ZERO);
      }

      ECPoint toECPoint(BigInteger modulus) {
         if (this.isInfinity()) {
            return ECPoint.POINT_INFINITY;
         } else {
            BigInteger zInv = this.z.modInverse(modulus);
            BigInteger zInv2 = zInv.multiply(zInv).mod(modulus);
            return new ECPoint(this.x.multiply(zInv2).mod(modulus), this.y.multiply(zInv2).mod(modulus).multiply(zInv).mod(modulus));
         }
      }
   }
}
