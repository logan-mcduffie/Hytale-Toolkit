package com.nimbusds.jose.proc;

import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.shaded.jcip.ThreadSafe;
import java.util.Objects;

@ThreadSafe
abstract class AbstractJWKSelectorWithSource<C extends SecurityContext> {
   private final JWKSource<C> jwkSource;

   public AbstractJWKSelectorWithSource(JWKSource<C> jwkSource) {
      this.jwkSource = Objects.requireNonNull(jwkSource);
   }

   public JWKSource<C> getJWKSource() {
      return this.jwkSource;
   }
}
