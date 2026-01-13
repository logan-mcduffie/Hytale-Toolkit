package io.netty.handler.ssl;

public final class SslProtocols {
   @Deprecated
   public static final String SSL_v2_HELLO = "SSLv2Hello";
   @Deprecated
   public static final String SSL_v2 = "SSLv2";
   @Deprecated
   public static final String SSL_v3 = "SSLv3";
   @Deprecated
   public static final String TLS_v1 = "TLSv1";
   @Deprecated
   public static final String TLS_v1_1 = "TLSv1.1";
   public static final String TLS_v1_2 = "TLSv1.2";
   public static final String TLS_v1_3 = "TLSv1.3";

   private SslProtocols() {
   }
}
