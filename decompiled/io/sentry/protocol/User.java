package io.sentry.protocol;

import io.sentry.ILogger;
import io.sentry.JsonDeserializer;
import io.sentry.JsonSerializable;
import io.sentry.JsonUnknown;
import io.sentry.ObjectReader;
import io.sentry.ObjectWriter;
import io.sentry.SentryLevel;
import io.sentry.SentryOptions;
import io.sentry.util.CollectionUtils;
import io.sentry.util.Objects;
import io.sentry.vendor.gson.stream.JsonToken;
import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class User implements JsonUnknown, JsonSerializable {
   @Nullable
   private String email;
   @Nullable
   private String id;
   @Nullable
   private String username;
   @Nullable
   private String ipAddress;
   @Deprecated
   @Nullable
   private String name;
   @Nullable
   private Geo geo;
   @Nullable
   private Map<String, @NotNull String> data;
   @Nullable
   private Map<String, @NotNull Object> unknown;

   public User() {
   }

   public User(@NotNull User user) {
      this.email = user.email;
      this.username = user.username;
      this.id = user.id;
      this.ipAddress = user.ipAddress;
      this.name = user.name;
      this.geo = user.geo;
      this.data = CollectionUtils.newConcurrentHashMap(user.data);
      this.unknown = CollectionUtils.newConcurrentHashMap(user.unknown);
   }

   public static User fromMap(@NotNull Map<String, Object> map, @NotNull SentryOptions options) {
      User user = new User();
      Map<String, Object> unknown = null;

      for (Entry<String, Object> entry : map.entrySet()) {
         Object value = entry.getValue();
         String var7 = entry.getKey();
         switch (var7) {
            case "email":
               user.email = value instanceof String ? (String)value : null;
               break;
            case "id":
               user.id = value instanceof String ? (String)value : null;
               break;
            case "username":
               user.username = value instanceof String ? (String)value : null;
               break;
            case "ip_address":
               user.ipAddress = value instanceof String ? (String)value : null;
               break;
            case "name":
               user.name = value instanceof String ? (String)value : null;
               break;
            case "geo":
               Map<Object, Object> geo = value instanceof Map ? (Map)value : null;
               if (geo == null) {
                  break;
               }

               ConcurrentHashMap<String, Object> geoData = new ConcurrentHashMap<>();

               for (Entry<Object, Object> geoEntry : geo.entrySet()) {
                  if (geoEntry.getKey() instanceof String && geoEntry.getValue() != null) {
                     geoData.put((String)geoEntry.getKey(), geoEntry.getValue());
                  } else {
                     options.getLogger().log(SentryLevel.WARNING, "Invalid key type in gep map.");
                  }
               }

               user.geo = Geo.fromMap(geoData);
               break;
            case "data":
               Map<Object, Object> data = value instanceof Map ? (Map)value : null;
               if (data != null) {
                  ConcurrentHashMap<String, String> userData = new ConcurrentHashMap<>();

                  for (Entry<Object, Object> dataEntry : data.entrySet()) {
                     if (dataEntry.getKey() instanceof String && dataEntry.getValue() != null) {
                        userData.put((String)dataEntry.getKey(), dataEntry.getValue().toString());
                     } else {
                        options.getLogger().log(SentryLevel.WARNING, "Invalid key or null value in data map.");
                     }
                  }

                  user.data = userData;
               }
               break;
            default:
               if (unknown == null) {
                  unknown = new ConcurrentHashMap<>();
               }

               unknown.put(entry.getKey(), entry.getValue());
         }
      }

      user.unknown = unknown;
      return user;
   }

   @Nullable
   public String getEmail() {
      return this.email;
   }

   public void setEmail(@Nullable String email) {
      this.email = email;
   }

   @Nullable
   public String getId() {
      return this.id;
   }

   public void setId(@Nullable String id) {
      this.id = id;
   }

   @Nullable
   public String getUsername() {
      return this.username;
   }

   public void setUsername(@Nullable String username) {
      this.username = username;
   }

   @Nullable
   public String getIpAddress() {
      return this.ipAddress;
   }

   public void setIpAddress(@Nullable String ipAddress) {
      this.ipAddress = ipAddress;
   }

   @Deprecated
   @Nullable
   public String getName() {
      return this.name;
   }

   @Deprecated
   public void setName(@Nullable String name) {
      this.name = name;
   }

   @Nullable
   public Geo getGeo() {
      return this.geo;
   }

   public void setGeo(@Nullable Geo geo) {
      this.geo = geo;
   }

   @Nullable
   public Map<String, @NotNull String> getData() {
      return this.data;
   }

   public void setData(@Nullable Map<String, @NotNull String> data) {
      this.data = CollectionUtils.newConcurrentHashMap(data);
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (o != null && this.getClass() == o.getClass()) {
         User user = (User)o;
         return Objects.equals(this.email, user.email)
            && Objects.equals(this.id, user.id)
            && Objects.equals(this.username, user.username)
            && Objects.equals(this.ipAddress, user.ipAddress);
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      return Objects.hash(this.email, this.id, this.username, this.ipAddress);
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
      if (this.email != null) {
         writer.name("email").value(this.email);
      }

      if (this.id != null) {
         writer.name("id").value(this.id);
      }

      if (this.username != null) {
         writer.name("username").value(this.username);
      }

      if (this.ipAddress != null) {
         writer.name("ip_address").value(this.ipAddress);
      }

      if (this.name != null) {
         writer.name("name").value(this.name);
      }

      if (this.geo != null) {
         writer.name("geo");
         this.geo.serialize(writer, logger);
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

   public static final class Deserializer implements JsonDeserializer<User> {
      @NotNull
      public User deserialize(@NotNull ObjectReader reader, @NotNull ILogger logger) throws Exception {
         reader.beginObject();
         User user = new User();
         Map<String, Object> unknown = null;

         while (reader.peek() == JsonToken.NAME) {
            String nextName = reader.nextName();
            switch (nextName) {
               case "email":
                  user.email = reader.nextStringOrNull();
                  break;
               case "id":
                  user.id = reader.nextStringOrNull();
                  break;
               case "username":
                  user.username = reader.nextStringOrNull();
                  break;
               case "ip_address":
                  user.ipAddress = reader.nextStringOrNull();
                  break;
               case "name":
                  user.name = reader.nextStringOrNull();
                  break;
               case "geo":
                  user.geo = new Geo.Deserializer().deserialize(reader, logger);
                  break;
               case "data":
                  user.data = CollectionUtils.newConcurrentHashMap((Map<String, String>)reader.nextObjectOrNull());
                  break;
               default:
                  if (unknown == null) {
                     unknown = new ConcurrentHashMap<>();
                  }

                  reader.nextUnknown(logger, unknown, nextName);
            }
         }

         user.setUnknown(unknown);
         reader.endObject();
         return user;
      }
   }

   public static final class JsonKeys {
      public static final String EMAIL = "email";
      public static final String ID = "id";
      public static final String USERNAME = "username";
      public static final String IP_ADDRESS = "ip_address";
      public static final String NAME = "name";
      public static final String GEO = "geo";
      public static final String DATA = "data";
   }
}
