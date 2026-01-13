package io.sentry.util;

import io.sentry.ILogger;
import io.sentry.ISerializer;
import io.sentry.JsonSerializable;
import io.sentry.SentryLevel;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicIntegerArray;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.ApiStatus.Internal;

@Internal
public final class JsonSerializationUtils {
   private static final Charset UTF_8 = Charset.forName("UTF-8");

   @NotNull
   public static Map<String, Object> calendarToMap(@NotNull Calendar calendar) {
      Map<String, Object> map = new HashMap<>();
      map.put("year", calendar.get(1));
      map.put("month", calendar.get(2));
      map.put("dayOfMonth", calendar.get(5));
      map.put("hourOfDay", calendar.get(11));
      map.put("minute", calendar.get(12));
      map.put("second", calendar.get(13));
      return map;
   }

   @NotNull
   public static List<Object> atomicIntegerArrayToList(@NotNull AtomicIntegerArray array) {
      int numberOfItems = array.length();
      List<Object> list = new ArrayList<>(numberOfItems);

      for (int i = 0; i < numberOfItems; i++) {
         list.add(array.get(i));
      }

      return list;
   }

   @Nullable
   public static byte[] bytesFrom(@NotNull ISerializer serializer, @NotNull ILogger logger, @NotNull JsonSerializable serializable) {
      try {
         ByteArrayOutputStream stream = new ByteArrayOutputStream();

         byte[] var5;
         try {
            Writer writer = new BufferedWriter(new OutputStreamWriter(stream, UTF_8));

            try {
               serializer.serialize(serializable, writer);
               var5 = stream.toByteArray();
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
               stream.close();
            } catch (Throwable var7) {
               var10.addSuppressed(var7);
            }

            throw var10;
         }

         stream.close();
         return var5;
      } catch (Throwable var11) {
         logger.log(SentryLevel.ERROR, "Could not serialize serializable", var11);
         return null;
      }
   }

   public static long byteSizeOf(@NotNull ISerializer serializer, @NotNull ILogger logger, @Nullable JsonSerializable serializable) {
      if (serializable == null) {
         return 0L;
      } else {
         try {
            JsonSerializationUtils.ByteCountingWriter writer = new JsonSerializationUtils.ByteCountingWriter();
            serializer.serialize(serializable, writer);
            return writer.getByteCount();
         } catch (Throwable var4) {
            logger.log(SentryLevel.ERROR, "Could not calculate size of serializable", var4);
            return 0L;
         }
      }
   }

   private static final class ByteCountingWriter extends Writer {
      private long byteCount = 0L;

      private ByteCountingWriter() {
      }

      @Override
      public void write(char[] cbuf, int off, int len) {
         for (int i = off; i < off + len; i++) {
            this.byteCount = this.byteCount + utf8ByteCount(cbuf[i]);
         }
      }

      @Override
      public void write(int c) {
         this.byteCount = this.byteCount + utf8ByteCount((char)c);
      }

      @Override
      public void write(@NotNull String str, int off, int len) {
         for (int i = off; i < off + len; i++) {
            this.byteCount = this.byteCount + utf8ByteCount(str.charAt(i));
         }
      }

      @Override
      public void flush() {
      }

      @Override
      public void close() {
      }

      public long getByteCount() {
         return this.byteCount;
      }

      private static int utf8ByteCount(char c) {
         if (c <= 127) {
            return 1;
         } else if (c <= 2047) {
            return 2;
         } else {
            return Character.isSurrogate(c) ? 2 : 3;
         }
      }
   }
}
