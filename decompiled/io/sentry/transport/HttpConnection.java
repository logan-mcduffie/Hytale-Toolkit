package io.sentry.transport;

import io.sentry.RequestDetails;
import io.sentry.SentryEnvelope;
import io.sentry.SentryLevel;
import io.sentry.SentryOptions;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.Proxy.Type;
import java.nio.charset.Charset;
import java.util.Map.Entry;
import java.util.zip.GZIPOutputStream;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSocketFactory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.TestOnly;

final class HttpConnection {
   private static final Charset UTF_8 = Charset.forName("UTF-8");
   @Nullable
   private final Proxy proxy;
   @NotNull
   private final RequestDetails requestDetails;
   @NotNull
   private final SentryOptions options;
   @NotNull
   private final RateLimiter rateLimiter;

   public HttpConnection(@NotNull SentryOptions options, @NotNull RequestDetails requestDetails, @NotNull RateLimiter rateLimiter) {
      this(options, requestDetails, AuthenticatorWrapper.getInstance(), rateLimiter);
   }

   HttpConnection(
      @NotNull SentryOptions options,
      @NotNull RequestDetails requestDetails,
      @NotNull AuthenticatorWrapper authenticatorWrapper,
      @NotNull RateLimiter rateLimiter
   ) {
      this.requestDetails = requestDetails;
      this.options = options;
      this.rateLimiter = rateLimiter;
      this.proxy = this.resolveProxy(options.getProxy());
      if (this.proxy != null && options.getProxy() != null) {
         String proxyUser = options.getProxy().getUser();
         String proxyPassword = options.getProxy().getPass();
         if (proxyUser != null && proxyPassword != null) {
            authenticatorWrapper.setDefault(new ProxyAuthenticator(proxyUser, proxyPassword));
         }
      }
   }

   @Nullable
   private Proxy resolveProxy(@Nullable SentryOptions.Proxy optionsProxy) {
      Proxy proxy = null;
      if (optionsProxy != null) {
         String port = optionsProxy.getPort();
         String host = optionsProxy.getHost();
         if (port != null && host != null) {
            try {
               Type type;
               if (optionsProxy.getType() != null) {
                  type = optionsProxy.getType();
               } else {
                  type = Type.HTTP;
               }

               InetSocketAddress proxyAddr = new InetSocketAddress(host, Integer.parseInt(port));
               proxy = new Proxy(type, proxyAddr);
            } catch (NumberFormatException var7) {
               this.options.getLogger().log(SentryLevel.ERROR, var7, "Failed to parse Sentry Proxy port: " + optionsProxy.getPort() + ". Proxy is ignored");
            }
         }
      }

      return proxy;
   }

   @NotNull
   HttpURLConnection open() throws IOException {
      return (HttpURLConnection)(this.proxy == null ? this.requestDetails.getUrl().openConnection() : this.requestDetails.getUrl().openConnection(this.proxy));
   }

   @NotNull
   private HttpURLConnection createConnection() throws IOException {
      HttpURLConnection connection = this.open();

      for (Entry<String, String> header : this.requestDetails.getHeaders().entrySet()) {
         connection.setRequestProperty(header.getKey(), header.getValue());
      }

      connection.setRequestMethod("POST");
      connection.setDoOutput(true);
      connection.setRequestProperty("Content-Encoding", "gzip");
      connection.setRequestProperty("Content-Type", "application/x-sentry-envelope");
      connection.setRequestProperty("Accept", "application/json");
      connection.setRequestProperty("Connection", "close");
      connection.setConnectTimeout(this.options.getConnectionTimeoutMillis());
      connection.setReadTimeout(this.options.getReadTimeoutMillis());
      SSLSocketFactory sslSocketFactory = this.options.getSslSocketFactory();
      if (connection instanceof HttpsURLConnection && sslSocketFactory != null) {
         ((HttpsURLConnection)connection).setSSLSocketFactory(sslSocketFactory);
      }

      connection.connect();
      return connection;
   }

