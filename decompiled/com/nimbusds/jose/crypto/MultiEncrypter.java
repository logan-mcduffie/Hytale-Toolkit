package com.nimbusds.jose.crypto;

import com.nimbusds.jose.EncryptionMethod;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWEAlgorithm;
import com.nimbusds.jose.JWECryptoParts;
import com.nimbusds.jose.JWEEncrypter;
import com.nimbusds.jose.JWEHeader;
import com.nimbusds.jose.KeyLengthException;
import com.nimbusds.jose.Payload;
import com.nimbusds.jose.UnprotectedHeader;
import com.nimbusds.jose.crypto.impl.AAD;
import com.nimbusds.jose.crypto.impl.JWEHeaderValidation;
import com.nimbusds.jose.crypto.impl.MultiCryptoProvider;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.KeyType;
import com.nimbusds.jose.shaded.jcip.ThreadSafe;
import com.nimbusds.jose.util.Base64URL;
import com.nimbusds.jose.util.JSONArrayUtils;
import com.nimbusds.jose.util.JSONObjectUtils;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.crypto.SecretKey;

@ThreadSafe
public class MultiEncrypter extends MultiCryptoProvider implements JWEEncrypter {
   private static final String[] RECIPIENT_HEADER_PARAMS = new String[]{"kid", "alg", "x5u", "x5t", "x5t#S256", "x5c"};
   private final JWKSet keys;

   public MultiEncrypter(JWKSet keys) throws KeyLengthException {
      this(keys, findDirectCEK(keys));
   }

   public MultiEncrypter(JWKSet keys, SecretKey contentEncryptionKey) throws KeyLengthException {
      super(contentEncryptionKey);

      for (JWK jwk : keys.getKeys()) {
         KeyType kty = jwk.getKeyType();
         if (jwk.getAlgorithm() == null) {
            throw new IllegalArgumentException("Each JWK must specify a key encryption algorithm");
         }

         JWEAlgorithm alg = JWEAlgorithm.parse(jwk.getAlgorithm().toString());
         if (JWEAlgorithm.DIR.equals(alg) && KeyType.OCT.equals(kty) && !jwk.toOctetSequenceKey().toSecretKey("AES").equals(contentEncryptionKey)) {
            throw new IllegalArgumentException("Bad CEK");
         }

         if ((!KeyType.RSA.equals(kty) || !RSAEncrypter.SUPPORTED_ALGORITHMS.contains(alg))
            && (!KeyType.EC.equals(kty) || !ECDHEncrypter.SUPPORTED_ALGORITHMS.contains(alg))
            && (!KeyType.OCT.equals(kty) || !AESEncrypter.SUPPORTED_ALGORITHMS.contains(alg))
            && (!KeyType.OCT.equals(kty) || !DirectEncrypter.SUPPORTED_ALGORITHMS.contains(alg))
            && (!KeyType.OKP.equals(kty) || !X25519Encrypter.SUPPORTED_ALGORITHMS.contains(alg))) {
            throw new IllegalArgumentException("Unsupported key encryption algorithm: " + alg);
         }
      }

      this.keys = keys;
   }

   private static SecretKey findDirectCEK(JWKSet keys) {
      if (keys != null) {
         for (JWK jwk : keys.getKeys()) {
            if (JWEAlgorithm.DIR.equals(jwk.getAlgorithm()) && KeyType.OCT.equals(jwk.getKeyType())) {
               return jwk.toOctetSequenceKey().toSecretKey("AES");
            }
         }
      }

      return null;
   }

   @Deprecated
   public JWECryptoParts encrypt(JWEHeader header, byte[] clearText) throws JOSEException {
      return this.encrypt(header, clearText, AAD.compute(header));
   }

