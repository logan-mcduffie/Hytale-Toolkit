package com.google.common.flogger.backend;

import com.google.common.flogger.parser.ParseException;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

public final class FormatOptions {
   private static final int MAX_ALLOWED_WIDTH = 999999;
   private static final int MAX_ALLOWED_PRECISION = 999999;
   private static final String FLAG_CHARS_ORDERED = " #(+,-0";
   private static final int MIN_FLAG_VALUE = 32;
   private static final int MAX_FLAG_VALUE = 48;
   private static final long ENCODED_FLAG_INDICES;
   public static final int FLAG_PREFIX_SPACE_FOR_POSITIVE_VALUES = 1;
   public static final int FLAG_SHOW_ALT_FORM = 2;
   public static final int FLAG_USE_PARENS_FOR_NEGATIVE_VALUES = 4;
   public static final int FLAG_PREFIX_PLUS_FOR_POSITIVE_VALUES = 8;
   public static final int FLAG_SHOW_GROUPING = 16;
   public static final int FLAG_LEFT_ALIGN = 32;
   public static final int FLAG_SHOW_LEADING_ZEROS = 64;
   public static final int FLAG_UPPER_CASE = 128;
   public static final int ALL_FLAGS = 255;
   public static final int UNSET = -1;
   private static final FormatOptions DEFAULT;
   private final int flags;
   private final int width;
   private final int precision;

   private static int indexOfFlagCharacter(char c) {
      return (int)(ENCODED_FLAG_INDICES >>> 3 * (c - 32) & 7L) - 1;
   }

   public static FormatOptions getDefault() {
      return DEFAULT;
   }

   public static FormatOptions of(int flags, int width, int precision) {
      if (!checkFlagConsistency(flags, width != -1)) {
         throw new IllegalArgumentException("invalid flags: 0x" + Integer.toHexString(flags));
      } else if ((width < 1 || width > 999999) && width != -1) {
         throw new IllegalArgumentException("invalid width: " + width);
      } else if ((precision < 0 || precision > 999999) && precision != -1) {
         throw new IllegalArgumentException("invalid precision: " + precision);
      } else {
         return new FormatOptions(flags, width, precision);
      }
   }

   public static FormatOptions parse(String message, int pos, int end, boolean isUpperCase) throws ParseException {
      if (pos == end && !isUpperCase) {
         return DEFAULT;
      } else {
         int flags = isUpperCase ? 128 : 0;

         while (pos != end) {
            char c = message.charAt(pos++);
            if (c < ' ' || c > '0') {
               int widthStart = pos - 1;
               if (c > '9') {
                  throw ParseException.atPosition("invalid flag", message, widthStart);
               } else {
                  int width = c - '0';

                  while (pos != end) {
                     c = message.charAt(pos++);
                     if (c == '.') {
                        return new FormatOptions(flags, width, parsePrecision(message, pos, end));
                     }

                     int n = (char)(c - '0');
                     if (n >= 10) {
                        throw ParseException.atPosition("invalid width character", message, pos - 1);
                     }

                     width = width * 10 + n;
                     if (width > 999999) {
                        throw ParseException.withBounds("width too large", message, widthStart, end);
                     }
                  }

                  return new FormatOptions(flags, width, -1);
               }
            }

            int flagIdx = indexOfFlagCharacter(c);
            if (flagIdx < 0) {
               if (c == '.') {
                  return new FormatOptions(flags, -1, parsePrecision(message, pos, end));
               }

               throw ParseException.atPosition("invalid flag", message, pos - 1);
            }

            int flagBit = 1 << flagIdx;
            if ((flags & flagBit) != 0) {
               throw ParseException.atPosition("repeated flag", message, pos - 1);
            }

            flags |= flagBit;
         }

         return new FormatOptions(flags, -1, -1);
      }
   }

   private static int parsePrecision(String message, int start, int end) throws ParseException {
      if (start == end) {
         throw ParseException.atPosition("missing precision", message, start - 1);
      } else {
         int precision = 0;

         for (int pos = start; pos < end; pos++) {
            int n = (char)(message.charAt(pos) - '0');
            if (n >= 10) {
               throw ParseException.atPosition("invalid precision character", message, pos);
            }

            precision = precision * 10 + n;
            if (precision > 999999) {
               throw ParseException.withBounds("precision too large", message, start, end);
            }
         }

         if (precision == 0 && end != start + 1) {
            throw ParseException.withBounds("invalid precision", message, start, end);
         } else {
            return precision;
         }
      }
   }

