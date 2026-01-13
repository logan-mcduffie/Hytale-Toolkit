package org.bouncycastle.crypto.engines;

import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.CryptoServicesRegistrar;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.OutputLengthException;
import org.bouncycastle.crypto.constraints.DefaultServiceProperties;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Pack;

public class DSTU7624Engine implements BlockCipher {
   private long[] internalState;
   private long[] workingKey;
   private long[][] roundKeys;
   private int wordsInBlock;
   private int wordsInKey;
   private static final int ROUNDS_128 = 10;
   private static final int ROUNDS_256 = 14;
   private static final int ROUNDS_512 = 18;
   private int roundsAmount;
   private boolean forEncryption;
   private static final byte[] S0 = new byte[]{
      -88,
      67,
      95,
      6,
      107,
      117,
      108,
      89,
      113,
      -33,
      -121,
      -107,
      23,
      -16,
      -40,
      9,
      109,
      -13,
      29,
      -53,
      -55,
      77,
      44,
      -81,
      121,
      -32,
      -105,
      -3,
      111,
      75,
      69,
      57,
      62,
      -35,
      -93,
      79,
      -76,
      -74,
      -102,
      14,
      31,
      -65,
      21,
      -31,
      73,
      -46,
      -109,
      -58,
      -110,
      114,
      -98,
      97,
      -47,
      99,
      -6,
      -18,
      -12,
      25,
      -43,
      -83,
      88,
      -92,
      -69,
      -95,
      -36,
      -14,
      -125,
      55,
      66,
      -28,
      122,
      50,
      -100,
      -52,
      -85,
      74,
      -113,
      110,
      4,
      39,
      46,
      -25,
      -30,
      90,
      -106,
      22,
      35,
      43,
      -62,
      101,
      102,
      15,
      -68,
      -87,
      71,
      65,
      52,
      72,
      -4,
      -73,
      106,
      -120,
      -91,
      83,
      -122,
      -7,
      91,
      -37,
      56,
      123,
      -61,
      30,
      34,
      51,
      36,
      40,
      54,
      -57,
      -78,
      59,
      -114,
      119,
      -70,
      -11,
      20,
      -97,
      8,
      85,
      -101,
      76,
      -2,
      96,
      92,
      -38,
      24,
      70,
      -51,
      125,
      33,
      -80,
      63,
      27,
      -119,
      -1,
      -21,
      -124,
      105,
      58,
      -99,
      -41,
      -45,
      112,
      103,
      64,
      -75,
      -34,
      93,
      48,
      -111,
      -79,
      120,
      17,
      1,
      -27,
      0,
      104,
      -104,
      -96,
      -59,
      2,
      -90,
      116,
      45,
      11,
      -94,
      118,
      -77,
      -66,
      -50,
      -67,
      -82,
      -23,
      -118,
      49,
      28,
      -20,
      -15,
      -103,
      -108,
      -86,
      -10,
      38,
      47,
      -17,
      -24,
      -116,
      53,
      3,
      -44,
      127,
      -5,
      5,
      -63,
      94,
      -112,
      32,
      61,
      -126,
      -9,
      -22,
      10,
      13,
      126,
      -8,
      80,
      26,
      -60,
      7,
      87,
      -72,
      60,
      98,
      -29,
      -56,
      -84,
      82,
      100,
      16,
      -48,
      -39,
      19,
      12,
      18,
      41,
      81,
      -71,
      -49,
      -42,
      115,
      -115,
      -127,
      84,
      -64,
      -19,
      78,
      68,
      -89,
      42,
      -123,
      37,
      -26,
      -54,
      124,
      -117,
      86,
      -128
   };
   private static final byte[] S1 = new byte[]{
      -50,
      -69,
      -21,
      -110,
      -22,
      -53,
      19,
      -63,
      -23,
      58,
      -42,
      -78,
      -46,
      -112,
      23,
      -8,
      66,
      21,
      86,
      -76,
      101,
      28,
      -120,
      67,
      -59,
      92,
      54,
      -70,
      -11,
      87,
      103,
      -115,
      49,
      -10,
      100,
      88,
      -98,
      -12,
      34,
      -86,
      117,
      15,
      2,
      -79,
      -33,
      109,
      115,
      77,
      124,
      38,
      46,
      -9,
      8,
      93,
      68,
      62,
      -97,
      20,
      -56,
      -82,
      84,
      16,
      -40,
      -68,
      26,
      107,
      105,
      -13,
      -67,
      51,
      -85,
      -6,
      -47,
      -101,
      104,
      78,
      22,
      -107,
      -111,
      -18,
      76,
      99,
      -114,
      91,
      -52,
      60,
      25,
      -95,
      -127,
      73,
      123,
      -39,
      111,
      55,
      96,
      -54,
      -25,
      43,
      72,
      -3,
      -106,
      69,
      -4,
      65,
      18,
      13,
      121,
      -27,
      -119,
      -116,
      -29,
      32,
      48,
      -36,
      -73,
      108,
      74,
      -75,
      63,
      -105,
      -44,
      98,
      45,
      6,
      -92,
      -91,
      -125,
      95,
      42,
      -38,
      -55,
      0,
      126,
      -94,
      85,
      -65,
      17,
      -43,
      -100,
      -49,
      14,
      10,
      61,
      81,
      125,
      -109,
      27,
      -2,
      -60,
      71,
      9,
      -122,
      11,
      -113,
      -99,
      106,
      7,
      -71,
      -80,
      -104,
      24,
      50,
      113,
      75,
      -17,
      59,
      112,
      -96,
      -28,
      64,
      -1,
      -61,
      -87,
      -26,
      120,
      -7,
      -117,
      70,
      -128,
      30,
      56,
      -31,
      -72,
      -88,
      -32,
      12,
      35,
      118,
      29,
      37,
      36,
      5,
      -15,
      110,
      -108,
      40,
      -102,
      -124,
      -24,
      -93,
      79,
      119,
      -45,
      -123,
      -30,
      82,
      -14,
      -126,
      80,
      122,
      47,
      116,
      83,
      -77,
      97,
      -81,
      57,
      53,
      -34,
      -51,
      31,
      -103,
      -84,
      -83,
      114,
      44,
      -35,
      -48,
      -121,
      -66,
      94,
      -90,
      -20,
      4,
      -58,
      3,
      52,
      -5,
      -37,
      89,
      -74,
      -62,
      1,
      -16,
      90,
      -19,
      -89,
      102,
      33,
      127,
      -118,
      39,
      -57,
      -64,
      41,
      -41
   };
   private static final byte[] S2 = new byte[]{
      -109,
      -39,
      -102,
      -75,
      -104,
      34,
      69,
      -4,
      -70,
      106,
      -33,
      2,
      -97,
      -36,
      81,
      89,
      74,
      23,
      43,
      -62,
      -108,
      -12,
      -69,
      -93,
      98,
      -28,
      113,
      -44,
      -51,
      112,
      22,
      -31,
      73,
      60,
      -64,
      -40,
      92,
      -101,
      -83,
      -123,
      83,
      -95,
      122,
      -56,
      45,
      -32,
      -47,
      114,
      -90,
      44,
      -60,
      -29,
      118,
      120,
      -73,
      -76,
      9,
      59,
      14,
      65,
      76,
      -34,
      -78,
      -112,
      37,
      -91,
      -41,
      3,
      17,
      0,
      -61,
      46,
      -110,
      -17,
      78,
      18,
      -99,
      125,
      -53,
      53,
      16,
      -43,
      79,
      -98,
      77,
      -87,
      85,
      -58,
      -48,
      123,
      24,
      -105,
      -45,
      54,
      -26,
      72,
      86,
      -127,
      -113,
      119,
      -52,
      -100,
      -71,
      -30,
      -84,
      -72,
      47,
      21,
      -92,
      124,
      -38,
      56,
      30,
      11,
      5,
      -42,
      20,
      110,
      108,
      126,
      102,
      -3,
      -79,
      -27,
      96,
      -81,
      94,
      51,
      -121,
      -55,
      -16,
      93,
      109,
      63,
      -120,
      -115,
      -57,
      -9,
      29,
      -23,
      -20,
      -19,
      -128,
      41,
      39,
      -49,
      -103,
      -88,
      80,
      15,
      55,
      36,
      40,
      48,
      -107,
      -46,
      62,
      91,
      64,
      -125,
      -77,
      105,
      87,
      31,
      7,
      28,
      -118,
      -68,
      32,
      -21,
      -50,
      -114,
      -85,
      -18,
      49,
      -94,
      115,
      -7,
      -54,
      58,
      26,
      -5,
      13,
      -63,
      -2,
      -6,
      -14,
      111,
      -67,
      -106,
      -35,
      67,
      82,
      -74,
      8,
      -13,
      -82,
      -66,
      25,
      -119,
      50,
      38,
      -80,
      -22,
      75,
      100,
      -124,
      -126,
      107,
      -11,
      121,
      -65,
      1,
      95,
      117,
      99,
      27,
      35,
      61,
      104,
      42,
      101,
      -24,
      -111,
      -10,
      -1,
      19,
      88,
      -15,
      71,
      10,
      127,
      -59,
      -89,
      -25,
      97,
      90,
      6,
      70,
      68,
      66,
      4,
      -96,
      -37,
      57,
      -122,
      84,
      -86,
      -116,
      52,
      33,
      -117,
      -8,
      12,
      116,
      103
   };
   private static final byte[] S3 = new byte[]{
      104,
      -115,
      -54,
      77,
      115,
      75,
      78,
      42,
      -44,
      82,
      38,
      -77,
      84,
      30,
      25,
      31,
      34,
      3,
      70,
      61,
      45,
      74,
      83,
      -125,
      19,
      -118,
      -73,
      -43,
      37,
      121,
      -11,
      -67,
      88,
      47,
      13,
      2,
      -19,
      81,
      -98,
      17,
      -14,
      62,
      85,
      94,
      -47,
      22,
      60,
      102,
      112,
      93,
      -13,
      69,
      64,
      -52,
      -24,
      -108,
      86,
      8,
      -50,
      26,
      58,
      -46,
      -31,
      -33,
      -75,
      56,
      110,
      14,
      -27,
      -12,
      -7,
      -122,
      -23,
      79,
      -42,
      -123,
      35,
      -49,
      50,
      -103,
      49,
      20,
      -82,
      -18,
      -56,
      72,
      -45,
      48,
      -95,
      -110,
      65,
      -79,
      24,
      -60,
      44,
      113,
      114,
      68,
      21,
      -3,
      55,
      -66,
      95,
      -86,
      -101,
      -120,
      -40,
      -85,
      -119,
      -100,
      -6,
      96,
      -22,
      -68,
      98,
      12,
      36,
      -90,
      -88,
      -20,
      103,
      32,
      -37,
      124,
      40,
      -35,
      -84,
      91,
      52,
      126,
      16,
      -15,
      123,
      -113,
      99,
      -96,
      5,
      -102,
      67,
      119,
      33,
      -65,
      39,
      9,
      -61,
      -97,
      -74,
      -41,
      41,
      -62,
      -21,
      -64,
      -92,
      -117,
      -116,
      29,
      -5,
      -1,
      -63,
      -78,
      -105,
      46,
      -8,
      101,
      -10,
      117,
      7,
      4,
      73,
      51,
      -28,
      -39,
      -71,
      -48,
      66,
      -57,
      108,
      -112,
      0,
      -114,
      111,
      80,
      1,
      -59,
      -38,
      71,
      63,
      -51,
      105,
      -94,
      -30,
      122,
      -89,
      -58,
      -109,
      15,
      10,
      6,
      -26,
      43,
      -106,
      -93,
      28,
      -81,
      106,
      18,
      -124,
      57,
      -25,
      -80,
      -126,
      -9,
      -2,
      -99,
      -121,
      92,
      -127,
      53,
      -34,
      -76,
      -91,
      -4,
      -128,
      -17,
      -53,
      -69,
      107,
      118,
      -70,
      90,
      125,
      120,
      11,
      -107,
      -29,
      -83,
      116,
      -104,
      59,
      54,
      100,
      109,
      -36,
      -16,
      89,
      -87,
      76,
      23,
      127,
      -111,
      -72,
      -55,
      87,
      27,
      -32,
      97
   };
   private static final byte[] T0 = new byte[]{
      -92,
      -94,
      -87,
      -59,
      78,
      -55,
      3,
      -39,
      126,
      15,
      -46,
      -83,
      -25,
      -45,
      39,
      91,
      -29,
      -95,
      -24,
      -26,
      124,
      42,
      85,
      12,
      -122,
      57,
      -41,
      -115,
      -72,
      18,
      111,
      40,
      -51,
      -118,
      112,
      86,
      114,
      -7,
      -65,
      79,
      115,
      -23,
      -9,
      87,
      22,
      -84,
      80,
      -64,
      -99,
      -73,
      71,
      113,
      96,
      -60,
      116,
      67,
      108,
      31,
      -109,
      119,
      -36,
      -50,
      32,
      -116,
      -103,
      95,
      68,
      1,
      -11,
      30,
      -121,
      94,
      97,
      44,
      75,
      29,
      -127,
      21,
      -12,
      35,
      -42,
      -22,
      -31,
      103,
      -15,
      127,
      -2,
      -38,
      60,
      7,
      83,
      106,
      -124,
      -100,
      -53,
      2,
      -125,
      51,
      -35,
      53,
      -30,
      89,
      90,
      -104,
      -91,
      -110,
      100,
      4,
      6,
      16,
      77,
      28,
      -105,
      8,
      49,
      -18,
      -85,
      5,
      -81,
      121,
      -96,
      24,
      70,
      109,
      -4,
      -119,
      -44,
      -57,
      -1,
      -16,
      -49,
      66,
      -111,
      -8,
      104,
      10,
      101,
      -114,
      -74,
      -3,
      -61,
      -17,
      120,
      76,
      -52,
      -98,
      48,
      46,
      -68,
      11,
      84,
      26,
      -90,
      -69,
      38,
      -128,
      72,
      -108,
      50,
      125,
      -89,
      63,
      -82,
      34,
      61,
      102,
      -86,
      -10,
      0,
      93,
      -67,
      74,
      -32,
      59,
      -76,
      23,
      -117,
      -97,
      118,
      -80,
      36,
      -102,
      37,
      99,
      -37,
      -21,
      122,
      62,
      92,
      -77,
      -79,
      41,
      -14,
      -54,
      88,
      110,
      -40,
      -88,
      47,
      117,
      -33,
      20,
      -5,
      19,
      73,
      -120,
      -78,
      -20,
      -28,
      52,
      45,
      -106,
      -58,
      58,
      -19,
      -107,
      14,
      -27,
      -123,
      107,
      64,
      33,
      -101,
      9,
      25,
      43,
      82,
      -34,
      69,
      -93,
      -6,
      81,
      -62,
      -75,
      -47,
      -112,
      -71,
      -13,
      55,
      -63,
      13,
      -70,
      65,
      17,
      56,
      123,
      -66,
      -48,
      -43,
      105,
      54,
      -56,
      98,
      27,
      -126,
      -113
   };
   private static final byte[] T1 = new byte[]{
      -125,
      -14,
      42,
      -21,
      -23,
      -65,
      123,
      -100,
      52,
      -106,
      -115,
      -104,
      -71,
      105,
      -116,
      41,
      61,
      -120,
      104,
      6,
      57,
      17,
      76,
      14,
      -96,
      86,
      64,
      -110,
      21,
      -68,
      -77,
      -36,
      111,
      -8,
      38,
      -70,
      -66,
      -67,
      49,
      -5,
      -61,
      -2,
      -128,
      97,
      -31,
      122,
      50,
      -46,
      112,
      32,
      -95,
      69,
      -20,
      -39,
      26,
      93,
      -76,
      -40,
      9,
      -91,
      85,
      -114,
      55,
      118,
      -87,
      103,
      16,
      23,
      54,
      101,
      -79,
      -107,
      98,
      89,
      116,
      -93,
      80,
      47,
      75,
      -56,
      -48,
      -113,
      -51,
      -44,
      60,
      -122,
      18,
      29,
      35,
      -17,
      -12,
      83,
      25,
      53,
      -26,
      127,
      94,
      -42,
      121,
      81,
      34,
      20,
      -9,
      30,
      74,
      66,
      -101,
      65,
      115,
      45,
      -63,
      92,
      -90,
      -94,
      -32,
      46,
      -45,
      40,
      -69,
      -55,
      -82,
      106,
      -47,
      90,
      48,
      -112,
      -124,
      -7,
      -78,
      88,
      -49,
      126,
      -59,
      -53,
      -105,
      -28,
      22,
      108,
      -6,
      -80,
      109,
      31,
      82,
      -103,
      13,
      78,
      3,
      -111,
      -62,
      77,
      100,
      119,
      -97,
      -35,
      -60,
      73,
      -118,
      -102,
      36,
      56,
      -89,
      87,
      -123,
      -57,
      124,
      125,
      -25,
      -10,
      -73,
      -84,
      39,
      70,
      -34,
      -33,
      59,
      -41,
      -98,
      43,
      11,
      -43,
      19,
      117,
      -16,
      114,
      -74,
      -99,
      27,
      1,
      63,
      68,
      -27,
      -121,
      -3,
      7,
      -15,
      -85,
      -108,
      24,
      -22,
      -4,
      58,
      -126,
      95,
      5,
      84,
      -37,
      0,
      -117,
      -29,
      72,
      12,
      -54,
      120,
      -119,
      10,
      -1,
      62,
      91,
      -127,
      -18,
      113,
      -30,
      -38,
      44,
      -72,
      -75,
      -52,
      110,
      -88,
      107,
      -83,
      96,
      -58,
      8,
      4,
      2,
      -24,
      -11,
      79,
      -92,
      -13,
      -64,
      -50,
      67,
      37,
      28,
      33,
      51,
      15,
      -81,
      71,
      -19,
      102,
      99,
      -109,
      -86
   };
   private static final byte[] T2 = new byte[]{
      69,
      -44,
      11,
      67,
      -15,
      114,
      -19,
      -92,
      -62,
      56,
      -26,
      113,
      -3,
      -74,
      58,
      -107,
      80,
      68,
      75,
      -30,
      116,
      107,
      30,
      17,
      90,
      -58,
      -76,
      -40,
      -91,
      -118,
      112,
      -93,
      -88,
      -6,
      5,
      -39,
      -105,
      64,
      -55,
      -112,
      -104,
      -113,
      -36,
      18,
      49,
      44,
      71,
      106,
      -103,
      -82,
      -56,
      127,
      -7,
      79,
      93,
      -106,
      111,
      -12,
      -77,
      57,
      33,
      -38,
      -100,
      -123,
      -98,
      59,
      -16,
      -65,
      -17,
      6,
      -18,
      -27,
      95,
      32,
      16,
      -52,
      60,
      84,
      74,
      82,
      -108,
      14,
      -64,
      40,
      -10,
      86,
      96,
      -94,
      -29,
      15,
      -20,
      -99,
      36,
      -125,
      126,
      -43,
      124,
      -21,
      24,
      -41,
      -51,
      -35,
      120,
      -1,
      -37,
      -95,
      9,
      -48,
      118,
      -124,
      117,
      -69,
      29,
      26,
      47,
      -80,
      -2,
      -42,
      52,
      99,
      53,
      -46,
      42,
      89,
      109,
      77,
      119,
      -25,
      -114,
      97,
      -49,
      -97,
      -50,
      39,
      -11,
      -128,
      -122,
      -57,
      -90,
      -5,
      -8,
      -121,
      -85,
      98,
      63,
      -33,
      72,
      0,
      20,
      -102,
      -67,
      91,
      4,
      -110,
      2,
      37,
      101,
      76,
      83,
      12,
      -14,
      41,
      -81,
      23,
      108,
      65,
      48,
      -23,
      -109,
      85,
      -9,
      -84,
      104,
      38,
      -60,
      125,
      -54,
      122,
      62,
      -96,
      55,
      3,
      -63,
      54,
      105,
      102,
      8,
      22,
      -89,
      -68,
      -59,
      -45,
      34,
      -73,
      19,
      70,
      50,
      -24,
      87,
      -120,
      43,
      -127,
      -78,
      78,
      100,
      28,
      -86,
      -111,
      88,
      46,
      -101,
      92,
      27,
      81,
      115,
      66,
      35,
      1,
      110,
      -13,
      13,
      -66,
      61,
      10,
      45,
      31,
      103,
      51,
      25,
      123,
      94,
      -22,
      -34,
      -117,
      -53,
      -87,
      -116,
      -115,
      -83,
      73,
      -126,
      -28,
      -70,
      -61,
      21,
      -47,
      -32,
      -119,
      -4,
      -79,
      -71,
      -75,
      7,
      121,
      -72,
      -31
   };
   private static final byte[] T3 = new byte[]{
      -78,
      -74,
      35,
      17,
      -89,
      -120,
      -59,
      -90,
      57,
      -113,
      -60,
      -24,
      115,
      34,
      67,
      -61,
      -126,
      39,
      -51,
      24,
      81,
      98,
      45,
      -9,
      92,
      14,
      59,
      -3,
      -54,
      -101,
      13,
      15,
      121,
      -116,
      16,
      76,
      116,
      28,
      10,
      -114,
      124,
      -108,
      7,
      -57,
      94,
      20,
      -95,
      33,
      87,
      80,
      78,
      -87,
      -128,
      -39,
      -17,
      100,
      65,
      -49,
      60,
      -18,
      46,
      19,
      41,
      -70,
      52,
      90,
      -82,
      -118,
      97,
      51,
      18,
      -71,
      85,
      -88,
      21,
      5,
      -10,
      3,
      6,
      73,
      -75,
      37,
      9,
      22,
      12,
      42,
      56,
      -4,
      32,
      -12,
      -27,
      127,
      -41,
      49,
      43,
      102,
      111,
      -1,
      114,
      -122,
      -16,
      -93,
      47,
      120,
      0,
      -68,
      -52,
      -30,
      -80,
      -15,
      66,
      -76,
      48,
      95,
      96,
      4,
      -20,
      -91,
      -29,
      -117,
      -25,
      29,
      -65,
      -124,
      123,
      -26,
      -127,
      -8,
      -34,
      -40,
      -46,
      23,
      -50,
      75,
      71,
      -42,
      105,
      108,
      25,
      -103,
      -102,
      1,
      -77,
      -123,
      -79,
      -7,
      89,
      -62,
      55,
      -23,
      -56,
      -96,
      -19,
      79,
      -119,
      104,
      109,
      -43,
      38,
      -111,
      -121,
      88,
      -67,
      -55,
      -104,
      -36,
      117,
      -64,
      118,
      -11,
      103,
      107,
      126,
      -21,
      82,
      -53,
      -47,
      91,
      -97,
      11,
      -37,
      64,
      -110,
      26,
      -6,
      -84,
      -28,
      -31,
      113,
      31,
      101,
      -115,
      -105,
      -98,
      -107,
      -112,
      93,
      -73,
      -63,
      -81,
      84,
      -5,
      2,
      -32,
      53,
      -69,
      58,
      77,
      -83,
      44,
      61,
      86,
      8,
      27,
      74,
      -109,
      106,
      -85,
      -72,
      122,
      -14,
      125,
      -38,
      63,
      -2,
      62,
      -66,
      -22,
      -86,
      68,
      -58,
      -48,
      54,
      72,
      112,
      -106,
      119,
      36,
      83,
      -33,
      -13,
      -125,
      40,
      50,
      69,
      30,
      -92,
      -45,
      -94,
      70,
      110,
      -100,
      -35,
      99,
      -44,
      -99
   };