   @NotNull
   public TransportResult send(@NotNull SentryEnvelope envelope) throws IOException {
      this.options.getSocketTagger().tagSockets();
      HttpURLConnection connection = this.createConnection();

      try {
         OutputStream outputStream = connection.getOutputStream();

         try {
            GZIPOutputStream gzip = new GZIPOutputStream(outputStream);

            try {
               this.options.getSerializer().serialize(envelope, gzip);
            } catch (Throwable var17) {
               try {
                  gzip.close();
               } catch (Throwable var16) {
                  var17.addSuppressed(var16);
               }

               throw var17;
            }

            gzip.close();
         } catch (Throwable var18) {
            if (outputStream != null) {
               try {
                  outputStream.close();
               } catch (Throwable var15) {
                  var18.addSuppressed(var15);
               }
            }

            throw var18;
         }

         if (outputStream != null) {
            outputStream.close();
         }
      } catch (Throwable var19) {
         this.options.getLogger().log(SentryLevel.ERROR, var19, "An exception occurred while submitting the envelope to the Sentry server.");
      } finally {
         TransportResult result = this.readAndLog(connection);
         this.options.getSocketTagger().untagSockets();
      }

      Object var21;
      return (TransportResult)var21;
   }

   @NotNull
   private TransportResult readAndLog(@NotNull HttpURLConnection connection) {
      TransportResult var9;
      try {
         int responseCode = connection.getResponseCode();
         this.updateRetryAfterLimits(connection, responseCode);
         if (this.isSuccessfulResponseCode(responseCode)) {
            this.options.getLogger().log(SentryLevel.DEBUG, "Envelope sent successfully.");
            return TransportResult.success();
         }

         this.options.getLogger().log(SentryLevel.ERROR, "Request failed, API returned %s", responseCode);
         if (this.options.isDebug()) {
            String errorMessage = this.getErrorMessageFromStream(connection);
            this.options.getLogger().log(SentryLevel.ERROR, "%s", errorMessage);
         }

         var9 = TransportResult.error(responseCode);
      } catch (IOException var7) {
         this.options.getLogger().log(SentryLevel.ERROR, var7, "Error reading and logging the response stream");
         return TransportResult.error();
      } finally {
         this.closeAndDisconnect(connection);
      }

      return var9;
   }

   public void updateRetryAfterLimits(@NotNull HttpURLConnection connection, int responseCode) {
      String retryAfterHeader = connection.getHeaderField("Retry-After");
      String sentryRateLimitHeader = connection.getHeaderField("X-Sentry-Rate-Limits");
      this.rateLimiter.updateRetryAfterLimits(sentryRateLimitHeader, retryAfterHeader, responseCode);
   }

   private void closeAndDisconnect(@NotNull HttpURLConnection connection) {
      try {
         connection.getInputStream().close();
      } catch (IOException var6) {
      } finally {
         connection.disconnect();
      }
   }

   @NotNull
   private String getErrorMessageFromStream(@NotNull HttpURLConnection connection) {
      try {
         InputStream errorStream = connection.getErrorStream();

         String var7;
         try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(errorStream, UTF_8));

            try {
               StringBuilder sb = new StringBuilder();

               String line;
               for (boolean first = true; (line = reader.readLine()) != null; first = false) {
                  if (!first) {
                     sb.append("\n");
                  }

                  sb.append(line);
               }

               var7 = sb.toString();
            } catch (Throwable var10) {
               try {
                  reader.close();
               } catch (Throwable var9) {
                  var10.addSuppressed(var9);
               }

               throw var10;
            }

            reader.close();
         } catch (Throwable var11) {
            if (errorStream != null) {
               try {
                  errorStream.close();
               } catch (Throwable var8) {
                  var11.addSuppressed(var8);
               }
            }

            throw var11;
         }

         if (errorStream != null) {
            errorStream.close();
         }

         return var7;
      } catch (IOException var12) {
         return "Failed to obtain error message while analyzing send failure.";
      }
   }

   private boolean isSuccessfulResponseCode(int responseCode) {
      return responseCode == 200;
   }

   @TestOnly
   @Nullable
   Proxy getProxy() {
      return this.proxy;
   }
}
