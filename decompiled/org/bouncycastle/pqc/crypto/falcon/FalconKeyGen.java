package org.bouncycastle.pqc.crypto.falcon;

import org.bouncycastle.crypto.digests.SHAKEDigest;
import org.bouncycastle.util.Pack;

class FalconKeyGen {
   private static final short[] REV10 = new short[]{
      0,
      512,
      256,
      768,
      128,
      640,
      384,
      896,
      64,
      576,
      320,
      832,
      192,
      704,
      448,
      960,
      32,
      544,
      288,
      800,
      160,
      672,
      416,
      928,
      96,
      608,
      352,
      864,
      224,
      736,
      480,
      992,
      16,
      528,
      272,
      784,
      144,
      656,
      400,
      912,
      80,
      592,
      336,
      848,
      208,
      720,
      464,
      976,
      48,
      560,
      304,
      816,
      176,
      688,
      432,
      944,
      112,
      624,
      368,
      880,
      240,
      752,
      496,
      1008,
      8,
      520,
      264,
      776,
      136,
      648,
      392,
      904,
      72,
      584,
      328,
      840,
      200,
      712,
      456,
      968,
      40,
      552,
      296,
      808,
      168,
      680,
      424,
      936,
      104,
      616,
      360,
      872,
      232,
      744,
      488,
      1000,
      24,
      536,
      280,
      792,
      152,
      664,
      408,
      920,
      88,
      600,
      344,
      856,
      216,
      728,
      472,
      984,
      56,
      568,
      312,
      824,
      184,
      696,
      440,
      952,
      120,
      632,
      376,
      888,
      248,
      760,
      504,
      1016,
      4,
      516,
      260,
      772,
      132,
      644,
      388,
      900,
      68,
      580,
      324,
      836,
      196,
      708,
      452,
      964,
      36,
      548,
      292,
      804,
      164,
      676,
      420,
      932,
      100,
      612,
      356,
      868,
      228,
      740,
      484,
      996,
      20,
      532,
      276,
      788,
      148,
      660,
      404,
      916,
      84,
      596,
      340,
      852,
      212,
      724,
      468,
      980,
      52,
      564,
      308,
      820,
      180,
      692,
      436,
      948,
      116,
      628,
      372,
      884,
      244,
      756,
      500,
      1012,
      12,
      524,
      268,
      780,
      140,
      652,
      396,
      908,
      76,
      588,
      332,
      844,
      204,
      716,
      460,
      972,
      44,
      556,
      300,
      812,
      172,
      684,
      428,
      940,
      108,
      620,
      364,
      876,
      236,
      748,
      492,
      1004,
      28,
      540,
      284,
      796,
      156,
      668,
      412,
      924,
      92,
      604,
      348,
      860,
      220,
      732,
      476,
      988,
      60,
      572,
      316,
      828,
      188,
      700,
      444,
      956,
      124,
      636,
      380,
      892,
      252,
      764,
      508,
      1020,
      2,
      514,
      258,
      770,
      130,
      642,
      386,
      898,
      66,
      578,
      322,
      834,
      194,
      706,
      450,
      962,
      34,
      546,
      290,
      802,
      162,
      674,
      418,
      930,
      98,
      610,
      354,
      866,
      226,
      738,
      482,
      994,
      18,
      530,
      274,
      786,
      146,
      658,
      402,
      914,
      82,
      594,
      338,
      850,
      210,
      722,
      466,
      978,
      50,
      562,
      306,
      818,
      178,
      690,
      434,
      946,
      114,
      626,
      370,
      882,
      242,
      754,
      498,
      1010,
      10,
      522,
      266,
      778,
      138,
      650,
      394,
      906,
      74,
      586,
      330,
      842,
      202,
      714,
      458,
      970,
      42,
      554,
      298,
      810,
      170,
      682,
      426,
      938,
      106,
      618,
      362,
      874,
      234,
      746,
      490,
      1002,
      26,
      538,
      282,
      794,
      154,
      666,
      410,
      922,
      90,
      602,
      346,
      858,
      218,
      730,
      474,
      986,
      58,
      570,
      314,
      826,
      186,
      698,
      442,
      954,
      122,
      634,
      378,
      890,
      250,
      762,
      506,
      1018,
      6,
      518,
      262,
      774,
      134,
      646,
      390,
      902,
      70,
      582,
      326,
      838,
      198,
      710,
      454,
      966,
      38,
      550,
      294,
      806,
      166,
      678,
      422,
      934,
      102,
      614,
      358,
      870,
      230,
      742,
      486,
      998,
      22,
      534,
      278,
      790,
      150,
      662,
      406,
      918,
      86,
      598,
      342,
      854,
      214,
      726,
      470,
      982,
      54,
      566,
      310,
      822,
      182,
      694,
      438,
      950,
      118,
      630,
      374,
      886,
      246,
      758,
      502,
      1014,
      14,
      526,
      270,
      782,
      142,
      654,
      398,
      910,
      78,
      590,
      334,
      846,
      206,
      718,
      462,
      974,
      46,
      558,
      302,
      814,
      174,
      686,
      430,
      942,
      110,
      622,
      366,
      878,
      238,
      750,
      494,
      1006,
      30,
      542,
      286,
      798,
      158,
      670,
      414,
      926,
      94,
      606,
      350,
      862,
      222,
      734,
      478,
      990,
      62,
      574,
      318,
      830,
      190,
      702,
      446,
      958,
      126,
      638,
      382,
      894,
      254,
      766,
      510,
      1022,
      1,
      513,
      257,
      769,
      129,
      641,
      385,
      897,
      65,
      577,
      321,
      833,
      193,
      705,
      449,
      961,
      33,
      545,
      289,
      801,
      161,
      673,
      417,
      929,
      97,
      609,
      353,
      865,
      225,
      737,
      481,
      993,
      17,
      529,
      273,
      785,
      145,
      657,
      401,
      913,
      81,
      593,
      337,
      849,
      209,
      721,
      465,
      977,
      49,
      561,
      305,
      817,
      177,
      689,
      433,
      945,
      113,
      625,
      369,
      881,
      241,
      753,
      497,
      1009,
      9,
      521,
      265,
      777,
      137,
      649,
      393,
      905,
      73,
      585,
      329,
      841,
      201,
      713,
      457,
      969,
      41,
      553,
      297,
      809,
      169,
      681,
      425,
      937,
      105,
      617,
      361,
      873,
      233,
      745,
      489,
      1001,
      25,
      537,
      281,
      793,
      153,
      665,
      409,
      921,
      89,
      601,
      345,
      857,
      217,
      729,
      473,
      985,
      57,
      569,
      313,
      825,
      185,
      697,
      441,
      953,
      121,
      633,
      377,
      889,
      249,
      761,
      505,
      1017,
      5,
      517,
      261,
      773,
      133,
      645,
      389,
      901,
      69,
      581,
      325,
      837,
      197,
      709,
      453,
      965,
      37,
      549,
      293,
      805,
      165,
      677,
      421,
      933,
      101,
      613,
      357,
      869,
      229,
      741,
      485,
      997,
      21,
      533,
      277,
      789,
      149,
      661,
      405,
      917,
      85,
      597,
      341,
      853,
      213,
      725,
      469,
      981,
      53,
      565,
      309,
      821,
      181,
      693,
      437,
      949,
      117,
      629,
      373,
      885,
      245,
      757,
      501,
      1013,
      13,
      525,
      269,
      781,
      141,
      653,
      397,
      909,
      77,
      589,
      333,
      845,
      205,
      717,
      461,
      973,
      45,
      557,
      301,
      813,
      173,
      685,
      429,
      941,
      109,
      621,
      365,
      877,
      237,
      749,
      493,
      1005,
      29,
      541,
      285,
      797,
      157,
      669,
      413,
      925,
      93,
      605,
      349,
      861,
      221,
      733,
      477,
      989,
      61,
      573,
      317,
      829,
      189,
      701,
      445,
      957,
      125,
      637,
      381,
      893,
      253,
      765,
      509,
      1021,
      3,
      515,
      259,
      771,
      131,
      643,
      387,
      899,
      67,
      579,
      323,
      835,
      195,
      707,
      451,
      963,
      35,
      547,
      291,
      803,
      163,
      675,
      419,
      931,
      99,
      611,
      355,
      867,
      227,
      739,
      483,
      995,
      19,
      531,
      275,
      787,
      147,
      659,
      403,
      915,
      83,
      595,
      339,
      851,
      211,
      723,
      467,
      979,
      51,
      563,
      307,
      819,
      179,
      691,
      435,
      947,
      115,
      627,
      371,
      883,
      243,
      755,
      499,
      1011,
      11,
      523,
      267,
      779,
      139,
      651,
      395,
      907,
      75,
      587,
      331,
      843,
      203,
      715,
      459,
      971,
      43,
      555,
      299,
      811,
      171,
      683,
      427,
      939,
      107,
      619,
      363,
      875,
      235,
      747,
      491,
      1003,
      27,
      539,
      283,
      795,
      155,
      667,
      411,
      923,
      91,
      603,
      347,
      859,
      219,
      731,
      475,
      987,
      59,
      571,
      315,
      827,
      187,
      699,
      443,
      955,
      123,
      635,
      379,
      891,
      251,
      763,
      507,
      1019,
      7,
      519,
      263,
      775,
      135,
      647,
      391,
      903,
      71,
      583,
      327,
      839,
      199,
      711,
      455,
      967,
      39,
      551,
      295,
      807,
      167,
      679,
      423,
      935,
      103,
      615,
      359,
      871,
      231,
      743,
      487,
      999,
      23,
      535,
      279,
      791,
      151,
      663,
      407,
      919,
      87,
      599,
      343,
      855,
      215,
      727,
      471,
      983,
      55,
      567,
      311,
      823,
      183,
      695,
      439,
      951,
      119,
      631,
      375,
      887,
      247,
      759,
      503,
      1015,
      15,
      527,
      271,
      783,
      143,
      655,
      399,
      911,
      79,
      591,
      335,
      847,
      207,
      719,
      463,
      975,
      47,
      559,
      303,
      815,
      175,
      687,
      431,
      943,
      111,
      623,
      367,
      879,
      239,
      751,
      495,
      1007,
      31,
      543,
      287,
      799,
      159,
      671,
      415,
      927,
      95,
      607,
      351,
      863,
      223,
      735,
      479,
      991,
      63,
      575,
      319,
      831,
      191,
      703,
      447,
      959,
      127,
      639,
      383,
      895,
      255,
      767,
      511,
      1023
   };
   private static final long[] gauss_1024_12289 = new long[]{
      1283868770400643928L,
      6416574995475331444L,
      4078260278032692663L,
      2353523259288686585L,
      1227179971273316331L,
      575931623374121527L,
      242543240509105209L,
      91437049221049666L,
      30799446349977173L,
      9255276791179340L,
      2478152334826140L,
      590642893610164L,
      125206034929641L,
      23590435911403L,
      3948334035941L,
      586753615614L,
      77391054539L,
      9056793210L,
      940121950L,
      86539696L,
      7062824L,
      510971L,
      32764L,
      1862L,
      94L,
      4L,
      0L
   };
   private static final int[] MAX_BL_SMALL = new int[]{1, 1, 2, 2, 4, 7, 14, 27, 53, 106, 209};
   private static final int[] MAX_BL_LARGE = new int[]{2, 2, 5, 7, 12, 21, 40, 78, 157, 308};
   private static final int[] bitlength_avg = new int[]{4, 11, 24, 50, 102, 202, 401, 794, 1577, 3138, 6308};
   private static final int[] bitlength_std = new int[]{0, 1, 1, 1, 1, 2, 4, 5, 8, 13, 25};
   private static final int DEPTH_INT_FG = 4;