   public DSTU7624Engine(int var1) throws IllegalArgumentException {
      if (var1 != 128 && var1 != 256 && var1 != 512) {
         throw new IllegalArgumentException("unsupported block length: only 128/256/512 are allowed");
      } else {
         this.wordsInBlock = var1 >>> 6;
         this.internalState = new long[this.wordsInBlock];
      }
   }

   @Override
   public void init(boolean var1, CipherParameters var2) throws IllegalArgumentException {
      if (!(var2 instanceof KeyParameter)) {
         throw new IllegalArgumentException("Invalid parameter passed to DSTU7624Engine init");
      } else {
         this.forEncryption = var1;
         byte[] var3 = ((KeyParameter)var2).getKey();
         int var4 = var3.length << 3;
         int var5 = this.wordsInBlock << 6;
         if (var4 != 128 && var4 != 256 && var4 != 512) {
            throw new IllegalArgumentException("unsupported key length: only 128/256/512 are allowed");
         } else if (var4 != var5 && var4 != 2 * var5) {
            throw new IllegalArgumentException("Unsupported key length");
         } else {
            switch (var4) {
               case 128:
                  this.roundsAmount = 10;
                  CryptoServicesRegistrar.checkConstraints(new DefaultServiceProperties(this.getAlgorithmName(), 128, var2, Utils.getPurpose(var1)));
                  break;
               case 256:
                  this.roundsAmount = 14;
                  CryptoServicesRegistrar.checkConstraints(new DefaultServiceProperties(this.getAlgorithmName(), 256, var2, Utils.getPurpose(var1)));
                  break;
               case 512:
                  this.roundsAmount = 18;
                  CryptoServicesRegistrar.checkConstraints(new DefaultServiceProperties(this.getAlgorithmName(), 256, var2, Utils.getPurpose(var1)));
            }

            this.wordsInKey = var4 >>> 6;
            this.roundKeys = new long[this.roundsAmount + 1][];

            for (int var6 = 0; var6 < this.roundKeys.length; var6++) {
               this.roundKeys[var6] = new long[this.wordsInBlock];
            }

            this.workingKey = new long[this.wordsInKey];
            if (var3.length != var4 >>> 3) {
               throw new IllegalArgumentException("Invalid key parameter passed to DSTU7624Engine init");
            } else {
               Pack.littleEndianToLong(var3, 0, this.workingKey);
               long[] var7 = new long[this.wordsInBlock];
               this.workingKeyExpandKT(this.workingKey, var7);
               this.workingKeyExpandEven(this.workingKey, var7);
               this.workingKeyExpandOdd();
            }
         }
      }
   }

