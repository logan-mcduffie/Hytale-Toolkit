package org.bouncycastle.jcajce.provider.digest;

import org.bouncycastle.crypto.CipherKeyGenerator;
import org.bouncycastle.crypto.digests.SkeinDigest;
import org.bouncycastle.crypto.macs.HMac;
import org.bouncycastle.crypto.macs.SkeinMac;
import org.bouncycastle.jcajce.provider.config.ConfigurableProvider;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseKeyGenerator;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseMac;

public class Skein {
   private Skein() {
   }

   public static class DigestSkein1024 extends BCMessageDigest implements Cloneable {
      public DigestSkein1024(int var1) {
         super(new SkeinDigest(1024, var1));
      }

      @Override
      public Object clone() throws CloneNotSupportedException {
         BCMessageDigest var1 = (BCMessageDigest)super.clone();
         var1.digest = new SkeinDigest((SkeinDigest)this.digest);
         return var1;
      }
   }

   public static class DigestSkein256 extends BCMessageDigest implements Cloneable {
      public DigestSkein256(int var1) {
         super(new SkeinDigest(256, var1));
      }

      @Override
      public Object clone() throws CloneNotSupportedException {
         BCMessageDigest var1 = (BCMessageDigest)super.clone();
         var1.digest = new SkeinDigest((SkeinDigest)this.digest);
         return var1;
      }
   }

   public static class DigestSkein512 extends BCMessageDigest implements Cloneable {
      public DigestSkein512(int var1) {
         super(new SkeinDigest(512, var1));
      }

      @Override
      public Object clone() throws CloneNotSupportedException {
         BCMessageDigest var1 = (BCMessageDigest)super.clone();
         var1.digest = new SkeinDigest((SkeinDigest)this.digest);
         return var1;
      }
   }

   public static class Digest_1024_1024 extends Skein.DigestSkein1024 {
      public Digest_1024_1024() {
         super(1024);
      }
   }

   public static class Digest_1024_384 extends Skein.DigestSkein1024 {
      public Digest_1024_384() {
         super(384);
      }
   }

   public static class Digest_1024_512 extends Skein.DigestSkein1024 {
      public Digest_1024_512() {
         super(512);
      }
   }

   public static class Digest_256_128 extends Skein.DigestSkein256 {
      public Digest_256_128() {
         super(128);
      }
   }

   public static class Digest_256_160 extends Skein.DigestSkein256 {
      public Digest_256_160() {
         super(160);
      }
   }

   public static class Digest_256_224 extends Skein.DigestSkein256 {
      public Digest_256_224() {
         super(224);
      }
   }

   public static class Digest_256_256 extends Skein.DigestSkein256 {
      public Digest_256_256() {
         super(256);
      }
   }

   public static class Digest_512_128 extends Skein.DigestSkein512 {
      public Digest_512_128() {
         super(128);
      }
   }

   public static class Digest_512_160 extends Skein.DigestSkein512 {
      public Digest_512_160() {
         super(160);
      }
   }

   public static class Digest_512_224 extends Skein.DigestSkein512 {
      public Digest_512_224() {
         super(224);
      }
   }

   public static class Digest_512_256 extends Skein.DigestSkein512 {
      public Digest_512_256() {
         super(256);
      }
   }

   public static class Digest_512_384 extends Skein.DigestSkein512 {
      public Digest_512_384() {
         super(384);
      }
   }

   public static class Digest_512_512 extends Skein.DigestSkein512 {
      public Digest_512_512() {
         super(512);
      }
   }

   public static class HMacKeyGenerator_1024_1024 extends BaseKeyGenerator {
      public HMacKeyGenerator_1024_1024() {
         super("HMACSkein-1024-1024", 1024, new CipherKeyGenerator());
      }
   }

   public static class HMacKeyGenerator_1024_384 extends BaseKeyGenerator {
      public HMacKeyGenerator_1024_384() {
         super("HMACSkein-1024-384", 384, new CipherKeyGenerator());
      }
   }

   public static class HMacKeyGenerator_1024_512 extends BaseKeyGenerator {
      public HMacKeyGenerator_1024_512() {
         super("HMACSkein-1024-512", 512, new CipherKeyGenerator());
      }
   }

   public static class HMacKeyGenerator_256_128 extends BaseKeyGenerator {
      public HMacKeyGenerator_256_128() {
         super("HMACSkein-256-128", 128, new CipherKeyGenerator());
      }
   }

   public static class HMacKeyGenerator_256_160 extends BaseKeyGenerator {
      public HMacKeyGenerator_256_160() {
         super("HMACSkein-256-160", 160, new CipherKeyGenerator());
      }
   }

   public static class HMacKeyGenerator_256_224 extends BaseKeyGenerator {
      public HMacKeyGenerator_256_224() {
         super("HMACSkein-256-224", 224, new CipherKeyGenerator());
      }
   }