   private static int mkn(int var0) {
      return 1 << var0;
   }

   private static int modp_set(int var0, int var1) {
      return var0 + (var1 & -(var0 >>> 31));
   }

   private static int modp_norm(int var0, int var1) {
      return var0 - (var1 & (var0 - (var1 + 1 >>> 1) >>> 31) - 1);
   }

   private static int modp_ninv31(int var0) {
      int var1 = 2 - var0;
      var1 *= 2 - var0 * var1;
      var1 *= 2 - var0 * var1;
      var1 *= 2 - var0 * var1;
      var1 *= 2 - var0 * var1;
      return 2147483647 & -var1;
   }

   private static int modp_R(int var0) {
      return Integer.MIN_VALUE - var0;
   }

   private static int modp_add(int var0, int var1, int var2) {
      int var3 = var0 + var1 - var2;
      return var3 + (var2 & -(var3 >>> 31));
   }

   private static int modp_sub(int var0, int var1, int var2) {
      int var3 = var0 - var1;
      return var3 + (var2 & -(var3 >>> 31));
   }

   private static int modp_montymul(int var0, int var1, int var2, int var3) {
      long var4 = toUnsignedLong(var0) * toUnsignedLong(var1);
      long var6 = (var4 * var3 & 2147483647L) * var2;
      int var8 = (int)(var4 + var6 >>> 31) - var2;
      return var8 + (var2 & -(var8 >>> 31));
   }

   private static int modp_R2(int var0, int var1) {
      int var2 = modp_R(var0);
      var2 = modp_add(var2, var2, var0);
      var2 = modp_montymul(var2, var2, var0, var1);
      var2 = modp_montymul(var2, var2, var0, var1);
      var2 = modp_montymul(var2, var2, var0, var1);
      var2 = modp_montymul(var2, var2, var0, var1);
      var2 = modp_montymul(var2, var2, var0, var1);
      return var2 + (var0 & -(var2 & 1)) >>> 1;
   }

   private static int modp_Rx(int var0, int var1, int var2, int var3) {
      var0--;
      int var5 = var3;
      int var6 = modp_R(var1);

      for (int var4 = 0; 1 << var4 <= var0; var4++) {
         if ((var0 & 1 << var4) != 0) {
            var6 = modp_montymul(var6, var5, var1, var2);
         }

         var5 = modp_montymul(var5, var5, var1, var2);
      }

      return var6;
   }

   private static int modp_div(int var0, int var1, int var2, int var3, int var4) {
      int var6 = var2 - 2;
      int var5 = var4;

      for (int var7 = 30; var7 >= 0; var7--) {
         var5 = modp_montymul(var5, var5, var2, var3);
         int var8 = modp_montymul(var5, var1, var2, var3);
         var5 ^= (var5 ^ var8) & -(var6 >>> var7 & 1);
      }

      var5 = modp_montymul(var5, 1, var2, var3);
      return modp_montymul(var0, var5, var2, var3);
   }

   private static void modp_mkgm2(int[] var0, int var1, int[] var2, int var3, int var4, int var5, int var6, int var7) {
      int var9 = mkn(var4);
      int var14 = modp_R2(var6, var7);
      var5 = modp_montymul(var5, var14, var6, var7);

      for (int var10 = var4; var10 < 10; var10++) {
         var5 = modp_montymul(var5, var5, var6, var7);
      }

      int var11 = modp_div(var14, var5, var6, var7, modp_R(var6));
      int var17 = 10 - var4;
      int var13;
      int var12 = var13 = modp_R(var6);

      for (int var8 = 0; var8 < var9; var8++) {
         short var15 = REV10[var8 << var17];
         var0[var1 + var15] = var12;
         var2[var3 + var15] = var13;
         var12 = modp_montymul(var12, var5, var6, var7);
         var13 = modp_montymul(var13, var11, var6, var7);
      }
   }

   private static void modp_NTT2_ext(int[] var0, int var1, int var2, int[] var3, int var4, int var5, int var6, int var7) {
      if (var5 != 0) {
         int var10 = mkn(var5);
         int var8 = var10;

         for (byte var9 = 1; var9 < var10; var9 <<= 1) {
            int var11 = var8 >> 1;
            int var12 = 0;

            for (int var13 = 0; var12 < var9; var13 += var8) {
               int var14 = var3[var4 + var9 + var12];
               int var16 = var1 + var13 * var2;
               int var17 = var16 + var11 * var2;

               for (int var15 = 0; var15 < var11; var17 += var2) {
                  int var18 = var0[var16];
                  int var19 = modp_montymul(var0[var17], var14, var6, var7);
                  var0[var16] = modp_add(var18, var19, var6);
                  var0[var17] = modp_sub(var18, var19, var6);
                  var15++;
                  var16 += var2;
               }

               var12++;
            }

            var8 = var11;
         }
      }
   }

   private static void modp_iNTT2_ext(int[] var0, int var1, int var2, int[] var3, int var4, int var5, int var6, int var7) {
      if (var5 != 0) {
         int var10 = mkn(var5);
         int var8 = 1;

         for (int var9 = var10; var9 > 1; var9 >>= 1) {
            int var14 = var9 >> 1;
            int var15 = var8 << 1;
            int var16 = 0;

            for (int var17 = 0; var16 < var14; var17 += var15) {
               int var18 = var3[var4 + var14 + var16];
               int var20 = var1 + var17 * var2;
               int var21 = var20 + var8 * var2;

               for (int var19 = 0; var19 < var8; var21 += var2) {
                  int var22 = var0[var20];
                  int var23 = var0[var21];
                  var0[var20] = modp_add(var22, var23, var6);
                  var0[var21] = modp_montymul(modp_sub(var22, var23, var6), var18, var6, var7);
                  var19++;
                  var20 += var2;
               }

               var16++;
            }

            var8 = var15;
         }

         int var12 = 1 << 31 - var5;
         int var11 = 0;

         for (int var13 = var1; var11 < var10; var13 += var2) {
            var0[var13] = modp_montymul(var0[var13], var12, var6, var7);
            var11++;
         }
      }
   }

   private static void modp_NTT2(int[] var0, int var1, int[] var2, int var3, int var4, int var5, int var6) {
      modp_NTT2_ext(var0, var1, 1, var2, var3, var4, var5, var6);
   }

   private static void modp_iNTT2(int[] var0, int var1, int[] var2, int var3, int var4, int var5, int var6) {
      modp_iNTT2_ext(var0, var1, 1, var2, var3, var4, var5, var6);
   }

   private static void modp_poly_rec_res(int[] var0, int var1, int var2, int var3, int var4, int var5) {
      int var6 = 1 << var2 - 1;

      for (int var7 = 0; var7 < var6; var7++) {
         int var8 = var0[var1 + (var7 << 1)];
         int var9 = var0[var1 + (var7 << 1) + 1];
         var0[var1 + var7] = modp_montymul(modp_montymul(var8, var9, var3, var4), var5, var3, var4);
      }
   }

   private static void zint_sub(int[] var0, int var1, int[] var2, int var3, int var4, int var5) {
      int var7 = 0;
      int var8 = -var5;

      for (int var6 = 0; var6 < var4; var6++) {
         int var11 = var1 + var6;
         int var9 = var0[var11];
         int var10 = var9 - var2[var3 + var6] - var7;
         var7 = var10 >>> 31;
         var9 ^= (var10 & 2147483647 ^ var9) & var8;
         var0[var11] = var9;
      }
   }

