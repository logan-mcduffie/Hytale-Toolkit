package com.nimbusds.jose.proc;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JOSEObject;
import com.nimbusds.jose.JWEObject;
import com.nimbusds.jose.JWSObject;
import com.nimbusds.jose.Payload;
import com.nimbusds.jose.PlainObject;
import java.text.ParseException;

public interface JOSEProcessor<C extends SecurityContext> {
   Payload process(String var1, C var2) throws ParseException, BadJOSEException, JOSEException;

   Payload process(JOSEObject var1, C var2) throws BadJOSEException, JOSEException;

   Payload process(PlainObject var1, C var2) throws BadJOSEException, JOSEException;

   Payload process(JWSObject var1, C var2) throws BadJOSEException, JOSEException;

   Payload process(JWEObject var1, C var2) throws BadJOSEException, JOSEException;
}
