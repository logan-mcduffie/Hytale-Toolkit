package com.nimbusds.jose.crypto;

import com.nimbusds.jose.CriticalHeaderParamsAware;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWEDecrypter;
import com.nimbusds.jose.JWEHeader;
import com.nimbusds.jose.crypto.impl.AAD;
import com.nimbusds.jose.crypto.impl.CriticalHeaderParamsDeferral;
import com.nimbusds.jose.crypto.impl.ECDH1PU;
import com.nimbusds.jose.crypto.impl.ECDH1PUCryptoProvider;
import com.nimbusds.jose.jwk.Curve;
import com.nimbusds.jose.jwk.OctetKeyPair;
import com.nimbusds.jose.shaded.jcip.ThreadSafe;
import com.nimbusds.jose.util.Base64URL;
import java.util.Collections;
import java.util.Set;
import javax.crypto.SecretKey;

@ThreadSafe
public class ECDH1PUX25519Decrypter extends ECDH1PUCryptoProvider implements JWEDecrypter, CriticalHeaderParamsAware {
   private final OctetKeyPair privateKey;
   private final OctetKeyPair publicKey;
   private final CriticalHeaderParamsDeferral critPolicy = new CriticalHeaderParamsDeferral();

   public ECDH1PUX25519Decrypter(OctetKeyPair privateKey, OctetKeyPair publicKey) throws JOSEException {
      this(privateKey, publicKey, null);
   }

   public ECDH1PUX25519Decrypter(OctetKeyPair privateKey, OctetKeyPair publicKey, Set<String> defCritHeaders) throws JOSEException {
      super(privateKey.getCurve(), null);
      this.privateKey = privateKey;
      this.publicKey = publicKey;
      this.critPolicy.setDeferredCriticalHeaderParams(defCritHeaders);
   }

   @Override
   public Set<Curve> supportedEllipticCurves() {
      return Collections.singleton(Curve.X25519);
   }

   public OctetKeyPair getPrivateKey() {
      return this.privateKey;
   }

   public OctetKeyPair getPublicKey() {
      return this.publicKey;
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
         throw new JOSEException("Missing ephemeral public key \"epk\" JWE header parameter");
      } else {
         SecretKey Z = ECDH1PU.deriveRecipientZ(this.privateKey, this.publicKey, ephemeralPublicKey);
         return this.decryptWithZ(header, aad, Z, encryptedKey, iv, cipherText, authTag);
      }
   }
}
