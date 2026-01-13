package com.nimbusds.jose.util;

import com.nimbusds.jose.shaded.gson.Gson;
import com.nimbusds.jose.shaded.gson.GsonBuilder;
import com.nimbusds.jose.shaded.gson.Strictness;
import com.nimbusds.jose.shaded.gson.ToNumberPolicy;
import com.nimbusds.jose.shaded.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class JSONArrayUtils {
   private static final Gson GSON = new GsonBuilder()
      .setStrictness(Strictness.STRICT)
      .serializeNulls()
      .setObjectToNumberStrategy(ToNumberPolicy.LONG_OR_DOUBLE)
      .disableHtmlEscaping()
      .create();

   public static List<Object> parse(String s) throws ParseException {
      if (s == null) {
         throw new ParseException("The JSON array string must not be null", 0);
      } else if (s.trim().isEmpty()) {
         throw new ParseException("Invalid JSON array", 0);
      } else {
         Type listType = TypeToken.getParameterized(List.class, Object.class).getType();

         try {
            return GSON.fromJson(s, listType);
         } catch (Exception var3) {
            throw new ParseException("Invalid JSON array", 0);
         } catch (StackOverflowError var4) {
            throw new ParseException("Excessive JSON object and / or array nesting", 0);
         }
      }
   }

   public static String toJSONString(List<?> jsonArray) {
      return GSON.toJson(Objects.requireNonNull(jsonArray));
   }

   public static List<Object> newJSONArray() {
      return new ArrayList<>();
   }

   private JSONArrayUtils() {
   }
}