   private static int zint_mul_small(int[] var0, int var1, int var2, int var3) {
      int var5 = 0;

      for (int var4 = 0; var4 < var2; var4++) {
         long var6 = toUnsignedLong(var0[var1 + var4]) * toUnsignedLong(var3) + var5;
         var0[var1 + var4] = (int)var6 & 2147483647;
         var5 = (int)(var6 >> 31);
      }

      return var5;
   }

   private static int zint_mod_small_unsigned(int[] var0, int var1, int var2, int var3, int var4, int var5) {
      int var6 = 0;
      int var7 = var2;

      while (var7-- > 0) {
         var6 = modp_montymul(var6, var5, var3, var4);
         int var8 = var0[var1 + var7] - var3;
         var8 += var3 & -(var8 >>> 31);
         var6 = modp_add(var6, var8, var3);
      }

      return var6;
   }

   private static int zint_mod_small_signed(int[] var0, int var1, int var2, int var3, int var4, int var5, int var6) {
      if (var2 == 0) {
         return 0;
      } else {
         int var7 = zint_mod_small_unsigned(var0, var1, var2, var3, var4, var5);
         return modp_sub(var7, var6 & -(var0[var1 + var2 - 1] >>> 30), var3);
      }
   }

   private static void zint_add_mul_small(int[] var0, int var1, int[] var2, int var3, int var4, int var5) {
      int var7 = 0;

      for (int var6 = 0; var6 < var4; var6++) {
         int var8 = var0[var1 + var6];
         int var9 = var2[var3 + var6];
         long var10 = toUnsignedLong(var9) * toUnsignedLong(var5) + toUnsignedLong(var8) + toUnsignedLong(var7);
         var0[var1 + var6] = (int)var10 & 2147483647;
         var7 = (int)(var10 >>> 31);
      }

      var0[var1 + var4] = var7;
   }

   private static void zint_norm_zero(int[] var0, int var1, int[] var2, int var3, int var4) {
      int var6 = 0;
      int var7 = 0;
      int var5 = var4;

      while (var5-- > 0) {
         int var8 = var0[var1 + var5];
         int var9 = var2[var3 + var5] >>> 1 | var7 << 30;
         var7 = var2[var3 + var5] & 1;
         int var10 = var9 - var8;
         var10 = -var10 >>> 31 | -(var10 >>> 31);
         var6 |= var10 & (var6 & 1) - 1;
      }

      zint_sub(var0, var1, var2, var3, var4, var6 >>> 31);
   }

   private static void zint_rebuild_CRT(int[] var0, int var1, int var2, int var3, int var4, int var5, int[] var6, int var7) {
      var6[var7] = FalconSmallPrimeList.PRIMES[0].p;

      for (int var8 = 1; var8 < var2; var8++) {
         int var10 = FalconSmallPrimeList.PRIMES[var8].p;
         int var12 = FalconSmallPrimeList.PRIMES[var8].s;
         int var11 = modp_ninv31(var10);
         int var13 = modp_R2(var10, var11);
         int var14 = 0;
         int var9 = var1;

         while (var14 < var4) {
            int var15 = var0[var9 + var8];
            int var16 = zint_mod_small_unsigned(var0, var9, var8, var10, var11, var13);
            int var17 = modp_montymul(var12, modp_sub(var15, var16, var10), var10, var11);
            zint_add_mul_small(var0, var9, var6, var7, var8, var17);
            var14++;
            var9 += var3;
         }

         var6[var7 + var8] = zint_mul_small(var6, var7, var8, var10);
      }

      if (var5 != 0) {
         int var18 = 0;

         for (int var19 = var1; var18 < var4; var19 += var3) {
            zint_norm_zero(var0, var19, var6, var7, var2);
            var18++;
         }
      }
   }

   private static void zint_negate(int[] var0, int var1, int var2, int var3) {
      int var5 = var3;
      int var6 = -var3 >>> 1;

      for (int var4 = 0; var4 < var2; var4++) {
         int var7 = var0[var1 + var4];
         var7 = (var7 ^ var6) + var5;
         var0[var1 + var4] = var7 & 2147483647;
         var5 = var7 >>> 31;
      }
   }

   private static int zint_co_reduce(int[] var0, int var1, int[] var2, int var3, int var4, long var5, long var7, long var9, long var11) {
      long var14 = 0L;
      long var16 = 0L;

      for (int var13 = 0; var13 < var4; var13++) {
         int var20 = var0[var1 + var13];
         int var21 = var2[var3 + var13];
         long var22 = var20 * var5 + var21 * var7 + var14;
         long var24 = var20 * var9 + var21 * var11 + var16;
         if (var13 > 0) {
            var0[var1 + var13 - 1] = (int)var22 & 2147483647;
            var2[var3 + var13 - 1] = (int)var24 & 2147483647;
         }

         var14 = var22 >> 31;
         var16 = var24 >> 31;
      }

      var0[var1 + var4 - 1] = (int)var14;
      var2[var3 + var4 - 1] = (int)var16;
      int var18 = (int)(var14 >>> 63);
      int var19 = (int)(var16 >>> 63);
      zint_negate(var0, var1, var4, var18);
      zint_negate(var2, var3, var4, var19);
      return var18 | var19 << 1;
   }

   private static void zint_finish_mod(int[] var0, int var1, int var2, int[] var3, int var4, int var5) {
      int var7 = 0;

      for (int var6 = 0; var6 < var2; var6++) {
         var7 = var0[var1 + var6] - var3[var4 + var6] - var7 >>> 31;
      }

      int var8 = -var5 >>> 1;
      int var9 = -(var5 | 1 - var7);
      var7 = var5;

      for (int var12 = 0; var12 < var2; var12++) {
         int var10 = var0[var1 + var12];
         int var11 = (var3[var4 + var12] ^ var8) & var9;
         var10 = var10 - var11 - var7;
         var0[var1 + var12] = var10 & 2147483647;
         var7 = var10 >>> 31;
      }
   }

   private static void zint_co_reduce_mod(
      int[] var0, int var1, int[] var2, int var3, int[] var4, int var5, int var6, int var7, long var8, long var10, long var12, long var14
   ) {
      long var17 = 0L;
      long var19 = 0L;
      int var21 = (var0[var1] * (int)var8 + var2[var3] * (int)var10) * var7 & 2147483647;
      int var22 = (var0[var1] * (int)var12 + var2[var3] * (int)var14) * var7 & 2147483647;

      for (int var16 = 0; var16 < var6; var16++) {
         int var23 = var0[var1 + var16];
         int var24 = var2[var3 + var16];
         long var25 = var23 * var8 + var24 * var10 + var4[var5 + var16] * toUnsignedLong(var21) + var17;
         long var27 = var23 * var12 + var24 * var14 + var4[var5 + var16] * toUnsignedLong(var22) + var19;
         if (var16 > 0) {
            var0[var1 + var16 - 1] = (int)var25 & 2147483647;
            var2[var3 + var16 - 1] = (int)var27 & 2147483647;
         }

         var17 = var25 >> 31;
         var19 = var27 >> 31;
      }

      var0[var1 + var6 - 1] = (int)var17;
      var2[var3 + var6 - 1] = (int)var19;
      zint_finish_mod(var0, var1, var6, var4, var5, (int)(var17 >>> 63));
      zint_finish_mod(var2, var3, var6, var4, var5, (int)(var19 >>> 63));
   }

   private static int zint_bezout(int[] var0, int var1, int[] var2, int var3, int[] var4, int var5, int[] var6, int var7, int var8, int[] var9, int var10) {
      if (var8 == 0) {
         return 0;
      } else {
         int var11 = var1;
         int var13 = var3;
         int var12 = var10;
         int var14 = var10 + var8;
         int var15 = var14 + var8;
         int var16 = var15 + var8;
         int var17 = modp_ninv31(var4[var5]);
         int var18 = modp_ninv31(var6[var7]);
         System.arraycopy(var4, var5, var9, var15, var8);
         System.arraycopy(var6, var7, var9, var16, var8);
         var0[var1] = 1;
         var2[var3] = 0;

         for (int var22 = 1; var22 < var8; var22++) {
            var0[var11 + var22] = 0;
            var2[var13 + var22] = 0;
         }

         System.arraycopy(var6, var7, var9, var10, var8);
         System.arraycopy(var4, var5, var9, var14, var8);
         var9[var14]--;

         for (int var19 = 62 * var8 + 30; var19 >= 30; var19 -= 30) {
            int var53 = -1;
            int var23 = -1;
            int var24 = 0;
            int var25 = 0;
            int var26 = 0;
            int var27 = 0;
            int var21 = var8;

            while (var21-- > 0) {
               int var44 = var9[var15 + var21];
               int var45 = var9[var16 + var21];
               var24 ^= (var24 ^ var44) & var53;
               var25 ^= (var25 ^ var44) & var23;
               var26 ^= (var26 ^ var45) & var53;
               var27 ^= (var27 ^ var45) & var23;
               var23 = var53;
               var53 &= ((var44 | var45) + Integer.MAX_VALUE >>> 31) - 1;
            }

            var25 |= var24 & var23;
            var24 &= ~var23;
            var27 |= var26 & var23;
            var26 &= ~var23;
            long var28 = (toUnsignedLong(var24) << 31) + toUnsignedLong(var25);
            long var30 = (toUnsignedLong(var26) << 31) + toUnsignedLong(var27);
            int var32 = var9[var15];
            int var33 = var9[var16];
            long var34 = 1L;
            long var36 = 0L;
            long var38 = 0L;
            long var40 = 1L;

            for (int var42 = 0; var42 < 31; var42++) {
               long var50 = var30 - var28;
               int var70 = (int)((var50 ^ (var28 ^ var30) & (var28 ^ var50)) >>> 63);
               int var71 = var32 >> var42 & 1;
               int var46 = var33 >> var42 & 1;
               int var47 = var71 & var46 & var70;
               int var48 = var71 & var46 & ~var70;
               int var49 = var47 | var71 ^ 1;
               var32 -= var33 & -var47;
               var28 -= var30 & -toUnsignedLong(var47);
               var34 -= var38 & -var47;
               var36 -= var40 & -var47;
               var33 -= var32 & -var48;
               var30 -= var28 & -toUnsignedLong(var48);
               var38 -= var34 & -var48;
               var40 -= var36 & -var48;
               var32 += var32 & var49 - 1;
               var34 += var34 & var49 - 1L;
               var36 += var36 & var49 - 1L;
               var28 ^= (var28 ^ var28 >> 1) & -toUnsignedLong(var49);
               var33 += var33 & -var49;
               var38 += var38 & -var49;
               var40 += var40 & -var49;
               var30 ^= (var30 ^ var30 >> 1) & toUnsignedLong(var49) - 1L;
            }

            int var43 = zint_co_reduce(var9, var15, var9, var16, var8, var34, var36, var38, var40);
            var34 -= var34 + var34 & -(var43 & 1);
            var36 -= var36 + var36 & -(var43 & 1);
            var38 -= var38 + var38 & -(var43 >>> 1);
            var40 -= var40 + var40 & -(var43 >>> 1);
            zint_co_reduce_mod(var0, var11, var9, var12, var6, var7, var8, var18, var34, var36, var38, var40);
            zint_co_reduce_mod(var2, var13, var9, var14, var4, var5, var8, var17, var34, var36, var38, var40);
         }

         int var20 = var9[var15] ^ 1;

         for (int var52 = 1; var52 < var8; var52++) {
            var20 |= var9[var15 + var52];
         }

         return 1 - ((var20 | -var20) >>> 31) & var4[var5] & var6[var7];
      }
   }

