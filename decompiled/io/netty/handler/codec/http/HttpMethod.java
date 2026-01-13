package io.netty.handler.codec.http;

import io.netty.util.AsciiString;
import io.netty.util.internal.ObjectUtil;

public class HttpMethod implements Comparable<HttpMethod> {
   private static final String GET_STRING = "GET";
   private static final String POST_STRING = "POST";
   public static final HttpMethod OPTIONS = new HttpMethod("OPTIONS");
   public static final HttpMethod GET = new HttpMethod("GET");
   public static final HttpMethod HEAD = new HttpMethod("HEAD");
   public static final HttpMethod POST = new HttpMethod("POST");
   public static final HttpMethod PUT = new HttpMethod("PUT");
   public static final HttpMethod PATCH = new HttpMethod("PATCH");
   public static final HttpMethod DELETE = new HttpMethod("DELETE");
   public static final HttpMethod TRACE = new HttpMethod("TRACE");
   public static final HttpMethod CONNECT = new HttpMethod("CONNECT");
   private final AsciiString name;

   public static HttpMethod valueOf(String name) {
      switch (name) {
         case "OPTIONS":
            return OPTIONS;
         case "GET":
            return GET;
         case "HEAD":
            return HEAD;
         case "POST":
            return POST;
         case "PUT":
            return PUT;
         case "PATCH":
            return PATCH;
         case "DELETE":
            return DELETE;
         case "TRACE":
            return TRACE;
         case "CONNECT":
            return CONNECT;
         default:
            return new HttpMethod(name);
      }
   }

   public HttpMethod(String name) {
      name = ObjectUtil.checkNonEmptyAfterTrim(name, "name");
      int index = HttpUtil.validateToken(name);
      if (index != -1) {
         throw new IllegalArgumentException("Illegal character in HTTP Method: 0x" + Integer.toHexString(name.charAt(index)));
      } else {
         this.name = AsciiString.cached(name);
      }
   }

   public String name() {
      return this.name.toString();
   }

   public AsciiString asciiName() {
      return this.name;
   }

   @Override
   public int hashCode() {
      return this.name().hashCode();
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (!(o instanceof HttpMethod)) {
         return false;
      } else {
         HttpMethod that = (HttpMethod)o;
         return this.name().equals(that.name());
      }
   }

   @Override
   public String toString() {
      return this.name.toString();
   }

   public int compareTo(HttpMethod o) {
      return o == this ? 0 : this.name().compareTo(o.name());
   }
}