   @Override
   public JWECryptoParts encrypt(JWEHeader header, byte[] clearText, byte[] aad) throws JOSEException {
      if (aad == null) {
         throw new JOSEException("Missing JWE additional authenticated data (AAD)");
      } else {
         EncryptionMethod enc = header.getEncryptionMethod();
         SecretKey cek = this.getCEK(enc);
         JWEHeader recipientHeader = null;
         Base64URL encryptedKey = null;
         Base64URL cipherText = null;
         Base64URL iv = null;
         Base64URL tag = null;
         Payload payload = new Payload(clearText);
         List<Object> recipients = JSONArrayUtils.newJSONArray();
         Iterator jweJsonObject = this.keys.getKeys().iterator();

         while (true) {
            JWEEncrypter encrypter;
            JWEAlgorithm alg;
            while (true) {
               if (!jweJsonObject.hasNext()) {
                  if (recipients.size() > 1) {
                     Map<String, Object> jweJsonObjectx = JSONObjectUtils.newJSONObject();
                     jweJsonObjectx.put("recipients", recipients);
                     encryptedKey = Base64URL.encode(JSONObjectUtils.toJSONString(jweJsonObjectx));
                  }

                  return new JWECryptoParts(header, encryptedKey, iv, cipherText, tag);
               }

               JWK key = (JWK)jweJsonObject.next();
               KeyType kty = key.getKeyType();
               Map<String, Object> keyMap = key.toJSONObject();
               UnprotectedHeader.Builder unprotected = new UnprotectedHeader.Builder();

               for (String param : RECIPIENT_HEADER_PARAMS) {
                  if (keyMap.containsKey(param)) {
                     unprotected.param(param, keyMap.get(param));
                  }
               }

               try {
                  recipientHeader = (JWEHeader)header.join(unprotected.build());
               } catch (Exception var25) {
                  throw new JOSEException(var25.getMessage(), var25);
               }

               alg = JWEHeaderValidation.getAlgorithmAndEnsureNotNull(recipientHeader);
               if (KeyType.RSA.equals(kty) && RSAEncrypter.SUPPORTED_ALGORITHMS.contains(alg)) {
                  encrypter = new RSAEncrypter(key.toRSAKey().toRSAPublicKey(), cek);
                  break;
               }

               if (KeyType.EC.equals(kty) && ECDHEncrypter.SUPPORTED_ALGORITHMS.contains(alg)) {
                  encrypter = new ECDHEncrypter(key.toECKey().toECPublicKey(), cek);
                  break;
               }

               if (KeyType.OCT.equals(kty) && AESEncrypter.SUPPORTED_ALGORITHMS.contains(alg)) {
                  encrypter = new AESEncrypter(key.toOctetSequenceKey().toSecretKey("AES"), cek);
                  break;
               }

               if (KeyType.OCT.equals(kty) && DirectEncrypter.SUPPORTED_ALGORITHMS.contains(alg)) {
                  encrypter = new DirectEncrypter(key.toOctetSequenceKey().toSecretKey("AES"));
                  break;
               }

               if (KeyType.OKP.equals(kty) && X25519Encrypter.SUPPORTED_ALGORITHMS.contains(alg)) {
                  encrypter = new X25519Encrypter(key.toOctetKeyPair().toPublicJWK(), cek);
                  break;
               }
            }

            JWECryptoParts jweParts = encrypter.encrypt(recipientHeader, payload.toBytes(), aad);
            Map<String, Object> recipientHeaderMap = jweParts.getHeader().toJSONObject();

            for (String paramx : header.getIncludedParams()) {
               recipientHeaderMap.remove(paramx);
            }

            Map<String, Object> recipient = JSONObjectUtils.newJSONObject();
            recipient.put("header", recipientHeaderMap);
            if (!JWEAlgorithm.DIR.equals(alg)) {
               recipient.put("encrypted_key", jweParts.getEncryptedKey().toString());
            }

            recipients.add(recipient);
            if (recipients.size() == 1) {
               payload = new Payload("");
               encryptedKey = jweParts.getEncryptedKey();
               iv = jweParts.getInitializationVector();
               cipherText = jweParts.getCipherText();
               tag = jweParts.getAuthenticationTag();
            }
         }
      }
   }
}
