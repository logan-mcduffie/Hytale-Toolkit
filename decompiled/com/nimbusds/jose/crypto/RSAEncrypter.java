package com.nimbusds.jose.crypto;

import com.nimbusds.jose.EncryptionMethod;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWEAlgorithm;
import com.nimbusds.jose.JWECryptoParts;
import com.nimbusds.jose.JWEEncrypter;
import com.nimbusds.jose.JWEEncrypterOption;
import com.nimbusds.jose.JWEHeader;
import com.nimbusds.jose.crypto.impl.AAD;
import com.nimbusds.jose.crypto.impl.AlgorithmSupportMessage;
import com.nimbusds.jose.crypto.impl.ContentCryptoProvider;
import com.nimbusds.jose.crypto.impl.JWEHeaderValidation;
import com.nimbusds.jose.crypto.impl.RSA1_5;
import com.nimbusds.jose.crypto.impl.RSACryptoProvider;
import com.nimbusds.jose.crypto.impl.RSA_OAEP;
import com.nimbusds.jose.crypto.impl.RSA_OAEP_SHA2;
import com.nimbusds.jose.crypto.opts.CipherMode;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.shaded.jcip.ThreadSafe;
import com.nimbusds.jose.util.Base64URL;
import java.security.interfaces.RSAPublicKey;
import java.util.Collections;
import java.util.Objects;
import java.util.Set;
import javax.crypto.SecretKey;

@ThreadSafe
public class RSAEncrypter extends RSACryptoProvider implements JWEEncrypter {
   private final RSAPublicKey publicKey;
   private final Set<JWEEncrypterOption> opts;

   public RSAEncrypter(RSAPublicKey publicKey) {
      this(publicKey, null);
   }

   public RSAEncrypter(RSAKey rsaJWK) throws JOSEException {
      this(rsaJWK.toRSAPublicKey());
   }

   public RSAEncrypter(RSAPublicKey publicKey, SecretKey contentEncryptionKey) {
      this(publicKey, contentEncryptionKey, Collections.emptySet());
   }

   public RSAEncrypter(RSAPublicKey publicKey, SecretKey contentEncryptionKey, Set<JWEEncrypterOption> opts) {
      super(contentEncryptionKey);
      this.publicKey = Objects.requireNonNull(publicKey);
      this.opts = opts != null ? opts : Collections.emptySet();
   }

   public RSAPublicKey getPublicKey() {
      return this.publicKey;
   }

   @Deprecated
   public JWECryptoParts encrypt(JWEHeader header, byte[] clearText) throws JOSEException {
      return this.encrypt(header, clearText, AAD.compute(header));
   }

   private CipherMode resolveCipherModeForOAEP() {
      return this.opts.contains(CipherMode.ENCRYPT_DECRYPT) ? CipherMode.ENCRYPT_DECRYPT : CipherMode.WRAP_UNWRAP;
   }

   @Override
   public JWECryptoParts encrypt(JWEHeader header, byte[] clearText, byte[] aad) throws JOSEException {
      JWEAlgorithm alg = JWEHeaderValidation.getAlgorithmAndEnsureNotNull(header);
      EncryptionMethod enc = header.getEncryptionMethod();
      SecretKey cek = this.getCEK(enc);
      Base64URL encryptedKey;
      if (alg.equals(JWEAlgorithm.RSA1_5)) {
         encryptedKey = Base64URL.encode(RSA1_5.encryptCEK(this.publicKey, cek, this.getJCAContext().getKeyEncryptionProvider()));
      } else if (alg.equals(JWEAlgorithm.RSA_OAEP)) {
         encryptedKey = Base64URL.encode(
            RSA_OAEP.encryptCEK(this.publicKey, cek, this.resolveCipherModeForOAEP(), this.getJCAContext().getKeyEncryptionProvider())
         );
      } else if (alg.equals(JWEAlgorithm.RSA_OAEP_256)) {
         encryptedKey = Base64URL.encode(
            RSA_OAEP_SHA2.encryptCEK(this.publicKey, cek, 256, this.resolveCipherModeForOAEP(), this.getJCAContext().getKeyEncryptionProvider())
         );
      } else if (alg.equals(JWEAlgorithm.RSA_OAEP_384)) {
         encryptedKey = Base64URL.encode(
            RSA_OAEP_SHA2.encryptCEK(this.publicKey, cek, 384, this.resolveCipherModeForOAEP(), this.getJCAContext().getKeyEncryptionProvider())
         );
      } else {
         if (!alg.equals(JWEAlgorithm.RSA_OAEP_512)) {
            throw new JOSEException(AlgorithmSupportMessage.unsupportedJWEAlgorithm(alg, SUPPORTED_ALGORITHMS));
         }

         encryptedKey = Base64URL.encode(
            RSA_OAEP_SHA2.encryptCEK(this.publicKey, cek, 512, this.resolveCipherModeForOAEP(), this.getJCAContext().getKeyEncryptionProvider())
         );
      }

      return ContentCryptoProvider.encrypt(header, clearText, aad, cek, encryptedKey, this.getJCAContext());
   }
}
