package com.google.common.flogger.parser;

import com.google.common.flogger.parameter.BraceStyleParameter;

public class DefaultBraceStyleMessageParser extends BraceStyleMessageParser {
   private static final BraceStyleMessageParser INSTANCE = new DefaultBraceStyleMessageParser();

   public static BraceStyleMessageParser getInstance() {
      return INSTANCE;
   }

   private DefaultBraceStyleMessageParser() {
   }

   @Override
   public void parseBraceFormatTerm(MessageBuilder<?> builder, int index, String message, int termStart, int formatStart, int termEnd) throws ParseException {
      if (formatStart != -1) {
         throw ParseException.withBounds("the default brace style parser does not allow trailing format specifiers", message, formatStart - 1, termEnd - 1);
      } else {
         builder.addParameter(termStart, termEnd, BraceStyleParameter.of(index));
      }
   }
}
