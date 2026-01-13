package org.bson.json;

class JsonInt32Converter implements Converter<Integer> {
   public void convert(Integer value, StrictJsonWriter writer) {
      writer.writeNumber(Integer.toString(value));
   }
}
