package com.nimbusds.jose.crypto;

import com.google.crypto.tink.subtle.X25519;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWECryptoParts;
import com.nimbusds.jose.JWEEncrypter;
import com.nimbusds.jose.JWEHeader;
import com.nimbusds.jose.crypto.impl.AAD;
import com.nimbusds.jose.crypto.impl.ECDH;
import com.nimbusds.jose.crypto.impl.ECDHCryptoProvider;
import com.nimbusds.jose.jwk.Curve;
import com.nimbusds.jose.jwk.OctetKeyPair;
import com.nimbusds.jose.shaded.jcip.ThreadSafe;
import com.nimbusds.jose.util.Base64URL;
import java.security.InvalidKeyException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Set;
import javax.crypto.SecretKey;

@ThreadSafe
public class X25519Encrypter extends ECDHCryptoProvider implements JWEEncrypter {
   private final OctetKeyPair publicKey;

   public X25519Encrypter(OctetKeyPair publicKey) throws JOSEException {
      this(publicKey, null);
   }

   public X25519Encrypter(OctetKeyPair publicKey, SecretKey contentEncryptionKey) throws JOSEException {
      super(publicKey.getCurve(), contentEncryptionKey);
      if (!Curve.X25519.equals(publicKey.getCurve())) {
         throw new JOSEException("X25519Encrypter only supports OctetKeyPairs with crv=X25519");
      } else if (publicKey.isPrivate()) {
         throw new JOSEException("X25519Encrypter requires a public key, use OctetKeyPair.toPublicJWK()");
      } else {
         this.publicKey = publicKey;
      }
   }

   @Override
   public Set<Curve> supportedEllipticCurves() {
      return Collections.singleton(Curve.X25519);
   }

   public OctetKeyPair getPublicKey() {
      return this.publicKey;
   }

   @Deprecated
   public JWECryptoParts encrypt(JWEHeader header, byte[] clearText) throws JOSEException {
      return this.encrypt(header, clearText, AAD.compute(header));
   }

   @Override
   public JWECryptoParts encrypt(JWEHeader header, byte[] clearText, byte[] aad) throws JOSEException {
      byte[] ephemeralPrivateKeyBytes = X25519.generatePrivateKey();

      byte[] ephemeralPublicKeyBytes;
      try {
         ephemeralPublicKeyBytes = X25519.publicFromPrivate(ephemeralPrivateKeyBytes);
      } catch (InvalidKeyException var11) {
         throw new JOSEException(var11.getMessage(), var11);
      }

      OctetKeyPair ephemeralPrivateKey = new OctetKeyPair.Builder(this.getCurve(), Base64URL.encode(ephemeralPublicKeyBytes))
         .d(Base64URL.encode(ephemeralPrivateKeyBytes))
         .build();
      OctetKeyPair ephemeralPublicKey = ephemeralPrivateKey.toPublicJWK();
      JWEHeader updatedHeader = new JWEHeader.Builder(header).ephemeralPublicKey(ephemeralPublicKey).build();
      SecretKey Z = ECDH.deriveSharedSecret(this.publicKey, ephemeralPrivateKey);
      byte[] updatedAAD = Arrays.equals(AAD.compute(header), aad) ? AAD.compute(updatedHeader) : aad;
      return this.encryptWithZ(updatedHeader, Z, clearText, updatedAAD);
   }
}