   private static void zint_add_scaled_mul_small(int[] var0, int var1, int var2, int[] var3, int var4, int var5, int var6, int var7, int var8) {
      if (var5 != 0) {
         int var10 = -(var3[var4 + var5 - 1] >>> 30) >>> 1;
         int var11 = 0;
         int var12 = 0;

         for (int var9 = var7; var9 < var2; var9++) {
            int var13 = var9 - var7;
            int var14 = var13 < var5 ? var3[var4 + var13] : var10;
            int var15 = var14 << var8 & 2147483647 | var11;
            var11 = var14 >>> 31 - var8;
            long var17 = toUnsignedLong(var15) * var6 + toUnsignedLong(var0[var1 + var9]) + var12;
            var0[var1 + var9] = (int)var17 & 2147483647;
            int var16 = (int)(var17 >>> 31);
            var12 = var16;
         }
      }
   }

   private static void zint_sub_scaled(int[] var0, int var1, int var2, int[] var3, int var4, int var5, int var6, int var7) {
      if (var5 != 0) {
         int var9 = -(var3[var4 + var5 - 1] >>> 30) >>> 1;
         int var10 = 0;
         int var11 = 0;

         for (int var8 = var6; var8 < var2; var8++) {
            int var12 = var8 - var6;
            int var14 = var12 < var5 ? var3[var4 + var12] : var9;
            int var15 = var14 << var7 & 2147483647 | var10;
            var10 = var14 >>> 31 - var7;
            int var13 = var0[var1 + var8] - var15 - var11;
            var0[var1 + var8] = var13 & 2147483647;
            var11 = var13 >>> 31;
         }
      }
   }

   private static int zint_one_to_plain(int[] var0, int var1) {
      int var2 = var0[var1];
      return var2 | (var2 & 1073741824) << 1;
   }

   private static void poly_big_to_fp(double[] var0, int[] var1, int var2, int var3, int var4, int var5) {
      int var6 = mkn(var5);
      if (var3 == 0) {
         for (int var17 = 0; var17 < var6; var17++) {
            var0[var17] = 0.0;
         }
      } else {
         int var7 = 0;

         while (var7 < var6) {
            int var9 = -(var1[var2 + var3 - 1] >>> 30);
            int var11 = var9 >>> 1;
            int var10 = var9 & 1;
            double var12 = 0.0;
            double var14 = 1.0;

            for (int var8 = 0; var8 < var3; var14 *= 2.1474836E9F) {
               int var16 = (var1[var2 + var8] ^ var11) + var10;
               var10 = var16 >>> 31;
               var16 &= Integer.MAX_VALUE;
               var16 -= var16 << 1 & var9;
               var12 += var16 * var14;
               var8++;
            }

            var0[var7] = var12;
            var7++;
            var2 += var4;
         }
      }
   }

   private static int poly_big_to_small(byte[] var0, int var1, int[] var2, int var3, int var4, int var5) {
      int var6 = mkn(var5);

      for (int var7 = 0; var7 < var6; var7++) {
         int var8 = zint_one_to_plain(var2, var3 + var7);
         if (var8 < -var4 || var8 > var4) {
            return 0;
         }

         var0[var1 + var7] = (byte)var8;
      }

      return 1;
   }

   private static void poly_sub_scaled(
      int[] var0, int var1, int var2, int var3, int[] var4, int var5, int var6, int var7, int[] var8, int var9, int var10, int var11
   ) {
      int var12 = mkn(var11);

      for (int var13 = 0; var13 < var12; var13++) {
         int var14 = -var8[var13];
         int var16 = var1 + var13 * var3;
         int var17 = var5;

         for (int var15 = 0; var15 < var12; var15++) {
            zint_add_scaled_mul_small(var0, var16, var2, var4, var17, var6, var14, var9, var10);
            if (var13 + var15 == var12 - 1) {
               var16 = var1;
               var14 = -var14;
            } else {
               var16 += var3;
            }

            var17 += var7;
         }
      }
   }

   private static void poly_sub_scaled_ntt(
      int[] var0, int var1, int var2, int var3, int[] var4, int var5, int var6, int var7, int[] var8, int var9, int var10, int var11, int[] var12, int var13
   ) {
      int var20 = mkn(var11);
      int var22 = var6 + 1;
      int var14 = var13;
      int var15 = var13 + mkn(var11);
      int var16 = var15 + mkn(var11);
      int var17 = var16 + var20 * var22;

      for (int var21 = 0; var21 < var22; var21++) {
         int var23 = FalconSmallPrimeList.PRIMES[var21].p;
         int var24 = modp_ninv31(var23);
         int var25 = modp_R2(var23, var24);
         int var26 = modp_Rx(var6, var23, var24, var25);
         modp_mkgm2(var12, var14, var12, var15, var11, FalconSmallPrimeList.PRIMES[var21].g, var23, var24);

         for (int var27 = 0; var27 < var20; var27++) {
            var12[var17 + var27] = modp_set(var8[var27], var23);
         }

         modp_NTT2(var12, var17, var12, var14, var11, var23, var24);
         int var32 = 0;
         int var19 = var5;

         for (int var18 = var16 + var21; var32 < var20; var18 += var22) {
            var12[var18] = zint_mod_small_signed(var4, var19, var6, var23, var24, var25, var26);
            var32++;
            var19 += var7;
         }

         modp_NTT2_ext(var12, var16 + var21, var22, var12, var14, var11, var23, var24);
         var32 = 0;

         for (int var28 = var16 + var21; var32 < var20; var28 += var22) {
            var12[var28] = modp_montymul(modp_montymul(var12[var17 + var32], var12[var28], var23, var24), var25, var23, var24);
            var32++;
         }

         modp_iNTT2_ext(var12, var16 + var21, var22, var12, var15, var11, var23, var24);
      }

      zint_rebuild_CRT(var12, var16, var22, var22, var20, 1, var12, var17);
      int var31 = 0;
      int var29 = var1;

      for (int var30 = var16; var31 < var20; var30 += var22) {
         zint_sub_scaled(var0, var29, var2, var12, var30, var22, var9, var10);
         var31++;
         var29 += var3;
      }
   }

   private static long get_rng_u64(SHAKEDigest var0) {
      byte[] var1 = new byte[8];
      var0.doOutput(var1, 0, var1.length);
      return Pack.littleEndianToLong(var1, 0);
   }

   private static int mkgauss(SHAKEDigest var0, int var1) {
      int var3 = 1 << 10 - var1;
      int var4 = 0;

      for (int var2 = 0; var2 < var3; var2++) {
         long var5 = get_rng_u64(var0);
         int var10 = (int)(var5 >>> 63);
         var5 &= Long.MAX_VALUE;
         int var7 = (int)(var5 - gauss_1024_12289[0] >>> 63);
         int var8 = 0;
         var5 = get_rng_u64(var0);
         var5 &= Long.MAX_VALUE;

         for (int var9 = 1; var9 < gauss_1024_12289.length; var9++) {
            int var11 = (int)(var5 - gauss_1024_12289[var9] >>> 63) ^ 1;
            var8 |= var9 & -(var11 & (var7 ^ 1));
            var7 |= var11;
         }

         var8 = (var8 ^ -var10) + var10;
         var4 += var8;
      }

      return var4;
   }

   private static int poly_small_sqnorm(byte[] var0, int var1) {
      int var2 = mkn(var1);
      int var4 = 0;
      int var5 = 0;

      for (int var3 = 0; var3 < var2; var3++) {
         byte var6 = var0[var3];
         var4 += var6 * var6;
         var5 |= var4;
      }

      return var4 | -(var5 >>> 31);
   }

   private static void poly_small_to_fp(double[] var0, int var1, byte[] var2, int var3) {
      int var4 = mkn(var3);

      for (int var5 = 0; var5 < var4; var5++) {
         var0[var1 + var5] = var2[var5];
      }
   }

