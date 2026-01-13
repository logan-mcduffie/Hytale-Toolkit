package org.bson.json;

public interface StrictJsonWriter {
   void writeName(String var1);

   void writeBoolean(boolean var1);

   void writeBoolean(String var1, boolean var2);

   void writeNumber(String var1);

   void writeNumber(String var1, String var2);

   void writeString(String var1);

   void writeString(String var1, String var2);

   void writeRaw(String var1);

   void writeRaw(String var1, String var2);

   void writeNull();

   void writeNull(String var1);

   void writeStartArray();

   void writeStartArray(String var1);

   void writeStartObject();

   void writeStartObject(String var1);

   void writeEndArray();

   void writeEndObject();

   boolean isTruncated();
}
