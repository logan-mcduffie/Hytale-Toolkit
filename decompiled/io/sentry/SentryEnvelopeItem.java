package io.sentry;

import io.sentry.clientreport.ClientReport;
import io.sentry.exception.SentryEnvelopeException;
import io.sentry.protocol.SentryTransaction;
import io.sentry.protocol.profiling.SentryProfile;
import io.sentry.util.FileUtils;
import io.sentry.util.JsonSerializationUtils;
import io.sentry.util.Objects;
import io.sentry.vendor.Base64;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.Charset;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.Callable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.ApiStatus.Internal;

@Internal
public final class SentryEnvelopeItem {
   private static final long MAX_PROFILE_CHUNK_SIZE = 52428800L;
   private static final Charset UTF_8 = Charset.forName("UTF-8");
   private final SentryEnvelopeItemHeader header;
   @Nullable
   private final Callable<byte[]> dataFactory;
   @Nullable
   private byte[] data;

   SentryEnvelopeItem(@NotNull SentryEnvelopeItemHeader header, byte[] data) {
      this.header = Objects.requireNonNull(header, "SentryEnvelopeItemHeader is required.");
      this.data = data;
      this.dataFactory = null;
   }

   SentryEnvelopeItem(@NotNull SentryEnvelopeItemHeader header, @Nullable Callable<byte[]> dataFactory) {
      this.header = Objects.requireNonNull(header, "SentryEnvelopeItemHeader is required.");
      this.dataFactory = Objects.requireNonNull(dataFactory, "DataFactory is required.");
      this.data = null;
   }

   @NotNull
   public byte[] getData() throws Exception {
      if (this.data == null && this.dataFactory != null) {
         this.data = this.dataFactory.call();
      }

      return this.data;
   }

   @NotNull
   public SentryEnvelopeItemHeader getHeader() {
      return this.header;
   }

   @NotNull
   public static SentryEnvelopeItem fromSession(@NotNull ISerializer serializer, @NotNull Session session) throws IOException {
      Objects.requireNonNull(serializer, "ISerializer is required.");
      Objects.requireNonNull(session, "Session is required.");
      SentryEnvelopeItem.CachedItem cachedItem = new SentryEnvelopeItem.CachedItem(() -> {
         ByteArrayOutputStream stream = new ByteArrayOutputStream();

         byte[] var4;
         try {
            Writer writer = new BufferedWriter(new OutputStreamWriter(stream, UTF_8));

            try {
               serializer.serialize(session, writer);
               var4 = stream.toByteArray();
            } catch (Throwable var8) {
               try {
                  writer.close();
               } catch (Throwable var7) {
                  var8.addSuppressed(var7);
               }

               throw var8;
            }

            writer.close();
         } catch (Throwable var9) {
            try {
               stream.close();
            } catch (Throwable var6) {
               var9.addSuppressed(var6);
            }

            throw var9;
         }

         stream.close();
         return var4;
      });
      SentryEnvelopeItemHeader itemHeader = new SentryEnvelopeItemHeader(SentryItemType.Session, () -> cachedItem.getBytes().length, "application/json", null);
      return new SentryEnvelopeItem(itemHeader, () -> cachedItem.getBytes());
   }

   @Nullable
   public SentryEvent getEvent(@NotNull ISerializer serializer) throws Exception {
      if (this.header != null && this.header.getType() == SentryItemType.Event) {
         Reader eventReader = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(this.getData()), UTF_8));

         SentryEvent var3;
         try {
            var3 = serializer.deserialize(eventReader, SentryEvent.class);
         } catch (Throwable var6) {
            try {
               eventReader.close();
            } catch (Throwable var5) {
               var6.addSuppressed(var5);
            }

            throw var6;
         }

