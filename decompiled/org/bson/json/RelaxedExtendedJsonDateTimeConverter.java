package org.bson.json;

class RelaxedExtendedJsonDateTimeConverter implements Converter<Long> {
   private static final Converter<Long> FALLBACK_CONVERTER = new ExtendedJsonDateTimeConverter();
   private static final long LAST_MS_OF_YEAR_9999 = 253402300799999L;

   public void convert(Long value, StrictJsonWriter writer) {
      if (value >= 0L && value <= 253402300799999L) {
         writer.writeStartObject();
         writer.writeString("$date", DateTimeFormatter.format(value));
         writer.writeEndObject();
      } else {
         FALLBACK_CONVERTER.convert(value, writer);
      }
   }
}
