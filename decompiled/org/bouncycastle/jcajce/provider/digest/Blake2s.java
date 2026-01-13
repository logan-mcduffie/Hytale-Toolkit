package org.bouncycastle.jcajce.provider.digest;

import org.bouncycastle.crypto.digests.Blake2sDigest;
import org.bouncycastle.internal.asn1.misc.MiscObjectIdentifiers;
import org.bouncycastle.jcajce.provider.config.ConfigurableProvider;

public class Blake2s {
   private Blake2s() {
   }

   public static class Blake2s128 extends BCMessageDigest implements Cloneable {
      public Blake2s128() {
         super(new Blake2sDigest(128));
      }

      @Override
      public Object clone() throws CloneNotSupportedException {
         Blake2s.Blake2s128 var1 = (Blake2s.Blake2s128)super.clone();
         var1.digest = new Blake2sDigest((Blake2sDigest)this.digest);
         return var1;
      }
   }

   public static class Blake2s160 extends BCMessageDigest implements Cloneable {
      public Blake2s160() {
         super(new Blake2sDigest(160));
      }

      @Override
      public Object clone() throws CloneNotSupportedException {
         Blake2s.Blake2s160 var1 = (Blake2s.Blake2s160)super.clone();
         var1.digest = new Blake2sDigest((Blake2sDigest)this.digest);
         return var1;
      }
   }

   public static class Blake2s224 extends BCMessageDigest implements Cloneable {
      public Blake2s224() {
         super(new Blake2sDigest(224));
      }

      @Override
      public Object clone() throws CloneNotSupportedException {
         Blake2s.Blake2s224 var1 = (Blake2s.Blake2s224)super.clone();
         var1.digest = new Blake2sDigest((Blake2sDigest)this.digest);
         return var1;
      }
   }

   public static class Blake2s256 extends BCMessageDigest implements Cloneable {
      public Blake2s256() {
         super(new Blake2sDigest(256));
      }

      @Override
      public Object clone() throws CloneNotSupportedException {
         Blake2s.Blake2s256 var1 = (Blake2s.Blake2s256)super.clone();
         var1.digest = new Blake2sDigest((Blake2sDigest)this.digest);
         return var1;
      }
   }

   public static class Mappings extends DigestAlgorithmProvider {
      private static final String PREFIX = Blake2s.class.getName();

      @Override
      public void configure(ConfigurableProvider var1) {
         var1.addAlgorithm("MessageDigest.BLAKE2S-256", PREFIX + "$Blake2s256");
         var1.addAlgorithm("Alg.Alias.MessageDigest." + MiscObjectIdentifiers.id_blake2s256, "BLAKE2S-256");
         var1.addAlgorithm("MessageDigest.BLAKE2S-224", PREFIX + "$Blake2s224");
         var1.addAlgorithm("Alg.Alias.MessageDigest." + MiscObjectIdentifiers.id_blake2s224, "BLAKE2S-224");
         var1.addAlgorithm("MessageDigest.BLAKE2S-160", PREFIX + "$Blake2s160");
         var1.addAlgorithm("Alg.Alias.MessageDigest." + MiscObjectIdentifiers.id_blake2s160, "BLAKE2S-160");
         var1.addAlgorithm("MessageDigest.BLAKE2S-128", PREFIX + "$Blake2s128");
         var1.addAlgorithm("Alg.Alias.MessageDigest." + MiscObjectIdentifiers.id_blake2s128, "BLAKE2S-128");
      }
   }
}
