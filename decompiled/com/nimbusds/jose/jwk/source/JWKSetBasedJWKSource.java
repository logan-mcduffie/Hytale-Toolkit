package com.nimbusds.jose.jwk.source;

import com.nimbusds.jose.KeySourceException;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSelector;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.proc.SecurityContext;
import com.nimbusds.jose.shaded.jcip.ThreadSafe;
import java.io.Closeable;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

@ThreadSafe
public class JWKSetBasedJWKSource<C extends SecurityContext> implements JWKSource<C>, Closeable {
   private final JWKSetSource<C> source;

   public JWKSetBasedJWKSource(JWKSetSource<C> source) {
      Objects.requireNonNull(source);
      this.source = source;
   }

   @Override
   public List<JWK> get(JWKSelector jwkSelector, C context) throws KeySourceException {
      long currentTime = System.currentTimeMillis();
      JWKSet jwkSet = this.source.getJWKSet(JWKSetCacheRefreshEvaluator.noRefresh(), currentTime, context);
      List<JWK> select = jwkSelector.select(jwkSet);
      if (select.isEmpty()) {
         JWKSet recentJwkSet = this.source.getJWKSet(JWKSetCacheRefreshEvaluator.referenceComparison(jwkSet), currentTime, context);
         select = jwkSelector.select(recentJwkSet);
      }

      return select;
   }

   public JWKSetSource<C> getJWKSetSource() {
      return this.source;
   }

   @Override
   public void close() throws IOException {
      this.source.close();
   }
}
