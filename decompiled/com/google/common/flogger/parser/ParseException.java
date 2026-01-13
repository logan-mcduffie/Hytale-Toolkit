package com.google.common.flogger.parser;

public final class ParseException extends RuntimeException {
   private static final String ELLIPSIS = "...";
   private static final int SNIPPET_LENGTH = 5;

   public static ParseException withBounds(String errorMessage, String logMessage, int start, int end) {
      return new ParseException(msg(errorMessage, logMessage, start, end), logMessage);
   }

   public static ParseException atPosition(String errorMessage, String logMessage, int position) {
      return new ParseException(msg(errorMessage, logMessage, position, position + 1), logMessage);
   }

   public static ParseException withStartPosition(String errorMessage, String logMessage, int start) {
      return new ParseException(msg(errorMessage, logMessage, start, -1), logMessage);
   }

   public static ParseException generic(String errorMessage, String logMessage) {
      return new ParseException(errorMessage, logMessage);
   }

   private ParseException(String errorMessage, String logMessage) {
      super(errorMessage);
   }

   private static String msg(String errorMessage, String logMessage, int errorStart, int errorEnd) {
      if (errorEnd < 0) {
         errorEnd = logMessage.length();
      }

      StringBuilder out = new StringBuilder(errorMessage).append(": ");
      if (errorStart > 5 + "...".length()) {
         out.append("...").append(logMessage, errorStart - 5, errorStart);
      } else {
         out.append(logMessage, 0, errorStart);
      }

      out.append('[').append(logMessage.substring(errorStart, errorEnd)).append(']');
      if (logMessage.length() - errorEnd > 5 + "...".length()) {
         out.append(logMessage, errorEnd, errorEnd + 5).append("...");
      } else {
         out.append(logMessage, errorEnd, logMessage.length());
      }

      return out.toString();
   }

   @Override
   public synchronized Throwable fillInStackTrace() {
      return this;
   }
}
