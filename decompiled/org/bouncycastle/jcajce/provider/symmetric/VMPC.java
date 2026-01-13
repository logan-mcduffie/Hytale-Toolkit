package org.bouncycastle.jcajce.provider.symmetric;

import org.bouncycastle.crypto.CipherKeyGenerator;
import org.bouncycastle.crypto.engines.VMPCEngine;
import org.bouncycastle.crypto.macs.VMPCMac;
import org.bouncycastle.jcajce.provider.config.ConfigurableProvider;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseKeyGenerator;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseMac;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseStreamCipher;
import org.bouncycastle.jcajce.provider.util.AlgorithmProvider;

public final class VMPC {
   private VMPC() {
   }

   public static class Base extends BaseStreamCipher {
      public Base() {
         super(new VMPCEngine(), 16);
      }
   }

   public static class KeyGen extends BaseKeyGenerator {
      public KeyGen() {
         super("VMPC", 128, new CipherKeyGenerator());
      }
   }

   public static class Mac extends BaseMac {
      public Mac() {
         super(new VMPCMac());
      }
   }

   public static class Mappings extends AlgorithmProvider {
      private static final String PREFIX = VMPC.class.getName();

      @Override
      public void configure(ConfigurableProvider var1) {
         var1.addAlgorithm("Cipher.VMPC", PREFIX + "$Base");
         var1.addAlgorithm("KeyGenerator.VMPC", PREFIX + "$KeyGen");
         var1.addAlgorithm("Mac.VMPCMAC", PREFIX + "$Mac");
         var1.addAlgorithm("Alg.Alias.Mac.VMPC", "VMPCMAC");
         var1.addAlgorithm("Alg.Alias.Mac.VMPC-MAC", "VMPCMAC");
      }
   }
}
