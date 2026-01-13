package io.netty.handler.codec.quic;

import io.netty.handler.ssl.OpenSslCertificateException;
import java.security.cert.CertPathValidatorException;
import java.security.cert.CertificateExpiredException;
import java.security.cert.CertificateNotYetValidException;
import java.security.cert.CertificateRevokedException;
import java.security.cert.X509Certificate;
import java.security.cert.CertPathValidatorException.BasicReason;
import java.security.cert.CertPathValidatorException.Reason;
import javax.net.ssl.X509ExtendedTrustManager;
import javax.net.ssl.X509TrustManager;
import org.jetbrains.annotations.Nullable;

final class BoringSSLCertificateVerifyCallback {
   private static final boolean TRY_USING_EXTENDED_TRUST_MANAGER;
   private final QuicheQuicSslEngineMap engineMap;
   private final X509TrustManager manager;

   BoringSSLCertificateVerifyCallback(QuicheQuicSslEngineMap engineMap, @Nullable X509TrustManager manager) {
      this.engineMap = engineMap;
      this.manager = manager;
   }

   int verify(long ssl, byte[][] x509, String authAlgorithm) {
      QuicheQuicSslEngine engine = this.engineMap.get(ssl);
      if (engine == null) {
         return BoringSSL.X509_V_ERR_UNSPECIFIED;
      } else if (this.manager == null) {
         this.engineMap.remove(ssl);
         return BoringSSL.X509_V_ERR_UNSPECIFIED;
      } else {
         X509Certificate[] peerCerts = BoringSSL.certificates(x509);

         try {
            if (engine.getUseClientMode()) {
               if (TRY_USING_EXTENDED_TRUST_MANAGER && this.manager instanceof X509ExtendedTrustManager) {
                  ((X509ExtendedTrustManager)this.manager).checkServerTrusted(peerCerts, authAlgorithm, engine);
               } else {
                  this.manager.checkServerTrusted(peerCerts, authAlgorithm);
               }
            } else if (TRY_USING_EXTENDED_TRUST_MANAGER && this.manager instanceof X509ExtendedTrustManager) {
               ((X509ExtendedTrustManager)this.manager).checkClientTrusted(peerCerts, authAlgorithm, engine);
            } else {
               this.manager.checkClientTrusted(peerCerts, authAlgorithm);
            }

            return BoringSSL.X509_V_OK;
         } catch (Throwable var8) {
            this.engineMap.remove(ssl);
            if (var8 instanceof OpenSslCertificateException) {
               return ((OpenSslCertificateException)var8).errorCode();
            } else if (var8 instanceof CertificateExpiredException) {
               return BoringSSL.X509_V_ERR_CERT_HAS_EXPIRED;
            } else {
               return var8 instanceof CertificateNotYetValidException ? BoringSSL.X509_V_ERR_CERT_NOT_YET_VALID : translateToError(var8);
            }
         }
      }
   }

   private static int translateToError(Throwable cause) {
      if (cause instanceof CertificateRevokedException) {
         return BoringSSL.X509_V_ERR_CERT_REVOKED;
      } else {
         for (Throwable wrapped = cause.getCause(); wrapped != null; wrapped = wrapped.getCause()) {
            if (wrapped instanceof CertPathValidatorException) {
               CertPathValidatorException ex = (CertPathValidatorException)wrapped;
               Reason reason = ex.getReason();
               if (reason == BasicReason.EXPIRED) {
                  return BoringSSL.X509_V_ERR_CERT_HAS_EXPIRED;
               }

               if (reason == BasicReason.NOT_YET_VALID) {
                  return BoringSSL.X509_V_ERR_CERT_NOT_YET_VALID;
               }

               if (reason == BasicReason.REVOKED) {
                  return BoringSSL.X509_V_ERR_CERT_REVOKED;
               }
            }
         }

         return BoringSSL.X509_V_ERR_UNSPECIFIED;
      }
   }

   static {
      boolean tryUsingExtendedTrustManager;
      try {
         Class.forName(X509ExtendedTrustManager.class.getName());
         tryUsingExtendedTrustManager = true;
      } catch (Throwable var2) {
         tryUsingExtendedTrustManager = false;
      }

      TRY_USING_EXTENDED_TRUST_MANAGER = tryUsingExtendedTrustManager;
   }
}
