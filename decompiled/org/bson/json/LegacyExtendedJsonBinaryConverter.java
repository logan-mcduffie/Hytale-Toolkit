package org.bson.json;

import org.bson.BsonBinary;
import org.bson.internal.Base64;

class LegacyExtendedJsonBinaryConverter implements Converter<BsonBinary> {
   public void convert(BsonBinary value, StrictJsonWriter writer) {
      writer.writeStartObject();
      writer.writeString("$binary", Base64.encode(value.getData()));
      writer.writeString("$type", String.format("%02X", value.getType()));
      writer.writeEndObject();
   }
}
