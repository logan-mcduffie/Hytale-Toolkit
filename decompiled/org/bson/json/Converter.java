package org.bson.json;

public interface Converter<T> {
   void convert(T var1, StrictJsonWriter var2);
}
