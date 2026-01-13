package io.netty.handler.codec.http;

import io.netty.util.AsciiString;

public final class HttpHeaderValidationUtil {
   private HttpHeaderValidationUtil() {
   }

   public static boolean isConnectionHeader(CharSequence name, boolean ignoreTeHeader) {
      int len = name.length();
      switch (len) {
         case 2:
            return ignoreTeHeader ? false : AsciiString.contentEqualsIgnoreCase(name, HttpHeaderNames.TE);
         case 7:
            return AsciiString.contentEqualsIgnoreCase(name, HttpHeaderNames.UPGRADE);
         case 10:
            return AsciiString.contentEqualsIgnoreCase(name, HttpHeaderNames.CONNECTION)
               || AsciiString.contentEqualsIgnoreCase(name, HttpHeaderNames.KEEP_ALIVE);
         case 16:
            return AsciiString.contentEqualsIgnoreCase(name, HttpHeaderNames.PROXY_CONNECTION);
         case 17:
            return AsciiString.contentEqualsIgnoreCase(name, HttpHeaderNames.TRANSFER_ENCODING);
         default:
            return false;
      }
   }

   public static boolean isTeNotTrailers(CharSequence name, CharSequence value) {
      return name.length() != 2
         ? false
         : AsciiString.contentEqualsIgnoreCase(name, HttpHeaderNames.TE) && !AsciiString.contentEqualsIgnoreCase(value, HttpHeaderValues.TRAILERS);
   }

   public static int validateValidHeaderValue(CharSequence value) {
      int length = value.length();
      if (length == 0) {
         return -1;
      } else {
         return value instanceof AsciiString ? verifyValidHeaderValueAsciiString((AsciiString)value) : verifyValidHeaderValueCharSequence(value);
      }
   }

   private static int verifyValidHeaderValueAsciiString(AsciiString value) {
      byte[] array = value.array();
      int start = value.arrayOffset();
      int b = array[start] & 255;
      if (b >= 33 && b != 127) {
         int end = start + value.length();

         for (int i = start + 1; i < end; i++) {
            b = array[i] & 255;
            if (b < 32 && b != 9 || b == 127) {
               return i - start;
            }
         }

         return -1;
      } else {
         return 0;
      }
   }

   private static int verifyValidHeaderValueCharSequence(CharSequence value) {
      int b = value.charAt(0);
      if (b >= 33 && b != 127) {
         int length = value.length();

         for (int i = 1; i < length; i++) {
            int var4 = value.charAt(i);
            if (var4 < ' ' && var4 != '\t' || var4 == 127) {
               return i;
            }
         }

         return -1;
      } else {
         return 0;
      }
   }

   public static int validateToken(CharSequence token) {
      return HttpUtil.validateToken(token);
   }
}
