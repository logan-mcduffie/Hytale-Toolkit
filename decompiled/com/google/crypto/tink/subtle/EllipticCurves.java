package com.google.crypto.tink.subtle;

import com.google.crypto.tink.internal.BigIntegerEncoding;
import com.google.crypto.tink.internal.EllipticCurvesUtil;
import java.math.BigInteger;
import java.security.GeneralSecurityException;
import java.security.InvalidAlgorithmParameterException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.interfaces.ECPrivateKey;
import java.security.interfaces.ECPublicKey;
import java.security.spec.ECParameterSpec;
import java.security.spec.ECPoint;
import java.security.spec.ECPrivateKeySpec;
import java.security.spec.ECPublicKeySpec;
import java.security.spec.EllipticCurve;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;
import javax.crypto.KeyAgreement;

public final class EllipticCurves {
   public static ECParameterSpec getNistP256Params() {
      return EllipticCurvesUtil.NIST_P256_PARAMS;
   }

   public static ECParameterSpec getNistP384Params() {
      return EllipticCurvesUtil.NIST_P384_PARAMS;
   }

   public static ECParameterSpec getNistP521Params() {
      return EllipticCurvesUtil.NIST_P521_PARAMS;
   }

   static void checkPublicKey(ECPublicKey key) throws GeneralSecurityException {
      EllipticCurvesUtil.checkPointOnCurve(key.getW(), key.getParams().getCurve());
   }

   public static boolean isNistEcParameterSpec(ECParameterSpec spec) {
      return EllipticCurvesUtil.isNistEcParameterSpec(spec);
   }

   public static boolean isSameEcParameterSpec(ECParameterSpec one, ECParameterSpec two) {
      return EllipticCurvesUtil.isSameEcParameterSpec(one, two);
   }

   public static void validatePublicKey(ECPublicKey publicKey, ECPrivateKey privateKey) throws GeneralSecurityException {
      validatePublicKeySpec(publicKey, privateKey);
      EllipticCurvesUtil.checkPointOnCurve(publicKey.getW(), privateKey.getParams().getCurve());
   }

   static void validatePublicKeySpec(ECPublicKey publicKey, ECPrivateKey privateKey) throws GeneralSecurityException {
      try {
         ECParameterSpec publicKeySpec = publicKey.getParams();
         ECParameterSpec privateKeySpec = privateKey.getParams();
         if (!isSameEcParameterSpec(publicKeySpec, privateKeySpec)) {
            throw new GeneralSecurityException("invalid public key spec");
         }
      } catch (NullPointerException | IllegalArgumentException var4) {
         throw new GeneralSecurityException(var4);
      }
   }

   public static BigInteger getModulus(EllipticCurve curve) throws GeneralSecurityException {
      return EllipticCurvesUtil.getModulus(curve);
   }

   public static int fieldSizeInBits(EllipticCurve curve) throws GeneralSecurityException {
      return getModulus(curve).subtract(BigInteger.ONE).bitLength();
   }

   public static int fieldSizeInBytes(EllipticCurve curve) throws GeneralSecurityException {
      return (fieldSizeInBits(curve) + 7) / 8;
   }

   private static BigInteger modSqrt(BigInteger x, BigInteger p) throws GeneralSecurityException {
      if (p.signum() != 1) {
         throw new InvalidAlgorithmParameterException("p must be positive");
      } else {
         x = x.mod(p);
         BigInteger squareRoot = null;
         if (x.equals(BigInteger.ZERO)) {
            return BigInteger.ZERO;
         } else {
            if (p.testBit(0) && p.testBit(1)) {
               BigInteger q = p.add(BigInteger.ONE).shiftRight(2);
               squareRoot = x.modPow(q, p);
            } else if (p.testBit(0) && !p.testBit(1)) {
               BigInteger a = BigInteger.ONE;
               BigInteger d = null;
               BigInteger q1 = p.subtract(BigInteger.ONE).shiftRight(1);
               int tries = 0;

               while (true) {
                  d = a.multiply(a).subtract(x).mod(p);
                  if (d.equals(BigInteger.ZERO)) {
                     return a;
                  }

                  BigInteger t = d.modPow(q1, p);
                  if (t.add(BigInteger.ONE).equals(p)) {
                     t = p.add(BigInteger.ONE).shiftRight(1);
                     BigInteger u = a;
                     BigInteger v = BigInteger.ONE;

                     for (int bit = t.bitLength() - 2; bit >= 0; bit--) {
                        BigInteger tmp = u.multiply(v);
                        u = u.multiply(u).add(v.multiply(v).mod(p).multiply(d)).mod(p);
                        v = tmp.add(tmp).mod(p);
                        if (t.testBit(bit)) {
                           tmp = u.multiply(a).add(v.multiply(d)).mod(p);
                           v = a.multiply(v).add(u).mod(p);
                           u = tmp;
                        }
                     }

                     squareRoot = u;
                     break;
                  }

                  if (!t.equals(BigInteger.ONE)) {
                     throw new InvalidAlgorithmParameterException("p is not prime");
                  }

                  a = a.add(BigInteger.ONE);
                  if (++tries == 128 && !p.isProbablePrime(80)) {
                     throw new InvalidAlgorithmParameterException("p is not prime");
                  }
               }
            }

            if (squareRoot != null && squareRoot.multiply(squareRoot).mod(p).compareTo(x) != 0) {
               throw new GeneralSecurityException("Could not find a modular square root");
            } else {
               return squareRoot;
            }
         }
      }
   }

