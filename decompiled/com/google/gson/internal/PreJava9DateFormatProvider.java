package com.google.gson.internal;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class PreJava9DateFormatProvider {
   private PreJava9DateFormatProvider() {
   }

   public static DateFormat getUsDateTimeFormat(int dateStyle, int timeStyle) {
      String pattern = getDatePartOfDateTimePattern(dateStyle) + " " + getTimePartOfDateTimePattern(timeStyle);
      return new SimpleDateFormat(pattern, Locale.US);
   }

   private static String getDatePartOfDateTimePattern(int dateStyle) {
      switch (dateStyle) {
         case 0:
            return "EEEE, MMMM d, yyyy";
         case 1:
            return "MMMM d, yyyy";
         case 2:
            return "MMM d, yyyy";
         case 3:
            return "M/d/yy";
         default:
            throw new IllegalArgumentException("Unknown DateFormat style: " + dateStyle);
      }
   }

   private static String getTimePartOfDateTimePattern(int timeStyle) {
      switch (timeStyle) {
         case 0:
         case 1:
            return "h:mm:ss a z";
         case 2:
            return "h:mm:ss a";
         case 3:
            return "h:mm a";
         default:
            throw new IllegalArgumentException("Unknown DateFormat style: " + timeStyle);
      }
   }
}
