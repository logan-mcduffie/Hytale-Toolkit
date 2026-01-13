package org.bouncycastle.jcajce.provider.digest;

import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.nist.NISTObjectIdentifiers;
import org.bouncycastle.crypto.CipherKeyGenerator;
import org.bouncycastle.crypto.digests.ParallelHash;
import org.bouncycastle.crypto.digests.SHA3Digest;
import org.bouncycastle.crypto.digests.SHAKEDigest;
import org.bouncycastle.crypto.digests.TupleHash;
import org.bouncycastle.crypto.macs.HMac;
import org.bouncycastle.crypto.macs.KMAC;
import org.bouncycastle.jcajce.provider.config.ConfigurableProvider;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseKeyGenerator;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseMac;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseSecretKeyFactory;

public class SHA3 {
   private SHA3() {
   }

   public static class Digest224 extends SHA3.DigestSHA3 {
      public Digest224() {
         super(224);
      }
   }

   public static class Digest256 extends SHA3.DigestSHA3 {
      public Digest256() {
         super(256);
      }
   }

   public static class Digest384 extends SHA3.DigestSHA3 {
      public Digest384() {
         super(384);
      }
   }

   public static class Digest512 extends SHA3.DigestSHA3 {
      public Digest512() {
         super(512);
      }
   }

   public static class DigestParallelHash extends BCMessageDigest implements Cloneable {
      public DigestParallelHash(int var1, int var2) {
         super(new ParallelHash(var1, null, 128, var2));
      }

      @Override
      public Object clone() throws CloneNotSupportedException {
         BCMessageDigest var1 = (BCMessageDigest)super.clone();
         var1.digest = new ParallelHash((ParallelHash)this.digest);
         return var1;
      }
   }

   public static class DigestParallelHash128_256 extends SHA3.DigestParallelHash {
      public DigestParallelHash128_256() {
         super(128, 256);
      }
   }

   public static class DigestParallelHash256_512 extends SHA3.DigestParallelHash {
      public DigestParallelHash256_512() {
         super(256, 512);
      }
   }

   public static class DigestSHA3 extends BCMessageDigest implements Cloneable {
      public DigestSHA3(int var1) {
         super(new SHA3Digest(var1));
      }

      @Override
      public Object clone() throws CloneNotSupportedException {
         BCMessageDigest var1 = (BCMessageDigest)super.clone();
         var1.digest = new SHA3Digest((SHA3Digest)this.digest);
         return var1;
      }
   }

   public static class DigestSHAKE extends BCMessageDigest implements Cloneable {
      public DigestSHAKE(int var1, int var2) {
         super(new SHAKEDigest(var1));
      }

      @Override
      public Object clone() throws CloneNotSupportedException {
         BCMessageDigest var1 = (BCMessageDigest)super.clone();
         var1.digest = new SHAKEDigest((SHAKEDigest)this.digest);
         return var1;
      }
   }

   public static class DigestShake128_256 extends SHA3.DigestSHAKE {
      public DigestShake128_256() {
         super(128, 256);
      }
   }

   public static class DigestShake256_512 extends SHA3.DigestSHAKE {
      public DigestShake256_512() {
         super(256, 512);
      }
   }

   public static class DigestTupleHash extends BCMessageDigest implements Cloneable {
      public DigestTupleHash(int var1, int var2) {
         super(new TupleHash(var1, null, var2));
      }

      @Override
      public Object clone() throws CloneNotSupportedException {
         BCMessageDigest var1 = (BCMessageDigest)super.clone();
         var1.digest = new TupleHash((TupleHash)this.digest);
         return var1;
      }
   }

   public static class DigestTupleHash128_256 extends SHA3.DigestTupleHash {
      public DigestTupleHash128_256() {
         super(128, 256);
      }
   }

   public static class DigestTupleHash256_512 extends SHA3.DigestTupleHash {
      public DigestTupleHash256_512() {
         super(256, 512);
      }
   }

   public static class HashMac224 extends SHA3.HashMacSHA3 {
      public HashMac224() {
         super(224);
      }
   }

   public static class HashMac256 extends SHA3.HashMacSHA3 {
      public HashMac256() {
         super(256);
      }
   }

   public static class HashMac384 extends SHA3.HashMacSHA3 {
      public HashMac384() {
         super(384);
      }
   }

   public static class HashMac512 extends SHA3.HashMacSHA3 {
      public HashMac512() {
         super(512);
      }
   }

