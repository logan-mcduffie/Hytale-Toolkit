package com.nimbusds.jose.jwk.source;

import com.nimbusds.jose.jwk.JWKSet;
import java.util.Objects;

class ReferenceComparisonRefreshJWKSetEvaluator extends JWKSetCacheRefreshEvaluator {
   private final JWKSet jwkSet;

   public ReferenceComparisonRefreshJWKSetEvaluator(JWKSet jwkSet) {
      this.jwkSet = jwkSet;
   }

   @Override
   public boolean requiresRefresh(JWKSet jwkSet) {
      return jwkSet == this.jwkSet;
   }

   @Override
   public int hashCode() {
      return Objects.hash(this.jwkSet);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      } else if (obj == null) {
         return false;
      } else if (this.getClass() != obj.getClass()) {
         return false;
      } else {
         ReferenceComparisonRefreshJWKSetEvaluator other = (ReferenceComparisonRefreshJWKSetEvaluator)obj;
         return Objects.equals(this.jwkSet, other.jwkSet);
      }
   }
}
