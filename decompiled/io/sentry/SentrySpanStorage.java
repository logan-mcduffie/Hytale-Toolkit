package io.sentry;

import io.sentry.util.AutoClosableReentrantLock;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.ApiStatus.Internal;

@Deprecated
@Internal
public final class SentrySpanStorage {
   @Nullable
   private static volatile SentrySpanStorage INSTANCE;
   @NotNull
   private static final AutoClosableReentrantLock staticLock = new AutoClosableReentrantLock();
   @NotNull
   private final Map<String, ISpan> spans = new ConcurrentHashMap<>();

   @NotNull
   public static SentrySpanStorage getInstance() {
      if (INSTANCE == null) {
         ISentryLifecycleToken ignored = staticLock.acquire();

         try {
            if (INSTANCE == null) {
               INSTANCE = new SentrySpanStorage();
            }
         } catch (Throwable var4) {
            if (ignored != null) {
               try {
                  ignored.close();
               } catch (Throwable var3) {
                  var4.addSuppressed(var3);
               }
            }

            throw var4;
         }

         if (ignored != null) {
            ignored.close();
         }
      }

      return INSTANCE;
   }

   private SentrySpanStorage() {
   }

   public void store(@NotNull String spanId, @NotNull ISpan span) {
      this.spans.put(spanId, span);
   }

   @Nullable
   public ISpan get(@Nullable String spanId) {
      return this.spans.get(spanId);
   }

   @Nullable
   public ISpan removeAndGet(@Nullable String spanId) {
      return this.spans.remove(spanId);
   }
}
