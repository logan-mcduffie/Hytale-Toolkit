package com.nimbusds.jose.shaded.gson.internal.bind;

import com.nimbusds.jose.shaded.gson.Gson;
import com.nimbusds.jose.shaded.gson.JsonSyntaxException;
import com.nimbusds.jose.shaded.gson.TypeAdapter;
import com.nimbusds.jose.shaded.gson.TypeAdapterFactory;
import com.nimbusds.jose.shaded.gson.internal.JavaVersion;
import com.nimbusds.jose.shaded.gson.internal.PreJava9DateFormatProvider;
import com.nimbusds.jose.shaded.gson.internal.bind.util.ISO8601Utils;
import com.nimbusds.jose.shaded.gson.reflect.TypeToken;
import com.nimbusds.jose.shaded.gson.stream.JsonReader;
import com.nimbusds.jose.shaded.gson.stream.JsonToken;
import com.nimbusds.jose.shaded.gson.stream.JsonWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.TimeZone;

public final class DefaultDateTypeAdapter<T extends Date> extends TypeAdapter<T> {
   private static final String SIMPLE_NAME = "DefaultDateTypeAdapter";
   public static final TypeAdapterFactory DEFAULT_STYLE_FACTORY = new TypeAdapterFactory() {
      @Override
      public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> typeToken) {
         return typeToken.getRawType() == Date.class ? new DefaultDateTypeAdapter<>(DefaultDateTypeAdapter.DateType.DATE, 2, 2) : null;
      }

      @Override
      public String toString() {
         return "DefaultDateTypeAdapter#DEFAULT_STYLE_FACTORY";
      }
   };
   private final DefaultDateTypeAdapter.DateType<T> dateType;
   private final List<DateFormat> dateFormats = new ArrayList<>();

   private DefaultDateTypeAdapter(DefaultDateTypeAdapter.DateType<T> dateType, String datePattern) {
      this.dateType = Objects.requireNonNull(dateType);
      this.dateFormats.add(new SimpleDateFormat(datePattern, Locale.US));
      if (!Locale.getDefault().equals(Locale.US)) {
         this.dateFormats.add(new SimpleDateFormat(datePattern));
      }
   }

   private DefaultDateTypeAdapter(DefaultDateTypeAdapter.DateType<T> dateType, int dateStyle, int timeStyle) {
      this.dateType = Objects.requireNonNull(dateType);
      this.dateFormats.add(DateFormat.getDateTimeInstance(dateStyle, timeStyle, Locale.US));
      if (!Locale.getDefault().equals(Locale.US)) {
         this.dateFormats.add(DateFormat.getDateTimeInstance(dateStyle, timeStyle));
      }

      if (JavaVersion.isJava9OrLater()) {
         this.dateFormats.add(PreJava9DateFormatProvider.getUsDateTimeFormat(dateStyle, timeStyle));
      }
   }

   public void write(JsonWriter out, Date value) throws IOException {
      if (value == null) {
         out.nullValue();
      } else {
         DateFormat dateFormat = this.dateFormats.get(0);
         String dateFormatAsString;
         synchronized (this.dateFormats) {
            dateFormatAsString = dateFormat.format(value);
         }

         out.value(dateFormatAsString);
      }
   }

   public T read(JsonReader in) throws IOException {
      if (in.peek() == JsonToken.NULL) {
         in.nextNull();
         return null;
      } else {
         Date date = this.deserializeToDate(in);
         return this.dateType.deserialize(date);
      }
   }

   private Date deserializeToDate(JsonReader in) throws IOException {
      String s = in.nextString();
      synchronized (this.dateFormats) {
         for (DateFormat dateFormat : this.dateFormats) {
            TimeZone originalTimeZone = dateFormat.getTimeZone();

            Date var7;
            try {
               var7 = dateFormat.parse(s);
            } catch (ParseException var15) {
               continue;
            } finally {
               dateFormat.setTimeZone(originalTimeZone);
            }

            return var7;
         }
      }

      try {
         return ISO8601Utils.parse(s, new ParsePosition(0));
      } catch (ParseException var14) {
         throw new JsonSyntaxException("Failed parsing '" + s + "' as Date; at path " + in.getPreviousPath(), var14);
      }
   }

   @Override
   public String toString() {
      DateFormat defaultFormat = this.dateFormats.get(0);
      return defaultFormat instanceof SimpleDateFormat
         ? "DefaultDateTypeAdapter(" + ((SimpleDateFormat)defaultFormat).toPattern() + ')'
         : "DefaultDateTypeAdapter(" + defaultFormat.getClass().getSimpleName() + ')';
   }

   public abstract static class DateType<T extends Date> {
      public static final DefaultDateTypeAdapter.DateType<Date> DATE = new DefaultDateTypeAdapter.DateType<Date>(Date.class) {
         @Override
         protected Date deserialize(Date date) {
            return date;
         }
      };
      private final Class<T> dateClass;

      protected DateType(Class<T> dateClass) {
         this.dateClass = dateClass;
      }

      protected abstract T deserialize(Date var1);

      private TypeAdapterFactory createFactory(DefaultDateTypeAdapter<T> adapter) {
         return TypeAdapters.newFactory(this.dateClass, adapter);
      }

      public final TypeAdapterFactory createAdapterFactory(String datePattern) {
         return this.createFactory(new DefaultDateTypeAdapter<>(this, datePattern));
      }

      public final TypeAdapterFactory createAdapterFactory(int dateStyle, int timeStyle) {
         return this.createFactory(new DefaultDateTypeAdapter<>(this, dateStyle, timeStyle));
      }
   }
}
