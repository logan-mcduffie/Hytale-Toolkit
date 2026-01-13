package com.google.common.flogger.backend;

public enum FormatChar {
   STRING('s', FormatType.GENERAL, "-#", true),
   BOOLEAN('b', FormatType.BOOLEAN, "-", true),
   CHAR('c', FormatType.CHARACTER, "-", true),
   DECIMAL('d', FormatType.INTEGRAL, "-0+ ,(", false),
   OCTAL('o', FormatType.INTEGRAL, "-#0(", false),
   HEX('x', FormatType.INTEGRAL, "-#0(", true),
   FLOAT('f', FormatType.FLOAT, "-#0+ ,(", false),
   EXPONENT('e', FormatType.FLOAT, "-#0+ (", true),
   GENERAL('g', FormatType.FLOAT, "-0+ ,(", true),
   EXPONENT_HEX('a', FormatType.FLOAT, "-#0+ ", true);

   private static final FormatChar[] MAP = new FormatChar[26];
   private final char formatChar;
   private final FormatType type;
   private final int allowedFlags;
   private final String defaultFormatString;

   private static int indexOf(char letter) {
      return (letter | 32) - 97;
   }

   private static boolean isLowerCase(char letter) {
      return (letter & ' ') != 0;
   }

   public static FormatChar of(char c) {
      FormatChar fc = MAP[indexOf(c)];
      if (isLowerCase(c)) {
         return fc;
      } else {
         return fc != null && fc.hasUpperCaseVariant() ? fc : null;
      }
   }

   private FormatChar(char c, FormatType type, String allowedFlagChars, boolean hasUpperCaseVariant) {
      this.formatChar = c;
      this.type = type;
      this.allowedFlags = FormatOptions.parseValidFlags(allowedFlagChars, hasUpperCaseVariant);
      this.defaultFormatString = "%" + c;
   }

   public char getChar() {
      return this.formatChar;
   }

   public FormatType getType() {
      return this.type;
   }

   int getAllowedFlags() {
      return this.allowedFlags;
   }

   private boolean hasUpperCaseVariant() {
      return (this.allowedFlags & 128) != 0;
   }

   public String getDefaultFormatString() {
      return this.defaultFormatString;
   }

   static {
      for (FormatChar fc : values()) {
         MAP[indexOf(fc.getChar())] = fc;
      }
   }
}
