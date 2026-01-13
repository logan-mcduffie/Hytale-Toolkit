package io.sentry.cache;

import io.sentry.DateUtils;
import io.sentry.Hint;
import io.sentry.ISentryLifecycleToken;
import io.sentry.SentryCrashLastRunState;
import io.sentry.SentryEnvelope;
import io.sentry.SentryEnvelopeItem;
import io.sentry.SentryItemType;
import io.sentry.SentryLevel;
import io.sentry.SentryOptions;
import io.sentry.SentryUUID;
import io.sentry.Session;
import io.sentry.UncaughtExceptionHandlerIntegration;
import io.sentry.hints.AbnormalExit;
import io.sentry.hints.SessionEnd;
import io.sentry.hints.SessionStart;
import io.sentry.transport.NoOpEnvelopeCache;
import io.sentry.util.AutoClosableReentrantLock;
import io.sentry.util.HintUtils;
import io.sentry.util.Objects;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.ApiStatus.Internal;

@Internal
public class EnvelopeCache extends CacheStrategy implements IEnvelopeCache {
   public static final String SUFFIX_ENVELOPE_FILE = ".envelope";
   public static final String PREFIX_CURRENT_SESSION_FILE = "session";
   public static final String PREFIX_PREVIOUS_SESSION_FILE = "previous_session";
   static final String SUFFIX_SESSION_FILE = ".json";
   public static final String CRASH_MARKER_FILE = "last_crash";
   public static final String NATIVE_CRASH_MARKER_FILE = ".sentry-native/last_crash";
   public static final String STARTUP_CRASH_MARKER_FILE = "startup_crash";
   private final CountDownLatch previousSessionLatch;
   @NotNull
   private final Map<SentryEnvelope, String> fileNameMap = new WeakHashMap<>();
   @NotNull
   protected final AutoClosableReentrantLock cacheLock = new AutoClosableReentrantLock();
   @NotNull
   protected final AutoClosableReentrantLock sessionLock = new AutoClosableReentrantLock();

   @NotNull
   public static IEnvelopeCache create(@NotNull SentryOptions options) {
      String cacheDirPath = options.getCacheDirPath();
      int maxCacheItems = options.getMaxCacheItems();
      if (cacheDirPath == null) {
         options.getLogger().log(SentryLevel.WARNING, "cacheDirPath is null, returning NoOpEnvelopeCache");
         return NoOpEnvelopeCache.getInstance();
      } else {
         return new EnvelopeCache(options, cacheDirPath, maxCacheItems);
      }
   }

   public EnvelopeCache(@NotNull SentryOptions options, @NotNull String cacheDirPath, int maxCacheItems) {
      super(options, cacheDirPath, maxCacheItems);
      this.previousSessionLatch = new CountDownLatch(1);
   }

   @Override
   public void store(@NotNull SentryEnvelope envelope, @NotNull Hint hint) {
      this.storeInternal(envelope, hint);
   }

   @Override
   public boolean storeEnvelope(@NotNull SentryEnvelope envelope, @NotNull Hint hint) {
      return this.storeInternal(envelope, hint);
   }

