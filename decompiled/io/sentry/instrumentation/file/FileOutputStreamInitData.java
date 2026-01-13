package io.sentry.instrumentation.file;

import io.sentry.ISpan;
import io.sentry.SentryOptions;
import java.io.File;
import java.io.FileOutputStream;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

final class FileOutputStreamInitData {
   @Nullable
   final File file;
   @Nullable
   final ISpan span;
   final boolean append;
   @NotNull
   final FileOutputStream delegate;
   @NotNull
   final SentryOptions options;

   FileOutputStreamInitData(@Nullable File file, boolean append, @Nullable ISpan span, @NotNull FileOutputStream delegate, @NotNull SentryOptions options) {
      this.file = file;
      this.append = append;
      this.span = span;
      this.delegate = delegate;
      this.options = options;
   }
}
