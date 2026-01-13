package com.google.crypto.tink.subtle;

import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.io.BufferedReader;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.interfaces.ECKey;
import java.security.interfaces.RSAKey;
import java.security.spec.ECParameterSpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import javax.annotation.Nullable;

@Deprecated
public enum PemKeyType {
   RSA_PSS_2048_SHA256("RSA", "RSASSA-PSS", 2048, Enums.HashType.SHA256),
   RSA_PSS_3072_SHA256("RSA", "RSASSA-PSS", 3072, Enums.HashType.SHA256),
   RSA_PSS_4096_SHA256("RSA", "RSASSA-PSS", 4096, Enums.HashType.SHA256),
   RSA_PSS_4096_SHA512("RSA", "RSASSA-PSS", 4096, Enums.HashType.SHA512),
   RSA_SIGN_PKCS1_2048_SHA256("RSA", "RSASSA-PKCS1-v1_5", 2048, Enums.HashType.SHA256),
   RSA_SIGN_PKCS1_3072_SHA256("RSA", "RSASSA-PKCS1-v1_5", 3072, Enums.HashType.SHA256),
   RSA_SIGN_PKCS1_4096_SHA256("RSA", "RSASSA-PKCS1-v1_5", 4096, Enums.HashType.SHA256),
   RSA_SIGN_PKCS1_4096_SHA512("RSA", "RSASSA-PKCS1-v1_5", 4096, Enums.HashType.SHA512),
   ECDSA_P256_SHA256("EC", "ECDSA", 256, Enums.HashType.SHA256),
   ECDSA_P384_SHA384("EC", "ECDSA", 384, Enums.HashType.SHA384),
   ECDSA_P521_SHA512("EC", "ECDSA", 521, Enums.HashType.SHA512);

   public final String keyType;
   public final String algorithm;
   public final int keySizeInBits;
   public final Enums.HashType hash;
   private static final String PUBLIC_KEY = "PUBLIC KEY";
   private static final String PRIVATE_KEY = "PRIVATE KEY";
   private static final String BEGIN = "-----BEGIN ";
   private static final String END = "-----END ";
   private static final String MARKER = "-----";

   private PemKeyType(String keyType, String algorithm, int keySizeInBits, Enums.HashType hash) {
      this.keyType = keyType;
      this.algorithm = algorithm;
      this.keySizeInBits = keySizeInBits;
      this.hash = hash;
   }

   @Nullable
   public Key readKey(BufferedReader reader) throws IOException {
      String line = reader.readLine();

      while (line != null && !line.startsWith("-----BEGIN ")) {
         line = reader.readLine();
      }

      if (line == null) {
         return null;
      } else {
         line = line.trim().substring("-----BEGIN ".length());
         int index = line.indexOf("-----");
         if (index < 0) {
            return null;
         } else {
            String type = line.substring(0, index);
            String endMarker = "-----END " + type + "-----";
            StringBuilder base64key = new StringBuilder();

            while ((line = reader.readLine()) != null) {
               if (line.indexOf(":") <= 0) {
                  if (line.contains(endMarker)) {
                     break;
                  }

                  base64key.append(line);
               }
            }

            try {
               byte[] key = Base64.decode(base64key.toString(), 0);
               if (type.contains("PUBLIC KEY")) {
                  return this.getPublicKey(key);
               } else {
                  return type.contains("PRIVATE KEY") ? this.getPrivateKey(key) : null;
               }
            } catch (IllegalArgumentException | GeneralSecurityException var8) {
               return null;
            }
         }
      }
   }

   private Key getPublicKey(final byte[] key) throws GeneralSecurityException {
      KeyFactory keyFactory = EngineFactory.KEY_FACTORY.getInstance(this.keyType);
      return this.validate(keyFactory.generatePublic(new X509EncodedKeySpec(key)));
   }

   private Key getPrivateKey(final byte[] key) throws GeneralSecurityException {
      KeyFactory keyFactory = EngineFactory.KEY_FACTORY.getInstance(this.keyType);
      return this.validate(keyFactory.generatePrivate(new PKCS8EncodedKeySpec(key)));
   }

   @CanIgnoreReturnValue
   private Key validate(Key key) throws GeneralSecurityException {
      if (this.keyType.equals("RSA")) {
         RSAKey rsaKey = (RSAKey)key;
         int foundKeySizeInBits = rsaKey.getModulus().bitLength();
         if (foundKeySizeInBits != this.keySizeInBits) {
            throw new GeneralSecurityException(String.format("invalid RSA key size, want %d got %d", this.keySizeInBits, foundKeySizeInBits));
         }
      } else {
         ECKey ecKey = (ECKey)key;
         ECParameterSpec ecParams = ecKey.getParams();
         if (!EllipticCurves.isNistEcParameterSpec(ecParams)) {
            throw new GeneralSecurityException("unsupport EC spec: " + ecParams.toString());
         }

         int foundKeySizeInBits = EllipticCurves.fieldSizeInBits(ecParams.getCurve());
         if (foundKeySizeInBits != this.keySizeInBits) {
            throw new GeneralSecurityException(String.format("invalid EC key size, want %d got %d", this.keySizeInBits, foundKeySizeInBits));
         }
      }

      return key;
   }
}
