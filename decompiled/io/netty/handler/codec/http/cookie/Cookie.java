package io.netty.handler.codec.http.cookie;

public interface Cookie extends Comparable<Cookie> {
   long UNDEFINED_MAX_AGE = Long.MIN_VALUE;

   String name();

   String value();

   void setValue(String var1);

   boolean wrap();

   void setWrap(boolean var1);

   String domain();

   void setDomain(String var1);

   String path();

   void setPath(String var1);

   long maxAge();

   void setMaxAge(long var1);

   boolean isSecure();

   void setSecure(boolean var1);

   boolean isHttpOnly();

   void setHttpOnly(boolean var1);
}