   private static BigInteger computeY(BigInteger x, boolean lsb, EllipticCurve curve) throws GeneralSecurityException {
      BigInteger p = getModulus(curve);
      BigInteger a = curve.getA();
      BigInteger b = curve.getB();
      BigInteger rhs = x.multiply(x).add(a).multiply(x).add(b).mod(p);
      BigInteger y = modSqrt(rhs, p);
      if (lsb != y.testBit(0)) {
         y = p.subtract(y).mod(p);
      }

      return y;
   }

   @Deprecated
   public static BigInteger getY(BigInteger x, boolean lsb, EllipticCurve curve) throws GeneralSecurityException {
      return computeY(x, lsb, curve);
   }

   private static byte[] toMinimalSignedNumber(byte[] bs) {
      int start = 0;

      while (start < bs.length && bs[start] == 0) {
         start++;
      }

      if (start == bs.length) {
         start = bs.length - 1;
      }

      int extraZero = 0;
      if ((bs[start] & 128) == 128) {
         extraZero = 1;
      }

      byte[] res = new byte[bs.length - start + extraZero];
      System.arraycopy(bs, start, res, extraZero, bs.length - start);
      return res;
   }

   public static byte[] ecdsaIeee2Der(byte[] ieee) throws GeneralSecurityException {
      if (ieee.length % 2 == 0 && ieee.length != 0 && ieee.length <= 132) {
         byte[] r = toMinimalSignedNumber(Arrays.copyOf(ieee, ieee.length / 2));
         byte[] s = toMinimalSignedNumber(Arrays.copyOfRange(ieee, ieee.length / 2, ieee.length));
         int offset = 0;
         int length = 2 + r.length + 1 + 1 + s.length;
         byte[] der;
         if (length >= 128) {
            der = new byte[length + 3];
            der[offset++] = 48;
            der[offset++] = -127;
            der[offset++] = (byte)length;
         } else {
            der = new byte[length + 2];
            der[offset++] = 48;
            der[offset++] = (byte)length;
         }

         der[offset++] = 2;
         der[offset++] = (byte)r.length;
         System.arraycopy(r, 0, der, offset, r.length);
         offset += r.length;
         der[offset++] = 2;
         der[offset++] = (byte)s.length;
         System.arraycopy(s, 0, der, offset, s.length);
         return der;
      } else {
         throw new GeneralSecurityException("Invalid IEEE_P1363 encoding");
      }
   }

   public static byte[] ecdsaDer2Ieee(byte[] der, int ieeeLength) throws GeneralSecurityException {
      if (!isValidDerEncoding(der)) {
         throw new GeneralSecurityException("Invalid DER encoding");
      } else {
         byte[] ieee = new byte[ieeeLength];
         int length = der[1] & 255;
         int offset = 2;
         if (length >= 128) {
            offset++;
         }

         int var10001 = ++offset;
         offset++;
         int rLength = der[var10001];
         int extraZero = 0;
         if (der[offset] == 0) {
            extraZero = 1;
         }

         System.arraycopy(der, offset + extraZero, ieee, ieeeLength / 2 - rLength + extraZero, rLength - extraZero);
         offset += rLength + 1;
         int sLength = der[offset++];
         int var12 = 0;
         if (der[offset] == 0) {
            var12 = 1;
         }

         System.arraycopy(der, offset + var12, ieee, ieeeLength - sLength + var12, sLength - var12);
         return ieee;
      }
   }

