package com.nimbusds.jwt.proc;

import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.KeySourceException;
import com.nimbusds.jose.proc.SecurityContext;
import com.nimbusds.jwt.JWTClaimsSet;
import java.security.Key;
import java.util.List;

public interface JWTClaimsSetAwareJWSKeySelector<C extends SecurityContext> {
   List<? extends Key> selectKeys(JWSHeader var1, JWTClaimsSet var2, C var3) throws KeySourceException;
}
