package com.google.crypto.tink.subtle;

import com.google.protobuf.ByteString;
import java.security.GeneralSecurityException;
import java.security.interfaces.ECPrivateKey;
import java.security.interfaces.ECPublicKey;
import java.security.interfaces.RSAPrivateCrtKey;
import java.security.interfaces.RSAPublicKey;

public final class SelfKeyTestValidators {
   private static final ByteString TEST_MESSAGE = ByteString.copyFromUtf8("Tink and Wycheproof.");

   private SelfKeyTestValidators() {
   }

   public static final void validateRsaSsaPss(
      RSAPrivateCrtKey privateKey, RSAPublicKey publicKey, Enums.HashType sigHash, Enums.HashType mgf1Hash, int saltLength
   ) throws GeneralSecurityException {
      RsaSsaPssSignJce rsaSigner = new RsaSsaPssSignJce(privateKey, sigHash, mgf1Hash, saltLength);
      RsaSsaPssVerifyJce rsaVerifier = new RsaSsaPssVerifyJce(publicKey, sigHash, mgf1Hash, saltLength);

      try {
         rsaVerifier.verify(rsaSigner.sign(TEST_MESSAGE.toByteArray()), TEST_MESSAGE.toByteArray());
      } catch (GeneralSecurityException var8) {
         throw new GeneralSecurityException("RSA PSS signing with private key followed by verifying with public key failed. The key may be corrupted.", var8);
      }
   }

   public static final void validateRsaSsaPkcs1(RSAPrivateCrtKey privateKey, RSAPublicKey publicKey, Enums.HashType sigHash) throws GeneralSecurityException {
      RsaSsaPkcs1SignJce rsaSigner = new RsaSsaPkcs1SignJce(privateKey, sigHash);
      RsaSsaPkcs1VerifyJce rsaVerifier = new RsaSsaPkcs1VerifyJce(publicKey, sigHash);

      try {
         rsaVerifier.verify(rsaSigner.sign(TEST_MESSAGE.toByteArray()), TEST_MESSAGE.toByteArray());
      } catch (GeneralSecurityException var6) {
         throw new GeneralSecurityException("RSA PKCS1 signing with private key followed by verifying with public key failed. The key may be corrupted.", var6);
      }
   }

   public static final void validateEcdsa(ECPrivateKey privateKey, ECPublicKey publicKey, Enums.HashType hash, EllipticCurves.EcdsaEncoding encoding) throws GeneralSecurityException {
      EcdsaSignJce ecdsaSigner = new EcdsaSignJce(privateKey, hash, encoding);
      EcdsaVerifyJce ecdsaverifier = new EcdsaVerifyJce(publicKey, hash, encoding);

      try {
         ecdsaverifier.verify(ecdsaSigner.sign(TEST_MESSAGE.toByteArray()), TEST_MESSAGE.toByteArray());
      } catch (GeneralSecurityException var7) {
         throw new GeneralSecurityException("ECDSA signing with private key followed by verifying with public key failed. The key may be corrupted.", var7);
      }
   }
}
