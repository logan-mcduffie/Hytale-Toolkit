package io.sentry;

import io.sentry.vendor.gson.stream.JsonReader;
import io.sentry.vendor.gson.stream.JsonToken;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.ApiStatus.Internal;

@Internal
public final class JsonObjectReader implements ObjectReader {
   @NotNull
   private final JsonReader jsonReader;

   public JsonObjectReader(Reader in) {
      this.jsonReader = new JsonReader(in);
   }

   @Nullable
   @Override
   public String nextStringOrNull() throws IOException {
      if (this.jsonReader.peek() == JsonToken.NULL) {
         this.jsonReader.nextNull();
         return null;
      } else {
         return this.jsonReader.nextString();
      }
   }

   @Nullable
   @Override
   public Double nextDoubleOrNull() throws IOException {
      if (this.jsonReader.peek() == JsonToken.NULL) {
         this.jsonReader.nextNull();
         return null;
      } else {
         return this.jsonReader.nextDouble();
      }
   }

   @Nullable
   @Override
   public Float nextFloatOrNull() throws IOException {
      if (this.jsonReader.peek() == JsonToken.NULL) {
         this.jsonReader.nextNull();
         return null;
      } else {
         return this.nextFloat();
      }
   }

   @Override
   public float nextFloat() throws IOException {
      return (float)this.jsonReader.nextDouble();
   }

   @Nullable
   @Override
   public Long nextLongOrNull() throws IOException {
      if (this.jsonReader.peek() == JsonToken.NULL) {
         this.jsonReader.nextNull();
         return null;
      } else {
         return this.jsonReader.nextLong();
      }
   }

   @Nullable
   @Override
   public Integer nextIntegerOrNull() throws IOException {
      if (this.jsonReader.peek() == JsonToken.NULL) {
         this.jsonReader.nextNull();
         return null;
      } else {
         return this.jsonReader.nextInt();
      }
   }

   @Nullable
   @Override
   public Boolean nextBooleanOrNull() throws IOException {
      if (this.jsonReader.peek() == JsonToken.NULL) {
         this.jsonReader.nextNull();
         return null;
      } else {
         return this.jsonReader.nextBoolean();
      }
   }

   @Override
   public void nextUnknown(ILogger logger, Map<String, Object> unknown, String name) {
      try {
         unknown.put(name, this.nextObjectOrNull());
      } catch (Exception var5) {
         logger.log(SentryLevel.ERROR, var5, "Error deserializing unknown key: %s", name);
      }
   }

   @Nullable
   @Override
   public <T> List<T> nextListOrNull(@NotNull ILogger logger, @NotNull JsonDeserializer<T> deserializer) throws IOException {
      if (this.jsonReader.peek() == JsonToken.NULL) {
         this.jsonReader.nextNull();
         return null;
      } else {
         this.jsonReader.beginArray();
         List<T> list = new ArrayList<>();
         if (this.jsonReader.hasNext()) {
            do {
               try {
                  list.add(deserializer.deserialize(this, logger));
               } catch (Exception var5) {
                  logger.log(SentryLevel.WARNING, "Failed to deserialize object in list.", var5);
               }
            } while (this.jsonReader.peek() == JsonToken.BEGIN_OBJECT);
         }

         this.jsonReader.endArray();
         return list;
      }
   }

   @Nullable
   @Override
   public <T> Map<String, T> nextMapOrNull(@NotNull ILogger logger, @NotNull JsonDeserializer<T> deserializer) throws IOException {
      if (this.jsonReader.peek() == JsonToken.NULL) {
         this.jsonReader.nextNull();
         return null;
      } else {
         this.jsonReader.beginObject();
         Map<String, T> map = new HashMap<>();
         if (this.jsonReader.hasNext()) {
            do {
               try {
                  String key = this.jsonReader.nextName();
                  map.put(key, deserializer.deserialize(this, logger));
               } catch (Exception var5) {
                  logger.log(SentryLevel.WARNING, "Failed to deserialize object in map.", var5);
               }
            } while (this.jsonReader.peek() == JsonToken.BEGIN_OBJECT || this.jsonReader.peek() == JsonToken.NAME);
         }

         this.jsonReader.endObject();
         return map;
      }
   }

