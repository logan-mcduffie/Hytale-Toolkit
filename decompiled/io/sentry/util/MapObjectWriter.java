package io.sentry.util;

import io.sentry.DateUtils;
import io.sentry.ILogger;
import io.sentry.JsonSerializable;
import io.sentry.ObjectWriter;
import io.sentry.SentryLevel;
import java.io.IOException;
import java.net.InetAddress;
import java.net.URI;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Currency;
import java.util.Date;
import java.util.HashMap;
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
public final class MapObjectWriter implements ObjectWriter {
   @NotNull
   final Map<String, Object> root;
   @NotNull
   final ArrayDeque<Object> stack;

   public MapObjectWriter(@NotNull Map<String, Object> root) {
      this.root = root;
      this.stack = new ArrayDeque<>();
      this.stack.addLast(root);
   }

   public MapObjectWriter name(@NotNull String name) throws IOException {
      this.stack.add(name);
      return this;
   }

   public MapObjectWriter value(@NotNull ILogger logger, @Nullable Object object) throws IOException {
      if (object == null) {
         this.nullValue();
      } else if (object instanceof Character) {
         this.value(Character.toString((Character)object));
      } else if (object instanceof String) {
         this.value((String)object);
      } else if (object instanceof Boolean) {
         this.value(((Boolean)object).booleanValue());
      } else if (object instanceof Number) {
         this.value((Number)object);
      } else if (object instanceof Date) {
         this.serializeDate(logger, (Date)object);
      } else if (object instanceof TimeZone) {
         this.serializeTimeZone(logger, (TimeZone)object);
      } else if (object instanceof JsonSerializable) {
         ((JsonSerializable)object).serialize(this, logger);
      } else if (object instanceof Collection) {
         this.serializeCollection(logger, (Collection<?>)object);
      } else if (object.getClass().isArray()) {
         this.serializeCollection(logger, Arrays.asList((Object[])object));
      } else if (object instanceof Map) {
         this.serializeMap(logger, (Map<?, ?>)object);
      } else if (object instanceof Locale) {
         this.value(object.toString());
      } else if (object instanceof AtomicIntegerArray) {
         this.serializeCollection(logger, JsonSerializationUtils.atomicIntegerArrayToList((AtomicIntegerArray)object));
      } else if (object instanceof AtomicBoolean) {
         this.value(((AtomicBoolean)object).get());
      } else if (object instanceof URI) {
         this.value(object.toString());
      } else if (object instanceof InetAddress) {
         this.value(object.toString());
      } else if (object instanceof UUID) {
         this.value(object.toString());
      } else if (object instanceof Currency) {
         this.value(object.toString());
      } else if (object instanceof Calendar) {
         this.serializeMap(logger, JsonSerializationUtils.calendarToMap((Calendar)object));
      } else if (object.getClass().isEnum()) {
         this.value(object.toString());
      } else {
         logger.log(SentryLevel.WARNING, "Failed serializing unknown object.", object);
      }

      return this;
   }

   @Override
   public void setLenient(boolean lenient) {
   }

   @Override
   public void setIndent(@Nullable String indent) {
   }

   @Nullable
   @Override
   public String getIndent() {
      return null;
   }

   public MapObjectWriter beginArray() throws IOException {
      this.stack.add(new ArrayList());
      return this;
   }

   public MapObjectWriter endArray() throws IOException {
      this.endObject();
      return this;
   }

   public MapObjectWriter beginObject() throws IOException {
      this.stack.addLast(new HashMap());
      return this;
   }

   public MapObjectWriter endObject() throws IOException {
      Object value = this.stack.removeLast();
      this.postValue(value);
      return this;
   }

   public MapObjectWriter value(@Nullable String value) throws IOException {
      this.postValue(value);
      return this;
   }

   @Override
   public ObjectWriter jsonValue(@Nullable String value) throws IOException {
      return this;
   }

   public MapObjectWriter nullValue() throws IOException {
      this.postValue(null);
      return this;
   }

   public MapObjectWriter value(boolean value) throws IOException {
      this.postValue(value);
      return this;
   }

   public MapObjectWriter value(@Nullable Boolean value) throws IOException {
      this.postValue(value);
      return this;
   }

   public MapObjectWriter value(double value) throws IOException {
      this.postValue(value);
      return this;
   }

   public MapObjectWriter value(long value) throws IOException {
      this.postValue(value);
      return this;
   }

   public MapObjectWriter value(@Nullable Number value) throws IOException {
      this.postValue(value);
      return this;
   }

   private void serializeDate(@NotNull ILogger logger, @NotNull Date date) throws IOException {
      try {
         this.value(DateUtils.getTimestamp(date));
      } catch (Exception var4) {
         logger.log(SentryLevel.ERROR, "Error when serializing Date", var4);
         this.nullValue();
      }
   }

   private void serializeTimeZone(@NotNull ILogger logger, @NotNull TimeZone timeZone) throws IOException {
      try {
         this.value(timeZone.getID());
      } catch (Exception var4) {
         logger.log(SentryLevel.ERROR, "Error when serializing TimeZone", var4);
         this.nullValue();
      }
   }

   private void serializeCollection(@NotNull ILogger logger, @NotNull Collection<?> collection) throws IOException {
      this.beginArray();

      for (Object object : collection) {
         this.value(logger, object);
      }

      this.endArray();
   }

   private void serializeMap(@NotNull ILogger logger, @NotNull Map<?, ?> map) throws IOException {
      this.beginObject();

      for (Object key : map.keySet()) {
         if (key instanceof String) {
            this.name((String)key);
            this.value(logger, map.get(key));
         }
      }

      this.endObject();
   }

   private void postValue(@Nullable Object value) {
      Object topStackElement = this.stack.peekLast();
      if (topStackElement instanceof List) {
         ((List)topStackElement).add(value);
      } else {
         if (!(topStackElement instanceof String)) {
            throw new IllegalStateException("Invalid stack state, expected array or string on top");
         }

         String key = (String)this.stack.removeLast();
         this.peekObject().put(key, value);
      }
   }

   @NotNull
   private Map<String, Object> peekObject() {
      Object item = this.stack.peekLast();
      if (item == null) {
         throw new IllegalStateException("Stack is empty.");
      } else if (item instanceof Map) {
         return (Map<String, Object>)item;
      } else {
         throw new IllegalStateException("Stack element is not a Map.");
      }
   }
}