   public static boolean isValidDerEncoding(final byte[] sig) {
      if (sig.length < 8) {
         return false;
      } else if (sig[0] != 48) {
         return false;
      } else {
         int totalLen = sig[1] & 255;
         int totalLenLen = 1;
         if (totalLen == 129) {
            totalLenLen = 2;
            totalLen = sig[2] & 255;
            if (totalLen < 128) {
               return false;
            }
         } else if (totalLen == 128 || totalLen > 129) {
            return false;
         }

         if (totalLen != sig.length - 1 - totalLenLen) {
            return false;
         } else if (sig[1 + totalLenLen] != 2) {
            return false;
         } else {
            int rLen = sig[1 + totalLenLen + 1] & 255;
            if (1 + totalLenLen + 1 + 1 + rLen + 1 >= sig.length) {
               return false;
            } else if (rLen == 0) {
               return false;
            } else if ((sig[3 + totalLenLen] & 255) >= 128) {
               return false;
            } else if (rLen > 1 && sig[3 + totalLenLen] == 0 && (sig[4 + totalLenLen] & 255) < 128) {
               return false;
            } else if (sig[3 + totalLenLen + rLen] != 2) {
               return false;
            } else {
               int sLen = sig[1 + totalLenLen + 1 + 1 + rLen + 1] & 255;
               if (1 + totalLenLen + 1 + 1 + rLen + 1 + 1 + sLen != sig.length) {
                  return false;
               } else if (sLen == 0) {
                  return false;
               } else {
                  return (sig[5 + totalLenLen + rLen] & 255) >= 128
                     ? false
                     : sLen <= 1 || sig[5 + totalLenLen + rLen] != 0 || (sig[6 + totalLenLen + rLen] & 255) >= 128;
               }
            }
         }
      }
   }

   public static int encodingSizeInBytes(EllipticCurve curve, EllipticCurves.PointFormatType format) throws GeneralSecurityException {
      int coordinateSize = fieldSizeInBytes(curve);
      switch (format) {
         case UNCOMPRESSED:
            return 2 * coordinateSize + 1;
         case COMPRESSED:
            return coordinateSize + 1;
         case DO_NOT_USE_CRUNCHY_UNCOMPRESSED:
            return 2 * coordinateSize;
         default:
            throw new GeneralSecurityException("unknown EC point format");
      }
   }

   public static ECPoint ecPointDecode(EllipticCurve curve, EllipticCurves.PointFormatType format, byte[] encoded) throws GeneralSecurityException {
      return pointDecode(curve, format, encoded);
   }

   public static ECPoint pointDecode(EllipticCurves.CurveType curveType, EllipticCurves.PointFormatType format, byte[] encoded) throws GeneralSecurityException {
      return pointDecode(getCurveSpec(curveType).getCurve(), format, encoded);
   }

   public static ECPoint pointDecode(EllipticCurve curve, EllipticCurves.PointFormatType format, byte[] encoded) throws GeneralSecurityException {
      int coordinateSize = fieldSizeInBytes(curve);
      switch (format) {
         case UNCOMPRESSED:
            if (encoded.length != 2 * coordinateSize + 1) {
               throw new GeneralSecurityException("invalid point size");
            } else {
               if (encoded[0] != 4) {
                  throw new GeneralSecurityException("invalid point format");
               }

               BigInteger x = new BigInteger(1, Arrays.copyOfRange(encoded, 1, coordinateSize + 1));
               BigInteger y = new BigInteger(1, Arrays.copyOfRange(encoded, coordinateSize + 1, encoded.length));
               ECPoint point = new ECPoint(x, y);
               EllipticCurvesUtil.checkPointOnCurve(point, curve);
               return point;
            }
         case COMPRESSED:
            BigInteger p = getModulus(curve);
            if (encoded.length != coordinateSize + 1) {
               throw new GeneralSecurityException("compressed point has wrong length");
            } else {
               boolean lsb;
               if (encoded[0] == 2) {
                  lsb = false;
               } else {
                  if (encoded[0] != 3) {
                     throw new GeneralSecurityException("invalid format");
                  }

                  lsb = true;
               }

               BigInteger x = new BigInteger(1, Arrays.copyOfRange(encoded, 1, encoded.length));
               if (x.signum() != -1 && x.compareTo(p) < 0) {
                  BigInteger y = computeY(x, lsb, curve);
                  return new ECPoint(x, y);
               }

               throw new GeneralSecurityException("x is out of range");
            }
         case DO_NOT_USE_CRUNCHY_UNCOMPRESSED:
            if (encoded.length != 2 * coordinateSize) {
               throw new GeneralSecurityException("invalid point size");
            }

            BigInteger x = new BigInteger(1, Arrays.copyOf(encoded, coordinateSize));
            BigInteger y = new BigInteger(1, Arrays.copyOfRange(encoded, coordinateSize, encoded.length));
            ECPoint point = new ECPoint(x, y);
            EllipticCurvesUtil.checkPointOnCurve(point, curve);
            return point;
         default:
            throw new GeneralSecurityException("invalid format:" + format);
      }
   }

