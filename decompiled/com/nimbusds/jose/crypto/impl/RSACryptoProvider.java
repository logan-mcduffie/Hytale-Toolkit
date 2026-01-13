package com.nimbusds.jose.crypto.impl;

import com.nimbusds.jose.EncryptionMethod;
import com.nimbusds.jose.JWEAlgorithm;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;
import javax.crypto.SecretKey;

public abstract class RSACryptoProvider extends BaseJWEProvider {
   public static final Set<JWEAlgorithm> SUPPORTED_ALGORITHMS;
   public static final Set<EncryptionMethod> SUPPORTED_ENCRYPTION_METHODS = ContentCryptoProvider.SUPPORTED_ENCRYPTION_METHODS;

   protected RSACryptoProvider(SecretKey cek) {
      super(SUPPORTED_ALGORITHMS, ContentCryptoProvider.SUPPORTED_ENCRYPTION_METHODS, cek);
   }

   static {
      Set<JWEAlgorithm> algs = new LinkedHashSet<>();
      algs.add(JWEAlgorithm.RSA1_5);
      algs.add(JWEAlgorithm.RSA_OAEP);
      algs.add(JWEAlgorithm.RSA_OAEP_256);
      algs.add(JWEAlgorithm.RSA_OAEP_384);
      algs.add(JWEAlgorithm.RSA_OAEP_512);
      SUPPORTED_ALGORITHMS = Collections.unmodifiableSet(algs);
   }
}
