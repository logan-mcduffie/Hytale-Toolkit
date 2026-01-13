package org.bouncycastle.math.ec.rfc8032;

import org.bouncycastle.math.raw.Nat;
import org.bouncycastle.math.raw.Nat448;

abstract class Scalar448 {
   static final int SIZE = 14;
   private static final int SCALAR_BYTES = 57;
   private static final long M26L = 67108863L;
   private static final long M28L = 268435455L;
   private static final long M32L = 4294967295L;
   private static final int TARGET_LENGTH = 447;
   private static final int[] L = new int[]{
      -1420278541, 595116690, -1916432555, 560775794, -1361693040, -1001465015, 2093622249, -1, -1, -1, -1, -1, -1, 1073741823
   };
   private static final int[] LSq = new int[]{
      463601321,
      -1045562440,
      1239460018,
      -1189350089,
      -412821483,
      1160071467,
      -1564970643,
      1256291574,
      -1170454588,
      -240530412,
      2118977290,
      -1845154869,
      -1618855054,
      -1019204973,
      1437344377,
      -1849925303,
      1189267370,
      280387897,
      -680846520,
      -500732508,
      -1100672524,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      268435455
   };
   private static final int L_0 = 78101261;
   private static final int L_1 = 141809365;
   private static final int L_2 = 175155932;
   private static final int L_3 = 64542499;
   private static final int L_4 = 158326419;
   private static final int L_5 = 191173276;
   private static final int L_6 = 104575268;
   private static final int L_7 = 137584065;
   private static final int L4_0 = 43969588;
   private static final int L4_1 = 30366549;
   private static final int L4_2 = 163752818;
   private static final int L4_3 = 258169998;
   private static final int L4_4 = 96434764;
   private static final int L4_5 = 227822194;
   private static final int L4_6 = 149865618;
   private static final int L4_7 = 550336261;

   static boolean checkVar(byte[] var0, int[] var1) {
      if (var0[56] != 0) {
         return false;
      } else {
         decode(var0, var1);
         return !Nat.gte(14, var1, L);
      }
   }

   static void decode(byte[] var0, int[] var1) {
      Codec.decode32(var0, 0, var1, 0, 14);
   }

   static void getOrderWnafVar(int var0, byte[] var1) {
      Wnaf.getSignedVar(L, var0, var1);
   }

   static void multiply225Var(int[] var0, int[] var1, int[] var2) {
      int[] var3 = new int[22];
      Nat.mul(var1, 0, 8, var0, 0, 14, var3, 0);
      if (var1[7] < 0) {
         Nat.addTo(14, L, 0, var3, 8);
         Nat.subFrom(14, var0, 0, var3, 8);
      }

      byte[] var4 = new byte[88];
      Codec.encode32(var3, 0, 22, var4, 0);
      byte[] var5 = reduce704(var4);
      decode(var5, var2);
   }

