package org.bouncycastle.jcajce.provider.symmetric;

import org.bouncycastle.crypto.CipherKeyGenerator;
import org.bouncycastle.crypto.engines.BlowfishEngine;
import org.bouncycastle.crypto.macs.CMac;
import org.bouncycastle.crypto.modes.CBCBlockCipher;
import org.bouncycastle.internal.asn1.misc.MiscObjectIdentifiers;
import org.bouncycastle.jcajce.provider.config.ConfigurableProvider;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseBlockCipher;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseKeyGenerator;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseMac;
import org.bouncycastle.jcajce.provider.symmetric.util.IvAlgorithmParameters;
import org.bouncycastle.jcajce.provider.util.AlgorithmProvider;

public final class Blowfish {
   private Blowfish() {
   }

   public static class AlgParams extends IvAlgorithmParameters {
      @Override
      protected String engineToString() {
         return "Blowfish IV";
      }
   }

   public static class CBC extends BaseBlockCipher {
      public CBC() {
         super(new CBCBlockCipher(new BlowfishEngine()), 64);
      }
   }

   public static class CMAC extends BaseMac {
      public CMAC() {
         super(new CMac(new BlowfishEngine()));
      }
   }

   public static class ECB extends BaseBlockCipher {
      public ECB() {
         super(new BlowfishEngine());
      }
   }

   public static class KeyGen extends BaseKeyGenerator {
      public KeyGen() {
         super("Blowfish", 128, new CipherKeyGenerator());
      }
   }

   public static class Mappings extends AlgorithmProvider {
      private static final String PREFIX = Blowfish.class.getName();

      @Override
      public void configure(ConfigurableProvider var1) {
         var1.addAlgorithm("Mac.BLOWFISHCMAC", PREFIX + "$CMAC");
         var1.addAlgorithm("Cipher.BLOWFISH", PREFIX + "$ECB");
         var1.addAlgorithm("Cipher", MiscObjectIdentifiers.cryptlib_algorithm_blowfish_CBC, PREFIX + "$CBC");
         var1.addAlgorithm("KeyGenerator.BLOWFISH", PREFIX + "$KeyGen");
         var1.addAlgorithm("Alg.Alias.KeyGenerator", MiscObjectIdentifiers.cryptlib_algorithm_blowfish_CBC, "BLOWFISH");
         var1.addAlgorithm("AlgorithmParameters.BLOWFISH", PREFIX + "$AlgParams");
         var1.addAlgorithm("Alg.Alias.AlgorithmParameters", MiscObjectIdentifiers.cryptlib_algorithm_blowfish_CBC, "BLOWFISH");
      }
   }
}
