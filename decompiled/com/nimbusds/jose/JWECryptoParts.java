package com.nimbusds.jose;

import com.nimbusds.jose.shaded.jcip.Immutable;
import com.nimbusds.jose.util.Base64URL;
import java.util.Objects;

@Immutable
public final class JWECryptoParts {
   private final JWEHeader header;
   private final Base64URL encryptedKey;
   private final Base64URL iv;
   private final Base64URL cipherText;
   private final Base64URL authenticationTag;

   public JWECryptoParts(Base64URL encryptedKey, Base64URL iv, Base64URL cipherText, Base64URL authenticationTag) {
      this(null, encryptedKey, iv, cipherText, authenticationTag);
   }

   public JWECryptoParts(JWEHeader header, Base64URL encryptedKey, Base64URL iv, Base64URL cipherText, Base64URL authenticationTag) {
      this.header = header;
      this.encryptedKey = encryptedKey;
      this.iv = iv;
      this.cipherText = Objects.requireNonNull(cipherText);
      this.authenticationTag = authenticationTag;
   }

   public JWEHeader getHeader() {
      return this.header;
   }

   public Base64URL getEncryptedKey() {
      return this.encryptedKey;
   }

   public Base64URL getInitializationVector() {
      return this.iv;
   }

   public Base64URL getCipherText() {
      return this.cipherText;
   }

   public Base64URL getAuthenticationTag() {
      return this.authenticationTag;
   }
}
