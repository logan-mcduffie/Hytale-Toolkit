package io.netty.handler.ssl;

import io.netty.buffer.ByteBufAllocator;
import java.util.List;
import javax.net.ssl.SNIServerName;

public final class OpenSslEngine extends ReferenceCountedOpenSslEngine {
   OpenSslEngine(
      OpenSslContext context,
      ByteBufAllocator alloc,
      String peerHost,
      int peerPort,
      boolean jdkCompatibilityMode,
      String endpointIdentificationAlgorithm,
      List<SNIServerName> serverNames
   ) {
      super(context, alloc, peerHost, peerPort, jdkCompatibilityMode, false, endpointIdentificationAlgorithm, serverNames);
   }

   @Override
   protected void finalize() throws Throwable {
      super.finalize();
      OpenSsl.releaseIfNeeded(this);
   }
}
