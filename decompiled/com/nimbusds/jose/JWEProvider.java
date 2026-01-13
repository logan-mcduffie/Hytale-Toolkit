package com.nimbusds.jose;

import com.nimbusds.jose.jca.JCAAware;
import com.nimbusds.jose.jca.JWEJCAContext;
import java.util.Set;

public interface JWEProvider extends JOSEProvider, JCAAware<JWEJCAContext> {
   Set<JWEAlgorithm> supportedJWEAlgorithms();

   Set<EncryptionMethod> supportedEncryptionMethods();
}