   private boolean storeInternal(@NotNull SentryEnvelope envelope, @NotNull Hint hint) {
      Objects.requireNonNull(envelope, "Envelope is required.");
      this.rotateCacheIfNeeded(this.allEnvelopeFiles());
      File currentSessionFile = getCurrentSessionFile(this.directory.getAbsolutePath());
      File previousSessionFile = getPreviousSessionFile(this.directory.getAbsolutePath());
      if (HintUtils.hasType(hint, SessionEnd.class) && !currentSessionFile.delete()) {
         this.options.getLogger().log(SentryLevel.WARNING, "Current envelope doesn't exist.");
      }

      if (HintUtils.hasType(hint, AbnormalExit.class)) {
         this.tryEndPreviousSession(hint);
      }

      if (HintUtils.hasType(hint, SessionStart.class)) {
         this.movePreviousSession(currentSessionFile, previousSessionFile);
         this.updateCurrentSession(currentSessionFile, envelope);
         boolean crashedLastRun = false;
         File crashMarkerFile = new File(this.options.getCacheDirPath(), ".sentry-native/last_crash");
         if (crashMarkerFile.exists()) {
            crashedLastRun = true;
         }

         if (!crashedLastRun) {
            File javaCrashMarkerFile = new File(this.options.getCacheDirPath(), "last_crash");
            if (javaCrashMarkerFile.exists()) {
               this.options.getLogger().log(SentryLevel.INFO, "Crash marker file exists, crashedLastRun will return true.");
               crashedLastRun = true;
               if (!javaCrashMarkerFile.delete()) {
                  this.options.getLogger().log(SentryLevel.ERROR, "Failed to delete the crash marker file. %s.", javaCrashMarkerFile.getAbsolutePath());
               }
            }
         }

         SentryCrashLastRunState.getInstance().setCrashedLastRun(crashedLastRun);
         this.flushPreviousSession();
      }

      File envelopeFile = this.getEnvelopeFile(envelope);
      if (envelopeFile.exists()) {
         this.options
            .getLogger()
            .log(SentryLevel.WARNING, "Not adding Envelope to offline storage because it already exists: %s", envelopeFile.getAbsolutePath());
         return true;
      } else {
         this.options.getLogger().log(SentryLevel.DEBUG, "Adding Envelope to offline storage: %s", envelopeFile.getAbsolutePath());
         boolean didWriteToDisk = this.writeEnvelopeToDisk(envelopeFile, envelope);
         if (HintUtils.hasType(hint, UncaughtExceptionHandlerIntegration.UncaughtExceptionHint.class)) {
            this.writeCrashMarkerFile();
         }

         return didWriteToDisk;
      }
   }

   private void tryEndPreviousSession(@NotNull Hint hint) {
      Object sdkHint = HintUtils.getSentrySdkHint(hint);
      if (sdkHint instanceof AbnormalExit) {
         File previousSessionFile = getPreviousSessionFile(this.directory.getAbsolutePath());
         if (previousSessionFile.exists()) {
            this.options.getLogger().log(SentryLevel.WARNING, "Previous session is not ended, we'd need to end it.");

            try {
               Reader reader = new BufferedReader(new InputStreamReader(new FileInputStream(previousSessionFile), UTF_8));

               label71: {
                  try {
                     Session session = this.serializer.getValue().deserialize(reader, Session.class);
                     if (session != null) {
                        AbnormalExit abnormalHint = (AbnormalExit)sdkHint;
                        Long abnormalExitTimestamp = abnormalHint.timestamp();
                        Date timestamp = null;
                        if (abnormalExitTimestamp != null) {
                           timestamp = DateUtils.getDateTime(abnormalExitTimestamp);
                           Date sessionStart = session.getStarted();
                           if (sessionStart == null || timestamp.before(sessionStart)) {
                              this.options
                                 .getLogger()
                                 .log(SentryLevel.WARNING, "Abnormal exit happened before previous session start, not ending the session.");
                              break label71;
                           }
                        }

                        String abnormalMechanism = abnormalHint.mechanism();
                        session.update(Session.State.Abnormal, null, true, abnormalMechanism);
                        session.end(timestamp);
                        this.writeSessionToDisk(previousSessionFile, session);
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
                  return;
               }

               reader.close();
               return;
            } catch (Throwable var12) {
               this.options.getLogger().log(SentryLevel.ERROR, "Error processing previous session.", var12);
            }
         } else {
            this.options.getLogger().log(SentryLevel.DEBUG, "No previous session file to end.");
         }
      }
   }

   private void writeCrashMarkerFile() {
      File crashMarkerFile = new File(this.options.getCacheDirPath(), "last_crash");

      try {
         OutputStream outputStream = new FileOutputStream(crashMarkerFile);

         try {
            String timestamp = DateUtils.getTimestamp(DateUtils.getCurrentDateTime());
            outputStream.write(timestamp.getBytes(UTF_8));
            outputStream.flush();
         } catch (Throwable var6) {
            try {
               outputStream.close();
            } catch (Throwable var5) {
               var6.addSuppressed(var5);
            }

            throw var6;
         }

         outputStream.close();
      } catch (Throwable var7) {
         this.options.getLogger().log(SentryLevel.ERROR, "Error writing the crash marker file to the disk", var7);
      }
   }

   private void updateCurrentSession(@NotNull File currentSessionFile, @NotNull SentryEnvelope envelope) {
      Iterable<SentryEnvelopeItem> items = envelope.getItems();
      if (items.iterator().hasNext()) {
         SentryEnvelopeItem item = items.iterator().next();
         if (SentryItemType.Session.equals(item.getHeader().getType())) {
            try {
               Reader reader = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(item.getData()), UTF_8));

               try {
                  Session session = this.serializer.getValue().deserialize(reader, Session.class);
                  if (session == null) {
                     this.options.getLogger().log(SentryLevel.ERROR, "Item of type %s returned null by the parser.", item.getHeader().getType());
                  } else {
                     this.writeSessionToDisk(currentSessionFile, session);
                  }
               } catch (Throwable var9) {
                  try {
                     reader.close();
                  } catch (Throwable var8) {
                     var9.addSuppressed(var8);
                  }

                  throw var9;
               }

               reader.close();
            } catch (Throwable var10) {
               this.options.getLogger().log(SentryLevel.ERROR, "Item failed to process.", var10);
            }
         } else {
            this.options.getLogger().log(SentryLevel.INFO, "Current envelope has a different envelope type %s", item.getHeader().getType());
         }
      } else {
         this.options.getLogger().log(SentryLevel.INFO, "Current envelope %s is empty", currentSessionFile.getAbsolutePath());
      }
   }

