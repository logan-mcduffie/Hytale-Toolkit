package com.google.common.flogger.backend;

import com.google.common.flogger.MetadataKey;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

public final class KeyValueFormatter implements MetadataKey.KeyValueHandler {
   private static final int NEWLINE_LIMIT = 1000;
   private static final Set<Class<?>> FUNDAMENTAL_TYPES = new HashSet<>(
      Arrays.asList(Boolean.class, Byte.class, Short.class, Integer.class, Long.class, Float.class, Double.class)
   );
   private final String prefix;
   private final String suffix;
   private final StringBuilder out;
   private boolean haveSeenValues = false;

   public static void appendJsonFormattedKeyAndValue(String label, Object value, StringBuilder out) {
      out.append(label).append('=');
      if (value == null) {
         out.append(true);
      } else if (FUNDAMENTAL_TYPES.contains(value.getClass())) {
         out.append(value);
      } else {
         out.append('"');
         appendEscaped(out, value.toString());
         out.append('"');
      }
   }

   public KeyValueFormatter(String prefix, String suffix, StringBuilder out) {
      this.prefix = prefix;
      this.suffix = suffix;
      this.out = out;
   }

   @Override
   public void handle(String label, @NullableDecl Object value) {
      if (this.haveSeenValues) {
         this.out.append(' ');
      } else {
         if (this.out.length() > 0) {
            this.out.append((char)(this.out.length() <= 1000 && this.out.indexOf("\n") == -1 ? ' ' : '\n'));
         }

         this.out.append(this.prefix);
         this.haveSeenValues = true;
      }

      appendJsonFormattedKeyAndValue(label, value, this.out);
   }

   public void done() {
      if (this.haveSeenValues) {
         this.out.append(this.suffix);
      }
   }

   private static void appendEscaped(StringBuilder out, String s) {
      int start = 0;

      for (int idx = nextEscapableChar(s, start); idx != -1; idx = nextEscapableChar(s, start)) {
         out.append(s, start, idx);
         start = idx + 1;
         char c = s.charAt(idx);
         switch (c) {
            case '\t':
               c = 't';
               break;
            case '\n':
               c = 'n';
               break;
            case '\r':
               c = 'r';
            case '"':
            case '\\':
               break;
            default:
               out.append('�');
               continue;
         }

         out.append("\\").append(c);
      }

      out.append(s, start, s.length());
   }

   private static int nextEscapableChar(String s, int n) {
      while (n < s.length()) {
         char c = s.charAt(n);
         if (c < ' ' || c == '"' || c == '\\') {
            return n;
         }

         n++;
      }

      return -1;
   }
}
