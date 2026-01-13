package com.nimbusds.jose;

import com.nimbusds.jose.jca.JCAAware;
import com.nimbusds.jose.jca.JCAContext;
import java.util.Set;

public interface JWSProvider extends JOSEProvider, JCAAware<JCAContext> {
   Set<JWSAlgorithm> supportedJWSAlgorithms();
}
