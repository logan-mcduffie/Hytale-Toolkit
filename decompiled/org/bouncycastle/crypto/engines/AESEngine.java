package org.bouncycastle.crypto.engines;

import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.CryptoServicesRegistrar;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.DefaultMultiBlockCipher;
import org.bouncycastle.crypto.MultiBlockCipher;
import org.bouncycastle.crypto.OutputLengthException;
import org.bouncycastle.crypto.constraints.DefaultServiceProperties;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Pack;

public class AESEngine extends DefaultMultiBlockCipher {
   private static final byte[] S = new byte[]{
      99,
      124,
      119,
      123,
      -14,
      107,
      111,
      -59,
      48,
      1,
      103,
      43,
      -2,
      -41,
      -85,
      118,
      -54,
      -126,
      -55,
      125,
      -6,
      89,
      71,
      -16,
      -83,
      -44,
      -94,
      -81,
      -100,
      -92,
      114,
      -64,
      -73,
      -3,
      -109,
      38,
      54,
      63,
      -9,
      -52,
      52,
      -91,
      -27,
      -15,
      113,
      -40,
      49,
      21,
      4,
      -57,
      35,
      -61,
      24,
      -106,
      5,
      -102,
      7,
      18,
      -128,
      -30,
      -21,
      39,
      -78,
      117,
      9,
      -125,
      44,
      26,
      27,
      110,
      90,
      -96,
      82,
      59,
      -42,
      -77,
      41,
      -29,
      47,
      -124,
      83,
      -47,
      0,
      -19,
      32,
      -4,
      -79,
      91,
      106,
      -53,
      -66,
      57,
      74,
      76,
      88,
      -49,
      -48,
      -17,
      -86,
      -5,
      67,
      77,
      51,
      -123,
      69,
      -7,
      2,
      127,
      80,
      60,
      -97,
      -88,
      81,
      -93,
      64,
      -113,
      -110,
      -99,
      56,
      -11,
      -68,
      -74,
      -38,
      33,
      16,
      -1,
      -13,
      -46,
      -51,
      12,
      19,
      -20,
      95,
      -105,
      68,
      23,
      -60,
      -89,
      126,
      61,
      100,
      93,
      25,
      115,
      96,
      -127,
      79,
      -36,
      34,
      42,
      -112,
      -120,
      70,
      -18,
      -72,
      20,
      -34,
      94,
      11,
      -37,
      -32,
      50,
      58,
      10,
      73,
      6,
      36,
      92,
      -62,
      -45,
      -84,
      98,
      -111,
      -107,
      -28,
      121,
      -25,
      -56,
      55,
      109,
      -115,
      -43,
      78,
      -87,
      108,
      86,
      -12,
      -22,
      101,
      122,
      -82,
      8,
      -70,
      120,
      37,
      46,
      28,
      -90,
      -76,
      -58,
      -24,
      -35,
      116,
      31,
      75,
      -67,
      -117,
      -118,
      112,
      62,
      -75,
      102,
      72,
      3,
      -10,
      14,
      97,
      53,
      87,
      -71,
      -122,
      -63,
      29,
      -98,
      -31,
      -8,
      -104,
      17,
      105,
      -39,
      -114,
      -108,
      -101,
      30,
      -121,
      -23,
      -50,
      85,
      40,
      -33,
      -116,
      -95,
      -119,
      13,
      -65,
      -26,
      66,
      104,
      65,
      -103,
      45,
      15,
      -80,
      84,
      -69,
      22
   };
   private static final byte[] Si = new byte[]{
      82,
      9,
      106,
      -43,
      48,
      54,
      -91,
      56,
      -65,
      64,
      -93,
      -98,
      -127,
      -13,
      -41,
      -5,
      124,
      -29,
      57,
      -126,
      -101,
      47,
      -1,
      -121,
      52,
      -114,
      67,
      68,
      -60,
      -34,
      -23,
      -53,
      84,
      123,
      -108,
      50,
      -90,
      -62,
      35,
      61,
      -18,
      76,
      -107,
      11,
      66,
      -6,
      -61,
      78,
      8,
      46,
      -95,
      102,
      40,
      -39,
      36,
      -78,
      118,
      91,
      -94,
      73,
      109,
      -117,
      -47,
      37,
      114,
      -8,
      -10,
      100,
      -122,
      104,
      -104,
      22,
      -44,
      -92,
      92,
      -52,
      93,
      101,
      -74,
      -110,
      108,
      112,
      72,
      80,
      -3,
      -19,
      -71,
      -38,
      94,
      21,
      70,
      87,
      -89,
      -115,
      -99,
      -124,
      -112,
      -40,
      -85,
      0,
      -116,
      -68,
      -45,
      10,
      -9,
      -28,
      88,
      5,
      -72,
      -77,
      69,
      6,
      -48,
      44,
      30,
      -113,
      -54,
      63,
      15,
      2,
      -63,
      -81,
      -67,
      3,
      1,
      19,
      -118,
      107,
      58,
      -111,
      17,
      65,
      79,
      103,
      -36,
      -22,
      -105,
      -14,
      -49,
      -50,
      -16,
      -76,
      -26,
      115,
      -106,
      -84,
      116,
      34,
      -25,
      -83,
      53,
      -123,
      -30,
      -7,
      55,
      -24,
      28,
      117,
      -33,
      110,
      71,
      -15,
      26,
      113,
      29,
      41,
      -59,
      -119,
      111,
      -73,
      98,
      14,
      -86,
      24,
      -66,
      27,
      -4,
      86,
      62,
      75,
      -58,
      -46,
      121,
      32,
      -102,
      -37,
      -64,
      -2,
      120,
      -51,
      90,
      -12,
      31,
      -35,
      -88,
      51,
      -120,
      7,
      -57,
      49,
      -79,
      18,
      16,
      89,
      39,
      -128,
      -20,
      95,
      96,
      81,
      127,
      -87,
      25,
      -75,
      74,
      13,
      45,
      -27,
      122,
      -97,
      -109,
      -55,
      -100,
      -17,
      -96,
      -32,
      59,
      77,
      -82,
      42,
      -11,
      -80,
      -56,
      -21,
      -69,
      60,
      -125,
      83,
      -103,
      97,
      23,
      43,
      4,
      126,
      -70,
      119,
      -42,
      38,
      -31,
      105,
      20,
      99,
      85,
      33,
      12,
      125
   };
   private static final int[] rcon = new int[]{
      1, 2, 4, 8, 16, 32, 64, 128, 27, 54, 108, 216, 171, 77, 154, 47, 94, 188, 99, 198, 151, 53, 106, 212, 179, 125, 250, 239, 197, 145
   };
   private static final int[] T0 = new int[]{
      -1520213050,
      -2072216328,
      -1720223762,
      -1921287178,
      234025727,
      -1117033514,
      -1318096930,
      1422247313,
      1345335392,
      50397442,
      -1452841010,
      2099981142,
      436141799,
      1658312629,
      -424957107,
      -1703512340,
      1170918031,
      -1652391393,
      1086966153,
      -2021818886,
      368769775,
      -346465870,
      -918075506,
      200339707,
      -324162239,
      1742001331,
      -39673249,
      -357585083,
      -1080255453,
      -140204973,
      -1770884380,
      1539358875,
      -1028147339,
      486407649,
      -1366060227,
      1780885068,
      1513502316,
      1094664062,
      49805301,
      1338821763,
      1546925160,
      -190470831,
      887481809,
      150073849,
      -1821281822,
      1943591083,
      1395732834,
      1058346282,
      201589768,
      1388824469,
      1696801606,
      1589887901,
      672667696,
      -1583966665,
      251987210,
      -1248159185,
      151455502,
      907153956,
      -1686077413,
      1038279391,
      652995533,
      1764173646,
      -843926913,
      -1619692054,
      453576978,
      -1635548387,
      1949051992,
      773462580,
      756751158,
      -1301385508,
      -296068428,
      -73359269,
      -162377052,
      1295727478,
      1641469623,
      -827083907,
      2066295122,
      1055122397,
      1898917726,
      -1752923117,
      -179088474,
      1758581177,
      0,
      753790401,
      1612718144,
      536673507,
      -927878791,
      -312779850,
      -1100322092,
      1187761037,
      -641810841,
      1262041458,
      -565556588,
      -733197160,
      -396863312,
      1255133061,
      1808847035,
      720367557,
      -441800113,
      385612781,
      -985447546,
      -682799718,
      1429418854,
      -1803188975,
      -817543798,
      284817897,
      100794884,
      -2122350594,
      -263171936,
      1144798328,
      -1163944155,
      -475486133,
      -212774494,
      -22830243,
      -1069531008,
      -1970303227,
      -1382903233,
      -1130521311,
      1211644016,
      83228145,
      -541279133,
      -1044990345,
      1977277103,
      1663115586,
      806359072,
      452984805,
      250868733,
      1842533055,
      1288555905,
      336333848,
      890442534,
      804056259,
      -513843266,
      -1567123659,
      -867941240,
      957814574,
      1472513171,
      -223893675,
      -2105639172,
      1195195770,
      -1402706744,
      -413311558,
      723065138,
      -1787595802,
      -1604296512,
      -1736343271,
      -783331426,
      2145180835,
      1713513028,
      2116692564,
      -1416589253,
      -2088204277,
      -901364084,
      703524551,
      -742868885,
      1007948840,
      2044649127,
      -497131844,
      487262998,
      1994120109,
      1004593371,
      1446130276,
      1312438900,
      503974420,
      -615954030,
      168166924,
      1814307912,
      -463709000,
      1573044895,
      1859376061,
      -273896381,
      -1503501628,
      -1466855111,
      -1533700815,
      937747667,
      -1954973198,
      854058965,
      1137232011,
      1496790894,
      -1217565222,
      -1936880383,
      1691735473,
      -766620004,
      -525751991,
      -1267962664,
      -95005012,
      133494003,
      636152527,
      -1352309302,
      -1904575756,
      -374428089,
      403179536,
      -709182865,
      -2005370640,
      1864705354,
      1915629148,
      605822008,
      -240736681,
      -944458637,
      1371981463,
      602466507,
      2094914977,
      -1670089496,
      555687742,
      -582268010,
      -591544991,
      -2037675251,
      -2054518257,
      -1871679264,
      1111375484,
      -994724495,
      -1436129588,
      -666351472,
      84083462,
      32962295,
      302911004,
      -1553899070,
      1597322602,
      -111716434,
      -793134743,
      -1853454825,
      1489093017,
      656219450,
      -1180787161,
      954327513,
      335083755,
      -1281845205,
      856756514,
      -1150719534,
      1893325225,
      -1987146233,
      -1483434957,
      -1231316179,
      572399164,
      -1836611819,
      552200649,
      1238290055,
      -11184726,
      2015897680,
      2061492133,
      -1886614525,
      -123625127,
      -2138470135,
      386731290,
      -624967835,
      837215959,
      -968736124,
      -1201116976,
      -1019133566,
      -1332111063,
      1999449434,
      286199582,
      -877612933,
      -61582168,
      -692339859,
      974525996
   };
   private static final int[] Tinv0 = new int[]{
      1353184337,
      1399144830,
      -1012656358,
      -1772214470,
      -882136261,
      -247096033,
      -1420232020,
      -1828461749,
      1442459680,
      -160598355,
      -1854485368,
      625738485,
      -52959921,
      -674551099,
      -2143013594,
      -1885117771,
      1230680542,
      1729870373,
      -1743852987,
      -507445667,
      41234371,
      317738113,
      -1550367091,
      -956705941,
      -413167869,
      -1784901099,
      -344298049,
      -631680363,
      763608788,
      -752782248,
      694804553,
      1154009486,
      1787413109,
      2021232372,
      1799248025,
      -579749593,
      -1236278850,
      397248752,
      1722556617,
      -1271214467,
      407560035,
      -2110711067,
      1613975959,
      1165972322,
      -529046351,
      -2068943941,
      480281086,
      -1809118983,
      1483229296,
      436028815,
      -2022908268,
      -1208452270,
      601060267,
      -503166094,
      1468997603,
      715871590,
      120122290,
      63092015,
      -1703164538,
      -1526188077,
      -226023376,
      -1297760477,
      -1167457534,
      1552029421,
      723308426,
      -1833666137,
      -252573709,
      -1578997426,
      -839591323,
      -708967162,
      526529745,
      -1963022652,
      -1655493068,
      -1604979806,
      853641733,
      1978398372,
      971801355,
      -1427152832,
      111112542,
      1360031421,
      -108388034,
      1023860118,
      -1375387939,
      1186850381,
      -1249028975,
      90031217,
      1876166148,
      -15380384,
      620468249,
      -1746289194,
      -868007799,
      2006899047,
      -1119688528,
      -2004121337,
      945494503,
      -605108103,
      1191869601,
      -384875908,
      -920746760,
      0,
      -2088337399,
      1223502642,
      -1401941730,
      1316117100,
      -67170563,
      1446544655,
      517320253,
      658058550,
      1691946762,
      564550760,
      -783000677,
      976107044,
      -1318647284,
      266819475,
      -761860428,
      -1634624741,
      1338359936,
      -1574904735,
      1766553434,
      370807324,
      179999714,
      -450191168,
      1138762300,
      488053522,
      185403662,
      -1379431438,
      -1180125651,
      -928440812,
      -2061897385,
      1275557295,
      -1143105042,
      -44007517,
      -1624899081,
      -1124765092,
      -985962940,
      880737115,
      1982415755,
      -590994485,
      1761406390,
      1676797112,
      -891538985,
      277177154,
      1076008723,
      538035844,
      2099530373,
      -130171950,
      288553390,
      1839278535,
      1261411869,
      -214912292,
      -330136051,
      -790380169,
      1813426987,
      -1715900247,
      -95906799,
      577038663,
      -997393240,
      440397984,
      -668172970,
      -275762398,
      -951170681,
      -1043253031,
      -22885748,
      906744984,
      -813566554,
      685669029,
      646887386,
      -1530942145,
      -459458004,
      227702864,
      -1681105046,
      1648787028,
      -1038905866,
      -390539120,
      1593260334,
      -173030526,
      -1098883681,
      2090061929,
      -1456614033,
      -1290656305,
      999926984,
      -1484974064,
      1852021992,
      2075868123,
      158869197,
      -199730834,
      28809964,
      -1466282109,
      1701746150,
      2129067946,
      147831841,
      -420997649,
      -644094022,
      -835293366,
      -737566742,
      -696471511,
      -1347247055,
      824393514,
      815048134,
      -1067015627,
      935087732,
      -1496677636,
      -1328508704,
      366520115,
      1251476721,
      -136647615,
      240176511,
      804688151,
      -1915335306,
      1303441219,
      1414376140,
      -553347356,
      -474623586,
      461924940,
      -1205916479,
      2136040774,
      82468509,
      1563790337,
      1937016826,
      776014843,
      1511876531,
      1389550482,
      861278441,
      323475053,
      -1939744870,
      2047648055,
      -1911228327,
      -1992551445,
      -299390514,
      902390199,
      -303751967,
      1018251130,
      1507840668,
      1064563285,
      2043548696,
      -1086863501,
      -355600557,
      1537932639,
      342834655,
      -2032450440,
      -2114736182,
      1053059257,
      741614648,
      1598071746,
      1925389590,
      203809468,
      -1958134744,
      1100287487,
      1895934009,
      -558691320,
      -1662733096,
      -1866377628,
      1636092795,
      1890988757,
      1952214088,
      1113045200
   };
   private static final int m1 = -2139062144;
   private static final int m2 = 2139062143;
   private static final int m3 = 27;
   private static final int m4 = -1061109568;
   private static final int m5 = 1061109567;
   private int ROUNDS;
   private int[][] WorkingKey = null;
   private boolean forEncryption;
   private byte[] s;
   private static final int BLOCK_SIZE = 16;

