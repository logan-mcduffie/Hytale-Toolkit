package io.sentry.util;

import java.util.Arrays;
import java.util.UUID;

public final class UUIDStringUtils {
   private static final int SENTRY_UUID_STRING_LENGTH = 32;
   private static final int SENTRY_SPAN_UUID_STRING_LENGTH = 16;
   private static final char[] HEX_DIGITS = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
   private static final long[] HEX_VALUES = new long[128];

   public static String toSentryIdString(UUID uuid) {
      long mostSignificantBits = uuid.getMostSignificantBits();
      long leastSignificantBits = uuid.getLeastSignificantBits();
      return toSentryIdString(mostSignificantBits, leastSignificantBits);
   }

   public static String toSentryIdString(long mostSignificantBits, long leastSignificantBits) {
      char[] uuidChars = new char[32];
      fillMostSignificantBits(uuidChars, mostSignificantBits);
      uuidChars[16] = HEX_DIGITS[(int)((leastSignificantBits & -1152921504606846976L) >>> 60)];
      uuidChars[17] = HEX_DIGITS[(int)((leastSignificantBits & 1080863910568919040L) >>> 56)];
      uuidChars[18] = HEX_DIGITS[(int)((leastSignificantBits & 67553994410557440L) >>> 52)];
      uuidChars[19] = HEX_DIGITS[(int)((leastSignificantBits & 4222124650659840L) >>> 48)];
      uuidChars[20] = HEX_DIGITS[(int)((leastSignificantBits & 263882790666240L) >>> 44)];
      uuidChars[21] = HEX_DIGITS[(int)((leastSignificantBits & 16492674416640L) >>> 40)];
      uuidChars[22] = HEX_DIGITS[(int)((leastSignificantBits & 1030792151040L) >>> 36)];
      uuidChars[23] = HEX_DIGITS[(int)((leastSignificantBits & 64424509440L) >>> 32)];
      uuidChars[24] = HEX_DIGITS[(int)((leastSignificantBits & 4026531840L) >>> 28)];
      uuidChars[25] = HEX_DIGITS[(int)((leastSignificantBits & 251658240L) >>> 24)];
      uuidChars[26] = HEX_DIGITS[(int)((leastSignificantBits & 15728640L) >>> 20)];
      uuidChars[27] = HEX_DIGITS[(int)((leastSignificantBits & 983040L) >>> 16)];
      uuidChars[28] = HEX_DIGITS[(int)((leastSignificantBits & 61440L) >>> 12)];
      uuidChars[29] = HEX_DIGITS[(int)((leastSignificantBits & 3840L) >>> 8)];
      uuidChars[30] = HEX_DIGITS[(int)((leastSignificantBits & 240L) >>> 4)];
      uuidChars[31] = HEX_DIGITS[(int)(leastSignificantBits & 15L)];
      return new String(uuidChars);
   }

   public static String toSentrySpanIdString(UUID uuid) {
      long mostSignificantBits = uuid.getMostSignificantBits();
      return toSentrySpanIdString(mostSignificantBits);
   }

   public static String toSentrySpanIdString(long mostSignificantBits) {
      char[] uuidChars = new char[16];
      fillMostSignificantBits(uuidChars, mostSignificantBits);
      return new String(uuidChars);
   }

   private static void fillMostSignificantBits(char[] uuidChars, long mostSignificantBits) {
      uuidChars[0] = HEX_DIGITS[(int)((mostSignificantBits & -1152921504606846976L) >>> 60)];
      uuidChars[1] = HEX_DIGITS[(int)((mostSignificantBits & 1080863910568919040L) >>> 56)];
      uuidChars[2] = HEX_DIGITS[(int)((mostSignificantBits & 67553994410557440L) >>> 52)];
      uuidChars[3] = HEX_DIGITS[(int)((mostSignificantBits & 4222124650659840L) >>> 48)];
      uuidChars[4] = HEX_DIGITS[(int)((mostSignificantBits & 263882790666240L) >>> 44)];
      uuidChars[5] = HEX_DIGITS[(int)((mostSignificantBits & 16492674416640L) >>> 40)];
      uuidChars[6] = HEX_DIGITS[(int)((mostSignificantBits & 1030792151040L) >>> 36)];
      uuidChars[7] = HEX_DIGITS[(int)((mostSignificantBits & 64424509440L) >>> 32)];
      uuidChars[8] = HEX_DIGITS[(int)((mostSignificantBits & 4026531840L) >>> 28)];
      uuidChars[9] = HEX_DIGITS[(int)((mostSignificantBits & 251658240L) >>> 24)];
      uuidChars[10] = HEX_DIGITS[(int)((mostSignificantBits & 15728640L) >>> 20)];
      uuidChars[11] = HEX_DIGITS[(int)((mostSignificantBits & 983040L) >>> 16)];
      uuidChars[12] = HEX_DIGITS[(int)((mostSignificantBits & 61440L) >>> 12)];
      uuidChars[13] = HEX_DIGITS[(int)((mostSignificantBits & 3840L) >>> 8)];
      uuidChars[14] = HEX_DIGITS[(int)((mostSignificantBits & 240L) >>> 4)];
      uuidChars[15] = HEX_DIGITS[(int)(mostSignificantBits & 15L)];
   }

   static {
      Arrays.fill(HEX_VALUES, -1L);
      HEX_VALUES[48] = 0L;
      HEX_VALUES[49] = 1L;
      HEX_VALUES[50] = 2L;
      HEX_VALUES[51] = 3L;
      HEX_VALUES[52] = 4L;
      HEX_VALUES[53] = 5L;
      HEX_VALUES[54] = 6L;
      HEX_VALUES[55] = 7L;
      HEX_VALUES[56] = 8L;
      HEX_VALUES[57] = 9L;
      HEX_VALUES[97] = 10L;
      HEX_VALUES[98] = 11L;
      HEX_VALUES[99] = 12L;
      HEX_VALUES[100] = 13L;
      HEX_VALUES[101] = 14L;
      HEX_VALUES[102] = 15L;
      HEX_VALUES[65] = 10L;
      HEX_VALUES[66] = 11L;
      HEX_VALUES[67] = 12L;
      HEX_VALUES[68] = 13L;
      HEX_VALUES[69] = 14L;
      HEX_VALUES[70] = 15L;
   }
}