   static byte[] reduce704(byte[] var0) {
      long var1 = Codec.decode32(var0, 0) & 4294967295L;
      long var3 = Codec.decode24(var0, 4) << 4 & 4294967295L;
      long var5 = Codec.decode32(var0, 7) & 4294967295L;
      long var7 = Codec.decode24(var0, 11) << 4 & 4294967295L;
      long var9 = Codec.decode32(var0, 14) & 4294967295L;
      long var11 = Codec.decode24(var0, 18) << 4 & 4294967295L;
      long var13 = Codec.decode32(var0, 21) & 4294967295L;
      long var15 = Codec.decode24(var0, 25) << 4 & 4294967295L;
      long var17 = Codec.decode32(var0, 28) & 4294967295L;
      long var19 = Codec.decode24(var0, 32) << 4 & 4294967295L;
      long var21 = Codec.decode32(var0, 35) & 4294967295L;
      long var23 = Codec.decode24(var0, 39) << 4 & 4294967295L;
      long var25 = Codec.decode32(var0, 42) & 4294967295L;
      long var27 = Codec.decode24(var0, 46) << 4 & 4294967295L;
      long var29 = Codec.decode32(var0, 49) & 4294967295L;
      long var31 = Codec.decode24(var0, 53) << 4 & 4294967295L;
      long var33 = Codec.decode32(var0, 56) & 4294967295L;
      long var35 = Codec.decode24(var0, 60) << 4 & 4294967295L;
      long var37 = Codec.decode32(var0, 63) & 4294967295L;
      long var39 = Codec.decode24(var0, 67) << 4 & 4294967295L;
      long var41 = Codec.decode32(var0, 70) & 4294967295L;
      long var43 = Codec.decode24(var0, 74) << 4 & 4294967295L;
      long var45 = Codec.decode32(var0, 77) & 4294967295L;
      long var47 = Codec.decode24(var0, 81) << 4 & 4294967295L;
      long var49 = Codec.decode32(var0, 84) & 4294967295L;
      long var51 = 0L;
      var51 += var49 >>> 28;
      var49 &= 268435455L;
      var19 += var51 * 43969588L;
      var21 += var51 * 30366549L;
      var23 += var51 * 163752818L;
      var25 += var51 * 258169998L;
      var27 += var51 * 96434764L;
      var29 += var51 * 227822194L;
      var31 += var51 * 149865618L;
      var33 += var51 * 550336261L;
      var43 += var41 >>> 28;
      var41 &= 268435455L;
      var45 += var43 >>> 28;
      var43 &= 268435455L;
      var47 += var45 >>> 28;
      var45 &= 268435455L;
      var49 += var47 >>> 28;
      var47 &= 268435455L;
      var17 += var49 * 43969588L;
      var19 += var49 * 30366549L;
      var21 += var49 * 163752818L;
      var23 += var49 * 258169998L;
      var25 += var49 * 96434764L;
      var27 += var49 * 227822194L;
      var29 += var49 * 149865618L;
      var31 += var49 * 550336261L;
      var15 += var47 * 43969588L;
      var17 += var47 * 30366549L;
      var19 += var47 * 163752818L;
      var21 += var47 * 258169998L;
      var23 += var47 * 96434764L;
      var25 += var47 * 227822194L;
      var27 += var47 * 149865618L;
      var29 += var47 * 550336261L;
      var13 += var45 * 43969588L;
      var15 += var45 * 30366549L;
      var17 += var45 * 163752818L;
      var19 += var45 * 258169998L;
      var21 += var45 * 96434764L;
      var23 += var45 * 227822194L;
      var25 += var45 * 149865618L;
      var27 += var45 * 550336261L;
      var37 += var35 >>> 28;
      var35 &= 268435455L;
      var39 += var37 >>> 28;
      var37 &= 268435455L;
      var41 += var39 >>> 28;
      var39 &= 268435455L;
      var43 += var41 >>> 28;
      var41 &= 268435455L;
      var11 += var43 * 43969588L;
      var13 += var43 * 30366549L;
      var15 += var43 * 163752818L;
      var17 += var43 * 258169998L;
      var19 += var43 * 96434764L;
      var21 += var43 * 227822194L;
      var23 += var43 * 149865618L;
      var25 += var43 * 550336261L;
      var9 += var41 * 43969588L;
      var11 += var41 * 30366549L;
      var13 += var41 * 163752818L;
      var15 += var41 * 258169998L;
      var17 += var41 * 96434764L;
      var19 += var41 * 227822194L;
      var21 += var41 * 149865618L;
      var23 += var41 * 550336261L;
      var7 += var39 * 43969588L;
      var9 += var39 * 30366549L;
      var11 += var39 * 163752818L;
      var13 += var39 * 258169998L;
      var15 += var39 * 96434764L;
      var17 += var39 * 227822194L;
      var19 += var39 * 149865618L;
      var21 += var39 * 550336261L;
      var31 += var29 >>> 28;
      var29 &= 268435455L;
      var33 += var31 >>> 28;
      var31 &= 268435455L;
      var35 += var33 >>> 28;
      var33 &= 268435455L;
      var37 += var35 >>> 28;
      var35 &= 268435455L;
      var5 += var37 * 43969588L;
      var7 += var37 * 30366549L;
      var9 += var37 * 163752818L;
      var11 += var37 * 258169998L;
      var13 += var37 * 96434764L;
      var15 += var37 * 227822194L;
      var17 += var37 * 149865618L;
      var19 += var37 * 550336261L;
      var3 += var35 * 43969588L;
      var5 += var35 * 30366549L;
      var7 += var35 * 163752818L;
      var9 += var35 * 258169998L;
      var11 += var35 * 96434764L;
      var13 += var35 * 227822194L;
      var15 += var35 * 149865618L;
      var17 += var35 * 550336261L;
      var33 *= 4L;
      var33 += var31 >>> 26;
      var31 &= 67108863L;
      var1 += ++var33 * 78101261L;
      var3 += var33 * 141809365L;
      var5 += var33 * 175155932L;
      var7 += var33 * 64542499L;
      var9 += var33 * 158326419L;
      var11 += var33 * 191173276L;
      var13 += var33 * 104575268L;
      var15 += var33 * 137584065L;
      var3 += var1 >>> 28;
      var1 &= 268435455L;
      var5 += var3 >>> 28;
      var3 &= 268435455L;
      var7 += var5 >>> 28;
      var5 &= 268435455L;
      var9 += var7 >>> 28;
      var7 &= 268435455L;
      var11 += var9 >>> 28;
      var9 &= 268435455L;
      var13 += var11 >>> 28;
      var11 &= 268435455L;
      var15 += var13 >>> 28;
      var13 &= 268435455L;
      var17 += var15 >>> 28;
      var15 &= 268435455L;
      var19 += var17 >>> 28;
      var17 &= 268435455L;
      var21 += var19 >>> 28;
      var19 &= 268435455L;
      var23 += var21 >>> 28;
      var21 &= 268435455L;
      var25 += var23 >>> 28;
      var23 &= 268435455L;
      var27 += var25 >>> 28;
      var25 &= 268435455L;
      var29 += var27 >>> 28;
      var27 &= 268435455L;
      var31 += var29 >>> 28;
      var29 &= 268435455L;
      var33 = var31 >>> 26;
      var31 &= 67108863L;
      var33--;
      var1 -= var33 & 78101261L;
      var3 -= var33 & 141809365L;
      var5 -= var33 & 175155932L;
      var7 -= var33 & 64542499L;
      var9 -= var33 & 158326419L;
      var11 -= var33 & 191173276L;
      var13 -= var33 & 104575268L;
      var15 -= var33 & 137584065L;
      var3 += var1 >> 28;
      var1 &= 268435455L;
      var5 += var3 >> 28;
      var3 &= 268435455L;
      var7 += var5 >> 28;
      var5 &= 268435455L;
      var9 += var7 >> 28;
      var7 &= 268435455L;
      var11 += var9 >> 28;
      var9 &= 268435455L;
      var13 += var11 >> 28;
      var11 &= 268435455L;
      var15 += var13 >> 28;
      var13 &= 268435455L;
      var17 += var15 >> 28;
      var15 &= 268435455L;
      var19 += var17 >> 28;
      var17 &= 268435455L;
      var21 += var19 >> 28;
      var19 &= 268435455L;
      var23 += var21 >> 28;
      var21 &= 268435455L;
      var25 += var23 >> 28;
      var23 &= 268435455L;
      var27 += var25 >> 28;
      var25 &= 268435455L;
      var29 += var27 >> 28;
      var27 &= 268435455L;
      var31 += var29 >> 28;
      var29 &= 268435455L;
      byte[] var53 = new byte[57];
      Codec.encode56(var1 | var3 << 28, var53, 0);
      Codec.encode56(var5 | var7 << 28, var53, 7);
      Codec.encode56(var9 | var11 << 28, var53, 14);
      Codec.encode56(var13 | var15 << 28, var53, 21);
      Codec.encode56(var17 | var19 << 28, var53, 28);
      Codec.encode56(var21 | var23 << 28, var53, 35);
      Codec.encode56(var25 | var27 << 28, var53, 42);
      Codec.encode56(var29 | var31 << 28, var53, 49);
      return var53;
   }

