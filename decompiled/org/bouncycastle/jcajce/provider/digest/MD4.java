package org.bouncycastle.jcajce.provider.digest;

import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.crypto.CipherKeyGenerator;
import org.bouncycastle.crypto.digests.MD4Digest;
import org.bouncycastle.crypto.macs.HMac;
import org.bouncycastle.jcajce.provider.config.ConfigurableProvider;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseKeyGenerator;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseMac;

public class MD4 {
   private MD4() {
   }

   public static class Digest extends BCMessageDigest implements Cloneable {
      public Digest() {
         super(new MD4Digest());
      }

      @Override
      public Object clone() throws CloneNotSupportedException {
         MD4.Digest var1 = (MD4.Digest)super.clone();
         var1.digest = new MD4Digest((MD4Digest)this.digest);
         return var1;
      }
   }

   public static class HashMac extends BaseMac {
      public HashMac() {
         super(new HMac(new MD4Digest()));
      }
   }

   public static class KeyGenerator extends BaseKeyGenerator {
      public KeyGenerator() {
         super("HMACMD4", 128, new CipherKeyGenerator());
      }
   }

   public static class Mappings extends DigestAlgorithmProvider {
      private static final String PREFIX = MD4.class.getName();

      @Override
      public void configure(ConfigurableProvider var1) {
         var1.addAlgorithm("MessageDigest.MD4", PREFIX + "$Digest");
         var1.addAlgorithm("Alg.Alias.MessageDigest." + PKCSObjectIdentifiers.md4, "MD4");
         this.addHMACAlgorithm(var1, "MD4", PREFIX + "$HashMac", PREFIX + "$KeyGenerator");
      }
   }
}
