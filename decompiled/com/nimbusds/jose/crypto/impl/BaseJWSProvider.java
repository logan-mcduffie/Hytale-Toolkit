package com.nimbusds.jose.crypto.impl;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSProvider;
import com.nimbusds.jose.jca.JCAContext;
import java.util.Collections;
import java.util.Objects;
import java.util.Set;

public abstract class BaseJWSProvider implements JWSProvider {
   private final Set<JWSAlgorithm> algs;
   private final JCAContext jcaContext = new JCAContext();

   public BaseJWSProvider(Set<JWSAlgorithm> algs) {
      this.algs = Collections.unmodifiableSet(Objects.requireNonNull(algs));
   }

   @Override
   public Set<JWSAlgorithm> supportedJWSAlgorithms() {
      return this.algs;
   }

   @Override
   public JCAContext getJCAContext() {
      return this.jcaContext;
   }
}
