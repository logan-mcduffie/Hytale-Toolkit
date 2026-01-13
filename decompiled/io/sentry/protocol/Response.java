package io.sentry.protocol;

import io.sentry.ILogger;
import io.sentry.JsonDeserializer;
import io.sentry.JsonSerializable;
import io.sentry.JsonUnknown;
import io.sentry.ObjectReader;
import io.sentry.ObjectWriter;
import io.sentry.util.CollectionUtils;
import io.sentry.vendor.gson.stream.JsonToken;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class Response implements JsonUnknown, JsonSerializable {
   public static final String TYPE = "response";
   @Nullable
   private String cookies;
   @Nullable
   private Map<String, String> headers;
   @Nullable
   private Integer statusCode;
   @Nullable
   private Long bodySize;
   @Nullable
   private Object data;
   @Nullable
   private Map<String, Object> unknown;

   public Response() {
   }

   public Response(@NotNull Response response) {
      this.cookies = response.cookies;
      this.headers = CollectionUtils.newConcurrentHashMap(response.headers);
      this.unknown = CollectionUtils.newConcurrentHashMap(response.unknown);
      this.statusCode = response.statusCode;
      this.bodySize = response.bodySize;
      this.data = response.data;
   }

   @Nullable
   public String getCookies() {
      return this.cookies;
   }

   public void setCookies(@Nullable String cookies) {
      this.cookies = cookies;
   }

   @Nullable
   public Map<String, String> getHeaders() {
      return this.headers;
   }

   public void setHeaders(@Nullable Map<String, String> headers) {
      this.headers = CollectionUtils.newConcurrentHashMap(headers);
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

   @Nullable
   public Integer getStatusCode() {
      return this.statusCode;
   }

   public void setStatusCode(@Nullable Integer statusCode) {
      this.statusCode = statusCode;
   }

   @Nullable
   public Long getBodySize() {
      return this.bodySize;
   }

   public void setBodySize(@Nullable Long bodySize) {
      this.bodySize = bodySize;
   }

   @Nullable
   public Object getData() {
      return this.data;
   }

   public void setData(@Nullable Object data) {
      this.data = data;
   }

   @Override
   public void serialize(@NotNull ObjectWriter writer, @NotNull ILogger logger) throws IOException {
      writer.beginObject();
      if (this.cookies != null) {
         writer.name("cookies").value(this.cookies);
      }

      if (this.headers != null) {
         writer.name("headers").value(logger, this.headers);
      }

      if (this.statusCode != null) {
         writer.name("status_code").value(logger, this.statusCode);
      }

      if (this.bodySize != null) {
         writer.name("body_size").value(logger, this.bodySize);
      }

      if (this.data != null) {
         writer.name("data").value(logger, this.data);
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

   public static final class Deserializer implements JsonDeserializer<Response> {
      @NotNull
      public Response deserialize(@NotNull ObjectReader reader, @NotNull ILogger logger) throws Exception {
         reader.beginObject();
         Response response = new Response();
         Map<String, Object> unknown = null;

         while (reader.peek() == JsonToken.NAME) {
            String nextName = reader.nextName();
            switch (nextName) {
               case "cookies":
                  response.cookies = reader.nextStringOrNull();
                  break;
               case "headers":
                  Map<String, String> deserializedHeaders = (Map<String, String>)reader.nextObjectOrNull();
                  if (deserializedHeaders != null) {
                     response.headers = CollectionUtils.newConcurrentHashMap(deserializedHeaders);
                  }
                  break;
               case "status_code":
                  response.statusCode = reader.nextIntegerOrNull();
                  break;
               case "body_size":
                  response.bodySize = reader.nextLongOrNull();
                  break;
               case "data":
                  response.data = reader.nextObjectOrNull();
                  break;
               default:
                  if (unknown == null) {
                     unknown = new ConcurrentHashMap<>();
                  }

                  reader.nextUnknown(logger, unknown, nextName);
            }
         }

         response.setUnknown(unknown);
         reader.endObject();
         return response;
      }
   }

   public static final class JsonKeys {
      public static final String COOKIES = "cookies";
      public static final String HEADERS = "headers";
      public static final String STATUS_CODE = "status_code";
      public static final String BODY_SIZE = "body_size";
      public static final String DATA = "data";
   }
}
