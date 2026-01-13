package io.sentry.protocol;

import io.sentry.ILogger;
import io.sentry.JsonDeserializer;
import io.sentry.JsonSerializable;
import io.sentry.JsonUnknown;
import io.sentry.ObjectReader;
import io.sentry.ObjectWriter;
import io.sentry.vendor.gson.stream.JsonToken;
import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class Geo implements JsonUnknown, JsonSerializable {
   @Nullable
   private String city;
   @Nullable
   private String countryCode;
   @Nullable
   private String region;
   @Nullable
   private Map<String, @NotNull Object> unknown;

   public Geo() {
   }

   public Geo(@NotNull Geo geo) {
      this.city = geo.city;
      this.countryCode = geo.countryCode;
      this.region = geo.region;
   }

   public static Geo fromMap(@NotNull Map<String, Object> map) {
      Geo geo = new Geo();

      for (Entry<String, Object> entry : map.entrySet()) {
         Object value = entry.getValue();
         String var5 = entry.getKey();
         switch (var5) {
            case "city":
               geo.city = value instanceof String ? (String)value : null;
               break;
            case "country_code":
               geo.countryCode = value instanceof String ? (String)value : null;
               break;
            case "region":
               geo.region = value instanceof String ? (String)value : null;
         }
      }

      return geo;
   }

   @Nullable
   public String getCity() {
      return this.city;
   }

   public void setCity(@Nullable String city) {
      this.city = city;
   }

   @Nullable
   public String getCountryCode() {
      return this.countryCode;
   }

   public void setCountryCode(@Nullable String countryCode) {
      this.countryCode = countryCode;
   }

   @Nullable
   public String getRegion() {
      return this.region;
   }

   public void setRegion(@Nullable String region) {
      this.region = region;
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

   @Override
   public void serialize(@NotNull ObjectWriter writer, @NotNull ILogger logger) throws IOException {
      writer.beginObject();
      if (this.city != null) {
         writer.name("city").value(this.city);
      }

      if (this.countryCode != null) {
         writer.name("country_code").value(this.countryCode);
      }

      if (this.region != null) {
         writer.name("region").value(this.region);
      }

      if (this.unknown != null) {
         for (String key : this.unknown.keySet()) {
            Object value = this.unknown.get(key);
            writer.name(key);
            writer.value(logger, value);
         }
      }

      writer.endObject();
   }

   public static final class Deserializer implements JsonDeserializer<Geo> {
      @NotNull
      public Geo deserialize(@NotNull ObjectReader reader, @NotNull ILogger logger) throws Exception {
         reader.beginObject();
         Geo geo = new Geo();
         Map<String, Object> unknown = null;

         while (reader.peek() == JsonToken.NAME) {
            String nextName = reader.nextName();
            switch (nextName) {
               case "city":
                  geo.city = reader.nextStringOrNull();
                  break;
               case "country_code":
                  geo.countryCode = reader.nextStringOrNull();
                  break;
               case "region":
                  geo.region = reader.nextStringOrNull();
                  break;
               default:
                  if (unknown == null) {
                     unknown = new ConcurrentHashMap<>();
                  }

                  reader.nextUnknown(logger, unknown, nextName);
            }
         }

         geo.setUnknown(unknown);
         reader.endObject();
         return geo;
      }
   }

   public static final class JsonKeys {
      public static final String CITY = "city";
      public static final String COUNTRY_CODE = "country_code";
      public static final String REGION = "region";
   }
}
