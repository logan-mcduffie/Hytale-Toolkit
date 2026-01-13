package org.bouncycastle.jcajce.provider.digest;

import org.bouncycastle.crypto.CipherKeyGenerator;
import org.bouncycastle.crypto.digests.TigerDigest;
import org.bouncycastle.crypto.macs.HMac;
import org.bouncycastle.internal.asn1.iana.IANAObjectIdentifiers;
import org.bouncycastle.jcajce.provider.config.ConfigurableProvider;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseKeyGenerator;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseMac;
import org.bouncycastle.jcajce.provider.symmetric.util.PBESecretKeyFactory;

public class Tiger {
   private Tiger() {
   }

   public static class Digest extends BCMessageDigest implements Cloneable {
      public Digest() {
         super(new TigerDigest());
      }

      @Override
      public Object clone() throws CloneNotSupportedException {
         Tiger.Digest var1 = (Tiger.Digest)super.clone();
         var1.digest = new TigerDigest((TigerDigest)this.digest);
         return var1;
      }
   }

   public static class HashMac extends BaseMac {
      public HashMac() {
         super(new HMac(new TigerDigest()));
      }
   }

   public static class KeyGenerator extends BaseKeyGenerator {
      public KeyGenerator() {
         super("HMACTIGER", 192, new CipherKeyGenerator());
      }
   }

   public static class Mappings extends DigestAlgorithmProvider {
      private static final String PREFIX = Tiger.class.getName();

      @Override
      public void configure(ConfigurableProvider var1) {
         var1.addAlgorithm("MessageDigest.TIGER", PREFIX + "$Digest");
         var1.addAlgorithm("MessageDigest.Tiger", PREFIX + "$Digest");
         this.addHMACAlgorithm(var1, "TIGER", PREFIX + "$HashMac", PREFIX + "$KeyGenerator");
         this.addHMACAlias(var1, "TIGER", IANAObjectIdentifiers.hmacTIGER);
         var1.addAlgorithm("SecretKeyFactory.PBEWITHHMACTIGER", PREFIX + "$PBEWithMacKeyFactory");
      }
   }

   public static class PBEWithHashMac extends BaseMac {
      public PBEWithHashMac() {
         super(new HMac(new TigerDigest()), 2, 3, 192);
      }
   }

   public static class PBEWithMacKeyFactory extends PBESecretKeyFactory {
      public PBEWithMacKeyFactory() {
         super("PBEwithHmacTiger", null, false, 2, 3, 192, 0);
      }
   }

   public static class TigerHmac extends BaseMac {
      public TigerHmac() {
         super(new HMac(new TigerDigest()));
      }
   }
}
