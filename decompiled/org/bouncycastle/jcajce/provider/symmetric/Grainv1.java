package org.bouncycastle.jcajce.provider.symmetric;

import org.bouncycastle.crypto.CipherKeyGenerator;
import org.bouncycastle.crypto.engines.Grainv1Engine;
import org.bouncycastle.jcajce.provider.config.ConfigurableProvider;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseKeyGenerator;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseStreamCipher;
import org.bouncycastle.jcajce.provider.symmetric.util.IvAlgorithmParameters;
import org.bouncycastle.jcajce.provider.util.AlgorithmProvider;

public final class Grainv1 {
   private Grainv1() {
   }

   public static class AlgParams extends IvAlgorithmParameters {
      @Override
      protected String engineToString() {
         return "Grainv1 IV";
      }
   }

   public static class Base extends BaseStreamCipher {
      public Base() {
         super(new Grainv1Engine(), 8);
      }
   }

   public static class KeyGen extends BaseKeyGenerator {
      public KeyGen() {
         super("Grainv1", 80, new CipherKeyGenerator());
      }
   }

   public static class Mappings extends AlgorithmProvider {
      private static final String PREFIX = Grainv1.class.getName();

      @Override
      public void configure(ConfigurableProvider var1) {
         var1.addAlgorithm("Cipher.Grainv1", PREFIX + "$Base");
         var1.addAlgorithm("KeyGenerator.Grainv1", PREFIX + "$KeyGen");
         var1.addAlgorithm("AlgorithmParameters.Grainv1", PREFIX + "$AlgParams");
      }
   }
}
