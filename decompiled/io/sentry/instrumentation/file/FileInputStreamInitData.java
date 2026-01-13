package io.sentry.instrumentation.file;

import io.sentry.ISpan;
import io.sentry.SentryOptions;
import java.io.File;
import java.io.FileInputStream;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

final class FileInputStreamInitData {
   @Nullable
   final File file;
   @Nullable
   final ISpan span;
   @NotNull
   final FileInputStream delegate;
   @NotNull
   final SentryOptions options;

   FileInputStreamInitData(@Nullable File file, @Nullable ISpan span, @NotNull FileInputStream delegate, @NotNull SentryOptions options) {
      this.file = file;
      this.span = span;
      this.delegate = delegate;
      this.options = options;
   }
}
