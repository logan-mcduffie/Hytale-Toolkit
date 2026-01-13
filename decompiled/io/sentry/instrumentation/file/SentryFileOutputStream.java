package io.sentry.instrumentation.file;

import io.sentry.IScopes;
import io.sentry.ISpan;
import io.sentry.ScopesAdapter;
import io.sentry.SentryOptions;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class SentryFileOutputStream extends FileOutputStream {
   @NotNull
   private final FileOutputStream delegate;
   @NotNull
   private final FileIOSpanManager spanManager;

   public SentryFileOutputStream(@Nullable String name) throws FileNotFoundException {
      this(name != null ? new File(name) : null, false, ScopesAdapter.getInstance());
   }

   public SentryFileOutputStream(@Nullable String name, boolean append) throws FileNotFoundException {
      this(init(name != null ? new File(name) : null, append, null, ScopesAdapter.getInstance()));
   }

   public SentryFileOutputStream(@Nullable File file) throws FileNotFoundException {
      this(file, false, ScopesAdapter.getInstance());
   }

   public SentryFileOutputStream(@Nullable File file, boolean append) throws FileNotFoundException {
      this(init(file, append, null, ScopesAdapter.getInstance()));
   }

   public SentryFileOutputStream(@NotNull FileDescriptor fdObj) {
      this(init(fdObj, null, ScopesAdapter.getInstance()), fdObj);
   }

   SentryFileOutputStream(@Nullable File file, boolean append, @NotNull IScopes scopes) throws FileNotFoundException {
      this(init(file, append, null, scopes));
   }

   private SentryFileOutputStream(@NotNull FileOutputStreamInitData data, @NotNull FileDescriptor fd) {
      super(fd);
      this.spanManager = new FileIOSpanManager(data.span, data.file, data.options);
      this.delegate = data.delegate;
   }

   private SentryFileOutputStream(@NotNull FileOutputStreamInitData data) throws FileNotFoundException {
      super(getFileDescriptor(data.delegate));
      this.spanManager = new FileIOSpanManager(data.span, data.file, data.options);
      this.delegate = data.delegate;
   }

   private static FileOutputStreamInitData init(@Nullable File file, boolean append, @Nullable FileOutputStream delegate, @NotNull IScopes scopes) throws FileNotFoundException {
      ISpan span = FileIOSpanManager.startSpan(scopes, "file.write");
      if (delegate == null) {
         delegate = new FileOutputStream(file, append);
      }

      return new FileOutputStreamInitData(file, append, span, delegate, scopes.getOptions());
   }

   private static FileOutputStreamInitData init(@NotNull FileDescriptor fd, @Nullable FileOutputStream delegate, @NotNull IScopes scopes) {
      ISpan span = FileIOSpanManager.startSpan(scopes, "file.write");
      if (delegate == null) {
         delegate = new FileOutputStream(fd);
      }

      return new FileOutputStreamInitData(null, false, span, delegate, scopes.getOptions());
   }

   @Override
   public void write(int b) throws IOException {
      this.spanManager.performIO(() -> {
         this.delegate.write(b);
         return 1;
      });
   }

   @Override
   public void write(byte @NotNull [] b) throws IOException {
      this.spanManager.performIO(() -> {
         this.delegate.write(b);
         return b.length;
      });
   }

   @Override
   public void write(byte @NotNull [] b, int off, int len) throws IOException {
      this.spanManager.performIO(() -> {
         this.delegate.write(b, off, len);
         return len;
      });
   }

   @Override
   public void close() throws IOException {
      this.spanManager.finish(this.delegate);
      super.close();
   }

   private static FileDescriptor getFileDescriptor(@NotNull FileOutputStream stream) throws FileNotFoundException {
      try {
         return stream.getFD();
      } catch (IOException var2) {
         throw new FileNotFoundException("No file descriptor");
      }
   }

   public static final class Factory {
      public static FileOutputStream create(@NotNull FileOutputStream delegate, @Nullable String name) throws FileNotFoundException {
         IScopes scopes = ScopesAdapter.getInstance();
         return (FileOutputStream)(isTracingEnabled(scopes)
            ? new SentryFileOutputStream(SentryFileOutputStream.init(name != null ? new File(name) : null, false, delegate, ScopesAdapter.getInstance()))
            : delegate);
      }

      public static FileOutputStream create(@NotNull FileOutputStream delegate, @Nullable String name, boolean append) throws FileNotFoundException {
         IScopes scopes = ScopesAdapter.getInstance();
         return (FileOutputStream)(isTracingEnabled(scopes)
            ? new SentryFileOutputStream(SentryFileOutputStream.init(name != null ? new File(name) : null, append, delegate, ScopesAdapter.getInstance()))
            : delegate);
      }

      public static FileOutputStream create(@NotNull FileOutputStream delegate, @Nullable File file) throws FileNotFoundException {
         IScopes scopes = ScopesAdapter.getInstance();
         return (FileOutputStream)(isTracingEnabled(scopes)
            ? new SentryFileOutputStream(SentryFileOutputStream.init(file, false, delegate, ScopesAdapter.getInstance()))
            : delegate);
      }

      public static FileOutputStream create(@NotNull FileOutputStream delegate, @Nullable File file, boolean append) throws FileNotFoundException {
         IScopes scopes = ScopesAdapter.getInstance();
         return (FileOutputStream)(isTracingEnabled(scopes)
            ? new SentryFileOutputStream(SentryFileOutputStream.init(file, append, delegate, ScopesAdapter.getInstance()))
            : delegate);
      }

      public static FileOutputStream create(@NotNull FileOutputStream delegate, @NotNull FileDescriptor fdObj) {
         IScopes scopes = ScopesAdapter.getInstance();
         return (FileOutputStream)(isTracingEnabled(scopes)
            ? new SentryFileOutputStream(SentryFileOutputStream.init(fdObj, delegate, ScopesAdapter.getInstance()), fdObj)
            : delegate);
      }

      public static FileOutputStream create(@NotNull FileOutputStream delegate, @Nullable File file, @NotNull IScopes scopes) throws FileNotFoundException {
         return (FileOutputStream)(isTracingEnabled(scopes) ? new SentryFileOutputStream(SentryFileOutputStream.init(file, false, delegate, scopes)) : delegate);
      }

      private static boolean isTracingEnabled(@NotNull IScopes scopes) {
         SentryOptions options = scopes.getOptions();
         return options.isTracingEnabled();
      }
   }
}
