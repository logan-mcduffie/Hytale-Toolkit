package com.nimbusds.jose.jwk;

import com.nimbusds.jose.Algorithm;
import com.nimbusds.jose.JWEAlgorithm;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.Requirement;
import com.nimbusds.jose.shaded.jcip.Immutable;
import com.nimbusds.jose.util.JSONStringUtils;
import java.io.Serializable;

@Immutable
public final class KeyType implements Serializable {
   private static final long serialVersionUID = 1L;
   private final String value;
   private final Requirement requirement;
   public static final KeyType EC = new KeyType("EC", Requirement.RECOMMENDED);
   public static final KeyType RSA = new KeyType("RSA", Requirement.REQUIRED);
   public static final KeyType OCT = new KeyType("oct", Requirement.OPTIONAL);
   public static final KeyType OKP = new KeyType("OKP", Requirement.OPTIONAL);

   public KeyType(String value, Requirement req) {
      if (value == null) {
         throw new IllegalArgumentException("The key type value must not be null");
      } else {
         this.value = value;
         this.requirement = req;
      }
   }

   public String getValue() {
      return this.value;
   }

   public Requirement getRequirement() {
      return this.requirement;
   }

   @Override
   public int hashCode() {
      return this.value.hashCode();
   }

   @Override
   public boolean equals(Object object) {
      return object instanceof KeyType && this.toString().equals(object.toString());
   }

   @Override
   public String toString() {
      return this.value;
   }

   public String toJSONString() {
      return JSONStringUtils.toJSONString(this.value);
   }

   public static KeyType parse(String s) {
      if (s == null) {
         throw new IllegalArgumentException("The key type to parse must not be null");
      } else if (s.equals(EC.getValue())) {
         return EC;
      } else if (s.equals(RSA.getValue())) {
         return RSA;
      } else if (s.equals(OCT.getValue())) {
         return OCT;
      } else {
         return s.equals(OKP.getValue()) ? OKP : new KeyType(s, null);
      }
   }

   public static KeyType forAlgorithm(Algorithm alg) {
      if (alg == null) {
         return null;
      } else if (JWSAlgorithm.Family.RSA.contains(alg)) {
         return RSA;
      } else if (JWSAlgorithm.Family.EC.contains(alg)) {
         return EC;
      } else if (JWSAlgorithm.Family.HMAC_SHA.contains(alg)) {
         return OCT;
      } else if (JWEAlgorithm.Family.RSA.contains(alg)) {
         return RSA;
      } else if (JWEAlgorithm.Family.ECDH_ES.contains(alg)) {
         return EC;
      } else if (JWEAlgorithm.DIR.equals(alg)) {
         return OCT;
      } else if (JWEAlgorithm.Family.AES_GCM_KW.contains(alg)) {
         return OCT;
      } else if (JWEAlgorithm.Family.AES_KW.contains(alg)) {
         return OCT;
      } else if (JWEAlgorithm.Family.PBES2.contains(alg)) {
         return OCT;
      } else {
         return JWSAlgorithm.Family.ED.contains(alg) ? OKP : null;
      }
   }
}
