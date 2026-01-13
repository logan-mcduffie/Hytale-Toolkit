package com.nimbusds.jose.shaded.gson;

import com.nimbusds.jose.shaded.gson.internal.LazilyParsedNumber;
import com.nimbusds.jose.shaded.gson.internal.NumberLimits;
import com.nimbusds.jose.shaded.gson.stream.JsonReader;
import com.nimbusds.jose.shaded.gson.stream.MalformedJsonException;
import java.io.IOException;
import java.math.BigDecimal;

public enum ToNumberPolicy implements ToNumberStrategy {
   DOUBLE {
      public Double readNumber(JsonReader in) throws IOException {
         return in.nextDouble();
      }
   },
   LAZILY_PARSED_NUMBER {
      @Override
      public Number readNumber(JsonReader in) throws IOException {
         return new LazilyParsedNumber(in.nextString());
      }
   },
   LONG_OR_DOUBLE {
      @Override
      public Number readNumber(JsonReader in) throws IOException, JsonParseException {
         String value = in.nextString();
         if (value.indexOf(46) >= 0) {
            return this.parseAsDouble(value, in);
         } else {
            try {
               return Long.parseLong(value);
            } catch (NumberFormatException var4) {
               return this.parseAsDouble(value, in);
            }
         }
      }

      private Number parseAsDouble(String value, JsonReader in) throws IOException {
         try {
            Double d = Double.valueOf(value);
            if ((d.isInfinite() || d.isNaN()) && !in.isLenient()) {
               throw new MalformedJsonException("JSON forbids NaN and infinities: " + d + "; at path " + in.getPreviousPath());
            } else {
               return d;
            }
         } catch (NumberFormatException var4) {
            throw new JsonParseException("Cannot parse " + value + "; at path " + in.getPreviousPath(), var4);
         }
      }
   },
   BIG_DECIMAL {
      public BigDecimal readNumber(JsonReader in) throws IOException {
         String value = in.nextString();

         try {
            return NumberLimits.parseBigDecimal(value);
         } catch (NumberFormatException var4) {
            throw new JsonParseException("Cannot parse " + value + "; at path " + in.getPreviousPath(), var4);
         }
      }
   };

   private ToNumberPolicy() {
   }
}
