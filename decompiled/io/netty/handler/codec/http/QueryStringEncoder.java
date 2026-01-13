package io.netty.handler.codec.http;

import io.netty.util.CharsetUtil;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.StringUtil;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;

public class QueryStringEncoder {
   private final Charset charset;
   private final StringBuilder uriBuilder;
   private boolean hasParams;
   private static final byte WRITE_UTF_UNKNOWN = 63;
   private static final char[] CHAR_MAP = "0123456789ABCDEF".toCharArray();

   public QueryStringEncoder(String uri) {
      this(uri, HttpConstants.DEFAULT_CHARSET);
   }

   public QueryStringEncoder(String uri, Charset charset) {
      ObjectUtil.checkNotNull(charset, "charset");
      this.uriBuilder = new StringBuilder(uri);
      this.charset = CharsetUtil.UTF_8.equals(charset) ? null : charset;
   }

   public void addParam(String name, String value) {
      ObjectUtil.checkNotNull(name, "name");
      if (this.hasParams) {
         this.uriBuilder.append('&');
      } else {
         this.uriBuilder.append('?');
         this.hasParams = true;
      }

      this.encodeComponent(name);
      if (value != null) {
         this.uriBuilder.append('=');
         this.encodeComponent(value);
      }
   }

   private void encodeComponent(CharSequence s) {
      if (this.charset == null) {
         this.encodeUtf8Component(s);
      } else {
         this.encodeNonUtf8Component(s);
      }
   }

   public URI toUri() throws URISyntaxException {
      return new URI(this.toString());
   }

   @Override
   public String toString() {
      return this.uriBuilder.toString();
   }

   private void encodeNonUtf8Component(CharSequence s) {
      char[] buf = null;
      int i = 0;
      int len = s.length();

      while (i < len) {
         char c = s.charAt(i);
         if (dontNeedEncoding(c)) {
            this.uriBuilder.append(c);
            i++;
         } else {
            int index = 0;
            if (buf == null) {
               buf = new char[s.length() - i];
            }

            do {
               buf[index] = c;
               index++;
               i++;
            } while (i < s.length() && !dontNeedEncoding(c = s.charAt(i)));

            byte[] bytes = new String(buf, 0, index).getBytes(this.charset);

            for (byte b : bytes) {
               this.appendEncoded(b);
            }
         }
      }
   }

   private void encodeUtf8Component(CharSequence s) {
      int i = 0;

      for (int len = s.length(); i < len; i++) {
         char c = s.charAt(i);
         if (!dontNeedEncoding(c)) {
            this.encodeUtf8Component(s, i, len);
            return;
         }
      }

      this.uriBuilder.append(s);
   }

   private void encodeUtf8Component(CharSequence s, int encodingStart, int len) {
      if (encodingStart > 0) {
         this.uriBuilder.append(s, 0, encodingStart);
      }

      this.encodeUtf8ComponentSlow(s, encodingStart, len);
   }

   private void encodeUtf8ComponentSlow(CharSequence s, int start, int len) {
      for (int i = start; i < len; i++) {
         char c = s.charAt(i);
         if (c < 128) {
            if (dontNeedEncoding(c)) {
               this.uriBuilder.append(c);
            } else {
               this.appendEncoded(c);
            }
         } else if (c < 2048) {
            this.appendEncoded(192 | c >> 6);
            this.appendEncoded(128 | c & '?');
         } else if (StringUtil.isSurrogate(c)) {
            if (!Character.isHighSurrogate(c)) {
               this.appendEncoded(63);
            } else {
               if (++i == s.length()) {
                  this.appendEncoded(63);
                  break;
               }

               this.writeUtf8Surrogate(c, s.charAt(i));
            }
         } else {
            this.appendEncoded(224 | c >> '\f');
            this.appendEncoded(128 | c >> 6 & 63);
            this.appendEncoded(128 | c & '?');
         }
      }
   }

   private void writeUtf8Surrogate(char c, char c2) {
      if (!Character.isLowSurrogate(c2)) {
         this.appendEncoded(63);
         this.appendEncoded(Character.isHighSurrogate(c2) ? 63 : c2);
      } else {
         int codePoint = Character.toCodePoint(c, c2);
         this.appendEncoded(240 | codePoint >> 18);
         this.appendEncoded(128 | codePoint >> 12 & 63);
         this.appendEncoded(128 | codePoint >> 6 & 63);
         this.appendEncoded(128 | codePoint & 63);
      }
   }

   private void appendEncoded(int b) {
      this.uriBuilder.append('%').append(forDigit(b >> 4)).append(forDigit(b));
   }

   private static char forDigit(int digit) {
      return CHAR_MAP[digit & 15];
   }

   private static boolean dontNeedEncoding(char ch) {
      return ch >= 'a' && ch <= 'z' || ch >= 'A' && ch <= 'Z' || ch >= '0' && ch <= '9' || ch == '-' || ch == '_' || ch == '.' || ch == '*' || ch == '~';
   }
}