   public static class HashMacSHA3 extends BaseMac {
      public HashMacSHA3(int var1) {
         super(new HMac(new SHA3Digest(var1)));
      }
   }

   public static class KMac128 extends BaseMac {
      public KMac128() {
         super(new KMAC(128, new byte[0]));
      }
   }

   public static class KMac256 extends BaseMac {
      public KMac256() {
         super(new KMAC(256, new byte[0]));
      }
   }

   public static class KeyFactory224 extends SHA3.KeyFactorySHA3 {
      public KeyFactory224() {
         super(224, NISTObjectIdentifiers.id_hmacWithSHA3_224);
      }
   }

   public static class KeyFactory256 extends SHA3.KeyFactorySHA3 {
      public KeyFactory256() {
         super(256, NISTObjectIdentifiers.id_hmacWithSHA3_256);
      }
   }

   public static class KeyFactory384 extends SHA3.KeyFactorySHA3 {
      public KeyFactory384() {
         super(384, NISTObjectIdentifiers.id_hmacWithSHA3_384);
      }
   }

   public static class KeyFactory512 extends SHA3.KeyFactorySHA3 {
      public KeyFactory512() {
         super(512, NISTObjectIdentifiers.id_hmacWithSHA3_512);
      }
   }

   public static class KeyFactoryKMAC extends BaseSecretKeyFactory {
      public KeyFactoryKMAC(int var1, ASN1ObjectIdentifier var2) {
         super("KMAC" + var1, var2);
      }
   }

   public static class KeyFactoryKMAC128 extends SHA3.KeyFactoryKMAC {
      public KeyFactoryKMAC128() {
         super(128, NISTObjectIdentifiers.id_KmacWithSHAKE128);
      }
   }

   public static class KeyFactoryKMAC256 extends SHA3.KeyFactoryKMAC {
      public KeyFactoryKMAC256() {
         super(256, NISTObjectIdentifiers.id_KmacWithSHAKE256);
      }
   }

   public static class KeyFactorySHA3 extends BaseSecretKeyFactory {
      public KeyFactorySHA3(int var1, ASN1ObjectIdentifier var2) {
         super("HmacSHA3-" + var1, var2);
      }
   }

   public static class KeyGenerator224 extends SHA3.KeyGeneratorSHA3 {
      public KeyGenerator224() {
         super(224);
      }
   }

   public static class KeyGenerator256 extends SHA3.KeyGeneratorSHA3 {
      public KeyGenerator256() {
         super(256);
      }
   }

   public static class KeyGenerator384 extends SHA3.KeyGeneratorSHA3 {
      public KeyGenerator384() {
         super(384);
      }
   }

   public static class KeyGenerator512 extends SHA3.KeyGeneratorSHA3 {
      public KeyGenerator512() {
         super(512);
      }
   }

   public static class KeyGeneratorSHA3 extends BaseKeyGenerator {
      public KeyGeneratorSHA3(int var1) {
         super("HMACSHA3-" + var1, var1, new CipherKeyGenerator());
      }
   }

   public static class Mappings extends DigestAlgorithmProvider {
      private static final String PREFIX = SHA3.class.getName();