   public static class HMacKeyGenerator_256_256 extends BaseKeyGenerator {
      public HMacKeyGenerator_256_256() {
         super("HMACSkein-256-256", 256, new CipherKeyGenerator());
      }
   }

   public static class HMacKeyGenerator_512_128 extends BaseKeyGenerator {
      public HMacKeyGenerator_512_128() {
         super("HMACSkein-512-128", 128, new CipherKeyGenerator());
      }
   }

   public static class HMacKeyGenerator_512_160 extends BaseKeyGenerator {
      public HMacKeyGenerator_512_160() {
         super("HMACSkein-512-160", 160, new CipherKeyGenerator());
      }
   }

   public static class HMacKeyGenerator_512_224 extends BaseKeyGenerator {
      public HMacKeyGenerator_512_224() {
         super("HMACSkein-512-224", 224, new CipherKeyGenerator());
      }
   }

   public static class HMacKeyGenerator_512_256 extends BaseKeyGenerator {
      public HMacKeyGenerator_512_256() {
         super("HMACSkein-512-256", 256, new CipherKeyGenerator());
      }
   }

   public static class HMacKeyGenerator_512_384 extends BaseKeyGenerator {
      public HMacKeyGenerator_512_384() {
         super("HMACSkein-512-384", 384, new CipherKeyGenerator());
      }
   }

   public static class HMacKeyGenerator_512_512 extends BaseKeyGenerator {
      public HMacKeyGenerator_512_512() {
         super("HMACSkein-512-512", 512, new CipherKeyGenerator());
      }
   }

   public static class HashMac_1024_1024 extends BaseMac {
      public HashMac_1024_1024() {
         super(new HMac(new SkeinDigest(1024, 1024)));
      }
   }

   public static class HashMac_1024_384 extends BaseMac {
      public HashMac_1024_384() {
         super(new HMac(new SkeinDigest(1024, 384)));
      }
   }

   public static class HashMac_1024_512 extends BaseMac {
      public HashMac_1024_512() {
         super(new HMac(new SkeinDigest(1024, 512)));
      }
   }

   public static class HashMac_256_128 extends BaseMac {
      public HashMac_256_128() {
         super(new HMac(new SkeinDigest(256, 128)));
      }
   }

   public static class HashMac_256_160 extends BaseMac {
      public HashMac_256_160() {
         super(new HMac(new SkeinDigest(256, 160)));
      }
   }

   public static class HashMac_256_224 extends BaseMac {
      public HashMac_256_224() {
         super(new HMac(new SkeinDigest(256, 224)));
      }
   }

   public static class HashMac_256_256 extends BaseMac {
      public HashMac_256_256() {
         super(new HMac(new SkeinDigest(256, 256)));
      }
   }

   public static class HashMac_512_128 extends BaseMac {
      public HashMac_512_128() {
         super(new HMac(new SkeinDigest(512, 128)));
      }
   }

   public static class HashMac_512_160 extends BaseMac {
      public HashMac_512_160() {
         super(new HMac(new SkeinDigest(512, 160)));
      }
   }

   public static class HashMac_512_224 extends BaseMac {
      public HashMac_512_224() {
         super(new HMac(new SkeinDigest(512, 224)));
      }
   }

   public static class HashMac_512_256 extends BaseMac {
      public HashMac_512_256() {
         super(new HMac(new SkeinDigest(512, 256)));
      }
   }

   public static class HashMac_512_384 extends BaseMac {
      public HashMac_512_384() {
         super(new HMac(new SkeinDigest(512, 384)));
      }
   }

   public static class HashMac_512_512 extends BaseMac {
      public HashMac_512_512() {
         super(new HMac(new SkeinDigest(512, 512)));
      }
   }

   public static class Mappings extends DigestAlgorithmProvider {
      private static final String PREFIX = Skein.class.getName();

