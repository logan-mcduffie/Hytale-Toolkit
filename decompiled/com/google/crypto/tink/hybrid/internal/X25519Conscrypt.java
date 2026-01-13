package com.google.crypto.tink.hybrid.internal;

import com.google.crypto.tink.internal.ConscryptUtil;
import com.google.crypto.tink.internal.Util;
import com.google.crypto.tink.subtle.Bytes;
import com.google.errorprone.annotations.Immutable;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.Provider;
import java.security.PublicKey;
import java.security.spec.KeySpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;
import javax.crypto.KeyAgreement;

@Immutable
public final class X25519Conscrypt implements X25519 {
   private static final int PRIVATE_KEY_LEN = 32;
   private static final int PUBLIC_KEY_LEN = 32;
   private static final byte[] x25519Pkcs8Prefix = new byte[]{48, 46, 2, 1, 0, 48, 5, 6, 3, 43, 101, 110, 4, 34, 4, 32};
   private static final byte[] x25519X509Prefix = new byte[]{48, 42, 48, 5, 6, 3, 43, 101, 110, 3, 33, 0};
   final Provider provider;

   private X25519Conscrypt(Provider provider) {
      this.provider = provider;
   }

   public static X25519 create() throws GeneralSecurityException {
      Provider provider = ConscryptUtil.providerOrNull();
      if (provider == null) {
         throw new GeneralSecurityException("Conscrypt is not available.");
      } else {
         KeyFactory unusedKeyFactory = KeyFactory.getInstance("XDH", provider);
         KeyAgreement unusedKeyAgreement = KeyAgreement.getInstance("XDH", provider);
         X25519 output = new X25519Conscrypt(provider);
         X25519.KeyPair unused = output.generateKeyPair();
         return output;
      }
   }

   @Override
   public X25519.KeyPair generateKeyPair() throws GeneralSecurityException {
      KeyPairGenerator keyGen = KeyPairGenerator.getInstance("XDH", this.provider);
      keyGen.initialize(255);
      java.security.KeyPair keyPair = keyGen.generateKeyPair();
      byte[] pkcs8EncodedPrivateKey = keyPair.getPrivate().getEncoded();
      if (pkcs8EncodedPrivateKey.length != 32 + x25519Pkcs8Prefix.length) {
         throw new GeneralSecurityException("Invalid encoded private key length");
      } else if (!Util.isPrefix(x25519Pkcs8Prefix, pkcs8EncodedPrivateKey)) {
         throw new GeneralSecurityException("Invalid encoded private key prefix");
      } else {
         byte[] privateKey = Arrays.copyOfRange(pkcs8EncodedPrivateKey, x25519Pkcs8Prefix.length, pkcs8EncodedPrivateKey.length);
         byte[] x509EncodedPublicKey = keyPair.getPublic().getEncoded();
         if (x509EncodedPublicKey.length != 32 + x25519X509Prefix.length) {
            throw new GeneralSecurityException("Invalid encoded public key length");
         } else if (!Util.isPrefix(x25519X509Prefix, x509EncodedPublicKey)) {
            throw new GeneralSecurityException("Invalid encoded public key prefix");
         } else {
            byte[] publicKey = Arrays.copyOfRange(x509EncodedPublicKey, x25519X509Prefix.length, x509EncodedPublicKey.length);
            return new X25519.KeyPair(privateKey, publicKey);
         }
      }
   }

   @Override
   public byte[] computeSharedSecret(byte[] privateValue, byte[] peersPublicValue) throws GeneralSecurityException {
      KeyFactory keyFactory = KeyFactory.getInstance("XDH", this.provider);
      if (privateValue.length != 32) {
         throw new InvalidKeyException("Invalid X25519 private key");
      } else {
         KeySpec privateKeySpec = new PKCS8EncodedKeySpec(Bytes.concat(x25519Pkcs8Prefix, privateValue));
         PrivateKey privateKey = keyFactory.generatePrivate(privateKeySpec);
         if (peersPublicValue.length != 32) {
            throw new InvalidKeyException("Invalid X25519 public key");
         } else {
            KeySpec publicKeySpec = new X509EncodedKeySpec(Bytes.concat(x25519X509Prefix, peersPublicValue));
            PublicKey publicKey = keyFactory.generatePublic(publicKeySpec);
            KeyAgreement keyAgreementA = KeyAgreement.getInstance("XDH", this.provider);
            keyAgreementA.init(privateKey);
            keyAgreementA.doPhase(publicKey, true);
            return keyAgreementA.generateSecret();
         }
      }
   }
}