      @Override
      public void configure(ConfigurableProvider var1) {
         var1.addAlgorithm("MessageDigest.SHA3-224", PREFIX + "$Digest224");
         var1.addAlgorithm("MessageDigest.SHA3-256", PREFIX + "$Digest256");
         var1.addAlgorithm("MessageDigest.SHA3-384", PREFIX + "$Digest384");
         var1.addAlgorithm("MessageDigest.SHA3-512", PREFIX + "$Digest512");
         var1.addAlgorithm("MessageDigest", NISTObjectIdentifiers.id_sha3_224, PREFIX + "$Digest224");
         var1.addAlgorithm("MessageDigest", NISTObjectIdentifiers.id_sha3_256, PREFIX + "$Digest256");
         var1.addAlgorithm("MessageDigest", NISTObjectIdentifiers.id_sha3_384, PREFIX + "$Digest384");
         var1.addAlgorithm("MessageDigest", NISTObjectIdentifiers.id_sha3_512, PREFIX + "$Digest512");
         var1.addAlgorithm("MessageDigest.SHAKE256-512", PREFIX + "$DigestShake256_512");
         var1.addAlgorithm("MessageDigest.SHAKE128-256", PREFIX + "$DigestShake128_256");
         var1.addAlgorithm("MessageDigest", NISTObjectIdentifiers.id_shake256, PREFIX + "$DigestShake256_512");
         var1.addAlgorithm("MessageDigest", NISTObjectIdentifiers.id_shake128, PREFIX + "$DigestShake128_256");
         var1.addAlgorithm("Alg.Alias.MessageDigest.SHAKE256", "SHAKE256-512");
         var1.addAlgorithm("Alg.Alias.MessageDigest.SHAKE128", "SHAKE128-256");
         this.addHMACAlgorithm(var1, "SHA3-224", PREFIX + "$HashMac224", PREFIX + "$KeyGenerator224");
         this.addHMACAlias(var1, "SHA3-224", NISTObjectIdentifiers.id_hmacWithSHA3_224);
         var1.addAlgorithm("SecretKeyFactory.HMACSHA3-224", PREFIX + "$KeyFactory224");
         var1.addAlgorithm("Alg.Alias.SecretKeyFactory." + NISTObjectIdentifiers.id_hmacWithSHA3_224, "HMACSHA3-224");
         this.addHMACAlgorithm(var1, "SHA3-256", PREFIX + "$HashMac256", PREFIX + "$KeyGenerator256");
         this.addHMACAlias(var1, "SHA3-256", NISTObjectIdentifiers.id_hmacWithSHA3_256);
         var1.addAlgorithm("SecretKeyFactory.HMACSHA3-256", PREFIX + "$KeyFactory256");
         var1.addAlgorithm("Alg.Alias.SecretKeyFactory." + NISTObjectIdentifiers.id_hmacWithSHA3_256, "HMACSHA3-256");
         this.addHMACAlgorithm(var1, "SHA3-384", PREFIX + "$HashMac384", PREFIX + "$KeyGenerator384");
         this.addHMACAlias(var1, "SHA3-384", NISTObjectIdentifiers.id_hmacWithSHA3_384);
         var1.addAlgorithm("SecretKeyFactory.HMACSHA3-384", PREFIX + "$KeyFactory384");
         var1.addAlgorithm("Alg.Alias.SecretKeyFactory." + NISTObjectIdentifiers.id_hmacWithSHA3_384, "HMACSHA3-384");
         this.addHMACAlgorithm(var1, "SHA3-512", PREFIX + "$HashMac512", PREFIX + "$KeyGenerator512");
         this.addHMACAlias(var1, "SHA3-512", NISTObjectIdentifiers.id_hmacWithSHA3_512);
         var1.addAlgorithm("SecretKeyFactory.HMACSHA3-512", PREFIX + "$KeyFactory512");
         var1.addAlgorithm("Alg.Alias.SecretKeyFactory." + NISTObjectIdentifiers.id_hmacWithSHA3_512, "HMACSHA3-512");
         this.addKMACAlgorithm(var1, "128", PREFIX + "$KMac128", PREFIX + "$KeyGenerator256");
         var1.addAlgorithm("SecretKeyFactory.KMAC128", PREFIX + "$KeyFactoryKMAC128");
         var1.addAlgorithm("Alg.Alias.SecretKeyFactory." + NISTObjectIdentifiers.id_Kmac128, "KMAC128");
         this.addKMACAlgorithm(var1, "256", PREFIX + "$KMac256", PREFIX + "$KeyGenerator512");
         var1.addAlgorithm("SecretKeyFactory.KMAC256", PREFIX + "$KeyFactoryKMAC256");
         var1.addAlgorithm("Alg.Alias.SecretKeyFactory." + NISTObjectIdentifiers.id_Kmac256, "KMAC256");
         var1.addAlgorithm("MessageDigest.TUPLEHASH256-512", PREFIX + "$DigestTupleHash256_512");
         var1.addAlgorithm("MessageDigest.TUPLEHASH128-256", PREFIX + "$DigestTupleHash128_256");
         var1.addAlgorithm("Alg.Alias.MessageDigest.TUPLEHASH256", "TUPLEHASH256-512");
         var1.addAlgorithm("Alg.Alias.MessageDigest.TUPLEHASH128", "TUPLEHASH128-256");
         var1.addAlgorithm("MessageDigest.PARALLELHASH256-512", PREFIX + "$DigestParallelHash256_512");
         var1.addAlgorithm("MessageDigest.PARALLELHASH128-256", PREFIX + "$DigestParallelHash128_256");
         var1.addAlgorithm("Alg.Alias.MessageDigest.PARALLELHASH256", "PARALLELHASH256-512");
         var1.addAlgorithm("Alg.Alias.MessageDigest.PARALLELHASH128", "PARALLELHASH128-256");
      }
   }
}
