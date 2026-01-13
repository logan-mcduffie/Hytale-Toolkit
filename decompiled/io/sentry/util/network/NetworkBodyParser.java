package io.sentry.util.network;

import io.sentry.ILogger;
import io.sentry.SentryLevel;
import io.sentry.vendor.gson.stream.JsonReader;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.ApiStatus.Internal;

@Internal
public final class NetworkBodyParser {
   private NetworkBodyParser() {
   }

   @Nullable
   public static NetworkBody fromBytes(
      @Nullable byte[] bytes, @Nullable String contentType, @Nullable String charset, int maxSizeBytes, @NotNull ILogger logger
   ) {
      if (bytes == null || bytes.length == 0) {
         return null;
      } else if (contentType != null && isBinaryContentType(contentType)) {
         return new NetworkBody("[Binary data, " + bytes.length + " bytes, type: " + contentType + "]");
      } else {
         try {
            String effectiveCharset = charset != null ? charset : "UTF-8";
            int size = Math.min(bytes.length, maxSizeBytes);
            boolean isPartial = bytes.length > maxSizeBytes;
            String content = new String(bytes, 0, size, effectiveCharset);
            return parse(content, contentType, isPartial, logger);
         } catch (UnsupportedEncodingException var9) {
            logger.log(SentryLevel.WARNING, "Failed to decode bytes: " + var9.getMessage());
            return new NetworkBody(
               "[Failed to decode bytes, " + bytes.length + " bytes]", Collections.singletonList(NetworkBody.NetworkBodyWarning.BODY_PARSE_ERROR)
            );
         }
      }
   }

   @Nullable
   private static NetworkBody parse(@Nullable String content, @Nullable String contentType, boolean isPartial, @Nullable ILogger logger) {
      if (content != null && !content.isEmpty()) {
         if (contentType != null) {
            String lowerContentType = contentType.toLowerCase(Locale.ROOT);
            if (lowerContentType.contains("application/x-www-form-urlencoded")) {
               return parseFormUrlEncoded(content, isPartial, logger);
            }

            if (lowerContentType.contains("application/json")) {
               return parseJson(content, isPartial, logger);
            }
         }

         List<NetworkBody.NetworkBodyWarning> warnings = isPartial ? Collections.singletonList(NetworkBody.NetworkBodyWarning.TEXT_TRUNCATED) : null;
         return new NetworkBody(content, warnings);
      } else {
         return null;
      }
   }

   @NotNull
   private static NetworkBody parseJson(@NotNull String content, boolean isPartial, @Nullable ILogger logger) {
      try {
         JsonReader reader = new JsonReader(new StringReader(content));

         NetworkBody var11;
         label57: {
            NetworkBody var7;
            try {
               NetworkBodyParser.SaferJsonParser.Result result = NetworkBodyParser.SaferJsonParser.parse(reader);
               Object data = result.data;
               if (data == null && !isPartial && !result.errored && !result.hitMaxDepth) {
                  var11 = new NetworkBody(null);
                  break label57;
               }

               List<NetworkBody.NetworkBodyWarning> warnings;
               if (isPartial || result.hitMaxDepth) {
                  warnings = Collections.singletonList(NetworkBody.NetworkBodyWarning.JSON_TRUNCATED);
               } else if (result.errored) {
                  warnings = Collections.singletonList(NetworkBody.NetworkBodyWarning.INVALID_JSON);
               } else {
                  warnings = null;
               }

               var7 = new NetworkBody(data, warnings);
            } catch (Throwable var9) {
               try {
                  reader.close();
               } catch (Throwable var8) {
                  var9.addSuppressed(var8);
               }

               throw var9;
            }

            reader.close();
            return var7;
         }

         reader.close();
         return var11;
      } catch (Exception var10) {
         if (logger != null) {
            logger.log(SentryLevel.WARNING, "Failed to parse JSON: " + var10.getMessage());
         }

         return new NetworkBody(null, Collections.singletonList(NetworkBody.NetworkBodyWarning.INVALID_JSON));
      }
   }

