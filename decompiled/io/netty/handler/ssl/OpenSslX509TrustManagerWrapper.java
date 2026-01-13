package io.netty.handler.ssl;

import io.netty.util.internal.EmptyArrays;
import io.netty.util.internal.PlatformDependent;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.lang.reflect.Field;
import java.security.AccessController;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivilegedAction;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509ExtendedTrustManager;
import javax.net.ssl.X509TrustManager;

final class OpenSslX509TrustManagerWrapper {
   private static final InternalLogger LOGGER = InternalLoggerFactory.getInstance(OpenSslX509TrustManagerWrapper.class);
   private static final OpenSslX509TrustManagerWrapper.TrustManagerWrapper WRAPPER;
   private static final OpenSslX509TrustManagerWrapper.TrustManagerWrapper DEFAULT = new OpenSslX509TrustManagerWrapper.TrustManagerWrapper() {
      @Override
      public X509TrustManager wrapIfNeeded(X509TrustManager manager) {
         return manager;
      }
   };

   static boolean isWrappingSupported() {
      return WRAPPER != DEFAULT;
   }

   private OpenSslX509TrustManagerWrapper() {
   }

   static X509TrustManager wrapIfNeeded(X509TrustManager trustManager) {
      return WRAPPER.wrapIfNeeded(trustManager);
   }

   private static SSLContext newSSLContext() throws NoSuchAlgorithmException, NoSuchProviderException {
      return SSLContext.getInstance("TLS", "SunJSSE");
   }

   static {
      OpenSslX509TrustManagerWrapper.TrustManagerWrapper wrapper = DEFAULT;
      Throwable cause = null;
      Throwable unsafeCause = PlatformDependent.getUnsafeUnavailabilityCause();
      if (unsafeCause == null) {
         SSLContext context;
         try {
            context = newSSLContext();
            context.init(null, new TrustManager[]{new X509TrustManager() {
               @Override
               public void checkClientTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
                  throw new CertificateException();
               }

               @Override
               public void checkServerTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
                  throw new CertificateException();
               }

               @Override
               public X509Certificate[] getAcceptedIssuers() {
                  return EmptyArrays.EMPTY_X509_CERTIFICATES;
               }
            }}, null);
         } catch (Throwable var6) {
            context = null;
            cause = var6;
         }

         if (cause != null) {
            LOGGER.debug("Unable to access wrapped TrustManager", cause);
         } else {
            final SSLContext finalContext = context;
            Object maybeWrapper = AccessController.doPrivileged(new PrivilegedAction<Object>() {
               @Override
               public Object run() {
                  try {
                     Field contextSpiField = SSLContext.class.getDeclaredField("contextSpi");
                     long spiOffset = PlatformDependent.objectFieldOffset(contextSpiField);
                     Object spi = PlatformDependent.getObject(finalContext, spiOffset);
                     if (spi != null) {
                        Class<?> clazz = spi.getClass();

                        do {
                           try {
                              Field trustManagerField = clazz.getDeclaredField("trustManager");
                              long tmOffset = PlatformDependent.objectFieldOffset(trustManagerField);
                              Object trustManager = PlatformDependent.getObject(spi, tmOffset);
                              if (trustManager instanceof X509ExtendedTrustManager) {
                                 return new OpenSslX509TrustManagerWrapper.UnsafeTrustManagerWrapper(spiOffset, tmOffset);
                              }
                           } catch (NoSuchFieldException var10) {
                           }

                           clazz = clazz.getSuperclass();
                        } while (clazz != null);
                     }

                     throw new NoSuchFieldException();
                  } catch (SecurityException | NoSuchFieldException var11) {
                     return var11;
                  }
               }
            });
            if (maybeWrapper instanceof Throwable) {
               LOGGER.debug("Unable to access wrapped TrustManager", (Throwable)maybeWrapper);
            } else {
               wrapper = (OpenSslX509TrustManagerWrapper.TrustManagerWrapper)maybeWrapper;
            }
         }
      } else {
         LOGGER.debug("Unable to access wrapped TrustManager", cause);
      }

      WRAPPER = wrapper;
   }

   private interface TrustManagerWrapper {
      X509TrustManager wrapIfNeeded(X509TrustManager var1);
   }

   private static final class UnsafeTrustManagerWrapper implements OpenSslX509TrustManagerWrapper.TrustManagerWrapper {
      private final long spiOffset;
      private final long tmOffset;

      UnsafeTrustManagerWrapper(long spiOffset, long tmOffset) {
         this.spiOffset = spiOffset;
         this.tmOffset = tmOffset;
      }

      @Override
      public X509TrustManager wrapIfNeeded(X509TrustManager manager) {
         if (!(manager instanceof X509ExtendedTrustManager)) {
            try {
               SSLContext ctx = OpenSslX509TrustManagerWrapper.newSSLContext();
               ctx.init(null, new TrustManager[]{manager}, null);
               Object spi = PlatformDependent.getObject(ctx, this.spiOffset);
               if (spi != null) {
                  Object tm = PlatformDependent.getObject(spi, this.tmOffset);
                  if (tm instanceof X509ExtendedTrustManager) {
                     return (X509TrustManager)tm;
                  }
               }
            } catch (NoSuchProviderException | KeyManagementException | NoSuchAlgorithmException var5) {
               PlatformDependent.throwException(var5);
            }
         }

         return manager;
      }
   }
}