   @Override
   public String getAlgorithmName() {
      return "DSTU7624";
   }

   @Override
   public int getBlockSize() {
      return this.wordsInBlock << 3;
   }

   @Override
   public int processBlock(byte[] var1, int var2, byte[] var3, int var4) throws DataLengthException, IllegalStateException {
      if (this.workingKey == null) {
         throw new IllegalStateException("DSTU7624Engine not initialised");
      } else if (var2 + this.getBlockSize() > var1.length) {
         throw new DataLengthException("Input buffer too short");
      } else if (var4 + this.getBlockSize() > var3.length) {
         throw new OutputLengthException("Output buffer too short");
      } else {
         if (this.forEncryption) {
            switch (this.wordsInBlock) {
               case 2:
                  this.encryptBlock_128(var1, var2, var3, var4);
                  break;
               default:
                  Pack.littleEndianToLong(var1, var2, this.internalState);
                  this.addRoundKey(0);
                  int var5 = 0;

                  while (true) {
                     this.subBytes();
                     this.shiftRows();
                     this.mixColumns();
                     if (++var5 == this.roundsAmount) {
                        this.addRoundKey(this.roundsAmount);
                        Pack.longToLittleEndian(this.internalState, var3, var4);
                        break;
                     }

                     this.xorRoundKey(var5);
                  }
            }
         } else {
            switch (this.wordsInBlock) {
               case 2:
                  this.decryptBlock_128(var1, var2, var3, var4);
                  break;
               default:
                  Pack.littleEndianToLong(var1, var2, this.internalState);
                  this.subRoundKey(this.roundsAmount);
                  int var6 = this.roundsAmount;

                  while (true) {
                     this.mixColumnsInv();
                     this.invShiftRows();
                     this.invSubBytes();
                     if (--var6 == 0) {
                        this.subRoundKey(0);
                        Pack.longToLittleEndian(this.internalState, var3, var4);
                        break;
                     }

                     this.xorRoundKey(var6);
                  }
            }
         }

         return this.getBlockSize();
      }
   }

