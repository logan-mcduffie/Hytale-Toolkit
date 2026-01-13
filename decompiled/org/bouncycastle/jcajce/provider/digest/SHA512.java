package org.bouncycastle.jcajce.provider.digest;

import org.bouncycastle.asn1.nist.NISTObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.crypto.CipherKeyGenerator;
import org.bouncycastle.crypto.digests.SHA512Digest;
import org.bouncycastle.crypto.digests.SHA512tDigest;
import org.bouncycastle.crypto.macs.HMac;
import org.bouncycastle.crypto.macs.OldHMac;
import org.bouncycastle.jcajce.provider.config.ConfigurableProvider;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseKeyGenerator;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseMac;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseSecretKeyFactory;

public class SHA512 {
   private SHA512() {
   }

   public static class Digest extends BCMessageDigest implements Cloneable {
      public Digest() {
         super(new SHA512Digest());
      }

      @Override
      public Object clone() throws CloneNotSupportedException {
         SHA512.Digest var1 = (SHA512.Digest)super.clone();
         var1.digest = new SHA512Digest((SHA512Digest)this.digest);
         return var1;
      }
   }

   public static class DigestT extends BCMessageDigest implements Cloneable {
      public DigestT(int var1) {
         super(new SHA512tDigest(var1));
      }

      @Override
      public Object clone() throws CloneNotSupportedException {
         SHA512.DigestT var1 = (SHA512.DigestT)super.clone();
         var1.digest = new SHA512tDigest((SHA512tDigest)this.digest);
         return var1;
      }
   }

   public static class DigestT224 extends SHA512.DigestT {
      public DigestT224() {
         super(224);
      }
   }

   public static class DigestT256 extends SHA512.DigestT {
      public DigestT256() {
         super(256);
      }
   }

   public static class HashMac extends BaseMac {
      public HashMac() {
         super(new HMac(new SHA512Digest()));
      }
   }

   public static class HashMacT224 extends BaseMac {
      public HashMacT224() {
         super(new HMac(new SHA512tDigest(224)));
      }
   }

   public static class HashMacT256 extends BaseMac {
      public HashMacT256() {
         super(new HMac(new SHA512tDigest(256)));
      }
   }

   public static class KeyFactory extends BaseSecretKeyFactory {
      public KeyFactory() {
         super("HmacSHA512", null);
      }
   }

   public static class KeyFactory224 extends BaseSecretKeyFactory {
      public KeyFactory224() {
         super("HmacSHA512/224", null);
      }
   }

   public static class KeyFactory256 extends BaseSecretKeyFactory {
      public KeyFactory256() {
         super("HmacSHA512/256", null);
      }
   }

   public static class KeyGenerator extends BaseKeyGenerator {
      public KeyGenerator() {
         super("HMACSHA512", 512, new CipherKeyGenerator());
      }
   }

   public static class KeyGeneratorT224 extends BaseKeyGenerator {
      public KeyGeneratorT224() {
         super("HMACSHA512/224", 224, new CipherKeyGenerator());
      }
   }

   public static class KeyGeneratorT256 extends BaseKeyGenerator {
      public KeyGeneratorT256() {
         super("HMACSHA512/256", 256, new CipherKeyGenerator());
      }
   }

   public static class Mappings extends DigestAlgorithmProvider {
      private static final String PREFIX = SHA512.class.getName();

      @Override
      public void configure(ConfigurableProvider var1) {
         var1.addAlgorithm("MessageDigest.SHA-512", PREFIX + "$Digest");
         var1.addAlgorithm("Alg.Alias.MessageDigest.SHA512", "SHA-512");
         var1.addAlgorithm("Alg.Alias.MessageDigest." + NISTObjectIdentifiers.id_sha512, "SHA-512");
         var1.addAlgorithm("MessageDigest.SHA-512/224", PREFIX + "$DigestT224");
         var1.addAlgorithm("Alg.Alias.MessageDigest.SHA512/224", "SHA-512/224");
         var1.addAlgorithm("Alg.Alias.MessageDigest.SHA512224", "SHA-512/224");
         var1.addAlgorithm("Alg.Alias.MessageDigest.SHA-512(224)", "SHA-512/224");
         var1.addAlgorithm("Alg.Alias.MessageDigest.SHA512(224)", "SHA-512/224");
         var1.addAlgorithm("Alg.Alias.MessageDigest." + NISTObjectIdentifiers.id_sha512_224, "SHA-512/224");
         var1.addAlgorithm("MessageDigest.SHA-512/256", PREFIX + "$DigestT256");
         var1.addAlgorithm("Alg.Alias.MessageDigest.SHA512/256", "SHA-512/256");
         var1.addAlgorithm("Alg.Alias.MessageDigest.SHA512256", "SHA-512/256");
         var1.addAlgorithm("Alg.Alias.MessageDigest.SHA-512(256)", "SHA-512/256");
         var1.addAlgorithm("Alg.Alias.MessageDigest.SHA512(256)", "SHA-512/256");
         var1.addAlgorithm("Alg.Alias.MessageDigest." + NISTObjectIdentifiers.id_sha512_256, "SHA-512/256");
         var1.addAlgorithm("Mac.OLDHMACSHA512", PREFIX + "$OldSHA512");
         var1.addAlgorithm("Mac.PBEWITHHMACSHA512", PREFIX + "$HashMac");
         this.addHMACAlgorithm(var1, "SHA512", PREFIX + "$HashMac", PREFIX + "$KeyGenerator");
         this.addHMACAlias(var1, "SHA512", PKCSObjectIdentifiers.id_hmacWithSHA512);
         this.addHMACAlgorithm(var1, "SHA512/224", PREFIX + "$HashMacT224", PREFIX + "$KeyGeneratorT224");
         this.addHMACAlgorithm(var1, "SHA512/256", PREFIX + "$HashMacT256", PREFIX + "$KeyGeneratorT256");
         var1.addAlgorithm("SecretKeyFactory.HMACSHA512", PREFIX + "$KeyFactory");
         var1.addAlgorithm("Alg.Alias.SecretKeyFactory." + PKCSObjectIdentifiers.id_hmacWithSHA512, "HMACSHA512");
         var1.addAlgorithm("SecretKeyFactory.HMACSHA512/224", PREFIX + "$KeyFactory224");
         var1.addAlgorithm("Alg.Alias.SecretKeyFactory.HMACSHA512(224)", "HMACSHA512/224");
         var1.addAlgorithm("Alg.Alias.SecretKeyFactory." + PKCSObjectIdentifiers.id_hmacWithSHA512_224, "HMACSHA512/224");
         var1.addAlgorithm("SecretKeyFactory.HMACSHA512/256", PREFIX + "$KeyFactory256");
         var1.addAlgorithm("Alg.Alias.SecretKeyFactory.HMACSHA512(256)", "HMACSHA512/256");
         var1.addAlgorithm("Alg.Alias.SecretKeyFactory." + PKCSObjectIdentifiers.id_hmacWithSHA512_256, "HMACSHA512/256");
      }
   }

   public static class OldSHA512 extends BaseMac {
      public OldSHA512() {
         super(new OldHMac(new SHA512Digest()));
      }
   }
}
