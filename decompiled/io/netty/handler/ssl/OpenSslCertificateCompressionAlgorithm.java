package io.netty.handler.ssl;

import javax.net.ssl.SSLEngine;

public interface OpenSslCertificateCompressionAlgorithm {
   byte[] compress(SSLEngine var1, byte[] var2) throws Exception;

   byte[] decompress(SSLEngine var1, int var2, byte[] var3) throws Exception;

   int algorithmId();
}
