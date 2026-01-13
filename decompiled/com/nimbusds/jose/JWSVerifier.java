package com.nimbusds.jose;

import com.nimbusds.jose.util.Base64URL;

public interface JWSVerifier extends JWSProvider {
   boolean verify(JWSHeader var1, byte[] var2, Base64URL var3) throws JOSEException;
}
