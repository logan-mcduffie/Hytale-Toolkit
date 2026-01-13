package io.sentry.cache;

import io.sentry.ISerializer;
import io.sentry.SentryEnvelope;
import io.sentry.SentryEnvelopeItem;
import io.sentry.SentryItemType;
import io.sentry.SentryLevel;
import io.sentry.SentryOptions;
import io.sentry.Session;
import io.sentry.clientreport.DiscardReason;
import io.sentry.util.LazyEvaluator;
import io.sentry.util.Objects;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

abstract class CacheStrategy {
   protected static final Charset UTF_8 = Charset.forName("UTF-8");
   @NotNull
   protected SentryOptions options;
   @NotNull
   protected final LazyEvaluator<ISerializer> serializer = new LazyEvaluator<>(() -> this.options.getSerializer());
   @NotNull
   protected final File directory;
   private final int maxSize;

   CacheStrategy(@NotNull SentryOptions options, @NotNull String directoryPath, int maxSize) {
      Objects.requireNonNull(directoryPath, "Directory is required.");
      this.options = Objects.requireNonNull(options, "SentryOptions is required.");
      this.directory = new File(directoryPath);
      this.maxSize = maxSize;
   }

   protected boolean isDirectoryValid() {
      if (this.directory.isDirectory() && this.directory.canWrite() && this.directory.canRead()) {
         return true;
      } else {
         this.options.getLogger().log(SentryLevel.ERROR, "The directory for caching files is inaccessible.: %s", this.directory.getAbsolutePath());
         return false;
      }
   }

   private void sortFilesOldestToNewest(@NotNull File[] files) {
      if (files.length > 1) {
         Arrays.sort(files, (f1, f2) -> Long.compare(f1.lastModified(), f2.lastModified()));
      }
   }

   protected void rotateCacheIfNeeded(@NotNull File[] files) {
      int length = files.length;
      if (length >= this.maxSize) {
         this.options.getLogger().log(SentryLevel.WARNING, "Cache folder if full (respecting maxSize). Rotating files");
         int totalToBeDeleted = length - this.maxSize + 1;
         this.sortFilesOldestToNewest(files);
         File[] notDeletedFiles = Arrays.copyOfRange(files, totalToBeDeleted, length);

         for (int i = 0; i < totalToBeDeleted; i++) {
            File file = files[i];
            this.moveInitFlagIfNecessary(file, notDeletedFiles);
            if (!file.delete()) {
               this.options.getLogger().log(SentryLevel.WARNING, "File can't be deleted: %s", file.getAbsolutePath());
            }
         }
      }
   }

   private void moveInitFlagIfNecessary(@NotNull File currentFile, @NotNull File[] notDeletedFiles) {
      SentryEnvelope currentEnvelope = this.readEnvelope(currentFile);
      if (currentEnvelope != null && this.isValidEnvelope(currentEnvelope)) {
         this.options.getClientReportRecorder().recordLostEnvelope(DiscardReason.CACHE_OVERFLOW, currentEnvelope);
         Session currentSession = this.getFirstSession(currentEnvelope);
         if (currentSession != null && this.isValidSession(currentSession)) {
            Boolean currentSessionInit = currentSession.getInit();
            if (currentSessionInit != null && currentSessionInit) {
               for (File notDeletedFile : notDeletedFiles) {
                  SentryEnvelope envelope = this.readEnvelope(notDeletedFile);
                  if (envelope != null && this.isValidEnvelope(envelope)) {
                     SentryEnvelopeItem newSessionItem = null;
                     Iterator<SentryEnvelopeItem> itemsIterator = envelope.getItems().iterator();

                     while (itemsIterator.hasNext()) {
                        SentryEnvelopeItem envelopeItem = itemsIterator.next();
                        if (this.isSessionType(envelopeItem)) {
                           Session session = this.readSession(envelopeItem);
                           if (session != null && this.isValidSession(session)) {
                              Boolean init = session.getInit();
                              if (init != null && init) {
                                 this.options.getLogger().log(SentryLevel.ERROR, "Session %s has 2 times the init flag.", currentSession.getSessionId());
                                 return;
                              }

                              if (currentSession.getSessionId() != null && currentSession.getSessionId().equals(session.getSessionId())) {
                                 session.setInitAsTrue();

                                 try {
                                    newSessionItem = SentryEnvelopeItem.fromSession(this.serializer.getValue(), session);
                                    itemsIterator.remove();
                                 } catch (IOException var17) {
                                    this.options
                                       .getLogger()
                                       .log(SentryLevel.ERROR, var17, "Failed to create new envelope item for the session %s", currentSession.getSessionId());
                                 }
                                 break;
                              }
                           }
                        }
                     }

                     if (newSessionItem != null) {
                        SentryEnvelope newEnvelope = this.buildNewEnvelope(envelope, newSessionItem);
                        long notDeletedFileTimestamp = notDeletedFile.lastModified();
                        if (!notDeletedFile.delete()) {
                           this.options.getLogger().log(SentryLevel.WARNING, "File can't be deleted: %s", notDeletedFile.getAbsolutePath());
                        }

                        this.saveNewEnvelope(newEnvelope, notDeletedFile, notDeletedFileTimestamp);
                        break;
                     }
                  }
               }
            }
         }
      }
   }

