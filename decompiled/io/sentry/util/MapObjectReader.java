package io.sentry.util;

import io.sentry.ILogger;
import io.sentry.JsonDeserializer;
import io.sentry.ObjectReader;
import io.sentry.SentryLevel;
import io.sentry.vendor.gson.stream.JsonToken;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Date;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.AbstractMap.SimpleEntry;
import java.util.Map.Entry;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class MapObjectReader implements ObjectReader {
   private final Deque<Entry<String, Object>> stack = new ArrayDeque<>();

   public MapObjectReader(Map<String, Object> root) {
      this.stack.addLast(new SimpleEntry<>(null, root));
   }

   @Override
   public void nextUnknown(@NotNull ILogger logger, Map<String, Object> unknown, String name) {
      try {
         unknown.put(name, this.nextObjectOrNull());
      } catch (Exception var5) {
         logger.log(SentryLevel.ERROR, var5, "Error deserializing unknown key: %s", name);
      }
   }

   @Nullable
   @Override
   public <T> List<T> nextListOrNull(@NotNull ILogger logger, @NotNull JsonDeserializer<T> deserializer) throws IOException {
      if (this.peek() == JsonToken.NULL) {
         this.nextNull();
         return null;
      } else {
         try {
            this.beginArray();
            List<T> list = new ArrayList<>();
            if (this.hasNext()) {
               do {
                  try {
                     list.add(deserializer.deserialize(this, logger));
                  } catch (Exception var5) {
                     logger.log(SentryLevel.WARNING, "Failed to deserialize object in list.", var5);
                  }
               } while (this.peek() == JsonToken.BEGIN_OBJECT);
            }

            this.endArray();
            return list;
         } catch (Exception var6) {
            throw new IOException(var6);
         }
      }
   }

   @Nullable
   @Override
   public <T> Map<String, T> nextMapOrNull(@NotNull ILogger logger, @NotNull JsonDeserializer<T> deserializer) throws IOException {
      if (this.peek() == JsonToken.NULL) {
         this.nextNull();
         return null;
      } else {
         try {
            this.beginObject();
            Map<String, T> map = new HashMap<>();
            if (this.hasNext()) {
               do {
                  try {
                     String key = this.nextName();
                     map.put(key, deserializer.deserialize(this, logger));
                  } catch (Exception var5) {
                     logger.log(SentryLevel.WARNING, "Failed to deserialize object in map.", var5);
                  }
               } while (this.peek() == JsonToken.BEGIN_OBJECT || this.peek() == JsonToken.NAME);
            }

            this.endObject();
            return map;
         } catch (Exception var6) {
            throw new IOException(var6);
         }
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

         try {
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
         } catch (Exception var6) {
            throw new IOException(var6);
         }
      }
   }

   @Nullable
   @Override
   public <T> T nextOrNull(@NotNull ILogger logger, @NotNull JsonDeserializer<T> deserializer) throws Exception {
      return this.nextValueOrNull(logger, deserializer);
   }

   @Nullable
   @Override
   public Date nextDateOrNull(@NotNull ILogger logger) throws IOException {
      String dateString = this.nextStringOrNull();
      return ObjectReader.dateOrNull(dateString, logger);
   }

   @Nullable
   @Override
   public TimeZone nextTimeZoneOrNull(@NotNull ILogger logger) throws IOException {
      String timeZoneId = this.nextStringOrNull();
      return timeZoneId != null ? TimeZone.getTimeZone(timeZoneId) : null;
   }

   @Nullable
   @Override
   public Object nextObjectOrNull() throws IOException {
      return this.nextValueOrNull();
   }

   @NotNull
   @Override
   public JsonToken peek() throws IOException {
      if (this.stack.isEmpty()) {
         return JsonToken.END_DOCUMENT;
      } else {
         Entry<String, Object> currentEntry = this.stack.peekLast();
         if (currentEntry == null) {
            return JsonToken.END_DOCUMENT;
         } else if (currentEntry.getKey() != null) {
            return JsonToken.NAME;
         } else {
            Object value = currentEntry.getValue();
            if (value instanceof Map) {
               return JsonToken.BEGIN_OBJECT;
            } else if (value instanceof List) {
               return JsonToken.BEGIN_ARRAY;
            } else if (value instanceof String) {
               return JsonToken.STRING;
            } else if (value instanceof Number) {
               return JsonToken.NUMBER;
            } else if (value instanceof Boolean) {
               return JsonToken.BOOLEAN;
            } else {
               return value instanceof JsonToken ? (JsonToken)value : JsonToken.END_DOCUMENT;
            }
         }
      }
   }

   @NotNull
   @Override
   public String nextName() throws IOException {
      Entry<String, Object> currentEntry = this.stack.peekLast();
      if (currentEntry != null && currentEntry.getKey() != null) {
         return currentEntry.getKey();
      } else {
         throw new IOException("Expected a name but was " + this.peek());
      }
   }

   @Override
   public void beginObject() throws IOException {
      Entry<String, Object> currentEntry = this.stack.removeLast();
      if (currentEntry == null) {
         throw new IOException("No more entries");
      } else {
         Object value = currentEntry.getValue();
         if (!(value instanceof Map)) {
            throw new IOException("Current token is not an object");
         } else {
            this.stack.addLast(new SimpleEntry<>(null, JsonToken.END_OBJECT));

            for (Entry<String, Object> entry : ((Map)value).entrySet()) {
               this.stack.addLast(entry);
            }
         }
      }
   }

   @Override
   public void endObject() throws IOException {
      if (this.stack.size() > 1) {
         this.stack.removeLast();
      }
   }

   @Override
   public void beginArray() throws IOException {
      Entry<String, Object> currentEntry = this.stack.removeLast();
      if (currentEntry == null) {
         throw new IOException("No more entries");
      } else {
         Object value = currentEntry.getValue();
         if (!(value instanceof List)) {
            throw new IOException("Current token is not an object");
         } else {
            this.stack.addLast(new SimpleEntry<>(null, JsonToken.END_ARRAY));

            for (int i = ((List)value).size() - 1; i >= 0; i--) {
               Object entry = ((List)value).get(i);
               this.stack.addLast(new SimpleEntry<>(null, entry));
            }
         }
      }
   }

   @Override
   public void endArray() throws IOException {
      if (this.stack.size() > 1) {
         this.stack.removeLast();
      }
   }

   @Override
   public boolean hasNext() throws IOException {
      return !this.stack.isEmpty();
   }

   @Override
   public int nextInt() throws IOException {
      Object value = this.nextValueOrNull();
      if (value instanceof Number) {
         return ((Number)value).intValue();
      } else {
         throw new IOException("Expected int");
      }
   }

   @Nullable
   @Override
   public Integer nextIntegerOrNull() throws IOException {
      Object value = this.nextValueOrNull();
      return value instanceof Number ? ((Number)value).intValue() : null;
   }

   @Override
   public long nextLong() throws IOException {
      Object value = this.nextValueOrNull();
      if (value instanceof Number) {
         return ((Number)value).longValue();
      } else {
         throw new IOException("Expected long");
      }
   }

   @Nullable
   @Override
   public Long nextLongOrNull() throws IOException {
      Object value = this.nextValueOrNull();
      return value instanceof Number ? ((Number)value).longValue() : null;
   }

   @Override
   public String nextString() throws IOException {
      String value = this.nextValueOrNull();
      if (value != null) {
         return value;
      } else {
         throw new IOException("Expected string");
      }
   }

   @Nullable
   @Override
   public String nextStringOrNull() throws IOException {
      return this.nextValueOrNull();
   }

   @Override
   public boolean nextBoolean() throws IOException {
      Boolean value = this.nextValueOrNull();
      if (value != null) {
         return value;
      } else {
         throw new IOException("Expected boolean");
      }
   }

   @Nullable
   @Override
   public Boolean nextBooleanOrNull() throws IOException {
      return this.nextValueOrNull();
   }

   @Override
   public double nextDouble() throws IOException {
      Object value = this.nextValueOrNull();
      if (value instanceof Number) {
         return ((Number)value).doubleValue();
      } else {
         throw new IOException("Expected double");
      }
   }

   @Nullable
   @Override
   public Double nextDoubleOrNull() throws IOException {
      Object value = this.nextValueOrNull();
      return value instanceof Number ? ((Number)value).doubleValue() : null;
   }

   @Nullable
   @Override
   public Float nextFloatOrNull() throws IOException {
      Object value = this.nextValueOrNull();
      return value instanceof Number ? ((Number)value).floatValue() : null;
   }

   @Override
   public float nextFloat() throws IOException {
      Object value = this.nextValueOrNull();
      if (value instanceof Number) {
         return ((Number)value).floatValue();
      } else {
         throw new IOException("Expected float");
      }
   }

   @Override
   public void nextNull() throws IOException {
      Object value = this.nextValueOrNull();
      if (value != null) {
         throw new IOException("Expected null but was " + this.peek());
      }
   }

   @Override
   public void setLenient(boolean lenient) {
   }

   @Override
   public void skipValue() throws IOException {
   }

   @Nullable
   private <T> T nextValueOrNull() throws IOException {
      try {
         return this.nextValueOrNull(null, null);
      } catch (Exception var2) {
         throw new IOException(var2);
      }
   }

   @Nullable
   private <T> T nextValueOrNull(@Nullable ILogger logger, @Nullable JsonDeserializer<T> deserializer) throws Exception {
      Entry<String, Object> currentEntry = this.stack.peekLast();
      if (currentEntry == null) {
         return null;
      } else {
         T value = (T)currentEntry.getValue();
         if (deserializer != null && logger != null) {
            return deserializer.deserialize(this, logger);
         } else {
            this.stack.removeLast();
            return value;
         }
      }
   }

   @Override
   public void close() throws IOException {
      this.stack.clear();
   }
}
