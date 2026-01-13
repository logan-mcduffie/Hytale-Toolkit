package com.nimbusds.jose.jwk.source;

import com.nimbusds.jose.jwk.JWKSet;

@Deprecated
public interface JWKSetCache {
   void put(JWKSet var1);

   JWKSet get();

   boolean requiresRefresh();
}
