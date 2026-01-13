package org.bouncycastle.pqc.jcajce.provider;

import org.bouncycastle.asn1.bc.BCObjectIdentifiers;
import org.bouncycastle.jcajce.provider.config.ConfigurableProvider;
import org.bouncycastle.jcajce.provider.util.AsymmetricAlgorithmProvider;
import org.bouncycastle.pqc.jcajce.provider.cmce.CMCEKeyFactorySpi;

public class CMCE {
   private static final String PREFIX = "org.bouncycastle.pqc.jcajce.provider.cmce.";

   public static class Mappings extends AsymmetricAlgorithmProvider {
      @Override
      public void configure(ConfigurableProvider var1) {
         var1.addAlgorithm("KeyFactory.CMCE", "org.bouncycastle.pqc.jcajce.provider.cmce.CMCEKeyFactorySpi");
         var1.addAlgorithm("KeyPairGenerator.CMCE", "org.bouncycastle.pqc.jcajce.provider.cmce.CMCEKeyPairGeneratorSpi");
         var1.addAlgorithm("KeyGenerator.CMCE", "org.bouncycastle.pqc.jcajce.provider.cmce.CMCEKeyGeneratorSpi");
         CMCEKeyFactorySpi var2 = new CMCEKeyFactorySpi();
         var1.addAlgorithm("Cipher.CMCE", "org.bouncycastle.pqc.jcajce.provider.cmce.CMCECipherSpi$Base");
         var1.addAlgorithm("Alg.Alias.Cipher." + BCObjectIdentifiers.pqc_kem_mceliece, "CMCE");
         this.addCipherAlgorithm(
            var1, "mceliece348864", "org.bouncycastle.pqc.jcajce.provider.cmce.CMCECipherSpi$MCE348864", BCObjectIdentifiers.mceliece348864_r3
         );
         this.addCipherAlgorithm(
            var1, "mceliece460896", "org.bouncycastle.pqc.jcajce.provider.cmce.CMCECipherSpi$MCE460896", BCObjectIdentifiers.mceliece460896_r3
         );
         this.addCipherAlgorithm(
            var1, "mceliece6688128", "org.bouncycastle.pqc.jcajce.provider.cmce.CMCECipherSpi$MCE6688128", BCObjectIdentifiers.mceliece6688128_r3
         );
         this.addCipherAlgorithm(
            var1, "mceliece6960119", "org.bouncycastle.pqc.jcajce.provider.cmce.CMCECipherSpi$MCE6960119", BCObjectIdentifiers.mceliece6960119_r3
         );
         this.addCipherAlgorithm(
            var1, "mceliece8192128", "org.bouncycastle.pqc.jcajce.provider.cmce.CMCECipherSpi$MCE8192128", BCObjectIdentifiers.mceliece8192128_r3
         );
         this.registerOid(var1, BCObjectIdentifiers.pqc_kem_mceliece, "CMCE", var2);
         this.registerOidAlgorithmParameters(var1, BCObjectIdentifiers.pqc_kem_mceliece, "CMCE");
      }
   }
}
