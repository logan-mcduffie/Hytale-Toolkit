package org.bouncycastle.jcajce.provider.digest;

import org.bouncycastle.crypto.digests.Blake3Digest;
import org.bouncycastle.internal.asn1.misc.MiscObjectIdentifiers;
import org.bouncycastle.jcajce.provider.config.ConfigurableProvider;

public class Blake3 {
   private Blake3() {
   }

   public static class Blake3_256 extends BCMessageDigest implements Cloneable {
      public Blake3_256() {
         super(new Blake3Digest(256));
      }

      @Override
      public Object clone() throws CloneNotSupportedException {
         Blake3.Blake3_256 var1 = (Blake3.Blake3_256)super.clone();
         var1.digest = new Blake3Digest((Blake3Digest)this.digest);
         return var1;
      }
   }

   public static class Mappings extends DigestAlgorithmProvider {
      private static final String PREFIX = Blake3.class.getName();

      @Override
      public void configure(ConfigurableProvider var1) {
         var1.addAlgorithm("MessageDigest.BLAKE3-256", PREFIX + "$Blake3_256");
         var1.addAlgorithm("Alg.Alias.MessageDigest." + MiscObjectIdentifiers.blake3_256, "BLAKE3-256");
      }
   }
}
