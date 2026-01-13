package com.nimbusds.jose.jwk.source;

import com.nimbusds.jose.KeySourceException;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSelector;
import com.nimbusds.jose.proc.SecurityContext;
import java.util.List;

public interface JWKSource<C extends SecurityContext> {
   List<JWK> get(JWKSelector var1, C var2) throws KeySourceException;
}
