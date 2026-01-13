package org.bson.json;

class ExtendedJsonInt32Converter implements Converter<Integer> {
   public void convert(Integer value, StrictJsonWriter writer) {
      writer.writeStartObject();
      writer.writeName("$numberInt");
      writer.writeString(Integer.toString(value));
      writer.writeEndObject();
   }
}
