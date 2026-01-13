package io.netty.handler.codec;

public interface ValueConverter<T> {
   T convertObject(Object var1);

   T convertBoolean(boolean var1);

   boolean convertToBoolean(T var1);

   T convertByte(byte var1);

   byte convertToByte(T var1);

   T convertChar(char var1);

   char convertToChar(T var1);

   T convertShort(short var1);

   short convertToShort(T var1);

   T convertInt(int var1);

   int convertToInt(T var1);

   T convertLong(long var1);

   long convertToLong(T var1);

   T convertTimeMillis(long var1);

   long convertToTimeMillis(T var1);

   T convertFloat(float var1);

   float convertToFloat(T var1);

   T convertDouble(double var1);

   double convertToDouble(T var1);
}
