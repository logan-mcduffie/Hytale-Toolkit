package io.netty.handler.codec.quic;

import io.netty.handler.ssl.SslContextOption;
import java.util.Map;
import java.util.Set;

public final class BoringSSLContextOption<T> extends SslContextOption<T> {
   public static final BoringSSLContextOption<String[]> GROUPS = new BoringSSLContextOption<>("GROUPS");
   public static final BoringSSLContextOption<String[]> SIGNATURE_ALGORITHMS = new BoringSSLContextOption<>("SIGNATURE_ALGORITHMS");
   public static final BoringSSLContextOption<Set<String>> CLIENT_KEY_TYPES = new BoringSSLContextOption<>("CLIENT_KEY_TYPES");
   public static final BoringSSLContextOption<Map<String, String>> SERVER_KEY_TYPES = new BoringSSLContextOption<>("SERVER_KEY_TYPES");

   private BoringSSLContextOption(String name) {
      super(name);
   }
}
