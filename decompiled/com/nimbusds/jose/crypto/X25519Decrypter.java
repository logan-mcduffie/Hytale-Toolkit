package com.nimbusds.jose.crypto;

import com.nimbusds.jose.CriticalHeaderParamsAware;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWEDecrypter;
import com.nimbusds.jose.JWEHeader;
import com.nimbusds.jose.crypto.impl.AAD;
import com.nimbusds.jose.crypto.impl.CriticalHeaderParamsDeferral;
import com.nimbusds.jose.crypto.impl.ECDH;
import com.nimbusds.jose.crypto.impl.ECDHCryptoProvider;
import com.nimbusds.jose.jwk.Curve;
import com.nimbusds.jose.jwk.OctetKeyPair;
import com.nimbusds.jose.util.Base64URL;
import java.util.Collections;
import java.util.Set;
import javax.crypto.SecretKey;

public class X25519Decrypter extends ECDHCryptoProvider implements JWEDecrypter, CriticalHeaderParamsAware {
   private final OctetKeyPair privateKey;
   private final CriticalHeaderParamsDeferral critPolicy = new CriticalHeaderParamsDeferral();

   public X25519Decrypter(OctetKeyPair privateKey) throws JOSEException {
      this(privateKey, null);
   }

   public X25519Decrypter(OctetKeyPair privateKey, Set<String> defCritHeaders) throws JOSEException {
      super(privateKey.getCurve(), null);
      if (!Curve.X25519.equals(privateKey.getCurve())) {
         throw new JOSEException("X25519Decrypter only supports OctetKeyPairs with crv=X25519");
      } else if (!privateKey.isPrivate()) {
         throw new JOSEException("The OctetKeyPair doesn't contain a private part");
      } else {
         this.privateKey = privateKey;
         this.critPolicy.setDeferredCriticalHeaderParams(defCritHeaders);
      }
   }

   @Override
   public Set<Curve> supportedEllipticCurves() {
      return Collections.singleton(Curve.X25519);
   }

   public OctetKeyPair getPrivateKey() {
      return this.privateKey;
   }

   @Override
   public Set<String> getProcessedCriticalHeaderParams() {
      return this.critPolicy.getProcessedCriticalHeaderParams();
   }

   @Override
   public Set<String> getDeferredCriticalHeaderParams() {
      return this.critPolicy.getProcessedCriticalHeaderParams();
   }

   @Deprecated
   public byte[] decrypt(JWEHeader header, Base64URL encryptedKey, Base64URL iv, Base64URL cipherText, Base64URL authTag) throws JOSEException {
      return this.decrypt(header, encryptedKey, iv, cipherText, authTag, AAD.compute(header));
   }

   @Override
   public byte[] decrypt(JWEHeader header, Base64URL encryptedKey, Base64URL iv, Base64URL cipherText, Base64URL authTag, byte[] aad) throws JOSEException {
      this.critPolicy.ensureHeaderPasses(header);
      OctetKeyPair ephemeralPublicKey = (OctetKeyPair)header.getEphemeralPublicKey();
      if (ephemeralPublicKey == null) {
         throw new JOSEException("Missing ephemeral public key epk JWE header parameter");
      } else if (!this.privateKey.getCurve().equals(ephemeralPublicKey.getCurve())) {
         throw new JOSEException("Curve of ephemeral public key does not match curve of private key");
      } else {
         SecretKey Z = ECDH.deriveSharedSecret(ephemeralPublicKey, this.privateKey);
         return this.decryptWithZ(header, aad, Z, encryptedKey, iv, cipherText, authTag);
      }
   }
}