      @Override
      public void configure(ConfigurableProvider var1) {
         var1.addAlgorithm("MessageDigest.Skein-256-128", PREFIX + "$Digest_256_128");
         var1.addAlgorithm("MessageDigest.Skein-256-160", PREFIX + "$Digest_256_160");
         var1.addAlgorithm("MessageDigest.Skein-256-224", PREFIX + "$Digest_256_224");
         var1.addAlgorithm("MessageDigest.Skein-256-256", PREFIX + "$Digest_256_256");
         var1.addAlgorithm("MessageDigest.Skein-512-128", PREFIX + "$Digest_512_128");
         var1.addAlgorithm("MessageDigest.Skein-512-160", PREFIX + "$Digest_512_160");
         var1.addAlgorithm("MessageDigest.Skein-512-224", PREFIX + "$Digest_512_224");
         var1.addAlgorithm("MessageDigest.Skein-512-256", PREFIX + "$Digest_512_256");
         var1.addAlgorithm("MessageDigest.Skein-512-384", PREFIX + "$Digest_512_384");
         var1.addAlgorithm("MessageDigest.Skein-512-512", PREFIX + "$Digest_512_512");
         var1.addAlgorithm("MessageDigest.Skein-1024-384", PREFIX + "$Digest_1024_384");
         var1.addAlgorithm("MessageDigest.Skein-1024-512", PREFIX + "$Digest_1024_512");
         var1.addAlgorithm("MessageDigest.Skein-1024-1024", PREFIX + "$Digest_1024_1024");
         this.addHMACAlgorithm(var1, "Skein-256-128", PREFIX + "$HashMac_256_128", PREFIX + "$HMacKeyGenerator_256_128");
         this.addHMACAlgorithm(var1, "Skein-256-160", PREFIX + "$HashMac_256_160", PREFIX + "$HMacKeyGenerator_256_160");
         this.addHMACAlgorithm(var1, "Skein-256-224", PREFIX + "$HashMac_256_224", PREFIX + "$HMacKeyGenerator_256_224");
         this.addHMACAlgorithm(var1, "Skein-256-256", PREFIX + "$HashMac_256_256", PREFIX + "$HMacKeyGenerator_256_256");
         this.addHMACAlgorithm(var1, "Skein-512-128", PREFIX + "$HashMac_512_128", PREFIX + "$HMacKeyGenerator_512_128");
         this.addHMACAlgorithm(var1, "Skein-512-160", PREFIX + "$HashMac_512_160", PREFIX + "$HMacKeyGenerator_512_160");
         this.addHMACAlgorithm(var1, "Skein-512-224", PREFIX + "$HashMac_512_224", PREFIX + "$HMacKeyGenerator_512_224");
         this.addHMACAlgorithm(var1, "Skein-512-256", PREFIX + "$HashMac_512_256", PREFIX + "$HMacKeyGenerator_512_256");
         this.addHMACAlgorithm(var1, "Skein-512-384", PREFIX + "$HashMac_512_384", PREFIX + "$HMacKeyGenerator_512_384");
         this.addHMACAlgorithm(var1, "Skein-512-512", PREFIX + "$HashMac_512_512", PREFIX + "$HMacKeyGenerator_512_512");
         this.addHMACAlgorithm(var1, "Skein-1024-384", PREFIX + "$HashMac_1024_384", PREFIX + "$HMacKeyGenerator_1024_384");
         this.addHMACAlgorithm(var1, "Skein-1024-512", PREFIX + "$HashMac_1024_512", PREFIX + "$HMacKeyGenerator_1024_512");
         this.addHMACAlgorithm(var1, "Skein-1024-1024", PREFIX + "$HashMac_1024_1024", PREFIX + "$HMacKeyGenerator_1024_1024");
         this.addSkeinMacAlgorithm(var1, 256, 128);
         this.addSkeinMacAlgorithm(var1, 256, 160);
         this.addSkeinMacAlgorithm(var1, 256, 224);
         this.addSkeinMacAlgorithm(var1, 256, 256);
         this.addSkeinMacAlgorithm(var1, 512, 128);
         this.addSkeinMacAlgorithm(var1, 512, 160);
         this.addSkeinMacAlgorithm(var1, 512, 224);
         this.addSkeinMacAlgorithm(var1, 512, 256);
         this.addSkeinMacAlgorithm(var1, 512, 384);
         this.addSkeinMacAlgorithm(var1, 512, 512);
         this.addSkeinMacAlgorithm(var1, 1024, 384);
         this.addSkeinMacAlgorithm(var1, 1024, 512);
         this.addSkeinMacAlgorithm(var1, 1024, 1024);
      }

      private void addSkeinMacAlgorithm(ConfigurableProvider var1, int var2, int var3) {
         String var4 = "Skein-MAC-" + var2 + "-" + var3;
         String var5 = PREFIX + "$SkeinMac_" + var2 + "_" + var3;
         String var6 = PREFIX + "$SkeinMacKeyGenerator_" + var2 + "_" + var3;
         var1.addAlgorithm("Mac." + var4, var5);
         var1.addAlgorithm("Alg.Alias.Mac.Skein-MAC" + var2 + "/" + var3, var4);
         var1.addAlgorithm("KeyGenerator." + var4, var6);
         var1.addAlgorithm("Alg.Alias.KeyGenerator.Skein-MAC" + var2 + "/" + var3, var4);
      }
   }

   public static class SkeinMacKeyGenerator_1024_1024 extends BaseKeyGenerator {
      public SkeinMacKeyGenerator_1024_1024() {
         super("Skein-MAC-1024-1024", 1024, new CipherKeyGenerator());
      }
   }

