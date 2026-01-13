package org.bson.json;

import org.bson.BsonMinKey;

class ShellMinKeyConverter implements Converter<BsonMinKey> {
   public void convert(BsonMinKey value, StrictJsonWriter writer) {
      writer.writeRaw("MinKey");
   }
}
