package com.nimbusds.jose.jwk;

import com.nimbusds.jose.JOSEException;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.X509Certificate;

public interface AsymmetricJWK {
   PublicKey toPublicKey() throws JOSEException;

   PrivateKey toPrivateKey() throws JOSEException;

   KeyPair toKeyPair() throws JOSEException;

   boolean matches(X509Certificate var1);
}
