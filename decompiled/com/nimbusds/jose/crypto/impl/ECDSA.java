package com.nimbusds.jose.crypto.impl;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.jwk.Curve;
import com.nimbusds.jose.jwk.ECParameterTable;
import com.nimbusds.jose.util.ByteUtils;
import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.security.Provider;
import java.security.Signature;
import java.security.interfaces.ECKey;
import java.security.spec.ECParameterSpec;
import java.util.Set;

public class ECDSA {
   public static JWSAlgorithm resolveAlgorithm(ECKey ecKey) throws JOSEException {
      ECParameterSpec ecParameterSpec = ecKey.getParams();
      return resolveAlgorithm(Curve.forECParameterSpec(ecParameterSpec));
   }

   public static JWSAlgorithm resolveAlgorithm(Curve curve) throws JOSEException {
      if (curve == null) {
         throw new JOSEException("The EC key curve is not supported, must be P-256, P-384 or P-521");
      } else if (Curve.P_256.equals(curve)) {
         return JWSAlgorithm.ES256;
      } else if (Curve.SECP256K1.equals(curve)) {
         return JWSAlgorithm.ES256K;
      } else if (Curve.P_384.equals(curve)) {
         return JWSAlgorithm.ES384;
      } else if (Curve.P_521.equals(curve)) {
         return JWSAlgorithm.ES512;
      } else {
         throw new JOSEException("Unexpected curve: " + curve);
      }
   }

   public static Signature getSignerAndVerifier(JWSAlgorithm alg, Provider jcaProvider) throws JOSEException {
      String jcaAlg;
      if (alg.equals(JWSAlgorithm.ES256)) {
         jcaAlg = "SHA256withECDSA";
      } else if (alg.equals(JWSAlgorithm.ES256K)) {
         jcaAlg = "SHA256withECDSA";
      } else if (alg.equals(JWSAlgorithm.ES384)) {
         jcaAlg = "SHA384withECDSA";
      } else {
         if (!alg.equals(JWSAlgorithm.ES512)) {
            throw new JOSEException(AlgorithmSupportMessage.unsupportedJWSAlgorithm(alg, ECDSAProvider.SUPPORTED_ALGORITHMS));
         }

         jcaAlg = "SHA512withECDSA";
      }

      try {
         return jcaProvider != null ? Signature.getInstance(jcaAlg, jcaProvider) : Signature.getInstance(jcaAlg);
      } catch (NoSuchAlgorithmException var4) {
         throw new JOSEException("Unsupported ECDSA algorithm: " + var4.getMessage(), var4);
      }
   }

   public static int getSignatureByteArrayLength(JWSAlgorithm alg) throws JOSEException {
      if (alg.equals(JWSAlgorithm.ES256)) {
         return 64;
      } else if (alg.equals(JWSAlgorithm.ES256K)) {
         return 64;
      } else if (alg.equals(JWSAlgorithm.ES384)) {
         return 96;
      } else if (alg.equals(JWSAlgorithm.ES512)) {
         return 132;
      } else {
         throw new JOSEException(AlgorithmSupportMessage.unsupportedJWSAlgorithm(alg, ECDSAProvider.SUPPORTED_ALGORITHMS));
      }
   }

   public static byte[] transcodeSignatureToConcat(byte[] derSignature, int outputLength) throws JOSEException {
      if (derSignature.length >= 8 && derSignature[0] == 48) {
         int offset;
         if (derSignature[1] > 0) {
            offset = 2;
         } else {
            if (derSignature[1] != -127) {
               throw new JOSEException("Invalid ECDSA signature format");
            }

            offset = 3;
         }

         byte rLength = derSignature[offset + 1];
         int i = rLength;

         while (i > 0 && derSignature[offset + 2 + rLength - i] == 0) {
            i--;
         }

         byte sLength = derSignature[offset + 2 + rLength + 1];
         int j = sLength;

         while (j > 0 && derSignature[offset + 2 + rLength + 2 + sLength - j] == 0) {
            j--;
         }

         int rawLen = Math.max(i, j);
         rawLen = Math.max(rawLen, outputLength / 2);
         if ((derSignature[offset - 1] & 255) == derSignature.length - offset
            && (derSignature[offset - 1] & 255) == 2 + rLength + 2 + sLength
            && derSignature[offset] == 2
            && derSignature[offset + 2 + rLength] == 2) {
            byte[] concatSignature = new byte[2 * rawLen];
            System.arraycopy(derSignature, offset + 2 + rLength - i, concatSignature, rawLen - i, i);
            System.arraycopy(derSignature, offset + 2 + rLength + 2 + sLength - j, concatSignature, 2 * rawLen - j, j);
            return concatSignature;
         } else {
            throw new JOSEException("Invalid ECDSA signature format");
         }
      } else {
         throw new JOSEException("Invalid ECDSA signature format");
      }
   }

