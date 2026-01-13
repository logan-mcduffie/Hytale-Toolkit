package com.nimbusds.jose.crypto.impl;

import com.nimbusds.jose.EncryptionMethod;
import com.nimbusds.jose.JWEAlgorithm;
import com.nimbusds.jose.util.StandardCharset;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

public abstract class PasswordBasedCryptoProvider extends BaseJWEProvider {
   public static final Set<JWEAlgorithm> SUPPORTED_ALGORITHMS;
   public static final Set<EncryptionMethod> SUPPORTED_ENCRYPTION_METHODS = ContentCryptoProvider.SUPPORTED_ENCRYPTION_METHODS;
   private final byte[] password;

   protected PasswordBasedCryptoProvider(byte[] password) {
      super(SUPPORTED_ALGORITHMS, ContentCryptoProvider.SUPPORTED_ENCRYPTION_METHODS, null);
      if (password != null && password.length != 0) {
         this.password = password;
      } else {
         throw new IllegalArgumentException("The password must not be null or empty");
      }
   }

   public byte[] getPassword() {
      return this.password;
   }

   public String getPasswordString() {
      return new String(this.password, StandardCharset.UTF_8);
   }

   static {
      Set<JWEAlgorithm> algs = new LinkedHashSet<>();
      algs.add(JWEAlgorithm.PBES2_HS256_A128KW);
      algs.add(JWEAlgorithm.PBES2_HS384_A192KW);
      algs.add(JWEAlgorithm.PBES2_HS512_A256KW);
      SUPPORTED_ALGORITHMS = Collections.unmodifiableSet(algs);
   }
}
