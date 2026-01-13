package org.bson.json;

class ExtendedJsonInt64Converter implements Converter<Long> {
   public void convert(Long value, StrictJsonWriter writer) {
      writer.writeStartObject();
      writer.writeName("$numberLong");
      writer.writeString(Long.toString(value));
      writer.writeEndObject();
   }
}
