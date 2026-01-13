package org.bouncycastle.math.raw;

public class Interleave {
   private static final long M32 = 1431655765L;
   private static final long M64 = 6148914691236517205L;
   private static final long M64R = -6148914691236517206L;

   public static int expand8to16(int var0) {
      var0 &= 255;
      var0 = (var0 | var0 << 4) & 3855;
      var0 = (var0 | var0 << 2) & 13107;
      return (var0 | var0 << 1) & 21845;
   }

   public static int expand16to32(int var0) {
      var0 &= 65535;
      var0 = (var0 | var0 << 8) & 16711935;
      var0 = (var0 | var0 << 4) & 252645135;
      var0 = (var0 | var0 << 2) & 858993459;
      return (var0 | var0 << 1) & 1431655765;
   }

   public static long expand32to64(int var0) {
      var0 = Bits.bitPermuteStep(var0, 65280, 8);
      var0 = Bits.bitPermuteStep(var0, 15728880, 4);
      var0 = Bits.bitPermuteStep(var0, 202116108, 2);
      var0 = Bits.bitPermuteStep(var0, 572662306, 1);
      return (var0 >>> 1 & 1431655765L) << 32 | var0 & 1431655765L;
   }

   public static void expand64To128(long var0, long[] var2, int var3) {
      var0 = Bits.bitPermuteStep(var0, 4294901760L, 16);
      var0 = Bits.bitPermuteStep(var0, 280375465148160L, 8);
      var0 = Bits.bitPermuteStep(var0, 67555025218437360L, 4);
      var0 = Bits.bitPermuteStep(var0, 868082074056920076L, 2);
      var0 = Bits.bitPermuteStep(var0, 2459565876494606882L, 1);
      var2[var3] = var0 & 6148914691236517205L;
      var2[var3 + 1] = var0 >>> 1 & 6148914691236517205L;
   }

   public static void expand64To128(long[] var0, int var1, int var2, long[] var3, int var4) {
      for (int var5 = 0; var5 < var2; var5++) {
         expand64To128(var0[var1 + var5], var3, var4);
         var4 += 2;
      }
   }

   public static void expand64To128Rev(long var0, long[] var2, int var3) {
      var0 = Bits.bitPermuteStep(var0, 4294901760L, 16);
      var0 = Bits.bitPermuteStep(var0, 280375465148160L, 8);
      var0 = Bits.bitPermuteStep(var0, 67555025218437360L, 4);
      var0 = Bits.bitPermuteStep(var0, 868082074056920076L, 2);
      var0 = Bits.bitPermuteStep(var0, 2459565876494606882L, 1);
      var2[var3] = var0 & -6148914691236517206L;
      var2[var3 + 1] = var0 << 1 & -6148914691236517206L;
   }

   public static int shuffle(int var0) {
      var0 = Bits.bitPermuteStep(var0, 65280, 8);
      var0 = Bits.bitPermuteStep(var0, 15728880, 4);
      var0 = Bits.bitPermuteStep(var0, 202116108, 2);
      return Bits.bitPermuteStep(var0, 572662306, 1);
   }

   public static long shuffle(long var0) {
      var0 = Bits.bitPermuteStep(var0, 4294901760L, 16);
      var0 = Bits.bitPermuteStep(var0, 280375465148160L, 8);
      var0 = Bits.bitPermuteStep(var0, 67555025218437360L, 4);
      var0 = Bits.bitPermuteStep(var0, 868082074056920076L, 2);
      return Bits.bitPermuteStep(var0, 2459565876494606882L, 1);
   }

   public static int shuffle2(int var0) {
      var0 = Bits.bitPermuteStep(var0, 11141290, 7);
      var0 = Bits.bitPermuteStep(var0, 52428, 14);
      var0 = Bits.bitPermuteStep(var0, 15728880, 4);
      return Bits.bitPermuteStep(var0, 65280, 8);
   }

   public static long shuffle2(long var0) {
      var0 = Bits.bitPermuteStep(var0, 4278255360L, 24);
      var0 = Bits.bitPermuteStep(var0, 57421771435671756L, 6);
      var0 = Bits.bitPermuteStep(var0, 264913582878960L, 12);
      return Bits.bitPermuteStep(var0, 723401728380766730L, 3);
   }

   public static long shuffle3(long var0) {
      var0 = Bits.bitPermuteStep(var0, 47851476196393130L, 7);
      var0 = Bits.bitPermuteStep(var0, 225176545447116L, 14);
      return Bits.bitPermuteStep(var0, 4042322160L, 28);
   }

   public static int unshuffle(int var0) {
      var0 = Bits.bitPermuteStep(var0, 572662306, 1);
      var0 = Bits.bitPermuteStep(var0, 202116108, 2);
      var0 = Bits.bitPermuteStep(var0, 15728880, 4);
      return Bits.bitPermuteStep(var0, 65280, 8);
   }

   public static long unshuffle(long var0) {
      var0 = Bits.bitPermuteStep(var0, 2459565876494606882L, 1);
      var0 = Bits.bitPermuteStep(var0, 868082074056920076L, 2);
      var0 = Bits.bitPermuteStep(var0, 67555025218437360L, 4);
      var0 = Bits.bitPermuteStep(var0, 280375465148160L, 8);
      return Bits.bitPermuteStep(var0, 4294901760L, 16);
   }

   public static int unshuffle2(int var0) {
      var0 = Bits.bitPermuteStep(var0, 65280, 8);
      var0 = Bits.bitPermuteStep(var0, 15728880, 4);
      var0 = Bits.bitPermuteStep(var0, 52428, 14);
      return Bits.bitPermuteStep(var0, 11141290, 7);
   }

   public static long unshuffle2(long var0) {
      var0 = Bits.bitPermuteStep(var0, 723401728380766730L, 3);
      var0 = Bits.bitPermuteStep(var0, 264913582878960L, 12);
      var0 = Bits.bitPermuteStep(var0, 57421771435671756L, 6);
      return Bits.bitPermuteStep(var0, 4278255360L, 24);
   }

   public static long unshuffle3(long var0) {
      return shuffle3(var0);
   }
}
