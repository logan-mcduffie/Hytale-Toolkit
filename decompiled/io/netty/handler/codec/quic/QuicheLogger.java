package io.netty.handler.codec.quic;

import io.netty.util.internal.logging.InternalLogger;

final class QuicheLogger {
   private final InternalLogger logger;

   QuicheLogger(InternalLogger logger) {
      this.logger = logger;
   }

   void log(String msg) {
      this.logger.trace(msg);
   }
}
