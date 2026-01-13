package com.nimbusds.jose.crypto.impl;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.jwk.Curve;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

public abstract class ECDSAProvider extends BaseJWSProvider {
   public static final Set<JWSAlgorithm> SUPPORTED_ALGORITHMS;
   public static final Set<Curve> SUPPORTED_CURVES;

   protected ECDSAProvider(JWSAlgorithm alg) throws JOSEException {
      super(Collections.singleton(alg));
      if (!SUPPORTED_ALGORITHMS.contains(alg)) {
         throw new JOSEException("Unsupported EC DSA algorithm: " + alg);
      }
   }

   public JWSAlgorithm supportedECDSAAlgorithm() {
      return this.supportedJWSAlgorithms().iterator().next();
   }

   static {
      Set<JWSAlgorithm> algs = new LinkedHashSet<>();
      algs.add(JWSAlgorithm.ES256);
      algs.add(JWSAlgorithm.ES256K);
      algs.add(JWSAlgorithm.ES384);
      algs.add(JWSAlgorithm.ES512);
      SUPPORTED_ALGORITHMS = Collections.unmodifiableSet(algs);
      Set<Curve> curves = new LinkedHashSet<>();
      curves.add(Curve.P_256);
      curves.add(Curve.SECP256K1);
      curves.add(Curve.P_384);
      curves.add(Curve.P_521);
      SUPPORTED_CURVES = Collections.unmodifiableSet(curves);
   }
}
