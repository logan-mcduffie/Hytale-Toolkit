package com.nimbusds.jose.crypto.impl;

import java.security.AlgorithmParameters;
import java.security.NoSuchAlgorithmException;
import java.security.Provider;

public class AlgorithmParametersHelper {
   public static AlgorithmParameters getInstance(String name, Provider provider) throws NoSuchAlgorithmException {
      return provider == null ? AlgorithmParameters.getInstance(name) : AlgorithmParameters.getInstance(name, provider);
   }
}