   public static class SkeinMacKeyGenerator_1024_384 extends BaseKeyGenerator {
      public SkeinMacKeyGenerator_1024_384() {
         super("Skein-MAC-1024-384", 384, new CipherKeyGenerator());
      }
   }

   public static class SkeinMacKeyGenerator_1024_512 extends BaseKeyGenerator {
      public SkeinMacKeyGenerator_1024_512() {
         super("Skein-MAC-1024-512", 512, new CipherKeyGenerator());
      }
   }

   public static class SkeinMacKeyGenerator_256_128 extends BaseKeyGenerator {
      public SkeinMacKeyGenerator_256_128() {
         super("Skein-MAC-256-128", 128, new CipherKeyGenerator());
      }
   }

   public static class SkeinMacKeyGenerator_256_160 extends BaseKeyGenerator {
      public SkeinMacKeyGenerator_256_160() {
         super("Skein-MAC-256-160", 160, new CipherKeyGenerator());
      }
   }

   public static class SkeinMacKeyGenerator_256_224 extends BaseKeyGenerator {
      public SkeinMacKeyGenerator_256_224() {
         super("Skein-MAC-256-224", 224, new CipherKeyGenerator());
      }
   }

   public static class SkeinMacKeyGenerator_256_256 extends BaseKeyGenerator {
      public SkeinMacKeyGenerator_256_256() {
         super("Skein-MAC-256-256", 256, new CipherKeyGenerator());
      }
   }

   public static class SkeinMacKeyGenerator_512_128 extends BaseKeyGenerator {
      public SkeinMacKeyGenerator_512_128() {
         super("Skein-MAC-512-128", 128, new CipherKeyGenerator());
      }
   }

   public static class SkeinMacKeyGenerator_512_160 extends BaseKeyGenerator {
      public SkeinMacKeyGenerator_512_160() {
         super("Skein-MAC-512-160", 160, new CipherKeyGenerator());
      }
   }

   public static class SkeinMacKeyGenerator_512_224 extends BaseKeyGenerator {
      public SkeinMacKeyGenerator_512_224() {
         super("Skein-MAC-512-224", 224, new CipherKeyGenerator());
      }
   }

   public static class SkeinMacKeyGenerator_512_256 extends BaseKeyGenerator {
      public SkeinMacKeyGenerator_512_256() {
         super("Skein-MAC-512-256", 256, new CipherKeyGenerator());
      }
   }

   public static class SkeinMacKeyGenerator_512_384 extends BaseKeyGenerator {
      public SkeinMacKeyGenerator_512_384() {
         super("Skein-MAC-512-384", 384, new CipherKeyGenerator());
      }
   }

   public static class SkeinMacKeyGenerator_512_512 extends BaseKeyGenerator {
      public SkeinMacKeyGenerator_512_512() {
         super("Skein-MAC-512-512", 512, new CipherKeyGenerator());
      }
   }

   public static class SkeinMac_1024_1024 extends BaseMac {
      public SkeinMac_1024_1024() {
         super(new SkeinMac(1024, 1024));
      }
   }

   public static class SkeinMac_1024_384 extends BaseMac {
      public SkeinMac_1024_384() {
         super(new SkeinMac(1024, 384));
      }
   }

   public static class SkeinMac_1024_512 extends BaseMac {
      public SkeinMac_1024_512() {
         super(new SkeinMac(1024, 512));
      }
   }

   public static class SkeinMac_256_128 extends BaseMac {
      public SkeinMac_256_128() {
         super(new SkeinMac(256, 128));
      }
   }

   public static class SkeinMac_256_160 extends BaseMac {
      public SkeinMac_256_160() {
         super(new SkeinMac(256, 160));
      }
   }

   public static class SkeinMac_256_224 extends BaseMac {
      public SkeinMac_256_224() {
         super(new SkeinMac(256, 224));
      }
   }

   public static class SkeinMac_256_256 extends BaseMac {
      public SkeinMac_256_256() {
         super(new SkeinMac(256, 256));
      }
   }

   public static class SkeinMac_512_128 extends BaseMac {
      public SkeinMac_512_128() {
         super(new SkeinMac(512, 128));
      }
   }

   public static class SkeinMac_512_160 extends BaseMac {
      public SkeinMac_512_160() {
         super(new SkeinMac(512, 160));
      }
   }

   public static class SkeinMac_512_224 extends BaseMac {
      public SkeinMac_512_224() {
         super(new SkeinMac(512, 224));
      }
   }

   public static class SkeinMac_512_256 extends BaseMac {
      public SkeinMac_512_256() {
         super(new SkeinMac(512, 256));
      }
   }

   public static class SkeinMac_512_384 extends BaseMac {
      public SkeinMac_512_384() {
         super(new SkeinMac(512, 384));
      }
   }

   public static class SkeinMac_512_512 extends BaseMac {
      public SkeinMac_512_512() {
         super(new SkeinMac(512, 512));
      }
   }
}
