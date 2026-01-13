package com.google.protobuf;

import java.util.List;

public interface EnumValueOrBuilder extends MessageOrBuilder {
   String getName();

   ByteString getNameBytes();

   int getNumber();

   List<Option> getOptionsList();

   Option getOptions(int index);

   int getOptionsCount();

   List<? extends OptionOrBuilder> getOptionsOrBuilderList();

   OptionOrBuilder getOptionsOrBuilder(int index);
}
