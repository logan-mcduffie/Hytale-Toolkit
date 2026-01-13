package com.nimbusds.jose;

public interface JWEEncrypter extends JWEProvider {
   JWECryptoParts encrypt(JWEHeader var1, byte[] var2, byte[] var3) throws JOSEException;
}