   private static void make_fg_step(int[] var0, int var1, int var2, int var3, int var4, int var5) {
      int var6 = 1 << var2;
      int var7 = var6 >> 1;
      int var9 = MAX_BL_SMALL[var3];
      int var10 = MAX_BL_SMALL[var3 + 1];
      int var11 = var1;
      int var12 = var1 + var7 * var10;
      int var13 = var12 + var7 * var10;
      int var14 = var13 + var6 * var9;
      int var15 = var14 + var6 * var9;
      int var16 = var15 + var6;
      int var17 = var16 + var6;
      System.arraycopy(var0, var1, var0, var13, 2 * var6 * var9);

      for (int var8 = 0; var8 < var9; var8++) {
         int var18 = FalconSmallPrimeList.PRIMES[var8].p;
         int var19 = modp_ninv31(var18);
         int var20 = modp_R2(var18, var19);
         modp_mkgm2(var0, var15, var0, var16, var2, FalconSmallPrimeList.PRIMES[var8].g, var18, var19);
         int var21 = 0;

         for (int var22 = var13 + var8; var21 < var6; var22 += var9) {
            var0[var17 + var21] = var0[var22];
            var21++;
         }

         if (var4 == 0) {
            modp_NTT2(var0, var17, var0, var15, var2, var18, var19);
         }

         var21 = 0;

         for (int var34 = var11 + var8; var21 < var7; var34 += var10) {
            int var23 = var0[var17 + (var21 << 1)];
            int var24 = var0[var17 + (var21 << 1) + 1];
            var0[var34] = modp_montymul(modp_montymul(var23, var24, var18, var19), var20, var18, var19);
            var21++;
         }

         if (var4 != 0) {
            modp_iNTT2_ext(var0, var13 + var8, var9, var0, var16, var2, var18, var19);
         }

         var21 = 0;

         for (int var35 = var14 + var8; var21 < var6; var35 += var9) {
            var0[var17 + var21] = var0[var35];
            var21++;
         }

         if (var4 == 0) {
            modp_NTT2(var0, var17, var0, var15, var2, var18, var19);
         }

         var21 = 0;

         for (int var36 = var12 + var8; var21 < var7; var36 += var10) {
            int var41 = var0[var17 + (var21 << 1)];
            int var46 = var0[var17 + (var21 << 1) + 1];
            var0[var36] = modp_montymul(modp_montymul(var41, var46, var18, var19), var20, var18, var19);
            var21++;
         }

         if (var4 != 0) {
            modp_iNTT2_ext(var0, var14 + var8, var9, var0, var16, var2, var18, var19);
         }

         if (var5 == 0) {
            modp_iNTT2_ext(var0, var11 + var8, var10, var0, var16, var2 - 1, var18, var19);
            modp_iNTT2_ext(var0, var12 + var8, var10, var0, var16, var2 - 1, var18, var19);
         }
      }

      zint_rebuild_CRT(var0, var13, var9, var9, var6, 1, var0, var15);
      zint_rebuild_CRT(var0, var14, var9, var9, var6, 1, var0, var15);

      for (int var26 = var9; var26 < var10; var26++) {
         int var27 = FalconSmallPrimeList.PRIMES[var26].p;
         int var28 = modp_ninv31(var27);
         int var29 = modp_R2(var27, var28);
         int var33 = modp_Rx(var9, var27, var28, var29);
         modp_mkgm2(var0, var15, var0, var16, var2, FalconSmallPrimeList.PRIMES[var26].g, var27, var28);
         int var37 = 0;

         for (int var42 = var13; var37 < var6; var42 += var9) {
            var0[var17 + var37] = zint_mod_small_signed(var0, var42, var9, var27, var28, var29, var33);
            var37++;
         }

         modp_NTT2(var0, var17, var0, var15, var2, var27, var28);
         var37 = 0;

         for (int var43 = var11 + var26; var37 < var7; var43 += var10) {
            int var47 = var0[var17 + (var37 << 1)];
            int var25 = var0[var17 + (var37 << 1) + 1];
            var0[var43] = modp_montymul(modp_montymul(var47, var25, var27, var28), var29, var27, var28);
            var37++;
         }

         var37 = 0;

         for (int var44 = var14; var37 < var6; var44 += var9) {
            var0[var17 + var37] = zint_mod_small_signed(var0, var44, var9, var27, var28, var29, var33);
            var37++;
         }

         modp_NTT2(var0, var17, var0, var15, var2, var27, var28);
         var37 = 0;

         for (int var45 = var12 + var26; var37 < var7; var45 += var10) {
            int var48 = var0[var17 + (var37 << 1)];
            int var49 = var0[var17 + (var37 << 1) + 1];
            var0[var45] = modp_montymul(modp_montymul(var48, var49, var27, var28), var29, var27, var28);
            var37++;
         }

         if (var5 == 0) {
            modp_iNTT2_ext(var0, var11 + var26, var10, var0, var16, var2 - 1, var27, var28);
            modp_iNTT2_ext(var0, var12 + var26, var10, var0, var16, var2 - 1, var27, var28);
         }
      }
   }

   private static void make_fg(int[] var0, int var1, byte[] var2, byte[] var3, int var4, int var5, int var6) {
      int var7 = mkn(var4);
      int var9 = var1;
      int var10 = var1 + var7;
      int var11 = FalconSmallPrimeList.PRIMES[0].p;

      for (int var8 = 0; var8 < var7; var8++) {
         var0[var9 + var8] = modp_set(var2[var8], var11);
         var0[var10 + var8] = modp_set(var3[var8], var11);
      }

      if (var5 == 0 && var6 != 0) {
         int var15 = FalconSmallPrimeList.PRIMES[0].p;
         int var16 = modp_ninv31(var15);
         int var13 = var10 + var7;
         int var14 = var13 + var7;
         modp_mkgm2(var0, var13, var0, var14, var4, FalconSmallPrimeList.PRIMES[0].g, var15, var16);
         modp_NTT2(var0, var9, var0, var13, var4, var15, var16);
         modp_NTT2(var0, var10, var0, var13, var4, var15, var16);
      } else {
         for (int var12 = 0; var12 < var5; var12++) {
            make_fg_step(var0, var1, var4 - var12, var12, var12 != 0 ? 1 : 0, var12 + 1 >= var5 && var6 == 0 ? 0 : 1);
         }
      }
   }

   private static int solve_NTRU_deepest(int var0, byte[] var1, byte[] var2, int[] var3) {
      int var4 = MAX_BL_SMALL[var0];
      byte var5 = 0;
      int var6 = var5 + var4;
      int var7 = var6 + var4;
      int var8 = var7 + var4;
      int var9 = var8 + var4;
      make_fg(var3, var7, var1, var2, var0, var0, 0);
      zint_rebuild_CRT(var3, var7, var4, var4, 2, 0, var3, var9);
      if (zint_bezout(var3, var6, var3, var5, var3, var7, var3, var8, var4, var3, var9) == 0) {
         return 0;
      } else {
         short var10 = 12289;
         return zint_mul_small(var3, var5, var4, var10) == 0 && zint_mul_small(var3, var6, var4, var10) == 0 ? 1 : 0;
      }
   }

