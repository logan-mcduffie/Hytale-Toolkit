package org.bouncycastle.pqc.jcajce.provider;

import org.bouncycastle.asn1.bc.BCObjectIdentifiers;
import org.bouncycastle.jcajce.provider.config.ConfigurableProvider;
import org.bouncycastle.jcajce.provider.util.AsymmetricAlgorithmProvider;
import org.bouncycastle.pqc.jcajce.provider.bike.BIKEKeyFactorySpi;

public class BIKE {
   private static final String PREFIX = "org.bouncycastle.pqc.jcajce.provider.bike.";

   public static class Mappings extends AsymmetricAlgorithmProvider {
      @Override
      public void configure(ConfigurableProvider var1) {
         var1.addAlgorithm("KeyFactory.BIKE", "org.bouncycastle.pqc.jcajce.provider.bike.BIKEKeyFactorySpi");
         var1.addAlgorithm("KeyPairGenerator.BIKE", "org.bouncycastle.pqc.jcajce.provider.bike.BIKEKeyPairGeneratorSpi");
         var1.addAlgorithm("KeyGenerator.BIKE", "org.bouncycastle.pqc.jcajce.provider.bike.BIKEKeyGeneratorSpi");
         BIKEKeyFactorySpi var2 = new BIKEKeyFactorySpi();
         var1.addAlgorithm("Cipher.BIKE", "org.bouncycastle.pqc.jcajce.provider.bike.BIKECipherSpi$Base");
         var1.addAlgorithm("Alg.Alias.Cipher." + BCObjectIdentifiers.pqc_kem_bike, "BIKE");
         this.addCipherAlgorithm(var1, "BIKE128", "org.bouncycastle.pqc.jcajce.provider.bike.BIKECipherSpi$BIKE128", BCObjectIdentifiers.bike128);
         this.addCipherAlgorithm(var1, "BIKE192", "org.bouncycastle.pqc.jcajce.provider.bike.BIKECipherSpi$BIKE192", BCObjectIdentifiers.bike192);
         this.addCipherAlgorithm(var1, "BIKE256", "org.bouncycastle.pqc.jcajce.provider.bike.BIKECipherSpi$BIKE256", BCObjectIdentifiers.bike256);
         this.registerOid(var1, BCObjectIdentifiers.pqc_kem_bike, "BIKE", var2);
      }
   }
}
