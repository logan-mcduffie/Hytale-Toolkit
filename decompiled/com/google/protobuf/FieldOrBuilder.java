package com.google.protobuf;

import java.util.List;

public interface FieldOrBuilder extends MessageOrBuilder {
   int getKindValue();

   Field.Kind getKind();

   int getCardinalityValue();

   Field.Cardinality getCardinality();

   int getNumber();

   String getName();

   ByteString getNameBytes();

   String getTypeUrl();

   ByteString getTypeUrlBytes();

   int getOneofIndex();

   boolean getPacked();

   List<Option> getOptionsList();

   Option getOptions(int index);

   int getOptionsCount();

   List<? extends OptionOrBuilder> getOptionsOrBuilderList();

   OptionOrBuilder getOptionsOrBuilder(int index);

   String getJsonName();

   ByteString getJsonNameBytes();

   String getDefaultValue();

   ByteString getDefaultValueBytes();
}
