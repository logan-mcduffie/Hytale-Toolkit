package com.google.common.flogger.backend;

import com.google.common.flogger.parameter.DateTimeFormat;
import com.google.common.flogger.parameter.Parameter;
import com.google.common.flogger.parameter.ParameterVisitor;
import com.google.common.flogger.parser.MessageBuilder;
import com.google.common.flogger.util.Checks;
import java.util.Calendar;
import java.util.Date;
import java.util.Formattable;

public class BaseMessageFormatter extends MessageBuilder<StringBuilder> implements ParameterVisitor {
   private static final String MISSING_ARGUMENT_MESSAGE = "[ERROR: MISSING LOG ARGUMENT]";
   private static final String EXTRA_ARGUMENT_MESSAGE = " [ERROR: UNUSED LOG ARGUMENTS]";
   protected final Object[] args;
   protected final StringBuilder out;
   private int literalStart = 0;

   public static StringBuilder appendFormattedMessage(LogData data, StringBuilder out) {
      if (data.getTemplateContext() != null) {
         BaseMessageFormatter formatter = new BaseMessageFormatter(data.getTemplateContext(), data.getArguments(), out);
         out = formatter.build();
         if (data.getArguments().length > formatter.getExpectedArgumentCount()) {
            out.append(" [ERROR: UNUSED LOG ARGUMENTS]");
         }
      } else {
         out.append(MessageUtils.safeToString(data.getLiteralArgument()));
      }

      return out;
   }

   protected BaseMessageFormatter(TemplateContext context, Object[] args, StringBuilder out) {
      super(context);
      this.args = Checks.checkNotNull(args, "arguments");
      this.out = Checks.checkNotNull(out, "buffer");
   }

   private static void appendFormatted(StringBuilder out, Object value, FormatChar format, FormatOptions options) {
      switch (format) {
         case STRING:
            if (value instanceof Formattable) {
               MessageUtils.safeFormatTo((Formattable)value, out, options);
               return;
            }

            if (options.isDefault()) {
               out.append(MessageUtils.safeToString(value));
               return;
            }
            break;
         case DECIMAL:
         case BOOLEAN:
            if (options.isDefault()) {
               out.append(value);
               return;
            }
            break;
         case HEX:
            if (options.filter(128, false, false).equals(options)) {
               MessageUtils.appendHex(out, (Number)value, options);
               return;
            }
            break;
         case CHAR:
            if (options.isDefault()) {
               if (value instanceof Character) {
                  out.append(value);
                  return;
               }

               int codePoint = ((Number)value).intValue();
               if (codePoint >>> 16 == 0) {
                  out.append((char)codePoint);
                  return;
               }

               out.append(Character.toChars(codePoint));
               return;
            }
      }

      String formatString = format.getDefaultFormatString();
      if (!options.isDefault()) {
         char chr = format.getChar();
         if (options.shouldUpperCase()) {
            chr &= '\uffdf';
         }

         formatString = options.appendPrintfOptions(new StringBuilder("%")).append(chr).toString();
      }

      out.append(String.format(MessageUtils.FORMAT_LOCALE, formatString, value));
   }

   @Override
   public void addParameterImpl(int termStart, int termEnd, Parameter param) {
      this.getParser().unescape(this.out, this.getMessage(), this.literalStart, termStart);
      param.accept(this, this.args);
      this.literalStart = termEnd;
   }

   public StringBuilder buildImpl() {
      this.getParser().unescape(this.out, this.getMessage(), this.literalStart, this.getMessage().length());
      return this.out;
   }

   @Override
   public void visit(Object value, FormatChar format, FormatOptions options) {
      if (format.getType().canFormat(value)) {
         appendFormatted(this.out, value, format, options);
      } else {
         appendInvalid(this.out, value, format.getDefaultFormatString());
      }
   }

   @Override
   public void visitDateTime(Object value, DateTimeFormat format, FormatOptions options) {
      if (!(value instanceof Date) && !(value instanceof Calendar) && !(value instanceof Long)) {
         appendInvalid(this.out, value, "%t" + format.getChar());
      } else {
         String formatString = options.appendPrintfOptions(new StringBuilder("%"))
            .append((char)(options.shouldUpperCase() ? 'T' : 't'))
            .append(format.getChar())
            .toString();
         this.out.append(String.format(MessageUtils.FORMAT_LOCALE, formatString, value));
      }
   }

   @Override
   public void visitPreformatted(Object value, String formatted) {
      this.out.append(formatted);
   }

   @Override
   public void visitMissing() {
      this.out.append("[ERROR: MISSING LOG ARGUMENT]");
   }

   @Override
   public void visitNull() {
      this.out.append("null");
   }

   private static void appendInvalid(StringBuilder out, Object value, String formatString) {
      out.append("[INVALID: format=")
         .append(formatString)
         .append(", type=")
         .append(value.getClass().getCanonicalName())
         .append(", value=")
         .append(MessageUtils.safeToString(value))
         .append("]");
   }
}
