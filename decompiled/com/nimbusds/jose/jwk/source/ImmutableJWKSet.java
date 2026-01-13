package com.nimbusds.jose.jwk.source;

import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSelector;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.proc.SecurityContext;
import com.nimbusds.jose.shaded.jcip.Immutable;
import java.util.List;
import java.util.Objects;

@Immutable
public class ImmutableJWKSet<C extends SecurityContext> implements JWKSource<C> {
   private final JWKSet jwkSet;

   public ImmutableJWKSet(JWKSet jwkSet) {
      this.jwkSet = Objects.requireNonNull(jwkSet);
   }

   public JWKSet getJWKSet() {
      return this.jwkSet;
   }

   @Override
   public List<JWK> get(JWKSelector jwkSelector, C context) {
      return jwkSelector.select(this.jwkSet);
   }
}