   private boolean writeEnvelopeToDisk(@NotNull File file, @NotNull SentryEnvelope envelope) {
      if (file.exists()) {
         this.options.getLogger().log(SentryLevel.DEBUG, "Overwriting envelope to offline storage: %s", file.getAbsolutePath());
         if (!file.delete()) {
            this.options.getLogger().log(SentryLevel.ERROR, "Failed to delete: %s", file.getAbsolutePath());
         }
      }

      try {
         OutputStream outputStream = new FileOutputStream(file);

         try {
            this.serializer.getValue().serialize(envelope, outputStream);
         } catch (Throwable var7) {
            try {
               outputStream.close();
            } catch (Throwable var6) {
               var7.addSuppressed(var6);
            }

            throw var7;
         }

         outputStream.close();
         return true;
      } catch (Throwable var8) {
         this.options.getLogger().log(SentryLevel.ERROR, var8, "Error writing Envelope %s to offline storage", file.getAbsolutePath());
         return false;
      }
   }

   private void writeSessionToDisk(@NotNull File file, @NotNull Session session) {
      try {
         OutputStream outputStream = new FileOutputStream(file);

         try {
            Writer writer = new BufferedWriter(new OutputStreamWriter(outputStream, UTF_8));

            try {
               this.options.getLogger().log(SentryLevel.DEBUG, "Overwriting session to offline storage: %s", session.getSessionId());
               this.serializer.getValue().serialize(session, writer);
            } catch (Throwable var9) {
               try {
                  writer.close();
               } catch (Throwable var8) {
                  var9.addSuppressed(var8);
               }

               throw var9;
            }

            writer.close();
         } catch (Throwable var10) {
            try {
               outputStream.close();
            } catch (Throwable var7) {
               var10.addSuppressed(var7);
            }

            throw var10;
         }

         outputStream.close();
      } catch (Throwable var11) {
         this.options.getLogger().log(SentryLevel.ERROR, var11, "Error writing Session to offline storage: %s", session.getSessionId());
      }
   }

   @Override
   public void discard(@NotNull SentryEnvelope envelope) {
      Objects.requireNonNull(envelope, "Envelope is required.");
      File envelopeFile = this.getEnvelopeFile(envelope);
      if (envelopeFile.delete()) {
         this.options.getLogger().log(SentryLevel.DEBUG, "Discarding envelope from cache: %s", envelopeFile.getAbsolutePath());
      } else {
         this.options.getLogger().log(SentryLevel.DEBUG, "Envelope was not cached or could not be deleted: %s", envelopeFile.getAbsolutePath());
      }
   }