   @Nullable
   @Override
   public <T> Map<String, List<T>> nextMapOfListOrNull(@NotNull ILogger logger, @NotNull JsonDeserializer<T> deserializer) throws IOException {
      if (this.peek() == JsonToken.NULL) {
         this.nextNull();
         return null;
      } else {
         Map<String, List<T>> result = new HashMap<>();
         this.beginObject();
         if (this.hasNext()) {
            do {
               String key = this.nextName();
               List<T> list = this.nextListOrNull(logger, deserializer);
               if (list != null) {
                  result.put(key, list);
               }
            } while (this.peek() == JsonToken.BEGIN_OBJECT || this.peek() == JsonToken.NAME);
         }

         this.endObject();
         return result;
      }
   }

   @Nullable
   @Override
   public <T> T nextOrNull(@NotNull ILogger logger, @NotNull JsonDeserializer<T> deserializer) throws Exception {
      if (this.jsonReader.peek() == JsonToken.NULL) {
         this.jsonReader.nextNull();
         return null;
      } else {
         return deserializer.deserialize(this, logger);
      }
   }

   @Nullable
   @Override
   public Date nextDateOrNull(ILogger logger) throws IOException {
      if (this.jsonReader.peek() == JsonToken.NULL) {
         this.jsonReader.nextNull();
         return null;
      } else {
         return ObjectReader.dateOrNull(this.jsonReader.nextString(), logger);
      }
   }

   @Nullable
   @Override
   public TimeZone nextTimeZoneOrNull(ILogger logger) throws IOException {
      if (this.jsonReader.peek() == JsonToken.NULL) {
         this.jsonReader.nextNull();
         return null;
      } else {
         try {
            return TimeZone.getTimeZone(this.jsonReader.nextString());
         } catch (Exception var3) {
            logger.log(SentryLevel.ERROR, "Error when deserializing TimeZone", var3);
            return null;
         }
      }
   }

   @Nullable
   @Override
   public Object nextObjectOrNull() throws IOException {
      return new JsonObjectDeserializer().deserialize(this);
   }

   @NotNull
   @Override
   public JsonToken peek() throws IOException {
      return this.jsonReader.peek();
   }

   @NotNull
   @Override
   public String nextName() throws IOException {
      return this.jsonReader.nextName();
   }

   @Override
   public void beginObject() throws IOException {
      this.jsonReader.beginObject();
   }

   @Override
   public void endObject() throws IOException {
      this.jsonReader.endObject();
   }

   @Override
   public void beginArray() throws IOException {
      this.jsonReader.beginArray();
   }

   @Override
   public void endArray() throws IOException {
      this.jsonReader.endArray();
   }

   @Override
   public boolean hasNext() throws IOException {
      return this.jsonReader.hasNext();
   }

   @Override
   public int nextInt() throws IOException {
      return this.jsonReader.nextInt();
   }

   @Override
   public long nextLong() throws IOException {
      return this.jsonReader.nextLong();
   }

   @Override
   public String nextString() throws IOException {
      return this.jsonReader.nextString();
   }

   @Override
   public boolean nextBoolean() throws IOException {
      return this.jsonReader.nextBoolean();
   }

   @Override
   public double nextDouble() throws IOException {
      return this.jsonReader.nextDouble();
   }

   @Override
   public void nextNull() throws IOException {
      this.jsonReader.nextNull();
   }

   @Override
   public void setLenient(boolean lenient) {
      this.jsonReader.setLenient(lenient);
   }

   @Override
   public void skipValue() throws IOException {
      this.jsonReader.skipValue();
   }

   @Override
   public void close() throws IOException {
      this.jsonReader.close();
   }
}
