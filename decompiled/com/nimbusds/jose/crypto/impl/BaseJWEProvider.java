package com.nimbusds.jose.crypto.impl;

import com.nimbusds.jose.EncryptionMethod;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWEAlgorithm;
import com.nimbusds.jose.JWEProvider;
import com.nimbusds.jose.jca.JWEJCAContext;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import javax.crypto.SecretKey;

public abstract class BaseJWEProvider implements JWEProvider {
   private static final Set<String> ACCEPTABLE_CEK_ALGS = Collections.unmodifiableSet(new HashSet<>(Arrays.asList("AES", "ChaCha20")));
   private final Set<JWEAlgorithm> algs;
   private final Set<EncryptionMethod> encs;
   private final JWEJCAContext jcaContext = new JWEJCAContext();
   private final SecretKey cek;

   public BaseJWEProvider(Set<JWEAlgorithm> algs, Set<EncryptionMethod> encs) {
      this(algs, encs, null);
   }

   public BaseJWEProvider(Set<JWEAlgorithm> algs, Set<EncryptionMethod> encs, SecretKey cek) {
      if (algs == null) {
         throw new IllegalArgumentException("The supported JWE algorithm set must not be null");
      } else {
         this.algs = Collections.unmodifiableSet(algs);
         if (encs == null) {
            throw new IllegalArgumentException("The supported encryption methods must not be null");
         } else {
            this.encs = encs;
            if (cek == null || algs.size() <= 1 || cek.getAlgorithm() != null && ACCEPTABLE_CEK_ALGS.contains(cek.getAlgorithm())) {
               this.cek = cek;
            } else {
               throw new IllegalArgumentException("The algorithm of the content encryption key (CEK) must be AES or ChaCha20");
            }
         }
      }
   }

   @Override
   public Set<JWEAlgorithm> supportedJWEAlgorithms() {
      return this.algs;
   }

   @Override
   public Set<EncryptionMethod> supportedEncryptionMethods() {
      return this.encs;
   }

   public JWEJCAContext getJCAContext() {
      return this.jcaContext;
   }

   protected boolean isCEKProvided() {
      return this.cek != null;
   }

   protected SecretKey getCEK(EncryptionMethod enc) throws JOSEException {
      return !this.isCEKProvided() && enc != null ? ContentCryptoProvider.generateCEK(enc, this.jcaContext.getSecureRandom()) : this.cek;
   }
}
