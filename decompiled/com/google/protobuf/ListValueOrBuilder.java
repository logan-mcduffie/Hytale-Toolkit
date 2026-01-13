package com.google.protobuf;

import java.util.List;

public interface ListValueOrBuilder extends MessageOrBuilder {
   List<Value> getValuesList();

   Value getValues(int index);

   int getValuesCount();

   List<? extends ValueOrBuilder> getValuesOrBuilderList();

   ValueOrBuilder getValuesOrBuilder(int index);
}
