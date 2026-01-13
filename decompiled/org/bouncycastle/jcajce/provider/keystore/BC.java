package org.bouncycastle.jcajce.provider.keystore;

import org.bouncycastle.jcajce.provider.config.ConfigurableProvider;
import org.bouncycastle.jcajce.provider.util.AsymmetricAlgorithmProvider;
import org.bouncycastle.util.Properties;

public class BC {
   private static final String PREFIX = "org.bouncycastle.jcajce.provider.keystore.bc.";

   public static class Mappings extends AsymmetricAlgorithmProvider {
      @Override
      public void configure(ConfigurableProvider var1) {
         var1.addAlgorithm("KeyStore.BKS", "org.bouncycastle.jcajce.provider.keystore.bc.BcKeyStoreSpi$Std");
         if (Properties.isOverrideSet("org.bouncycastle.bks.enable_v1")) {
            var1.addAlgorithm("KeyStore.BKS-V1", "org.bouncycastle.jcajce.provider.keystore.bc.BcKeyStoreSpi$Version1");
         }

         var1.addAlgorithm("KeyStore.BouncyCastle", "org.bouncycastle.jcajce.provider.keystore.bc.BcKeyStoreSpi$BouncyCastleStore");
         var1.addAlgorithm("Alg.Alias.KeyStore.UBER", "BouncyCastle");
         var1.addAlgorithm("Alg.Alias.KeyStore.BOUNCYCASTLE", "BouncyCastle");
         var1.addAlgorithm("Alg.Alias.KeyStore.bouncycastle", "BouncyCastle");
      }
   }
}
