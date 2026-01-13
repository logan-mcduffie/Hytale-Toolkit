package com.nimbusds.jose.crypto.impl;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWEAlgorithm;
import com.nimbusds.jose.JWEHeader;

public class JWEHeaderValidation {
   public static JWEAlgorithm getAlgorithmAndEnsureNotNull(JWEHeader jweHeader) throws JOSEException {
      JWEAlgorithm alg = jweHeader.getAlgorithm();
      if (alg == null) {
         throw new JOSEException("The algorithm \"alg\" header parameter must not be null");
      } else {
         return alg;
      }
   }

   private JWEHeaderValidation() {
   }
}
