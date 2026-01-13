package org.bson.json;

import org.bson.BsonUndefined;

class ExtendedJsonUndefinedConverter implements Converter<BsonUndefined> {
   public void convert(BsonUndefined value, StrictJsonWriter writer) {
      writer.writeStartObject();
      writer.writeBoolean("$undefined", true);
      writer.writeEndObject();
   }
}