   private static int shift(int var0, int var1) {
      return var0 >>> var1 | var0 << -var1;
   }

   private static int FFmulX(int var0) {
      return (var0 & 2139062143) << 1 ^ ((var0 & -2139062144) >>> 7) * 27;
   }

   private static int FFmulX2(int var0) {
      int var1 = (var0 & 1061109567) << 2;
      int var2 = var0 & -1061109568;
      var2 ^= var2 >>> 1;
      return var1 ^ var2 >>> 2 ^ var2 >>> 5;
   }

   private static int inv_mcol(int var0) {
      int var2 = var0 ^ shift(var0, 8);
      int var1 = var0 ^ FFmulX(var2);
      var2 ^= FFmulX2(var1);
      return var1 ^ var2 ^ shift(var2, 16);
   }

   private static int subWord(int var0) {
      return S[var0 & 0xFF] & 0xFF | (S[var0 >> 8 & 0xFF] & 0xFF) << 8 | (S[var0 >> 16 & 0xFF] & 0xFF) << 16 | S[var0 >> 24 & 0xFF] << 24;
   }

   private int[][] generateWorkingKey(byte[] var1, boolean var2) {
      int var3 = var1.length;
      if (var3 >= 16 && var3 <= 32 && (var3 & 7) == 0) {
         int[][] var5;
         int var4 = var3 >>> 2;
         this.ROUNDS = var4 + 6;
         var5 = new int[this.ROUNDS + 1][4];
         label58:
         switch (var4) {
            case 4:
               int var19 = Pack.littleEndianToInt(var1, 0);
               var5[0][0] = var19;
               int var23 = Pack.littleEndianToInt(var1, 4);
               var5[0][1] = var23;
               int var27 = Pack.littleEndianToInt(var1, 8);
               var5[0][2] = var27;
               int var30 = Pack.littleEndianToInt(var1, 12);
               var5[0][3] = var30;

               for (int var33 = 1; var33 <= 10; var33++) {
                  int var36 = subWord(shift(var30, 8)) ^ rcon[var33 - 1];
                  var19 ^= var36;
                  var5[var33][0] = var19;
                  var23 ^= var19;
                  var5[var33][1] = var23;
                  var27 ^= var23;
                  var5[var33][2] = var27;
                  var30 ^= var27;
                  var5[var33][3] = var30;
               }
               break;
            case 5:
            case 7:
            default:
               throw new IllegalStateException("Should never get here");
            case 6:
               int var17 = Pack.littleEndianToInt(var1, 0);
               var5[0][0] = var17;
               int var21 = Pack.littleEndianToInt(var1, 4);
               var5[0][1] = var21;
               int var25 = Pack.littleEndianToInt(var1, 8);
               var5[0][2] = var25;
               int var28 = Pack.littleEndianToInt(var1, 12);
               var5[0][3] = var28;
               int var31 = Pack.littleEndianToInt(var1, 16);
               int var34 = Pack.littleEndianToInt(var1, 20);
               byte var37 = 1;
               int var38 = 1;

               while (true) {
                  var5[var37][0] = var31;
                  var5[var37][1] = var34;
                  int var41 = subWord(shift(var34, 8)) ^ var38;
                  var38 <<= 1;
                  int var18 = var17 ^ var41;
                  var5[var37][2] = var18;
                  int var22 = var21 ^ var18;
                  var5[var37][3] = var22;
                  int var26 = var25 ^ var22;
                  var5[var37 + 1][0] = var26;
                  int var29 = var28 ^ var26;
                  var5[var37 + 1][1] = var29;
                  var31 ^= var29;
                  var5[var37 + 1][2] = var31;
                  var34 ^= var31;
                  var5[var37 + 1][3] = var34;
                  var41 = subWord(shift(var34, 8)) ^ var38;
                  var38 <<= 1;
                  var17 = var18 ^ var41;
                  var5[var37 + 2][0] = var17;
                  var21 = var22 ^ var17;
                  var5[var37 + 2][1] = var21;
                  var25 = var26 ^ var21;
                  var5[var37 + 2][2] = var25;
                  var28 = var29 ^ var25;
                  var5[var37 + 2][3] = var28;
                  var37 += 3;
                  if (var37 >= 13) {
                     break label58;
                  }

                  var31 ^= var28;
                  var34 ^= var31;
               }
            case 8:
               int var6 = Pack.littleEndianToInt(var1, 0);
               var5[0][0] = var6;
               int var7 = Pack.littleEndianToInt(var1, 4);
               var5[0][1] = var7;
               int var8 = Pack.littleEndianToInt(var1, 8);
               var5[0][2] = var8;
               int var9 = Pack.littleEndianToInt(var1, 12);
               var5[0][3] = var9;
               int var10 = Pack.littleEndianToInt(var1, 16);
               var5[1][0] = var10;
               int var11 = Pack.littleEndianToInt(var1, 20);
               var5[1][1] = var11;
               int var12 = Pack.littleEndianToInt(var1, 24);
               var5[1][2] = var12;
               int var13 = Pack.littleEndianToInt(var1, 28);
               var5[1][3] = var13;
               int var14 = 2;
               byte var15 = 1;

               while (true) {
                  int var16 = subWord(shift(var13, 8)) ^ var15;
                  var15 <<= 1;
                  var6 ^= var16;
                  var5[var14][0] = var6;
                  var7 ^= var6;
                  var5[var14][1] = var7;
                  var8 ^= var7;
                  var5[var14][2] = var8;
                  var9 ^= var8;
                  var5[var14][3] = var9;
                  if (++var14 >= 15) {
                     break;
                  }

                  var16 = subWord(var9);
                  var10 ^= var16;
                  var5[var14][0] = var10;
                  var11 ^= var10;
                  var5[var14][1] = var11;
                  var12 ^= var11;
                  var5[var14][2] = var12;
                  var13 ^= var12;
                  var5[var14][3] = var13;
                  var14++;
               }
         }

         if (!var2) {
            for (int var20 = 1; var20 < this.ROUNDS; var20++) {
               for (int var24 = 0; var24 < 4; var24++) {
                  var5[var20][var24] = inv_mcol(var5[var20][var24]);
               }
            }
         }

         return var5;
      } else {
         throw new IllegalArgumentException("Key length not 128/192/256 bits.");
      }
   }

