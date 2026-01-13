package org.bson.json;

class JsonSymbolConverter implements Converter<String> {
   public void convert(String value, StrictJsonWriter writer) {
      writer.writeStartObject();
      writer.writeString("$symbol", value);
      writer.writeEndObject();
   }
}
