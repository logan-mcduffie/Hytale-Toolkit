package org.bouncycastle.jcajce.provider.symmetric;

import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.BufferedBlockCipher;
import org.bouncycastle.crypto.CipherKeyGenerator;
import org.bouncycastle.crypto.engines.SerpentEngine;
import org.bouncycastle.crypto.engines.TnepresEngine;
import org.bouncycastle.crypto.generators.Poly1305KeyGenerator;
import org.bouncycastle.crypto.macs.GMac;
import org.bouncycastle.crypto.modes.CBCBlockCipher;
import org.bouncycastle.crypto.modes.CFBBlockCipher;
import org.bouncycastle.crypto.modes.GCMBlockCipher;
import org.bouncycastle.crypto.modes.OFBBlockCipher;
import org.bouncycastle.internal.asn1.gnu.GNUObjectIdentifiers;
import org.bouncycastle.jcajce.provider.config.ConfigurableProvider;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseBlockCipher;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseKeyGenerator;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseMac;
import org.bouncycastle.jcajce.provider.symmetric.util.BlockCipherProvider;
import org.bouncycastle.jcajce.provider.symmetric.util.IvAlgorithmParameters;

public final class Serpent {
   private Serpent() {
   }

   public static class AlgParams extends IvAlgorithmParameters {
      @Override
      protected String engineToString() {
         return "Serpent IV";
      }
   }

   public static class CBC extends BaseBlockCipher {
      public CBC() {
         super(new CBCBlockCipher(new SerpentEngine()), 128);
      }
   }

   public static class CBC128 extends BaseBlockCipher {
      public CBC128() {
         super(128, new CBCBlockCipher(new SerpentEngine()), 128);
      }
   }

   public static class CBC192 extends BaseBlockCipher {
      public CBC192() {
         super(192, new CBCBlockCipher(new SerpentEngine()), 128);
      }
   }

   public static class CBC256 extends BaseBlockCipher {
      public CBC256() {
         super(256, new CBCBlockCipher(new SerpentEngine()), 128);
      }
   }

   public static class CFB extends BaseBlockCipher {
      public CFB() {
         super(new BufferedBlockCipher(new CFBBlockCipher(new SerpentEngine(), 128)), 128);
      }
   }

   public static class CFB128 extends BaseBlockCipher {
      public CFB128() {
         super(128, new BufferedBlockCipher(new CFBBlockCipher(new SerpentEngine(), 128)), 128);
      }
   }

   public static class CFB192 extends BaseBlockCipher {
      public CFB192() {
         super(192, new BufferedBlockCipher(new CFBBlockCipher(new SerpentEngine(), 128)), 128);
      }
   }

   public static class CFB256 extends BaseBlockCipher {
      public CFB256() {
         super(256, new BufferedBlockCipher(new CFBBlockCipher(new SerpentEngine(), 128)), 128);
      }
   }

   public static class ECB extends BaseBlockCipher {
      public ECB() {
         super(new BlockCipherProvider() {
            @Override
            public BlockCipher get() {
               return new SerpentEngine();
            }
         });
      }
   }

   public static class ECB128 extends BaseBlockCipher {
      public ECB128() {
         super(128, new BlockCipherProvider() {
            @Override
            public BlockCipher get() {
               return new SerpentEngine();
            }
         });
      }
   }

   public static class ECB192 extends BaseBlockCipher {
      public ECB192() {
         super(192, new BlockCipherProvider() {
            @Override
            public BlockCipher get() {
               return new SerpentEngine();
            }
         });
      }
   }

   public static class ECB256 extends BaseBlockCipher {
      public ECB256() {
         super(256, new BlockCipherProvider() {
            @Override
            public BlockCipher get() {
               return new SerpentEngine();
            }
         });
      }
   }

   public static class KeyGen extends BaseKeyGenerator {
      public KeyGen() {
         super("Serpent", 192, new CipherKeyGenerator());
      }
   }

   public static class Mappings extends SymmetricAlgorithmProvider {
      private static final String PREFIX = Serpent.class.getName();

