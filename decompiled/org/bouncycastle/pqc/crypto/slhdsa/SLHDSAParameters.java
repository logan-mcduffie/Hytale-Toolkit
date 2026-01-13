package org.bouncycastle.pqc.crypto.slhdsa;

public class SLHDSAParameters {
   public static final int TYPE_PURE = 0;
   public static final int TYPE_SHA2_256 = 1;
   public static final int TYPE_SHA2_512 = 2;
   public static final int TYPE_SHAKE128 = 3;
   public static final int TYPE_SHAKE256 = 4;
   public static final SLHDSAParameters sha2_128f = new SLHDSAParameters("sha2-128f", new SLHDSAParameters.Sha2EngineProvider(16, 16, 22, 6, 33, 66), 0);
   public static final SLHDSAParameters sha2_128s = new SLHDSAParameters("sha2-128s", new SLHDSAParameters.Sha2EngineProvider(16, 16, 7, 12, 14, 63), 0);
   public static final SLHDSAParameters sha2_192f = new SLHDSAParameters("sha2-192f", new SLHDSAParameters.Sha2EngineProvider(24, 16, 22, 8, 33, 66), 0);
   public static final SLHDSAParameters sha2_192s = new SLHDSAParameters("sha2-192s", new SLHDSAParameters.Sha2EngineProvider(24, 16, 7, 14, 17, 63), 0);
   public static final SLHDSAParameters sha2_256f = new SLHDSAParameters("sha2-256f", new SLHDSAParameters.Sha2EngineProvider(32, 16, 17, 9, 35, 68), 0);
   public static final SLHDSAParameters sha2_256s = new SLHDSAParameters("sha2-256s", new SLHDSAParameters.Sha2EngineProvider(32, 16, 8, 14, 22, 64), 0);
   public static final SLHDSAParameters shake_128f = new SLHDSAParameters("shake-128f", new SLHDSAParameters.Shake256EngineProvider(16, 16, 22, 6, 33, 66), 0);
   public static final SLHDSAParameters shake_128s = new SLHDSAParameters("shake-128s", new SLHDSAParameters.Shake256EngineProvider(16, 16, 7, 12, 14, 63), 0);
   public static final SLHDSAParameters shake_192f = new SLHDSAParameters("shake-192f", new SLHDSAParameters.Shake256EngineProvider(24, 16, 22, 8, 33, 66), 0);
   public static final SLHDSAParameters shake_192s = new SLHDSAParameters("shake-192s", new SLHDSAParameters.Shake256EngineProvider(24, 16, 7, 14, 17, 63), 0);
   public static final SLHDSAParameters shake_256f = new SLHDSAParameters("shake-256f", new SLHDSAParameters.Shake256EngineProvider(32, 16, 17, 9, 35, 68), 0);
   public static final SLHDSAParameters shake_256s = new SLHDSAParameters("shake-256s", new SLHDSAParameters.Shake256EngineProvider(32, 16, 8, 14, 22, 64), 0);
   public static final SLHDSAParameters sha2_128f_with_sha256 = new SLHDSAParameters(
      "sha2-128f-with-sha256", new SLHDSAParameters.Sha2EngineProvider(16, 16, 22, 6, 33, 66), 1
   );
   public static final SLHDSAParameters sha2_128s_with_sha256 = new SLHDSAParameters(
      "sha2-128s-with-sha256", new SLHDSAParameters.Sha2EngineProvider(16, 16, 7, 12, 14, 63), 1
   );
   public static final SLHDSAParameters sha2_192f_with_sha512 = new SLHDSAParameters(
      "sha2-192f-with-sha512", new SLHDSAParameters.Sha2EngineProvider(24, 16, 22, 8, 33, 66), 2
   );
   public static final SLHDSAParameters sha2_192s_with_sha512 = new SLHDSAParameters(
      "sha2-192s-with-sha512", new SLHDSAParameters.Sha2EngineProvider(24, 16, 7, 14, 17, 63), 2
   );
   public static final SLHDSAParameters sha2_256f_with_sha512 = new SLHDSAParameters(
      "sha2-256f-with-sha512", new SLHDSAParameters.Sha2EngineProvider(32, 16, 17, 9, 35, 68), 2
   );
   public static final SLHDSAParameters sha2_256s_with_sha512 = new SLHDSAParameters(
      "sha2-256s-with-sha512", new SLHDSAParameters.Sha2EngineProvider(32, 16, 8, 14, 22, 64), 2
   );
   public static final SLHDSAParameters shake_128f_with_shake128 = new SLHDSAParameters(
      "shake-128f-with-shake128", new SLHDSAParameters.Shake256EngineProvider(16, 16, 22, 6, 33, 66), 3
   );
   public static final SLHDSAParameters shake_128s_with_shake128 = new SLHDSAParameters(
      "shake-128s-with-shake128", new SLHDSAParameters.Shake256EngineProvider(16, 16, 7, 12, 14, 63), 3
   );
   public static final SLHDSAParameters shake_192f_with_shake256 = new SLHDSAParameters(
      "shake-192f-with-shake256", new SLHDSAParameters.Shake256EngineProvider(24, 16, 22, 8, 33, 66), 4
   );
   public static final SLHDSAParameters shake_192s_with_shake256 = new SLHDSAParameters(
      "shake-192s-with-shake256", new SLHDSAParameters.Shake256EngineProvider(24, 16, 7, 14, 17, 63), 4
   );
   public static final SLHDSAParameters shake_256f_with_shake256 = new SLHDSAParameters(
      "shake-256f-with-shake256", new SLHDSAParameters.Shake256EngineProvider(32, 16, 17, 9, 35, 68), 4
   );
   public static final SLHDSAParameters shake_256s_with_shake256 = new SLHDSAParameters(
      "shake-256s-with-shake256", new SLHDSAParameters.Shake256EngineProvider(32, 16, 8, 14, 22, 64), 4
   );
   private final String name;
   private final SLHDSAEngineProvider engineProvider;
   private final int preHashDigest;

