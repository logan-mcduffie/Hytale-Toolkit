package com.nimbusds.jose.jwk.source;

import com.nimbusds.jose.KeySourceException;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.proc.SecurityContext;
import java.io.Closeable;

public interface JWKSetSource<C extends SecurityContext> extends Closeable {
   JWKSet getJWKSet(JWKSetCacheRefreshEvaluator var1, long var2, C var4) throws KeySourceException;
}