      @Override
      public void configure(ConfigurableProvider var1) {
         var1.addAlgorithm("Cipher.Serpent", PREFIX + "$ECB");
         var1.addAlgorithm("KeyGenerator.Serpent", PREFIX + "$KeyGen");
         var1.addAlgorithm("AlgorithmParameters.Serpent", PREFIX + "$AlgParams");
         var1.addAlgorithm("Cipher.Tnepres", PREFIX + "$TECB");
         var1.addAlgorithm("KeyGenerator.Tnepres", PREFIX + "$TKeyGen");
         var1.addAlgorithm("AlgorithmParameters.Tnepres", PREFIX + "$TAlgParams");
         var1.addAlgorithm("Cipher", GNUObjectIdentifiers.Serpent_128_ECB, PREFIX + "$ECB128");
         var1.addAlgorithm("Cipher", GNUObjectIdentifiers.Serpent_192_ECB, PREFIX + "$ECB192");
         var1.addAlgorithm("Cipher", GNUObjectIdentifiers.Serpent_256_ECB, PREFIX + "$ECB256");
         var1.addAlgorithm("Cipher", GNUObjectIdentifiers.Serpent_128_CBC, PREFIX + "$CBC128");
         var1.addAlgorithm("Cipher", GNUObjectIdentifiers.Serpent_192_CBC, PREFIX + "$CBC192");
         var1.addAlgorithm("Cipher", GNUObjectIdentifiers.Serpent_256_CBC, PREFIX + "$CBC256");
         var1.addAlgorithm("Cipher", GNUObjectIdentifiers.Serpent_128_CFB, PREFIX + "$CFB128");
         var1.addAlgorithm("Cipher", GNUObjectIdentifiers.Serpent_192_CFB, PREFIX + "$CFB192");
         var1.addAlgorithm("Cipher", GNUObjectIdentifiers.Serpent_256_CFB, PREFIX + "$CFB256");
         var1.addAlgorithm("Cipher", GNUObjectIdentifiers.Serpent_128_OFB, PREFIX + "$OFB128");
         var1.addAlgorithm("Cipher", GNUObjectIdentifiers.Serpent_192_OFB, PREFIX + "$OFB192");
         var1.addAlgorithm("Cipher", GNUObjectIdentifiers.Serpent_256_OFB, PREFIX + "$OFB256");
         this.addGMacAlgorithm(var1, "SERPENT", PREFIX + "$SerpentGMAC", PREFIX + "$KeyGen");
         this.addGMacAlgorithm(var1, "TNEPRES", PREFIX + "$TSerpentGMAC", PREFIX + "$TKeyGen");
         this.addPoly1305Algorithm(var1, "SERPENT", PREFIX + "$Poly1305", PREFIX + "$Poly1305KeyGen");
      }
   }

   public static class OFB extends BaseBlockCipher {
      public OFB() {
         super(new BufferedBlockCipher(new OFBBlockCipher(new SerpentEngine(), 128)), 128);
      }
   }

   public static class OFB128 extends BaseBlockCipher {
      public OFB128() {
         super(128, new BufferedBlockCipher(new OFBBlockCipher(new SerpentEngine(), 128)), 128);
      }
   }

   public static class OFB192 extends BaseBlockCipher {
      public OFB192() {
         super(192, new BufferedBlockCipher(new OFBBlockCipher(new SerpentEngine(), 128)), 128);
      }
   }

   public static class OFB256 extends BaseBlockCipher {
      public OFB256() {
         super(256, new BufferedBlockCipher(new OFBBlockCipher(new SerpentEngine(), 128)), 128);
      }
   }

   public static class Poly1305 extends BaseMac {
      public Poly1305() {
         super(new org.bouncycastle.crypto.macs.Poly1305(new SerpentEngine()));
      }
   }

   public static class Poly1305KeyGen extends BaseKeyGenerator {
      public Poly1305KeyGen() {
         super("Poly1305-Serpent", 256, new Poly1305KeyGenerator());
      }
   }

   public static class SerpentGMAC extends BaseMac {
      public SerpentGMAC() {
         super(new GMac(GCMBlockCipher.newInstance(new SerpentEngine())));
      }
   }

   public static class TAlgParams extends IvAlgorithmParameters {
      @Override
      protected String engineToString() {
         return "Tnepres IV";
      }
   }

   public static class TECB extends BaseBlockCipher {
      public TECB() {
         super(new BlockCipherProvider() {
            @Override
            public BlockCipher get() {
               return new TnepresEngine();
            }
         });
      }
   }

   public static class TKeyGen extends BaseKeyGenerator {
      public TKeyGen() {
         super("Tnepres", 192, new CipherKeyGenerator());
      }
   }

   public static class TSerpentGMAC extends BaseMac {
      public TSerpentGMAC() {
         super(new GMac(GCMBlockCipher.newInstance(new TnepresEngine())));
      }
   }
}
