package com.nimbusds.jose.mint;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSObject;
import com.nimbusds.jose.Payload;
import com.nimbusds.jose.proc.SecurityContext;

public interface JWSMinter<C extends SecurityContext> {
   JWSObject mint(JWSHeader var1, Payload var2, C var3) throws JOSEException;
}
