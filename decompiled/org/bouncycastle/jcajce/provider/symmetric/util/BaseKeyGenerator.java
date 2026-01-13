package org.bouncycastle.jcajce.provider.symmetric.util;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidParameterException;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;
import javax.crypto.KeyGeneratorSpi;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import org.bouncycastle.crypto.CipherKeyGenerator;
import org.bouncycastle.crypto.CryptoServicesRegistrar;
import org.bouncycastle.crypto.KeyGenerationParameters;

public class BaseKeyGenerator extends KeyGeneratorSpi {
   protected String algName;
   protected int keySize;
   protected int defaultKeySize;
   protected CipherKeyGenerator engine;
   protected boolean uninitialised = true;

   protected BaseKeyGenerator(String var1, int var2, CipherKeyGenerator var3) {
      this.algName = var1;
      this.keySize = this.defaultKeySize = var2;
      this.engine = var3;
   }

   @Override
   protected void engineInit(AlgorithmParameterSpec var1, SecureRandom var2) throws InvalidAlgorithmParameterException {
      throw new InvalidAlgorithmParameterException("Not Implemented");
   }

   @Override
   protected void engineInit(SecureRandom var1) {
      if (var1 != null) {
         this.engine.init(new KeyGenerationParameters(var1, this.defaultKeySize));
         this.uninitialised = false;
      }
   }

   @Override
   protected void engineInit(int var1, SecureRandom var2) {
      try {
         if (var2 == null) {
            var2 = CryptoServicesRegistrar.getSecureRandom();
         }

         this.engine.init(new KeyGenerationParameters(var2, var1));
         this.uninitialised = false;
      } catch (IllegalArgumentException var4) {
         throw new InvalidParameterException(var4.getMessage());
      }
   }

   @Override
   protected SecretKey engineGenerateKey() {
      if (this.uninitialised) {
         this.engine.init(new KeyGenerationParameters(CryptoServicesRegistrar.getSecureRandom(), this.defaultKeySize));
         this.uninitialised = false;
      }

      return new SecretKeySpec(this.engine.generateKey(), this.algName);
   }
}
