package io.sentry;

import io.sentry.util.JsonSerializationUtils;
import java.io.IOException;
import java.net.InetAddress;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Currency;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicIntegerArray;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.ApiStatus.Internal;

@Internal
public final class JsonObjectSerializer {
   public static final String OBJECT_PLACEHOLDER = "[OBJECT]";
   public final JsonReflectionObjectSerializer jsonReflectionObjectSerializer;

   public JsonObjectSerializer(int maxDepth) {
      this.jsonReflectionObjectSerializer = new JsonReflectionObjectSerializer(maxDepth);
   }

   public void serialize(@NotNull ObjectWriter writer, @NotNull ILogger logger, @Nullable Object object) throws IOException {
      if (object == null) {
         writer.nullValue();
      } else if (object instanceof Character) {
         writer.value(Character.toString((Character)object));
      } else if (object instanceof String) {
         writer.value((String)object);
      } else if (object instanceof Boolean) {
         writer.value(((Boolean)object).booleanValue());
      } else if (object instanceof Number) {
         writer.value((Number)object);
      } else if (object instanceof Date) {
         this.serializeDate(writer, logger, (Date)object);
      } else if (object instanceof TimeZone) {
         this.serializeTimeZone(writer, logger, (TimeZone)object);
      } else if (object instanceof JsonSerializable) {
         ((JsonSerializable)object).serialize(writer, logger);
      } else if (object instanceof Collection) {
         this.serializeCollection(writer, logger, (Collection<?>)object);
      } else if (object instanceof boolean[]) {
         List<Boolean> bools = new ArrayList<>(((boolean[])object).length);

         for (boolean b : (boolean[])object) {
            bools.add(b);
         }

         this.serializeCollection(writer, logger, bools);
      } else if (object instanceof byte[]) {
         List<Byte> bytes = new ArrayList<>(((byte[])object).length);

         for (byte b : (byte[])object) {
            bytes.add(b);
         }

         this.serializeCollection(writer, logger, bytes);
      } else if (object instanceof short[]) {
         List<Short> shorts = new ArrayList<>(((short[])object).length);

         for (short s : (short[])object) {
            shorts.add(s);
         }

         this.serializeCollection(writer, logger, shorts);
      } else if (object instanceof char[]) {
         List<Character> chars = new ArrayList<>(((char[])object).length);

         for (char s : (char[])object) {
            chars.add(s);
         }

         this.serializeCollection(writer, logger, chars);
      } else if (object instanceof int[]) {
         List<Integer> ints = new ArrayList<>(((int[])object).length);

         for (int i : (int[])object) {
            ints.add(i);
         }

         this.serializeCollection(writer, logger, ints);
      } else if (object instanceof long[]) {
         List<Long> longs = new ArrayList<>(((long[])object).length);

         for (long l : (long[])object) {
            longs.add(l);
         }

         this.serializeCollection(writer, logger, longs);
      } else if (object instanceof float[]) {
         List<Float> floats = new ArrayList<>(((float[])object).length);

         for (float f : (float[])object) {
            floats.add(f);
         }

         this.serializeCollection(writer, logger, floats);
      } else if (object instanceof double[]) {
         List<Double> doubles = new ArrayList<>(((double[])object).length);

         for (double d : (double[])object) {
            doubles.add(d);
         }

         this.serializeCollection(writer, logger, doubles);
      } else if (object.getClass().isArray()) {
         this.serializeCollection(writer, logger, Arrays.asList((Object[])object));
      } else if (object instanceof Map) {
         this.serializeMap(writer, logger, (Map<?, ?>)object);
      } else if (object instanceof Locale) {
         writer.value(object.toString());
      } else if (object instanceof AtomicIntegerArray) {
         this.serializeCollection(writer, logger, JsonSerializationUtils.atomicIntegerArrayToList((AtomicIntegerArray)object));
      } else if (object instanceof AtomicBoolean) {
         writer.value(((AtomicBoolean)object).get());
      } else if (object instanceof URI) {
         writer.value(object.toString());
      } else if (object instanceof InetAddress) {
         writer.value(object.toString());
      } else if (object instanceof UUID) {
         writer.value(object.toString());
      } else if (object instanceof Currency) {
         writer.value(object.toString());
      } else if (object instanceof Calendar) {
         this.serializeMap(writer, logger, JsonSerializationUtils.calendarToMap((Calendar)object));
      } else if (object.getClass().isEnum()) {
         writer.value(object.toString());
      } else {
         try {
            Object serializableObject = this.jsonReflectionObjectSerializer.serialize(object, logger);
            this.serialize(writer, logger, serializableObject);
         } catch (Exception var10) {
            logger.log(SentryLevel.ERROR, "Failed serializing unknown object.", var10);
            writer.value("[OBJECT]");
         }
      }
   }

   private void serializeDate(@NotNull ObjectWriter writer, @NotNull ILogger logger, @NotNull Date date) throws IOException {
      try {
         writer.value(DateUtils.getTimestamp(date));
      } catch (Exception var5) {
         logger.log(SentryLevel.ERROR, "Error when serializing Date", var5);
         writer.nullValue();
      }
   }

   private void serializeTimeZone(@NotNull ObjectWriter writer, @NotNull ILogger logger, @NotNull TimeZone timeZone) throws IOException {
      try {
         writer.value(timeZone.getID());
      } catch (Exception var5) {
         logger.log(SentryLevel.ERROR, "Error when serializing TimeZone", var5);
         writer.nullValue();
      }
   }

   private void serializeCollection(@NotNull ObjectWriter writer, @NotNull ILogger logger, @NotNull Collection<?> collection) throws IOException {
      writer.beginArray();

      for (Object object : collection) {
         this.serialize(writer, logger, object);
      }

      writer.endArray();
   }

   private void serializeMap(@NotNull ObjectWriter writer, @NotNull ILogger logger, @NotNull Map<?, ?> map) throws IOException {
      writer.beginObject();

      for (Object key : map.keySet()) {
         if (key instanceof String) {
            writer.name((String)key);
            this.serialize(writer, logger, map.get(key));
         }
      }

      writer.endObject();
   }
}