   public static MultiBlockCipher newInstance() {
      return new AESEngine();
   }

   @Deprecated
   public AESEngine() {
      CryptoServicesRegistrar.checkConstraints(new DefaultServiceProperties(this.getAlgorithmName(), 256));
   }

   @Override
   public void init(boolean var1, CipherParameters var2) {
      if (var2 instanceof KeyParameter) {
         this.WorkingKey = this.generateWorkingKey(((KeyParameter)var2).getKey(), var1);
         this.forEncryption = var1;
         if (var1) {
            this.s = Arrays.clone(S);
         } else {
            this.s = Arrays.clone(Si);
         }

         CryptoServicesRegistrar.checkConstraints(new DefaultServiceProperties(this.getAlgorithmName(), this.bitsOfSecurity(), var2, Utils.getPurpose(var1)));
      } else {
         throw new IllegalArgumentException("invalid parameter passed to AES init - " + var2.getClass().getName());
      }
   }

   @Override
   public String getAlgorithmName() {
      return "AES";
   }

   @Override
   public int getBlockSize() {
      return 16;
   }

   @Override
   public int processBlock(byte[] var1, int var2, byte[] var3, int var4) {
      if (this.WorkingKey == null) {
         throw new IllegalStateException("AES engine not initialised");
      } else if (var2 > var1.length - 16) {
         throw new DataLengthException("input buffer too short");
      } else if (var4 > var3.length - 16) {
         throw new OutputLengthException("output buffer too short");
      } else {
         if (this.forEncryption) {
            this.encryptBlock(var1, var2, var3, var4, this.WorkingKey);
         } else {
            this.decryptBlock(var1, var2, var3, var4, this.WorkingKey);
         }

         return 16;
      }
   }

