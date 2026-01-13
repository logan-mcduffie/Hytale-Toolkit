package org.bson.internal;

public final class Base64 {
   private static final int BYTES_PER_UNENCODED_BLOCK = 3;
   private static final int BYTES_PER_ENCODED_BLOCK = 4;
   private static final int SIX_BIT_MASK = 63;
   private static final byte PAD = 61;
   private static final byte[] ENCODE_TABLE = new byte[]{
      65,
      66,
      67,
      68,
      69,
      70,
      71,
      72,
      73,
      74,
      75,
      76,
      77,
      78,
      79,
      80,
      81,
      82,
      83,
      84,
      85,
      86,
      87,
      88,
      89,
      90,
      97,
      98,
      99,
      100,
      101,
      102,
      103,
      104,
      105,
      106,
      107,
      108,
      109,
      110,
      111,
      112,
      113,
      114,
      115,
      116,
      117,
      118,
      119,
      120,
      121,
      122,
      48,
      49,
      50,
      51,
      52,
      53,
      54,
      55,
      56,
      57,
      43,
      47
   };
   private static final int[] DECODE_TABLE = new int[128];

   public static byte[] decode(String s) {
      int delta = s.endsWith("==") ? 2 : (s.endsWith("=") ? 1 : 0);
      byte[] buffer = new byte[s.length() * 3 / 4 - delta];
      int mask = 255;
      int pos = 0;

      for (int i = 0; i < s.length(); i += 4) {
         int c0 = DECODE_TABLE[s.charAt(i)];
         int c1 = DECODE_TABLE[s.charAt(i + 1)];
         buffer[pos++] = (byte)((c0 << 2 | c1 >> 4) & mask);
         if (pos >= buffer.length) {
            return buffer;
         }

         int c2 = DECODE_TABLE[s.charAt(i + 2)];
         buffer[pos++] = (byte)((c1 << 4 | c2 >> 2) & mask);
         if (pos >= buffer.length) {
            return buffer;
         }

         int c3 = DECODE_TABLE[s.charAt(i + 3)];
         buffer[pos++] = (byte)((c2 << 6 | c3) & mask);
      }

      return buffer;
   }

   public static String encode(byte[] in) {
      int modulus = 0;
      int bitWorkArea = 0;
      int numEncodedBytes = in.length / 3 * 4 + (in.length % 3 == 0 ? 0 : 4);
      byte[] buffer = new byte[numEncodedBytes];
      int pos = 0;

      for (int b : in) {
         modulus = (modulus + 1) % 3;
         if (b < 0) {
            b += 256;
         }

         bitWorkArea = (bitWorkArea << 8) + b;
         if (0 == modulus) {
            buffer[pos++] = ENCODE_TABLE[bitWorkArea >> 18 & 63];
            buffer[pos++] = ENCODE_TABLE[bitWorkArea >> 12 & 63];
            buffer[pos++] = ENCODE_TABLE[bitWorkArea >> 6 & 63];
            buffer[pos++] = ENCODE_TABLE[bitWorkArea & 63];
         }
      }

      switch (modulus) {
         case 1:
            buffer[pos++] = ENCODE_TABLE[bitWorkArea >> 2 & 63];
            buffer[pos++] = ENCODE_TABLE[bitWorkArea << 4 & 63];
            buffer[pos++] = 61;
            buffer[pos] = 61;
            break;
         case 2:
            buffer[pos++] = ENCODE_TABLE[bitWorkArea >> 10 & 63];
            buffer[pos++] = ENCODE_TABLE[bitWorkArea >> 4 & 63];
            buffer[pos++] = ENCODE_TABLE[bitWorkArea << 2 & 63];
            buffer[pos] = 61;
      }

      return byteArrayToString(buffer);
   }

   private static String byteArrayToString(byte[] buffer) {
      return new String(buffer, 0, 0, buffer.length);
   }

   private Base64() {
   }

   static {
      int i = 0;

      while (i < ENCODE_TABLE.length) {
         DECODE_TABLE[ENCODE_TABLE[i]] = i++;
      }
   }
}