   @Override
   public void reset() {
      Arrays.fill(this.internalState, 0L);
   }

   private void addRoundKey(int var1) {
      long[] var2 = this.roundKeys[var1];

      for (int var3 = 0; var3 < this.wordsInBlock; var3++) {
         this.internalState[var3] = this.internalState[var3] + var2[var3];
      }
   }

   private void subRoundKey(int var1) {
      long[] var2 = this.roundKeys[var1];

      for (int var3 = 0; var3 < this.wordsInBlock; var3++) {
         this.internalState[var3] = this.internalState[var3] - var2[var3];
      }
   }

   private void xorRoundKey(int var1) {
      long[] var2 = this.roundKeys[var1];

      for (int var3 = 0; var3 < this.wordsInBlock; var3++) {
         this.internalState[var3] = this.internalState[var3] ^ var2[var3];
      }
   }

   private void workingKeyExpandKT(long[] var1, long[] var2) {
      long[] var3 = new long[this.wordsInBlock];
      long[] var4 = new long[this.wordsInBlock];
      this.internalState = new long[this.wordsInBlock];
      this.internalState[0] = this.internalState[0] + (this.wordsInBlock + this.wordsInKey + 1);
      if (this.wordsInBlock == this.wordsInKey) {
         System.arraycopy(var1, 0, var3, 0, var3.length);
         System.arraycopy(var1, 0, var4, 0, var4.length);
      } else {
         System.arraycopy(var1, 0, var3, 0, this.wordsInBlock);
         System.arraycopy(var1, this.wordsInBlock, var4, 0, this.wordsInBlock);
      }

      for (int var5 = 0; var5 < this.internalState.length; var5++) {
         this.internalState[var5] = this.internalState[var5] + var3[var5];
      }

      this.subBytes();
      this.shiftRows();
      this.mixColumns();

      for (int var6 = 0; var6 < this.internalState.length; var6++) {
         this.internalState[var6] = this.internalState[var6] ^ var4[var6];
      }

      this.subBytes();
      this.shiftRows();
      this.mixColumns();

      for (int var7 = 0; var7 < this.internalState.length; var7++) {
         this.internalState[var7] = this.internalState[var7] + var3[var7];
      }

      this.subBytes();
      this.shiftRows();
      this.mixColumns();
      System.arraycopy(this.internalState, 0, var2, 0, this.wordsInBlock);
   }

