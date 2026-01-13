package org.bouncycastle.pqc.jcajce.provider;

import org.bouncycastle.asn1.bc.BCObjectIdentifiers;
import org.bouncycastle.jcajce.provider.config.ConfigurableProvider;
import org.bouncycastle.jcajce.provider.util.AsymmetricAlgorithmProvider;
import org.bouncycastle.pqc.jcajce.provider.picnic.PicnicKeyFactorySpi;

public class Picnic {
   private static final String PREFIX = "org.bouncycastle.pqc.jcajce.provider.picnic.";

   public static class Mappings extends AsymmetricAlgorithmProvider {
      @Override
      public void configure(ConfigurableProvider var1) {
         var1.addAlgorithm("KeyFactory.PICNIC", "org.bouncycastle.pqc.jcajce.provider.picnic.PicnicKeyFactorySpi");
         var1.addAlgorithm("KeyPairGenerator.PICNIC", "org.bouncycastle.pqc.jcajce.provider.picnic.PicnicKeyPairGeneratorSpi");
         this.addSignatureAlgorithm(var1, "PICNIC", "org.bouncycastle.pqc.jcajce.provider.picnic.SignatureSpi$Base", BCObjectIdentifiers.picnic_signature);
         this.addSignatureAlgorithm(
            var1, "SHAKE256", "PICNIC", "org.bouncycastle.pqc.jcajce.provider.picnic.SignatureSpi$withShake256", BCObjectIdentifiers.picnic_with_shake256
         );
         this.addSignatureAlgorithm(
            var1, "SHA512", "PICNIC", "org.bouncycastle.pqc.jcajce.provider.picnic.SignatureSpi$withSha512", BCObjectIdentifiers.picnic_with_sha512
         );
         this.addSignatureAlgorithm(
            var1, "SHA3-512", "PICNIC", "org.bouncycastle.pqc.jcajce.provider.picnic.SignatureSpi$withSha3512", BCObjectIdentifiers.picnic_with_sha3_512
         );
         PicnicKeyFactorySpi var2 = new PicnicKeyFactorySpi();
         this.registerOid(var1, BCObjectIdentifiers.picnic_key, "Picnic", var2);
         this.registerOid(var1, BCObjectIdentifiers.picnicl1fs, "Picnic", var2);
         this.registerOid(var1, BCObjectIdentifiers.picnicl1ur, "Picnic", var2);
         this.registerOid(var1, BCObjectIdentifiers.picnicl3fs, "Picnic", var2);
         this.registerOid(var1, BCObjectIdentifiers.picnicl3ur, "Picnic", var2);
         this.registerOid(var1, BCObjectIdentifiers.picnicl5fs, "Picnic", var2);
         this.registerOid(var1, BCObjectIdentifiers.picnicl5ur, "Picnic", var2);
         this.registerOid(var1, BCObjectIdentifiers.picnic3l1, "Picnic", var2);
         this.registerOid(var1, BCObjectIdentifiers.picnic3l3, "Picnic", var2);
         this.registerOid(var1, BCObjectIdentifiers.picnic3l5, "Picnic", var2);
         this.registerOid(var1, BCObjectIdentifiers.picnicl1full, "Picnic", var2);
         this.registerOid(var1, BCObjectIdentifiers.picnicl3full, "Picnic", var2);
         this.registerOid(var1, BCObjectIdentifiers.picnicl5full, "Picnic", var2);
      }
   }
}
