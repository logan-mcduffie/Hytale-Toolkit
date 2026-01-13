package org.bson.json;

class RelaxedExtendedJsonInt64Converter implements Converter<Long> {
   public void convert(Long value, StrictJsonWriter writer) {
      writer.writeNumber(Long.toString(value));
   }
}
