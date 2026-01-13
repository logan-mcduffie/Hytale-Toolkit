package com.nimbusds.jose.mint;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSObject;
import com.nimbusds.jose.Payload;
import com.nimbusds.jose.crypto.factories.DefaultJWSSignerFactory;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKMatcher;
import com.nimbusds.jose.jwk.JWKSelector;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.JWKSecurityContext;
import com.nimbusds.jose.proc.SecurityContext;
import com.nimbusds.jose.produce.JWSSignerFactory;
import java.util.List;

public class DefaultJWSMinter<C extends SecurityContext> implements ConfigurableJWSMinter<C> {
   private JWKSource<C> jwkSource;
   private JWSSignerFactory jwsSignerFactory = new DefaultJWSSignerFactory();

   @Override
   public JWSObject mint(JWSHeader header, Payload payload, C context) throws JOSEException {
      List<JWK> jwks = this.jwks(header, context);
      if (jwks.isEmpty()) {
         throw new JOSEException("No JWKs found for signing");
      } else {
         JWK jwk = jwks.get(0);
         JWSHeader withJwk = new JWSHeader.Builder(header)
            .keyID(jwk.getKeyID())
            .x509CertURL(jwk.getX509CertURL())
            .x509CertChain(jwk.getX509CertChain())
            .x509CertSHA256Thumbprint(jwk.getX509CertSHA256Thumbprint())
            .x509CertThumbprint(jwk.getX509CertThumbprint())
            .build();
         JWSObject jws = new JWSObject(withJwk, payload);
         if (this.jwsSignerFactory == null) {
            throw new JOSEException("No JWS signer factory configured");
         } else {
            jws.sign(this.jwsSignerFactory.createJWSSigner(jwk));
            return jws;
         }
      }
   }

   private List<JWK> jwks(JWSHeader header, C context) throws JOSEException {
      JWKMatcher matcher = JWKMatcher.forJWSHeader(header);
      JWKSelector selector = new JWKSelector(matcher);
      if (context instanceof JWKSecurityContext) {
         return selector.select(new JWKSet(((JWKSecurityContext)context).getKeys()));
      } else if (this.jwkSource == null) {
         throw new JOSEException("No JWK source configured");
      } else {
         return this.jwkSource.get(selector, context);
      }
   }

   @Override
   public JWKSource<C> getJWKSource() {
      return this.jwkSource;
   }

   @Override
   public void setJWKSource(JWKSource<C> jwkSource) {
      this.jwkSource = jwkSource;
   }

   @Override
   public JWSSignerFactory getJWSSignerFactory() {
      return this.jwsSignerFactory;
   }

   @Override
   public void setJWSSignerFactory(JWSSignerFactory jwsSignerFactory) {
      this.jwsSignerFactory = jwsSignerFactory;
   }
}
