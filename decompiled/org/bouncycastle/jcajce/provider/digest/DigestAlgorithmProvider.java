package org.bouncycastle.jcajce.provider.digest;

import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.jcajce.provider.config.ConfigurableProvider;
import org.bouncycastle.jcajce.provider.util.AlgorithmProvider;

abstract class DigestAlgorithmProvider extends AlgorithmProvider {
   protected void addHMACAlgorithm(ConfigurableProvider var1, String var2, String var3, String var4) {
      String var5 = "HMAC" + var2;
      var1.addAlgorithm("Mac." + var5, var3);
      var1.addAlgorithm("Alg.Alias.Mac.HMAC-" + var2, var5);
      var1.addAlgorithm("Alg.Alias.Mac.HMAC/" + var2, var5);
      var1.addAlgorithm("KeyGenerator." + var5, var4);
      var1.addAlgorithm("Alg.Alias.KeyGenerator.HMAC-" + var2, var5);
      var1.addAlgorithm("Alg.Alias.KeyGenerator.HMAC/" + var2, var5);
   }

   protected void addKMACAlgorithm(ConfigurableProvider var1, String var2, String var3, String var4) {
      String var5 = "KMAC" + var2;
      var1.addAlgorithm("Mac." + var5, var3);
      var1.addAlgorithm("KeyGenerator." + var5, var4);
      var1.addAlgorithm("Alg.Alias.KeyGenerator.KMAC" + var2, var5);
   }

   protected void addHMACAlias(ConfigurableProvider var1, String var2, ASN1ObjectIdentifier var3) {
      String var4 = "HMAC" + var2;
      var1.addAlgorithm("Alg.Alias.Mac." + var3, var4);
      var1.addAlgorithm("Alg.Alias.KeyGenerator." + var3, var4);
   }
}
