package io.netty.util.internal;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public final class StringUtil {
   public static final String EMPTY_STRING = "";
   public static final String NEWLINE = SystemPropertyUtil.get("line.separator", "\n");
   public static final char DOUBLE_QUOTE = '"';
   public static final char COMMA = ',';
   public static final char LINE_FEED = '\n';
   public static final char CARRIAGE_RETURN = '\r';
   public static final char TAB = '\t';
   public static final char SPACE = ' ';
   private static final String[] BYTE2HEX_PAD = new String[256];
   private static final String[] BYTE2HEX_NOPAD = new String[256];
   private static final byte[] HEX2B;
   private static final int CSV_NUMBER_ESCAPE_CHARACTERS = 7;
   private static final char PACKAGE_SEPARATOR_CHAR = '.';

   private StringUtil() {
   }

   public static String substringAfter(String value, char delim) {
      int pos = value.indexOf(delim);
      return pos >= 0 ? value.substring(pos + 1) : null;
   }

   public static String substringBefore(String value, char delim) {
      int pos = value.indexOf(delim);
      return pos >= 0 ? value.substring(0, pos) : null;
   }

   public static boolean commonSuffixOfLength(String s, String p, int len) {
      return s != null && p != null && len >= 0 && s.regionMatches(s.length() - len, p, p.length() - len, len);
   }

   public static String byteToHexStringPadded(int value) {
      return BYTE2HEX_PAD[value & 0xFF];
   }

   public static <T extends Appendable> T byteToHexStringPadded(T buf, int value) {
      try {
         buf.append(byteToHexStringPadded(value));
      } catch (IOException var3) {
         PlatformDependent.throwException(var3);
      }

      return buf;
   }

   public static String toHexStringPadded(byte[] src) {
      return toHexStringPadded(src, 0, src.length);
   }

   public static String toHexStringPadded(byte[] src, int offset, int length) {
      return toHexStringPadded(new StringBuilder(length << 1), src, offset, length).toString();
   }

   public static <T extends Appendable> T toHexStringPadded(T dst, byte[] src) {
      return toHexStringPadded(dst, src, 0, src.length);
   }

   public static <T extends Appendable> T toHexStringPadded(T dst, byte[] src, int offset, int length) {
      int end = offset + length;

      for (int i = offset; i < end; i++) {
         byteToHexStringPadded(dst, src[i]);
      }

      return dst;
   }

   public static String byteToHexString(int value) {
      return BYTE2HEX_NOPAD[value & 0xFF];
   }

   public static <T extends Appendable> T byteToHexString(T buf, int value) {
      try {
         buf.append(byteToHexString(value));
      } catch (IOException var3) {
         PlatformDependent.throwException(var3);
      }

      return buf;
   }

   public static String toHexString(byte[] src) {
      return toHexString(src, 0, src.length);
   }

   public static String toHexString(byte[] src, int offset, int length) {
      return toHexString(new StringBuilder(length << 1), src, offset, length).toString();
   }

   public static <T extends Appendable> T toHexString(T dst, byte[] src) {
      return toHexString(dst, src, 0, src.length);
   }

   public static <T extends Appendable> T toHexString(T dst, byte[] src, int offset, int length) {
      assert length >= 0;

      if (length == 0) {
         return dst;
      } else {
         int end = offset + length;
         int endMinusOne = end - 1;
         int i = offset;

         while (i < endMinusOne && src[i] == 0) {
            i++;
         }

         byteToHexString(dst, src[i++]);
         int remaining = end - i;
         toHexStringPadded(dst, src, i, remaining);
         return dst;
      }
   }

   public static int decodeHexNibble(char c) {
      return HEX2B[c];
   }

   public static int decodeHexNibble(byte b) {
      return HEX2B[b];
   }

   public static byte decodeHexByte(CharSequence s, int pos) {
      int hi = decodeHexNibble(s.charAt(pos));
      int lo = decodeHexNibble(s.charAt(pos + 1));
      if (hi != -1 && lo != -1) {
         return (byte)((hi << 4) + lo);
      } else {
         throw new IllegalArgumentException(String.format("invalid hex byte '%s' at index %d of '%s'", s.subSequence(pos, pos + 2), pos, s));
      }
   }

   public static byte[] decodeHexDump(CharSequence hexDump, int fromIndex, int length) {
      if (length < 0 || (length & 1) != 0) {
         throw new IllegalArgumentException("length: " + length);
      } else if (length == 0) {
         return EmptyArrays.EMPTY_BYTES;
      } else {
         byte[] bytes = new byte[length >>> 1];

         for (int i = 0; i < length; i += 2) {
            bytes[i >>> 1] = decodeHexByte(hexDump, fromIndex + i);
         }

         return bytes;
      }
   }

   public static byte[] decodeHexDump(CharSequence hexDump) {
      return decodeHexDump(hexDump, 0, hexDump.length());
   }

   public static String className(Object o) {
      return o == null ? "null_object" : o.getClass().getName();
   }

   public static String simpleClassName(Object o) {
      return o == null ? "null_object" : simpleClassName(o.getClass());
   }

   public static String simpleClassName(Class<?> clazz) {
      String className = ObjectUtil.checkNotNull(clazz, "clazz").getName();
      int lastDotIdx = className.lastIndexOf(46);
      return lastDotIdx > -1 ? className.substring(lastDotIdx + 1) : className;
   }

   public static CharSequence escapeCsv(CharSequence value) {
      return escapeCsv(value, false);
   }

   public static CharSequence escapeCsv(CharSequence value, boolean trimWhiteSpace) {
      int length = ObjectUtil.checkNotNull(value, "value").length();
      int start;
      int last;
      if (trimWhiteSpace) {
         start = indexOfFirstNonOwsChar(value, length);
         last = indexOfLastNonOwsChar(value, start, length);
      } else {
         start = 0;
         last = length - 1;
      }

      if (start > last) {
         return "";
      } else {
         int firstUnescapedSpecial = -1;
         boolean quoted = false;
         if (isDoubleQuote(value.charAt(start))) {
            quoted = isDoubleQuote(value.charAt(last)) && last > start;
            if (quoted) {
               start++;
               last--;
            } else {
               firstUnescapedSpecial = start;
            }
         }

         if (firstUnescapedSpecial < 0) {
            if (quoted) {
               for (int i = start; i <= last; i++) {
                  if (isDoubleQuote(value.charAt(i))) {
                     if (i == last || !isDoubleQuote(value.charAt(i + 1))) {
                        firstUnescapedSpecial = i;
                        break;
                     }

                     i++;
                  }
               }
            } else {
               for (int ix = start; ix <= last; ix++) {
                  char c = value.charAt(ix);
                  if (c == '\n' || c == '\r' || c == ',') {
                     firstUnescapedSpecial = ix;
                     break;
                  }

                  if (isDoubleQuote(c)) {
                     if (ix == last || !isDoubleQuote(value.charAt(ix + 1))) {
                        firstUnescapedSpecial = ix;
                        break;
                     }

                     ix++;
                  }
               }
            }

            if (firstUnescapedSpecial < 0) {
               return quoted ? value.subSequence(start - 1, last + 2) : value.subSequence(start, last + 1);
            }
         }

         StringBuilder result = new StringBuilder(last - start + 1 + 7);
         result.append('"').append(value, start, firstUnescapedSpecial);

         for (int ix = firstUnescapedSpecial; ix <= last; ix++) {
            char cx = value.charAt(ix);
            if (isDoubleQuote(cx)) {
               result.append('"');
               if (ix < last && isDoubleQuote(value.charAt(ix + 1))) {
                  ix++;
               }
            }

            result.append(cx);
         }

         return result.append('"');
      }
   }

   public static CharSequence unescapeCsv(CharSequence value) {
      int length = ObjectUtil.checkNotNull(value, "value").length();
      if (length == 0) {
         return value;
      } else {
         int last = length - 1;
         boolean quoted = isDoubleQuote(value.charAt(0)) && isDoubleQuote(value.charAt(last)) && length != 1;
         if (!quoted) {
            validateCsvFormat(value);
            return value;
         } else {
            StringBuilder unescaped = InternalThreadLocalMap.get().stringBuilder();

            for (int i = 1; i < last; i++) {
               char current = value.charAt(i);
               if (current == '"') {
                  if (!isDoubleQuote(value.charAt(i + 1)) || i + 1 == last) {
                     throw newInvalidEscapedCsvFieldException(value, i);
                  }

                  i++;
               }

               unescaped.append(current);
            }

            return unescaped.toString();
         }
      }
   }

   public static List<CharSequence> unescapeCsvFields(CharSequence value) {
      List<CharSequence> unescaped = new ArrayList<>(2);
      StringBuilder current = InternalThreadLocalMap.get().stringBuilder();
      boolean quoted = false;
      int last = value.length() - 1;

      for (int i = 0; i <= last; i++) {
         char c = value.charAt(i);
         if (quoted) {
            switch (c) {
               case '"':
                  if (i == last) {
                     unescaped.add(current.toString());
                     return unescaped;
                  }

                  char next = value.charAt(++i);
                  if (next == '"') {
                     current.append('"');
                  } else {
                     if (next != ',') {
                        throw newInvalidEscapedCsvFieldException(value, i - 1);
                     }

                     quoted = false;
                     unescaped.add(current.toString());
                     current.setLength(0);
                  }
                  break;
               default:
                  current.append(c);
            }
         } else {
            switch (c) {
               case '\n':
               case '\r':
                  throw newInvalidEscapedCsvFieldException(value, i);
               case '"':
                  if (current.length() != 0) {
                     throw newInvalidEscapedCsvFieldException(value, i);
                  }

                  quoted = true;
                  break;
               case ',':
                  unescaped.add(current.toString());
                  current.setLength(0);
                  break;
               default:
                  current.append(c);
            }
         }
      }

      if (quoted) {
         throw newInvalidEscapedCsvFieldException(value, last);
      } else {
         unescaped.add(current.toString());
         return unescaped;
      }
   }

   private static void validateCsvFormat(CharSequence value) {
      int length = value.length();

      for (int i = 0; i < length; i++) {
         switch (value.charAt(i)) {
            case '\n':
            case '\r':
            case '"':
            case ',':
               throw newInvalidEscapedCsvFieldException(value, i);
         }
      }
   }

   private static IllegalArgumentException newInvalidEscapedCsvFieldException(CharSequence value, int index) {
      return new IllegalArgumentException("invalid escaped CSV field: " + value + " index: " + index);
   }

   public static int length(String s) {
      return s == null ? 0 : s.length();
   }

   public static boolean isNullOrEmpty(String s) {
      return s == null || s.isEmpty();
   }

   public static int indexOfNonWhiteSpace(CharSequence seq, int offset) {
      while (offset < seq.length()) {
         if (!Character.isWhitespace(seq.charAt(offset))) {
            return offset;
         }

         offset++;
      }

      return -1;
   }

   public static int indexOfWhiteSpace(CharSequence seq, int offset) {
      while (offset < seq.length()) {
         if (Character.isWhitespace(seq.charAt(offset))) {
            return offset;
         }

         offset++;
      }

      return -1;
   }

   public static boolean isSurrogate(char c) {
      return c >= '\ud800' && c <= '\udfff';
   }

   private static boolean isDoubleQuote(char c) {
      return c == '"';
   }

   public static boolean endsWith(CharSequence s, char c) {
      int len = s.length();
      return len > 0 && s.charAt(len - 1) == c;
   }

   public static CharSequence trimOws(CharSequence value) {
      int length = value.length();
      if (length == 0) {
         return value;
      } else {
         int start = indexOfFirstNonOwsChar(value, length);
         int end = indexOfLastNonOwsChar(value, start, length);
         return start == 0 && end == length - 1 ? value : value.subSequence(start, end + 1);
      }
   }

   public static CharSequence join(CharSequence separator, Iterable<? extends CharSequence> elements) {
      ObjectUtil.checkNotNull(separator, "separator");
      ObjectUtil.checkNotNull(elements, "elements");
      Iterator<? extends CharSequence> iterator = elements.iterator();
      if (!iterator.hasNext()) {
         return "";
      } else {
         CharSequence firstElement = iterator.next();
         if (!iterator.hasNext()) {
            return firstElement;
         } else {
            StringBuilder builder = new StringBuilder(firstElement);

            do {
               builder.append(separator).append(iterator.next());
            } while (iterator.hasNext());

            return builder;
         }
      }
   }

   private static int indexOfFirstNonOwsChar(CharSequence value, int length) {
      int i = 0;

      while (i < length && isOws(value.charAt(i))) {
         i++;
      }

      return i;
   }

   private static int indexOfLastNonOwsChar(CharSequence value, int start, int length) {
      int i = length - 1;

      while (i > start && isOws(value.charAt(i))) {
         i--;
      }

      return i;
   }

   private static boolean isOws(char c) {
      return c == ' ' || c == '\t';
   }

   static {
      for (int i = 0; i < BYTE2HEX_PAD.length; i++) {
         String str = Integer.toHexString(i);
         BYTE2HEX_PAD[i] = i > 15 ? str : '0' + str;
         BYTE2HEX_NOPAD[i] = str;
      }

      HEX2B = new byte[65536];
      Arrays.fill(HEX2B, (byte)-1);
      HEX2B[48] = 0;
      HEX2B[49] = 1;
      HEX2B[50] = 2;
      HEX2B[51] = 3;
      HEX2B[52] = 4;
      HEX2B[53] = 5;
      HEX2B[54] = 6;
      HEX2B[55] = 7;
      HEX2B[56] = 8;
      HEX2B[57] = 9;
      HEX2B[65] = 10;
      HEX2B[66] = 11;
      HEX2B[67] = 12;
      HEX2B[68] = 13;
      HEX2B[69] = 14;
      HEX2B[70] = 15;
      HEX2B[97] = 10;
      HEX2B[98] = 11;
      HEX2B[99] = 12;
      HEX2B[100] = 13;
      HEX2B[101] = 14;
      HEX2B[102] = 15;
   }
}
