package org.bouncycastle.jcajce.provider.symmetric;

import org.bouncycastle.crypto.CipherKeyGenerator;
import org.bouncycastle.crypto.engines.SkipjackEngine;
import org.bouncycastle.crypto.macs.CBCBlockCipherMac;
import org.bouncycastle.crypto.macs.CFBBlockCipherMac;
import org.bouncycastle.jcajce.provider.config.ConfigurableProvider;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseBlockCipher;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseKeyGenerator;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseMac;
import org.bouncycastle.jcajce.provider.symmetric.util.IvAlgorithmParameters;
import org.bouncycastle.jcajce.provider.util.AlgorithmProvider;

public final class Skipjack {
   private Skipjack() {
   }

   public static class AlgParams extends IvAlgorithmParameters {
      @Override
      protected String engineToString() {
         return "Skipjack IV";
      }
   }

   public static class ECB extends BaseBlockCipher {
      public ECB() {
         super(new SkipjackEngine());
      }
   }

   public static class KeyGen extends BaseKeyGenerator {
      public KeyGen() {
         super("Skipjack", 80, new CipherKeyGenerator());
      }
   }

   public static class Mac extends BaseMac {
      public Mac() {
         super(new CBCBlockCipherMac(new SkipjackEngine()));
      }
   }

   public static class MacCFB8 extends BaseMac {
      public MacCFB8() {
         super(new CFBBlockCipherMac(new SkipjackEngine()));
      }
   }

   public static class Mappings extends AlgorithmProvider {
      private static final String PREFIX = Skipjack.class.getName();

      @Override
      public void configure(ConfigurableProvider var1) {
         var1.addAlgorithm("Cipher.SKIPJACK", PREFIX + "$ECB");
         var1.addAlgorithm("KeyGenerator.SKIPJACK", PREFIX + "$KeyGen");
         var1.addAlgorithm("AlgorithmParameters.SKIPJACK", PREFIX + "$AlgParams");
         var1.addAlgorithm("Mac.SKIPJACKMAC", PREFIX + "$Mac");
         var1.addAlgorithm("Alg.Alias.Mac.SKIPJACK", "SKIPJACKMAC");
         var1.addAlgorithm("Mac.SKIPJACKMAC/CFB8", PREFIX + "$MacCFB8");
         var1.addAlgorithm("Alg.Alias.Mac.SKIPJACK/CFB8", "SKIPJACKMAC/CFB8");
      }
   }
}
