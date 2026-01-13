package io.netty.util.internal;

public final class SWARUtil {
   public static long compilePattern(byte byteToFind) {
      return (byteToFind & 255L) * 72340172838076673L;
   }

   public static long applyPattern(long word, long pattern) {
      long input = word ^ pattern;
      long tmp = (input & 9187201950435737471L) + 9187201950435737471L;
      return ~(tmp | input | 9187201950435737471L);
   }

   public static int getIndex(long word, boolean isBigEndian) {
      int zeros = isBigEndian ? Long.numberOfLeadingZeros(word) : Long.numberOfTrailingZeros(word);
      return zeros >>> 3;
   }

   private static long applyUpperCasePattern(long word) {
      long rotated = word & 9187201950435737471L;
      rotated += 2676586395008836901L;
      rotated &= 9187201950435737471L;
      rotated += 1880844493789993498L;
      rotated &= ~word;
      return rotated & -9187201950435737472L;
   }

   private static int applyUpperCasePattern(int word) {
      int rotated = word & 2139062143;
      rotated += 623191333;
      rotated &= 2139062143;
      rotated += 437918234;
      rotated &= ~word;
      return rotated & -2139062144;
   }

   private static long applyLowerCasePattern(long word) {
      long rotated = word & 9187201950435737471L;
      rotated += 361700864190383365L;
      rotated &= 9187201950435737471L;
      rotated += 1880844493789993498L;
      rotated &= ~word;
      return rotated & -9187201950435737472L;
   }

   private static int applyLowerCasePattern(int word) {
      int rotated = word & 2139062143;
      rotated += 84215045;
      rotated &= 2139062143;
      rotated += 437918234;
      rotated &= ~word;
      return rotated & -2139062144;
   }

   public static boolean containsUpperCase(long word) {
      return applyUpperCasePattern(word) != 0L;
   }

   public static boolean containsUpperCase(int word) {
      return applyUpperCasePattern(word) != 0;
   }

   public static boolean containsLowerCase(long word) {
      return applyLowerCasePattern(word) != 0L;
   }

   public static boolean containsLowerCase(int word) {
      return applyLowerCasePattern(word) != 0;
   }

   public static long toLowerCase(long word) {
      long mask = applyUpperCasePattern(word) >>> 2;
      return word | mask;
   }

   public static int toLowerCase(int word) {
      int mask = applyUpperCasePattern(word) >>> 2;
      return word | mask;
   }

   public static long toUpperCase(long word) {
      long mask = applyLowerCasePattern(word) >>> 2;
      return word & ~mask;
   }

   public static int toUpperCase(int word) {
      int mask = applyLowerCasePattern(word) >>> 2;
      return word & ~mask;
   }

   private SWARUtil() {
   }
}
