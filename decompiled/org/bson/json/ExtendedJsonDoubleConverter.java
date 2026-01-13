package org.bson.json;

class ExtendedJsonDoubleConverter implements Converter<Double> {
   public void convert(Double value, StrictJsonWriter writer) {
      writer.writeStartObject();
      writer.writeName("$numberDouble");
      writer.writeString(Double.toString(value));
      writer.writeEndObject();
   }
}