   static int parseValidFlags(String flagChars, boolean hasUpperVariant) {
      int flags = hasUpperVariant ? 128 : 0;

      for (int i = 0; i < flagChars.length(); i++) {
         int flagIdx = indexOfFlagCharacter(flagChars.charAt(i));
         if (flagIdx < 0) {
            throw new IllegalArgumentException("invalid flags: " + flagChars);
         }

         flags |= 1 << flagIdx;
      }

      return flags;
   }

   private FormatOptions(int flags, int width, int precision) {
      this.flags = flags;
      this.width = width;
      this.precision = precision;
   }

   public FormatOptions filter(int allowedFlags, boolean allowWidth, boolean allowPrecision) {
      if (this.isDefault()) {
         return this;
      } else {
         int newFlags = allowedFlags & this.flags;
         int newWidth = allowWidth ? this.width : -1;
         int newPrecision = allowPrecision ? this.precision : -1;
         if (newFlags == 0 && newWidth == -1 && newPrecision == -1) {
            return DEFAULT;
         } else {
            return newFlags == this.flags && newWidth == this.width && newPrecision == this.precision
               ? this
               : new FormatOptions(newFlags, newWidth, newPrecision);
         }
      }
   }

   public boolean isDefault() {
      return this == getDefault();
   }

   public int getWidth() {
      return this.width;
   }

   public int getPrecision() {
      return this.precision;
   }

   public boolean validate(int allowedFlags, boolean allowPrecision) {
      if (this.isDefault()) {
         return true;
      } else if ((this.flags & ~allowedFlags) != 0) {
         return false;
      } else {
         return !allowPrecision && this.precision != -1 ? false : checkFlagConsistency(this.flags, this.getWidth() != -1);
      }
   }

   static boolean checkFlagConsistency(int flags, boolean hasWidth) {
      if ((flags & 9) == 9) {
         return false;
      } else {
         return (flags & 96) == 96 ? false : (flags & 96) == 0 || hasWidth;
      }
   }

   public boolean areValidFor(FormatChar formatChar) {
      return this.validate(formatChar.getAllowedFlags(), formatChar.getType().supportsPrecision());
   }

   public int getFlags() {
      return this.flags;
   }

   public boolean shouldLeftAlign() {
      return (this.flags & 32) != 0;
   }

   public boolean shouldShowAltForm() {
      return (this.flags & 2) != 0;
   }

   public boolean shouldShowLeadingZeros() {
      return (this.flags & 64) != 0;
   }

   public boolean shouldPrefixPlusForPositiveValues() {
      return (this.flags & 8) != 0;
   }

   public boolean shouldPrefixSpaceForPositiveValues() {
      return (this.flags & 1) != 0;
   }

   public boolean shouldShowGrouping() {
      return (this.flags & 16) != 0;
   }

   public boolean shouldUpperCase() {
      return (this.flags & 128) != 0;
   }

   public StringBuilder appendPrintfOptions(StringBuilder out) {
      if (!this.isDefault()) {
         int optionFlags = this.flags & -129;

         for (int bit = 0; 1 << bit <= optionFlags; bit++) {
            if ((optionFlags & 1 << bit) != 0) {
               out.append(" #(+,-0".charAt(bit));
            }
         }

         if (this.width != -1) {
            out.append(this.width);
         }

         if (this.precision != -1) {
            out.append('.').append(this.precision);
         }
      }

      return out;
   }

   @Override
   public boolean equals(@NullableDecl Object o) {
      if (o == this) {
         return true;
      } else if (!(o instanceof FormatOptions)) {
         return false;
      } else {
         FormatOptions other = (FormatOptions)o;
         return other.flags == this.flags && other.width == this.width && other.precision == this.precision;
      }
   }

   @Override
   public int hashCode() {
      int result = this.flags;
      result = 31 * result + this.width;
      return 31 * result + this.precision;
   }

   static {
      long encoded = 0L;

      for (int i = 0; i < " #(+,-0".length(); i++) {
         long n = " #(+,-0".charAt(i) - ' ';
         encoded |= i + 1L << (int)(3L * n);
      }

      ENCODED_FLAG_INDICES = encoded;
      DEFAULT = new FormatOptions(0, -1, -1);
   }
}
