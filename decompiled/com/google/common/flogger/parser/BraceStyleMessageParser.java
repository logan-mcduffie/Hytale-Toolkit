package com.google.common.flogger.parser;

public abstract class BraceStyleMessageParser extends MessageParser {
   private static final char BRACE_STYLE_SEPARATOR = ',';

   abstract void parseBraceFormatTerm(MessageBuilder<?> var1, int var2, String var3, int var4, int var5, int var6) throws ParseException;

   @Override
   public final void unescape(StringBuilder out, String message, int start, int end) {
      unescapeBraceFormat(out, message, start, end);
   }

   @Override
   protected final <T> void parseImpl(MessageBuilder<T> builder) throws ParseException {
      String message = builder.getMessage();

      label56:
      for (int pos = nextBraceFormatTerm(message, 0); pos >= 0; pos = nextBraceFormatTerm(message, pos)) {
         int termStart = pos++;
         int indexStart = termStart + 1;
         int index = 0;

         while (pos < message.length()) {
            char c = message.charAt(pos++);
            int digit = (char)(c - '0');
            if (digit >= 10) {
               digit = pos - 1 - indexStart;
               if (digit == 0) {
                  throw ParseException.withBounds("missing index", message, termStart, pos);
               }

               if (message.charAt(indexStart) == '0' && digit > 1) {
                  throw ParseException.withBounds("index has leading zero", message, indexStart, pos - 1);
               }

               int trailingPartStart;
               if (c == '}') {
                  trailingPartStart = -1;
               } else {
                  if (c != ',') {
                     throw ParseException.withBounds("malformed index", message, termStart + 1, pos);
                  }

                  trailingPartStart = pos;

                  do {
                     if (pos == message.length()) {
                        throw ParseException.withStartPosition("unterminated parameter", message, termStart);
                     }
                  } while (message.charAt(pos++) != '}');
               }

               this.parseBraceFormatTerm(builder, index, message, termStart, trailingPartStart, pos);
               continue label56;
            }

            index = 10 * index + digit;
            if (index >= 1000000) {
               throw ParseException.withBounds("index too large", message, indexStart, pos);
            }
         }

         throw ParseException.withStartPosition("unterminated parameter", message, termStart);
      }
   }

   static int nextBraceFormatTerm(String message, int pos) throws ParseException {
      label34:
      while (pos < message.length()) {
         char c = message.charAt(pos++);
         if (c == '{') {
            return pos - 1;
         }

         if (c == '\'') {
            if (pos == message.length()) {
               throw ParseException.withStartPosition("trailing single quote", message, pos - 1);
            }

            if (message.charAt(pos++) != '\'') {
               int quote = pos - 2;

               while (pos != message.length()) {
                  if (message.charAt(pos++) == '\'') {
                     continue label34;
                  }
               }

               throw ParseException.withStartPosition("unmatched single quote", message, quote);
            }
         }
      }

      return -1;
   }

   static void unescapeBraceFormat(StringBuilder out, String message, int start, int end) {
      int pos = start;
      boolean isQuoted = false;

      while (pos < end) {
         char c = message.charAt(pos++);
         if (c == '\\' || c == '\'') {
            int quoteStart = pos - 1;
            if (c == '\\') {
               c = message.charAt(pos++);
               if (c != '\'') {
                  continue;
               }
            }

            out.append(message, start, quoteStart);
            start = pos;
            if (pos == end) {
               break;
            }

            if (isQuoted) {
               isQuoted = false;
            } else if (message.charAt(pos) != '\'') {
               isQuoted = true;
            } else {
               pos++;
            }
         }
      }

      if (start < end) {
         out.append(message, start, end);
      }
   }
}
