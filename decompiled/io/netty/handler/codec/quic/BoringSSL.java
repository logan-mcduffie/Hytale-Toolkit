package io.netty.handler.codec.quic;

import io.netty.handler.ssl.util.LazyX509Certificate;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.cert.X509Certificate;
import org.jetbrains.annotations.Nullable;

final class BoringSSL {
   static final int SSL_VERIFY_NONE = BoringSSLNativeStaticallyReferencedJniMethods.ssl_verify_none();
   static final int SSL_VERIFY_FAIL_IF_NO_PEER_CERT = BoringSSLNativeStaticallyReferencedJniMethods.ssl_verify_fail_if_no_peer_cert();
   static final int SSL_VERIFY_PEER = BoringSSLNativeStaticallyReferencedJniMethods.ssl_verify_peer();
   static final int X509_V_OK = BoringSSLNativeStaticallyReferencedJniMethods.x509_v_ok();
   static final int X509_V_ERR_CERT_HAS_EXPIRED = BoringSSLNativeStaticallyReferencedJniMethods.x509_v_err_cert_has_expired();
   static final int X509_V_ERR_CERT_NOT_YET_VALID = BoringSSLNativeStaticallyReferencedJniMethods.x509_v_err_cert_not_yet_valid();
   static final int X509_V_ERR_CERT_REVOKED = BoringSSLNativeStaticallyReferencedJniMethods.x509_v_err_cert_revoked();
   static final int X509_V_ERR_UNSPECIFIED = BoringSSLNativeStaticallyReferencedJniMethods.x509_v_err_unspecified();

   static long SSLContext_new(
      boolean server,
      String[] applicationProtocols,
      BoringSSLHandshakeCompleteCallback handshakeCompleteCallback,
      BoringSSLCertificateCallback certificateCallback,
      BoringSSLCertificateVerifyCallback verifyCallback,
      @Nullable BoringSSLTlsextServernameCallback servernameCallback,
      @Nullable BoringSSLKeylogCallback keylogCallback,
      @Nullable BoringSSLSessionCallback sessionCallback,
      @Nullable BoringSSLPrivateKeyMethod privateKeyMethod,
      BoringSSLSessionTicketCallback sessionTicketCallback,
      int verifyMode,
      byte[][] subjectNames
   ) {
      return SSLContext_new0(
         server,
         toWireFormat(applicationProtocols),
         handshakeCompleteCallback,
         certificateCallback,
         verifyCallback,
         servernameCallback,
         keylogCallback,
         sessionCallback,
         privateKeyMethod,
         sessionTicketCallback,
         verifyMode,
         subjectNames
      );
   }

   private static byte @Nullable [] toWireFormat(String @Nullable [] applicationProtocols) {
      if (applicationProtocols == null) {
         return null;
      } else {
         try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();

            byte[] var10;
            try {
               for (String p : applicationProtocols) {
                  byte[] bytes = p.getBytes(StandardCharsets.US_ASCII);
                  out.write(bytes.length);
                  out.write(bytes);
               }

               var10 = out.toByteArray();
            } catch (Throwable var8) {
               try {
                  out.close();
               } catch (Throwable var7) {
                  var8.addSuppressed(var7);
               }

               throw var8;
            }

            out.close();
            return var10;
         } catch (IOException var9) {
            throw new IllegalStateException(var9);
         }
      }
   }

   static native long SSLContext_new();

   private static native long SSLContext_new0(
      boolean var0,
      byte @Nullable [] var1,
      Object var2,
      Object var3,
      Object var4,
      @Nullable Object var5,
      @Nullable Object var6,
      @Nullable Object var7,
      @Nullable Object var8,
      Object var9,
      int var10,
      byte[][] var11
   );

   static native void SSLContext_set_early_data_enabled(long var0, boolean var2);

   static native long SSLContext_setSessionCacheSize(long var0, long var2);

   static native long SSLContext_setSessionCacheTimeout(long var0, long var2);

   static native void SSLContext_setSessionTicketKeys(long var0, boolean var2);

   static int SSLContext_set1_groups_list(long ctx, String... groups) {
      if (groups == null) {
         throw new NullPointerException("curves");
      } else if (groups.length == 0) {
         throw new IllegalArgumentException();
      } else {
         StringBuilder sb = new StringBuilder();

         for (String group : groups) {
            sb.append(group);
            sb.append(':');
         }

         sb.setLength(sb.length() - 1);
         return SSLContext_set1_groups_list(ctx, sb.toString());
      }
   }

   static int SSLContext_set1_sigalgs_list(long ctx, String... sigalgs) {
      if (sigalgs.length == 0) {
         throw new IllegalArgumentException();
      } else {
         StringBuilder sb = new StringBuilder();

         for (String sigalg : sigalgs) {
            sb.append(sigalg);
            sb.append(':');
         }

         sb.setLength(sb.length() - 1);
         return SSLContext_set1_sigalgs_list(ctx, sb.toString());
      }
   }

   private static native int SSLContext_set1_sigalgs_list(long var0, String var2);

   private static native int SSLContext_set1_groups_list(long var0, String var2);

   static native void SSLContext_free(long var0);

   static long SSL_new(long context, boolean server, String hostname) {
      return SSL_new0(context, server, tlsExtHostName(hostname));
   }

   static native long SSL_new0(long var0, boolean var2, @Nullable String var3);

   static native void SSL_free(long var0);

   static native Runnable SSL_getTask(long var0);

   static native void SSL_cleanup(long var0);

   static native long EVP_PKEY_parse(byte[] var0, String var1);

   static native void EVP_PKEY_free(long var0);

   static native long CRYPTO_BUFFER_stack_new(long var0, byte[][] var2);

   static native void CRYPTO_BUFFER_stack_free(long var0);

   @Nullable
   static native String ERR_last_error();

   @Nullable
   private static String tlsExtHostName(@Nullable String hostname) {
      if (hostname != null && hostname.endsWith(".")) {
         hostname = hostname.substring(0, hostname.length() - 1);
      }

      return hostname;
   }

   static X509Certificate[] certificates(byte[][] chain) {
      X509Certificate[] peerCerts = new X509Certificate[chain.length];

      for (int i = 0; i < peerCerts.length; i++) {
         peerCerts[i] = new LazyX509Certificate(chain[i]);
      }

      return peerCerts;
   }

   static byte[][] subjectNames(X509Certificate[] certificates) {
      byte[][] subjectNames = new byte[certificates.length][];

      for (int i = 0; i < certificates.length; i++) {
         subjectNames[i] = certificates[i].getSubjectX500Principal().getEncoded();
      }

      return subjectNames;
   }

   private BoringSSL() {
   }
}
