package com.nimbusds.jose.crypto;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWECryptoParts;
import com.nimbusds.jose.JWEEncrypter;
import com.nimbusds.jose.JWEHeader;
import com.nimbusds.jose.crypto.impl.AAD;
import com.nimbusds.jose.crypto.impl.ECDH1PU;
import com.nimbusds.jose.crypto.impl.ECDH1PUCryptoProvider;
import com.nimbusds.jose.jwk.Curve;
import com.nimbusds.jose.jwk.OctetKeyPair;
import com.nimbusds.jose.jwk.gen.OctetKeyPairGenerator;
import com.nimbusds.jose.shaded.jcip.ThreadSafe;
import java.util.Arrays;
import java.util.Collections;
import java.util.Set;
import javax.crypto.SecretKey;

@ThreadSafe
public class ECDH1PUX25519Encrypter extends ECDH1PUCryptoProvider implements JWEEncrypter {
   private final OctetKeyPair publicKey;
   private final OctetKeyPair privateKey;

   public ECDH1PUX25519Encrypter(OctetKeyPair privateKey, OctetKeyPair publicKey) throws JOSEException {
      this(privateKey, publicKey, null);
   }

   public ECDH1PUX25519Encrypter(OctetKeyPair privateKey, OctetKeyPair publicKey, SecretKey contentEncryptionKey) throws JOSEException {
      super(publicKey.getCurve(), contentEncryptionKey);
      this.publicKey = publicKey;
      this.privateKey = privateKey;
   }

   @Override
   public Set<Curve> supportedEllipticCurves() {
      return Collections.singleton(Curve.X25519);
   }

   public OctetKeyPair getPublicKey() {
      return this.publicKey;
   }

   public OctetKeyPair getPrivateKey() {
      return this.privateKey;
   }

   @Deprecated
   public JWECryptoParts encrypt(JWEHeader header, byte[] clearText) throws JOSEException {
      return this.encrypt(header, clearText, AAD.compute(header));
   }

   @Override
   public JWECryptoParts encrypt(JWEHeader header, byte[] clearText, byte[] aad) throws JOSEException {
      OctetKeyPair ephemeralPrivateKey = new OctetKeyPairGenerator(this.getCurve()).generate();
      OctetKeyPair ephemeralPublicKey = ephemeralPrivateKey.toPublicJWK();
      JWEHeader updatedHeader = new JWEHeader.Builder(header).ephemeralPublicKey(ephemeralPublicKey).build();
      SecretKey Z = ECDH1PU.deriveSenderZ(this.privateKey, this.publicKey, ephemeralPrivateKey);
      byte[] updatedAAD = Arrays.equals(AAD.compute(header), aad) ? AAD.compute(updatedHeader) : aad;
      return this.encryptWithZ(updatedHeader, Z, clearText, updatedAAD);
   }
}
