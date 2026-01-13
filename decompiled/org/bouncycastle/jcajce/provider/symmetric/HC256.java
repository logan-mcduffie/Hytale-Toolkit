package org.bouncycastle.jcajce.provider.symmetric;

import org.bouncycastle.crypto.CipherKeyGenerator;
import org.bouncycastle.crypto.engines.HC256Engine;
import org.bouncycastle.jcajce.provider.config.ConfigurableProvider;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseKeyGenerator;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseStreamCipher;
import org.bouncycastle.jcajce.provider.symmetric.util.IvAlgorithmParameters;
import org.bouncycastle.jcajce.provider.util.AlgorithmProvider;

public final class HC256 {
   private HC256() {
   }

   public static class AlgParams extends IvAlgorithmParameters {
      @Override
      protected String engineToString() {
         return "HC256 IV";
      }
   }

   public static class Base extends BaseStreamCipher {
      public Base() {
         super(new HC256Engine(), 32);
      }
   }

   public static class KeyGen extends BaseKeyGenerator {
      public KeyGen() {
         super("HC256", 256, new CipherKeyGenerator());
      }
   }

   public static class Mappings extends AlgorithmProvider {
      private static final String PREFIX = HC256.class.getName();

      @Override
      public void configure(ConfigurableProvider var1) {
         var1.addAlgorithm("Cipher.HC256", PREFIX + "$Base");
         var1.addAlgorithm("KeyGenerator.HC256", PREFIX + "$KeyGen");
         var1.addAlgorithm("AlgorithmParameters.HC256", PREFIX + "$AlgParams");
      }
   }
}