   private static int solve_NTRU_intermediate(int var0, byte[] var1, byte[] var2, int var3, int[] var4) {
      int var5 = var0 - var3;
      int var6 = 1 << var5;
      int var7 = var6 >> 1;
      int var8 = MAX_BL_SMALL[var3];
      int var9 = MAX_BL_SMALL[var3 + 1];
      int var10 = MAX_BL_LARGE[var3];
      int var14 = 0;
      int var15 = var14 + var9 * var7;
      int var18 = var15 + var9 * var7;
      make_fg(var4, var18, var1, var2, var0, var3, 1);
      int var16 = 0;
      int var17 = var16 + var6 * var10;
      int var20 = var17 + var6 * var10;
      int var34 = var6 * var8;
      System.arraycopy(var4, var18, var4, var20, var34 + var34);
      var18 = var20;
      int var19 = var20 + var34;
      var20 = var19 + var34;
      var34 = var7 * var9;
      System.arraycopy(var4, var14, var4, var20, var34 + var34);
      var14 = var20;
      var15 = var20 + var34;

      for (int var13 = 0; var13 < var10; var13++) {
         int var35 = FalconSmallPrimeList.PRIMES[var13].p;
         int var36 = modp_ninv31(var35);
         int var37 = modp_R2(var35, var36);
         int var38 = modp_Rx(var9, var35, var36, var37);
         int var39 = 0;
         int var40 = var14;
         int var41 = var15;
         int var42 = var16 + var13;

         for (int var43 = var17 + var13; var39 < var7; var43 += var10) {
            var4[var42] = zint_mod_small_signed(var4, var40, var9, var35, var36, var37, var38);
            var4[var43] = zint_mod_small_signed(var4, var41, var9, var35, var36, var37, var38);
            var39++;
            var40 += var9;
            var41 += var9;
            var42 += var10;
         }
      }

      for (int var52 = 0; var52 < var10; var52++) {
         int var69 = FalconSmallPrimeList.PRIMES[var52].p;
         int var73 = modp_ninv31(var69);
         int var77 = modp_R2(var69, var73);
         if (var52 == var8) {
            zint_rebuild_CRT(var4, var18, var8, var8, var6, 1, var4, var20);
            zint_rebuild_CRT(var4, var19, var8, var8, var6, 1, var4, var20);
         }

         int var80 = var20 + var6;
         int var82 = var80 + var6;
         int var84 = var82 + var6;
         modp_mkgm2(var4, var20, var4, var80, var5, FalconSmallPrimeList.PRIMES[var52].g, var69, var73);
         if (var52 < var8) {
            int var44 = 0;
            int var31 = var18 + var52;

            for (int var32 = var19 + var52; var44 < var6; var32 += var8) {
               var4[var82 + var44] = var4[var31];
               var4[var84 + var44] = var4[var32];
               var44++;
               var31 += var8;
            }

            modp_iNTT2_ext(var4, var18 + var52, var8, var4, var80, var5, var69, var73);
            modp_iNTT2_ext(var4, var19 + var52, var8, var4, var80, var5, var69, var73);
         } else {
            int var45 = modp_Rx(var8, var69, var73, var77);
            int var88 = 0;
            int var60 = var18;

            for (int var64 = var19; var88 < var6; var64 += var8) {
               var4[var82 + var88] = zint_mod_small_signed(var4, var60, var8, var69, var73, var77, var45);
               var4[var84 + var88] = zint_mod_small_signed(var4, var64, var8, var69, var73, var77, var45);
               var88++;
               var60 += var8;
            }

            modp_NTT2(var4, var82, var4, var20, var5, var69, var73);
            modp_NTT2(var4, var84, var4, var20, var5, var69, var73);
         }

         int var85 = var84 + var6;
         int var87 = var85 + var7;
         int var89 = 0;
         int var61 = var16 + var52;

         for (int var65 = var17 + var52; var89 < var7; var65 += var10) {
            var4[var85 + var89] = var4[var61];
            var4[var87 + var89] = var4[var65];
            var89++;
            var61 += var10;
         }

         modp_NTT2(var4, var85, var4, var20, var5 - 1, var69, var73);
         modp_NTT2(var4, var87, var4, var20, var5 - 1, var69, var73);
         var89 = 0;
         var61 = var16 + var52;

         for (int var66 = var17 + var52; var89 < var7; var66 += var10 << 1) {
            int var92 = var4[var82 + (var89 << 1)];
            int var46 = var4[var82 + (var89 << 1) + 1];
            int var47 = var4[var84 + (var89 << 1)];
            int var48 = var4[var84 + (var89 << 1) + 1];
            int var49 = modp_montymul(var4[var85 + var89], var77, var69, var73);
            int var50 = modp_montymul(var4[var87 + var89], var77, var69, var73);
            var4[var61] = modp_montymul(var48, var49, var69, var73);
            var4[var61 + var10] = modp_montymul(var47, var49, var69, var73);
            var4[var66] = modp_montymul(var46, var50, var69, var73);
            var4[var66 + var10] = modp_montymul(var92, var50, var69, var73);
            var89++;
            var61 += var10 << 1;
         }

         modp_iNTT2_ext(var4, var16 + var52, var10, var4, var80, var5, var69, var73);
         modp_iNTT2_ext(var4, var17 + var52, var10, var4, var80, var5, var69, var73);
      }

      zint_rebuild_CRT(var4, var16, var10, var10, var6, 1, var4, var20);
      zint_rebuild_CRT(var4, var17, var10, var10, var6, 1, var4, var20);
      double[] var21 = new double[var6];
      double[] var22 = new double[var6];
      double[] var23 = new double[var6];
      double[] var24 = new double[var6];
      double[] var25 = new double[var6 >> 1];
      int[] var33 = new int[var6];
      int var11 = Math.min(var8, 10);
      poly_big_to_fp(var23, var4, var18 + var8 - var11, var11, var8, var5);
      poly_big_to_fp(var24, var4, var19 + var8 - var11, var11, var8, var5);
      int var26 = 31 * (var8 - var11);
      int var27 = bitlength_avg[var3] - 6 * bitlength_std[var3];
      int var28 = bitlength_avg[var3] + 6 * bitlength_std[var3];
      FalconFFT.FFT(var23, 0, var5);
      FalconFFT.FFT(var24, 0, var5);
      FalconFFT.poly_invnorm2_fft(var25, 0, var23, 0, var24, 0, var5);
      FalconFFT.poly_adj_fft(var23, 0, var5);
      FalconFFT.poly_adj_fft(var24, 0, var5);
      int var12 = var10;
      int var29 = 31 * var10;
      int var30 = var29 - var27;

      while (true) {
         var11 = Math.min(var12, 10);
         int var70 = 31 * (var12 - var11);
         poly_big_to_fp(var21, var4, var16 + var12 - var11, var11, var10, var5);
         poly_big_to_fp(var22, var4, var17 + var12 - var11, var11, var10, var5);
         FalconFFT.FFT(var21, 0, var5);
         FalconFFT.FFT(var22, 0, var5);
         FalconFFT.poly_mul_fft(var21, 0, var23, 0, var5);
         FalconFFT.poly_mul_fft(var22, 0, var24, 0, var5);
         FalconFFT.poly_add(var22, 0, var21, 0, var5);
         FalconFFT.poly_mul_autoadj_fft(var22, 0, var25, 0, var5);
         FalconFFT.iFFT(var22, 0, var5);
         int var74 = var30 - var70 + var26;
         double var86;
         if (var74 < 0) {
            var74 = -var74;
            var86 = 2.0;
         } else {
            var86 = 0.5;
         }

         double var83;
         for (var83 = 1.0; var74 != 0; var86 *= var86) {
            if ((var74 & 1) != 0) {
               var83 *= var86;
            }

            var74 >>= 1;
         }

         for (int var53 = 0; var53 < var6; var53++) {
            double var91 = var22[var53] * var83;
            if (-2.147483647E9 >= var91 || var91 >= 2.147483647E9) {
               return 0;
            }

            var33[var53] = (int)FPREngine.fpr_rint(var91);
         }

         int var81 = var30 / 31;
         int var79 = var30 % 31;
         if (var3 <= 4) {
            poly_sub_scaled_ntt(var4, var16, var12, var10, var4, var18, var8, var8, var33, var81, var79, var5, var4, var20);
            poly_sub_scaled_ntt(var4, var17, var12, var10, var4, var19, var8, var8, var33, var81, var79, var5, var4, var20);
         } else {
            poly_sub_scaled(var4, var16, var12, var10, var4, var18, var8, var8, var33, var81, var79, var5);
            poly_sub_scaled(var4, var17, var12, var10, var4, var19, var8, var8, var33, var81, var79, var5);
         }

         int var78 = var30 + var28 + 10;
         if (var78 < var29) {
            var29 = var78;
            if (var12 * 31 >= var78 + 31) {
               var12--;
            }
         }

         if (var30 <= 0) {
            if (var12 < var8) {
               for (int var54 = 0; var54 < var6; var17 += var10) {
                  var74 = -(var4[var16 + var12 - 1] >>> 30) >>> 1;

                  for (int var71 = var12; var71 < var8; var71++) {
                     var4[var16 + var71] = var74;
                  }

                  var74 = -(var4[var17 + var12 - 1] >>> 30) >>> 1;

                  for (int var72 = var12; var72 < var8; var72++) {
                     var4[var17 + var72] = var74;
                  }

                  var54++;
                  var16 += var10;
               }
            }

            int var55 = 0;
            int var63 = 0;

            for (int var67 = 0; var55 < var6 << 1; var67 += var10) {
               System.arraycopy(var4, var67, var4, var63, var8);
               var55++;
               var63 += var8;
            }

            return 1;
         }

         var30 -= 25;
         if (var30 < 0) {
            var30 = 0;
         }
      }
   }

