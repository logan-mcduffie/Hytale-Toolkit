package ch.randelshofer.fastdoubleparser;

final class FastDoubleSwar {
   protected static boolean isDigit(char c) {
      return (char)(c - '0') < '\n';
   }

   protected static boolean isDigit(byte c) {
      return (char)(c - 48) < '\n';
   }

   public static boolean isEightDigits(byte[] a, int offset) {
      return isEightDigitsUtf8(readLongLE(a, offset));
   }

   public static boolean isEightDigits(char[] a, int offset) {
      long first = a[offset] | (long)a[offset + 1] << 16 | (long)a[offset + 2] << 32 | (long)a[offset + 3] << 48;
      long second = a[offset + 4] | (long)a[offset + 5] << 16 | (long)a[offset + 6] << 32 | (long)a[offset + 7] << 48;
      return isEightDigitsUtf16(first, second);
   }

   public static boolean isEightDigits(CharSequence a, int offset) {
      boolean success = true;

      for (int i = 0; i < 8; i++) {
         char ch = a.charAt(i + offset);
         success &= isDigit(ch);
      }

      return success;
   }

   public static boolean isEightDigitsUtf16(long first, long second) {
      long fval = first - 13511005043687472L;
      long sval = second - 13511005043687472L;
      long fpre = first + 19703549022044230L | fval;
      long spre = second + 19703549022044230L | sval;
      return ((fpre | spre) & -35747867511423104L) == 0L;
   }

   public static boolean isEightDigitsUtf8(long chunk) {
      long val = chunk - 3472328296227680304L;
      long predicate = (chunk + 5063812098665367110L | val) & -9187201950435737472L;
      return predicate == 0L;
   }

   public static boolean isEightZeroes(byte[] a, int offset) {
      return isEightZeroesUtf8(readLongLE(a, offset));
   }

   public static boolean isEightZeroes(CharSequence a, int offset) {
      boolean success = true;

      for (int i = 0; i < 8; i++) {
         success &= '0' == a.charAt(i + offset);
      }

      return success;
   }

   public static boolean isEightZeroes(char[] a, int offset) {
      long first = a[offset] | (long)a[offset + 1] << 16 | (long)a[offset + 2] << 32 | (long)a[offset + 3] << 48;
      long second = a[offset + 4] | (long)a[offset + 5] << 16 | (long)a[offset + 6] << 32 | (long)a[offset + 7] << 48;
      return isEightZeroesUtf16(first, second);
   }

   public static boolean isEightZeroesUtf16(long first, long second) {
      return first == 13511005043687472L && second == 13511005043687472L;
   }

   public static boolean isEightZeroesUtf8(long chunk) {
      return chunk == 3472328296227680304L;
   }

   public static int readIntBE(byte[] a, int offset) {
      return (a[offset] & 0xFF) << 24 | (a[offset + 1] & 0xFF) << 16 | (a[offset + 2] & 0xFF) << 8 | a[offset + 3] & 0xFF;
   }

   public static int readIntLE(byte[] a, int offset) {
      return (a[offset + 3] & 0xFF) << 24 | (a[offset + 2] & 0xFF) << 16 | (a[offset + 1] & 0xFF) << 8 | a[offset] & 0xFF;
   }

   public static long readLongBE(byte[] a, int offset) {
      return (a[offset] & 255L) << 56
         | (a[offset + 1] & 255L) << 48
         | (a[offset + 2] & 255L) << 40
         | (a[offset + 3] & 255L) << 32
         | (a[offset + 4] & 255L) << 24
         | (a[offset + 5] & 255L) << 16
         | (a[offset + 6] & 255L) << 8
         | a[offset + 7] & 255L;
   }

   public static long readLongLE(byte[] a, int offset) {
      return (a[offset + 7] & 255L) << 56
         | (a[offset + 6] & 255L) << 48
         | (a[offset + 5] & 255L) << 40
         | (a[offset + 4] & 255L) << 32
         | (a[offset + 3] & 255L) << 24
         | (a[offset + 2] & 255L) << 16
         | (a[offset + 1] & 255L) << 8
         | a[offset] & 255L;
   }

