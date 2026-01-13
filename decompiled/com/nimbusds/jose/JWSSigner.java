package com.nimbusds.jose;

import com.nimbusds.jose.util.Base64URL;

public interface JWSSigner extends JWSProvider {
   Base64URL sign(JWSHeader var1, byte[] var2) throws JOSEException;
}
