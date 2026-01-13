package org.bouncycastle.jcajce.provider.symmetric;

import org.bouncycastle.crypto.CipherKeyGenerator;
import org.bouncycastle.jcajce.provider.config.ConfigurableProvider;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseKeyGenerator;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseMac;
import org.bouncycastle.jcajce.provider.util.AlgorithmProvider;

public final class SipHash {
   private SipHash() {
   }

   public static class KeyGen extends BaseKeyGenerator {
      public KeyGen() {
         super("SipHash", 128, new CipherKeyGenerator());
      }
   }

   public static class Mac24 extends BaseMac {
      public Mac24() {
         super(new org.bouncycastle.crypto.macs.SipHash());
      }
   }

   public static class Mac48 extends BaseMac {
      public Mac48() {
         super(new org.bouncycastle.crypto.macs.SipHash(4, 8));
      }
   }

   public static class Mappings extends AlgorithmProvider {
      private static final String PREFIX = SipHash.class.getName();

      @Override
      public void configure(ConfigurableProvider var1) {
         var1.addAlgorithm("Mac.SIPHASH-2-4", PREFIX + "$Mac24");
         var1.addAlgorithm("Alg.Alias.Mac.SIPHASH", "SIPHASH-2-4");
         var1.addAlgorithm("Mac.SIPHASH-4-8", PREFIX + "$Mac48");
         var1.addAlgorithm("KeyGenerator.SIPHASH", PREFIX + "$KeyGen");
         var1.addAlgorithm("Alg.Alias.KeyGenerator.SIPHASH-2-4", "SIPHASH");
         var1.addAlgorithm("Alg.Alias.KeyGenerator.SIPHASH-4-8", "SIPHASH");
      }
   }
}
