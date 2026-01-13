package com.nimbusds.jose.jwk;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.shaded.jcip.Immutable;
import java.io.Serializable;
import java.security.spec.ECParameterSpec;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Immutable
public final class Curve implements Serializable {
   private static final long serialVersionUID = 1L;
   public static final Curve P_256 = new Curve("P-256", "secp256r1", "1.2.840.10045.3.1.7");
   public static final Curve SECP256K1 = new Curve("secp256k1", "secp256k1", "1.3.132.0.10");
   @Deprecated
   public static final Curve P_256K = new Curve("P-256K", "secp256k1", "1.3.132.0.10");
   public static final Curve P_384 = new Curve("P-384", "secp384r1", "1.3.132.0.34");
   public static final Curve P_521 = new Curve("P-521", "secp521r1", "1.3.132.0.35");
   public static final Curve Ed25519 = new Curve("Ed25519", "Ed25519", null);
   public static final Curve Ed448 = new Curve("Ed448", "Ed448", null);
   public static final Curve X25519 = new Curve("X25519", "X25519", null);
   public static final Curve X448 = new Curve("X448", "X448", null);
   private final String name;
   private final String stdName;
   private final String oid;

   public Curve(String name) {
      this(name, null, null);
   }

   public Curve(String name, String stdName, String oid) {
      this.name = Objects.requireNonNull(name);
      this.stdName = stdName;
      this.oid = oid;
   }

   public String getName() {
      return this.name;
   }

   public String getStdName() {
      return this.stdName;
   }

   public String getOID() {
      return this.oid;
   }

   public ECParameterSpec toECParameterSpec() {
      return ECParameterTable.get(this);
   }

   @Override
   public String toString() {
      return this.getName();
   }

   @Override
   public boolean equals(Object object) {
      return object instanceof Curve && this.toString().equals(object.toString());
   }

   @Override
   public int hashCode() {
      return Objects.hash(this.getName());
   }

   public static Curve parse(String s) {
      if (s == null || s.trim().isEmpty()) {
         throw new IllegalArgumentException("The cryptographic curve string must not be null or empty");
      } else if (s.equals(P_256.getName())) {
         return P_256;
      } else if (s.equals(P_256K.getName())) {
         return P_256K;
      } else if (s.equals(SECP256K1.getName())) {
         return SECP256K1;
      } else if (s.equals(P_384.getName())) {
         return P_384;
      } else if (s.equals(P_521.getName())) {
         return P_521;
      } else if (s.equals(Ed25519.getName())) {
         return Ed25519;
      } else if (s.equals(Ed448.getName())) {
         return Ed448;
      } else if (s.equals(X25519.getName())) {
         return X25519;
      } else {
         return s.equals(X448.getName()) ? X448 : new Curve(s);
      }
   }

   public static Curve forStdName(String stdName) {
      if ("secp256r1".equals(stdName) || "prime256v1".equals(stdName)) {
         return P_256;
      } else if ("secp256k1".equals(stdName)) {
         return SECP256K1;
      } else if ("secp384r1".equals(stdName)) {
         return P_384;
      } else if ("secp521r1".equals(stdName)) {
         return P_521;
      } else if (Ed25519.getStdName().equals(stdName)) {
         return Ed25519;
      } else if (Ed448.getStdName().equals(stdName)) {
         return Ed448;
      } else if (X25519.getStdName().equals(stdName)) {
         return X25519;
      } else {
         return X448.getStdName().equals(stdName) ? X448 : null;
      }
   }

   public static Curve forOID(String oid) {
      if (P_256.getOID().equals(oid)) {
         return P_256;
      } else if (SECP256K1.getOID().equals(oid)) {
         return SECP256K1;
      } else if (P_384.getOID().equals(oid)) {
         return P_384;
      } else {
         return P_521.getOID().equals(oid) ? P_521 : null;
      }
   }

   public static Set<Curve> forJWSAlgorithm(JWSAlgorithm alg) {
      if (JWSAlgorithm.ES256.equals(alg)) {
         return Collections.singleton(P_256);
      } else if (JWSAlgorithm.ES256K.equals(alg)) {
         return Collections.singleton(SECP256K1);
      } else if (JWSAlgorithm.ES384.equals(alg)) {
         return Collections.singleton(P_384);
      } else if (JWSAlgorithm.ES512.equals(alg)) {
         return Collections.singleton(P_521);
      } else {
         return JWSAlgorithm.EdDSA.equals(alg) ? Collections.unmodifiableSet(new HashSet<>(Arrays.asList(Ed25519, Ed448))) : null;
      }
   }

   public static Curve forECParameterSpec(ECParameterSpec spec) {
      return ECParameterTable.get(spec);
   }
}
