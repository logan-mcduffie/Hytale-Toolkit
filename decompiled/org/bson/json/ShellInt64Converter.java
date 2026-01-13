package org.bson.json;

class ShellInt64Converter implements Converter<Long> {
   public void convert(Long value, StrictJsonWriter writer) {
      if (value >= -2147483648L && value <= 2147483647L) {
         writer.writeRaw(String.format("NumberLong(%d)", value));
      } else {
         writer.writeRaw(String.format("NumberLong(\"%d\")", value));
      }
   }
}