   public static byte[] transcodeSignatureToDER(byte[] jwsSignature) throws JOSEException {
      try {
         int rawLen = jwsSignature.length / 2;
         int i = rawLen;

         while (i > 0 && jwsSignature[rawLen - i] == 0) {
            i--;
         }

         int j = i;
         if (jwsSignature[rawLen - i] < 0) {
            j = i + 1;
         }

         int k = rawLen;

         while (k > 0 && jwsSignature[2 * rawLen - k] == 0) {
            k--;
         }

         int l = k;
         if (jwsSignature[2 * rawLen - k] < 0) {
            l = k + 1;
         }

         int len = 2 + j + 2 + l;
         if (len > 255) {
            throw new JOSEException("Invalid ECDSA signature format");
         } else {
            int offset;
            byte[] derSignature;
            if (len < 128) {
               derSignature = new byte[4 + j + 2 + l];
               offset = 1;
            } else {
               derSignature = new byte[5 + j + 2 + l];
               derSignature[1] = -127;
               offset = 2;
            }

            derSignature[0] = 48;
            derSignature[offset++] = (byte)len;
            derSignature[offset++] = 2;
            derSignature[offset++] = (byte)j;
            System.arraycopy(jwsSignature, rawLen - i, derSignature, offset + j - i, i);
            offset += j;
            derSignature[offset++] = 2;
            derSignature[offset++] = (byte)l;
            System.arraycopy(jwsSignature, 2 * rawLen - k, derSignature, offset + l - k, k);
            return derSignature;
         }
      } catch (Exception var9) {
         if (var9 instanceof JOSEException) {
            throw var9;
         } else {
            throw new JOSEException(var9.getMessage(), var9);
         }
      }
   }

   public static void ensureLegalSignature(byte[] jwsSignature, JWSAlgorithm jwsAlg) throws JOSEException {
      if (ByteUtils.isZeroFilled(jwsSignature)) {
         throw new JOSEException("Blank signature");
      } else {
         Set<Curve> matchingCurves = Curve.forJWSAlgorithm(jwsAlg);
         if (matchingCurves != null && matchingCurves.size() <= 1) {
            Curve curve = matchingCurves.iterator().next();
            ECParameterSpec ecParameterSpec = ECParameterTable.get(curve);
            if (ecParameterSpec == null) {
               throw new JOSEException("Unsupported curve: " + curve);
            } else {
               int signatureLength = getSignatureByteArrayLength(jwsAlg);
               if (getSignatureByteArrayLength(jwsAlg) != jwsSignature.length) {
                  throw new JOSEException("Illegal signature length");
               } else {
                  int valueLength = signatureLength / 2;
                  byte[] rBytes = ByteUtils.subArray(jwsSignature, 0, valueLength);
                  BigInteger rValue = new BigInteger(1, rBytes);
                  byte[] sBytes = ByteUtils.subArray(jwsSignature, valueLength, valueLength);
                  BigInteger sValue = new BigInteger(1, sBytes);
                  if (!sValue.equals(BigInteger.ZERO) && !rValue.equals(BigInteger.ZERO)) {
                     BigInteger N = ecParameterSpec.getOrder();
                     if (N.compareTo(rValue) < 1 || N.compareTo(sValue) < 1) {
                        throw new JOSEException("S and R must not exceed N");
                     } else if (rValue.mod(N).equals(BigInteger.ZERO) || sValue.mod(N).equals(BigInteger.ZERO)) {
                        throw new JOSEException("R or S mod N != 0 check failed");
                     }
                  } else {
                     throw new JOSEException("S and R must not be 0");
                  }
               }
            }
         } else {
            throw new JOSEException("Unsupported JWS algorithm: " + jwsAlg);
         }
      }
   }

   private ECDSA() {
   }
}
