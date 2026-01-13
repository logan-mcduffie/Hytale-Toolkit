package org.bson.json;

import org.bson.BsonTimestamp;

class ShellTimestampConverter implements Converter<BsonTimestamp> {
   public void convert(BsonTimestamp value, StrictJsonWriter writer) {
      writer.writeRaw(String.format("Timestamp(%d, %d)", value.getTime(), value.getInc()));
   }
}