   private void workingKeyExpandEven(long[] var1, long[] var2) {
      long[] var3 = new long[this.wordsInKey];
      long[] var4 = new long[this.wordsInBlock];
      byte var5 = 0;
      System.arraycopy(var1, 0, var3, 0, this.wordsInKey);
      long var6 = 281479271743489L;

      while (true) {
         for (int var8 = 0; var8 < this.wordsInBlock; var8++) {
            var4[var8] = var2[var8] + var6;
         }

         for (int var11 = 0; var11 < this.wordsInBlock; var11++) {
            this.internalState[var11] = var3[var11] + var4[var11];
         }

         this.subBytes();
         this.shiftRows();
         this.mixColumns();

         for (int var12 = 0; var12 < this.wordsInBlock; var12++) {
            this.internalState[var12] = this.internalState[var12] ^ var4[var12];
         }

         this.subBytes();
         this.shiftRows();
         this.mixColumns();

         for (int var13 = 0; var13 < this.wordsInBlock; var13++) {
            this.internalState[var13] = this.internalState[var13] + var4[var13];
         }

         System.arraycopy(this.internalState, 0, this.roundKeys[var5], 0, this.wordsInBlock);
         if (this.roundsAmount == var5) {
            break;
         }

         if (this.wordsInBlock != this.wordsInKey) {
            var5 += 2;
            var6 <<= 1;

            for (int var14 = 0; var14 < this.wordsInBlock; var14++) {
               var4[var14] = var2[var14] + var6;
            }

            for (int var15 = 0; var15 < this.wordsInBlock; var15++) {
               this.internalState[var15] = var3[this.wordsInBlock + var15] + var4[var15];
            }

            this.subBytes();
            this.shiftRows();
            this.mixColumns();

            for (int var16 = 0; var16 < this.wordsInBlock; var16++) {
               this.internalState[var16] = this.internalState[var16] ^ var4[var16];
            }

            this.subBytes();
            this.shiftRows();
            this.mixColumns();

            for (int var17 = 0; var17 < this.wordsInBlock; var17++) {
               this.internalState[var17] = this.internalState[var17] + var4[var17];
            }

            System.arraycopy(this.internalState, 0, this.roundKeys[var5], 0, this.wordsInBlock);
            if (this.roundsAmount == var5) {
               break;
            }
         }

         var5 += 2;
         var6 <<= 1;
         long var18 = var3[0];

         for (int var10 = 1; var10 < var3.length; var10++) {
            var3[var10 - 1] = var3[var10];
         }

         var3[var3.length - 1] = var18;
      }
   }

