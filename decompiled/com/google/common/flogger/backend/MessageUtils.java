package com.google.common.flogger.backend;

import com.google.common.flogger.LogSite;
import java.io.IOException;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Formattable;
import java.util.Formatter;
import java.util.Locale;

public final class MessageUtils {
   static final Locale FORMAT_LOCALE = Locale.ROOT;

   private MessageUtils() {
   }

   public static String safeToString(Object value) {
      try {
         return value != null ? toString(value) : "null";
      } catch (RuntimeException var2) {
         return getErrorString(value, var2);
      }
   }

   private static String toString(Object value) {
      if (!value.getClass().isArray()) {
         return String.valueOf(value);
      } else if (value instanceof int[]) {
         return Arrays.toString((int[])value);
      } else if (value instanceof long[]) {
         return Arrays.toString((long[])value);
      } else if (value instanceof byte[]) {
         return Arrays.toString((byte[])value);
      } else if (value instanceof char[]) {
         return Arrays.toString((char[])value);
      } else if (value instanceof short[]) {
         return Arrays.toString((short[])value);
      } else if (value instanceof float[]) {
         return Arrays.toString((float[])value);
      } else if (value instanceof double[]) {
         return Arrays.toString((double[])value);
      } else {
         return value instanceof boolean[] ? Arrays.toString((boolean[])value) : Arrays.toString((Object[])value);
      }
   }

   public static void safeFormatTo(Formattable value, StringBuilder out, FormatOptions options) {
      int formatFlags = options.getFlags() & 162;
      if (formatFlags != 0) {
         formatFlags = ((formatFlags & 32) != 0 ? 1 : 0) | ((formatFlags & 128) != 0 ? 2 : 0) | ((formatFlags & 2) != 0 ? 4 : 0);
      }

      int originalLength = out.length();
      Formatter formatter = new Formatter(out, FORMAT_LOCALE);

      try {
         value.formatTo(formatter, formatFlags, options.getWidth(), options.getPrecision());
      } catch (RuntimeException var9) {
         RuntimeException e = var9;
         out.setLength(originalLength);

         try {
            formatter.out().append(getErrorString(value, e));
         } catch (IOException var8) {
         }
      }
   }

   static void appendHex(StringBuilder out, Number number, FormatOptions options) {
      boolean isUpper = options.shouldUpperCase();
      long n = number.longValue();
      if (number instanceof Long) {
         appendHex(out, n, isUpper);
      } else if (number instanceof Integer) {
         appendHex(out, n & 4294967295L, isUpper);
      } else if (number instanceof Byte) {
         appendHex(out, n & 255L, isUpper);
      } else if (number instanceof Short) {
         appendHex(out, n & 65535L, isUpper);
      } else {
         if (!(number instanceof BigInteger)) {
            throw new IllegalStateException("unsupported number type: " + number.getClass());
         }

         String hex = ((BigInteger)number).toString(16);
         out.append(isUpper ? hex.toUpperCase(FORMAT_LOCALE) : hex);
      }
   }

   private static void appendHex(StringBuilder out, long n, boolean isUpper) {
      if (n == 0L) {
         out.append("0");
      } else {
         String hexChars = isUpper ? "0123456789ABCDEF" : "0123456789abcdef";

         for (int shift = 63 - Long.numberOfLeadingZeros(n) & -4; shift >= 0; shift -= 4) {
            out.append(hexChars.charAt((int)(n >>> shift & 15L)));
         }
      }
   }

   private static String getErrorString(Object value, RuntimeException e) {
      String errorMessage;
      try {
         errorMessage = e.toString();
      } catch (RuntimeException var4) {
         errorMessage = var4.getClass().getSimpleName();
      }

      return "{" + value.getClass().getName() + "@" + System.identityHashCode(value) + ": " + errorMessage + "}";
   }

   public static boolean appendLogSite(LogSite logSite, StringBuilder out) {
      if (logSite == LogSite.INVALID) {
         return false;
      } else {
         out.append(logSite.getClassName()).append('.').append(logSite.getMethodName()).append(':').append(logSite.getLineNumber());
         return true;
      }
   }
}
