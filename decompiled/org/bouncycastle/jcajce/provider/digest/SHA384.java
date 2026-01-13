package org.bouncycastle.jcajce.provider.digest;

import org.bouncycastle.asn1.nist.NISTObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.crypto.CipherKeyGenerator;
import org.bouncycastle.crypto.digests.SHA384Digest;
import org.bouncycastle.crypto.macs.HMac;
import org.bouncycastle.crypto.macs.OldHMac;
import org.bouncycastle.jcajce.provider.config.ConfigurableProvider;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseKeyGenerator;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseMac;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseSecretKeyFactory;

public class SHA384 {
   private SHA384() {
   }

   public static class Digest extends BCMessageDigest implements Cloneable {
      public Digest() {
         super(new SHA384Digest());
      }

      @Override
      public Object clone() throws CloneNotSupportedException {
         SHA384.Digest var1 = (SHA384.Digest)super.clone();
         var1.digest = new SHA384Digest((SHA384Digest)this.digest);
         return var1;
      }
   }

   public static class HashMac extends BaseMac {
      public HashMac() {
         super(new HMac(new SHA384Digest()));
      }
   }

   public static class KeyFactory extends BaseSecretKeyFactory {
      public KeyFactory() {
         super("HmacSHA384", null);
      }
   }

   public static class KeyGenerator extends BaseKeyGenerator {
      public KeyGenerator() {
         super("HMACSHA384", 384, new CipherKeyGenerator());
      }
   }

   public static class Mappings extends DigestAlgorithmProvider {
      private static final String PREFIX = SHA384.class.getName();

      @Override
      public void configure(ConfigurableProvider var1) {
         var1.addAlgorithm("MessageDigest.SHA-384", PREFIX + "$Digest");
         var1.addAlgorithm("Alg.Alias.MessageDigest.SHA384", "SHA-384");
         var1.addAlgorithm("Alg.Alias.MessageDigest." + NISTObjectIdentifiers.id_sha384, "SHA-384");
         var1.addAlgorithm("Mac.OLDHMACSHA384", PREFIX + "$OldSHA384");
         var1.addAlgorithm("Mac.PBEWITHHMACSHA384", PREFIX + "$HashMac");
         this.addHMACAlgorithm(var1, "SHA384", PREFIX + "$HashMac", PREFIX + "$KeyGenerator");
         this.addHMACAlias(var1, "SHA384", PKCSObjectIdentifiers.id_hmacWithSHA384);
         var1.addAlgorithm("SecretKeyFactory.HMACSHA384", PREFIX + "$KeyFactory");
         var1.addAlgorithm("Alg.Alias.SecretKeyFactory." + PKCSObjectIdentifiers.id_hmacWithSHA384, "HMACSHA384");
      }
   }

   public static class OldSHA384 extends BaseMac {
      public OldSHA384() {
         super(new OldHMac(new SHA384Digest()));
      }
   }
}
