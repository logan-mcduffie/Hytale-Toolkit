package com.google.common.flogger.parameter;

import com.google.common.flogger.backend.FormatChar;
import com.google.common.flogger.backend.FormatOptions;
import com.google.common.flogger.backend.FormatType;
import java.text.MessageFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class BraceStyleParameter extends Parameter {
   private static final FormatOptions WITH_GROUPING = FormatOptions.of(16, -1, -1);
   private static final MessageFormat prototypeMessageFormatter = new MessageFormat("{0}", Locale.ROOT);
   private static final int MAX_CACHED_PARAMETERS = 10;
   private static final BraceStyleParameter[] DEFAULT_PARAMETERS = new BraceStyleParameter[10];

   public static BraceStyleParameter of(int index) {
      return index < 10 ? DEFAULT_PARAMETERS[index] : new BraceStyleParameter(index);
   }

   private BraceStyleParameter(int index) {
      super(FormatOptions.getDefault(), index);
   }

   @Override
   protected void accept(ParameterVisitor visitor, Object value) {
      if (FormatType.INTEGRAL.canFormat(value)) {
         visitor.visit(value, FormatChar.DECIMAL, WITH_GROUPING);
      } else if (FormatType.FLOAT.canFormat(value)) {
         visitor.visit(value, FormatChar.FLOAT, WITH_GROUPING);
      } else if (value instanceof Date) {
         String formatted = ((MessageFormat)prototypeMessageFormatter.clone()).format(new Object[]{value}, new StringBuffer(), null).toString();
         visitor.visitPreformatted(value, formatted);
      } else if (value instanceof Calendar) {
         visitor.visitDateTime(value, DateTimeFormat.DATETIME_FULL, this.getFormatOptions());
      } else {
         visitor.visit(value, FormatChar.STRING, this.getFormatOptions());
      }
   }

   @Override
   public String getFormat() {
      return "%s";
   }

   static {
      for (int index = 0; index < 10; index++) {
         DEFAULT_PARAMETERS[index] = new BraceStyleParameter(index);
      }
   }
}
