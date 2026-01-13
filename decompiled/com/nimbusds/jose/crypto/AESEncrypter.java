package com.nimbusds.jose.crypto;

import com.nimbusds.jose.EncryptionMethod;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWEAlgorithm;
import com.nimbusds.jose.JWECryptoParts;
import com.nimbusds.jose.JWEEncrypter;
import com.nimbusds.jose.JWEHeader;
import com.nimbusds.jose.KeyLengthException;
import com.nimbusds.jose.crypto.impl.AAD;
import com.nimbusds.jose.crypto.impl.AESCryptoProvider;
import com.nimbusds.jose.crypto.impl.AESGCM;
import com.nimbusds.jose.crypto.impl.AESGCMKW;
import com.nimbusds.jose.crypto.impl.AESKW;
import com.nimbusds.jose.crypto.impl.AlgorithmSupportMessage;
import com.nimbusds.jose.crypto.impl.AuthenticatedCipherText;
import com.nimbusds.jose.crypto.impl.ContentCryptoProvider;
import com.nimbusds.jose.crypto.impl.JWEHeaderValidation;
import com.nimbusds.jose.jwk.OctetSequenceKey;
import com.nimbusds.jose.shaded.jcip.ThreadSafe;
import com.nimbusds.jose.util.Base64URL;
import com.nimbusds.jose.util.ByteUtils;
import com.nimbusds.jose.util.Container;
import java.util.Arrays;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

@ThreadSafe
public class AESEncrypter extends AESCryptoProvider implements JWEEncrypter {
   public AESEncrypter(SecretKey kek, SecretKey contentEncryptionKey) throws KeyLengthException {
      super(kek, contentEncryptionKey);
   }

   public AESEncrypter(SecretKey kek) throws KeyLengthException {
      this(kek, null);
   }

   public AESEncrypter(byte[] keyBytes) throws KeyLengthException {
      this(new SecretKeySpec(keyBytes, "AES"));
   }

   public AESEncrypter(OctetSequenceKey octJWK) throws KeyLengthException {
      this(octJWK.toSecretKey("AES"));
   }

   @Deprecated
   public JWECryptoParts encrypt(JWEHeader header, byte[] clearText) throws JOSEException {
      return this.encrypt(header, clearText, AAD.compute(header));
   }

   @Override
   public JWECryptoParts encrypt(JWEHeader header, byte[] clearText, byte[] aad) throws JOSEException {
      JWEAlgorithm alg = JWEHeaderValidation.getAlgorithmAndEnsureNotNull(header);
      EncryptionMethod enc = header.getEncryptionMethod();
      AESEncrypter.AlgFamily algFamily;
      if (alg.equals(JWEAlgorithm.A128KW)) {
         if (ByteUtils.safeBitLength(this.getKey().getEncoded()) != 128) {
            throw new KeyLengthException("The Key Encryption Key (KEK) length must be 128 bits for A128KW encryption");
         }

         algFamily = AESEncrypter.AlgFamily.AESKW;
      } else if (alg.equals(JWEAlgorithm.A192KW)) {
         if (ByteUtils.safeBitLength(this.getKey().getEncoded()) != 192) {
            throw new KeyLengthException("The Key Encryption Key (KEK) length must be 192 bits for A192KW encryption");
         }

         algFamily = AESEncrypter.AlgFamily.AESKW;
      } else if (alg.equals(JWEAlgorithm.A256KW)) {
         if (ByteUtils.safeBitLength(this.getKey().getEncoded()) != 256) {
            throw new KeyLengthException("The Key Encryption Key (KEK) length must be 256 bits for A256KW encryption");
         }

         algFamily = AESEncrypter.AlgFamily.AESKW;
      } else if (alg.equals(JWEAlgorithm.A128GCMKW)) {
         if (ByteUtils.safeBitLength(this.getKey().getEncoded()) != 128) {
            throw new KeyLengthException("The Key Encryption Key (KEK) length must be 128 bits for A128GCMKW encryption");
         }

         algFamily = AESEncrypter.AlgFamily.AESGCMKW;
      } else if (alg.equals(JWEAlgorithm.A192GCMKW)) {
         if (ByteUtils.safeBitLength(this.getKey().getEncoded()) != 192) {
            throw new KeyLengthException("The Key Encryption Key (KEK) length must be 192 bits for A192GCMKW encryption");
         }

         algFamily = AESEncrypter.AlgFamily.AESGCMKW;
      } else {
         if (!alg.equals(JWEAlgorithm.A256GCMKW)) {
            throw new JOSEException(AlgorithmSupportMessage.unsupportedJWEAlgorithm(alg, SUPPORTED_ALGORITHMS));
         }

         if (ByteUtils.safeBitLength(this.getKey().getEncoded()) != 256) {
            throw new KeyLengthException("The Key Encryption Key (KEK) length must be 256 bits for A256GCMKW encryption");
         }

         algFamily = AESEncrypter.AlgFamily.AESGCMKW;
      }

      SecretKey cek = this.getCEK(enc);
      JWEHeader updatedHeader;
      Base64URL encryptedKey;
      if (AESEncrypter.AlgFamily.AESKW.equals(algFamily)) {
         encryptedKey = Base64URL.encode(AESKW.wrapCEK(cek, this.getKey(), this.getJCAContext().getKeyEncryptionProvider()));
         updatedHeader = header;
      } else {
         assert AESEncrypter.AlgFamily.AESGCMKW.equals(algFamily);

         Container<byte[]> keyIV = new Container<>(AESGCM.generateIV(this.getJCAContext().getSecureRandom()));
         AuthenticatedCipherText authCiphCEK = AESGCMKW.encryptCEK(cek, keyIV, this.getKey(), this.getJCAContext().getKeyEncryptionProvider());
         encryptedKey = Base64URL.encode(authCiphCEK.getCipherText());
         updatedHeader = new JWEHeader.Builder(header).iv(Base64URL.encode(keyIV.get())).authTag(Base64URL.encode(authCiphCEK.getAuthenticationTag())).build();
      }

      byte[] updatedAAD = Arrays.equals(AAD.compute(header), aad) ? AAD.compute(updatedHeader) : aad;
      return ContentCryptoProvider.encrypt(updatedHeader, clearText, updatedAAD, cek, encryptedKey, this.getJCAContext());
   }

   private static enum AlgFamily {
      AESKW,
      AESGCMKW;
   }
}