   static byte[] reduce912(byte[] var0) {
      long var1 = Codec.decode32(var0, 0) & 4294967295L;
      long var3 = Codec.decode24(var0, 4) << 4 & 4294967295L;
      long var5 = Codec.decode32(var0, 7) & 4294967295L;
      long var7 = Codec.decode24(var0, 11) << 4 & 4294967295L;
      long var9 = Codec.decode32(var0, 14) & 4294967295L;
      long var11 = Codec.decode24(var0, 18) << 4 & 4294967295L;
      long var13 = Codec.decode32(var0, 21) & 4294967295L;
      long var15 = Codec.decode24(var0, 25) << 4 & 4294967295L;
      long var17 = Codec.decode32(var0, 28) & 4294967295L;
      long var19 = Codec.decode24(var0, 32) << 4 & 4294967295L;
      long var21 = Codec.decode32(var0, 35) & 4294967295L;
      long var23 = Codec.decode24(var0, 39) << 4 & 4294967295L;
      long var25 = Codec.decode32(var0, 42) & 4294967295L;
      long var27 = Codec.decode24(var0, 46) << 4 & 4294967295L;
      long var29 = Codec.decode32(var0, 49) & 4294967295L;
      long var31 = Codec.decode24(var0, 53) << 4 & 4294967295L;
      long var33 = Codec.decode32(var0, 56) & 4294967295L;
      long var35 = Codec.decode24(var0, 60) << 4 & 4294967295L;
      long var37 = Codec.decode32(var0, 63) & 4294967295L;
      long var39 = Codec.decode24(var0, 67) << 4 & 4294967295L;
      long var41 = Codec.decode32(var0, 70) & 4294967295L;
      long var43 = Codec.decode24(var0, 74) << 4 & 4294967295L;
      long var45 = Codec.decode32(var0, 77) & 4294967295L;
      long var47 = Codec.decode24(var0, 81) << 4 & 4294967295L;
      long var49 = Codec.decode32(var0, 84) & 4294967295L;
      long var51 = Codec.decode24(var0, 88) << 4 & 4294967295L;
      long var53 = Codec.decode32(var0, 91) & 4294967295L;
      long var55 = Codec.decode24(var0, 95) << 4 & 4294967295L;
      long var57 = Codec.decode32(var0, 98) & 4294967295L;
      long var59 = Codec.decode24(var0, 102) << 4 & 4294967295L;
      long var61 = Codec.decode32(var0, 105) & 4294967295L;
      long var63 = Codec.decode24(var0, 109) << 4 & 4294967295L;
      long var65 = Codec.decode16(var0, 112) & 4294967295L;
      var33 += var65 * 43969588L;
      var35 += var65 * 30366549L;
      var37 += var65 * 163752818L;
      var39 += var65 * 258169998L;
      var41 += var65 * 96434764L;
      var43 += var65 * 227822194L;
      var45 += var65 * 149865618L;
      var47 += var65 * 550336261L;
      var63 += var61 >>> 28;
      var61 &= 268435455L;
      var31 += var63 * 43969588L;
      var33 += var63 * 30366549L;
      var35 += var63 * 163752818L;
      var37 += var63 * 258169998L;
      var39 += var63 * 96434764L;
      var41 += var63 * 227822194L;
      var43 += var63 * 149865618L;
      var45 += var63 * 550336261L;
      var29 += var61 * 43969588L;
      var31 += var61 * 30366549L;
      var33 += var61 * 163752818L;
      var35 += var61 * 258169998L;
      var37 += var61 * 96434764L;
      var39 += var61 * 227822194L;
      var41 += var61 * 149865618L;
      var43 += var61 * 550336261L;
      var59 += var57 >>> 28;
      var57 &= 268435455L;
      var27 += var59 * 43969588L;
      var29 += var59 * 30366549L;
      var31 += var59 * 163752818L;
      var33 += var59 * 258169998L;
      var35 += var59 * 96434764L;
      var37 += var59 * 227822194L;
      var39 += var59 * 149865618L;
      var41 += var59 * 550336261L;
      var25 += var57 * 43969588L;
      var27 += var57 * 30366549L;
      var29 += var57 * 163752818L;
      var31 += var57 * 258169998L;
      var33 += var57 * 96434764L;
      var35 += var57 * 227822194L;
      var37 += var57 * 149865618L;
      var39 += var57 * 550336261L;
      var55 += var53 >>> 28;
      var53 &= 268435455L;
      var23 += var55 * 43969588L;
      var25 += var55 * 30366549L;
      var27 += var55 * 163752818L;
      var29 += var55 * 258169998L;
      var31 += var55 * 96434764L;
      var33 += var55 * 227822194L;
      var35 += var55 * 149865618L;
      var37 += var55 * 550336261L;
      var21 += var53 * 43969588L;
      var23 += var53 * 30366549L;
      var25 += var53 * 163752818L;
      var27 += var53 * 258169998L;
      var29 += var53 * 96434764L;
      var31 += var53 * 227822194L;
      var33 += var53 * 149865618L;
      var35 += var53 * 550336261L;
      var51 += var49 >>> 28;
      var49 &= 268435455L;
      var19 += var51 * 43969588L;
      var21 += var51 * 30366549L;
      var23 += var51 * 163752818L;
      var25 += var51 * 258169998L;
      var27 += var51 * 96434764L;
      var29 += var51 * 227822194L;
      var31 += var51 * 149865618L;
      var33 += var51 * 550336261L;
      var43 += var41 >>> 28;
      var41 &= 268435455L;
      var45 += var43 >>> 28;
      var43 &= 268435455L;
      var47 += var45 >>> 28;
      var45 &= 268435455L;
      var49 += var47 >>> 28;
      var47 &= 268435455L;
      var17 += var49 * 43969588L;
      var19 += var49 * 30366549L;
      var21 += var49 * 163752818L;
      var23 += var49 * 258169998L;
      var25 += var49 * 96434764L;
      var27 += var49 * 227822194L;
      var29 += var49 * 149865618L;
      var31 += var49 * 550336261L;
      var15 += var47 * 43969588L;
      var17 += var47 * 30366549L;
      var19 += var47 * 163752818L;
      var21 += var47 * 258169998L;
      var23 += var47 * 96434764L;
      var25 += var47 * 227822194L;
      var27 += var47 * 149865618L;
      var29 += var47 * 550336261L;
      var13 += var45 * 43969588L;
      var15 += var45 * 30366549L;
      var17 += var45 * 163752818L;
      var19 += var45 * 258169998L;
      var21 += var45 * 96434764L;
      var23 += var45 * 227822194L;
      var25 += var45 * 149865618L;
      var27 += var45 * 550336261L;
      var37 += var35 >>> 28;
      var35 &= 268435455L;
      var39 += var37 >>> 28;
      var37 &= 268435455L;
      var41 += var39 >>> 28;
      var39 &= 268435455L;
      var43 += var41 >>> 28;
      var41 &= 268435455L;
      var11 += var43 * 43969588L;
      var13 += var43 * 30366549L;
      var15 += var43 * 163752818L;
      var17 += var43 * 258169998L;
      var19 += var43 * 96434764L;
      var21 += var43 * 227822194L;
      var23 += var43 * 149865618L;
      var25 += var43 * 550336261L;
      var9 += var41 * 43969588L;
      var11 += var41 * 30366549L;
      var13 += var41 * 163752818L;
      var15 += var41 * 258169998L;
      var17 += var41 * 96434764L;
      var19 += var41 * 227822194L;
      var21 += var41 * 149865618L;
      var23 += var41 * 550336261L;
      var7 += var39 * 43969588L;
      var9 += var39 * 30366549L;
      var11 += var39 * 163752818L;
      var13 += var39 * 258169998L;
      var15 += var39 * 96434764L;
      var17 += var39 * 227822194L;
      var19 += var39 * 149865618L;
      var21 += var39 * 550336261L;
      var31 += var29 >>> 28;
      var29 &= 268435455L;
      var33 += var31 >>> 28;
      var31 &= 268435455L;
      var35 += var33 >>> 28;
      var33 &= 268435455L;
      var37 += var35 >>> 28;
      var35 &= 268435455L;
      var5 += var37 * 43969588L;
      var7 += var37 * 30366549L;
      var9 += var37 * 163752818L;
      var11 += var37 * 258169998L;
      var13 += var37 * 96434764L;
      var15 += var37 * 227822194L;
      var17 += var37 * 149865618L;
      var19 += var37 * 550336261L;
      var3 += var35 * 43969588L;
      var5 += var35 * 30366549L;
      var7 += var35 * 163752818L;
      var9 += var35 * 258169998L;
      var11 += var35 * 96434764L;
      var13 += var35 * 227822194L;
      var15 += var35 * 149865618L;
      var17 += var35 * 550336261L;
      var33 *= 4L;
      var33 += var31 >>> 26;
      var31 &= 67108863L;
      var1 += ++var33 * 78101261L;
      var3 += var33 * 141809365L;
      var5 += var33 * 175155932L;
      var7 += var33 * 64542499L;
      var9 += var33 * 158326419L;
      var11 += var33 * 191173276L;
      var13 += var33 * 104575268L;
      var15 += var33 * 137584065L;
      var3 += var1 >>> 28;
      var1 &= 268435455L;
      var5 += var3 >>> 28;
      var3 &= 268435455L;
      var7 += var5 >>> 28;
      var5 &= 268435455L;
      var9 += var7 >>> 28;
      var7 &= 268435455L;
      var11 += var9 >>> 28;
      var9 &= 268435455L;
      var13 += var11 >>> 28;
      var11 &= 268435455L;
      var15 += var13 >>> 28;
      var13 &= 268435455L;
      var17 += var15 >>> 28;
      var15 &= 268435455L;
      var19 += var17 >>> 28;
      var17 &= 268435455L;
      var21 += var19 >>> 28;
      var19 &= 268435455L;
      var23 += var21 >>> 28;
      var21 &= 268435455L;
      var25 += var23 >>> 28;
      var23 &= 268435455L;
      var27 += var25 >>> 28;
      var25 &= 268435455L;
      var29 += var27 >>> 28;
      var27 &= 268435455L;
      var31 += var29 >>> 28;
      var29 &= 268435455L;
      var33 = var31 >>> 26;
      var31 &= 67108863L;
      var33--;
      var1 -= var33 & 78101261L;
      var3 -= var33 & 141809365L;
      var5 -= var33 & 175155932L;
      var7 -= var33 & 64542499L;
      var9 -= var33 & 158326419L;
      var11 -= var33 & 191173276L;
      var13 -= var33 & 104575268L;
      var15 -= var33 & 137584065L;
      var3 += var1 >> 28;
      var1 &= 268435455L;
      var5 += var3 >> 28;
      var3 &= 268435455L;
      var7 += var5 >> 28;
      var5 &= 268435455L;
      var9 += var7 >> 28;
      var7 &= 268435455L;
      var11 += var9 >> 28;
      var9 &= 268435455L;
      var13 += var11 >> 28;
      var11 &= 268435455L;
      var15 += var13 >> 28;
      var13 &= 268435455L;
      var17 += var15 >> 28;
      var15 &= 268435455L;
      var19 += var17 >> 28;
      var17 &= 268435455L;
      var21 += var19 >> 28;
      var19 &= 268435455L;
      var23 += var21 >> 28;
      var21 &= 268435455L;
      var25 += var23 >> 28;
      var23 &= 268435455L;
      var27 += var25 >> 28;
      var25 &= 268435455L;
      var29 += var27 >> 28;
      var27 &= 268435455L;
      var31 += var29 >> 28;
      var29 &= 268435455L;
      byte[] var67 = new byte[57];
      Codec.encode56(var1 | var3 << 28, var67, 0);
      Codec.encode56(var5 | var7 << 28, var67, 7);
      Codec.encode56(var9 | var11 << 28, var67, 14);
      Codec.encode56(var13 | var15 << 28, var67, 21);
      Codec.encode56(var17 | var19 << 28, var67, 28);
      Codec.encode56(var21 | var23 << 28, var67, 35);
      Codec.encode56(var25 | var27 << 28, var67, 42);
      Codec.encode56(var29 | var31 << 28, var67, 49);
      return var67;
   }

