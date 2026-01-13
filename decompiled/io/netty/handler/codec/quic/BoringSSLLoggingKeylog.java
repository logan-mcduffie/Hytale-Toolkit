package io.netty.handler.codec.quic;

import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import javax.net.ssl.SSLEngine;

final class BoringSSLLoggingKeylog implements BoringSSLKeylog {
   static final BoringSSLLoggingKeylog INSTANCE = new BoringSSLLoggingKeylog();
   private static final InternalLogger logger = InternalLoggerFactory.getInstance(BoringSSLLoggingKeylog.class);

   private BoringSSLLoggingKeylog() {
   }

   @Override
   public void logKey(SSLEngine engine, String key) {
      logger.debug(key);
   }
}
