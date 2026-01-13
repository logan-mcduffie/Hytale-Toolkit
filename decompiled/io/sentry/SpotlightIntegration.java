package io.sentry;

import io.sentry.util.IntegrationUtils;
import io.sentry.util.Platform;
import java.io.Closeable;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.util.concurrent.RejectedExecutionException;
import java.util.zip.GZIPOutputStream;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.TestOnly;
import org.jetbrains.annotations.ApiStatus.Internal;

@Internal
public final class SpotlightIntegration implements Integration, SentryOptions.BeforeEnvelopeCallback, Closeable {
   @Nullable
   private SentryOptions options;
   @NotNull
   private ILogger logger = NoOpLogger.getInstance();
   @NotNull
   private ISentryExecutorService executorService = NoOpSentryExecutorService.getInstance();

   @Override
   public void register(@NotNull IScopes scopes, @NotNull SentryOptions options) {
      this.options = options;
      this.logger = options.getLogger();
      if (options.getBeforeEnvelopeCallback() == null && options.isEnableSpotlight()) {
         this.executorService = new SentryExecutorService(options);
         options.setBeforeEnvelopeCallback(this);
         this.logger.log(SentryLevel.DEBUG, "SpotlightIntegration enabled.");
         IntegrationUtils.addIntegrationToSdkVersion("Spotlight");
      } else {
         this.logger.log(SentryLevel.DEBUG, "SpotlightIntegration is not enabled. BeforeEnvelopeCallback is already set or spotlight is not enabled.");
      }
   }

   @Override
   public void execute(@NotNull SentryEnvelope envelope, @Nullable Hint hint) {
      try {
         this.executorService.submit(() -> this.sendEnvelope(envelope));
      } catch (RejectedExecutionException var4) {
         this.logger.log(SentryLevel.WARNING, "Spotlight envelope submission rejected.", var4);
      }
   }

   private void sendEnvelope(@NotNull SentryEnvelope envelope) {
      try {
         if (this.options == null) {
            throw new IllegalArgumentException("SentryOptions are required to send envelopes.");
         }

         String spotlightConnectionUrl = this.getSpotlightConnectionUrl();
         HttpURLConnection connection = this.createConnection(spotlightConnectionUrl);

         try {
            OutputStream outputStream = connection.getOutputStream();

            try {
               GZIPOutputStream gzip = new GZIPOutputStream(outputStream);

               try {
                  this.options.getSerializer().serialize(envelope, gzip);
               } catch (Throwable var19) {
                  try {
                     gzip.close();
                  } catch (Throwable var18) {
                     var19.addSuppressed(var18);
                  }

                  throw var19;
               }

               gzip.close();
            } catch (Throwable var20) {
               if (outputStream != null) {
                  try {
                     outputStream.close();
                  } catch (Throwable var17) {
                     var20.addSuppressed(var17);
                  }
               }

               throw var20;
            }

            if (outputStream != null) {
               outputStream.close();
            }
         } catch (Throwable var21) {
            this.logger.log(SentryLevel.ERROR, "An exception occurred while submitting the envelope to the Sentry server.", var21);
         } finally {
            int responseCode = connection.getResponseCode();
            this.logger.log(SentryLevel.DEBUG, "Envelope sent to spotlight: %d", responseCode);
            this.closeAndDisconnect(connection);
         }
      } catch (Exception var23) {
         this.logger.log(SentryLevel.ERROR, "An exception occurred while creating the connection to spotlight.", var23);
      }
   }

   @TestOnly
   public String getSpotlightConnectionUrl() {
      if (this.options != null && this.options.getSpotlightConnectionUrl() != null) {
         return this.options.getSpotlightConnectionUrl();
      } else {
         return Platform.isAndroid() ? "http://10.0.2.2:8969/stream" : "http://localhost:8969/stream";
      }
   }

   @NotNull
   private HttpURLConnection createConnection(@NotNull String url) throws Exception {
      HttpURLConnection connection = (HttpURLConnection)URI.create(url).toURL().openConnection();
      connection.setReadTimeout(1000);
      connection.setConnectTimeout(1000);
      connection.setRequestMethod("POST");
      connection.setDoOutput(true);
      connection.setRequestProperty("Content-Encoding", "gzip");
      connection.setRequestProperty("Content-Type", "application/x-sentry-envelope");
      connection.setRequestProperty("Accept", "application/json");
      connection.setRequestProperty("Connection", "close");
      connection.connect();
      return connection;
   }

   private void closeAndDisconnect(@NotNull HttpURLConnection connection) {
      try {
         connection.getInputStream().close();
      } catch (IOException var6) {
      } finally {
         connection.disconnect();
      }
   }

   @Override
   public void close() throws IOException {
      this.executorService.close(0L);
      if (this.options != null && this.options.getBeforeEnvelopeCallback() == this) {
         this.options.setBeforeEnvelopeCallback(null);
      }
   }
}
