package io.sentry.cache;

import io.sentry.Breadcrumb;
import io.sentry.IScope;
import io.sentry.ScopeObserverAdapter;
import io.sentry.SentryLevel;
import io.sentry.SentryOptions;
import io.sentry.SpanContext;
import io.sentry.cache.tape.ObjectQueue;
import io.sentry.cache.tape.QueueFile;
import io.sentry.protocol.Contexts;
import io.sentry.protocol.Request;
import io.sentry.protocol.SentryId;
import io.sentry.protocol.User;
import io.sentry.util.LazyEvaluator;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.Map;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class PersistingScopeObserver extends ScopeObserverAdapter {
   private static final Charset UTF_8 = Charset.forName("UTF-8");
   public static final String SCOPE_CACHE = ".scope-cache";
   public static final String USER_FILENAME = "user.json";
   public static final String BREADCRUMBS_FILENAME = "breadcrumbs.json";
   public static final String TAGS_FILENAME = "tags.json";
   public static final String EXTRAS_FILENAME = "extras.json";
   public static final String CONTEXTS_FILENAME = "contexts.json";
   public static final String REQUEST_FILENAME = "request.json";
   public static final String LEVEL_FILENAME = "level.json";
   public static final String FINGERPRINT_FILENAME = "fingerprint.json";
   public static final String TRANSACTION_FILENAME = "transaction.json";
   public static final String TRACE_FILENAME = "trace.json";
   public static final String REPLAY_FILENAME = "replay.json";
   @NotNull
   private SentryOptions options;
   @NotNull
   private final LazyEvaluator<ObjectQueue<Breadcrumb>> breadcrumbsQueue = new LazyEvaluator<>(() -> {
      File cacheDir = CacheUtils.ensureCacheDir(this.options, ".scope-cache");
      if (cacheDir == null) {
         this.options.getLogger().log(SentryLevel.INFO, "Cache dir is not set, cannot store in scope cache");
         return ObjectQueue.createEmpty();
      } else {
         QueueFile queueFile = null;
         File file = new File(cacheDir, "breadcrumbs.json");

         try {
            try {
               queueFile = new QueueFile.Builder(file).size(this.options.getMaxBreadcrumbs()).build();
            } catch (IOException var5) {
               file.delete();
               queueFile = new QueueFile.Builder(file).size(this.options.getMaxBreadcrumbs()).build();
            }
         } catch (IOException var6) {
            this.options.getLogger().log(SentryLevel.ERROR, "Failed to create breadcrumbs queue", var6);
            return ObjectQueue.createEmpty();
         }

         return ObjectQueue.create(queueFile, new ObjectQueue.Converter<Breadcrumb>() {
            @Nullable
            public Breadcrumb from(byte[] source) {
               try {
                  Reader reader = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(source), PersistingScopeObserver.UTF_8));

                  Breadcrumb var3;
                  try {
                     var3 = PersistingScopeObserver.this.options.getSerializer().deserialize(reader, Breadcrumb.class);
                  } catch (Throwable var6) {
                     try {
                        reader.close();
                     } catch (Throwable var5) {
                        var6.addSuppressed(var5);
                     }

                     throw var6;
                  }

                  reader.close();
                  return var3;
               } catch (Throwable var7) {
                  PersistingScopeObserver.this.options.getLogger().log(SentryLevel.ERROR, var7, "Error reading entity from scope cache");
                  return null;
               }
            }

            public void toStream(Breadcrumb value, OutputStream sink) throws IOException {
               Writer writer = new BufferedWriter(new OutputStreamWriter(sink, PersistingScopeObserver.UTF_8));

               try {
                  PersistingScopeObserver.this.options.getSerializer().serialize(value, writer);
               } catch (Throwable var7) {
                  try {
                     writer.close();
                  } catch (Throwable var6) {
                     var7.addSuppressed(var6);
                  }

                  throw var7;
               }

               writer.close();
            }
         });
      }
   });

   public PersistingScopeObserver(@NotNull SentryOptions options) {
      this.options = options;
   }

   @Override
   public void setUser(@Nullable User user) {
      this.serializeToDisk(() -> {
         if (user == null) {
            this.delete("user.json");
         } else {
            this.store(user, "user.json");
         }
      });
   }

   @Override
   public void addBreadcrumb(@NotNull Breadcrumb crumb) {
      this.serializeToDisk(() -> {
         try {
            this.breadcrumbsQueue.getValue().add(crumb);
         } catch (IOException var3) {
            this.options.getLogger().log(SentryLevel.ERROR, "Failed to add breadcrumb to file queue", var3);
         }
      });
   }

   @Override
   public void setBreadcrumbs(@NotNull Collection<Breadcrumb> breadcrumbs) {
      if (breadcrumbs.isEmpty()) {
         this.serializeToDisk(() -> {
            try {
               this.breadcrumbsQueue.getValue().clear();
            } catch (IOException var2) {
               this.options.getLogger().log(SentryLevel.ERROR, "Failed to clear breadcrumbs from file queue", var2);
            }
         });
      }
   }

   @Override
   public void setTags(@NotNull Map<String, String> tags) {
      this.serializeToDisk(() -> this.store(tags, "tags.json"));
   }

   @Override
   public void setExtras(@NotNull Map<String, Object> extras) {
      this.serializeToDisk(() -> this.store(extras, "extras.json"));
   }

   @Override
   public void setRequest(@Nullable Request request) {
      this.serializeToDisk(() -> {
         if (request == null) {
            this.delete("request.json");
         } else {
            this.store(request, "request.json");
         }
      });
   }

   @Override
   public void setFingerprint(@NotNull Collection<String> fingerprint) {
      this.serializeToDisk(() -> this.store(fingerprint, "fingerprint.json"));
   }

   @Override
   public void setLevel(@Nullable SentryLevel level) {
      this.serializeToDisk(() -> {
         if (level == null) {
            this.delete("level.json");
         } else {
            this.store(level, "level.json");
         }
      });
   }

   @Override
   public void setTransaction(@Nullable String transaction) {
      this.serializeToDisk(() -> {
         if (transaction == null) {
            this.delete("transaction.json");
         } else {
            this.store(transaction, "transaction.json");
         }
      });
   }

   @Override
   public void setTrace(@Nullable SpanContext spanContext, @NotNull IScope scope) {
      this.serializeToDisk(() -> {
         if (spanContext == null) {
            this.store(scope.getPropagationContext().toSpanContext(), "trace.json");
         } else {
            this.store(spanContext, "trace.json");
         }
      });
   }

   @Override
   public void setContexts(@NotNull Contexts contexts) {
      this.serializeToDisk(() -> this.store(contexts, "contexts.json"));
   }

   @Override
   public void setReplayId(@NotNull SentryId replayId) {
      this.serializeToDisk(() -> this.store(replayId, "replay.json"));
   }

   private void serializeToDisk(@NotNull Runnable task) {
      if (this.options.isEnableScopePersistence()) {
         if (Thread.currentThread().getName().contains("SentryExecutor")) {
            try {
               task.run();
            } catch (Throwable var3) {
               this.options.getLogger().log(SentryLevel.ERROR, "Serialization task failed", var3);
            }
         } else {
            try {
               this.options.getExecutorService().submit(() -> {
                  try {
                     task.run();
                  } catch (Throwable var3x) {
                     this.options.getLogger().log(SentryLevel.ERROR, "Serialization task failed", var3x);
                  }
               });
            } catch (Throwable var4) {
               this.options.getLogger().log(SentryLevel.ERROR, "Serialization task could not be scheduled", var4);
            }
         }
      }
   }

   private <T> void store(@NotNull T entity, @NotNull String fileName) {
      store(this.options, entity, fileName);
   }

   private void delete(@NotNull String fileName) {
      CacheUtils.delete(this.options, ".scope-cache", fileName);
   }

   public static <T> void store(@NotNull SentryOptions options, @NotNull T entity, @NotNull String fileName) {
      CacheUtils.store(options, entity, ".scope-cache", fileName);
   }

   @Nullable
   public <T> T read(@NotNull SentryOptions options, @NotNull String fileName, @NotNull Class<T> clazz) {
      if (fileName.equals("breadcrumbs.json")) {
         try {
            return clazz.cast(this.breadcrumbsQueue.getValue().asList());
         } catch (IOException var5) {
            options.getLogger().log(SentryLevel.ERROR, "Unable to read serialized breadcrumbs from QueueFile");
            return null;
         }
      } else {
         return CacheUtils.read(options, ".scope-cache", fileName, clazz, null);
      }
   }

   public void resetCache() {
      try {
         this.breadcrumbsQueue.getValue().clear();
      } catch (IOException var2) {
         this.options.getLogger().log(SentryLevel.ERROR, "Failed to clear breadcrumbs from file queue", var2);
      }

      this.delete("user.json");
      this.delete("level.json");
      this.delete("request.json");
      this.delete("fingerprint.json");
      this.delete("contexts.json");
      this.delete("extras.json");
      this.delete("tags.json");
      this.delete("trace.json");
      this.delete("transaction.json");
   }
}