   @Override
   public void reset() {
   }

   private void encryptBlock(byte[] var1, int var2, byte[] var3, int var4, int[][] var5) {
      int var6 = Pack.littleEndianToInt(var1, var2 + 0);
      int var7 = Pack.littleEndianToInt(var1, var2 + 4);
      int var8 = Pack.littleEndianToInt(var1, var2 + 8);
      int var9 = Pack.littleEndianToInt(var1, var2 + 12);
      int var10 = var6 ^ var5[0][0];
      int var11 = var7 ^ var5[0][1];
      int var12 = var8 ^ var5[0][2];
      int var13 = 1;
      int var17 = var9 ^ var5[0][3];

      while (var13 < this.ROUNDS - 1) {
         int var14 = T0[var10 & 0xFF]
            ^ shift(T0[var11 >> 8 & 0xFF], 24)
            ^ shift(T0[var12 >> 16 & 0xFF], 16)
            ^ shift(T0[var17 >> 24 & 0xFF], 8)
            ^ var5[var13][0];
         int var15 = T0[var11 & 0xFF]
            ^ shift(T0[var12 >> 8 & 0xFF], 24)
            ^ shift(T0[var17 >> 16 & 0xFF], 16)
            ^ shift(T0[var10 >> 24 & 0xFF], 8)
            ^ var5[var13][1];
         int var16 = T0[var12 & 0xFF]
            ^ shift(T0[var17 >> 8 & 0xFF], 24)
            ^ shift(T0[var10 >> 16 & 0xFF], 16)
            ^ shift(T0[var11 >> 24 & 0xFF], 8)
            ^ var5[var13][2];
         var17 = T0[var17 & 0xFF] ^ shift(T0[var10 >> 8 & 0xFF], 24) ^ shift(T0[var11 >> 16 & 0xFF], 16) ^ shift(T0[var12 >> 24 & 0xFF], 8) ^ var5[var13++][3];
         var10 = T0[var14 & 0xFF] ^ shift(T0[var15 >> 8 & 0xFF], 24) ^ shift(T0[var16 >> 16 & 0xFF], 16) ^ shift(T0[var17 >> 24 & 0xFF], 8) ^ var5[var13][0];
         var11 = T0[var15 & 0xFF] ^ shift(T0[var16 >> 8 & 0xFF], 24) ^ shift(T0[var17 >> 16 & 0xFF], 16) ^ shift(T0[var14 >> 24 & 0xFF], 8) ^ var5[var13][1];
         var12 = T0[var16 & 0xFF] ^ shift(T0[var17 >> 8 & 0xFF], 24) ^ shift(T0[var14 >> 16 & 0xFF], 16) ^ shift(T0[var15 >> 24 & 0xFF], 8) ^ var5[var13][2];
         var17 = T0[var17 & 0xFF] ^ shift(T0[var14 >> 8 & 0xFF], 24) ^ shift(T0[var15 >> 16 & 0xFF], 16) ^ shift(T0[var16 >> 24 & 0xFF], 8) ^ var5[var13++][3];
      }

      int var24 = T0[var10 & 0xFF] ^ shift(T0[var11 >> 8 & 0xFF], 24) ^ shift(T0[var12 >> 16 & 0xFF], 16) ^ shift(T0[var17 >> 24 & 0xFF], 8) ^ var5[var13][0];
      int var25 = T0[var11 & 0xFF] ^ shift(T0[var12 >> 8 & 0xFF], 24) ^ shift(T0[var17 >> 16 & 0xFF], 16) ^ shift(T0[var10 >> 24 & 0xFF], 8) ^ var5[var13][1];
      int var26 = T0[var12 & 0xFF] ^ shift(T0[var17 >> 8 & 0xFF], 24) ^ shift(T0[var10 >> 16 & 0xFF], 16) ^ shift(T0[var11 >> 24 & 0xFF], 8) ^ var5[var13][2];
      var17 = T0[var17 & 0xFF] ^ shift(T0[var10 >> 8 & 0xFF], 24) ^ shift(T0[var11 >> 16 & 0xFF], 16) ^ shift(T0[var12 >> 24 & 0xFF], 8) ^ var5[var13++][3];
      var6 = S[var24 & 0xFF] & 255
         ^ (S[var25 >> 8 & 0xFF] & 255) << 8
         ^ (this.s[var26 >> 16 & 0xFF] & 255) << 16
         ^ this.s[var17 >> 24 & 0xFF] << 24
         ^ var5[var13][0];
      var7 = this.s[var25 & 0xFF] & 255
         ^ (S[var26 >> 8 & 0xFF] & 255) << 8
         ^ (S[var17 >> 16 & 0xFF] & 255) << 16
         ^ this.s[var24 >> 24 & 0xFF] << 24
         ^ var5[var13][1];
      var8 = this.s[var26 & 0xFF] & 255
         ^ (S[var17 >> 8 & 0xFF] & 255) << 8
         ^ (S[var24 >> 16 & 0xFF] & 255) << 16
         ^ S[var25 >> 24 & 0xFF] << 24
         ^ var5[var13][2];
      var9 = this.s[var17 & 0xFF] & 255
         ^ (this.s[var24 >> 8 & 0xFF] & 255) << 8
         ^ (this.s[var25 >> 16 & 0xFF] & 255) << 16
         ^ S[var26 >> 24 & 0xFF] << 24
         ^ var5[var13][3];
      Pack.intToLittleEndian(var6, var3, var4 + 0);
      Pack.intToLittleEndian(var7, var3, var4 + 4);
      Pack.intToLittleEndian(var8, var3, var4 + 8);
      Pack.intToLittleEndian(var9, var3, var4 + 12);
   }