   @Nullable
   private SentryEnvelope readEnvelope(@NotNull File file) {
      try {
         InputStream inputStream = new BufferedInputStream(new FileInputStream(file));

         SentryEnvelope var3;
         try {
            var3 = this.serializer.getValue().deserializeEnvelope(inputStream);
         } catch (Throwable var6) {
            try {
               inputStream.close();
            } catch (Throwable var5) {
               var6.addSuppressed(var5);
            }

            throw var6;
         }

         inputStream.close();
         return var3;
      } catch (IOException var7) {
         this.options.getLogger().log(SentryLevel.ERROR, "Failed to deserialize the envelope.", var7);
         return null;
      }
   }

   @Nullable
   private Session getFirstSession(@NotNull SentryEnvelope envelope) {
      for (SentryEnvelopeItem item : envelope.getItems()) {
         if (this.isSessionType(item)) {
            return this.readSession(item);
         }
      }

      return null;
   }

   private boolean isValidSession(@NotNull Session session) {
      if (!session.getStatus().equals(Session.State.Ok)) {
         return false;
      } else {
         String sessionId = session.getSessionId();
         return sessionId != null;
      }
   }

   private boolean isSessionType(@Nullable SentryEnvelopeItem item) {
      return item == null ? false : item.getHeader().getType().equals(SentryItemType.Session);
   }

   @Nullable
   private Session readSession(@NotNull SentryEnvelopeItem item) {
      try {
         Reader reader = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(item.getData()), UTF_8));

         Session var3;
         try {
            var3 = this.serializer.getValue().deserialize(reader, Session.class);
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
         this.options.getLogger().log(SentryLevel.ERROR, "Failed to deserialize the session.", var7);
         return null;
      }
   }

   private void saveNewEnvelope(@NotNull SentryEnvelope envelope, @NotNull File file, long timestamp) {
      try {
         OutputStream outputStream = new FileOutputStream(file);

         try {
            this.serializer.getValue().serialize(envelope, outputStream);
            file.setLastModified(timestamp);
         } catch (Throwable var9) {
            try {
               outputStream.close();
            } catch (Throwable var8) {
               var9.addSuppressed(var8);
            }

            throw var9;
         }

         outputStream.close();
      } catch (Throwable var10) {
         this.options.getLogger().log(SentryLevel.ERROR, "Failed to serialize the new envelope to the disk.", var10);
      }
   }

   @NotNull
   private SentryEnvelope buildNewEnvelope(@NotNull SentryEnvelope envelope, @NotNull SentryEnvelopeItem sessionItem) {
      List<SentryEnvelopeItem> newEnvelopeItems = new ArrayList<>();

      for (SentryEnvelopeItem newEnvelopeItem : envelope.getItems()) {
         newEnvelopeItems.add(newEnvelopeItem);
      }

      newEnvelopeItems.add(sessionItem);
      return new SentryEnvelope(envelope.getHeader(), newEnvelopeItems);
   }

   private boolean isValidEnvelope(@NotNull SentryEnvelope envelope) {
      return envelope.getItems().iterator().hasNext();
   }
}
