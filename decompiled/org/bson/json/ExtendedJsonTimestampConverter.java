package org.bson.json;

import org.bson.BsonTimestamp;

class ExtendedJsonTimestampConverter implements Converter<BsonTimestamp> {
   public void convert(BsonTimestamp value, StrictJsonWriter writer) {
      writer.writeStartObject();
      writer.writeStartObject("$timestamp");
      writer.writeNumber("t", Long.toUnsignedString(Integer.toUnsignedLong(value.getTime())));
      writer.writeNumber("i", Long.toUnsignedString(Integer.toUnsignedLong(value.getInc())));
      writer.writeEndObject();
      writer.writeEndObject();
   }
}
