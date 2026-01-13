package io.netty.handler.ssl;

import java.security.cert.Certificate;
import java.util.Map;
import javax.net.ssl.SSLException;

interface OpenSslInternalSession extends OpenSslSession {
   void prepareHandshake();

   OpenSslSessionId sessionId();

   void setLocalCertificate(Certificate[] var1);

   void setSessionDetails(long var1, long var3, OpenSslSessionId var5, Map<String, Object> var6);

   Map<String, Object> keyValueStorage();

   void setLastAccessedTime(long var1);

   void tryExpandApplicationBufferSize(int var1);

   void handshakeFinished(byte[] var1, String var2, String var3, byte[] var4, byte[][] var5, long var6, long var8) throws SSLException;
}
