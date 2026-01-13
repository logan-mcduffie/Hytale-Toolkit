package org.bson.json;

import org.bson.BsonRegularExpression;

class LegacyExtendedJsonRegularExpressionConverter implements Converter<BsonRegularExpression> {
   public void convert(BsonRegularExpression value, StrictJsonWriter writer) {
      writer.writeStartObject();
      writer.writeString("$regex", value.getPattern());
      writer.writeString("$options", value.getOptions());
      writer.writeEndObject();
   }
}
