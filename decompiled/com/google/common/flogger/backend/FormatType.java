package com.google.common.flogger.backend;

import java.math.BigDecimal;
import java.math.BigInteger;

public enum FormatType {
   GENERAL(false, true) {
      @Override
      public boolean canFormat(Object arg) {
         return true;
      }
   },
   BOOLEAN(false, false) {
      @Override
      public boolean canFormat(Object arg) {
         return arg instanceof Boolean;
      }
   },
   CHARACTER(false, false) {
      @Override
      public boolean canFormat(Object arg) {
         if (arg instanceof Character) {
            return true;
         } else {
            return !(arg instanceof Integer) && !(arg instanceof Byte) && !(arg instanceof Short)
               ? false
               : Character.isValidCodePoint(((Number)arg).intValue());
         }
      }
   },
   INTEGRAL(true, false) {
      @Override
      public boolean canFormat(Object arg) {
         return arg instanceof Integer || arg instanceof Long || arg instanceof Byte || arg instanceof Short || arg instanceof BigInteger;
      }
   },
   FLOAT(true, true) {
      @Override
      public boolean canFormat(Object arg) {
         return arg instanceof Double || arg instanceof Float || arg instanceof BigDecimal;
      }
   };

   private final boolean isNumeric;
   private final boolean supportsPrecision;

   private FormatType(boolean isNumeric, boolean supportsPrecision) {
      this.isNumeric = isNumeric;
      this.supportsPrecision = supportsPrecision;
   }

   boolean supportsPrecision() {
      return this.supportsPrecision;
   }

   public boolean isNumeric() {
      return this.isNumeric;
   }

   public abstract boolean canFormat(Object var1);
}
