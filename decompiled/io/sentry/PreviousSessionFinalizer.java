package io.sentry;

import io.sentry.cache.EnvelopeCache;
import io.sentry.cache.IEnvelopeCache;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.util.Date;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

final class PreviousSessionFinalizer implements Runnable {
   private static final Charset UTF_8 = Charset.forName("UTF-8");
   @NotNull
   private final SentryOptions options;
   @NotNull
   private final IScopes scopes;

   PreviousSessionFinalizer(@NotNull SentryOptions options, @NotNull IScopes scopes) {
      this.options = options;
      this.scopes = scopes;
   }

   @Override
   public void run() {
      String cacheDirPath = this.options.getCacheDirPath();
      if (cacheDirPath == null) {
         this.options.getLogger().log(SentryLevel.INFO, "Cache dir is not set, not finalizing the previous session.");
      } else if (!this.options.isEnableAutoSessionTracking()) {
         this.options.getLogger().log(SentryLevel.DEBUG, "Session tracking is disabled, bailing from previous session finalizer.");
      } else {
         IEnvelopeCache cache = this.options.getEnvelopeDiskCache();
         if (cache instanceof EnvelopeCache && !((EnvelopeCache)cache).waitPreviousSessionFlush()) {
            this.options.getLogger().log(SentryLevel.WARNING, "Timed out waiting to flush previous session to its own file in session finalizer.");
         } else {
            File previousSessionFile = EnvelopeCache.getPreviousSessionFile(cacheDirPath);
            ISerializer serializer = this.options.getSerializer();
            if (previousSessionFile.exists()) {
               this.options.getLogger().log(SentryLevel.WARNING, "Current session is not ended, we'd need to end it.");

               try {
                  Reader reader = new BufferedReader(new InputStreamReader(new FileInputStream(previousSessionFile), UTF_8));

                  try {
                     Session session = serializer.deserialize(reader, Session.class);
                     if (session == null) {
                        this.options
                           .getLogger()
                           .log(SentryLevel.ERROR, "Stream from path %s resulted in a null envelope.", previousSessionFile.getAbsolutePath());
                     } else {
                        Date timestamp = null;
                        File crashMarkerFile = new File(this.options.getCacheDirPath(), ".sentry-native/last_crash");
                        if (crashMarkerFile.exists()) {
                           this.options.getLogger().log(SentryLevel.INFO, "Crash marker file exists, last Session is gonna be Crashed.");
                           timestamp = this.getTimestampFromCrashMarkerFile(crashMarkerFile);
                           if (!crashMarkerFile.delete()) {
                              this.options.getLogger().log(SentryLevel.ERROR, "Failed to delete the crash marker file. %s.", crashMarkerFile.getAbsolutePath());
                           }

                           session.update(Session.State.Crashed, null, true);
                        }

                        if (session.getAbnormalMechanism() == null) {
                           session.end(timestamp);
                        }

                        SentryEnvelope fromSession = SentryEnvelope.from(serializer, session, this.options.getSdkVersion());
                        this.scopes.captureEnvelope(fromSession);
                     }
                  } catch (Throwable var11) {
                     try {
                        reader.close();
                     } catch (Throwable var10) {
                        var11.addSuppressed(var10);
                     }

                     throw var11;
                  }

                  reader.close();
               } catch (Throwable var12) {
                  this.options.getLogger().log(SentryLevel.ERROR, "Error processing previous session.", var12);
               }

               if (!previousSessionFile.delete()) {
                  this.options.getLogger().log(SentryLevel.WARNING, "Failed to delete the previous session file.");
               }
            }
         }
      }
   }

   @Nullable
   private Date getTimestampFromCrashMarkerFile(@NotNull File markerFile) {
      try {
         BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(markerFile), UTF_8));

         Date var4;
         try {
            String timestamp = reader.readLine();
            this.options.getLogger().log(SentryLevel.DEBUG, "Crash marker file has %s timestamp.", timestamp);
            var4 = DateUtils.getDateTime(timestamp);
         } catch (Throwable var6) {
            try {
               reader.close();
            } catch (Throwable var5) {
               var6.addSuppressed(var5);
            }

            throw var6;
         }

         reader.close();
         return var4;
      } catch (IOException var7) {
         this.options.getLogger().log(SentryLevel.ERROR, "Error reading the crash marker file.", var7);
      } catch (IllegalArgumentException var8) {
         this.options.getLogger().log(SentryLevel.ERROR, var8, "Error converting the crash timestamp.");
      }

      return null;
   }
}
