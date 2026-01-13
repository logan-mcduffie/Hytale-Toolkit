package com.nimbusds.jose.proc;

import com.nimbusds.jose.JOSEObjectType;

public interface JOSEObjectTypeVerifier<C extends SecurityContext> {
   void verify(JOSEObjectType var1, C var2) throws BadJOSEException;
}