   public static int tryToParseEightDigits(char[] a, int offset) {
      long first = a[offset] | (long)a[offset + 1] << 16 | (long)a[offset + 2] << 32 | (long)a[offset + 3] << 48;
      long second = a[offset + 4] | (long)a[offset + 5] << 16 | (long)a[offset + 6] << 32 | (long)a[offset + 7] << 48;
      return tryToParseEightDigitsUtf16(first, second);
   }

   public static int tryToParseEightDigits(byte[] a, int offset) {
      return tryToParseEightDigitsUtf8(readLongLE(a, offset));
   }

   public static int tryToParseEightDigits(CharSequence str, int offset) {
      long first = str.charAt(offset) | (long)str.charAt(offset + 1) << 16 | (long)str.charAt(offset + 2) << 32 | (long)str.charAt(offset + 3) << 48;
      long second = str.charAt(offset + 4) | (long)str.charAt(offset + 5) << 16 | (long)str.charAt(offset + 6) << 32 | (long)str.charAt(offset + 7) << 48;
      return tryToParseEightDigitsUtf16(first, second);
   }

   public static int tryToParseEightDigitsUtf16(long first, long second) {
      long fval = first - 13511005043687472L;
      long sval = second - 13511005043687472L;
      long fpre = first + 19703549022044230L | fval;
      long spre = second + 19703549022044230L | sval;
      return ((fpre | spre) & -35747867511423104L) != 0L ? -1 : (int)(sval * 281475406208040961L >>> 48) + (int)(fval * 281475406208040961L >>> 48) * 10000;
   }

   public static int tryToParseEightDigitsUtf8(byte[] a, int offset) {
      return tryToParseEightDigitsUtf8(readLongLE(a, offset));
   }

   public static int tryToParseEightDigitsUtf8(long chunk) {
      long val = chunk - 3472328296227680304L;
      long predicate = (chunk + 5063812098665367110L | val) & -9187201950435737472L;
      if (predicate != 0L) {
         return -1;
      } else {
         long mask = 1095216660735L;
         long mul1 = 4294967296000100L;
         long mul2 = 42949672960001L;
         val = val * 10L + (val >>> 8);
         val = (val & mask) * mul1 + (val >>> 16 & mask) * mul2 >>> 32;
         return (int)val;
      }
   }

   public static long tryToParseEightHexDigits(CharSequence str, int offset) {
      long first = (long)str.charAt(offset) << 48 | (long)str.charAt(offset + 1) << 32 | (long)str.charAt(offset + 2) << 16 | str.charAt(offset + 3);
      long second = (long)str.charAt(offset + 4) << 48 | (long)str.charAt(offset + 5) << 32 | (long)str.charAt(offset + 6) << 16 | str.charAt(offset + 7);
      return tryToParseEightHexDigitsUtf16(first, second);
   }

   public static long tryToParseEightHexDigits(char[] chars, int offset) {
      long first = (long)chars[offset] << 48 | (long)chars[offset + 1] << 32 | (long)chars[offset + 2] << 16 | chars[offset + 3];
      long second = (long)chars[offset + 4] << 48 | (long)chars[offset + 5] << 32 | (long)chars[offset + 6] << 16 | chars[offset + 7];
      return tryToParseEightHexDigitsUtf16(first, second);
   }

   public static long tryToParseEightHexDigits(byte[] a, int offset) {
      return tryToParseEightHexDigitsUtf8(readLongBE(a, offset));
   }

   public static long tryToParseEightHexDigitsUtf16(long first, long second) {
      if (((first | second) & -71777214294589696L) != 0L) {
         return -1L;
      } else {
         long f = first * 65792L;
         long s = second * 65792L;
         long utf8Bytes = f & -281474976710656L | (f & 4294901760L) << 16 | (s & -281474976710656L) >>> 32 | (s & 4294901760L) >>> 16;
         return tryToParseEightHexDigitsUtf8(utf8Bytes);
      }
   }

