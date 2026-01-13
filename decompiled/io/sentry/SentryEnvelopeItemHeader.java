package io.sentry;

import io.sentry.util.Objects;
import io.sentry.vendor.gson.stream.JsonToken;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.ApiStatus.Internal;

@Internal
public final class SentryEnvelopeItemHeader implements JsonSerializable, JsonUnknown {
   @Nullable
   private final String contentType;
   @Nullable
   private final Integer itemCount;
   @Nullable
   private final String fileName;
   @Nullable
   private final String platform;
   @NotNull
   private final SentryItemType type;
   private final int length;
   @Nullable
   private final Callable<Integer> getLength;
   @Nullable
   private final String attachmentType;
   @Nullable
   private Map<String, Object> unknown;

   @NotNull
   public SentryItemType getType() {
      return this.type;
   }

   public int getLength() {
      if (this.getLength != null) {
         try {
            return this.getLength.call();
         } catch (Throwable var2) {
            return -1;
         }
      } else {
         return this.length;
      }
   }

   @Nullable
   public String getContentType() {
      return this.contentType;
   }

   @Nullable
   public String getFileName() {
      return this.fileName;
   }

   @Nullable
   public String getPlatform() {
      return this.platform;
   }

   @Internal
   public SentryEnvelopeItemHeader(
      @NotNull SentryItemType type,
      int length,
      @Nullable String contentType,
      @Nullable String fileName,
      @Nullable String attachmentType,
      @Nullable String platform,
      @Nullable Integer itemCount
   ) {
      this.type = Objects.requireNonNull(type, "type is required");
      this.contentType = contentType;
      this.length = length;
      this.fileName = fileName;
      this.getLength = null;
      this.attachmentType = attachmentType;
      this.platform = platform;
      this.itemCount = itemCount;
   }

   SentryEnvelopeItemHeader(
      @NotNull SentryItemType type,
      @Nullable Callable<Integer> getLength,
      @Nullable String contentType,
      @Nullable String fileName,
      @Nullable String attachmentType
   ) {
      this(type, getLength, contentType, fileName, attachmentType, null, null);
   }

   SentryEnvelopeItemHeader(
      @NotNull SentryItemType type,
      @Nullable Callable<Integer> getLength,
      @Nullable String contentType,
      @Nullable String fileName,
      @Nullable String attachmentType,
      @Nullable String platform,
      @Nullable Integer itemCount
   ) {
      this.type = Objects.requireNonNull(type, "type is required");
      this.contentType = contentType;
      this.length = -1;
      this.fileName = fileName;
      this.getLength = getLength;
      this.attachmentType = attachmentType;
      this.platform = platform;
      this.itemCount = itemCount;
   }

   SentryEnvelopeItemHeader(@NotNull SentryItemType type, @Nullable Callable<Integer> getLength, @Nullable String contentType, @Nullable String fileName) {
      this(type, getLength, contentType, fileName, null);
   }

   @Nullable
   public String getAttachmentType() {
      return this.attachmentType;
   }

   @Override
   public void serialize(@NotNull ObjectWriter writer, @NotNull ILogger logger) throws IOException {
      writer.beginObject();
      if (this.contentType != null) {
         writer.name("content_type").value(this.contentType);
      }

      if (this.fileName != null) {
         writer.name("filename").value(this.fileName);
      }

      writer.name("type").value(logger, this.type);
      if (this.attachmentType != null) {
         writer.name("attachment_type").value(this.attachmentType);
      }

      if (this.platform != null) {
         writer.name("platform").value(this.platform);
      }

      if (this.itemCount != null) {
         writer.name("item_count").value(this.itemCount);
      }

      writer.name("length").value((long)this.getLength());
      if (this.unknown != null) {
         for (String key : this.unknown.keySet()) {
            Object value = this.unknown.get(key);
            writer.name(key);
            writer.value(logger, value);
         }
      }

      writer.endObject();
   }

   @Nullable
   @Override
   public Map<String, Object> getUnknown() {
      return this.unknown;
   }

   @Override
   public void setUnknown(@Nullable Map<String, Object> unknown) {
      this.unknown = unknown;
   }

   public static final class Deserializer implements JsonDeserializer<SentryEnvelopeItemHeader> {
      @NotNull
      public SentryEnvelopeItemHeader deserialize(@NotNull ObjectReader reader, @NotNull ILogger logger) throws Exception {
         reader.beginObject();
         String contentType = null;
         String fileName = null;
         SentryItemType type = null;
         int length = 0;
         String attachmentType = null;
         String platform = null;
         Integer itemCount = null;
         Map<String, Object> unknown = null;

         while (reader.peek() == JsonToken.NAME) {
            String nextName = reader.nextName();
            switch (nextName) {
               case "content_type":
                  contentType = reader.nextStringOrNull();
                  break;
               case "filename":
                  fileName = reader.nextStringOrNull();
                  break;
               case "type":
                  type = reader.nextOrNull(logger, new SentryItemType.Deserializer());
                  break;
               case "length":
                  length = reader.nextInt();
                  break;
               case "attachment_type":
                  attachmentType = reader.nextStringOrNull();
                  break;
               case "platform":
                  platform = reader.nextStringOrNull();
                  break;
               case "item_count":
                  itemCount = reader.nextIntegerOrNull();
                  break;
               default:
                  if (unknown == null) {
                     unknown = new HashMap<>();
                  }

                  reader.nextUnknown(logger, unknown, nextName);
            }
         }

         if (type == null) {
            throw this.missingRequiredFieldException("type", logger);
         } else {
            SentryEnvelopeItemHeader sentryEnvelopeItemHeader = new SentryEnvelopeItemHeader(
               type, length, contentType, fileName, attachmentType, platform, itemCount
            );
            sentryEnvelopeItemHeader.setUnknown(unknown);
            reader.endObject();
            return sentryEnvelopeItemHeader;
         }
      }

      private Exception missingRequiredFieldException(String field, ILogger logger) {
         String message = "Missing required field \"" + field + "\"";
         Exception exception = new IllegalStateException(message);
         logger.log(SentryLevel.ERROR, message, exception);
         return exception;
      }
   }

   public static final class JsonKeys {
      public static final String CONTENT_TYPE = "content_type";
      public static final String FILENAME = "filename";
      public static final String TYPE = "type";
      public static final String ATTACHMENT_TYPE = "attachment_type";
      public static final String LENGTH = "length";
      public static final String PLATFORM = "platform";
      public static final String ITEM_COUNT = "item_count";
   }
}