   private void workingKeyExpandOdd() {
      for (byte var1 = 1; var1 < this.roundsAmount; var1 += 2) {
         this.rotateLeft(this.roundKeys[var1 - 1], this.roundKeys[var1]);
      }
   }

   private void decryptBlock_128(byte[] var1, int var2, byte[] var3, int var4) {
      long var5 = Pack.littleEndianToLong(var1, var2);
      long var7 = Pack.littleEndianToLong(var1, var2 + 8);
      long[] var9 = this.roundKeys[this.roundsAmount];
      var5 -= var9[0];
      var7 -= var9[1];
      int var10 = this.roundsAmount;

      while (true) {
         var5 = mixColumnInv(var5);
         var7 = mixColumnInv(var7);
         int var11 = (int)var5;
         int var12 = (int)(var5 >>> 32);
         int var13 = (int)var7;
         int var14 = (int)(var7 >>> 32);
         byte var15 = T0[var11 & 0xFF];
         byte var16 = T1[var11 >>> 8 & 0xFF];
         byte var17 = T2[var11 >>> 16 & 0xFF];
         byte var18 = T3[var11 >>> 24];
         var11 = var15 & 255 | (var16 & 255) << 8 | (var17 & 255) << 16 | var18 << 24;
         byte var19 = T0[var14 & 0xFF];
         byte var20 = T1[var14 >>> 8 & 0xFF];
         byte var21 = T2[var14 >>> 16 & 0xFF];
         byte var22 = T3[var14 >>> 24];
         var14 = var19 & 255 | (var20 & 255) << 8 | (var21 & 255) << 16 | var22 << 24;
         var5 = var11 & 4294967295L | (long)var14 << 32;
         var15 = T0[var13 & 0xFF];
         var16 = T1[var13 >>> 8 & 0xFF];
         var17 = T2[var13 >>> 16 & 0xFF];
         var18 = T3[var13 >>> 24];
         var13 = var15 & 255 | (var16 & 255) << 8 | (var17 & 255) << 16 | var18 << 24;
         var19 = T0[var12 & 0xFF];
         var20 = T1[var12 >>> 8 & 0xFF];
         var21 = T2[var12 >>> 16 & 0xFF];
         var22 = T3[var12 >>> 24];
         var12 = var19 & 255 | (var20 & 255) << 8 | (var21 & 255) << 16 | var22 << 24;
         var7 = var13 & 4294967295L | (long)var12 << 32;
         if (--var10 == 0) {
            var9 = this.roundKeys[0];
            var5 -= var9[0];
            var7 -= var9[1];
            Pack.longToLittleEndian(var5, var3, var4);
            Pack.longToLittleEndian(var7, var3, var4 + 8);
            return;
         }

         var9 = this.roundKeys[var10];
         var5 ^= var9[0];
         var7 ^= var9[1];
      }
   }

   private void encryptBlock_128(byte[] var1, int var2, byte[] var3, int var4) {
      long var5 = Pack.littleEndianToLong(var1, var2);
      long var7 = Pack.littleEndianToLong(var1, var2 + 8);
      long[] var9 = this.roundKeys[0];
      var5 += var9[0];
      var7 += var9[1];
      int var10 = 0;

      while (true) {
         int var11 = (int)var5;
         int var12 = (int)(var5 >>> 32);
         int var13 = (int)var7;
         int var14 = (int)(var7 >>> 32);
         byte var15 = S0[var11 & 0xFF];
         byte var16 = S1[var11 >>> 8 & 0xFF];
         byte var17 = S2[var11 >>> 16 & 0xFF];
         byte var18 = S3[var11 >>> 24];
         var11 = var15 & 255 | (var16 & 255) << 8 | (var17 & 255) << 16 | var18 << 24;
         byte var19 = S0[var14 & 0xFF];
         byte var20 = S1[var14 >>> 8 & 0xFF];
         byte var21 = S2[var14 >>> 16 & 0xFF];
         byte var22 = S3[var14 >>> 24];
         var14 = var19 & 255 | (var20 & 255) << 8 | (var21 & 255) << 16 | var22 << 24;
         var5 = var11 & 4294967295L | (long)var14 << 32;
         var15 = S0[var13 & 0xFF];
         var16 = S1[var13 >>> 8 & 0xFF];
         var17 = S2[var13 >>> 16 & 0xFF];
         var18 = S3[var13 >>> 24];
         var13 = var15 & 255 | (var16 & 255) << 8 | (var17 & 255) << 16 | var18 << 24;
         var19 = S0[var12 & 0xFF];
         var20 = S1[var12 >>> 8 & 0xFF];
         var21 = S2[var12 >>> 16 & 0xFF];
         var22 = S3[var12 >>> 24];
         var12 = var19 & 255 | (var20 & 255) << 8 | (var21 & 255) << 16 | var22 << 24;
         var7 = var13 & 4294967295L | (long)var12 << 32;
         var5 = mixColumn(var5);
         var7 = mixColumn(var7);
         if (++var10 == this.roundsAmount) {
            var9 = this.roundKeys[this.roundsAmount];
            var5 += var9[0];
            var7 += var9[1];
            Pack.longToLittleEndian(var5, var3, var4);
            Pack.longToLittleEndian(var7, var3, var4 + 8);
            return;
         }

         var9 = this.roundKeys[var10];
         var5 ^= var9[0];
         var7 ^= var9[1];
      }
   }