   private static int solve_NTRU_binary_depth1(int var0, byte[] var1, byte[] var2, int[] var3) {
      byte var4 = 1;
      int var6 = 1 << var0;
      int var5 = var0 - var4;
      int var7 = 1 << var5;
      int var8 = var7 >> 1;
      int var9 = MAX_BL_SMALL[var4];
      int var10 = MAX_BL_SMALL[var4 + 1];
      int var11 = MAX_BL_LARGE[var4];
      byte var13 = 0;
      int var14 = var13 + var10 * var8;
      int var15 = var14 + var10 * var8;
      int var16 = var15 + var11 * var7;

      for (int var12 = 0; var12 < var11; var12++) {
         int var28 = FalconSmallPrimeList.PRIMES[var12].p;
         int var29 = modp_ninv31(var28);
         int var30 = modp_R2(var28, var29);
         int var31 = modp_Rx(var10, var28, var29, var30);
         int var32 = 0;
         int var33 = var13;
         int var34 = var14;
         int var35 = var15 + var12;

         for (int var36 = var16 + var12; var32 < var8; var36 += var11) {
            var3[var35] = zint_mod_small_signed(var3, var33, var10, var28, var29, var30, var31);
            var3[var36] = zint_mod_small_signed(var3, var34, var10, var28, var29, var30, var31);
            var32++;
            var33 += var10;
            var34 += var10;
            var35 += var11;
         }
      }

      System.arraycopy(var3, var15, var3, 0, var11 * var7);
      byte var48 = 0;
      System.arraycopy(var3, var16, var3, var48 + var11 * var7, var11 * var7);
      var16 = var48 + var11 * var7;
      int var17 = var16 + var11 * var7;
      int var18 = var17 + var9 * var7;
      int var19 = var18 + var9 * var7;

      for (int var45 = 0; var45 < var11; var45++) {
         int var57 = FalconSmallPrimeList.PRIMES[var45].p;
         int var59 = modp_ninv31(var57);
         int var60 = modp_R2(var57, var59);
         int var61 = var19 + var6;
         int var63 = var61 + var7;
         int var65 = var63 + var6;
         modp_mkgm2(var3, var19, var3, var61, var0, FalconSmallPrimeList.PRIMES[var45].g, var57, var59);

         for (int var38 = 0; var38 < var6; var38++) {
            var3[var63 + var38] = modp_set(var1[var38], var57);
            var3[var65 + var38] = modp_set(var2[var38], var57);
         }

         modp_NTT2(var3, var63, var3, var19, var0, var57, var59);
         modp_NTT2(var3, var65, var3, var19, var0, var57, var59);

         for (int var37 = var0; var37 > var5; var37--) {
            modp_poly_rec_res(var3, var63, var37, var57, var59, var60);
            modp_poly_rec_res(var3, var65, var37, var57, var59, var60);
         }

         System.arraycopy(var3, var61, var3, var19 + var7, var7);
         var61 = var19 + var7;
         System.arraycopy(var3, var63, var3, var61 + var7, var7);
         var63 = var61 + var7;
         System.arraycopy(var3, var65, var3, var63 + var7, var7);
         var65 = var63 + var7;
         int var67 = var65 + var7;
         int var68 = var67 + var8;
         int var69 = 0;
         int var26 = var48 + var45;

         for (int var27 = var16 + var45; var69 < var8; var27 += var11) {
            var3[var67 + var69] = var3[var26];
            var3[var68 + var69] = var3[var27];
            var69++;
            var26 += var11;
         }

         modp_NTT2(var3, var67, var3, var19, var5 - 1, var57, var59);
         modp_NTT2(var3, var68, var3, var19, var5 - 1, var57, var59);
         var69 = 0;
         var26 = var48 + var45;

         for (int var55 = var16 + var45; var69 < var8; var55 += var11 << 1) {
            int var39 = var3[var63 + (var69 << 1)];
            int var40 = var3[var63 + (var69 << 1) + 1];
            int var41 = var3[var65 + (var69 << 1)];
            int var42 = var3[var65 + (var69 << 1) + 1];
            int var43 = modp_montymul(var3[var67 + var69], var60, var57, var59);
            int var44 = modp_montymul(var3[var68 + var69], var60, var57, var59);
            var3[var26] = modp_montymul(var42, var43, var57, var59);
            var3[var26 + var11] = modp_montymul(var41, var43, var57, var59);
            var3[var55] = modp_montymul(var40, var44, var57, var59);
            var3[var55 + var11] = modp_montymul(var39, var44, var57, var59);
            var69++;
            var26 += var11 << 1;
         }

         modp_iNTT2_ext(var3, var48 + var45, var11, var3, var61, var5, var57, var59);
         modp_iNTT2_ext(var3, var16 + var45, var11, var3, var61, var5, var57, var59);
         if (var45 < var9) {
            modp_iNTT2(var3, var63, var3, var61, var5, var57, var59);
            modp_iNTT2(var3, var65, var3, var61, var5, var57, var59);
            var69 = 0;
            var26 = var17 + var45;

            for (int var56 = var18 + var45; var69 < var7; var56 += var9) {
               var3[var26] = var3[var63 + var69];
               var3[var56] = var3[var65 + var69];
               var69++;
               var26 += var9;
            }
         }
      }

      zint_rebuild_CRT(var3, var48, var11, var11, var7 << 1, 1, var3, var19);
      zint_rebuild_CRT(var3, var17, var9, var9, var7 << 1, 1, var3, var19);
      double[] var20 = new double[var7];
      double[] var21 = new double[var7];
      poly_big_to_fp(var20, var3, var48, var11, var11, var5);
      poly_big_to_fp(var21, var3, var16, var11, var11, var5);
      System.arraycopy(var3, var17, var3, 0, 2 * var9 * var7);
      byte var51 = 0;
      var18 = var51 + var9 * var7;
      double[] var22 = new double[var7];
      double[] var23 = new double[var7];
      poly_big_to_fp(var22, var3, var51, var9, var9, var5);
      poly_big_to_fp(var23, var3, var18, var9, var9, var5);
      FalconFFT.FFT(var20, 0, var5);
      FalconFFT.FFT(var21, 0, var5);
      FalconFFT.FFT(var22, 0, var5);
      FalconFFT.FFT(var23, 0, var5);
      double[] var24 = new double[var7];
      double[] var25 = new double[var7 >> 1];
      FalconFFT.poly_add_muladj_fft(var24, var20, var21, var22, var23, var5);
      FalconFFT.poly_invnorm2_fft(var25, 0, var22, 0, var23, 0, var5);
      FalconFFT.poly_mul_autoadj_fft(var24, 0, var25, 0, var5);
      FalconFFT.iFFT(var24, 0, var5);

      for (int var46 = 0; var46 < var7; var46++) {
         double var58 = var24[var46];
         if (var58 >= 9.223372E18F || -9.223372E18F >= var58) {
            return 0;
         }

         var24[var46] = FPREngine.fpr_rint(var58);
      }

      FalconFFT.FFT(var24, 0, var5);
      FalconFFT.poly_mul_fft(var22, 0, var24, 0, var5);
      FalconFFT.poly_mul_fft(var23, 0, var24, 0, var5);
      FalconFFT.poly_sub(var20, 0, var22, 0, var5);
      FalconFFT.poly_sub(var21, 0, var23, 0, var5);
      FalconFFT.iFFT(var20, 0, var5);
      FalconFFT.iFFT(var21, 0, var5);
      var16 = var48 + var7;

      for (int var47 = 0; var47 < var7; var47++) {
         var3[var48 + var47] = (int)FPREngine.fpr_rint(var20[var47]);
         var3[var16 + var47] = (int)FPREngine.fpr_rint(var21[var47]);
      }

      return 1;
   }

   private static int solve_NTRU_binary_depth0(int var0, byte[] var1, byte[] var2, int[] var3) {
      int var4 = 1 << var0;
      int var5 = var4 >> 1;
      int var7 = FalconSmallPrimeList.PRIMES[0].p;
      int var8 = modp_ninv31(var7);
      int var9 = modp_R2(var7, var8);
      byte var10 = 0;
      int var11 = var10 + var5;
      int var19 = var11 + var5;
      int var20 = var19 + var4;
      int var17 = var20 + var4;
      int var18 = var17 + var4;
      modp_mkgm2(var3, var17, var3, var18, var0, FalconSmallPrimeList.PRIMES[0].g, var7, var8);

      for (int var6 = 0; var6 < var5; var6++) {
         var3[var10 + var6] = modp_set(zint_one_to_plain(var3, var10 + var6), var7);
         var3[var11 + var6] = modp_set(zint_one_to_plain(var3, var11 + var6), var7);
      }

      modp_NTT2(var3, var10, var3, var17, var0 - 1, var7, var8);
      modp_NTT2(var3, var11, var3, var17, var0 - 1, var7, var8);

      for (int var30 = 0; var30 < var4; var30++) {
         var3[var19 + var30] = modp_set(var1[var30], var7);
         var3[var20 + var30] = modp_set(var2[var30], var7);
      }

      modp_NTT2(var3, var19, var3, var17, var0, var7, var8);
      modp_NTT2(var3, var20, var3, var17, var0, var7, var8);

      for (byte var31 = 0; var31 < var4; var31 += 2) {
         int var24 = var3[var19 + var31];
         int var25 = var3[var19 + var31 + 1];
         int var26 = var3[var20 + var31];
         int var27 = var3[var20 + var31 + 1];
         int var28 = modp_montymul(var3[var10 + (var31 >> 1)], var9, var7, var8);
         int var29 = modp_montymul(var3[var11 + (var31 >> 1)], var9, var7, var8);
         var3[var19 + var31] = modp_montymul(var27, var28, var7, var8);
         var3[var19 + var31 + 1] = modp_montymul(var26, var28, var7, var8);
         var3[var20 + var31] = modp_montymul(var25, var29, var7, var8);
         var3[var20 + var31 + 1] = modp_montymul(var24, var29, var7, var8);
      }

      modp_iNTT2(var3, var19, var3, var18, var0, var7, var8);
      modp_iNTT2(var3, var20, var3, var18, var0, var7, var8);
      var11 = var10 + var4;
      int var12 = var11 + var4;
      System.arraycopy(var3, var19, var3, var10, 2 * var4);
      int var13 = var12 + var4;
      int var14 = var13 + var4;
      int var15 = var14 + var4;
      int var16 = var15 + var4;
      modp_mkgm2(var3, var12, var3, var13, var0, FalconSmallPrimeList.PRIMES[0].g, var7, var8);
      modp_NTT2(var3, var10, var3, var12, var0, var7, var8);
      modp_NTT2(var3, var11, var3, var12, var0, var7, var8);
      var3[var15] = var3[var16] = modp_set(var1[0], var7);

      for (int var32 = 1; var32 < var4; var32++) {
         var3[var15 + var32] = modp_set(var1[var32], var7);
         var3[var16 + var4 - var32] = modp_set(-var1[var32], var7);
      }

      modp_NTT2(var3, var15, var3, var12, var0, var7, var8);
      modp_NTT2(var3, var16, var3, var12, var0, var7, var8);

      for (int var33 = 0; var33 < var4; var33++) {
         int var49 = modp_montymul(var3[var16 + var33], var9, var7, var8);
         var3[var13 + var33] = modp_montymul(var49, var3[var10 + var33], var7, var8);
         var3[var14 + var33] = modp_montymul(var49, var3[var15 + var33], var7, var8);
      }

      var3[var15] = var3[var16] = modp_set(var2[0], var7);

      for (int var34 = 1; var34 < var4; var34++) {
         var3[var15 + var34] = modp_set(var2[var34], var7);
         var3[var16 + var4 - var34] = modp_set(-var2[var34], var7);
      }

      modp_NTT2(var3, var15, var3, var12, var0, var7, var8);
      modp_NTT2(var3, var16, var3, var12, var0, var7, var8);

      for (int var35 = 0; var35 < var4; var35++) {
         int var50 = modp_montymul(var3[var16 + var35], var9, var7, var8);
         var3[var13 + var35] = modp_add(var3[var13 + var35], modp_montymul(var50, var3[var11 + var35], var7, var8), var7);
         var3[var14 + var35] = modp_add(var3[var14 + var35], modp_montymul(var50, var3[var15 + var35], var7, var8), var7);
      }

      modp_mkgm2(var3, var12, var3, var15, var0, FalconSmallPrimeList.PRIMES[0].g, var7, var8);
      modp_iNTT2(var3, var13, var3, var15, var0, var7, var8);
      modp_iNTT2(var3, var14, var3, var15, var0, var7, var8);

      for (int var36 = 0; var36 < var4; var36++) {
         var3[var12 + var36] = modp_norm(var3[var13 + var36], var7);
         var3[var13 + var36] = modp_norm(var3[var14 + var36], var7);
      }

      double[] var51 = new double[3 * var4];
      byte var21 = 0;
      int var22 = var21 + var4;
      int var23 = var22 + var4;

      for (int var37 = 0; var37 < var4; var37++) {
         var51[var23 + var37] = var3[var13 + var37];
      }

      FalconFFT.FFT(var51, var23, var0);
      System.arraycopy(var51, var23, var51, var22, var5);
      var23 = var22 + var5;

      for (int var38 = 0; var38 < var4; var38++) {
         var51[var23 + var38] = var3[var12 + var38];
      }

      FalconFFT.FFT(var51, var23, var0);
      FalconFFT.poly_div_autoadj_fft(var51, var23, var51, var22, var0);
      FalconFFT.iFFT(var51, var23, var0);

      for (int var39 = 0; var39 < var4; var39++) {
         var3[var12 + var39] = modp_set((int)FPREngine.fpr_rint(var51[var23 + var39]), var7);
      }

      var13 = var12 + var4;
      var14 = var13 + var4;
      var15 = var14 + var4;
      var16 = var15 + var4;
      modp_mkgm2(var3, var13, var3, var14, var0, FalconSmallPrimeList.PRIMES[0].g, var7, var8);

      for (int var40 = 0; var40 < var4; var40++) {
         var3[var15 + var40] = modp_set(var1[var40], var7);
         var3[var16 + var40] = modp_set(var2[var40], var7);
      }

      modp_NTT2(var3, var12, var3, var13, var0, var7, var8);
      modp_NTT2(var3, var15, var3, var13, var0, var7, var8);
      modp_NTT2(var3, var16, var3, var13, var0, var7, var8);

      for (int var41 = 0; var41 < var4; var41++) {
         int var52 = modp_montymul(var3[var12 + var41], var9, var7, var8);
         var3[var10 + var41] = modp_sub(var3[var10 + var41], modp_montymul(var52, var3[var15 + var41], var7, var8), var7);
         var3[var11 + var41] = modp_sub(var3[var11 + var41], modp_montymul(var52, var3[var16 + var41], var7, var8), var7);
      }

      modp_iNTT2(var3, var10, var3, var14, var0, var7, var8);
      modp_iNTT2(var3, var11, var3, var14, var0, var7, var8);

      for (int var42 = 0; var42 < var4; var42++) {
         var3[var10 + var42] = modp_norm(var3[var10 + var42], var7);
         var3[var11 + var42] = modp_norm(var3[var11 + var42], var7);
      }

      return 1;
   }

