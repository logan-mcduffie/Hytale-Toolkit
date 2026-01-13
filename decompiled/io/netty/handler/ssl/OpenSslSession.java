package io.netty.handler.ssl;

import javax.net.ssl.SSLSession;

public interface OpenSslSession extends SSLSession {
   boolean hasPeerCertificates();

   OpenSslSessionContext getSessionContext();
}
