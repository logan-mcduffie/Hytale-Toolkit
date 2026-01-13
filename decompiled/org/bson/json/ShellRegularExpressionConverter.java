package org.bson.json;

import org.bson.BsonRegularExpression;

class ShellRegularExpressionConverter implements Converter<BsonRegularExpression> {
   public void convert(BsonRegularExpression value, StrictJsonWriter writer) {
      String escaped = value.getPattern().equals("") ? "(?:)" : value.getPattern().replace("/", "\\/");
      writer.writeRaw("/" + escaped + "/" + value.getOptions());
   }
}
