package org.bson.json;

class ExtendedJsonDateTimeConverter implements Converter<Long> {
   public void convert(Long value, StrictJsonWriter writer) {
      writer.writeStartObject();
      writer.writeStartObject("$date");
      writer.writeString("$numberLong", Long.toString(value));
      writer.writeEndObject();
      writer.writeEndObject();
   }
}
