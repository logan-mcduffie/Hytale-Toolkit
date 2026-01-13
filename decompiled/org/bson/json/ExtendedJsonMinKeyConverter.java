package org.bson.json;

import org.bson.BsonMinKey;

class ExtendedJsonMinKeyConverter implements Converter<BsonMinKey> {
   public void convert(BsonMinKey value, StrictJsonWriter writer) {
      writer.writeStartObject();
      writer.writeNumber("$minKey", "1");
      writer.writeEndObject();
   }
}
