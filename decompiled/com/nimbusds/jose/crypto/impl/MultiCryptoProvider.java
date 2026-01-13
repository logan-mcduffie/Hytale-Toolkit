package com.nimbusds.jose.crypto.impl;

import com.nimbusds.jose.EncryptionMethod;
import com.nimbusds.jose.JWEAlgorithm;
import com.nimbusds.jose.KeyLengthException;
import com.nimbusds.jose.jwk.Curve;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import javax.crypto.SecretKey;

public abstract class MultiCryptoProvider extends BaseJWEProvider {
   public static final Set<JWEAlgorithm> SUPPORTED_ALGORITHMS;
   public static final Set<EncryptionMethod> SUPPORTED_ENCRYPTION_METHODS = ContentCryptoProvider.SUPPORTED_ENCRYPTION_METHODS;
   public static final Map<Integer, Set<JWEAlgorithm>> COMPATIBLE_ALGORITHMS;
   public static final Set<Curve> SUPPORTED_ELLIPTIC_CURVES;

   public Set<Curve> supportedEllipticCurves() {
      return SUPPORTED_ELLIPTIC_CURVES;
   }

   protected MultiCryptoProvider(SecretKey cek) throws KeyLengthException {
      super(SUPPORTED_ALGORITHMS, ContentCryptoProvider.SUPPORTED_ENCRYPTION_METHODS, cek);
   }

   static {
      Set<JWEAlgorithm> algs = new LinkedHashSet<>();
      algs.add(null);
      algs.add(JWEAlgorithm.A128KW);
      algs.add(JWEAlgorithm.A192KW);
      algs.add(JWEAlgorithm.A256KW);
      algs.add(JWEAlgorithm.A128GCMKW);
      algs.add(JWEAlgorithm.A192GCMKW);
      algs.add(JWEAlgorithm.A256GCMKW);
      algs.add(JWEAlgorithm.DIR);
      algs.add(JWEAlgorithm.ECDH_ES_A128KW);
      algs.add(JWEAlgorithm.ECDH_ES_A192KW);
      algs.add(JWEAlgorithm.ECDH_ES_A256KW);
      algs.add(JWEAlgorithm.RSA1_5);
      algs.add(JWEAlgorithm.RSA_OAEP);
      algs.add(JWEAlgorithm.RSA_OAEP_256);
      algs.add(JWEAlgorithm.RSA_OAEP_384);
      algs.add(JWEAlgorithm.RSA_OAEP_512);
      SUPPORTED_ALGORITHMS = Collections.unmodifiableSet(algs);
      Map<Integer, Set<JWEAlgorithm>> algsMap = new HashMap<>();
      Set<JWEAlgorithm> bit128Algs = new HashSet<>();
      Set<JWEAlgorithm> bit192Algs = new HashSet<>();
      Set<JWEAlgorithm> bit256Algs = new HashSet<>();
      bit128Algs.add(JWEAlgorithm.A128GCMKW);
      bit128Algs.add(JWEAlgorithm.A128KW);
      bit192Algs.add(JWEAlgorithm.A192GCMKW);
      bit192Algs.add(JWEAlgorithm.A192KW);
      bit256Algs.add(JWEAlgorithm.A256GCMKW);
      bit256Algs.add(JWEAlgorithm.A256KW);
      algsMap.put(128, Collections.unmodifiableSet(bit128Algs));
      algsMap.put(192, Collections.unmodifiableSet(bit192Algs));
      algsMap.put(256, Collections.unmodifiableSet(bit256Algs));
      COMPATIBLE_ALGORITHMS = Collections.unmodifiableMap(algsMap);
      Set<Curve> curves = new LinkedHashSet<>();
      curves.add(Curve.P_256);
      curves.add(Curve.P_384);
      curves.add(Curve.P_521);
      curves.add(Curve.X25519);
      SUPPORTED_ELLIPTIC_CURVES = Collections.unmodifiableSet(curves);
   }
}
