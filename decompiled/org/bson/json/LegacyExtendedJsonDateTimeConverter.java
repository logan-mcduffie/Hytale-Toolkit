package org.bson.json;

class LegacyExtendedJsonDateTimeConverter implements Converter<Long> {
   public void convert(Long value, StrictJsonWriter writer) {
      writer.writeStartObject();
      writer.writeNumber("$date", Long.toString(value));
      writer.writeEndObject();
   }
}
