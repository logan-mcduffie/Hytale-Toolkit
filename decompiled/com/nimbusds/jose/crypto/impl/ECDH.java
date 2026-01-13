package com.nimbusds.jose.crypto.impl;

import com.google.crypto.tink.subtle.X25519;
import com.nimbusds.jose.EncryptionMethod;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWEAlgorithm;
import com.nimbusds.jose.JWEHeader;
import com.nimbusds.jose.jwk.Curve;
import com.nimbusds.jose.jwk.OctetKeyPair;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.Provider;
import java.security.interfaces.ECPublicKey;
import javax.crypto.KeyAgreement;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class ECDH {
   public static ECDH.AlgorithmMode resolveAlgorithmMode(JWEAlgorithm alg) throws JOSEException {
      if (alg.equals(JWEAlgorithm.ECDH_ES)) {
         return ECDH.AlgorithmMode.DIRECT;
      } else if (!alg.equals(JWEAlgorithm.ECDH_ES_A128KW) && !alg.equals(JWEAlgorithm.ECDH_ES_A192KW) && !alg.equals(JWEAlgorithm.ECDH_ES_A256KW)) {
         throw new JOSEException(AlgorithmSupportMessage.unsupportedJWEAlgorithm(alg, ECDHCryptoProvider.SUPPORTED_ALGORITHMS));
      } else {
         return ECDH.AlgorithmMode.KW;
      }
   }

   public static int sharedKeyLength(JWEAlgorithm alg, EncryptionMethod enc) throws JOSEException {
      if (alg.equals(JWEAlgorithm.ECDH_ES)) {
         int length = enc.cekBitLength();
         if (length == 0) {
            throw new JOSEException("Unsupported JWE encryption method " + enc);
         } else {
            return length;
         }
      } else if (alg.equals(JWEAlgorithm.ECDH_ES_A128KW)) {
         return 128;
      } else if (alg.equals(JWEAlgorithm.ECDH_ES_A192KW)) {
         return 192;
      } else if (alg.equals(JWEAlgorithm.ECDH_ES_A256KW)) {
         return 256;
      } else {
         throw new JOSEException(AlgorithmSupportMessage.unsupportedJWEAlgorithm(alg, ECDHCryptoProvider.SUPPORTED_ALGORITHMS));
      }
   }

   public static SecretKey deriveSharedSecret(ECPublicKey publicKey, PrivateKey privateKey, Provider provider) throws JOSEException {
      KeyAgreement keyAgreement;
      try {
         if (provider != null) {
            keyAgreement = KeyAgreement.getInstance("ECDH", provider);
         } else {
            keyAgreement = KeyAgreement.getInstance("ECDH");
         }
      } catch (NoSuchAlgorithmException var6) {
         throw new JOSEException("Couldn't get an ECDH key agreement instance: " + var6.getMessage(), var6);
      }

      try {
         keyAgreement.init(privateKey);
         keyAgreement.doPhase(publicKey, true);
      } catch (InvalidKeyException var5) {
         throw new JOSEException("Invalid key for ECDH key agreement: " + var5.getMessage(), var5);
      }

      return new SecretKeySpec(keyAgreement.generateSecret(), "AES");
   }

   public static SecretKey deriveSharedSecret(OctetKeyPair publicKey, OctetKeyPair privateKey) throws JOSEException {
      if (publicKey.isPrivate()) {
         throw new JOSEException("Expected public key but received OKP with 'd' value");
      } else if (!Curve.X25519.equals(publicKey.getCurve())) {
         throw new JOSEException("Expected public key OKP with crv=X25519");
      } else if (!privateKey.isPrivate()) {
         throw new JOSEException("Expected private key but received OKP without 'd' value");
      } else if (!Curve.X25519.equals(privateKey.getCurve())) {
         throw new JOSEException("Expected private key OKP with crv=X25519");
      } else {
         byte[] privateKeyBytes = privateKey.getDecodedD();
         byte[] publicKeyBytes = publicKey.getDecodedX();

         byte[] sharedSecretBytes;
         try {
            sharedSecretBytes = X25519.computeSharedSecret(privateKeyBytes, publicKeyBytes);
         } catch (InvalidKeyException var6) {
            throw new JOSEException(var6.getMessage(), var6);
         }

         return new SecretKeySpec(sharedSecretBytes, "AES");
      }
   }

   public static SecretKey deriveSharedKey(JWEHeader header, SecretKey Z, ConcatKDF concatKDF) throws JOSEException {
      int sharedKeyLength = sharedKeyLength(header.getAlgorithm(), header.getEncryptionMethod());
      ECDH.AlgorithmMode algMode = resolveAlgorithmMode(header.getAlgorithm());
      String algID;
      if (algMode == ECDH.AlgorithmMode.DIRECT) {
         algID = header.getEncryptionMethod().getName();
      } else {
         if (algMode != ECDH.AlgorithmMode.KW) {
            throw new JOSEException("Unsupported JWE ECDH algorithm mode: " + algMode);
         }

         algID = header.getAlgorithm().getName();
      }

      return concatKDF.deriveKey(
         Z,
         sharedKeyLength,
         ConcatKDF.encodeDataWithLength(algID.getBytes(StandardCharsets.US_ASCII)),
         ConcatKDF.encodeDataWithLength(header.getAgreementPartyUInfo()),
         ConcatKDF.encodeDataWithLength(header.getAgreementPartyVInfo()),
         ConcatKDF.encodeIntData(sharedKeyLength),
         ConcatKDF.encodeNoData()
      );
   }

   private ECDH() {
   }

   public static enum AlgorithmMode {
      DIRECT,
      KW;
   }
}
