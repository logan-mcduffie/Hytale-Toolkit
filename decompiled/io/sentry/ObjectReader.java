package io.sentry;

import io.sentry.vendor.gson.stream.JsonToken;
import java.io.Closeable;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface ObjectReader extends Closeable {
   @Nullable
   static Date dateOrNull(@Nullable String dateString, @NotNull ILogger logger) {
      if (dateString == null) {
         return null;
      } else {
         try {
            return DateUtils.getDateTime(dateString);
         } catch (Exception var5) {
            try {
               return DateUtils.getDateTimeWithMillisPrecision(dateString);
            } catch (Exception var4) {
               logger.log(SentryLevel.ERROR, "Error when deserializing millis timestamp format.", var4);
               return null;
            }
         }
      }
   }

   void nextUnknown(ILogger var1, Map<String, Object> var2, String var3);

   @Nullable
   <T> List<T> nextListOrNull(@NotNull ILogger var1, @NotNull JsonDeserializer<T> var2) throws IOException;

   @Nullable
   <T> Map<String, T> nextMapOrNull(@NotNull ILogger var1, @NotNull JsonDeserializer<T> var2) throws IOException;

   @Nullable
   <T> Map<String, List<T>> nextMapOfListOrNull(@NotNull ILogger var1, @NotNull JsonDeserializer<T> var2) throws IOException;

   @Nullable
   <T> T nextOrNull(@NotNull ILogger var1, @NotNull JsonDeserializer<T> var2) throws Exception;

   @Nullable
   Date nextDateOrNull(ILogger var1) throws IOException;

   @Nullable
   TimeZone nextTimeZoneOrNull(ILogger var1) throws IOException;

   @Nullable
   Object nextObjectOrNull() throws IOException;

   @NotNull
   JsonToken peek() throws IOException;

   @NotNull
   String nextName() throws IOException;

   void beginObject() throws IOException;

   void endObject() throws IOException;

   void beginArray() throws IOException;

   void endArray() throws IOException;

   boolean hasNext() throws IOException;

   int nextInt() throws IOException;

   @Nullable
   Integer nextIntegerOrNull() throws IOException;

   long nextLong() throws IOException;

   @Nullable
   Long nextLongOrNull() throws IOException;

   String nextString() throws IOException;

   @Nullable
   String nextStringOrNull() throws IOException;

   boolean nextBoolean() throws IOException;

   @Nullable
   Boolean nextBooleanOrNull() throws IOException;

   double nextDouble() throws IOException;

   @Nullable
   Double nextDoubleOrNull() throws IOException;

   float nextFloat() throws IOException;

   @Nullable
   Float nextFloatOrNull() throws IOException;

   void nextNull() throws IOException;

   void setLenient(boolean var1);

   void skipValue() throws IOException;
}
