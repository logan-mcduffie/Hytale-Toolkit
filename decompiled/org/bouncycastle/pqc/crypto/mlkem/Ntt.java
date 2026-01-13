package org.bouncycastle.pqc.crypto.mlkem;

class Ntt {
   public static final short[] nttZetas = new short[]{
      2285,
      2571,
      2970,
      1812,
      1493,
      1422,
      287,
      202,
      3158,
      622,
      1577,
      182,
      962,
      2127,
      1855,
      1468,
      573,
      2004,
      264,
      383,
      2500,
      1458,
      1727,
      3199,
      2648,
      1017,
      732,
      608,
      1787,
      411,
      3124,
      1758,
      1223,
      652,
      2777,
      1015,
      2036,
      1491,
      3047,
      1785,
      516,
      3321,
      3009,
      2663,
      1711,
      2167,
      126,
      1469,
      2476,
      3239,
      3058,
      830,
      107,
      1908,
      3082,
      2378,
      2931,
      961,
      1821,
      2604,
      448,
      2264,
      677,
      2054,
      2226,
      430,
      555,
      843,
      2078,
      871,
      1550,
      105,
      422,
      587,
      177,
      3094,
      3038,
      2869,
      1574,
      1653,
      3083,
      778,
      1159,
      3182,
      2552,
      1483,
      2727,
      1119,
      1739,
      644,
      2457,
      349,
      418,
      329,
      3173,
      3254,
      817,
      1097,
      603,
      610,
      1322,
      2044,
      1864,
      384,
      2114,
      3193,
      1218,
      1994,
      2455,
      220,
      2142,
      1670,
      2144,
      1799,
      2051,
      794,
      1819,
      2475,
      2459,
      478,
      3221,
      3021,
      996,
      991,
      958,
      1869,
      1522,
      1628
   };
   public static final short[] nttZetasInv = new short[]{
      1701,
      1807,
      1460,
      2371,
      2338,
      2333,
      308,
      108,
      2851,
      870,
      854,
      1510,
      2535,
      1278,
      1530,
      1185,
      1659,
      1187,
      3109,
      874,
      1335,
      2111,
      136,
      1215,
      2945,
      1465,
      1285,
      2007,
      2719,
      2726,
      2232,
      2512,
      75,
      156,
      3000,
      2911,
      2980,
      872,
      2685,
      1590,
      2210,
      602,
      1846,
      777,
      147,
      2170,
      2551,
      246,
      1676,
      1755,
      460,
      291,
      235,
      3152,
      2742,
      2907,
      3224,
      1779,
      2458,
      1251,
      2486,
      2774,
      2899,
      1103,
      1275,
      2652,
      1065,
      2881,
      725,
      1508,
      2368,
      398,
      951,
      247,
      1421,
      3222,
      2499,
      271,
      90,
      853,
      1860,
      3203,
      1162,
      1618,
      666,
      320,
      8,
      2813,
      1544,
      282,
      1838,
      1293,
      2314,
      552,
      2677,
      2106,
      1571,
      205,
      2918,
      1542,
      2721,
      2597,
      2312,
      681,
      130,
      1602,
      1871,
      829,
      2946,
      3065,
      1325,
      2756,
      1861,
      1474,
      1202,
      2367,
      3147,
      1752,
      2707,
      171,
      3127,
      3042,
      1907,
      1836,
      1517,
      359,
      758,
      1441
   };

   public static short[] ntt(short[] var0) {
      short[] var1 = new short[256];
      System.arraycopy(var0, 0, var1, 0, var1.length);
      int var5 = 1;

      for (short var2 = 128; var2 >= 2; var2 >>= 1) {
         int var3 = 0;

         while (var3 < 256) {
            short var7 = nttZetas[var5++];

            int var4;
            for (var4 = var3; var4 < var3 + var2; var4++) {
               short var6 = factorQMulMont(var7, var1[var4 + var2]);
               var1[var4 + var2] = (short)(var1[var4] - var6);
               var1[var4] += var6;
            }

            var3 = var4 + var2;
         }
      }

      return var1;
   }

   public static short[] invNtt(short[] var0) {
      short[] var1 = new short[256];
      System.arraycopy(var0, 0, var1, 0, 256);
      int var5 = 0;

      for (byte var2 = 2; var2 <= 128; var2 <<= 1) {
         int var3 = 0;

         while (var3 < 256) {
            short var7 = nttZetasInv[var5++];

            int var4;
            for (var4 = var3; var4 < var3 + var2; var4++) {
               short var6 = var1[var4];
               var1[var4] = Reduce.barretReduce((short)(var6 + var1[var4 + var2]));
               var1[var4 + var2] = (short)(var6 - var1[var4 + var2]);
               var1[var4 + var2] = factorQMulMont(var7, var1[var4 + var2]);
            }

            var3 = var4 + var2;
         }
      }

      for (int var8 = 0; var8 < 256; var8++) {
         var1[var8] = factorQMulMont(var1[var8], nttZetasInv[127]);
      }

      return var1;
   }

   public static short factorQMulMont(short var0, short var1) {
      return Reduce.montgomeryReduce(var0 * var1);
   }

   public static void baseMult(Poly var0, int var1, short var2, short var3, short var4, short var5, short var6) {
      short var7 = factorQMulMont(var3, var5);
      var7 = factorQMulMont(var7, var6);
      var7 = (short)(var7 + factorQMulMont(var2, var4));
      var0.setCoeffIndex(var1, var7);
      short var8 = factorQMulMont(var2, var5);
      var8 = (short)(var8 + factorQMulMont(var3, var4));
      var0.setCoeffIndex(var1 + 1, var8);
   }
}
