package com.google.protobuf;

import java.util.List;

public interface ApiOrBuilder extends MessageOrBuilder {
   String getName();

   ByteString getNameBytes();

   List<Method> getMethodsList();

   Method getMethods(int index);

   int getMethodsCount();

   List<? extends MethodOrBuilder> getMethodsOrBuilderList();

   MethodOrBuilder getMethodsOrBuilder(int index);

   List<Option> getOptionsList();

   Option getOptions(int index);

   int getOptionsCount();

   List<? extends OptionOrBuilder> getOptionsOrBuilderList();

   OptionOrBuilder getOptionsOrBuilder(int index);

   String getVersion();

   ByteString getVersionBytes();

   boolean hasSourceContext();

   SourceContext getSourceContext();

   SourceContextOrBuilder getSourceContextOrBuilder();

   List<Mixin> getMixinsList();

   Mixin getMixins(int index);

   int getMixinsCount();

   List<? extends MixinOrBuilder> getMixinsOrBuilderList();

   MixinOrBuilder getMixinsOrBuilder(int index);

   int getSyntaxValue();

   Syntax getSyntax();

   String getEdition();

   ByteString getEditionBytes();
}