   @NotNull
   private static NetworkBody parseFormUrlEncoded(@NotNull String content, boolean isPartial, @Nullable ILogger logger) {
      try {
         Map<String, Object> params = new HashMap<>();
         String[] pairs = content.split("&", -1);

         for (String pair : pairs) {
            int idx = pair.indexOf("=");
            if (idx > 0) {
               String key = URLDecoder.decode(pair.substring(0, idx), "UTF-8");
               String value = idx < pair.length() - 1 ? URLDecoder.decode(pair.substring(idx + 1), "UTF-8") : "";
               if (params.containsKey(key)) {
                  Object existing = params.get(key);
                  if (existing instanceof List) {
                     List<String> list = (List<String>)existing;
                     list.add(value);
                  } else {
                     List<String> list = new ArrayList<>();
                     list.add((String)existing);
                     list.add(value);
                     params.put(key, list);
                  }
               } else {
                  params.put(key, value);
               }
            }
         }

         List<NetworkBody.NetworkBodyWarning> warnings;
         if (isPartial) {
            warnings = Collections.singletonList(NetworkBody.NetworkBodyWarning.TEXT_TRUNCATED);
         } else {
            warnings = null;
         }

         return new NetworkBody(params, warnings);
      } catch (UnsupportedEncodingException var14) {
         if (logger != null) {
            logger.log(SentryLevel.WARNING, "Failed to parse form data: " + var14.getMessage());
         }

         return new NetworkBody(null, Collections.singletonList(NetworkBody.NetworkBodyWarning.BODY_PARSE_ERROR));
      }
   }

   private static boolean isBinaryContentType(@NotNull String contentType) {
      String lower = contentType.toLowerCase(Locale.ROOT);
      return lower.contains("image/")
         || lower.contains("video/")
         || lower.contains("audio/")
         || lower.contains("application/octet-stream")
         || lower.contains("application/pdf")
         || lower.contains("application/zip")
         || lower.contains("application/gzip");
   }

   private static class SaferJsonParser {
      private static final int MAX_DEPTH = 100;
      final NetworkBodyParser.SaferJsonParser.Result result = new NetworkBodyParser.SaferJsonParser.Result();

      @NotNull
      public static NetworkBodyParser.SaferJsonParser.Result parse(@NotNull JsonReader reader) {
         NetworkBodyParser.SaferJsonParser parser = new NetworkBodyParser.SaferJsonParser();
         parser.result.data = parser.parse(reader, 0);
         return parser.result;
      }

      @Nullable
      private Object parse(@NotNull JsonReader reader, int currentDepth) {
         if (this.result.errored) {
            return null;
         } else if (currentDepth >= 100) {
            this.result.hitMaxDepth = true;
            return null;
         } else {
            try {
               switch (reader.peek()) {
                  case BEGIN_OBJECT:
                     Map<String, Object> map = new LinkedHashMap<>();

                     try {
                        reader.beginObject();

                        while (reader.hasNext() && !this.result.errored) {
                           String name = reader.nextName();
                           map.put(name, this.parse(reader, currentDepth + 1));
                        }

                        reader.endObject();
                     } catch (Exception var7) {
                        this.result.errored = true;
                        return map;
                     }

                     return map;
                  case BEGIN_ARRAY:
                     List<Object> list = new ArrayList<>();

                     try {
                        reader.beginArray();

                        while (reader.hasNext() && !this.result.errored) {
                           list.add(this.parse(reader, currentDepth + 1));
                        }

                        reader.endArray();
                     } catch (Exception var6) {
                        this.result.errored = true;
                        return list;
                     }

                     return list;
                  case STRING:
                     return reader.nextString();
                  case NUMBER:
                     return reader.nextDouble();
                  case BOOLEAN:
                     return reader.nextBoolean();
                  case NULL:
                     reader.nextNull();
                     return null;
                  default:
                     this.result.errored = true;
                     return null;
               }
            } catch (Exception var8) {
               this.result.errored = true;
               return null;
            }
         }
      }

      private static class Result {
         @Nullable
         private Object data;
         private boolean hitMaxDepth;
         private boolean errored;

         private Result() {
         }
      }
   }
}
