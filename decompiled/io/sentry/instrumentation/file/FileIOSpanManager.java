package io.sentry.instrumentation.file;

import io.sentry.IScopes;
import io.sentry.ISpan;
import io.sentry.SentryIntegrationPackageStorage;
import io.sentry.SentryOptions;
import io.sentry.SentryStackTraceFactory;
import io.sentry.SpanStatus;
import io.sentry.util.Platform;
import io.sentry.util.StringUtils;
import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

final class FileIOSpanManager {
   @Nullable
   private final ISpan currentSpan;
   @Nullable
   private final File file;
   @NotNull
   private final SentryOptions options;
   @NotNull
   private SpanStatus spanStatus = SpanStatus.OK;
   private long byteCount;
   @NotNull
   private final SentryStackTraceFactory stackTraceFactory;

   @Nullable
   static ISpan startSpan(@NotNull IScopes scopes, @NotNull String op) {
      ISpan parent = (ISpan)(Platform.isAndroid() ? scopes.getTransaction() : scopes.getSpan());
      return parent != null ? parent.startChild(op) : null;
   }

   FileIOSpanManager(@Nullable ISpan currentSpan, @Nullable File file, @NotNull SentryOptions options) {
      this.currentSpan = currentSpan;
      this.file = file;
      this.options = options;
      this.stackTraceFactory = new SentryStackTraceFactory(options);
      SentryIntegrationPackageStorage.getInstance().addIntegration("FileIO");
   }

   <T> T performIO(@NotNull FileIOSpanManager.FileIOCallable<T> operation) throws IOException {
      try {
         T result = operation.call();
         if (result instanceof Integer) {
            int resUnboxed = (Integer)result;
            if (resUnboxed != -1) {
               this.byteCount += resUnboxed;
            }
         } else if (result instanceof Long) {
            long resUnboxed = (Long)result;
            if (resUnboxed != -1L) {
               this.byteCount += resUnboxed;
            }
         }

         return result;
      } catch (IOException var5) {
         this.spanStatus = SpanStatus.INTERNAL_ERROR;
         if (this.currentSpan != null) {
            this.currentSpan.setThrowable(var5);
         }

         throw var5;
      }
   }

   void finish(@NotNull Closeable delegate) throws IOException {
      try {
         delegate.close();
      } catch (IOException var6) {
         this.spanStatus = SpanStatus.INTERNAL_ERROR;
         if (this.currentSpan != null) {
            this.currentSpan.setThrowable(var6);
         }

         throw var6;
      } finally {
         this.finishSpan();
      }
   }

   private void finishSpan() {
      if (this.currentSpan != null) {
         String byteCountToString = StringUtils.byteCountToString(this.byteCount);
         if (this.file != null) {
            String description = this.getDescription(this.file);
            this.currentSpan.setDescription(description);
            if (this.options.isSendDefaultPii()) {
               this.currentSpan.setData("file.path", this.file.getAbsolutePath());
            }
         } else {
            this.currentSpan.setDescription(byteCountToString);
         }

         this.currentSpan.setData("file.size", this.byteCount);
         boolean isMainThread = this.options.getThreadChecker().isMainThread();
         this.currentSpan.setData("blocked_main_thread", isMainThread);
         if (isMainThread) {
            this.currentSpan.setData("call_stack", this.stackTraceFactory.getInAppCallStack());
         }

         this.currentSpan.finish(this.spanStatus);
      }
   }

   @NotNull
   private String getDescription(@NotNull File file) {
      String byteCountToString = StringUtils.byteCountToString(this.byteCount);
      if (this.options.isSendDefaultPii()) {
         return file.getName() + " (" + byteCountToString + ")";
      } else {
         int lastDotIndex = file.getName().lastIndexOf(46);
         if (lastDotIndex > 0 && lastDotIndex < file.getName().length() - 1) {
            String fileExtension = file.getName().substring(lastDotIndex);
            return "***" + fileExtension + " (" + byteCountToString + ")";
         } else {
            return "*** (" + byteCountToString + ")";
         }
      }
   }

   @FunctionalInterface
   interface FileIOCallable<T> {
      T call() throws IOException;
   }
}
