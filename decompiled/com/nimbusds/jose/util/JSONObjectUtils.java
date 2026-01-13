package com.nimbusds.jose.util;

import com.nimbusds.jose.shaded.gson.Gson;
import com.nimbusds.jose.shaded.gson.GsonBuilder;
import com.nimbusds.jose.shaded.gson.Strictness;
import com.nimbusds.jose.shaded.gson.ToNumberPolicy;
import com.nimbusds.jose.shaded.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class JSONObjectUtils {
   private static final Gson GSON = new GsonBuilder()
      .setStrictness(Strictness.STRICT)
      .serializeNulls()
      .setObjectToNumberStrategy(ToNumberPolicy.LONG_OR_DOUBLE)
      .disableHtmlEscaping()
      .create();

   public static Map<String, Object> parse(String s) throws ParseException {
      return parse(s, -1);
   }

   public static Map<String, Object> parse(String s, int sizeLimit) throws ParseException {
      if (s == null) {
         throw new ParseException("The JSON object string must not be null", 0);
      } else if (s.trim().isEmpty()) {
         throw new ParseException("Invalid JSON object", 0);
      } else if (sizeLimit >= 0 && s.length() > sizeLimit) {
         throw new ParseException("The parsed string is longer than the max accepted size of " + sizeLimit + " characters", 0);
      } else {
         Type mapType = TypeToken.getParameterized(Map.class, String.class, Object.class).getType();

         try {
            return GSON.fromJson(s, mapType);
         } catch (Exception var4) {
            throw new ParseException("Invalid JSON object", 0);
         } catch (StackOverflowError var5) {
            throw new ParseException("Excessive JSON object and / or array nesting", 0);
         }
      }
   }

   @Deprecated
   public static Map<String, Object> parseJSONObject(String s) throws ParseException {
      return parse(s);
   }

   private static <T> T getGeneric(Map<String, Object> o, String name, Class<T> clazz) throws ParseException {
      if (o.get(name) == null) {
         return null;
      } else {
         Object value = o.get(name);
         if (!clazz.isAssignableFrom(value.getClass())) {
            throw new ParseException("Unexpected type of JSON object member " + name + "", 0);
         } else {
            return (T)value;
         }
      }
   }

   public static boolean getBoolean(Map<String, Object> o, String name) throws ParseException {
      Boolean value = getGeneric(o, name, Boolean.class);
      if (value == null) {
         throw new ParseException("JSON object member " + name + " is missing or null", 0);
      } else {
         return value;
      }
   }

   public static int getInt(Map<String, Object> o, String name) throws ParseException {
      Number value = getGeneric(o, name, Number.class);
      if (value == null) {
         throw new ParseException("JSON object member " + name + " is missing or null", 0);
      } else {
         return value.intValue();
      }
   }

   public static long getLong(Map<String, Object> o, String name) throws ParseException {
      Number value = getGeneric(o, name, Number.class);
      if (value == null) {
         throw new ParseException("JSON object member " + name + " is missing or null", 0);
      } else {
         return value.longValue();
      }
   }

   public static float getFloat(Map<String, Object> o, String name) throws ParseException {
      Number value = getGeneric(o, name, Number.class);
      if (value == null) {
         throw new ParseException("JSON object member " + name + " is missing or null", 0);
      } else {
         return value.floatValue();
      }
   }

   public static double getDouble(Map<String, Object> o, String name) throws ParseException {
      Number value = getGeneric(o, name, Number.class);
      if (value == null) {
         throw new ParseException("JSON object member " + name + " is missing or null", 0);
      } else {
         return value.doubleValue();
      }
   }

   public static String getString(Map<String, Object> o, String name) throws ParseException {
      return getGeneric(o, name, String.class);
   }

   public static URI getURI(Map<String, Object> o, String name) throws ParseException {
      String value = getString(o, name);
      if (value == null) {
         return null;
      } else {
         try {
            return new URI(value);
         } catch (URISyntaxException var4) {
            throw new ParseException(var4.getMessage(), 0);
         }
      }
   }

   public static List<Object> getJSONArray(Map<String, Object> o, String name) throws ParseException {
      return getGeneric(o, name, List.class);
   }

   public static String[] getStringArray(Map<String, Object> o, String name) throws ParseException {
      List<Object> jsonArray = getJSONArray(o, name);
      if (jsonArray == null) {
         return null;
      } else {
         try {
            return jsonArray.toArray(new String[0]);
         } catch (ArrayStoreException var4) {
            throw new ParseException("JSON object member " + name + " is not an array of strings", 0);
         }
      }
   }

   public static Map<String, Object>[] getJSONObjectArray(Map<String, Object> o, String name) throws ParseException {
      List<Object> jsonArray = getJSONArray(o, name);
      if (jsonArray == null) {
         return null;
      } else if (jsonArray.isEmpty()) {
         return new HashMap[0];
      } else {
         for (Object member : jsonArray) {
            if (member != null && member instanceof Map) {
               try {
                  return jsonArray.toArray(new Map[0]);
               } catch (ArrayStoreException var6) {
                  break;
               }
            }
         }

         throw new ParseException("JSON object member " + name + " is not an array of JSON objects", 0);
      }
   }

   public static List<String> getStringList(Map<String, Object> o, String name) throws ParseException {
      String[] array = getStringArray(o, name);
      return array == null ? null : Arrays.asList(array);
   }

   public static Map<String, Object> getJSONObject(Map<String, Object> o, String name) throws ParseException {
      Map<?, ?> jsonObject = getGeneric(o, name, Map.class);
      if (jsonObject == null) {
         return null;
      } else {
         for (Object oKey : jsonObject.keySet()) {
            if (!(oKey instanceof String)) {
               throw new ParseException("JSON object member " + name + " not a JSON object", 0);
            }
         }

         return (Map<String, Object>)jsonObject;
      }
   }

   public static Base64URL getBase64URL(Map<String, Object> o, String name) throws ParseException {
      String value = getString(o, name);
      return value == null ? null : new Base64URL(value);
   }

   public static Date getEpochSecondAsDate(Map<String, Object> o, String name) throws ParseException {
      Number value = getGeneric(o, name, Number.class);
      return value == null ? null : com.nimbusds.jwt.util.DateUtils.fromSecondsSinceEpoch(value.longValue());
   }

   public static String toJSONString(Map<String, ?> o) {
      return GSON.toJson(Objects.requireNonNull(o));
   }

   public static Map<String, Object> newJSONObject() {
      return new HashMap<>();
   }

   private JSONObjectUtils() {
   }
}
