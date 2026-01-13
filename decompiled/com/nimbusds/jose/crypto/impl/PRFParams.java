package com.nimbusds.jose.crypto.impl;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWEAlgorithm;
import com.nimbusds.jose.shaded.jcip.Immutable;
import java.security.Provider;
import java.util.Objects;

@Immutable
public final class PRFParams {
   private final String jcaMacAlg;
   private final Provider macProvider;
   private final int dkLen;

   public PRFParams(String jcaMacAlg, Provider macProvider, int dkLen) {
      this.jcaMacAlg = Objects.requireNonNull(jcaMacAlg);
      this.macProvider = macProvider;
      this.dkLen = dkLen;
   }

   public String getMACAlgorithm() {
      return this.jcaMacAlg;
   }

   public Provider getMacProvider() {
      return this.macProvider;
   }

   public int getDerivedKeyByteLength() {
      return this.dkLen;
   }

   public static PRFParams resolve(JWEAlgorithm alg, Provider macProvider) throws JOSEException {
      String jcaMagAlg;
      int dkLen;
      if (JWEAlgorithm.PBES2_HS256_A128KW.equals(alg)) {
         jcaMagAlg = "HmacSHA256";
         dkLen = 16;
      } else if (JWEAlgorithm.PBES2_HS384_A192KW.equals(alg)) {
         jcaMagAlg = "HmacSHA384";
         dkLen = 24;
      } else {
         if (!JWEAlgorithm.PBES2_HS512_A256KW.equals(alg)) {
            throw new JOSEException(AlgorithmSupportMessage.unsupportedJWEAlgorithm(alg, PasswordBasedCryptoProvider.SUPPORTED_ALGORITHMS));
         }

         jcaMagAlg = "HmacSHA512";
         dkLen = 32;
      }

      return new PRFParams(jcaMagAlg, macProvider, dkLen);
   }
}
