package com.google.common.flogger.parser;

import com.google.common.flogger.backend.FormatChar;
import com.google.common.flogger.backend.FormatOptions;
import com.google.common.flogger.parameter.DateTimeFormat;
import com.google.common.flogger.parameter.DateTimeParameter;
import com.google.common.flogger.parameter.Parameter;
import com.google.common.flogger.parameter.ParameterVisitor;
import com.google.common.flogger.parameter.SimpleParameter;

public class DefaultPrintfMessageParser extends PrintfMessageParser {
   private static final PrintfMessageParser INSTANCE = new DefaultPrintfMessageParser();

   public static PrintfMessageParser getInstance() {
      return INSTANCE;
   }

   private DefaultPrintfMessageParser() {
   }

   @Override
   public int parsePrintfTerm(MessageBuilder<?> builder, int index, String message, int termStart, int specStart, int formatStart) throws ParseException {
      int termEnd = formatStart + 1;
      char typeChar = message.charAt(formatStart);
      boolean isUpperCase = (typeChar & ' ') == 0;
      FormatOptions options = FormatOptions.parse(message, specStart, formatStart, isUpperCase);
      FormatChar formatChar = FormatChar.of(typeChar);
      Parameter parameter;
      if (formatChar != null) {
         if (!options.areValidFor(formatChar)) {
            throw ParseException.withBounds("invalid format specifier", message, termStart, termEnd);
         }

         parameter = SimpleParameter.of(index, formatChar, options);
      } else if (typeChar != 't' && typeChar != 'T') {
         if (typeChar != 'h' && typeChar != 'H') {
            throw ParseException.withBounds("invalid format specification", message, termStart, formatStart + 1);
         }

         if (!options.validate(160, false)) {
            throw ParseException.withBounds("invalid format specification", message, termStart, termEnd);
         }

         parameter = wrapHexParameter(options, index);
      } else {
         if (!options.validate(160, false)) {
            throw ParseException.withBounds("invalid format specification", message, termStart, termEnd);
         }

         if (++termEnd > message.length()) {
            throw ParseException.atPosition("truncated format specifier", message, termStart);
         }

         DateTimeFormat dateTimeFormat = DateTimeFormat.of(message.charAt(formatStart + 1));
         if (dateTimeFormat == null) {
            throw ParseException.atPosition("illegal date/time conversion", message, formatStart + 1);
         }

         parameter = DateTimeParameter.of(dateTimeFormat, options, index);
      }

      builder.addParameter(termStart, termEnd, parameter);
      return termEnd;
   }

   private static Parameter wrapHexParameter(final FormatOptions options, int index) {
      return new Parameter(options, index) {
         @Override
         protected void accept(ParameterVisitor visitor, Object value) {
            visitor.visit(value.hashCode(), FormatChar.HEX, this.getFormatOptions());
         }

         @Override
         public String getFormat() {
            return options.shouldUpperCase() ? "%H" : "%h";
         }
      };
   }
}
