package com.nimbusds.jose.mint;

import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import com.nimbusds.jose.produce.JWSSignerFactory;

public interface JWSMinterConfiguration<C extends SecurityContext> {
   JWKSource<C> getJWKSource();

   void setJWKSource(JWKSource<C> var1);

   JWSSignerFactory getJWSSignerFactory();

   void setJWSSignerFactory(JWSSignerFactory var1);
}
