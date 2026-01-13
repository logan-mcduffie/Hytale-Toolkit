package com.nimbusds.jose.util;

import com.nimbusds.jose.shaded.gson.Gson;

public class JSONStringUtils {
   public static String toJSONString(String string) {
      return new Gson().toJson(string);
   }

   private JSONStringUtils() {
   }
}
