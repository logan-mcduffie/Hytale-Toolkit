package org.bouncycastle.jcajce.provider.asymmetric;

import java.util.HashMap;
import java.util.Map;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.jcajce.provider.asymmetric.compositesignatures.CompositeIndex;
import org.bouncycastle.jcajce.provider.asymmetric.compositesignatures.KeyFactorySpi;
import org.bouncycastle.jcajce.provider.config.ConfigurableProvider;
import org.bouncycastle.jcajce.provider.util.AsymmetricAlgorithmProvider;

public class CompositeSignatures {
   private static final String PREFIX = "org.bouncycastle.jcajce.provider.asymmetric.compositesignatures.";
   private static final Map<String, String> compositesAttributes = new HashMap<>();

   static {
      compositesAttributes.put("SupportedKeyClasses", "org.bouncycastle.jcajce.CompositePublicKey|org.bouncycastle.jcajce.CompositePrivateKey");
      compositesAttributes.put("SupportedKeyFormats", "PKCS#8|X.509");
   }

   public static class Mappings extends AsymmetricAlgorithmProvider {
      @Override
      public void configure(ConfigurableProvider var1) {
         var1.addAlgorithm("Signature.COMPOSITE", "org.bouncycastle.jcajce.provider.asymmetric.compositesignatures.SignatureSpi$COMPOSITE");

         for (ASN1ObjectIdentifier var3 : CompositeIndex.getSupportedIdentifiers()) {
            String var4 = CompositeIndex.getAlgorithmName(var3);
            String var5 = var4.replace('-', '_');
            var1.addAlgorithm("Alg.Alias.KeyFactory", var3, "COMPOSITE");
            var1.addAlgorithm("Alg.Alias.KeyFactory." + var4, "COMPOSITE");
            var1.addAlgorithm("KeyPairGenerator." + var4, "org.bouncycastle.jcajce.provider.asymmetric.compositesignatures.KeyPairGeneratorSpi$" + var5);
            var1.addAlgorithm("Alg.Alias.KeyPairGenerator", var3, var4);
            var1.addAlgorithm("Signature." + var4, "org.bouncycastle.jcajce.provider.asymmetric.compositesignatures.SignatureSpi$" + var5);
            var1.addAlgorithm("Alg.Alias.Signature", var3, var4);
            var1.addAlgorithm(
               "Signature." + var4 + "-PREHASH", "org.bouncycastle.jcajce.provider.asymmetric.compositesignatures.SignatureSpi$" + var5 + "_PREHASH"
            );
            var1.addKeyInfoConverter(var3, new KeyFactorySpi());
         }
      }
   }
}
