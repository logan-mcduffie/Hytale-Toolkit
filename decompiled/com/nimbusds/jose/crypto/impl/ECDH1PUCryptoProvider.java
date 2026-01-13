package com.nimbusds.jose.crypto.impl;

import com.nimbusds.jose.EncryptionMethod;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWEAlgorithm;
import com.nimbusds.jose.JWECryptoParts;
import com.nimbusds.jose.JWEHeader;
import com.nimbusds.jose.jwk.Curve;
import com.nimbusds.jose.util.Base64URL;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;
import javax.crypto.SecretKey;

public abstract class ECDH1PUCryptoProvider extends BaseJWEProvider {
   public static final Set<JWEAlgorithm> SUPPORTED_ALGORITHMS;
   public static final Set<EncryptionMethod> SUPPORTED_ENCRYPTION_METHODS = ContentCryptoProvider.SUPPORTED_ENCRYPTION_METHODS;
   private final Curve curve;
   private final ConcatKDF concatKDF;

   protected ECDH1PUCryptoProvider(Curve curve, SecretKey cek) throws JOSEException {
      super(SUPPORTED_ALGORITHMS, ContentCryptoProvider.SUPPORTED_ENCRYPTION_METHODS, cek);
      Curve definedCurve = curve != null ? curve : new Curve("unknown");
      if (!this.supportedEllipticCurves().contains(curve)) {
         throw new JOSEException(AlgorithmSupportMessage.unsupportedEllipticCurve(definedCurve, this.supportedEllipticCurves()));
      } else {
         this.curve = curve;
         this.concatKDF = new ConcatKDF("SHA-256");
      }
   }

   protected ConcatKDF getConcatKDF() {
      return this.concatKDF;
   }

   public abstract Set<Curve> supportedEllipticCurves();

   public Curve getCurve() {
      return this.curve;
   }

   protected JWECryptoParts encryptWithZ(JWEHeader header, SecretKey Z, byte[] clearText, byte[] aad) throws JOSEException {
      JWEAlgorithm alg = JWEHeaderValidation.getAlgorithmAndEnsureNotNull(header);
      ECDH.AlgorithmMode algMode = ECDH1PU.resolveAlgorithmMode(alg);
      EncryptionMethod enc = header.getEncryptionMethod();
      if (algMode.equals(ECDH.AlgorithmMode.DIRECT)) {
         if (this.isCEKProvided()) {
            throw new JOSEException("The provided CEK is not supported");
         } else {
            this.getConcatKDF().getJCAContext().setProvider(this.getJCAContext().getMACProvider());
            SecretKey cek = ECDH1PU.deriveSharedKey(header, Z, this.getConcatKDF());
            return ContentCryptoProvider.encrypt(header, clearText, aad, cek, null, this.getJCAContext());
         }
      } else if (algMode.equals(ECDH.AlgorithmMode.KW)) {
         if (!EncryptionMethod.Family.AES_CBC_HMAC_SHA.contains(enc)) {
            throw new JOSEException(AlgorithmSupportMessage.unsupportedEncryptionMethod(header.getEncryptionMethod(), EncryptionMethod.Family.AES_CBC_HMAC_SHA));
         } else {
            SecretKey cek = this.getCEK(enc);
            JWECryptoParts encrypted = ContentCryptoProvider.encrypt(header, clearText, aad, cek, null, this.getJCAContext());
            SecretKey sharedKey = ECDH1PU.deriveSharedKey(header, Z, encrypted.getAuthenticationTag(), this.getConcatKDF());
            Base64URL encryptedKey = Base64URL.encode(AESKW.wrapCEK(cek, sharedKey, this.getJCAContext().getKeyEncryptionProvider()));
            return new JWECryptoParts(header, encryptedKey, encrypted.getInitializationVector(), encrypted.getCipherText(), encrypted.getAuthenticationTag());
         }
      } else {
         throw new JOSEException("Unexpected JWE ECDH algorithm mode: " + algMode);
      }
   }

   protected byte[] decryptWithZ(JWEHeader header, byte[] aad, SecretKey Z, Base64URL encryptedKey, Base64URL iv, Base64URL cipherText, Base64URL authTag) throws JOSEException {
      JWEAlgorithm alg = JWEHeaderValidation.getAlgorithmAndEnsureNotNull(header);
      ECDH.AlgorithmMode algMode = ECDH1PU.resolveAlgorithmMode(alg);
      this.getConcatKDF().getJCAContext().setProvider(this.getJCAContext().getMACProvider());
      SecretKey cek;
      if (algMode.equals(ECDH.AlgorithmMode.DIRECT)) {
         cek = ECDH1PU.deriveSharedKey(header, Z, this.getConcatKDF());
      } else {
         if (!algMode.equals(ECDH.AlgorithmMode.KW)) {
            throw new JOSEException("Unexpected JWE ECDH algorithm mode: " + algMode);
         }

         if (encryptedKey == null) {
            throw new JOSEException("Missing JWE encrypted key");
         }

         SecretKey sharedKey = ECDH1PU.deriveSharedKey(header, Z, authTag, this.getConcatKDF());
         cek = AESKW.unwrapCEK(sharedKey, encryptedKey.decode(), this.getJCAContext().getKeyEncryptionProvider());
      }

      return ContentCryptoProvider.decrypt(header, aad, null, iv, cipherText, authTag, cek, this.getJCAContext());
   }

   static {
      Set<JWEAlgorithm> algs = new LinkedHashSet<>();
      algs.add(JWEAlgorithm.ECDH_1PU);
      algs.add(JWEAlgorithm.ECDH_1PU_A128KW);
      algs.add(JWEAlgorithm.ECDH_1PU_A192KW);
      algs.add(JWEAlgorithm.ECDH_1PU_A256KW);
      SUPPORTED_ALGORITHMS = Collections.unmodifiableSet(algs);
   }
}
