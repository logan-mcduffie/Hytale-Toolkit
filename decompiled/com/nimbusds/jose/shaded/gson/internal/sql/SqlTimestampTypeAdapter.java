package com.nimbusds.jose.shaded.gson.internal.sql;

import com.nimbusds.jose.shaded.gson.Gson;
import com.nimbusds.jose.shaded.gson.TypeAdapter;
import com.nimbusds.jose.shaded.gson.TypeAdapterFactory;
import com.nimbusds.jose.shaded.gson.reflect.TypeToken;
import com.nimbusds.jose.shaded.gson.stream.JsonReader;
import com.nimbusds.jose.shaded.gson.stream.JsonWriter;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.Date;

class SqlTimestampTypeAdapter extends TypeAdapter<Timestamp> {
   static final TypeAdapterFactory FACTORY = new TypeAdapterFactory() {
      @Override
      public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> typeToken) {
         if (typeToken.getRawType() == Timestamp.class) {
            TypeAdapter<Date> dateTypeAdapter = gson.getAdapter(Date.class);
            return new SqlTimestampTypeAdapter(dateTypeAdapter);
         } else {
            return null;
         }
      }
   };
   private final TypeAdapter<Date> dateTypeAdapter;

   private SqlTimestampTypeAdapter(TypeAdapter<Date> dateTypeAdapter) {
      this.dateTypeAdapter = dateTypeAdapter;
   }

   public Timestamp read(JsonReader in) throws IOException {
      Date date = this.dateTypeAdapter.read(in);
      return date != null ? new Timestamp(date.getTime()) : null;
   }

   public void write(JsonWriter out, Timestamp value) throws IOException {
      this.dateTypeAdapter.write(out, value);
   }
}
