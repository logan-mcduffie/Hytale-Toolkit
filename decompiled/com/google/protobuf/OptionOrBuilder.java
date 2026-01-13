package com.google.protobuf;

public interface OptionOrBuilder extends MessageOrBuilder {
   String getName();

   ByteString getNameBytes();

   boolean hasValue();

   Any getValue();

   AnyOrBuilder getValueOrBuilder();
}
