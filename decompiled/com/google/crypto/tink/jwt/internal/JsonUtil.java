package com.google.crypto.tink.jwt.internal;

import com.google.crypto.tink.internal.JsonParser;
import com.google.crypto.tink.jwt.JwtInvalidException;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.io.IOException;

public final class JsonUtil {
   public static boolean isValidString(String s) {
      return JsonParser.isValidString(s);
   }

   public static JsonObject parseJson(String jsonString) throws JwtInvalidException {
      try {
         return JsonParser.parse(jsonString).getAsJsonObject();
      } catch (JsonParseException | IOException | IllegalStateException var2) {
         throw new JwtInvalidException("invalid JSON: " + var2);
      }
   }

   public static JsonArray parseJsonArray(String jsonString) throws JwtInvalidException {
      try {
         return JsonParser.parse(jsonString).getAsJsonArray();
      } catch (JsonParseException | IOException | IllegalStateException var2) {
         throw new JwtInvalidException("invalid JSON: " + var2);
      }
   }

   private JsonUtil() {
   }
}
