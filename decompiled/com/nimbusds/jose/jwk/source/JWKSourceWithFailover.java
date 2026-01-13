package com.nimbusds.jose.jwk.source;

import com.nimbusds.jose.KeySourceException;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSelector;
import com.nimbusds.jose.proc.SecurityContext;
import com.nimbusds.jose.shaded.jcip.ThreadSafe;
import com.nimbusds.jose.util.IOUtils;
import java.io.Closeable;
import java.util.List;
import java.util.Objects;

@ThreadSafe
public class JWKSourceWithFailover<C extends SecurityContext> implements JWKSource<C>, Closeable {
   private final JWKSource<C> jwkSource;
   private final JWKSource<C> failoverJWKSource;

   public JWKSourceWithFailover(JWKSource<C> jwkSource, JWKSource<C> failoverJWKSource) {
      Objects.requireNonNull(jwkSource, "The primary JWK source must not be null");
      this.jwkSource = jwkSource;
      this.failoverJWKSource = failoverJWKSource;
   }

   private List<JWK> failover(Exception exception, JWKSelector jwkSelector, C context) throws KeySourceException {
      try {
         return this.failoverJWKSource.get(jwkSelector, context);
      } catch (KeySourceException var5) {
         throw new KeySourceException(exception.getMessage() + "; Failover JWK source retrieval failed with: " + var5.getMessage(), var5);
      }
   }

   @Override
   public List<JWK> get(JWKSelector jwkSelector, C context) throws KeySourceException {
      try {
         return this.jwkSource.get(jwkSelector, context);
      } catch (Exception var4) {
         return this.failover(var4, jwkSelector, context);
      }
   }

   @Override
   public void close() {
      if (this.jwkSource instanceof Closeable) {
         IOUtils.closeSilently((Closeable)this.jwkSource);
      }

      if (this.failoverJWKSource instanceof Closeable) {
         IOUtils.closeSilently((Closeable)this.failoverJWKSource);
      }
   }
}
