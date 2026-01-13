package com.google.protobuf;

import java.util.Map;

public interface StructOrBuilder extends MessageOrBuilder {
   int getFieldsCount();

   boolean containsFields(String key);

   @Deprecated
   Map<String, Value> getFields();

   Map<String, Value> getFieldsMap();

   Value getFieldsOrDefault(String key, Value defaultValue);

   Value getFieldsOrThrow(String key);
}
