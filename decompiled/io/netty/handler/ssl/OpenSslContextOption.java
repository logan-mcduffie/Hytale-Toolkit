package io.netty.handler.ssl;

public final class OpenSslContextOption<T> extends SslContextOption<T> {
   public static final OpenSslContextOption<Boolean> USE_TASKS = new OpenSslContextOption<>("USE_TASKS");
   public static final OpenSslContextOption<Boolean> TLS_FALSE_START = new OpenSslContextOption<>("TLS_FALSE_START");
   public static final OpenSslContextOption<OpenSslPrivateKeyMethod> PRIVATE_KEY_METHOD = new OpenSslContextOption<>("PRIVATE_KEY_METHOD");
   public static final OpenSslContextOption<OpenSslAsyncPrivateKeyMethod> ASYNC_PRIVATE_KEY_METHOD = new OpenSslContextOption<>("ASYNC_PRIVATE_KEY_METHOD");
   public static final OpenSslContextOption<OpenSslCertificateCompressionConfig> CERTIFICATE_COMPRESSION_ALGORITHMS = new OpenSslContextOption<>(
      "CERTIFICATE_COMPRESSION_ALGORITHMS"
   );
   public static final OpenSslContextOption<Integer> MAX_CERTIFICATE_LIST_BYTES = new OpenSslContextOption<>("MAX_CERTIFICATE_LIST_BYTES");
   public static final OpenSslContextOption<String[]> GROUPS = new OpenSslContextOption<>("GROUPS");
   public static final OpenSslContextOption<Integer> TMP_DH_KEYLENGTH = new OpenSslContextOption<>("TMP_DH_KEYLENGTH");
   public static final OpenSslContextOption<Boolean> USE_JDK_PROVIDER_SIGNATURES = new OpenSslContextOption<>("USE_JDK_PROVIDER_SIGNATURES");

   private OpenSslContextOption(String name) {
      super(name);
   }
}
