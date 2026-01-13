package io.netty.handler.ssl;

import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.X509TrustManager;

public interface ResumableX509ExtendedTrustManager extends X509TrustManager {
   void resumeClientTrusted(X509Certificate[] var1, SSLEngine var2) throws CertificateException;

   void resumeServerTrusted(X509Certificate[] var1, SSLEngine var2) throws CertificateException;
}
