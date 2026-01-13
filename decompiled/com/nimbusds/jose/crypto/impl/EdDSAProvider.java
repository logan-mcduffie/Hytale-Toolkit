package com.nimbusds.jose.crypto.impl;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.jwk.Curve;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public abstract class EdDSAProvider extends BaseJWSProvider {
   public static final Set<JWSAlgorithm> SUPPORTED_ALGORITHMS = Collections.unmodifiableSet(
      new HashSet<>(Arrays.asList(JWSAlgorithm.EdDSA, JWSAlgorithm.Ed25519))
   );
   public static final Set<Curve> SUPPORTED_CURVES = Collections.singleton(Curve.Ed25519);

   protected EdDSAProvider() {
      super(SUPPORTED_ALGORITHMS);
   }
}
