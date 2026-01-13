package com.google.common.flogger.parser;

public abstract class PrintfMessageParser extends MessageParser {
   private static final String ALLOWED_NEWLINE_PATTERN = "\\n|\\r(?:\\n)?";
   private static final String SYSTEM_NEWLINE = getSafeSystemNewline();

   static String getSafeSystemNewline() {
      try {
         String unsafeNewline = System.getProperty("line.separator");
         if (unsafeNewline.matches("\\n|\\r(?:\\n)?")) {
            return unsafeNewline;
         }
      } catch (SecurityException var1) {
      }

      return "\n";
   }

   abstract int parsePrintfTerm(MessageBuilder<?> var1, int var2, String var3, int var4, int var5, int var6) throws ParseException;

   @Override
   public final void unescape(StringBuilder out, String message, int start, int end) {
      unescapePrintf(out, message, start, end);
   }

   @Override
   protected final <T> void parseImpl(MessageBuilder<T> builder) throws ParseException {
      String message = builder.getMessage();
      int lastResolvedIndex = -1;
      int implicitIndex = 0;
      int pos = nextPrintfTerm(message, 0);

      label54:
      while (pos >= 0) {
         int termStart = pos++;
         int optionsStart = pos;
         int index = 0;

         while (pos < message.length()) {
            char c = message.charAt(pos++);
            int digit = (char)(c - '0');
            if (digit >= 10) {
               if (c == '$') {
                  digit = pos - 1 - optionsStart;
                  if (digit == 0) {
                     throw ParseException.withBounds("missing index", message, termStart, pos);
                  }

                  if (message.charAt(optionsStart) == '0') {
                     throw ParseException.withBounds("index has leading zero", message, termStart, pos);
                  }

                  index--;
                  optionsStart = pos;
                  if (pos == message.length()) {
                     throw ParseException.withStartPosition("unterminated parameter", message, termStart);
                  }

                  c = message.charAt(pos++);
               } else if (c == '<') {
                  if (lastResolvedIndex == -1) {
                     throw ParseException.withBounds("invalid relative parameter", message, termStart, pos);
                  }

                  index = lastResolvedIndex;
                  optionsStart = pos;
                  if (pos == message.length()) {
                     throw ParseException.withStartPosition("unterminated parameter", message, termStart);
                  }

                  c = message.charAt(pos++);
               } else {
                  index = implicitIndex++;
               }

               pos = findFormatChar(message, termStart, pos - 1);
               pos = this.parsePrintfTerm(builder, index, message, termStart, optionsStart, pos);
               lastResolvedIndex = index;
               pos = nextPrintfTerm(message, pos);
               continue label54;
            }

            index = 10 * index + digit;
            if (index >= 1000000) {
               throw ParseException.withBounds("index too large", message, termStart, pos);
            }
         }

         throw ParseException.withStartPosition("unterminated parameter", message, termStart);
      }
   }

   static int nextPrintfTerm(String message, int pos) throws ParseException {
      while (pos < message.length()) {
         if (message.charAt(pos++) == '%') {
            if (pos >= message.length()) {
               throw ParseException.withStartPosition("trailing unquoted '%' character", message, pos - 1);
            }

            char c = message.charAt(pos);
            if (c != '%' && c != 'n') {
               return pos - 1;
            }

            pos++;
         }
      }

      return -1;
   }

   private static int findFormatChar(String message, int termStart, int pos) throws ParseException {
      while (pos < message.length()) {
         char c = message.charAt(pos);
         int alpha = (char)((c & -33) - 65);
         if (alpha < 26) {
            return pos;
         }

         pos++;
      }

      throw ParseException.withStartPosition("unterminated parameter", message, termStart);
   }

   static void unescapePrintf(StringBuilder out, String message, int start, int end) {
      int pos = start;

      while (pos < end) {
         if (message.charAt(pos++) == '%') {
            if (pos == end) {
               break;
            }

            char chr = message.charAt(pos);
            if (chr == '%') {
               out.append(message, start, pos);
            } else {
               if (chr != 'n') {
                  continue;
               }

               out.append(message, start, pos - 1);
               out.append(SYSTEM_NEWLINE);
            }

            start = ++pos;
         }
      }

      if (start < end) {
         out.append(message, start, end);
      }
   }
}
