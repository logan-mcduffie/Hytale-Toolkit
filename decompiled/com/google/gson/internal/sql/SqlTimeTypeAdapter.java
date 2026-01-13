package com.google.gson.internal.sql;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import java.sql.Time;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

final class SqlTimeTypeAdapter extends TypeAdapter<Time> {
   static final TypeAdapterFactory FACTORY = new TypeAdapterFactory() {
      @Override
      public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> typeToken) {
         return typeToken.getRawType() == Time.class ? new SqlTimeTypeAdapter() : null;
      }
   };
   private final DateFormat format = new SimpleDateFormat("hh:mm:ss a");

   private SqlTimeTypeAdapter() {
   }

   public Time read(JsonReader in) throws IOException {
      if (in.peek() == JsonToken.NULL) {
         in.nextNull();
         return null;
      } else {
         String s = in.nextString();
         synchronized (this) {
            TimeZone originalTimeZone = this.format.getTimeZone();

            Time var6;
            try {
               Date date = this.format.parse(s);
               var6 = new Time(date.getTime());
            } catch (ParseException var12) {
               throw new JsonSyntaxException("Failed parsing '" + s + "' as SQL Time; at path " + in.getPreviousPath(), var12);
            } finally {
               this.format.setTimeZone(originalTimeZone);
            }

            return var6;
         }
      }
   }

   public void write(JsonWriter out, Time value) throws IOException {
      if (value == null) {
         out.nullValue();
      } else {
         String timeString;
         synchronized (this) {
            timeString = this.format.format(value);
         }

         out.value(timeString);
      }
   }
}