   public static long tryToParseEightHexDigitsUtf8(long chunk) {
      long lt_0 = chunk - 3472328296227680304L;
      lt_0 &= -9187201950435737472L;
      long gt_9 = chunk + 5063812098665367110L;
      gt_9 &= -9187201950435737472L;
      long vec = (chunk | 2314885530818453536L) - 3472328296227680304L;
      long ge_a = vec + 5714873654208057167L;
      ge_a &= -9187201950435737472L;
      long le_f = vec - 3978709506094217015L;
      if ((lt_0 | gt_9) != (ge_a & le_f)) {
         return -1L;
      } else {
         long gt_9mask = (gt_9 >>> 7) * 255L;
         long v = vec & ~gt_9mask | vec - (2821266740684990247L & gt_9mask);
         long v2 = v | v >>> 4;
         long v3 = v2 & 71777214294589695L;
         long v4 = v3 | v3 >>> 8;
         return v4 >>> 16 & 4294901760L | v4 & 65535L;
      }
   }

   public static int tryToParseFourDigits(char[] a, int offset) {
      long first = a[offset] | (long)a[offset + 1] << 16 | (long)a[offset + 2] << 32 | (long)a[offset + 3] << 48;
      return tryToParseFourDigitsUtf16(first);
   }

   public static int tryToParseFourDigits(CharSequence str, int offset) {
      long first = str.charAt(offset) | (long)str.charAt(offset + 1) << 16 | (long)str.charAt(offset + 2) << 32 | (long)str.charAt(offset + 3) << 48;
      return tryToParseFourDigitsUtf16(first);
   }

   public static int tryToParseFourDigits(byte[] a, int offset) {
      return tryToParseFourDigitsUtf8(readIntLE(a, offset));
   }

   public static int tryToParseFourDigitsUtf16(long first) {
      long fval = first - 13511005043687472L;
      long fpre = first + 19703549022044230L | fval;
      return (fpre & -35747867511423104L) != 0L ? -1 : (int)(fval * 281475406208040961L >>> 48);
   }

   public static int tryToParseFourDigitsUtf8(int chunk) {
      int val = chunk - 808464432;
      int predicate = (chunk + 1179010630 | val) & -2139062144;
      if (predicate != 0L) {
         return -1;
      } else {
         val = val * 2561 >>> 8;
         return (val & 0xFF) * 100 + ((val & 0xFF0000) >> 16);
      }
   }

   public static int tryToParseUpTo7Digits(byte[] str, int from, int to) {
      int result = 0;
      boolean success = true;

      while (from < to) {
         byte ch = str[from];
         int digit = (char)(ch - 48);
         success &= digit < 10;
         result = 10 * result + digit;
         from++;
      }

      return success ? result : -1;
   }

   public static int tryToParseUpTo7Digits(char[] str, int from, int to) {
      int result = 0;
      boolean success = true;

      while (from < to) {
         char ch = str[from];
         int digit = (char)(ch - '0');
         success &= digit < 10;
         result = 10 * result + digit;
         from++;
      }

      return success ? result : -1;
   }

   public static int tryToParseUpTo7Digits(CharSequence str, int from, int to) {
      int result = 0;
      boolean success = true;

      while (from < to) {
         char ch = str.charAt(from);
         int digit = (char)(ch - '0');
         success &= digit < 10;
         result = 10 * result + digit;
         from++;
      }

      return success ? result : -1;
   }

   public static void writeIntBE(byte[] a, int offset, int v) {
      a[offset] = (byte)(v >>> 24);
      a[offset + 1] = (byte)(v >>> 16);
      a[offset + 2] = (byte)(v >>> 8);
      a[offset + 3] = (byte)v;
   }

   public static void writeLongBE(byte[] a, int offset, long v) {
      a[offset] = (byte)(v >>> 56);
      a[offset + 1] = (byte)(v >>> 48);
      a[offset + 2] = (byte)(v >>> 40);
      a[offset + 3] = (byte)(v >>> 32);
      a[offset + 4] = (byte)(v >>> 24);
      a[offset + 5] = (byte)(v >>> 16);
      a[offset + 6] = (byte)(v >>> 8);
      a[offset + 7] = (byte)v;
   }

   public static double fma(double a, double b, double c) {
      return a * b + c;
   }
}
