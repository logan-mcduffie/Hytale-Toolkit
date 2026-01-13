package com.nimbusds.jose.shaded.gson.internal;

import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.ObjectStreamException;
import java.math.BigDecimal;

public final class LazilyParsedNumber extends Number {
   private final String value;

   public LazilyParsedNumber(String value) {
      this.value = value;
   }

   private BigDecimal asBigDecimal() {
      return NumberLimits.parseBigDecimal(this.value);
   }

   @Override
   public int intValue() {
      try {
         return Integer.parseInt(this.value);
      } catch (NumberFormatException var4) {
         try {
            return (int)Long.parseLong(this.value);
         } catch (NumberFormatException var3) {
            return this.asBigDecimal().intValue();
         }
      }
   }

   @Override
   public long longValue() {
      try {
         return Long.parseLong(this.value);
      } catch (NumberFormatException var2) {
         return this.asBigDecimal().longValue();
      }
   }

   @Override
   public float floatValue() {
      return Float.parseFloat(this.value);
   }

   @Override
   public double doubleValue() {
      return Double.parseDouble(this.value);
   }

   @Override
   public String toString() {
      return this.value;
   }

   private Object writeReplace() throws ObjectStreamException {
      return this.asBigDecimal();
   }

   private void readObject(ObjectInputStream in) throws IOException {
      throw new InvalidObjectException("Deserialization is unsupported");
   }

   @Override
   public int hashCode() {
      return this.value.hashCode();
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      } else if (obj instanceof LazilyParsedNumber) {
         LazilyParsedNumber other = (LazilyParsedNumber)obj;
         return this.value.equals(other.value);
      } else {
         return false;
      }
   }
}