   private SLHDSAParameters(String var1, SLHDSAEngineProvider var2, int var3) {
      this.name = var1;
      this.engineProvider = var2;
      this.preHashDigest = var3;
   }

   public String getName() {
      return this.name;
   }

   public int getType() {
      return this.preHashDigest;
   }

   public int getN() {
      return this.engineProvider.getN();
   }

   SLHDSAEngine getEngine() {
      return this.engineProvider.get();
   }

   public boolean isPreHash() {
      return this.preHashDigest != 0;
   }

   private static class Sha2EngineProvider implements SLHDSAEngineProvider {
      private final int n;
      private final int w;
      private final int d;
      private final int a;
      private final int k;
      private final int h;

      public Sha2EngineProvider(int var1, int var2, int var3, int var4, int var5, int var6) {
         this.n = var1;
         this.w = var2;
         this.d = var3;
         this.a = var4;
         this.k = var5;
         this.h = var6;
      }

      @Override
      public int getN() {
         return this.n;
      }

      @Override
      public SLHDSAEngine get() {
         return new SLHDSAEngine.Sha2Engine(this.n, this.w, this.d, this.a, this.k, this.h);
      }
   }

   private static class Shake256EngineProvider implements SLHDSAEngineProvider {
      private final int n;
      private final int w;
      private final int d;
      private final int a;
      private final int k;
      private final int h;

      public Shake256EngineProvider(int var1, int var2, int var3, int var4, int var5, int var6) {
         this.n = var1;
         this.w = var2;
         this.d = var3;
         this.a = var4;
         this.k = var5;
         this.h = var6;
      }

      @Override
      public int getN() {
         return this.n;
      }

      @Override
      public SLHDSAEngine get() {
         return new SLHDSAEngine.Shake256Engine(this.n, this.w, this.d, this.a, this.k, this.h);
      }
   }
}
