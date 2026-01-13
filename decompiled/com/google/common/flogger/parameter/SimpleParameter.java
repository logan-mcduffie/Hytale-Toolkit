package com.google.common.flogger.parameter;

import com.google.common.flogger.backend.FormatChar;
import com.google.common.flogger.backend.FormatOptions;
import com.google.common.flogger.util.Checks;
import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;

public final class SimpleParameter extends Parameter {
   private static final int MAX_CACHED_PARAMETERS = 10;
   private static final Map<FormatChar, SimpleParameter[]> DEFAULT_PARAMETERS;
   private final FormatChar formatChar;
   private final String formatString;

   private static SimpleParameter[] createParameterArray(FormatChar formatChar) {
      SimpleParameter[] parameters = new SimpleParameter[10];

      for (int index = 0; index < 10; index++) {
         parameters[index] = new SimpleParameter(index, formatChar, FormatOptions.getDefault());
      }

      return parameters;
   }

   public static SimpleParameter of(int index, FormatChar formatChar, FormatOptions options) {
      return index < 10 && options.isDefault() ? DEFAULT_PARAMETERS.get(formatChar)[index] : new SimpleParameter(index, formatChar, options);
   }

   private SimpleParameter(int index, FormatChar formatChar, FormatOptions options) {
      super(options, index);
      this.formatChar = Checks.checkNotNull(formatChar, "format char");
      this.formatString = options.isDefault() ? formatChar.getDefaultFormatString() : buildFormatString(options, formatChar);
   }

   static String buildFormatString(FormatOptions options, FormatChar formatChar) {
      char c = formatChar.getChar();
      c = options.shouldUpperCase() ? (char)(c & -33) : c;
      return options.appendPrintfOptions(new StringBuilder("%")).append(c).toString();
   }

   @Override
   protected void accept(ParameterVisitor visitor, Object value) {
      visitor.visit(value, this.formatChar, this.getFormatOptions());
   }

   @Override
   public String getFormat() {
      return this.formatString;
   }

   static {
      Map<FormatChar, SimpleParameter[]> map = new EnumMap<>(FormatChar.class);

      for (FormatChar fc : FormatChar.values()) {
         map.put(fc, createParameterArray(fc));
      }

      DEFAULT_PARAMETERS = Collections.unmodifiableMap(map);
   }
}