   private static int solve_NTRU(int var0, byte[] var1, byte[] var2, byte[] var3, int var4, int[] var5) {
      byte var16 = 0;
      int var6 = mkn(var0);
      if (solve_NTRU_deepest(var0, var2, var3, var5) == 0) {
         return 0;
      } else {
         if (var0 <= 2) {
            int var17 = var0;

            while (var17-- > 0) {
               if (solve_NTRU_intermediate(var0, var2, var3, var17, var5) == 0) {
                  return 0;
               }
            }
         } else {
            int var21 = var0;

            while (var21-- > 2) {
               if (solve_NTRU_intermediate(var0, var2, var3, var21, var5) == 0) {
                  return 0;
               }
            }

            if (solve_NTRU_binary_depth1(var0, var2, var3, var5) == 0) {
               return 0;
            }

            if (solve_NTRU_binary_depth0(var0, var2, var3, var5) == 0) {
               return 0;
            }
         }

         byte[] var22 = new byte[var6];
         if (poly_big_to_small(var1, 0, var5, 0, var4, var0) != 0 && poly_big_to_small(var22, var16, var5, var6, var4, var0) != 0) {
            byte var11 = 0;
            int var8 = var11 + var6;
            int var9 = var8 + var6;
            int var10 = var9 + var6;
            int var12 = var10 + var6;
            int var13 = FalconSmallPrimeList.PRIMES[0].p;
            int var14 = modp_ninv31(var13);
            modp_mkgm2(var5, var12, var5, 0, var0, FalconSmallPrimeList.PRIMES[0].g, var13, var14);

            for (int var7 = 0; var7 < var6; var7++) {
               var5[var11 + var7] = modp_set(var22[var16 + var7], var13);
            }

            for (int var19 = 0; var19 < var6; var19++) {
               var5[var8 + var19] = modp_set(var2[var19], var13);
               var5[var9 + var19] = modp_set(var3[var19], var13);
               var5[var10 + var19] = modp_set(var1[var19], var13);
            }

            modp_NTT2(var5, var8, var5, var12, var0, var13, var14);
            modp_NTT2(var5, var9, var5, var12, var0, var13, var14);
            modp_NTT2(var5, var10, var5, var12, var0, var13, var14);
            modp_NTT2(var5, var11, var5, var12, var0, var13, var14);
            int var15 = modp_montymul(12289, 1, var13, var14);

            for (int var20 = 0; var20 < var6; var20++) {
               int var18 = modp_sub(
                  modp_montymul(var5[var8 + var20], var5[var11 + var20], var13, var14),
                  modp_montymul(var5[var9 + var20], var5[var10 + var20], var13, var14),
                  var13
               );
               if (var18 != var15) {
                  return 0;
               }
            }

            return 1;
         } else {
            return 0;
         }
      }
   }

   private static void poly_small_mkgauss(SHAKEDigest var0, byte[] var1, int var2) {
      int var3 = mkn(var2);
      int var5 = 0;

      for (int var4 = 0; var4 < var3; var4++) {
         int var6;
         while (true) {
            var6 = mkgauss(var0, var2);
            if (var6 >= -127 && var6 <= 127) {
               if (var4 != var3 - 1) {
                  var5 ^= var6 & 1;
                  break;
               }

               if ((var5 ^ var6 & 1) != 0) {
                  break;
               }
            }
         }

         var1[var4] = (byte)var6;
      }
   }

   static void keygen(SHAKEDigest var0, byte[] var1, byte[] var2, byte[] var3, short[] var4, int var5) {
      int var6 = mkn(var5);

      while (true) {
         double[] var10 = new double[3 * var6];
         poly_small_mkgauss(var0, var1, var5);
         poly_small_mkgauss(var0, var2, var5);
         int var21 = 1 << FalconCodec.max_fg_bits[var5] - 1;

         for (int var7 = 0; var7 < var6; var7++) {
            if (var1[var7] >= var21 || var1[var7] <= -var21 || var2[var7] >= var21 || var2[var7] <= -var21) {
               var21 = -1;
               break;
            }
         }

         if (var21 >= 0) {
            int var18 = poly_small_sqnorm(var1, var5);
            int var19 = poly_small_sqnorm(var2, var5);
            int var20 = var18 + var19 | -((var18 | var19) >>> 31);
            if ((var20 & 4294967295L) < 16823L) {
               byte var13 = 0;
               int var14 = var13 + var6;
               int var15 = var14 + var6;
               poly_small_to_fp(var10, var13, var1, var5);
               poly_small_to_fp(var10, var14, var2, var5);
               FalconFFT.FFT(var10, var13, var5);
               FalconFFT.FFT(var10, var14, var5);
               FalconFFT.poly_invnorm2_fft(var10, var15, var10, var13, var10, var14, var5);
               FalconFFT.poly_adj_fft(var10, var13, var5);
               FalconFFT.poly_adj_fft(var10, var14, var5);
               FalconFFT.poly_mulconst(var10, var13, 12289.0, var5);
               FalconFFT.poly_mulconst(var10, var14, 12289.0, var5);
               FalconFFT.poly_mul_autoadj_fft(var10, var13, var10, var15, var5);
               FalconFFT.poly_mul_autoadj_fft(var10, var14, var10, var15, var5);
               FalconFFT.iFFT(var10, var13, var5);
               FalconFFT.iFFT(var10, var14, var5);
               double var16 = 0.0;

               for (int var22 = 0; var22 < var6; var22++) {
                  var16 += var10[var13 + var22] * var10[var13 + var22] + var10[var14 + var22] * var10[var14 + var22];
               }

               if (!(var16 >= 16822.4121)) {
                  short[] var9 = new short[2 * var6];
                  byte var11;
                  int var12;
                  if (var4 == null) {
                     var11 = 0;
                     var4 = var9;
                     var12 = var11 + var6;
                  } else {
                     var11 = 0;
                     var12 = 0;
                  }

                  if (FalconVrfy.compute_public(var4, var11, var1, var2, var5, var9, var12) != 0) {
                     int[] var8 = var5 > 2 ? new int[28 * var6] : new int[28 * var6 * 3];
                     var21 = (1 << FalconCodec.max_FG_bits[var5] - 1) - 1;
                     if (solve_NTRU(var5, var3, var1, var2, var21, var8) != 0) {
                        return;
                     }
                  }
               }
            }
         }
      }
   }

   private static long toUnsignedLong(int var0) {
      return var0 & 4294967295L;
   }
}
