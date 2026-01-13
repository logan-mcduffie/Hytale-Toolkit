package com.nimbusds.jose.jwk.source;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.shaded.jcip.Immutable;
import java.util.Date;
import java.util.Objects;

@Deprecated
@Immutable
public final class JWKSetWithTimestamp {
   private final JWKSet jwkSet;
   private final Date timestamp;

   public JWKSetWithTimestamp(JWKSet jwkSet) {
      this(jwkSet, new Date());
   }

   public JWKSetWithTimestamp(JWKSet jwkSet, Date timestamp) {
      this.jwkSet = Objects.requireNonNull(jwkSet);
      this.timestamp = Objects.requireNonNull(timestamp);
   }

   public JWKSet getJWKSet() {
      return this.jwkSet;
   }

   public Date getDate() {
      return this.timestamp;
   }
}
