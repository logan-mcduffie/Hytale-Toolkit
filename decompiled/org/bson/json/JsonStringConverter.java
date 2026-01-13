package org.bson.json;

class JsonStringConverter implements Converter<String> {
   public void convert(String value, StrictJsonWriter writer) {
      writer.writeString(value);
   }
}