   public static byte[] pointEncode(EllipticCurves.CurveType curveType, EllipticCurves.PointFormatType format, ECPoint point) throws GeneralSecurityException {
      return pointEncode(getCurveSpec(curveType).getCurve(), format, point);
   }

   public static byte[] pointEncode(EllipticCurve curve, EllipticCurves.PointFormatType format, ECPoint point) throws GeneralSecurityException {
      EllipticCurvesUtil.checkPointOnCurve(point, curve);
      int coordinateSize = fieldSizeInBytes(curve);
      switch (format) {
         case UNCOMPRESSED: {
            byte[] encoded = new byte[2 * coordinateSize + 1];
            byte[] x = BigIntegerEncoding.toBigEndianBytes(point.getAffineX());
            byte[] y = BigIntegerEncoding.toBigEndianBytes(point.getAffineY());
            System.arraycopy(y, 0, encoded, 1 + 2 * coordinateSize - y.length, y.length);
            System.arraycopy(x, 0, encoded, 1 + coordinateSize - x.length, x.length);
            encoded[0] = 4;
            return encoded;
         }
         case COMPRESSED: {
            byte[] encoded = new byte[coordinateSize + 1];
            byte[] x = BigIntegerEncoding.toBigEndianBytes(point.getAffineX());
            System.arraycopy(x, 0, encoded, 1 + coordinateSize - x.length, x.length);
            encoded[0] = (byte)(point.getAffineY().testBit(0) ? 3 : 2);
            return encoded;
         }
         case DO_NOT_USE_CRUNCHY_UNCOMPRESSED: {
            byte[] encoded = new byte[2 * coordinateSize];
            byte[] x = BigIntegerEncoding.toBigEndianBytes(point.getAffineX());
            if (x.length > coordinateSize) {
               x = Arrays.copyOfRange(x, x.length - coordinateSize, x.length);
            }

            byte[] y = BigIntegerEncoding.toBigEndianBytes(point.getAffineY());
            if (y.length > coordinateSize) {
               y = Arrays.copyOfRange(y, y.length - coordinateSize, y.length);
            }

            System.arraycopy(y, 0, encoded, 2 * coordinateSize - y.length, y.length);
            System.arraycopy(x, 0, encoded, coordinateSize - x.length, x.length);
            return encoded;
         }
         default:
            throw new GeneralSecurityException("invalid format:" + format);
      }
   }

   public static ECParameterSpec getCurveSpec(EllipticCurves.CurveType curve) throws NoSuchAlgorithmException {
      switch (curve) {
         case NIST_P256:
            return getNistP256Params();
         case NIST_P384:
            return getNistP384Params();
         case NIST_P521:
            return getNistP521Params();
         default:
            throw new NoSuchAlgorithmException("curve not implemented:" + curve);
      }
   }

   public static ECPublicKey getEcPublicKey(final byte[] x509PublicKey) throws GeneralSecurityException {
      KeyFactory kf = EngineFactory.KEY_FACTORY.getInstance("EC");
      return (ECPublicKey)kf.generatePublic(new X509EncodedKeySpec(x509PublicKey));
   }

   public static ECPublicKey getEcPublicKey(EllipticCurves.CurveType curve, EllipticCurves.PointFormatType pointFormat, final byte[] publicKey) throws GeneralSecurityException {
      return getEcPublicKey(getCurveSpec(curve), pointFormat, publicKey);
   }

   public static ECPublicKey getEcPublicKey(ECParameterSpec spec, EllipticCurves.PointFormatType pointFormat, final byte[] publicKey) throws GeneralSecurityException {
      ECPoint point = pointDecode(spec.getCurve(), pointFormat, publicKey);
      ECPublicKeySpec pubSpec = new ECPublicKeySpec(point, spec);
      KeyFactory kf = EngineFactory.KEY_FACTORY.getInstance("EC");
      return (ECPublicKey)kf.generatePublic(pubSpec);
   }

