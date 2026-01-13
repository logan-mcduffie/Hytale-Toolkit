package org.bouncycastle.jcajce.provider.symmetric;

import org.bouncycastle.crypto.CipherKeyGenerator;
import org.bouncycastle.crypto.engines.XSalsa20Engine;
import org.bouncycastle.jcajce.provider.config.ConfigurableProvider;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseKeyGenerator;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseStreamCipher;
import org.bouncycastle.jcajce.provider.symmetric.util.IvAlgorithmParameters;
import org.bouncycastle.jcajce.provider.util.AlgorithmProvider;

public final class XSalsa20 {
   private XSalsa20() {
   }

   public static class AlgParams extends IvAlgorithmParameters {
      @Override
      protected String engineToString() {
         return "XSalsa20 IV";
      }
   }

   public static class Base extends BaseStreamCipher {
      public Base() {
         super(new XSalsa20Engine(), 24);
      }
   }

   public static class KeyGen extends BaseKeyGenerator {
      public KeyGen() {
         super("XSalsa20", 256, new CipherKeyGenerator());
      }
   }

   public static class Mappings extends AlgorithmProvider {
      private static final String PREFIX = XSalsa20.class.getName();

      @Override
      public void configure(ConfigurableProvider var1) {
         var1.addAlgorithm("Cipher.XSALSA20", PREFIX + "$Base");
         var1.addAlgorithm("KeyGenerator.XSALSA20", PREFIX + "$KeyGen");
         var1.addAlgorithm("AlgorithmParameters.XSALSA20", PREFIX + "$AlgParams");
      }
   }
}
