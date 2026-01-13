package org.bson.json;

import org.bson.BsonMaxKey;

class ShellMaxKeyConverter implements Converter<BsonMaxKey> {
   public void convert(BsonMaxKey value, StrictJsonWriter writer) {
      writer.writeRaw("MaxKey");
   }
}
