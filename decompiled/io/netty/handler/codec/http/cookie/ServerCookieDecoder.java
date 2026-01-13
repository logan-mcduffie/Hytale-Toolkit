package io.netty.handler.codec.http.cookie;

import io.netty.util.internal.ObjectUtil;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public final class ServerCookieDecoder extends CookieDecoder {
   private static final String RFC2965_VERSION = "$Version";
   private static final String RFC2965_PATH = "$Path";
   private static final String RFC2965_DOMAIN = "$Domain";
   private static final String RFC2965_PORT = "$Port";
   public static final ServerCookieDecoder STRICT = new ServerCookieDecoder(true);
   public static final ServerCookieDecoder LAX = new ServerCookieDecoder(false);

   private ServerCookieDecoder(boolean strict) {
      super(strict);
   }

   public List<Cookie> decodeAll(String header) {
      List<Cookie> cookies = new ArrayList<>();
      this.decode(cookies, header);
      return Collections.unmodifiableList(cookies);
   }

   public Set<Cookie> decode(String header) {
      Set<Cookie> cookies = new TreeSet<>();
      this.decode(cookies, header);
      return cookies;
   }

   private void decode(Collection<? super Cookie> cookies, String header) {
      int headerLen = ObjectUtil.checkNotNull(header, "header").length();
      if (headerLen != 0) {
         int i = 0;
         boolean rfc2965Style = false;
         if (header.regionMatches(true, 0, "$Version", 0, "$Version".length())) {
            i = header.indexOf(59) + 1;
            rfc2965Style = true;
         }

         while (i != headerLen) {
            char c = header.charAt(i);
            if (c != '\t' && c != '\n' && c != 11 && c != '\f' && c != '\r' && c != ' ' && c != ',' && c != ';') {
               c = (char)i;

               int nameEnd;
               int valueBegin;
               int valueEnd;
               while (true) {
                  char curChar = header.charAt(i);
                  if (curChar == ';') {
                     nameEnd = i;
                     valueEnd = -1;
                     valueBegin = -1;
                     break;
                  }

                  if (curChar == '=') {
                     nameEnd = i++;
                     if (i == headerLen) {
                        valueEnd = 0;
                        valueBegin = 0;
                     } else {
                        valueBegin = i;
                        int semiPos = header.indexOf(59, i);
                        valueEnd = i = semiPos > 0 ? semiPos : headerLen;
                     }
                     break;
                  }

                  if (++i == headerLen) {
                     nameEnd = headerLen;
                     valueEnd = -1;
                     valueBegin = -1;
                     break;
                  }
               }

               if (!rfc2965Style
                  || !header.regionMatches(c, "$Path", 0, "$Path".length())
                     && !header.regionMatches(c, "$Domain", 0, "$Domain".length())
                     && !header.regionMatches(c, "$Port", 0, "$Port".length())) {
                  DefaultCookie cookie = this.initCookie(header, c, nameEnd, valueBegin, valueEnd);
                  if (cookie != null) {
                     cookies.add(cookie);
                  }
               }
            } else {
               i++;
            }
         }
      }
   }
}
