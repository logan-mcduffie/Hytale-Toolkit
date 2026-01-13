package org.bouncycastle.jcajce.provider.digest;

import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.crypto.CipherKeyGenerator;
import org.bouncycastle.crypto.digests.MD5Digest;
import org.bouncycastle.crypto.macs.HMac;
import org.bouncycastle.internal.asn1.iana.IANAObjectIdentifiers;
import org.bouncycastle.jcajce.provider.config.ConfigurableProvider;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseKeyGenerator;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseMac;

public class MD5 {
   private MD5() {
   }

   public static class Digest extends BCMessageDigest implements Cloneable {
      public Digest() {
         super(new MD5Digest());
      }

      @Override
      public Object clone() throws CloneNotSupportedException {
         MD5.Digest var1 = (MD5.Digest)super.clone();
         var1.digest = new MD5Digest((MD5Digest)this.digest);
         return var1;
      }
   }

   public static class HashMac extends BaseMac {
      public HashMac() {
         super(new HMac(new MD5Digest()));
      }
   }

   public static class KeyGenerator extends BaseKeyGenerator {
      public KeyGenerator() {
         super("HMACMD5", 128, new CipherKeyGenerator());
      }
   }

   public static class Mappings extends DigestAlgorithmProvider {
      private static final String PREFIX = MD5.class.getName();

      @Override
      public void configure(ConfigurableProvider var1) {
         var1.addAlgorithm("MessageDigest.MD5", PREFIX + "$Digest");
         var1.addAlgorithm("Alg.Alias.MessageDigest." + PKCSObjectIdentifiers.md5, "MD5");
         this.addHMACAlgorithm(var1, "MD5", PREFIX + "$HashMac", PREFIX + "$KeyGenerator");
         this.addHMACAlias(var1, "MD5", IANAObjectIdentifiers.hmacMD5);
      }
   }
}
