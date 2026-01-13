package io.sentry.protocol;

import io.sentry.ILogger;
import io.sentry.JsonDeserializer;
import io.sentry.JsonSerializable;
import io.sentry.JsonUnknown;
import io.sentry.ObjectReader;
import io.sentry.ObjectWriter;
import io.sentry.util.CollectionUtils;
import io.sentry.util.Objects;
import io.sentry.vendor.gson.stream.JsonToken;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class Request implements JsonUnknown, JsonSerializable {
   @Nullable
   private String url;
   @Nullable
   private String method;
   @Nullable
   private String queryString;
   @Nullable
   private Object data;
   @Nullable
   private String cookies;
   @Nullable
   private Map<String, String> headers;
   @Nullable
   private Map<String, String> env;
   @Nullable
   private Long bodySize;
   @Nullable
   private Map<String, String> other;
   @Nullable
   private String fragment;
   @Nullable
   private String apiTarget;
   @Nullable
   private Map<String, Object> unknown;

   public Request() {
   }

   public Request(@NotNull Request request) {
      this.url = request.url;
      this.cookies = request.cookies;
      this.method = request.method;
      this.queryString = request.queryString;
      this.headers = CollectionUtils.newConcurrentHashMap(request.headers);
      this.env = CollectionUtils.newConcurrentHashMap(request.env);
      this.other = CollectionUtils.newConcurrentHashMap(request.other);
      this.unknown = CollectionUtils.newConcurrentHashMap(request.unknown);
      this.data = request.data;
      this.fragment = request.fragment;
      this.bodySize = request.bodySize;
      this.apiTarget = request.apiTarget;
   }

   @Nullable
   public String getUrl() {
      return this.url;
   }

   public void setUrl(@Nullable String url) {
      this.url = url;
   }

   @Nullable
   public String getMethod() {
      return this.method;
   }

   public void setMethod(@Nullable String method) {
      this.method = method;
   }

   @Nullable
   public String getQueryString() {
      return this.queryString;
   }

   public void setQueryString(@Nullable String queryString) {
      this.queryString = queryString;
   }

   @Nullable
   public Object getData() {
      return this.data;
   }

   public void setData(@Nullable Object data) {
      this.data = data;
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
   public Map<String, String> getEnvs() {
      return this.env;
   }

   public void setEnvs(@Nullable Map<String, String> env) {
      this.env = CollectionUtils.newConcurrentHashMap(env);
   }

   @Nullable
   public Map<String, String> getOthers() {
      return this.other;
   }

   public void setOthers(@Nullable Map<String, String> other) {
      this.other = CollectionUtils.newConcurrentHashMap(other);
   }

   @Nullable
   public String getFragment() {
      return this.fragment;
   }

   public void setFragment(@Nullable String fragment) {
      this.fragment = fragment;
   }

   @Nullable
   public Long getBodySize() {
      return this.bodySize;
   }

   public void setBodySize(@Nullable Long bodySize) {
      this.bodySize = bodySize;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (o != null && this.getClass() == o.getClass()) {
         Request request = (Request)o;
         return Objects.equals(this.url, request.url)
            && Objects.equals(this.method, request.method)
            && Objects.equals(this.queryString, request.queryString)
            && Objects.equals(this.cookies, request.cookies)
            && Objects.equals(this.headers, request.headers)
            && Objects.equals(this.env, request.env)
            && Objects.equals(this.bodySize, request.bodySize)
            && Objects.equals(this.fragment, request.fragment)
            && Objects.equals(this.apiTarget, request.apiTarget);
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      return Objects.hash(this.url, this.method, this.queryString, this.cookies, this.headers, this.env, this.bodySize, this.fragment, this.apiTarget);
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
   public String getApiTarget() {
      return this.apiTarget;
   }

   public void setApiTarget(@Nullable String apiTarget) {
      this.apiTarget = apiTarget;
   }

   @Override
   public void serialize(@NotNull ObjectWriter writer, @NotNull ILogger logger) throws IOException {
      writer.beginObject();
      if (this.url != null) {
         writer.name("url").value(this.url);
      }

      if (this.method != null) {
         writer.name("method").value(this.method);
      }

      if (this.queryString != null) {
         writer.name("query_string").value(this.queryString);
      }

      if (this.data != null) {
         writer.name("data").value(logger, this.data);
      }

      if (this.cookies != null) {
         writer.name("cookies").value(this.cookies);
      }

      if (this.headers != null) {
         writer.name("headers").value(logger, this.headers);
      }

      if (this.env != null) {
         writer.name("env").value(logger, this.env);
      }

      if (this.other != null) {
         writer.name("other").value(logger, this.other);
      }

      if (this.fragment != null) {
         writer.name("fragment").value(logger, this.fragment);
      }

      if (this.bodySize != null) {
         writer.name("body_size").value(logger, this.bodySize);
      }

      if (this.apiTarget != null) {
         writer.name("api_target").value(logger, this.apiTarget);
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

   public static final class Deserializer implements JsonDeserializer<Request> {
      @NotNull
      public Request deserialize(@NotNull ObjectReader reader, @NotNull ILogger logger) throws Exception {
         reader.beginObject();
         Request request = new Request();
         Map<String, Object> unknown = null;

         while (reader.peek() == JsonToken.NAME) {
            String nextName = reader.nextName();
            switch (nextName) {
               case "url":
                  request.url = reader.nextStringOrNull();
                  break;
               case "method":
                  request.method = reader.nextStringOrNull();
                  break;
               case "query_string":
                  request.queryString = reader.nextStringOrNull();
                  break;
               case "data":
                  request.data = reader.nextObjectOrNull();
                  break;
               case "cookies":
                  request.cookies = reader.nextStringOrNull();
                  break;
               case "headers":
                  Map<String, String> deserializedHeaders = (Map<String, String>)reader.nextObjectOrNull();
                  if (deserializedHeaders != null) {
                     request.headers = CollectionUtils.newConcurrentHashMap(deserializedHeaders);
                  }
                  break;
               case "env":
                  Map<String, String> deserializedEnv = (Map<String, String>)reader.nextObjectOrNull();
                  if (deserializedEnv != null) {
                     request.env = CollectionUtils.newConcurrentHashMap(deserializedEnv);
                  }
                  break;
               case "other":
                  Map<String, String> deserializedOther = (Map<String, String>)reader.nextObjectOrNull();
                  if (deserializedOther != null) {
                     request.other = CollectionUtils.newConcurrentHashMap(deserializedOther);
                  }
                  break;
               case "fragment":
                  request.fragment = reader.nextStringOrNull();
                  break;
               case "body_size":
                  request.bodySize = reader.nextLongOrNull();
                  break;
               case "api_target":
                  request.apiTarget = reader.nextStringOrNull();
                  break;
               default:
                  if (unknown == null) {
                     unknown = new ConcurrentHashMap<>();
                  }

                  reader.nextUnknown(logger, unknown, nextName);
            }
         }

         request.setUnknown(unknown);
         reader.endObject();
         return request;
      }
   }

   public static final class JsonKeys {
      public static final String URL = "url";
      public static final String METHOD = "method";
      public static final String QUERY_STRING = "query_string";
      public static final String DATA = "data";
      public static final String COOKIES = "cookies";
      public static final String HEADERS = "headers";
      public static final String ENV = "env";
      public static final String OTHER = "other";
      public static final String FRAGMENT = "fragment";
      public static final String BODY_SIZE = "body_size";
      public static final String API_TARGET = "api_target";
   }
}
