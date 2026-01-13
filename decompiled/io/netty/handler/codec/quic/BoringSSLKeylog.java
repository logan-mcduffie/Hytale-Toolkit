package io.netty.handler.codec.quic;

import javax.net.ssl.SSLEngine;

public interface BoringSSLKeylog {
   void logKey(SSLEngine var1, String var2);
}
