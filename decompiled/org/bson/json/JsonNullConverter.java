package org.bson.json;

import org.bson.BsonNull;

class JsonNullConverter implements Converter<BsonNull> {
   public void convert(BsonNull value, StrictJsonWriter writer) {
      writer.writeNull();
   }
}
