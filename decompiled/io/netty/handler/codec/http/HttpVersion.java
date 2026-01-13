package io.netty.handler.codec.http;

import io.netty.buffer.ByteBuf;
import io.netty.util.CharsetUtil;
import io.netty.util.internal.ObjectUtil;

public class HttpVersion implements Comparable<HttpVersion> {
   static final String HTTP_1_0_STRING = "HTTP/1.0";
   static final String HTTP_1_1_STRING = "HTTP/1.1";
   public static final HttpVersion HTTP_1_0 = new HttpVersion("HTTP", 1, 0, false, true);
   public static final HttpVersion HTTP_1_1 = new HttpVersion("HTTP", 1, 1, true, true);
   private final String protocolName;
   private final int majorVersion;
   private final int minorVersion;
   private final String text;
   private final boolean keepAliveDefault;
   private final byte[] bytes;

   public static HttpVersion valueOf(String text) {
      return valueOf(text, false);
   }

   static HttpVersion valueOf(String text, boolean strict) {
      ObjectUtil.checkNotNull(text, "text");
      if (text == "HTTP/1.1") {
         return HTTP_1_1;
      } else if (text == "HTTP/1.0") {
         return HTTP_1_0;
      } else {
         text = text.trim();
         if (text.isEmpty()) {
            throw new IllegalArgumentException("text is empty (possibly HTTP/0.9)");
         } else {
            HttpVersion version = version0(text);
            if (version == null) {
               version = new HttpVersion(text, strict, true);
            }

            return version;
         }
      }
   }

   private static HttpVersion version0(String text) {
      if ("HTTP/1.1".equals(text)) {
         return HTTP_1_1;
      } else {
         return "HTTP/1.0".equals(text) ? HTTP_1_0 : null;
      }
   }

   public HttpVersion(String text, boolean keepAliveDefault) {
      this(text, false, keepAliveDefault);
   }

   HttpVersion(String text, boolean strict, boolean keepAliveDefault) {
      text = ObjectUtil.checkNonEmptyAfterTrim(text, "text").toUpperCase();
      if (strict) {
         if (text.length() != 8 || !text.startsWith("HTTP/") || text.charAt(6) != '.') {
            throw new IllegalArgumentException("invalid version format: " + text);
         }

         this.protocolName = "HTTP";
         this.majorVersion = toDecimal(text.charAt(5));
         this.minorVersion = toDecimal(text.charAt(7));
      } else {
         int slashIndex = text.indexOf(47);
         int dotIndex = text.indexOf(46, slashIndex + 1);
         if (slashIndex <= 0 || dotIndex <= slashIndex + 1 || dotIndex >= text.length() - 1 || hasWhitespace(text, slashIndex)) {
            throw new IllegalArgumentException("invalid version format: " + text);
         }

         this.protocolName = text.substring(0, slashIndex);
         this.majorVersion = parseInt(text, slashIndex + 1, dotIndex);
         this.minorVersion = parseInt(text, dotIndex + 1, text.length());
      }

      this.text = this.protocolName + '/' + this.majorVersion + '.' + this.minorVersion;
      this.keepAliveDefault = keepAliveDefault;
      this.bytes = null;
   }

   private static boolean hasWhitespace(String s, int end) {
      for (int i = 0; i < end; i++) {
         if (Character.isWhitespace(s.charAt(i))) {
            return true;
         }
      }

      return false;
   }

   private static int parseInt(String text, int start, int end) {
      int result = 0;

      for (int i = start; i < end; i++) {
         char ch = text.charAt(i);
         result = result * 10 + toDecimal(ch);
      }

      return result;
   }

   private static int toDecimal(int value) {
      if (value >= 48 && value <= 57) {
         return value - 48;
      } else {
         throw new IllegalArgumentException(
            "Invalid version number, only 0-9 (0x30-0x39) allowed, but received a '" + (char)value + "' (0x" + Integer.toHexString(value) + ")"
         );
      }
   }

   public HttpVersion(String protocolName, int majorVersion, int minorVersion, boolean keepAliveDefault) {
      this(protocolName, majorVersion, minorVersion, keepAliveDefault, false);
   }

   private HttpVersion(String protocolName, int majorVersion, int minorVersion, boolean keepAliveDefault, boolean bytes) {
      protocolName = ObjectUtil.checkNonEmptyAfterTrim(protocolName, "protocolName").toUpperCase();

      for (int i = 0; i < protocolName.length(); i++) {
         if (Character.isISOControl(protocolName.charAt(i)) || Character.isWhitespace(protocolName.charAt(i))) {
            throw new IllegalArgumentException("invalid character in protocolName");
         }
      }

      ObjectUtil.checkPositiveOrZero(majorVersion, "majorVersion");
      ObjectUtil.checkPositiveOrZero(minorVersion, "minorVersion");
      this.protocolName = protocolName;
      this.majorVersion = majorVersion;
      this.minorVersion = minorVersion;
      this.text = protocolName + '/' + majorVersion + '.' + minorVersion;
      this.keepAliveDefault = keepAliveDefault;
      if (bytes) {
         this.bytes = this.text.getBytes(CharsetUtil.US_ASCII);
      } else {
         this.bytes = null;
      }
   }

   public String protocolName() {
      return this.protocolName;
   }

   public int majorVersion() {
      return this.majorVersion;
   }

   public int minorVersion() {
      return this.minorVersion;
   }

   public String text() {
      return this.text;
   }

   public boolean isKeepAliveDefault() {
      return this.keepAliveDefault;
   }

   @Override
   public String toString() {
      return this.text();
   }

   @Override
   public int hashCode() {
      return (this.protocolName().hashCode() * 31 + this.majorVersion()) * 31 + this.minorVersion();
   }

   @Override
   public boolean equals(Object o) {
      if (!(o instanceof HttpVersion)) {
         return false;
      } else {
         HttpVersion that = (HttpVersion)o;
         return this.minorVersion() == that.minorVersion() && this.majorVersion() == that.majorVersion() && this.protocolName().equals(that.protocolName());
      }
   }

   public int compareTo(HttpVersion o) {
      int v = this.protocolName().compareTo(o.protocolName());
      if (v != 0) {
         return v;
      } else {
         v = this.majorVersion() - o.majorVersion();
         return v != 0 ? v : this.minorVersion() - o.minorVersion();
      }
   }

   void encode(ByteBuf buf) {
      if (this.bytes == null) {
         buf.writeCharSequence(this.text, CharsetUtil.US_ASCII);
      } else {
         buf.writeBytes(this.bytes);
      }
   }
}
