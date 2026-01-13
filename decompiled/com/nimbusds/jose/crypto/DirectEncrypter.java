package com.nimbusds.jose.crypto;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWEAlgorithm;
import com.nimbusds.jose.JWECryptoParts;
import com.nimbusds.jose.JWEEncrypter;
import com.nimbusds.jose.JWEHeader;
import com.nimbusds.jose.KeyLengthException;
import com.nimbusds.jose.crypto.impl.AAD;
import com.nimbusds.jose.crypto.impl.AlgorithmSupportMessage;
import com.nimbusds.jose.crypto.impl.ContentCryptoProvider;
import com.nimbusds.jose.crypto.impl.DirectCryptoProvider;
import com.nimbusds.jose.crypto.impl.JWEHeaderValidation;
import com.nimbusds.jose.jwk.OctetSequenceKey;
import com.nimbusds.jose.shaded.jcip.ThreadSafe;
import com.nimbusds.jose.util.Base64URL;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

@ThreadSafe
public class DirectEncrypter extends DirectCryptoProvider implements JWEEncrypter {
   public DirectEncrypter(SecretKey key) throws KeyLengthException {
      super(key);
   }

   public DirectEncrypter(byte[] keyBytes) throws KeyLengthException {
      this(new SecretKeySpec(keyBytes, "AES"));
   }

   public DirectEncrypter(OctetSequenceKey octJWK) throws KeyLengthException {
      this(octJWK.toSecretKey("AES"));
   }

   @Deprecated
   public JWECryptoParts encrypt(JWEHeader header, byte[] clearText) throws JOSEException {
      return this.encrypt(header, clearText, AAD.compute(header));
   }

   @Override
   public JWECryptoParts encrypt(JWEHeader header, byte[] clearText, byte[] aad) throws JOSEException {
      JWEAlgorithm alg = JWEHeaderValidation.getAlgorithmAndEnsureNotNull(header);
      if (!alg.equals(JWEAlgorithm.DIR)) {
         throw new JOSEException(AlgorithmSupportMessage.unsupportedJWEAlgorithm(alg, SUPPORTED_ALGORITHMS));
      } else {
         Base64URL encryptedKey = null;
         return ContentCryptoProvider.encrypt(header, clearText, aad, this.getKey(), encryptedKey, this.getJCAContext());
      }
   }
}
