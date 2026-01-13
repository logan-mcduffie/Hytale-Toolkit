package com.nimbusds.jose.crypto;

import com.nimbusds.jose.CriticalHeaderParamsAware;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWEAlgorithm;
import com.nimbusds.jose.JWEDecrypter;
import com.nimbusds.jose.JWEHeader;
import com.nimbusds.jose.KeyLengthException;
import com.nimbusds.jose.crypto.impl.AAD;
import com.nimbusds.jose.crypto.impl.AESCryptoProvider;
import com.nimbusds.jose.crypto.impl.AESGCMKW;
import com.nimbusds.jose.crypto.impl.AESKW;
import com.nimbusds.jose.crypto.impl.AlgorithmSupportMessage;
import com.nimbusds.jose.crypto.impl.AuthenticatedCipherText;
import com.nimbusds.jose.crypto.impl.ContentCryptoProvider;
import com.nimbusds.jose.crypto.impl.CriticalHeaderParamsDeferral;
import com.nimbusds.jose.crypto.impl.JWEHeaderValidation;
import com.nimbusds.jose.jwk.OctetSequenceKey;
import com.nimbusds.jose.shaded.jcip.ThreadSafe;
import com.nimbusds.jose.util.Base64URL;
import java.util.Set;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

@ThreadSafe
public class AESDecrypter extends AESCryptoProvider implements JWEDecrypter, CriticalHeaderParamsAware {
   private final CriticalHeaderParamsDeferral critPolicy = new CriticalHeaderParamsDeferral();

   public AESDecrypter(SecretKey kek) throws KeyLengthException {
      this(kek, null);
   }

   public AESDecrypter(byte[] keyBytes) throws KeyLengthException {
      this(new SecretKeySpec(keyBytes, "AES"));
   }

   public AESDecrypter(OctetSequenceKey octJWK) throws KeyLengthException {
      this(octJWK.toSecretKey("AES"));
   }

   public AESDecrypter(SecretKey kek, Set<String> defCritHeaders) throws KeyLengthException {
      super(kek, null);
      this.critPolicy.setDeferredCriticalHeaderParams(defCritHeaders);
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
      if (encryptedKey == null) {
         throw new JOSEException("Missing JWE encrypted key");
      } else if (iv == null) {
         throw new JOSEException("Missing JWE initialization vector (IV)");
      } else if (authTag == null) {
         throw new JOSEException("Missing JWE authentication tag");
      } else {
         JWEAlgorithm alg = JWEHeaderValidation.getAlgorithmAndEnsureNotNull(header);
         this.critPolicy.ensureHeaderPasses(header);
         int keyLength = header.getEncryptionMethod().cekBitLength();
         SecretKey cek;
         if (!alg.equals(JWEAlgorithm.A128KW) && !alg.equals(JWEAlgorithm.A192KW) && !alg.equals(JWEAlgorithm.A256KW)) {
            if (!alg.equals(JWEAlgorithm.A128GCMKW) && !alg.equals(JWEAlgorithm.A192GCMKW) && !alg.equals(JWEAlgorithm.A256GCMKW)) {
               throw new JOSEException(AlgorithmSupportMessage.unsupportedJWEAlgorithm(alg, SUPPORTED_ALGORITHMS));
            }

            if (header.getIV() == null) {
               throw new JOSEException("Missing JWE \"iv\" header parameter");
            }

            byte[] keyIV = header.getIV().decode();
            if (header.getAuthTag() == null) {
               throw new JOSEException("Missing JWE \"tag\" header parameter");
            }

            byte[] keyTag = header.getAuthTag().decode();
            AuthenticatedCipherText authEncrCEK = new AuthenticatedCipherText(encryptedKey.decode(), keyTag);
            cek = AESGCMKW.decryptCEK(this.getKey(), keyIV, authEncrCEK, keyLength, this.getJCAContext().getKeyEncryptionProvider());
         } else {
            cek = AESKW.unwrapCEK(this.getKey(), encryptedKey.decode(), this.getJCAContext().getKeyEncryptionProvider());
         }

         return ContentCryptoProvider.decrypt(header, aad, encryptedKey, iv, cipherText, authTag, cek, this.getJCAContext());
      }
   }
}
