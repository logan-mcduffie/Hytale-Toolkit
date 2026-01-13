package com.nimbusds.jwt;

public interface JWTClaimsSetTransformer<T> {
   T transform(JWTClaimsSet var1);
}