   static boolean reduceBasisVar(int[] var0, int[] var1, int[] var2) {
      int[] var3 = new int[28];
      System.arraycopy(LSq, 0, var3, 0, 28);
      int[] var4 = new int[28];
      Nat448.square(var0, var4);
      var4[0]++;
      int[] var5 = new int[28];
      Nat448.mul(L, var0, var5);
      int[] var6 = new int[28];
      int[] var7 = new int[8];
      System.arraycopy(L, 0, var7, 0, 8);
      int[] var8 = new int[8];
      int[] var9 = new int[8];
      System.arraycopy(var0, 0, var9, 0, 8);
      int[] var10 = new int[8];
      var10[0] = 1;
      int var11 = 1788;
      int var12 = 27;
      int var13 = ScalarUtil.getBitLengthPositive(var12, var4);

      while (var13 > 447) {
         if (--var11 < 0) {
            return false;
         }

         int var14 = ScalarUtil.getBitLength(var12, var5);
         int var15 = var14 - var13;
         var15 &= ~(var15 >> 31);
         if (var5[var12] < 0) {
            ScalarUtil.addShifted_NP(var12, var15, var3, var4, var5, var6);
            ScalarUtil.addShifted_UV(7, var15, var7, var8, var9, var10);
         } else {
            ScalarUtil.subShifted_NP(var12, var15, var3, var4, var5, var6);
            ScalarUtil.subShifted_UV(7, var15, var7, var8, var9, var10);
         }

         if (ScalarUtil.lessThan(var12, var3, var4)) {
            int[] var16 = var7;
            var7 = var9;
            var9 = var16;
            int[] var17 = var8;
            var8 = var10;
            var10 = var17;
            int[] var18 = var3;
            var3 = var4;
            var4 = var18;
            var12 = var13 >>> 5;
            var13 = ScalarUtil.getBitLengthPositive(var12, var18);
         }
      }

      System.arraycopy(var9, 0, var1, 0, 8);
      System.arraycopy(var10, 0, var2, 0, 8);
      return true;
   }

   static void toSignedDigits(int var0, int[] var1, int[] var2) {
      var2[14] = (1 << var0 - 448) + Nat.cadd(14, ~var1[0] & 1, var1, L, var2);
      Nat.shiftDownBit(15, var2, 0);
   }
}
