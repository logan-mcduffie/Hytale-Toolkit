package org.bson.json;

import org.bson.types.ObjectId;

class ExtendedJsonObjectIdConverter implements Converter<ObjectId> {
   public void convert(ObjectId value, StrictJsonWriter writer) {
      writer.writeStartObject();
      writer.writeString("$oid", value.toHexString());
      writer.writeEndObject();
   }
}
