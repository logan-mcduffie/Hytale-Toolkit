package org.jline.style;

import java.util.function.Function;

public final class InterpolationHelper {
   private static final char ESCAPE_CHAR = '\\';
   private static final String DELIM_START = "@{";
   private static final String DELIM_STOP = "}";
   private static final String MARKER = "@__";

   private InterpolationHelper() {
   }

   public static String substVars(String val, Function<String, String> callback, boolean defaultsToEmptyString) throws IllegalArgumentException {
      return unescape(doSubstVars(val, callback, defaultsToEmptyString));
   }

   private static String doSubstVars(String val, Function<String, String> callback, boolean defaultsToEmptyString) throws IllegalArgumentException {
      int stopDelim = -1;

      int startDelim;
      do {
         stopDelim = val.indexOf("}", stopDelim + 1);

         while (stopDelim > 0 && val.charAt(stopDelim - 1) == '\\') {
            stopDelim = val.indexOf("}", stopDelim + 1);
         }

         startDelim = val.indexOf("@{");

         while (stopDelim >= 0) {
            int idx = val.indexOf("@{", startDelim + "@{".length());
            if (idx < 0 || idx > stopDelim) {
               break;
            }

            if (idx < stopDelim) {
               startDelim = idx;
            }
         }
      } while (startDelim >= 0 && stopDelim >= 0 && stopDelim < startDelim + "@{".length());

      if (startDelim >= 0 && stopDelim >= 0) {
         String variable = val.substring(startDelim + "@{".length(), stopDelim);
         String substValue = null;
         if (variable.length() > 0 && callback != null) {
            substValue = callback.apply(variable);
         }

         if (substValue == null) {
            if (defaultsToEmptyString) {
               substValue = "";
            } else {
               substValue = "@__{" + variable + "}";
            }
         }

         val = val.substring(0, startDelim) + substValue + val.substring(stopDelim + "}".length());
         return doSubstVars(val, callback, defaultsToEmptyString);
      } else {
         return val;
      }
   }

   private static String unescape(String val) {
      val = val.replaceAll("@__", "@");

      for (int escape = val.indexOf(92); escape >= 0 && escape < val.length() - 1; escape = val.indexOf(92, escape + 1)) {
         char c = val.charAt(escape + 1);
         if (c == '{' || c == '}' || c == '\\') {
            val = val.substring(0, escape) + val.substring(escape + 1);
         }
      }

      return val;
   }
}
