package com.nimbusds.jwt.proc;

import com.nimbusds.jose.proc.SecurityContext;
import com.nimbusds.jwt.JWTClaimsSet;

public interface JWTClaimsSetVerifier<C extends SecurityContext> {
   void verify(JWTClaimsSet var1, C var2) throws BadJWTException;
}
