package org.bouncycastle.jcajce.provider.symmetric.util;

import java.security.AlgorithmParameterGeneratorSpi;
import java.security.AlgorithmParameters;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import org.bouncycastle.jcajce.util.BCJcaJceHelper;
import org.bouncycastle.jcajce.util.JcaJceHelper;

public abstract class BaseAlgorithmParameterGenerator extends AlgorithmParameterGeneratorSpi {
   private final JcaJceHelper helper = new BCJcaJceHelper();
   protected SecureRandom random;
   protected int strength = 1024;

   protected final AlgorithmParameters createParametersInstance(String var1) throws NoSuchAlgorithmException, NoSuchProviderException {
      return this.helper.createAlgorithmParameters(var1);
   }

   @Override
   protected void engineInit(int var1, SecureRandom var2) {
      this.strength = var1;
      this.random = var2;
   }
}
