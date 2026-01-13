package io.sentry.instrumentation.file;

import io.sentry.IScopes;
import io.sentry.ISpan;
import io.sentry.ScopesAdapter;
import io.sentry.SentryOptions;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class SentryFileInputStream extends FileInputStream {
   @NotNull
   private final FileInputStream delegate;
   @NotNull
   private final FileIOSpanManager spanManager;

   public SentryFileInputStream(@Nullable String name) throws FileNotFoundException {
      this(name != null ? new File(name) : null, ScopesAdapter.getInstance());
   }

   public SentryFileInputStream(@Nullable File file) throws FileNotFoundException {
      this(file, ScopesAdapter.getInstance());
   }

   public SentryFileInputStream(@NotNull FileDescriptor fdObj) {
      this(fdObj, ScopesAdapter.getInstance());
   }

   SentryFileInputStream(@Nullable File file, @NotNull IScopes scopes) throws FileNotFoundException {
      this(init(file, null, scopes));
   }

   SentryFileInputStream(@NotNull FileDescriptor fdObj, @NotNull IScopes scopes) {
      this(init(fdObj, null, scopes), fdObj);
   }

   private SentryFileInputStream(@NotNull FileInputStreamInitData data, @NotNull FileDescriptor fd) {
      super(fd);
      this.spanManager = new FileIOSpanManager(data.span, data.file, data.options);
      this.delegate = data.delegate;
   }

   private SentryFileInputStream(@NotNull FileInputStreamInitData data) throws FileNotFoundException {
      super(getFileDescriptor(data.delegate));
      this.spanManager = new FileIOSpanManager(data.span, data.file, data.options);
      this.delegate = data.delegate;
   }

   private static FileInputStreamInitData init(@Nullable File file, @Nullable FileInputStream delegate, @NotNull IScopes scopes) throws FileNotFoundException {
      ISpan span = FileIOSpanManager.startSpan(scopes, "file.read");
      if (delegate == null) {
         delegate = new FileInputStream(file);
      }

      return new FileInputStreamInitData(file, span, delegate, scopes.getOptions());
   }

   private static FileInputStreamInitData init(@NotNull FileDescriptor fd, @Nullable FileInputStream delegate, @NotNull IScopes scopes) {
      ISpan span = FileIOSpanManager.startSpan(scopes, "file.read");
      if (delegate == null) {
         delegate = new FileInputStream(fd);
      }

      return new FileInputStreamInitData(null, span, delegate, scopes.getOptions());
   }

   @Override
   public int read() throws IOException {
      AtomicInteger result = new AtomicInteger(0);
      this.spanManager.performIO(() -> {
         int res = this.delegate.read();
         result.set(res);
         return res != -1 ? 1 : 0;
      });
      return result.get();
   }

   @Override
   public int read(byte @NotNull [] b) throws IOException {
      return this.spanManager.performIO(() -> this.delegate.read(b));
   }

   @Override
   public int read(byte @NotNull [] b, int off, int len) throws IOException {
      return this.spanManager.performIO(() -> this.delegate.read(b, off, len));
   }

   @Override
   public long skip(long n) throws IOException {
      return this.spanManager.performIO(() -> this.delegate.skip(n));
   }

   @Override
   public void close() throws IOException {
      this.spanManager.finish(this.delegate);
      super.close();
   }

   private static FileDescriptor getFileDescriptor(@NotNull FileInputStream stream) throws FileNotFoundException {
      try {
         return stream.getFD();
      } catch (IOException var2) {
         throw new FileNotFoundException("No file descriptor");
      }
   }

   public static final class Factory {
      public static FileInputStream create(@NotNull FileInputStream delegate, @Nullable String name) throws FileNotFoundException {
         IScopes scopes = ScopesAdapter.getInstance();
         return (FileInputStream)(isTracingEnabled(scopes)
            ? new SentryFileInputStream(SentryFileInputStream.init(name != null ? new File(name) : null, delegate, scopes))
            : delegate);
      }

      public static FileInputStream create(@NotNull FileInputStream delegate, @Nullable File file) throws FileNotFoundException {
         IScopes scopes = ScopesAdapter.getInstance();
         return (FileInputStream)(isTracingEnabled(scopes) ? new SentryFileInputStream(SentryFileInputStream.init(file, delegate, scopes)) : delegate);
      }

      public static FileInputStream create(@NotNull FileInputStream delegate, @NotNull FileDescriptor descriptor) {
         IScopes scopes = ScopesAdapter.getInstance();
         return (FileInputStream)(isTracingEnabled(scopes)
            ? new SentryFileInputStream(SentryFileInputStream.init(descriptor, delegate, scopes), descriptor)
            : delegate);
      }

      static FileInputStream create(@NotNull FileInputStream delegate, @Nullable File file, @NotNull IScopes scopes) throws FileNotFoundException {
         return (FileInputStream)(isTracingEnabled(scopes) ? new SentryFileInputStream(SentryFileInputStream.init(file, delegate, scopes)) : delegate);
      }

      private static boolean isTracingEnabled(@NotNull IScopes scopes) {
         SentryOptions options = scopes.getOptions();
         return options.isTracingEnabled();
      }
   }
}
