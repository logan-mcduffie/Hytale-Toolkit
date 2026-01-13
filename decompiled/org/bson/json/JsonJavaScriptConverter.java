package org.bson.json;

class JsonJavaScriptConverter implements Converter<String> {
   public void convert(String value, StrictJsonWriter writer) {
      writer.writeStartObject();
      writer.writeString("$code", value);
      writer.writeEndObject();
   }
}
