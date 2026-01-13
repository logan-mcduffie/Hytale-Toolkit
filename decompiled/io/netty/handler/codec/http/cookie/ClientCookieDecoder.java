package io.netty.handler.codec.http.cookie;

import io.netty.handler.codec.DateFormatter;
import io.netty.util.internal.ObjectUtil;
import java.util.Date;

public final class ClientCookieDecoder extends CookieDecoder {
   public static final ClientCookieDecoder STRICT = new ClientCookieDecoder(true);
   public static final ClientCookieDecoder LAX = new ClientCookieDecoder(false);

   private ClientCookieDecoder(boolean strict) {
      super(strict);
   }

   public Cookie decode(String header) {
      int headerLen = ObjectUtil.checkNotNull(header, "header").length();
      if (headerLen == 0) {
         return null;
      } else {
         ClientCookieDecoder.CookieBuilder cookieBuilder = null;
         int i = 0;

         while (i != headerLen) {
            char c = header.charAt(i);
            if (c == ',') {
               break;
            }

            if (c != '\t' && c != '\n' && c != 11 && c != '\f' && c != '\r' && c != ' ' && c != ';') {
               c = (char)i;

               while (true) {
                  char curChar = header.charAt(i);
                  int nameEnd;
                  int valueBegin;
                  int valueEnd;
                  if (curChar == ';') {
                     nameEnd = i;
                     valueEnd = -1;
                     valueBegin = -1;
                  } else if (curChar == '=') {
                     nameEnd = i++;
                     if (i == headerLen) {
                        valueEnd = 0;
                        valueBegin = 0;
                     } else {
                        valueBegin = i;
                        int semiPos = header.indexOf(59, i);
                        valueEnd = i = semiPos > 0 ? semiPos : headerLen;
                     }
                  } else {
                     if (++i != headerLen) {
                        continue;
                     }

                     nameEnd = headerLen;
                     valueEnd = -1;
                     valueBegin = -1;
                  }

                  if (valueEnd > 0 && header.charAt(valueEnd - 1) == ',') {
                     valueEnd--;
                  }

                  if (cookieBuilder == null) {
                     DefaultCookie cookie = this.initCookie(header, c, nameEnd, valueBegin, valueEnd);
                     if (cookie == null) {
                        return null;
                     }

                     cookieBuilder = new ClientCookieDecoder.CookieBuilder(cookie, header);
                  } else {
                     cookieBuilder.appendAttribute(c, nameEnd, valueBegin, valueEnd);
                  }
                  break;
               }
            } else {
               i++;
            }
         }

         return cookieBuilder != null ? cookieBuilder.cookie() : null;
      }
   }

   private static class CookieBuilder {
      private final String header;
      private final DefaultCookie cookie;
      private String domain;
      private String path;
      private long maxAge = Long.MIN_VALUE;
      private int expiresStart;
      private int expiresEnd;
      private boolean secure;
      private boolean httpOnly;
      private CookieHeaderNames.SameSite sameSite;
      private boolean partitioned;

      CookieBuilder(DefaultCookie cookie, String header) {
         this.cookie = cookie;
         this.header = header;
      }

      private long mergeMaxAgeAndExpires() {
         if (this.maxAge != Long.MIN_VALUE) {
            return this.maxAge;
         } else {
            if (isValueDefined(this.expiresStart, this.expiresEnd)) {
               Date expiresDate = DateFormatter.parseHttpDate(this.header, this.expiresStart, this.expiresEnd);
               if (expiresDate != null) {
                  long maxAgeMillis = expiresDate.getTime() - System.currentTimeMillis();
                  return maxAgeMillis / 1000L + (maxAgeMillis % 1000L != 0L ? 1 : 0);
               }
            }

            return Long.MIN_VALUE;
         }
      }

      Cookie cookie() {
         this.cookie.setDomain(this.domain);
         this.cookie.setPath(this.path);
         this.cookie.setMaxAge(this.mergeMaxAgeAndExpires());
         this.cookie.setSecure(this.secure);
         this.cookie.setHttpOnly(this.httpOnly);
         this.cookie.setSameSite(this.sameSite);
         this.cookie.setPartitioned(this.partitioned);
         return this.cookie;
      }

      void appendAttribute(int keyStart, int keyEnd, int valueStart, int valueEnd) {
         int length = keyEnd - keyStart;
         if (length == 4) {
            this.parse4(keyStart, valueStart, valueEnd);
         } else if (length == 6) {
            this.parse6(keyStart, valueStart, valueEnd);
         } else if (length == 7) {
            this.parse7(keyStart, valueStart, valueEnd);
         } else if (length == 8) {
            this.parse8(keyStart, valueStart, valueEnd);
         } else if (length == 11) {
            this.parse11(keyStart);
         }
      }

      private void parse4(int nameStart, int valueStart, int valueEnd) {
         if (this.header.regionMatches(true, nameStart, "Path", 0, 4)) {
            this.path = this.computeValue(valueStart, valueEnd);
         }
      }

      private void parse6(int nameStart, int valueStart, int valueEnd) {
         if (this.header.regionMatches(true, nameStart, "Domain", 0, 5)) {
            this.domain = this.computeValue(valueStart, valueEnd);
         } else if (this.header.regionMatches(true, nameStart, "Secure", 0, 5)) {
            this.secure = true;
         }
      }

      private void setMaxAge(String value) {
         try {
            this.maxAge = Math.max(Long.parseLong(value), 0L);
         } catch (NumberFormatException var3) {
         }
      }

      private void parse7(int nameStart, int valueStart, int valueEnd) {
         if (this.header.regionMatches(true, nameStart, "Expires", 0, 7)) {
            this.expiresStart = valueStart;
            this.expiresEnd = valueEnd;
         } else if (this.header.regionMatches(true, nameStart, "Max-Age", 0, 7)) {
            this.setMaxAge(this.computeValue(valueStart, valueEnd));
         }
      }

      private void parse8(int nameStart, int valueStart, int valueEnd) {
         if (this.header.regionMatches(true, nameStart, "HTTPOnly", 0, 8)) {
            this.httpOnly = true;
         } else if (this.header.regionMatches(true, nameStart, "SameSite", 0, 8)) {
            this.sameSite = CookieHeaderNames.SameSite.of(this.computeValue(valueStart, valueEnd));
         }
      }

      private void parse11(int nameStart) {
         if (this.header.regionMatches(true, nameStart, "Partitioned", 0, 11)) {
            this.partitioned = true;
         }
      }

      private static boolean isValueDefined(int valueStart, int valueEnd) {
         return valueStart != -1 && valueStart != valueEnd;
      }

      private String computeValue(int valueStart, int valueEnd) {
         return isValueDefined(valueStart, valueEnd) ? this.header.substring(valueStart, valueEnd) : null;
      }
   }
}
