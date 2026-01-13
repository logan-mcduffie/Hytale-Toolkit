package org.bouncycastle.jcajce.provider.digest;

import org.bouncycastle.crypto.CipherKeyGenerator;
import org.bouncycastle.crypto.digests.WhirlpoolDigest;
import org.bouncycastle.crypto.macs.HMac;
import org.bouncycastle.internal.asn1.iso.ISOIECObjectIdentifiers;
import org.bouncycastle.jcajce.provider.config.ConfigurableProvider;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseKeyGenerator;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseMac;

public class Whirlpool {
   private Whirlpool() {
   }

   public static class Digest extends BCMessageDigest implements Cloneable {
      public Digest() {
         super(new WhirlpoolDigest());
      }

      @Override
      public Object clone() throws CloneNotSupportedException {
         Whirlpool.Digest var1 = (Whirlpool.Digest)super.clone();
         var1.digest = new WhirlpoolDigest((WhirlpoolDigest)this.digest);
         return var1;
      }
   }

   public static class HashMac extends BaseMac {
      public HashMac() {
         super(new HMac(new WhirlpoolDigest()));
      }
   }

   public static class KeyGenerator extends BaseKeyGenerator {
      public KeyGenerator() {
         super("HMACWHIRLPOOL", 512, new CipherKeyGenerator());
      }
   }

   public static class Mappings extends DigestAlgorithmProvider {
      private static final String PREFIX = Whirlpool.class.getName();

      @Override
      public void configure(ConfigurableProvider var1) {
         var1.addAlgorithm("MessageDigest.WHIRLPOOL", PREFIX + "$Digest");
         var1.addAlgorithm("MessageDigest", ISOIECObjectIdentifiers.whirlpool, PREFIX + "$Digest");
         this.addHMACAlgorithm(var1, "WHIRLPOOL", PREFIX + "$HashMac", PREFIX + "$KeyGenerator");
      }
   }
}
