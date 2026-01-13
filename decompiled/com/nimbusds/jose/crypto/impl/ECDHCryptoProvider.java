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

public abstract class ECDHCryptoProvider extends BaseJWEProvider {
   public static final Set<JWEAlgorithm> SUPPORTED_ALGORITHMS;
   public static final Set<EncryptionMethod> SUPPORTED_ENCRYPTION_METHODS = ContentCryptoProvider.SUPPORTED_ENCRYPTION_METHODS;
   private final Curve curve;
   private final ConcatKDF concatKDF;

   protected ECDHCryptoProvider(Curve curve, SecretKey cek) throws JOSEException {
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
      ECDH.AlgorithmMode algMode = ECDH.resolveAlgorithmMode(alg);
      EncryptionMethod enc = header.getEncryptionMethod();
      this.getConcatKDF().getJCAContext().setProvider(this.getJCAContext().getMACProvider());
      SecretKey sharedKey = ECDH.deriveSharedKey(header, Z, this.getConcatKDF());
      SecretKey cek;
      Base64URL encryptedKey;
      if (algMode.equals(ECDH.AlgorithmMode.DIRECT)) {
         if (this.isCEKProvided()) {
            throw new JOSEException("The provided CEK is not supported");
         }

         cek = sharedKey;
         encryptedKey = null;
      } else {
         if (!algMode.equals(ECDH.AlgorithmMode.KW)) {
            throw new JOSEException("Unexpected JWE ECDH algorithm mode: " + algMode);
         }

         cek = this.getCEK(enc);
         encryptedKey = Base64URL.encode(AESKW.wrapCEK(cek, sharedKey, this.getJCAContext().getKeyEncryptionProvider()));
      }

      return ContentCryptoProvider.encrypt(header, clearText, aad, cek, encryptedKey, this.getJCAContext());
   }

   protected byte[] decryptWithZ(JWEHeader header, byte[] aad, SecretKey Z, Base64URL encryptedKey, Base64URL iv, Base64URL cipherText, Base64URL authTag) throws JOSEException {
      JWEAlgorithm alg = JWEHeaderValidation.getAlgorithmAndEnsureNotNull(header);
      ECDH.AlgorithmMode algMode = ECDH.resolveAlgorithmMode(alg);
      this.getConcatKDF().getJCAContext().setProvider(this.getJCAContext().getMACProvider());
      SecretKey sharedKey = ECDH.deriveSharedKey(header, Z, this.getConcatKDF());
      SecretKey cek;
      if (algMode.equals(ECDH.AlgorithmMode.DIRECT)) {
         cek = sharedKey;
      } else {
         if (!algMode.equals(ECDH.AlgorithmMode.KW)) {
            throw new JOSEException("Unexpected JWE ECDH algorithm mode: " + algMode);
         }

         if (encryptedKey == null) {
            throw new JOSEException("Missing JWE encrypted key");
         }

         cek = AESKW.unwrapCEK(sharedKey, encryptedKey.decode(), this.getJCAContext().getKeyEncryptionProvider());
      }

      return ContentCryptoProvider.decrypt(header, aad, encryptedKey, iv, cipherText, authTag, cek, this.getJCAContext());
   }

   static {
      Set<JWEAlgorithm> algs = new LinkedHashSet<>();
      algs.add(JWEAlgorithm.ECDH_ES);
      algs.add(JWEAlgorithm.ECDH_ES_A128KW);
      algs.add(JWEAlgorithm.ECDH_ES_A192KW);
      algs.add(JWEAlgorithm.ECDH_ES_A256KW);
      SUPPORTED_ALGORITHMS = Collections.unmodifiableSet(algs);
   }
}
