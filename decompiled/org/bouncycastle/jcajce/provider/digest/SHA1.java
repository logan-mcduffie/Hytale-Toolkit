package org.bouncycastle.jcajce.provider.digest;

import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.crypto.CipherKeyGenerator;
import org.bouncycastle.crypto.digests.SHA1Digest;
import org.bouncycastle.crypto.macs.HMac;
import org.bouncycastle.internal.asn1.iana.IANAObjectIdentifiers;
import org.bouncycastle.internal.asn1.oiw.OIWObjectIdentifiers;
import org.bouncycastle.jcajce.provider.config.ConfigurableProvider;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseKeyGenerator;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseMac;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseSecretKeyFactory;
import org.bouncycastle.jcajce.provider.symmetric.util.PBESecretKeyFactory;

public class SHA1 {
   private SHA1() {
   }

   public static class Digest extends BCMessageDigest implements Cloneable {
      public Digest() {
         super(new SHA1Digest());
      }

      @Override
      public Object clone() throws CloneNotSupportedException {
         SHA1.Digest var1 = (SHA1.Digest)super.clone();
         var1.digest = new SHA1Digest((SHA1Digest)this.digest);
         return var1;
      }
   }

   public static class HashMac extends BaseMac {
      public HashMac() {
         super(new HMac(new SHA1Digest()));
      }
   }

   public static class KeyFactory extends BaseSecretKeyFactory {
      public KeyFactory() {
         super("HmacSHA1", null);
      }
   }

   public static class KeyGenerator extends BaseKeyGenerator {
      public KeyGenerator() {
         super("HMACSHA1", 160, new CipherKeyGenerator());
      }
   }

   public static class Mappings extends DigestAlgorithmProvider {
      private static final String PREFIX = SHA1.class.getName();

      @Override
      public void configure(ConfigurableProvider var1) {
         var1.addAlgorithm("MessageDigest.SHA-1", PREFIX + "$Digest");
         var1.addAlgorithm("Alg.Alias.MessageDigest.SHA1", "SHA-1");
         var1.addAlgorithm("Alg.Alias.MessageDigest.SHA", "SHA-1");
         var1.addAlgorithm("Alg.Alias.MessageDigest." + OIWObjectIdentifiers.idSHA1, "SHA-1");
         this.addHMACAlgorithm(var1, "SHA1", PREFIX + "$HashMac", PREFIX + "$KeyGenerator");
         this.addHMACAlias(var1, "SHA1", PKCSObjectIdentifiers.id_hmacWithSHA1);
         this.addHMACAlias(var1, "SHA1", IANAObjectIdentifiers.hmacSHA1);
         var1.addAlgorithm("Mac.PBEWITHHMACSHA", PREFIX + "$SHA1Mac");
         var1.addAlgorithm("Mac.PBEWITHHMACSHA1", PREFIX + "$SHA1Mac");
         var1.addAlgorithm("Alg.Alias.SecretKeyFactory.PBEWITHHMACSHA", "PBEWITHHMACSHA1");
         var1.addAlgorithm("Alg.Alias.SecretKeyFactory." + OIWObjectIdentifiers.idSHA1, "PBEWITHHMACSHA1");
         var1.addAlgorithm("Alg.Alias.Mac." + OIWObjectIdentifiers.idSHA1, "PBEWITHHMACSHA");
         var1.addAlgorithm("SecretKeyFactory.PBEWITHHMACSHA1", PREFIX + "$PBEWithMacKeyFactory");
         var1.addAlgorithm("SecretKeyFactory.HMACSHA1", PREFIX + "$KeyFactory");
         var1.addAlgorithm("Alg.Alias.SecretKeyFactory." + PKCSObjectIdentifiers.id_hmacWithSHA1, "HMACSHA1");
      }
   }

   public static class PBEWithMacKeyFactory extends PBESecretKeyFactory {
      public PBEWithMacKeyFactory() {
         super("PBEwithHmacSHA1", null, false, 2, 1, 160, 0);
      }
   }

   public static class SHA1Mac extends BaseMac {
      public SHA1Mac() {
         super(new HMac(new SHA1Digest()));
      }
   }
}
