package io.sentry;

import io.sentry.hints.Flushable;
import io.sentry.hints.Retryable;
import io.sentry.util.HintUtils;
import io.sentry.util.Objects;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.ApiStatus.Internal;

@Internal
public final class EnvelopeSender extends DirectoryProcessor implements IEnvelopeSender {
   @NotNull
   private final IScopes scopes;
   @NotNull
   private final ISerializer serializer;
   @NotNull
   private final ILogger logger;

   public EnvelopeSender(@NotNull IScopes scopes, @NotNull ISerializer serializer, @NotNull ILogger logger, long flushTimeoutMillis, int maxQueueSize) {
      super(scopes, logger, flushTimeoutMillis, maxQueueSize);
      this.scopes = Objects.requireNonNull(scopes, "Scopes are required.");
      this.serializer = Objects.requireNonNull(serializer, "Serializer is required.");
      this.logger = Objects.requireNonNull(logger, "Logger is required.");
   }

   @Override
   protected void processFile(@NotNull File file, @NotNull Hint hint) {
      if (!file.isFile()) {
         this.logger.log(SentryLevel.DEBUG, "'%s' is not a file.", file.getAbsolutePath());
      } else if (!this.isRelevantFileName(file.getName())) {
         this.logger.log(SentryLevel.DEBUG, "File '%s' doesn't match extension expected.", file.getAbsolutePath());
      } else if (!file.getParentFile().canWrite()) {
         this.logger.log(SentryLevel.WARNING, "File '%s' cannot be deleted so it will not be processed.", file.getAbsolutePath());
      } else {
         try {
            InputStream is = new BufferedInputStream(new FileInputStream(file));

            try {
               SentryEnvelope envelope = this.serializer.deserializeEnvelope(is);
               if (envelope == null) {
                  this.logger.log(SentryLevel.ERROR, "Failed to deserialize cached envelope %s", file.getAbsolutePath());
               } else {
                  this.scopes.captureEnvelope(envelope, hint);
               }

               HintUtils.runIfHasTypeLogIfNot(hint, Flushable.class, this.logger, flushable -> {
                  if (!flushable.waitFlush()) {
                     this.logger.log(SentryLevel.WARNING, "Timed out waiting for envelope submission.");
                  }
               });
            } catch (Throwable var14) {
               try {
                  is.close();
               } catch (Throwable var13) {
                  var14.addSuppressed(var13);
               }

               throw var14;
            }

            is.close();
         } catch (FileNotFoundException var15) {
            this.logger.log(SentryLevel.ERROR, var15, "File '%s' cannot be found.", file.getAbsolutePath());
         } catch (IOException var16) {
            this.logger.log(SentryLevel.ERROR, var16, "I/O on file '%s' failed.", file.getAbsolutePath());
         } catch (Throwable var17) {
            this.logger.log(SentryLevel.ERROR, var17, "Failed to capture cached envelope %s", file.getAbsolutePath());
            HintUtils.runIfHasTypeLogIfNot(hint, Retryable.class, this.logger, retryable -> {
               retryable.setRetry(false);
               this.logger.log(SentryLevel.INFO, var17, "File '%s' won't retry.", file.getAbsolutePath());
            });
         } finally {
            HintUtils.runIfHasTypeLogIfNot(hint, Retryable.class, this.logger, retryable -> {
               if (!retryable.isRetry()) {
                  this.safeDelete(file, "after trying to capture it");
                  this.logger.log(SentryLevel.DEBUG, "Deleted file %s.", file.getAbsolutePath());
               } else {
                  this.logger.log(SentryLevel.INFO, "File not deleted since retry was marked. %s.", file.getAbsolutePath());
               }
            });
         }
      }
   }

   @Override
   protected boolean isRelevantFileName(@NotNull String fileName) {
      return fileName.endsWith(".envelope");
   }

   @Override
   public void processEnvelopeFile(@NotNull String path, @NotNull Hint hint) {
      Objects.requireNonNull(path, "Path is required.");
      this.processFile(new File(path), hint);
   }

   private void safeDelete(@NotNull File file, @NotNull String errorMessageSuffix) {
      try {
         if (!file.delete()) {
            this.logger.log(SentryLevel.ERROR, "Failed to delete '%s' %s", file.getAbsolutePath(), errorMessageSuffix);
         }
      } catch (Throwable var4) {
         this.logger.log(SentryLevel.ERROR, var4, "Failed to delete '%s' %s", file.getAbsolutePath(), errorMessageSuffix);
      }
   }
}