         eventReader.close();
         return var3;
      } else {
         return null;
      }
   }

   @NotNull
   public static SentryEnvelopeItem fromEvent(@NotNull ISerializer serializer, @NotNull SentryBaseEvent event) {
      Objects.requireNonNull(serializer, "ISerializer is required.");
      Objects.requireNonNull(event, "SentryEvent is required.");
      SentryEnvelopeItem.CachedItem cachedItem = new SentryEnvelopeItem.CachedItem(() -> {
         ByteArrayOutputStream stream = new ByteArrayOutputStream();

         byte[] var4;
         try {
            Writer writer = new BufferedWriter(new OutputStreamWriter(stream, UTF_8));

            try {
               serializer.serialize(event, writer);
               var4 = stream.toByteArray();
            } catch (Throwable var8) {
               try {
                  writer.close();
               } catch (Throwable var7) {
                  var8.addSuppressed(var7);
               }

               throw var8;
            }

            writer.close();
         } catch (Throwable var9) {
            try {
               stream.close();
            } catch (Throwable var6) {
               var9.addSuppressed(var6);
            }

            throw var9;
         }

         stream.close();
         return var4;
      });
      SentryEnvelopeItemHeader itemHeader = new SentryEnvelopeItemHeader(
         SentryItemType.resolve(event), () -> cachedItem.getBytes().length, "application/json", null
      );
      return new SentryEnvelopeItem(itemHeader, () -> cachedItem.getBytes());
   }

   @Nullable
   public SentryTransaction getTransaction(@NotNull ISerializer serializer) throws Exception {
      if (this.header != null && this.header.getType() == SentryItemType.Transaction) {
         Reader eventReader = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(this.getData()), UTF_8));

         SentryTransaction var3;
         try {
            var3 = serializer.deserialize(eventReader, SentryTransaction.class);
         } catch (Throwable var6) {
            try {
               eventReader.close();
            } catch (Throwable var5) {
               var6.addSuppressed(var5);
            }

            throw var6;
         }

         eventReader.close();
         return var3;
      } else {
         return null;
      }
   }

   @Nullable
   public SentryLogEvents getLogs(@NotNull ISerializer serializer) throws Exception {
      if (this.header != null && this.header.getType() == SentryItemType.Log) {
         Reader eventReader = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(this.getData()), UTF_8));

         SentryLogEvents var3;
         try {
            var3 = serializer.deserialize(eventReader, SentryLogEvents.class);
         } catch (Throwable var6) {
            try {
               eventReader.close();
            } catch (Throwable var5) {
               var6.addSuppressed(var5);
            }

            throw var6;
         }

         eventReader.close();
         return var3;
      } else {
         return null;
      }
   }

   public static SentryEnvelopeItem fromUserFeedback(@NotNull ISerializer serializer, @NotNull UserFeedback userFeedback) {
      Objects.requireNonNull(serializer, "ISerializer is required.");
      Objects.requireNonNull(userFeedback, "UserFeedback is required.");
      SentryEnvelopeItem.CachedItem cachedItem = new SentryEnvelopeItem.CachedItem(() -> {
         ByteArrayOutputStream stream = new ByteArrayOutputStream();

         byte[] var4;
         try {
            Writer writer = new BufferedWriter(new OutputStreamWriter(stream, UTF_8));

            try {
               serializer.serialize(userFeedback, writer);
               var4 = stream.toByteArray();
            } catch (Throwable var8) {
               try {
                  writer.close();
               } catch (Throwable var7) {
                  var8.addSuppressed(var7);
               }

               throw var8;
            }

            writer.close();
         } catch (Throwable var9) {
            try {
               stream.close();
            } catch (Throwable var6) {
               var9.addSuppressed(var6);
            }

            throw var9;
         }

         stream.close();
         return var4;
      });
      SentryEnvelopeItemHeader itemHeader = new SentryEnvelopeItemHeader(
         SentryItemType.UserFeedback, () -> cachedItem.getBytes().length, "application/json", null
      );
      return new SentryEnvelopeItem(itemHeader, () -> cachedItem.getBytes());
   }

   public static SentryEnvelopeItem fromCheckIn(@NotNull ISerializer serializer, @NotNull CheckIn checkIn) {
      Objects.requireNonNull(serializer, "ISerializer is required.");
      Objects.requireNonNull(checkIn, "CheckIn is required.");
      SentryEnvelopeItem.CachedItem cachedItem = new SentryEnvelopeItem.CachedItem(() -> {
         ByteArrayOutputStream stream = new ByteArrayOutputStream();

         byte[] var4;
         try {
            Writer writer = new BufferedWriter(new OutputStreamWriter(stream, UTF_8));

            try {
               serializer.serialize(checkIn, writer);
               var4 = stream.toByteArray();
            } catch (Throwable var8) {
               try {
                  writer.close();
               } catch (Throwable var7) {
                  var8.addSuppressed(var7);
               }

               throw var8;
            }

            writer.close();
         } catch (Throwable var9) {
            try {
               stream.close();
            } catch (Throwable var6) {
               var9.addSuppressed(var6);
            }

            throw var9;
         }

         stream.close();
         return var4;
      });
      SentryEnvelopeItemHeader itemHeader = new SentryEnvelopeItemHeader(SentryItemType.CheckIn, () -> cachedItem.getBytes().length, "application/json", null);
      return new SentryEnvelopeItem(itemHeader, () -> cachedItem.getBytes());
   }

   public static SentryEnvelopeItem fromAttachment(
      @NotNull ISerializer serializer, @NotNull ILogger logger, @NotNull Attachment attachment, long maxAttachmentSize
   ) {
      SentryEnvelopeItem.CachedItem cachedItem = new SentryEnvelopeItem.CachedItem(
         () -> {
            if (attachment.getBytes() != null) {
               byte[] data = attachment.getBytes();
               ensureAttachmentSizeLimit(data.length, maxAttachmentSize, attachment.getFilename());
               return data;
            } else {
               if (attachment.getSerializable() != null) {
                  JsonSerializable serializable = attachment.getSerializable();
                  byte[] data = JsonSerializationUtils.bytesFrom(serializer, logger, serializable);
                  if (data != null) {
                     ensureAttachmentSizeLimit(data.length, maxAttachmentSize, attachment.getFilename());
                     return data;
                  }
               } else {
                  if (attachment.getPathname() != null) {
                     return FileUtils.readBytesFromFile(attachment.getPathname(), maxAttachmentSize);
                  }

                  if (attachment.getByteProvider() != null) {
                     byte[] data = attachment.getByteProvider().call();
                     if (data != null) {
                        ensureAttachmentSizeLimit(data.length, maxAttachmentSize, attachment.getFilename());
                        return data;
                     }
                  }
               }

               throw new SentryEnvelopeException(
                  String.format(
                     "Couldn't attach the attachment %s.\nPlease check that either bytes, serializable, path or provider is set.", attachment.getFilename()
                  )
               );
            }
         }
      );
      SentryEnvelopeItemHeader itemHeader = new SentryEnvelopeItemHeader(
         SentryItemType.Attachment, () -> cachedItem.getBytes().length, attachment.getContentType(), attachment.getFilename(), attachment.getAttachmentType()
      );
      return new SentryEnvelopeItem(itemHeader, () -> cachedItem.getBytes());
   }

   private static void ensureAttachmentSizeLimit(long size, long maxAttachmentSize, @NotNull String filename) throws SentryEnvelopeException {
      if (size > maxAttachmentSize) {
         throw new SentryEnvelopeException(
            String.format(
               "Dropping attachment with filename '%s', because the size of the passed bytes with %d bytes is bigger than the maximum allowed attachment size of %d bytes.",
               filename,
               size,
               maxAttachmentSize
            )
         );
      }
   }

   @NotNull
   public static SentryEnvelopeItem fromProfileChunk(@NotNull ProfileChunk profileChunk, @NotNull ISerializer serializer) throws SentryEnvelopeException {
      return fromProfileChunk(profileChunk, serializer, NoOpProfileConverter.getInstance());
   }

   @NotNull
   public static SentryEnvelopeItem fromProfileChunk(
      @NotNull ProfileChunk profileChunk, @NotNull ISerializer serializer, @NotNull IProfileConverter profileConverter
   ) throws SentryEnvelopeException {
      File traceFile = profileChunk.getTraceFile();
      SentryEnvelopeItem.CachedItem cachedItem = new SentryEnvelopeItem.CachedItem(() -> {
         if (!traceFile.exists()) {
            throw new SentryEnvelopeException(String.format("Dropping profile chunk, because the file '%s' doesn't exists", traceFile.getName()));
         } else {
            if ("java".equals(profileChunk.getPlatform())) {
               if (NoOpProfileConverter.getInstance().equals(profileConverter)) {
                  throw new SentryEnvelopeException("No ProfileConverter available, dropping chunk.");
               }

               try {
                  SentryProfile profile = profileConverter.convertFromFile(traceFile.getAbsolutePath());
                  profileChunk.setSentryProfile(profile);
               } catch (Exception var22) {
                  throw new SentryEnvelopeException("Profile conversion failed", var22);
               }
            } else {
               byte[] traceFileBytes = FileUtils.readBytesFromFile(traceFile.getPath(), 52428800L);
               String base64Trace = Base64.encodeToString(traceFileBytes, 3);
               if (base64Trace.isEmpty()) {
                  throw new SentryEnvelopeException("Profiling trace file is empty");
               }

               profileChunk.setSampledProfile(base64Trace);
            }

            byte[] var6;
            try {
               ByteArrayOutputStream stream = new ByteArrayOutputStream();

               try {
                  Writer writer = new BufferedWriter(new OutputStreamWriter(stream, UTF_8));

                  try {
                     serializer.serialize(profileChunk, writer);
                     var6 = stream.toByteArray();
                  } catch (Throwable var18) {
                     try {
                        writer.close();
                     } catch (Throwable var17) {
                        var18.addSuppressed(var17);
                     }

                     throw var18;
                  }

                  writer.close();
               } catch (Throwable var19) {
                  try {
                     stream.close();
                  } catch (Throwable var16) {
                     var19.addSuppressed(var16);
                  }

                  throw var19;
               }

               stream.close();
            } catch (IOException var20) {
               throw new SentryEnvelopeException(String.format("Failed to serialize profile chunk\n%s", var20.getMessage()));
            } finally {
               traceFile.delete();
            }

            return var6;
         }
      });
      SentryEnvelopeItemHeader itemHeader = new SentryEnvelopeItemHeader(
         SentryItemType.ProfileChunk, () -> cachedItem.getBytes().length, "application-json", traceFile.getName(), null, profileChunk.getPlatform(), null
      );
      return new SentryEnvelopeItem(itemHeader, () -> cachedItem.getBytes());
   }

   @NotNull
   public static SentryEnvelopeItem fromProfilingTrace(@NotNull ProfilingTraceData profilingTraceData, long maxTraceFileSize, @NotNull ISerializer serializer) throws SentryEnvelopeException {
      File traceFile = profilingTraceData.getTraceFile();
      SentryEnvelopeItem.CachedItem cachedItem = new SentryEnvelopeItem.CachedItem(() -> {
         if (!traceFile.exists()) {
            throw new SentryEnvelopeException(String.format("Dropping profiling trace data, because the file '%s' doesn't exists", traceFile.getName()));
         } else {
            byte[] traceFileBytes = FileUtils.readBytesFromFile(traceFile.getPath(), maxTraceFileSize);
            String base64Trace = Base64.encodeToString(traceFileBytes, 3);
            if (base64Trace.isEmpty()) {
               throw new SentryEnvelopeException("Profiling trace file is empty");
            } else {
               profilingTraceData.setSampledProfile(base64Trace);
               profilingTraceData.readDeviceCpuFrequencies();

               byte[] var9;
               try {
                  ByteArrayOutputStream stream = new ByteArrayOutputStream();

                  try {
                     Writer writer = new BufferedWriter(new OutputStreamWriter(stream, UTF_8));

                     try {
                        serializer.serialize(profilingTraceData, writer);
                        var9 = stream.toByteArray();
                     } catch (Throwable var20) {
                        try {
                           writer.close();
                        } catch (Throwable var19) {
                           var20.addSuppressed(var19);
                        }

                        throw var20;
                     }

                     writer.close();
                  } catch (Throwable var21) {
                     try {
                        stream.close();
                     } catch (Throwable var18) {
                        var21.addSuppressed(var18);
                     }

                     throw var21;
                  }

                  stream.close();
               } catch (IOException var22) {
                  throw new SentryEnvelopeException(String.format("Failed to serialize profiling trace data\n%s", var22.getMessage()));
               } finally {
                  traceFile.delete();
               }

               return var9;
            }
         }
      });
      SentryEnvelopeItemHeader itemHeader = new SentryEnvelopeItemHeader(
         SentryItemType.Profile, () -> cachedItem.getBytes().length, "application-json", traceFile.getName()
      );
      return new SentryEnvelopeItem(itemHeader, () -> cachedItem.getBytes());
   }

   @NotNull
   public static SentryEnvelopeItem fromClientReport(@NotNull ISerializer serializer, @NotNull ClientReport clientReport) throws IOException {
      Objects.requireNonNull(serializer, "ISerializer is required.");
      Objects.requireNonNull(clientReport, "ClientReport is required.");
      SentryEnvelopeItem.CachedItem cachedItem = new SentryEnvelopeItem.CachedItem(() -> {
         ByteArrayOutputStream stream = new ByteArrayOutputStream();

         byte[] var4;
         try {
            Writer writer = new BufferedWriter(new OutputStreamWriter(stream, UTF_8));

            try {
               serializer.serialize(clientReport, writer);
               var4 = stream.toByteArray();
            } catch (Throwable var8) {
               try {
                  writer.close();
               } catch (Throwable var7) {
                  var8.addSuppressed(var7);
               }

               throw var8;
            }

            writer.close();
         } catch (Throwable var9) {
            try {
               stream.close();
            } catch (Throwable var6) {
               var9.addSuppressed(var6);
            }

            throw var9;
         }

         stream.close();
         return var4;
      });
      SentryEnvelopeItemHeader itemHeader = new SentryEnvelopeItemHeader(
         SentryItemType.resolve(clientReport), () -> cachedItem.getBytes().length, "application/json", null
      );
      return new SentryEnvelopeItem(itemHeader, () -> cachedItem.getBytes());
   }

   @Nullable
   public ClientReport getClientReport(@NotNull ISerializer serializer) throws Exception {
      if (this.header != null && this.header.getType() == SentryItemType.ClientReport) {
         Reader eventReader = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(this.getData()), UTF_8));

         ClientReport var3;
         try {
            var3 = serializer.deserialize(eventReader, ClientReport.class);
         } catch (Throwable var6) {
            try {
               eventReader.close();
            } catch (Throwable var5) {
               var6.addSuppressed(var5);
            }

            throw var6;
         }

         eventReader.close();
         return var3;
      } else {
         return null;
      }
   }

   public static SentryEnvelopeItem fromReplay(
      @NotNull ISerializer serializer,
      @NotNull ILogger logger,
      @NotNull SentryReplayEvent replayEvent,
      @Nullable ReplayRecording replayRecording,
      boolean cleanupReplayFolder
   ) {
      File replayVideo = replayEvent.getVideoFile();
      SentryEnvelopeItem.CachedItem cachedItem = new SentryEnvelopeItem.CachedItem(() -> {
         Writer writer;
         try {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();

            byte[] videoBytes;
            try {
               writer = new BufferedWriter(new OutputStreamWriter(stream, UTF_8));

               try {
                  Map<String, byte[]> replayPayload = new LinkedHashMap<>();
                  serializer.serialize(replayEvent, writer);
                  replayPayload.put(SentryItemType.ReplayEvent.getItemType(), stream.toByteArray());
                  stream.reset();
                  if (replayRecording != null) {
                     serializer.serialize(replayRecording, writer);
                     replayPayload.put(SentryItemType.ReplayRecording.getItemType(), stream.toByteArray());
                     stream.reset();
                  }

                  if (replayVideo != null && replayVideo.exists()) {
                     videoBytes = FileUtils.readBytesFromFile(replayVideo.getPath(), 10485760L);
                     if (videoBytes.length > 0) {
                        replayPayload.put(SentryItemType.ReplayVideo.getItemType(), videoBytes);
                     }
                  }

                  videoBytes = serializeToMsgpack(replayPayload);
               } catch (Throwable var19) {
                  try {
                     writer.close();
                  } catch (Throwable var18) {
                     var19.addSuppressed(var18);
                  }

                  throw var19;
               }

               writer.close();
            } catch (Throwable var20) {
               try {
                  stream.close();
               } catch (Throwable var17) {
                  var20.addSuppressed(var17);
               }

               throw var20;
            }

            stream.close();
            return videoBytes;
         } catch (Throwable var21) {
            logger.log(SentryLevel.ERROR, "Could not serialize replay recording", var21);
            writer = null;
         } finally {
            if (replayVideo != null) {
               if (cleanupReplayFolder) {
                  FileUtils.deleteRecursively(replayVideo.getParentFile());
               } else {
                  replayVideo.delete();
               }
            }
         }

         return (byte[])writer;
      });
      SentryEnvelopeItemHeader itemHeader = new SentryEnvelopeItemHeader(SentryItemType.ReplayVideo, () -> cachedItem.getBytes().length, null, null);
      return new SentryEnvelopeItem(itemHeader, () -> cachedItem.getBytes());
   }

   public static SentryEnvelopeItem fromLogs(@NotNull ISerializer serializer, @NotNull SentryLogEvents logEvents) {
      Objects.requireNonNull(serializer, "ISerializer is required.");
      Objects.requireNonNull(logEvents, "SentryLogEvents is required.");
      SentryEnvelopeItem.CachedItem cachedItem = new SentryEnvelopeItem.CachedItem(() -> {
         ByteArrayOutputStream stream = new ByteArrayOutputStream();

         byte[] var4;
         try {
            Writer writer = new BufferedWriter(new OutputStreamWriter(stream, UTF_8));

            try {
               serializer.serialize(logEvents, writer);
               var4 = stream.toByteArray();
            } catch (Throwable var8) {
               try {
                  writer.close();
               } catch (Throwable var7) {
                  var8.addSuppressed(var7);
               }

               throw var8;
            }

            writer.close();
         } catch (Throwable var9) {
            try {
               stream.close();
            } catch (Throwable var6) {
               var9.addSuppressed(var6);
            }

            throw var9;
         }

         stream.close();
         return var4;
      });
      SentryEnvelopeItemHeader itemHeader = new SentryEnvelopeItemHeader(
         SentryItemType.Log, () -> cachedItem.getBytes().length, "application/vnd.sentry.items.log+json", null, null, null, logEvents.getItems().size()
      );
      return new SentryEnvelopeItem(itemHeader, () -> cachedItem.getBytes());
   }

   private static byte[] serializeToMsgpack(@NotNull Map<String, byte[]> map) throws IOException {
      ByteArrayOutputStream baos = new ByteArrayOutputStream();

      byte[] var10;
      try {
         baos.write((byte)(128 | map.size()));

         for (Entry<String, byte[]> entry : map.entrySet()) {
            byte[] keyBytes = entry.getKey().getBytes(UTF_8);
            int keyLength = keyBytes.length;
            baos.write(-39);
            baos.write((byte)keyLength);
            baos.write(keyBytes);
            byte[] valueBytes = entry.getValue();
            int valueLength = valueBytes.length;
            baos.write(-58);
            baos.write(ByteBuffer.allocate(4).order(ByteOrder.BIG_ENDIAN).putInt(valueLength).array());
            baos.write(valueBytes);
         }

         var10 = baos.toByteArray();
      } catch (Throwable var9) {
         try {
            baos.close();
         } catch (Throwable var8) {
            var9.addSuppressed(var8);
         }

         throw var9;
      }

      baos.close();
      return var10;
   }

   private static class CachedItem {
      @Nullable
      private byte[] bytes;
      @Nullable
      private final Callable<byte[]> dataFactory;

      public CachedItem(@Nullable Callable<byte[]> dataFactory) {
         this.dataFactory = dataFactory;
      }

      @NotNull
      public byte[] getBytes() throws Exception {
         if (this.bytes == null && this.dataFactory != null) {
            this.bytes = this.dataFactory.call();
         }

         return orEmptyArray(this.bytes);
      }

      @NotNull
      private static byte[] orEmptyArray(@Nullable byte[] bytes) {
         return bytes != null ? bytes : new byte[0];
      }
   }
}
