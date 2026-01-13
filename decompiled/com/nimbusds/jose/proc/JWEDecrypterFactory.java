package com.nimbusds.jose.proc;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWEDecrypter;
import com.nimbusds.jose.JWEHeader;
import com.nimbusds.jose.JWEProvider;
import java.security.Key;

public interface JWEDecrypterFactory extends JWEProvider {
   JWEDecrypter createJWEDecrypter(JWEHeader var1, Key var2) throws JOSEException;
}
