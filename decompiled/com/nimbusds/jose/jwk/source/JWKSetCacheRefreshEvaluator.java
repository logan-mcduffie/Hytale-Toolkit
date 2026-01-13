package com.nimbusds.jose.jwk.source;

import com.nimbusds.jose.jwk.JWKSet;

public abstract class JWKSetCacheRefreshEvaluator {
   public static JWKSetCacheRefreshEvaluator forceRefresh() {
      return ForceRefreshJWKSetCacheEvaluator.getInstance();
   }

   public static JWKSetCacheRefreshEvaluator noRefresh() {
      return NoRefreshJWKSetCacheEvaluator.getInstance();
   }

   public static JWKSetCacheRefreshEvaluator referenceComparison(JWKSet jwtSet) {
      return new ReferenceComparisonRefreshJWKSetEvaluator(jwtSet);
   }

   public abstract boolean requiresRefresh(JWKSet var1);
}
