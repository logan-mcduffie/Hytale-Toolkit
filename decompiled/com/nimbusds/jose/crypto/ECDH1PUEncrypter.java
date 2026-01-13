package com.nimbusds.jose.crypto;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWECryptoParts;
import com.nimbusds.jose.JWEEncrypter;
import com.nimbusds.jose.JWEHeader;
import com.nimbusds.jose.crypto.impl.AAD;
import com.nimbusds.jose.crypto.impl.ECDH1PU;
import com.nimbusds.jose.crypto.impl.ECDH1PUCryptoProvider;
import com.nimbusds.jose.jwk.Curve;
import com.nimbusds.jose.jwk.ECKey;
import com.nimbusds.jose.shaded.jcip.ThreadSafe;
import java.security.InvalidAlgorithmParameterException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.Provider;
import java.security.interfaces.ECPrivateKey;
import java.security.interfaces.ECPublicKey;
import java.security.spec.ECParameterSpec;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;
import javax.crypto.SecretKey;

@ThreadSafe
public class ECDH1PUEncrypter extends ECDH1PUCryptoProvider implements JWEEncrypter {
   public static final Set<Curve> SUPPORTED_ELLIPTIC_CURVES;
   private final ECPublicKey publicKey;
   private final ECPrivateKey privateKey;

   public ECDH1PUEncrypter(ECPrivateKey privateKey, ECPublicKey publicKey) throws JOSEException {
      this(privateKey, publicKey, null);
   }

   public ECDH1PUEncrypter(ECPrivateKey privateKey, ECPublicKey publicKey, SecretKey contentEncryptionKey) throws JOSEException {
      super(Curve.forECParameterSpec(publicKey.getParams()), contentEncryptionKey);
      this.privateKey = privateKey;
      this.publicKey = publicKey;
   }

   public ECPublicKey getPublicKey() {
      return this.publicKey;
   }

   public ECPrivateKey getPrivateKey() {
      return this.privateKey;
   }

   @Override
   public Set<Curve> supportedEllipticCurves() {
      return SUPPORTED_ELLIPTIC_CURVES;
   }

   @Deprecated
   public JWECryptoParts encrypt(JWEHeader header, byte[] clearText) throws JOSEException {
      return this.encrypt(header, clearText, AAD.compute(header));
   }

   @Override
   public JWECryptoParts encrypt(JWEHeader header, byte[] clearText, byte[] aad) throws JOSEException {
      KeyPair ephemeralKeyPair = this.generateEphemeralKeyPair(this.publicKey.getParams());
      ECPublicKey ephemeralPublicKey = (ECPublicKey)ephemeralKeyPair.getPublic();
      ECPrivateKey ephemeralPrivateKey = (ECPrivateKey)ephemeralKeyPair.getPrivate();
      JWEHeader updatedHeader = new JWEHeader.Builder(header).ephemeralPublicKey(new ECKey.Builder(this.getCurve(), ephemeralPublicKey).build()).build();
      SecretKey Z = ECDH1PU.deriveSenderZ(this.privateKey, this.publicKey, ephemeralPrivateKey, this.getJCAContext().getKeyEncryptionProvider());
      byte[] updatedAAD = Arrays.equals(AAD.compute(header), aad) ? AAD.compute(updatedHeader) : aad;
      return this.encryptWithZ(updatedHeader, Z, clearText, updatedAAD);
   }

   private KeyPair generateEphemeralKeyPair(ECParameterSpec ecParameterSpec) throws JOSEException {
      Provider keProvider = this.getJCAContext().getKeyEncryptionProvider();

      try {
         KeyPairGenerator generator;
         if (keProvider != null) {
            generator = KeyPairGenerator.getInstance("EC", keProvider);
         } else {
            generator = KeyPairGenerator.getInstance("EC");
         }

         generator.initialize(ecParameterSpec);
         return generator.generateKeyPair();
      } catch (InvalidAlgorithmParameterException | NoSuchAlgorithmException var4) {
         throw new JOSEException("Couldn't generate ephemeral EC key pair: " + var4.getMessage(), var4);
      }
   }

   static {
      Set<Curve> curves = new LinkedHashSet<>();
      curves.add(Curve.P_256);
      curves.add(Curve.P_384);
      curves.add(Curve.P_521);
      SUPPORTED_ELLIPTIC_CURVES = Collections.unmodifiableSet(curves);
   }
}
