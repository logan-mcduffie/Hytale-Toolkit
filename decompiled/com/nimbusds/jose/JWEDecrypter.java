package com.nimbusds.jose;

import com.nimbusds.jose.util.Base64URL;

public interface JWEDecrypter extends JWEProvider {
   byte[] decrypt(JWEHeader var1, Base64URL var2, Base64URL var3, Base64URL var4, Base64URL var5, byte[] var6) throws JOSEException;
}