   private void subBytes() {
      for (int var1 = 0; var1 < this.wordsInBlock; var1++) {
         long var2 = this.internalState[var1];
         int var4 = (int)var2;
         int var5 = (int)(var2 >>> 32);
         byte var6 = S0[var4 & 0xFF];
         byte var7 = S1[var4 >>> 8 & 0xFF];
         byte var8 = S2[var4 >>> 16 & 0xFF];
         byte var9 = S3[var4 >>> 24];
         var4 = var6 & 255 | (var7 & 255) << 8 | (var8 & 255) << 16 | var9 << 24;
         byte var10 = S0[var5 & 0xFF];
         byte var11 = S1[var5 >>> 8 & 0xFF];
         byte var12 = S2[var5 >>> 16 & 0xFF];
         byte var13 = S3[var5 >>> 24];
         var5 = var10 & 255 | (var11 & 255) << 8 | (var12 & 255) << 16 | var13 << 24;
         this.internalState[var1] = var4 & 4294967295L | (long)var5 << 32;
      }
   }

   private void invSubBytes() {
      for (int var1 = 0; var1 < this.wordsInBlock; var1++) {
         long var2 = this.internalState[var1];
         int var4 = (int)var2;
         int var5 = (int)(var2 >>> 32);
         byte var6 = T0[var4 & 0xFF];
         byte var7 = T1[var4 >>> 8 & 0xFF];
         byte var8 = T2[var4 >>> 16 & 0xFF];
         byte var9 = T3[var4 >>> 24];
         var4 = var6 & 255 | (var7 & 255) << 8 | (var8 & 255) << 16 | var9 << 24;
         byte var10 = T0[var5 & 0xFF];
         byte var11 = T1[var5 >>> 8 & 0xFF];
         byte var12 = T2[var5 >>> 16 & 0xFF];
         byte var13 = T3[var5 >>> 24];
         var5 = var10 & 255 | (var11 & 255) << 8 | (var12 & 255) << 16 | var13 << 24;
         this.internalState[var1] = var4 & 4294967295L | (long)var5 << 32;
      }
   }

   private void shiftRows() {
      switch (this.wordsInBlock) {
         case 2:
            long var25 = this.internalState[0];
            long var33 = this.internalState[1];
            long var41 = (var25 ^ var33) & -4294967296L;
            var25 ^= var41;
            var33 ^= var41;
            this.internalState[0] = var25;
            this.internalState[1] = var33;
            break;
         case 4:
            long var22 = this.internalState[0];
            long var30 = this.internalState[1];
            long var38 = this.internalState[2];
            long var45 = this.internalState[3];
            long var51 = (var22 ^ var38) & -4294967296L;
            var22 ^= var51;
            var38 ^= var51;
            var51 = (var30 ^ var45) & 281474976645120L;
            var30 ^= var51;
            var45 ^= var51;
            var51 = (var22 ^ var30) & -281470681808896L;
            var22 ^= var51;
            var30 ^= var51;
            var51 = (var38 ^ var45) & -281470681808896L;
            var38 ^= var51;
            var45 ^= var51;
            this.internalState[0] = var22;
            this.internalState[1] = var30;
            this.internalState[2] = var38;
            this.internalState[3] = var45;
            break;
         case 8:
            long var1 = this.internalState[0];
            long var3 = this.internalState[1];
            long var5 = this.internalState[2];
            long var7 = this.internalState[3];
            long var9 = this.internalState[4];
            long var11 = this.internalState[5];
            long var13 = this.internalState[6];
            long var15 = this.internalState[7];
            long var17 = (var1 ^ var9) & -4294967296L;
            var1 ^= var17;
            var9 ^= var17;
            var17 = (var3 ^ var11) & 72057594021150720L;
            var3 ^= var17;
            var11 ^= var17;
            var17 = (var5 ^ var13) & 281474976645120L;
            var5 ^= var17;
            var13 ^= var17;
            var17 = (var7 ^ var15) & 1099511627520L;
            var7 ^= var17;
            var15 ^= var17;
            var17 = (var1 ^ var5) & -281470681808896L;
            var1 ^= var17;
            var5 ^= var17;
            var17 = (var3 ^ var7) & 72056494543077120L;
            var3 ^= var17;
            var7 ^= var17;
            var17 = (var9 ^ var13) & -281470681808896L;
            var9 ^= var17;
            var13 ^= var17;
            var17 = (var11 ^ var15) & 72056494543077120L;
            var11 ^= var17;
            var15 ^= var17;
            var17 = (var1 ^ var3) & -71777214294589696L;
            var1 ^= var17;
            var3 ^= var17;
            var17 = (var5 ^ var7) & -71777214294589696L;
            var5 ^= var17;
            var7 ^= var17;
            var17 = (var9 ^ var11) & -71777214294589696L;
            var9 ^= var17;
            var11 ^= var17;
            var17 = (var13 ^ var15) & -71777214294589696L;
            var13 ^= var17;
            var15 ^= var17;
            this.internalState[0] = var1;
            this.internalState[1] = var3;
            this.internalState[2] = var5;
            this.internalState[3] = var7;
            this.internalState[4] = var9;
            this.internalState[5] = var11;
            this.internalState[6] = var13;
            this.internalState[7] = var15;
            break;
         default:
            throw new IllegalStateException("unsupported block length: only 128/256/512 are allowed");
      }
   }