   @NotNull
   private File getEnvelopeFile(@NotNull SentryEnvelope envelope) {
      ISentryLifecycleToken ignored = this.cacheLock.acquire();

      File var4;
      try {
         String fileName;
         if (this.fileNameMap.containsKey(envelope)) {
            fileName = this.fileNameMap.get(envelope);
         } else {
            fileName = SentryUUID.generateSentryId() + ".envelope";
            this.fileNameMap.put(envelope, fileName);
         }

         var4 = new File(this.directory.getAbsolutePath(), fileName);
      } catch (Throwable var6) {
         if (ignored != null) {
            try {
               ignored.close();
            } catch (Throwable var5) {
               var6.addSuppressed(var5);
            }
         }

         throw var6;
      }

      if (ignored != null) {
         ignored.close();
      }

      return var4;
   }

   @NotNull
   public static File getCurrentSessionFile(@NotNull String cacheDirPath) {
      return new File(cacheDirPath, "session.json");
   }

   @NotNull
   public static File getPreviousSessionFile(@NotNull String cacheDirPath) {
      return new File(cacheDirPath, "previous_session.json");
   }

   @NotNull
   @Override
   public Iterator<SentryEnvelope> iterator() {
      File[] allCachedEnvelopes = this.allEnvelopeFiles();
      List<SentryEnvelope> ret = new ArrayList<>(allCachedEnvelopes.length);

      for (File file : allCachedEnvelopes) {
         try {
            InputStream is = new BufferedInputStream(new FileInputStream(file));

            try {
               ret.add(this.serializer.getValue().deserializeEnvelope(is));
            } catch (Throwable var11) {
               try {
                  is.close();
               } catch (Throwable var10) {
                  var11.addSuppressed(var10);
               }

               throw var11;
            }

            is.close();
         } catch (FileNotFoundException var12) {
            this.options
               .getLogger()
               .log(SentryLevel.DEBUG, "Envelope file '%s' disappeared while converting all cached files to envelopes.", file.getAbsolutePath());
         } catch (IOException var13) {
            this.options.getLogger().log(SentryLevel.ERROR, String.format("Error while reading cached envelope from file %s", file.getAbsolutePath()), var13);
         }
      }

      return ret.iterator();
   }

   @NotNull
   private File[] allEnvelopeFiles() {
      if (this.isDirectoryValid()) {
         File[] files = this.directory.listFiles((__, fileName) -> fileName.endsWith(".envelope"));
         if (files != null) {
            return files;
         }
      }

      return new File[0];
   }

   public boolean waitPreviousSessionFlush() {
      try {
         return this.previousSessionLatch.await(this.options.getSessionFlushTimeoutMillis(), TimeUnit.MILLISECONDS);
      } catch (InterruptedException var2) {
         Thread.currentThread().interrupt();
         this.options.getLogger().log(SentryLevel.DEBUG, "Timed out waiting for previous session to flush.");
         return false;
      }
   }

   public void flushPreviousSession() {
      this.previousSessionLatch.countDown();
   }

   public void movePreviousSession(@NotNull File currentSessionFile, @NotNull File previousSessionFile) {
      ISentryLifecycleToken ignored = this.sessionLock.acquire();

      try {
         if (previousSessionFile.exists()) {
            this.options.getLogger().log(SentryLevel.DEBUG, "Previous session file already exists, deleting it.");
            if (!previousSessionFile.delete()) {
               this.options.getLogger().log(SentryLevel.WARNING, "Unable to delete previous session file: %s", previousSessionFile);
            }
         }

         if (currentSessionFile.exists()) {
            this.options.getLogger().log(SentryLevel.INFO, "Moving current session to previous session.");

            try {
               boolean renamed = currentSessionFile.renameTo(previousSessionFile);
               if (!renamed) {
                  this.options.getLogger().log(SentryLevel.WARNING, "Unable to move current session to previous session.");
               }
            } catch (Throwable var7) {
               this.options.getLogger().log(SentryLevel.ERROR, "Error moving current session to previous session.", var7);
            }
         }
      } catch (Throwable var8) {
         if (ignored != null) {
            try {
               ignored.close();
            } catch (Throwable var6) {
               var8.addSuppressed(var6);
            }
         }

         throw var8;
      }

      if (ignored != null) {
         ignored.close();
      }
   }
}
