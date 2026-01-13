package com.nimbusds.jose.crypto;

import com.nimbusds.jose.CriticalHeaderParamsAware;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWEAlgorithm;
import com.nimbusds.jose.JWEDecrypter;
import com.nimbusds.jose.JWEHeader;
import com.nimbusds.jose.crypto.impl.AAD;
import com.nimbusds.jose.crypto.impl.AESKW;
import com.nimbusds.jose.crypto.impl.ContentCryptoProvider;
import com.nimbusds.jose.crypto.impl.CriticalHeaderParamsDeferral;
import com.nimbusds.jose.crypto.impl.JWEHeaderValidation;
import com.nimbusds.jose.crypto.impl.PBKDF2;
import com.nimbusds.jose.crypto.impl.PRFParams;
import com.nimbusds.jose.crypto.impl.PasswordBasedCryptoProvider;
import com.nimbusds.jose.shaded.jcip.ThreadSafe;
import com.nimbusds.jose.util.Base64URL;
import com.nimbusds.jose.util.StandardCharset;
import java.util.Set;
import javax.crypto.SecretKey;

@ThreadSafe
public class PasswordBasedDecrypter extends PasswordBasedCryptoProvider implements JWEDecrypter, CriticalHeaderParamsAware {
   public static final int MAX_ALLOWED_ITERATION_COUNT = 1000000;
   private final CriticalHeaderParamsDeferral critPolicy = new CriticalHeaderParamsDeferral();

   public PasswordBasedDecrypter(byte[] password) {
      super(password);
   }

   public PasswordBasedDecrypter(String password) {
      super(password.getBytes(StandardCharset.UTF_8));
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
      } else if (header.getPBES2Salt() == null) {
         throw new JOSEException("Missing JWE p2s header parameter");
      } else {
         byte[] salt = header.getPBES2Salt().decode();
         if (header.getPBES2Count() < 1) {
            throw new JOSEException("Missing JWE p2c header parameter");
         } else {
            int iterationCount = header.getPBES2Count();
            if (iterationCount > 1000000) {
               throw new JOSEException("The JWE p2c header exceeds the maximum allowed 1000000 count");
            } else {
               this.critPolicy.ensureHeaderPasses(header);
               JWEAlgorithm alg = JWEHeaderValidation.getAlgorithmAndEnsureNotNull(header);
               byte[] formattedSalt = PBKDF2.formatSalt(alg, salt);
               PRFParams prfParams = PRFParams.resolve(alg, this.getJCAContext().getMACProvider());
               SecretKey psKey = PBKDF2.deriveKey(this.getPassword(), formattedSalt, iterationCount, prfParams, this.getJCAContext().getProvider());
               SecretKey cek = AESKW.unwrapCEK(psKey, encryptedKey.decode(), this.getJCAContext().getKeyEncryptionProvider());
               return ContentCryptoProvider.decrypt(header, aad, encryptedKey, iv, cipherText, authTag, cek, this.getJCAContext());
            }
         }
      }
   }
}