   private void decryptBlock(byte[] var1, int var2, byte[] var3, int var4, int[][] var5) {
      int var6 = Pack.littleEndianToInt(var1, var2 + 0);
      int var7 = Pack.littleEndianToInt(var1, var2 + 4);
      int var8 = Pack.littleEndianToInt(var1, var2 + 8);
      int var9 = Pack.littleEndianToInt(var1, var2 + 12);
      int var10 = var6 ^ var5[this.ROUNDS][0];
      int var11 = var7 ^ var5[this.ROUNDS][1];
      int var12 = var8 ^ var5[this.ROUNDS][2];
      int var13 = this.ROUNDS - 1;
      int var17 = var9 ^ var5[this.ROUNDS][3];

      while (var13 > 1) {
         int var14 = Tinv0[var10 & 0xFF]
            ^ shift(Tinv0[var17 >> 8 & 0xFF], 24)
            ^ shift(Tinv0[var12 >> 16 & 0xFF], 16)
            ^ shift(Tinv0[var11 >> 24 & 0xFF], 8)
            ^ var5[var13][0];
         int var15 = Tinv0[var11 & 0xFF]
            ^ shift(Tinv0[var10 >> 8 & 0xFF], 24)
            ^ shift(Tinv0[var17 >> 16 & 0xFF], 16)
            ^ shift(Tinv0[var12 >> 24 & 0xFF], 8)
            ^ var5[var13][1];
         int var16 = Tinv0[var12 & 0xFF]
            ^ shift(Tinv0[var11 >> 8 & 0xFF], 24)
            ^ shift(Tinv0[var10 >> 16 & 0xFF], 16)
            ^ shift(Tinv0[var17 >> 24 & 0xFF], 8)
            ^ var5[var13][2];
         var17 = Tinv0[var17 & 0xFF]
            ^ shift(Tinv0[var12 >> 8 & 0xFF], 24)
            ^ shift(Tinv0[var11 >> 16 & 0xFF], 16)
            ^ shift(Tinv0[var10 >> 24 & 0xFF], 8)
            ^ var5[var13--][3];
         var10 = Tinv0[var14 & 0xFF]
            ^ shift(Tinv0[var17 >> 8 & 0xFF], 24)
            ^ shift(Tinv0[var16 >> 16 & 0xFF], 16)
            ^ shift(Tinv0[var15 >> 24 & 0xFF], 8)
            ^ var5[var13][0];
         var11 = Tinv0[var15 & 0xFF]
            ^ shift(Tinv0[var14 >> 8 & 0xFF], 24)
            ^ shift(Tinv0[var17 >> 16 & 0xFF], 16)
            ^ shift(Tinv0[var16 >> 24 & 0xFF], 8)
            ^ var5[var13][1];
         var12 = Tinv0[var16 & 0xFF]
            ^ shift(Tinv0[var15 >> 8 & 0xFF], 24)
            ^ shift(Tinv0[var14 >> 16 & 0xFF], 16)
            ^ shift(Tinv0[var17 >> 24 & 0xFF], 8)
            ^ var5[var13][2];
         var17 = Tinv0[var17 & 0xFF]
            ^ shift(Tinv0[var16 >> 8 & 0xFF], 24)
            ^ shift(Tinv0[var15 >> 16 & 0xFF], 16)
            ^ shift(Tinv0[var14 >> 24 & 0xFF], 8)
            ^ var5[var13--][3];
      }

      int var23 = Tinv0[var10 & 0xFF]
         ^ shift(Tinv0[var17 >> 8 & 0xFF], 24)
         ^ shift(Tinv0[var12 >> 16 & 0xFF], 16)
         ^ shift(Tinv0[var11 >> 24 & 0xFF], 8)
         ^ var5[var13][0];
      int var24 = Tinv0[var11 & 0xFF]
         ^ shift(Tinv0[var10 >> 8 & 0xFF], 24)
         ^ shift(Tinv0[var17 >> 16 & 0xFF], 16)
         ^ shift(Tinv0[var12 >> 24 & 0xFF], 8)
         ^ var5[var13][1];
      int var25 = Tinv0[var12 & 0xFF]
         ^ shift(Tinv0[var11 >> 8 & 0xFF], 24)
         ^ shift(Tinv0[var10 >> 16 & 0xFF], 16)
         ^ shift(Tinv0[var17 >> 24 & 0xFF], 8)
         ^ var5[var13][2];
      var17 = Tinv0[var17 & 0xFF]
         ^ shift(Tinv0[var12 >> 8 & 0xFF], 24)
         ^ shift(Tinv0[var11 >> 16 & 0xFF], 16)
         ^ shift(Tinv0[var10 >> 24 & 0xFF], 8)
         ^ var5[var13][3];
      var6 = Si[var23 & 0xFF] & 255
         ^ (this.s[var17 >> 8 & 0xFF] & 255) << 8
         ^ (this.s[var25 >> 16 & 0xFF] & 255) << 16
         ^ Si[var24 >> 24 & 0xFF] << 24
         ^ var5[0][0];
      var7 = this.s[var24 & 0xFF] & 255
         ^ (this.s[var23 >> 8 & 0xFF] & 255) << 8
         ^ (Si[var17 >> 16 & 0xFF] & 255) << 16
         ^ this.s[var25 >> 24 & 0xFF] << 24
         ^ var5[0][1];
      var8 = this.s[var25 & 0xFF] & 255
         ^ (Si[var24 >> 8 & 0xFF] & 255) << 8
         ^ (Si[var23 >> 16 & 0xFF] & 255) << 16
         ^ this.s[var17 >> 24 & 0xFF] << 24
         ^ var5[0][2];
      var9 = Si[var17 & 0xFF] & 255
         ^ (this.s[var25 >> 8 & 0xFF] & 255) << 8
         ^ (this.s[var24 >> 16 & 0xFF] & 255) << 16
         ^ this.s[var23 >> 24 & 0xFF] << 24
         ^ var5[0][3];
      Pack.intToLittleEndian(var6, var3, var4 + 0);
      Pack.intToLittleEndian(var7, var3, var4 + 4);
      Pack.intToLittleEndian(var8, var3, var4 + 8);
      Pack.intToLittleEndian(var9, var3, var4 + 12);
   }

   private int bitsOfSecurity() {
      return this.WorkingKey == null ? 256 : this.WorkingKey.length - 7 << 5;
   }
}
