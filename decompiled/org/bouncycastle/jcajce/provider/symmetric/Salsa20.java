package org.bouncycastle.jcajce.provider.symmetric;

import org.bouncycastle.crypto.CipherKeyGenerator;
import org.bouncycastle.crypto.engines.Salsa20Engine;
import org.bouncycastle.jcajce.provider.config.ConfigurableProvider;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseKeyGenerator;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseStreamCipher;
import org.bouncycastle.jcajce.provider.symmetric.util.IvAlgorithmParameters;
import org.bouncycastle.jcajce.provider.util.AlgorithmProvider;

public final class Salsa20 {
   private Salsa20() {
   }

   public static class AlgParams extends IvAlgorithmParameters {
      @Override
      protected String engineToString() {
         return "Salsa20 IV";
      }
   }

   public static class Base extends BaseStreamCipher {
      public Base() {
         super(new Salsa20Engine(), 8);
      }
   }

   public static class KeyGen extends BaseKeyGenerator {
      public KeyGen() {
         super("Salsa20", 128, new CipherKeyGenerator());
      }
   }

   public static class Mappings extends AlgorithmProvider {
      private static final String PREFIX = Salsa20.class.getName();

      @Override
      public void configure(ConfigurableProvider var1) {
         var1.addAlgorithm("Cipher.SALSA20", PREFIX + "$Base");
         var1.addAlgorithm("KeyGenerator.SALSA20", PREFIX + "$KeyGen");
         var1.addAlgorithm("AlgorithmParameters.SALSA20", PREFIX + "$AlgParams");
      }
   }
}
