package com.nimbusds.jose.proc;

import com.nimbusds.jose.jwk.JWK;
import java.util.List;
import java.util.Objects;

public class JWKSecurityContext implements SecurityContext {
   private final List<JWK> keys;

   public JWKSecurityContext(List<JWK> keys) {
      this.keys = Objects.requireNonNull(keys);
   }

   public List<JWK> getKeys() {
      return this.keys;
   }
}
