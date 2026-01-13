package com.google.protobuf;

import java.util.List;

public interface EnumOrBuilder extends MessageOrBuilder {
   String getName();

   ByteString getNameBytes();

   List<EnumValue> getEnumvalueList();

   EnumValue getEnumvalue(int index);

   int getEnumvalueCount();

   List<? extends EnumValueOrBuilder> getEnumvalueOrBuilderList();

   EnumValueOrBuilder getEnumvalueOrBuilder(int index);

   List<Option> getOptionsList();

   Option getOptions(int index);

   int getOptionsCount();

   List<? extends OptionOrBuilder> getOptionsOrBuilderList();

   OptionOrBuilder getOptionsOrBuilder(int index);

   boolean hasSourceContext();

   SourceContext getSourceContext();

   SourceContextOrBuilder getSourceContextOrBuilder();

   int getSyntaxValue();

   Syntax getSyntax();

   String getEdition();

   ByteString getEditionBytes();
}
