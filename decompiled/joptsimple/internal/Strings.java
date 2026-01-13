package joptsimple.internal;

import java.util.Arrays;
import java.util.Iterator;

public final class Strings {
   public static final String EMPTY = "";
   public static final String LINE_SEPARATOR = System.getProperty("line.separator");

   private Strings() {
      throw new UnsupportedOperationException();
   }

   public static String repeat(char ch, int count) {
      StringBuilder buffer = new StringBuilder();

      for (int i = 0; i < count; i++) {
         buffer.append(ch);
      }

      return buffer.toString();
   }

   public static boolean isNullOrEmpty(String target) {
      return target == null || target.isEmpty();
   }

   public static String surround(String target, char begin, char end) {
      return begin + target + end;
   }

   public static String join(String[] pieces, String separator) {
      return join(Arrays.asList(pieces), separator);
   }

   public static String join(Iterable<String> pieces, String separator) {
      StringBuilder buffer = new StringBuilder();
      Iterator<String> iter = pieces.iterator();

      while (iter.hasNext()) {
         buffer.append(iter.next());
         if (iter.hasNext()) {
            buffer.append(separator);
         }
      }

      return buffer.toString();
   }
}
