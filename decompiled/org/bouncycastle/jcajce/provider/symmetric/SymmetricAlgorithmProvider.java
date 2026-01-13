package org.bouncycastle.jcajce.provider.symmetric;

import org.bouncycastle.jcajce.provider.config.ConfigurableProvider;
import org.bouncycastle.jcajce.provider.util.AlgorithmProvider;

abstract class SymmetricAlgorithmProvider extends AlgorithmProvider {
   protected void addCMacAlgorithm(ConfigurableProvider var1, String var2, String var3, String var4) {
      var1.addAlgorithm("Mac." + var2 + "-CMAC", var3);
      var1.addAlgorithm("Alg.Alias.Mac." + var2 + "CMAC", var2 + "-CMAC");
      var1.addAlgorithm("KeyGenerator." + var2 + "-CMAC", var4);
      var1.addAlgorithm("Alg.Alias.KeyGenerator." + var2 + "CMAC", var2 + "-CMAC");
   }

   protected void addGMacAlgorithm(ConfigurableProvider var1, String var2, String var3, String var4) {
      var1.addAlgorithm("Mac." + var2 + "-GMAC", var3);
      var1.addAlgorithm("Alg.Alias.Mac." + var2 + "GMAC", var2 + "-GMAC");
      var1.addAlgorithm("KeyGenerator." + var2 + "-GMAC", var4);
      var1.addAlgorithm("Alg.Alias.KeyGenerator." + var2 + "GMAC", var2 + "-GMAC");
   }

   protected void addPoly1305Algorithm(ConfigurableProvider var1, String var2, String var3, String var4) {
      var1.addAlgorithm("Mac.POLY1305-" + var2, var3);
      var1.addAlgorithm("Alg.Alias.Mac.POLY1305" + var2, "POLY1305-" + var2);
      var1.addAlgorithm("KeyGenerator.POLY1305-" + var2, var4);
      var1.addAlgorithm("Alg.Alias.KeyGenerator.POLY1305" + var2, "POLY1305-" + var2);
   }
}
