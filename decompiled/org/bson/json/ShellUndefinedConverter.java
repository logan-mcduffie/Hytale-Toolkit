package org.bson.json;

import org.bson.BsonUndefined;

class ShellUndefinedConverter implements Converter<BsonUndefined> {
   public void convert(BsonUndefined value, StrictJsonWriter writer) {
      writer.writeRaw("undefined");
   }
}
