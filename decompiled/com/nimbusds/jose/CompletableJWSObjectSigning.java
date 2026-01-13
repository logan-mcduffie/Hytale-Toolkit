package com.nimbusds.jose;

import com.nimbusds.jose.util.Base64URL;
import java.security.Signature;

public interface CompletableJWSObjectSigning {
   Signature getInitializedSignature();

   Base64URL complete() throws JOSEException;
}
