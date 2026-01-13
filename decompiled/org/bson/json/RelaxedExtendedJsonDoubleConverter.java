package org.bson.json;

class RelaxedExtendedJsonDoubleConverter implements Converter<Double> {
   private static final Converter<Double> FALLBACK_CONVERTER = new ExtendedJsonDoubleConverter();

   public void convert(Double value, StrictJsonWriter writer) {
      if (!value.isNaN() && !value.isInfinite()) {
         writer.writeNumber(Double.toString(value));
      } else {
         FALLBACK_CONVERTER.convert(value, writer);
      }
   }
}
