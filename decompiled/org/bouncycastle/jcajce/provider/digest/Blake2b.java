package org.bouncycastle.jcajce.provider.digest;

import org.bouncycastle.crypto.digests.Blake2bDigest;
import org.bouncycastle.internal.asn1.misc.MiscObjectIdentifiers;
import org.bouncycastle.jcajce.provider.config.ConfigurableProvider;

public class Blake2b {
   private Blake2b() {
   }

   public static class Blake2b160 extends BCMessageDigest implements Cloneable {
      public Blake2b160() {
         super(new Blake2bDigest(160));
      }

      @Override
      public Object clone() throws CloneNotSupportedException {
         Blake2b.Blake2b160 var1 = (Blake2b.Blake2b160)super.clone();
         var1.digest = new Blake2bDigest((Blake2bDigest)this.digest);
         return var1;
      }
   }

   public static class Blake2b256 extends BCMessageDigest implements Cloneable {
      public Blake2b256() {
         super(new Blake2bDigest(256));
      }

      @Override
      public Object clone() throws CloneNotSupportedException {
         Blake2b.Blake2b256 var1 = (Blake2b.Blake2b256)super.clone();
         var1.digest = new Blake2bDigest((Blake2bDigest)this.digest);
         return var1;
      }
   }

   public static class Blake2b384 extends BCMessageDigest implements Cloneable {
      public Blake2b384() {
         super(new Blake2bDigest(384));
      }

      @Override
      public Object clone() throws CloneNotSupportedException {
         Blake2b.Blake2b384 var1 = (Blake2b.Blake2b384)super.clone();
         var1.digest = new Blake2bDigest((Blake2bDigest)this.digest);
         return var1;
      }
   }

   public static class Blake2b512 extends BCMessageDigest implements Cloneable {
      public Blake2b512() {
         super(new Blake2bDigest(512));
      }

      @Override
      public Object clone() throws CloneNotSupportedException {
         Blake2b.Blake2b512 var1 = (Blake2b.Blake2b512)super.clone();
         var1.digest = new Blake2bDigest((Blake2bDigest)this.digest);
         return var1;
      }
   }

   public static class Mappings extends DigestAlgorithmProvider {
      private static final String PREFIX = Blake2b.class.getName();

      @Override
      public void configure(ConfigurableProvider var1) {
         var1.addAlgorithm("MessageDigest.BLAKE2B-512", PREFIX + "$Blake2b512");
         var1.addAlgorithm("Alg.Alias.MessageDigest." + MiscObjectIdentifiers.id_blake2b512, "BLAKE2B-512");
         var1.addAlgorithm("MessageDigest.BLAKE2B-384", PREFIX + "$Blake2b384");
         var1.addAlgorithm("Alg.Alias.MessageDigest." + MiscObjectIdentifiers.id_blake2b384, "BLAKE2B-384");
         var1.addAlgorithm("MessageDigest.BLAKE2B-256", PREFIX + "$Blake2b256");
         var1.addAlgorithm("Alg.Alias.MessageDigest." + MiscObjectIdentifiers.id_blake2b256, "BLAKE2B-256");
         var1.addAlgorithm("MessageDigest.BLAKE2B-160", PREFIX + "$Blake2b160");
         var1.addAlgorithm("Alg.Alias.MessageDigest." + MiscObjectIdentifiers.id_blake2b160, "BLAKE2B-160");
      }
   }
}