   private void invShiftRows() {
      switch (this.wordsInBlock) {
         case 2:
            long var25 = this.internalState[0];
            long var33 = this.internalState[1];
            long var41 = (var25 ^ var33) & -4294967296L;
            var25 ^= var41;
            var33 ^= var41;
            this.internalState[0] = var25;
            this.internalState[1] = var33;
            break;
         case 4:
            long var22 = this.internalState[0];
            long var30 = this.internalState[1];
            long var38 = this.internalState[2];
            long var45 = this.internalState[3];
            long var51 = (var22 ^ var30) & -281470681808896L;
            var22 ^= var51;
            var30 ^= var51;
            var51 = (var38 ^ var45) & -281470681808896L;
            var38 ^= var51;
            var45 ^= var51;
            var51 = (var22 ^ var38) & -4294967296L;
            var22 ^= var51;
            var38 ^= var51;
            var51 = (var30 ^ var45) & 281474976645120L;
            var30 ^= var51;
            var45 ^= var51;
            this.internalState[0] = var22;
            this.internalState[1] = var30;
            this.internalState[2] = var38;
            this.internalState[3] = var45;
            break;
         case 8:
            long var1 = this.internalState[0];
            long var3 = this.internalState[1];
            long var5 = this.internalState[2];
            long var7 = this.internalState[3];
            long var9 = this.internalState[4];
            long var11 = this.internalState[5];
            long var13 = this.internalState[6];
            long var15 = this.internalState[7];
            long var17 = (var1 ^ var3) & -71777214294589696L;
            var1 ^= var17;
            var3 ^= var17;
            var17 = (var5 ^ var7) & -71777214294589696L;
            var5 ^= var17;
            var7 ^= var17;
            var17 = (var9 ^ var11) & -71777214294589696L;
            var9 ^= var17;
            var11 ^= var17;
            var17 = (var13 ^ var15) & -71777214294589696L;
            var13 ^= var17;
            var15 ^= var17;
            var17 = (var1 ^ var5) & -281470681808896L;
            var1 ^= var17;
            var5 ^= var17;
            var17 = (var3 ^ var7) & 72056494543077120L;
            var3 ^= var17;
            var7 ^= var17;
            var17 = (var9 ^ var13) & -281470681808896L;
            var9 ^= var17;
            var13 ^= var17;
            var17 = (var11 ^ var15) & 72056494543077120L;
            var11 ^= var17;
            var15 ^= var17;
            var17 = (var1 ^ var9) & -4294967296L;
            var1 ^= var17;
            var9 ^= var17;
            var17 = (var3 ^ var11) & 72057594021150720L;
            var3 ^= var17;
            var11 ^= var17;
            var17 = (var5 ^ var13) & 281474976645120L;
            var5 ^= var17;
            var13 ^= var17;
            var17 = (var7 ^ var15) & 1099511627520L;
            var7 ^= var17;
            var15 ^= var17;
            this.internalState[0] = var1;
            this.internalState[1] = var3;
            this.internalState[2] = var5;
            this.internalState[3] = var7;
            this.internalState[4] = var9;
            this.internalState[5] = var11;
            this.internalState[6] = var13;
            this.internalState[7] = var15;
            break;
         default:
            throw new IllegalStateException("unsupported block length: only 128/256/512 are allowed");
      }
   }

   private static long mixColumn(long var0) {
      long var2 = mulX(var0);
      long var4 = rotate(8, var0) ^ var0;
      var4 ^= rotate(16, var4);
      var4 ^= rotate(48, var0);
      long var6 = mulX2(var4 ^ var0 ^ var2);
      return var4 ^ rotate(32, var6) ^ rotate(40, var2) ^ rotate(48, var2);
   }

   private void mixColumns() {
      for (int var1 = 0; var1 < this.wordsInBlock; var1++) {
         this.internalState[var1] = mixColumn(this.internalState[var1]);
      }
   }

   private static long mixColumnInv(long var0) {
      long var2 = var0 ^ rotate(8, var0);
      var2 ^= rotate(32, var2);
      var2 ^= rotate(48, var0);
      long var4 = var2 ^ var0;
      long var6 = rotate(48, var0);
      long var8 = rotate(56, var0);
      long var10 = var4 ^ var8;
      long var12 = rotate(56, var4);
      var12 ^= mulX(var10);
      long var14 = rotate(16, var4) ^ var0;
      var14 ^= rotate(40, mulX(var12) ^ var0);
      long var16 = var4 ^ var6;
      var16 ^= mulX(var14);
      long var18 = rotate(16, var2);
      var18 ^= mulX(var16);
      long var20 = var4 ^ rotate(24, var0) ^ var6 ^ var8;
      var20 ^= mulX(var18);
      long var22 = rotate(32, var4) ^ var0 ^ var8;
      var22 ^= mulX(var20);
      return var2 ^ mulX(rotate(40, var22));
   }

   private void mixColumnsInv() {
      for (int var1 = 0; var1 < this.wordsInBlock; var1++) {
         this.internalState[var1] = mixColumnInv(this.internalState[var1]);
      }
   }

   private static long mulX(long var0) {
      return (var0 & 9187201950435737471L) << 1 ^ ((var0 & -9187201950435737472L) >>> 7) * 29L;
   }

   private static long mulX2(long var0) {
      return (var0 & 4557430888798830399L) << 2 ^ ((var0 & -9187201950435737472L) >>> 6) * 29L ^ ((var0 & 4629771061636907072L) >>> 6) * 29L;
   }

   private static long rotate(int var0, long var1) {
      return var1 >>> var0 | var1 << -var0;
   }

   private void rotateLeft(long[] var1, long[] var2) {
      switch (this.wordsInBlock) {
         case 2:
            long var20 = var1[0];
            long var22 = var1[1];
            var2[0] = var20 >>> 56 | var22 << 8;
            var2[1] = var22 >>> 56 | var20 << 8;
            break;
         case 4:
            long var19 = var1[0];
            long var21 = var1[1];
            long var23 = var1[2];
            long var24 = var1[3];
            var2[0] = var21 >>> 24 | var23 << 40;
            var2[1] = var23 >>> 24 | var24 << 40;
            var2[2] = var24 >>> 24 | var19 << 40;
            var2[3] = var19 >>> 24 | var21 << 40;
            break;
         case 8:
            long var3 = var1[0];
            long var5 = var1[1];
            long var7 = var1[2];
            long var9 = var1[3];
            long var11 = var1[4];
            long var13 = var1[5];
            long var15 = var1[6];
            long var17 = var1[7];
            var2[0] = var7 >>> 24 | var9 << 40;
            var2[1] = var9 >>> 24 | var11 << 40;
            var2[2] = var11 >>> 24 | var13 << 40;
            var2[3] = var13 >>> 24 | var15 << 40;
            var2[4] = var15 >>> 24 | var17 << 40;
            var2[5] = var17 >>> 24 | var3 << 40;
            var2[6] = var3 >>> 24 | var5 << 40;
            var2[7] = var5 >>> 24 | var7 << 40;
            break;
         default:
            throw new IllegalStateException("unsupported block length: only 128/256/512 are allowed");
      }
   }
}
