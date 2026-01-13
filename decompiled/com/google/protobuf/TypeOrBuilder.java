package com.google.protobuf;

import java.util.List;

public interface TypeOrBuilder extends MessageOrBuilder {
   String getName();

   ByteString getNameBytes();

   List<Field> getFieldsList();

   Field getFields(int index);

   int getFieldsCount();

   List<? extends FieldOrBuilder> getFieldsOrBuilderList();

   FieldOrBuilder getFieldsOrBuilder(int index);

   List<String> getOneofsList();

   int getOneofsCount();

   String getOneofs(int index);

   ByteString getOneofsBytes(int index);

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
