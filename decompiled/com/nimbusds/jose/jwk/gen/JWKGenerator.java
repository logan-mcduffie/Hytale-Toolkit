package com.nimbusds.jose.jwk.gen;

import com.nimbusds.jose.Algorithm;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.KeyOperation;
import com.nimbusds.jose.jwk.KeyUse;
import java.security.KeyStore;
import java.security.Provider;
import java.security.SecureRandom;
import java.util.Date;
import java.util.Set;

public abstract class JWKGenerator<T extends JWK> {
   protected KeyUse use;
   protected Set<KeyOperation> ops;
   protected Algorithm alg;
   protected String kid;
   protected boolean tprKid;
   protected Date exp;
   protected Date nbf;
   protected Date iat;
   protected KeyStore keyStore;
   protected Provider provider;
   protected SecureRandom secureRandom;

   public JWKGenerator<T> keyUse(KeyUse use) {
      this.use = use;
      return this;
   }

   public JWKGenerator<T> keyOperations(Set<KeyOperation> ops) {
      this.ops = ops;
      return this;
   }

   public JWKGenerator<T> algorithm(Algorithm alg) {
      this.alg = alg;
      return this;
   }

   public JWKGenerator<T> keyID(String kid) {
      this.kid = kid;
      return this;
   }

   public JWKGenerator<T> keyIDFromThumbprint(boolean tprKid) {
      this.tprKid = tprKid;
      return this;
   }

   public JWKGenerator<T> expirationTime(Date exp) {
      this.exp = exp;
      return this;
   }

   public JWKGenerator<T> notBeforeTime(Date nbf) {
      this.nbf = nbf;
      return this;
   }

   public JWKGenerator<T> issueTime(Date iat) {
      this.iat = iat;
      return this;
   }

   public JWKGenerator<T> keyStore(KeyStore keyStore) {
      this.keyStore = keyStore;
      return this;
   }

   public JWKGenerator<T> provider(Provider provider) {
      this.provider = provider;
      return this;
   }

   public JWKGenerator<T> secureRandom(SecureRandom secureRandom) {
      this.secureRandom = secureRandom;
      return this;
   }

   public abstract T generate() throws JOSEException;
}