   public static ECPublicKey getEcPublicKey(EllipticCurves.CurveType curve, final byte[] x, final byte[] y) throws GeneralSecurityException {
      ECParameterSpec ecParams = getCurveSpec(curve);
      BigInteger pubX = new BigInteger(1, x);
      BigInteger pubY = new BigInteger(1, y);
      ECPoint w = new ECPoint(pubX, pubY);
      EllipticCurvesUtil.checkPointOnCurve(w, ecParams.getCurve());
      ECPublicKeySpec spec = new ECPublicKeySpec(w, ecParams);
      KeyFactory kf = EngineFactory.KEY_FACTORY.getInstance("EC");
      return (ECPublicKey)kf.generatePublic(spec);
   }

   public static ECPrivateKey getEcPrivateKey(final byte[] pkcs8PrivateKey) throws GeneralSecurityException {
      KeyFactory kf = EngineFactory.KEY_FACTORY.getInstance("EC");
      return (ECPrivateKey)kf.generatePrivate(new PKCS8EncodedKeySpec(pkcs8PrivateKey));
   }

   public static ECPrivateKey getEcPrivateKey(EllipticCurves.CurveType curve, final byte[] keyValue) throws GeneralSecurityException {
      ECParameterSpec ecParams = getCurveSpec(curve);
      BigInteger privValue = BigIntegerEncoding.fromUnsignedBigEndianBytes(keyValue);
      ECPrivateKeySpec spec = new ECPrivateKeySpec(privValue, ecParams);
      KeyFactory kf = EngineFactory.KEY_FACTORY.getInstance("EC");
      return (ECPrivateKey)kf.generatePrivate(spec);
   }

   public static KeyPair generateKeyPair(EllipticCurves.CurveType curve) throws GeneralSecurityException {
      return generateKeyPair(getCurveSpec(curve));
   }

   public static KeyPair generateKeyPair(ECParameterSpec spec) throws GeneralSecurityException {
      KeyPairGenerator keyGen = EngineFactory.KEY_PAIR_GENERATOR.getInstance("EC");
      keyGen.initialize(spec);
      return keyGen.generateKeyPair();
   }

   static void validateSharedSecret(byte[] secret, ECPrivateKey privateKey) throws GeneralSecurityException {
      EllipticCurve privateKeyCurve = privateKey.getParams().getCurve();
      BigInteger x = new BigInteger(1, secret);
      if (x.signum() != -1 && x.compareTo(getModulus(privateKeyCurve)) < 0) {
         Object unused = computeY(x, true, privateKeyCurve);
      } else {
         throw new GeneralSecurityException("shared secret is out of range");
      }
   }

   public static byte[] computeSharedSecret(ECPrivateKey myPrivateKey, ECPublicKey peerPublicKey) throws GeneralSecurityException {
      validatePublicKeySpec(peerPublicKey, myPrivateKey);
      return computeSharedSecret(myPrivateKey, peerPublicKey.getW());
   }

   public static byte[] computeSharedSecret(ECPrivateKey myPrivateKey, ECPoint publicPoint) throws GeneralSecurityException {
      EllipticCurvesUtil.checkPointOnCurve(publicPoint, myPrivateKey.getParams().getCurve());
      ECParameterSpec privSpec = myPrivateKey.getParams();
      ECPublicKeySpec publicKeySpec = new ECPublicKeySpec(publicPoint, privSpec);
      KeyFactory kf = EngineFactory.KEY_FACTORY.getInstance("EC");
      PublicKey publicKey = kf.generatePublic(publicKeySpec);
      KeyAgreement ka = EngineFactory.KEY_AGREEMENT.getInstance("ECDH");
      ka.init(myPrivateKey);

      try {
         ka.doPhase(publicKey, true);
         byte[] secret = ka.generateSecret();
         validateSharedSecret(secret, myPrivateKey);
         return secret;
      } catch (IllegalStateException var8) {
         throw new GeneralSecurityException(var8);
      }
   }

   private EllipticCurves() {
   }

   public static enum CurveType {
      NIST_P256,
      NIST_P384,
      NIST_P521;
   }

   public static enum EcdsaEncoding {
      IEEE_P1363,
      DER;
   }

   public static enum PointFormatType {
      UNCOMPRESSED,
      COMPRESSED,
      DO_NOT_USE_CRUNCHY_UNCOMPRESSED;
   }
}
